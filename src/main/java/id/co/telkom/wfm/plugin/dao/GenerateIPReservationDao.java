package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.ListGenerateAttributes;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONException;
import org.json.XML;
import org.json.simple.JSONObject;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;

public class GenerateIPReservationDao {
    //====================================
    // Insert to table INTEGRATION_HISTORY
    //====================================
    public void insertIntegrationHistory(String wonum, String apiType, String request, String response, String currentDate) throws SQLException {
        // Generate UUID
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String integrationHistorySet = "INSERT INTO INTEGRATION_HISTORY (WFMWOID, INTEGRATION_TYPE, PARAM1, REQUEST, RESPONSE, EXEC_DATE) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(integrationHistorySet.toString())) {
            ps.setString(1, uuId);
            ps.setString(2, wonum);
            ps.setString(3, "RESERVEIP");
            ps.setString(4, apiType);
            ps.setString(5, request);
            ps.setString(6, response);
            ps.setString(7, currentDate);
        }
    }

    //=========================================================
    // Call API Surrounding Generate SID CONNECTIVITY for SDWAN
    //=========================================================
    public JSONObject callGenerateConnectivity(String wonum, String serviceType, String nameReq, String cardinality, String ipType, String vrf, String ipArea,String ipVersion, String packageType, ListGenerateAttributes listGenerate) throws MalformedURLException, IOException, JSONException {
        try {
            String soapRequest = "";

            if(serviceType.equals("VPN") || serviceType=="VPN IP Global" || serviceType=="VPN IP Business" || serviceType=="VPN IP Domestik"){
                LogUtil.info(this.getClass().getName(), "INI VPN : " + serviceType);
                soapRequest = createSoapRequestVPN(serviceType, nameReq,cardinality, ipType);
            }else if(serviceType.equals("CDN")){
                LogUtil.info(this.getClass().getName(), "INI CDN : " + serviceType);
                soapRequest = createSoapRequestVersion(serviceType, vrf, ipType, ipArea, ipVersion);
            }else if(serviceType.equals("TRANSIT")){
                LogUtil.info(this.getClass().getName(), "INI TRANSIT : " + serviceType);
                soapRequest = createSoapReuestAllocateIPV6(serviceType, ipType, ipArea, ipVersion);
            }else{
                LogUtil.info(this.getClass().getName(), "INI ELSE : " + serviceType);
                soapRequest = createSoapRequest(serviceType, vrf, ipType, ipArea, cardinality);
            }
            LogUtil.info(this.getClass().getName(), "INI REQUEST : " + soapRequest);

            String urlres = "http://10.6.28.132:7001/EnterpriseFeasibilityUim/EnterpriseFeasibilityUimHTTP";
            URL url = new URL(urlres);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            // Set Headers
            connection.setRequestProperty("Accept", "application/xml");
            connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            try ( // Write XML
                  OutputStream outputStream = connection.getOutputStream()) {
                byte[] b = soapRequest.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }

            StringBuilder response;
            try ( // Read XML
                  InputStream inputStream = connection.getInputStream()) {
                byte[] res = new byte[2048];
                int i = 0;
                response = new StringBuilder();
                while ((i = inputStream.read(res)) != -1) {
                    response.append(new String(res, 0, i));
                }
            }
            StringBuilder result = response;
            org.json.JSONObject temp = XML.toJSONObject(result.toString());
            System.out.println("temp " + temp.toString());
            LogUtil.info(this.getClass().getName(), "INI RESPONSE : " + temp.toString());

            //Parsing response data
            LogUtil.info(this.getClass().getName(), "############ Parsing Data Response ##############");

            org.json.JSONObject envelope = temp.getJSONObject("env:Envelope").getJSONObject("env:Body");
            org.json.JSONObject service = envelope.getJSONObject("ent:reserveServiceIpSubnetResponse");
            int statusCode = service.getInt("statusCode");

            LogUtil.info(this.getClass().getName(), "StatusCode : " + statusCode);
//            ListGenerateAttributes listGenerate = new ListGenerateAttributes();

            if (statusCode == 404) {
                LogUtil.info(this.getClass().getName(), "SubnetReserved Not found!");
                listGenerate.setStatusCode(statusCode);
            } else if (statusCode == 200 || statusCode == 4000) {
//                    "GatewayAddress": "36.88.255.74",
//                    "ServiceIp": "36.88.255.72/29",
//                    "IpDomain": "Global IP Address Domain",
//                    "NetworkAddress": "36.88.255.73",
//                    "reservationID": "22464129273",
//                    "IpType": "LAN",
//                    "SubnetMask": "255.255.255.248",
//                    "NetMask": "29",
//                    "IpArea": "REG-2"

                org.json.JSONObject serviceInfo = service.getJSONObject("SubnetReserved");
                LogUtil.info(this.getClass().getName(), "SubnetReserved "+serviceInfo);
                String gatewayAddressRes = serviceInfo.getString("GatewayAddress");
                String serviceIpRes = serviceInfo.getString("ServiceIp");
                String ipDomainRes = serviceInfo.getString("IpDomain");
                String networkAddressRes = serviceInfo.getString("NetworkAddress");
                String reservationIDRes = serviceInfo.getString("reservationID");
                String subnetMaskRes = serviceInfo.getString("SubnetMask");
                String netMaskRes = serviceInfo.getString("NetMask");
                String ipAreaRes = serviceInfo.getString("IpArea");


                listGenerate.setGateawayAddress(gatewayAddressRes);
                listGenerate.setServiceIp(serviceIpRes);
                listGenerate.setIpDomain(ipDomainRes);
                listGenerate.setNetworkAddress(networkAddressRes);
                listGenerate.setReservationId(reservationIDRes);
                listGenerate.setSubnetMask(subnetMaskRes);
                listGenerate.setNetMask(netMaskRes);
                listGenerate.setIpArea(ipAreaRes);
                listGenerate.setIpType(ipType);
                listGenerate.setPackageType(packageType);
                listGenerate.setStatusCode3(statusCode);

                LogUtil.info(this.getClass().getName(), "Data : " +listGenerate);
                LogUtil.info(this.getClass().getName(), "get attribute : " + listGenerate.getStatusCode3());
                String message = "Data Tidak Ditemukan";
                if(listGenerate.getIpType().equals("LAN")){
                    boolean updateWOSpec = updateWorkOrderSpec(wonum,listGenerate,"LAN%", serviceType, cardinality);
                    if(updateWOSpec){
                        message="IP LAN Reserved with reservationID:"+listGenerate.getReservationId();
                    }
                }else if(listGenerate.getIpType().equals("WAN") && listGenerate.getPackageType().equals("GLOBAL")){
                    boolean updateWOSpec = updateWorkOrderSpec(wonum,listGenerate,"WAN%", serviceType, cardinality);
                    if(updateWOSpec){
                         message="IP WAN Reserved with reservationID:"+listGenerate.getReservationId();
                    }
                }else if(listGenerate.getIpType().equals("WAN") && listGenerate.getPackageType().equals("DOMESTIK")){
                    boolean updateWOSpec = updateWorkOrderSpec(wonum,listGenerate,"WAN%DOMESTIK", serviceType, cardinality);
                    if(updateWOSpec){
                        message="IP WAN Domestic Reserved with reservationID:"+listGenerate.getReservationId();
                    }
                }
                listGenerate.setMessage(message);
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Call Failed." + e);
        }
//        ListGenerateAttributes attr = new ListGenerateAttributes();
        return null;

    }
    public void allTable() throws SQLException{
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try {
            Connection con = ds.getConnection();
            DatabaseMetaData md = con.getMetaData();
            ResultSet rsa = md.getTables(null, null, "%", null);
            while (rsa.next()) {
                LogUtil.info(getClass().getName(), "Table Name: " + rsa.getString(3));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    public boolean updateWorkOrderSpec(String wonum, ListGenerateAttributes listGenerateAttributes,String ipType, String serviceType, String cardinality) throws SQLException{
        boolean status = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String query = "SELECT * FROM APP_FD_WORKORDERSPEC";
        try {
            Connection con = ds.getConnection();
            if (con != null && !con.isClosed()) {
                PreparedStatement ps = con.prepareStatement(query);
//                ps.setString(1, wonum);
//                ps.setString(2, "("+ipType+")");
//                ps.executeUpdate();
//                LogUtil.info(getClass().getName(), "Wonum: " + wonum);
//                LogUtil.info(getClass().getName(), "IpType: " + ipType);
//                LogUtil.info(getClass().getName(), "Query ps to string: " + ps.toString());

                ResultSet rs = ps.executeQuery();
                LogUtil.info(getClass().getName(), "RS Select: " + rs);

                if (rs != null) {
                    int size = 0;
                    while (rs.next()) {
                        size++;
                        String c_alnvalue = "";
                        String c_assetattrid = rs.getString("c_assetattrid");
                        LogUtil.info(getClass().getName(), "Asset Attribute ID : " + c_assetattrid);
                        if (ipType.equals("WAN%DOMESTIK")) {
                            ipType.replace("%", "-%-");
                        } else {
                            ipType.replace("%", "-%");
                        }
                        LogUtil.info(getClass().getName(), "IP TYPE: " + ipType);

                        if (c_assetattrid.equals(ipType.replace("%", "GATEWAYADDRESS"))) {
                            status = updateALNVALUE(wonum, c_assetattrid, listGenerateAttributes.getGateawayAddress(), con);
                        }
                        if (c_assetattrid.equals(ipType.replace("%", "IPDOMAIN"))) {
                            status = updateALNVALUE(wonum, c_assetattrid, listGenerateAttributes.getIpDomain(), con);
                        }
                        if (c_assetattrid.equals(ipType.replace("%", "NETWORKADDRESS"))) {
                            status = updateALNVALUE(wonum, c_assetattrid, listGenerateAttributes.getNetworkAddress(), con);
                        }
                        if (c_assetattrid.equals(ipType.replace("%", "reservationID"))) {
                            status = updateALNVALUE(wonum, c_assetattrid, listGenerateAttributes.getReservationId(), con);
                        }
                        if (c_assetattrid.equals(ipType.replace("%", "ServiceIp"))) {
                            status = updateALNVALUE(wonum, c_assetattrid, listGenerateAttributes.getServiceIp(), con);
                        }
                        if (c_assetattrid.equals(ipType.replace("%", "SUBNETMASK"))) {
                            if (serviceType == "CDN" && cardinality == "6") {
                                status = updateALNVALUE(wonum, c_assetattrid, listGenerateAttributes.getNetMask(), con);
                            } else {
                                status = updateALNVALUE(wonum, c_assetattrid, listGenerateAttributes.getSubnetMask(), con);
                            }
                        }
                    }
                    LogUtil.info(getClass().getName(), "Berhasil merubah data , count : " + size + ", status : " + status);
                } else {
                    LogUtil.info(getClass().getName(), "Gagal merubah data");
                }
            }else{
                LogUtil.info(getClass().getName(), "Disconnect");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }

        return status;
    }

    public boolean updateALNVALUE(String wonum, String c_assetattrid,String valueUpdate, Connection con) throws SQLException {
        boolean status = false;

        String queryUpdate = "UPDATE APP_FD_WORKORDERSPEC SET c_alnvalue= ? WHERE c_wonum = ? AND c_assetattrid = ?";
        PreparedStatement ps = con.prepareStatement(queryUpdate);
        ps.setString(1, valueUpdate);
        ps.setString(2, wonum);
        ps.setString(3, c_assetattrid);
        LogUtil.info(getClass().getName(), "Query Update : "+queryUpdate);
        ResultSet update = ps.executeQuery();
        LogUtil.info(getClass().getName(), "Result Set Update : "+update);
        return true;
    }

    public void insertIntoDeviceTable(String wonum, ListGenerateAttributes listGenerate) throws SQLException {
//        ListGenerateAttributes listAttribute = new ListGenerateAttributes();
        // Generate UUID
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        StringBuilder insert = new StringBuilder();
//        insert
//                .append("INSERT INTO app_fd_tk_deviceattribute")
//                .append("(")
//                .append("id,")
//                .append("c_ref_num,")
//                .append("c_attr_name,")
//                .append("c_attr_type,")
//                .append("description")
//                .append(")")
//                .append("VALUES")
//                .append("(")
//                .append("?,")
//                .append("?,")
//                .append("?,")
//                .append("?,")
//                .append("?");
        String insert = "INSERT INTO APP_FD_TK_DEVICEATTRIBUTE (ID, C_REF_NUM, C_ATTR_NAME, C_ATTR_TYPE, C_DESCRIPTION) VALUES (?, ?, ?, ?, ?)";
//      Table workorderspec
        try (Connection con = ds.getConnection(); PreparedStatement ps = con.prepareStatement(insert.toString())) {
            ps.setString(1, uuId);
            ps.setString(2, wonum);
            ps.setString(3, listGenerate.getName());
            ps.setString(4, "");
            ps.setString(5, listGenerate.getId());

            int exe = ps.executeUpdate();

            if (exe > 0) {
                LogUtil.info(this.getClass().getName(), "insert data successfully");
            }
        }catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    public String createSoapRequestVPN(String serviceType, String name, String cardinality, String ipType){
        String request = "<soapenv:Envelope xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\"\n"
                + "                  xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <soapenv:Body>\n"
                + "        <ent:reserveServiceIpSubnetRequest>\n"
                + "            <ServiceType>"+serviceType+"</ServiceType>\n"
                + "            <SubnetReservation> \n"
                + "                 <name>"+name+"</name>\n"
                + "                 <Cardinality>"+cardinality+"</Cardinality>\n"
                + "                 <IpType>"+ipType+"</IpType>\n"
                + "            </SubnetReservation> \n"
                + "        </ent:reserveServiceIpSubnetRequest>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return request;
    }

    public String createSoapRequestVersion(String serviceType, String vrf, String ipType, String ipArea, String ipVersion){
        String request = "<soapenv:Envelope xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\"\n"
                + "                  xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <soapenv:Body>\n"
                + "        <ent:reserveServiceIpSubnetRequest>\n"
                + "            <ServiceType>"+serviceType+"</ServiceType>\n"
                + "            <SubnetReservation> \n"
                + "                 <VRF>"+vrf+"</VRF>\n"
                + "                 <IpType>"+ipType+"</IpType>\n"
                + "                 <IpArea>"+ipArea+"</IpArea>\n"
                + "                 <IPVersion>"+ipVersion+"</IPVersion>\n"
                + "            </SubnetReservation> \n"
                + "        </ent:reserveServiceIpSubnetRequest>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return request;
    }

    public String createSoapReuestAllocateIPV6(String serviceType, String ipType, String ipArea, String ipVersion){
        String request = "<soapenv:Envelope xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\"\n"
                + "                  xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <soapenv:Body>\n"
                + "        <ent:reserveServiceIpSubnetRequest>\n"
                + "            <ServiceType>"+serviceType+"</ServiceType>\n"
                + "            <SubnetReservation> \n"
                + "                 <IpType>"+ipType+"</IpType>\n"
                + "                 <IpArea>"+ipArea+"</IpArea>\n"
                + "                 <IPVersion>"+ipVersion+"</IPVersion>\n"
                + "            </SubnetReservation> \n"
                + "        </ent:reserveServiceIpSubnetRequest>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return request;
    }

    public String createSoapRequest(String serviceType, String vrf, String ipType, String ipArea, String cardinality){
        String request = "<soapenv:Envelope xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\"\n"
                + "                  xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <soapenv:Body>\n"
                + "        <ent:reserveServiceIpSubnetRequest>\n"
                + "            <ServiceType>"+serviceType+"</ServiceType>\n"
                + "            <SubnetReservation> \n"
                + "                 <VRF>"+vrf+"</VRF>\n"
                + "                 <IpType>"+ipType+"</IpType>\n"
                + "                 <IpArea>"+ipArea+"</IpArea>\n"
                + "                 <Cardinality>"+cardinality+"</Cardinality>\n"
                + "            </SubnetReservation> \n"
                + "        </ent:reserveServiceIpSubnetRequest>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return request;
    }
}
