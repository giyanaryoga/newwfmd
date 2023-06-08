/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

//import id.co.telkom.wfm.plugin.model.ListAttributes;
//import id.co.telkom.wfm.plugin.model.ListDevice;
//import id.co.telkom.wfm.plugin.model.ListOssItem;
import id.co.telkom.wfm.plugin.model.ListOssItemAttribute;
import id.co.telkom.wfm.plugin.model.ActivityTask;
import id.co.telkom.wfm.plugin.model.ListClassStructure;
import id.co.telkom.wfm.plugin.model.ListClassSpec;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
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

    public void insertToWoActivity (PreparedStatement ps, String parent, ActivityTask act, String detailActCode, String description, String serviceType, String sequence, String actplace, String classstructureid, String siteId, String correlationId, String ownerGroup, String cpe_model, String cpe_vendor, String cpe_serialnumber, String cpe_validation) throws SQLException{              
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
        ps.setString(17, correlationId);       
        ps.setString(18, ownerGroup);
        ps.setString(19, cpe_model);
        ps.setString(20, cpe_vendor);
        ps.setString(21, cpe_serialnumber);
        ps.setString(22, cpe_validation);
    }
    
    public void generateActivityTask (String parent, String activity, ActivityTask act, String siteId, String correlationId, String ownerGroup, String model, String vendor, String serial_number, String cpeValidate) {
        String insert = "INSERT INTO app_fd_workorder (id, c_parent, c_wonum, c_detailactcode, c_description, c_servicetype, c_wosequence, c_actplace, c_classstructureid, c_status, c_wfmdoctype, c_orgid, c_siteId, c_worktype, c_woclass, c_taskid, c_correlation, c_ownergroup, c_cpe_model, c_cpe_vendor, c_cpe_serial_number, c_cpe_validation, dateModified) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
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
                            insertToWoActivity(ps, parent, act, activity, rs.getString("c_description"), "", rs.getString("c_sequence"), rs.getString("c_actplace"), rs.getString("c_classstructureid"), siteId, correlationId, ownerGroup, model, vendor, serial_number, cpeValidate);
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
    }
    
    public void insertToWoAttribute (PreparedStatement ps, String classStructureId, String classSpecId, String parent, String siteId, String attr_name, String attr_value, String isRequired, String isShared, String isReported, String readOnly ) throws SQLException{              
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
        ps.setString(1, uuId);
        ps.setString(2, classStructureId);
        ps.setString(3, classSpecId);
        ps.setString(4, "TELKOM");
        ps.setString(5, parent);
        ps.setString(6, siteId);
        ps.setString(7, attr_name);
        ps.setString(8, attr_value);
        ps.setString(9, isRequired);
        ps.setString(10, isShared);
        ps.setString(11, isReported);
        ps.setString(12, readOnly);
    }
    
    public void GenerateTaskAttribute(String parent, ActivityTask act, ListOssItemAttribute listOssAttr, String siteid, ListClassSpec taskAttr) throws SQLException {
        String insert = "INSERT INTO app_fd_workorderspec (id, c_classstructureid, c_classspecid, c_orgid, c_wonum, c_siteid, c_attribute_name, c_alnvalue, c_isrequired, c_isshared, c_isreported, c_readonly, dateCreated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
        String wonum = parent +" - "+ (act.getTaskId()/10-1);
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT cls.c_classstructureid, cls.c_assetattrid, cls.c_classspecid, cls.c_isrequired, cls.c_isshared, cls.c_isreported, cls.c_readonly FROM app_fd_classspec cls WHERE cls.c_assetattrid = ?";
        try {
            Connection con = ds.getConnection();
            con.setAutoCommit(false); 
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                PreparedStatement stmt = con.prepareStatement(query);
                try {
                    stmt.setString(1, listOssAttr.getAttrName());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()){
                        insertToWoAttribute(ps, rs.getString("c_classstructureid"), rs.getString("c_classspecid"), wonum, siteid, rs.getString("c_assetattrid"), listOssAttr.getAttrValue(), rs.getString("c_isrequired"), rs.getString("c_isshared"), rs.getString("c_isreported"), rs.getString("c_readonly"));
                        int exe = ps.executeUpdate();
                        //Checking insert status
                        if (exe > 0) {
                            LogUtil.info(getClass().getName(), "insert WO Activity Attribute for " +listOssAttr.getAttrName()+ " done");
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
                //Close connection of con
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
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }

    
//    public Object getLabor(String wonum) throws SQLException {
//        JSONObject resultObj = new JSONObject();
//        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT c_laborcode, c_laborname FROM app_fd_workorder WHERE c_wonum = ?";
//        try(Connection con = ds.getConnection();
//            PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, wonum);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()){
//                resultObj.put("laborCode", rs.getString("c_laborcode"));
//                resultObj.put("laborName", rs.getString("c_laborname"));
//            }
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
//        } finally {
//            ds.getConnection().close();
//        }
//        return resultObj;
//    }
}
