/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import id.co.telkom.wfm.plugin.controller.validateNonCoreProduct;
import id.co.telkom.wfm.plugin.UpdateTaskStatusEbis;
import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import id.co.telkom.wfm.plugin.dao.TaskHistoryDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.dao.TestUpdateStatusEbisDao;
import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
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
    TestUpdateStatusEbisDao daoTestUpdate = new TestUpdateStatusEbisDao();
    validateNonCoreProduct validateNonCoreProduct = new validateNonCoreProduct();
    TimeUtil time = new TimeUtil();
    final JSONObject res = new JSONObject();
    HttpServletResponse hsr1;

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
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
        return startwa;
    }

    public boolean compwaTask(UpdateStatusParam param) {
        boolean compwa = false;
        try {
            boolean isMandatoryValue = daoTestUpdate.checkMandatory(param.getWonum());
            LogUtil.info(getClass().getName(), "test: " + isMandatoryValue);
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

            switch (param.getDescription()) {
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
                case "Upload Berita Acara":
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
                        response.put("code", 200);
                        response.put("message", isWoDocValue);
                        LogUtil.info(getClass().getName(), "RESULT : " + isWoDocValue);
                        LogUtil.info(getClass().getName(), "RESPONSE : " + res);
                    }
                } catch (SQLException e) {
                    LogUtil.info(getClass().getName(), "ERROR : " + e);
                } catch (JSONException ex) {
                    Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
                }
                break;

                default:
                    // Define the next move
                    final String nextMove = daoTestUpdate.nextMove(param.getParent(), Integer.toString(nextTaskId));

                    if ("COMPLETE".equals(nextMove)) {
                        completeTask(param);
                        response.put("code", 200);
                        response.put("message", "Berhasil mengupdate status, Mengirim Status COMPLETE ke OSM");
//                        response = "Berhasil mengupdate status, Mengirim Status COMPLETE ke OSM";
                    } else {
                        //Give LABASSIGN to next task
                        nextAssign = daoTestUpdate.nextAssign(param.getParent(), Integer.toString(nextTaskId), param.getModifiedBy());
                        if (nextAssign) {
                            response.put("code", 200);
                            response.put("message", "Update Status compwa is success");
//                            response = "Update Status compwa is success";
//                            LogUtil.info(getClass().getName(), "RESPONSE : " + res);
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
