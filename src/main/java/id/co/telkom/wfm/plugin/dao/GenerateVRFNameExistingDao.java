package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.util.TimeUtil;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Date;

public class GenerateVRFNameExistingDao {
    TimeUtil time = new TimeUtil();
    public String findVRF(String wonum) throws SQLException, JSONException {
        String result = null;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid IN ('VRF_NAME')";

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result = rs.getString("c_value");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return result;
    }


    public String callGenerateVRFNameExisting(String wonum) {
        String msg = "";
        try {
            LogUtil.info(this.getClass().getName(), "\nSending 'GET' request to URL : " + wonum);
            String vrfName= findVRF(wonum);
            LogUtil.info(this.getClass().getName(), "\nVRF Name : " + vrfName);


            String url = "https://api-emas.telkom.co.id:8443/api/vrf/find?vrfName=*"+vrfName+"*";
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Accept", "application/json");
            int responseCode = con.getResponseCode();
            LogUtil.info(this.getClass().getName(), "\nSending 'GET' request to URL : " + url);
            LogUtil.info(this.getClass().getName(), "Response Code : " + responseCode);

            if (responseCode == 400) {
                LogUtil.info(this.getClass().getName(), "STO not found");

            } else if (responseCode == 200) {
                DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
                Connection connection = ds.getConnection();

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                LogUtil.info(this.getClass().getName(), "STO : " + response);
                in.close();

                    // At this point, 'response' contains the JSON data as a string
                String jsonData = response.toString();

                // Now, parse the JSON data using org.json library
//                JSONObject jsonObject = new JSONObject(jsonData);
////                JSONObject port = jsonObject.getJSONObject("port");
//                JSONArray arrayPort = jsonObject.getJSONArray("port");
//                for (int i = 0; i < arrayPort.length(); i++) {
//                    JSONObject portObject = arrayPort.getJSONObject(i);
//                    msg=msg+"Portname: "+portObject.getString("name")+"\n";
//                    msg=msg+"Keyname: "+portObject.getString("key")+"\n";
//
//                    LogUtil.info(this.getClass().getName(), "Object Port :" + arrayPort.toString());
//                    String description = portObject.getString("name");
//                    String attr_type = "";
//                    updatetkDeviceattribute(wonum, description, attr_type, "AN_UPLINK_PORTNAME",connection);
//
//                    description = portObject.getString("key");
//                    attr_type = portObject.getString("name");
//                    updatetkDeviceattribute(wonum, description, attr_type, "AN_UPLINK_PORTID",connection);
//                }
                return "VRF Name Existing Found\n"+msg;
            }
        } catch (Exception e) {
            msg = e.getMessage();
            msg = "Generate VRF Name Failed.\n"+msg;
            LogUtil.info(this.getClass().getName(), "Trace error here :" + e.getMessage());
        }
        return msg;
    }

}
