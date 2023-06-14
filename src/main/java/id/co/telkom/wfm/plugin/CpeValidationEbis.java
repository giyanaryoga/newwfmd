/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.CpeValidationEbisDao;
import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class CpeValidationEbis extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - CPE Validation - Web Service";
    
    @Override
    public String renderTemplate(FormData fd, Map map) {
        return "";
    }

    @Override
    public String getName() {
        return this.pluginName;
    }

    @Override
    public String getVersion() {
        return "7.00";
    }

    @Override
    public String getDescription() {
        return this.pluginName;
    }

    @Override
    public String getLabel() {
        return this.pluginName;
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {
        //@@Start..
        LogUtil.info(getClass().getName(), "Start Process: Validate CPE");
        //@Authorization
        if ("POST".equals(hsr.getMethod())) {
            try{
                 //@Parsing message
                //HttpServletRequest get JSON Post data
                StringBuffer jb = new StringBuffer();
                String line = null;
                try {//read the response JSON to string buffer
                    BufferedReader reader = hsr.getReader();
                    while((line = reader.readLine()) !=null)
                        jb.append(line);
                } catch (Exception e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                //Parse JSON String to JSON Object
                String bodyParam = jb.toString(); //String
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject)parser.parse(bodyParam);//JSON Object
                //Store param
                String wonum = data_obj.get("wonum").toString();
                String cpeVendor = data_obj.get("cpeVendor").toString();
                String cpeModel = data_obj.get("cpeModel").toString();
                String cpeSerialNumber = data_obj.get("cpeSerialNumber").toString();
//                String laborCode = data_obj.get("laborCode").toString();
                //Get EAI Token for access scmt
                ScmtIntegrationEbisDao scmtIntegrationDao = new ScmtIntegrationEbisDao();
                String eaiToken = scmtIntegrationDao.getScmtToken();
                LogUtil.info(getClassName(), "Token: " + eaiToken);
                //Get Query NTE
                CpeValidationEbisDao dao = new CpeValidationEbisDao();
                JSONObject data = null;
                try {
                    data = (JSONObject) dao.getQueryNte(cpeSerialNumber, eaiToken);
                } catch (SQLException ex) {
                    Logger.getLogger(CpeValidationEbis.class.getName()).log(Level.SEVERE, null, ex);
                }
                JSONObject apiItem = (JSONObject)data.get("apiItemResponse");
                JSONObject eaiStatus = (JSONObject)apiItem.get("eaiStatus");
                String statusCode = eaiStatus.get("srcResponseCode").toString();
                switch (statusCode) {
                    case "200":
                        //                    JSONArray item_array = (JSONArray)apiItem.get("eaiBody");
//                    String locationCode = "";
//                    for (Object object : item_array){
//                        JSONObject obj = (JSONObject)object;
//                        locationCode = (obj.get("location_code") == null ? "" : obj.get("location_code").toString());
//                    }
                        //Validate CPE
//                    if (locationCode.equals(laborCode)){
                        boolean updateValidation = dao.updateCpeValidation(wonum, cpeVendor, cpeModel, cpeSerialNumber);
                        if (updateValidation){
                            JSONObject res = new JSONObject();
                            res.put("message", "validasi berhasil!");
                            res.writeJSONString(hsr1.getWriter());
                            LogUtil.info(getClassName(), "CPE validation success for " + wonum);
                            hsr1.setStatus(200);
                        } else {
                            JSONObject res = new JSONObject();
                            res.put("message", "validasi gagal!");
                            res.writeJSONString(hsr1.getWriter());
                            LogUtil.info(getClassName(), "CPE validation error for " + wonum);
                            hsr1.setStatus(422);
                        }
//                    } else {
//                        hsr1.setStatus(265);
//                        JSONObject res = new JSONObject();
//                        res.put("message", "validasi gagal, perangkat ini terdaftar di lokasi: ");
//                        res.writeJSONString(hsr1.getWriter());
//                    } 
                        break;
                    case "404":
                        hsr1.sendError(404, "serial number tidak ditemukan");
                        break;
                    default:
                        hsr1.sendError(465, "error, silahkan kontak administrator");
                        break;
                }
            } catch (ParseException e){
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else if (!"POST".equals(hsr.getMethod())){
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            } 
        }
    }
    
}