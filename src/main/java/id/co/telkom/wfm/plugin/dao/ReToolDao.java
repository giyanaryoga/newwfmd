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
                .append(" SELECT ")
                .append(" c_wonum, ")
                .append(" c_workzone, ")
                .append(" c_scorderno, ")
                .append(" c_siteid, ")
                .append(" c_serviceaddress, ")
                .append(" c_productname, ")
                .append(" c_statusdate, ")
                .append(" c_ownergroup, ")
                .append(" c_customer_name ")
                .append(" FROM app_fd_workorder WHERE ")
                .append(" c_woclass = 'WORKORDER' ")
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
}
