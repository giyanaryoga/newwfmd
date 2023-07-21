/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import id.co.telkom.wfm.plugin.dao.TaskActivityDao;
import id.co.telkom.wfm.plugin.dao.TaskGenerateEbisDao;
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
    
//    private String dateFormatter(String sourceDate){
//        DateTimeFormatter sourceFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        String convertedDate = LocalDateTime.parse(sourceDate, sourceFormat).format(targetFormat);
//        return convertedDate;
//    }

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {
        //@@Start.. 
        TimeUtil time = new TimeUtil();
        JsonUtil json = new JsonUtil();
        final JSONObject res = new JSONObject(); 
        //@Authorization
        //Plugin API configuration
        GenerateWonumEbisDao dao = new GenerateWonumEbisDao();
        TaskGenerateEbisDao dao2 = new TaskGenerateEbisDao();
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
                String id = UuidGenerator.getInstance().getUuid();//generating uuid
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
                String crmOrderType = (body.get("CRMORDERTYPE") == null ? "" : body.get("CRMORDERTYPE").toString());
                String custName = (body.get("CUSTOMER_NAME") == null ? "" : body.get("CUSTOMER_NAME").toString());
                String description = (body.get("DESCRIPTION") == null ? "" : body.get("DESCRIPTION").toString());
                String jmsCorrelationId = (body.get("JMSCORRELATIONID") == null ? "" : body.get("JMSCORRELATIONID").toString());
                String prodName = (body.get("PRODUCTNAME") == null ? "" : body.get("PRODUCTNAME").toString());
                String prodType = (body.get("PRODUCTTYPE") == null ? "" : body.get("PRODUCTTYPE").toString());
                String reportBy = (body.get("REPORTEDBY") == null ? "" : body.get("REPORTEDBY").toString());
//                String sourceDate = (body.get("SCHEDSTART") == null ? "" : body.get("SCHEDSTART").toString());
                String schedStart = (body.get("SCHEDSTART") == null ? "" : time.parseDate(body.get("SCHEDSTART").toString(), "yyyy-MM-dd HH:mm:ss"));
                String scOrderNo = (body.get("SCORDERNO") == null ? "" : body.get("SCORDERNO").toString());
                String custAddress = (body.get("SERVICEADDRESS") == null ? "" : body.get("SERVICEADDRESS").toString());
                String serviceNum = (body.get("SERVICENUM") == null ? "" : body.get("SERVICENUM").toString());
                String workZone = (body.get("WORKZONE") == null ? "" : body.get("WORKZONE").toString());
                String siteId = (body.get("SITEID") == null ? dao.lookupSiteId(workZone) : body.get("SITEID").toString());
                String status = (body.get("STATUS") == null ? "" : body.get("STATUS").toString());
                String tkCustomHeader01 = (body.get("TK_CUSTOM_HEADER_01") == null ? "" : body.get("TK_CUSTOM_HEADER_01").toString());
                String tkWo4 = (body.get("TK_WORKORDER_04") == null ? "" : body.get("TK_WORKORDER_04").toString());
                String woRevisionNo = (body.get("WOREVISIONNO") == null ? "" : body.get("WOREVISIONNO").toString());
                String workType = (body.get("WORKTYPE") == null ? "" : body.get("WORKTYPE").toString());
                String woClass = "WORKORDER"; //Hardcoded variable
//                DateTimeFormatter currentDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String statusDate = time.getCurrentTime();
                          
                //@Main process start..
                //Generate wonum with counter function from DB
                String wonum = dao.getWonum();
                String parent = wonum;
                //Checking the match of siteId and wonum w/ previous 'generate wonum request'
                
                LogUtil.info(getClass().getName(), "Start Process: Generate task | Wonum: " + parent);
                //Getting Owner group from tkmapping
                String ownerGroup = dao2.getOwnerGroup(workZone);
                final boolean insertWoStatus = dao.insertToWoTable(id, wonum, crmOrderType, custName, custAddress, description, prodName, prodType, scOrderNo, workZone, siteId, workType, schedStart, reportBy, woClass, woRevisionNo, jmsCorrelationId, status, serviceNum, tkWo4, ownerGroup, statusDate);
                
                //@Work Order attribute
                JSONArray attr_array = (JSONArray)body.get("WORKORDERATTRIBUTE");
                ListAttributes listAttr = new ListAttributes();
                //Loop getting each attribute
                for (int i = 0 ; i < attr_array.size() ; i++){
                    JSONObject attr_arrayObj = (JSONObject)attr_array.get(i);
                    //Store attribute
                    listAttr.setTlkwoAttrName(attr_arrayObj.get("ATTR_NAME").toString());
                    listAttr.setTlkwoAttrValue((attr_arrayObj.get("ATTR_VALUE").toString() == null) ? "" : attr_arrayObj.get("ATTR_VALUE").toString());
                    String sequence = (attr_arrayObj.get("SEQUENCE") == null ? "" : attr_arrayObj.get("SEQUENCE").toString());
                    listAttr.setSequence(sequence);
                    //Insert attribute
                    boolean insertAttrStatus = dao.insertToWoAttrTable(wonum, listAttr);
                    listAttr.setTlkwoInsertAttrStatus(insertAttrStatus);
                }
                
                //@OSSItem
                Object ossitem_arrayObj = (Object)body.get("OSSITEM");
                ListOssItem listOssItem = new ListOssItem();
                ActivityTask act = new ActivityTask();
                ListOssItemAttribute listOssItemAtt = new ListOssItemAttribute();
                ListCpeValidate cpeValidated = new ListCpeValidate();
                act.setTaskId(10);
                int counter = 1;
                
                String[] splittedJms = jmsCorrelationId.split("_");
                String orderId = splittedJms[0];
                JSONArray oss_item = new JSONArray();
                List<JSONObject> taskList = new ArrayList<>();
                
                if (ossitem_arrayObj instanceof JSONObject){
                    oss_item.add(ossitem_arrayObj);
                    LogUtil.info(getClass().getName(), "TASK :" + oss_item);
                } else if (ossitem_arrayObj instanceof JSONArray) {
                    oss_item = (JSONArray) ossitem_arrayObj;
                    LogUtil.info(getClass().getName(), "TASK :" + oss_item);
                }
                
                for(int j = 0; j < ((JSONArray) oss_item).size(); j++) {
                    JSONObject oss_itemObj = (JSONObject)((JSONArray) oss_item).get(j);
                    JSONObject task = new JSONObject();
                    
                    listOssItem.setAction(((JSONObject) oss_itemObj).get("ACTION").toString());
                    listOssItem.setCorrelationid(((JSONObject) oss_itemObj).get("CORRELATIONID").toString());
                    listOssItem.setItemname(((JSONObject) oss_itemObj).get("ITEMNAME").toString());
                    String itemName = json.getString(oss_itemObj, "ITEMNAME");
                    String correlationId = listOssItem.getCorrelationid();
                    
                    //TASK GENERATE
                    JSONObject detailAct = dao2.getDetailTask(itemName);
                    LogUtil.info(getClass().getName(), "DETAIL TASK :" + detailAct);
                    task.put("description", detailAct.get("description"));
                    task.put("correlation", correlationId);
                    task.put("sequence", (int) detailAct.get("sequence"));
                    task.put("actplace", detailAct.get("actPlace"));
                    task.put("classStructureId", detailAct.get("classstructureid"));

                    JSONArray taskAttrList = new JSONArray();
                    JSONArray ossitem_attr = (JSONArray)((JSONObject)oss_itemObj).get("OSSITEMATTRIBUTE");
                    for (Object ossItemAttr : ossitem_attr) {
                        JSONObject arrayObj2 = (JSONObject)ossItemAttr;
                        listOssItemAtt.setAttrName(arrayObj2.get("ATTR_NAME").toString());
                        listOssItemAtt.setAttrValue(arrayObj2.get("ATTR_VALUE").toString() == null ? "" : arrayObj2.get("ATTR_VALUE").toString());
                        JSONObject taskAttrItem = new JSONObject();
                        String attrName = listOssItemAtt.getAttrName();
                        String attrValue = listOssItemAtt.getAttrValue();
                        taskAttrItem.put("attrName", attrName);
                        taskAttrItem.put("attrValue", attrValue);
                        taskAttrList.add(taskAttrItem);
                    }
                    
                    task.put("task_attr", taskAttrList);
                    taskList.add(task);
                    LogUtil.info(getClass().getName(), "TASK "+j+" :" + taskList);
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
                    sortedTask.put("wonum", parent + " - " + counter);
                    sortedTask.put("taskid", counter*10);
                    if (counter != 1) {
                        sortedTask.put("status", "APPR"); 
                    } else {
                        sortedTask.put("status", "LABASSIGN");   
                    }

                    counter = counter + 1;
                    LogUtil.info(getClass().getName(), "SORTED TASK :" + sortedTask);
//                    LogUtil.info(getClass().getName(), "===== WONUM =" + (String) sortedTask.get("wonum"));
//                    LogUtil.info(getClass().getName(), "===== TASK ID =" + sortedTask.get("taskid"));
//                    LogUtil.info(getClass().getName(), "===== CLASSSTRUCTUREID =" + (String) sortedTask.get("classStructureId"));
                    //GENERATE OSS ITEM
                    dao.insertToOssItem(wonum, listOssItem);
                    //GENERATE TASK
                    dao2.generateActivityTask(parent, siteId, sortedTask.get("correlation").toString(), ownerGroup, sortedTask);
                    //GENERATE ASSIGNMENT
                    dao2.generateAssignment(sortedTask.get("description").toString(), schedStart, parent);

                    //TASK ATTRIBUTE GENERATE
                    dao2.GenerateTaskAttribute((String) sortedTask.get("classStructureId"), (String) sortedTask.get("wonum"), orderId);
                    
                    JSONArray taskAttrArray = (JSONArray) sortedTask.get("task_attr");
                    LogUtil.info(getClass().getName(), "SORTED TASK ATTRIBUTE :" + taskAttrArray);
                    for (Object taskAttrArrayObj: taskAttrArray) {
                        JSONObject taskAttrObj = (JSONObject)taskAttrArrayObj;
                        String attrName = (String) taskAttrObj.get("attrName");
//                        String attrValue = (String) taskAttrObj.get("attrValue");
                        if (attrName.equalsIgnoreCase(dao2.getTaskAttrName(attrName))) {
                            listOssItemAtt.setAttrValue((taskAttrObj.get("attrValue").toString() == null) ? "" : taskAttrObj.get("attrValue").toString());
                            String attrValue = (String) taskAttrObj.get("attrValue");
                            dao2.updateValueTaskAttribute((String) sortedTask.get("wonum"), attrName, attrValue);
                        }
                        if (!attrName.equalsIgnoreCase(dao2.getTaskAttrName(attrName))) {
                            listOssItemAtt.setAttrValue((taskAttrObj.get("attrValue").toString() == null) ? "" : taskAttrObj.get("attrValue").toString());
                                String attrValue = (String) taskAttrObj.get("attrValue");
                            dao2.updateNameTaskAttribute(attrName, (String) sortedTask.get("wonum"), orderId, attrValue);
                        }
                        
                        //@insert Oss Item Attribute
                        dao.insertToOssAttribute(taskAttrObj);
                        
                        switch (attrName) {
                            case "NTE_MODEL":
                                cpeValidated.setModel(listOssItemAtt.getAttrValue());
                                LogUtil.info(getClass().getName(), "list model " +cpeValidated.getModel()+ " done");
                                break;
                            case "NTE_MANUFACTUR":
                                cpeValidated.setVendor(listOssItemAtt.getAttrValue());
                                LogUtil.info(getClass().getName(), "list vendor " +cpeValidated.getVendor()+ " done");
                                break;
                            case "NTE_SERIALNUMBER":
                                cpeValidated.setSerial_number(listOssItemAtt.getAttrValue());
                                LogUtil.info(getClass().getName(), "list serial_number " +cpeValidated.getSerial_number()+ " done");
                                break;
                            case "AP_SERIALNUMBER":
                                cpeValidated.setSerial_number(listOssItemAtt.getAttrValue());
                                LogUtil.info(getClass().getName(), "list serial_number " +cpeValidated.getSerial_number()+ " done");
                                break;
                            default:
                                cpeValidated.setModel(null);
                                cpeValidated.setVendor(null);
                                cpeValidated.setSerial_number(null);
                                break;
                        }
                        String cpeValidate = "";
                        LogUtil.info(getClass().getName(), "list cpe " + cpeValidated.getModel() + ", " + cpeValidated.getVendor() + ", " + cpeValidated.getSerial_number() + ", " +cpeValidate+ " done");
                        if (cpeValidated.getModel() != null && cpeValidated.getVendor() != null) {
                            if (cpeValidated.getSerial_number().isEmpty()) {
                                cpeValidate = "";
                                boolean updateCpe = dao2.updateWoCpe(cpeValidated.getModel(), cpeValidated.getVendor(), cpeValidated.getSerial_number(), cpeValidate, parent, act);
                                cpeValidated.setUpdateCpeValidate(updateCpe);
                            } else {
                                cpeValidate = "PASS";
                                boolean updateCpe = dao2.updateWoCpe(cpeValidated.getModel(), cpeValidated.getVendor(), cpeValidated.getSerial_number(), cpeValidate, parent, act);
                                cpeValidated.setUpdateCpeValidate(updateCpe);
                            }
                        }
                    }
                }
                
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
                        data.put("SITEID", siteId);
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
