/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//import org.json.simple.JSONArray;

/**
 *
 * @author ASUS
 */
public class GenerateMeServiceDao {

    public JSONObject getAssetattridType(String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid IN ('PE_NAME','PE_PORTNAME', 'ME_SERVICE_IPADDRESS', 'NTE_TYPE')";
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

    public boolean updateDeviceLinkPortByIp(String wonum, JSONArray manufacture, JSONArray name, JSONArray ipAddress) throws SQLException {
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update.append("UPDATE APP_FD_WORKORDERSPEC")
                .append("SET c_value = CASE c_assetattrid")
                .append("WHEN 'ME_SERVICE_MANUFACTURE' THEN ?")
                .append("WHEN 'ME_SERVICE_NAME' THEN ?")
                .append("WHEN 'ME_SERVICE_IPADDRESS' THEN ?")
                .append("ELSE 'Missing' END")
                .append("WHERE c_wonum = ?");
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update.toString());
                try {
//                    ps.setString(1, manufacture.toJSONString());
//                    ps.setString(2, name.toJSONString());
//                    ps.setString(3, ipAddress.toJSONString());
                    ps.setString(5, wonum);

                    int exe = ps.executeUpdate();
                    if (exe > 0) {
                        result = true;
                        LogUtil.info(getClass().getName(), "ME Service updated to " + wonum);
                    }
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Throwable throwable) {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null) {
                    con.close();
                }
            } catch (Throwable throwable) {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return result;
    }

    public boolean updateDeviceLinkPort(String wonum, String manufacture, String name, String ipAddress, String mtu, String key, String portName) throws SQLException {
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update.append("UPDATE APP_FD_WORKORDERSPEC")
                .append("SET c_value = CASE c_assetattrid")
                .append("WHEN 'ME_SERVICE_MANUFACTURE' THEN ?")
                .append("WHEN 'ME_SERVICE_NAME' THEN ?")
                .append("WHEN 'ME_SERVICE_IPADDRESS' THEN ?")
                .append("WHEN 'ME_SERVICE_PORT_MTU' THEN ?")
                .append("WHEN 'ME_SERVICE_KEY' THEN ?")
                .append("WHEN 'ME_SERVICE_PORTNAME' THEN ?")
                .append("ELSE 'Missing' END")
                .append("WHERE c_wonum = ?");
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update.toString());
                try {
                    ps.setString(1, manufacture);
                    ps.setString(2, name);
                    ps.setString(3, ipAddress);
                    ps.setString(4, mtu);
                    ps.setString(5, key);
                    ps.setString(6, portName);
                    ps.setString(7, wonum);

                    int exe = ps.executeUpdate();
                    if (exe > 0) {
                        result = true;
                        LogUtil.info(getClass().getName(), "ME Service updated to " + wonum);
                    }
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Throwable throwable) {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null) {
                    con.close();
                }
            } catch (Throwable throwable) {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return result;
    }

    public JSONArray callGenerateMeService(String wonum) {
        try {
            String url = "https://api-emas.telkom.co.id:8443/api/device/linkedPort?" + "deviceName=" + getAssetattridType(wonum).get("PE_NAME").toString() + "&portName=" + getAssetattridType(wonum).get("PE_PORTNAME").toString().replace("/", "%2F") + "&deviceLink=" + "PE_METROE" + "&portStatus=ACTIVE";
            String urlByIp = "https://api-emas.telkom.co.id:8443/api/device/find?" + "ipAddress=" + getAssetattridType(wonum).get("ME_SERVICE_IPADDRESS").toString();
            URL getDeviceLinkPort = new URL(url);
            URL getDeviceLinkPortByIp = new URL(urlByIp);

            if (getAssetattridType(wonum).getString("NTE_TYPE") != null) {

                if (getAssetattridType(wonum).getString("NTE_TYPE") == "DirectME") {
                    HttpURLConnection con = (HttpURLConnection) getDeviceLinkPortByIp.openConnection();

                    con.setRequestMethod("GET");
                    con.setRequestProperty("Accept", "application/json");
                    int responseCode = con.getResponseCode();
                    LogUtil.info(this.getClass().getName(), "\nSending 'GET' request to URL : " + urlByIp);
                    LogUtil.info(this.getClass().getName(), "Response Code : " + responseCode);

                    if (responseCode == 400) {
                        LogUtil.info(this.getClass().getName(), "STO not found");

                    } else if (responseCode == 200) {
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        String inputLine;
                        StringBuffer response = new StringBuffer();
                        while ((inputLine = in.readLine()) != null) {
                            response.append(inputLine);
                        }
                        LogUtil.info(this.getClass().getName(), "ME Service : " + response);
                        in.close();

                        // At this point, 'response' contains the JSON data as a string
                        String jsonData = response.toString();

                        // Now, parse the JSON data using org.json library
                        JSONArray jsonArr = new JSONArray(jsonData);

                    }
                } else {
                    HttpURLConnection con = (HttpURLConnection) getDeviceLinkPort.openConnection();

                    con.setRequestMethod("GET");
                    con.setRequestProperty("Accept", "application/json");
                    int responseCode = con.getResponseCode();
                    LogUtil.info(this.getClass().getName(), "\nSending 'GET' request to URL : " + url);
                    LogUtil.info(this.getClass().getName(), "Response Code : " + responseCode);

                    if (responseCode == 400) {
                        LogUtil.info(this.getClass().getName(), "STO not found");

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

                        LogUtil.info(this.getClass().getName(), "ME_SERVICE_MANUFACTUR : " + manufactur);
                        LogUtil.info(this.getClass().getName(), "ME_SERVICE_NAME : " + name);
                        LogUtil.info(this.getClass().getName(), "ME_SERVICE_IPADDRESS : " + ipAddress);
                        LogUtil.info(this.getClass().getName(), "ME_SERVICEC_PORT_MTU : " + mtu);
                        LogUtil.info(this.getClass().getName(), "ME_SERVICE_KEY : " + key);
                        LogUtil.info(this.getClass().getName(), "ME_SERVICE_PORTNAME : " + portName);

                        // Update STO, REGION, WITEL, DATEL from table WORKORDERSPEC
                        updateDeviceLinkPort(wonum, manufactur, name, ipAddress, mtu, key, name);
                    }
                }
            }

        } catch (Exception e) {
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return null;
    }
}
