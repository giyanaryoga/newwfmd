/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.TaskAttribute;
import id.co.telkom.wfm.plugin.dao.TaskAttributeUpdateDao;
import id.co.telkom.wfm.plugin.dao.TaskActivityDao;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author Giyanaryoga Puguh
 */
public class validateTaskAttribute {
    TaskAttributeUpdateDao taskAttrDao = new TaskAttributeUpdateDao();
    TaskActivityDao taskDao = new TaskActivityDao();
    
    private static final String nteType1[] = {"L2Switch", "DirectME", "DirectPE"};
    private static final String taskNTEAvailable[] = {
        "Survey-Ondesk", "Site-Survey", "Survey-Ondesk Wifi", "Site-Survey Wifi"
    };
    
    private void validateRole(String wonum) {
        try {
            String value = taskAttrDao.getTaskAttrValue(wonum, "ROLE");
            String valueNteType = taskAttrDao.getTaskAttrValue(wonum, "NTE_TYPE");
            
            if (value.equalsIgnoreCase("STP")) {
                taskAttrDao.updateMandatory(wonum, "NTE_TYPE", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 0);
                
                taskAttrDao.updateTaskAttrViewLike(wonum, "NTE", 0);
            } else {
                //IF ROLE = NTE
                LogUtil.info(getClass().getName(), "NTE_TYPE =" +valueNteType);
                taskAttrDao.updateTaskAttrViewLike(wonum, "STP", 0);
                if (valueNteType == null) {
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 0);
                } else if (valueNteType.equalsIgnoreCase("ONT")) {
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 1);
                } else if (Arrays.asList(nteType1).contains(valueNteType)) {
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 0);
                } else {
                    LogUtil.info(getClass().getName(), "NTE_TYPE not yet inserted");
                }
                taskAttrDao.updateTaskAttrView(wonum, "STP_NETWORKLOCATION", 1);
                taskAttrDao.updateTaskAttrView(wonum, "STP_NETWORKLOCATION_LOV", 1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void nteAvailable(String wonum) {
        try {
            String value = taskAttrDao.getTaskAttrValue(wonum, "NTE_AVAILABLE");
            String detailactcode = taskAttrDao.getActivity(wonum);
            if (Arrays.asList(nteType1).contains(detailactcode)) {
                if (value.equalsIgnoreCase("YES")) {
                    taskAttrDao.updateMandatory(wonum, "NTE_TYPE", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORTNAME", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 1);
                    
                    taskAttrDao.updateTaskValue(wonum, "NTE_TYPE", "None");
                    taskAttrDao.updateTaskValue(wonum, "NTE_NAME", "None");
                    taskAttrDao.updateTaskValue(wonum, "NTE_SERIALNUMBER", "None");
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORTNAME", "None");
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORT", "None");
                } else {
                    taskAttrDao.updateMandatory(wonum, "NTE_TYPE", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORTNAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                    
                    taskAttrDao.updateTaskValue(wonum, "NTE_TYPE", "");
                    taskAttrDao.updateTaskValue(wonum, "NTE_NAME", "");
                    taskAttrDao.updateTaskValue(wonum, "NTE_SERIALNUMBER", "");
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORTNAME", "");
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORT", "");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateValueNextTask(String parent, String attrName, String attrValue) {
        try {
            JSONArray taskAttrParent = taskAttrDao.getTaskAttributeParent(parent);
            for (Object obj : taskAttrParent) {
                JSONObject taskAttrObj = (JSONObject)obj;
//                LogUtil.info(getClass().getName(), "Task attribute = " +taskAttrObj);
                String attr_name = taskAttrObj.get("task_attr_name").toString();
                String attr_value = (taskAttrObj.get("task_attr_value") == null ? "" : taskAttrObj.get("task_attr_value").toString());
                if (attr_name.equalsIgnoreCase(attrName)) {
                    if (attr_value == null || attr_value == "" || attr_value == "None") {
                        taskAttrDao.updateTaskValueParent(parent, attr_name, attrValue);
                        LogUtil.info(getClass().getName(), "Task attribute value insert from assetattrid same");
                    }
                    LogUtil.info(getClass().getName(), "Task attribute value is not null");
                }
//                LogUtil.info(getClass().getName(), "Task attribute name is not same");
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateSTO(String wonum, String attrValue) {
        try {
            JSONObject workzone = taskAttrDao.getWorkzoneRegional(attrValue);
            taskAttrDao.updateTaskValue(wonum, "REGION", workzone.get("region").toString());
            taskAttrDao.updateTaskValue(wonum, "WITEL", workzone.get("subregion").toString());
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void validate(String parent, String wonum, String attrName, String attrValue) {
        if (attrName.equalsIgnoreCase("ROLE") || attrName.equalsIgnoreCase("NTE_TYPE")) {
            validateRole(wonum);
        } else if (!attrName.equalsIgnoreCase("APPROVAL")) {
            validateValueNextTask(parent, attrName, attrValue);
        }
        nteAvailable(wonum);
        if (attrName.equalsIgnoreCase("STO") && !attrValue.equalsIgnoreCase("NAS")) {
            validateSTO(wonum, attrValue);
        }
    }
}
