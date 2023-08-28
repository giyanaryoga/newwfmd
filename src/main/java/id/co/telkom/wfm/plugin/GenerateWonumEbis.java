/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import id.co.telkom.wfm.plugin.dao.TaskActivityDao;
import id.co.telkom.wfm.plugin.dao.TaskHistoryDao;
import id.co.telkom.wfm.plugin.dao.TestGenerateDao;
import id.co.telkom.wfm.plugin.model.ListAttributes;
import id.co.telkom.wfm.plugin.model.ListOssItem;
import id.co.telkom.wfm.plugin.model.ListOssItemAttribute;
import id.co.telkom.wfm.plugin.model.ActivityTask;
import id.co.telkom.wfm.plugin.model.ListClassSpec;
import id.co.telkom.wfm.plugin.model.ListCpeValidate;
import id.co.telkom.wfm.plugin.util.JsonUtil;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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
        TaskActivityDao dao2 = new TaskActivityDao();
//        TestGenerateDao dao2 = new TestGenerateDao();
        TaskHistoryDao taskHistory = new TaskHistoryDao();
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
                int duration = 0;
                JSONObject workorder = new JSONObject();
                workorder.put("crmOrderType", (body.get("CRMORDERTYPE") == null ? "" : body.get("CRMORDERTYPE").toString()));
                workorder.put("custName", (body.get("CUSTOMER_NAME") == null ? "" : body.get("CUSTOMER_NAME").toString()));
                workorder.put("description", (body.get("DESCRIPTION") == null ? "" : body.get("DESCRIPTION").toString()));
                workorder.put("jmsCorrelationId", (body.get("JMSCORRELATIONID") == null ? "" : body.get("JMSCORRELATIONID").toString()));
                workorder.put("prodName", (body.get("PRODUCTNAME") == null ? "" : body.get("PRODUCTNAME").toString()));
                workorder.put("prodType", (body.get("PRODUCTTYPE") == null ? "" : body.get("PRODUCTTYPE").toString()));
                workorder.put("reportBy", (body.get("REPORTEDBY") == null ? "" : body.get("REPORTEDBY").toString()));
                workorder.put("schedStart", (body.get("SCHEDSTART") == null ? null : time.parseDate(body.get("SCHEDSTART").toString(), "yyyy-MM-dd HH:mm:ss")));
                workorder.put("scOrderNo", (body.get("SCORDERNO") == null ? "" : body.get("SCORDERNO").toString()));
                workorder.put("custAddress", (body.get("SERVICEADDRESS") == null ? "" : body.get("SERVICEADDRESS").toString()));
                workorder.put("serviceNum", (body.get("SERVICENUM") == null ? "" : body.get("SERVICENUM").toString()));
                workorder.put("workZone", (body.get("WORKZONE") == null ? "" : body.get("WORKZONE").toString()));
                workorder.put("siteId", (body.get("SITEID") == null ? dao.lookupSiteId(workorder.get("workZone").toString()) : body.get("SITEID").toString()));
                workorder.put("status", (body.get("STATUS").toString() != "STARTWORK" ? "STARTWORK" : body.get("STATUS").toString()));
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
                //Getting Owner group from tkmapping
                String ownerGroup = dao2.getOwnerGroup(workorder.get("workZone").toString());
                workorder.put("wonum", wonum);
                workorder.put("ownerGroup", ownerGroup);
                
                //@Work Order attribute
                JSONArray attr_array = (JSONArray)body.get("WORKORDERATTRIBUTE");
                JSONArray AttributeWO = new JSONArray();
                
                ListAttributes listAttr = new ListAttributes();
                //Loop getting each attribute
                for (int i = 0 ; i < attr_array.size() ; i++){
                    JSONObject attr_arrayObj = (JSONObject)attr_array.get(i);
                    JSONObject woAttribute = new JSONObject();
                    //Store attribute
                    listAttr.setTlkwoAttrName(attr_arrayObj.get("ATTR_NAME").toString());
                    listAttr.setTlkwoAttrValue(attr_arrayObj.get("ATTR_VALUE").toString() == null ? "" : attr_arrayObj.get("ATTR_VALUE").toString());
                    String sequence = (attr_arrayObj.get("SEQUENCE") == null ? "" : attr_arrayObj.get("SEQUENCE").toString());
                    listAttr.setSequence(sequence);
                    
                    woAttribute.put("woAttrName", (attr_arrayObj.get("ATTR_NAME") == null ? "" : attr_arrayObj.get("ATTR_NAME").toString()));
                    woAttribute.put("woAttrValue", (attr_arrayObj.get("ATTR_VALUE") == null ? "" : attr_arrayObj.get("ATTR_VALUE").toString()));
                    woAttribute.put("woAttrSequence", sequence);
                    AttributeWO.add(woAttribute);
                    
                    //Insert attribute
                    boolean insertAttrStatus = dao.insertToWoAttrTable(workorder.get("wonum").toString(), listAttr);
                    listAttr.setTlkwoInsertAttrStatus(insertAttrStatus);
                }
                
                //@OSSItem
                Object ossitem_arrayObj = (Object)body.get("OSSITEM");
                
//                ListOssItem listOssItem = new ListOssItem();
//                ActivityTask act = new ActivityTask();
//                ListOssItemAttribute listOssItemAtt = new ListOssItemAttribute();
//                ListAttributes listAttribute = new ListAttributes();
//                ListCpeValidate cpeValidated = new ListCpeValidate();

//                    act.setTaskId(10);
                int counter = 1;
                String TaskDescription = "";
                String ownerGroupTask = "";
                String[] splittedJms = workorder.get("jmsCorrelationId").toString().split("_");
                String orderId = splittedJms[0];
                JSONArray oss_item = new JSONArray();
                List<JSONObject> taskList = new ArrayList<>();
                
                if (ossitem_arrayObj == null) {
                    JSONArray taskAttrNonCore = new JSONArray();
                    JSONArray detailTaskNonCore = dao2.getDetailTaskNonCore(workorder.get("prodName").toString(), workorder.get("crmOrderType").toString());
                    for (Object obj : detailTaskNonCore) {
                        JSONObject taskNonCoreObj = (JSONObject)obj;
                        JSONObject taskNoncore = new JSONObject();
                        taskNoncore.put("ACTION", "ADD");
                        taskNoncore.put("CORRELATIONID", "35363732383333303936333333323130");
                        taskNoncore.put("ITEMNAME", taskNonCoreObj.get("activity").toString());
                        taskNoncore.put("OSSITEMATTRIBUTE", taskAttrNonCore);
                        oss_item.add(taskNoncore);
                    }
                    LogUtil.info(getClass().getName(), "TASK : " +oss_item);
                }

                if (ossitem_arrayObj instanceof JSONObject){
                    oss_item.add(ossitem_arrayObj);
//                    LogUtil.info(getClass().getName(), "TASK :" + oss_item);
                } else if (ossitem_arrayObj instanceof JSONArray) {
                    oss_item = (JSONArray) ossitem_arrayObj;
//                    LogUtil.info(getClass().getName(), "TASK :" + oss_item);
                }
                
                JSONObject oss = (JSONObject)((JSONArray) oss_item).get(0);
                JSONObject detailAct1 = dao2.getDetailTask(((JSONObject) oss).get("ITEMNAME").toString());
                TaskDescription = (String) detailAct1.get("description");
                workorder.put("TaskDescription", TaskDescription);

                for(int j = 0; j < ((JSONArray) oss_item).size(); j++) {
                    JSONObject oss_itemObj = (JSONObject)((JSONArray) oss_item).get(j);
                    JSONObject task = new JSONObject();

//                    listOssItem.setAction(((JSONObject) oss_itemObj).get("ACTION").toString());
//                    listOssItem.setCorrelationid(((JSONObject) oss_itemObj).get("CORRELATIONID").toString());
//                    listOssItem.setItemname(((JSONObject) oss_itemObj).get("ITEMNAME").toString());
                    String itemName = ((JSONObject) oss_itemObj).get("ITEMNAME").toString();
                    String correlationId = ((JSONObject) oss_itemObj).get("CORRELATIONID").toString();
                    //TASK GENERATE
                    JSONObject detailAct = dao2.getDetailTask(itemName);
//                    LogUtil.info(getClass().getName(), "DETAIL TASK :" + detailAct);
                    task.put("activity", detailAct.get("activity"));
                    task.put("description", detailAct.get("description"));
                    task.put("correlation", correlationId);
                    task.put("sequence", (int) detailAct.get("sequence"));
                    task.put("actplace", detailAct.get("actPlace"));
                    task.put("ownerGroup", (detailAct.get("ownergroup") == null ? "" : detailAct.get("ownergroup")));
                    task.put("duration", (int) detailAct.get("duration"));
                    duration = (int) task.get("duration");

                    JSONArray taskAttrList = new JSONArray();
                    JSONArray ossitem_attr = (JSONArray)((JSONObject)oss_itemObj).get("OSSITEMATTRIBUTE");
                    for (Object ossItemAttr : ossitem_attr) {
                        JSONObject arrayObj2 = (JSONObject)ossItemAttr;
                        JSONObject taskAttrItem = new JSONObject();

//                        listOssItemAtt.setAttrName(arrayObj2.get("ATTR_NAME").toString());
//                        String attrName = listOssItemAtt.getAttrName();
//                        listOssItemAtt.setAttrValue(arrayObj2.get("ATTR_VALUE").toString() == null ? "" : arrayObj2.get("ATTR_VALUE").toString());
//                        String attrValue = listOssItemAtt.getAttrValue();

                        taskAttrItem.put("attrName", arrayObj2.get("ATTR_NAME").toString());
                        taskAttrItem.put("attrValue", arrayObj2.get("ATTR_VALUE").toString() == null ? "" : arrayObj2.get("ATTR_VALUE").toString());
                        taskAttrList.add(taskAttrItem);
                    }

                    task.put("task_attr", taskAttrList);
                    taskList.add(task);
//                    LogUtil.info(getClass().getName(), "TASK "+j+" :" + taskList);
                }

                Collections.sort(taskList, new Comparator<JSONObject>(){
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        int valA = (int) o1.get("sequence");
                        int valB = (int) o2.get("sequence"); 
                        System.out.println("valA: " + valA);
//                        LogUtil.info(getClass().getName(), "Compare 1 =" +valA);
                        System.out.println("valB: " + valB);
//                        LogUtil.info(getClass().getName(), "Compare 2 =" +valB);
                        return valA - valB;
                    }
                });

                for(JSONObject sortedTask: taskList) {
                    String wonumChild = dao.getWonum();
                    sortedTask.put("wonum", wonumChild);
                    sortedTask.put("parent", parent);
                    sortedTask.put("taskid", counter*10);

                    if ((int) sortedTask.get("taskid") != 10) {
                        sortedTask.put("status", "APPR"); 
                    } else {
                        sortedTask.put("status", "LABASSIGN");   
                    }

                    if (sortedTask.get("ownerGroup").toString() != "") {
                        ownerGroupTask = dao2.getOwnerGroupPerson(sortedTask.get("ownerGroup").toString());
                    } else {
                        ownerGroupTask = dao2.getOwnerGroup(workorder.get("workZone").toString());
                    }

                    counter = counter + 1;
//                    LogUtil.info(getClass().getName(), "SORTED TASK :" + sortedTask);
                    //GENERATE OSS ITEM
                    dao.insertToOssItem((String) sortedTask.get("wonum"), sortedTask);
                    //GENERATE TASK
                    dao2.generateActivityTask(parent, workorder.get("siteId").toString(), sortedTask.get("correlation").toString(), sortedTask, ownerGroupTask, workorder);
                    //GENERATE ASSIGNMENT
                    dao2.generateAssignment(sortedTask.get("activity").toString(), workorder.get("schedStart").toString(), parent);
                    //GENERATE TASK HISTORY
                    taskHistory.insertTaskStatus((String) sortedTask.get("wonum"), "Generate Wonum from OSM", "extOSM");

                    //TASK ATTRIBUTE GENERATE
                    dao2.GenerateTaskAttribute((String) sortedTask.get("activity"), (String) sortedTask.get("wonum"), orderId, workorder.get("siteId").toString());

                    JSONArray taskAttrArray = (JSONArray) sortedTask.get("task_attr");
//                    LogUtil.info(getClass().getName(), "SORTED TASK ATTRIBUTE :" + taskAttrArray);
                    for (Object taskAttrArrayObj: taskAttrArray) {
                        String attrName = "";
                        String attrValue = "";
                        JSONObject taskAttrObj = (JSONObject)taskAttrArrayObj;
                        attrName = taskAttrObj.get("attrName").toString();
                        attrValue = taskAttrObj.get("attrValue").toString();
                        if (attrName.equalsIgnoreCase(dao2.getTaskAttrName(attrName))) {
                            if (attrValue.isEmpty()) {
                                //GENERATE VALUE FROM WORKORDERATTRIBUTE
                                for (Object objWoAttr : AttributeWO) {
                                    JSONObject arrayObj3 = (JSONObject)objWoAttr;
                                    JSONObject resp = dao.getWoAttrName(parent, arrayObj3.get("woAttrName").toString());
                                    String AttrNameWo = resp.get("attr_name").toString().toUpperCase();
                                    String AttrValueWo = (arrayObj3.get("woAttrValue").toString() == null ? "" : arrayObj3.get("woAttrValue").toString());
                                    if (AttrNameWo.equals(attrName)) {
                                        dao2.updateValueTaskAttribute((String) sortedTask.get("wonum"), attrName, AttrValueWo);
//                                        LogUtil.info(getClass().getName(), "ATTRIBUTE NAME WO == TASK ATTRIBUTE NAME");
                                    }
                                }
                            } else {
                                dao2.updateValueTaskAttribute((String) sortedTask.get("wonum"), attrName, attrValue);
//                                LogUtil.info(getClass().getName(), "ATTRIBUTE NAME != TASK ATTRIBUTE NAME");  
                            }
                        }
                        //@insert Oss Item Attribute
                        dao.insertToOssAttribute(taskAttrObj, (String) sortedTask.get("wonum"));
                    }
                }
                
                workorder.put("duration", duration);
                final boolean insertWoStatus = dao.insertToWoTable2(workorder);
                
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
            } catch (ParseException e){
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            } catch (SQLException ex) {
                Logger.getLogger(GenerateWonumEbis.class.getName()).log(Level.SEVERE, null, ex);
            }
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
