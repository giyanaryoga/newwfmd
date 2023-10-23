/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.controller.InsertIntegrationHistory;
import java.io.*;
import java.net.*;
import java.net.URL;
import java.sql.*;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.*;
import org.json.*;
import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class NonCoreCompleteDao {
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

    // Generate ServiceAddress
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
                + "?, SERVICEADDRESSIDSEQ.NEXTVAL, ?, ?, ?, ?, ?)";

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
                LogUtil.info(getClass().getName(), "Generate Service Address Successfully for " + addresscode);
            } else {
                LogUtil.info(getClass().getName(), "Data insertion failed.");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
    }
    
    // Clear Asset 
    private boolean clearAssetNoncore(String assetnum, Connection con) throws SQLException {
        boolean status = false;
        String queryDelete = "DELETE FROM app_fd_asset WHERE c_assetnum = ?";
        PreparedStatement ps = con.prepareStatement(queryDelete);
        ps.setString(1, assetnum);
        int count = ps.executeUpdate();
        if (count > 0) {
            status = true;
        }
        LogUtil.info(getClass().getName(), "Status Delete : " + status);
        return status;
    }
    // Generate Asset
    public void generateServiceAsset(String assetnum, String location, String saddresscode, String assettype, String serviceno, String classstructureid) throws SQLException {
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
                + " (?, ASSETIDSEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, ?)";

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
            
            clearAssetNoncore(assetnum, con);            
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "Generate Service Asset Successfully for " + assetnum);
            } else {
                LogUtil.info(getClass().getName(), "Data insertion failed.");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
    }

    // Generate attribute AssetSpec
    public void generateAssetSpecAttribute(String assetnum, String siteid, String detailactcode, String wonum) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        java.util.Date date = new java.util.Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        String selectQuery
                = "SELECT c_mandatory, c_classstructureid, c_assetattrid, c_sequence, c_defaultvalue "
                + "FROM app_fd_classspec WHERE c_activity = ?";
        String insertQuery
                = "INSERT INTO app_fd_assetspec"
                + "(id, "
                + "c_assetnum, "
                + "c_assetattrid, "
                + "c_classstructureid, "
                + "c_displaysequence, "
                + "c_alnvalue, "
                + "c_changedate, "
                + "c_siteid, "
                + "c_orgid, "
                + "c_mandatory, "
                + "c_assetspecid) "
                + "VALUES "
                + "(?, ?, ?, ?, ?, "
                + "(SELECT c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid = ?), "
                + "?, ?, ?, ?, ASSETSPECIDSEQ.NEXTVAL)";

        try (Connection con = ds.getConnection()) {
            boolean oldAutoCommit = con.getAutoCommit();
            LogUtil.info(getClass().getName(), "start' auto commit state: " + oldAutoCommit);
            con.setAutoCommit(false);

            try (PreparedStatement psSelect = con.prepareStatement(selectQuery);
                    PreparedStatement psInsert = con.prepareStatement(insertQuery)) {
                con.setAutoCommit(false);
                psSelect.setString(1, detailactcode);

                ResultSet rs = psSelect.executeQuery();
                while (rs.next()) {
                    psInsert.setString(1, UuidGenerator.getInstance().getUuid());
                    psInsert.setString(2, assetnum);
                    psInsert.setString(3, (rs.getString("c_assetattrid") == null ? "" : rs.getString("c_assetattrid")));
                    psInsert.setString(4, (rs.getString("c_classstructureid") == null ? "" : rs.getString("c_classstructureid")));
                    psInsert.setString(5, (rs.getString("c_sequence") == null ? "" : rs.getString("c_sequence")));
                    psInsert.setString(6, wonum);
                    psInsert.setString(7, (rs.getString("c_assetattrid") == null ? "" : rs.getString("c_assetattrid")));
                    psInsert.setTimestamp(8, timestamp);
                    psInsert.setString(9, siteid);
                    psInsert.setString(10, "TELKOM");
                    psInsert.setString(11, (rs.getString("c_mandatory") == null ? "" : rs.getString("c_mandatory")));
                    psInsert.addBatch();
                }
                clearAttributeNoncore(assetnum, con);
                int[] exe = psInsert.executeBatch();
                if (exe.length > 0) {
                    LogUtil.info(getClass().getName(), "Success generated " + exe.length + " task attributes, for " + assetnum);
                }
                con.commit();
            } catch (SQLException e) {
                LogUtil.error(getClass().getName(), e, "Trace Error Here: " + e.getMessage());
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(oldAutoCommit);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName() + " | generateTaskAttribute", e, "Trace Error Here: " + e.getMessage());
            throw e;
        }
    }
    
    // Clear Attribute
    private boolean clearAttributeNoncore(String assetnum, Connection con) throws SQLException {
        boolean status = false;
        String queryDelete = "DELETE FROM app_fd_assetspec WHERE c_assetnum = ?";
        PreparedStatement ps = con.prepareStatement(queryDelete);
        ps.setString(1, assetnum);
        int count = ps.executeUpdate();
        if (count > 0) {
            status = true;
        }
        LogUtil.info(getClass().getName(), "Status Delete : " + status);
        return status;
    }
    
    // Reserve Resource UIM
    public JSONObject reserveResource(String serviceId) throws MalformedURLException, IOException, JSONException {
        InsertIntegrationHistory dao = new InsertIntegrationHistory();
        try {
            JSONArray attributes = getAttributeNoncore(serviceId);
            String attrName = "";
            String attrValue = "";

            String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <ent:reserveResourcesRequest>\n"
                    + "         <!--Optional:-->\n"
                    + "         <ServiceType>NONCORE</ServiceType>\n"
                    + "         <reservationID>" + serviceId + "</reservationID>\n"
                    + "         <!--1 to 20 repetitions:-->\n";

            String request3 = "      </ent:reserveResourcesRequest>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";

            StringBuilder repeatedRequest = new StringBuilder();
            for (int i = 0; i < attributes.length(); i++) {
                org.json.JSONObject result = attributes.getJSONObject(i);
                attrName = result.getString("attributeName");
                attrValue = result.getString("attributeValue");
                
                String request2 = " <AttributeInformation>\n"
                        + "            <attributeName>" + attrName + "</attributeName>\n"
                        + "            <attributeValue>" + attrValue + "</attributeValue>\n"
                        + "         </AttributeInformation>\n";
                repeatedRequest.append(request2);
            }

            String finalRequest = request + repeatedRequest.toString() + request3;

            LogUtil.info(getClass().getName(), "Reserve Request : " + finalRequest);
            String urlres = "http://10.60.170.43:7051/EnterpriseFeasibilityUim/EnterpriseFeasibilityUimHTTP";
            URL url = new URL(urlres);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            // Set Headers
            connection.setRequestProperty("Accept", "application/xml");
            connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            try ( // Write XML
                    OutputStream outputStream = connection.getOutputStream()) {
                byte[] b = finalRequest.getBytes("UTF-8");
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
            
            dao.insertIntegrationHistory(serviceId, "COMPLETENONCORE", "COMPLETENONCORE", finalRequest, temp.toString());
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Call Failed." + e);
        }
        return null;
    }
    
    // Get Attribute from table assetspec
    public JSONArray getAttributeNoncore(String assetnum) throws SQLException {
        JSONArray listAttr = new JSONArray();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_alnvalue FROM app_fd_assetspec WHERE c_assetnum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, assetnum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject attributeObject = new JSONObject();
                attributeObject.put("attributeName", rs.getString("c_assetattrid"));
                attributeObject.put("attributeValue", rs.getString("c_alnvalue"));
                listAttr.put(attributeObject);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return listAttr;
    }
}
