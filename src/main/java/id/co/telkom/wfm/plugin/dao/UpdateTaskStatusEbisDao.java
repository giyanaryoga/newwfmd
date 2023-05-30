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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class UpdateTaskStatusEbisDao {
    
    public boolean updateTask(String wonum, String status) throws SQLException{
        boolean updateTask = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//        String update = "UPDATE app_fd_woactivity SET c_status = ? WHERE c_wonum = ? AND c_wfmdoctype = 'NEW'";
        String update = "UPDATE app_fd_workorder SET c_status = ?, dateModified = sysdate WHERE c_wonum = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        try(Connection con = ds.getConnection(); 
            PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, status);
            ps.setString(2, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0){
                LogUtil.info(getClass().getName(), "update task berhasil");
                updateTask = true;
            } else {
                LogUtil.info(getClass().getName(), "update task gagal");
            }
        } catch (Exception e) {
          LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
          ds.getConnection().close();
        }
        return updateTask;
    }
    
    public String nextMove(String parent, String nextTaskId) throws SQLException {
        String nextMove = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT c_wosequence FROM APP_FD_WOACTIVITY WHERE c_parent = ? AND c_taskId = ? AND c_wfmdoctype = 'NEW' AND C_WOSEQUENCE IN ('10','20','30','40','50','60')";
        String query = "SELECT c_wosequence FROM app_fd_workorder WHERE c_parent = ? AND c_taskid = ? AND c_wosequence IN ('10','20','30','40','50','60') AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        try(Connection con = ds.getConnection(); 
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ps.setString(2, nextTaskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final String result = rs.getString("c_wosequence");
                if (result.equals("10") || result.equals("20") || result.equals("30") || result.equals("40") || result.equals("50") || result.equals("60")) {
                    nextMove = "ASSIGNTASK";
                } else {
                    nextMove = "COMPLETE";
                }
                LogUtil.info(getClass().getName(), "next move: " + nextMove);
            } else {
                nextMove = "COMPLETE";
                LogUtil.info(getClass().getName(), "next move: " + nextMove);
            }
        } catch (SQLException e) {
          LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
          ds.getConnection().close();
        }
        return nextMove;
    }
    
    public boolean nextAssign(String parent, String nextTaskId) throws SQLException{
        boolean nextAssign = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//        String update = "UPDATE app_fd_woactivity SET c_status = 'LABASSIGN' WHERE c_parent = ? AND c_taskid = ? AND c_wfmdoctype = 'NEW'";
        String update = "UPDATE app_fd_workorder SET c_status = 'LABASSIGN', dateModified = sysdate WHERE c_parent = ? AND c_taskid = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        try(Connection con = ds.getConnection(); 
            PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, parent);
            ps.setString(2, nextTaskId);
            int exe = ps.executeUpdate();
            if (exe > 0){
                LogUtil.info(getClass().getName(), "next assign berhasil");
                nextAssign = true;
            } else {
              LogUtil.info(getClass().getName(), "next assign gagal");
            }
        } catch(Exception e){
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
          ds.getConnection().close();
        }
        return nextAssign;
    }
    
    public void updateParentStatus(String wonum, String status, String statusDate, String jmsCorr) throws SQLException {
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE ")
            .append("app_fd_workorder ")
            .append("SET ");
        if (!jmsCorr.equals("")) {
            update.append("c_jmscorrelationid = ?, ");
        }
        update
            .append("c_status = ?, ")
            .append("c_statusdate = ?, ")
            .append("dateModified = sysdate ")
            .append("WHERE ")
            .append("c_wonum = ?");
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            int index = 0;
            if(!jmsCorr.equals("")){
                index++;
                ps.setString(index, jmsCorr);
            }
            ps.setString(1 + index, status);
            ps.setString(2 + index, statusDate);
            ps.setString(3 + index, wonum);
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
    
    public JSONObject getCompleteJson (String parent) throws SQLException {
        JSONArray itemArray = new JSONArray();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_detailactcode, c_wosequence, c_correlation, c_status, c_wonum FROM app_fd_workorder WHERE c_parent = ? AND c_wosequence IN ('10', '20', '30', '40', '50', '60') AND c_wfmdoctype = 'NEW'";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject itemObj = buildTaskAttribute (rs.getString("c_wonum"), rs.getString("c_detailactcode"), 
                        rs.getString("c_wosequence"), rs.getString("c_correlation"), 
                        rs.getString("c_status"));
                itemArray.add(itemObj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        // add the item array
        JSONObject completeJson = buildTaskJson (parent, itemArray);
        //return complete json
        return completeJson;
    }
    
    private JSONObject buildTaskJson (String wonum, Object itemArrayObj) throws SQLException {
        JSONObject milestoneInput = new JSONObject();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_jmscorrelationid, c_worevisionno, c_status  FROM app_fd_workorder WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
               //Milestone input
                milestoneInput.put("JMSCorrelationID", rs.getString("c_jmscorrelationid"));
                milestoneInput.put("WFMWOId", wonum);
                milestoneInput.put("WoRevisionNo", rs.getString("c_worevisionno"));
                milestoneInput.put("WOStatus", rs.getString("c_status"));
                if (itemArrayObj instanceof JSONObject) {
                    milestoneInput.put("Item", itemArrayObj);
                }
                if (itemArrayObj instanceof JSONArray) {
                    for (int i = 0; i < ((JSONArray) itemArrayObj).size(); i++) {
                        JSONObject item = (JSONObject)((JSONArray) itemArrayObj).get(i);
                        item.put("Item", itemArrayObj);
                    }
                }
                milestoneInput.put("item", itemArrayObj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        
       // Wrapper
        JSONObject milestoneWorkForce = new JSONObject();
        milestoneWorkForce.put("MilestoneInput", milestoneInput);
        
        JSONObject body = new JSONObject();
        body.put("milestoneWorkForce", milestoneWorkForce);
        
        JSONObject envelope = new JSONObject();
        envelope.put("Header", "");
        envelope.put("Body", body);
        
        JSONObject completeJson = new JSONObject();
        completeJson.put("Envelope", envelope);
        
        return completeJson;
    }
    private JSONObject getListAttribute (String wonum) throws SQLException {
        JSONObject attributeObject = new JSONObject();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT C_ALNVALUE, C_ATTRIBUTE_NAME FROM APP_FD_WORKORDERSPEC a, APP_FD_WORKORDER b WHERE a.C_WONUM = b.C_WONUM AND a.C_ISSHARED = 1 AND b.C_WOCLASS = 'ACTIVITY' AND b.C_WONUM = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, wonum);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    attributeObject.put("Name", rs.getString("C_ATTRIBUTE_NAME"));
                    attributeObject.put("Value", rs.getString("C_ALNVALUE"));
                }
            } catch (SQLException e) {
                LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
            } finally {
                ds.getConnection().close();
            }
        return attributeObject;
    }
    
    private JSONObject buildTaskAttribute (String wonum, String name, String sequence, String correlation, String status) throws SQLException{
        JSONObject itemObj = new JSONObject();
//        JSONObject attrArray = new JSONObject();
        itemObj.put("Sequence", sequence);
        itemObj.put("Name", name);
        itemObj.put("Correlation", correlation);
        itemObj.put("Status", status);
        
        
        // Wrapper
        JSONArray attribute = new JSONArray();
        for (int i = 0; i < attribute.size(); i++) {
            JSONObject attrObj = (JSONObject)((JSONArray) attribute).get(i);
            attrObj.put("Attribute", getListAttribute(wonum));
        }
        
//        JSONObject attribute = new JSONObject();
//        attribute.put("Attribute", getListAttribute(wonum));
        
        JSONObject attributes = new JSONObject();
        attributes.put("Attributes", attribute);
        
        JSONObject serviceDetail = new JSONObject();
        serviceDetail.put("ServiceDetail", attributes);
        
        itemObj.put("ServiceDetails", serviceDetail);
        return itemObj;
    }
    
    
    
}
