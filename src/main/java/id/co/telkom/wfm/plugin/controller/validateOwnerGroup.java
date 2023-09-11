/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.NonCoreCompleteDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.dao.TkMappingOwnerGroupDao;
import id.co.telkom.wfm.plugin.dao.TaskActivityDao;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
    TaskActivityDao taskDao = new TaskActivityDao();
    private static String dc_type;
    private static String dc_type_segment;
    private static final String dcType1[] = {"Wholesale", "OLO_MS"};
    private static final String dcType2[] = {"DBS", "DES", "DGS"};
    
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
    
    private String getDivision(JSONObject workorder) throws SQLException {
        String division = product.getTaskAttrValue(workorder.get("wonum").toString(), "AM_Division");
        if (division == null) division = "";
        return division;
    }
    
    private String getDCTypeSegment(JSONObject workorder) throws SQLException {
        dc_type = product.getTaskAttrValue(workorder.get("wonum").toString(), "DC_Type");
        dc_type_segment = dc_type;
        return dc_type_segment;
    }
    
    private String getWorkzoneNonCore(JSONObject workorder, String activity) throws SQLException {
        String act1[] = {
            "WFMNonCore BER Test WDM End 1","WFMNonCore Integrasi Access Port WDM End 1",
            "WFMNonCore Allocate Access Cust End 1","WFMNonCore Integrasi Access Cust End 1"
        };
        String act2[] = {
            "WFMNonCore BER Test WDM End 2","WFMNonCore Integrasi Access Port WDM End 2",
            "WFMNonCore Allocate Access Cust End 2","WFMNonCore Integrasi Access Cust End 2"
        };
        String act3[] = {
            "WFMNonCore Activate Metro IPPBX","WFMNonCore Modify Metro IPPBX",
            "WFMNonCore Deactivate Metro IPPBX","WFMNonCore Review Order Suspend","WFMNonCore Resume Service IPPBX"
        };
        String act4[] = {
            "WFMNonCore Allocate Metro Access","WFMNonCore Activate Access",
            "WFMNonCore Pickup NTE","WFMNonCore Install NTE"
        };
        String workzone = "";
        if (workorder.get("prodName").toString().equalsIgnoreCase("SL_WDM")) {
            if (Arrays.asList(act1).contains(activity)) {
                workzone = product.getTaskAttrValue(workorder.get("wonum").toString(), "STO END 1");
            } else if (Arrays.asList(act2).contains(activity)) {
                workzone = product.getTaskAttrValue(workorder.get("wonum").toString(), "STO END 2");
            } else {
                workzone = workorder.get("workZone").toString();
            }
        }
        if (workorder.get("prodName").toString().equalsIgnoreCase("INF_IPPBX")) {
            if (Arrays.asList(act3).contains(activity)) {
                String workzoneOrig = product.getTaskAttrValue(workorder.get("wonum").toString(), "Origination_STO");
                workzone = workzoneOrig;
            }
            if (Arrays.asList(act4).contains(activity)) {
                String workzoneDest = product.getTaskAttrValue(workorder.get("wonum").toString(), "Destination_STO");
                workzone = workzoneDest;
            }
        }
        return workzone;
    }
    
    public String ownerGroupParent(JSONObject workorder) throws SQLException {
        String workzone = workorder.get("workZone").toString();
        String siteId = workorder.get("siteId").toString();
        String ownerGroup = "";
        if (dc_type == null) {
            ownerGroup = tkMapping.getOwnerGroupParentCCAN(workzone, siteId);
        } else if (Arrays.asList(dcType1).contains(dc_type)) {
            ownerGroup = tkMapping.getOwnerGroupParentSegment(workzone, siteId, dc_type);
        } else if (Arrays.asList(dcType2).contains(dc_type)) {
            ownerGroup = tkMapping.getOwnerGroupParentSegment(workzone, siteId, dc_type);
        } else {
            ownerGroup = "";
        }
        return ownerGroup;
    }
    
    public String ownerGroupTask(JSONObject taskObj, JSONObject workorder) throws SQLException {
        boolean isTaskNonConn = tkMapping.getTaskNonConn(taskObj.get("activity").toString());
        String dcType = this.getDCTypeSegment(workorder);
        String supplier = this.getSupplier(workorder);
        String division = this.getDivision(workorder);
        String classstructureid = taskObj.get("classstructureid").toString();
        String ownerGroupSet = "";
        if (isTaskNonConn) {
            //Non - Connectivity mapping
            if (!"ASTINET".equalsIgnoreCase(workorder.get("prodName").toString()) && !"Approval_Project_Management".equalsIgnoreCase(taskObj.get("activity").toString()) && "Wholesale".equalsIgnoreCase(dcType)) {
                String ownerGroup = tkMapping.getOwnerGroup1("NAS", workorder.get("prodName").toString(), dcType, supplier, classstructureid);
                ownerGroupSet = ownerGroup;
                if (ownerGroupSet  == null) {
                    String ownerGroup2 = tkMapping.getOwnerGroup2("NAS", workorder.get("prodName").toString(), dcType, classstructureid);
                    ownerGroupSet = ownerGroup2;
                    if (ownerGroupSet == null) {
                        String ownerGroup3 = tkMapping.getOwnerGroup3(workorder.get("workZone").toString(), dcType, classstructureid);
                        ownerGroupSet = ownerGroup3;
                    }
                }
            } else if ("Approval_Project_Management".equalsIgnoreCase(taskObj.get("activity").toString()) && Arrays.asList(dcType1).contains(dcType)) {
                String ownerGroup = tkMapping.getOwnerGroup4(workorder.get("workZone").toString(), dcType, classstructureid);
                ownerGroupSet = ownerGroup;
            } else if ("Approval_Project_Management".equalsIgnoreCase(taskObj.get("activity").toString()) && Arrays.asList(dcType2).contains(dcType)) {
                String ownerGroup2 = "";
                if (division == null || division.equalsIgnoreCase("")) {
                    ownerGroup2 = tkMapping.getOwnerGroup5("NAS", dcType, classstructureid);
                } else {
                    ownerGroup2 = tkMapping.getOwnerGroup6(division, dcType, classstructureid);
                }
                ownerGroupSet = ownerGroup2;
                if (ownerGroupSet == null) {
                    ownerGroup2 = tkMapping.getOwnerGroup7("NAS", dcType, classstructureid);
                    ownerGroupSet = ownerGroup2;
                } else if (!Arrays.asList(dcType2).contains(dcType) && "Approval_Project_Management".equalsIgnoreCase(taskObj.get("activity").toString())) {
                    ownerGroup2 = tkMapping.getOwnerGroup8("NAS", dcType, classstructureid);
                    ownerGroupSet = ownerGroup2;
                } else {
                    ownerGroup2 = tkMapping.getOwnerGroup9("NAS", dcType, workorder.get("prodName").toString(), classstructureid);
                    ownerGroupSet = ownerGroup2;
                }
            }
//            if (ownerGroupSet == null) {
//                automation scriptnya gak paham maksudnya, di skip dlu deh
//            }
        } else {
            //Connectivity mapping
            
        }
        
        return ownerGroupSet;
    }
}
