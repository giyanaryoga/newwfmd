/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.TaskAttributeDao;
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
 * @author User
 */
public class TaskAttributeEbis extends Element implements PluginWebSupport {
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
    //@@Start..
        LogUtil.info(getClass().getName(), "Start Process: Get Task Attribute");
    //@Authorization
        if ("POST".equals(hsr.getMethod())) {
            //@Parsing message
            //HttpServletRequest get JSON Post data
            StringBuilder jb = new StringBuilder();
            String line = "";
            try {//read the response JSON to string buffer
                BufferedReader reader = hsr.getReader();
                while((line = reader.readLine()) !=null)
                    jb.append(line);
                TaskAttributeDao dao = new TaskAttributeDao();
                JSONArray response = new JSONArray();
                
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                //Parse JSON String to JSON Object
                String bodyParam = jb.toString(); //String
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject)parser.parse(bodyParam);//JSON Object
                //Store param
                String wonum = data_obj.get("wonum").toString();
                String classStructureId = data_obj.get("classstructureid").toString();
                
                response = dao.getAttribute(wonum, classStructureId);
                
                if (!response.isEmpty()) {
                    String statusHeaders = "200";
                    String statusRequest = "Success";
                    JSONObject res1 = new JSONObject();
                    res1.put("code", statusHeaders);
                    res1.put("message", statusRequest);
                    res1.put("data", response);
                } else {
                    String statusHeaders = "422";
                    String statusRequest = "Failed";
                    JSONObject res1 = new JSONObject();
                    res1.put("code", statusHeaders);
                    res1.put("message", statusRequest);
                    res1.put("data", "Data is not found");
                }
            } catch (IOException | SQLException | ParseException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else if (!"POST".equals(hsr.getMethod())){
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            } 
        }
    }
}
