/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.ListAssignment;
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

/**
 *
 * @author User
 */
public class UpdateAssignmentEbisDao {
    private Timestamp getTimeStamp() {
        ZonedDateTime zdt = ZonedDateTime.now(ZoneId.of("Asia/Jakarta"));
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"); 
        Timestamp ts =  Timestamp.valueOf(zdt.toLocalDateTime().format(format));
        return ts;
    }

    public boolean getStatusTask(String wonum) throws SQLException {
        boolean status = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_status FROM app_fd_workorder WHERE c_wonum = ? AND c_woclass = 'ACTIVITY'";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                String statusParent = rs.getString("c_status");
                if (statusParent != "COMPWA") {
                    status = true;
                    LogUtil.info(getClass().getName(), "Status laborname = " +status);
                } else {
                    status = false;
                    LogUtil.info(getClass().getName(), "Status laborname = " +status);
                }
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return status;
    }
    
    public String getLaborName(String laborcode) throws SQLException {
        String laborname = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_laborcode, c_displayname "
                + "FROM app_fd_labor WHERE c_laborcode = ? ";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, laborcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                laborname = rs.getString("c_displayname");
                LogUtil.info(getClass().getName(), "laborname = " +laborname);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return laborname;
    }
    
    public boolean validateLabor(String laborcode) throws SQLException {
        boolean validateLabor = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_laborcode, c_personid, c_displayname "
                + "FROM app_fd_labor WHERE c_laborcode = ? ";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, laborcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                validateLabor = true;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return validateLabor;
    }
    
    public boolean validateCrew(String amcrew) throws SQLException {
        boolean validateCrew = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_amcrew, c_amcrewtype, a.c_description, a.c_orgid, a.c_status "
                + "FROM app_fd_amcrew WHERE c_amcrew = ? ";
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, amcrew);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                validateCrew = true;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return validateCrew;
    }
    
    public void updateLabor(ListAssignment param) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_assignment SET ")
            .append("c_chief_code = ?, ")
            .append("c_chief_name = ?, ")
            .append("c_status = ?, ")
            .append("datemodified = ?, ")
            .append("modifiedby = ?, ")
            .append("modifiedbyname = ? ")
            .append("WHERE ")
            .append("c_wonum = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, param.getChiefcode());
            ps.setString(2, param.getChiefName());
            ps.setString(3, "ASSIGNED");
            ps.setTimestamp(4, getTimeStamp());
            ps.setString(5, param.getUser());
            ps.setString(6, param.getName());
            ps.setString(7, param.getWonum());
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), param.getWonum() + " | Laborcode update :  " + param.getChiefcode());
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updatePartner(ListAssignment param) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_assignment SET ")
            .append("c_partner_code = ?, ")
            .append("c_partner_name = ?, ")
            .append("c_status = ?, ")
            .append("datemodified = ?, ")
            .append("modifiedby = ?, ")
            .append("modifiedbyname = ? ")
            .append("WHERE ")
            .append("c_wonum = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, param.getPartnerCode());
            ps.setString(2, param.getPartnerName());
            ps.setString(3, "ASSIGNED");
            ps.setTimestamp(4, getTimeStamp());
            ps.setString(5, param.getUser());
            ps.setString(6, param.getName());
            ps.setString(7, param.getWonum());
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), param.getWonum() + " | Laborcode update :  " + param.getPartnerCode());
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateLaborWaitAssign(ListAssignment param) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_assignment SET ")
            .append("c_chief_code = ?, ")
            .append("c_chief_name = ?, ")
            .append("c_partner_code = ?, ")
            .append("c_partner_name = ?, ")
            .append("c_status = ?, ")
            .append("datemodified = ?, ")
            .append("modifiedby = ?, ")
            .append("modifiedbyname = ? ")
            .append("WHERE ")
            .append("c_wonum = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, param.getChiefcode());
            ps.setString(2, param.getChiefName());
            ps.setString(3, param.getPartnerCode());
            ps.setString(4, param.getPartnerName());
            ps.setString(5, "WAITASSIGN");
            ps.setTimestamp(6, getTimeStamp());
            ps.setString(7, param.getUser());
            ps.setString(8, param.getName());
            ps.setString(9, param.getWonum());
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), param.getWonum() + " | Laborcode update :  " + param.getChiefcode());
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateCrew(ListAssignment param) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_assignment SET ")
            .append("c_amcrew = ?, ")
            .append("c_status = ?, ")
            .append("datemodified = ?, ")
            .append("modifiedby = ?, ")
            .append("modifiedbyname = ? ")
            .append("WHERE ")
            .append("c_wonum = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, param.getAmcrew());
            ps.setString(2, "ASSIGNED");
            ps.setTimestamp(3, getTimeStamp());
            ps.setString(4, param.getUser());
            ps.setString(5, param.getName());
            ps.setString(6, param.getWonum());
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), param.getWonum() + " | Crew update :  " + param.getAmcrew());
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateLaborWorkOrder(ListAssignment param) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_workorder SET ")
            .append("c_chief_code = ?, ")
            .append("c_chief_name = ?, ")
            .append("c_partner_code = ?, ")
            .append("c_partner_name = ?, ")
            .append("c_assignment_type = ?, ")
            .append("c_assignment_status = ?, ")
            .append("datemodified = ?, ")
            .append("modifiedby = ?, ")
            .append("modifiedbyname = ? ")
            .append("WHERE ")
            .append("c_wonum = ?")
            .append("AND c_woclass = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, param.getChiefcode());
            ps.setString(2, param.getChiefName());
            ps.setString(3, param.getPartnerCode());
            ps.setString(4, param.getPartnerName());
            ps.setString(5, "NO MANJA");
            ps.setString(6, "ASSIGNED");
            ps.setTimestamp(7, getTimeStamp());
            ps.setString(8, param.getUser());
            ps.setString(9, param.getName());
            ps.setString(10, param.getWonum());
            ps.setString(11, "ACTIVITY");
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), param.getWonum() + " | Updated Workorder Laborcode :  " + param.getChiefcode());
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void deleteLaborWorkOrder(ListAssignment param) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_workorder SET ")
            .append("c_chief_code = ?, ")
            .append("c_chief_name = ?, ")
            .append("c_partner_code = ?, ")
            .append("c_partner_name = ?, ")
            .append("c_assignment_type = ?, ")
            .append("c_assignment_status = ?, ")
            .append("datemodified = ?, ")
            .append("modifiedby = ?, ")
            .append("modifiedbyname = ? ")
            .append("WHERE ")
            .append("c_wonum = ?")
            .append("AND c_woclass = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, param.getChiefcode());
            ps.setString(2, param.getChiefName());
            ps.setString(3, param.getPartnerCode());
            ps.setString(4, param.getPartnerName());
            ps.setString(5, "NO MANJA");
            ps.setString(6, "WAITASSIGN");
            ps.setTimestamp(7, getTimeStamp());
            ps.setString(8, param.getUser());
            ps.setString(9, param.getName());
            ps.setString(10, param.getWonum());
            ps.setString(11, "ACTIVITY");
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), param.getWonum() + " | Updated Workorder Laborcode :  " + param.getChiefcode());
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateCrewWorkOrder(ListAssignment param) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_workorder SET ")
            .append("c_amcrew = ?, ")
            .append("c_assignment_type = ?, ")
            .append("c_assignment_status = ?, ")
            .append("datemodified = ?, ")
            .append("modifiedby = ?, ")
            .append("modifiedbyname = ? ")
            .append("WHERE ")
            .append("c_wonum = ?")
            .append("AND c_woclass = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, param.getAmcrew());
            ps.setString(2, "NO MANJA");
            ps.setString(3, "ASSIGNED");
            ps.setTimestamp(4, getTimeStamp());
            ps.setString(5, param.getUser());
            ps.setString(6, param.getName());
            ps.setString(7, param.getWonum());
            ps.setString(8, "ACTIVITY");
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), param.getWonum() + " | Updated Workorder Amcrew :  " + param.getAmcrew());
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void insertAssignmentLog(ListAssignment param) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String uuId = UuidGenerator.getInstance().getUuid();
        StringBuilder insert = new StringBuilder();
        insert
            .append("INSERT INTO app_fd_assignmentlog ")
            .append("( C_ASSIGNMENTLOGID, ")
            .append(" id, ")
            .append(" c_wonum, ")
            .append(" c_chief_code, ")
            .append(" c_chief_name, ")
            .append(" c_partner_code, ")
            .append(" c_partner_name, ")
            .append(" c_amcrew, ")
            .append(" c_assignment_status, ")
            .append(" c_assignment_type, ")
            .append(" createdby, ")
            .append(" createdbyname, ")
            .append(" datecreated )")
            .append(" VALUES ")
            .append("( ")
            .append(" ASSIGNMENTLOGIDSEQ.NEXTVAL, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" ?, ")
            .append(" sysdate ")
            .append(" )");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(insert.toString())) {
            ps.setString(1, uuId);
            ps.setString(2, param.getWonum());
            ps.setString(3, param.getChiefcode());
            ps.setString(4, param.getChiefName());
            ps.setString(5, param.getPartnerCode());
            ps.setString(6, param.getPartnerName());
            ps.setString(7, param.getAmcrew());
            ps.setString(8, "ASSIGNED");
            ps.setString(9, "NO MANJA");
            ps.setString(10, param.getUser());
            ps.setString(11, param.getName());
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), "Success insert assignment log");
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
}
