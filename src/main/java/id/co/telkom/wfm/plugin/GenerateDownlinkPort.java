/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.GenerateDownlinkPortDao;
import id.co.telkom.wfm.plugin.model.ListGenerateAttributes;
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

/**
 *
 * @author ASUS
 */
public class GenerateDownlinkPort extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Generate Downlink Port - Web Service";

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
        GenerateDownlinkPortDao dao = new GenerateDownlinkPortDao();

        //@@Start..
        LogUtil.info(this.getClass().getName(), "############## START PROCESS GENERATE DOWNLINK PORT ###############");

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
                } catch (IOException e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());

                //Parse JSON String to JSON Object
                String bodyParam = jb.toString(); //String
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject) parser.parse(bodyParam);//JSON Object
                //Store param
                String bandwidth = data_obj.get("bandwidth").toString();
                String odpName = data_obj.get("odpName").toString();
                String downlinkPortName = data_obj.get("downlinkPortName").toString();
                String downlinkPortID = data_obj.get("downlinkPortID").toString();
                String sto = data_obj.get("sto").toString();
                
//                String orderId = data_obj.get("orderId").toString();
                ListGenerateAttributes listAttribute = new ListGenerateAttributes();
                try {
                    dao.callGenerateDownlinkPort(bandwidth, odpName, downlinkPortName, downlinkPortID, sto, listAttribute);
//                    LogUtil.info(this.getClassName(), "get data: " + listAttribute.getStatusCode3());

                    if (listAttribute.getStatusCode3() == 404) {
                        JSONObject res1 = new JSONObject();
                        res1.put("code", 404);
                        res1.put("message", "No Service found!.");
                        res1.writeJSONString(hsr1.getWriter());
//                        dao.insertIntegrationHistory(wonum, line, wonum, wonum, orderId);
                    } else {
//                        dao.moveFirst(wonum);
//                        dao.insertIntoDeviceTable(wonum, listAttribute);
//                        dao.insertIntegrationHistory(wonum, line, wonum, wonum, orderId);
                        JSONObject res = new JSONObject();
                        res.put("code", 200);
                        res.put("message", "update data successfully");
                        res.writeJSONString(hsr1.getWriter());
                    }
                } catch (IOException ex) {
                    Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (Exception ex) {
                Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
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
