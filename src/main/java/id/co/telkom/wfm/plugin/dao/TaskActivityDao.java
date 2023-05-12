/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

//import id.co.telkom.wfm.plugin.model.ListAttributes;
//import id.co.telkom.wfm.plugin.model.ListDevice;
import id.co.telkom.wfm.plugin.model.ListOssItem;
import id.co.telkom.wfm.plugin.model.ActivityTask;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
//import java.sql.Statement;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONObject;
//import org.json.simple.JSONArray;

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
            }  
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }    
    }
    
    public void updateWoActivityAttribute(String parent, ListOssItem listOss){
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_woactivity SET c_servicenum = ?, c_insintno = ?, c_instelno = ?, c_downloadspeed = ?, c_uploadspeed = ?, c_package_name = ?, c_iptvfeatures = ? WHERE c_parent = ? AND c_correlation = ? AND c_wfmdoctype = 'NEW'";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, listOss.getServiceNumber());
//                    ps.setString(2, listOss.getInsintno());
//                    ps.setString(3, listOss.getInstelno());
                    ps.setString(4, listOss.getDownloadSpeed());
                    ps.setString(5, listOss.getUploadSpeed());
                    ps.setString(6, listOss.getPackageName());
                    ps.setString(7, listOss.getIpTvFeatures());
                    ps.setString(8, parent);
                    ps.setString(9, listOss.getCorrelationid());
                    
                    //Execute insert
                    int exe = ps.executeUpdate();
                    if (exe > 0) {
                        LogUtil.info(getClass().getName(), "update WO Activity Attribute for " + listOss.getItemname() + " done");  
                    }
                    //Close connection of statement
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
                //Close connection of con
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
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
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
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }

    public void insertToWoActivity (PreparedStatement ps, String parent, ActivityTask act, String detailActCode, String description, String serviceType, String sequence, String actplace, String classstructureid, String siteId, String laborCode, String laborName, String correlationId, String ownerGroup) throws SQLException{              
        ps.setString(1, UuidGenerator.getInstance().getUuid());
        ps.setString(2, parent);
        ps.setString(3, parent + " - " + act.getTaskId()/10);
        ps.setString(4, detailActCode);
        ps.setString(5, description);
        ps.setString(6, serviceType);
        ps.setString(7, sequence);
        ps.setString(8, actplace);
        ps.setString(9, classstructureid);
        ps.setString(10, assignStatus(act));
        ps.setString(11, "NEW");
        ps.setString(12, "TELKOM");     
        ps.setString(13, siteId);
        ps.setString(14, "WFM");
        ps.setString(15, "ACTIVITY");       
        ps.setString(16, Integer.toString(act.getTaskId()));       
        ps.setString(17, laborCode);       
        ps.setString(18, laborName);       
        ps.setString(19, correlationId);       
        ps.setString(20, ownerGroup);       
    }
    
    public void generateActivityTask (String parent, String activity, ActivityTask act, String siteId, String laborCode, String laborName, String correlationId, String ownerGroup){
        String insert = "INSERT INTO app_fd_woactivity (id, c_parent, c_wonum, c_detailactcode, c_description, c_servicetype, c_wosequence, c_actplace, c_classstructureid, c_status, c_wfmdoctype, c_orgid, c_siteId, c_worktype, c_woclass, c_taskid, c_laborcode, c_laborname, c_correlation, c_ownergroup, dateCreated, dateModified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, sysdate)";
        //Check type of task
//        if (!actType.equals("val")){
            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
            String query = "SELECT c_description, c_sequence, c_actplace, c_classstructureid FROM app_fd_detailactivity WHERE c_activity = ? ";
            try {
                Connection con = ds.getConnection();
                con.setAutoCommit(false); 
                try {               
                    PreparedStatement ps = con.prepareStatement(insert);
                    PreparedStatement stmt = con.prepareStatement(query);
                    try {       
                        stmt.setString(1, activity);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()){
                            insertToWoActivity(ps, parent, act, activity, rs.getString("c_description"), "", rs.getString("c_sequence"), rs.getString("c_actplace"), rs.getString("c_classstructureid"), siteId, laborCode, laborName, correlationId, ownerGroup);
                            int exe = ps.executeUpdate();
                            //Checking insert status
                            if (exe > 0) {
                                LogUtil.info(getClass().getName(), "'" + rs.getString("c_description") + "' generated as task");
                                act.setTaskId(act.getTaskId()+10);
                            }
                            con.commit();
                        } else con.rollback();
                        con.setAutoCommit(true);
                        if (ps != null)
                            ps.close();
                        if (stmt != null)
                            stmt.close();
                    } catch (Throwable throwable) {
                            try {
                                if (ps != null)
                                    ps.close();
                                if (stmt != null)
                                    stmt.close();
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
                }  
            } catch (SQLException e) {
                LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
            } 
        //Val type of task
//        } else {
//            String seq = "";
//            String description = "";
//            if ("Voice".equals(activity)) {seq = "120"; description = "Testing Service Voice";}
//            else if ("Internet".equals(activity)) {seq = "121"; description = "Testing Service Internet";}
//            else if ("Broadband".equals(activity)) {seq = "122"; description = "Testing Service Broadband";}
//            else if ("IPTV".equals(activity)) {seq = "123"; description = "Testing Service IPTV";}
//            else if ("DigitalService".equals(activity)) {seq = "124"; description = "Testing Service DigitalService";}
//            DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//            try {
//                Connection con = ds.getConnection();
//                try {               
//                    PreparedStatement ps = con.prepareStatement(insert);
//                    try {       
//                        insertToWoActivity(ps, parent, act, "Validate_Service", description, activity, seq, "HOME", "", siteId, laborCode, laborName, correlationId, ownerGroup);
//                        int exe = ps.executeUpdate();
//                        //Checking insert status
//                        if (exe > 0) {
//                            LogUtil.info(getClass().getName(), "Testing Service '" + activity + "' generated as task.");
//                            act.setTaskId(act.getTaskId()+10);
//                        }
//                        if (ps != null)
//                        ps.close();
//                    } catch (Throwable throwable) {
//                        try {
//                            if (ps != null)
//                                ps.close();
//                        } catch (Throwable throwable1) {
//                            throwable.addSuppressed(throwable1);
//                        }    
//                        throw throwable;
//                    }
//                    if (con !=null)
//                        con.close();    
//                } catch (Throwable throwable) {
//                    if (con !=null)
//                        try {
//                            con.close();
//                        }catch(Throwable throwable1){
//                            throwable.addSuppressed(throwable1);
//                        }
//                    throw throwable;
//                }  
//            } catch (SQLException e) {
//                LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
//            } 
//        }
    }
    
     public String assignStatus(ActivityTask act){
        String status = "";
//        if (act.getTaskId()==10){
//            status = "LABASSIGN";
//        } else {
            status = "WAPPR";
//        }
        return status;
    }
    
    public Object getLabor(String wonum) throws SQLException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_laborcode, c_laborname FROM app_fd_workorder WHERE c_wonum = ?";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                resultObj.put("laborCode", rs.getString("c_laborcode"));
                resultObj.put("laborName", rs.getString("c_laborname"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return resultObj;
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
        String query = "SELECT c_ownergroup , c_classstructureid FROM app_fd_tkmapping WHERE c_workzone = ? AND c_classstructureid IN (SELECT c_classstructureid FROM app_fd_classstructure WHERE c_classificationid = 'WFM' AND c_parent IN (SELECT c_classstructureid FROM app_fd_classstructure WHERE c_classificationid='FULFILLMENT'))";
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
}
