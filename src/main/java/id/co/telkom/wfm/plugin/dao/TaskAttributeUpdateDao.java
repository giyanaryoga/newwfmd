/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class TaskAttributeUpdateDao {
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

    public boolean updateAttributeMyStaff(String wonum, String siteId, String assetAttrId, String value, String changeBy, String modifiedBy, String changeDate) throws SQLException {
        boolean taskUpdated = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append(" UPDATE app_fd_workorderspec SET ")
                .append(" c_value = ?, ")
                .append(" c_changeby = ?, ")
                .append(" c_changedate = ?, ")
                .append(" c_siteid = ?, ")
                .append(" datemodified = ?, ")
                .append(" modifiedby = ? ")
                .append(" WHERE c_wonum = ? ")
                .append(" AND ")
                .append(" c_assetattrid = ? ");

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, changeBy);
            ps.setString(4, changeDate);
            ps.setString(5, siteId);
            ps.setTimestamp(6, getTimeStamp());
            ps.setString(7, modifiedBy);
            ps.setString(8, wonum);
            ps.setString(9, assetAttrId);

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
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return activity;
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
                LogUtil.info(getClass().getName(), parent + " | Assetattrid mandatory update to:  " + assetattrid);
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
                LogUtil.info(getClass().getName(), wonum + " | Assetattrid mandatory update to:  " + assetattrid);
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
                LogUtil.info(getClass().getName(), wonum + " | Assetattrid mandatory update to:  " + assetattrid);
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
                LogUtil.info(getClass().getName(), parent + " | Assetattrid mandatory update to:  " + assetattrid);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

//    public void updateTaskAttrView1(String wonum, String assetattrid, int isview) throws SQLException {
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        StringBuilder update = new StringBuilder();
//        update
//                .append("UPDATE app_fd_workorderspec SET ")
//                .append("c_isview = ?, ")
//                .append("datemodified = ? ")
//                .append("WHERE ")
//                .append("c_wonum = ?")
//                .append("AND c_assetattrid = ?");
//        try (Connection con = ds.getConnection();
//                PreparedStatement ps = con.prepareStatement(update.toString())) {
//            ps.setInt(1, isview);
//            ps.setTimestamp(2, getTimeStamp());
//            ps.setString(3, wonum);
//            ps.setString(4, assetattrid);
//            int exe = ps.executeUpdate();
//            if (exe > 0) {
//                LogUtil.info(getClass().getName(), wonum + " | Assetattrid mandatory update to:  " + assetattrid);
//            }
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
//        } finally {
//            ds.getConnection().close();
//        }
//    }

    public void updateWO(String table, String setvalue, String condition) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "UPDATE "+table+" SET "+setvalue+" WHERE "+condition;
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), " Update WO Activity , Query :   " + query);
            }else{
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
        String query = "INSERT INTO "+table+" ("+columns+") VALUES ("+values+")";
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " Insert WO  , Query :   " + query);
            }else{
                LogUtil.info(getClass().getName(), wonum + " Insert WO  FAILED, Query:  " + query);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    public void updateTKDeviceAttribute(String wonum, String setvalue, String condition) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "UPDATE app_fd_workorder SET "+setvalue+" WHERE c_wonum = ? "+condition;
        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " Update TK Device Attribute , Query :   " + query);
            }else{
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
            if (rs.next()) {
                value = rs.getString("c_description");
            }
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
                + "WHEN 'STP_NETWORKLOCATION' THEN ? "
                + "ELSE 'Missing' END "
                + "WHERE c_parent = ? "
                + "AND c_assetattrid IN ('STP_ID', 'STP_SPECIFICATION', 'STP_NETWORKLOCATION')";

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
}
