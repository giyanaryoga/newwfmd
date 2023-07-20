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
import java.util.Scanner;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
//import javax.security.cert.CertificateExpiredException;
//import javax.security.cert.CertificateNotYetValidException;
//import javax.security.cert.X509Certificate;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
//import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class ValidateStoDao {
//    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateExpiredException, CertificateNotYetValidException {
//        try {
//            chain[0].checkValidity();
//        } catch (Exception e) {
//            throw new CertificateExpiredException("Certificate not available or trusted");
//        }
//    }

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

//    public String getLat(String wonum){
//        String result= "";
//        Connection con = null;
//        PreparedStatement ps = null;
//        ResultSet rs = null;
//        
//        try {
//            DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//            con = ds.getConnection();
//            
//            if(!con.isClosed()){
//                String selectQuery = "SELECT c_assetattrid, c_alnvalue FROM APP_FD_WORKORDERSPEC WHERE c_wonum = ? AND c_assetattrid IN ('LATITUDE')";
//                ps = con.prepareStatement(selectQuery);
//                ps.setString(1, wonum);
//                rs = ps.executeQuery();
//                
//                if(rs.next()){
//                    result = rs.getString("c_alnvalue");
//                    LogUtil.info(this.getClass().getName(), "Latitude : " + result );
//                }
//            }
//        } catch (Exception e) {
//            LogUtil.info(getClass().getName(), e.getMessage());
//        } finally{
//            try {
//                if(rs!= null){
//                    rs.close();
//                }
//                if(ps!= null){
//                    ps.close();
//                }
//                if(con!= null){
//                    con.close();
//                }
//            } catch (Exception e) {
//            }
//        }
//        return result;
//    }
    public JSONObject getAssetattrid(String wonum) throws SQLException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT C_ASSETATTRID, C_ALNVALUE FROM APP_FD_WORKORDERSPEC WHERE C_WONUM = ? AND C_ASSETATTRID IN ('PRODUCT_TYPE','LATITUDE','LONGITUDE')";
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

    public JSONObject callUimaxStoValidation(String lat, String lon, String serviceType) {
        try {
            setupSSLFactory();
//            String url = "https://api-emas.telkom.co.id:8443/api/area/stoByCoordinate?" + "lat=" + getAssetattrid(wonum).get("LATITUDE").toString() + "&lon=" + getAssetattrid(wonum).get("LONGITUDE").toString() + "&serviceType=" + "ASTINET";
            String url = "https://api-emas.telkom.co.id:8443/api/area/stoByCoordinate?" + "lat=" + lat + "&lon=" + lon + "&serviceType=" + serviceType;

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int responseCode = con.getResponseCode();
            LogUtil.info(this.getClass().getName(), "\nSending 'GET' request to URL : " + url);
            LogUtil.info(this.getClass().getName(), "Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            if (responseCode != 200) {
                LogUtil.info(this.getClass().getName(), "STO not found");
            } else if (responseCode == 200) {
                LogUtil.info(this.getClass().getName(), "Response : " + response.toString());
                
                LogUtil.info(this.getClass().getName(), "STO : " + response);
            }
            in.close();
        } catch (Exception e) {
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return null;
    }

}
