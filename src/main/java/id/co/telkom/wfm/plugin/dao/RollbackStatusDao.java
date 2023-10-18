/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.JSONException;
import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class RollbackStatusDao {

    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        Timestamp ts = Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }

    private String updateTaskStatus(String wonum, String status, String modifiedBy) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_status = ?, modifiedby = ?, dateModified = sysdate WHERE c_wonum = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {

            ps.setString(1, status);
            ps.setString(2, modifiedBy);
            ps.setString(3, wonum);
            int exe = ps.executeUpdate();

            if (exe > 0) {
                return "Update task status berhasil";
            } else {
                return "Update task gagal";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private void updateParentStatus(String wonum, String status, String modifiedBy) throws SQLException {
        String update = "UPDATE app_fd_workorder SET modifiedby = ?, c_status = ?, c_statusdate = ?, dateModified = ? WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            int index = 0;
            ps.setString(1 + index, modifiedBy);
            ps.setString(2 + index, status);
            ps.setTimestamp(3 + index, getTimeStamp());
            ps.setTimestamp(4 + index, getTimeStamp());
            ps.setString(5 + index, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Status updated to: " + status);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    private Map<String, String> getWoAttribute(String parent) throws JSONException {
        Map<String, String> allAttributesDict = new HashMap<>();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String selectQuery = "SELECT C_WONUM, C_STATUS "
                + "FROM APP_FD_WORKORDER "
                + "WHERE C_PARENT = ? "
                + "AND C_WOCLASS = 'ACTIVITY'";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                allAttributesDict.put("C_WONUM", rs.getString("C_STATUS"));
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return allAttributesDict;
    }

    public boolean rollbackStatus(String parent, String reqWonum, String modifiedBy) throws JSONException, SQLException {
        Map<String, String> attributeData = getWoAttribute(parent);
        LogUtil.info(getClass().getName(), "LIST ATTRIBUTE : " + attributeData);
        updateParentStatus(parent, "STARTWORK", modifiedBy);
        boolean isCompwaFound = false;
        for (Map.Entry<String, String> entry : attributeData.entrySet()) {
            String wonum = entry.getKey();
            String status = entry.getValue();
           
            if ("COMPWA".equals(status) && wonum.equals(reqWonum)) {
                // Mengembalikan status dari "COMPWA" ke "STARTWA"
                updateTaskStatus(wonum, "STARTWA", modifiedBy);
                // Menampilkan pesan ke konsol atau melakukan tindakan lain yang diperlukan
                System.out.println("Rollback status untuk wonum: " + wonum);
            } else if (isCompwaFound) {
                updateTaskStatus(wonum, "APPR", modifiedBy);
            }
        }
        // Mencetak data yang diperbarui ke konsol
        for (Map.Entry<String, String> entry : attributeData.entrySet()) {
            LogUtil.info(getClass().getName(), "wonum: " + entry.getKey() + ", status: " + entry.getValue());
        }
        return isCompwaFound;
    }
}
