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
                .append(" child.C_WOSEQUENCE, ")
                .append(" FROM app_fd_workorder parent, app_fd_workorder child WHERE ")
                .append(" parent.C_WONUM = child.C_PARENT ")
                .append(" AND child.C_DETAILACTCODE = 'Shipment_Delivery' ")
                .append(" c_wonum = ? ");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
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
}
