/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.dao.ValidateStoDao;
import id.co.telkom.wfm.plugin.model.ListAttributes;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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

/**
 *
 * @author ASUS
 */
public class ValidateSto extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Validate STO - Web Service";

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
        LogUtil.info(getClass().getName(), "Start Process: Generate Validate STO");
        //@Authorization
        try {
            ValidateStoDao dao = new ValidateStoDao();
            JSONObject result = new JSONObject();
            // Store Params
            if (hsr.getParameterMap().containsKey("wonum")) {
                String wonum = hsr.getParameter("wonum") == null ? "" : hsr.getParameter("wonum");
//                String lat = hsr.getParameter("lat");
//                String lon = hsr.getParameter("lon");
//                String serviceType = hsr.getParameter("serviceType");
               JSONObject data = dao.getAssetattrid(wonum);
                LogUtil.info(getClassName(), "List Attribute :" + data);
                dao.getAssetattrid(wonum);
            }

        } catch (Exception e) {
            LogUtil.error(getClassName(), e, "Trace Error Here : " + e.getMessage());
        }
    }

}
