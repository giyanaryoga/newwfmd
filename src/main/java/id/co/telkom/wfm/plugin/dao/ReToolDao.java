/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class ReToolDao {
    public JSONObject getWorkorder(String wonum) throws SQLException {
        JSONObject activityProp = new JSONObject();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT DISTINCT ")
                .append(" parent.C_WONUM, ")
                .append(" parent.c_workzone, ")
                .append(" parent.C_SCORDERNO, ")
                .append(" parent.C_SITEID, ")
                .append(" parent.C_SERVICEADDRESS, ")
                .append(" parent.C_PRODUCTNAME, ")
                .append(" parent.C_STATUSDATE, ")
                .append(" parent.C_OWNERGROUP, ")
                .append(" parent.C_CUSTOMER_NAME, ")
                .append(" child.C_WONUM, ")
                .append(" child.C_DETAILACTCODE, ")
                .append(" child.C_WOSEQUENCE ")
                .append(" FROM app_fd_workorder parent, app_fd_workorder child WHERE ")
                .append(" parent.C_WONUM = child.C_PARENT ")
                .append(" AND child.C_DETAILACTCODE = 'Shipment_Delivery' ")
                .append(" child.c_wonum = ? ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activityProp.put("parent", rs.getString("parent.C_WONUM"));
                activityProp.put("wonum", rs.getString("child.C_WONUM"));
                activityProp.put("customerName", rs.getString("parent.C_CUSTOMER_NAME"));
                activityProp.put("ownerGroup", rs.getString("parent.C_OWNERGROUP"));
                activityProp.put("workzone", rs.getString("parent.c_workzone"));
                activityProp.put("scOrderNo", rs.getString("parent.C_SCORDERNO"));
                activityProp.put("siteid", rs.getString("parent.C_SITEID"));
                activityProp.put("serviceAddress", rs.getString("parent.C_SERVICEADDRESS"));
                activityProp.put("productName", rs.getString("parent.C_PRODUCTNAME"));
                activityProp.put("statusDate", rs.getString("parent.C_STATUSDATE"));
                activityProp.put("objectName", rs.getString("doc.C_OBJECTNAME"));
                activityProp.put("url", rs.getString("doc.C_URL"));
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
    
    public JSONObject getDocLinks(String wonum) throws SQLException {
        JSONObject activityProp = new JSONObject();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT DISTINCT ")
                .append(" C_WONUM, ")
                .append(" c_OBJECTNAME, ")
                .append(" C_FILENAME, ")
                .append(" C_DOCUMENTNAME, ")
                .append(" C_URL ")
                .append(" FROM app_fd_doclinks child WHERE ")
                .append(" AND C_DOCUMENTNAME = 'SERVICE_DETAIL' ")
                .append(" c_wonum = ? ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activityProp.put("wonum", rs.getString("C_WONUM"));
                activityProp.put("objectName", rs.getString("c_OBJECTNAME"));
                activityProp.put("filename", rs.getString("C_FILENAME"));
                activityProp.put("documentName", rs.getString("C_DOCUMENTNAME"));
                activityProp.put("url", rs.getString("C_URL"));
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
    
    public int checkAttachedFile(String wonum, String documentName) throws SQLException {
        int isAttachedFile = 0;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String selectQuery = "SELECT DISTINCT c_documentname FROM app_fd_doclinks WHERE c_wonum = ? AND c_documentname = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, wonum);
            ps.setString(2, documentName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("c_documentname") != null) {
                    isAttachedFile = 1;
                    LogUtil.info(getClass().getName(), "isAttachedFile = " + isAttachedFile);
                } else {
                    isAttachedFile = 0;
                    LogUtil.info(getClass().getName(), "isAttachedFile = " + isAttachedFile);
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return isAttachedFile;
    }
    
    public String docName(String wonum, String documentName) throws SQLException {
        String file = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String selectQuery = "SELECT DISTINCT c_documentname FROM app_fd_doclinks WHERE c_wonum = ? AND c_documentname = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, wonum);
            ps.setString(2, documentName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("c_documentname") != null) {
                    file = rs.getString("c_documentname").toString();
                    LogUtil.info(getClass().getName(), "File = " + file);
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return file;
    }
    
//    public String getObjectName(String wonum, String documentName) throws SQLException {
//        String objName = "";
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String selectQuery = "SELECT DISTINCT c_objectname FROM app_fd_doclinks WHERE c_wonum = ? AND c_documentname = ? AND c_filename like '%.csv'";
//        try (Connection con = ds.getConnection();
//                PreparedStatement ps = con.prepareStatement(selectQuery)) {
//            ps.setString(1, wonum);
//            ps.setString(2, documentName);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                if (rs.getString("c_objectname") != null) {
//                    objName = rs.getString("c_objectname").toString();
//                    LogUtil.info(getClass().getName(), "Object Name = " + objName);
//                } else {
//                    objName = "";
//                }
//            }
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
//        }
//        return objName;
//    }
}
