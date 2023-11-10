/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.controller.ValidateRevised;
import id.co.telkom.wfm.plugin.controller.ValidateTaskAttribute;
import id.co.telkom.wfm.plugin.controller.ValidateTaskAttribute2;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.DefaultApplicationPlugin;

/**
 *
 * @author User
 */
public class TaskAttribute extends DefaultApplicationPlugin {

    String pluginName = "Telkom New WFM - Task Attribute Save Button - Default Plugin";

    @Override
    public Object execute(Map map) {
        ValidateRevised logicValidate = new ValidateRevised();
        ValidateTaskAttribute logicTaskAttr = new ValidateTaskAttribute();
        ValidateTaskAttribute2 logicTaskAttr2 = new ValidateTaskAttribute2();
                
        String parent = getPropertyString("parent");
        String task = getPropertyString("task");
        String taskId = getPropertyString("taskid");
        String wonum = getPropertyString("wonum");
        String attrName = getPropertyString("assetattrid");
        String attrValue = getPropertyString("value");
        String classstrcutureid = getPropertyString("classstrcutureid");
        String siteid = getPropertyString("siteid");
        String orgid = getPropertyString("orgid");
        LogUtil.info(this.getClassName(), "PARENT: " + parent);
        LogUtil.info(this.getClassName(), "WONUM: " + wonum);
        LogUtil.info(this.getClassName(), "TASK ID: " + taskId);
        LogUtil.info(this.getClassName(), "TASK: " + task);
        LogUtil.info(this.getClassName(), "ATTRIBUTE NAME: " + attrName);
        LogUtil.info(this.getClassName(), "ATTRIBUTE VALUE: " + attrValue);
        LogUtil.info(this.getClassName(), "CLASSSTRUCTUREID VALUE: " + classstrcutureid);
        LogUtil.info(this.getClassName(), "SITEID VALUE: " + siteid);
        LogUtil.info(this.getClassName(), "ORGID VALUE: " + orgid);
        
        try {
            logicTaskAttr.validate(parent, wonum, attrName, attrValue);
        } catch (SQLException ex) {
            Logger.getLogger(TaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
        logicTaskAttr2.validate(parent, wonum, attrName, attrValue);
        logicValidate.validate(parent, wonum, attrName, attrValue, task);
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
