/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import id.co.telkom.wfm.plugin.dao.UpdateStatusMyStaffDao;
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
public class UpdateTaskStatusMyStaff extends Element implements PluginWebSupport {
    String pluginName = "Telkom New WFM - Update Status Task MyStaff - Web Service";
    
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
       return "7.0.0";
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
        UpdateStatusMyStaffDao dao = new UpdateStatusMyStaffDao();
        boolean isAuthSuccess = dao.getApiAttribute(hsr.getHeader("api_id"), hsr.getHeader("api_key"));
        boolean methodStatus = false;
        LogUtil.info(getClass().getName(), "Start Process: Update Task Status MyStaff");
        //@Authorization
        if ("POST".equals(hsr.getMethod()))
            methodStatus = true;
        if (methodStatus && isAuthSuccess) {
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
                JSONObject body = (JSONObject) parser.parse(bodyParam);//JSON Object
                
                
                JSONObject taskObj = new JSONObject();
                
                //Store param
                String task = "updateTaskStatus";
                String wonum = (body.get("wonum") == null ? "" : body.get("wonum").toString());
                String siteId = (body.get("siteid") == null ? "" : body.get("siteid").toString());
                String wolo1 = (body.get("wolo1") == null ? "" : body.get("wolo1").toString());
                String status = (body.get("status") == null ? "" : body.get("status").toString());
                String cpe_vendor = (body.get("cpe_vendor") == null ? "" : body.get("cpe_vendor").toString());
                String cpe_model = (body.get("cpe_model") == null ? "" : body.get("cpe_model").toString());
                String cpe_serial_number = (body.get("cpe_serial_number") == null ? "" : body.get("cpe_serial_number").toString());
                String tk_custom_header_03 = (body.get("tk_custom_header_03") == null ? "" : body.get("tk_custom_header_03").toString());
                String tk_custom_header_04 = (body.get("tk_custom_header_04") == null ? "" : body.get("tk_custom_header_04").toString());
                String tk_custom_header_10 = (body.get("tk_custom_header_10") == null ? "" : body.get("tk_custom_header_10").toString());
                String labor_scmt = (body.get("labor_scmt") == null ? "" : body.get("labor_scmt").toString());
                String longitude = (body.get("longitude") == null ? "" : body.get("longitude").toString());
                String latitude = (body.get("latitude") == null ? "" : body.get("latitude").toString());
                String statusiface = (body.get("statusiface") == null ? "" : body.get("statusiface").toString());
                String np_statusmemo = (body.get("np_statusmemo") == null ? "" : body.get("np_statusmemo").toString());
                String errorcode = (body.get("errorcode") == null ? "" : body.get("errorcode").toString());
                String suberrorcode = (body.get("suberrorcode") == null ? "" : body.get("suberrorcode").toString());
                String engineermemo = (body.get("engineermemo") == null ? "" : body.get("engineermemo").toString());
                String urlevidence = (body.get("urlevidence") == null ? "" : body.get("urlevidence").toString());
                String currentDate = time.getCurrentTime();
                
                JSONObject getTaskUpdate = dao.getTask(wonum);
                taskObj.put("taskid", (int) getTaskUpdate.get("taskid"));
                taskObj.put("wosequence", getTaskUpdate.get("wosequence"));
                taskObj.put("detailactcode", getTaskUpdate.get("detailactcode"));
                taskObj.put("description", getTaskUpdate.get("description"));
                taskObj.put("parent", getTaskUpdate.get("parent"));
                LogUtil.info(getClassName(), "taskObj: " + taskObj);

                int nextTaskId = (int) taskObj.get("taskid") + 10;
                String woSequence = taskObj.get("wosequence").toString();
                String description = taskObj.get("description").toString();
                String parent = taskObj.get("parent").toString();
                LogUtil.info(getClassName(), "parent: " + parent);
                LogUtil.info(getClassName(), "wosequence: " + woSequence);
                LogUtil.info(getClassName(), "description: " + description);
                
//                int nextTaskId = Integer.parseInt(taskId) + 10;
                if (woSequence.equals("10") || woSequence.equals("20") || woSequence.equals("30") || woSequence.equals("40") || woSequence.equals("50") || woSequence.equals("60")) {
                    //Update status
//                    UpdateTaskStatusEbisDao updateTaskStatusEbisDao = new UpdateTaskStatusEbisDao();
                    if ("STARTWA".equals(body.get("status"))) {
                        final boolean updateTask = dao.updateTask(wonum, status);
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
                        // update task status
                        dao.updateTask(wonum, status);
                        
                        if (description.equals("Registration Suplychain") || description.equals("Replace NTE") || description.equals("Registration Suplychain Wifi")) {
                            // Start of Set Install/Set Dismantle
                            ScmtIntegrationEbisDao scmtIntegrationEbisDao = new ScmtIntegrationEbisDao();
                            scmtIntegrationEbisDao.sendInstall(parent);

                            JSONObject res = new JSONObject();
                            res.put("code", "255");
                            res.put("message", "Success");
                            res.writeJSONString(hsr1.getWriter());
                            hsr1.setStatus(255);
                            final boolean nextAssign = dao.nextAssign(parent, Integer.toString(nextTaskId));
                            if (nextAssign) {
                                hsr1.setStatus(200);
                            }
//                            updateTaskStatusEbisDao.updateTask(wonum, status);
                        } else {
                            // Define the next move
                            final String nextMove = dao.nextMove(parent, Integer.toString(nextTaskId));

                            if ("COMPLETE".equals(nextMove)) {
                                try {
                                    // Update parent status
                                    dao.updateParentStatus(parent, "COMPLETE", currentDate);
                                    LogUtil.info(getClass().getName(), "Update COMPLETE Successfully");

                                    // update task status
                                    final boolean updateTask = dao.updateTask(wonum, status);
                                    if (updateTask) {
                                        hsr1.setStatus(200);
                                    }

                                    // Insert data to table WFMMILESTONE
                                    dao.insertToWfmMilestone(parent, siteId, currentDate);

                                    //Create response
                                    JSONObject dataRes = new JSONObject();
                                    dataRes.put("wonum", parent);
                                    dataRes.put("milestone", status);
                                    JSONObject res = new JSONObject();
                                    res.put("code", "200");
                                    res.put("message", "Success");
                                    res.put("data", dataRes);
                                    res.writeJSONString(hsr1.getWriter());
                                    
                                    //Build Response
                                    JSONObject data = dao.getCompleteJson(parent);

                                    // Response to Kafka
                                    String topic = "WFM_MILESTONE_ENTERPRISE_" + siteId.replaceAll("\\s+", "");
                                    String kafkaRes = data.toJSONString();
                                    KafkaProducerTool kaf = new KafkaProducerTool();
                                    kaf.generateMessage(kafkaRes, topic, "");
                                    hsr1.setStatus(200);
                                } catch (IOException | SQLException e) {
                                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                                }
                            } else {
                                //Give LABASSIGN to next task
                                final boolean nextAssign = dao.nextAssign(parent, Integer.toString(nextTaskId));
                                if (nextAssign) {
                                    hsr1.setStatus(200);
                                }
                                dao.updateWoDesc(parent, Integer.toString(nextTaskId));
                                dao.updateTask(wonum, status);
                            }
                        }
                    }
                }
            } catch (ParseException | SQLException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        //Authorization failed 
        } else if (!methodStatus) {
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (Exception e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else {
            try {
                hsr1.sendError(401, "Invalid Authentication");
            } catch (Exception e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        }
    }
}
