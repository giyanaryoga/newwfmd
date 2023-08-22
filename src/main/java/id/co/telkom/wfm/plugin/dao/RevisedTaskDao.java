/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.RevisedTask;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class RevisedTaskDao {
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"); 
        Timestamp ts =  Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
    
    public void reviseTask(String parent) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_wfmdoctype = ?, datemodified = ? WHERE c_parent = ?";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, "REVISED");
                    ps.setTimestamp(2, getTimeStamp());
                    ps.setString(3, parent);
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) 
                        LogUtil.info(getClass().getName(), "Older activity task has been revised, will be deactivated task");
                    if (ps != null)
                        ps.close();
                } catch (SQLException throwable) {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (SQLException throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null)
                    con.close();
            } catch (SQLException throwable) {
                try {
                    if (con != null)
                        con.close();
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }
    
    public void reviseTaskNonConn_reviewOrder(String parent) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_wfmdoctype = ?, datemodified = ? WHERE c_parent = ? "
                + "AND c_detailactcode NOT IN ('REVIEW_ORDER')";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, "REVISED");
                    ps.setTimestamp(2, getTimeStamp());
                    ps.setString(3, parent);
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) 
                        LogUtil.info(getClass().getName(), "Next activity task has been revised, will be deactivated task");
                    if (ps != null)
                        ps.close();
                } catch (SQLException throwable) {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (SQLException throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null)
                    con.close();
            } catch (SQLException throwable) {
                try {
                    if (con != null)
                        con.close();
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }
    
    public void reviseTaskNonConn_toReviewOrder(String parent) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_wfmdoctype = ?, datemodified = ? WHERE c_parent = ? AND c_wfmdoctype = 'NEW'"
                + " AND c_detailactcode IN ('REVIEW_ORDER', 'Shipment_Delivery', 'Activate_Service', 'Upload_Berita_Acara', 'Approval_Project_Management')";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, "REVISED");
                    ps.setTimestamp(2, getTimeStamp());
                    ps.setString(3, parent);
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) 
                        LogUtil.info(getClass().getName(), "Next activity task has been revised, will be deactivated task");
                    if (ps != null)
                        ps.close();
                } catch (SQLException throwable) {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (SQLException throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null)
                    con.close();
            } catch (SQLException throwable) {
                try {
                    if (con != null)
                        con.close();
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }

    public void reviseTaskNonConn_toShipmentDelivery(String parent) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_wfmdoctype = ?, datemodified = ? WHERE c_parent = ? AND c_wfmdoctype = 'NEW'"
                + " AND c_detailactcode IN ('Shipment_Delivery', 'Activate_Service', 'Upload_Berita_Acara', 'Approval_Project_Management')";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, "REVISED");
                    ps.setTimestamp(2, getTimeStamp());
                    ps.setString(3, parent);
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) 
                        LogUtil.info(getClass().getName(), "Next activity task has been revised, will be deactivated task");
                    if (ps != null)
                        ps.close();
                } catch (SQLException throwable) {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (SQLException throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null)
                    con.close();
            } catch (SQLException throwable) {
                try {
                    if (con != null)
                        con.close();
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }
    
    public void reviseTaskNonConn_toActivateService(String parent) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_wfmdoctype = ?, datemodified = ? WHERE c_parent = ? AND c_wfmdoctype = 'NEW'"
                + " AND c_detailactcode IN ('Activate_Service', 'Upload_Berita_Acara', 'Approval_Project_Management')";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, "REVISED");
                    ps.setTimestamp(2, getTimeStamp());
                    ps.setString(3, parent);
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) 
                        LogUtil.info(getClass().getName(), "Next activity task has been revised, will be deactivated task");
                    if (ps != null)
                        ps.close();
                } catch (SQLException throwable) {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (SQLException throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null)
                    con.close();
            } catch (SQLException throwable) {
                try {
                    if (con != null)
                        con.close();
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }
    
    public void reviseTaskNonConn_toUploadBA(String parent) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_wfmdoctype = ?, datemodified = ? WHERE c_parent = ? AND c_wfmdoctype = 'NEW'"
                + " AND c_detailactcode IN ('Upload_Berita_Acara', 'Approval_Project_Management')";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, "REVISED");
                    ps.setTimestamp(2, getTimeStamp());
                    ps.setString(3, parent);
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) 
                        LogUtil.info(getClass().getName(), "Next activity task has been revised, will be deactivated task");
                    if (ps != null)
                        ps.close();
                } catch (SQLException throwable) {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (SQLException throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null)
                    con.close();
            } catch (SQLException throwable) {
                try {
                    if (con != null)
                        con.close();
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }
    
    public String getDomainId(String wonum) throws SQLException {
        String domainid = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_domainid FROM app_fd_workorderspec WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                domainid = rs.getString("c_domainid");
            }   
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return domainid;
    }
    
    public JSONArray getValueDomain(String domainid) throws SQLException {
        JSONArray getDomain = new JSONArray();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_description, c_value FROM app_fd_alndomain WHERE c_domainid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, domainid);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject getObj = new JSONObject();
                getObj.put("description", rs.getString("c_description"));
                getObj.put("value", rs.getString("c_value"));
                getDomain.add(getObj);
                LogUtil.info(getClass().getName(), "ALN Domain = " +getDomain);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return getDomain;
    }

    public boolean checkAttrName(String attrName, String wonum) throws SQLException {
        boolean isTrue = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid FROM app_fd_workorderspec WHERE c_assetattrid = ? AND c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, attrName);
            ps.setString(2, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                isTrue = true;
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return isTrue;
    }
    
    public boolean checkAttrValue(String attrName, String wonum) throws SQLException {
        boolean isTrue = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_value FROM app_fd_workorderspec WHERE c_assetattrid = ? AND c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, attrName);
            ps.setString(2, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                isTrue = true;
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return isTrue;
    }
    
    public JSONArray getTask(String parent) throws SQLException {
        JSONArray activity = new JSONArray();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_taskid, ")
                .append(" c_wonum, ")
                .append(" c_parent, ")
                .append(" c_orgid, ")
                .append(" c_detailactcode, ")
                .append(" c_description, ")
                .append(" c_actplace, ")
                .append(" c_wosequence, ")
                .append(" c_correlation, ")
                .append(" c_ownergroup, ")
                .append(" c_siteid, ")
                .append(" c_woclass, ")
                .append(" c_worktype, ")
                .append(" c_estdur ")
                .append(" FROM app_fd_workorder WHERE ")
                .append(" c_woclass = 'ACTIVITY' AND c_wfmdoctype = 'NEW' AND ")
                .append(" c_parent = ? ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject activityProp = new JSONObject();
                activityProp.put("taskid", rs.getInt("c_taskid"));
                activityProp.put("wonum", rs.getString("c_wonum"));
                activityProp.put("parent", rs.getString("c_parent"));
                activityProp.put("orgid", rs.getString("c_orgid"));
                activityProp.put("description", rs.getString("c_description"));
                activityProp.put("detailActCode", rs.getString("c_detailactcode"));
                activityProp.put("actPlace", rs.getString("c_actplace"));
                activityProp.put("woSequence", rs.getInt("c_wosequence"));
                activityProp.put("correlation", rs.getString("c_correlation"));
                activityProp.put("ownerGroup", rs.getString("c_ownergroup"));
                activityProp.put("siteid", rs.getString("c_siteid"));
                activityProp.put("woClass", rs.getString("c_woclass"));
                activityProp.put("workType", rs.getString("c_worktype"));
                activityProp.put("duration", rs.getInt("c_estdur"));
                activity.add(activityProp);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close(); 
        }
        return activity;
    }
    
    public JSONArray getTaskRevised(String parent) throws SQLException {
        JSONArray activity = new JSONArray();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_taskid, ")
                .append(" c_wonum, ")
                .append(" c_parent, ")
                .append(" c_orgid, ")
                .append(" c_detailactcode, ")
                .append(" c_description, ")
                .append(" c_actplace, ")
                .append(" c_wosequence, ")
                .append(" c_correlation, ")
                .append(" c_ownergroup, ")
                .append(" c_siteid, ")
                .append(" c_woclass, ")
                .append(" c_worktype, ")
                .append(" c_estdur ")
                .append(" FROM app_fd_workorder WHERE ")
                .append(" c_woclass = 'ACTIVITY' AND c_wfmdoctype = 'REVISED' AND ")
                .append(" c_parent = ? ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject activityProp = new JSONObject();
                activityProp.put("taskid", rs.getInt("c_taskid"));
                activityProp.put("wonum", rs.getString("c_wonum"));
                activityProp.put("parent", rs.getString("c_parent"));
                activityProp.put("orgid", rs.getString("c_orgid"));
                activityProp.put("description", rs.getString("c_description"));
                activityProp.put("detailActCode", rs.getString("c_detailactcode"));
                activityProp.put("actPlace", rs.getString("c_actplace"));
                activityProp.put("woSequence", rs.getInt("c_wosequence"));
                activityProp.put("correlation", rs.getString("c_correlation"));
                activityProp.put("ownerGroup", rs.getString("c_ownergroup"));
                activityProp.put("siteid", rs.getString("c_siteid"));
                activityProp.put("woClass", rs.getString("c_woclass"));
                activityProp.put("workType", rs.getString("c_worktype"));
                activityProp.put("duration", rs.getInt("c_estdur"));
                activity.add(activityProp);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close(); 
        }
        return activity;
    }

    
    public String getTaskName(String wonum) throws SQLException {
        String taskName = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_itemname FROM app_fd_ossitem WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                taskName = rs.getString("c_itemname");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return taskName;
    }
    
    public Integer getTaskId(String wonum) throws SQLException {
        int taskid = 0;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_taskid FROM app_fd_workorder WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                taskid = rs.getInt("c_taskid");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return taskid;
    }

    public void generateActivityTask(String parent) throws SQLException {
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_taskid, ")
                .append(" c_wonum, ")
                .append(" c_parent, ")
                .append(" c_orgid, ")
                .append(" c_detailactcode, ")
                .append(" c_description, ")
                .append(" c_actplace, ")
                .append(" c_wosequence, ")
                .append(" c_correlation, ")
                .append(" c_ownergroup, ")
                .append(" c_siteid, ")
                .append(" c_woclass, ")
                .append(" c_worktype ")
                .append(" FROM app_fd_workorder WHERE ")
                .append(" c_woclass = 'ACTIVITY' AND ")
                .append(" c_parent = ? ");
        
        StringBuilder insert = new StringBuilder();
        insert
                .append(" INSERT INTO app_fd_workorder ( ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" c_parent, ")
                .append(" c_wonum, ")
                .append(" c_detailactcode, ")
                .append(" c_description, ")
                .append(" c_wosequence, ")
                .append(" c_actplace, ")
                .append(" c_status, ")
                .append(" c_wfmdoctype, ")
                .append(" c_orgid, ")
                .append(" c_siteId, ")
                .append(" c_worktype, ")
                .append(" c_woclass, ")
                .append(" c_taskid, ")
                .append(" c_correlation, ")
                .append(" c_ownergroup ")
                .append(" ) ")
                .append(" VALUES ( ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");
            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps1 = con.prepareStatement(query.toString());
            PreparedStatement ps2 = con.prepareStatement(insert.toString());){
                ps1.setString(1, parent);
                ResultSet rs = ps1.executeQuery();
                while (rs.next()) {
                    ps2.setString(1, UuidGenerator.getInstance().getUuid());
                    ps2.setTimestamp(2, getTimeStamp());
                    ps2.setString(3, parent);
                    ps2.setString(4, rs.getString("c_wonum"));
                    ps2.setString(5, rs.getString("c_detailactcode")); //activity
                    ps2.setString(6, rs.getString("c_description"));   //activity
                    ps2.setInt(7, rs.getInt("c_wosequence"));
                    ps2.setString(8, rs.getString("c_actplace"));
                    if (rs.getInt("c_taskid") == 10) {
                        ps2.setString(9, "LABASSIGN");
                    } else {
                        ps2.setString(9, "APPR");
                    }
                    ps2.setString(10, "NEW");
                    ps2.setString(11, rs.getString("c_orgid"));     
                    ps2.setString(12, rs.getString("c_siteid"));
                    ps2.setString(13, rs.getString("c_worktype"));
                    ps2.setString(14, rs.getString("c_woclass"));       
                    ps2.setInt(15, rs.getInt("c_taskid"));
                    ps2.setString(16, rs.getString("c_correlation"));  
                    ps2.setString(17, rs.getString("c_ownergroup"));
                    ps2.addBatch();
                }

                int[] exe = ps2.executeBatch();
                //Checking insert status
                if (exe.length > 0) {
                    LogUtil.info(getClass().getName(), "Success generate new task!");
                }
        } catch(SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void generateActivityTaskNonConn(String parent, String activity) throws SQLException {
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_taskid, ")
                .append(" c_wonum, ")
                .append(" c_parent, ")
                .append(" c_orgid, ")
                .append(" c_detailactcode, ")
                .append(" c_description, ")
                .append(" c_actplace, ")
                .append(" c_wosequence, ")
                .append(" c_correlation, ")
                .append(" c_ownergroup, ")
                .append(" c_siteid, ")
                .append(" c_woclass, ")
                .append(" c_worktype ")
                .append(" FROM app_fd_workorder WHERE ")
                .append(" c_woclass = 'ACTIVITY' AND ")
                .append(" c_parent = ? AND c_detailactcode = ?");
        
        StringBuilder insert = new StringBuilder();
        insert
                .append(" INSERT INTO app_fd_workorder ( ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" c_parent, ")
                .append(" c_wonum, ")
                .append(" c_detailactcode, ")
                .append(" c_description, ")
                .append(" c_wosequence, ")
                .append(" c_actplace, ")
                .append(" c_status, ")
                .append(" c_wfmdoctype, ")
                .append(" c_orgid, ")
                .append(" c_siteId, ")
                .append(" c_worktype, ")
                .append(" c_woclass, ")
                .append(" c_taskid, ")
                .append(" c_correlation, ")
                .append(" c_ownergroup ")
                .append(" ) ")
                .append(" VALUES ( ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");
            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps1 = con.prepareStatement(query.toString());
            PreparedStatement ps2 = con.prepareStatement(insert.toString());){
                ps1.setString(1, parent);
                ps1.setString(2, activity);
                ResultSet rs = ps1.executeQuery();
                while (rs.next()) {
                    ps2.setString(1, UuidGenerator.getInstance().getUuid());
                    ps2.setTimestamp(2, getTimeStamp());
                    ps2.setString(3, parent);
                    ps2.setString(4, rs.getString("c_wonum"));
                    ps2.setString(5, rs.getString("c_detailactcode")); //activity
                    ps2.setString(6, rs.getString("c_description"));   //activity
                    ps2.setInt(7, rs.getInt("c_wosequence"));
                    ps2.setString(8, rs.getString("c_actplace"));
                    if (rs.getInt("c_taskid") == 10) {
                        ps2.setString(9, "LABASSIGN");
                    } else {
                        ps2.setString(9, "APPR");
                    }
                    ps2.setString(10, "NEW");
                    ps2.setString(11, rs.getString("c_orgid"));     
                    ps2.setString(12, rs.getString("c_siteid"));
                    ps2.setString(13, rs.getString("c_worktype"));
                    ps2.setString(14, rs.getString("c_woclass"));       
                    ps2.setInt(15, rs.getInt("c_taskid"));
                    ps2.setString(16, rs.getString("c_correlation"));  
                    ps2.setString(17, rs.getString("c_ownergroup"));
                }

                int exe = ps2.executeUpdate();
                //Checking insert status
                if (exe > 0) {
                    LogUtil.info(getClass().getName(), "Success generate new task!");
                }
        } catch(SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public JSONObject getTaskAttr(String wonum) throws SQLException {
        JSONObject activityProp = new JSONObject();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_attr_name, ")
                .append(" c_attr_value, ")
                .append(" c_wonum ")
                .append(" FROM app_fd_ossitemattribute WHERE ")
                .append(" c_wonum = ? ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activityProp.put("wonum", rs.getString("c_wonum"));
                activityProp.put("attrName", rs.getString("c_attr_name"));
                activityProp.put("attrValue", rs.getString("c_attr_value"));
            } else {
                activityProp = null;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close(); 
        }
        return activityProp;
    }
    
    public void updateNullAttrValue(String assetAttrId, String wonum) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorderspec SET c_value = ?, datemodified = ? WHERE c_wonum = ? AND c_assetattrid = ?";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, "");
                    ps.setTimestamp(2, getTimeStamp());
                    ps.setString(3, wonum);
                    ps.setString(4, assetAttrId);
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) 
                        LogUtil.info(getClass().getName(), "Older task attribute value is null");
                    if (ps != null)
                        ps.close();
                } catch (SQLException throwable) {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (SQLException throwable1) {
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
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }
}