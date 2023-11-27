/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.TaskAttributeUpdateDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.model.MyStaffParam;
import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import id.co.telkom.wfm.plugin.util.ResponseAPI;
import java.io.IOException;
import java.sql.SQLException;
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
public class ValidateMyStaff {
    UpdateTaskStatusEbisDao daoUpdate = new UpdateTaskStatusEbisDao();
    TaskAttributeUpdateDao daoAttr = new TaskAttributeUpdateDao();
    ValidateTaskAttribute validateTaskAttr1 = new ValidateTaskAttribute();
    ValidateTaskAttribute2 validateTaskAttr2 = new ValidateTaskAttribute2();
    ValidateRevised validateRevised = new ValidateRevised();
    ValidateTaskStatus validateTaskStatus = new ValidateTaskStatus();
    JSONObject taskObj = new JSONObject();
    ResponseAPI responseTemplete = new ResponseAPI();
    private static final String[] unusedUpdateParams = {"task","wonum","siteid","statusiface","np_statusmemo","chiefcode"};
    
    public JSONObject updateTaskStatus(UpdateStatusParam param, JSONObject body) {
        JSONObject resp = new JSONObject();
        try {
            LogUtil.info(getClass().getName(), "Start Process: Update Task Status MyStaff");
            param.setWonum(body.get("wonum") == null ? "" : body.get("wonum").toString());
            param.setSiteId(body.get("siteid") == null ? "" : body.get("siteid").toString());
            param.setWolo1(body.get("wolo1") == null ? "" : body.get("wolo1").toString());
            param.setStatus(body.get("status") == null ? "" : body.get("status").toString());
            param.setCpeVendor(body.get("cpe_vendor") == null ? "" : body.get("cpe_vendor").toString());
            param.setCpeModel(body.get("cpe_model") == null ? "" : body.get("cpe_model").toString());
            param.setCpeSerialNumber(body.get("cpe_serial_number") == null ? "" : body.get("cpe_serial_number").toString());
            param.setTkCustomHeader03(body.get("tk_custom_header_03") == null ? "" : body.get("tk_custom_header_03").toString());
            param.setTkCustomHeader04(body.get("tk_custom_header_04") == null ? "" : body.get("tk_custom_header_04").toString());
            param.setTkCustomHeader10(body.get("tk_custom_header_10") == null ? "" : body.get("tk_custom_header_10").toString());
            param.setLaborScmt(body.get("labor_scmt") == null ? "" : body.get("labor_scmt").toString());
            param.setLatitude(body.get("latitude") == null ? "" : body.get("latitude").toString());
            param.setLongitude(body.get("longitude") == null ? "" : body.get("longitude").toString());
            param.setStatusiface(body.get("statusiface") == null ? "" : body.get("statusiface").toString());
            param.setStatusMemo(body.get("np_statusmemo") == null ? "" : body.get("np_statusmemo").toString());
            param.setErrorCode(body.get("errorcode") == null ? "" : body.get("errorcode").toString());
            param.setEngineerMemo((body.get("engineermemo") == null ? "" : body.get("engineermemo").toString()));
            param.setUrlEvidence(body.get("urlevidence") == null ? "" : body.get("urlevidence").toString());
            param.setSubErrorCode(body.get("suberrorcode") == null ? "" : body.get("suberrorcode").toString());

            JSONObject getTaskUpdate = daoUpdate.getTask(param.getWonum());
            taskObj.put("taskid", (int) getTaskUpdate.get("taskid"));
            taskObj.put("wosequence", getTaskUpdate.get("wosequence"));
            taskObj.put("detailactcode", getTaskUpdate.get("detailactcode"));
            taskObj.put("description", getTaskUpdate.get("description"));
            taskObj.put("parent", getTaskUpdate.get("parent"));
            String woSequence = taskObj.get("wosequence").toString();
//            String description = taskObj.get("description").toString();
            String parent = taskObj.get("parent").toString();
            param.setSequence(woSequence);
            param.setParent(parent);
            param.setActivity(taskObj.get("detailactcode").toString());
            param.setTaskId(taskObj.get("taskid").toString());
//            LogUtil.info(ValidateMyStaff.class.getName(), "parent: " + parent);
//            LogUtil.info(ValidateMyStaff.class.getName(), "wosequence: " + woSequence);
//            LogUtil.info(ValidateMyStaff.class.getName(), "description: " + description);
            String message = "";
            switch (param.getStatus()) {
                case "STARTWA":
                    boolean validate = validateTaskStatus.startTask(param);
                    if (validate) {
                        message = "Successfully update status";
                        resp = responseTemplete.ResponseMessage(200, message);
                    } else {
                        message = "Please assign task to Laborcode / Amcrew first";
                        resp = responseTemplete.ResponseMessage(422, message);
                    }
                    break;
                case "COMPWA":
                    String validateCompwa = validateTaskStatus.compwaTask(param);
                    LogUtil.info(getClass().getName(), "VALIDATE: " + validateCompwa);
                    if (validateCompwa.equalsIgnoreCase("true")) {
                        JSONObject response = validateTaskStatus.validateTask(param);
                        if ((int) response.get("code") == 200) {
                            resp = responseTemplete.ResponseMessage(200, response.get("message").toString());
                        } else {
                            resp = responseTemplete.ResponseMessage((int) response.get("code"), response.get("message").toString());
                        }
                    } else {
                        message = "Please insert Task Attribute in Mandatory";
                        resp = responseTemplete.ResponseMessage(422, message);
                    }
                    break;
                case "":
                    //jika status null/"" maka hanya update column lain tanpa mengupdate status
                    //column yang tidak terupdate: WONUM, SITEID
                    daoUpdate.updateMyStaffStatusNull(param.getWonum(), param);
                    message = "Success Update Workorder for Status is NULL";
                    resp = responseTemplete.ResponseMessage(200, message);
                    break;
            }
        } catch (SQLException | JSONException | IOException ex) {
            Logger.getLogger(ValidateMyStaff.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resp;
    }
    
    public JSONObject updateTaskAttr(MyStaffParam paramAttr, JSONObject body) {
        JSONObject res = new JSONObject();
        try {
            LogUtil.info(getClass().getName(), "Start Process: Update Task Attribute MyStaff");
            Object request = (Object)body.get("workorderspec");
            JSONArray request_wospec = new JSONArray();
            JSONArray data = new JSONArray();
            boolean updateAttr = false;

            if (request instanceof JSONObject){
                request_wospec.add(request);
            } else if (request instanceof JSONArray) {
                request_wospec = (JSONArray) request;
            }

            for (Object obj : request_wospec) {
                JSONObject attrObj = (JSONObject)obj;
                JSONObject resp = new JSONObject();
                paramAttr.setWonum(attrObj.get("wonum") == null ? "" : attrObj.get("wonum").toString());
                paramAttr.setAssetAttrId(attrObj.get("assetattrid") == null ? "" : attrObj.get("assetattrid").toString());
                paramAttr.setValue(attrObj.get("alnvalue") == null ? "" : attrObj.get("alnvalue").toString());
                paramAttr.setSiteid(attrObj.get("siteid") == null ? "" : attrObj.get("siteid").toString());
                paramAttr.setChangeBy(attrObj.get("changeby") == null ? "" : attrObj.get("changeby").toString());
                paramAttr.setChangeDate(attrObj.get("changedate") == null ? "" : attrObj.get("changedate").toString());
                
                JSONObject getTaskUpdate = daoUpdate.getTask(paramAttr.getWonum());
                taskObj.put("taskid", (int) getTaskUpdate.get("taskid"));
                taskObj.put("wosequence", getTaskUpdate.get("wosequence"));
                taskObj.put("detailactcode", getTaskUpdate.get("detailactcode"));
                taskObj.put("description", getTaskUpdate.get("description"));
                taskObj.put("parent", getTaskUpdate.get("parent"));
                paramAttr.setParent(taskObj.get("parent").toString());
                paramAttr.setDetailactcode(taskObj.get("detailactcode").toString());
                
                updateAttr = daoAttr.updateAttributeMyStaff(paramAttr);
                validateTaskAttr1.validate(paramAttr.getParent(), paramAttr.getWonum(), paramAttr.getAssetAttrId(), paramAttr.getValue());
                validateTaskAttr2.validate(paramAttr.getParent(), paramAttr.getWonum(), paramAttr.getAssetAttrId(), paramAttr.getValue());
                validateRevised.validate(paramAttr.getParent(), paramAttr.getWonum(), paramAttr.getAssetAttrId(), paramAttr.getValue(), paramAttr.getDetailactcode());
                
                resp.put("wonum", paramAttr.getWonum());
                resp.put("siteid", paramAttr.getSiteid());
                resp.put("attribute name ", paramAttr.getAssetAttrId());
                resp.put("attribute value", paramAttr.getValue());
                data.add(resp);
            }
            
            if (updateAttr) {
                res.put("code", 200);
                res.put("message", "Success");
            } else {
                res.put("code", 422);
                res.put("message", "Failed");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateMyStaff.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(ValidateMyStaff.class.getName()).log(Level.SEVERE, null, ex);
        }
        return res;
    }

}
