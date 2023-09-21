/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import id.co.telkom.wfm.plugin.controller.validateGenerateTask;
import id.co.telkom.wfm.plugin.controller.validateOwnerGroup;
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
import org.joget.plugin.base.PluginWebSupport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author giyanaryoga
 */
public class GenerateWonumEbis extends Element implements PluginWebSupport {
    String pluginName = "Telkom New WFM - Generate Wonum - Web Service";
    
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
        TimeUtil time = new TimeUtil();
//        JsonUtil json = new JsonUtil();
        final JSONObject res = new JSONObject(); 
        //@Authorization
        //Plugin API configuration
        GenerateWonumEbisDao dao = new GenerateWonumEbisDao();
        validateGenerateTask validateTask = new validateGenerateTask();
//        validateOwnerGroup validateOwnerGroup = new validateOwnerGroup();
        dao.getApiAttribute();
        String apiIdPlugin = dao.apiId;
        String apiKeyPlugin = dao.apiKey;
        //Requester API configuration
        String headerApiId = hsr.getHeader("api_id");
        String headerApiKey = hsr.getHeader("api_key");
        //Conditional checking
        boolean methodStatus = false;
        boolean authStatus = false;
        //Checking
        if ("POST".equals(hsr.getMethod()))
            methodStatus = true;
        if (apiIdPlugin.equals(headerApiId) && apiKeyPlugin.equals(headerApiKey))
            authStatus = true;
        //Authorization success
        if (methodStatus && authStatus) {
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
//                String id = UuidGenerator.getInstance().getUuid();//generating uuid
                String bodyParam = jb.toString();
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject)parser.parse(bodyParam);
                JSONObject root = (JSONObject)data_obj.get("CreateMXTELKOWO");
                JSONObject section = (JSONObject)root.get("MXTELKOWOSet");
                JSONObject body = (JSONObject)section.get("WORKORDER");
                //'Start' log
                LogUtil.info(getClass().getName(), "Start Process: Generate Wonum | SC Order No: " + (body.get("SCORDERNO") == null ? "" : body.get("SCORDERNO").toString()));
                //@Store param
                //Store JSONObject to Work Order param
                float duration = 0;
                JSONObject workorder = new JSONObject();
                workorder.put("crmOrderType", (body.get("CRMORDERTYPE") == null ? "" : body.get("CRMORDERTYPE").toString()));
                workorder.put("custName", (body.get("CUSTOMER_NAME") == null ? "" : body.get("CUSTOMER_NAME").toString()));
                workorder.put("description", (body.get("DESCRIPTION") == null ? "" : body.get("DESCRIPTION").toString()));
                workorder.put("jmsCorrelationId", (body.get("JMSCORRELATIONID") == null ? "" : body.get("JMSCORRELATIONID").toString()));
                workorder.put("prodName", (body.get("PRODUCTNAME") == null ? "" : body.get("PRODUCTNAME").toString()));
                workorder.put("prodType", (body.get("PRODUCTTYPE") == null ? "" : body.get("PRODUCTTYPE").toString()));
                workorder.put("reportBy", (body.get("REPORTEDBY") == null ? "" : body.get("REPORTEDBY").toString()));
                workorder.put("schedStart", (body.get("SCHEDSTART") == null ? "" : time.parseDate(body.get("SCHEDSTART").toString(), "yyyy-MM-dd HH:mm:ss")));
                workorder.put("scOrderNo", (body.get("SCORDERNO") == null ? "" : body.get("SCORDERNO").toString()));
                workorder.put("custAddress", (body.get("SERVICEADDRESS") == null ? "" : body.get("SERVICEADDRESS").toString()));
                workorder.put("serviceNum", (body.get("SERVICENUM") == null ? "" : body.get("SERVICENUM").toString()));
                workorder.put("workZone", (body.get("WORKZONE") == null ? "" : body.get("WORKZONE").toString()));
                workorder.put("siteId", (body.get("SITEID") == null ? dao.lookupSiteId(workorder.get("workZone").toString()) : body.get("SITEID").toString()));
                workorder.put("status", (body.get("STATUS") != "STARTWORK" ? "STARTWORK" : body.get("STATUS").toString()));
                workorder.put("tkCustomHeader01", (body.get("TK_CUSTOM_HEADER_01") == null ? "" : body.get("TK_CUSTOM_HEADER_01").toString()));
                workorder.put("tkWo4", (body.get("TK_WORKORDER_04") == null ? "" : body.get("TK_WORKORDER_04").toString()));
                workorder.put("woRevisionNo", (body.get("WOREVISIONNO") == null ? "" : body.get("WOREVISIONNO").toString()));
                workorder.put("workType", (body.get("WORKTYPE") == null ? "" : body.get("WORKTYPE").toString()));
                workorder.put("latitude", (body.get("LATITUDE") == null ? "" : body.get("LATITUDE").toString()));
                workorder.put("longitude", (body.get("LONGITUDE") == null ? "" : body.get("LONGITUDE").toString()));
                workorder.put("statusDate", time.getCurrentTime());
                workorder.put("woClass", "WORKORDER");
                
                //@Main process start..
                //Generate wonum with counter function from DB
                String wonum = dao.getWonum();
                String parent = wonum;
                //Checking the match of siteId and wonum w/ previous 'generate wonum request'
                LogUtil.info(getClass().getName(), "Start Process: Generate task | Wonum: " + parent);
                //Define JSON WORKORDER request
                JSONArray attr_array = (JSONArray)body.get("WORKORDERATTRIBUTE");
                Object ossitem_arrayObj = (Object)body.get("OSSITEM");
                
                //Getting Owner group from tkmapping
//                String ownerGroup = validateOwnerGroup.ownerGroupParent(workorder);
//                String ownerGroup = dao2.getOwnerGroup(workorder.get("workZone").toString());
                workorder.put("wonum", wonum);
//                workorder.put("ownerGroup", ownerGroup);
                
                //@Work Order attribute
                JSONArray AttributeWO = new JSONArray();
                //Loop getting each attribute
                for (int i = 0 ; i < attr_array.size() ; i++){
                    JSONObject attr_arrayObj = (JSONObject)attr_array.get(i);
                    JSONObject woAttribute = new JSONObject();
                    //Store attribute
                    woAttribute.put("woAttrName", (attr_arrayObj.get("ATTR_NAME") == null ? "" : attr_arrayObj.get("ATTR_NAME").toString()));
                    woAttribute.put("woAttrValue", (attr_arrayObj.get("ATTR_VALUE") == null ? "" : attr_arrayObj.get("ATTR_VALUE").toString()));
                    woAttribute.put("woAttrSequence", (attr_arrayObj.get("SEQUENCE") == null ? "" : attr_arrayObj.get("SEQUENCE").toString()));
                    AttributeWO.add(woAttribute);
                    //Insert attribute
                    dao.insertToWoAttrTable2(workorder.get("wonum").toString(), woAttribute);
                }
                
                JSONArray oss_item = new JSONArray();
                LogUtil.info(getClass().getName(), "OSS ITEM = " +ossitem_arrayObj);
                
                if (ossitem_arrayObj == null) {
                    LogUtil.info(getClass().getName(), "OSS ITEM IS NULL");
                    validateTask.generateTaskNonCore(oss_item, workorder, duration);
                } else {
                    LogUtil.info(getClass().getName(), "OSS ITEM IS NOT NULL");
                    validateTask.generateTaskCore(ossitem_arrayObj, oss_item, workorder, duration);
                }
                
                workorder.put("duration", duration);
                final boolean insertWoStatus = dao.insertToWoTable1(workorder);
                
                //@@End
                //@Response
                if (wonum != null && insertWoStatus){
                    try {
                        //Success response
                        LogUtil.info(getClassName(), "Process End - Wonum Generated [" + wonum + "]");
                        LogUtil.info(getClassName(), "Send response to 'Engine' and 'WFM Kafka'");
                        String statusHeaders = "200";
                        String statusRequest = "Success";
                        //Create response
                        JSONObject outer1 = new JSONObject();
                        JSONObject outer2 = new JSONObject();
                        JSONObject outer3 = new JSONObject();
                        JSONObject data = new JSONObject();
                        data.put("WONUM", wonum);
                        data.put("SITEID", workorder.get("siteId").toString());
                        outer1.put("WORKORDER", data);
                        outer2.put("WORKORDERMboKeySet", outer1);
                        outer3.put("CreateMXTELKOWOResponse", outer2);
                        JSONObject res1 = new JSONObject();
                        res1.put("code", statusHeaders);
                        res1.put("message", statusRequest);
                        res1.put("data", data);
                        res1.writeJSONString(hsr1.getWriter());
                        //Response to Kafka
                        String kafkaRes = outer3.toJSONString();
                        KafkaProducerTool kafkaProducerTool = new KafkaProducerTool();
                        kafkaProducerTool.generateMessage(kafkaRes, "WFM_WONUM", "");
                    } catch (IOException e) {
                        LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                    }  
                }
            }catch (ParseException e){
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
            //Authorization failed
            
        //Authorization failed    
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
