///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package id.co.telkom.wfm.plugin.dao;
//
//import id.co.telkom.wfm.plugin.model.APIConfig;
//import id.co.telkom.wfm.plugin.util.ConnUtil;
//import id.co.telkom.wfm.plugin.util.RequestAPI;
//import java.sql.SQLException;
//import okhttp3.MediaType;
//import okhttp3.RequestBody;
//import org.json.simple.JSONObject;
//
///**
// *
// * @author ASUS
// */
//public class GenerateStpNetLocDao {
//    public String apiId = "";
//    public String apiKey = "";
//    
//    public Object getStpNetLoc(String latitude, String longitude) throws SQLException {
//        // Prepare variable
//        JSONObject data_obj = null;
//        JSONObject api_obj;
//        
//        String response;
//        // Get configuration to 'POST'
////        RequestAPI api = new RequestAPI();
//        ConnUtil connUtil = new ConnUtil();
//        try {
//            APIConfig apiConfig = new APIConfig();
//            apiConfig.setUrl("http://10.60.170.43:7051/EnterpriseFeasibilityUim/EnterpriseFeasibilityUimHTTP");
//            
//            // Create request
//            JSONObject  = xml.toJSONObject();
////            req.put("detail", "false");
////            req.put("role", "STP");
////            req.put("latitude", latitude);
////            req.put("latitude", longitude);
//
//            String xml = "<soapenv:Body xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ent=\"http://xmlns.oracle.com/communications/inventory/webservice/enterpriseFeasibility\">\n" +
//                            "  <ent:findDeviceByCriteriaRequest>\n" +
//                            "    <DeviceInfo>\n" +
//                            "      <detail>false</detail>\n" +
//                            "      <role>STP</role>\n" +
//                            "    </DeviceInfo>\n" +
//                            "    <ServiceLocation>\n" +
//                            "      <latitude>-6.916131661</latitude>\n" +
//                            "      <longitude>107.61371456</longitude>\n" +
//                            "    </ServiceLocation>\n" +
//                            "  </ent:findDeviceByCriteriaRequest>\n" +
//                            "</soapenv:Body>";
//            RequestBody body = RequestBody.create(MediaType.parse("application/soap+xml"), xml);
//        }
//    }
//}
