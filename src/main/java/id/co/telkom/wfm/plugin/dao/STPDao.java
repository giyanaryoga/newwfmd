package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.util.DeviceUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class STPDao {

    DeviceUtil util = new DeviceUtil();

    public String getSTPPortSoapResquest(String node1, String node1_value, String detail, String role) {
        String request = "<soapenv:Envelope xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\"\n"
                + "                  xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                + "    <soapenv:Body>\n"
                + "        <ent:findDeviceByCriteriaRequest>\n"
                + "            <DeviceInfo>\n"
                + "                 <" + node1 + ">" + node1_value + "</" + node1 + ">\n"
                + "                 <detail>" + detail + "</detail>\n"
                + "                 <role>" + role + "</role>\n"
                + "            </DeviceInfo>\n"
                + "        </ent:findDeviceByCriteriaRequest>\n"
                + "    </soapenv:Body>\n"
                + "</soapenv:Envelope>";
        return request;
    }

    public String getSTPPortSoapResponse(String wonum, String network_location, String role) {
        String msg = "";
        try {
            String request = getSTPPortSoapResquest("name", network_location, "true", role);
            // Hit API UIM Dev
            JSONObject temp = util.callUIM(request);

            //Parsing response data
            LogUtil.info(this.getClass().getName(), "############ Parsing Data Response ##############");

            JSONObject envelope = temp.getJSONObject("env:Envelope").getJSONObject("env:Body");
            LogUtil.info(this.getClass().getName(), "envelope : " + envelope);
            JSONObject service = envelope.getJSONObject("ent:findDeviceByCriteriaResponse");
            int statusCode = service.getInt("statusCode");

            LogUtil.info(this.getClass().getName(), "StatusCode : " + statusCode);
//            ListGenerateAttributes listGenerate = new ListGenerateAttributes();
            if (statusCode == 404) {
                LogUtil.info(this.getClass().getName(), "No device found for network location");
//                listGenerate.setStatusCode(statusCode);

                msg = msg + "Call Not Found.";
            } else if (statusCode == 200 || statusCode == 4000) {
                JSONObject deviceInfo = service.getJSONObject("DeviceInfo");
                JSONArray ports = deviceInfo.getJSONArray("ports");

                // Delete TK Device Attribut
                boolean deleteTK = deletetkDeviceattribute(wonum);
                LogUtil.info(this.getClass().getName(), "Delete TK Device : " + deleteTK);

                // Insert TK Device Attribut
                for (int i = 0; i < ports.length(); i++) {
                    JSONObject portObject = ports.getJSONObject(i);
                    LogUtil.info(this.getClass().getName(), "Object Port :" + ports.toString());
                    String name = portObject.getString("name");
                    String id = portObject.getString("id");
                    msg = msg + "STP Name :" + name + "\n";
                    msg = msg + "STP ID : " + id + "\n";
                    LogUtil.info(this.getClass().getName(), "Name : " + name);
                    LogUtil.info(this.getClass().getName(), "ID : " + id);
                    boolean insertStpName = insertIntoDeviceTable(wonum, "STP_PORT_NAME", name, network_location);
                    boolean insertStpID = insertIntoDeviceTable(wonum, "STP_PORT_ID", id, name);

                    LogUtil.info(this.getClass().getName(), "Insert STP Name TK Device : " + insertStpName);
                    LogUtil.info(this.getClass().getName(), "Insert STP ID TK Device : " + insertStpID);
                }   

            }
        } catch (Exception e) {
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return msg;
    }

    public boolean deletetkDeviceattribute(String wonum) throws SQLException {
        boolean status = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        Connection con = ds.getConnection();
        String queryDelete = "DELETE FROM app_fd_tk_deviceattribute WHERE c_ref_num = ? AND c_attr_name in ('STP_PORT_NAME','STP_PORT_ID')";
        PreparedStatement ps = con.prepareStatement(queryDelete);
        ps.setString(1, wonum);
        int count = ps.executeUpdate();
        if (count > 0) {
            status = true;
        }
        LogUtil.info(getClass().getName(), "Status Delete : " + status);
        return status;
    }

    public boolean insertIntoDeviceTable(String wonum, String name, String description, String attr_type) throws SQLException {
//        ListGenerateAttributes listAttribute = new ListGenerateAttributes();
        // Generate UUID
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        boolean status = false;
        String insert = "INSERT INTO APP_FD_TK_DEVICEATTRIBUTE (ID, C_REF_NUM, C_ATTR_NAME, C_ATTR_TYPE, C_DESCRIPTION) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(insert.toString())) {

            ps.setString(1, uuId);
            ps.setString(2, wonum);
            ps.setString(3, name);
            ps.setString(4, attr_type);
            ps.setString(5, description);

            int exe = ps.executeUpdate();

            if (exe > 0) {
                status = true;
                LogUtil.info(this.getClass().getName(), "insert data successfully");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return status;
    }

    public String getRole(String wonum) throws SQLException, JSONException {
        String role = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_value FROM APP_FD_WORKORDERSPEC WHERE c_wonum=? AND c_assetattrid='ROLE'";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                role = rs.getString("c_value");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return role;
    }

    public String getSTP_NETWORKLOCATION(String wonum, String alnvalue) throws SQLException, JSONException {
        String result = "";
        String role = getRole(wonum);
        LogUtil.info(this.getClass().getName(), "Role :" + role);

        if (role != null) {
            if (role.equals("STP")) {
                result = getSTPPortSoapResponse(wonum, alnvalue, role);
                if (result.isEmpty()) {
                    result = "No device found for network location: " + alnvalue;
                }
            }
        }
        return result;
    }

}
