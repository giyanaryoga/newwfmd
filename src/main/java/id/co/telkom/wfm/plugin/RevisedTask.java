/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import java.util.*;
import javafx.scene.control.TextField;
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
        return "";
    }

    @Override
    public String getName() {
        return this.pluginName;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getVersion() {
        return "7.00";
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        return this.pluginName;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getLabel() {
        return this.pluginName;
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getClassName() {
        return getClass().getName();
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getPropertyOptions() {
        return "";
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
//    private void PropertiesObj(String wonum, String assetAttrId, String value) {
//        Properties pps = new Properties();
//        pps.setProperty("wonum", wonum);
//        pps.setProperty("assetAttrId", assetAttrId);
//        pps.setProperty("value", value);
//        
//         TextField textField = new TextField();
//         textField.
//    }
    
}
