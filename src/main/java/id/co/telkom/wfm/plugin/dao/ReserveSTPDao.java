/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.util.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.*;
import java.util.Arrays;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.*;

/**
 *
 * @author ASUS
 */
public class ReserveSTPDao {

    ConnUtil util = new ConnUtil();
    DeviceUtil deviceUtil = new DeviceUtil();
    TaskAttributeUpdateDao attribute = new TaskAttributeUpdateDao();

    public String getParams(String wonum) throws SQLException, JSONException {
        String resultObj = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "select c_detailactcode FROM app_fd_workorder\n"
                + "WHERE c_wonum = ?\n"
                + "AND c_status = 'STARTWA'";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                resultObj = rs.getString("c_detailactcode");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

    private String createSoapRequestUnReserve(String reservationID) {
        String request = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "  <SOAP-ENV:Body>\n"
                + "    <releaseReservationRequest xmlns=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\">\n"
                + "      <reservationID>" + reservationID + "</reservationID>\n"
                + "    </releaseReservationRequest>\n"
                + "  </SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>";

        return request;
    }

    public JSONObject getSoapResponseUnReserve(String reservationID) throws IOException, MalformedURLException, JSONException {
        String request = createSoapRequestUnReserve(reservationID);
        JSONObject temp = deviceUtil.callUIM(request);
        return temp;
    }

    private String createSoapRequestReservation(String odpName, String odpId, String odpPortName, String odpPortId) {
        String request = "<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\"\n"
                + "    xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\">\n"
                + "    <SOAP-ENV:Header/>\n"
                + "    <SOAP-ENV:Body>\n"
                + "        <ent:reserveEndpointRequest>\n"
                + "            <ServiceType>1</ServiceType>\n"
                + "            <SLG_Flavour>1</SLG_Flavour>\n"
                + "            <SalesOrderId>1</SalesOrderId>\n"
                + "            <NoOfNodes>1</NoOfNodes>\n"
                + "            <ServiceStatus>1</ServiceStatus>\n"
                + "            <Bandwidth>1</Bandwidth>\n"
                + "            <reservationID>1</reservationID>\n"
                + "            <BandwidthGlobal>1</BandwidthGlobal>\n"
                + "            <Package>1</Package>\n"
                + "            <ReservedDeviceInformation>\n"
                + "                <Type>STP</Type>\n"
                + "                <ID>" + odpId + "</ID>\n"
                + "                <Name>" + odpName + "</Name>\n"
                + "                <DownlinkPort>\n"
                + "                    <Name>" + odpPortName + "</Name>\n"
                + "                    <ID>" + odpPortId + "</ID>\n"
                + "                </DownlinkPort>\n"
                + "            </ReservedDeviceInformation>\n"
                + "        </ent:reserveEndpointRequest>\n"
                + "    </SOAP-ENV:Body>\n"
                + "</SOAP-ENV:Envelope>";

        return request;
    }

    public String getSoapResponseReservation(String wonum, String odpName, String odpId, String odpPortName, String odpPortId) throws IOException, MalformedURLException, JSONException, SQLException {
        String reservationID = "";
        String request = createSoapRequestReservation(odpName, odpId, odpPortName, odpPortId);

        // call UIM
        JSONObject temp = deviceUtil.callUIM(request);
        // Parsing response data
        LogUtil.info(this.getClass().getName(), "############ Parsing Data Response ##############");
        JSONObject envelope = temp.getJSONObject("env:Envelope").getJSONObject("env:Body");
        JSONObject reservation = envelope.getJSONObject("ent:reserveEndpointResponse");
        int statusCode = reservation.getInt("statusCode");
        if (statusCode == 4001) {
            reservationID = "Failed to reserved";
            attribute.updateWO("app_fd_workorderspec", "c_value='" + reservationID + "'", "c_wonum='" + wonum + "' AND c_assetattrid='STP_PORT_RESERVATION_ID'");
        } else {
            reservationID = reservation.getString("reservationID");
            LogUtil.info(getClass().getName(), "RESERVATIONID : " + reservationID);
            String value = attribute.getTaskAttrValue(wonum, "STP_PORT_RESERVATION_ID");
            LogUtil.info(getClass().getName(), "RESERVATIONID VALUE : " + value);
            if (reservationID.isEmpty()) {
                reservationID = "Failed to reserved";
                attribute.updateWO("app_fd_workorderspec", "c_value='" + reservationID + "'", "c_wonum='" + wonum + "' AND c_assetattrid='STP_PORT_RESERVATION_ID'");
            } else {
                if (value.isEmpty()) {
                    attribute.updateWO("app_fd_workorderspec", "c_value='" + reservationID + "'", "c_wonum='" + wonum + "' AND c_assetattrid='STP_PORT_RESERVATION_ID'");
                }
            }

        }

        return reservationID;
    }

    public JSONObject getAttributes(String wonum) throws JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT C_ASSETATTRID, C_VALUE "
                + "FROM APP_FD_WORKORDERSPEC "
                + "WHERE C_WONUM = ? "
                + "AND C_ASSETATTRID IN ('STP_NAME_ALN','STP_PORT_NAME_ALN','STP_ID', 'STP_PORT_ID', 'STP_PORT_RESERVATION_ID')";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultObj.put(rs.getString("C_ASSETATTRID"), rs.getString("C_VALUE"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }

}
