/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.model.ListLabor;
import id.co.telkom.wfm.plugin.dao.UpdateAssignmentEbisDao;
//import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author User
 */
public class UpdateAssignmentEbis extends Element implements PluginWebSupport {

    String pluginName = "Telkom New WFM - Update Assignment Labor Ebis - Web Service";

    @Override
    public String renderTemplate(FormData fd, Map map) {
        return "";
    }

    @Override
    public String getName() {
        return this.pluginName;
    }

    @Override
    public String getVersion() {
        return "7.00";
    }

    @Override
    public String getDescription() {
        return this.pluginName;
    }

    @Override
    public String getLabel() {
        return this.pluginName;
    }

    @Override
    public String getClassName() {
        return getClass().getName();
    }

    @Override
    public String getPropertyOptions() {
        return "";
    }

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {
        //@@Start..
        LogUtil.info(getClass().getName(), "Start Process: Update Labor");
        //@Authorization
        if ("POST".equals(hsr.getMethod())) {
            try {
                //@Parsing message
                //HttpServletRequest get JSON Post data
                StringBuilder jb = new StringBuilder();
                String line;
                try {//read the response JSON to string buffer
                    BufferedReader reader = hsr.getReader();
                    while ((line = reader.readLine()) != null) {
                        jb.append(line);
                    }
                } catch (IOException e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                //Parse JSON String to JSON Object
                String bodyParam = jb.toString(); //String
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject) parser.parse(bodyParam);//JSON Object
                JSONObject body = (JSONObject) data_obj.get("Request");

                //Store param
                String wonum = (body.get("wonum") == null ? "" : body.get("wonum").toString());
                String taskId = (body.get("taskId") == null ? "" : body.get("taskId").toString());
                String craft = (body.get("craft") == null ? "" : body.get("craft").toString());
                String crewType = (body.get("amcrewtype") == null ? "" : body.get("amcrewtype").toString());
                String crew = (body.get("amcrew") == null ? "" : body.get("amcrew").toString());
                String laborcode = (body.get("laborcode") == null ? "" : body.get("laborcode").toString());
//                DateTimeFormatter currentDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
//                String currentDate = LocalDateTime.now().format(currentDateFormat);
                UpdateAssignmentEbisDao dao = new UpdateAssignmentEbisDao();
                ListLabor listLabor = new ListLabor();
                try {
                    boolean updatedTemp = dao.updateLaborTemp(wonum, laborcode, craft, crewType, crew);
//                    dao.getLabor(laborcode, listLabor);
                    String laborname = listLabor.getLaborname();
                    boolean validateLabor = dao.getLabor(laborcode, listLabor);
                    if (validateLabor) {
                        LogUtil.info(getClass().getName(), "Laborcode: " + listLabor.getLaborcode() + ", Laborname: " + listLabor.getLaborname());
                    /**
                     * laborcode ada di table labor 
                     */
                        if (updatedTemp) {
                            dao.updateLabor(laborcode, laborname, wonum);
                        /**
                         * update c_laborcode dan c_laborname di table app_fd_workorder
                         */
                            dao.updateLaborWorkOrder(laborcode, laborname, wonum);
                        }

                        String statusHeaders = "200";
                        String statusRequest = "Success";
                        JSONObject response = new JSONObject();
                        JSONObject data = new JSONObject();
                        response.put("status", statusHeaders);
                        response.put("message", statusRequest);
                        response.put("response", data);
                        data.put("WONUM", wonum);
                        data.put("LABORCODE", laborcode);
                        data.put("LABORNAME", laborname);
                        response.writeJSONString(hsr1.getWriter());
                    } else {
                        LogUtil.info(getClass().getName(), "Laborcode and laborname is not found!");
                    /**
                     * laborcode tidak ada di table labor
                     */
                        String statusHeaders = "422";
                        String statusRequest = "Failed";
                        JSONObject response = new JSONObject();
                        JSONObject data = new JSONObject();
                        response.put("status", statusHeaders);
                        response.put("message", statusRequest);
                        response.put("response", data);
                        data.put("message", "Laborcode and laborname is not found!");
                        response.writeJSONString(hsr1.getWriter());
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(UpdateAssignmentEbis.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (ParseException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else if (!"POST".equals(hsr.getMethod())) {
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (IOException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        }
    }
}

