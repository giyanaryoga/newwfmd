/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.util.ConnUtil;
import id.co.telkom.wfm.plugin.util.RequestAPI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author User
 */
public class CpeValidationEbisDao {
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"); 
        Timestamp ts =  Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
    
    public Object getQueryNte(String cpeSerialNumber, String eaiToken) throws SQLException{
        //Prepare variable
        JSONObject data_obj = null;
        String response = "";
        //Get configuration to 'POST'
        RequestAPI api = new RequestAPI();
        ConnUtil connUtil = new ConnUtil();
        try {
            APIConfig apiConfig = new APIConfig();
            apiConfig = connUtil.getApiParam("get_query_nte");
            apiConfig.setUrl(apiConfig.getUrl() + "/" + cpeSerialNumber + "/WFM");
            apiConfig.setApiToken(eaiToken);
            HashMap<String, String> param = new HashMap<>();
            //Send request with okhttp
            response = api.sendGetQueryNte(apiConfig, param);
            //Parse response
            JSONParser parse = new JSONParser();
            LogUtil.info(getClass().getName(), "response query nte: " + response);
            data_obj = (JSONObject)parse.parse(response);
        } catch (ParseException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return data_obj;
    }
    
    public boolean updateCpeValidation(String wonum, String cpeVendor, String cpeModel, String cpeSerialNumber){
        boolean updateStatus = false;    
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        // change 22
        String update = "UPDATE app_fd_workorder SET c_cpe_validation = 'PASS', c_cpe_vendor = ?, c_cpe_model = ?, c_cpe_serial_number = ?, datemodified = ? WHERE c_wonum = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        // change 22
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(update);
                try {
                    ps.setString(1, cpeVendor);
                    ps.setString(2, cpeModel);
                    ps.setString(3, cpeSerialNumber);
                    ps.setString(4, wonum);
                    ps.setTimestamp(5, getTimeStamp());
                    int exe = ps.executeUpdate();
                    //Checking insert status
                    if (exe > 0) {
                        updateStatus = true;
                        LogUtil.info(getClass().getName(), "CPE Validation: PASS | wonum: " + wonum);
                    }   
                    if (ps != null)
                        ps.close();
                } catch (Throwable throwable) {
                    try {
                        if (ps != null)
                            ps.close();
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null)
                    con.close();
            } catch (Throwable throwable) {
                try {
                    if (con != null)
                        con.close();
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
        return updateStatus;
    }
    
    public boolean[] cpeModelCheck(boolean[] arrayBoolean, String cpeVendor, String cpeModel, int snLength, List<String> taskList) throws SQLException {
        Arrays.fill(arrayBoolean, Boolean.FALSE);
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_vendor, c_description, c_serialnumlength  FROM app_fd_cpemodel WHERE c_model = ?";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, cpeModel);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                arrayBoolean[0] = true;
                if ((rs.getString("c_vendor") == null ? "" : rs.getString("c_vendor").toString()).equalsIgnoreCase(cpeVendor)) {
                    arrayBoolean[1] = true;
                }
                if (snLength == rs.getInt("c_serialnumlength")) {
                    arrayBoolean[2] = true;
                }
                if (taskList.contains((rs.getString("c_description") == null ? "" : rs.getString("c_description").toString()))) {
                    arrayBoolean[3] = true;
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return arrayBoolean;
    }
    
    public boolean checkCpeVendor(String vendor) throws SQLException {
        boolean cpeVendor = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_vendor FROM app_fd_cpevendor WHERE c_vendor = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, vendor);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                cpeVendor = true;
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return cpeVendor;
    }
}
