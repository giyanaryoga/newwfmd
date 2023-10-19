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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author ASUS
 */
public class GetSIDNetmonkDao {

    DeviceUtil deviceUtil = new DeviceUtil();

    private String createSoapRequestGetSIDConn(String orderID) {
        String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\">\n"
                + "   <soapenv:Header/>\n"
                + "   <soapenv:Body>\n"
                + "      <ent:findServiceByOrderRequest>\n"
                + "         <OrderID>" + orderID + "</OrderID>\n"
                + "      </ent:findServiceByOrderRequest>\n"
                + "   </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return request;
    }

    private String getSoapResponseNetmonk(String orderID) {
        String request = createSoapRequestGetSIDConn(orderID);
        String serviceId = "";
        try {
            org.json.JSONObject temp = deviceUtil.callUIM(request);

            JSONObject envelope = temp.getJSONObject("env:Envelope").getJSONObject("env:Body");
            JSONObject findServiceOrder = envelope.getJSONObject("ent:findServiceByOrderResponse");
            int statusCode = findServiceOrder.getInt("statusCode");
            if (statusCode == 404) {
                LogUtil.info(getClass().getName(), "Service Not Found");
            } else if (statusCode == 200) {
                JSONArray serviceInfo = findServiceOrder.getJSONArray("ServiceInfo");
                JSONObject data = (JSONObject) serviceInfo.get(0);
                serviceId = data.getString("id");
            } else {
                LogUtil.info(getClass().getName(), "Error here");
            }

        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Call Failed." + e);
        }
        return serviceId;
    }

    public String validateSIDNetmonk(JSONObject attribute) throws SQLException, JSONException {
        String result = "";
        String productname = attribute.getString("productname");
        String flagND = attribute.getString("nd");
        String scorderno = attribute.getString("scorderno");
        String serviceID = attribute.getString("ServiceID");
        String crmordertype = attribute.getString("crmordertype");
        String[] splitscorder = scorderno.split("_");
        String orderid = splitscorder[0];

        if (productname.equals("Nadeefa Netmonk") && crmordertype.equals("New Insatall") && flagND.isEmpty()) {
            String ServiceID = serviceID;
            if (ServiceID.isEmpty()) {
                String resultSID = getSoapResponseNetmonk(orderid);
                result = "get SID Connectivity Successfully, this is your SID : " + resultSID;
                if (resultSID.isEmpty()) {
                    result = "Get SID Connectivity Failed";
                }
            }
        }
        return result;
    }

}
