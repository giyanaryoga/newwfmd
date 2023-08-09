/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

//import id.co.telkom.wfm.plugin.model.ListAttributes;
//import id.co.telkom.wfm.plugin.model.ListDevice;
//import id.co.telkom.wfm.plugin.model.ListOssItem;
//import id.co.telkom.wfm.plugin.model.ListOssItemAttribute;
//import id.co.telkom.wfm.plugin.model.ListCpeValidate;
import id.co.telkom.wfm.plugin.model.ActivityTask;
import id.co.telkom.wfm.plugin.model.ListClassSpec;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class TestGenerateDao {
    public String apiId = "";
    public String apiKey = "";
    
    public void getApiAttribute (){
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_api_id, c_api_key FROM app_fd_api_wfm WHERE c_use_of_api = 'fetch_integration_param'";
        try {
            Connection con = ds.getConnection();
            try {               
                PreparedStatement ps = con.prepareStatement(query);
                try {
                    try {
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()){
                            this.apiId = rs.getString("c_api_id");
                            this.apiKey = rs.getString("c_api_key");         
                        }
                    } catch(SQLException e){
                        LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
                    }
                    if (ps !=null)
                        ps.close();
                } catch (Throwable throwable) {
                    if (ps !=null)
                        try {
                            ps.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }    
                    throw throwable;
                }
                if (con !=null)
                    con.close();    
            } catch (Throwable throwable) {
                if (con !=null)
                    try {
                        con.close();
                    }catch(Throwable throwable1){
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
    
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"); 
        Timestamp ts =  Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
    
     public String assignStatus(ActivityTask act){
        String status = "";
        if (act.getTaskId()==10){
            status = "LABASSIGN";
        } else {
            status = "APPR";
        }
        return status;
    }
     
    public String getWorkzone(String wonum) throws SQLException {
        String workzone = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_workzone FROM app_fd_workorder WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                workzone = rs.getString("c_workzone");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return workzone;
    }
    
    public String getOwnerGroup(String workzone) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping WHERE c_workzone = ? AND c_classstructureid IN (SELECT c_classstructureid FROM app_fd_classstructure WHERE c_classificationid = 'WFM' AND c_parent IN (SELECT c_classstructureid FROM app_fd_classstructure WHERE c_classificationid='FULFILLMENT'))";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }

    public String getOwnerGroupPerson(String personGroup) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_persongroup, c_description FROM app_fd_persongroup WHERE c_persongroup = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, personGroup);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_description");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public JSONObject getDetailTask(String activity) throws SQLException {
        JSONObject activityProp = new JSONObject();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_activity, c_description, c_actplace, c_attributes, c_sequence, c_ownergroup, c_duration FROM app_fd_detailactivity WHERE c_activity = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, activity);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activityProp.put("activity", rs.getString("c_activity"));
                activityProp.put("description", rs.getString("c_description"));
                activityProp.put("sequence", rs.getInt("c_sequence"));
                activityProp.put("actPlace", rs.getString("c_actplace"));
                activityProp.put("ownergroup", rs.getString("c_ownergroup"));
                activityProp.put("attributes", rs.getInt("c_attributes"));
                activityProp.put("duration", rs.getInt("c_duration"));
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
    
    public String getTaskAttrName(String attrName) throws SQLException {
        String taskAttrName = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid FROM app_fd_workorderspec WHERE c_assetattrid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, attrName);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                taskAttrName = rs.getString("c_assetattrid");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return taskAttrName;
    }
    
    public void reviseTask(String parent){
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_woactivity SET c_wfmdoctype = ? WHERE c_parent = ?";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, "REVISED");
                    ps.setString(2, parent);
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

    public boolean updateWoCpe(String cpeModel, String cpeVendor, String cpeSerialNumber, String cpeValidasi, String parent, ActivityTask act){
        String wonum = parent + " - " + ((act.getTaskId()/10) - 1);
        boolean updateCpe = false;    
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");// change 03
        StringBuilder update = new StringBuilder();
        update
                .append(" UPDATE app_fd_workorder SET ")
                .append(" c_cpe_model = ?, ")
                .append(" c_cpe_vendor = ?, ")
                .append(" c_cpe_serial_number = ?, ")
                .append(" c_cpe_validation = ?, ")
                .append(" dateModified = ? ")
                .append(" WHERE ")
                .append(" c_wonum = ? ")
                .append(" AND ")
                .append(" c_woclass = 'ACTIVITY' ");
        // change 03
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update.toString());
                // change 03
                try {
                    ps.setString(1, cpeModel);
                    ps.setString(2, cpeVendor);
                    ps.setString(3, cpeSerialNumber);
                    ps.setString(4, cpeValidasi);
                    ps.setTimestamp(5, getTimeStamp());
                    // change 03 where clause
                    ps.setString(6, wonum);
//                    ps.setString(6, Integer.toString(act.getTaskId()));
                    // change 03
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        updateCpe = true;
                        LogUtil.info(getClass().getName(), " CPE updated succes to " + wonum);
                    }   
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
        return updateCpe;
    }
    
    public void generateActivityTask(String parent, String siteId, String correlationId, JSONObject taskObj, String ownerGroup) throws SQLException {
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
                .append(" c_estdur, ")
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
                .append(" ?, ")
                .append(" ? ")
                .append(" ) ");
            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(insert.toString());){
            ps.setString(1, UuidGenerator.getInstance().getUuid());
            ps.setTimestamp(2, getTimeStamp());
            ps.setString(3, parent);
            ps.setString(4, taskObj.get("wonum").toString());
            ps.setString(5, taskObj.get("activity").toString()); //activity
            ps.setString(6, taskObj.get("description").toString());   //activity
            ps.setString(7, taskObj.get("sequence").toString());
            ps.setString(8, taskObj.get("actplace").toString());
            ps.setString(9, taskObj.get("status").toString());
            ps.setString(10, "NEW");
            ps.setString(11, "TELKOM");     
            ps.setString(12, siteId);
            ps.setString(13, "WFM");
            ps.setString(14, "ACTIVITY");       
            ps.setString(15, taskObj.get("taskid").toString());
            ps.setString(16, correlationId);
            ps.setInt(17, (int) taskObj.get("duration"));
            ps.setString(18, ownerGroup);
            
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
    
    public void GenerateTaskAttribute(String activity, String wonum, String orderId, String siteId) throws SQLException {
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_classspecid, ")
                .append(" c_orgid, ")
                .append(" c_assetattrid, ")
                .append(" c_description, ")
                .append(" c_sequence, ")
                .append(" c_domainid, ")
                .append(" c_readonly, ")
                .append(" c_isrequired, ") //joinan dari classspecusewith
                .append(" c_isshared ")
                .append(" FROM app_fd_classspec WHERE ")
                .append(" c_activity = ? ");  //this is for next patching
        
        StringBuilder insert = new StringBuilder();
        insert
                .append(" INSERT INTO app_fd_workorderspec ")
                .append(" ( ")
                //TEMPLATE CONFIGURATION
                .append(" id, dateCreated, createdBy, createdByName,  ")
                //TASK ATTRIBUTE
                .append(" c_wonum, c_assetattrid, c_siteid, c_orgid, c_classspecid, c_orderid, c_displaysequence, c_domainid, ")
                //PERMISSION
                .append(" c_readonly, c_isrequired, c_isshared ")
                .append(" ) ")
                .append(" VALUES ")
                .append(" ( ")
                //VALUES TEMPLATE CONFIGURATION
                .append(" ?, ?, 'admin', 'Admin admin', ")
                //VALUES TASK ATTRIBUTE
                .append(" ?, ?, ?, ?, ?, ?, ?, ?, ")
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
                    psInsert.setString(5, siteId);
                    psInsert.setString(6, rs.getString("c_orgid"));
                    psInsert.setString(7, rs.getString("c_classspecid"));
                    psInsert.setString(8, orderId);
                    psInsert.setString(9, rs.getString("c_sequence"));
                    psInsert.setString(10, rs.getString("c_domainid"));
                    psInsert.setString(11, rs.getString("c_readonly"));
                    psInsert.setString(12, rs.getString("c_isrequired"));
                    psInsert.setString(13, rs.getString("c_isshared"));
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
      
//    public void updateNameTaskAttribute(String assetattrid, String wonum, String orderId, String attrValue) throws SQLException {
//        StringBuilder query = new StringBuilder();
//        query
//                .append(" SELECT ")
////                .append(" c_classstructureid, ")
//                .append(" c_classspecid, ")
//                .append(" c_orgid, ")
//                .append(" c_assetattrid, ")
//                .append(" c_readonly, ")
//                .append(" c_isrequired, ") //joinan dari classspecusewith
//                .append(" c_isshared ")
//                .append(" FROM app_fd_classspec WHERE ")
//                .append(" c_assetattrid = ? ");  //this is for next patching
//        
//        StringBuilder insert = new StringBuilder();
//        insert
//                .append(" INSERT INTO app_fd_workorderspec ")
//                .append(" ( ")
//                //TEMPLATE CONFIGURATION
//                .append(" id, dateCreated, createdBy, createdByName,  ")
//                //TASK ATTRIBUTE
//                .append(" c_wonum, c_assetattrid, c_orgid, c_classspecid, c_orderid, c_value, ")
//                //PERMISSION
//                .append(" c_readonly, c_isrequired, c_isshared ")
//                .append(" ) ")
//                .append(" VALUES ")
//                .append(" ( ")
//                //VALUES TEMPLATE CONFIGURATION
//                .append(" ?, ?, 'admin', 'Admin admin', ")
//                //VALUES TASK ATTRIBUTE
//                .append(" ?, ?, ?, ?, ?, ?, ")
//                //VALUES PERMISSION
//                .append(" ?, ?, ? ")
//                .append(" ) ");
//        
//        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//        try(Connection con = ds.getConnection()) {
//            boolean oldAutoCommit = con.getAutoCommit();
//            LogUtil.info(getClass().getName(), "'start' auto commit state: " + oldAutoCommit);
//            con.setAutoCommit(false);
//            try(PreparedStatement ps = con.prepareStatement(query.toString());
//                PreparedStatement psInsert = con.prepareStatement(insert.toString())) {
//                    ps.setString(1, assetattrid);
//                    ResultSet rs = ps.executeQuery();
//                while (rs.next()) {
//                    psInsert.setString(1, UuidGenerator.getInstance().getUuid());
//                    psInsert.setTimestamp(2, getTimeStamp());
//                    psInsert.setString(3, wonum);
//                    psInsert.setString(4, rs.getString("c_assetattrid"));
//                    psInsert.setString(5, rs.getString("c_orgid"));
//                    psInsert.setString(6, rs.getString("c_classspecid"));
//                    psInsert.setString(7, orderId);
//                    psInsert.setString(8, attrValue);
//                    psInsert.setString(9, rs.getString("c_readonly"));
//                    psInsert.setString(10, rs.getString("c_isrequired"));
//                    psInsert.setString(11, rs.getString("c_isshared"));
//                    psInsert.addBatch();
//                }
//                int[] exe = psInsert.executeBatch();
//                if (exe.length > 0) {
//                    LogUtil.info(getClass().getName(), "Success generated task attributes, for " + assetattrid);
//                }
//                con.commit();
//            } catch(SQLException e) {
//                LogUtil.error(getClass().getName(), e, "Trace Error Here: " + e.getMessage());
//                con.rollback();
//            } finally {
//                con.setAutoCommit(oldAutoCommit);
//            }
//        } catch(SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace Error Here: " + e.getMessage());
//        }
//    }
    
    public boolean updateValueTaskAttribute(String wonum, String attrName, String attrValue){
        boolean updateValue = false;    
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");// change 03
        StringBuilder update = new StringBuilder();
        update
                .append(" UPDATE app_fd_workorderspec SET ")
                .append(" c_alnvalue = ?, ")
                .append(" c_value = ?, ")
                .append(" dateModified = ? ")
                .append(" WHERE ")
                .append(" c_wonum = ? ")
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
                    ps.setString(4, wonum);
                    ps.setString(5, attrName);
                    // change 03
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        updateValue = true;
                        LogUtil.info(getClass().getName(), " Task Attribute updated to " + wonum);
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
    
    public void insertToAssignment(PreparedStatement ps, String parent, String wonum, String taskid, String status, String description, String scheduledate) throws SQLException{              
        ps.setString(1, UuidGenerator.getInstance().getUuid());
        ps.setString(2, parent);
        ps.setString(3, wonum);
        ps.setString(4, taskid);
        ps.setString(5, status);
        ps.setString(6, description);
        ps.setString(7, "WFM");
        ps.setString(8, "ACTIVITY");
        ps.setString(9, scheduledate);
    }
    
    public void generateAssignment(String detailtask, String scheduledate, String parent) {
        String insert = "INSERT INTO app_fd_assignment "
                + "(id, c_parent, c_wonum, c_taskid, c_status, c_description, c_wfmdoctype, c_woclass, c_scheduledate, dateCreated) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
            String query = "SELECT c_description, c_taskid, c_wonum FROM app_fd_workorder WHERE c_detailactcode = ? AND c_parent = ? AND c_actplace = 'OUTSIDE'";
            
            try {
                Connection con = ds.getConnection();
                con.setAutoCommit(false); 
                try {               
                    PreparedStatement ps = con.prepareStatement(insert);
                    PreparedStatement stmt = con.prepareStatement(query);
                    try {       
                        stmt.setString(1, detailtask);
                        stmt.setString(2, parent);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()){
                            ps.setString(1, UuidGenerator.getInstance().getUuid());
                            ps.setString(2, parent);
                            ps.setString(3, rs.getString("c_wonum"));
                            ps.setString(4, rs.getString("c_taskid"));
                            ps.setString(5, "WAITASSIGN");
                            ps.setString(6, rs.getString("c_description"));
                            ps.setString(7, "WFM");
                            ps.setString(8, "ACTIVITY");
                            ps.setString(9, scheduledate);
                            
                            int exe = ps.executeUpdate();
                            //Checking insert status
                            if (exe > 0) {
                                LogUtil.info(getClass().getName(), "'" + rs.getString("c_description") + "' generated as assignment");
                            }
                            con.commit();
                        } else con.rollback();
                        con.setAutoCommit(true);
                        if (ps != null)
                            ps.close();
                        if (stmt != null)
                            stmt.close();
                    } catch (SQLException throwable) {
                            try {
                                if (ps != null)
                                    ps.close();
                                if (stmt != null)
                                    stmt.close();
                            } catch (SQLException throwable1) {
                                throwable.addSuppressed(throwable1);
                            }    
                        throw throwable;
                    }
                    if (con !=null)
                        con.close();    
                } catch (Throwable throwable) {
                    if (con !=null)
                        try {
                            con.close();
                        }catch(SQLException throwable1){
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
