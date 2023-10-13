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
    public int isIPTransitNAS(String activity) throws SQLException {
        int isTransitNas = 0;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT act.c_activity, cls.c_classificationid FROM app_fd_classstructure cls, app_fd_detailactivity act "
                + "WHERE cls.c_classstructureid = act.c_classstructureid AND act.c_activity = ?";
        String query2 = "SELECT * FROM app_fd_customconfigdata "
                + "WHERE c_configcode = 'IPTRANSIT' AND c_attrtype = 'CLASSIFICATION FOR NAS' AND c_attrname = 'CLASSIFICATIONID' "
                + "AND c_attrstatus = 'ACTIVE' AND c_attrvalue = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            PreparedStatement ps2 = con.prepareStatement(query2)) {
            ps.setString(1, activity);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String classsification = rs.getString("cls.c_classificationid");
                ps2.setString(1, classsification);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    isTransitNas = 1;
                }
//                    isTransitNas = 0;
            }
//            isTransitNas = 0;
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return isTransitNas;
    }
    
    public int isIPTransitREG(String activity) throws SQLException {
        int isTransitReg = 0;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT act.c_activity, cls.c_classificationid FROM app_fd_classstructure cls, app_fd_detailactivity act "
                + "WHERE cls.c_classstructureid = act.c_classstructureid AND act.c_activity = ?";
        String query2 = "SELECT * FROM app_fd_customconfigdata"
                + "WHERE c_configcode = 'IPTRANSIT' AND c_attrtype = 'CLASSIFICATION FOR REG' AND c_attrname = 'CLASSIFICATIONID' "
                + "AND c_attrstatus = 'ACTIVE' AND c_attrvalue = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query);
            PreparedStatement ps2 = con.prepareStatement(query2)) {
            ps.setString(1, activity);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String classsification = rs.getString("cls.c_classificationid");
                ps2.setString(1, classsification);
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    isTransitReg = 1;
                }
//                    isTransitReg = 0;
            }
//            isTransitReg = 0;
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return isTransitReg;
    }
    
    public String getOwnerGroup1(String workzone, String prodName, String segment, String supplier, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_productname = ? AND c_tkcustomersegment = ? AND c_supplier = ? "
                + "AND c_classstructureid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, prodName);
            ps.setString(3, segment);
            ps.setString(4, supplier);
            ps.setString(5, classstructureid); //classstructureid
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
    
    public String getOwnerGroup2(String workzone, String prodName, String segment, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_productname = ? AND c_tkcustomersegment = ? AND c_classstructureid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, prodName);
            ps.setString(3, segment);
            ps.setString(4, classstructureid); //classstructureid
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
    
    public String getOwnerGroup3(String workzone, String segment, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_classstructureid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ps.setString(3, classstructureid); //classstructureid
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
    
    public String getOwnerGroup4(String workzone, String segment, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_classstructureid = ? AND c_supplier is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ps.setString(3, classstructureid); //classstructureid
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
    
    public String getOwnerGroup5(String workzone, String segment, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_classstructureid = ? AND c_productname is null AND c_amdivision is null AND c_supplier is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ps.setString(3, classstructureid); //classstructureid
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
    
    public String getOwnerGroup6(String amdivision, String segment, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_amdivision = ? AND c_tkcustomersegment = ? AND c_classstructureid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, amdivision);
            ps.setString(2, segment);
            ps.setString(3, classstructureid); //classstructureid
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
    
    public String getOwnerGroup7(String workzone, String segment, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_classstructureid = ?"
                + "AND c_productname is null AND c_supplier is null AND c_amdivision is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ps.setString(3, classstructureid); //classstructureid
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
    
    public String getOwnerGroup8(String workzone, String segment, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_classstructureid = ?"
                + "c_supplier is null AND c_amdivision is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ps.setString(3, classstructureid); //classstructureid
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
    
    public String getOwnerGroup9(String workzone, String segment, String productname, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_productname = ? AND c_classstructureid = ?"
                + "c_supplier is null AND c_amdivision is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ps.setString(3, productname);
            ps.setString(4, classstructureid); //classstructureid
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
    
    public String getOwnerGroup10(String workzone, String segment, String productname, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_tkcustomersegment = ? AND c_productname = ? AND c_classstructureid = ?"
                + "c_supplier is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, segment);
            ps.setString(3, productname);
            ps.setString(4, classstructureid); //classstructureid
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
    
    public String getOwnerGroup11(String workzone, String productname, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_productname = ? AND c_classstructureid = ?"
                + "c_supplier is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, productname);
            ps.setString(3, classstructureid); //classstructureid
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
    
    public String getOwnerGroup12(String workzone, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_classstructureid = ?"
                + "c_supplier is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, classstructureid); //classstructureid
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
    
    public String getOwnerGroupConn1(String workzone, String classstructureid, String segment) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_classstructureid = ? AND c_tkcustomersegment = ? ";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, classstructureid); //classstructureid
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
    
    public String getOwnerGroupConn2(String workzone, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_classstructureid = ? AND c_tkcustomersegment is null";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, classstructureid); //classstructureid
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
    
    public String getOwnerGroupConn3(String workzone, String supplier, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_workzone = ? AND c_supplier = ? AND c_classstructureid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, workzone);
            ps.setString(2, supplier);
            ps.setString(3, classstructureid); //classstructureid
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
    
    public String getOwnerGroupNonCore(String supplier, String productname, String classstructureid) throws SQLException {
        String ownerGroup = "";
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
                + "WHERE c_supplier = ? AND c_productname = ? AND c_classstructureid = ?";
        try (Connection con = ds.getConnection();
            PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, supplier);
            ps.setString(2, productname);
            ps.setString(3, classstructureid); //classstructureid
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

//    public boolean getTaskNonConn1(String activity) throws SQLException {
//        boolean task = false;
//        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT c_activity, c_description FROM APP_FD_DETAILACTIVITY \n" +
//                        "WHERE C_ACTIVITY IN \n" +
//                        "('REVIEW_ORDER', \n" +
//                        "'Activate_Service', \n" +
//                        "'Upload_Berita_Acara', \n" +
//                        "'Modify_Service', \n" +
//                        "'Shipment_Delivery', \n" +
//                        "'Suspend_Service', \n" +
//                        "'Resume_Service', \n" +
//                        "'Deactivate_Service', \n" +
//                        "'Approval_Project_Management');";
//        try (Connection con = ds.getConnection();
//            PreparedStatement ps = con.prepareStatement(query)) {
////            ps.setString(1, activity);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next()) {
//                if (rs.getString("c_activity").equalsIgnoreCase(activity)) {
//                    task = true;
//                }
//                task = false;
//            }
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
//        } finally {
//            ds.getConnection().close();
//        }
//        return task;
//    }
//    
//    public String getOwnerGroupParentCCAN1(String workzone, String siteid) throws SQLException {
//        String ownerGroup = "";
//        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
//                + "WHERE c_workzone = ? AND c_siteid = ? AND c_ownergroup like 'CCAN%'";
//        try (Connection con = ds.getConnection();
//            PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, workzone);
//            ps.setString(2, siteid);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next())
//                ownerGroup = rs.getString("c_ownergroup");
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
//        } finally {
//            ds.getConnection().close();
//        }
//        return ownerGroup;
//    }
//    
//    public String getOwnerGroupParentSegment1(String workzone, String siteid, String segment) throws SQLException {
//        String ownerGroup = "";
//        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
//        String query = "SELECT c_ownergroup, c_classstructureid FROM app_fd_tkmapping "
//                + "WHERE c_workzone = ? AND c_siteid = ? AND c_tkcustomersegment = ?";
//        try (Connection con = ds.getConnection();
//            PreparedStatement ps = con.prepareStatement(query)) {
//            ps.setString(1, workzone);
//            ps.setString(2, siteid);
//            ps.setString(3, segment);
//            ResultSet rs = ps.executeQuery();
//            if (rs.next())
//                ownerGroup = rs.getString("c_ownergroup");
//        } catch (SQLException e) {
//            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
//        } finally {
//            ds.getConnection().close();
//        }
//        return ownerGroup;
//    }