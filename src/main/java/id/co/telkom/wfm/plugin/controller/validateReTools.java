/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.model.ListReTools;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import id.co.telkom.wfm.plugin.dao.ReToolDao;
import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.util.ConnUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class ValidateReTools {
    GenerateWonumEbisDao woDao = new GenerateWonumEbisDao();
    ReToolDao reDao = new ReToolDao();
    IntegrationHistory integrationHistory = new IntegrationHistory();
    ConnUtil connUtil = new ConnUtil();
    ListReTools param = new ListReTools();
    
    private void sendUrl(ListReTools param) {
        try {
            APIConfig apiConfig = new APIConfig();
            apiConfig = connUtil.getApiParam("send_url_obl");
            URL url = new URL(apiConfig.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", apiConfig.getClientSecret());
//            LogUtil.info(this.getClass().getName(), "URL = "+apiConfig.getUrl());
            conn.setDoOutput(true);
            JSONObject bodyParam = new JSONObject();
            //requestnya belum
            bodyParam.put("customer_id", 1);
            bodyParam.put("wo_number", param.getWonum());
            bodyParam.put("document_name", param.getDocName());
            bodyParam.put("url", param.getUrlDoc());
            bodyParam.put("ext_order_number", param.getExtOrderNo());
            bodyParam.put("kb_number", param.getNomorKb());
            bodyParam.put("kl_number", param.getNomorKl());
            bodyParam.put("transaction_number", param.getOblTrcNo());
            bodyParam.put("service_id", param.getServiceId());
            bodyParam.put("supplier_code", param.getSupplierCode());
            bodyParam.put("partner_name", param.getNamaMitra());
            bodyParam.put("site_id", param.getSiteId());
            
            String request = bodyParam.toString();
//            LogUtil.info(this.getClass().getName(), "REQUEST OBL : " + request);
            try (OutputStream outputStream = conn.getOutputStream()) {
                byte[] b = request.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }
            LogUtil.info(this.getClass().getName(), "response code = "+conn.getResponseCode());
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                LogUtil.info(this.getClass().getName(), "Success Send URL");
            } else {
                LogUtil.info(this.getClass().getName(), "Failed Send URL");
            }
            
            InputStream inputStr = conn.getInputStream();
            StringBuilder response = new StringBuilder();
            byte[] res = new byte[2048];
            int i = 0;
            while ((i = inputStr.read(res)) != -1) {
                response.append(new String(res, 0, i));
            }
            JSONObject responseObj = new JSONObject();
            responseObj.put("body", response);
            responseObj.put("status", responseCode);
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createCustomerSCMT(ListReTools param) {
        try {
            APIConfig apiConfig = new APIConfig();
            apiConfig = connUtil.getApiParam("create_customer_obl");
            URL url = new URL(apiConfig.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", apiConfig.getClientSecret());
            conn.setDoOutput(true);
            JSONObject bodyParam = new JSONObject();

            bodyParam.put("customer_code", param.getCustCode());
            bodyParam.put("customer_name", param.getCustName());
            bodyParam.put("service_id", param.getServiceId());
            bodyParam.put("install_loc", param.getInstallLoc());
            bodyParam.put("address", param.getAddress());
            bodyParam.put("longitude", param.getLongitude());
            bodyParam.put("latitude", param.getLatitude());
            bodyParam.put("work_zone", param.getWorkzone());
            
            String request = bodyParam.toString();
            try (OutputStream outputStream = conn.getOutputStream()) {
                byte[] b = request.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                LogUtil.info(this.getClass().getName(), "Success Create Customer SCMT");
            } else {
                LogUtil.info(this.getClass().getName(), "Failed Create Customer SCMT");
            }
            
            InputStream inputStr = conn.getInputStream();
            StringBuilder response = new StringBuilder();
            byte[] res = new byte[2048];
            int i = 0;
            while ((i = inputStr.read(res)) != -1) {
                response.append(new String(res, 0, i));
            }
            JSONObject responseObj = new JSONObject();
            responseObj.put("body", response);
            responseObj.put("status", responseCode);
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void generateUrlMinio(String parent, String objectName) {
        try {
            APIConfig apiConfig = new APIConfig();
            apiConfig = connUtil.getApiParam("generate_url_minio");
            URL url = new URL(apiConfig.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("api_id", apiConfig.getApiId());
            conn.setRequestProperty("api_key", apiConfig.getApiKey());
            conn.setDoOutput(true);
            JSONObject bodyParam = new JSONObject();
            
            bodyParam.put("wonum", parent);
            bodyParam.put("objectname", objectName);
            bodyParam.put("expiration", 7);
            bodyParam.put("timeunit", "DAYS");
            
            String request = bodyParam.toString();
            try (OutputStream outputStream = conn.getOutputStream()) {
                byte[] b = request.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                LogUtil.info(this.getClass().getName(), "Success Generate URL Minio");
            } else {
                LogUtil.info(this.getClass().getName(), "Failed Generate URL Minio");
            }
            
            InputStream inputStr = conn.getInputStream();
            StringBuilder response = new StringBuilder();
            byte[] res = new byte[2048];
            int i = 0;
            while ((i = inputStr.read(res)) != -1) {
                response.append(new String(res, 0, i));
            }
            JSONObject responseObj = new JSONObject();
            responseObj.put("body", response);
            responseObj.put("status", responseCode);
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void validateOBL(String wonum) {
        try {
            JSONObject workorder = reDao.getWorkorder(wonum);
//            LogUtil.info(this.getClass().getName(), "workorder = "+workorder);
            param.setParent(workorder.get("parent").toString());
            String objName = reDao.getObjectName(param.getParent(), "SERVICE_DETAIL");
            String productName = workorder.get("productName").toString();
            //Generate URL Minio
            boolean isURL = reDao.getUrl(wonum, objName);
            if (isURL) {
                generateUrlMinio(param.getParent(), objName);
            }
            JSONObject doclink = reDao.getDocLinks(param.getParent(), objName);
//            LogUtil.info(this.getClass().getName(), "doclink = "+doclink);
//            LogUtil.info(this.getClass().getName(), "PRODUCT NAME = "+contractName);
            
            param.setWonum(workorder.get("wonum").toString());
            String oblTrcNo = param.getWonum(); //wonum parent
            String namaMitra = (workorder.get("ownerGroup") == null ? "" : workorder.get("ownerGroup").toString());
            String customerCode = woDao.getValueWorkorderAttribute(wonum, "CustomerID");
            String customerName = workorder.get("customerName").toString();
            String address = workorder.get("serviceAddress").toString();
            String workzone = workorder.get("workzone").toString();
            String extOrderNo = workorder.get("scOrderNo").toString();
            String siteid = workorder.get("siteid").toString();
            String statusDate = workorder.get("statusDate").toString();
            String supplierCode = "OBL"+ "|"+customerCode;
            String contractName = "";
            if (woDao.getValueWorkorderAttribute(wonum, "ContractName") == null) {
                contractName = productName;
            } else {
                contractName = woDao.getValueWorkorderAttribute(wonum, "ContractName");
            }
            String nomorKb = woDao.getValueWorkorderAttribute(wonum, "AgreementName");
            String nomorKl = contractName;
            String serviceId = woDao.getValueWorkorderAttribute(wonum, "Service_ID");
            String latitude = woDao.getValueWorkorderAttribute(wonum, "Latitude");
            String longitude = woDao.getValueWorkorderAttribute(wonum, "Longitude");
            String url = doclink.get("url").toString();
            String docName = doclink.get("documentName").toString();
            
            param.setDocName(docName);
            param.setUrlDoc(url);
            param.setNomorKb(nomorKb == null ? "" : nomorKb);
            param.setNomorKl(nomorKl);
            param.setExtOrderNo(extOrderNo);
            param.setOblTrcNo(oblTrcNo);
            param.setServiceId(serviceId);
            param.setSupplierCode(supplierCode);
            param.setSiteId(siteid);
            param.setNamaMitra(namaMitra);
            param.setInstallDate(statusDate);

            param.setLatitude(latitude);
            param.setLongitude(longitude);
            param.setCustCode(customerCode);
            param.setCustName(customerName);
            param.setServiceId(serviceId);
            param.setInstallLoc(serviceId);
            param.setAddress(address);
            param.setWorkzone(workzone);
            //Create Customer to SCMT tool
            createCustomerSCMT(param);
            //Send URL document to ReTools
            sendUrl(param);
//            LogUtil.info(this.getClass().getName(), "CONTRACT NAME = "+contractName);
//            LogUtil.info(this.getClass().getName(), "NAMA MITRA = "+param.getNamaMitra());
//            LogUtil.info(this.getClass().getName(), "NOMOR KL = "+param.getNomorKl());
        } catch (SQLException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
