/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.ListLabor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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
        // change 04
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                String statusParent = rs.getString("c_status");
                if (statusParent.equals("LABASSIGN")) {
                    status = true;
                    LogUtil.info(getClass().getName(), "Status laborname = " +status);
                } else {
                    status = false;
                    LogUtil.info(getClass().getName(), "Status laborname = " +status);
                }
            } 
//            else con.rollback();
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
        String query = "SELECT l.c_laborcode, p.c_displayname "
                + "FROM app_fd_labor l, app_fd_person2 p WHERE l.c_personid = p.c_personid and c_laborcode = ? ";
        // change 04
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, laborcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                laborname = rs.getString("c_displayname");
                LogUtil.info(getClass().getName(), "laborname = " +laborname);
            } 
//            else con.rollback();
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
        String query = "SELECT l.c_laborcode, l.c_personid, p.c_displayname "
                + "FROM app_fd_labor l, app_fd_person2 p WHERE l.c_personid = p.c_personid and c_laborcode = ? ";
        // change 04
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, laborcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                validateLabor = true;
            } 
//            else con.rollback();
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
        String query = "SELECT a.c_amcrew, a.c_amcrewtype, a.c_description, a.c_orgid, a.c_status "
                + "FROM app_fd_amcrew a WHERE c_amcrew = ? ";
        // change 04
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, amcrew);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                validateCrew = true;
            } 
//            else con.rollback();
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return validateCrew;
    }
    
    public void updateLabor(String laborcode, String laborname, String wonum) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_assignment SET ")
            .append("c_laborcode = ?, ")
            .append("c_displayname = ?, ")
            .append("c_status = ?, ")
            .append("datemodified = ? ")
            .append("WHERE ")
            .append("c_wonum = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, laborcode);
            ps.setString(2, laborname);
            ps.setString(3, "ASSIGNED");
            ps.setTimestamp(4, getTimeStamp());
            ps.setString(5, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Laborcode update :  " + laborcode);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateCrew(String amcrew, String wonum) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_assignment SET ")
            .append("c_amcrew = ?, ")
            .append("c_status = ?, ")
            .append("datemodified = ? ")
            .append("WHERE ")
            .append("c_wonum = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, amcrew);
            ps.setString(2, "ASSIGNED");
            ps.setTimestamp(3, getTimeStamp());
            ps.setString(4, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Crew update :  " + amcrew);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateLaborWorkOrder(String laborcode, String laborname, String wonum) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_workorder SET ")
            .append("c_chief_code = ?, ")
            .append("c_chief_name = ?, ")
            .append("c_assignment_type = ?, ")
            .append("c_assignment_status = ?, ")
            .append("datemodified = ? ")
            .append("WHERE ")
            .append("c_wonum = ?")
            .append("AND c_woclass = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, laborcode);
            ps.setString(2, laborname);
            ps.setString(3, "MANJA");
            ps.setString(4, "ASSIGNED");
            ps.setTimestamp(5, getTimeStamp());
            ps.setString(6, wonum);
            ps.setString(7, "ACTIVITY");
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Updated Workorder Laborcode :  " + laborcode);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
    
    public void updateCrewWorkOrder(String laborcode, String wonum) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_workorder SET ")
            .append("c_amcrew = ?, ")
            .append("c_assignment_type = ?, ")
            .append("c_assignment_status = ?, ")
            .append("datemodified = ? ")
            .append("WHERE ")
            .append("c_wonum = ?")
            .append("AND c_woclass = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, laborcode);
            ps.setString(2, "MANJA");
            ps.setString(3, "ASSIGNED");
            ps.setTimestamp(4, getTimeStamp());
            ps.setString(5, wonum);
            ps.setString(6, "ACTIVITY");
            int exe = ps.executeUpdate();
            if (exe > 0) {
                LogUtil.info(getClass().getName(), wonum + " | Updated Workorder Amcrew :  " + laborcode);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
    }
}
