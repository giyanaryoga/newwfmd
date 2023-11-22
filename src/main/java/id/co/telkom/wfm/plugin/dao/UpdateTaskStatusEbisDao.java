/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.*;
import org.json.JSONException;
import org.json.simple.*;

/**
 *
 * @author ASUS
 */
public class UpdateTaskStatusEbisDao {
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        Timestamp ts = Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }
    
    public boolean getApiAttribute(String apiId, String apiKey) {
        boolean  isAuthSuccess = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_api_id, c_api_key FROM app_fd_api_wfm WHERE c_use_of_api = 'mystaff_integration'";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (apiId.equals(rs.getString("c_api_id")) && apiKey.equals(rs.getString("c_api_key"))) {
                    isAuthSuccess = true;
                } else {
                    isAuthSuccess = false;
                }
            }
        } catch(SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return isAuthSuccess;
    }
    
    public JSONObject getTask(String wonum) throws SQLException {
        JSONObject activityProp = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_taskid, c_wosequence, c_detailactcode, c_description, c_parent  FROM app_fd_workorder WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activityProp.put("taskid", rs.getInt("c_taskid"));
                activityProp.put("wosequence", rs.getString("c_wosequence"));
                activityProp.put("detailactcode", rs.getString("c_detailactcode"));
                activityProp.put("description", rs.getString("c_description"));
                activityProp.put("parent", rs.getString("c_parent"));
            } else {
                activityProp = null;
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return activityProp;
    }
    
    public JSONObject getTaskAttr(String wonum) throws SQLException {
        JSONObject activityProp = new JSONObject();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value, c_mandatory, c_isshared FROM app_fd_workorderspec WHERE c_wonum = ? AND c_mandatory = 1";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                activityProp.put("attrName", rs.getInt("c_assetattrid"));
                activityProp.put("attrValue", rs.getString("c_value"));
                activityProp.put("mandatory", rs.getString("c_isrequired"));
                activityProp.put("shared", rs.getString("c_isshared"));
//                activityProp.put("parent", rs.getString("c_parent"));
            } else {
                activityProp = null;
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return activityProp;
    }
    
    public JSONArray isRequired(String wonum) throws SQLException {
        JSONArray valueArray = new JSONArray();
        int value = 0;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value, c_alnvalue, c_mandatory FROM app_fd_workorderspec WHERE c_wonum = ? "
                + "AND c_mandatory = 1"
                + "AND c_value is NULL";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject valueObj = new JSONObject();
                String value2 = rs.getString("c_value");
                if (value2 != null) {
                    value = 0;
                } else {
                    value = 1;
                }
                valueObj.put("value", value);
                valueArray.add(valueObj);
                LogUtil.info(getClass().getName(), rs.getString("c_assetattrid") + " = " + rs.getString("c_value") + " = " + rs.getString("c_mandatory") + " = " + value);
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return valueArray;
    }
    
    public JSONArray checkMandatory(String wonum) throws SQLException {
        JSONArray valueArray = new JSONArray();
        int value = 0;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_assetattrid, c_value, c_alnvalue, c_mandatory FROM app_fd_workorderspec WHERE c_wonum = ? "
                + "AND c_mandatory = 1"
                + "AND c_value = 'None'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject valueObj = new JSONObject();
                String value2 = rs.getString("c_value");
                if (value2 != null) {
                    value = 0;
                } else {
                    value = 1;
                }
                valueObj.put("value", value);
                valueArray.add(valueObj);
                LogUtil.info(getClass().getName(), rs.getString("c_assetattrid") + " = " + rs.getString("c_value") + " = " + rs.getString("c_mandatory") + " = " + value);
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return valueArray;
    }
    
    public String getProductName(String wonum) throws SQLException {
        String productName = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT parent.c_productname FROM app_fd_workorder parent, app_fd_workorder child WHERE parent.c_wonum = child.c_parent AND child.c_woclass = 'ACTIVITY' AND child.c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                productName = rs.getString("parent.c_productname");
                LogUtil.info(getClass().getName(), "Product Name = " + productName);
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return productName;
    }
    
    public String getTaskAttrValue(String wonum, String attrName) throws SQLException {
        String taskAttrValue = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(2, attrName);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                taskAttrValue = rs.getString("c_value");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return taskAttrValue;
    }

    public String checkAssignment(String wonum) throws SQLException {
        String assign = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_laborcode, c_status FROM app_fd_assignment WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String assignment = rs.getString("c_status");
                if (assignment.equalsIgnoreCase("ASSIGNED")) {
                    assign = "Assign";
                    LogUtil.info(getClass().getName(), "Task sudah assign ke labor");
                } else {
                    assign = "Not Assign";
                    LogUtil.info(getClass().getName(), "Task belum assign ke labor");
                }
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return assign;
    }
    
    public String checkActPlace(String wonum) throws SQLException {
        String actPlace = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_actplace FROM app_fd_workorder WHERE c_wonum = ?";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                actPlace = rs.getString("c_actplace");
                LogUtil.info(getClass().getName(), "Act Place " + actPlace);
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return actPlace;
    }

    public int checkAttachedFile(String wonum, String documentName) throws SQLException {
        int isAttachedFile = 0;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectQuery = "SELECT DISTINCT c_documentname FROM app_fd_doclinks WHERE c_wonum = ? AND c_documentname = ?";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectQuery)) {

            ps.setString(1, wonum);
            ps.setString(2, documentName);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString("c_documentname") != null) {
                    isAttachedFile = 1;
                    LogUtil.info(getClass().getName(), "isAttachedFile = " + isAttachedFile);
                } else {
                    isAttachedFile = 0;
                    LogUtil.info(getClass().getName(), "isAttachedFile = " + isAttachedFile);
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return isAttachedFile;
    }

    public int isProductNameDigital(String wonum) throws SQLException {
        int isProductName = 0;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        String selectProduct = "SELECT c_productname FROM APP_FD_WORKORDER \n"
                + "WHERE c_productname IN ('VPN IP Netmonk', 'Nadeefa Netmonk', 'Pijar Sekolah', 'Omni Communication Assistant') \n"
                + "AND c_wonum = ?";
        
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(selectProduct)) {
            ps.setString(1, wonum);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (rs.getString("c_productname") != null) {
                    LogUtil.info(getClass().getName(), "isProductName = " + isProductName);
                    isProductName = 1;
                } else {
                    isProductName = 0;
                    LogUtil.info(getClass().getName(), "isProductName = " + isProductName);
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        }
        return isProductName;
    }

    public String checkWoDoc(String wonum) throws SQLException, JSONException {
        String value = "";
        int checkDoc = isProductNameDigital(wonum);
        LogUtil.info(this.getClass().getName(), "Is Product: " + checkDoc);
        
        if (checkAttachedFile(wonum, "BAA") == 0 && isProductNameDigital(wonum) == 1) {
            value = "File BAA belum diupload. Attach/upload file BAA sebelum update status Complete Work Activity (COMPWA). \\nPastikan dokumen telah diupload dengan nama dokumen 'BAA'";

        } else if (isProductNameDigital(wonum) == 0 && checkAttachedFile(wonum, "BAST") == 0
                && checkAttachedFile(wonum, "BAPLA") == 0
                && checkAttachedFile(wonum, "BAPL") == 0
                && checkAttachedFile(wonum, "WO") == 0
                && checkAttachedFile(wonum, "SPK") == 0
                && checkAttachedFile(wonum, "KL") == 0) {
            value = "File BAST/BAPL/BAPLA dan KL/WO/SPK belum diupload. Attach/upload file BAST/BAPL/BAPLA dan KL/WO/SPK sebelum update status Complete Work Activity (COMPWA). \\nPastikan minimal 2 dokumen telah diupload dengan nama dokumen 'BAST'/ 'BAPL' / 'BAPLA' dan 'KL'/ 'WO' / 'SPK'";

        } else if (isProductNameDigital(wonum) == 0
                && (checkAttachedFile(wonum, "BAST") == 1
                || checkAttachedFile(wonum, "BAPLA") == 1
                || checkAttachedFile(wonum, "BAPL") == 1)
                && checkAttachedFile(wonum, "WO") == 0
                && checkAttachedFile(wonum, "SPK") == 0
                && checkAttachedFile(wonum, "KL") == 0) {
            value = "File KL/WO/SPK belum diupload. Attach/upload file BAST/BAPL/BAPLA dan KL/WO/SPK sebelum update status Complete Work Activity (COMPWA). \\nPastikan minimal 2 dokumen telah diupload dengan nama dokumen 'BAST'/ 'BAPL' / 'BAPLA' dan 'KL'/ 'WO' / 'SPK'";

        } else if (isProductNameDigital(wonum) == 0
                && checkAttachedFile(wonum, "BAPLA") == 0
                && checkAttachedFile(wonum, "BAPL") == 0
                && (checkAttachedFile(wonum, "WO") == 1
                || checkAttachedFile(wonum, "SPK") == 1
                || checkAttachedFile(wonum, "KL") == 1)) {
            value = "File BAST/BAPL/BAPLA belum diupload. Attach/upload file BAST/BAPL/BAPLA dan KL/WO/SPK sebelum update status Complete Work Activity (COMPWA). \\nPastikan minimal 2 dokumen telah diupload dengan nama dokumen 'BAST'/ 'BAPL' / 'BAPLA' dan 'KL'/ 'WO' / 'SPK'";
        } else {
            value = "Nama File sudah benar, Update status COMPWA berhasil";
        }

        return value;
    }

    //===========================
    // Function Update Task
    //===========================
    public String updateTask(String wonum, String status, String modifiedBy, String modifiedByName) {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_status = ?, modifiedby = ?, modifiedbyname = ?, dateModified = sysdate WHERE c_wonum = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";

        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {

            ps.setString(1, status);
            ps.setString(2, modifiedBy);
            ps.setString(3, modifiedByName);
            ps.setString(4, wonum);
            int exe = ps.executeUpdate();

            if (exe > 0) {
                return "Update task status berhasil";
            } else {
                return "Update task gagal";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    //===========================================================================
    // Checking Jika ada task selanjutnya maka ASSIGNTASK jika tidak Set COMPLETE 
    //===========================================================================
    public String nextMove(String parent, int nextTaskId) throws SQLException {
        String nextMove = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_wosequence FROM app_fd_workorder WHERE c_parent = ? AND c_taskid = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, parent);
            ps.setInt(2, nextTaskId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                nextMove = "ASSIGNTASK";
                LogUtil.info(getClass().getName(), "next move: " + nextMove);
            } else {
                nextMove = "COMPLETE";
                LogUtil.info(getClass().getName(), "next move: " + nextMove);
            }
            LogUtil.info(getClass().getName(), "query: " + query);
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
    public boolean nextAssign(String parent, int nextTaskId, String modifiedBy, String modifiedByName) throws SQLException {
        boolean nextAssign = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_workorder SET c_status = 'LABASSIGN', dateModified = ?, modifiedby = ?, modifiedbyname = ? WHERE c_parent = ? AND c_taskid = ? AND c_wfmdoctype = 'NEW' AND c_woclass = 'ACTIVITY'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update)) {
            ps.setTimestamp(1, getTimeStamp());
            ps.setString(2, modifiedBy);
            ps.setString(3, modifiedByName);
            ps.setString(4, parent);
            ps.setInt(5, nextTaskId);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "next assign berhasil");
                nextAssign = true;
                updateWoDesc(parent, nextTaskId, modifiedBy, modifiedByName);
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

    public void updateWoDesc(String parent, int nextTaskId, String modifiedBy, String modifiedByName) throws SQLException {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_description, c_ownergroup, c_schedstart, c_schedfinish, c_estdur FROM app_fd_workorder WHERE c_parent = ? AND c_taskid = ? AND c_status = 'LABASSIGN' AND c_wfmdoctype = 'NEW'";
        String update = "UPDATE app_fd_workorder SET modifiedby = ?, c_description = ?, c_ownergroup = ?, c_schedstart = ?, c_schedfinish = ?, c_estdur = ?, modifiedbyname = ?, dateModified = sysdate WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        try (Connection con = ds.getConnection();
                PreparedStatement ps1 = con.prepareStatement(query);
                PreparedStatement ps2 = con.prepareStatement(update)) {
            ps1.setString(1, parent);
            ps1.setInt(2, nextTaskId);
            ResultSet rs = ps1.executeQuery();
            if (rs.next()) {
                ps2.setString(1, modifiedBy);
                ps2.setString(2, rs.getString("c_description"));
                ps2.setString(3, rs.getString("c_ownergroup"));
                ps2.setTimestamp(4, Timestamp.valueOf(rs.getString("c_schedstart")));
                ps2.setTimestamp(5, Timestamp.valueOf(rs.getString("c_schedfinish")));
                ps2.setFloat(6, rs.getFloat("c_estdur"));
                ps2.setString(7, modifiedByName);
                ps2.setString(8, parent);
                int exe = ps2.executeUpdate();
                if (exe > 0) {
                    LogUtil.info(getClass().getName(), "description parent and ownergroup is updated");
                } else {
                    LogUtil.info(getClass().getName(), "description parent and ownergroup is not updated");
                }
            } else {
                con.commit();
            }
        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }

    //========================================
    // UPDATE WOSTATUS
    //========================================
    public void updateParentStatus(String wonum, String status, String statusDate, String modifiedBy, String modifiedByName) throws SQLException {
        String update = "UPDATE app_fd_workorder SET modifiedby = ?, c_status = ?, c_statusdate = ?, dateModified = ?, modifiedbyname = ? WHERE c_wonum = ? AND c_woclass = 'WORKORDER'";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            int index = 0;
            ps.setString(1 + index, modifiedBy);
            ps.setString(2 + index, status);
            ps.setTimestamp(3 + index, getTimeStamp());
            ps.setTimestamp(4 + index, getTimeStamp());
            ps.setString(5 + index, modifiedByName);
            ps.setString(6 + index, wonum);
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
        String query = "SELECT c_detailactcode, c_wosequence, c_correlation, c_status, c_wonum FROM app_fd_workorder WHERE c_parent = ? AND c_wfmdoctype = 'NEW'";
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
                milestoneInput.put("WoRevisionNo", rs.getString("c_worevisionno") == null ? "" : rs.getString("c_worevisionno"));
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

    private JSONArray getListAttribute(String wonum) throws SQLException {
        JSONArray listAttr = new JSONArray();
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT C_VALUE, C_ASSETATTRID, C_ISSHARED  FROM APP_FD_WORKORDERSPEC a WHERE a.C_ISSHARED = 1 AND a.C_WONUM = ?";
        String query = "SELECT C_WONUM, C_VALUE, C_DESCRIPTION, C_ISSHARED FROM APP_FD_WORKORDERSPEC a WHERE a.C_ISSHARED = 1 AND (a.C_WONUM = ? OR (a.C_WONUM = ? AND EXISTS (SELECT 1 FROM app_fd_workorder WHERE c_wonum = ? AND c_description = 'Survey On Desk')))";
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {
            String newwonum = wonum;
            String a = newwonum.substring(0, newwonum.length() - 1);
            int b = Integer.parseInt(newwonum.substring(newwonum.length() - 1)) - 1;
            String result = a + b;
            LogUtil.info(getClass().getName(), " Wonum : " + a + b);

            ps.setString(1, wonum);
            ps.setString(2, result);
            ps.setString(3, result);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                JSONObject attributeObject = new JSONObject();
                attributeObject.put("Name", rs.getString("C_DESCRIPTION"));
                attributeObject.put("Value", rs.getString("C_VALUE"));
                listAttr.add(attributeObject);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return listAttr;
    }

    private JSONObject buildTaskAttribute(String wonum, String name, String sequence, String correlation, String status) throws SQLException {
        JSONObject itemObj = new JSONObject();
        itemObj.put("Sequence", sequence);
        itemObj.put("Name", name);
        itemObj.put("Correlation", correlation);
        itemObj.put("Status", status);

        // checking attribute
        JSONObject attributeObj = new JSONObject();
        if (getListAttribute(wonum).isEmpty()) {
            attributeObj.put("Attribute", "");
        } else {
            attributeObj.put("Attribute", getListAttribute(wonum));
        }
        // Checking attribute
//        if (name.equals("Survey-Ondesk")) {
//            attributeObj.put("Attribute", "");
//        }

        JSONObject attributes = new JSONObject();
        attributes.put("Attributes", attributeObj);

        JSONObject serviceDetail = new JSONObject();
//        serviceDetail.put("ServiceDetail", attributeObj);
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
        String query = "SELECT c_detailactcode, c_wosequence, c_correlation, c_status, c_wonum, FROM app_fd_workorder WHERE c_parent = ? AND c_wfmdoctype = 'NEW' AND C_STATUS NOT LIKE 'APPR'";
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
//        TimeUtil time = new TimeUtil();

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
                .append(" WFMMILESTONEIDSEQ.NEXTVAL, ")
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
//                    ps.setString(8, statusDate);
                    ps.setTimestamp(8, Timestamp.valueOf(statusDate));

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
    
    public void updateActualStart(String parent, String actstart) throws SQLException {
        String update = "UPDATE app_fd_workorder SET c_actstart = ?, dateModified = sysdate WHERE c_wonum = ?";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(actstart));
            ps.setString(2, parent);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "actStart updated");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateActualFinish(String parent, String actfinish) throws SQLException {
        String update = "UPDATE app_fd_workorder SET c_actfinish = ?, dateModified = sysdate WHERE c_wonum = ?";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(actfinish));
            ps.setString(2, parent);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "actFinish updated");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateActualStartTask(String parent, String actstart) throws SQLException {
        String update = "UPDATE app_fd_workorder SET c_actstart = ?, dateModified = sysdate WHERE c_parent = ?";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(actstart));
            ps.setString(2, parent);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "actStart updated");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateActualFinishTask(String parent, String actfinish) throws SQLException {
        String update = "UPDATE app_fd_workorder SET c_actfinish = ?, dateModified = sysdate WHERE c_parent = ?";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        try (Connection con = ds.getConnection();
                PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setTimestamp(1, Timestamp.valueOf(actfinish));
            ps.setString(2, parent);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "actFinish updated");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
}
