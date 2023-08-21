/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import id.co.telkom.wfm.plugin.UpdateTaskStatusEbis;
import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import id.co.telkom.wfm.plugin.dao.TaskHistoryDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletResponse;
import org.joget.commons.util.LogUtil;
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
    
    public void validate(UpdateStatusParam param) {
        int nextTaskId = Integer.parseInt(param.getTaskId()) + 10;
        boolean updateTask = false;
        boolean nextAssign = false;
        String isWoDocValue = "";
        HttpServletResponse hsr1 = null;
        JSONObject res = new JSONObject();
        
        try {
            switch(param.getStatus()) {
                case "STARTWA":
                    boolean isAssigned = daoUpdate.checkAssignment(param.getWonum());
                    if (!isAssigned) {
                        String checkActPlace = daoUpdate.checkActPlace(param.getWonum());
                        if (checkActPlace.equals("OUTSIDE")) {
                            hsr1.setStatus(421);
                            hsr1.sendError(400, "Task is not Assign to Labor yet");
                        } else {
                            updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                            if (updateTask) {
                                hsr1.setStatus(200);
                                daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy());
                            }
                            res.put("code", "200");
                            res.put("status", param.getStatus());
                            res.put("wonum", param.getWonum());
                            res.put("message", "Successfully update status");
                            res.writeJSONString(hsr1.getWriter());
                        }
                    } else {
                        updateTask = daoUpdate.updateTask(param.getWonum(), param.getStatus(), param.getModifiedBy());
                        if (updateTask) {
                            hsr1.setStatus(200);
                            daoHistory.insertTaskStatus(param.getWonum(), param.getMemo(), param.getModifiedBy());
                        }
                        res.put("code", "200");
                        res.put("status", param.getStatus());
                        res.put("wonum", param.getWonum());
                        res.put("message", "Successfully update status");
                        res.writeJSONString(hsr1.getWriter());
                    }
                    break;
                default:
                    hsr1.setStatus(420);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(UpdateTaskStatusEbis.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(validateTaskStatus.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
