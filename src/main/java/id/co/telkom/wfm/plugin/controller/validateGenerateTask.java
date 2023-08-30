/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.TestGenerateDao;
import id.co.telkom.wfm.plugin.dao.TaskActivityDao;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import id.co.telkom.wfm.plugin.dao.TaskHistoryDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class validateGenerateTask {
    GenerateWonumEbisDao generateDao = new GenerateWonumEbisDao();
    TestGenerateDao dao2 = new TestGenerateDao();
    TaskActivityDao dao = new TaskActivityDao();
    TaskHistoryDao historyDao = new TaskHistoryDao();
    List<JSONObject> taskList = new ArrayList<>();
    String TaskDescription = "";
    String ownerGroupTask = "";
    
    public void generateTaskNonCore(JSONArray oss_item, JSONObject workorder, JSONArray AttributeWO, int duration) {
        try {
            JSONArray arrayNull = new JSONArray();
            boolean isGenerateTask = true;
            String prodName = workorder.get("prodName").toString();
            String crmOrderType = workorder.get("crmOrderType").toString();
            JSONArray detailTaskNonCore = dao2.getDetailTaskNonCore(prodName, crmOrderType);
            for (Object obj : detailTaskNonCore) {
                JSONObject taskNonCoreObj = (JSONObject)obj;
                isGenerateTask = isGenerateTask(workorder, prodName, taskNonCoreObj.get("activity").toString());
                JSONObject taskNoncore = new JSONObject();
                taskNoncore.put("ACTION", "ADD");
                taskNoncore.put("CORRELATIONID", "35363732383333303936333333323130");
                taskNoncore.put("ITEMNAME", taskNonCoreObj.get("activity").toString());
                taskNoncore.put("OSSITEMATTRIBUTE", arrayNull);
                oss_item.add(taskNoncore);
                LogUtil.info(getClass().getName(), "GENERATE TASK : " +isGenerateTask);
            }
            
            if (isGenerateTask) {
                int counter = 1;
                String[] splittedJms = workorder.get("jmsCorrelationId").toString().split("_");
                String orderId = splittedJms[0];
                
                defineTask(oss_item, duration);
                sortedTask();
                generateTask(workorder, AttributeWO, counter, orderId);
            } else {
                LogUtil.info(getClass().getName(), "TIDAK GENERATE TASK");
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateGenerateTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private boolean isGenerateTask(JSONObject workorder, String prodName, String activity) {
        boolean isTrue = true;
        try {
            String dcType = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "DC_Type");
            String inNumb = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "IN_NUMBER");
            String[] actTask = {"WFMNonCore Activate SS","WFMNonCore Activate IMS","WFMNonCore Activate TDM"};
            
            if (dcType == "Wholesale" || dcType == "CIS") {
                if (prodName == "INF_IPPBX" && activity == "WFMNonCore Registration Number To CRM") {
                    isTrue = false;
                    LogUtil.info(getClass().getName(), "isGenerateTask NonCore :" +isTrue);
                }
            }
            if (dcType.equalsIgnoreCase("DGS")) {
                if (prodName.equalsIgnoreCase("MM_IP_TRANSIT") && activity.equalsIgnoreCase("WFMNonCore Create MRTG")) {
                    isTrue = false;
                }
            }
            if (prodName.equalsIgnoreCase("INF_CALLCENTER") && activity.equals(actTask)) {
                if (!inNumb.equalsIgnoreCase("")) {
                    if (inNumb.length() == 3) {
                        isTrue = false;
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateGenerateTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        return isTrue;
    }
    
    public void generateTaskCore(Object ossitem_arrayObj, JSONArray oss_item, JSONObject workorder, JSONArray AttributeWO, int duration) {
        try {
            int counter = 1;
            String[] splittedJms = workorder.get("jmsCorrelationId").toString().split("_");
            String orderId = splittedJms[0];
            
            if (ossitem_arrayObj instanceof JSONObject){
                oss_item.add(ossitem_arrayObj);
            } else if (ossitem_arrayObj instanceof JSONArray) {
                oss_item = (JSONArray) ossitem_arrayObj;
            }
            
            defineTask(oss_item, duration);
            sortedTask();
            generateTask(workorder, AttributeWO, counter, orderId);
        } catch (SQLException ex) {
            Logger.getLogger(validateGenerateTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void sortedTask() {
        Collections.sort(taskList, new Comparator<JSONObject>(){
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                int valA = (int) o1.get("sequence");
                int valB = (int) o2.get("sequence"); 
                System.out.println("valA: " + valA);
                System.out.println("valB: " + valB);
                return valA - valB;
            }
        });
    }
    
    private void defineTask(JSONArray oss_item, int duration) {
        for(int j = 0; j < ((JSONArray) oss_item).size(); j++) {
            try {
                JSONObject oss_itemObj = (JSONObject)((JSONArray) oss_item).get(j);
                JSONArray ossitem_attr = (JSONArray)((JSONObject)oss_itemObj).get("OSSITEMATTRIBUTE");
                JSONObject task = new JSONObject();
                
                String itemName = ((JSONObject) oss_itemObj).get("ITEMNAME").toString();
                String correlationId = ((JSONObject) oss_itemObj).get("CORRELATIONID").toString();
                //TASK GENERATE
                JSONObject detailAct = dao2.getDetailTask(itemName);
                task.put("activity", detailAct.get("activity"));
                task.put("description", detailAct.get("description"));
                task.put("correlation", correlationId);
                task.put("sequence", (int) detailAct.get("sequence"));
                task.put("actplace", detailAct.get("actPlace"));
                task.put("ownerGroup", (detailAct.get("ownergroup") == null ? "" : detailAct.get("ownergroup")));
                task.put("duration", (int) detailAct.get("duration"));
                duration = (int) task.get("duration");
                
                JSONArray taskAttrList = new JSONArray();
                for (Object ossItemAttr : ossitem_attr) {
                    JSONObject arrayObj2 = (JSONObject)ossItemAttr;
                    JSONObject taskAttrItem = new JSONObject();
                    
                    taskAttrItem.put("attrName", arrayObj2.get("ATTR_NAME").toString());
                    taskAttrItem.put("attrValue", arrayObj2.get("ATTR_VALUE").toString() == null ? "" : arrayObj2.get("ATTR_VALUE").toString());
                    taskAttrList.add(taskAttrItem);
                }
                
                task.put("task_attr", taskAttrList);
                taskList.add(task);
            } catch (SQLException ex) {
                Logger.getLogger(validateGenerateTask.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void generateTask(JSONObject workorder, JSONArray AttributeWO, int counter, String orderId) throws SQLException {
        for(JSONObject sortedTask: taskList) {
            String wonumChild = generateDao.getWonum();
            sortedTask.put("wonum", wonumChild);
            sortedTask.put("parent", workorder.get("wonum").toString());
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

            //GENERATE OSS ITEM
            generateDao.insertToOssItem(sortedTask);
            //GENERATE TASK
            dao2.generateActivityTask(sortedTask, workorder, ownerGroupTask);
            //GENERATE ASSIGNMENT
            dao2.generateAssignment(sortedTask, workorder);
            //GENERATE TASK HISTORY
            historyDao.insertTaskStatus((String) sortedTask.get("wonum"), "Generate Wonum from OSM", "extOSM", "extOSM");
            //TASK ATTRIBUTE GENERATE
            dao2.GenerateTaskAttribute(sortedTask, workorder, orderId);
            JSONArray taskAttrArray = (JSONArray) sortedTask.get("task_attr");
            for (Object taskAttrArrayObj: taskAttrArray) {
                JSONObject taskAttrObj = (JSONObject)taskAttrArrayObj;
                String attrName = taskAttrObj.get("attrName").toString();
                String attrValue = taskAttrObj.get("attrValue").toString();
                //@insert Oss Item Attribute
                generateDao.insertToOssAttribute(taskAttrObj, (String) sortedTask.get("wonum"));
                if (attrName.equalsIgnoreCase(dao2.getTaskAttrName(attrName))) {
                    if (attrValue.isEmpty()) {
                        //GENERATE VALUE FROM WORKORDERATTRIBUTE
                        for (Object objWoAttr : AttributeWO) {
                            JSONObject arrayObj3 = (JSONObject)objWoAttr;
                            JSONObject resp = generateDao.getWoAttrName(workorder.get("wonum").toString(), arrayObj3.get("woAttrName").toString());
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
            }
            counter = counter + 1;
        }
    }
    
//    private void validateCPE(JSONObject cpeValidate) {
//        switch (cpeValidate.get("attrName").toString()) {
//            case "NTE_MODEL":
//                cpeValidate.get("attrValue").toString();
////                cpeValidated.setModel(attrValue);
////                LogUtil.info(getClass().getName(), "list model " +cpeValidated.getModel()+ " done");
//                break;
//            case "NTE_MANUFACTUR":
//                cpeValidate.get("attrValue").toString();
////                cpeValidated.setVendor(attrValue);
////                LogUtil.info(getClass().getName(), "list vendor " +cpeValidated.getVendor()+ " done");
//                break;
//            case "NTE_SERIALNUMBER":
//                cpeValidate.get("attrValue").toString();
////                cpeValidated.setSerial_number(attrValue);
////                LogUtil.info(getClass().getName(), "list serial_number " +cpeValidated.getSerial_number()+ " done");
//                break;
//            case "AP_SERIALNUMBER":
//                cpeValidate.get("attrValue").toString();
////                cpeValidated.setSerial_number(attrValue);
////                LogUtil.info(getClass().getName(), "list serial_number " +cpeValidated.getSerial_number()+ " done");
//                break;
//            default:
////                cpeValidated.setModel(null);
////                cpeValidated.setVendor(null);
////                cpeValidated.setSerial_number(null);
//                break;
//        }
//        String validateCpe = "";
////        LogUtil.info(getClass().getName(), "list cpe " + cpeValidated.getModel() + ", " + cpeValidated.getVendor() + ", " + cpeValidated.getSerial_number() + ", " +cpeValidate+ " done");
////        if (cpeValidated.getModel() != null && cpeValidated.getVendor() != null) {
////            if (cpeValidated.getSerial_number() == null) {
////                cpeValidate = "";
////                boolean updateCpe = dao2.updateWoCpe(cpeValidated.getModel(), cpeValidated.getVendor(), cpeValidated.getSerial_number(), cpeValidate, (String) sortedTask.get("wonum"));
////                cpeValidated.setUpdateCpeValidate(updateCpe);
////            } else {
////                cpeValidate = "PASS";
////                boolean updateCpe = dao2.updateWoCpe(cpeValidated.getModel(), cpeValidated.getVendor(), cpeValidated.getSerial_number(), cpeValidate, (String) sortedTask.get("wonum"));
////                cpeValidated.setUpdateCpeValidate(updateCpe);
////            }
////        } else {
////            cpeValidate = "";
////            boolean updateCpe = dao2.updateWoCpe(cpeValidated.getModel(), cpeValidated.getVendor(), cpeValidated.getSerial_number(), cpeValidate, (String) sortedTask.get("wonum"));
////            cpeValidated.setUpdateCpeValidate(updateCpe);
////        }
//    }
}
