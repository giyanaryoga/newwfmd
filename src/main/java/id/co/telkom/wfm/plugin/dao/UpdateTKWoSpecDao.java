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

/**
 *
 * @author User
 */
public class UpdateTKWoSpecDao {
    public boolean getApiAttribute(String apiId, String apiKey) throws SQLException {
        boolean  isAuthSuccess = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_api_id, c_api_key FROM app_fd_api_wfm WHERE c_use_of_api = 'mytech_integration'";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (apiId.equals(rs.getString("c_api_id")) && apiKey.equals(rs.getString("c_api_key"))) {
                    isAuthSuccess = true;
                }
            }
        } catch(SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return isAuthSuccess;
    }
    
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"); 
        Timestamp ts =  Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
    
    public boolean updateAttributeMyStaff(String wonum, String siteId, String assetAttrId, String value, String changeBy, String changeDate) throws SQLException {
        boolean taskUpdated = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
                .append(" UPDATE app_fd_workorderspec SET ")
                .append(" c_value = ?, ")
                .append(" c_alnvalue = ?, ")
                .append(" c_changeby = ?, ")
                .append(" c_changedate = ?, ")
                .append(" c_siteid = ?, ")
                .append(" datemodified = ?, ")
                .append(" modifiedby = ? ")
                .append(" WHERE c_wonum = ? ")
                .append(" AND ")
                .append(" c_assetattrid = ? ");
//        if (!siteId.equals("")) {
//            update
//                    .append(" AND c_siteid = ? ");
//        }
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, value);
            ps.setString(2, value);
            ps.setString(3, changeBy);
            ps.setString(4, changeDate);
            ps.setString(5, siteId);
            ps.setTimestamp(6, getTimeStamp());
            ps.setString(7, changeBy);
            ps.setString(8, wonum);
            ps.setString(9, assetAttrId);
            
            int exe = ps.executeUpdate();
            if (exe > 0){
                taskUpdated = true;
                LogUtil.info(getClass().getName(), "update task attribute mystaff berhasil");
            } else {
                LogUtil.info(getClass().getName(), "update task attribute gagal");
          }
        } catch (Exception e) {
          LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
          ds.getConnection().close();
        }
        return taskUpdated;
    } 
}
