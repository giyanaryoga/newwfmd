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
    
    private void validateRole (String wonum) {
        try {
            JSONArray taskAttr = taskAttrDao.getTaskAttrWonum(wonum);
            for (Object obj : taskAttr) {
                JSONObject taskAttrObj = (JSONObject)obj;
                String attr_name = taskAttrObj.get("assetattrid").toString();
                String value = taskAttrDao.getTaskAttrValue(wonum, "ROLE");
                String valueNteType = taskAttrDao.getTaskAttrValue(wonum, "NTE_TYPE");
//                String[] splitAttrName = attr_name.split("_");
//                String roleSplit = splitAttrName[0]; // NTE atau STP
                if (value.equalsIgnoreCase("STP")) {
                    taskAttrDao.updateMandatory(wonum, "NTE_TYPE", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 0);
                } else {
                    //IF ROLE = NTE
                    if (valueNteType.equalsIgnoreCase("ONT")) {
                        taskAttrDao.updateMandatory(wonum, "NTE_NAME", 1);
                        taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 1);
                        taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 1);
                        taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 1);
                        taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 1);
                    } 
                    if (Arrays.asList(nteType1).contains(valueNteType)) {
                        taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                        taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                        taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                        taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 0);
                        taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 0);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateValueNextTask (String parent, String attrName) {
        try {
            JSONArray taskAttrParent = taskAttrDao.getTaskAttributeParent(parent);
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void validate (String parent, String wonum, String attrName, String attrValue, String task) {
        if (attrName.equalsIgnoreCase("ROLE")) {
            validateRole(wonum);
        }
    }
}
