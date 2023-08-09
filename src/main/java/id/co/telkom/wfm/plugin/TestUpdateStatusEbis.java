/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import id.co.telkom.wfm.plugin.dao.TestUpdateStatusEbisDao;
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
public class TestUpdateStatusEbis  extends Element implements PluginWebSupport {

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
                //Store param
                String status = (body.get("status") == null ? "" : body.get("status").toString());
                String wonum = null;
                String parent = null;
                String taskId = null;
                String siteId = null;
                String woSequence = null;
                String woStatus = null;
                String description = null;
//                String error = null;
                String errorCode = null;
                String engineerMemo = null;
                String currentDate = null;

                wonum = (body.get("wonum") == null ? "" : body.get("wonum").toString());
                parent = wonum.substring(0, 11);
                taskId = (body.get("taskId") == null ? "" : body.get("taskId").toString());
                siteId = (body.get("siteId") == null ? "" : body.get("siteId").toString());
                woSequence = (body.get("woSequence") == null ? "" : body.get("woSequence").toString());
                woStatus = (body.get("woStatus") == null ? "" : body.get("woStatus").toString());
                description = (body.get("description") == null ? "" : body.get("description").toString());
                currentDate = time.getCurrentTime();

                int nextTaskId = Integer.parseInt(taskId) + 10;
                
                switch(body.get("status").toString()) {
                    case "STARTWA" :
                        final boolean updateTask = updateTaskStatusEbisDao.updateTask(wonum, status);
                        if (updateTask) {
                            hsr1.setStatus(200);
                        }
                        JSONObject res = new JSONObject();
                        res.put("code", "200");
                        res.put("status", status);
                        res.put("wonum", wonum);
                        res.put("message", "Successfully update status");
                        res.writeJSONString(hsr1.getWriter());
                        break;
                    case "COMPWA" :
                        break;
                    default :
                        break;
                }
                
                if (woSequence.equals("10") || woSequence.equals("20") || woSequence.equals("30") || woSequence.equals("40") || woSequence.equals("50") || woSequence.equals("60")) {
                    //Update status
                    if ("STARTWA".equals(body.get("status"))) {
                        final boolean updateTask = updateTaskStatusEbisDao.updateTask(wonum, status);
                        if (updateTask) {
                            hsr1.setStatus(200);
                        }
                        JSONObject res = new JSONObject();
                        res.put("code", "200");
                        res.put("status", status);
                        res.put("wonum", wonum);
                        res.put("message", "Successfully update status");
                        res.writeJSONString(hsr1.getWriter());
                    } else if ("COMPWA".equals(body.get("status"))) {
                        boolean isMandatoryValue = updateTaskStatusEbisDao.checkMandatory(wonum);
                        if (isMandatoryValue == true) {
                            // update task status
                            updateTaskStatusEbisDao.updateTask(wonum, status);

                            if (description.equals("Registration Suplychain") || description.equals("Replace NTE") || description.equals("Registration Suplychain Wifi")) {
                                // Start of Set Install/Set Dismantle
                                ScmtIntegrationEbisDao scmtIntegrationEbisDao = new ScmtIntegrationEbisDao();
                                scmtIntegrationEbisDao.sendInstall(parent);

                                JSONObject res = new JSONObject();
                                res.put("code", "255");
                                res.put("message", "Success");
                                res.writeJSONString(hsr1.getWriter());
                                hsr1.setStatus(255);
                                final boolean nextAssign = updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId));
                                if (nextAssign) {
                                    hsr1.setStatus(200);

                                }
    //                            updateTaskStatusEbisDao.updateTask(wonum, status);
                            } else {
                                // Define the next move
                                final String nextMove = updateTaskStatusEbisDao.nextMove(parent, Integer.toString(nextTaskId));

                                if ("COMPLETE".equals(nextMove)) {
                                    try {
                                        // Update parent status
                                        updateTaskStatusEbisDao.updateParentStatus(parent, "COMPLETE", currentDate);
                                        LogUtil.info(getClass().getName(), "Update COMPLETE Successfully");

                                        // update task status
                                        final boolean updateTask = updateTaskStatusEbisDao.updateTask(wonum, status);
                                        if (updateTask) {
                                            hsr1.setStatus(200);
                                        }

                                        // Insert data to table WFMMILESTONE
                                        updateTaskStatusEbisDao.insertToWfmMilestone(parent, siteId, currentDate);

                                        //Create response
                                        JSONObject dataRes = new JSONObject();
                                        dataRes.put("wonum", parent);
                                        dataRes.put("milestone", woStatus);
                                        JSONObject res = new JSONObject();
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
                                    final boolean nextAssign = updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId));
                                    if (nextAssign) {
                                        hsr1.setStatus(200);
    //                                    updateTaskStatusEbisDao.updateWoDesc(parent, Integer.toString(nextTaskId));
                                    }
                                    updateTaskStatusEbisDao.updateWoDesc(parent, Integer.toString(nextTaskId));
                                    updateTaskStatusEbisDao.updateTask(wonum, status);
                                }
                            }
                        } else {
                            hsr1.setStatus(422);
                        }
                    }
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
    
//    private void compwaSendToSCMT(String description) {
//        TestUpdateStatusEbisDao updateDao = new TestUpdateStatusEbisDao();
//    }
}
