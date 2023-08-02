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

/**
 *
 * @author User
 */
public class RevisedTask extends DefaultApplicationPlugin {
    String pluginName = "Telkom New WFM - Revised Task - Default Plugin";
    
    @Override
    public Object execute(Map map) {
        RevisedTaskDao dao = new RevisedTaskDao();
        
        String wonum = getPropertyString("wonum");
        String attrName = getPropertyString("assetattrid");
        String attrValue = getPropertyString("value");
        String[] parent = wonum.split(" - ");
        
        LogUtil.info(this.getClassName(), "WONUM: "+ wonum);
        LogUtil.info(this.getClassName(), "ATTRIBUTE NAME: "+ attrName);
        LogUtil.info(this.getClassName(), "ATTRIBUTE VALUE: "+ attrValue);
        String wonumParent = parent[0];
        String childWonum = parent[1];
        String status = null;
        
        try {
            if (attrName.equalsIgnoreCase("APPROVAL_SURVEY")) {
                if (attrValue.equalsIgnoreCase("BACK_TO_SURVEY")) {
                    dao.reviseTask(wonumParent);
//                    int taskId = dao.getTaskId(wonumParent);
//                    if (childWonum == "1") {
//                        status = "LABASSIGN";
//                    } else {
//                        status = "APPR";
//                    }
                    dao.generateActivityTask(wonumParent);
                }
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
