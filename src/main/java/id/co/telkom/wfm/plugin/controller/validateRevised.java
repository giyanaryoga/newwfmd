/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.RevisedTask;
import id.co.telkom.wfm.plugin.dao.RevisedTaskDao;
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
public class validateRevised {
    RevisedTaskDao dao = new RevisedTaskDao();
    
    public void validate (String parent, String wonum, String attrName, String attrValue, String task) {
        try {
            JSONArray taskArray = dao.getTask(parent);
            LogUtil.info(getClass().getName(), "Task => " + taskArray);

            switch(attrName){
                case "APPROVAL_SURVEY":
                    if (attrValue.equalsIgnoreCase("BACK_TO_SURVEY")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(getClass().getName(), "Approval Survey is not BACK_TO_SURVEY");
                    }
                break;
                case "APPROVAL":
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
                                if (description.equalsIgnoreCase(attrValue)) {
                                    switch (value) {
                                        case "REJECTED TO REVIEW ORDER":
                                            LogUtil.info(getClass().getName(), "Approval is REJECTED TO REVIEW ORDER!");
                                            dao.reviseTaskNonConn_toReviewOrder(parent);
                                            taskArray2 = dao.getTaskRevised(parent);
                                            LogUtil.info(getClass().getName(), "Task : " +taskArray2);
                                            for (Object obj2 : taskArray2) {
                                                JSONObject taskObj = (JSONObject)obj2;
                                                LogUtil.info(getClass().getName(), "Wonum : " +taskObj.get("detailActCode").toString());
                                                dao.generateActivityTaskNonConn(taskObj.get("parent").toString(), taskObj.get("detailActCode").toString());
                                                dao.updateWoDesc(taskObj.get("parent").toString());
                                            }
                                            break;
                                        case "REJECTED TO SHIPMENT AND DELIVERY TASK":
                                            LogUtil.info(getClass().getName(), "Approval is REJECTED TO SHIPMENT AND DELIVERY TASK!");
                                            dao.reviseTaskNonConn_toShipmentDelivery(parent);
                                            taskArray2 = dao.getTaskRevised(parent);
                                            LogUtil.info(getClass().getName(), "Task : " +taskArray2);
                                            for (Object obj2 : taskArray2) {
                                                JSONObject taskObj = (JSONObject)obj2;
                                                LogUtil.info(getClass().getName(), "Wonum : " +taskObj.get("detailActCode").toString());
                                                dao.generateActivityTaskNonConn(taskObj.get("parent").toString(), taskObj.get("detailActCode").toString());
                                                dao.updateWoDesc(taskObj.get("parent").toString());
                                            }
                                            break;
                                        case "REJECTED TO ACTIVATE SERVICE":
                                            LogUtil.info(getClass().getName(), "Approval is REJECTED TO ACTIVATE SERVICE!");
                                            dao.reviseTaskNonConn_toActivateService(parent);
                                            taskArray2 = dao.getTaskRevised(parent);
                                            LogUtil.info(getClass().getName(), "Task : " +taskArray2);
                                            for (Object obj2 : taskArray2) {
                                                JSONObject taskObj = (JSONObject)obj2;
                                                LogUtil.info(getClass().getName(), "Wonum : " +taskObj.get("detailActCode").toString());
                                                dao.generateActivityTaskNonConn(taskObj.get("parent").toString(), taskObj.get("detailActCode").toString());
                                                dao.updateWoDesc(taskObj.get("parent").toString());
                                            }
                                            break;
                                        case "REJECTED TO UPLOAD BERITA ACARA":
                                            LogUtil.info(getClass().getName(), "Approval is REJECTED TO UPLOAD BERITA ACARA!");
                                            dao.reviseTaskNonConn_toUploadBA(parent);
                                            taskArray2 = dao.getTaskRevised(parent);
                                            LogUtil.info(getClass().getName(), "Task : " +taskArray2);
                                            for (Object obj2 : taskArray2) {
                                                JSONObject taskObj = (JSONObject)obj2;
                                                LogUtil.info(getClass().getName(), "Wonum : " +taskObj.get("detailActCode").toString());
                                                dao.generateActivityTaskNonConn(taskObj.get("parent").toString(), taskObj.get("detailActCode").toString());
                                                dao.updateWoDesc(taskObj.get("parent").toString());
                                            }
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
                        if (attrValue.equalsIgnoreCase("REJECTED")) {
                            dao.reviseTask(parent);
                            dao.generateActivityTask(parent);
                        } else {
                            LogUtil.info(getClass().getName(), "Approval is not REJECTED");
                            LogUtil.info(getClass().getName(), "Task" + taskArray);
                        }
                    }
                break;
                case "NODE_ID":
                    if (!attrValue.equalsIgnoreCase("None")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(getClass().getName(), "Approval Survey is None");
                    }
                break;
                case "ACCESS REQUIRED 1":
                    if (attrValue.equalsIgnoreCase("NO")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(getClass().getName(), "Approval Survey is not NO");
                    }
                break;
                case "ACCESS REQUIRED 2":
                    if (attrValue.equalsIgnoreCase("NO")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(getClass().getName(), "Approval Survey is not NO");
                    }
                break;
                case "APPROVED":
                    if (attrValue.equalsIgnoreCase("REJECTED")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(getClass().getName(), "Approval Survey is not REJECTED");
                    }
                break;
                case "TIPE MODIFY":
                    if (!attrValue.equalsIgnoreCase("Modify Number")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else if (attrValue.equalsIgnoreCase("Modify Concurrent")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else if (attrValue.equalsIgnoreCase("Modify IP")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else if (attrValue.equalsIgnoreCase("Modify Bandwidth") || attrValue.equalsIgnoreCase("Modify Address")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else if (attrValue.equalsIgnoreCase("Modify Concurrent Dan Bandwidth")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else if (attrValue.equalsIgnoreCase("Modify Number, Concurrent, Bandwidth")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(getClass().getName(), "Approval Survey is not Modify Number");
                    }
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
                default:
                    LogUtil.info(getClass().getName(), "Attribute name dan value tidak memenuhi Revised Task");
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(RevisedTask.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
