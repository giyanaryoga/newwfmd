/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.ListGenerateAttributes;
import java.io.*;
import java.net.*;
import java.sql.*;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.*;
/**
 *
 * @author ASUS
 */
public class ValidateVrfDao {

    public JSONObject getAssetattrid(String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid IN ('VRF_NAME','PE_NAME', 'RD')";
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

    public boolean updateVrf(String wonum, String rtExport, String rtImport, String rd, String maxRoutes, String asnNumber) {
        boolean result = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String updateQuery = "UPDATE APP_FD_WORKORDERSPEC "
                + "SET c_value = CASE c_assetattrid "
                + "WHEN 'RT_EXPORT' THEN ? "
                + "WHEN 'RT_IMPORT' THEN ? "
                + "WHEN 'RD' THEN ? "
                + "WHEN 'MAX_ROUTES' THEN ? "
                + "WHEN 'ASN_NUMBER' THEN ? "
                + "ELSE 'Missing' END "
                + "WHERE c_wonum = ? "
                + "AND c_assetattrid IN ('RT_EXPORT','RT_IMPORT', 'RD', 'MAX_ROUTES', 'ASN_NUMBER')";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(updateQuery)) {
            ps.setString(1, rtExport);
            ps.setString(2, rtImport);
            ps.setString(3, rd);
            ps.setString(4, maxRoutes);
            ps.setString(4, asnNumber);
            ps.setString(5, wonum);

            int exe = ps.executeUpdate();
            if (exe > 0) {
                result = true;
                LogUtil.info(getClass().getName(), "STO updated to " + wonum);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return result;
    }

    public JSONObject callUimaxValidateVrf(String wonum, ListGenerateAttributes listGenerate) throws MalformedURLException, IOException, JSONException, SQLException {
        JSONObject attributes = getAssetattrid(wonum);
        String vrfName = attributes.optString("VRF_NAME", "null");
        String deviceName = attributes.optString("PE_NAME", "null");
        String rd = attributes.optString("RD");

        if (rd != "") {
            LogUtil.info(this.getClass().getName(), "RD is already generated, Refresh/Reopen order to view the RD, RT Import, RT Export detail.");
        } else {
            try {
                String url = "https://api-emas.telkom.co.id:8443/api/vrf/find?" + "vrfName=" + vrfName + "&deviceName=" + deviceName;

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("GET");
                con.setRequestProperty("Accept", "application/json");
                int responseCode = con.getResponseCode();
                LogUtil.info(this.getClass().getName(), "\nSending 'GET' request to URL : " + url);
                LogUtil.info(this.getClass().getName(), "Response Code : " + responseCode);

                if (responseCode == 404) {
                    LogUtil.info(this.getClass().getName(), "Generate VRF Failed.");
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
                    LogUtil.info(this.getClass().getName(), "VRF : " + response);
                    in.close();

                    // 'response' contains the JSON data as a string
                    String jsonData = response.toString();

                    JSONArray jsonArray = new JSONArray(jsonData);

                    if (jsonArray.length() > 0) {
                        JSONObject jsonObject = jsonArray.getJSONObject(0);

                        String maxRoutes = jsonObject.get("maxRoutes").toString();
                        String reservedRD = jsonObject.get("reservedRD").toString();
                        String[] asnNumberRaw = reservedRD.split(":");
                        String asnNumber = asnNumberRaw[0];
                        String rtImport = "";
                        String rtExport = "";
                        JSONArray deviceListArray = jsonObject.getJSONArray("deviceList");
                        JSONArray rtImportArray = jsonObject.getJSONArray("rtImport");
                        JSONArray rtExportArray = jsonObject.getJSONArray("rtExport");

                        LogUtil.info(this.getClass().getName(), "Max Routes: " + maxRoutes);
                        LogUtil.info(this.getClass().getName(), "Max ReservedRD: " + reservedRD);
                        LogUtil.info(this.getClass().getName(), "ASN Number: " + asnNumber);

                        // Getting values from deviceList
                        for (int i = 0; i < deviceListArray.length(); i++) {
                            JSONObject deviceObj = deviceListArray.getJSONObject(i);
                            String name = deviceObj.getString("name");
//                            LogUtil.info(this.getClass().getName(), "Device " + (i + 1) + ":");
                            LogUtil.info(this.getClass().getName(), "Name: " + name);
                        }

                        // Getting values from rtImport
                        for (int i = 0; i < rtImportArray.length(); i++) {
                            rtImport = rtImportArray.getString(i);
                            LogUtil.info(this.getClass().getName(), "rtImport " + (i + 1) + ": " + rtImport);
                        }

                        // Getting values from rtExport
                        for (int i = 0; i < rtExportArray.length(); i++) {
                            rtExport = rtExportArray.getString(i);
                            LogUtil.info(this.getClass().getName(), "rtExport " + (i + 1) + ": " + rtExport);
                        }
                        
                        if (attributes != null) {
                            updateVrf(wonum, rtExport, rtImport, reservedRD, maxRoutes, asnNumber);
                        }
                    }
                }
            } catch (Exception e) {
                LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
            }
        }
        return null;
    }
}
