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
        Timestamp ts =  Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
    
//    public boolean getApiAttribute(String apiId, String apiKey) {
//        boolean  isAuthSuccess = false;
//        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT c_api_id, c_api_key FROM app_fd_api_wfm WHERE c_use_of_api = 'mystaff_integration'";
//        try(Connection con = ds.getConnection();
//            PreparedStatement ps = con.prepareStatement(query)) {
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                if (apiId.equals(rs.getString("c_api_id")) && apiKey.equals(rs.getString("c_api_key"))) {
//                    isAuthSuccess = true;
//                } else {
//                    isAuthSuccess = false;
//                }
//            }
//        } catch(SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
//        }
//        return isAuthSuccess;
//    }
    
//    public JSONObject getTask(String wonum) throws SQLException {
//        JSONObject activityProp = new JSONObject();
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT c_taskid, c_wosequence, c_detailactcode, c_description, c_parent  FROM app_fd_workorder WHERE c_wonum = ?";
//        try (Connection con = ds.getConnection();
//                PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, wonum);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                activityProp.put("taskid", rs.getInt("c_taskid"));
//                activityProp.put("wosequence", rs.getString("c_wosequence"));
//                activityProp.put("detailactcode", rs.getString("c_detailactcode"));
//                activityProp.put("description", rs.getString("c_description"));
//                activityProp.put("parent", rs.getString("c_parent"));
//            } else {
//                activityProp = null;
//            }
//        } catch (Exception e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
//        } finally {
//            ds.getConnection().close();
//        }
//        return activityProp;
//    }
    
    public boolean updateAttributeMyStaff(String wonum, String siteId, String assetAttrId, String value, String changeBy, String modifiedBy, String changeDate) throws SQLException {
        boolean taskUpdated = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
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
        
        try(Connection con = ds.getConnection();
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
            if (exe > 0){
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
    
    public JSONArray getAttrWoAttribute(String wonum) throws SQLException {
        JSONArray woAttr = new JSONArray();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_attr_name, c_attr_value FROM app_fd_workorderattribute WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                String woAttrName = rs.getString("c_attr_name");
                String woAttrValue = rs.getString("c_attr_value");
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
    
    public JSONArray getTaskAttribute(String parent) throws SQLException {
        JSONArray woAttr = new JSONArray();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value FROM app_fd_workorderspec WHERE c_parent = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                String taskAttrName = rs.getString("c_assetattrid");
                String taskAttrValue = rs.getString("c_value");
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
    
    public boolean updateValueTaskAttributeFromWorkorderAttr(String parent, String attrName, String attrValue){
        boolean updateValue = false;    
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");// change 03
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
                    if (ps != null)
                        ps.close();
                } catch (Throwable throwable) {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null)
                    con.close();
            } catch (Throwable throwable) {
                try {
                    if (con != null)
                        con.close();
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
}
