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
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONException;
import org.json.JSONObject;
//import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class ValidateStoDao {
    class MyTrustManager implements X509TrustManager {

        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authtype) {
            // Do nothing
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authtype) {
            // Do nothing
        }

        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }

    class MyHostnameVerifier implements HostnameVerifier {

        public boolean verify(String hostname, javax.net.ssl.SSLSession session) {
            return true;
        }
    }

    public void setupSSLFactory() throws Exception {
        X509TrustManager[] tm = {new MyTrustManager()};
        SSLContext sc = SSLContext.getInstance("SSL");
        SecureRandom rs = new SecureRandom();
        sc.init(null, tm, rs);
        HostnameVerifier allHostsValid = new MyHostnameVerifier();
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }

    public JSONObject getAssetattrid(String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid IN ('PRODUCT_TYPE','LATITUDE','LONGITUDE')";
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

    public JSONObject callUimaxStoValidation(String wonum, String lat, String lon, String serviceType) {
        try {
            setupSSLFactory();
//            String url = "https://api-emas.telkom.co.id:8443/api/area/stoByCoordinate?" + "lat=" + getAssetattrid(wonum).get("LATITUDE").toString() + "&lon=" + getAssetattrid(wonum).get("LONGITUDE").toString() + "&serviceType=" + getAssetattrid(wonum).get("PRODUCT_TYPE").toString();
            String url = "https://api-emas.telkom.co.id:8443/api/area/stoByCoordinate?" + "lat=" + lat + "&lon=" + lon + "&serviceType=" + serviceType;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

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
                // Access data from the JSON object as needed
                String sto = jsonObject.getString("name");
                String stodesc = jsonObject.getString("description");
                JSONObject witelObj = jsonObject.getJSONObject("witel");
                String witel = witelObj.getString("name");
                JSONObject regionObj = jsonObject.getJSONObject("region");
                String region = regionObj.getString("name");
                JSONObject datelObj = jsonObject.getJSONObject("datel");
                String datel = datelObj.getString("name");
                LogUtil.info(this.getClass().getName(), "STO : " + sto);
                LogUtil.info(this.getClass().getName(), "STO Description : " + stodesc);
                LogUtil.info(this.getClass().getName(), "Region : " + region);
                LogUtil.info(this.getClass().getName(), "Witel : " + witel);
                LogUtil.info(this.getClass().getName(), "Datel : " + datel);
            }
        } catch (Exception e) {
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return null;
    }
    
//    public void insertIntoWorkorderspec(String sto, String region, String witel, String datel) {
//        // Generate UUID
//        String uuId = UuidGenerator.getInstance().getUuid();
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//
//        String insert = "INSERT INTO APP_FD_WORKORDERSPEC (ID, C_, C_ATTR_NAME, C_ATTR_TYPE, C_DESCRIPTION) VALUES (?, ?, ?, ?, ?)";
//
//        try (Connection con = ds.getConnection();
//                PreparedStatement ps = con.prepareStatement(insert.toString())) {
//            ps.setString(1, uuId);
//            ps.setString(2, wonum);
//            ps.setString(3, listGenerate.getName());
//            ps.setString(4, "");
//            ps.setString(5, listGenerate.getId());
//
//            int exe = ps.executeUpdate();
//
//            if (exe > 0) {
//                LogUtil.info(this.getClass().getName(), "insert data successfully");
//            }
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
//        } finally {
//            ds.getConnection().close();
//        }
//    }

}
