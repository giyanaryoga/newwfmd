/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;

/**
 *
 * @author ASUS
 */
public class SetWifi6FlagDao {

    private int isWifi6(String apModel) {
        int result = 0;
        String[] apmodellist = {"AP_CISCO_C9130AXE-F", "AP_CISCO_C9105AXE-F", "AP_CISCO_C9124AXE-F", "C9105AXE-F", "C9124AXE-F", "C9130AXE-F", "C9105AXI-F"};
        if (Arrays.asList(apmodellist).contains(apModel)) {
            result = 1;
        } else {
            result = 0;
        }
        return result;
    }

    private int isWifi6hw(String apModel) {
        int result = 0;
        String apmodel = "AirEngine5761-11";
        if (apmodel.equals(apModel)) {
            result = 1;
        } else {
            result = 0;
        }
        return result;
    }

    private boolean updatewospecvalue(String attr_name, String value, String wonum) {
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE APP_FD_WORKORDERSPEC\n"
                + "SET c_value =\n"
                + "  CASE c_assetattrid\n"
                + "    WHEN " + attr_name + " THEN " + value + "\n"
                + "  END\n"
                + "WHERE c_wonum = ?\n"
                + "AND c_assetattrid IN (" + attr_name + ")";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, wonum);

            int exe = ps.executeUpdate();

            if (exe > 0) {
                result = true;
                LogUtil.info(getClass().getName(), "Attribute " + attr_name + " updated to " + value);
            }

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return result;
    }

    private String getDetailactcode(String wonum) {
        String resultObj = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_detailactcode FROM app_fd_workorder WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultObj = rs.getString("c_detailactcode");
                LogUtil.info(this.getClass().getName(), "Detailactcode : " + resultObj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

    public void setWifi6Flag(String value, String attrid, String wonum) {
        String[] listTask = {"Install AP", "Install AP MESH"};

        if (!value.equals("None") && !value.equals("") && attrid.equals("AP_MODEL")) {
            if (Arrays.asList(listTask).contains(getDetailactcode(wonum))) {
                String apModel = value;
                if (isWifi6(apModel) == 1) {
                    updatewospecvalue("SERVICE_TYPE", "WIFI6", wonum);
                } else if (isWifi6hw(apModel) == 1) {
                    updatewospecvalue("SERVICE_TYPE", "WIFI6HW", wonum);
                } else if (isWifi6(apModel) == 0) {
                    updatewospecvalue("SERVICE_TYPE", "WIFI", wonum);
                }
            }
        }
    }

}
