/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.*;
import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import id.co.telkom.wfm.plugin.util.JsonUtil;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import javax.sql.DataSource;

import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ASUS
 */
public class ValidateNonCoreProduct {

    TestUpdateStatusEbisDao daoTestUpdate = new TestUpdateStatusEbisDao();
    TaskHistoryDao daoHistory = new TaskHistoryDao();
    NonCoreCompleteDao daoNoncore = new NonCoreCompleteDao();
    JsonUtil requestJson = new JsonUtil();

    // List Task
    String[] listTask = {
        "WFMNonCore Review Order DSO", "WFMNonCore Review Order DSO",
        "WFMNonCore Review Order INF Transponder", "WFMNonCore Review Order TSQ IPPBX",
        "WFMNonCore Review Order TSQ", "WFMNonCore Review Order TSQ IP Transit",
        "WFMNonCore Get Device Access Wifi HomeSpot", "WFMNonCore Review Order DWS BOR",
        "WFMNonCore Review Order TSQ WDM", "WFMNonCore Review Order TSQ SDWAN",
        "WFMNonCore Review Order TSQ IPPBX NeuAPIX", "WFMNonCore Review Order Integrasi Link SMS A2P",
        "WFMNonCore Review Order neuCentrIX Layer 1"
    };
    // List Product
    String[] listProduct = {"WIFI_HOMESPOT", "TSA_CONSPART", "TSA_OSS_ISP", "TSA_OSS_OSP", "TSA_SPMS", "TSA_PM_ISP"};
    // List MO DO RO SO
    String[] listMoDoRoSo = {"Modify", "Disconnect", "Resume", "Suspend"};
    // List Product MM IP TRANSIT
    String[] productlist = {"MM_IP_TRANSIT"};
    // List Detailactcode 
    String[] detailactcodeList = {"WFMNonCore Allocate Service IPTransit", "WFMNonCore Validate Service IPTransit"};

    // Generate Format Number
    public String generate(String wonum, String defaultFormat) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddHHmm");
        String formattedDateTime = currentDateTime.format(formatter);

        String num = wonum.substring(wonum.length() - 3);
        String result = defaultFormat + formattedDateTime + num;
        return result;
    }

    // Get Params
    public JSONObject getParams(String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT wo2.c_wonum, wo1.c_crmordertype, wo1.c_productname, wo1.c_producttype, wo2.c_detailactcode, wo2.c_worktype, wo1.c_workzone, wo1.c_serviceaddress, wo1.c_customer_name\n"
                + "FROM app_fd_workorder wo1\n"
                + "JOIN app_fd_workorder wo2 ON wo1.c_wonum = wo2.c_parent\n"
                + "WHERE wo1.c_woclass = 'WORKORDER'\n"
                + "AND wo2.c_woclass = 'ACTIVITY'\n"
                + "AND wo2.c_wonum = ?";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultObj.put("crmordertype", rs.getString("c_crmordertype"));
                resultObj.put("productname", rs.getString("c_productname"));
                resultObj.put("producttype", rs.getString("c_producttype"));
                resultObj.put("detailactcode", rs.getString("c_detailactcode"));
                resultObj.put("worktype", rs.getString("c_worktype"));
                resultObj.put("workzone", rs.getString("c_workzone"));
                resultObj.put("serviceaddress", rs.getString("c_serviceaddress"));
                resultObj.put("customername", rs.getString("c_customer_name"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

    // Update SID Value if value != null
    public boolean updateSID(String parent) throws SQLException {
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String updateQuery
                = "UPDATE APP_FD_WORKORDERSPEC "
                + "SET c_value = CASE c_assetattrid "
                + "WHEN 'SID' THEN ? "
                + "ELSE 'Missing' END "
                + "WHERE c_parent = ? "
                + "AND c_assetattrid IN ('SID')";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(updateQuery)) {
            String newSidValue = generate(parent, "888");
            ps.setString(1, newSidValue);
            ps.setString(2, parent);

            int exe = ps.executeUpdate();

            if (exe > 0) {
                result = true;
                LogUtil.info(getClass().getName(), "Update SID Berhasil : " + result);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return result;
    }

    public String getAssetstatus(String serviceid) throws SQLException, JSONException {
        String status = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_status \n"
                + "FROM app_fd_asset \n"
                + "WHERE c_assetnum = ? \n";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, serviceid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                status = rs.getString("c_status");
                LogUtil.info(getClass().getName(), "Status asset : " + status);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return status;
    }

    public JSONObject getAssetspec(String serviceid, String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid \n"
                + "FROM app_fd_assetspec \n"
                + "WHERE c_assetnum = ? \n"
                + "AND c_assetattrid IN ( \n"
                + "SELECT c_assetattrid \n"
                + "FROM c_wonum = ? AND c_readonly = 0)\n";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, serviceid);
            ps.setString(2, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultObj.put("assetattrid", rs.getString("c_assetattrid"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

    // Get Assetattrid
    public JSONObject getAssetattrid(String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value \n"
                + "FROM app_fd_workorderspec \n"
                + "WHERE c_wonum = ? \n";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultObj.put("assetattrid", rs.getString("c_assetattrid"));
                resultObj.put("value", rs.getString("c_value"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

    // Get Locations
    public JSONObject getLocations(String location) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT * \n"
                + "FROM app_fd_locations \n"
                + "WHERE c_location = ? \n";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, location);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultObj.put("location", rs.getString("c_location"));
                resultObj.put("description", rs.getString("c_description"));
                resultObj.put("saddresscode", rs.getString("c_saddresscode"));
                resultObj.put("type", rs.getString("c_type"));
                resultObj.put("status", rs.getString("c_status"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

    // Function Update Asset Status
    public String updateStatus(String serviceid, String status, String modifiedBy) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_asset SET c_status = ?, modifiedby = ?, dateModified = sysdate WHERE c_assetnum = ?";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, status);
            ps.setString(2, modifiedBy);
            ps.setString(3, serviceid);
            int exe = ps.executeUpdate();

            if (exe > 0) {
                LogUtil.info(getClass().getName(), "Updated Status Successfully for " + serviceid + " : " + status);
                return "Update asset status berhasil";
            } else {
                return "Update asset status gagal";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Function update AssetSpec Value
    public String updateAssetSpecValue(String wonum, String value, String modifiedBy, String assetattrid) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorderspec SET c_value = ?, modifiedby = ?, dateModified = sysdate WHERE c_wonum = ? AND c_assetattrid = ?";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, value);
            ps.setString(2, modifiedBy);
            int exe = ps.executeUpdate();

            if (exe > 0) {
                return "Update value assetspec berhasil";
            } else {
                return "Update value assetspec gagal";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    // Function update AssetSpec Value
    public void updateLocation(String location, String description, String modifiedBy, String addresscode) throws SQLException {
        String uuId = UuidGenerator.getInstance().getUuid();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectQuery
                = "INSERT INTO app_fd_locations "
                + "( "
                + " id,"
                + " c_location,"
                + " c_description,"
                + " c_saddresscode,"
                + " c_disabled," 
                + " modifiedby,"
                + " c_type,"
                + " c_status)"
                + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, uuId);
            ps.setString(2, location);
            ps.setString(3, description);
            ps.setString(4, addresscode);
            ps.setInt(5, 0);
            ps.setString(6, modifiedBy);
            ps.setString(7, "FMS");
            ps.setString(8, "OPERATING");

            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "Insert Locations successfully " + location);
            } else {
                LogUtil.info(getClass().getName(), "Insert Locations Failed " + location);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
    }

    // validate status STARTWA
    public boolean validateStartwa(UpdateStatusParam param) throws SQLException, JSONException {
        boolean startwa = false;
        LogUtil.info(getClass().getName(), "VALIDATE STARTWA FOR NON-CORE");
        try {
            // Get Params
            JSONObject params = getParams(param.getWonum());
            String detailactcode = params.get("detailactcode").toString();
            String worktype = params.get("worktype").toString();
            String crmordertype = params.get("crmordertype").toString();
            String productname = params.get("productname").toString();

            LogUtil.info(this.getClass().getName(), "PARAMS : " + params);

            LogUtil.info(this.getClass().getName(), "WORKTYPE : " + worktype);
            LogUtil.info(this.getClass().getName(), "PRODUCTNAME : " + productname);
            LogUtil.info(this.getClass().getName(), "DETAILACTCODE : " + detailactcode);
            
            if (!Arrays.asList(listTask).contains(detailactcode) && !productname.equalsIgnoreCase("")) {
                startwa = true;
            } else if (!productname.equalsIgnoreCase("") && Arrays.asList(listTask).contains(detailactcode) && "New Install".equalsIgnoreCase(crmordertype)) {
                String SID = generate(param.getWonum(), "888");
//                updateSID(param.getWonum());
                updateSID(param.getParent());
                LogUtil.info(this.getClass().getName(), "MESSAGE: Berhasil Generate SID");
                startwa = true;
            } else {
                LogUtil.info(this.getClass().getName(), "MESSAGE: Gagal Generate SID");
                startwa = false;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return startwa;
    }

    public void validateComplete(UpdateStatusParam param) throws SQLException, JSONException, IOException {
        int isNoncore = 0;
        String taskattribute = daoNoncore.getTaskattributeValue(param.getWonum(), "SID");
        // Get Params
        JSONObject params = getParams(param.getWonum());
        String productname = params.optString("productname", null);
        String worktype = params.optString("worktype", null);
        String crmordertype = params.optString("crmordertype", null);

        isNoncore = daoNoncore.isNonCoreProduct(productname);

        if ("WFM".equals(worktype) && !Arrays.asList(listProduct).contains(productname) && !(daoNoncore.getTaskattributeValue(param.getWonum(), "APPROVAL")).equals("REJECTED")) {
            if (isNoncore == 1) {
                if (Arrays.asList(listMoDoRoSo).contains(crmordertype)) {
                    validateProduct(productname, crmordertype, param.getModifiedBy(), param.getWonum(), param.getSiteId());
                } else if (crmordertype.equals("New Install")) {
                    org.json.simple.JSONObject customerAccountidTemp = daoNoncore.getWorkorderattributeValue(param.getParent(), "CUSTOMERPARTY_ACCOUNTID");
                    String customerAccountIdTemp = customerAccountidTemp.toString();
                    String customerAccountid = "C" + customerAccountIdTemp.substring(3);

                    if (customerAccountid != null) {
                        String sID = taskattribute;
                        String addresscode = generate(param.getWonum(), "AD888");
                        String siteId = param.getSiteId();
                        String description = params.optString("serviceaddress").substring(0, Math.min(params.optString("serviceaddress").length(), 50));
                        String locationid = generate(param.getWonum(), "C888");
                        String customerName = params.optString("customername");
                        String assettype = "";
                        String productName = "";

                        JSONObject location = getLocations(customerAccountid);

                        if (location != null) {
                            try {
                                daoNoncore.generateServiceAddress(addresscode, siteId, description);
                                updateLocation(locationid, customerName, param.getModifiedBy(), addresscode);
                            } catch (SQLException e) {
                                LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
                            }
                            if (productname.equals("NeuCentrIX Interconnect Layer 1")) {
                                assettype = "NEUCENTRIX LAYER 1";
                                productName = productname.toUpperCase();
                            } else {
                                assettype = productName;
                            }

                            String classstructureid = daoNoncore.getAssetClassstructureid(productName);

                            daoNoncore.generateServiceAsset(sID, locationid, addresscode, assettype, sID, classstructureid);
                            daoNoncore.generateAssetSpecAttribute(sID, param.getParent());
                            daoNoncore.reserveResource(sID);
                        } else {
                            daoNoncore.generateServiceAddress(addresscode, siteId, description);

                            if (productname.equals("NeuCentrIX Interconnect Layer 1")) {
                                assettype = "NEUCENTRIX LAYER 1";
                                productName = productname.toUpperCase();
                            } else {
                                assettype = productName;
                            }

                            String classstructureid = daoNoncore.getAssetClassstructureid(productName);

                            daoNoncore.generateServiceAsset(sID, locationid, addresscode, assettype, sID, classstructureid);
                            daoNoncore.generateAssetSpecAttribute(sID, param.getWonum());
                            daoNoncore.reserveResource(sID);
                        }

                    }
                }
            }
        }
    }

    public String validateProduct(String productname, String crmordertype, String modifiedBy, String wonum, String siteId) throws SQLException, JSONException, IOException {
        org.json.simple.JSONObject serviceid = daoNoncore.getWorkorderattributeValue(wonum, "SID");
        String taskattribute = daoNoncore.getTaskattributeValue(wonum, "SID");
        String serviceId = serviceid.toJSONString();

        JSONObject assetspec = getAssetspec(serviceId, wonum);
        JSONObject workorderspec = getAssetattrid(wonum);

        if (!serviceid.isEmpty()) {
            String assetstatus = getAssetstatus(serviceId);
            if (assetstatus != null) {
                switch (crmordertype) {
                    case "Disconect":
                        updateStatus(serviceId, "INACTIVE", modifiedBy);
                        break;
                    case "Resume":
                        updateStatus(serviceId, "OPERATING", modifiedBy);
                        break;
                    case "Suspend":
                        updateStatus(serviceId, "SUSPEND", modifiedBy);
                        break;
                    case "Modify":
                        while (assetspec != null) {
                            if (workorderspec != null) {
                                updateAssetSpecValue(wonum, workorderspec.optString("value"), modifiedBy, assetspec.optString("assetattrid"));
                            }
                        }
                        String sID = taskattribute;
                        daoNoncore.reserveResource(sID);
                        break;
                }
            }
        }
        return null;
    }

    public void updateNonCoreAutoFillWorkorderSpec(String wonum, String attrname, String value) {
        boolean status = false;
        java.util.Date date = new Date();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String queryUpdate = "UPDATE app_fd_workorderspec SET set c_value=? WHERE c_wonum=? AND c_assetattrid=?";
        try {
            Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(queryUpdate);
            ps.setString(1, value);
            ps.setString(2, wonum);
            ps.setString(3, attrname);
            int count = ps.executeUpdate();
            if (count > 0) {
                status = true;
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }

        LogUtil.info(getClass().getName(), "Status Insert : " + status);
    }

    public boolean nonCoreAutoFill(String wonum) {
        // workorder where c_wonum ,c_woclass='activ'
        boolean status = false;

        try {
            JSONObject dataWO = getParams(wonum);
            String workzone = dataWO.optString("workzone", null);
            String productname = dataWO.optString("productname", null);
            String detailactcode = dataWO.optString("detailactcode", null);

            String devicetype = "ROUTER";
            String ServiceType = "TRANSIT";

            if (Arrays.asList(productlist).contains(productname) && Arrays.asList(detailactcodeList).contains(detailactcode)) {
                updateNonCoreAutoFillWorkorderSpec("SERVICE_TYPE", ServiceType, wonum);
                updateNonCoreAutoFillWorkorderSpec("DEVICETYPE", devicetype, wonum);
                updateNonCoreAutoFillWorkorderSpec("AREANAME", workzone, wonum);
                status = true;
            } else {
                status = false;
            }
        } catch (Exception e) {
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return status;
    }
}
