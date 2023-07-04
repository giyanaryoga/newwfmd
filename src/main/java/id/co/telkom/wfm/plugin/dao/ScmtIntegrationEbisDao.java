/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.model.ListAttributes;
import id.co.telkom.wfm.plugin.model.ListScmtIntegrationParam;
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
import javax.sql.DataSource;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author User
 */
public class ScmtIntegrationEbisDao {
    public String getScmtToken() {
        String token = "";
        RequestAPI api = new RequestAPI();
        ConnUtil connUtil = new ConnUtil();
        try {
          APIConfig apiConfig = new APIConfig();
          apiConfig = connUtil.getApiParam("get_eai_token_scmt");
          FormBody formBody = (new FormBody.Builder()).add("grant_type", apiConfig.getGrantType()).add("client_id", apiConfig.getClientId()).add("client_secret", apiConfig.getClientSecret()).build();
          String response = "";
          response = api.sendPostEaiToken(apiConfig, (RequestBody)formBody);
          JSONParser parse = new JSONParser();
          JSONObject data_obj = (JSONObject)parse.parse(response);
          token = data_obj.get("access_token").toString();
        } catch (Exception e) {
          LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } 
        return token;
    }
    
    public JSONObject getWoAttribute(String wonum) throws SQLException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT C_ATTR_NAME, C_ATTR_VALUE FROM APP_FD_WORKORDERATTRIBUTE WHERE C_WONUM = ? AND C_ATTR_NAME IN ('LONGITUDE', 'LATITUDE')";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                resultObj.put(rs.getString("C_ATTR_NAME"), rs.getString("C_ATTR_VALUE"));
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return resultObj;
    }
    
    public void sendInstall(String parent) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" parent.c_wonum, ")
                .append(" parent.c_scorderno, ")
                .append(" parent.c_customer_name,  ")
                .append(" parent.c_serviceaddress,  ")
                .append(" parent.c_workzone,  ")
                .append(" parent.c_servicenum, ")
                .append(" parent.c_siteid,  ")
                .append(" parent.c_description, ")
                .append(" child.c_laborcode,  ")
                .append(" item.c_alnvalue ")
                .append(" FROM app_fd_workorder parent  ")
                .append(" JOIN app_fd_workorder child  ")
                .append(" ON parent.c_wonum = child.c_parent  ")
                .append(" JOIN app_fd_workorderspec item ")
                .append(" ON child.c_wonum = item.c_wonum ")
                .append(" WHERE ")
                .append(" parent.c_wonum = ? ")
                .append(" AND ")
                .append(" child.c_description ")
                .append(" IN ('Registration Suplychain', 'Registration Suplychain Wifi') ")
                .append(" AND ")
                .append(" item.c_attribute_name = 'NTE_SERIALNUMBER' ")
                .append(" AND ")
                .append(" child.c_wfmdoctype = 'NEW' ");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ListScmtIntegrationParam scmtParam = new ListScmtIntegrationParam();
                
                ListAttributes attribute = new ListAttributes();
                scmtParam.setWonum((rs.getString("c_wonum") == null) ? "" : rs.getString("c_wonum"));
                scmtParam.setScOrderNo((rs.getString("c_scorderno") == null) ? "" : rs.getString("c_scorderno"));
                scmtParam.setLaborCode((rs.getString("c_laborcode") == null) ? "" : rs.getString("c_laborcode"));
                scmtParam.setCustomerName((rs.getString("c_customer_name") == null) ? "" : rs.getString("c_customer_name"));
                scmtParam.setServiceAddress((rs.getString("c_serviceaddress") == null) ? "" : rs.getString("c_serviceaddress"));
                scmtParam.setWorkzone((rs.getString("c_workzone") == null) ? "" : rs.getString("c_workzone"));
                scmtParam.setServiceNum((rs.getString("c_servicenum") == null) ? "" : rs.getString("c_servicenum"));
                scmtParam.setCpeSerialNumber((rs.getString("c_alnvalue") == null) ? "" : rs.getString("c_alnvalue"));
                attribute.setLongitude((getWoAttribute(parent).get("LONGITUDE").toString() == null) ? "" : getWoAttribute(parent).get("LONGITUDE").toString());
                attribute.setLatitude((getWoAttribute(parent).get("LATITUDE").toString() == null) ? "" : getWoAttribute(parent).get("LATITUDE").toString());
                scmtParam.setDescription((rs.getString("c_description") == null) ? "" : rs.getString("c_description"));
                scmtParam.setSiteId((rs.getString("c_siteid") == null) ? "" : rs.getString("c_siteid"));
                
                //Send install message to kafka
                JSONObject installMessage = buildInstallMessage(scmtParam, attribute);
                String kafkaRes = installMessage.toJSONString();
                KafkaProducerTool kaf = new KafkaProducerTool();
                kaf.generateMessage(kafkaRes, "WFM_NEWSCMT_INSTALL", "");
                LogUtil.info(getClass().getName(), " " + scmtParam + " keluar!!! ");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            if (ds.getConnection() != null)
                ds.getConnection().close();
        }
    }

    private JSONObject buildInstallMessage(ListScmtIntegrationParam scmtParam, ListAttributes attribute) {
        String segment = "";
        String order = scmtParam.getScOrderNo().substring(0,2);
        if (order.equalsIgnoreCase("1-"))
            segment = "DES";
        if (order.equalsIgnoreCase("2-"))
            segment = "DWS";
        if (order.equalsIgnoreCase("SC") || scmtParam.getScOrderNo().substring(0, 3).equalsIgnoreCase("MYI"))
            segment = "DCS";
        //Serial number
        JSONObject serialNumber = new JSONObject();
        serialNumber.put("number", scmtParam.getCpeSerialNumber());
        serialNumber.put("type", "serial_number");

        //Items
        JSONArray items = new JSONArray();
        items.add(serialNumber);
        //Eai body
        JSONObject eaiBody = new JSONObject();
        eaiBody.put("trc_type", Integer.valueOf(1));
        eaiBody.put("technician_code", scmtParam.getLaborCode());
        eaiBody.put("location_name", scmtParam.getCustomerName());
        eaiBody.put("location_code", scmtParam.getServiceNum());
        eaiBody.put("location_address", scmtParam.getServiceAddress());
        eaiBody.put("location_segment", segment);
        eaiBody.put("workzone", scmtParam.getWorkzone());
        eaiBody.put("service_id", scmtParam.getServiceNum());
        eaiBody.put("latitude", attribute.getLatitude());
        eaiBody.put("longitude", attribute.getLongitude());
        eaiBody.put("service_ord_id", scmtParam.getScOrderNo());
        eaiBody.put("regional", Integer.valueOf(Integer.parseInt(scmtParam.getSiteId().substring(4))));
        eaiBody.put("external_order_number", scmtParam.getWonum());
        eaiBody.put("items", items);
        eaiBody.put("creator", "ext007");
        eaiBody.put("caller", "WFM");
        //Eai header
        JSONObject eaiHeader = new JSONObject();
        eaiHeader.put("internalId", "");
        eaiHeader.put("externalId", scmtParam.getScOrderNo());
        eaiHeader.put("timestamp", getTimeStamp().toString());
        //Api asset request
        JSONObject apiAssetRequest = new JSONObject();
        apiAssetRequest.put("eaiHeader", eaiHeader);
        apiAssetRequest.put("eaiBody", eaiBody);
        //Install Message
        JSONObject installMessage = new JSONObject();
        installMessage.put("apiAssetRequest", apiAssetRequest);
        return installMessage;
    }
    
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        Timestamp ts = Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
}
