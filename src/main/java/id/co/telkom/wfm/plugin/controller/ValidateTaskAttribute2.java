/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.TaskAttributeDao2;
import id.co.telkom.wfm.plugin.dao.TaskAttributeUpdateDao;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class ValidateTaskAttribute2 {
    TaskAttributeDao2 taskAttrDao = new TaskAttributeDao2();
    TaskAttributeUpdateDao attrDao = new TaskAttributeUpdateDao();
    
    public void validate(String parent, String wonum, String attrName, String attrValue) {
        try {
            String task = attrDao.getActivity(wonum);
            switch (attrName) {
                case "ARNET PASSTHROUGH PORT WDM 1":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 1", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 2":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 2", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 3":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 3", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 4":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 4", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 5":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 5", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 6":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 6", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 7":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 7", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 8":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 8", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 9":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 9", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 10":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 10", attrValue);
                    }
                    break;
                default:
                    LogUtil.info(getClass().getName(), "Validate Task Attribute is not found and not execute!");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
