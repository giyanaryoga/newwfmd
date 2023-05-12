/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONObject;

/**
 *
 * @author Acer
 */
public class ConnUtil {
        public JSONObject getEnvVariableScheduling() throws SQLException {
        JSONObject resultObj = new JSONObject();
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT C_ENV_KEY, C_ENV_VALUE FROM APP_FD_ENV_VARIABLE WHERE C_ENV_KEY = 'SCHEDULING_API_KEY' OR C_ENV_KEY = 'SCHEDULING_API_ID' OR C_ENV_KEY = 'SCHEDULING_BASE'";
        try(Connection con = ds.getConnection(); 
            PreparedStatement ps = con.prepareStatement(query)) {
          ResultSet rs = ps.executeQuery();
          while (rs.next())
            resultObj.put(rs.getString("C_ENV_KEY"), rs.getString("C_ENV_VALUE")); 
        } catch (SQLException e) {
          LogUtil.error(getClass().getName(), e, "Trace error here : " + e.getMessage());
        } finally {
          ds.getConnection().close();
        } 
        return resultObj;
  }
}
