/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.ScmtIntegrationEbisDao;
import id.co.telkom.wfm.plugin.dao.UpdateTaskStatusEbisDao;
import id.co.telkom.wfm.plugin.kafka.KafkaProducerTool;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
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
 * @author ASUS
 */
public class UpdateTaskStatusEbis extends Element implements PluginWebSupport {
   
    String pluginName = "Telkom New WFM - Update Task Status Ebis - Web Service";

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
    
//    private String dateFormatter(String sourceDate){
//        DateTimeFormatter sourceFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//        DateTimeFormatter targetFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
//        String convertedDate = LocalDateTime.parse(sourceDate, sourceFormat).format(targetFormat);
//        return convertedDate;
//    }

    @Override
    public void webService(HttpServletRequest hsr, HttpServletResponse hsr1) throws ServletException, IOException {
//@@Start..
        LogUtil.info(getClass().getName(), "Start Process: Update Task Status");
        //@Authorization
        if ("POST".equals(hsr.getMethod())) {
            try{
                //@Parsing message
                //HttpServletRequest get JSON Post data
                StringBuffer jb = new StringBuffer();
                String line = null;
                try {//read the response JSON to string buffer
                    BufferedReader reader = hsr.getReader();
                    while((line = reader.readLine()) !=null)
                        jb.append(line);
                } catch (Exception e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
                LogUtil.info(getClassName(), "Request Body: " + jb.toString());
                //Parse JSON String to JSON Object
                String bodyParam = jb.toString(); //String
                JSONParser parser = new JSONParser();
                JSONObject data_obj = (JSONObject)parser.parse(bodyParam);//JSON Object
                //Store param
                String wonum = data_obj.get("wonum").toString();
                String parent = wonum.substring(0, 11);
                String taskId = data_obj.get("taskId").toString();
                String status = data_obj.get("status").toString();
                String siteId = (data_obj.get("siteId") == null ? "" : data_obj.get("siteId").toString());
                String woSequence = data_obj.get("woSequence").toString();
                String woStatus = data_obj.get("woStatus").toString();
                String description = data_obj.get("description").toString();
                DateTimeFormatter currentDateFormat = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                String currentDate = LocalDateTime.now().format(currentDateFormat);
                int nextTaskId = Integer.parseInt(taskId) + 10;
                if (woSequence.equals("10") || woSequence.equals("20") || woSequence.equals("30") || woSequence.equals("40") || woSequence.equals("50") || woSequence.equals("60")) { 
                    //Update status
                    UpdateTaskStatusEbisDao updateTaskStatusEbisDao = new UpdateTaskStatusEbisDao();
                    if ("STARTWA".equals(data_obj.get("status"))){
                        final boolean updateTask = updateTaskStatusEbisDao.updateTask(wonum, status);
                        if (updateTask) 
                            hsr1.setStatus(200);
                    } else if ("COMPWA".equals(data_obj.get("status"))){
                        final String nextMove = updateTaskStatusEbisDao.nextMove(parent, Integer.toString(nextTaskId));
                        if (description.equals("Registration Suplychain")) {
                            // Create Response
//                            JSONObject data = new JSONObject();
//                            data.put("wonum", parent);
//                            data.put("milestone status", data);
                            JSONObject res = new JSONObject();
                            res.put("code", "200");
                            res.put("message", "Success");
                            
                            
                            // update task status
                            updateTaskStatusEbisDao.updateTask(wonum, status);
                                
                            // res.put("data", data);
                            res.writeJSONString(hsr1.getWriter());
                            // Start of 'Install NTE'
                            ScmtIntegrationEbisDao scmtIntegrationEbisDao = new ScmtIntegrationEbisDao();

                            scmtIntegrationEbisDao.sendInstall(parent);
                            
                            final boolean nextAssign = updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId));
                            if (nextAssign)
                                    hsr1.setStatus(200);  
                                updateTaskStatusEbisDao.updateTask(wonum, status);
                        } else {
                            // Define the next move
                            
                            if("COMPLETE".equals(nextMove)) {                           
                                // Update parent status
                                updateTaskStatusEbisDao.updateParentStatus(parent, "COMPLETE", currentDate, "");

                                // update task status
                                updateTaskStatusEbisDao.updateTask(wonum, status);

                                //Build Response
                                JSONObject data = updateTaskStatusEbisDao.getCompleteJson(parent);

                                // Response to Kafka
                                String kafkaRes = data.toJSONString();
                                KafkaProducerTool kaf = new KafkaProducerTool();
                                kaf.generateMessage(kafkaRes, "WFM_MILESTONE", "");
                            } else {
                                //Give LABASSIGN to next task
                                final boolean nextAssign = updateTaskStatusEbisDao.nextAssign(parent, Integer.toString(nextTaskId));
                                if (nextAssign)
                                    hsr1.setStatus(200);  
                                updateTaskStatusEbisDao.updateTask(wonum, status);

                            }
                        }
                        
                        
                    }
                }   
            } catch (ParseException | SQLException e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            }
        } else if (!"POST".equals(hsr.getMethod())){
            try {
                hsr1.sendError(405, "Method Not Allowed");
            } catch (Exception e) {
                LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
            } 
        }
    }    
    
}