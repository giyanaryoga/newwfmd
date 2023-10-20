/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.*;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class IntegrationHistoryDao {
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"); 
        Timestamp ts =  Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }

    public void insertIntegrationHistory(String wonum, String integrationType, Integer exec_state, String req, String res, String result) {
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        StringBuilder insertQuery = new StringBuilder();
        insertQuery
                .append("INSERT INTO app_fd_integration_history ")
                .append("(")
                .append("id, ")
                .append("c_integration_historyid, ")
                .append("c_referenceid, ") //REFERENCEID = WONUM
                .append("c_param1, ")
                .append("c_param2, ")
                .append("c_param3, ")
                .append("c_param4, ")
                .append("c_param5, ")
                .append("c_integration_type, ")
                .append("c_exec_state, ")
                .append("c_request, ")
                .append("c_response, ")
                .append("c_insertdate, ")
                .append("c_exec_date, ")
                .append("c_result ")
                .append(") ")
                .append("VALUES ")
                .append("(?, ")
                .append("INTEGRATION_HISTORYIDSEQ.NEXTVAL, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?, ")
                .append("?) ");

        // Additional query to fetch values
        String selectQuery = "SELECT c_parent, c_assetattrid, c_orgid, c_siteid FROM app_fd_workorderspec WHERE c_wonum = ?";

        try (Connection con = ds.getConnection();
                PreparedStatement selectPs = con.prepareStatement(selectQuery);
                PreparedStatement insertPs = con.prepareStatement(insertQuery.toString())) {
            selectPs.setString(1, wonum);
            ResultSet resultSet = selectPs.executeQuery();
            if (resultSet.next()) {
                String fetchedParent = resultSet.getString("c_parent");
                String fetchedAssetAttrId = resultSet.getString("c_assetattrid");
//                String fetchedOrgid = resultSet.getString("c_orgid");
                String fetchedSiteid = resultSet.getString("c_siteid");

                insertPs.setString(1, uuId);
                insertPs.setString(2, wonum);
                insertPs.setString(3, fetchedParent); //param1
                insertPs.setString(4, fetchedAssetAttrId); //param2
                insertPs.setString(5, fetchedSiteid); //param3
                insertPs.setString(6, ""); //param4
                insertPs.setString(7, ""); //param5
                insertPs.setString(8, integrationType); //integration_type
                insertPs.setInt(9, exec_state); //exec_state
                insertPs.setString(10, req); //request
                insertPs.setString(11, res); //response
                insertPs.setTimestamp(12, getTimeStamp()); //insertdate
                insertPs.setTimestamp(13, getTimeStamp()); //exec_date
                insertPs.setString(14, result); //result

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
