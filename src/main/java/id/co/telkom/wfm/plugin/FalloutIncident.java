/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.FalloutIncidentDao;
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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ASUS
 */
public class FalloutIncident extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Fallout Incident - Web Service";

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
                JSONObject body = (JSONObject) parser.parse(bodyParam);

                //Store param
                String statusCode = (body.get("statusCode") == null ? "" : body.get("statusCode").toString());
                String ticketId = (body.get("ticketId") == null ? "" : body.get("ticketId").toString());
                String ownerGroup = (body.get("ownerGroup") == null ? "" : body.get("ownerGroup").toString());

                FalloutIncidentDao dao = new FalloutIncidentDao();
                boolean updateTask = dao.updateStatus(statusCode, ticketId, ownerGroup);
                LogUtil.info(this.getClassName(), "update status : " + updateTask);

                JSONObject res = new JSONObject();
                if (statusCode.equals("OPEN")) {
                    res.put("code", 422);
                    res.put("message", "Select resolve code 'RETRY or MANUAL' before saving");
                    res.writeJSONString(hsr1.getWriter());
                    hsr1.setStatus(200);
                } else {
                    if (updateTask == true) {
                        dao.buildFalloutJson(ticketId);

                        res.put("code", 200);
                        res.put("message", "Success mengirim data ke kafka");
//                    res.put("data", data);
                        res.writeJSONString(hsr1.getWriter());
                        hsr1.setStatus(200);
                        LogUtil.info(this.getClassName(), "update TRUE : " + updateTask);
                    } else {
                        res.put("code", 422);
                        res.put("message", "Send Message Failed");
                        res.writeJSONString(hsr1.getWriter());
                        hsr1.setStatus(422);
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

}
