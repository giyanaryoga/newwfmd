/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.GenerateFalloutDao;
import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
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
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.plugin.base.PluginWebSupport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ASUS
 */
public class GenerateFallout extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Generate Fallout - Web Service";

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
        final JSONObject res = new JSONObject();

        GenerateFalloutDao dao = new GenerateFalloutDao();

        dao.getApiAttribute();
        String apiIdPlugin = dao.apiId;
        String apiKeyPlugin = dao.apiKey;

        String headerApiId = hsr.getHeader("api_id");
        String headerApiKey = hsr.getHeader("api_key");

        boolean methodStatus = false;
        boolean authStatus = false;
        //Checking
        if ("POST".equals(hsr.getMethod())) {
            methodStatus = true;
        }
        if (apiIdPlugin.equals(headerApiId) && apiKeyPlugin.equals(headerApiKey)) {
            authStatus = true;
        }
        //Authorization success
        if (methodStatus && authStatus) {
            try {
                //@Parsing message
                //HttpServletRequest get JSON Post data
                StringBuilder jb = new StringBuilder();
                String line;
                try {//Read the response JSON to string buffer
                    BufferedReader reader = hsr.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }
                } catch (IOException e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                //Parse JSON String to JSONObject
//                String id = UuidGenerator.getInstance().getUuid();//generating uuid
                String bodyParam = jb.toString();
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject) parser.parse(bodyParam);

                //'Start' log
                LogUtil.info(getClass().getName(), "Start Process: Generate Fallout |  Ticket ID: " + (data_obj.get("ticketId") == null ? "" : data_obj.get("ticketId").toString()));
                //@Store param
                //Store JSONObject to Work Order param
//                String ownerGroup = (data_obj.get("ownerGroup") == null ? "" : data_obj.get("ownerGroup").toString());
                String customerType = (data_obj.get("customerType") == null ? "" : data_obj.get("customerType").toString());
                String workzone = (data_obj.get("workzone") == null ? "" : data_obj.get("workzone").toString());
                String region = (data_obj.get("region") == null ? "" : data_obj.get("region").toString());
                String classification = (data_obj.get("classification") == null ? "" : data_obj.get("classification").toString());
                String ossid = (data_obj.get("ossid") == null ? "" : data_obj.get("ossid").toString());
//                String statusCode = (data_obj.get("statusCode") == null ? "" : data_obj.get("statusCode").toString());
                String statusCode = "OPEN";
                String internalPriority = (data_obj.get("internalPriority") == null ? "" : data_obj.get("internalPriority").toString());
                String externalSystem = (data_obj.get("externalSystem") == null ? "" : data_obj.get("externalSystem").toString());
                String description = (data_obj.get("description") == null ? "" : data_obj.get("description").toString());
                String tk_channel = (data_obj.get("tk_channel") == null ? "" : data_obj.get("tk_channel").toString());
                String longDescription = (data_obj.get("longDescription") == null ? "" : data_obj.get("longDescription").toString());
                String assetNum = (data_obj.get("assetNum") == null ? "" : data_obj.get("assetNum").toString());
//                String currentDate = time.getCurrentTime();

                //@Main process start..
                //Generate wonum with counter function from DB
                String ticketId = dao.getTicketid();

                final boolean insertFallout = dao.insertToWoTable(externalSystem, longDescription, ossid, region, customerType, workzone, classification, description, internalPriority, statusCode, ticketId, tk_channel, assetNum);

                if (ticketId != null && insertFallout) {
                    try {
                        //Success response
                        LogUtil.info(getClassName(), "Process End - Fallout Generated [" + ticketId + "]");
                        LogUtil.info(getClassName(), "Send response to 'Engine' and 'WFM Kafka'");
                        String statusHeaders = "200";
                        String statusRequest = "Success";
                        //Create response
                        JSONObject data = new JSONObject();
                        data.put("TICKETID", ticketId);
                        data.put("REGION", region);
                        JSONObject res1 = new JSONObject();
                        res1.put("code", statusHeaders);
                        res1.put("message", statusRequest);
                        res1.put("data", data);
                        res1.writeJSONString(hsr1.getWriter());
                    } catch (IOException e) {
                        LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                    }  
                }
            } catch (ParseException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }

        } else if (!methodStatus) {
            try {
                hsr1.sendError(405, "Method Not Allowed");
                res.writeJSONString(hsr1.getWriter());
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else {
            try {
                hsr1.sendError(401, "Invalid Authentication");
                res.writeJSONString(hsr1.getWriter());
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        }
    }

}


