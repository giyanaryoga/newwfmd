package id.co.telkom.wfm.plugin;

import id.co.telkom.wfm.plugin.dao.GenerateIPReservationDao;
import id.co.telkom.wfm.plugin.dao.GenerateSidConnectivityDao;
import id.co.telkom.wfm.plugin.model.ListGenerateAttributes;
import org.joget.apps.form.model.Element;
import org.joget.apps.form.model.FormData;
import org.joget.commons.util.LogUtil;
import org.joget.plugin.base.PluginWebSupport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenerateIPReservation extends Element implements PluginWebSupport {

        String pluginName = "Telkom New WFM - Generate IP RESERVATION - Web Service";

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
            return "7.0.0";
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
            GenerateIPReservationDao dao = new GenerateIPReservationDao();

            //@@Start..
            LogUtil.info(this.getClass().getName(), "############## START PROCESS GENERATE IP RESERVATION ###############");

            //@Authorization
            if ("POST".equals(hsr.getMethod())) {
                try {
                    //@Parsing message
                    //HttpServletRequest get JSON Post data
                    StringBuffer jb = new StringBuffer();
                    String line = null;
                    try {//read the response JSON to string buffer
                        BufferedReader reader = hsr.getReader();
                        while ((line = reader.readLine()) != null) {
                            jb.append(line);
                        }
                    } catch (Exception e) {
                        LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                    }
                    LogUtil.info(getClassName(), "Request Body: " + jb.toString());

//                ListAttributes attribute = new ListAttributes();
                    //Parse JSON String to JSON Object
                    String bodyParam = jb.toString(); //String
                    JSONParser parser = new JSONParser();
                    JSONObject data_obj = (JSONObject) parser.parse(bodyParam);//JSON Object
                    //Store param
                    LogUtil.info(getClassName(), "Store Param "+data_obj);
                    String wonum = data_obj.get("wonum").toString();
                    String serviceType = data_obj.get("serviceType").toString();
                    String customername = data_obj.get("customername").toString();
                    String cardinality = data_obj.get("cardinality").toString();
                    String ipType = data_obj.get("ipType").toString();
                    String ipArea = data_obj.get("ipArea").toString();
                    String vrf = data_obj.get("vrf").toString();
                    String version = data_obj.get("version").toString();
                    String packageType = data_obj.get("packageType").toString();

                    LogUtil.info(this.getClassName(), "ListGenerateAttributes");
                    ListGenerateAttributes listAttribute = new ListGenerateAttributes();
                    try {
//                        dao.allTable();
                        LogUtil.info(this.getClassName(), "Star Process Call Generate IP Reservation ");
                        dao.callGenerateConnectivity(wonum, serviceType, customername, cardinality, ipType, vrf, ipArea, version, packageType, listAttribute);

//                    ListGenerateAttributes listAttribute = new ListGenerateAttributes();
                        LogUtil.info(this.getClassName(), "get data: " + listAttribute.getStatusCode3());

                        if (listAttribute.getStatusCode3() == 404) {
                            JSONObject res1 = new JSONObject();
                            res1.put("code", 404);
                            res1.put("message", "No Service found!.");
                            res1.writeJSONString(hsr1.getWriter());
//                        dao.insertIntegrationHistory(wonum, line, wonum, wonum, orderId);
                        } else {
//                            dao.moveFirst(wonum);
//                            dao.insertIntoDeviceTable(wonum, listAttribute);
//                            dao.insertIntegrationHistory(wonum, line, wonum, wonum, orderId);
                            JSONObject res = new JSONObject();
                            res.put("code", 200);
                            res.put("message", "update data successfully");
                            res.put("data", listAttribute.getMessage());
                            res.writeJSONString(hsr1.getWriter());
                        }
                    } catch (Exception ex) {
                        LogUtil.info(getClassName(), "Exception");
                        Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (Throwable ex) {
                        LogUtil.info(getClassName(), "Throwable");
                        Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } catch (ParseException ex) {
                    LogUtil.info(getClassName(), "ParseException");
                    Logger.getLogger(GenerateStpNetLoc.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (!"POST".equals(hsr.getMethod())) {
                try {
                    hsr1.sendError(405, "Method Not Allowed");
                } catch (Exception e) {
                    LogUtil.error(getClassName(), e, "Trace error here: " + e.getMessage());
                }
            }
        }
}
