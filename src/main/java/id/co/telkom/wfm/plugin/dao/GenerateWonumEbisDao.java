/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.model.ListAttributes;
import id.co.telkom.wfm.plugin.model.ListOssItem;
import id.co.telkom.wfm.plugin.model.ListOssItemAttribute;
import id.co.telkom.wfm.plugin.util.ConnUtil;
import id.co.telkom.wfm.plugin.util.RequestAPI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    
     public boolean insertToWoTable(String id, String wonum, String crmOrderType, String custName, String custAddress, String description, String prodName, String prodType, String scOrderNo, String workZone, String siteId, String workType, String schedStart, String reportBy,  String woClass, String woRevisionNo, String jmsCorrelationId, String status, String serviceNum, String tkWo4, String ownerGroup, String statusDate, String tkCustomHeader01, int duration){
        boolean insertStatus = false;    
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        
        String insert = "INSERT INTO app_fd_workorder (id, c_wonum, c_crmordertype, c_customer_name, c_serviceaddress, c_description, c_productname, c_producttype, c_scorderno, c_workzone, c_siteid, c_worktype, "
                + "c_schedstart, c_reportedby, c_woclass, c_worevisionno, c_jmscorrelationid, c_status, c_servicenum, c_tk_workorder_04, c_ownergroup, c_statusdate, c_tk_custom_header_01, c_estdur, dateCreated) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        //c_schedstart1 dan c_statusdate1 TEMPORARY masih, angka 1 dihilangkan jika sudah di hapus column di table
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, id);
                    ps.setString(2, wonum);
                    ps.setString(3, crmOrderType);
                    ps.setString(4, custName);
                    ps.setString(5, custAddress);
                    ps.setString(6, description);
                    ps.setString(7, prodName);
                    ps.setString(8, prodType);
                    ps.setString(9, scOrderNo);
                    ps.setString(10, workZone);
                    ps.setString(11, siteId);
                    ps.setString(12, workType);
                    ps.setTimestamp(13, schedStart == null ? null : Timestamp.valueOf(schedStart));
                    ps.setString(14, reportBy);
                    ps.setString(15, woClass);
                    ps.setString(16, woRevisionNo);
                    ps.setString(17, jmsCorrelationId);
                    ps.setString(18, status);
                    ps.setString(19, serviceNum);
                    ps.setString(20, tkWo4);
                    ps.setString(21, ownerGroup);
                    ps.setTimestamp(22, Timestamp.valueOf(statusDate));
                    ps.setString(23, tkCustomHeader01);
                    ps.setInt(24, duration);
                    ps.setTimestamp(25, getTimeStamp());
                    
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        insertStatus = true;
                        LogUtil.info(getClass().getName(), "Work Order param for '" + wonum + "' inserted to DB");
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

     public boolean insertToWoTable2(JSONObject param){
        boolean insertStatus = false;    
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        
        String insert = "INSERT INTO app_fd_workorder (id, c_wonum, c_crmordertype, c_customer_name, c_serviceaddress, c_description, c_productname, c_producttype, c_scorderno, c_workzone, c_siteid, c_worktype, "
                + "c_schedstart, c_reportedby, c_woclass, c_worevisionno, c_jmscorrelationid, c_status, c_servicenum, c_tk_workorder_04, c_ownergroup, c_statusdate, c_tk_custom_header_01, c_estdur, c_latitude, c_longitude, dateCreated) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        //c_schedstart1 dan c_statusdate1 TEMPORARY masih, angka 1 dihilangkan jika sudah di hapus column di table
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
                    ps.setString(6, param.get("description").toString());
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
                    ps.setInt(24, (int) param.get("duration"));
                    ps.setString(25, param.get("latitude").toString());
                    ps.setString(26, param.get("longitude").toString());
                    ps.setTimestamp(27, getTimeStamp());
                    
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        insertStatus = true;
                        LogUtil.info(getClass().getName(), "Work Order param for '" + param.get("wonum").toString() + "' inserted to DB");
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
        String insert = "INSERT INTO app_fd_ossitem (id, c_wonum, c_action, c_correlationid, c_itemname, dateCreated) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, uuId);
                    ps.setString(2, taskObj.get("wonum").toString());
                    ps.setString(3, "ADD");
                    ps.setString(4, taskObj.get("correlation").toString());
                    ps.setString(5, taskObj.get("activity").toString());
                    ps.setTimestamp(6, getTimeStamp());
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        insertStatus = true;
                        LogUtil.info(getClass().getName(), "ADD | OSS Item : " + taskObj.get("activity").toString() + ", CorrelationId : " + taskObj.get("correlation").toString() + ", insert to DB");
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
//        boolean insertStatus = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO app_fd_ossitemattribute (id, c_ossitemattributeid, c_ossitemid, c_attr_name, c_attr_value, c_wonum, dateCreated) VALUES (?, WFMDBDEV01.OSSITEMATTRIBUTEIDSEQ.NEXTVAL, WFMDBDEV01.OSSITEMIDSEQ.NEXTVAL, ?, ?, ?, ?)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, uuId);
                    ps.setString(2, ossItem.get("attrName").toString());
                    ps.setString(3, ossItem.get("attrValue").toString());
                    ps.setString(4, wonum);
                    ps.setTimestamp(5, getTimeStamp());
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
//                        insertStatus = true;
                        LogUtil.info(getClass().getName(), "Add | OssAttr : " + ossItem.get("attrName").toString() + ", Value: " + ossItem.get("attrValue").toString());
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
//        return insertStatus;
    }
    
    public boolean insertToWoAttrTable(String wonum, ListAttributes listAttr){
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
        boolean insertStatus = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO app_fd_workorderattribute (id, c_workorderattributeid, c_wonum, c_attr_name, c_attr_value, dateCreated) VALUES (?, WFMDBDEV01.WORKORDERATTRIBUTEIDSEQ.NEXTVAL, ?, ?, ?, ?)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, uuId);
                    ps.setString(2, wonum);
                    ps.setString(3, listAttr.getTlkwoAttrName());
                    ps.setString(4, listAttr.getTlkwoAttrValue());
                    ps.setTimestamp(5, getTimeStamp());
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        insertStatus = true;
                        LogUtil.info(getClass().getName(), "Add | Attr : " + listAttr.getTlkwoAttrName() + ", Value: " + listAttr.getTlkwoAttrValue() + ", sequence: " + listAttr.getSequence());
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
    
    public JSONObject getWoAttrName(String wonum, String attrName) throws SQLException {
        JSONObject attrProp = new JSONObject();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_attr_name, c_attr_value FROM app_fd_workorderattribute WHERE c_wonum = ? AND c_attr_name = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, attrName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                attrProp.put("attr_name", rs.getString("c_attr_name"));
                attrProp.put("attr_value", (rs.getString("c_attr_value") == null ? "" : rs.getString("c_attr_value")));
            } else {
                attrProp = null;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return attrProp;
    }
    
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"); 
        Timestamp ts =  Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
}
