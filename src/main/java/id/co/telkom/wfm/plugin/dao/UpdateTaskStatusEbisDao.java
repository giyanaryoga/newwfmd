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
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class UpdateTaskStatusEbisDao {

    //===========================
    // Function Update Task
    //===========================
    public boolean updateTask(String wonum, String status) throws SQLException {
        boolean updateTask = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_status = ?, dateModified = sysdate WHERE c_wonum = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, status);
            ps.setString(2, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
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

    //===========================================================================
    // Checking Jika ada task selanjutnya maka ASSIGNTASK jika tidak Set COMPLETE 
    //===========================================================================
    public String nextMove(String parent, String nextTaskId) throws SQLException {
        String nextMove = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_wosequence FROM app_fd_workorder WHERE c_parent = ? AND c_taskid = ? AND c_wosequence IN ('10','20','30','40','50','60') AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        try (Connection con = ds.getConnection();
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

    //=========================================
    // SET LABASSIGN FOR NEXT TASK
    //=========================================
    public boolean nextAssign(String parent, String nextTaskId) throws SQLException {
        boolean nextAssign = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_status = 'LABASSIGN', dateModified = sysdate WHERE c_parent = ? AND c_taskid = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setString(1, parent);
            ps.setString(2, nextTaskId);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "next assign berhasil");
                nextAssign = true;
            } else {
                LogUtil.info(getClass().getName(), "next assign gagal");
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return nextAssign;
    }

    //========================================
    // UPDATE WOSTATUS
    //========================================
    public void updateParentStatus(String wonum, String status, String statusDate) throws SQLException {
        String update = "UPDATE app_fd_workorder SET c_status = ?, c_statusdate = ?, dateModified = sysdate WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            int index = 0;
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

    //===============================
    // GET COMPLETE JSON
    //===============================
    public JSONObject getCompleteJson(String parent) throws SQLException {
        JSONArray itemArrayObj = new JSONArray();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_detailactcode, c_wosequence, c_correlation, c_status, c_wonum FROM app_fd_workorder WHERE c_parent = ? AND c_wosequence IN ('10', '20', '30', '40', '50', '60') AND c_wfmdoctype = 'NEW'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject itemObj = buildTaskAttribute(rs.getString("c_wonum"), rs.getString("c_detailactcode"),
                        rs.getString("c_wosequence"), rs.getString("c_correlation"),
                        rs.getString("c_status"));
                itemArrayObj.add(itemObj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        // add the item array
        JSONObject completeJson = buildTaskJson(parent, itemArrayObj);
        //return complete json
        return completeJson;
    }

    //=============================
    // BUILD TASK JSON FOR COMPLETE
    //=============================
    private JSONObject buildTaskJson(String wonum, Object itemArrayObj) throws SQLException {
        JSONObject milestoneInput = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
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
                milestoneInput.put("item", itemArrayObj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }

        // XML Wrapper Configuration
        JSONObject attrs = new JSONObject();
        attrs.put("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        attrs.put("xmlns:tel", "http://eaiprdis1/telkom/bie/newoss/integration/ws/milestoneWorkForce");
        JSONObject header = new JSONObject();
        header.put("@ns", "soapenv");
        //Wrapper
        JSONObject milestoneWorkForce = new JSONObject();
        milestoneWorkForce.put("MilestoneInput", milestoneInput);
        milestoneWorkForce.put("@ns", "tel");
        //
        JSONObject body = new JSONObject();
        body.put("milestoneWorkForce", milestoneWorkForce);
        body.put("@ns", "soapenv");
        //
        JSONObject envelope = new JSONObject();
        envelope.put("Header", header);
        envelope.put("Body", body);
        envelope.put("@ns", "soapenv");
        envelope.put("@attrs", attrs);
        //
        JSONObject completeJson = new JSONObject();
        completeJson.put("Envelope", envelope);
        //End of wrapper
        return completeJson;
    }

    private JSONObject getListAttribute(String wonum) throws SQLException {
        JSONArray listAttr = new JSONArray();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT C_VALUE, C_ASSETATTRID, C_ISSHARED  FROM APP_FD_WORKORDERSPEC a WHERE a.C_ISSHARED = 1 AND a.C_WONUM = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject attributeObject = new JSONObject();
                attributeObject.put("Name", rs.getString("C_ATTRIBUTE_NAME"));
                attributeObject.put("Value", rs.getString("C_ALNVALUE"));
                listAttr.add(attributeObject);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        JSONObject attributeObj = new JSONObject();
        attributeObj.put("attribute", listAttr);

        return attributeObj;
    }

    private JSONObject buildTaskAttribute(String wonum, String name, String sequence, String correlation, String status) throws SQLException {
        JSONObject itemObj = new JSONObject();
        itemObj.put("Sequence", sequence);
        itemObj.put("Name", name);
        itemObj.put("Correlation", correlation);
        itemObj.put("Status", status);
        
        // Wrapper
        JSONObject attributes = new JSONObject();
        attributes.put("Attributes", getListAttribute(wonum));

        JSONObject serviceDetail = new JSONObject();
        serviceDetail.put("ServiceDetail", attributes);

        itemObj.put("ServiceDetails", serviceDetail);
        return itemObj;
    }

    //===================================
    // UPDATE DATA IF TASK STATUS "FAILWA
    //===================================
    public void updateWorkFail(String wonum, String status, String errorCode, String engineerMemo, String statusDate) throws SQLException {
        String update = "UPDATE app_fd_workorder SET c_status = ?, c_errorcode = ?, c_engineermemo = ?, c_statusdate = ?, dateModified = sysdate WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            int index = 0;
            ps.setString(1 + index, status);
            ps.setString(2 + index, errorCode);
            ps.setString(3 + index, engineerMemo);
            ps.setString(4 + index, statusDate);
            ps.setString(5 + index, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Status updated to: " + status + "| Error code: " + errorCode + "| Engineer Memo: " + engineerMemo);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    private JSONObject buildTaskAttributeWorkFail(String wonum, String name, String sequence, String correlation, String status) throws SQLException {
        JSONObject itemObj = new JSONObject();
        itemObj.put("Sequence", sequence);
        itemObj.put("Name", name);
        itemObj.put("Correlation", correlation);
        itemObj.put("Status", status);
        
//        JSONObject attrError = new JSONObject();
//        attrError.put("ErrorCode", errorCode);
//        attrError.put("EngineerMemo", engineerMemo);
//        
//        itemObj.put("Error", attrError);
        // Wrapper
        JSONObject attributes = new JSONObject();
        attributes.put("Attributes", getListAttribute(wonum));

        JSONObject serviceDetail = new JSONObject();
        serviceDetail.put("ServiceDetail", attributes);

        itemObj.put("ServiceDetails", serviceDetail);
        return itemObj;
    }
    
    //===============================
    // GET FAILWORK JSON 
    //===============================
    public JSONObject getFailWorkJson(String parent) throws SQLException {
        JSONArray itemArrayObj = new JSONArray();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_detailactcode, c_wosequence, c_correlation, c_status, c_wonum, FROM app_fd_workorder WHERE c_parent = ? AND c_wosequence IN ('10', '20', '30', '40', '50', '60') AND c_wfmdoctype = 'NEW' AND C_STATUS NOT LIKE 'APPR'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject itemObj = buildTaskAttributeWorkFail(rs.getString("c_wonum"), rs.getString("c_detailactcode"),
                        rs.getString("c_wosequence"), rs.getString("c_correlation"),
                        rs.getString("c_status"));
                itemArrayObj.add(itemObj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        // add the item array
        JSONObject failWorkJson = buildFailWorkJson(parent, itemArrayObj);
        //return complete json
        return failWorkJson;
    }

    //================================
    // CREATE FORMAT FAILWORK JSON
    //================================
    private JSONObject buildFailWorkJson(String wonum, Object itemArrayObj) throws SQLException {
        JSONObject milestoneInput = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_jmscorrelationid, c_worevisionno, c_status, c_scorderno  FROM app_fd_workorder WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
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
                milestoneInput.put("SCOrderNo", rs.getString("c_scorderno"));
                milestoneInput.put("item", itemArrayObj);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }

        // XML Wrapper Configuration
        JSONObject attrs = new JSONObject();
        attrs.put("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
        attrs.put("xmlns:tel", "http://eaiprdis1/telkom/bie/newoss/integration/ws/milestoneWorkForce");
        JSONObject header = new JSONObject();
        header.put("@ns", "soapenv");
        //Wrapper
        JSONObject milestoneWorkForce = new JSONObject();
        milestoneWorkForce.put("MilestoneInput", milestoneInput);
        milestoneWorkForce.put("@ns", "tel");
        //
        JSONObject body = new JSONObject();
        body.put("milestoneWorkForce", milestoneWorkForce);
        body.put("@ns", "soapenv");
        //
        JSONObject envelope = new JSONObject();
        envelope.put("Header", header);
        envelope.put("Body", body);
        envelope.put("@ns", "soapenv");
        envelope.put("attrs", attrs);
        //
        JSONObject completeJson = new JSONObject();
        completeJson.put("Envelope", envelope);
        //End of wrapper
        return completeJson;
    }

    //====================================
    // INSERT TO TABLE APP_FD_WFMMILESTONE
    //====================================
    public void insertToWfmMilestone(String wonum, String siteId, String statusDate) {
        // Generate UUID
        String uuId = UuidGenerator.getInstance().getUuid();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder insert = new StringBuilder();
        insert
                .append(" INSERT INTO app_fd_wfmmilestone ")
                .append(" ( ")
                .append(" c_wfmmilestoneid, ")
                .append(" id, ")
                .append(" dateCreated, ")
                .append(" dateModified, ")
                .append(" c_scorderno, ")
                .append(" c_wonum, ")
                .append(" c_siteid, ")
                .append(" c_wostatus, ")
                .append(" c_milestonedate ")
                .append(" ) ")
                .append(" VALUES ")
                .append(" ( ")
                .append(" WFMDBDEV01.WFMMILESTONEIDSEQ.NEXTVAL, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ( ")
                .append(" SELECT ")
                .append(" c_scorderno ")
                .append(" FROM app_fd_workorder WHERE ")
                .append(" c_wonum = ? ")
                .append(" AND ")
                .append(" c_woclass = 'WORKORDER' ")
                .append(" ), ")
                .append(" ?, ")
                .append(" ?, ")
                .append(" ( ")
                .append(" SELECT ")
                .append(" c_status ")
                .append(" FROM app_fd_workorder WHERE ")
                .append(" c_wonum = ? ")
                .append(" AND ")
                .append(" c_woclass = 'WORKORDER' ")
                .append(" ), ")
                .append(" ? ")
                .append(" ) ");
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert.toString());
                try {
                    ps.setString(1, uuId);
                    ps.setTimestamp(2, getTimeStamp());
                    ps.setTimestamp(3, getTimeStamp());
                    ps.setString(4, wonum);
                    ps.setString(5, wonum);
                    ps.setString(6, siteId);
                    ps.setString(7, wonum);
                    ps.setString(8, statusDate);

                    int exe = ps.executeUpdate();
                    if (exe > 0) {
                        LogUtil.info(getClass().getName(), wonum + " inserted to WFM milestone log table successfully ");
                    }
                    if (ps != null) {
                        ps.close();
                    }
                } catch (Throwable throwable) {
                    try {
                        if (ps != null) {
                            ps.close();
                        }
                    } catch (Throwable throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null) {
                    con.close();
                }
            } catch (Throwable throwable) {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (Throwable throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }

    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        Timestamp ts = Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
}
