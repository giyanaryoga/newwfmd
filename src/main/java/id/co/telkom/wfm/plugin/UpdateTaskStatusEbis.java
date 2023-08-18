/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.*;
import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.io.*;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.joget.apps.form.model.*;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

/**
 *
 * @author ASUS
 */
public class UpdateTaskStatusEbis extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Update Task Status Ebis - Web Service";

    @Override
    public String renderTemplate(FormData fd, Map map) {
        return "";
    }

    @Override
    public String getName() {
        return this.pluginName;
    }

    @Override
    public String getVersion() {
        return "7.00";
    }

    @Override
    public String getDescription() {
        return this.pluginName;
    }

    @Override
    public String getLabel() {
        return this.pluginName;
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {
        TimeUtil time = new TimeUtil();
        //@@Start..
        LogUtil.info(getClass().getName(), "Start Process: Update Task Status");
        //@Authorization
        if ("POST".equals(hsr.getMethod())) {
            try {
                //@Parsing message
                //HttpServletRequest get JSON Post data
                StringBuilder jb = new StringBuilder();
                String line = null;
                try {//read the response JSON to string buffer
                    BufferedReader reader = hsr.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }
                } catch (IOException e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                //Parse JSON String to JSON Object
                String bodyParam = jb.toString(); //String
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject) parser.parse(bodyParam);//JSON Object
                JSONObject envelope = (JSONObject) data_obj.get("UpdateMXTELKOWO");
                JSONObject envelope2 = (JSONObject) envelope.get("MXTELKOWOSet");
                JSONObject body = (JSONObject) envelope2.get("WORKORDER");

                UpdateTaskStatusEbisDao updateTaskStatusEbisDao = new UpdateTaskStatusEbisDao();
                ScmtIntegrationEbisDao scmtIntegrationEbisDao = new ScmtIntegrationEbisDao();
                TaskHistoryDao taskHistoryDao = new TaskHistoryDao();

                JSONObject res = new JSONObject();

                //Store param
                String status;
                String wonum;
                String parent;
                String taskId;
                String siteId;
                String woSequence;
                String woStatus;
                String description;
                String errorCode;
                String engineerMemo;
                String memo;
                String modifiedBy;
                String currentDate;

                status = (body.get("status") == null ? "" : body.get("status").toString());
                wonum = (body.get("wonum") == null ? "" : body.get("wonum").toString());
                parent = (body.get("parent") == null ? "" : body.get("parent").toString());
                taskId = (body.get("taskId") == null ? "" : body.get("taskId").toString());
                siteId = (body.get("siteId") == null ? "" : body.get("siteId").toString());
                woSequence = (body.get("woSequence") == null ? "" : body.get("woSequence").toString());
                woStatus = (body.get("woStatus") == null ? "" : body.get("woStatus").toString());
                description = (body.get("description") == null ? "" : body.get("description").toString());
                memo = (body.get("memo") == null ? "" : body.get("memo").toString());
                modifiedBy = (body.get("modifiedBy") == null ? "" : body.get("modifiedBy").toString());
                currentDate = time.getCurrentTime();

                int nextTaskId = Integer.parseInt(taskId) + 10;
                boolean updateTask = false;
                boolean nextAssign = false;

                switch (body.get("status").toString()) {
                    case "STARTWA":
                        boolean isAssigned = updateTaskStatusEbisDao.checkAssignment(wonum);
                        if (!isAssigned) {
                            String checkActPlace = updateTaskStatusEbisDao.checkActPlace(wonum);
                            if (checkActPlace.equals("OUTSIDE")) {
                                hsr1.setStatus(421);
                            } else {
                                updateTask = updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
                                if (updateTask) {
                                    hsr1.setStatus(200);
                                    taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                                }
                                res.put("code", "200");
                                res.put("status", status);
                                res.put("wonum", wonum);
                                res.put("message", "Successfully update status");
                                res.writeJSONString(hsr1.getWriter());
                            }
                        } else {
                            updateTask = updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
                            if (updateTask) {
                                hsr1.setStatus(200);
                                taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                            }
                            res.put("code", "200");
                            res.put("status", status);
                            res.put("wonum", wonum);
                            res.put("message", "Successfully update status");
                            res.writeJSONString(hsr1.getWriter());
                        }
                        break;
                    case "COMPWA":
                        boolean isMandatoryValue = updateTaskStatusEbisDao.checkMandatory(wonum);
                        Integer isRequired = updateTaskStatusEbisDao.isRequired(wonum);
                        if (!isMandatoryValue && isRequired != 1) {
                            hsr1.setStatus(422);
                        } else {
                            switch (description) {
                                case "Registration Suplychain":
                                case "Registration Suplychain Wifi":
                                    // Start of Set Install
                                    scmtIntegrationEbisDao.sendInstall(parent);

                                    res.put("code", "255");
                                    res.put("message", "Success");
                                    res.writeJSONString(hsr1.getWriter());
                                    hsr1.setStatus(255);
                                    updateTask = updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
                                    nextAssign = updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId), modifiedBy);
                                    if (nextAssign && updateTask) {
                                        hsr1.setStatus(200);
                                        taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                                    }
                                    break;
                                case "Dismantle NTE":
                                case "Dismantle AP":
                                case "Dismantle AP MESH":
                                    // Start of Set Install
                                    scmtIntegrationEbisDao.sendDismantle(parent);

                                    res.put("code", "255");
                                    res.put("message", "Success");
                                    res.writeJSONString(hsr1.getWriter());
                                    hsr1.setStatus(255);
                                    updateTask = updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
                                    nextAssign = updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId), modifiedBy);
                                    if (nextAssign && updateTask) {
                                        hsr1.setStatus(200);
                                        taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                                    }
                                    break;
                                default:
                                    // Define the next move
                                    final String nextMove = updateTaskStatusEbisDao.nextMove(parent, Integer.toString(nextTaskId));

                                    if ("COMPLETE".equals(nextMove)) {
                                        try {
                                            // Update parent status
                                            updateTaskStatusEbisDao.updateParentStatus(parent, "COMPLETE", currentDate, modifiedBy);
//                                            LogUtil.info(getClass().getName(), "Update COMPLETE Successfully");

                                            // update task status
                                            updateTask = updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
                                            if (updateTask) {
                                                hsr1.setStatus(200);
                                                taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                                            }

                                            // Insert data to table WFMMILESTONE
                                            updateTaskStatusEbisDao.insertToWfmMilestone(wonum, siteId, currentDate);

                                            //Create response
                                            JSONObject dataRes = new JSONObject();
                                            dataRes.put("wonum", parent);
                                            dataRes.put("milestone", woStatus);

                                            res.put("code", "200");
                                            res.put("message", "Success");
                                            res.put("data", dataRes);
                                            res.writeJSONString(hsr1.getWriter());

                                            //Build Response
                                            JSONObject data = updateTaskStatusEbisDao.getCompleteJson(parent);

                                            // Response to Kafka
                                            String topic = "WFM_MILESTONE_ENTERPRISE_" + siteId.replaceAll("\\s+", "");
                                            String kafkaRes = data.toJSONString();
                                            KafkaProducerTool kaf = new KafkaProducerTool();
                                            kaf.generateMessage(kafkaRes, topic, "");
                                            hsr1.setStatus(256);
                                        } catch (IOException | SQLException e) {
                                            LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                                        }
                                    } else {
                                        //Give LABASSIGN to next task
                                        nextAssign = updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId), modifiedBy);
                                        if (nextAssign) {
                                            hsr1.setStatus(200);
                                        }
                                        updateTaskStatusEbisDao.updateWoDesc(parent, Integer.toString(nextTaskId), modifiedBy);
                                        updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
                                    }
                                    break;
                            }
                        }
                        break;
                    default:
                        hsr1.setStatus(420);
                        break;
                }
            } catch (ParseException | SQLException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else if (!"POST".equals(hsr.getMethod())) {
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        }
    }
}
