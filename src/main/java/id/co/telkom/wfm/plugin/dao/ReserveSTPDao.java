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

    private JSONObject getParams(String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT \n"
                + "spec.c_classspecid, \n"
                + "spec.c_sequence, \n"
                + "spec.c_assetattrid, \n"
                + "wo2.id, \n"
                + "wo2.c_classstructureid, \n"
                + "wo2.c_orgid, \n"
                + "wo2.c_siteid, \n"
                + "wo2.c_wonum, \n"
                + "wo2.c_parent, \n"
                + "wo1.c_crmordertype, \n"
                + "wo1.c_productname, \n"
                + "wo1.c_producttype, \n"
                + "wo2.c_detailactcode, \n"
                + "wo2.c_worktype, \n"
                + "wo1.c_scorderno, \n"
                + "wo1.c_ownergroup\n"
                + "FROM app_fd_workorder wo1\n"
                + "JOIN app_fd_workorder wo2 ON wo1.c_wonum = wo2.c_parent\n"
                + "JOIN app_fd_classspec spec ON wo2.c_classstructureid = spec.c_classstructureid\n"
                + "WHERE wo1.c_woclass = 'WORKORDER'\n"
                + "AND wo2.c_woclass = 'ACTIVITY'\n"
                + "AND wo2.c_wonum = ?"
                + "AND wo2.c_status = 'STARTWA'";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                resultObj.put("classspecid", rs.getString("c_classspecid"));
                resultObj.put("sequence", rs.getString("c_sequence"));
                resultObj.put("assetattrid", rs.getString("c_assetattrid"));
                resultObj.put("id", rs.getString("id"));
                resultObj.put("classstructureid", rs.getString("c_classstructureid"));
                resultObj.put("orgid", rs.getString("c_orgid"));
                resultObj.put("siteid", rs.getString("c_siteid"));
                resultObj.put("wonum", rs.getString("c_wonum"));
                resultObj.put("parent", rs.getString("c_parent"));
                resultObj.put("crmordertype", rs.getString("c_crmordertype"));
                resultObj.put("productname", rs.getString("c_productname"));
                resultObj.put("producttype", rs.getString("c_producttype"));
                resultObj.put("detailactcode", rs.getString("c_detailactcode"));
                resultObj.put("worktype", rs.getString("c_worktype"));
                resultObj.put("scorderno", rs.getString("c_scorderno"));
                resultObj.put("ownergroup", rs.getString("c_ownergroup"));
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

    private JSONObject getSoapResponseUnReserve(String reservationID) throws IOException, MalformedURLException, JSONException {
        APIConfig api = util.getApiParam("uim_dev");
        String urlres = api.getUrl();
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

    private JSONObject getSoapResponseReservation(String odpName, String odpId, String odpPortName, String odpPortId) throws IOException, MalformedURLException, JSONException {
        String request = createSoapRequestReservation(odpName, odpId, odpPortName, odpPortId);
        // call UIM
        JSONObject temp = deviceUtil.callUIM(request);
        return temp;
    }

    private JSONObject getAttributes(String wonum) throws JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT C_ASSETATTRID, C_VALUE "
                + "FROM WORKORDERSPEC "
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

    public void ReserveSTP(String attrid, String wonum) throws SQLException, JSONException, IOException {
        String[] listAttrid = {"STP_PORT_NAME_ALN", "STP_PORT_ID"};
        String[] listDetailActcode = {"Survey-Ondesk Manual", "Site-Survey Manual", "WFMNonCore Site Survey"};
        JSONObject param = getParams(wonum);
        JSONObject attribute = getAttributes(wonum);

        String alnValue = "";
        String odpName = attribute.optString("STP_NAME_ALN");
        String odpPortName = attribute.optString("STP_PORT_NAME_ALN");
        String odpId = attribute.optString("STP_ID");
        String odpPortId = attribute.optString("STP_PORT_ID");
        String reservationID = attribute.optString("STP_PORT_RESERVATION_ID");

        if (Arrays.asList(listAttrid).contains(attrid) && Arrays.asList(listDetailActcode).contains(param.optString("detailactcode"))) {
            // Periksa apakah alnValue tidak kosong atau "None"
            if (!"None".equals(alnValue) && !alnValue.isEmpty()) {
                // Hapus reservasi jika sudah ada
                if (!reservationID.isEmpty() && !"Failed to reserved".equals(reservationID)) {
                    getSoapResponseUnReserve(reservationID);
                }
                // Lakukan reservasi
                getSoapResponseReservation(odpName, odpId, alnValue, odpPortId);
            }
        }
    }
}
