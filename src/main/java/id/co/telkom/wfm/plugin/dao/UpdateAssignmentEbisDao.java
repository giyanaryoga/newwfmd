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
    public boolean getLabor(String laborcode, ListLabor listLabor) throws SQLException {
        boolean validateLabor = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT l.c_laborid, l.c_laborcode, l.c_status, l.c_supervisor, p.c_displayname "
                + "FROM app_fd_labor l, app_fd_person2 p WHERE l.c_personid = p.c_personid and c_laborcode = ? ";
        // change 04
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, laborcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                listLabor.setLaborid(rs.getString("c_laborid"));
                listLabor.setLaborcode(laborcode);
                listLabor.setStatusLabor(rs.getString("c_status"));
                listLabor.setSupervisor(rs.getString("c_supervisor"));
                listLabor.setLaborname(rs.getString("c_displayname"));
            } else con.rollback();
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return validateLabor;
    }
    
    public boolean updateLaborTemp(String wonum, String laborcodetemp, String craft, String amcrewtype, String amcrew) throws SQLException {
        boolean updateLabor = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_assignment SET ")
            .append("c_craft = ?, ")
            .append("c_amcrewtype = ?, ")
            .append("c_amcrew = ?, ")
            .append("c_laborcodetemp = ?, ")
            .append("c_datemodified = sysdate ")
            .append("WHERE ")
            .append("c_wonum = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, craft);
            ps.setString(2, amcrewtype);
            ps.setString(3, amcrew);
            ps.setString(4, laborcodetemp);
            ps.setString(5, wonum);
            int exe = ps.executeUpdate();
            if (exe > 0) {
                updateLabor = true;
                LogUtil.info(getClass().getName(), wonum + " | Laborcode update temp:  " + laborcodetemp);
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return updateLabor;
    }
    
    public void updateLabor(String laborcode, String laborname, String wonum) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_assignment SET ")
            .append("c_laborcode = ?, ")
            .append("c_displayname = ?, ")
            .append("c_status = ?, ")
            .append("c_laborcodetemp = NULL, ")
            .append("c_datemodified = sysdate ")
            .append("WHERE ")
            .append("c_wonum = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, laborcode);
            ps.setString(2, laborname);
            ps.setString(3, "ASSIGNED");
            ps.setString(4, wonum);
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
    
    public void updateLaborWorkOrder(String laborcode, String laborname, String wonum) throws SQLException {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        StringBuilder update = new StringBuilder();
        update
            .append("UPDATE app_fd_workorder SET ")
            .append("c_laborcode = ?, ")
            .append("c_laborname = ?, ")
            .append("c_datemodified = sysdate ")
            .append("WHERE ")
            .append("c_wonum = ?")
            .append("AND c_woclass = ?");
        try(Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(update.toString())) {
            ps.setString(1, laborcode);
            ps.setString(2, laborname);
            ps.setString(3, wonum);
            ps.setString(4, "ACTIVITY");
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
}
