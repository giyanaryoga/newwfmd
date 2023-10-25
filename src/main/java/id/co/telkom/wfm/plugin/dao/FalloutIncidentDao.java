/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

//import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.kafka.ResponseKafka;
import id.co.telkom.wfm.plugin.model.ListFormatFallout;
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

    public boolean updateStatus(String statusCode, String ticketId, String ownerGroup) throws SQLException {
        boolean updateStatus = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE APP_FD_INCIDENT SET C_TK_STATUSCODE = ?, C_OWNERGROUP = ?, datemodified = sysdate WHERE C_TICKETID = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, statusCode);
            ps.setString(2, ownerGroup);
            ps.setString(3, ticketId);
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

    public void buildFalloutJson(String ticketId) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ticketid, c_tk_channel, c_tk_classification, c_tk_ossid, c_tk_statuscode, c_tk_region, datemodified  FROM app_fd_incident WHERE c_ticketid = ?";
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            String region = "";
            ps.setString(1, ticketId);
            ResultSet rs = ps.executeQuery();
            JSONObject falloutMessage = new JSONObject();
            while (rs.next()) {
                ListFormatFallout format = new ListFormatFallout();
                format.setTicketId((rs.getString("c_ticketid") == null) ? "" : rs.getString("c_ticketid"));
                format.setTkChannel((rs.getString("c_tk_channel") == null) ? "" : rs.getString("c_tk_channel"));
                format.setClassification((rs.getString("c_tk_classification") == null) ? "" : rs.getString("c_tk_classification"));
                format.setOssid((rs.getString("c_tk_ossid") == null) ? "" : rs.getString("c_tk_ossid"));
                format.setStatusCode((rs.getString("c_tk_statuscode") == null) ? "" : rs.getString("c_tk_statuscode"));
                format.setDatemodified((rs.getString("datemodified") == null) ? "" : rs.getString("datemodified"));
                region = rs.getString("c_tk_region");    
                falloutMessage = buildFormatMessage(format);
            }
            String kafkaRes = falloutMessage.toJSONString();
            ResponseKafka responseKafka = new ResponseKafka();
            //KAFKA DEVELOPMENT
            responseKafka.FalloutIncident(kafkaRes, region);
            //KAFKA PRODUCTION
//            responseKafka.FalloutIncident(kafkaRes, region);
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public JSONObject buildFormatMessage(ListFormatFallout format) {
        String timestamp = format.getDatemodified();
        String[] datemodified = timestamp.split(",");
        String date = datemodified[0];
        
        JSONObject itemObject = new JSONObject();
        itemObject.put("FAULTID", format.getTicketId());
        itemObject.put("TK_CHANNEL", format.getTkChannel());
        itemObject.put("FAULTCATEGORY", format.getClassification());
        itemObject.put("OSSID", format.getOssid());
        itemObject.put("TK_STATUSCODE", format.getStatusCode());
        itemObject.put("TIMESTAMP", date);
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
