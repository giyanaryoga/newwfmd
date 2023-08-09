/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.FalloutIncidentDao;
import id.co.telkom.wfm.plugin.dao.GenerateFalloutDao;
import id.co.telkom.wfm.plugin.dao.IntegrationFalloutDao;
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
public class IntegrationFallout extends Element implements PluginWebSupport {

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
        LogUtil.info(getClass().getName(), "Start Process: Update Task Status");
        final JSONObject res = new JSONObject();

        IntegrationFalloutDao dao = new IntegrationFalloutDao();
        dao.getApiAttribute();
        String apiIdPlugin = dao.apiId;
        String apiKeyPlugin = dao.apiKey;

        String headerApiId = hsr.getHeader("api_id");
        String headerApiKey = hsr.getHeader("api_key");

        boolean methodStatus = "POST".equals(hsr.getMethod());
        boolean authStatus = apiIdPlugin.equals(headerApiId) && apiKeyPlugin.equals(headerApiKey);

        if (methodStatus && authStatus) {
            try {
                StringBuilder jb = new StringBuilder();
                String line;
                try {
                    BufferedReader reader = hsr.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }
                } catch (IOException e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());

                String bodyParam = jb.toString();
                JSONParser parser = new JSONParser();
                JSONObject body = (JSONObject) parser.parse(bodyParam);

                String statusCode = body.getOrDefault("statusCode", "").toString();
                String ticketId = body.getOrDefault("ticketId", "").toString();

                boolean updateTask = dao.updateStatus(statusCode, ticketId);
                LogUtil.info(getClassName(), "update status : " + updateTask);

                res.put("code", updateTask ? 200 : 422);
                res.put("message", updateTask ? "Successfully update status" : "Update data Failed");

                HttpServletResponse response = hsr1;
                response.setStatus(updateTask ? 200 : 422);
                res.writeJSONString(response.getWriter());

                LogUtil.info(getClassName(), "update " + (updateTask ? "TRUE" : "FALSE") + ": " + updateTask);
            } catch (ParseException | SQLException | IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else {
            try {
                int statusCode = authStatus ? 405 : 401;
                String errorMsg = authStatus ? "Method Not Allowed" : "Invalid Authentication";

                HttpServletResponse response = hsr1;
                response.sendError(statusCode, errorMsg);

                res.put("code", statusCode);
                res.put("message", errorMsg);
                res.writeJSONString(response.getWriter());
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        }

    }

}
