/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class JsonUtil {

    public String getString(JSONObject obj, String key) {
        return obj.get(key) == null ? "" : obj.get(key).toString();
    }

    public Long getLong(JSONObject obj, String key) {
        return obj.get(key) == null ? null : ((Number) obj.get(key)).longValue();
    }

    public JSONObject getUpdateStatusSuccessResp(String wonum, String status, String message) {
        //Create response
        JSONObject data = new JSONObject();
        data.put("wonum", wonum);
        data.put("status", status);
        JSONObject res = new JSONObject();
        res.put("code", 200);
        res.put("message", message);
        res.put("data", data);
        return res;
    }

    public JSONObject getUpdateStatusErrorResp(String wonum, String status, String message, int errorCode) {
        //Create response
        JSONObject data = new JSONObject();
        data.put("wonum", wonum);
        data.put("status", status);
        JSONObject res = new JSONObject();
        res.put("code", errorCode);
        res.put("message", message);
        res.put("data", data);
        return res;
    }

    public JSONObject reserveResourceUIM(String reservationId) throws SQLException {
        JSONObject reserveResource = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetnum from app_fd_assetspec where c_assetnum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, reservationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                reserveResource.put("reserveationID", rs.getString("c_assetnum"));
                reserveResource.put("AttributeInformation", getAttributeNoncore(reservationId));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }

        //XML Wrapper configuration
        reserveResource.put("ServiceType", "NONCORE");

        JSONObject attrs = new JSONObject();
        attrs.put("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        attrs.put("xmlns:soapenc", "http://schemas.xmlsoap.org/soap/encoding/");
        attrs.put("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
        attrs.put("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        attrs.put("xmlns:ent", "http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility");

        JSONObject reserveResourceRequest = new JSONObject();
        reserveResourceRequest.put("reserveResourcesRequest", reserveResource);

        JSONObject envelope = new JSONObject();
        envelope.put("Header", "");
        envelope.put("Body", reserveResourceRequest);
        envelope.put("@ns", "soapenv");
        envelope.put("attrs", attrs);

        JSONObject reserveJson = new JSONObject();
        reserveJson.put("Envelope", envelope);

        return reserveJson;
    }

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
                listAttr.add(attributeObject);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return listAttr;
    }
}
