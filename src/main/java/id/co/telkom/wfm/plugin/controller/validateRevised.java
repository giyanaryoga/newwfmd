/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.TaskAttribute;
import id.co.telkom.wfm.plugin.dao.RevisedTaskDao;
import id.co.telkom.wfm.plugin.dao.TaskAttributeUpdateDao;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import id.co.telkom.wfm.plugin.dao.TaskAttributeDao2;
import id.co.telkom.wfm.plugin.util.ResponseAPI;
import java.util.Arrays;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class ValidateRevised {
    RevisedTaskDao dao = new RevisedTaskDao();
    GenerateWonumEbisDao generateDao = new GenerateWonumEbisDao();
    TaskAttributeUpdateDao taskAttrDao = new TaskAttributeUpdateDao();
    TaskAttributeDao2 taskAttrDao2 = new TaskAttributeDao2();
    ResponseAPI response = new ResponseAPI();
    
    public void validate (String parent, String wonum, String attrName, String attrValue, String task) {
        try {
            JSONArray taskArray = dao.getTask(parent);
            task = taskAttrDao.getActivity(wonum);
            
            switch(attrName){
                case "APPROVAL_SURVEY":
                    validateApprovalSurvey(parent, task, attrValue);
                    response.ResponseMessage(200, "Success Revised Task!");
                break;
                case "APPROVAL":
                    validateNonConn(parent, wonum, task, attrValue);
                    validateApprovalTSQ(parent, wonum, task, attrValue);
                    validateApprovalMangoesky(parent, task, attrValue);
                break;
                case "NODE_ID":
                    validateNodeId(parent, task, attrValue);
                break;
                case "ACCESS REQUIRED 1":
                    validateAccessRequired1(parent, task, attrValue);
                break;
                case "ACCESS REQUIRED 2":
                    validateAccessRequired2(parent, task, attrValue);
                break;
                case "APPROVED":
                    validateApproved(parent, task, attrValue);
                break;
                case "TIPE MODIFY":
                    validateTipeModify(parent, task, attrValue);
                break;
                case "ACCEPT":
                    if (attrValue.equalsIgnoreCase("NO")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(getClass().getName(), "Approval Survey is not NO");
                    }
                break;
                case "ACCESS_REQUIRED":
                    if (attrValue.equalsIgnoreCase("NO")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(getClass().getName(), "Approval Survey is not NO");
                    }
                break;
                case "MODIFY_TYPE":
                    if (attrValue.equalsIgnoreCase("Bandwidth")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else if (attrValue.equalsIgnoreCase("Service (P2P dan P2MP)") || attrValue.equalsIgnoreCase("Port")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(getClass().getName(), "Approval Survey is not NO");
                    }
                break;
                case "COORDINATE CORRECT":
                    if (attrValue.equalsIgnoreCase("NO")) {
                        dao.reviseTaskDocType(wonum, "REVISED");
                    } else {
                        dao.reviseTaskDocType(wonum, "NEW");
                    }
                break;
                default:
                    LogUtil.info(getClass().getName(), "Attribute name dan value tidak memenuhi Revised Task");
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(TaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private JSONObject validateAccessRequired1(String parent, String task, String attrValue) throws SQLException {
        JSONObject res = new JSONObject();
        if (task.equalsIgnoreCase("WFMNonCore BER Test WDM Coordination")) {
            if (attrValue.equalsIgnoreCase("NO")) {
                dao.updateWfmDocType(parent, "REVISED", "AND c_detailactcode IN ('WFMNonCore Allocate Access Cust End 1', 'WFMNonCore Integrasi Access Cust End 1')");
                res = response.ResponseMessage(200, "Success Revised Task");
            } else {
                dao.updateWfmDocType(parent, "NEW", "AND c_detailactcode IN ('WFMNonCore Allocate Access Cust End 1', 'WFMNonCore Integrasi Access Cust End 1')");
                res = response.ResponseMessage(200, "Success Update New Task");
            }
        }
        return res;
    }
    
    private void validateAccessRequired2(String parent, String task, String attrValue) throws SQLException {
        if (task.equalsIgnoreCase("WFMNonCore BER Test WDM Coordination")) {
            if (attrValue.equalsIgnoreCase("NO")) {
                dao.updateWfmDocType(parent, "REVISED", "AND c_detailactcode IN ('WFMNonCore Allocate Access Cust End 2', 'WFMNonCore Integrasi Access Cust End 2')");
            } else {
                dao.updateWfmDocType(parent, "NEW", "AND c_detailactcode IN ('WFMNonCore Allocate Access Cust End 2', 'WFMNonCore Integrasi Access Cust End 2')");
            }
        }
    }
    
    private void validateApproved(String parent, String task, String attrValue) throws SQLException {
        if (task.equalsIgnoreCase("Approval E2E Testing Wifi")) {
            if (attrValue.equalsIgnoreCase("REJECTED")) {
                dao.reviseTask(parent);
                dao.generateActivityTask(parent);
            } else {
                LogUtil.info(getClass().getName(), "Approval Survey is not REJECTED");
            }
        }
    }
    
    private void validateTipeModify(String parent, String task, String attrValue) {
        String[] valueIn = {"Modify Access", "Modify Bandwidth", "Modify Concurrent", "Modify Concurrent Dan Bandwidth", 
            "Modify IP", "Modify Number, Concurrent, Bandwidth"};
        try {
            if (task.equalsIgnoreCase("WFMNonCore Review Order Modify IPPBX NeuAPIX")) {
                if (attrValue.equalsIgnoreCase("Modify Number")) {
                    dao.updateWfmDocType(parent, "REVISED", "AND c_detailactcode IN ('WFMNonCore Allocate Number', 'WFMNonCore Modify Softswitch NeuAPIX')");
                } else if (Arrays.asList(valueIn).contains(attrValue)) {
                    dao.updateWfmDocType(parent, "NEW", "AND c_detailactcode IN ('WFMNonCore Allocate Number', 'WFMNonCore Modify Softswitch NeuAPIX')");
                } else {
                    LogUtil.info(getClass().getName(), "Tipe Modify is not REJECTED");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateRevised.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateNodeId(String parent, String task, String attrValue) {
        try {
            if (task.equalsIgnoreCase("WFMNonCore Get Device Access Wifi HomeSpot")) {
                if (!attrValue.equalsIgnoreCase("None")) {
                    dao.updateWfmDocType(parent, "REVISED", "AND c_detailactcode IN ('WFMNonCore Preconfig Access Wifi HomeSpot', 'WFMNonCore Preconfig Service Wifi HomeSpot')");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateRevised.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateApprovalSurvey(String parent, String task, String attrValue) {
        String[] taskApproval = {"Approval Survey", "Approval Survey Wifi"};
        try {
            if (Arrays.asList(taskApproval).contains(task) && attrValue.equalsIgnoreCase("BACK_TO_SURVEY")) {
                dao.reviseTask(parent);
                GenerateTaskNew(parent);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateRevised.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateApprovalTSQ(String parent, String wonum, String task, String attrValue) {
        try {
            if (task.equalsIgnoreCase("WFMNonCore Review Order TSQ WDM")) {
                if (attrValue.equalsIgnoreCase("REJECTED")) {
                    dao.updateWfmDocType(parent, "REVISED", "AND c_wosequence > (select c_wosequence from app_fd_workorder where c_wonum = '"+wonum+"')");
                } else if (attrValue.equalsIgnoreCase("APPROVED")) {
                    dao.updateWfmDocType(parent, "REVISED", "AND c_wosequence > (select c_wosequence from app_fd_workorder where c_wonum = '"+wonum+"')");
                } else {
                    LogUtil.info(getClass().getName(), "Approval TSQ WDM is not REJECTED");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateRevised.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateApprovalMangoesky(String parent, String task, String attrValue) {
        String[] taskMangoesky = {
            "Approval Project Management Mangoesky", "Approval Project Management Mangoesky Modoroso"
        };
        String taskRevised = "'Approval Project Management Mangoesky','Approval Project Management Mangoesky Modoroso',"
                + "'Validation Mangoesky','Upload Berita Acara Mangoesky'";
        try {
            if (attrValue.equalsIgnoreCase("REJECTED") && Arrays.asList(taskMangoesky).contains(task)) {
                dao.updateWfmDocType(parent, "REVISED", "AND c_parent='"+parent+"' AND c_detailactcode IN ("+taskRevised+")");
                GenerateTaskNew(parent);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateRevised.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateNonConn(String parent, String wonum, String task, String attrValue) {
        try {
            JSONArray taskArray = dao.getTask(parent);
            
            if (task.equalsIgnoreCase("REVIEW_ORDER")) {
                if (attrValue.equalsIgnoreCase("REJECTED")) {
                    for(Object obj : taskArray) {
                        JSONObject taskObj = (JSONObject)obj;
                        dao.reviseTaskNonConn_reviewOrder(taskObj.get("parent").toString());
                    }
                } else {
                    LogUtil.info(getClass().getName(), "Approval is not REJECTED");
                }
            } else if (task.equalsIgnoreCase("Approval_Project_Management") || task.equalsIgnoreCase("Validate-Survey")) {
                String domainId = dao.getDomainId(wonum);
                LogUtil.info(getClass().getName(), "Domain Id = " +domainId);
                JSONArray domainArray = dao.getValueDomain(domainId);
                for(Object obj : domainArray) {
                    JSONObject taskObj2 = (JSONObject)obj;
                    String value = taskObj2.get("value").toString();
                    String description = taskObj2.get("description").toString();
                    JSONArray taskArray2 = new JSONArray();
                    if (value.equalsIgnoreCase(attrValue)) {
                        switch (value) {
                            case "REJECTED TO REVIEW ORDER":
                                LogUtil.info(getClass().getName(), "Approval is REJECTED TO REVIEW ORDER!");
                                dao.reviseTaskNonConn_toReviewOrder(parent);
                                taskArray2 = dao.getTaskRevised(parent);
                                LogUtil.info(getClass().getName(), "Task : " +taskArray2);
                                GenerateTaskNew(parent);
                                dao.updateWoDesc(parent);
                                break;
                            case "REJECTED TO SHIPMENT AND DELIVERY TASK":
                                LogUtil.info(getClass().getName(), "Approval is REJECTED TO SHIPMENT AND DELIVERY TASK!");
                                dao.reviseTaskNonConn_toShipmentDelivery(parent);
                                taskArray2 = dao.getTaskRevised(parent);
                                LogUtil.info(getClass().getName(), "Task : " +taskArray2);
                                GenerateTaskNew(parent);
                                dao.updateWoDesc(parent);
                                break;
                            case "REJECTED TO ACTIVATE SERVICE":
                                LogUtil.info(getClass().getName(), "Approval is REJECTED TO ACTIVATE SERVICE!");
                                dao.reviseTaskNonConn_toActivateService(parent);
                                taskArray2 = dao.getTaskRevised(parent);
                                LogUtil.info(getClass().getName(), "Task : " +taskArray2);
                                GenerateTaskNew(parent);
                                dao.updateWoDesc(parent);
                                break;
                            case "REJECTED TO UPLOAD BERITA ACARA":
                                LogUtil.info(getClass().getName(), "Approval is REJECTED TO UPLOAD BERITA ACARA!");
                                dao.reviseTaskNonConn_toUploadBA(parent);
                                taskArray2 = dao.getTaskRevised(parent);
                                LogUtil.info(getClass().getName(), "Task : " +taskArray2);
                                GenerateTaskNew(parent);
                                dao.updateWoDesc(parent);
                                break;
                            default:
                                LogUtil.info(getClass().getName(), "Approval is not REJECTED TO TASK BEFORE!");
                                break;
                        }
                    } else {
                        LogUtil.info(getClass().getName(), "Approval in alndomain is not match!");
                    }
                }
            } else {
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateRevised.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void GenerateTaskNew(String parent) {
        try {
            JSONArray taskTotal = dao.getTask(parent);
            JSONArray taskRevised = dao.getTaskRevised(parent);
            int totalTask = taskTotal.size();
            int x = 1;
            for (Object obj : taskRevised) {
                JSONObject taskObj = (JSONObject)obj;
                int nextTaskId = totalTask+x;
                taskObj.put("wonum", generateDao.getWonum());
                taskObj.put("taskid", nextTaskId*10);
                if (x==1) {
                    taskObj.put("status", "LABASSIGN");
                } else {
                    taskObj.put("status", "APPR");
                }
                dao.generateActivityTask(taskObj);
                x += 1;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateRevised.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateJumlahArnet(String parent, String task, String attrValue) {
        try {
            String[] value = {"0","1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
            JSONArray intValue = taskAttrDao2.getTaskWDM(parent);
            if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                int x=0;
                for (Object obj: intValue){
                    JSONObject actObj = (JSONObject)obj;
                    int intActivity = (int)actObj.get("int_activity");
                    if (Arrays.asList(value).contains(attrValue)) {
                        int attr_value = Integer.parseInt(attrValue);
                        if (attr_value == 0){
                            
                        } else if (attr_value < intActivity) {
                            
                        }
                    }
                    x++;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
