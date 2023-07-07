/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.ListDevice;
import id.co.telkom.wfm.plugin.model.ListDeviceAttribute;
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
import java.util.ArrayList;
import java.util.HashMap;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author ASUS
 */
public class GenerateStpNetLocDao {

    // ==========================================
    // Call API Surrounding Generate STP Net Loc
    //===========================================
    public String callGenerateStpNetLoc(String latitude, String longitude) throws JSONException, IOException, MalformedURLException, Exception {
        // Request Structure
        try {
            String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <ent:findDeviceByCriteriaRequest>\n"
                    + "       <!--Optional:-->\n"
                    + "         <ServiceLocation>\n"
                    + "            <!--Optional:-->\n"
                    + "            <latitude>" + latitude + "</latitude>\n"
                    + "            <longitude>" + longitude + "</longitude>\n"
                    + "         </ServiceLocation>\n"
                    + "         <DeviceInfo>\n"
                    + "            <role>STP</role>\n"
                    + "            <!--Optional:-->\n"
                    + "            <detail>false</detail>\n"
                    + "         </DeviceInfo>\n"
                    + "      </ent:findDeviceByCriteriaRequest>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";
            String urlres = "http://10.6.28.132:7001/EnterpriseFeasibilityUim/EnterpriseFeasibilityUimHTTP";
            URL url = new URL(urlres);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            // Set Headers
            connection.setRequestProperty("Accept", "application/xml");
            connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            try ( // Write XML
                    OutputStream outputStream = connection.getOutputStream()) {
                byte[] b = request.getBytes("UTF-8");
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

            // Parsing data response
            LogUtil.info(this.getClass().getName(), "############ Parsing Data Response ##############");

            JSONObject envelope = temp.getJSONObject("env:Envelope").getJSONObject("env:Body");
            JSONObject device = envelope.getJSONObject("ent:findDeviceByCriteriaResponse");
            int statusCode = device.getInt("statusCode");
            
            LogUtil.info(this.getClass().getName(), "Response Status : " + statusCode);


            if (statusCode != 4000) {
                LogUtil.info(this.getClass().getName(), "No Device found.");
            } else if (statusCode != 4001) {
                JSONObject deviceInfo = device.getJSONObject("DeviceInfo");
                String name = deviceInfo.getString("name");
                String type = deviceInfo.getString("networkLocation");
                LogUtil.info(this.getClass().getName(), "Device : " + name + type);
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Call Failed." + e);
        }
        return null;
    }

    //==========================================
    //  Get Location From WORKORDERATTRIBUTE
    //==========================================
    public JSONObject getDeviceLocation(String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT C_ATTR_NAME, C_ATTR_VALUE FROM APP_FD_WORKORDERATTRIBUTE WHERE C_WONUM = ? AND C_ATTR_NAME IN ('LONGITUDE', 'LATITUDE')";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultObj.put(rs.getString("C_ATTR_NAME"), rs.getString("C_ATTR_VALUE"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return resultObj;
    }

    public String moveFirst(String wonum) throws SQLException {
        String moveFirst = "";
//        String insert = "INSERT INTO app_fd_tk_deviceattribute (id, c_ref_num, c_attr_name, c_attr_type, description) VALUES (?, ?, ?, ?, ?)";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT * FROM APP_FD_TK_DEVICEATTRIBUTE WHERE c_ref_num = ? AND c_attr_name in ('STP_NETWORKLOCATION')";
        try (Connection con = ds.getConnection();
                //                PreparedStatement stmt = con.prepareStatement(insert);
                PreparedStatement ps = con.prepareStatement(query);) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs != null) {
                String delete = "DELETE FROM APP_FD_TK_DEVICEATTRIBUTE";
                ResultSet del = ps.executeQuery(delete);
                moveFirst = "Deleted data";
                LogUtil.info(getClass().getName(), "Berhasil menghapus data" + del);
            } else {
                LogUtil.info(getClass().getName(), "Gagal menghapus data");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return moveFirst;
    }

//    public void datas(PreparedStatement ps, String refNum, String attrName, String type, String description) throws SQLException {
//        ps.setString(1, UuidGenerator.getInstance().getUuid());
//        ps.setString(2, refNum);
//        ps.setString(2, attrName);
//        ps.setString(2, type);
//        ps.setString(2, "STP_NETWORKLOCATION");
//    }
    public void insertToDeviceTable(String wonum, HashMap<String, String> data) throws Throwable {
        // Generate UUID
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder insert = new StringBuilder();
        insert
                .append("INSERT INTO app_fd_tk_deviceattribute")
                .append("(")
                .append("id,")
                .append("c_ref_num,")
                .append("c_attr_name,")
                .append("c_attr_type,")
                .append("description")
                .append(")")
                .append("VALUES")
                .append("(")
                .append("?,")
                .append("?,")
                .append("?,")
                .append("?,")
                .append("?");

        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(insert.toString())) {
            ps.setString(1, uuId);
            ps.setString(2, data.get("attrName"));
            ps.setString(2, data.get("attrType"));
            ps.setString(2, data.get("description"));
            ps.setString(2, wonum);
        }
    }
}
