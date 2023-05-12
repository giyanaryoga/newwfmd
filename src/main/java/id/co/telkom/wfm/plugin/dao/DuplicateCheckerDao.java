/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class DuplicateCheckerDao {
    public boolean startWorkFallOut (String chkWonum, String chkSiteId){
    boolean isMatch = false;
    DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT ? FROM app_fd_workorder where c_siteid=?";
        try {
            Connection con = ds.getConnection();
            try {               
                PreparedStatement ps = con.prepareStatement(query);
                try {
                    try {
                        ps.setString(1, chkWonum);
                        ps.setString(2, chkSiteId);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()){
                            isMatch = true;
                        } else { 
                            isMatch = false;
                        }
                    } catch(SQLException e){
                        LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
                    }
                    if (ps !=null)
                        ps.close();
                } catch (SQLException throwable) {
                    if (ps !=null)
                        try {
                            ps.close();
                        } catch (SQLException throwable1) {
                            throwable.addSuppressed(throwable1);
                        }    
                    throw throwable;
                }
                if (con != null)
                    con.close();    
            } catch (SQLException throwable) {
                if (con !=null)
                    try {
                        con.close();
                    }catch(SQLException throwable1){
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }  
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    return isMatch;
    }
    
    public boolean childStatus (String chkParent){
    boolean hasChild = false;
    DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_haschild FROM app_fd_workorder where c_wonum=?";
        try {
            Connection con = ds.getConnection();
            try {               
                PreparedStatement ps = con.prepareStatement(query);
                try {
                    try {
                        ps.setString(1, chkParent);
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()){
                            hasChild = true;
                        } else { 
                            hasChild = false;
                        }
                    } catch(SQLException e){
                        LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
                    }
                    if (ps !=null)
                        ps.close();
                } catch (Throwable throwable) {
                    if (ps !=null)
                        try {
                            ps.close();
                        } catch (Throwable throwable1) {
                            throwable.addSuppressed(throwable1);
                        }    
                    throw throwable;
                }
                if (con !=null)
                    con.close();    
            } catch (Throwable throwable) {
                if (con !=null)
                    try {
                        con.close();
                    }catch(Throwable throwable1){
                        throwable.addSuppressed(throwable1);
                    }
                throw throwable;
            }  
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    return hasChild;
    }
}
