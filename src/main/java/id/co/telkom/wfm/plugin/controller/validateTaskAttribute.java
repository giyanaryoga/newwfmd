/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.TaskAttribute;
import id.co.telkom.wfm.plugin.dao.TaskAttributeUpdateDao;
import id.co.telkom.wfm.plugin.dao.TaskActivityDao;
import java.sql.SQLException;
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
    
    public void validate (String parent, String wonum, String attrName, String attrValue, String task) {
        try {
            taskDao.getTaskAttrValue(wonum, attrName);
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
