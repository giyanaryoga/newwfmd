/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

//import id.co.telkom.wfm.plugin.model.ListAttributes;
//import id.co.telkom.wfm.plugin.model.ListDevice;
//import id.co.telkom.wfm.plugin.model.ListOssItem;
import id.co.telkom.wfm.plugin.model.ListOssItemAttribute;
import id.co.telkom.wfm.plugin.model.ActivityTask;
import id.co.telkom.wfm.plugin.model.ListLabor;
import id.co.telkom.wfm.plugin.model.ListClassSpec;
import id.co.telkom.wfm.plugin.model.ListCpeValidate;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/**
 *
 * @author User
 */
public class UpdateAssignmentDao {
    public String getLabor(String laborcode, ListLabor listLabor) throws SQLException {
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
                listLabor.setLaborname("c_displayname");
            } else con.rollback();
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
            ds.getConnection().close();
        }
        return laborcode;
    }
}
