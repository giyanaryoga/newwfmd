/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.model.ListReTools;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import id.co.telkom.wfm.plugin.controller.IntegrationHistory;
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
public class validateReTools {
    GenerateWonumEbisDao woDao = new GenerateWonumEbisDao();
    ReToolDao reDao = new ReToolDao();
    IntegrationHistory integrationHistory = new IntegrationHistory();
    ConnUtil connUtil = new ConnUtil();
    
    private void sendUrl(ListReTools param) {
        try {
            APIConfig apiConfig = new APIConfig();
            apiConfig = connUtil.getApiParam("send_url_obl");
            URL url = new URL(apiConfig.getUrl());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            JSONObject bodyParam = new JSONObject();
            //requestnya belum
            bodyParam.put("WONUM", param.getWonum());
            bodyParam.put("DOCNAME", param.getDocName());
            bodyParam.put("URL", param.getUrlDoc());
            bodyParam.put("EXT_ORDER_NO", param.getExtOrderNo());
            bodyParam.put("NOMOR_KB", param.getNomorKb());
            bodyParam.put("NOMOR_KL", param.getNomorKl());
            bodyParam.put("OBL_TRC_NO", param.getOblTrcNo());
            bodyParam.put("SERVICE_ID", param.getServiceId());
            bodyParam.put("SUPPLIER_CODE", param.getSupplierCode());
            bodyParam.put("NAMA_MITRA", param.getNamaMitra());
            bodyParam.put("INSTALL_DATE", param.getInstallDate());
            bodyParam.put("SITEID", param.getSiteId());
            
            String request = bodyParam.toString();
            try (OutputStream outputStream = conn.getOutputStream()) {
                byte[] b = request.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }
            
            int responseCode = conn.getResponseCode();
            InputStream inputStr = conn.getInputStream();
            StringBuilder response = new StringBuilder();
            byte[] res = new byte[2048];
            int i = 0;
            while ((i = inputStr.read(res)) != -1) {
                response.append(new String(res, 0, i));
            }
            LogUtil.info(this.getClass().getName(), "INI RESPONSE : " + response.toString());
            JSONObject responseObj = new JSONObject();
            responseObj.put("body", response);
            
            if (responseCode == 200) {
                LogUtil.info(this.getClass().getName(), "Success POST");
                integrationHistory.insertKafka(param.getWonum(), apiConfig.getUrl(), "WFM_OBL_SEND_URL", "SUCCESS", bodyParam, responseObj);
            } else {
                LogUtil.info(this.getClass().getName(), "Failed POST");
                integrationHistory.insertKafka(param.getWonum(), apiConfig.getUrl(), "WFM_OBL_SEND_URL", "FAILED", bodyParam, responseObj);
            }
            
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
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
            InputStream inputStr = conn.getInputStream();
            StringBuilder response = new StringBuilder();
            byte[] res = new byte[2048];
            int i = 0;
            while ((i = inputStr.read(res)) != -1) {
                response.append(new String(res, 0, i));
            }
            LogUtil.info(this.getClass().getName(), "INI RESPONSE : " + response.toString());
            JSONObject responseObj = new JSONObject();
            responseObj.put("body", response);
            
            if (responseCode == 200) {
                LogUtil.info(this.getClass().getName(), "Success POST");
                integrationHistory.insertKafka(param.getWonum(), apiConfig.getUrl(), "WFM_CREATE_CUSTOMER_SCMT", "SUCCESS", bodyParam, responseObj);
            } else {
                LogUtil.info(this.getClass().getName(), "Failed POST");
                integrationHistory.insertKafka(param.getWonum(), apiConfig.getUrl(), "WFM_CREATE_CUSTOMER_SCMT", "FAILED", bodyParam, responseObj);
            }
            
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void validateOBL(String wonum) {
        try {
            ListReTools param = new ListReTools();
            JSONObject workorder = reDao.getWorkorder(wonum);
            String docName = reDao.docName(wonum, "SERVICE_DETAIL");
            
            param.setWonum(workorder.get("wonum").toString());
            param.setParent(workorder.get("parent").toString());
            String oblTrcNo = param.getWonum(); //wonum parent
            String namaMitra = workorder.get("ownerGroup").toString();
            String customerCode = woDao.getValueWorkorderAttribute(wonum, "CustomerID");
            String customerName = workorder.get("customerName").toString();
            String address = workorder.get("serviceAddress").toString();
            String workzone = workorder.get("workzone").toString();
            String extOrderNo = workorder.get("scOrderNo").toString();
            String nomorKb = woDao.getValueWorkorderAttribute(wonum, "AgreementName");
            String nomorKl = woDao.getValueWorkorderAttribute(wonum, "ContractName");
            String serviceId = woDao.getValueWorkorderAttribute(wonum, "Service_ID");
            String latitude = woDao.getValueWorkorderAttribute(wonum, "Lat");
            String longitude = woDao.getValueWorkorderAttribute(wonum, "Long");
            String supplierCode = "OBL"+ "|"+customerCode;
            String siteid = workorder.get("siteid").toString();
            String statusDate = workorder.get("statusDate").toString();
            
            //Kurang docName sama URL document
            param.setDocName(docName);
            param.setUrlDoc("? ObjectName ?");
            
            param.setNomorKb(nomorKb == null ? "" : nomorKb);
            param.setNomorKl(nomorKl == null ? workorder.get("productName").toString() : nomorKl);
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
        } catch (SQLException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
