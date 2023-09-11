package id.co.telkom.wfm.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author User
 */
public class TkMappingOwnerGroupDao {
    public boolean getTaskNonConn(String activity) throws SQLException {
        boolean task = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_activity, c_description FROM APP_FD_DETAILACTIVITY \n" +
                        "WHERE C_ACTIVITY IN \n" +
                        "('REVIEW_ORDER', \n" +
                        "'Activate_Service', \n" +
                        "'Upload_Berita_Acara', \n" +
                        "'Modify_Service', \n" +
                        "'Shipment_Delivery', \n" +
                        "'Suspend_Service', \n" +
                        "'Resume_Service', \n" +
                        "'Deactivate_Service', \n" +
                        "'Approval_Project_Management');";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, activity);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                if (activity == rs.getString("c_activity")) {
                    task = true;
                } else task = false;
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return task;
    }
    
    public String getOwnerGroupParentCCAN(String workzone, String siteid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_siteid = ? AND c_ownergroup like 'CCAN%'";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, siteid);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroupParentSegment(String workzone, String siteid, String segment) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_siteid = ? AND c_tkcustomersegment = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, siteid);
            ps.setString(3, segment);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }

    public String get(String wonum, String attrName) throws SQLException {
        String taskAttrValue = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_value FROM app_fd_workorderspec WHERE c_wonum = ? AND c_assetattrid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, wonum);
            ps.setString(1, attrName);
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
    
    public String getOwnerGroup1(String workzone, String prodName, String segment, String supplier) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_productname = ? AND c_tkcustomersegment = ? AND c_supplier = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, prodName);
            ps.setString(3, segment);
            ps.setString(4, supplier);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroup2(String workzone, String prodName, String segment) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_productname = ? AND c_tkcustomersegment = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, prodName);
            ps.setString(3, segment);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroup3(String workzone, String segment) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroup4(String workzone, String segment) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_supplier is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroup5(String workzone, String segment) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_productname is null AND c_amdivision is null AND c_supplier is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroup6(String amdivision, String segment) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_amdivision = ? AND c_tkcustomersegment = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, amdivision);
            ps.setString(2, segment);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroup7(String workzone, String segment) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ?"
                + "AND c_productname is null AND c_supplier is null AND c_amdivision is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroup8(String workzone, String segment) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ?"
                + "c_supplier is null AND c_amdivision is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroup9(String workzone, String segment, String productname) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_productname = ?"
                + "c_supplier is null AND c_amdivision is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ps.setString(3, productname);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
    
    public String getOwnerGroup10(String workzone, String segment, String productname) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_productname = ?"
                + "c_supplier is null AND c_amdivision is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ps.setString(3, productname);
            ResultSet rs = ps.executeQuery();
            if (rs.next())
                ownerGroup = rs.getString("c_ownergroup");
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return ownerGroup;
    }
}
