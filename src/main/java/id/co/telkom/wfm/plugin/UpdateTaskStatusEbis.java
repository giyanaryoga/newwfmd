/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.util.TimeUtil;
import id.co.telkom.wfm.plugin.controller.ValidateTaskStatus;
import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import id.co.telkom.wfm.plugin.util.ResponseAPI;
import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;
//import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.*;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.JSONException;
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
        WorkflowUserManager userMgr = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        boolean anonUser = userMgr.isCurrentUserAnonymous();
        if (!anonUser && "POST".equals(hsr.getMethod())) {
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

                ValidateTaskStatus validateTask = new ValidateTaskStatus();
                UpdateStatusParam param = new UpdateStatusParam();
                ResponseAPI responseTemplete = new ResponseAPI();
                JSONObject res = new JSONObject();

                //Store param
                String status = (body.get("status") == null ? "" : body.get("status").toString());
                String wonum = (body.get("wonum") == null ? "" : body.get("wonum").toString());
                String parent = (body.get("parent") == null ? "" : body.get("parent").toString());
                String taskId = (body.get("taskId") == null ? "" : body.get("taskId").toString());
                String siteId = (body.get("siteId") == null ? "" : body.get("siteId").toString());
                String woSequence = (body.get("woSequence") == null ? "" : body.get("woSequence").toString());
                String woStatus = (body.get("woStatus") == null ? "" : body.get("woStatus").toString());
                String description = (body.get("description") == null ? "" : body.get("description").toString());
                String activity = (body.get("activity") == null ? "" : body.get("activity").toString());
                String errorCode = (body.get("errorCode") == null ? "" : body.get("errorCode").toString());
                String engineerMemo = (body.get("engineerMemo") == null ? "" : body.get("engineerMemo").toString());
                String memo = (body.get("memo") == null ? "" : body.get("memo").toString());
                String modifiedBy = (body.get("modifiedBy") == null ? "" : body.get("modifiedBy").toString());
                String modifiedByName = (body.get("modifiedByName") == null ? "" : body.get("modifiedByName").toString());
                String chiefcode = (body.get("chiefcode") == null ? "" : body.get("chiefcode").toString());
                String currentDate = time.getCurrentTime();

                param.setParent(parent);
                param.setWonum(wonum);
                param.setDescription(description);
                param.setActivity(activity);
                param.setSiteId(siteId);
                param.setStatus(status);
                param.setTaskId(taskId);
                param.setWoStatus(woStatus);
                param.setMemo(memo);
                param.setModifiedBy(modifiedBy);
                param.setModifiedByName(modifiedByName);
                param.setChiefcode(chiefcode);
                param.setCurrentDate(currentDate);
                param.setErrorCode(errorCode);
                param.setEngineerMemo(engineerMemo);
                param.setSequence(woSequence);
                String message = "";

                //GET BYPASS CONFIGURATION
//                AppDefinition AppDef = AppUtil.getCurrentAppDefinition();
//                String compViaUi = "#envVariable.compViaUi#";
//                LogUtil.info(getClass().getName(), "compwaUI: " + compViaUi);
//                compViaUi = AppUtil.processHashVariable(compViaUi, null, null, null, AppDef);
//                validateTask.taskMandatoryVariable(param.getChiefcode(), compViaUi, param.getWonum());
                
                switch (status) {
                    case "STARTWA":
                        boolean validateStarwa = validateTask.startTask(param);
                        if (validateStarwa) {
                            message = "Successfully update status";
                            res = responseTemplete.getUpdateStatusSuccessResp(param.getWonum(), param.getStatus(), message);
                            res.writeJSONString(hsr1.getWriter());
                        } else {
                            message = "Please assign task to Laborcode / Amcrew first";
                            hsr1.sendError(422, message);
                        }
                        break;
                    case "COMPWA":
                        String validateCompwa = validateTask.compwaTask(param);
                        LogUtil.info(getClass().getName(), "VALIDATE: " + validateCompwa);
                        if (validateCompwa.equalsIgnoreCase("true") || validateCompwa.equalsIgnoreCase("")) {
                            JSONObject response = validateTask.validateTask(param);
                            if ((int) response.get("code") == 200) {
                                res = responseTemplete.getUpdateStatusSuccessResp(param.getWonum(), param.getStatus(), response.get("message").toString());
                                res.writeJSONString(hsr1.getWriter());
                            } else {
                                hsr1.sendError((int) response.get("code"), response.get("message").toString());
                            }
                        } else {
                            message = "Please insert Task Attribute in Mandatory";
                            hsr1.sendError(422, message);
                        }
                        break;
                    default:
                        message = "Status Task is not found";
                        hsr1.sendError(422, message);
                        break;
                }
            } catch (ParseException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            } catch (JSONException ex) {
                Logger.getLogger(UpdateTaskStatusEbis.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (anonUser || !"POST".equals(hsr.getMethod())) {
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        }
    }
}
