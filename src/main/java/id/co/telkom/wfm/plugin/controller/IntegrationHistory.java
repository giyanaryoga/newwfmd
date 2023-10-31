/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.controller;

import id.co.telkom.wfm.plugin.kafka.ResponseKafka;
import id.co.telkom.wfm.plugin.util.TimeUtil;
import java.sql.*;
import java.util.Date;
import javax.sql.DataSource;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class IntegrationHistory {
    ResponseKafka responseKafka = new ResponseKafka();
    TimeUtil time = new TimeUtil();
    
    public void insertKafka(String referenceid, String apitype, String typeint, String status, JSONObject request, JSONObject response) {
        JSONArray dataRequest = new JSONArray();
        JSONArray dataResponse = new JSONArray();
        JSONObject data = new JSONObject();
        
        dataRequest.add(request);
        dataResponse.add(response);
        data.put("referenceid", referenceid);
        data.put("integration_type", typeint);
        data.put("integration_api", apitype);
        data.put("status", status);
        data.put("exec_date", time.getCurrentTime());
        data.put("request", dataRequest);
        data.put("response", dataResponse);
        
        // Response to Kafka
        String kafkaRes = data.toJSONString();
        //KAFKA DEVELOPMENT
        responseKafka.IntegrationHistory(kafkaRes);
        //KAFKA PRODUCTION
//        responseKafka.IntegrationHistory(kafkaRes);
    }
}
