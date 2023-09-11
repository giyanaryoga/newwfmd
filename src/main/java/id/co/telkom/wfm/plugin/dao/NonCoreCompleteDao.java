/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.controller.InsertIntegrationHistory;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONObject;
import javax.xml.soap.*;

/**
 *
 * @author ASUS
 */
public class NonCoreCompleteDao {

    public String getStringSoapMessage(SOAPMessage soapMessage) {
        try {
            SOAPPart soapPart = soapMessage.getSOAPPart();
            SOAPEnvelope envelope = soapPart.getEnvelope();
            return envelope.toString();
        } catch (Exception e) {
            return "";
        }
    }

    // Check Product Non-Core
    public int isNonCoreProduct(String productname) {
        int value = 0;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectQuery = "SELECT c_productname FROM app_fd_wfmproduct WHERE c_productname = ?";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {

            ps.setString(1, productname);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString("c_productname") != null) {
                    value = 1;
                } else {
                    value = 0;
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return value;
    }

    // Get Workorderattribute value
    public JSONObject getWorkorderattributeValue(String wonum, String attrname) throws SQLException {
        JSONObject result = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectQuery = "SELECT c_attr_name, c_attr_value \n"
                + "FROM app_fd_workorderattribute \n"
                + "WHERE c_attr_name = ?\n"
                + "AND c_wonum = ?";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, attrname);
            ps.setString(2, wonum);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.put(rs.getString("c_attr_name"), rs.getString("c_attr_value"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return result;
    }

    // Get Task Attribute
    public String getTaskattributeValue(String wonum, String assetattrid) throws SQLException {
        String result = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String selectQuery = "SELECT c_value "
                + "FROM app_fd_workorderspec "
                + "WHERE c_wonum = ?"
                + "AND c_assetattrid = ? ";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, wonum);
            ps.setString(2, assetattrid);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                result = rs.getString("c_value");
                LogUtil.info(getClass().getName(), "Attribute Value " + assetattrid + " :" + result);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return result;
    }

    // Get AssetClassStructureid
    public String getAssetClassstructureid(String assetType) throws SQLException {
        String result = "";

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectQuery
                = "SELECT c_classstructureid \n"
                + "FROM app_fd_classstructure\n"
                + "WHERE c_classificationid = ? \n"
                + "AND c_parent IN (\n"
                + "    SELECT c_classstructureid \n"
                + "    FROM app_fd_classstructure \n"
                + "    WHERE c_classificationid = 'WFM_PRODUCT'\n"
                + "    )";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, assetType);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString("c_classstructureid");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return result;
    }

    public void generateServiceAddress(String addresscode, String siteid, String description) throws SQLException {
        String uuId = UuidGenerator.getInstance().getUuid();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectQuery
                = "INSERT INTO app_fd_serviceaddress"
                + " ("
                + " id,"
                + " c_serviceaddressid,"
                + " c_addresscode,"
                + " c_country,"
                + " c_siteid,"
                + " c_description,"
                + " c_orgid"
                + " ) VALUES ("
                + "?, WFMDBDEV01.SERVICEADDRESSIDSEQ.NEXTVAL, ?, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, uuId);
            ps.setString(2, addresscode);
            ps.setString(3, "ID");
            ps.setString(4, siteid);
            ps.setString(5, description);
            ps.setString(6, "TELKOM");

            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), addresscode + "Generate Service Address Successfully");
            } else {
                LogUtil.info(getClass().getName(), "Data insertion failed.");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
    }

    public void generateServiceAsset(String assetnum, String location, String saddresscode, String assettype, String serviceno, String classstructureid) {
        String uuId = UuidGenerator.getInstance().getUuid();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectQuery
                = "INSERT INTO app_fd_asset\n"
                + "(\n"
                + " id, \n"
                + " c_assetid, \n"
                + " c_assetnum,\n"
                + " c_location,\n"
                + " c_saddresscode,\n"
                + " c_assettype,\n"
                + " c_tk_serviceno,\n"
                + " c_classstructureid,\n"
                + " c_status\n"
                + " )\n"
                + " VALUES\n"
                + " (?, WFMDBDEV01.ASSETIDSEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, uuId);
            ps.setString(2, assetnum);
            ps.setString(3, location);
            ps.setString(4, saddresscode);
            ps.setString(5, assettype);
            ps.setString(6, serviceno);
            ps.setString(7, classstructureid);
            ps.setString(8, "OPERATING");

            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), assetnum + "Generate Service Asset Successfully");
            } else {
                LogUtil.info(getClass().getName(), "Data insertion failed.");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
    }

    public void generateServiceSpecFromWorkorder(String assetnum, String parent) throws SQLException {
        String assetattrid = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectAssetSpec = "SELECT c_assetattrid, c_alnvalue FROM app_fd_assetspec WHERE c_assetnum = ?";
        String insertAssetSpecValue = "INSERT INTO app_fd_assetspec (c_alnvalue) VALUES (?) WHERE c_assetnum = ?";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectAssetSpec);
                PreparedStatement psInsert = con.prepareStatement(insertAssetSpecValue)) {
            ps.setString(1, assetnum);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                assetattrid = rs.getString("c_assetattrid");
            }
            if (assetattrid == null) {
                String assetSpecValue = getTaskattributeValue(parent, rs.getString("c_assetattrid"));
                if (assetSpecValue != "") {
                    psInsert.setString(1, assetSpecValue);
                    psInsert.setString(2, assetnum);
                    psInsert.executeQuery();

                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
    }

    public void generateAssetWorkzone(String assetnum, String workzone, String siteid) {
        String uuId = UuidGenerator.getInstance().getUuid();

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectQuery
                = "INSERT INTO app_fd_assetworkzone \n"
                + "(\n"
                + " id,\n"
                + " c_assetworkzoneid,\n"
                + " c_assetnum,\n"
                + " c_workzone,\n"
                + " c_siteid,\n"
                + " c_type,\n"
                + " c_orgid\n"
                + " )\n"
                + " VALUES\n"
                + " (?, WFMDBDEV01.ASSETWORKZONEIDSEQ.NEXTVAL, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, uuId);
            ps.setString(2, assetnum);
            ps.setString(3, workzone);
            ps.setString(4, siteid);
            ps.setString(5, "ZONE");
            ps.setString(6, "TELKOM");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int exe = ps.executeUpdate();
                if (exe > 0) {
                    LogUtil.info(getClass().getName(), assetnum + "Generate Asset Workzone Successfully");
                } else {
                    LogUtil.info(getClass().getName(), "Data insertion failed.");
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
    }

    public static SOAPMessage createSoapRequestReserveResourceUIM(String reservationId, Map<String, String> attributeInfo) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();
        SOAPPart soapPart = soapMessage.getSOAPPart();
        SOAPEnvelope envelope = soapPart.getEnvelope();
        MimeHeaders headers = soapMessage.getMimeHeaders();
        headers.addHeader("SOAPAction", "http://xmlns.oracle.com/communications/inventory/webservice/FindDeviceByCriteria");
        envelope.addNamespaceDeclaration("ent", "http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility");
        SOAPBody soapBody = envelope.getBody();
        SOAPElement soapBodyElem = soapBody.addChildElement("reserveResourcesRequest", "ent");

        SOAPElement soapBodyElemServiceType = soapBodyElem.addChildElement("ServiceType");
        soapBodyElemServiceType.addTextNode("NONCORE");
        SOAPElement soapBodyElemReservationId = soapBodyElem.addChildElement("reservationID");
        soapBodyElemReservationId.addTextNode(reservationId);

        for (Map.Entry<String, String> entry : attributeInfo.entrySet()) {
            SOAPElement soapBodyElemAttributeInfo = soapBodyElem.addChildElement("AttributeInformation");
            SOAPElement soapBodyElemAttributeInfoName = soapBodyElemAttributeInfo.addChildElement("attributeName");
            soapBodyElemAttributeInfoName.addTextNode(entry.getKey());
            SOAPElement soapBodyElemAttributeInfoValue = soapBodyElemAttributeInfo.addChildElement("attributeValue");
            soapBodyElemAttributeInfoValue.addTextNode(entry.getValue());
        }
        soapMessage.saveChanges();
        return soapMessage;
    }

    public void reserveResourceUIM(String wonum, String SID) {
        InsertIntegrationHistory insertIntegration = new InsertIntegrationHistory();

        if (true) {
            Map<String, String> attributeInfo = new HashMap<>();
            try {
                SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
                SOAPConnection soapConnection = soapConnectionFactory.createConnection();

                DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
                String selectQuery = "SELECT * FROM app_fd_assetspec WHERE assetnum = ?";

                try (Connection con = ds.getConnection();
                        PreparedStatement ps = con.prepareStatement(selectQuery)) {
                    ps.setString(1, SID);
                    ResultSet rs = ps.executeQuery();

                    while (rs.next()) {
                        String attrId = rs.getString("c_assetattrid");
                        String alnValue = rs.getString("c_alnvalue");
                        attributeInfo.put(attrId, alnValue.isEmpty() ? "-" : alnValue);
                    }
                } catch (SQLException e) {
                    LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
                }

                SOAPMessage soapRequest = createSoapRequestReserveResourceUIM(SID, attributeInfo);
                SOAPMessage soapResponse = soapConnection.call(soapRequest, "http://10.60.170.43:7051/EnterpriseFeasibilityUim/EnterpriseFeasibilityUimHTTP");

                String requestString = getStringSoapMessage(soapRequest);
                String substringReq = requestString.substring(0, Math.min(requestString.length(), 500));
                String responseString = getStringSoapMessage(soapResponse);
                String substringRes = responseString.substring(0, Math.min(responseString.length(), 500));
                LogUtil.info(getClass().getName(), "Response Reserve Resource : " + responseString);
                LogUtil.info(getClass().getName(), "SubString Response Reserve Resource : " + substringRes);

                insertIntegration.insertIntegrationHistory(wonum, "COMPLETENONCORE", substringReq, substringRes, "COMPLETENONCORE");

                SOAPBody soapBody = soapResponse.getSOAPBody();
                LogUtil.info(this.getClass().getName(), "SOAP BODY :" + soapBody);
                soapConnection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
