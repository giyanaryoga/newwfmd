/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

//import id.co.telkom.wfm.plugin.model.ActivityTask;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
public class TaskActivityDao {
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
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping WHERE c_workzone = ? AND c_classstructureid IN (SELECT c_classstructureid FROM app_fd_classstructure WHERE c_classificationid = 'WFM_ACTIVITY' AND c_parent IN (SELECT c_classstructureid FROM app_fd_classstructure WHERE c_classificationid='FULFILLMENT'))";
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
        String query = "SELECT c_activity, c_description, c_actplace, c_attributes, c_sequence, c_ownergroup, c_duration, c_classstructureid "
                + "FROM app_fd_detailactivity WHERE c_activity = ?";
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
                activityProp.put("duration", rs.getFloat("c_duration"));
                activityProp.put("classstructureid", rs.getString("c_classstructureid"));
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
    
    public JSONArray getDetailTaskNonCore(String productName, String crmOrderType) throws SQLException {
        JSONArray taskArray = new JSONArray();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_activity, c_sequence, c_crmordertype FROM app_fd_wfmproducttask WHERE c_productname = ? AND c_crmordertype = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, productName);
            ps.setString(2, crmOrderType);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject activityProp = new JSONObject();
                activityProp.put("activity", rs.getString("c_activity"));
                activityProp.put("sequence", rs.getInt("c_sequence"));
                activityProp.put("crmOrderType", rs.getString("c_crmordertype"));
                taskArray.add(activityProp);
                LogUtil.info(getClass().getName(), "taskNonCore = " +taskArray);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return taskArray;
    }
    
    public String getTaskAttrName(String wonum, String attrName) throws SQLException {
        String taskAttrName = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, attrName);
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
    
    public String getTaskAttrValue(String wonum, String attrName) throws SQLException {
        String taskAttrValue = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, attrName);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                taskAttrValue = rs.getString("c_value");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return taskAttrValue;
    }
    
    public boolean updateWoCpe(String cpeModel, String cpeVendor, String cpeSerialNumber, String cpeValidasi, String wonum){
//        String wonum = parent + " - " + ((act.getTaskId()/10) - 1);
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
    
    public void generateActivityTask(JSONObject taskObj, JSONObject workorder, String ownerGroup) throws SQLException {
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
                .append(" c_scorderno, ")
                .append(" c_jmscorrelationid, ")
                .append(" c_ownergroup, ")
                .append(" c_schedstart, ")
                .append(" c_schedfinish, ")
                .append(" c_classstructureid ")
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
            ps.setString(3, taskObj.get("parent").toString());
            ps.setString(4, taskObj.get("wonum").toString());
            ps.setString(5, taskObj.get("activity").toString()); //activity
            ps.setString(6, taskObj.get("description").toString());   //activity
            ps.setString(7, taskObj.get("sequence").toString());
            ps.setString(8, taskObj.get("actplace").toString());
            ps.setString(9, taskObj.get("status").toString());
            ps.setString(10, "NEW");
            ps.setString(11, "TELKOM");     
            ps.setString(12, workorder.get("siteId").toString());
            ps.setString(13, "WFM");
            ps.setString(14, "ACTIVITY");       
            ps.setString(15, taskObj.get("taskid").toString());
            ps.setString(16, taskObj.get("correlation").toString());
            ps.setFloat(17, (float) taskObj.get("duration"));
            ps.setString(18, workorder.get("scOrderNo").toString());
            ps.setString(19, workorder.get("jmsCorrelationId").toString());
            ps.setString(20, ownerGroup);
            ps.setTimestamp(21, (taskObj.get("schedstart").toString() == "" ? getTimeStamp() : Timestamp.valueOf(taskObj.get("schedstart").toString())));
            ps.setTimestamp(22, Timestamp.valueOf(taskObj.get("schedfinish").toString()));
            ps.setString(23, taskObj.get("classstructureid").toString());
            
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
    
    private String getDomainType(String domainId) throws SQLException {
        String domainType = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_domaintype FROM app_fd_maxdomain WHERE c_domainid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, domainId);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                domainType = rs.getString("c_domaintype");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return domainType;
    }
    
    public void GenerateTaskAttribute(JSONObject taskObj, JSONObject workorder, String orderId) throws SQLException {
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
                .append(" c_mandatory, ") //joinan dari classspecusewith
                .append(" c_defaultvalue, ")
                .append(" c_classstructureid, ")
                .append(" c_isshared ")
                .append(" FROM app_fd_classspec WHERE ")
                .append(" c_classstructureid = ? ");  //this is for next patching
        
        StringBuilder insert = new StringBuilder();
        insert
                .append(" INSERT INTO app_fd_workorderspec ")
                .append(" ( ")
                //TEMPLATE CONFIGURATION
                .append(" id, dateCreated, createdBy, createdByName,  ")
                //TASK ATTRIBUTE
                .append(" c_wonum, c_assetattrid, c_description, c_siteid, c_orgid, c_classspecid, c_orderid, c_displaysequence, c_domainid, ")
                //PERMISSION
                .append(" c_readonly, c_isshared, c_mandatory, c_parent, c_value, c_classstructureid, c_domaintype, c_isview ")
                .append(" ) ")
                .append(" VALUES ")
                .append(" ( ")
                //VALUES TEMPLATE CONFIGURATION
                .append(" ?, ?, 'admin', 'Admin admin', ")
                //VALUES TASK ATTRIBUTE
                .append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ")
                //VALUES PERMISSION
                .append(" ?, ?, ?, ?, ?, ?, ?, ? ")
                .append(" ) ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try(Connection con = ds.getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            LogUtil.info(getClass().getName(), "'start' auto commit state: " + oldAutoCommit);
            con.setAutoCommit(false);
            try(PreparedStatement ps = con.prepareStatement(query.toString());
                PreparedStatement psInsert = con.prepareStatement(insert.toString())) {
                    ps.setString(1, taskObj.get("classstructureid").toString());
                    ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    psInsert.setString(1, UuidGenerator.getInstance().getUuid());
                    psInsert.setTimestamp(2, getTimeStamp());
                    psInsert.setString(3, taskObj.get("wonum").toString());
                    psInsert.setString(4, rs.getString("c_assetattrid"));
                    psInsert.setString(5, rs.getString("c_description"));
                    psInsert.setString(6, workorder.get("siteId").toString());
                    psInsert.setString(7, rs.getString("c_orgid"));
                    psInsert.setString(8, rs.getString("c_classspecid"));
                    psInsert.setString(9, orderId);
                    psInsert.setString(10, rs.getString("c_sequence"));
                    psInsert.setString(11, rs.getString("c_domainid"));
                    psInsert.setString(12, rs.getString("c_readonly"));
                    psInsert.setString(13, rs.getString("c_isshared"));
                    psInsert.setString(14, rs.getString("c_mandatory"));
                    psInsert.setString(15, taskObj.get("parent").toString());
                    psInsert.setString(16, rs.getString("c_defaultvalue"));
                    psInsert.setString(17, rs.getString("c_classstructureid"));
                    if (rs.getString("c_domainid") != null) {
                        psInsert.setString(18, getDomainType(rs.getString("c_domainid")));
                    } else {
                        psInsert.setString(18, "");
                    }
                    psInsert.setInt(19, 1);
                    psInsert.addBatch();
                }
                int[] exe = psInsert.executeBatch();
                if (exe.length > 0) {
                    LogUtil.info(getClass().getName(), "Success generated task attributes, for " + taskObj.get("activity").toString());
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
    
    public void generateAssignment(JSONObject taskObj, JSONObject workorder) {
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
                    stmt.setString(1, taskObj.get("activity").toString());
                    stmt.setString(2, taskObj.get("parent").toString());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()){
                        ps.setString(1, UuidGenerator.getInstance().getUuid());
                        ps.setString(2, taskObj.get("parent").toString());
                        ps.setString(3, rs.getString("c_wonum"));
                        ps.setString(4, rs.getString("c_taskid"));
                        ps.setString(5, "WAITASSIGN");
                        ps.setString(6, rs.getString("c_description"));
                        ps.setString(7, "WFM");
                        ps.setString(8, "ACTIVITY");
                        ps.setString(9, workorder.get("schedStart").toString());

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
    
    public void schedFinish(JSONObject activity, JSONObject workorder, Timestamp schedFinish) throws SQLException {
        StringBuilder update = new StringBuilder();
        update
                .append(" UPDATE app_fd_workorder SET ")
                .append(" c_schedfinish = ?, ")
                .append(" c_estdur = ? ")
                .append(" WHERE ")
                .append(" c_wonum = ? ")
                .append(" AND ")
                .append(" c_siteid = ? ")
                .append(" AND ")
                .append(" c_woclass = 'ACTIVITY' ");
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setTimestamp(1, schedFinish);
            ps.setFloat(2, (float) activity.get("duration"));
            ps.setString(3, activity.get("wonum").toString());
            ps.setString(4, workorder.get("siteId").toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            LogUtil.error(getClass().getName() + " | generateSchedulingInformation", e, "Trace error here: " + e.getMessage());
            throw e;
        }
    }
    
    public void deleteTask(String parent) throws SQLException {
        StringBuilder update = new StringBuilder();
        update
            .append(" DELETE FROM app_fd_workorder ")
            .append(" WHERE ")
            .append(" c_parent = ? ")
            .append(" AND ")
            .append(" c_woclass = 'ACTIVITY' ");
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, parent);
            int exe = ps.executeUpdate();
            //Checking insert status
            if (exe > 0) 
                LogUtil.info(getClass().getName(), "Older activity task has been deleted");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName() + " | generateSchedulingInformation", e, "Trace error here: " + e.getMessage());
            throw e;
        }
    }
    
    public JSONObject getProduct(String parent) throws SQLException {
        JSONObject getProduct = new JSONObject();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_productname, c_crmordertype, c_schedstart, c_wonum, c_siteid, c_jmscorrelationid, c_scorderno, c_workzone FROM app_fd_workorder WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                getProduct.put("wonum", rs.getString("c_wonum"));
                getProduct.put("prodName", rs.getString("c_productname"));
                getProduct.put("crmOrderType", rs.getString("c_crmordertype"));
                getProduct.put("workZone", rs.getString("c_workzone"));
                getProduct.put("schedStart", rs.getString("c_schedstart"));
                getProduct.put("siteId", rs.getString("c_siteid"));
                getProduct.put("jmsCorrelationId", rs.getString("c_jmscorrelationid"));
                getProduct.put("scOrderNo", rs.getString("c_scorderno"));
            } else {
                getProduct = null;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return getProduct;
    }
    
    public JSONArray getTaskWo(String parent) throws SQLException {
        JSONArray activity = new JSONArray();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_jmscorrelationid, c_scorderno, c_detailactcode FROM app_fd_workorder WHERE c_parent = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.next()) {
                    JSONObject activityProp = new JSONObject();
                    activityProp.put("jmsCorrId", rs.getString("c_jmscorrelationid"));
                    activityProp.put("scorderno", rs.getString("c_scorderno"));
                    activityProp.put("activity", rs.getString("c_detailactcode"));
                    activity.add(activityProp);
//                    LogUtil.info(getClass().getName(), "task = " + activity);
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return activity;
    }
}
