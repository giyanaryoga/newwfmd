/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.NonCoreCompleteDao;
import id.co.telkom.wfm.plugin.dao.TaskHistoryDao;
import id.co.telkom.wfm.plugin.dao.TestUpdateStatusEbisDao;
import id.co.telkom.wfm.plugin.model.UpdateStatusParam;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class validateNonCoreProduct {

    TestUpdateStatusEbisDao daoTestUpdate = new TestUpdateStatusEbisDao();
    TaskHistoryDao daoHistory = new TaskHistoryDao();
    NonCoreCompleteDao daoNoncore = new NonCoreCompleteDao();

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

    String[] listProduct = {"WIFI_HOMESPOT", "TSA_CONSPART", "TSA_OSS_ISP", "TSA_OSS_OSP", "TSA_SPMS", "TSA_PM_ISP"};

    // Generate SID
    public static String generateSid(String wonum) {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddHHmm");
        String formattedDateTime = currentDateTime.format(formatter);

        String num = wonum.substring(wonum.length() - 3);

        return "888" + formattedDateTime + num;
    }

    // Get Params
    public JSONObject getParams(String parent) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT wo1.c_crmordertype, wo1.c_productname, wo1.c_producttype, wo2.c_detailactcode, wo2.c_worktype\n"
                + "FROM app_fd_workorder wo1\n"
                + "JOIN app_fd_workorder wo2 ON wo1.c_wonum = wo2.c_parent\n"
                + "WHERE wo1.c_woclass = 'WORKORDER'\n"
                + "AND wo2.c_woclass = 'ACTIVITY'\n"
                + "AND wo1.c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultObj.put("crmordertype", rs.getString("c_crmordertype"));
                resultObj.put("productname", rs.getString("c_productname"));
                resultObj.put("producttype", rs.getString("c_producttype"));
                resultObj.put("detailactcode", rs.getString("c_detailactcode"));
                resultObj.put("worktype", rs.getString("c_worktype"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

    public JSONObject getParentCRMOrdertype(String parent) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_crmordertype, c_productname, c_producttype \n"
                + "FROM app_fd_workorder \n"
                + "where c_woclass = 'WORKORDER' \n"
                + "AND c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultObj.put("crmordertype", rs.getString("c_crmordertype"));
                resultObj.put("productname", rs.getString("c_productname"));
                resultObj.put("producttype", rs.getString("c_producttype"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

    // Update SID Value if value != null
    public String updateSID(String wonum) throws SQLException {
        String result = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectQuery
                = "SELECT c_value "
                + "FROM app_fd_workorderspec "
                + "WHERE c_assetattrid = 'SID' "
                + "AND c_wonum = ?";

        String insertQuery
                = "UPDATE app_fd_workorderspec SET c_value = ? "
                + "WHERE c_assetattrid = 'SID' "
                + "AND c_wonum =  ?";

        try (Connection con = ds.getConnection();
                PreparedStatement psSelect = con.prepareStatement(selectQuery);
                PreparedStatement psInsert = con.prepareStatement(insertQuery)) {
            psSelect.setString(1, wonum);
            ResultSet rs = psSelect.executeQuery();

            while (rs.next()) {
                result = rs.getString("c_value");
            }

            if (result != null) {
                String newSidValue = generateSid(wonum);
                psInsert.setString(1, newSidValue);
                psInsert.setString(2, wonum);
                psInsert.executeUpdate();
                result = newSidValue;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return result;
    }

    // validate status STARTWA
    public boolean validateStartwa(UpdateStatusParam param) throws SQLException, JSONException {
        boolean startwa = false;
        try {
            // Get Params
            JSONObject params = getParams(param.getParent());
            String detailactcode = (params.get("detailactcode") == null ? "" : params.get("detailactcode").toString());
            String worktype = (params.get("worktype") == null ? "" : params.get("worktype").toString());
            String crmordertype = (params.get("crmordertype") == null ? "" : params.get("crmordertype").toString());
            String productname = (params.get("productname") == null ? "" : params.get("productname").toString());

            LogUtil.info(this.getClass().getName(), "WORKTYPE : " + worktype);
            LogUtil.info(this.getClass().getName(), "PRODUCTNAME : " + productname);
            LogUtil.info(this.getClass().getName(), "DETAILACTCODE : " + detailactcode);

            if ("WFM".equals(worktype) && !productname.equalsIgnoreCase("") && Arrays.asList(listTask).contains(detailactcode)
                    && "New Install".equals(crmordertype)) {
                updateSID(param.getWonum());
                LogUtil.info(this.getClass().getName(), "MESSAGE: Berhasil Generate SID");
            } else {
                LogUtil.info(this.getClass().getName(), "MESSAGE: Gagal Generate SID");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return startwa;
    }

//    public boolean validateComplete(UpdateStatusParam param) throws SQLException, JSONException {
//        boolean complete = false;
//        // Get Params
//        JSONObject params = getParams(param.getParent());
//        String productname = (params.get("productname") == null ? "" : params.get("productname").toString());
//        String worktype = (params.get("worktype") == null ? "" : params.get("worktype").toString());
//
////        if ("WFM".equals(worktype) && Arrays.asList(listProduct).contains(productname) && getTaskattributeValue(param.getWonum(), "APPROVAL") != "REJECTED") {
////
////        }
//    }
}
