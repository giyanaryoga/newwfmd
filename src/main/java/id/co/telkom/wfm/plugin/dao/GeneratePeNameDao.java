/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
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

/**
 *
 * @author ASUS
 */
public class GeneratePeNameDao {

    public JSONObject getAssetattridType(String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid IN ('DEVICETYPE', 'AREANAME', 'AREATYPE', 'SERVICE_TYPE')";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                resultObj.put(rs.getString("c_assetattrid"), rs.getString("c_value"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

    public JSONObject callGeneratePeName(String wonum) throws MalformedURLException {
        try {
            String deviceType = getAssetattridType(wonum).get("DEVICETYPE").toString();
            String areaName = getAssetattridType(wonum).get("AREANAME").toString();
            String areaType = getAssetattridType(wonum).get("AREATYPE").toString();
            String serviceType = getAssetattridType(wonum).get("DEVICETYPE").toString();

            String url = "https://api-emas.telkom.co.id:8443/api/device/byServiceArea?" + "deviceType=" + deviceType + "&areaName=" + areaName + "&areaType=" + areaType + "&serviceType=" + serviceType;

            URL getUrlServiveByArea = new URL(url);

            HttpURLConnection con = (HttpURLConnection) getUrlServiveByArea.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int responseCode = con.getResponseCode();
            LogUtil.info(this.getClass().getName(), "\nSending 'GET' request to URL : " + url);
            LogUtil.info(this.getClass().getName(), "Response Code : " + responseCode);

            if (responseCode == 404) {
                LogUtil.info(this.getClass().getName(), "ME Access not found!");
            } else if (responseCode == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                LogUtil.info(this.getClass().getName(), "STO : " + response);
                in.close();

                // At this point, 'response' contains the JSON data as a string
                String jsonData = response.toString();

                // Now, parse the JSON data using org.json library
                JSONObject jsonObject = new JSONObject(jsonData);
                JSONObject jsonObj = jsonObject.getJSONObject("device");
                // Access data from the JSON object as needed
                String manufactur = jsonObj.getString("manufacturer");
                String name = jsonObj.getString("name");
                String ipAddress = jsonObj.getString("ipAddress");
                String mtu = jsonObject.getString("mtu");
                String key = jsonObject.getString("key");
                String portName = jsonObject.getString("name");

                LogUtil.info(this.getClass().getName(), "===============PARSING DATA==============");
                LogUtil.info(this.getClass().getName(), "ME_MANUFACTUR : " + manufactur);
                LogUtil.info(this.getClass().getName(), "ME_NAME : " + name);
                LogUtil.info(this.getClass().getName(), "ME_IPADDRESS : " + ipAddress);
                LogUtil.info(this.getClass().getName(), "ME_PORT_MTU : " + mtu);
                LogUtil.info(this.getClass().getName(), "ME_PORTID : " + key);
                LogUtil.info(this.getClass().getName(), "ME_PORTNAME : " + portName);

                // Update STO, REGION, WITEL, DATEL from table WORKORDERSPEC
//                        updateDeviceLinkPort(wonum, manufactur, name, ipAddress, mtu, key, portName);
            }

        } catch (Exception e) {
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return null;
    }
}
