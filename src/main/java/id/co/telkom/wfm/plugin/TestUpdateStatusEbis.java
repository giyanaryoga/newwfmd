/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import id.co.telkom.wfm.plugin.dao.TaskHistoryDao;
import id.co.telkom.wfm.plugin.dao.TestUpdateStatusEbisDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.datalist.service.JsonUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author User
 */
public class TestUpdateStatusEbis extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Test Update Task Status Ebis - Web Service";

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

                TestUpdateStatusEbisDao updateTaskStatusEbisDao = new TestUpdateStatusEbisDao();
                ScmtIntegrationEbisDao scmtIntegrationEbisDao = new ScmtIntegrationEbisDao();
                TaskHistoryDao taskHistoryDao = new TaskHistoryDao();

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
                String updateTask = "";
//                boolean updateTask = false;
                boolean nextAssign = false;
//                boolean isWoDocValue = false;
                String isWoDocValue = "";

                final JSONObject res = new JSONObject();

                switch (body.get("status").toString()) {
                    case "STARTWA":
                        boolean isAssigned = updateTaskStatusEbisDao.checkAssignment(wonum);
                        if (!isAssigned) {
                            String checkActPlace = updateTaskStatusEbisDao.checkActPlace(wonum);
                            if (checkActPlace.equals("OUTSIDE")) {
                                hsr1.setStatus(421);
                                res.put("code", 200);
                                res.put("wonum", wonum);
                                res.put("message", "Please assign task to Laborcode / Amcrew first");
                                res.writeJSONString(hsr1.getWriter());
                            } else {
                                updateTask = updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
//                                if (updateTask) {
//                                    hsr1.setStatus(200);
//                                    taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
//                                }
//                                res.put("code", "200");
//                                res.put("status", status);
//                                res.put("wonum", wonum);
//                                res.put("message", "Successfully update status");
//                                res.writeJSONString(hsr1.getWriter());
                                if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                                    hsr1.setStatus(200);
                                    taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                                    res.put("code", 200);
                                    res.put("status", status);
                                    res.put("wonum", wonum);
                                    res.put("message", "Successfully update status");
                                    res.writeJSONString(hsr1.getWriter());
                                }
                            }
                        } else {
                            updateTask = updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
//                            if (updateTask) {
//                                hsr1.setStatus(200);
//                                taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
//                            }
//                            res.put("code", "200");
//                            res.put("status", status);
//                            res.put("wonum", wonum);
//                            res.put("message", "Successfully update status");
//                            res.writeJSONString(hsr1.getWriter());
                            if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                                hsr1.setStatus(200);
                                taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                                res.put("code", 200);
                                res.put("status", status);
                                res.put("wonum", wonum);
                                res.put("message", "Successfully update status");
                                res.writeJSONString(hsr1.getWriter());
                            }
                        }
                        break;
                    case "COMPWA":
                        boolean isMandatoryValue = updateTaskStatusEbisDao.checkMandatory(wonum);
                        LogUtil.info(pluginName, "test: " + isMandatoryValue);
                        Integer isRequired = updateTaskStatusEbisDao.isRequired(wonum);
                        if (isMandatoryValue && isRequired != 1) {
//                            hsr1.setStatus(422);
                            res.put("code", 422);
                            res.put("wonum", wonum);
                            res.put("message", "Please insert Task Attribute in Mandatory");
                            res.writeJSONString(hsr1.getWriter());
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
//                                    if (nextAssign && updateTask) {
//                                        hsr1.setStatus(200);
//                                        taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
//                                    }

                                    if (nextAssign && updateTask.equalsIgnoreCase("Update task status berhasil")) {
                                        hsr1.setStatus(200);
                                        taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                                        res.put("code", 200);
                                        res.put("message", "Mengirim set Install Wifi");
                                        res.writeJSONString(hsr1.getWriter());
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
//                                    if (nextAssign && updateTask) {
//                                        hsr1.setStatus(200);
//                                        taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
//                                    }
                                    if (nextAssign && updateTask.equalsIgnoreCase("Update task status berhasil")) {
                                        hsr1.setStatus(200);
                                        taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                                        res.put("code", 200);
                                        res.put("message", "Mengirim set Install Dismantle");
                                        res.writeJSONString(hsr1.getWriter());
                                    }
                                    break;
                                case "Upload Berita Acara":
                                    // check documentname
                                    try {

                                    isWoDocValue = updateTaskStatusEbisDao.checkWoDoc(parent);
                                    if (isWoDocValue.equalsIgnoreCase("The Filename and Product name are correct, Update Status COMPWA Successfully")) {
                                        HttpServletResponse response = hsr1;
                                        response.setStatus(200);
                                        res.put("code", 200);
                                        res.put("message", isWoDocValue);
                                        res.writeJSONString(response.getWriter());
                                        
                                        updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
                                        updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId), modifiedBy);
                                        taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
                                    } else {
                                        HttpServletResponse response = hsr1;
                                        response.setStatus(200);
                                        res.put("code", 200);
                                        res.put("message", isWoDocValue);
                                        res.writeJSONString(response.getWriter());
                                        LogUtil.info(this.getClassName(), "RESULT : " + isWoDocValue);
                                        LogUtil.info(this.getClassName(), "RESPONSE : " + res);
                                    }
                                } catch (Exception e) {
                                }
                                break;
                                default:
                                    // Define the next move
                                    final String nextMove = updateTaskStatusEbisDao.nextMove(parent, Integer.toString(nextTaskId));

                                    if ("COMPLETE".equals(nextMove)) {
                                        try {
                                            // Update parent status
                                            updateTaskStatusEbisDao.updateParentStatus(parent, "COMPLETE", currentDate, modifiedBy);
                                            LogUtil.info(getClass().getName(), "Update COMPLETE Successfully");

                                            // update task status
                                            updateTask = updateTaskStatusEbisDao.updateTask(wonum, status, modifiedBy);
//                                            if (updateTask) {
//                                                hsr1.setStatus(200);
//                                                taskHistoryDao.insertTaskStatus(wonum, memo, modifiedBy);
//                                            }
                                            if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
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
                                            res.put("message", "Berhasil mengupdate status, Mengirim Status COMPLETE ke OSM");
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

//                if (woSequence.equals("10") || woSequence.equals("20") || woSequence.equals("30") || woSequence.equals("40") || woSequence.equals("50") || woSequence.equals("60")) {
//                    //Update status
//                    if ("STARTWA".equals(body.get("status"))) {
//                        final boolean updateTask = updateTaskStatusEbisDao.updateTask(wonum, status);
//                        if (updateTask) {
//                            hsr1.setStatus(200);
//                        }
//                        JSONObject res = new JSONObject();
//                        res.put("code", "200");
//                        res.put("status", status);
//                        res.put("wonum", wonum);
//                        res.put("message", "Successfully update status");
//                        res.writeJSONString(hsr1.getWriter());
//                    } else if ("COMPWA".equals(body.get("status"))) {
//                        boolean isMandatoryValue = updateTaskStatusEbisDao.checkMandatory(wonum);
//                        if (isMandatoryValue == true) {
//                            // update task status
//                            updateTaskStatusEbisDao.updateTask(wonum, status);
//
//                            if (description.equals("Registration Suplychain") || description.equals("Replace NTE") || description.equals("Registration Suplychain Wifi")) {
//                                // Start of Set Install/Set Dismantle
//                                ScmtIntegrationEbisDao scmtIntegrationEbisDao = new ScmtIntegrationEbisDao();
//                                scmtIntegrationEbisDao.sendInstall(parent);
//
//                                JSONObject res = new JSONObject();
//                                res.put("code", "255");
//                                res.put("message", "Success");
//                                res.writeJSONString(hsr1.getWriter());
//                                hsr1.setStatus(255);
//                                final boolean nextAssign = updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId));
//                                if (nextAssign) {
//                                    hsr1.setStatus(200);
//
//                                }
//    //                            updateTaskStatusEbisDao.updateTask(wonum, status);
//                            } else {
//                                // Define the next move
//                                final String nextMove = updateTaskStatusEbisDao.nextMove(parent, Integer.toString(nextTaskId));
//
//                                if ("COMPLETE".equals(nextMove)) {
//                                    try {
//                                        // Update parent status
//                                        updateTaskStatusEbisDao.updateParentStatus(parent, "COMPLETE", currentDate);
//                                        LogUtil.info(getClass().getName(), "Update COMPLETE Successfully");
//
//                                        // update task status
//                                        final boolean updateTask = updateTaskStatusEbisDao.updateTask(wonum, status);
//                                        if (updateTask) {
//                                            hsr1.setStatus(200);
//                                        }
//
//                                        // Insert data to table WFMMILESTONE
//                                        updateTaskStatusEbisDao.insertToWfmMilestone(parent, siteId, currentDate);
//
//                                        //Create response
//                                        JSONObject dataRes = new JSONObject();
//                                        dataRes.put("wonum", parent);
//                                        dataRes.put("milestone", woStatus);
//                                        JSONObject res = new JSONObject();
//                                        res.put("code", "200");
//                                        res.put("message", "Success");
//                                        res.put("data", dataRes);
//                                        res.writeJSONString(hsr1.getWriter());
//
//                                        //Build Response
//                                        JSONObject data = updateTaskStatusEbisDao.getCompleteJson(parent);
//
//                                        // Response to Kafka
//                                        String topic = "WFM_MILESTONE_ENTERPRISE_" + siteId.replaceAll("\\s+", "");
//                                        String kafkaRes = data.toJSONString();
//                                        KafkaProducerTool kaf = new KafkaProducerTool();
//                                        kaf.generateMessage(kafkaRes, topic, "");
//                                        hsr1.setStatus(256);
//                                    } catch (IOException | SQLException e) {
//                                        LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
//                                    }
//                                } else {
//                                    //Give LABASSIGN to next task
//                                    final boolean nextAssign = updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId));
//                                    if (nextAssign) {
//                                        hsr1.setStatus(200);
//    //                                    updateTaskStatusEbisDao.updateWoDesc(parent, Integer.toString(nextTaskId));
//                                    }
//                                    updateTaskStatusEbisDao.updateWoDesc(parent, Integer.toString(nextTaskId));
//                                    updateTaskStatusEbisDao.updateTask(wonum, status);
//                                }
//                            }
//                        } else {
//                            hsr1.setStatus(422);
//                        }
//                    }
//                }
