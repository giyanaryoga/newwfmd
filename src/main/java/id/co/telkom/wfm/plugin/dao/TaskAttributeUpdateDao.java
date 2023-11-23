/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.telkom.wfm.plugin.kafka.ResponseKafka;
import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.model.MyStaffParam;
import id.co.telkom.wfm.plugin.util.ConnUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class TaskAttributeUpdateDao {

    // FormatLogIntegrationHistory insertIntegrationHistory = new FormatLogIntegrationHistory();
    ResponseKafka responseKafka = new ResponseKafka();
    // get URL
    ConnUtil connUtil = new ConnUtil();
    APIConfig apiConfig = new APIConfig();

    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        Timestamp ts = Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }

    public JSONArray getAttrWoAttribute(String wonum) throws SQLException {
        JSONArray woAttr = new JSONArray();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_attr_name, c_attr_value FROM app_fd_workorderattribute WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                String woAttrName = rs.getString("c_attr_name");
                String woAttrValue = (rs.getString("c_attr_value") == null ? "" : rs.getString("c_attr_value"));
                obj.put("attr_name", woAttrName.toUpperCase());
                obj.put("attr_value", woAttrValue);
                woAttr.add(obj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return woAttr;
    }

    public JSONArray getTaskAttributeParent(String parent) throws SQLException {
        JSONArray woAttr = new JSONArray();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value FROM app_fd_workorderspec WHERE c_parent = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                String taskAttrName = rs.getString("c_assetattrid");
                String taskAttrValue = (rs.getString("c_value") == null ? "" : rs.getString("c_value"));
                obj.put("task_attr_name", taskAttrName);
                obj.put("task_attr_value", taskAttrValue);
                woAttr.add(obj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return woAttr;
    }

    public JSONArray getClassspec(String classstructureid, String condition) throws SQLException {
        JSONArray woAttr = new JSONArray();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_defaultvalue, c_classstructureid, c_sequence, c_mandatory, c_objectname, c_classspecid, c_orgid, c_readonly, c_samplevalue FROM APP_FD_CLASSSPEC WHERE C_CLASSSTRUCTUREID=? AND c_objectname='WOACTIVITY' AND " + condition;
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, classstructureid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                String c_assetattrid = rs.getString("c_assetattrid");
                String c_defaultvalue = (rs.getString("c_defaultvalue") == null ? "" : rs.getString("c_value"));
                String c_classstructureid = (rs.getString("c_classstructureid") == null ? "" : rs.getString("c_classstructureid"));
                String c_sequence = (rs.getString("c_sequence") == null ? "" : rs.getString("c_sequence"));
                String c_mandatory = (rs.getString("c_mandatory") == null ? "" : rs.getString("c_mandatory"));
                String c_objectname = (rs.getString("c_objectname") == null ? "" : rs.getString("c_objectname"));
                String c_classspecid = (rs.getString("c_classspecid") == null ? "" : rs.getString("c_classspecid"));
                String c_orgid = (rs.getString("c_orgid") == null ? "" : rs.getString("c_orgid"));
                String c_readonly = (rs.getString("c_readonly") == null ? "" : rs.getString("c_readonly"));
                String c_samplevalue = (rs.getString("c_samplevalue") == null ? "" : rs.getString("c_samplevalue"));
                obj.put("c_assetattrid", c_assetattrid);
                obj.put("c_defaultvalue", c_defaultvalue);
                obj.put("c_classstructureid", c_classstructureid);
                obj.put("c_sequence", c_sequence);
                obj.put("c_mandatory", c_mandatory);
                obj.put("c_classspecid", c_classspecid);
                obj.put("c_orgid", c_orgid);
                obj.put("c_objectname", c_objectname);
                obj.put("c_readonly", c_readonly);
                obj.put("c_samplevalue", c_samplevalue);
                woAttr.add(obj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return woAttr;
    }

    public JSONArray getTaskAttrWonum(String wonum) throws SQLException {
        JSONArray activityProp = new JSONArray();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_parent, c_wonum, c_assetattrid, c_value, c_mandatory FROM app_fd_workorderspec WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject attr = new JSONObject();
                attr.put("mandatory", rs.getInt("c_mandatory"));
                attr.put("assetattrid", rs.getString("c_assetattrid"));
                attr.put("value", rs.getString("c_value"));
                attr.put("parent", rs.getString("c_parent"));
                attr.put("wonum", rs.getString("c_wonum"));
                activityProp.add(attr);
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return activityProp;
    }

    public JSONObject getWorkzoneRegional(String workzone) throws SQLException {
        JSONObject workzoneObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_tk_subregion, c_tk_region FROM app_fd_workzone WHERE c_workzone = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                workzoneObj.put("subregion", rs.getString("c_tk_subregion"));
                workzoneObj.put("region", rs.getString("c_tk_region"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return workzoneObj;
    }

    public boolean updateAttributeMyStaff(MyStaffParam param) throws SQLException {
        boolean taskUpdated = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append(" UPDATE app_fd_workorderspec SET ")
                .append(" c_value = ?, ")
                .append(" c_changeby = ?, ")
                .append(" datemodified = sysdate, ")
                .append(" modifiedby = ? ")
                .append(" WHERE c_wonum = ? ")
                .append(" AND ")
                .append(" c_assetattrid = ? ")
                .append(" AND ")
                .append(" c_siteid = ? ");

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, param.getValue());
            ps.setString(2, param.getChangeBy());
            ps.setString(3, param.getModifiedBy());
            ps.setString(4, param.getWonum());
            ps.setString(5, param.getAssetAttrId());
            ps.setString(6, param.getSiteid());

            int exe = ps.executeUpdate();
            if (exe > 0) {
                taskUpdated = true;
                LogUtil.info(getClass().getName(), "update task attribute mystaff berhasil");
            } else {
                LogUtil.info(getClass().getName(), "update task attribute gagal");
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return taskUpdated;
    }

    public boolean updateValueTaskAttributeFromWorkorderAttr(String parent, String attrName, String attrValue) {
        boolean updateValue = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");// change 03
        StringBuilder update = new StringBuilder();
        update
                .append(" UPDATE app_fd_workorderspec SET ")
                .append(" c_alnvalue = ?, ")
                .append(" c_value = ?, ")
                .append(" dateModified = ? ")
                .append(" WHERE ")
                .append(" c_parent = ? ")
                .append(" AND ")
                .append(" c_assetattrid = ? ");
        // change 03
        try {
            Connection con = ds.getConnection();
            try {
                // change 03
                PreparedStatement ps = con.prepareStatement(update.toString());
                // change 03
                try {
                    ps.setString(1, attrValue);
                    ps.setString(2, attrValue);
                    ps.setTimestamp(3, getTimeStamp());
                    // change 03 where clause
                    ps.setString(4, parent);
                    ps.setString(5, attrName);
                    // change 03
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        updateValue = true;
//                        LogUtil.info(getClass().getName(), " Task Attribute updated to " + parent);
                    }
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Throwable throwable) {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null) {
                    con.close();
                }
            } catch (Throwable throwable) {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return updateValue;
    }

    public String getTaskAttrValue(String wonum, String assetattrid) throws SQLException {
        String value = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, assetattrid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                value = rs.getString("c_value");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return value;
    }
    public String getAttrValue(String wonum, String assetattrid) throws SQLException {
        String value = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, assetattrid);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                value = rs.getString("c_assetattrid");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return value;
    }

    public String getTaskAttrName(String wonum) throws SQLException {
        String assetattrid = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid FROM app_fd_workorderspec WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                assetattrid = rs.getString("c_value");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return assetattrid;
    }

    public String getActivity(String wonum) throws SQLException {
        String activity = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_detailactcode FROM app_fd_workorder WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activity = rs.getString("c_detailactcode");
                LogUtil.info(getClass().getName(), "Activity: " + activity);

            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return activity;
    }

    public String getClassStructureID(String wonum) throws SQLException {
        String c_classstrucuterid = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT C_CLASSSTRUCTUREID FROM app_fd_workorder WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                c_classstrucuterid = rs.getString("C_CLASSSTRUCTUREID");
                LogUtil.info(getClass().getName(), "C_CLASSSTRUCTUREID: " + c_classstrucuterid);

            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return c_classstrucuterid;
    }

    public String getWonumActivityTask(String parent, String activity) throws SQLException {
        String wonum = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_wonum FROM app_fd_workorder WHERE c_parent = ? AND c_detailactcode = ? AND c_woclass = 'ACTIVITY'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ps.setString(2, activity);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                wonum = rs.getString("c_wonum");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return wonum;
    }

    public String getProductName(String parent) throws SQLException {
        String productName = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_productname FROM app_fd_workorder WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                productName = rs.getString("c_productname");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return productName;
    }

    public String getWoAttrValue(String parent, String attrName) throws SQLException {
        String value = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_attr_value FROM app_fd_workorderattribute WHERE c_wonum = ? AND c_attr_name = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ps.setString(2, attrName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                value = rs.getString("c_attr_value");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return value;
    }

    public void updateMandatory(String wonum, String assetattrid, int mandatory) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append("UPDATE app_fd_workorderspec SET ")
                .append(" c_mandatory = ?, ")
                .append(" datemodified = ? ")
                .append(" WHERE ")
                .append(" c_wonum = ? ")
                .append(" AND c_assetattrid = ? ");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setInt(1, mandatory);
//            ps.setInt(2, mandatory);
            ps.setTimestamp(2, getTimeStamp());
            ps.setString(3, wonum);
            ps.setString(4, assetattrid);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Assetattrid mandatory update to:  " + assetattrid);
            } else {
                LogUtil.info(getClass().getName(), "Mandatory is not updated");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public void updateTaskValueParent(String parent, String assetattrid, String value) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append("UPDATE app_fd_workorderspec SET ")
                .append("c_value = ?, ")
                .append("datemodified = ? ")
                .append("WHERE ")
                .append("c_parent = ?")
                .append("AND c_assetattrid = ?");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, value);
            ps.setTimestamp(2, getTimeStamp());
            ps.setString(3, parent);
            ps.setString(4, assetattrid);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), parent + " | Updated task attribute parent where :  " + assetattrid);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public void updateTaskValue(String wonum, String assetattrid, String value) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append("UPDATE app_fd_workorderspec SET ")
                .append("c_value = ?, ")
                .append("datemodified = ? ")
                .append("WHERE ")
                .append("c_wonum = ?")
                .append("AND c_assetattrid = ?");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, value);
            ps.setTimestamp(2, getTimeStamp());
            ps.setString(3, wonum);
            ps.setString(4, assetattrid);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Assetattrid success updated");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public void updateStatusValue(String wonum, String assetattrid, String value, String detailactcode) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append("UPDATE app_fd_workorderspec SET ")
                .append("c_value = ?, ")
                .append("datemodified = ? ")
                .append("WHERE ")
                .append("c_wonum = ?")
                .append("AND c_detailactcode= ?")
                .append("AND c_assetattrid = ?");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, value);
            ps.setTimestamp(2, getTimeStamp());
            ps.setString(3, wonum);
            ps.setString(4, detailactcode);
            ps.setString(5, assetattrid);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Success updated task attribute from = "+detailactcode);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public void updateTaskAttrViewLike(String parent, String assetattrid, int isview) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append("UPDATE app_fd_workorderspec SET ")
                .append("c_isview = ?, ")
                .append("datemodified = ? ")
                .append("WHERE ")
                .append("c_parent = ?")
                .append("AND c_assetattrid like ?");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setInt(1, isview);
            ps.setTimestamp(2, getTimeStamp());
            ps.setString(3, parent);
            ps.setString(4, assetattrid);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), parent + " Updated task attribute Like  " + assetattrid);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public boolean checkData(String table, String condition) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT * FROM " + table + " WHERE " + condition;
        boolean status = false;
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            LogUtil.info(this.getClass().getName(), "Query : " + ps.toString());
            if (rs.next()) {
                status = true;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return status;
    }

    public void updateWO(String table, String setvalue, String condition) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "UPDATE " + table + " SET " + setvalue + " WHERE " + condition;
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), " Update WO Activity , Query :   " + query);
            } else {
                LogUtil.info(getClass().getName(), " Update WO Activity FAILED, Query:  " + query);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public void insertWO(String wonum, String table, String columns, String values) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "INSERT INTO " + table + " (" + columns + ") VALUES (" + values + ")";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " Insert WO  , Query :   " + query);
            } else {
                LogUtil.info(getClass().getName(), wonum + " Insert WO  FAILED, Query:  " + query);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public void deleteWO(String wonum, String table, String condition) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "DELETE FROM " + table + " WHERE " + condition;
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " Delete WO  , Query :   " + query);
            } else {
                LogUtil.info(getClass().getName(), wonum + " Delete WO  FAILED, Query:  " + query);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public void updateTKDeviceAttribute(String wonum, String setvalue, String condition) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "UPDATE app_fd_workorder SET " + setvalue + " WHERE c_wonum = ? " + condition;
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " Update TK Device Attribute , Query :   " + query);
            } else {
                LogUtil.info(getClass().getName(), wonum + " Update WO Activity FAILED, Query:  " + query);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public void deleteTaskAttrLike(String wonum, String assetattrid) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append("DELETE FROM app_fd_workorderspec ")
                .append("WHERE ")
                .append("c_parent = ?")
                .append("AND c_assetattrid LIKE ?")
                .append("AND c_assetattrid NOT IN ('STP_NETWORKLOCATION', 'STP_NETWORKLOCATION_LOV')");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, wonum);
            ps.setString(2, assetattrid);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Assetattrid delete:  " + assetattrid);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public void updateWoAttrView(String parent, String attrName, String attrValue) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append("UPDATE app_fd_workorderattribute SET ")
                .append("c_attr_value = ?, ")
                .append("datemodified = sysdate ")
                .append("WHERE ")
                .append("c_wonum = ?")
                .append("AND c_attr_name = ?");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, attrValue);
            ps.setString(2, parent);
            ps.setString(3, attrName);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), parent + " | Update workorder attribute:  " + attrName);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public String getTkdeviceAttrValue(String wonum, String attrname, String attrtype) throws SQLException {
        String value = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_description FROM app_fd_tk_deviceattribute WHERE c_ref_num = ? AND c_attr_name = ? AND c_attr_type = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, attrname);
            ps.setString(3, attrtype);
            ResultSet rs = ps.executeQuery();
            LogUtil.info(this.getClass().getName(), "Query : " + ps.toString());
            String status = "Not Found";
            if (rs.next()) {
                value = rs.getString("c_description");
                status = "Found";
            }
            LogUtil.info(this.getClass().getName(), "Data status: " + status);

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return value;
    }

    public boolean updateAttributeSTP(String parent, String stpid, String specification, String netLoc) throws SQLException {
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String updateQuery
                = "UPDATE app_fd_workorderspec "
                + "SET c_value = CASE c_assetattrid "
                + "WHEN 'STP_ID' THEN ? "
                + "WHEN 'STP_SPECIFICATION' THEN ? "
                + "WHEN 'STP_NETWORKLOCATION_LOV' THEN ? "
                + "ELSE 'Missing' END "
                + "WHERE c_parent = ? "
                + "AND c_assetattrid IN ('STP_ID', 'STP_SPECIFICATION', 'STP_NETWORKLOCATION_LOV')";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(updateQuery)) {
            ps.setString(1, stpid);
            ps.setString(2, specification);
            ps.setString(3, netLoc);
            ps.setString(4, parent);

            int exe = ps.executeUpdate();

            if (exe > 0) {
                result = true;
                LogUtil.info(getClass().getName(), "Attribute value updated to " + parent);
            }

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }

        return result;
    }

    public JSONObject getAssignment(String wonum) throws SQLException {
        JSONObject workzoneObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_laborcode, c_amcrew FROM app_fd_assignment WHERE c_wonum = ? AND c_status = 'ASSIGNED'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                workzoneObj.put("laborcode", rs.getString("c_laborcode"));
                workzoneObj.put("amcrew", rs.getString("c_amcrew"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return workzoneObj;
    }

    private String deleteTkDeviceattribute(String wonum) throws SQLException {
        String moveFirst = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String delete = "DELETE FROM app_fd_tk_deviceattribute WHERE c_ref_num = ? AND c_attr_name IN ('PE_PORTNAME', 'PE_KEY')";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(delete)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                moveFirst = "Deleted data";
                LogUtil.info(getClass().getName(), "Berhasil menghapus data");
            } else {
                LogUtil.info(getClass().getName(), "Gagal menghapus data");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return moveFirst;
    }

    private void insertToDeviceTable(String wonum, String name, String type, String description) throws Throwable {
        // Generate UUID
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO APP_FD_TK_DEVICEATTRIBUTE (ID, C_REF_NUM, C_ATTR_NAME, C_ATTR_TYPE, C_DESCRIPTION, DATECREATED) VALUES (?, ?, ?, ?, ?, SYSDATE)";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(insert)) {
            ps.setString(1, uuId);
            ps.setString(2, wonum);
            ps.setString(3, name);
            ps.setString(4, type);
            ps.setString(5, description);

            int exe = ps.executeUpdate();

            if (exe > 0) {
                LogUtil.info(this.getClass().getName(), "Berhasil menambahkan data");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public String getPEPorts(String wonum, String deviceName, String serviceType) throws MalformedURLException, IOException, Throwable {

        // temp message
        String message = "";
        try {
            apiConfig = connUtil.getApiParam("uimax_dev");

            // checking deviceName
            if (deviceName.equals("None")) {
                LogUtil.info(getClass().getName(), "Devicename is empty");
            } else if (!deviceName.isEmpty()) {
                String url = apiConfig.getUrl() + "api/device/portsByService?deviceName=" + deviceName + "&serviceType=" + serviceType + "&portPurpose=TRUNK&portStatus=ACTIVE";
                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                // set header
                con.setRequestMethod("GET");
                con.setRequestProperty("Accept", "application/json");
                int responseCode = con.getResponseCode();
                LogUtil.info(this.getClass().getName(), "\nSending 'GET' request to URL : " + url);
                LogUtil.info(this.getClass().getName(), "Response Code : " + responseCode);

                if (responseCode == 404) {
                    message = "PE PORT Not Found";
                    LogUtil.info(getClass().getName(), message);
                } else if (responseCode == 200) {
                    deleteTkDeviceattribute(wonum);

                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    LogUtil.info(this.getClass().getName(), "PE Name : " + response);
                    in.close();
                    // At this point, 'response' contains the JSON data as a string
                    String jsonData = response.toString();
                    // parse the JSON data using jackson
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(jsonData);

                    JsonNode portArray = jsonNode.get("port");

                    for (JsonNode portNode : portArray) {
                        String name = portNode.get("name").asText();
                        String key = portNode.get("key").asText();
                        LogUtil.info(getClass().getName(), "PE_PORTNAME : " + name);
                        LogUtil.info(getClass().getName(), "PE_KEY : " + key);
                        insertToDeviceTable(wonum, "PE_PORTNAME", "", name);
                        insertToDeviceTable(wonum, "PE_KEY", name, key);
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return message;
    }
}
