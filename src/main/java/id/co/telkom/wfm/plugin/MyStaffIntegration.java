/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.controller.ValidateMyStaff;
import id.co.telkom.wfm.plugin.model.MyStaffParam;
import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
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
        //@@Start..
        UpdateTaskStatusEbisDao dao = new UpdateTaskStatusEbisDao();
        ValidateMyStaff validate = new ValidateMyStaff();
        MyStaffParam paramAttr = new MyStaffParam();
        UpdateStatusParam param = new UpdateStatusParam();
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
                //Store param
                String task = (body.get("task") == null ? "" : body.get("task").toString());
                paramAttr.setTask(task);
                String modifiedBy = "MyStaff";
                param.setModifiedBy(modifiedBy);
                JSONObject response = new JSONObject();
                
                if (paramAttr.getTask().equalsIgnoreCase("updateTaskStatus")) {
                    JSONObject res = validate.updateTaskStatus(param, body);
                    int responseCode = (int) res.get("code");
                    if (responseCode == 200) {
                        response.put("code", 200);
                        response.put("message", res.get("message").toString());
                        response.writeJSONString(hsr1.getWriter());
                    } else {
                        hsr1.sendError(422, res.get("message").toString());
                        res.put("code", 422);
                        res.put("message", res.get("message").toString());
                    }
                } else if (paramAttr.getTask().equalsIgnoreCase("updateTaskAttributes")) {
                    JSONObject res = validate.updateTaskAttr(paramAttr, body);
                    int responseCode = (int) res.get("code");
                    if (responseCode == 200) {
                        response.put("code", 200);
                        response.put("message", "Success Updated Task Attribute!");
                        response.put("body", res.get("data"));
                        response.writeJSONString(hsr1.getWriter());
                    } else {
                        hsr1.sendError(422, "Failed update task attribute");
                        res.put("code", 422);
                        res.put("message", "Failed update task attribute");
                        res.put("data", res.get("data"));
                    }
                } else {
                    LogUtil.info(getClassName(), "Task request is not found");
                }
            } catch (ParseException | IOException e) {
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