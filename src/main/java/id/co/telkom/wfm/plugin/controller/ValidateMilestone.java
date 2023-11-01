/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.kafka.ResponseKafka;
import java.sql.*;
import java.util.logging.*;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class ValidateMilestone {

    UpdateTaskStatusEbisDao formatJson = new UpdateTaskStatusEbisDao();
    ResponseKafka responseKafka = new ResponseKafka();

    private String getStatus(String parent) throws SQLException {
        String status = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_status FROM app_fd_workorder WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                status = rs.getString("c_status");
                LogUtil.info(getClass().getName(), "Status WO " + status);
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return status;
    }

    public boolean triggerMilestone(String parent, String siteId) throws SQLException {
        boolean result = false;
        try {
            //Build Response
            JSONObject data = formatJson.getCompleteJson(parent);
            String status = getStatus(parent);
            if (status.equals("COMPLETE")) {
                // Response to Kafka
                String kafkaRes = data.toJSONString();
                //KAFKA DEVELOPMENT
                responseKafka.MilestoneEbis(kafkaRes, siteId);
                result = true;
            } else {
                result = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateGenerateTask.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
}
