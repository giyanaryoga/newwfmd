/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.kafka.ResponseKafka;
import id.co.telkom.wfm.plugin.util.MessageException;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.sql.*;
import java.util.HashMap;
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
    TimeUtil time = new TimeUtil();

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
            String statusDate = time.getCurrentTime();
            boolean checkMilestone = checkPreviousMilestone(parent, siteId, status);
            
            if (status.equals("COMPLETE") && checkMilestone) {
                // Response to Kafka
                String kafkaRes = data.toJSONString();
                formatJson.insertToWfmMilestone(parent, siteId, statusDate);
                //KAFKA DEVELOPMENT
                responseKafka.MilestoneEbis(kafkaRes, siteId);
                result = true;
            } else {
                formatJson.insertToWfmMilestone(parent, siteId, statusDate);
                result = false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ValidateGenerateTask.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessageException ex) {
            Logger.getLogger(ValidateMilestone.class.getName()).log(Level.SEVERE, null, ex);
        }

        return result;
    }
    
    private void checkTimeDifferences(Timestamp logDate) throws MessageException {
        int gapTime = (5 * 60); //5 minutes gap time
        Timestamp currentTime = time.getTimestampWithMillis();
        long milliseconds = currentTime.getTime() - logDate.getTime();
        int seconds = (int) (milliseconds/1000);
        int remainTime = gapTime - seconds;
        if (remainTime > 0) {
            //int hours = remainTime / 3600;
            int minutes = (remainTime % 3600) / 60;
            seconds = (remainTime % 3600) % 60;
            String exc = "Dapat melakukan retry trigger Milestone dalam " + minutes + " menit " + seconds + " detik.";
            throw new MessageException(exc);
        }
    }
    
    private boolean checkPreviousMilestone(String parent, String siteId, String milestoneStatus) throws MessageException {
        boolean isCheck = false;
        HashMap<String, Object> log = getMilestoneLog(parent, siteId);
        String logStatus = (String) log.getOrDefault("milestoneStatus", "");
        Timestamp logDate = (Timestamp) log.get("milestoneDate");
        if (!milestoneStatus.equals(logStatus)) {
            isCheck = false;
            String exc = "Milestone '" + milestoneStatus + "' sebelumnya belum dikirim ke OSM, milestone terakhir: " + logStatus;
            throw new MessageException(exc);
        }
        //Calculate time differences
        if (logDate != null) {
            isCheck = true;
            checkTimeDifferences(logDate);
        }
        return isCheck;
    }
    
    private HashMap<String, Object> getMilestoneLog(String parent, String siteId){
        HashMap<String, Object> milestoneLog = new HashMap<>();
        StringBuilder query = new StringBuilder();
        query
                .append(" SELECT ")
                .append(" c_wostatus, ")
                .append(" c_milestonedate ")
                .append(" FROM app_fd_wfmmilestone ")
                .append(" WHERE ")
                .append(" c_wonum = ? ")
                .append(" AND ")
                .append(" c_siteid = ? ")
                .append(" ORDER BY c_milestonedate DESC");
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query.toString())) {
            ps.setString(1, parent);
            ps.setString(2, siteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String milestoneStatus = rs.getString("c_wostatus") == null ? "" : rs.getString("c_wostatus");
                Timestamp milestoneDate = rs.getTimestamp("c_milestonedate");
                milestoneLog.put("milestoneStatus", milestoneStatus);
                milestoneLog.put("milestoneDate", milestoneDate);
            }
        } catch(SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return milestoneLog;
    }
}
