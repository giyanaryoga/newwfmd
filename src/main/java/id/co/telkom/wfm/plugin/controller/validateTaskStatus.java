/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import id.co.telkom.wfm.plugin.dao.*;
import id.co.telkom.wfm.plugin.kafka.ResponseKafka;
import id.co.telkom.wfm.plugin.util.MessageException;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.io.IOException;
import java.sql.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class ValidateTaskStatus {
    GenerateWonumEbisDao woDao = new GenerateWonumEbisDao();
    UpdateTaskStatusEbisDao daoUpdate = new UpdateTaskStatusEbisDao();
    ScmtIntegrationEbisDao daoScmt = new ScmtIntegrationEbisDao();
    TaskHistoryDao daoHistory = new TaskHistoryDao();
//    TestUpdateStatusEbisDao daoTestUpdate = new TestUpdateStatusEbisDao();
    FailwaDao failDao = new FailwaDao();
    ValidateNonCoreProduct validateNonCoreProduct = new ValidateNonCoreProduct();
    ValidateReTools validateRE = new ValidateReTools();
    NonCoreCompleteDao daoNonCore = new NonCoreCompleteDao();
    TaskAttributeUpdateDao taskAttrDao = new TaskAttributeUpdateDao();
    ResponseKafka responseKafka = new ResponseKafka();

    TimeUtil time = new TimeUtil();
//    final JSONObject res = new JSONObject();
    
    private void actualTime(UpdateStatusParam param) {
        String actstart = time.getCurrentTime();
        String actfinish = time.getCurrentTime();
        try {
            String status = param.getStatus();
            String woSequence = param.getSequence();
            int taskid = Integer.parseInt(param.getTaskId());
            int sequence = Integer.parseInt(woSequence);
            if (status.equalsIgnoreCase("STARTWA")) {
                if (sequence == 10) {
                    daoUpdate.updateActualStart(param.getParent(), actstart);
                    daoUpdate.updateActualStartTask(param.getParent(), actstart);   
                }
            } else {
                int nextTaskId = taskid + 10;
                String nextMove = daoUpdate.nextMove(param.getParent(), nextTaskId);
                if (status.equalsIgnoreCase("COMPWA") && nextMove.equalsIgnoreCase("COMPLETE")) {
                    daoUpdate.updateActualFinish(param.getParent(), actfinish);
                    daoUpdate.updateActualFinishTask(param.getParent(), actfinish);
                }   
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void taskMandatoryVariable(String chiefCode, String compViaUi, String wonum) throws MessageException {
        try {
            String actplace = daoUpdate.checkActPlace(wonum);
            if (actplace.equalsIgnoreCase("OUTSIDE")) {
                if (compViaUi.equals("0")) {
                    throw new MessageException("can not compwa via UI");
                } else if (chiefCode.equals("")) {
                    throw new MessageException("labor must be assigned");
                }   
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean startTask(UpdateStatusParam param) throws JSONException {
        boolean startwa = false;
        try {
            String isAssigned = daoUpdate.checkAssignment(param.getWonum());
            String checkActPlace = daoUpdate.checkActPlace(param.getWonum());
            taskValueFromWoAttr(param);
            
            if (!checkActPlace.equalsIgnoreCase("OUTSIDE")) {
                startwa = true;
                validateStartwaProduct(param);
                actualTime(param);
            } else if (isAssigned.equalsIgnoreCase("No Assign") && !checkActPlace.equalsIgnoreCase("OUTSIDE")) {
                startwa = true;
                validateStartwaProduct(param);
                actualTime(param);
            } else if (isAssigned.equalsIgnoreCase("No Assign") && checkActPlace.equalsIgnoreCase("OUTSIDE")) {
                startwa = false;
            } else  if (isAssigned.equalsIgnoreCase("Assign") && checkActPlace.equalsIgnoreCase("OUTSIDE")) {
                startwa = true;
                validateStartwaProduct(param);
                actualTime(param);
            } else {
                startwa = false;
            }
            LogUtil.info(getClass().getName(), "result startwa : " + startwa);
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return startwa;
    }

    public String compwaTask(UpdateStatusParam param) {
        String compwa = "";
        try {
            JSONArray isMandatoryNone = daoUpdate.checkMandatory(param.getWonum());
            JSONArray isMandatoryNull = daoUpdate.isRequired(param.getWonum());
            LogUtil.info(getClass().getName(), "None: " + isMandatoryNone);
            LogUtil.info(getClass().getName(), "Null: " + isMandatoryNull);
            
            int NoneLength = isMandatoryNone.size();
            int NullLength = isMandatoryNull.size();
            
            if (NoneLength == 0 && NullLength == 0) {
                compwa = "true";
            } else {
                compwa = "false";
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return compwa;
    }
    
    public boolean failTask(UpdateStatusParam param) throws JSONException {
        boolean failwa = false;
        try {
            String updateTask = "";
//            String response = "";
            updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
            
            if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                failwa = true;
                failDao.updateWorkFail(param.getWonum(), "WORKFAIL", param.getErrorCode(), param.getEngineerMemo());
                LogUtil.info(getClass().getName(), "Update WORKFAIL Successfully!");
                // Insert data to table WFMMILESTONE
                daoUpdate.insertToWfmMilestone(param.getWonum(), param.getSiteId(), time.getCurrentTime());
                daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                
                JSONObject dataRes = new JSONObject();
                dataRes.put("wonum", param.getParent());
                dataRes.put("milestone", param.getWoStatus());
                //Build Response
                JSONObject data = failDao.getFailWorkJson(param.getParent());
                // Response to Kafka
                String kafkaRes = data.toJSONString();
                //KAFKA DEVELOPMENT
                responseKafka.MilestoneEbis(kafkaRes, param.getSiteId());
                //KAFKA PRODUCTION
//                responseKafka.MilestoneEbis(kafkaRes, param.getSiteId());
            } else {
                failwa = false;
            }
            LogUtil.info(getClass().getName(), "result status : " + failwa);
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return failwa;
    }
    
    private boolean validateStartwaProduct(UpdateStatusParam param) {
        boolean startwa = false;
        String updateTask = "";
        String response;
        try {
            org.json.JSONObject params = validateNonCoreProduct.getParams(param.getWonum());
            String productname = (params.optString("productname", null));
            int isNoncore = daoNonCore.isNonCoreProduct(productname);
            if (isNoncore == 1) {
                boolean validatenoncore = validateNonCoreProduct.validateStartwa(param);
                boolean autofillnoncore = validateNonCoreProduct.nonCoreAutoFill(param.getParent());
                if (validatenoncore) {
                    //GENERATE SID
                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                    if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        response = "Success";
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        startwa = true;
                    }
                } else {
                    startwa = false;
                }
                LogUtil.info(getClass().getName(), "startwa1 : " + startwa);
                
                if (autofillnoncore) {
                    response = "success";
                    if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        response = "Success";
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        startwa = true;
                    }
                } else {
                    startwa = false;
                }
                
                LogUtil.info(getClass().getName(), "startwa2 : " + startwa);
                LogUtil.info(getClass().getName(), "autofillnoncore : " + autofillnoncore);
            } else {
                updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                    response = "Success";
                    daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                    startwa = true;
                } else {
                    startwa = false;
                }
            }
            LogUtil.info(getClass().getName(), "result startwa product : " + startwa);
        } catch (SQLException | JSONException ex) {
            Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return startwa;
    }

    private void completeTask(UpdateStatusParam param) throws JSONException, IOException {
        String updateTask = "";
        try {
            org.json.JSONObject params = validateNonCoreProduct.getParams(param.getWonum());
            String productname = (params.optString("productname", null));
            int isNoncore = daoNonCore.isNonCoreProduct(productname);

            if (isNoncore == 1) {
                //Validate Non-Core COMPLETE
                validateNonCoreProduct.validateComplete(param);
            }
            
            // Update parent status
            daoUpdate.updateParentStatus(param.getParent(), "COMPLETE", time.getCurrentTime(), param.getModifiedBy(), param.getModifiedByName());
            actualTime(param);
            // update task status
            updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
            if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
            }
            
            // Insert data to table WFMMILESTONE
            daoUpdate.insertToWfmMilestone(param.getParent(), param.getSiteId(), time.getCurrentTime());
            //Create response
            JSONObject dataRes = new JSONObject();
            dataRes.put("wonum", param.getParent());
            dataRes.put("milestone", param.getWoStatus());

            //Build Response
            JSONObject data = daoUpdate.getCompleteJson(param.getParent());
            // Response to Kafka
            String kafkaRes = data.toJSONString();
            //KAFKA DEVELOPMENT
            responseKafka.MilestoneEbis(kafkaRes, param.getSiteId());
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void taskValueFromWoAttr(UpdateStatusParam param) {
        try {
            JSONArray taskAttribute = taskAttrDao.getTaskAttributeParent(param.getParent());
            JSONArray workorderAttribute = taskAttrDao.getAttrWoAttribute(param.getParent());
            String taskAttrName;
            String taskAttrValue;
            String woAttrName;
            String woAttrValue;
//            LogUtil.info(getClass().getName(), "task attribute =" +taskAttribute);
//            LogUtil.info(getClass().getName(), "workorder attribute =" +workorderAttribute);
            for (Object obj : taskAttribute) {
                JSONObject taskAttrObj = (JSONObject)obj;
                taskAttrName = (taskAttrObj.get("task_attr_name") == null ? "" : taskAttrObj.get("task_attr_name").toString());
                taskAttrValue = (taskAttrObj.get("task_attr_value") == null ? "" : taskAttrObj.get("task_attr_value").toString());
                for (Object obj2 : workorderAttribute) {
                    JSONObject woAttrObj = (JSONObject)obj2;
                    woAttrName = woAttrObj.get("attr_name").toString();
                    woAttrValue = (woAttrObj.get("attr_value") == null ? "" : woAttrObj.get("attr_value").toString());
                    if (taskAttrName.equalsIgnoreCase(woAttrName)) {
                        taskAttrDao.updateValueTaskAttributeFromWorkorderAttr(param.getParent(), taskAttrName, woAttrValue);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JSONObject validateTask(UpdateStatusParam param) throws JSONException, IOException {
        JSONObject response = new JSONObject();
        try {
            String updateTask = "";
            boolean nextAssign = false;
            int nextTaskId = Integer.parseInt(param.getTaskId()) + 10;
            String isWoDocValue = "";
            String productName = "";
            String[] productList = {"SL_WDM", "INF_SL", "MM_GLOBAL_LINK"};

            switch (param.getActivity()) {
                case "Registration Suplychain":
                case "Registration Suplychain Wifi":
                    // Start of Set Install
                    daoScmt.sendInstall(param.getParent());
                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                    nextAssign = daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                    if (nextAssign && updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        response.put("code", 200);
                        response.put("message", "Mengirim set Install ke SCMT");
                    }
                    break;
                case "Dismantle NTE":
                case "Dismantle AP":
                case "Dismantle AP MESH":
                    // Start of Set Install
                    daoScmt.sendDismantle(param.getParent());
                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                    nextAssign = daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                    if (nextAssign && updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        response.put("code", 200);
                        response.put("message", "Mengirim set Dismantle ke SCMT");
                    }
                    break;
                case "Shipment_Delivery":
                    String woAttr = woDao.getValueWorkorderAttribute(param.getParent(), "ManagedService");
                    if (woAttr.equalsIgnoreCase("Yes") || woAttr.equalsIgnoreCase("YES")) {
                        //validasi attachment file SERVICE_DETAIL
                        int document = daoUpdate.checkAttachedFile(param.getParent(), "SERVICE_DETAIL");
                        if (document == 1) {
                            //response true send url to retools
                            JSONObject validateObl = validateRE.validateOBL(param.getParent());
                            int code = (int)validateObl.get("code");
                            if (code == 200) {
                                updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                                nextAssign = daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                                if (nextAssign && updateTask.equalsIgnoreCase("Update task status berhasil")) {
                                    daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                                    response.put("code", 200);
                                    response.put("message", "Berhasil mengupdate status, Mengirim Document to ReTools and create Customer!");
                                }   
                            } else {
                                response.put("code", (int)validateObl.get("code"));
                                response.put("message", validateObl.get("message").toString());
                            }
                        } else {
                            //response false, gagal send url dan kirim message gagal
                            response.put("code", 422);
                            response.put("message", "Document 'SERVICE_DETAIL' is not found!");
                        }
                    } else {
                        updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                        nextAssign = daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                        if (nextAssign && updateTask.equalsIgnoreCase("Update task status berhasil")) {
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                            response.put("code", 200);
                            response.put("message", "Berhasil mengupdate status Compwa!");
                        }
                    }
                    break;
                case "Upload_Berita_Acara":
                    // check documentname
                    try {
                        isWoDocValue = daoUpdate.checkWoDoc(param.getParent());
                        if (isWoDocValue.equalsIgnoreCase("Nama File sudah benar, Update status COMPWA berhasil")) {
                            response.put("code", 200);
                            response.put("message", isWoDocValue);

                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                            daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        } else {
                            response.put("code", 422);
                            response.put("message", isWoDocValue);
                        }
                    } catch (SQLException e) {
                        LogUtil.info(getClass().getName(), "ERROR : " + e);
                    } catch (JSONException ex) {
                        Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                break;
                case "WFMNonCore Review Order TSQ IP Transit":
                    if (productName.equalsIgnoreCase("MM_IP_TRANSIT")) {
                        String sbrValue = daoUpdate.getTaskAttrValue(param.getWonum(), "SBR");
                        int document = daoUpdate.checkAttachedFile(param.getParent(), "LOA");
                        if (sbrValue.equalsIgnoreCase("YES") && document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document LOA ada...");
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                            daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        } else {
                            response.put("code", 422);
                            response.put("message", "Document LOA Tidak ada...");
                        }
                    }
                    break;
                case "WFMNonCore Deactivate Access WDM":
                    productName = daoUpdate.getProductName(param.getWonum());
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoUpdate.checkAttachedFile(param.getParent(), "BA DEAKTIVASI AKSES");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document BA DEAKTIVASI AKSES ada...");
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                            daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        } else {
                            response.put("code", 422);
                            response.put("message", "Document BA DEAKTIVASI AKSES Tidak ada...");
                        }
                    }
                    break;
                case "WFMNonCore Deactivate WDM":
                    productName = daoUpdate.getProductName(param.getWonum());
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoUpdate.checkAttachedFile(param.getParent(), "BA DEAKTIVASI TRANS");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document BA DEAKTIVASI TRANS ada...");
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                            daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        } else {
                            response.put("code", 422);
                            response.put("message", "Document BA DEAKTIVASI TRANS Tidak ada...");
                        }
                    }
                    break;
                case "WFMNonCore Allocate Access":
                    productName = daoUpdate.getProductName(param.getWonum());
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoUpdate.checkAttachedFile(param.getParent(), "DATEK AKSES");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document DATEK AKSES ada...");
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                            daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        } else {
                            response.put("code", 422);
                            response.put("message", "Document DATEK AKSES Tidak ada...");
                        }
                    }
                    break;
                case "WFMNonCore Allocate WDM":
                    productName = daoUpdate.getProductName(param.getWonum());
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoUpdate.checkAttachedFile(param.getParent(), "DATEK TRANSPORT");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document DATEK TRANSPORT ada...");
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                            daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        } else {
                            response.put("code", 422);
                            response.put("message", "Document DATEK TRANSPORT Tidak ada...");
                        }
                    }
                    break;
                case "WFMNonCore Activate And Integration WDM":
                    productName = daoUpdate.getProductName(param.getWonum());
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoUpdate.checkAttachedFile(param.getParent(), "BA INTEGRASI TRANS");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document BA INTEGRASI TRANS ada...");
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                            daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                        } else {
                            response.put("code", 422);
                            response.put("message", "Document BA INTEGRASI TRANS Tidak ada...");
                        }
                    }
                    break;
//                case "Pickup NTE from SCM Manual":
//                case "Pickup NTE from SCM Wifi":
//                case "Pickup AP From SCM Wifi":
//                case "Pickup NTE from SCM":
//                case "Install NTE Manual":
//                case "Install NTE Wifi":
//                case "Install NTE":
//                case "Install AP":
//                    
//                    break;
                default:
                    // Define the next move
                    final String nextMove = daoUpdate.nextMove(param.getParent(), nextTaskId);
                    if ("COMPLETE".equals(nextMove)) {
                        completeTask(param);
                        response.put("code", 200);
                        response.put("message", "Berhasil mengupdate status, Mengirim Status COMPLETE ke OSM");
                    } else {
                        //Give LABASSIGN to next task
                        nextAssign = daoUpdate.nextAssign(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                        if (nextAssign) {
                            response.put("code", 200);
                            response.put("message", "Update Status compwa is success");
                        }
                        daoUpdate .updateWoDesc(param.getParent(), nextTaskId, param.getModifiedBy(), param.getModifiedByName());
                        daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy(), param.getModifiedByName());
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), param.getModifiedByName());
                    }
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
}
