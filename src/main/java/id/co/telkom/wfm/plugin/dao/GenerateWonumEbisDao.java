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
            }  
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return wonum;
    }
    
     public boolean insertToWoTable(String id, String wonum, String crmOrderType, String custName, String custAddress, String description, String prodName, String prodType, String scOrderNo, String workZone, String siteId, String workType, String schedStart, String reportBy,  String woClass, String woRevisionNo, String jmsCorrelationId, String status, String serviceNum, String tkWo4, String ownerGroup){
        boolean insertStatus = false;    
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO app_fd_workorder (id, c_wonum, c_crmordertype, c_customer_name, c_serviceaddress, c_description, c_productname, c_producttype, c_scorderno, c_workzone, c_siteid, c_worktype, c_schedstart, c_reportedby, c_woclass, c_worevisionno, c_jmscorrelationid, c_status, c_servicenum, c_tk_workorder_04, c_ownergroup, dateCreated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
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
                    ps.setString(13, schedStart);
                    ps.setString(14, reportBy);
                    ps.setString(15, woClass);
                    ps.setString(16, woRevisionNo);
                    ps.setString(17, jmsCorrelationId);
                    ps.setString(18, status);
                    ps.setString(19, serviceNum);
                    ps.setString(20, tkWo4);
                    ps.setString(21, ownerGroup);
//                    ps.setString(20, listAttr.getTechCode());
//                    ps.setString(21, listAttr.getTechName());
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
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return insertStatus;
    }
       
    public boolean insertToOssItem(String wonum, ListOssItem listOssItem){
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
        boolean insertStatus = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO app_fd_ossitem (id, c_wonum, c_action, c_correlationid, c_itemname, dateCreated, dateModified) VALUES (?, ?, ?, ?, ?, sysdate, sysdate)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, uuId);
                    ps.setString(2, wonum);
                    ps.setString(3, listOssItem.getAction());
                    ps.setString(4, listOssItem.getCorrelationid());
                    ps.setString(5, listOssItem.getItemname());
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        insertStatus = true;
                        LogUtil.info(getClass().getName(), "ADD | OSS Item : " + listOssItem.getItemname() + ", CorrelationId : " + listOssItem.getCorrelationid() + ", insert to DB");
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
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return insertStatus;
    }

    public boolean insertToOssAttribute(ListOssItemAttribute listOssItemAtt){
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
//        String ossItemId = insertToOssItem.uuId;
        boolean insertStatus = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO app_fd_ossitemattribute (id, c_ossitemattributeid, c_ossitemid, c_attr_name, c_attr_value, dateCreated, dateModified) VALUES (?, WFMDBDEV01.OSSITEMATTRIBUTEIDSEQ.NEXTVAL, WFMDBDEV01.OSSITEMIDSEQ.NEXTVAL, ?, ?, sysdate, sysdate)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, uuId);
                    ps.setString(2, listOssItemAtt.getAttrName());
                    ps.setString(3, listOssItemAtt.getAttrValue());
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        insertStatus = true;
                        LogUtil.info(getClass().getName(), "Add | OssAttr : " + listOssItemAtt.getAttrName() + ", Value: " + listOssItemAtt.getAttrValue());
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
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return insertStatus;
    }
    
        public void ossItemAttributeLoop (Object attrObj, ListOssItemAttribute listOssItemAtt){
            //Attribute level
            if (attrObj instanceof JSONObject){
                listOssItemAtt.setAttrName(((JSONObject) attrObj).get("ATTR_NAME").toString());
                listOssItemAtt.setAttrValue(((JSONObject) attrObj).get("ATTR_VALUE").toString());
                insertToOssAttribute(listOssItemAtt);
            } else if (attrObj instanceof JSONArray){
                for (int j = 0 ; j < ((JSONArray) attrObj).size() ; j++ ){
                    JSONObject deviceAttr = (JSONObject)((JSONArray) attrObj).get(j);
                    listOssItemAtt.setAttrName(deviceAttr.get("ATTR_NAME").toString());
                    listOssItemAtt.setAttrValue(deviceAttr.get("ATTR_VALUE").toString());
                    insertToOssAttribute(listOssItemAtt);
                }
            }
        }
    
    public boolean insertToWoAttrTable(String wonum, ListAttributes listAttr){
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
        boolean insertStatus = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO app_fd_workorderattribute (id, c_workorderattributeid, c_wonum, c_attr_name, c_attr_value, c_sequence, dateCreated) VALUES (?, WFMDBDEV01.WORKORDERATTRIBUTEIDSEQ.NEXTVAL, ?, ?, ?, ?, sysdate)";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert);
                try {
                    ps.setString(1, uuId);
                    ps.setString(2, wonum);
                    ps.setString(3, listAttr.getTlkwoAttrName());
                    ps.setString(4, listAttr.getTlkwoAttrValue());
                    ps.setString(5, listAttr.getSequence());
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
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return insertStatus;
    }
 
    
    public Object getLaborNewManja(String wonum, String bookId) throws SQLException{
    //Prepare variable
    JSONObject data_obj = null;
    JSONObject api_obj;
//        api_obj = null;
    String response;
    //Get configuration to 'POST'
    RequestAPI api = new RequestAPI();
    ConnUtil connUtil = new ConnUtil();
    api_obj = connUtil.getEnvVariableScheduling();
    try {
        APIConfig apiConfig = new APIConfig();
        apiConfig.setUrl(api_obj.get("SCHEDULING_BASE").toString() + "/jw/web/json/plugin/id.co.telkom.scheduling.plugin.ListSchedule/service");
        apiConfig.setApiId(api_obj.get("SCHEDULING_API_ID").toString());
        apiConfig.setApiKey(api_obj.get("SCHEDULING_API_KEY").toString());
        //Create request
        JSONObject req = new JSONObject();
        req.put("action", "autoAssign");
        req.put("externalID", "INC10145");
        req.put("bookingID", bookId);
        req.put("wonum", wonum);
        req.put("rk", "ODC-BEK-FCQ");
        String json = req.toJSONString();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),json);
        //Send request with okhttp
        response = api.sendPostWithoutToken(apiConfig, body);
        //Parse response
        JSONParser parse = new JSONParser();
        data_obj = (JSONObject)parse.parse(response);
    } catch (ParseException e) {
        LogUtil.error(getClass().getName(), e, "Error getting labor: " + e.getMessage());
    } catch (Exception e) {
        LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
    }
    return data_obj;
    }
}
