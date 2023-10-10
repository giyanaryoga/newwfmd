/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.model.UrlSendReTools;
import id.co.telkom.wfm.plugin.model.ListReTools;
import id.co.telkom.wfm.plugin.model.ListScmtIntegrationParam;
import id.co.telkom.wfm.plugin.dao.GenerateWonumEbisDao;
import id.co.telkom.wfm.plugin.controller.InsertIntegrationHistory;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.dao.ReToolDao;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.sql.DataSource;
//import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
//import org.joget.commons.util.UuidGenerator;
//import org.json.JSONException;
//import org.json.XML;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class validateReTools {
    GenerateWonumEbisDao woDao = new GenerateWonumEbisDao();
//    UpdateTaskStatusEbisDao updateDao = new UpdateTaskStatusEbisDao();
    ReToolDao reDao = new ReToolDao();
    InsertIntegrationHistory integrationDao = new InsertIntegrationHistory();
    UrlSendReTools urlRE = new UrlSendReTools();
//    ListScmtIntegrationParam scmtList = new ListScmtIntegrationParam();
    
    private void sendUrl(ListReTools param) {
        try {
            URL url = new URL(urlRE.getUrlSendRe());
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
            OutputStream str = conn.getOutputStream();
            byte[] b = request.getBytes("UTF-8");
            str.write(b);
            str.flush();
            str.close();
            
            int responseCode = conn.getResponseCode();
            InputStream inputStr = conn.getInputStream();
            StringBuilder response = new StringBuilder();
            byte[] res = new byte[2048];
            int i = 0;
            while ((i = inputStr.read(res)) != -1) {
                response.append(new String(res, 0, i));
            }
            LogUtil.info(this.getClass().getName(), "INI RESPONSE : " + response.toString());
            
            if (responseCode == 200) {
                LogUtil.info(this.getClass().getName(), "Success POST");
            } else {
                LogUtil.info(this.getClass().getName(), "Failed POST");
            }
            
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void createCustomerSCMT(ListScmtIntegrationParam paramScmt) {
        try {
            URL url = new URL(urlRE.getCreateCustomer());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            JSONObject bodyParam = new JSONObject();
            //requestnya belum
            bodyParam.put("customer_code", paramScmt.getCustomerCode());
            bodyParam.put("customer_name", paramScmt.getCustomerName());
            bodyParam.put("service_id", paramScmt.getServiceId());
            bodyParam.put("install_loc", paramScmt.getInstallLoc());
            bodyParam.put("address", paramScmt.getServiceAddress());
            bodyParam.put("longitude", paramScmt.getLongitude());
            bodyParam.put("latitude", paramScmt.getLatitude());
            bodyParam.put("work_zone", paramScmt.getWorkzone());
            
            String request = bodyParam.toString();
            DataOutputStream str1 = (DataOutputStream) conn.getOutputStream();
            str1.writeBytes(request);
            str1.flush();
            str1.close();
//            OutputStream str = conn.getOutputStream();
//            byte[] b = request.getBytes("UTF-8");
//            str.write(b);
//            str.flush();
//            str.close();
            
            int responseCode = conn.getResponseCode();
            InputStream inputStr = conn.getInputStream();
            StringBuilder response = new StringBuilder();
            byte[] res = new byte[2048];
            int i = 0;
            while ((i = inputStr.read(res)) != -1) {
                response.append(new String(res, 0, i));
            }
            LogUtil.info(this.getClass().getName(), "INI RESPONSE : " + response.toString());
            
            if (responseCode == 200) {
                LogUtil.info(this.getClass().getName(), "Success POST");
            } else {
                LogUtil.info(this.getClass().getName(), "Failed POST");
            }
            integrationDao.insertIntegrationHistory(paramScmt.getWonum(), "CRT_CST_SCM", request, response.toString(), "CreateCustomerSCMT");
            conn.disconnect();
        } catch (MalformedURLException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void validateOBL(String wonum) {
        try {
            ListReTools param = new ListReTools();
            ListScmtIntegrationParam paramScmt = new ListScmtIntegrationParam();
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
            param.setUrlDoc(urlRE.getApiMinio() + "? ObjectName ?");
            
            param.setNomorKb(nomorKb == null ? "" : nomorKb);
            param.setNomorKl(nomorKl == null ? workorder.get("productName").toString() : nomorKl);
            param.setExtOrderNo(extOrderNo);
            param.setOblTrcNo(oblTrcNo);
            param.setServiceId(serviceId);
            param.setSupplierCode(supplierCode);
            param.setSiteId(siteid);
            param.setNamaMitra(namaMitra);
            param.setInstallDate(statusDate);
            
            paramScmt.setLatitude(latitude);
            paramScmt.setLongitude(longitude);
            paramScmt.setCustomerCode(customerCode);
            paramScmt.setCustomerName(customerName);
            paramScmt.setServiceId(serviceId);
            paramScmt.setInstallLoc(serviceId);
            paramScmt.setServiceAddress(address);
            paramScmt.setWorkzone(workzone);
            
            //Create Customer to SCMT tool
            createCustomerSCMT(paramScmt);
            
            sendUrl(param);
        } catch (SQLException ex) {
            Logger.getLogger(validateReTools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
