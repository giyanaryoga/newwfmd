/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.util;

import id.co.telkom.wfm.plugin.model.APIConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author ASUS
 */
public class DeviceUtil {

    // GET WORKORDER PARAMS
    public JSONObject getParams(String wonum) throws SQLException, JSONException {
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
                + "AND wo2.c_wonum = ?";

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

    // CLEAR DATA TK_DEVICEATTRIBUTE WHERE REF_NUM = ?
    public void deleteTkDeviceattribute(String wonum) throws SQLException {
        DataSource dataSource = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String deleteQuery = "DELETE FROM APP_FD_TK_DEVICEATTRIBUTE WHERE C_REF_NUM = ?";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {

            preparedStatement.setString(1, wonum);
            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                LogUtil.info(getClass().getName(), "Berhasil menghapus data");
            } else {
                LogUtil.info(getClass().getName(), "Gagal menghapus data");
            }

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }

    // INSERT INTO TK_DEVICEATTRIBUTE
    public void insertToDeviceTable(String wonum, String name, String type, String description) throws Throwable {
        // Generate UUID
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO APP_FD_TK_DEVICEATTRIBUTE (ID, C_REF_NUM, C_ATTR_NAME, C_ATTR_TYPE, C_DESCRIPTION) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(insert)) {
            ps.setString(1, uuId);
            ps.setString(2, wonum);
            ps.setString(3, name);
            ps.setString(4, type);
            ps.setString(5, description);

            int exe = ps.executeUpdate();

            if (exe > 0) {
                LogUtil.info(this.getClass().getName(), "Berhasil menambahkan data");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    // INSERT WORKLOG
    public void insertWorkLog(String parent, String siteId, JSONObject workLog) throws SQLException, JSONException {
        StringBuilder insert = new StringBuilder();
        insert
                .append(" INSERT INTO app_fd_worklog ")
                .append(" ( ")
                //TEMPLATE CONFIGURATION
                .append(" id, dateCreated, dateModified, createdby, createdByName, modifiedBy, modifiedByName,  ")
                .append(" c_fk, ")//FOREIGN KEY
                .append(" c_recordkey, ")
                .append(" c_siteid, ")
                .append(" c_logtype, ")
                .append(" c_description, ")
                .append(" c_longdescription, ")
                .append(" c_createby, ")
                .append(" c_createdate ")
                .append(" ) ")
                .append(" VALUES ")
                .append(" ( ")
                .append(" ?, sysdate, sysdate, 'MYTECH', 'MYTECH', 'MYTECH', 'MYTECH', ")
                .append(" ( ")//GET ID AS FOREIGN KEY
                .append(" SELECT ")
                .append(" id ")
                .append(" FROM app_fd_workorder ")
                .append(" WHERE ")
                .append(" c_wonum = ? ")
                .append(" AND ")
                .append(" c_siteid = ? ")
                .append(" ), ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" 'MYTECH', ")
                .append(" ? ")
                .append(" ) ");
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(insert.toString())) {
            ps.setString(1, UuidGenerator.getInstance().getUuid());
            ps.setString(2, parent);
            ps.setString(3, siteId);
            ps.setString(4, parent);
            ps.setString(5, siteId);
            ps.setString(6, workLog.get("logtype") == null ? "" : workLog.get("logtype").toString());
            ps.setString(7, workLog.get("description") == null ? "" : workLog.get("description").toString());
            ps.setString(8, workLog.get("descriptionlongdescription") == null ? "" : workLog.get("descriptionlongdescription").toString());
            TimeUtil time = new TimeUtil();
            ps.setString(9, time.getCurrentTime());
            ps.executeUpdate();
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
            throw e;
        }
    }
    
    public JSONObject callUIM(String request) throws MalformedURLException, IOException, JSONException {
//        URLManager urlManager = new URLManager();
        ConnUtil util = new ConnUtil();
        APIConfig api = new APIConfig();
        
        util.getApiParam("uim_dev");
        
        String urlres = api.getUrl();
//        String urlres = urlManager.getURL("UIM");
        URL url = new URL(urlres);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        // Set Headers
        connection.setRequestProperty("Accept", "application/xml");
        connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
        try ( // Write XML
                OutputStream outputStream = connection.getOutputStream()) {
            byte[] b = request.getBytes("UTF-8");
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
        LogUtil.info(this.getClass().getName(), "INI REQUEST XML : " + request);
        LogUtil.info(this.getClass().getName(), "INI RESPONSE : " + temp.toString());
        
        return temp;
    }
}
