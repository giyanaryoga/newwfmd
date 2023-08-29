/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.controller.validateNonCoreProduct;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import id.co.telkom.wfm.plugin.controller.validateTaskStatus;
import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import id.co.telkom.wfm.plugin.util.ResponseAPI;
import java.io.*;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import org.joget.apps.form.model.*;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

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

                validateTaskStatus validateTask = new validateTaskStatus();
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
                String errorCode;
                String engineerMemo;
                String memo = (body.get("memo") == null ? "" : body.get("memo").toString());
                String modifiedBy = (body.get("modifiedBy") == null ? "" : body.get("modifiedBy").toString());
                String currentDate = time.getCurrentTime();

                param.setParent(parent);
                param.setWonum(wonum);
                param.setDescription(description);
                param.setSiteId(siteId);
                param.setStatus(status);
                param.setTaskId(taskId);
                param.setWoStatus(woStatus);
                param.setMemo(memo);
                param.setModifiedBy(modifiedBy);
                param.setCurrentDate(currentDate);
                boolean validate = false;
                boolean validatenoncore = false;
                String message = "";

                switch (status) {
                    case "STARTWA":
                        validate = validateTask.startTask(param);
                        if (validate) {
                            message = "Successfully update status";
                            res = responseTemplete.getUpdateStatusSuccessResp(param.getWonum(), param.getStatus(), message);
                            res.writeJSONString(hsr1.getWriter());
                        } else {
                            message = "Please assign task to Laborcode / Amcrew first";
                            res = responseTemplete.getUpdateStatusErrorResp(param.getWonum(), param.getStatus(), message, 422);
                            res.writeJSONString(hsr1.getWriter());
                        }
                        break;
                    case "COMPWA":
                        validate = validateTask.compwaTask(param);
                        JSONObject response = validateTask.validateTask(param);
                        if (validate) {
                            res = responseTemplete.getUpdateStatusSuccessResp(param.getWonum(), param.getStatus(), response.get("message").toString());
                            res.writeJSONString(hsr1.getWriter());
                        } else {
                            message = "Please insert Task Attribute in Mandatory";
                            res = responseTemplete.getUpdateStatusErrorResp(param.getWonum(), param.getStatus(), message, 422);
                            res.writeJSONString(hsr1.getWriter());
                        }
                        break;
                    default:
                        message = "Status Task is not found";
                        res = responseTemplete.getUpdateStatusErrorResp(param.getWonum(), param.getStatus(), message, 422);
                        res.writeJSONString(hsr1.getWriter());
                        break;
                }
            } catch (ParseException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            } catch (JSONException ex) {
                Logger.getLogger(TestUpdateStatusEbis.class.getName()).log(Level.SEVERE, null, ex);
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
