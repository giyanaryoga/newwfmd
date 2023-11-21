/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.UpdateAssignmentEbisDao;
import id.co.telkom.wfm.plugin.model.ListAssignment;
import id.co.telkom.wfm.plugin.util.ResponseAPI;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author User
 */
public class UpdateAssignmentEbis extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Update Assignment Labor Ebis - Web Service";

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
        LogUtil.info(getClass().getName(), "Start Process: Update Labor");
        //@Authorization
        if ("POST".equals(hsr.getMethod())) {
            try {
                //@Parsing message
                //HttpServletRequest get JSON Post data
                StringBuilder jb = new StringBuilder();
                String line;
                try {//read the response JSON to string buffer
                    BufferedReader reader = hsr.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }
                } catch (IOException e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                //Parse JSON String to JSON Object
                String bodyParam = jb.toString(); //String
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject) parser.parse(bodyParam);//JSON Object
                
                //Store param
                String wonum = (data_obj.get("wonum") == null ? "" : data_obj.get("wonum").toString());
                String status = (data_obj.get("status") == null ? "" : data_obj.get("status").toString());
                String taskId = (data_obj.get("taskId") == null ? "" : data_obj.get("taskId").toString());
                String craft = (data_obj.get("craft") == null ? "" : data_obj.get("craft").toString());
                String crewType = (data_obj.get("amcrewtype") == null ? "" : data_obj.get("amcrewtype").toString());
                String amcrew = (data_obj.get("amcrew") == null ? "" : data_obj.get("amcrew").toString());
                String laborhrs = (data_obj.get("laborhrs") == null ? "" : data_obj.get("laborhrs").toString());
                String chiefcode = (data_obj.get("chiefcode") == null ? "" : data_obj.get("chiefcode").toString());
                String partnerCode = (data_obj.get("partnerCode") == null ? "" : data_obj.get("partnerCode").toString());
                String user = (data_obj.get("modifiedBy") == null ? "" : data_obj.get("modifiedBy").toString());
                String name = (data_obj.get("modifiedByName") == null ? "" : data_obj.get("modifiedByName").toString());
                
                ListAssignment listAssignment = new ListAssignment();
                listAssignment.setWonum(wonum);
                listAssignment.setStatus(status);
                listAssignment.setTaskid(taskId);
                listAssignment.setCraft(craft);
                listAssignment.setCrewType(crewType);
                listAssignment.setAmcrew(amcrew);
                listAssignment.setLaborhrs(laborhrs);
                listAssignment.setChiefcode(chiefcode);
                listAssignment.setPartnerCode(partnerCode);
                listAssignment.setUser(user);
                listAssignment.setName(name);
                
                JSONObject res = new JSONObject();
                UpdateAssignmentEbisDao dao = new UpdateAssignmentEbisDao();
                ResponseAPI responseTemplete = new ResponseAPI();
                String message = "";
                
                try {
                    boolean getStatus = dao.getStatusTask(listAssignment.getWonum());
                    if (getStatus) {
                        if (!listAssignment.getChiefcode().equalsIgnoreCase("") || !listAssignment.getChiefcode().isEmpty()) {
                            boolean validChief = dao.validateLabor(listAssignment.getChiefcode());
                            
                            if (validChief) {
                                dao.getLaborName(listAssignment.getChiefcode());
                                String laborname = dao.getLaborName(listAssignment.getChiefcode());
                                listAssignment.setChiefName(laborname);
                                LogUtil.info(getClass().getName(), "Laborcode: " + listAssignment.getChiefcode() + ", Laborname: " + laborname);
                                /**
                                 * laborcode ada di table labor 
                                 */
                                dao.updateLabor(listAssignment);
                                /**
                                 * update c_laborcode dan c_laborname di table app_fd_workorder
                                 */
                                dao.updateLaborWorkOrder(listAssignment);
                                
                                message = "Success update Labor";
                                res = responseTemplete.getUpdateStatusSuccessResp(listAssignment.getWonum(), listAssignment.getStatus(), message);
                                res.writeJSONString(hsr1.getWriter());
                            } else {
                                hsr1.sendError(422, "Laborcode and laborname is not found!");
                            }
                        } else if (!listAssignment.getPartnerCode().isEmpty() || !listAssignment.getPartnerCode().equalsIgnoreCase("")) {
                            boolean validPartner = dao.validateLabor(listAssignment.getPartnerCode());
                            
                            if (validPartner) {
                                dao.getLaborName(listAssignment.getPartnerCode());
                                String laborname = dao.getLaborName(listAssignment.getPartnerCode());
                                listAssignment.setPartnerName(laborname);
                                LogUtil.info(getClass().getName(), "Laborcode: " + listAssignment.getPartnerCode() + ", Laborname: " + laborname);
                                /**
                                 * laborcode ada di table labor 
                                 */
                                dao.updatePartner(listAssignment);
                                /**
                                 * update c_laborcode dan c_laborname di table app_fd_workorder
                                 */
                                dao.updateLaborWorkOrder(listAssignment);
                                
                                message = "Success update Labor";
                                res = responseTemplete.getUpdateStatusSuccessResp(listAssignment.getWonum(), listAssignment.getStatus(), message);
                                res.writeJSONString(hsr1.getWriter());
                            } else {
                                hsr1.sendError(422, "Laborcode and laborname is not found!");
                            }
                        } else if (!listAssignment.getAmcrew().isEmpty() || !listAssignment.getAmcrew().equalsIgnoreCase("")) {
                            boolean validCrew = dao.validateCrew(listAssignment.getAmcrew());
                            
                            if (validCrew) {
                                LogUtil.info(getClass().getName(), "Amcrew: " + listAssignment.getAmcrew());
                                /**
                                 * laborcode ada di table labor 
                                 */
                                dao.updateCrew(listAssignment);
                                /**
                                 * update c_laborcode dan c_laborname di table app_fd_workorder
                                 */
                                dao.updateCrewWorkOrder(listAssignment);
                                
                                message = "Success update Amcrew";
                                res = responseTemplete.getUpdateStatusSuccessResp(listAssignment.getWonum(), listAssignment.getStatus(), message);
                                res.writeJSONString(hsr1.getWriter());
                            } else {
                                hsr1.sendError(422, "AmCrew is not found!");
                            }
                        } else if (listAssignment.getChiefcode().equalsIgnoreCase("") || listAssignment.getPartnerCode().equalsIgnoreCase("")) {
                            listAssignment.setChiefName("");
                            listAssignment.setPartnerName("");
                            dao.updateLaborWaitAssign(listAssignment);
                            dao.deleteLaborWorkOrder(listAssignment);
                            
                            message = "Success delete assignment";
                            res = responseTemplete.getUpdateStatusSuccessResp(listAssignment.getWonum(), listAssignment.getStatus(), message);
                            res.writeJSONString(hsr1.getWriter());
                        } else {
                            hsr1.sendError(422, "Laborcode and amcrew is null");
                        }
                        
                        // Insert into AssignmentLog
                        dao.insertAssignmentLog(listAssignment);
                    } else {
                        hsr1.sendError(422, "Status parent task is COMPWA");
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(UpdateAssignmentEbis.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (ParseException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else if (!"POST".equals(hsr.getMethod())) {
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        }
    }
}