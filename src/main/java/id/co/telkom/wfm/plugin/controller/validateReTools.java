/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.model.ListReTools;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import id.co.telkom.wfm.plugin.dao.ReToolDao;
import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.model.HttpResponse;
import id.co.telkom.wfm.plugin.util.ConnUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

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
    JSONParser parser = new JSONParser();
    
    private HttpResponse sendUrl(ListReTools param) {
        APIConfig apiConfig = new APIConfig();
        apiConfig = connUtil.getApiParam("send_url_obl");
        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(apiConfig.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", apiConfig.getClientSecret());
            conn.setDoOutput(true);
            JSONObject bodyParam = new JSONObject();
            //requestnya belum
            bodyParam.put("customer_id", Integer.parseInt(param.getCustomerId()));
            bodyParam.put("wo_number", param.getParent());
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
            try (OutputStream outputStream = conn.getOutputStream()) {
                byte[] b = request.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }
            
            int responseCode = conn.getResponseCode();
            StringBuilder response = new StringBuilder();
            BufferedReader br = null;
            
            if (responseCode == 200) {
                InputStream inputStr = conn.getInputStream();
                br = new BufferedReader(new InputStreamReader(inputStr));
            } else {
                InputStream errStr = conn.getErrorStream();
                br = new BufferedReader(new InputStreamReader(errStr));             
            }
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            return new HttpResponse(responseCode, response.toString());
        } catch (MalformedURLException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    private void createCustomerSCMT(ListReTools param) {
        APIConfig apiConfig = new APIConfig();
        apiConfig = connUtil.getApiParam("create_customer_obl");
        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(apiConfig.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
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
            responseObj.put("response", response);
            responseObj.put("status", responseCode);
            
            JSONObject data_obj = (JSONObject)parser.parse(response.toString());
            JSONObject body = (JSONObject) data_obj.get("body");
            String customerId = body.get("customer_id").toString();
            param.setCustomerId(customerId);
//            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | ParseException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    private void generateUrlMinio(String parent, String objectName) {
        APIConfig apiConfig = new APIConfig();
        apiConfig = connUtil.getApiParam("generate_url_minio");
        URL url = null;
        HttpURLConnection conn = null;
        try {
            url = new URL(apiConfig.getUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
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
//            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
    
    public JSONObject validateOBL(String wonum) {
        JSONObject resp = new JSONObject();
        try {
            JSONObject workorder = reDao.getWorkorder(wonum);
            param.setParent(workorder.get("parent").toString());
            String objName = reDao.getObjectName(param.getParent(), "SERVICE_DETAIL");
            String productName = workorder.get("productName").toString();
            //Generate URL Minio
            boolean isURL = reDao.getUrl(wonum, objName);
            if (isURL) {
                generateUrlMinio(param.getParent(), objName);
            }
            JSONObject doclink = reDao.getDocLinks(param.getParent(), objName);
            
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
            HttpResponse response = sendUrl(param);
            int code = response.getCode();
            JSONObject data_obj = (JSONObject)parser.parse(response.getBody());
            if (code == 200) {
                resp.put("code", code);
                resp.put("message", data_obj.get("body").toString());
            } else {
                resp.put("code", code);
                resp.put("message", data_obj.get("body").toString());
            }
        } catch (SQLException | ParseException ex) {
            Logger.getLogger(ValidateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
        return resp;
    }
}
