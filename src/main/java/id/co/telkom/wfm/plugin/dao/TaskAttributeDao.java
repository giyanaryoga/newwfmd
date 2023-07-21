/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.ListAttributes;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author User
 */
public class TaskAttributeDao {
    public String getAttrName(String wonum, String AttrName) throws SQLException {
        String attrName = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_wonum, c_attribute_name FROM app_fd_workorderspec WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                attrName = rs.getString("c_attribute_name");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return attrName;
    }
    
    public String getAttrValue(String wonum, String AttrName) throws SQLException {
        String attrValue = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_wonum, c_attribute_name, c_alnvalue FROM app_fd_workorderspec WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                attrValue = rs.getString("c_attribute_name");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return attrValue;
    }
    
    public JSONArray getAttribute(String wonum, String classStructureId) throws SQLException {
        JSONArray listAttribute = new JSONArray();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_wonum, c_classstructureid, c_attribute_name, c_alnvalue, c_isrequired, c_isshared, c_readonly \n"
                + "FROM app_fd_workorderspec WHERE c_wonum = ? AND c_classstructureid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, classStructureId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                JSONObject attributeObject = new JSONObject();
                attributeObject.put("wonum", rs.getString("c_wonum"));
                attributeObject.put("attribute_name", rs.getString("c_attribute_name"));
                attributeObject.put("attribute_value", rs.getString("c_alnvalue"));
                attributeObject.put("required", rs.getString("c_isrequired"));
                attributeObject.put("shared", rs.getString("c_isshared"));
                attributeObject.put("readonly", rs.getString("c_readonly"));
                listAttribute.add(attributeObject);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
       
        return listAttribute;
    }   
}
