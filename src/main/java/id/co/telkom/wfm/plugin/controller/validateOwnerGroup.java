/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.NonCoreCompleteDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.dao.TkMappingOwnerGroupDao;
import id.co.telkom.wfm.plugin.dao.TaskActivityDao;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import java.sql.SQLException;
//import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
/**
 *
 * @author User
 */
public class ValidateOwnerGroup {
    NonCoreCompleteDao productNonCore = new NonCoreCompleteDao();
    GenerateWonumEbisDao generateDao = new GenerateWonumEbisDao();
    UpdateTaskStatusEbisDao product = new UpdateTaskStatusEbisDao();
    TkMappingOwnerGroupDao tkMapping = new TkMappingOwnerGroupDao();
    TaskActivityDao taskDao = new TaskActivityDao();
    private static String dc_type;
    private static String dc_type_segment;
    private static final String dcType1[] = {"Wholesale", "OLO_MS"};
    private static final String dcType2[] = {"DBS", "DES", "DGS"};
    private static final String ProductNonConn[] = {
        "REVIEW_ORDER", "Activate_Service", "Upload_Berita_Acara", "Modify_Service",
        "Shipment_Delivery", "Suspend_Service", "Resume_Service", "Deactivate_Service",
        "Approval_Project_Management"
    };
    private static final String TSAProduct1[] = { "TSA_CONSPART","TSA_SPMS" };
    private static final String TSAProduct2[] = { "TSA_CONSPART","TSA_OSS_ISP","TSA_OSS_OSP","TSA_SPMS","TSA_PM_ISP" };
    private static final String activityTSA1[] = {
        "WFMNonCore Request Order to Vendor", "WFMNonCore Update Pekerjaan dan Biaya Partner",
        "WFMNonCore Approve Completion Partner"
    };
    private static final String activityTSA2[] = {
        "WFMNonCore Receive Working Order Vendor", "WFMNonCore Insert Time Delivery Partner",
        "WFMNonCore Monitoring Period Partner","WFMNonCore Initial Respond Partner SPMS",
        "WFMNonCore Approve Pekerjaan Partner","WFMNonCore Part Delivery Partner SPMS",
        "WFMNonCore Insert Time Delivery Partner SPMS","WFMNonCore Approve Completion Partner",
        "WFMNonCore Approve Completion Partner SPMS"
    };
    
    private String BastFlag(JSONObject workorder) throws SQLException {
        String bastflag = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "BASTFlag");
        return bastflag;
    }
    
    private String LocOrig(JSONObject workorder) throws SQLException {
        String locorig = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "Location_Origin");
        return locorig;
    }
    
    private String LocDest(JSONObject workorder) throws SQLException {
        String locdest = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "Location_Destination");
        return locdest;
    }
            
    private String getSupplier(JSONObject workorder) throws SQLException {
        String supplier = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "PartnerNumber");
        if (supplier == null) supplier = "";
        return supplier;
    }
    
    private String getDivision(JSONObject workorder) throws SQLException {
        String division = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "AM_Division");
        if (division == null) division = "";
        return division;
    }
    
    private String getDCTypeSegment(JSONObject workorder) throws SQLException {
        dc_type = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "DC_Type");
        String dcType[] = dc_type.split(" ");
        dc_type = dcType[0];
        dc_type_segment = dc_type;
        return dc_type_segment;
    }
    
    private String getDCTypeSegmentNonCore(JSONObject workorder) throws SQLException {
        String dc_type_noncore = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "DC_TYPE");
        if (dc_type_noncore.equalsIgnoreCase(null) || dc_type_noncore.equalsIgnoreCase("")) {
            dc_type = dc_type_noncore;
            dc_type_segment = dc_type;
        } else {
            dc_type = "";
            dc_type_segment = dc_type;
        }
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
                workzone = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "STO END 1");
            } else if (Arrays.asList(act2).contains(activity)) {
                workzone = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "STO END 2");
            } else {
                workzone = workorder.get("workZone").toString();
            }
        }
        if (workorder.get("prodName").toString().equalsIgnoreCase("INF_IPPBX")) {
            if (Arrays.asList(act3).contains(activity)) {
                String workzoneOrig = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "Origination_STO");
                workzone = workzoneOrig;
            }
            if (Arrays.asList(act4).contains(activity)) {
                String workzoneDest = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "Destination_STO");
                workzone = workzoneDest;
            }
        }
        return workzone;
    }
    
    public String ownerGroupTask(JSONObject taskObj, JSONObject workorder) throws SQLException {
        String ownerGroupSet;
        String classstructureid = taskObj.get("classstructureid").toString();
        String prodName = workorder.get("prodName").toString();
        String workzone = workorder.get("workZone").toString();
        String activity = taskObj.get("activity").toString();
        String crmOrder = workorder.get("crmOrderType").toString();
        String dcType = (this.getDCTypeSegment(workorder) == null ? "" : this.getDCTypeSegment(workorder));
        String supplier = (this.getSupplier(workorder) == null ? "" : this.getSupplier(workorder));
        String division = (this.getDivision(workorder) == null ? "" : this.getDivision(workorder));
        LogUtil.info(getClass().getName(), "DC TYPE = " +dcType);
        LogUtil.info(getClass().getName(), "CLASSSTRUCTURE = " +classstructureid);
        LogUtil.info(getClass().getName(), "WORKZONE = " +workzone);
        LogUtil.info(getClass().getName(), "SUPPLIER = " +supplier);
        
        if (Arrays.asList(ProductNonConn).contains(activity)) {
            //Non - Connectivity mapping
            if (!"ASTINET".equalsIgnoreCase(prodName) && !"Approval_Project_Management".equalsIgnoreCase(activity) && "Wholesale".equalsIgnoreCase(dcType)) {
                String ownerGroup = tkMapping.getOwnerGroup1("NAS", prodName, dcType, supplier, classstructureid);
                ownerGroupSet = ownerGroup;
                if (ownerGroupSet  == null) {
                    String ownerGroup2 = tkMapping.getOwnerGroup2("NAS", prodName, dcType, classstructureid);
                    ownerGroupSet = ownerGroup2;
                    if (ownerGroupSet == null) {
                        String ownerGroup3 = tkMapping.getOwnerGroup3(workzone, dcType, classstructureid);
                        ownerGroupSet = ownerGroup3;
                    }
                }
            } else if ("Approval_Project_Management".equalsIgnoreCase(activity) && Arrays.asList(dcType1).contains(dcType)) {
                String ownerGroup = tkMapping.getOwnerGroup4(workzone, dcType, classstructureid);
                ownerGroupSet = ownerGroup;
            } else if ("Approval_Project_Management".equalsIgnoreCase(activity) && Arrays.asList(dcType2).contains(dcType)) {
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
                }
            } else if (!Arrays.asList(dcType2).contains(dcType) && "Approval_Project_Management".equalsIgnoreCase(activity)) {
                ownerGroupSet = tkMapping.getOwnerGroup8("NAS", dcType, classstructureid);
            } else {
                ownerGroupSet = tkMapping.getOwnerGroup9("NAS", dcType, prodName, classstructureid);
            }
            if (ownerGroupSet == null) {
                ownerGroupSet = tkMapping.getOwnerGroup10("NAS", dcType, prodName, classstructureid);
                if (ownerGroupSet == null) {
                    ownerGroupSet = tkMapping.getOwnerGroup11("NAS", prodName, classstructureid);
                    if (ownerGroupSet == null) {
                        ownerGroupSet = tkMapping.getOwnerGroup11(workzone, prodName, classstructureid);
                        if (ownerGroupSet == null) {
                            ownerGroupSet = tkMapping.getOwnerGroup12(workzone, classstructureid);
                            if (ownerGroupSet == null) {
                                ownerGroupSet = tkMapping.getOwnerGroup11("NAS", prodName, classstructureid);
                            }
                        }
                    }
                }
            }
        } else {
            //NonCoreProduct
            if (productNonCore.isNonCoreProduct(prodName) == 1) {
                // Non Core OwnerGroup Logic
                // Logic Neucentrix interconnect
                if (activity.equalsIgnoreCase("WFMNonCore Integration DWDM Origin")) {
                    ownerGroupSet = tkMapping.getOwnerGroupNonCore(LocOrig(workorder), prodName, classstructureid);
                } else {
                    ownerGroupSet = "";
                }

                if (activity.equalsIgnoreCase("WFMNonCore Integration DWDM Destination")) {
                    ownerGroupSet = tkMapping.getOwnerGroupNonCore(LocDest(workorder), prodName, classstructureid);
                } else {
                    ownerGroupSet = "";
                }

                // CYS CyberSecurity
                if (prodName.equalsIgnoreCase("DDoS Protection") && activity.equalsIgnoreCase("Approval_Project_Management")) {
                    ownerGroupSet = "CYS (CYBERSECURITY DELIVERY)";
                }

                // Product TSA
                if (Arrays.asList(TSAProduct1).contains(prodName) && Arrays.asList(activityTSA1).contains(activity)) {
                    String partnerTSA = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "MITRA_TSA");
                    ownerGroupSet = partnerTSA;
                }
                if (Arrays.asList(TSAProduct2).contains(prodName) && Arrays.asList(activityTSA2).contains(activity)) {
                    String vendorTSA = generateDao.getValueWorkorderAttribute(workorder.get("wonum").toString(), "NAMA_VENDOR");
                    ownerGroupSet = vendorTSA;
                }
            } else {
                //IP Transit
                if (prodName.equalsIgnoreCase("IP Transit")) {
                    if (workzone.equalsIgnoreCase("NAS")) {
                        int ipTransitNAS = tkMapping.isIPTransitNAS(activity);
                        if (ipTransitNAS == 1) {
                            ownerGroupSet = tkMapping.getOwnerGroup2("NAS", prodName, dcType, classstructureid);
                        }  
                    } else {
                        int ipTransitREG = tkMapping.isIPTransitREG(activity);
                        if (ipTransitREG == 1) {
                            ownerGroupSet = tkMapping.getOwnerGroup2(workzone, prodName, dcType, classstructureid);
                        }
                    }
                }
                
                //Connectivity mapping
                ownerGroupSet = tkMapping.getOwnerGroupConn1(workzone, classstructureid, dcType);
                if (ownerGroupSet.equalsIgnoreCase("")) {
                    LogUtil.info(getClass().getName(), "Validate OwnerGroup1 = "+ownerGroupSet);
                    if (!supplier.equalsIgnoreCase("")) {
                        String ownerGroupSupplier = tkMapping.getOwnerGroupConn3(workzone, supplier, classstructureid);
                        ownerGroupSet = ownerGroupSupplier;
                    } else {
                        ownerGroupSet = tkMapping.getOwnerGroupConn2(workzone, classstructureid);
                    }
                }
                LogUtil.info(getClass().getName(), "Validate OwnerGroup2 = "+ownerGroupSet);
            }
        }
        
        if (activity.equalsIgnoreCase("Service Testing") && crmOrder.equalsIgnoreCase("Resume")) {
            ownerGroupSet = "DSS_ODM";
        }

        String bastFlag = BastFlag(workorder);
        if (bastFlag.equalsIgnoreCase("N") && activity.equalsIgnoreCase("Approval_Project_Management")) {
            ownerGroupSet = "SDA ASC";
        }
        
        LogUtil.info(getClass().getName(), "Validate OwnerGroup return = "+ownerGroupSet);
        
        return ownerGroupSet;
    }
}
