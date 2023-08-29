/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.controller;

import java.sql.*;
import java.util.Date;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author ASUS
 */
public class InsertIntegrationHistory {
    public void insertIntegrationHistory(String wonum, String apitype, String request, String response, String typeint) throws SQLException {
        Date currDate = new Date(); // You can replace this with your actual implementation for getting the current date

        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String insertQuery = "INSERT INTO app_fd_integration_history (c_referenceid, c_integration_type, c_param1, c_request, c_response, c_exec_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = ds.getConnection();
             PreparedStatement ps = con.prepareStatement(insertQuery)) {
            
            ps.setString(1, wonum);
            ps.setString(2, typeint);
            ps.setString(3, apitype);
            ps.setString(4, request);
            ps.setString(5, response);
            ps.setDate(6, new java.sql.Date(currDate.getTime()));

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int exe = ps.executeUpdate();
                if (exe > 0) {
                    LogUtil.info(getClass().getName(), wonum + "Insert Integration History Successfully");
                } else {
                    LogUtil.info(getClass().getName(), "Data insertion failed.");
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
    }

}
