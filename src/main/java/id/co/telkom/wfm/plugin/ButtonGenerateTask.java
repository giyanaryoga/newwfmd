/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.controller.ValidateGenerateTask;
import id.co.telkom.wfm.plugin.controller.ValidateMilestone;
import id.co.telkom.wfm.plugin.util.ResponseAPI;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.app.service.AppUtil;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.joget.workflow.model.service.WorkflowUserManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class ButtonGenerateTask extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Button Generate Task - Web Service";

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
        LogUtil.info(getClass().getName(), "Start Process: Button Generate Task");
        WorkflowUserManager userMgr = (WorkflowUserManager) AppUtil.getApplicationContext().getBean("workflowUserManager");
        boolean anonUser = userMgr.isCurrentUserAnonymous();
        //@Authorization
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
                JSONObject body = (JSONObject) parser.parse(bodyParam);//JSON Object

                ResponseAPI responseTemplete = new ResponseAPI();
                JSONObject res = new JSONObject();
                String message = "";
                ValidateGenerateTask validateGenerate = new ValidateGenerateTask();
                ValidateMilestone validateMilestone = new ValidateMilestone();

                String button = (body.get("button") == null ? "" : body.get("button").toString());
                String parent = (body.get("parent") == null ? "" : body.get("parent").toString());
                String siteid = (body.get("siteid") == null ? "" : body.get("siteid").toString());

                if (button.equalsIgnoreCase("regenTask")) {
                    boolean validate = validateGenerate.generateButton(parent);
                    if (validate) {
                        message = "Successfully update task";
                        res.put("code", 200);
                        res.put("message", message);
                        res.writeJSONString(hsr1.getWriter());
                    } else {
                        message = "Task is not generate!";
                        hsr1.sendError(422, message);
                    }
                } else if (button.equalsIgnoreCase("triggerMilestone")) {
                    boolean validate = validateMilestone.triggerMilestone(parent, siteid);
                    LogUtil.info(getClass().getName(), "Trigger Milestone : " + validate);
                    if (validate) {
                        res.put("code", 200);
                        res.put("message", "Milestone COMPLETE Berhasil Dikirim Ulang");
                        res.writeJSONString(hsr1.getWriter());
                    } else {
                        res.put("code", 422);
                        res.put("message", "Milestone COMPLETE tidak dapat dikirim ulang, hanya milestone berikut yang diperbolehkan: COMPLETE.");
                        res.writeJSONString(hsr1.getWriter());
                    }
                }

            } catch (ParseException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(ButtonGenerateTask.class.getName()).log(Level.SEVERE, null, ex);
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
