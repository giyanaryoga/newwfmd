/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.GenerateStpNetLocDao;
import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.model.ListAttributes;
import id.co.telkom.wfm.plugin.model.ListDeviceAttribute;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSON;
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
public class GenerateStpNetLoc extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Generate STP Network Location - Web Service";

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
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException, MalformedURLException {
        GenerateStpNetLocDao dao = new GenerateStpNetLocDao();
        

        //@@Start..
        LogUtil.info(this.getClass().getName(), "############## START PROCESS GENERATE STP NETWORK LOCATION ###############");

        //@Authorization
        if ("POST".equals(hsr.getMethod())) {
            try {
                //@Parsing message
                //HttpServletRequest get JSON Post data
                StringBuffer jb = new StringBuffer();
                String line = null;
                try {//read the response JSON to string buffer
                    BufferedReader reader = hsr.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }
                } catch (Exception e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                
                ListAttributes attribute = new ListAttributes();
                //Parse JSON String to JSON Object
                String bodyParam = jb.toString(); //String
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject) parser.parse(bodyParam);//JSON Object
                //Store param
                String wonum = data_obj.get("wonum").toString();
                String latitude = attribute.getLatitude(dao.getDeviceLocation(wonum).get("LATITUDE").toString());
                String longitude = attribute.getLongitude(dao.getDeviceLocation(wonum).get("LONGITUDE").toString());
//                HashMap<String, String> data = new HashMap<String, String>();
                
                try {
//                      dao.callGenerateStpNetLoc(latitude, longitude);
                      dao.callGenerateStpNetLoc(latitude, longitude);
//                      dao.moveFirst(wonum);
//                      dao.insertToDeviceTable(wonum, data);
//                    if(data == null) {
//                        JSONObject res1 = new JSONObject();
//                        res1.put("code", 404);
//                        res1.put("message", "No Device with the locations");
//                        res1.writeJSONString(hsr1.getWriter());
//                    } else {
//                        JSONObject res1 = new JSONObject();
//                        res1.put("code", 200);
//                        res1.put("message", "update data successfully");
//                        res1.put("data", data);
//                        res1.writeJSONString(hsr1.getWriter());
//                    }
                } catch (Exception ex) {
                    Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Throwable ex) {
                    Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                // Call API Surrounding using request header
//            if (hsr.getParameterMap().containsKey("latitude") && hsr.getParameterMap().containsKey("longitude")) {
//                String latitude = hsr.getParameter("latitude");
//                String longitude = hsr.getParameter("longitude");
//                try {
//                    generateStpNetLoc.callGenerateStpNetLoc(latitude, longitude);
//                } catch (Exception ex) {
//                    Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
            } catch (ParseException ex) {
                Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
            } catch (JSONException ex) {
                Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (!"POST".equals(hsr.getMethod())) {
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (Exception e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        }
    }
}
