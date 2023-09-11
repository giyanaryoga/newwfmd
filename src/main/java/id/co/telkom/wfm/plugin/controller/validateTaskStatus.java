/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import id.co.telkom.wfm.plugin.controller.validateNonCoreProduct;
import id.co.telkom.wfm.plugin.dao.*;
import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.*;
import static javassist.runtime.Desc.getParams;
import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class validateTaskStatus {

    UpdateTaskStatusEbisDao daoUpdate = new UpdateTaskStatusEbisDao();
    ScmtIntegrationEbisDao daoScmt = new ScmtIntegrationEbisDao();
    TaskHistoryDao daoHistory = new TaskHistoryDao();
//    TestUpdateStatusEbisDao daoTestUpdate = new TestUpdateStatusEbisDao();
    validateNonCoreProduct validateNonCoreProduct = new validateNonCoreProduct();
    NonCoreCompleteDao daoNonCore = new NonCoreCompleteDao();

    TimeUtil time = new TimeUtil();
    final JSONObject res = new JSONObject();

    public boolean startTask(UpdateStatusParam param) throws JSONException {
        boolean startwa = false;
        try {
            String updateTask = "";
            String response = "";

            String isAssigned = daoUpdate.checkAssignment(param.getWonum());
            String checkActPlace = daoUpdate.checkActPlace(param.getWonum());

            org.json.JSONObject params = validateNonCoreProduct.getParams(param.getWonum());
            String productname = (params.optString("productname", null));

            int isNoncore = daoNonCore.isNonCoreProduct(productname);
            if (isNoncore == 1) {
                boolean validatenoncore = validateNonCoreProduct.validateStartwa(param);
                boolean autofillnoncore = validateNonCoreProduct.nonCoreAutoFill(param.getParent());
                if (validatenoncore) {
                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                    if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        response = "Success";
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        startwa = true;
                    }
                    response = "Generate SID Berhasil";
                } else if (autofillnoncore) {
                    response = "success";
                    if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        response = "Success";
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        startwa = true;
                    }
                } else {
                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                    if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        response = "Success";
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        startwa = true;
                    }
                }
            } else {
                if (checkActPlace.equalsIgnoreCase("OUTSIDE") && isAssigned.equalsIgnoreCase("Assign")) {
                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                    if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        response = "Success";
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        startwa = true;
                    } else {
                        startwa = false;
                    }
                } else {
                    if (!checkActPlace.equalsIgnoreCase("OUTSIDE") && isAssigned.equalsIgnoreCase("No Assign")) {
                        startwa = true;
                        updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                        if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                            response = "Success";
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                            startwa = true;
                        } else {
                            startwa = false;
                        }
                    } else {
                        response = "Failed";
                        startwa = false;
                    }
                }
                
//                if (!startwa) {
//                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
//                    if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
//                        response = "Success";
//                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
//                        startwa = true;
//                    } else {
//                        startwa = false;
//                    }
//                } else {
//                    startwa = false;
//                    response = "Task is not Assign to Labor yet";
//                }
                   
//                if (!isAssigned && checkActPlace.equalsIgnoreCase("OUTSIDE")) {
//                    response = "Task is not Assign to Labor yet";
//                    startwa = false;
//                } else {
//                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
//                    if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
//                        response = "Success";
//                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
//                        startwa = true;
//                    }
//                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return startwa;
    }

    public boolean compwaTask(UpdateStatusParam param) {
        boolean compwa = false;
        try {
            boolean isMandatoryValue = daoUpdate.checkMandatory(param.getWonum());
            int required = daoUpdate.isRequired(param.getWonum());
            LogUtil.info(getClass().getName(), "test: " + isMandatoryValue);
            if (isMandatoryValue && required == 1) {
                compwa = true;
            } else if (!isMandatoryValue && required == 0) {
                compwa = true;
            } else {
                compwa = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return compwa;
    }

    private void completeTask(UpdateStatusParam param) throws JSONException {
        String updateTask = "";
        try {

            org.json.JSONObject params = validateNonCoreProduct.getParams(param.getWonum());
            String productname = (params.optString("productname", null));
            int isNoncore = daoNonCore.isNonCoreProduct(productname);

            if (isNoncore == 1) {
                // Update parent status
                daoUpdate.updateParentStatus(param.getParent(), "COMPLETE", time.getCurrentTime(), param.getModifiedBy());
                LogUtil.info(getClass().getName(), "Update COMPLETE Successfully");

                // update task status
                updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                    daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                }

                //Validate Non-Core COMPLETE
                validateNonCoreProduct.validateComplete(param);

                // Insert data to table WFMMILESTONE
                daoUpdate.insertToWfmMilestone(param.getWonum(), param.getSiteId(), time.getCurrentTime());
                //Create response
                JSONObject dataRes = new JSONObject();
                dataRes.put("wonum", param.getParent());
                dataRes.put("milestone", param.getWoStatus());

                //Build Response
                JSONObject data = daoUpdate.getCompleteJson(param.getParent());
                // Response to Kafka
//                String topic = "WFM_MILESTONE_ENTERPRISE_" + param.getSiteId().replaceAll("\\s+", "");
//                String kafkaRes = data.toJSONString();
//                KafkaProducerTool kaf = new KafkaProducerTool();
//                kaf.generateMessage(kafkaRes, topic, "");
            } else {
                // Update parent status
                daoUpdate.updateParentStatus(param.getParent(), "COMPLETE", time.getCurrentTime(), param.getModifiedBy());
                LogUtil.info(getClass().getName(), "Update COMPLETE Successfully");

                // update task status
                updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                    daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                }
                // Insert data to table WFMMILESTONE
                daoUpdate.insertToWfmMilestone(param.getWonum(), param.getSiteId(), time.getCurrentTime());
                //Create response
                JSONObject dataRes = new JSONObject();
                dataRes.put("wonum", param.getParent());
                dataRes.put("milestone", param.getWoStatus());

                //Build Response
                JSONObject data = daoUpdate.getCompleteJson(param.getParent());
                // Response to Kafka
                String topic = "WFM_MILESTONE_ENTERPRISE_" + param.getSiteId().replaceAll("\\s+", "");
                String kafkaRes = data.toJSONString();
                KafkaProducerTool kaf = new KafkaProducerTool();
                kaf.generateMessage(kafkaRes, topic, "");
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JSONObject validateTask(UpdateStatusParam param) throws JSONException {
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
                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                    nextAssign = daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                    if (nextAssign && updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        response.put("code", 200);
                        response.put("message", "Mengirim set Install ke SCMT");
                    }
                    break;
                case "Dismantle NTE":
                case "Dismantle AP":
                case "Dismantle AP MESH":
                    // Start of Set Install
                    daoScmt.sendDismantle(param.getParent());
                    updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                    nextAssign = daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                    if (nextAssign && updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        response.put("code", 200);
                        response.put("message", "Mengirim set Dismantle ke SCMT");
                    }
                    break;
                case "Upload_Berita_Acara":
                    // check documentname
                    try {
                        isWoDocValue = daoUpdate.checkWoDoc(param.getParent());
                        if (isWoDocValue.equalsIgnoreCase("Nama File sudah benar, Update status COMPWA berhasil")) {
                            response.put("code", 200);
                            response.put("message", isWoDocValue);

                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        } else {
                            response.put("code", 422);
                            response.put("message", isWoDocValue);
                        }
                    } catch (SQLException e) {
                        LogUtil.info(getClass().getName(), "ERROR : " + e);
                    } catch (JSONException ex) {
                        Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
                    }
                break;
                case "WFMNonCore Review Order TSQ IP Transit":
                    if (productName.equalsIgnoreCase("MM_IP_TRANSIT")) {
                        String sbrValue = daoUpdate.getTaskAttrValue(param.getWonum(), "SBR");
                        int document = daoUpdate.checkAttachedFile(param.getParent(), "LOA");
                        if (sbrValue.equalsIgnoreCase("YES") && document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document LOA ada...");
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
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
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
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
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
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
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
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
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
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
                            daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        } else {
                            response.put("code", 422);
                            response.put("message", "Document BA INTEGRASI TRANS Tidak ada...");
                        }
                    }
                    break;
                default:
                    // Define the next move
                    final String nextMove = daoUpdate.nextMove(param.getParent(), Integer.toString(nextTaskId));
                    if ("COMPLETE".equals(nextMove)) {
                        completeTask(param);
                        response.put("code", 200);
                        response.put("message", "Berhasil mengupdate status, Mengirim Status COMPLETE ke OSM");
                    } else {
                        //Give LABASSIGN to next task
                        nextAssign = daoUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                        if (nextAssign) {
                            response.put("code", 200);
                            response.put("message", "Update Status compwa is success");
                        }
                        daoUpdate.updateWoDesc(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                        daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                    }
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
}
