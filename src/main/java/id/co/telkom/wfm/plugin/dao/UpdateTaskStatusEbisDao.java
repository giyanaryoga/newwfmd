/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;

/**
 *
 * @author ASUS
 */
public class UpdateTaskStatusEbisDao {
    
    public boolean updateTask(String wonum, String status) throws SQLException{
        boolean updateTask = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_woactivity SET c_status = ? WHERE c_wonum = ? AND c_wfmdoctype = 'NEW'";
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
    
    public boolean nextAssign(String parent, String nextTaskId) throws SQLException{
        boolean nextAssign = false;
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String update = "UPDATE app_fd_woactivity SET c_status = 'LABASSIGN' WHERE c_parent = ? AND c_taskid = ? AND c_wfmdoctype = 'NEW'";
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
        
}
