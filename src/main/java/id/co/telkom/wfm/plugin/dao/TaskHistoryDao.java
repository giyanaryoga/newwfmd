/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.*;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.*;

/**
 *
 * @author ASUS
 */
public class TaskHistoryDao {

    public void insertTaskStatus(String wonum, String memo, String wostatusid) {
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        StringBuilder insertQuery = new StringBuilder();
        insertQuery
                .append("INSERT INTO app_fd_wostatus ")
                .append("(")
                .append("id, ")
                .append("c_wonum, ")
                .append("c_status, ")
                .append("c_memo, ")
                .append("c_orgid, ")
                .append("c_siteid, ")
                .append("c_wostatusid, ")
                .append("c_parent ")
//                .append("modifiedby ")
                .append(") ")
                .append("VALUES ")
                .append("(?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?) ");
//                .append("? ");

        // Additional query to fetch values
        String selectQuery = "SELECT c_parent, c_status, c_orgid, c_siteid FROM app_fd_workorder WHERE c_wonum = ?";

        try (Connection con = ds.getConnection();
                PreparedStatement selectPs = con.prepareStatement(selectQuery);
                PreparedStatement insertPs = con.prepareStatement(insertQuery.toString())) {
            selectPs.setString(1, wonum);
            ResultSet resultSet = selectPs.executeQuery();
            if (resultSet.next()) {
                String fetchedParent = resultSet.getString("c_parent");
                String fetchedStatus = resultSet.getString("c_status");
                String fetchedOrgid = resultSet.getString("c_orgid");
                String fetchedSiteid = resultSet.getString("c_siteid");

                insertPs.setString(1, uuId);
                insertPs.setString(2, wonum);
                insertPs.setString(3, fetchedStatus);
                insertPs.setString(4, memo);
                insertPs.setString(5, fetchedOrgid);
                insertPs.setString(6, fetchedSiteid);
                insertPs.setString(7, wostatusid);
                insertPs.setString(8, fetchedParent);
//                insertPs.setString(9, modifiedBy);

                int exe = insertPs.executeUpdate();
                if (exe > 0) {
                    LogUtil.info(getClass().getName(), wonum + " inserted to WOSTATUS table successfully ");
                }
            } else {
                LogUtil.info(getClass().getName(), "No data found for " + wonum);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }

}
