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
import id.co.telkom.wfm.plugin.model.ListLabor;
import id.co.telkom.wfm.plugin.model.ListClassSpec;
import id.co.telkom.wfm.plugin.model.ListCpeValidate;
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

    public String getCpeModel(String model) throws SQLException {
        String cpeModel = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_model, c_vendor FROM app_fd_cpemodel WHERE c_model = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, model);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                cpeModel = rs.getString("c_model");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return cpeModel;
    }
    
    public String getCpeVendor(String vendor) throws SQLException {
        String cpeVendor = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_vendor, c_model FROM app_fd_cpevendor WHERE c_vendor = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, vendor);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                cpeVendor = rs.getString("c_vendor");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return cpeVendor;
    }
    
    public String getClassStructure(String wonum) throws SQLException {
        String classStructure = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_classstructureid FROM app_fd_workder WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                classStructure = rs.getString("c_classstructureid");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return classStructure;
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

    public boolean updateWoCpe(String cpeModel, String cpeVendor, String cpeSerialNumber, String cpeValidasi, String parent, ActivityTask act){
//        ActivityTask act = new ActivityTask();
        String wonum = parent + " - " + ((act.getTaskId()/10) - 1);
        boolean updateCpe = false;    
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");// change 03
        StringBuilder update = new StringBuilder();
        update
                .append(" UPDATE app_fd_workorder SET ")
                .append(" c_cpe_model = ?, ")
                .append(" c_cpe_vendor = ?, ")
                .append(" c_cpe_serial_number = ?, ")
                .append(" c_cpe_validation = ? ")
                .append(" WHERE ")
                .append(" c_wonum = ? ")
//                .append(" AND ")
//                .append(" c_taskid = ? ")
                .append(" AND ")
                .append(" c_woclass = 'ACTIVITY' ");
        // change 03
        try {
            Connection con = ds.getConnection();
            try {
                //PreparedStatement ps = con.prepareStatement(update);
                // change 03
                PreparedStatement ps = con.prepareStatement(update.toString());
                // change 03
                try {
                    ps.setString(1, cpeModel);
                    ps.setString(2, cpeVendor);
                    ps.setString(3, cpeSerialNumber);
                    ps.setString(4, cpeValidasi);
                    // change 03 where clause
                    ps.setString(5, wonum);
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
        return updateCpe;
    }

    public void insertToWoActivity(PreparedStatement ps, String parent, ActivityTask act, String detailActCode, String description, String serviceType, String sequence, String actplace, String classstructureid, String siteId, String correlationId, String ownerGroup) throws SQLException{              
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
    }
    
    public void generateActivityTask(String parent, String activity, ActivityTask act, String siteId, String correlationId, String ownerGroup) {
        String insert = "INSERT INTO app_fd_workorder (id, c_parent, c_wonum, c_detailactcode, c_description, c_servicetype, c_wosequence, c_actplace, c_classstructureid, c_status, c_wfmdoctype, c_orgid, c_siteId, c_worktype, c_woclass, c_taskid, c_correlation, c_ownergroup, dateCreated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
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
                            insertToWoActivity(ps, parent, act, activity, rs.getString("c_description"), "", rs.getString("c_sequence"), rs.getString("c_actplace"), rs.getString("c_classstructureid"), siteId, correlationId, ownerGroup);
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
    
    public void insertToWoAttribute(PreparedStatement ps, String classStructureId, String classSpecId, String parent, String siteId, String attr_name, String attr_value, String isRequired, String isShared, String isReported, String readOnly, ActivityTask act ) throws SQLException{              
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
//        act.setTaskId(10);
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
        ps.setString(13, Integer.toString(act.getTaskId() - 10));
    }
    
    public void GenerateTaskAttribute(String parent, ActivityTask act, String siteid, ListClassSpec taskAttr) throws SQLException {
        String insert = "INSERT INTO app_fd_workorderspec (id, c_classstructureid, c_classspecid, c_orgid, c_wonum, c_siteid, c_attribute_name, c_alnvalue, c_isrequired, c_isshared, c_isreported, c_readonly, c_displaysequence, dateCreated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
        String wonum = parent +" - "+ ((act.getTaskId()/10)-1);
        String getClassStructureId = "SELECT c_classstructureid FROM app_fd_workorder WHERE c_wonum = ?";
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_classstructureid, c_assetattrid, c_classspecid, c_isrequired, c_isshared, c_isreported, c_readonly FROM app_fd_classspec WHERE c_classstructureid = ?";
        try {
            Connection con = ds.getConnection();
            con.setAutoCommit(false);
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                PreparedStatement stmt = con.prepareStatement(query);
                PreparedStatement getClassStructure = con.prepareStatement(getClassStructureId);
                try {
                    getClassStructure.setString(1, wonum);
                    ResultSet rs1 = getClassStructure.executeQuery();
                    if (rs1.next()){
                        taskAttr.setClassStructureId(rs1.getString("c_classstructureid"));
//                        stmt.setString(1, listOssAttr.getAttrName()); 
                        stmt.setString(1, taskAttr.getClassStructureId()); 
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()){
                            taskAttr.setAttrName(rs.getString("c_assetattrid"));
                            insertToWoAttribute(ps, taskAttr.getClassStructureId(), rs.getString("c_classspecid"), wonum, siteid, taskAttr.getAttrName(), taskAttr.getAttrValue(), rs.getString("c_isrequired"), rs.getString("c_isshared"), rs.getString("c_isreported"), rs.getString("c_readonly"), act);
                            int exe = ps.executeUpdate();
                            //Checking insert status
                            if (exe > 0) {
                                LogUtil.info(getClass().getName(), "insert WO Activity Attribute for " +taskAttr.getAttrName()+ " done");
                            }
                            con.commit();
                        } else con.rollback();
                    } else con.rollback();
                        con.setAutoCommit(true);
                        if (ps != null)
                            ps.close();
                        if (stmt != null)
                            stmt.close();
                        if (getClassStructure != null)
                            getClassStructure.close();
                    }
                catch (SQLException throwable) {
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
    
    public String getLabor(String laborcode, ListLabor listLabor) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_laborid, c_laborcode, c_status, c_supervisor FROM app_fd_labor WHERE c_laborcode = ? ";
        // change 04
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, laborcode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                listLabor.setLaborid(rs.getString("c_laborid"));
                listLabor.setLaborcode(laborcode);
                listLabor.setStatusLabor(rs.getString("c_status"));
                listLabor.setSupervisor(rs.getString("c_supervisor"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return laborcode;
    }
    
    public void insertToAssignment(PreparedStatement ps, String wonum, String status, String parent, String siteId, String attr_name, String attr_value ) throws SQLException{              
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
        ps.setString(1, uuId);
        ps.setString(2, wonum);
        ps.setString(3, status);
        ps.setString(4, "TELKOM");
        ps.setString(5, parent);
        ps.setString(6, siteId);
    }
}
