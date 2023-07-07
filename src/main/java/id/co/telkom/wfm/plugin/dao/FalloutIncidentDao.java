/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author ASUS
 */
public class FalloutIncidentDao {
    public boolean updateStatus(String statusCode, String ticketId) throws SQLException {
        boolean updateStatus = false;
        DataSource ds= (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE APP_FD_INCIDENT SET C_TK_STATUSCODE = ? WHERE C_TICKETID = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, statusCode);
            ps.setString(2, ticketId);
            int exe  = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "update status berhasil");
                updateStatus = true;
            } else {
                LogUtil.info(getClass().getName(), "update status gagal");
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return updateStatus;
    }
}
