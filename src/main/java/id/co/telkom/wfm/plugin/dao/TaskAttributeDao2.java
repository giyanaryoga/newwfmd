/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class TaskAttributeDao2 {
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        Timestamp ts = Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
    
    public void updateOwnerGroup(String wonum, String task, String ownerGroup) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "UPDATE app_fd_workorder SET c_ownergroup = ?, dateModified = sysdate WHERE c_wonum = ? AND c_detailactcode = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, ownerGroup);
            ps.setString(1, wonum);
            ps.setString(2, task);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
//                value = rs.getString("c_value");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public String getCrmOrderType(String parent) throws SQLException {
        String orderType = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_crmordertype FROM app_fd_workorder WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                orderType = rs.getString("c_crmordertype");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return orderType;
    }
    
    public String getScOrderNo(String parent) throws SQLException {
        String scorderno = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_scorderno FROM app_fd_workorder WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                scorderno = rs.getString("c_scorderno");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return scorderno;
    }
    
    public void updateReadOnly(String wonum, String assetattrid, int readonly) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append("UPDATE app_fd_workorderspec SET ")
                .append(" c_readonly = ?, ")
                .append(" datemodified = ? ")
                .append(" WHERE ")
                .append(" c_wonum = ? ")
                .append(" AND c_assetattrid = ? ");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setInt(1, readonly);
            ps.setTimestamp(2, getTimeStamp());
            ps.setString(3, wonum);
            ps.setString(4, assetattrid);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Assetattrid readonly update to:  " + assetattrid);
            } else {
                LogUtil.info(getClass().getName(), "Readonly is not updated");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void deleteTaskAttr(String wonum, String assetattrid) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append("DELETE FROM app_fd_workorderspec ")
                .append("WHERE ")
                .append("c_wonum = ?")
                .append("AND c_assetattrid = ?");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, wonum);
            ps.setString(2, assetattrid);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Assetattrid delete:  " + assetattrid);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void deleteTkDeviceattribute(String wonum, String attr_name) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String select = "SELECT * FROM app_fd_tk_deviceattribute WHERE c_ref_num = ? AND c_attr_name in (?)";
        String delete = "DELETE FROM app_fd_tk_deviceattribute WHERE c_ref_num = ? AND c_attr_name in (?)";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(select);
                PreparedStatement ps2 = con.prepareStatement(delete);) {
            ps.setString(1, wonum);
            ps.setString(2, attr_name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    LogUtil.info(getClass().getName(), "Berhasil menghapus data");
                }
            } else {
                LogUtil.info(getClass().getName(), "Gagal menghapus data");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void insertToDeviceTable(String wonum, String type, String attr_name, String description) throws SQLException {
        // Generate UUID
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String insert = "INSERT INTO APP_FD_TK_DEVICEATTRIBUTE (ID, C_REF_NUM, C_ATTR_NAME, C_ATTR_TYPE, C_DESCRIPTION) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(insert)) {
            ps.setString(1, uuId);
            ps.setString(2, wonum);
            ps.setString(3, attr_name);
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
    
    public JSONArray getTaskWDM(String parent) throws SQLException {
        JSONArray attrName = new JSONArray();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_detailactcode ")
                .append(" FROM app_fd_workorder WHERE ")
                .append(" c_parent = ? AND ")
                .append(" c_detailactcode in ('WFMNonCore PassThrough Port WDM 1','WFMNonCore PassThrough Port WDM 2','WFMNonCore PassThrough Port WDM 3',"
                        + "'WFMNonCore PassThrough Port WDM 4','WFMNonCore PassThrough Port WDM 5','WFMNonCore PassThrough Port WDM 6',"
                        + "'WFMNonCore PassThrough Port WDM 7','WFMNonCore PassThrough Port WDM 8','WFMNonCore PassThrough Port WDM 9',"
                        + "'WFMNonCore PassThrough Port WDM 10')");
        
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject intObj = new JSONObject();
                String activity = rs.getString("c_detailactcode");
                String[] splitted = activity.split("WDM ");
                String lastString = splitted[1];
                int task = Integer.parseInt(lastString);
                intObj.put("int_activity", task);
                attrName.add(intObj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close(); 
        }
        return attrName;
    }
    
    public String getDescTkDeviceAttr(String wonum, String attrName, String attrType) throws SQLException {
        String description = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_description FROM app_fd_tk_deviceattribute WHERE c_ref_num = ? AND c_attr_type = ? AND c_attr_name = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, attrType);
            ps.setString(3, attrName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                description = rs.getString("c_description");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return description;
    }
}
