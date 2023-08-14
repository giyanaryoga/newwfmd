/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author ASUS
 */
public class AbortOrderDao {

    // =====================
    // Get Request data 
    //======================
    public JSONObject getReqData(String wonum) throws SQLException, JSONException {
        JSONObject result = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_scorderno FROM APP_FD_WORKORDER WHERE c_num = ? AND c_woclass = 'WORKORDER'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query);) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.put("scorderno", rs.getString("c_scorderno"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return result;
    }

    // =====================
    // Call API Abort Order 
    //======================
    public JSONObject AbortOrder(String wonum, String parent) throws MalformedURLException, IOException {
        TimeUtil time = new TimeUtil();
        try {
            String scorderno = getReqData(wonum).get("scorderno").toString();

            String[] parts = scorderno.split("_");
            String orderId = parts[0];
            String serviceAccountId = parts[1];
            String assetIntegrationId = parts[2];

            String abortReason = "Abord By User";
            
            String soapRequest = createSoapRequestAbort(orderId, serviceAccountId, assetIntegrationId, abortReason);

            String urlres = "http://eaiesbinfdev.telkom.co.id:9122/ws/telkom.nb.siebel.webservices:abortOrder/telkom_nb_siebel_webservices_abortOrder_Port";

            URL url = new URL(urlres);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            // Set Headers
            connection.setRequestProperty("Accept", "application/xml");
            connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            try ( // Write XML
                    OutputStream outputStream = connection.getOutputStream()) {
                byte[] b = soapRequest.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }

            StringBuilder response;
            try ( // Read XML
                    InputStream inputStream = connection.getInputStream()) {
                byte[] res = new byte[2048];
                int i = 0;
                response = new StringBuilder();
                while ((i = inputStream.read(res)) != -1) {
                    response.append(new String(res, 0, i));
                }
            }
            StringBuilder result = response;
            JSONObject temp = XML.toJSONObject(result.toString());
            System.out.println("temp " + temp.toString());
            LogUtil.info(this.getClass().getName(), "INI RESPONSE : " + temp.toString());

            JSONObject envelope = temp.getJSONObject("Envelope").getJSONObject("Body");
            JSONObject abortRes = envelope.getJSONObject("abortOrderResponse");
            String statusMsg = abortRes.getString("statusMsg");

            LogUtil.info(this.getClass().getName(), "Status Message :" + statusMsg);
            String currentDate = time.getCurrentTime();
            if (statusMsg.equals("SUCCESS")) {
                updateTask(wonum, "CANCLE");
                updateParentStatus(parent, "CANCLEWORK", currentDate);
            } else {
                LogUtil.info(this.getClass().getName(), "Abort Order Failed.");
            }

        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Call Failed." + e);
        }
        return null;
    }

    public boolean updateTask(String wonum, String status) throws SQLException {
        boolean updateTask = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_status = ?, dateModified = sysdate WHERE c_wonum = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, status);
            ps.setString(2, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "update task berhasil");
                updateTask = true;
            } else {
                LogUtil.info(getClass().getName(), "update task gagal");
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return updateTask;
    }

    public void updateParentStatus(String wonum, String status, String statusDate) throws SQLException {
        String update = "UPDATE app_fd_workorder SET c_status = ?, c_statusdate = ?, dateModified = sysdate WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            int index = 0;
            ps.setString(1 + index, status);
            ps.setString(2 + index, statusDate);
            ps.setString(3 + index, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Status updated to: " + status);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public String createSoapRequestAbort(String orderId, String serviceAccountId, String assetIntegrationId, String abortReason) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:tel=\"http://eaiesbinf.telkom.co.id:9122/telkom.nb.siebel.webservices:abortOrder\">\n"
                + "    <soapenv:Header/>\n"
                + "    <soapenv:Body>\n"
                + "        <tel:abortOrder>\n"
                + "            <orderId>" + orderId + "</orderId>\n"
                + "            <serviceAccountId>" + serviceAccountId + "</serviceAccountId>\n"
                + "            <assetIntegrationId>" + assetIntegrationId + "</assetIntegrationId>\n"
                + "            <abortReason>" + abortReason + "</abortReason>\n"
                + "        </tel:abortOrder>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return request;
    }
}
