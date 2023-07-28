/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.ListGenerateAttributes;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.joget.commons.util.LogUtil;
import org.json.JSONObject;
import org.json.XML;

/**
 *
 * @author ASUS
 */
public class GenerateDownlinkPortDao {

    public JSONObject callGenerateDownlinkPort(String bandwidth, String odpName, String downlinkPortName, String downlinkPortID, String sto, ListGenerateAttributes listGenerate) throws MalformedURLException, IOException {
        try {
            String request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\">\n"
                    + "   <soapenv:Header/>\n"
                    + "   <soapenv:Body>\n"
                    + "      <ent:getAccessNodeDeviceRequest>\n"
                    + "         <Bandwidth>" + bandwidth + "</Bandwidth>\n"
                    + "         <ServiceEndPointDeviceInformation>\n"
                    + "            <Name>" + odpName + "</Name>\n"
                    + "            <DownlinkPort>\n"
                    + "               <name>" + downlinkPortName + "</name>\n"
                    + "               <id>" + downlinkPortID + "</id>\n"
                    + "            </DownlinkPort>\n"
                    + "            <STO>" + sto + "</STO>\n"
                    + "         </ServiceEndPointDeviceInformation>\n"
                    + "      </ent:getAccessNodeDeviceRequest>\n"
                    + "   </soapenv:Body>\n"
                    + "</soapenv:Envelope>";

            String urlres = "http://10.6.28.132:7001/EnterpriseFeasibilityUim/EnterpriseFeasibilityUimHTTP";
            URL url = new URL(urlres);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            // Set Headers
            connection.setRequestProperty("Accept", "application/xml");
            connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
            try ( // Write XML
                    OutputStream outputStream = connection.getOutputStream()) {
                byte[] b = request.getBytes("UTF-8");
                outputStream.write(b);
                outputStream.flush();
            }

            StringBuilder response;
            try ( // Read XML
                    InputStream inputStream = connection.getInputStream()) {
                byte[] res = new byte[2048];
                int i = 0;
                response = new StringBuilder();
                while ((i = inputStream.read(res)) != -1) {
                    response.append(new String(res, 0, i));
                }
            }
            StringBuilder result = response;
            org.json.JSONObject temp = XML.toJSONObject(result.toString());
            System.out.println("temp " + temp.toString());
            LogUtil.info(this.getClass().getName(), "INI RESPONSE : " + temp.toString());

            //Parsing response data
            LogUtil.info(this.getClass().getName(), "############ Parsing Data Response ##############");
            org.json.JSONObject envelope = temp.getJSONObject("env:Envelope").getJSONObject("env:Body");
            org.json.JSONObject device = envelope.getJSONObject("ent:getAccessNodeDeviceResponse");
            int statusCode = device.getInt("statusCode");

            LogUtil.info(this.getClass().getName(), "StatusCode : " + statusCode);

            if (statusCode == 4001) {
                LogUtil.info(this.getClass().getName(), "DownlinkPort Not found!");
                listGenerate.setStatusCode(statusCode);
            } else {
                JSONObject getDeviceInformation = device.getJSONObject("AccessDeviceInformation");
                
                JSONObject downlinkPort = getDeviceInformation.getJSONObject("DownlinkPort");
                String manufacture = getDeviceInformation.getString("Manufacturer");
                String name = getDeviceInformation.getString("Name");
                String ipAddress = getDeviceInformation.getString("IPAddress");
                String nmsIpaddress = getDeviceInformation.getString("NMSIPAddress");
                String sTO = getDeviceInformation.getString("STO");
                String id = getDeviceInformation.getString("Id");
                
                LogUtil.info(this.getClass().getName(), "DownlinkPort :" + downlinkPort);
                LogUtil.info(this.getClass().getName(), "Manufacture :" + manufacture);
                LogUtil.info(this.getClass().getName(), "Name :" + name);
                LogUtil.info(this.getClass().getName(), "IPAddress :" + ipAddress);
                LogUtil.info(this.getClass().getName(), "NMSIPAddress :" + nmsIpaddress);
                LogUtil.info(this.getClass().getName(), "STO :" + sTO);
                LogUtil.info(this.getClass().getName(), "ID :" + id);
            }

        } catch (Exception e) {
            LogUtil.error(getClass().getName(), e, "Call Failed." + e);
        }
        return null;
    }
    
//    public boolean updateData(String wonum, String )
}
