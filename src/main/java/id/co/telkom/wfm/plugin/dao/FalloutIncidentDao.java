/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
 * @author ASUS
 */
public class FalloutIncidentDao {

    public boolean updateStatus(String statusCode, String ticketId) throws SQLException {
        boolean updateStatus = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE APP_FD_INCIDENT SET C_TK_STATUSCODE = ? WHERE C_TICKETID = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, statusCode);
            ps.setString(2, ticketId);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "update status berhasil");
                updateStatus = true;
            } else {
                LogUtil.info(getClass().getName(), "update status gagal");
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return updateStatus;
    }

    public JSONObject buildFalloutJson(String ticketId) throws SQLException {
        JSONObject itemObject = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ticketid, c_tk_channel, c_tk_classification, c_tk_ossid, c_tk_statuscode  FROM app_fd_incident";
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, ticketId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                itemObject.put("FALLOUTID", rs.getString("c_ticketid"));
                itemObject.put("TK_CHANNEL", rs.getString("c_tk_channel"));
                itemObject.put("FAULTCATEGORY", rs.getString("c_tk_classification"));
                itemObject.put("OSSID", rs.getString("c_tk_ossid"));
                itemObject.put("TK_STATUSCODE", rs.getString("c_tk_statuscode"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        
        // XML Wrapper
        itemObject.put("SYMPTOMS", "GAMAS");
        itemObject.put("SID", "DUMMY");
        itemObject.put("FAULSTATE", "RESOLVED");
        
        JSONObject attrs = new JSONObject();
        attrs.put("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        attrs.put("xmlns:tel", "http://eaiesb.telkom.co.id:9121/telkom.nb.osb.assurance.ws:faultOSS");
        JSONObject header = new JSONObject();
        header.put("@ns", "soapenv");

        JSONObject faultOss = new JSONObject();
        faultOss.put("FaultOSSRcv", itemObject);
        faultOss.put("@ns", "tel");
        
        JSONObject body = new JSONObject();
        body.put("faultOSS", faultOss);
        body.put("@ns", "soapenv");

        JSONObject envelope = new JSONObject();
        envelope.put("Header", header);
        envelope.put("Body", body);
        envelope.put("@ns", "soapenv");
        envelope.put("@attrs", attrs);
        
        JSONObject falloutJson = new JSONObject();
        falloutJson.put("Envelope", envelope);
        //End of wrapper
        return falloutJson;
    }
}
