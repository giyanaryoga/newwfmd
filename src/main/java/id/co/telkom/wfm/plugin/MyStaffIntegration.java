/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

//import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.dao.TaskAttributeUpdateDao;
//import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class MyStaffIntegration extends Element implements PluginWebSupport {
    String pluginName = "Telkom New WFM - MyStaff Integration - Web Service";
    
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
        UpdateTaskStatusEbisDao dao = new UpdateTaskStatusEbisDao();
        TaskAttributeUpdateDao daoAttr = new TaskAttributeUpdateDao();
        boolean isAuthSuccess = dao.getApiAttribute(hsr.getHeader("api_id"), hsr.getHeader("api_key"));
        boolean methodStatus = false;
        LogUtil.info(getClass().getName(), "Start Process: MyStaff Integration");
        //@Authorization
        if ("POST".equals(hsr.getMethod()))
            methodStatus = true;
        if (methodStatus && isAuthSuccess) {
            try {
                //@Parsing message
                //HttpServletRequest get JSON Post data
                StringBuilder jb = new StringBuilder();
                String line = null;
                BufferedReader reader = hsr.getReader();
                while ((line = reader.readLine()) != null) {
                    jb.append(line);
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                //Parse JSON String to JSON Object
                String bodyParam = jb.toString(); //String
                JSONParser parser = new JSONParser();
                JSONObject body = (JSONObject) parser.parse(bodyParam);//JSON Object
                JSONObject taskObj = new JSONObject();
                //Store param
                String task = (body.get("task") == null ? "" : body.get("task").toString());
                String currentDate = time.getCurrentTime();
                String modifiedBy = "MyStaff";
                
                if (task.equalsIgnoreCase("updateTaskStatus")) {
                    LogUtil.info(getClass().getName(), "Start Process: Update Task Status MyStaff");
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
                    
                    JSONObject getTaskUpdate = dao.getTask(wonum);
                    taskObj.put("taskid", (int) getTaskUpdate.get("taskid"));
                    taskObj.put("wosequence", getTaskUpdate.get("wosequence"));
                    taskObj.put("detailactcode", getTaskUpdate.get("detailactcode"));
                    taskObj.put("description", getTaskUpdate.get("description"));
                    taskObj.put("parent", getTaskUpdate.get("parent"));
//                    LogUtil.info(getClassName(), "taskObj: " + taskObj);
                    int nextTaskId = (int) taskObj.get("taskid") + 10;
                    String woSequence = taskObj.get("wosequence").toString();
                    String description = taskObj.get("description").toString();
                    String parent = taskObj.get("parent").toString();
                    LogUtil.info(getClassName(), "parent: " + parent);
                    LogUtil.info(getClassName(), "wosequence: " + woSequence);
                    LogUtil.info(getClassName(), "description: " + description);
                
                
                } else if (task.equalsIgnoreCase("updateTaskAttributes")) {
                    LogUtil.info(getClass().getName(), "Start Process: Update Task Attribute MyStaff");
                    Object request = (Object)body.get("workorderspec");
                    JSONArray request_wospec = new JSONArray();
                    JSONArray data = new JSONArray();
                    boolean updateAttr = false;
                    
                    if (request instanceof JSONObject){
                        request_wospec.add(request);
                    } else if (request instanceof JSONArray) {
                        request_wospec = (JSONArray) request;
                    }
                    
                    for (Object obj : request_wospec) {
                        JSONObject attrObj = (JSONObject)obj;
                        JSONObject resp = new JSONObject();
                        String wonum = (attrObj.get("wonum") == null ? "" : attrObj.get("wonum").toString());
                        String assetAttrId = (attrObj.get("assetattrid") == null ? "" : attrObj.get("assetattrid").toString());
                        String value = (attrObj.get("alnvalue") == null ? "" : attrObj.get("alnvalue").toString());
                        String siteid = (attrObj.get("siteid") == null ? "" : attrObj.get("siteid").toString());
                        String changeDate = (attrObj.get("changedate") == null ? "" : attrObj.get("changedate").toString());
                        String changeBy = (attrObj.get("changeby") == null ? "" : attrObj.get("changeby").toString());
                        
                        updateAttr = daoAttr.updateAttributeMyStaff(wonum, siteid, assetAttrId, value, changeBy, modifiedBy, changeDate);
                        resp.put("wonum", wonum);
                        resp.put("siteid", siteid);
                        resp.put("attribute name ", assetAttrId);
                        resp.put("attribute value", value);
                        data.add(resp);
                        
                        JSONObject getTaskUpdate = dao.getTask(wonum);
                        taskObj.put("taskid", (int) getTaskUpdate.get("taskid"));
                        taskObj.put("wosequence", getTaskUpdate.get("wosequence"));
                        taskObj.put("detailactcode", getTaskUpdate.get("detailactcode"));
                        taskObj.put("description", getTaskUpdate.get("description"));
                        taskObj.put("parent", getTaskUpdate.get("parent"));
                    }
                    
                    String woSequence = taskObj.get("wosequence").toString();
                    String description = taskObj.get("description").toString();
                    String parent = taskObj.get("parent").toString();
                    LogUtil.info(getClassName(), "taskObj: " + taskObj);
                    LogUtil.info(getClassName(), "parent: " + parent);
                    LogUtil.info(getClassName(), "wosequence: " + woSequence);
                    LogUtil.info(getClassName(), "description: " + description);
                    JSONObject res = new JSONObject(); 
                    
                    if (updateAttr) {
                        hsr1.setStatus(200);
                        res.put("code", 200);
                        res.put("message", "Success");
                        res.put("data", data);
                        res.writeJSONString(hsr1.getWriter());
                    } else {
                        hsr1.sendError(400, "Failed update task attribute");
                        res.put("code", 404);
                        res.put("message", "Failed");
                        res.put("data", data);
                        res.writeJSONString(hsr1.getWriter());
                    }
                } else {
                    LogUtil.info(getClassName(), "Task request is not found");
                }
            } catch (ParseException | SQLException | IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        //Authorization failed 
        } else if (!methodStatus) {
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else {
            try {
                hsr1.sendError(401, "Invalid Authentication");
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        }
    }
}