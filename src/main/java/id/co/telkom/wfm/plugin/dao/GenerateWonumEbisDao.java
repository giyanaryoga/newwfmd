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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class GenerateWonumEbisDao {
    public String apiId = "";
    public String apiKey = "";
    
    public void getApiAttribute (){
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_api_id, c_api_key FROM app_fd_api_wfm WHERE c_use_of_api = 'generate_wonum' ";
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
                } catch (SQLException throwable) {
                    if (ps !=null)
                        try {
                            ps.close();
                        } catch (SQLException throwable1) {
                            throwable.addSuppressed(throwable1);
                        }    
                    throw throwable;
                }
                if (con != null)
                    con.close();    
            } catch (SQLException throwable) {
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
    
    public String lookupSiteId (String workZone){
        String siteId = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_tk_subregion FROM app_fd_workzone WHERE ? ";
        try {
            Connection con = ds.getConnection();
            try {               
                PreparedStatement ps = con.prepareStatement(query);
                try {
                    try {
                        ps.setString(1, workZone);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()){
                            siteId = rs.getString("c_tk_subregion");
                        }
                    } catch(SQLException e){
                        LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
                    }
                    if (ps !=null)
                        ps.close();
                } catch (SQLException throwable) {
                    if (ps !=null)
                        try {
                            ps.close();
                        } catch (SQLException throwable1) {
                            throwable.addSuppressed(throwable1);
                        }    
                    throw throwable;
                }
                if (con !=null)
                    con.close();    
            } catch (SQLException throwable) {
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
        return siteId;
    }
   
    public String getWonum(){
        String wonum = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT GENERATE_WONUM FROM dual";
        try {
            Connection con = ds.getConnection();
            try {               
                PreparedStatement ps = con.prepareStatement(query);
                try {
                    try {
                        
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()){
                            wonum = rs.getString("GENERATE_WONUM");
                        }
                    } catch(SQLException e){
                        LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
                    }
                    if (ps !=null)
                        ps.close();
                } catch (SQLException throwable) {
                    if (ps !=null)
                        try {
                            ps.close();
                        } catch (SQLException throwable1) {
                            throwable.addSuppressed(throwable1);
                        }    
                    throw throwable;
                }
                if (con !=null)
                    con.close();    
            } catch (SQLException throwable) {
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
        return wonum;
    }
    
    public String getValueWorkorderAttribute(String wonum, String woAttrName) throws SQLException {
        String woAttrValue = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_attr_value FROM app_fd_workorderattribute WHERE c_wonum = ? AND c_attr_name = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, woAttrName);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                woAttrValue = rs.getString("c_attr_value");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return woAttrValue;
    }
    
     public boolean insertToWoTable(JSONObject param){
        boolean insertStatus = false;    
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        
        String insert = "INSERT INTO app_fd_workorder (id, c_wonum, c_crmordertype, c_customer_name, c_serviceaddress, "
                + "c_description, c_productname, c_producttype, c_scorderno, c_workzone, c_siteid, c_worktype, "
                + "c_schedstart, c_reportedby, c_woclass, c_worevisionno, c_jmscorrelationid, c_status, c_servicenum, c_tk_workorder_04, "
                + "c_ownergroup, c_statusdate, c_tk_custom_header_01, c_estdur, c_latitude, c_longitude, c_segment, dateCreated) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, UuidGenerator.getInstance().getUuid());
                    ps.setString(2, param.get("wonum").toString());
                    ps.setString(3, param.get("crmOrderType").toString());
                    ps.setString(4, param.get("custName").toString());
                    ps.setString(5, param.get("custAddress").toString());
                    ps.setString(6, param.get("TaskDescription").toString());
                    ps.setString(7, param.get("prodName").toString());
                    ps.setString(8, param.get("prodType").toString());
                    ps.setString(9, param.get("scOrderNo").toString());
                    ps.setString(10, param.get("workZone").toString());
                    ps.setString(11, param.get("siteId").toString());
                    ps.setString(12, param.get("workType").toString());
                    ps.setTimestamp(13, (param.get("schedStart").toString() == "" ? getTimeStamp() : Timestamp.valueOf(param.get("schedStart").toString())));
                    ps.setString(14, param.get("reportBy").toString());
                    ps.setString(15, param.get("woClass").toString());
                    ps.setString(16, param.get("woRevisionNo").toString());
                    ps.setString(17, param.get("jmsCorrelationId").toString());
                    ps.setString(18, param.get("status").toString());
                    ps.setString(19, param.get("serviceNum").toString());
                    ps.setString(20, param.get("tkWo4").toString());
                    ps.setString(21, param.get("ownerGroup").toString());
                    ps.setTimestamp(22, Timestamp.valueOf(param.get("statusDate").toString()));
                    ps.setString(23, param.get("tkCustomHeader01").toString());
                    ps.setFloat(24, (float) param.get("duration"));
                    ps.setString(25, param.get("latitude").toString());
                    ps.setString(26, param.get("longitude").toString());
                    ps.setString(27, param.get("segment").toString());
                    ps.setTimestamp(28, getTimeStamp());
                    
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        insertStatus = true;
//                        LogUtil.info(getClass().getName(), "Work Order param for '" + param.get("wonum").toString() + "' inserted to DB");
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
        return insertStatus;
    }
    
    public boolean insertToOssItem(JSONObject taskObj){
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
        boolean insertStatus = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO app_fd_ossitem (id, c_ossitemid, c_wonum, c_parent, c_action, c_correlationid, c_itemname, dateCreated) "
                + "VALUES (?, OSSITEMIDSEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, uuId);
                    ps.setString(2, taskObj.get("wonum").toString());
                    ps.setString(3, taskObj.get("parent").toString());
                    ps.setString(4, "ADD");
                    ps.setString(5, taskObj.get("correlation").toString());
                    ps.setString(6, taskObj.get("activity").toString());
                    ps.setTimestamp(7, getTimeStamp());
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        insertStatus = true;
//                        LogUtil.info(getClass().getName(), "ADD | OSS Item : " + taskObj.get("activity").toString() + ", CorrelationId : " + taskObj.get("correlation").toString() + ", insert to DB");
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
        return insertStatus;
    }

    public void insertToOssAttribute(JSONObject ossItem, String wonum){
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String getOssItemId = "SELECT c_ossitemid FROM app_fd_ossitem WHERE c_wonum = ?";
        String insert = "INSERT INTO app_fd_ossitemattribute (id, c_ossitemattributeid, c_ossitemid, c_attr_name, c_attr_value, c_wonum, dateCreated) VALUES (?, OSSITEMATTRIBUTEIDSEQ.NEXTVAL, ?, ?, ?, ?, ?)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement psSelect = con.prepareStatement(getOssItemId);
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    psSelect.setString(1, wonum);
                    ResultSet rs = psSelect.executeQuery();
                    if (rs.next()) {
                        ps.setString(1, uuId);
                        ps.setInt(2, rs.getInt("c_ossitemid"));
                        ps.setString(3, ossItem.get("attrName").toString());
                        ps.setString(4, ossItem.get("attrValue").toString());
                        ps.setString(5, wonum);
                        ps.setTimestamp(6, getTimeStamp());
                        int exe = ps.executeUpdate();
                        //Checking insert status
                        if (exe > 0) {
//                            LogUtil.info(getClass().getName(), "Add | OssAttr : " + ossItem.get("attrName").toString() + ", Value: " + ossItem.get("attrValue").toString());
                        }   
                        if (ps != null)
                            ps.close();
                    }
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

    public boolean insertToWoAttrTable(String wonum, JSONObject woAttr){
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
        boolean insertStatus = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO app_fd_workorderattribute (id, c_workorderattributeid, c_wonum, c_attr_name, c_attr_value, c_sequence, dateCreated) VALUES (?, WORKORDERATTRIBUTEIDSEQ.NEXTVAL, ?, ?, ?, ?, ?)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, uuId);
                    ps.setString(2, wonum);
                    ps.setString(3, woAttr.get("woAttrName").toString());
                    ps.setString(4, woAttr.get("woAttrValue").toString());
                    ps.setInt(5, (int)woAttr.get("woAttrSequence"));
                    ps.setTimestamp(6, getTimeStamp());
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        insertStatus = true;
//                        LogUtil.info(getClass().getName(), "Add | Attr : " + woAttr.get("woAttrName").toString() + ", Value: " + woAttr.get("woAttrValue").toString() + ", sequence: " + woAttr.get("woAttrSequence").toString());
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
        return insertStatus;
    }

    public JSONArray getWoAttrName(String wonum) throws SQLException {
        JSONArray woAttrArray = new JSONArray();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_attr_name, c_attr_value FROM app_fd_workorderattribute WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject attrProp = new JSONObject();
                attrProp.put("attr_name", rs.getString("c_attr_name"));
                attrProp.put("attr_value", (rs.getString("c_attr_value") == null ? "" : rs.getString("c_attr_value")));
                woAttrArray.add(attrProp);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return woAttrArray;
    }
    
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"); 
        Timestamp ts =  Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
}
