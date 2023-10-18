/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.FalloutIncidentDao;
import id.co.telkom.wfm.plugin.dao.RollbackStatusDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ASUS
 */
public class RollbackStatus extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Rollback Status - Web Service";

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
                String parent = (body.get("parent") == null ? "" : body.get("parent").toString());
                String wonum = (body.get("wonum") == null ? "" : body.get("wonum").toString());

                RollbackStatusDao dao = new RollbackStatusDao();
                boolean updateTask = dao.rollbackStatus(parent,wonum, "WFM");
                LogUtil.info(this.getClassName(), "update status : " + updateTask);
                
                if (updateTask == true) {
                    JSONObject res = new JSONObject();
                    res.put("code", 200);
                    res.put("message", "Berhasil mengupdate status");
//                    res.put("data", data);
                    res.writeJSONString(hsr1.getWriter());
                    hsr1.setStatus(200);
                    LogUtil.info(this.getClassName(), "update TRUE : " + updateTask);
                } else {
                    JSONObject res = new JSONObject();
                    res.put("code", 422);
                    res.put("message", "Gagal mengupdate status");
                    res.writeJSONString(hsr1.getWriter());
                    hsr1.setStatus(422);
                }
            } catch (ParseException | SQLException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            } catch (JSONException ex) {
                Logger.getLogger(RollbackStatus.class.getName()).log(Level.SEVERE, null, ex);
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
