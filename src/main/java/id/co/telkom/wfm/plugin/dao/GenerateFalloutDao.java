/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.sql.Timestamp;
import java.util.Date;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.joget.commons.util.UuidGenerator;

/**
 *
 * @author ASUS
 */
public class GenerateFalloutDao {

    public String apiId = "";
    public String apiKey = "";

    public void getApiAttribute() {
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT c_api_id, c_api_key FROM app_fd_api_wfm WHERE c_use_of_api = 'falloutincident' ";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(query);
                try {
                    try {
                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            this.apiId = rs.getString("c_api_id");
                            this.apiKey = rs.getString("c_api_key");
                        }
                    } catch (SQLException e) {
                        LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
                    }
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException throwable) {
                    if (ps != null)
                        try {
                        ps.close();
                    } catch (SQLException throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException throwable) {
                if (con != null)
                    try {
                    con.close();
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
    }

    public String getTicketid() {
        String ticketId = "";
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT GENERATE_TICKETID FROM dual";
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(query);
                try {
                    try {

                        ResultSet rs = ps.executeQuery();
                        if (rs.next()) {
                            ticketId = rs.getString("GENERATE_TICKETID");
                        }
                    } catch (SQLException e) {
                        LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
                    }
                    if (ps != null) {
                        ps.close();
                    }
                } catch (SQLException throwable) {
                    if (ps != null)
                        try {
                        ps.close();
                    } catch (SQLException throwable1) {
                        throwable.addSuppressed(throwable1);
                    }
                    throw throwable;
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException throwable) {
                if (con != null)
                    try {
                    con.close();
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return ticketId;
    }

    public boolean insertToWoTable(String externalSystem, String longDescription, String ossid, String region, String customerType, String workzone, String classification, String description, String internalPriority, String statusCode, String ticketId, String tk_channel) {
//        TimeUtil time = new TimeUtil();
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String uuId = UuidGenerator.getInstance().getUuid();//generating uuid
        boolean insertStatus = false;
        DataSource ds = (DataSource) AppUtil.getApplicationContext().getBean("setupDataSource");

        StringBuilder insert = new StringBuilder();
        insert.append("INSERT INTO app_fd_incident ")
                .append("( ")
                .append("id, ")
                .append("c_externalsystem_ticketid, ")
                .append("c_description_longdescription, ")
                .append("c_tk_ossid, ")
                .append("c_tk_region, ")
                .append("c_tk_ticket_59, ")
                .append("c_tk_workzone, ")
                .append("c_tk_classification, ")
                .append("c_description, ")
                .append("c_internalpriority, ")
//                .append("c_ownergroup, ")
                .append("c_tk_statuscode, ")
                .append("c_ticketid, ")
                .append("c_tk_channel, ")
                .append("datecreated ")
                .append(") ")
                .append("VALUES ")
                .append("(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        try {
            Connection con = ds.getConnection();
            try {
                PreparedStatement ps = con.prepareStatement(insert.toString());
                ps.setString(1, uuId);
                    ps.setString(2, externalSystem);
                    ps.setString(3, longDescription);
                    ps.setString(4, ossid);
                    ps.setString(5, region);
                    ps.setString(6, customerType);
                    ps.setString(7, workzone);
                    ps.setString(8, classification);
                    ps.setString(9, description);
                    ps.setString(10, internalPriority);
//                    ps.setString(11, ownerGroup);
                    ps.setString(11, statusCode);
                    ps.setString(12, ticketId);
                    ps.setString(13, tk_channel);
                    ps.setTimestamp(14, timestamp);

                    ResultSet exe = ps.executeQuery();
                    //Checking insert status
                    if (exe.next()) {
                        insertStatus = true;
                        LogUtil.info(getClass().getName(), "Work Order param for '" + ticketId + "' inserted to DB");
                    }
                    if (ps != null) {
                        ps.close();
                    }
            } catch (SQLException throwable) {
                try {
                    if (con != null) {
                        con.close();
                    }
                } catch (SQLException throwable1) {
                    throwable.addSuppressed(throwable1);
                }
                throw throwable;
            } finally {
                ds.getConnection().close();
            }
        } catch (SQLException e) {
            LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        }
        return insertStatus;
    }
}
