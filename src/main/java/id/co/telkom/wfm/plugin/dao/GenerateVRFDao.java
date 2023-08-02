package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.util.TimeUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Date;

public class GenerateVRFDao {
    public JSONObject getAssetattrid(String wonum) throws SQLException, JSONException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid IN ('VRF_NAME','CUSTOMER_NAME', 'TOPOLOGY', 'SERVICE_TYPE', 'PE_NAME', 'RD')";

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String tempCValue = rs.getString("c_value").replace(" ", "%20");
                resultObj.put(rs.getString("c_assetattrid"), tempCValue);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return resultObj;
    }
    public boolean deletetkDeviceattribute(String wonum, Connection con) throws SQLException{
        boolean status = false;
        String queryDelete = "DELETE FROM app_fd_tk_deviceattribute WHERE c_ref_num = ?";
        PreparedStatement ps = con.prepareStatement(queryDelete);
        ps.setString(1, wonum);
        int count= ps.executeUpdate();
        if(count>0){
            status = true;
        }
        LogUtil.info(getClass().getName(), "Status Delete : "+status);
        return status;
    }

    public boolean updatetkDeviceattribute(String wonum, String description, String attr_type, String attr_name, Connection con) throws SQLException{
        boolean status = false;
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());

        String uuId = UuidGenerator.getInstance().getUuid();
        String queryUpdate = "INSERT INTO app_fd_tk_deviceattribute(id, c_description, c_attr_type , c_attr_name, c_ref_num, datecreated, datemodified) VALUES(?,?,?,?,?,?,?)";
        PreparedStatement ps = con.prepareStatement(queryUpdate);
        ps.setString(1, uuId);
        ps.setString(2, description);
        ps.setString(3, attr_type);
        ps.setString(4, attr_name);
        ps.setString(5, wonum);
        ps.setTimestamp(6, timestamp);
        ps.setTimestamp(7, timestamp);
        int count= ps.executeUpdate();
        if(count>0){
            status = true;
        }
        LogUtil.info(getClass().getName(), "Status Update : "+status);
        return status;
    }

    public String createRequest(String vrfName, String owner, String mesh, String maxRoutes){
        String value = "";
        try {
            JSONObject req = new JSONObject();
            req.put("vrfName", vrfName);
            req.put("serviceType", "VPN");
            req.put("owner", owner);
            req.put("topology", "MESH");
//            req.put("mesh", mesh);
            req.put("maxRoutes", 80);
//            req.put("maxRoutes", maxRoutes);
            value = req.toString();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        LogUtil.info(this.getClass().getName(), "\nJSON Request: " + value);
        return value;
    }
    public String callGenerateVRF(String wonum, String vrfName,String serviceType, String owner, String mesh, String maxRoutes) {
        String msg = "";
        try {
            JSONObject assetAttrId = getAssetattrid(wonum);
            LogUtil.info(this.getClass().getName(), "\nJSON Object: " + assetAttrId);
            String rd = assetAttrId.has("RD")? assetAttrId.get("RD").toString():null;
            vrfName = assetAttrId.has("VRF_NAME")? assetAttrId.get("VRF_NAME").toString():null;
            owner = assetAttrId.has("CUSTOMER_NAME")? assetAttrId.get("CUSTOMER_NAME").toString():null;
            String deviceName = assetAttrId.has("PE_NAME")? assetAttrId.get("PE_NAME").toString():null;
            String topology = assetAttrId.has("TOPOLOGY")? assetAttrId.get("TOPOLOGY").toString():null;
            serviceType = assetAttrId.has("SERVICE_TYPE")? assetAttrId.get("SERVICE_TYPE").toString():null;
            if(rd!=null){
                msg = "RD is already generated. Refresh/Reopen order to view the RD, RT Import, RT Export detail.";
            }else{
                String url = "https://api-emas.telkom.co.id:8443/api/vrf/generate";
                LogUtil.info(this.getClass().getName(), "\nSending 'POST' request to URL : " + url);

                URL obj = new URL(url);
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("POST");
                con.setRequestProperty("Accept", "application/json");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                try(OutputStream os = con.getOutputStream()) {
                    byte[] input = createRequest(vrfName, owner, mesh, maxRoutes).getBytes("utf-8");
                    os.write(input, 0, input.length);
                }
                int responseCode = con.getResponseCode();

                if(responseCode==200){
                    StringBuilder response;
                    try(BufferedReader br = new BufferedReader(
                            new InputStreamReader(con.getInputStream(), "utf-8"))) {
                        response = new StringBuilder();
                        String responseLine = null;
                        while ((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }
                        System.out.println(response.toString());
                    }

                    JSONObject jsonObject = new JSONObject(response.toString());
                    LogUtil.info(this.getClass().getName(), "\nresponseCode status : "+jsonObject.get("reservedRD"));
                }

            }
        } catch (Exception e) {
            msg = e.getMessage();
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return msg;
    }
}
