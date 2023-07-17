/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import java.security.SecureRandom;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateExpiredException;
import javax.security.cert.CertificateNotYetValidException;
import javax.security.cert.X509Certificate;

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
    
    String propName = "telkom.endpoint.uimax";
    String UrlByIp = propName+"/api/area/stoByCoordinate?";
    
    
}
