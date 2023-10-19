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
                + "      <reservationID>y" + reservationID + "</reservationID>\n"
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

    public JSONObject getSoapResponseReservation(String odpName, String odpId, String odpPortName, String odpPortId) throws IOException, MalformedURLException, JSONException {
        String request = createSoapRequestReservation(odpName, odpId, odpPortName, odpPortId);
        // call UIM
        JSONObject temp = deviceUtil.callUIM(request);
        return temp;
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
//
//    public String ReserveSTP(String attrid, String wonum) throws SQLException, JSONException, IOException {
//        String[] listAttrid = {"STP_PORT_NAME_ALN", "STP_PORT_ID"};
//        String[] listDetailActcode = {"Survey-Ondesk Manual", "Site-Survey Manual", "WFMNonCore Site Survey"};
//        String detailactcode = getParams(wonum);
//        JSONObject attribute = getAttributes(wonum);
//
//        String odpName = attribute.optString("STP_NAME_ALN");
//        String odpPortName = attribute.optString("STP_PORT_NAME_ALN");
//        String odpId = attribute.optString("STP_ID");
//        String odpPortId = attribute.optString("STP_PORT_ID");
//        String reservationID = attribute.optString("STP_PORT_RESERVATION_ID");
//
//        String[] attributes = {odpName, odpPortName, odpId, odpPortId, reservationID};
//
//        if (Arrays.asList(listAttrid).contains(attrid) && Arrays.asList(listDetailActcode).contains(detailactcode)) {
//            // Periksa apakah alnValue tidak kosong atau "None"
//            for (int i = 0; i < attributes.length; i++) {
//                if (attributes[i].equals("None") || attributes[i].isEmpty()) {
//                    // Hapus reservasi jika sudah ada
//                    if (!reservationID.isEmpty() && !"Failed to reserved".equals(reservationID)) {
//                        getSoapResponseUnReserve(reservationID);
//                    }
//                    // Lakukan reservasi
//                    getSoapResponseReservation(odpName, odpId, odpPortName, odpPortId);
//                }
//            }
//        }
//        return null;
//    }
}
