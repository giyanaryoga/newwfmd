/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.TaskAttributeDao2;
import id.co.telkom.wfm.plugin.dao.TaskAttributeUpdateDao;
import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.util.ConnUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class ValidateTaskAttribute2 {
    TaskAttributeUpdateDao taskAttrDao1 = new TaskAttributeUpdateDao();
    TaskAttributeDao2 taskAttrDao2 = new TaskAttributeDao2();
    ConnUtil connUtil = new ConnUtil();
    private static final String taskTSEL[] = {
        "WFMNonCore Review Order OSS ISP TSEL Regional", "WFMNonCore Review Order OSS OSP TSEL Regional"
    };
    private static final String taskBlokNomor[] = {
        "Populate Number", "Populate SBC", "Allocate Softswitch","Review Order SIP Trunk",
        "Validate Softswitch","Deactivate Softswitch", "Populate Number Modify", "Activate Softswitch"
    };
    
    public void validate(String parent, String wonum, String attrName, String attrValue) {
        try {
            String task = taskAttrDao1.getActivity(wonum);
            switch (attrName) {
                case "ARNET PASSTHROUGH PORT WDM 1":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 1", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 2":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 2", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 3":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 3", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 4":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 4", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 5":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 5", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 6":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 6", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 7":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 7", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 8":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 8", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 9":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 9", attrValue);
                    }
                    break;
                case "ARNET PASSTHROUGH PORT WDM 10":
                    if (task.equalsIgnoreCase("WFMNonCore Allocate New WDM")) {
                        taskAttrDao2.updateOwnerGroup(wonum, "WFMNonCore PassThrough Port WDM 10", attrValue);
                    }
                    break;
                case "PACKAGE":
                    validatePackage(wonum, task, attrValue);
                    break;
                case "SID_ASTINET_LOV":
                    taskAttrDao1.updateTaskValue(wonum, "SID ASTINET", attrValue);
                    break;
                case "SID_OTHER 1_LOV":
                    taskAttrDao1.updateTaskValue(wonum, "SID OTHER 1", attrValue);
                    break;
                case "SID_OTHER 2_LOV":
                    taskAttrDao1.updateTaskValue(wonum, "SID OTHER 2", attrValue);
                    break;
                case "SID_VPN IP_LOV":
                    taskAttrDao1.updateTaskValue(wonum, "SID VPN IP", attrValue);
                    break;
                case "NAMA_VENDOR":
                    setWoAttrVendor(parent, task, attrValue);
                    break;
                case "MITRA_TSA":
                    setWoAttrMitra(parent, task, attrValue);
                    break;
                case "AN_MANUFACTUR":
                    validateAnManufacture(parent, wonum, task, attrValue);
                    break;
                case "ENCRYPTION_METHOD":
                    validateEncryptionMethod(parent, wonum, attrValue);
                    break;
                case "APPROVAL":
                    validateApprovalWMS(parent, wonum, task);
                    break;
                case "JUMLAH_BLOK_NOMOR":
                    validateBlokNomor(wonum, task, attrValue);
                    break;
                case "CPE_MGMT_PE_NAME":
                    validatePE_Name(wonum, task, attrValue);
                    break;
                case "CPE_MGMT_PE_PORTNAME_LOV":
                    validatePE_NameLOV(wonum, attrValue);
                    break;
                case "SBC_NAME":
                    validateSBC_Name(wonum, task, attrValue);
                    break;
                case "SBC_PORTNAME_LOV":
                    validateSBC_NameLOV(wonum, attrValue);
                    break;
                case "CPE_MGMT_PE_NAME_LOV":
                    validateCpeMgmtPeName(wonum, attrValue);
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateBlokNomor(String wonum, String task, String attrValue) throws SQLException {
        if (Arrays.asList(taskBlokNomor).contains(task)) {
            if (!attrValue.equalsIgnoreCase("None") || !attrValue.equalsIgnoreCase("")) {
                int intValue = Integer.parseInt(attrValue);
                if (intValue>20 && intValue<1) {
                    
                } else {
                    for (int l = 1; l <= 20; l++) {
                        String attrName = "ND"+l+".START-END";
                        taskAttrDao2.deleteTaskAttr(wonum, attrName);
                    }
                }
            }
        } else if (task.equalsIgnoreCase("Activate Softswitch")) {
            if (!attrValue.equalsIgnoreCase("None") || !attrValue.equalsIgnoreCase("")) {
                int intValue = Integer.parseInt(attrValue);
                if (intValue>20 && intValue<1) {
                    
                } else {
                    for (int l = 1; l <= 20; l++) {
                        String attrName = "ND"+l+".PREFIX";
                        taskAttrDao2.deleteTaskAttr(wonum, attrName);
                    }
                }
            }
        }
    }
    
    private void validatePackage(String wonum, String task, String attrValue) throws SQLException {
        if (task.equalsIgnoreCase("Allocate IP Address") && attrValue.equalsIgnoreCase("IP Transit Beda Bandwidth")) {
            taskAttrDao1.updateMandatory(wonum, "WAN-SERVICEIP-DOMESTIK", 1);
            taskAttrDao1.updateMandatory(wonum, "WAN-SUBNETMASK-DOMESTIK", 1);
            taskAttrDao1.updateMandatory(wonum, "WAN-IPDOMAIN-DOMESTK", 1);
            taskAttrDao1.updateMandatory(wonum, "WAN-NETWORKADDRESS-DOMESTIK", 1);
            taskAttrDao1.updateMandatory(wonum, "WAN-GATEWAYADDRESS-DOMESTIK", 1);
            taskAttrDao1.updateMandatory(wonum, "WAN-RESERVATIONID-DOMESTIK", 1);
            
            taskAttrDao1.updateTaskValue(wonum, "WAN-SERVICEIP-DOMESTIK", "None");
            taskAttrDao1.updateTaskValue(wonum, "WAN-SUBNETMASK-DOMESTIK", "None");
            taskAttrDao1.updateTaskValue(wonum, "WAN-IPDOMAIN-DOMESTK", "None");
            taskAttrDao1.updateTaskValue(wonum, "WAN-NETWORKADDRESS-DOMESTIK", "None");
            taskAttrDao1.updateTaskValue(wonum, "WAN-GATEWAYADDRESS-DOMESTIK", "None");
            taskAttrDao1.updateTaskValue(wonum, "WAN-RESERVATIONID-DOMESTIK", "None");
        }
    }
    
    private void setWoAttrVendor(String parent, String task, String attrValue) throws SQLException {
        if (Arrays.asList(taskTSEL).contains(task)) {
            if (!attrValue.equalsIgnoreCase("")) {
                taskAttrDao1.updateWO("app_fd_workorderattribute", "c_attr_value='"+attrValue+"'", "c_wonum='"+parent+"' AND c_attr_name = 'NAMA_VENDOR'");
            }
        }
    }
    
    private void setWoAttrMitra(String parent, String task, String attrValue) throws SQLException {
        if (Arrays.asList(taskTSEL).contains(task)) {
            if (!attrValue.equalsIgnoreCase("")) {
                taskAttrDao1.updateWO("app_fd_workorderattribute", "c_attr_value='"+attrValue+"'", "c_wonum='"+parent+"' AND c_attr_name = 'MITRA_TSA'");
            }
        }
    }
    
    private void validateAnManufacture(String parent, String wonum, String task, String attrValue) {
        try {
            String product = taskAttrDao1.getProductName(parent);
            if (product.equalsIgnoreCase("Wifi Managed Service") && task.equalsIgnoreCase("Pull Drop Cable Wifi")) {
                if (attrValue.equalsIgnoreCase("ZTE")) {
                    taskAttrDao1.updateMandatory(wonum, "AN_DEVICE_ID", 0);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateEncryptionMethod(String parent, String wonum, String attrValue) {
        try {
            String product = taskAttrDao1.getProductName(parent);
            String orderType = taskAttrDao2.getCrmOrderType(parent);
            
            if (product.equalsIgnoreCase("SMS_A2P") && orderType.equalsIgnoreCase("Modify")) {
                String woAttrValue = taskAttrDao1.getWoAttrValue(parent, "Encryption_Method");
                if (attrValue != woAttrValue) {
                    taskAttrDao1.updateTaskValue(wonum, "ENCRYPTION_METHOD", woAttrValue);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String getScOrderNo(String parent) {
        String segment = "";
        try {
            String scOrderNo = taskAttrDao2.getScOrderNo(parent);
            if (scOrderNo.matches("1-(.*)")) {
                segment = "DES";
            } else if (scOrderNo.matches("2-(.*)")) {
                segment = "DWS";
            } else if (scOrderNo.matches("SC(.*)") || scOrderNo.matches("MYI(.*)")) {
                segment = "DCS";
            } else {
                segment = "";
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
        return segment;
    }
    
    private void validateApprovalWMS(String parent, String wonum, String task) {
        String[] taskList = {"Pull Drop Cable Wifi","Pickup NTE from SCM Wifi"};
        String[] segment = {"DES","DGS","DBS"};
        try {
            String product = taskAttrDao1.getProductName(parent);
            String dcType = taskAttrDao1.getWoAttrValue(parent, "DC_Type");
            String[] splittedDcType = dcType.split(" ");
            String dcTypeSplit = splittedDcType[0];
            if (product.equalsIgnoreCase("Wifi Managed Service") && Arrays.asList(taskList).contains(task) && getScOrderNo(parent).equalsIgnoreCase("DES")) {
                if (Arrays.asList(segment).contains(dcTypeSplit)) {
                    taskAttrDao2.updateReadOnly(wonum, "APPROVAL", 1);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validatePE_Name(String wonum, String task, String attrValue) {
        String[] listTask = {"PopulatePEPort Manage CE", "Entry VLAN For Manage CPE"};
        try {
            if (Arrays.asList(listTask).contains(task)) {
                if (!attrValue.equalsIgnoreCase("None")) {
                    String serviceType = taskAttrDao1.getTaskAttrValue(wonum, "SERVICE_TYPE");
                    getPEPort(wonum, attrValue, serviceType);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validatePE_NameLOV(String wonum, String attrValue) {
        try {
            if (attrValue.equalsIgnoreCase("None")) {
                taskAttrDao1.updateTaskValue(wonum, "CPE_MGMT_PE_PORTNAME", attrValue);
            } else {
                String value = taskAttrDao1.getTkdeviceAttrValue(wonum, "CPE_MGMT_PE_PORTNAME", attrValue);
                taskAttrDao1.updateTaskValue(wonum, "CPE_MGMT_PE_PORTNAME", value);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void getPEPort(String wonum, String deviceName, String serviceType) {
        try {
            APIConfig apiConfig = new APIConfig();
            apiConfig = connUtil.getApiParam("uimax_dev");
            URL url = new URL(apiConfig.getUrl()+"/api/device/portsByService?deviceName="+deviceName+"&serviceType="+serviceType+""
                    + "&portPurpose=TRUNK&portPurpose=ACTIVE");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            StringBuffer response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }   LogUtil.info(this.getClass().getName(), "Response = " + response);
            }
            // 'response' contains the JSON data as a string
            String jsonData = response.toString();
            JSONParser parser = new JSONParser();
            JSONObject data_obj = (JSONObject)parser.parse(jsonData);
            JSONArray jsonArray = (JSONArray) data_obj.get("port");
            taskAttrDao2.deleteTkDeviceattribute(wonum, "'CPE_MGMT_PE_PORTNAME','PE_KEY'");
            int x = 0;
            for (Object obj: jsonArray) {
                JSONObject peObj = (JSONObject)obj;
                String attr_name = "CPE_MGMT_PE_PORTNAME";
                String type = "";
                String description = peObj.get("name").toString();
                taskAttrDao2.insertToDeviceTable(wonum, type, attr_name, description);
                String attr_name2 = "PE_KEY";
                String type2 = peObj.get("name").toString();
                String description2 = peObj.get("key").toString();
                taskAttrDao2.insertToDeviceTable(wonum, type2, attr_name2, description2);
                x++;
            }
            
            if (conn.getResponseCode() != 200) {
                LogUtil.info(this.getClass().getName(), "Error get data SBC Port");
            }
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException | SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateSBC_Name(String wonum, String task, String attrValue) {
        try {
            if (task.equalsIgnoreCase("Populate SBC")) {
                if (!attrValue.equalsIgnoreCase("None")) {
                    String serviceType = taskAttrDao1.getTaskAttrValue(wonum, "SERVICE_TYPE");
                    getSBCPort(wonum, attrValue, serviceType);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateSBC_NameLOV(String wonum, String attrValue) {
        try {
            if (attrValue.equalsIgnoreCase("None")) {
                taskAttrDao1.updateTaskValue(wonum, "SBC_PORTNAME", attrValue);
            } else {
                String value = taskAttrDao1.getTkdeviceAttrValue(wonum, "SBC_PORTNAME", attrValue);
                taskAttrDao1.updateTaskValue(wonum, "SBC_PORTNAME", value);
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void getSBCPort(String wonum, String deviceName, String serviceType) {
        try {
            APIConfig apiConfig = new APIConfig();
            apiConfig = connUtil.getApiParam("uimax_dev");
            URL url = new URL(apiConfig.getUrl()+"/api/device/portsByService?deviceName="+deviceName+"&serviceType="+serviceType+""
                    + "&portPurpose=TRUNK&portPurpose=ACTIVE");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            StringBuffer response;
            try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                String inputLine;
                response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }   LogUtil.info(this.getClass().getName(), "Response = " + response);
            }
            // 'response' contains the JSON data as a string
            String jsonData = response.toString();
            JSONParser parser = new JSONParser();
            JSONObject data_obj = (JSONObject)parser.parse(jsonData);
            JSONArray portArray = (JSONArray) data_obj.get("port");
            taskAttrDao2.deleteTkDeviceattribute(wonum, "'SBC_PORTNAME'");
            int x = 0;
            for (Object obj : portArray) {
                JSONObject portObj = (JSONObject)obj;
                String attr_name = "SBC_PORTNAME";
                String type = "";
                String description = portObj.get("name").toString();
                taskAttrDao2.insertToDeviceTable(wonum, type, attr_name, description);
                x++;
            }
            
            if (conn.getResponseCode() != 200) {
                LogUtil.info(this.getClass().getName(), "Error get data SBC Port");
            }
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException | SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateCpeMgmtPeName(String wonum, String attrValue) {
        try {
            if (!attrValue.equalsIgnoreCase("None")) {
                String descPEIp = taskAttrDao2.getDescTkDeviceAttr(wonum, "CPE_MGMT_PE_IPADDRESS", attrValue);
                if (!descPEIp.isEmpty()) {
                    taskAttrDao1.updateTaskValue(wonum, "CPE_MGMT_PE_IPADDRESS", descPEIp);
                }
                String descPEMan = taskAttrDao2.getDescTkDeviceAttr(wonum, "CPE_MGMT_PE_MANUFACTUR", attrValue);
                if (!descPEMan.isEmpty()) {
                    taskAttrDao1.updateTaskValue(wonum, "CPE_MGMT_PE_MANUFACTUR", descPEMan);
                }
                String descPEMod = taskAttrDao2.getDescTkDeviceAttr(wonum, "CPE_MGMT_PE_MODEL", attrValue);
                if (!descPEMod.isEmpty()) {
                    taskAttrDao1.updateTaskValue(wonum, "CPE_MGMT_PE_MODEL", descPEMod);
                }
                String descPEName = taskAttrDao2.getDescTkDeviceAttr(wonum, "CPE_MGMT_PE_NAME", attrValue);
                if (!descPEName.isEmpty()) {
                    taskAttrDao1.updateTaskValue(wonum, "CPE_MGMT_PE_NAME", descPEName);
                }
            } else {
                
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateTaskAttribute2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
