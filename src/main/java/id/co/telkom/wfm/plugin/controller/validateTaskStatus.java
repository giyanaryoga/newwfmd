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
import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class validateTaskStatus {

    UpdateTaskStatusEbisDao daoUpdate = new UpdateTaskStatusEbisDao();
    ScmtIntegrationEbisDao daoScmt = new ScmtIntegrationEbisDao();
    TaskHistoryDao daoHistory = new TaskHistoryDao();
    TestUpdateStatusEbisDao daoTestUpdate = new TestUpdateStatusEbisDao();
    validateNonCoreProduct validateNonCoreProduct = new validateNonCoreProduct();
    TimeUtil time = new TimeUtil();
    final JSONObject res = new JSONObject();

    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        Timestamp ts = Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }

    public boolean startTask(UpdateStatusParam param) throws JSONException {
        boolean startwa = false;

        try {
            String updateTask = "";
            String response = "";
            boolean isAssigned = daoTestUpdate.checkAssignment(param.getWonum());
            String checkActPlace = daoTestUpdate.checkActPlace(param.getWonum());
            boolean validatenoncore = validateNonCoreProduct.validateStartwa(param);
            boolean autoFill = validateNonCoreProduct.nonCoreAutoFill(param.getParent());
            if (!isAssigned && checkActPlace.equalsIgnoreCase("OUTSIDE")) {
                response = "Task is not Assign to Labor yet";
                startwa = false;
            } else if (validatenoncore) {
                response = "Generate SID Successfully, Update Status STARTWA";
                startwa = true;
            } else {
                updateTask = daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                    response = "Success";
                    daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                    startwa = true;
                }
            }

            if(autoFill){
                response = response + "\nAuto Fill Successfully";
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return startwa;
    }

    public boolean compwaTask(UpdateStatusParam param) {
        boolean compwa = false;
        try {
            boolean isMandatoryValue = daoTestUpdate.checkMandatory(param.getWonum());
//            LogUtil.info(getClass().getName(), "test: " + isMandatoryValue);
            Integer isRequired = daoTestUpdate.isRequired(param.getWonum());
            compwa = !(isMandatoryValue && isRequired != 1);
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return compwa;
    }

    private void completeTask(UpdateStatusParam param) {
        String updateTask = "";
        String response = "";
        try {
            // Update parent status
            daoTestUpdate.updateParentStatus(param.getParent(), "COMPLETE", time.getCurrentTime(), param.getModifiedBy());
            LogUtil.info(getClass().getName(), "Update COMPLETE Successfully");

            // update task status
            updateTask = daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
            if (updateTask.equalsIgnoreCase("Update task status berhasil")) {
                daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
            }
            // Insert data to table WFMMILESTONE
            daoTestUpdate.insertToWfmMilestone(param.getWonum(), param.getSiteId(), time.getCurrentTime());
            //Create response
            JSONObject dataRes = new JSONObject();
            dataRes.put("wonum", param.getParent());
            dataRes.put("milestone", param.getWoStatus());

            //Build Response
            JSONObject data = daoTestUpdate.getCompleteJson(param.getParent());
            // Response to Kafka
            String topic = "WFM_MILESTONE_ENTERPRISE_" + param.getSiteId().replaceAll("\\s+", "");
            String kafkaRes = data.toJSONString();
            KafkaProducerTool kaf = new KafkaProducerTool();
            kaf.generateMessage(kafkaRes, topic, "");
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public JSONObject validateTask(UpdateStatusParam param) {
        JSONObject response = new JSONObject();
        try {
            String updateTask = "";
            boolean nextAssign = false;
            int nextTaskId = Integer.parseInt(param.getTaskId()) + 10;
            String isWoDocValue = "";
            String productName = daoTestUpdate.getProductName(param.getWonum());
            String[] productList = {"SL_WDM", "INF_SL", "MM_GLOBAL_LINK"};

            switch (param.getActivity()) {
                case "Registration Suplychain":
                case "Registration Suplychain Wifi":
                    // Start of Set Install
                    daoScmt.sendInstall(param.getParent());
                    updateTask = daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                    nextAssign = daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
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
                    updateTask = daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                    nextAssign = daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                    if (nextAssign && updateTask.equalsIgnoreCase("Update task status berhasil")) {
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        response.put("code", 200);
                        response.put("message", "Mengirim set Dismantle ke SCMT");
                    }
                    break;
                case "Upload_Berita_Acara":
                    // check documentname
                    try {
                    isWoDocValue = daoTestUpdate.checkWoDoc(param.getParent());
                    if (isWoDocValue.equalsIgnoreCase("Nama File sudah benar, Update status COMPWA berhasil")) {
                        response.put("code", 200);
                        response.put("message", isWoDocValue);

                        daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                        daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                        daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                    } else {
                        response.put("code", 400);
                        response.put("message", isWoDocValue);
                    }
                } catch (SQLException e) {
                    LogUtil.info(getClass().getName(), "ERROR : " + e);
                } catch (JSONException ex) {
                    Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;
                case "WFMNonCore Review Order TSQ IP Transit":
                    if(productName.equalsIgnoreCase("MM_IP_TRANSIT")) {
                        String sbrValue = daoTestUpdate.getTaskAttrValue(param.getWonum(), "SBR");
                        int document = daoTestUpdate.checkAttachedFile(param.getParent(), "LOA");
                        if (sbrValue.equalsIgnoreCase("YES") && document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document LOA ada...");
                            daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        } else {
                            response.put("code", 400);
                            response.put("message", "Document LOA Tidak ada...");
                        }
                    }
                break;
                case "WFMNonCore Deactivate Access WDM":
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoTestUpdate.checkAttachedFile(param.getParent(), "BA DEAKTIVASI AKSES");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document BA DEAKTIVASI AKSES ada...");
                            daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        } else {
                            response.put("code", 400);
                            response.put("message", "Document BA DEAKTIVASI AKSES Tidak ada...");
                        }
                    }
                break;
                case "WFMNonCore Deactivate WDM":
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoTestUpdate.checkAttachedFile(param.getParent(), "BA DEAKTIVASI TRANS");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document BA DEAKTIVASI TRANS ada...");
                            daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        } else {
                            response.put("code", 400);
                            response.put("message", "Document BA DEAKTIVASI TRANS Tidak ada...");
                        }
                    }
                break;
                case "WFMNonCore Allocate Access":
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoTestUpdate.checkAttachedFile(param.getParent(), "DATEK AKSES");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document DATEK AKSES ada...");
                            daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        } else {
                            response.put("code", 400);
                            response.put("message", "Document DATEK AKSES Tidak ada...");
                        }
                    }
                break;
                case "WFMNonCore Allocate WDM":
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoTestUpdate.checkAttachedFile(param.getParent(), "DATEK TRANSPORT");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document DATEK TRANSPORT ada...");
                            daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        } else {
                            response.put("code", 400);
                            response.put("message", "Document DATEK TRANSPORT Tidak ada...");
                        }
                    }
                break;
                case "WFMNonCore Activate And Integration WDM":
                    if (Arrays.asList(productList).contains(productName)) {
                        int document = daoTestUpdate.checkAttachedFile(param.getParent(), "BA INTEGRASI TRANS");
                        if (document == 1) {
                            response.put("code", 200);
                            response.put("message", "Document BA INTEGRASI TRANS ada...");
                            daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy(), "WFM");
                        } else {
                            response.put("code", 400);
                            response.put("message", "Document BA INTEGRASI TRANS Tidak ada...");
                        }
                    }
                break;
                default:
                    // Define the next move
                    final String nextMove = daoTestUpdate.nextMove(param.getParent(), Integer.toString(nextTaskId));

                    if ("COMPLETE".equals(nextMove)) {
                        completeTask(param);
                        response.put("code", 200);
                        response.put("message", "Berhasil mengupdate status, Mengirim Status COMPLETE ke OSM");
                    } else {
                        //Give LABASSIGN to next task
                        nextAssign = daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                        if (nextAssign) {
                            response.put("code", 200);
                            response.put("message", "Update Status compwa is success");
                        }
                        daoTestUpdate.updateWoDesc(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                        daoTestUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                    }
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
}
