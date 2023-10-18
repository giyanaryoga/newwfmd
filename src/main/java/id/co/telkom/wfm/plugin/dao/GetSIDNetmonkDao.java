/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.util.DeviceUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ASUS
 */
public class GetSIDNetmonkDao {
     DeviceUtil deviceUtil = new DeviceUtil();

//    private String getAssetattrid(String wonum) throws SQLException, JSONException {
//        String resultObj = "";
//        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT c_assetattrid, c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid IN ('LATITUDE','LONGITUDE')";
//        try (Connection con = ds.getConnection();
//                PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, wonum);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next()) {
//                resultObj = (rs.getString("c_assetattrid"), rs.getString("c_value"));
//                LogUtil.info(this.getClass().getName(), "Location : " + resultObj);
//            }
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
//        }
//        return resultObj;
//    }

    private String createSoapRequestGetSIDConn(String orderID) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ent:findServiceByOrderRequest>\n"
                + "         <OrderID>"+orderID+"</OrderID>\n"
                + "      </ent:findServiceByOrderRequest>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return request;
    }
    
    private void getSoapResponseNetmonk(String wonum, String orderID) {
        String request = createSoapRequestGetSIDConn(orderID);
        try {
            org.json.JSONObject temp = deviceUtil.callUIM(request);
            
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Call Failed." + e);
        }
    }
}
