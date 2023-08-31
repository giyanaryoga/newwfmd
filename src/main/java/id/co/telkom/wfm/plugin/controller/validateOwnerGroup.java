/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.NonCoreCompleteDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.dao.TkMappingOwnerGroupDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author User
 */
public class validateOwnerGroup {
    NonCoreCompleteDao productNonCore = new NonCoreCompleteDao();
    UpdateTaskStatusEbisDao product = new UpdateTaskStatusEbisDao();
    TkMappingOwnerGroupDao tkMapping = new TkMappingOwnerGroupDao();
    String dc_type;
    String dc_type_segment;
    
    private String BastFlag(JSONObject workorder) throws SQLException {
        String bastflag = product.getTaskAttrValue(workorder.get("wonum").toString(), "BASTFlag");
        return bastflag;
    }
    
    private String LocOrig(JSONObject workorder) throws SQLException {
        String locorig = product.getTaskAttrValue(workorder.get("wonum").toString(), "Location_Origin");
        return locorig;
    }
    
    private String LocDest(JSONObject workorder) throws SQLException {
        String locdest = product.getTaskAttrValue(workorder.get("wonum").toString(), "Location_Destination");
        return locdest;
    }
            
    private String getSupplier(JSONObject workorder) throws SQLException {
        String supplier = product.getTaskAttrValue(workorder.get("wonum").toString(), "PartnerNumber");
        if (supplier == null) supplier = "";
        return supplier;
    }
    
    private String getDCTypeSegment(JSONObject workorder) throws SQLException {
        dc_type = product.getTaskAttrValue(workorder.get("wonum").toString(), "DC_Type");
        dc_type_segment = dc_type;
        return dc_type_segment;
    }
}
