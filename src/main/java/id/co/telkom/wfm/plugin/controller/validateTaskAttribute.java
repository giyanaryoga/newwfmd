/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.TaskAttributeUpdateDao;
import id.co.telkom.wfm.plugin.dao.TaskActivityDao;
import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.util.ConnUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
    ConnUtil connUtil = new ConnUtil();

    private static final String nteType1[] = {"L2Switch", "DirectME", "DirectPE"};
    private static final String taskNTEAvailable[] = {
        "Survey-Ondesk", "Site-Survey", "Survey-Ondesk Wifi", "Site-Survey Wifi"
    };
    private static final String cpeActivity[] = {
        "Pickup AP From SCM Wifi", "Install AP", "Pickup NTE from SCM Wifi",
        "Install NTE Wifi", "Pickup NTE from SCM", "Pickup NTE from SCM Manual",
        "Install NTE", "Install NTE Manual"
    };

    private void validateRole(String parent, String wonum) {
        try {
            String value = taskAttrDao.getTaskAttrValue(wonum, "ROLE");
            String valueNteType = taskAttrDao.getTaskAttrValue(wonum, "NTE_TYPE");

            if (value.equalsIgnoreCase("STP")) {
                taskAttrDao.updateMandatory(wonum, "NTE_TYPE", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 0);
                taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 0);

                taskAttrDao.updateTaskAttrViewLike(parent, "NTE%", 0);
                taskAttrDao.updateTaskAttrViewLike(parent, "STP%", 1);
            } else {
                //IF ROLE = NTE
                LogUtil.info(getClass().getName(), "NTE_TYPE =" + valueNteType);
                taskAttrDao.updateTaskAttrViewLike(parent, "STP%", 0);
                taskAttrDao.updateTaskAttrViewLike(parent, "NTE%", 1);
                if (valueNteType == null) {
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 0);
                } else if (valueNteType.equalsIgnoreCase("ONT")) {
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 1);
                } else if (Arrays.asList(nteType1).contains(valueNteType)) {
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MODEL", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_MANUFACTUR", 0);
                } else {
                    LogUtil.info(getClass().getName(), "NTE_TYPE not yet inserted");
                }
                taskAttrDao.updateTaskAttrView(wonum, "STP_NETWORKLOCATION", 1);
                taskAttrDao.updateTaskAttrView(wonum, "STP_NETWORKLOCATION_LOV", 1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void nteAvailable(String wonum) {
        try {
            String value = taskAttrDao.getTaskAttrValue(wonum, "NTE_AVAILABLE");
            String detailactcode = taskAttrDao.getActivity(wonum);
            if (Arrays.asList(nteType1).contains(detailactcode)) {
                if (value.equalsIgnoreCase("YES")) {
                    taskAttrDao.updateMandatory(wonum, "NTE_TYPE", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORTNAME", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 1);

                    taskAttrDao.updateTaskValue(wonum, "NTE_TYPE", "None");
                    taskAttrDao.updateTaskValue(wonum, "NTE_NAME", "None");
                    taskAttrDao.updateTaskValue(wonum, "NTE_SERIALNUMBER", "None");
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORTNAME", "None");
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORT", "None");
                } else {
                    taskAttrDao.updateMandatory(wonum, "NTE_TYPE", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_NAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_SERIALNUMBER", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORTNAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);

                    taskAttrDao.updateTaskValue(wonum, "NTE_TYPE", "");
                    taskAttrDao.updateTaskValue(wonum, "NTE_NAME", "");
                    taskAttrDao.updateTaskValue(wonum, "NTE_SERIALNUMBER", "");
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORTNAME", "");
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORT", "");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void validateValueNextTask(String parent, String attrName, String attrValue) {
        try {
            JSONArray taskAttrParent = taskAttrDao.getTaskAttributeParent(parent);
            for (Object obj : taskAttrParent) {
                JSONObject taskAttrObj = (JSONObject) obj;
//                LogUtil.info(getClass().getName(), "Task attribute = " +taskAttrObj);
                String attr_name = taskAttrObj.get("task_attr_name").toString();
//                String attr_value = (taskAttrObj.get("task_attr_value") == null ? "" : taskAttrObj.get("task_attr_value").toString());
                if (attrName.equalsIgnoreCase(attr_name) ) {
                    if (!attrName.equalsIgnoreCase("APPROVAL")) {
                        taskAttrDao.updateTaskValueParent(parent, attr_name, attrValue);
                        LogUtil.info(getClass().getName(), "Task attribute value insert from assetattrid same");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void validateSTO(String wonum, String attrValue) {
        try {
            JSONObject workzone = taskAttrDao.getWorkzoneRegional(attrValue);
            taskAttrDao.updateTaskValue(wonum, "REGION", workzone.get("region").toString());
            taskAttrDao.updateTaskValue(wonum, "WITEL", workzone.get("subregion").toString());
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void validatePIC(String parent, String wonum) {
        try {
            String valuePic = taskAttrDao.getTaskAttrValue(wonum, "PIC_CONTACTNUMBER");
            if (!valuePic.equalsIgnoreCase("")) {
                taskAttrDao.updateWoAttrView(parent, "ContactPhoneNumber", valuePic);
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void validateNeuAPIX(String parent, String wonum, String attrValue) {
        try {
            String productName = taskAttrDao.getProductName(parent);
            String activity = taskAttrDao.getActivity(wonum);
            String nextWonum = taskAttrDao.getWonumActivityTask(parent, "WFMNonCore Review Order AO NeuAPIX");
            if (productName.equalsIgnoreCase("INF_IPPBX_NEUAPIX") && activity.equalsIgnoreCase("WFMNonCore Allocate Service NeuAPIX")) {
                if (attrValue.equalsIgnoreCase("JK5C")) {
                    taskAttrDao.updateTaskValue(nextWonum, "SBC IPADDRESS", "172.27.27.119");
                    taskAttrDao.updateTaskValue(nextWonum, "PORT SBC", "PKT2");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC MAIN NAME", "ME3-D2-JT");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC MAIN IPADDRESS", "172.30.129.1");
                    taskAttrDao.updateTaskValue(nextWonum, "PORT METRO SBC MAIN", "8/2/13");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC BACKUP NAME", "ME6-D2-JT");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC BACKUP IPADDRESS", "172.30.129.41");
                    taskAttrDao.updateTaskValue(nextWonum, "PORT METRO SBC BACKUP", "3/1/4");
                    taskAttrDao.updateTaskValue(nextWonum, "IP P2P SBC", "10.41.0.2/29");
                    taskAttrDao.updateTaskValue(nextWonum, "VLAN", "2500");
                    taskAttrDao.updateTaskValue(nextWonum, "VCID", "1973232500");
                    taskAttrDao.updateTaskValue(nextWonum, "PE_NAME", "PE3-D2-JT2-VPN");
                    taskAttrDao.updateTaskValue(nextWonum, "PE_IPADDRESS", "118.98.9.16");
                    taskAttrDao.updateTaskValue(nextWonum, "PE_PORTNAME", "Gi0/0/1/19");
                    taskAttrDao.updateTaskValue(nextWonum, "ME_SERVICE_NAME", "ME9-D2-JT ");
                    taskAttrDao.updateTaskValue(nextWonum, "ME_SERVICE_IPADDRESS", "172.30.129.100");
                    taskAttrDao.updateTaskValue(nextWonum, "ME_SERVICE_PORTNAME", "5/1/15");
                } else if (attrValue.equalsIgnoreCase("YG1C")) {
                    taskAttrDao.updateTaskValue(nextWonum, "SBC IPADDRESS", "172.27.75.199");
                    taskAttrDao.updateTaskValue(nextWonum, "PORT SBC", "PKT2");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC MAIN NAME", "ME9-D4-KBU");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC MAIN IPADDRESS", "172.31.63.135");
                    taskAttrDao.updateTaskValue(nextWonum, "PORT METRO SBC MAIN", "Gi3/0/7");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC BACKUP NAME", "ME9-D4-KBU");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC BACKUP IPADDRESS", "172.31.63.135");
                    taskAttrDao.updateTaskValue(nextWonum, "PORT METRO SBC BACKUP", "Gi3/0/4");
                    taskAttrDao.updateTaskValue(nextWonum, "IP P2P SBC", "10.41.0.10/29");
                    taskAttrDao.updateTaskValue(nextWonum, "VLAN", "2500");
                    taskAttrDao.updateTaskValue(nextWonum, "VCID", "1973432500");
                    taskAttrDao.updateTaskValue(nextWonum, "PE_NAME", "PE2-D4-KBU-VPN");
                    taskAttrDao.updateTaskValue(nextWonum, "PE_IPADDRESS", "118.98.9.122");
                    taskAttrDao.updateTaskValue(nextWonum, "PE_PORTNAME", "Gi0/4/1/4");
                    taskAttrDao.updateTaskValue(nextWonum, "ME_SERVICE_NAME", "ME-D4-KBU");
                    taskAttrDao.updateTaskValue(nextWonum, "ME_SERVICE_IPADDRESS", "1172.31.63.40");
                    taskAttrDao.updateTaskValue(nextWonum, "ME_SERVICE_PORTNAME", "Gi16/0/19");
                } else if (attrValue.equalsIgnoreCase("SB1C")) {
                    taskAttrDao.updateTaskValue(nextWonum, "SBC IPADDRESS", "172.27.87.7");
                    taskAttrDao.updateTaskValue(nextWonum, "PORT SBC", "PKT2");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC MAIN NAME", "ME3-D5-KBL");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC MAIN IPADDRESS", "172.31.64.67");
                    taskAttrDao.updateTaskValue(nextWonum, "PORT METRO SBC MAIN", "Gi8/1/1");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC BACKUP NAME", "ME4-D5-KBL");
                    taskAttrDao.updateTaskValue(nextWonum, "METRO SBC BACKUP IPADDRESS", "172.31.64.68");
                    taskAttrDao.updateTaskValue(nextWonum, "PORT METRO SBC BACKUP", "Gi8/1/1");
                    taskAttrDao.updateTaskValue(nextWonum, "IP P2P SBC", "10.41.0.18/29");
                    taskAttrDao.updateTaskValue(nextWonum, "VLAN", "2500");
                    taskAttrDao.updateTaskValue(nextWonum, "VCID", "1973532500");
                    taskAttrDao.updateTaskValue(nextWonum, "PE_NAME", "PE2-D5-KBL-VPN");
                    taskAttrDao.updateTaskValue(nextWonum, "PE_PORTNAME", "Gi0/0/1/0");
                    taskAttrDao.updateTaskValue(nextWonum, "ME_SERVICE_NAME", "ME9-D5-KBL");
                    taskAttrDao.updateTaskValue(nextWonum, "PE_IPADDRESS", "118.98.9.122");
                    taskAttrDao.updateTaskValue(nextWonum, "ME_SERVICE_IPADDRESS", "118.98.9.22");
                    taskAttrDao.updateTaskValue(nextWonum, "ME_SERVICE_PORTNAME", "Gi12/0/5");
                }
            } else {
                LogUtil.info(getClass().getName(), "Product name is not INF_IPPBX_NEUAPIX");
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void validateSTP(String parent, String wonum, String attrtype) throws SQLException {
        String id = taskAttrDao.getTkdeviceAttrValue(wonum, "STP_ID", attrtype);
        String specification = taskAttrDao.getTkdeviceAttrValue(wonum, "STP_SPECIFICATION", attrtype);
        String netLoc = taskAttrDao.getTaskAttrValue(wonum, "STP_NETWORKLOCATION_LOV");
        LogUtil.info(getClass().getName(), "STP_ID = " + id + "; STP_SPECIFICATION = " + specification + "; STP_NETWORKLOCATION = " +netLoc);
        try {
            boolean validate = taskAttrDao.updateAttributeSTP(parent, id, specification, netLoc);
            if (validate == true) {
                LogUtil.info(getClass().getName(), "Update Data Successfully");
            } else {
                LogUtil.info(getClass().getName(), "Update Data failed");
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateAPManufacture(String wonum, String attrValue) {
        try {
            String activity = taskAttrDao.getActivity(wonum);
            if (activity.equalsIgnoreCase("Populate AP Manufacture")) {
                if (attrValue.equalsIgnoreCase("HUAWEI")) {
                    taskAttrDao.updateTaskValue(wonum, "AP_MANUFACTURER_CODE", "HW");
                } else if (attrValue.equalsIgnoreCase("CISCO")) {
                    taskAttrDao.updateTaskValue(wonum, "AP_MANUFACTURER_CODE", "CI");
                } else if (attrValue.equalsIgnoreCase("AUTELAN")) {
                    taskAttrDao.updateTaskValue(wonum, "AP_MANUFACTURER_CODE", "AU");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateType(String wonum, String attrValue) {
        try {
            String[] act = { "Pickup AP From SCM Wifi", "Pickup NTE from SCM Wifi" };
            String activity = taskAttrDao.getActivity(wonum);
            if (Arrays.asList(act).contains(activity)) {
                if (attrValue.equalsIgnoreCase("NEW")) {
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORTNAME", 0);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 0);
                } else if (attrValue.equalsIgnoreCase("EXISTING")) {
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORTNAME", 1);
                    taskAttrDao.updateMandatory(wonum, "NTE_DOWNLINK_PORT", 1);
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORTNAME", "None");
                    taskAttrDao.updateTaskValue(wonum, "NTE_DOWNLINK_PORT", "None");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateAPStatus(String wonum, String attrValue) {
        try {
            String[] act = { "Pickup AP From SCM Wifi" };
            String activity = taskAttrDao.getActivity(wonum);
            if (Arrays.asList(act).contains(activity)) {
                if (attrValue.equalsIgnoreCase("NEW")) {
                    taskAttrDao.updateMandatory(wonum, "AP_COORDINATE", 0);
                    taskAttrDao.updateMandatory(wonum, "AP_MAC_ADDRESS", 0);
                    taskAttrDao.updateMandatory(wonum, "AP_MANUFACTURE", 0);
                    taskAttrDao.updateMandatory(wonum, "AP_MODEL", 0);
                    taskAttrDao.updateMandatory(wonum, "AP_PREPOSISI", 0);
                    taskAttrDao.updateMandatory(wonum, "AP_SERIALNUMBER", 0);
                } else if (attrValue.equalsIgnoreCase("EXISTING")) {
                    taskAttrDao.updateMandatory(wonum, "AP_COORDINATE", 1);
                    taskAttrDao.updateMandatory(wonum, "AP_MAC_ADDRESS", 1);
                    taskAttrDao.updateMandatory(wonum, "AP_MANUFACTURE", 1);
                    taskAttrDao.updateMandatory(wonum, "AP_MODEL", 1);
                    taskAttrDao.updateMandatory(wonum, "AP_PREPOSISI", 1);
                    taskAttrDao.updateMandatory(wonum, "AP_SERIALNUMBER", 1);
                    taskAttrDao.updateTaskValue(wonum, "AP_COORDINATE", "None");
                    taskAttrDao.updateTaskValue(wonum, "AP_MAC_ADDRESS", "None");
                    taskAttrDao.updateTaskValue(wonum, "AP_MANUFACTURE", "None");
                    taskAttrDao.updateTaskValue(wonum, "AP_MODEL", "None");
                    taskAttrDao.updateTaskValue(wonum, "AP_PREPOSISI", "None");
                    taskAttrDao.updateTaskValue(wonum, "AP_SERIALNUMBER", "None");
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void validateCpeScmt(String wonum, String attrValue) {
        try {
            String activity = taskAttrDao.getActivity(wonum);
            
            if (Arrays.asList(cpeActivity).contains(activity)) {
                APIConfig apiConfig = new APIConfig();
                apiConfig = connUtil.getApiParam("cpe_validation_ebis");
                URL url = new URL(apiConfig.getUrl());
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                
                JSONObject laborObj = taskAttrDao.getAssignment(wonum);
                JSONObject bodyParam = new JSONObject();
                bodyParam.put("wonum", wonum);
                bodyParam.put("cpeSerialNumber", attrValue);
                bodyParam.put("chiefCode", (laborObj.get("laborcode") == null ? "" : laborObj.get("laborcode").toString()));
                bodyParam.put("amcrew", (laborObj.get("amcrew") == null ? "" : laborObj.get("amcrew").toString()));
                
                String request = bodyParam.toString();
                try (OutputStream outputStream = conn.getOutputStream()) {
                    byte[] b = request.getBytes("UTF-8");
                    outputStream.write(b);
                    outputStream.flush();
                }
                //Response
                int responseCode = conn.getResponseCode();
                InputStream inputStr = conn.getInputStream();
                StringBuilder response = new StringBuilder();
                byte[] res = new byte[2048];
                int i = 0;
                while ((i = inputStr.read(res)) != -1) {
                    response.append(new String(res, 0, i));
                }
                LogUtil.info(this.getClass().getName(), "INI RESPONSE : " + response.toString());

                if (responseCode == 200) {
                    LogUtil.info(this.getClass().getName(), "Success POST");
                } else {
                    LogUtil.info(this.getClass().getName(), "Failed POST");
                }
            }
        } catch (SQLException | IOException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void validate(String parent, String wonum, String attrName, String attrValue) {
        try {
            validateValueNextTask(parent, attrName, attrValue);
            switch (attrName) {
                case "ROLE":
                case "NTE_TYPE":
                    validateRole(parent, wonum);
                    break;
                case "STO":
                    if (!attrValue.equalsIgnoreCase("NAS")) {
                        validateSTO(wonum, attrValue);
                    }
                    break;
                case "NTE_AVAILABLE":
                    nteAvailable(wonum);
                    break;
                case "PIC_CONTACTNUMBER":
                    validatePIC(parent, wonum);
                    break;
                case "HOSTNAME SBC":
                    validateNeuAPIX(parent, wonum, attrValue);
                    break;
                case "STP_NETWORKLOCATION_LOV":
                    validateSTP(parent, wonum, attrValue);
                    break;
                case "AP_MANUFACTURE":
                    validateAPManufacture(wonum, attrValue);
                    break;
                case "TYPE":
                    validateType(wonum, attrValue);
                    break;
                case "AP_STATUS":
                    validateAPStatus(wonum, attrValue);
                    break;
                case "NTE_SERIALNUMBER":
                case "AP_SERIALNUMBER":
                    validateCpeScmt(wonum, attrValue);
                    break;
                default:
                    LogUtil.info(getClass().getName(), "Validate Task Attribute is not found and not execute!");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(validateTaskAttribute.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

