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
    public JSONObject getWorkorder(String parent) throws SQLException {
        JSONObject activityProp = new JSONObject();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT DISTINCT ")
                .append(" child.c_parent, ")
                .append(" parent.c_workzone, ")
                .append(" parent.c_scorderno, ")
                .append(" parent.c_siteid, ")
                .append(" parent.c_serviceaddress, ")
                .append(" parent.c_productname, ")
                .append(" parent.c_statusdate, ")
                .append(" child.c_ownergroup, ")
                .append(" parent.c_customer_name, ")
                .append(" child.c_wonum ")
                .append(" FROM app_fd_workorder parent ")
                .append(" LEFT JOIN APP_FD_WORKORDER child ON parent.c_wonum = child.C_PARENT ")
                .append(" WHERE child.C_DETAILACTCODE = 'Shipment_Delivery' ")
                .append(" AND child.c_parent = ? ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activityProp.put("parent", rs.getString("c_parent"));
                activityProp.put("wonum", rs.getString("c_wonum"));
                activityProp.put("customerName", rs.getString("c_customer_name"));
                activityProp.put("ownerGroup", rs.getString("c_ownergroup"));
                activityProp.put("workzone", rs.getString("c_workzone"));
                activityProp.put("scOrderNo", rs.getString("c_scorderno"));
                activityProp.put("siteid", rs.getString("c_siteid"));
                activityProp.put("serviceAddress", rs.getString("c_serviceaddress"));
                activityProp.put("productName", rs.getString("c_productname"));
                activityProp.put("statusDate", rs.getString("c_statusdate"));
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
    
    public JSONObject getDocLinks(String wonum, String objName) throws SQLException {
        JSONObject activityProp = new JSONObject();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT DISTINCT ")
                .append(" C_WONUM, ")
                .append(" c_OBJECTNAME, ")
                .append(" C_FILENAME, ")
                .append(" C_DOCUMENTNAME, ")
                .append(" C_URL ")
                .append(" FROM app_fd_doclinks ")
                .append(" WHERE c_objectname = ? ")
                .append(" AND c_wonum = ? ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, objName);
            ps.setString(2, wonum);
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
    
    public boolean getUrl(String wonum, String objName) throws SQLException {
        boolean isUrl = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String selectQuery = "SELECT c_url FROM app_fd_doclinks WHERE c_wonum = ? AND c_objectname = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, wonum);
            ps.setString(2, objName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("c_url") == null) {
                    isUrl = true;
//                    LogUtil.info(getClass().getName(), "URL = " + isUrl);   
                } else {
                    isUrl = false;
//                    LogUtil.info(getClass().getName(), "URL = " + isUrl);
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return isUrl;
    }
    
    public String getObjectName(String wonum, String documentName) throws SQLException {
        String objName = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String selectQuery = "SELECT c_objectname FROM app_fd_doclinks WHERE c_wonum = ? AND c_documentname = ? AND c_filename like '%.csv'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, wonum);
            ps.setString(2, documentName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (rs.getString("c_objectname") != null) {
                    objName = rs.getString("c_objectname").toString();
//                    LogUtil.info(getClass().getName(), "Object Name = " + objName);
                } else {
                    objName = "";
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return objName;
    }
}
