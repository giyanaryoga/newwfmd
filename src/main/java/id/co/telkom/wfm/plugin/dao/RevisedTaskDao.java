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
import org.joget.commons.util.UuidGenerator;
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
    
    public void reviseTask(String parent){
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
    
    public JSONObject getTask(String task, String parent) throws SQLException {
        JSONObject activityProp = new JSONObject();
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
                .append(" c_detailactcode = ? AND ")
                .append(" c_parent = ? ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, task);
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activityProp.put("taskid", rs.getString("c_taskid"));
                activityProp.put("wonum", rs.getString("c_wonum"));
                activityProp.put("parent", rs.getString("c_parent"));
                activityProp.put("orgid", rs.getString("c_orgid"));
                activityProp.put("description", rs.getString("c_description"));
                activityProp.put("detailActCode", rs.getInt("c_detailactcode"));
                activityProp.put("actPlace", rs.getString("c_actplace"));
                activityProp.put("woSequence", rs.getInt("c_wosequence"));
                activityProp.put("correlation", rs.getInt("c_correlation"));
                activityProp.put("ownerGroup", rs.getInt("c_ownergroup"));
                activityProp.put("siteid", rs.getInt("c_siteid"));
                activityProp.put("woClass", rs.getInt("c_woclass"));
                activityProp.put("workType", rs.getInt("c_worktype"));
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
    
    public String getTaskName(String parent) throws SQLException {
        String taskName = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_itemname FROM app_fd_ossitem WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
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

    public void generateActivityTask(String parent, String siteId, String correlationId, String ownerGroup, JSONObject taskObj) throws SQLException {
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
//            String query = "SELECT c_description, c_sequence, c_actplace, c_classstructureid FROM app_fd_detailactivity WHERE c_activity = ? AND c_sequence = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(insert.toString());){
            ps.setString(1, UuidGenerator.getInstance().getUuid());
            ps.setTimestamp(2, getTimeStamp());
            ps.setString(3, taskObj.get("parent").toString());
            ps.setString(4, taskObj.get("wonum").toString());
            ps.setString(5, taskObj.get("activity").toString()); //activity
            ps.setString(6, taskObj.get("description").toString());   //activity
            ps.setString(7, taskObj.get("sequence").toString());
            ps.setString(8, taskObj.get("actplace").toString());
            ps.setString(9, taskObj.get("status").toString());
            ps.setString(10, "NEW");
            ps.setString(11, taskObj.get("orgid").toString());     
            ps.setString(12, taskObj.get("siteid").toString());
            ps.setString(13, taskObj.get("workType").toString());
            ps.setString(14, taskObj.get("woClass").toString());       
            ps.setString(15, taskObj.get("taskid").toString());
            ps.setString(16, taskObj.get("correlation").toString());    
            ps.setString(17, taskObj.get("ownerGroup").toString());
            
            int exe = ps.executeUpdate();
            //Checking insert status
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "'" + taskObj.get("description") + "' generated as task");
            }
        } catch(SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
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
    
    public boolean checkAttrValue(String attrValue, String wonum) throws SQLException {
        boolean isTrue = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_value FROM app_fd_workorderspec WHERE c_assetattrid = ? AND c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, attrValue);
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
//            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activityProp.put("taskid", rs.getString("c_taskid"));
                activityProp.put("wonum", rs.getString("c_wonum"));
                activityProp.put("parent", rs.getString("c_parent"));
                activityProp.put("orgid", rs.getString("c_orgid"));
                activityProp.put("description", rs.getString("c_description"));
                activityProp.put("detailActCode", rs.getInt("c_detailactcode"));
                activityProp.put("actPlace", rs.getString("c_actplace"));
                activityProp.put("woSequence", rs.getInt("c_wosequence"));
                activityProp.put("correlation", rs.getInt("c_correlation"));
                activityProp.put("ownerGroup", rs.getInt("c_ownergroup"));
                activityProp.put("siteid", rs.getInt("c_siteid"));
                activityProp.put("woClass", rs.getInt("c_woclass"));
                activityProp.put("workType", rs.getInt("c_worktype"));
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

    public void GenerateTaskAttribute(String activity, String wonum, String orderId) throws SQLException {
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_classspecid, ")
                .append(" c_orgid, ")
                .append(" c_assetattrid, ")
                .append(" c_description, ")
                .append(" c_sequence, ")
                .append(" c_readonly, ")
                .append(" c_isrequired, ")
                .append(" c_isshared ")
                .append(" FROM app_fd_classspec WHERE ")
                .append(" c_activity = ? ");
        
        StringBuilder insert = new StringBuilder();
        insert
                .append(" INSERT INTO app_fd_workorderspec ")
                .append(" ( ")
                //TEMPLATE CONFIGURATION
                .append(" id, dateCreated, createdBy, createdByName,  ")
                //TASK ATTRIBUTE
                .append(" c_wonum, c_assetattrid, c_orgid, c_classspecid, c_orderid, c_displaysequence, ")
                //PERMISSION
                .append(" c_readonly, c_isrequired, c_isshared ")
                .append(" ) ")
                .append(" VALUES ")
                .append(" ( ")
                //VALUES TEMPLATE CONFIGURATION
                .append(" ?, ?, 'admin', 'Admin admin', ")
                //VALUES TASK ATTRIBUTE
                .append(" ?, ?, ?, ?, ?, ?, ")
                //VALUES PERMISSION
                .append(" ?, ?, ? ")
                .append(" ) ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try(Connection con = ds.getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            LogUtil.info(getClass().getName(), "'start' auto commit state: " + oldAutoCommit);
            con.setAutoCommit(false);
            try(PreparedStatement ps = con.prepareStatement(query.toString());
                PreparedStatement psInsert = con.prepareStatement(insert.toString())) {
                    ps.setString(1, activity);
                    ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    psInsert.setString(1, UuidGenerator.getInstance().getUuid());
                    psInsert.setTimestamp(2, getTimeStamp());
                    psInsert.setString(3, wonum);
                    psInsert.setString(4, rs.getString("c_description"));
                    psInsert.setString(5, rs.getString("c_orgid"));
                    psInsert.setString(6, rs.getString("c_classspecid"));
                    psInsert.setString(7, orderId);
                    psInsert.setString(8, rs.getString("c_sequence"));
                    psInsert.setString(9, rs.getString("c_readonly"));
                    psInsert.setString(10, rs.getString("c_isrequired"));
                    psInsert.setString(11, rs.getString("c_isshared"));
                    psInsert.addBatch();
                }
                int[] exe = psInsert.executeBatch();
                if (exe.length > 0) {
                    LogUtil.info(getClass().getName(), "Success generated task attributes, for " + activity);
                }
                con.commit();
            } catch(SQLException e) {
                LogUtil.error(getClass().getName(), e, "Trace Error Here: " + e.getMessage());
                con.rollback();
            } finally {
                con.setAutoCommit(oldAutoCommit);
            }
        } catch(SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace Error Here: " + e.getMessage());
        }
    }
}
