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
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author ASUS
 */
public class IntegrationFalloutDao {

    public String apiId = "";
    public String apiKey = "";

    public void getApiAttribute() {
        String query = "SELECT c_api_id, c_api_key FROM app_fd_api_wfm WHERE c_use_of_api = 'falloutincident'";

        try (Connection connection = ((DataSource) AppUtil.getApplicationContext().getBean("setupDataSource")).getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery()) {

            if (resultSet.next()) {
                this.apiId = resultSet.getString("c_api_id");
                this.apiKey = resultSet.getString("c_api_key");
            }

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }

    public boolean updateStatus(String statusCode, String ticketId) throws SQLException {
        boolean updateStatus = false;

        DataSource dataSource = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String updateQuery = "UPDATE APP_FD_INCIDENT SET C_TK_STATUSCODE = ?, datemodified = sysdate WHERE C_TICKETID = ?";

        try (Connection connection = dataSource.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, statusCode);
            preparedStatement.setString(2, ticketId);

            int executionResult = preparedStatement.executeUpdate();
            if (executionResult > 0) {
                LogUtil.info(getClass().getName(), "update status berhasil");
                updateStatus = true;
            } else {
                LogUtil.info(getClass().getName(), "update status gagal");
            }

        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }

        return updateStatus;
    }

}
