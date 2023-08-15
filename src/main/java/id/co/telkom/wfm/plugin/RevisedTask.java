/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.RevisedTaskDao;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.joget.plugin.base.DefaultApplicationPlugin;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author User
 */
public class RevisedTask extends DefaultApplicationPlugin {
    String pluginName = "Telkom New WFM - Revised Task - Default Plugin";
    
    @Override
    public Object execute(Map map) {
        RevisedTaskDao dao = new RevisedTaskDao();
        
        String parent = getPropertyString("parent");
        String task = getPropertyString("task");
        Integer taskId = Integer.parseInt(getPropertyString("taskid"));
        String wonum = getPropertyString("wonum");
        String attrName = getPropertyString("assetattrid");
        String attrValue = getPropertyString("value");
//        String[] parent = wonum.split(" - ");
        
        LogUtil.info(this.getClassName(), "TASK ID: "+ taskId);
        LogUtil.info(this.getClassName(), "TASK: "+ task);
        LogUtil.info(this.getClassName(), "WONUM: "+ wonum);
        LogUtil.info(this.getClassName(), "ATTRIBUTE NAME: "+ attrName);
        LogUtil.info(this.getClassName(), "ATTRIBUTE VALUE: "+ attrValue);
//        String wonumParent = parent[0];
        int nextTaskId = taskId + 10;
        
        try {
            JSONArray taskArray = dao.getTask(parent);
            LogUtil.info(this.getClassName(), "Task" + taskArray);
            
            switch(attrName){
                case "APPROVAL_SURVEY":
                    if (attrValue.equalsIgnoreCase("BACK_TO_SURVEY")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(this.getClassName(), "Approval Survey is not BACK_TO_SURVEY");
                    }
                break;
                case "APPROVAL":
                    if (task.equalsIgnoreCase("REVIEW_ORDER")) {
                        if (attrValue.equalsIgnoreCase("REJECTED")) {
                            //Di Array dlu task parentnya
                            //lalu di foreach, dan update REVISED task selanjutnya
                        } else {
                            LogUtil.info(this.getClassName(), "Approval is not REJECTED");
                        }
                    } else {
                        if (attrValue.equalsIgnoreCase("REJECTED")) {
                            dao.reviseTask(parent);
                            dao.generateActivityTask(parent);
                        } else {
                            LogUtil.info(this.getClassName(), "Approval is not REJECTED");
                            LogUtil.info(this.getClassName(), "Task" + taskArray);
                        }
                    }
                break;
                case "NODE_ID":
                    if (!attrValue.equalsIgnoreCase("None")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(this.getClassName(), "Approval Survey is None");
                    }
                break;
                case "ACCESS REQUIRED 1":
                    if (attrValue.equalsIgnoreCase("NO")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(this.getClassName(), "Approval Survey is not NO");
                    }
                break;
                case "ACCESS REQUIRED 2":
                    if (attrValue.equalsIgnoreCase("NO")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(this.getClassName(), "Approval Survey is not NO");
                    }
                break;
                case "APPROVED":
                    if (attrValue.equalsIgnoreCase("REJECTED")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(this.getClassName(), "Approval Survey is not REJECTED");
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
                        LogUtil.info(this.getClassName(), "Approval Survey is not Modify Number");
                    }
                break;
                case "ACCEPT":
                    if (attrValue.equalsIgnoreCase("NO")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(this.getClassName(), "Approval Survey is not NO");
                    }
                break;
                case "ACCESS_REQUIRED":
                    if (attrValue.equalsIgnoreCase("NO")) {
                        dao.reviseTask(parent);
                        dao.generateActivityTask(parent);
                    } else {
                        LogUtil.info(this.getClassName(), "Approval Survey is not NO");
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
                        LogUtil.info(this.getClassName(), "Approval Survey is not NO");
                    }
                break;
                default:
                    LogUtil.info(this.getClassName(), "Attribute name dan value tidak memenuhi Revised Task");
                break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(RevisedTask.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
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
        return AppUtil.readPluginResource(getClassName(), "properties/taskAttribute.json", null, true, null);
    }
}
