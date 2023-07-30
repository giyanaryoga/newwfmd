/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.UpdateTKWoSpecDao;
//import id.co.telkom.wfm.plugin.util.JsonUtil;
//import id.co.telkom.wfm.plugin.util.TimeUtil;
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
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author User
 */
public class UpdateTkWoSpec extends Element implements PluginWebSupport {
    String pluginName = "Telkom New WFM - Update Task Attribute MyStaff - Web Service";
    
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
//        TimeUtil time = new TimeUtil();
//        JsonUtil json = new JsonUtil();
        //@Authorization
        //Plugin API configuration
        UpdateTKWoSpecDao dao = new UpdateTKWoSpecDao();
        boolean isAuthSuccess = false;
        try {
            isAuthSuccess = dao.getApiAttribute(hsr.getHeader("api_id"), hsr.getHeader("api_key"));
        } catch (SQLException e) {
             LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
        };
        //Conditional checking
        boolean methodStatus = false;
        //Checking
        if ("POST".equals(hsr.getMethod()))
            methodStatus = true;
        //Authorization success
        if (methodStatus && isAuthSuccess) {
            try {
                //@Parsing message
                //HttpServletRequest get JSON Post data
                StringBuilder jb = new StringBuilder();
                String line;
                try{//Read the response JSON to string buffer
                    BufferedReader reader = hsr.getReader();
                    while ((line = reader.readLine()) != null)
                        jb.append(line);
                } catch (IOException e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                //Parse JSON String to JSONObject
                String bodyParam = jb.toString();
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject)parser.parse(bodyParam);
                JSONObject envelope = (JSONObject)data_obj.get("Envelope");
                JSONObject header = (JSONObject)envelope.get("Header");
                JSONObject body = (JSONObject)envelope.get("Body");
                JSONObject tkwospec = (JSONObject)body.get("tkmyiTkwospecupdate");
                JSONObject updateTKWO = (JSONObject)tkwospec.get("UpdateTKWOSPEC");
                JSONObject tkwoset = (JSONObject)updateTKWO.get("TKWOSPECSet");
                Object request = (Object)tkwoset.get("WORKORDERSPEC");
                JSONArray request_wospec = new JSONArray();
                //@Store param
                //Store JSONObject to Work Order param
//                String value = (request.get("ALNVALUE") == null ? "" : request.get("ALNVALUE").toString());
//                String assetAttrId = request.get("ASSETATTRID").toString();
//                String changeBy = (request.get("CHANGEBY") == null ? "" : request.get("CHANGEBY").toString());
//                String changeDate = (request.get("CHANGEDATE") == null ? "" : request.get("CHANGEDATE").toString());
//                String siteId = (request.get("SITEID") == null ? "" : request.get("SITEID").toString());
//                String wonum = request.get("WONUM").toString();
//                req.add(request);
                boolean updateTaskAttr = false;
                
                if (request instanceof JSONObject){
                    request_wospec.add(request);
                } else if (request instanceof JSONArray) {
                    request_wospec = (JSONArray) request;
                }
                
                JSONArray data = new JSONArray();
                
                for(Object obj : request_wospec) {
                    JSONObject updateRequest = (JSONObject)obj;
                    JSONObject resp = new JSONObject();
                    
                    String value = (updateRequest.get("ALNVALUE") == null ? "" : updateRequest.get("ALNVALUE").toString());
                    String assetAttrId = updateRequest.get("ASSETATTRID").toString();
                    String changeBy = (updateRequest.get("CHANGEBY") == null ? "" : updateRequest.get("CHANGEBY").toString());
                    String changeDate = (updateRequest.get("CHANGEDATE") == null ? "" : updateRequest.get("CHANGEDATE").toString());
                    String siteId = (updateRequest.get("SITEID") == null ? "" : updateRequest.get("SITEID").toString());
                    String wonum = updateRequest.get("WONUM").toString();
                    
                    updateTaskAttr = dao.updateAttributeMyStaff(wonum, siteId, assetAttrId, value, changeBy, changeDate);
                    resp.put("wonum", wonum);
                    resp.put("siteid", siteId);
                    resp.put("attribute name ", assetAttrId);
                    resp.put("attribute value", value);
                    data.add(resp);
                }
                
//                String schedStart = (body.get("SCHEDSTART") == null ? "" : time.parseDate(body.get("SCHEDSTART").toString(), "yyyy-MM-dd HH:mm:ss"));
//                DateTimeFormatter currentDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//                String statusDate = time.getCurrentTime();
                JSONObject res = new JSONObject(); 
                //@@End
                //@Response
                if (updateTaskAttr) {
                    res.put("code", "200");
                    res.put("message", "Success");
                    res.put("data", data);
                    res.writeJSONString(hsr1.getWriter());
                } else {
                    res.put("code", "404");
                    res.put("message", "Wonum not found");
                    res.writeJSONString(hsr1.getWriter());
                }
            } catch (ParseException e){
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(UpdateTkWoSpec.class.getName()).log(Level.SEVERE, null, ex);
            }
        //Authorization failed    
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
