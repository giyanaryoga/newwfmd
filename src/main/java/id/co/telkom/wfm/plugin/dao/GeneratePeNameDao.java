/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import id.co.telkom.wfm.plugin.model.ListGenerateAttributes;
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
import org.joget.commons.util.UuidGenerator;
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

    public JSONObject getDetailactcode(String wonum) throws SQLException, JSONException {
        JSONObject result = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_detailactcode FROM APP_FD_WORKORDER WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query);) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result.put("detailactcode", rs.getString("c_detailactcode"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return result;
    }

    public boolean updateCommunityTransit(String wonum, String community) throws SQLException {
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update.append("UPDATE APP_FD_WORKORDERSPEC")
                .append("SET c_value = CASE c_assetattrid")
                .append("WHEN 'COMMUNITY_TRANSIT' THEN ?")
                .append("ELSE 'Missing' END")
                .append("WHERE c_wonum = ?");
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update.toString());
                try {
                    ps.setString(1, community);
                    ps.setString(2, wonum);

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

    public void insertToDeviceTable(String wonum, String name, String type, String description) throws Throwable {
        // Generate UUID
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO APP_FD_TK_DEVICEATTRIBUTE (ID, C_REF_NUM, C_ATTR_NAME, C_ATTR_TYPE, C_DESCRIPTION) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(insert)) {
            ps.setString(1, uuId);
            ps.setString(2, wonum);
            ps.setString(3, name);
            ps.setString(4, type);
            ps.setString(5, description);

            int exe = ps.executeUpdate();

            if (exe > 0) {
                LogUtil.info(this.getClass().getName(), "Berhasil menambahkan data");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public JSONObject callGeneratePeName(String wonum, ListGenerateAttributes listGenerate) throws MalformedURLException {
        try {
            String deviceType = getAssetattridType(wonum).get("DEVICETYPE").toString();
            String areaName = getAssetattridType(wonum).get("AREANAME").toString();
            String areaType = getAssetattridType(wonum).get("AREATYPE").toString();
            String serviceType = getAssetattridType(wonum).get("SERVICE_TYPE").toString();

            String url = "https://api-emas.telkom.co.id:8443/api/device/byServiceArea?" + "deviceType=" + deviceType + "&areaName=" + areaName + "&areaType=" + areaType + "&serviceType=" + serviceType;

            URL getUrlServiveByArea = new URL(url);

            HttpURLConnection con = (HttpURLConnection) getUrlServiveByArea.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int responseCode = con.getResponseCode();
            LogUtil.info(this.getClass().getName(), "\nSending 'GET' request to URL : " + url);
            LogUtil.info(this.getClass().getName(), "Response Code : " + responseCode);

            if (responseCode == 404) {
                LogUtil.info(this.getClass().getName(), "PE Name not found!");
                listGenerate.setStatusCode(responseCode);
            } else if (responseCode == 200) {
                listGenerate.setStatusCode(responseCode);
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                LogUtil.info(this.getClass().getName(), "PE Name : " + response);
                in.close();

                // At this point, 'response' contains the JSON data as a string
                String jsonData = response.toString();

                // Now, parse the JSON data using org.json library
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode portArrayNode = objectMapper.readTree(jsonData);
                LogUtil.info(this.getClass().getName(), "PE Name : " + portArrayNode);

                String community = portArrayNode.get(0).get("community").asText();
                String name = portArrayNode.get(0).get("name").asText();
                String manufactur = portArrayNode.get(0).get("manufacturer").asText();
                String ipAddress = portArrayNode.get(0).get("ipAddress").asText();
                String model = portArrayNode.get(0).get("model").asText();
                
                LogUtil.info(this.getClass().getName(), "COMMUNITY_TRANSIT" + community);
                LogUtil.info(this.getClass().getName(), "PE_NAME" + name);
                LogUtil.info(this.getClass().getName(), "PE_MANUFACTUR" + manufactur);
                LogUtil.info(this.getClass().getName(), "PE_IPADDRESS" + ipAddress);
                LogUtil.info(this.getClass().getName(), "PE_MODEL" + model);
               
//                if (getDetailactcode(wonum).get("detailactcode").toString() == "Populate PE Port IP Transit") {
//                    updateCommunityTransit(wonum, community);
//                }

                // Access data from the JSON object as needed
//                String manufactur = jsonObj.getString("manufacturer");
//                String name = jsonObj.getString("name");
//                String ipAddress = jsonObj.getString("ipAddress");
//                String mtu = jsonObject.getString("mtu");
//                String key = jsonObject.getString("key");
//                String portName = jsonObject.getString("name");
//                LogUtil.info(this.getClass().getName(), "===============PARSING DATA==============");
//                LogUtil.info(this.getClass().getName(), "ME_MANUFACTUR : " + manufactur);
//                LogUtil.info(this.getClass().getName(), "ME_NAME : " + name);
//                LogUtil.info(this.getClass().getName(), "ME_IPADDRESS : " + ipAddress);
//                LogUtil.info(this.getClass().getName(), "ME_PORT_MTU : " + mtu);
//                LogUtil.info(this.getClass().getName(), "ME_PORTID : " + key);
//                LogUtil.info(this.getClass().getName(), "ME_PORTNAME : " + portName);
                // Update STO, REGION, WITEL, DATEL from table WORKORDERSPEC
//                        updateDeviceLinkPort(wonum, manufactur, name, ipAddress, mtu, key, portName);
            }

        } catch (Exception e) {
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return null;
    }
}
