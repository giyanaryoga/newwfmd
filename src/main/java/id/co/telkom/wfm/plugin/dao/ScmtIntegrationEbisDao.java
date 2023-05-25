/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.dao;

import id.co.telkom.wfm.plugin.model.APIConfig;
import id.co.telkom.wfm.plugin.util.ConnUtil;
import id.co.telkom.wfm.plugin.util.RequestAPI;
import javax.sql.DataSource;
import okhttp3.FormBody;
import okhttp3.RequestBody;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.LogUtil;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author User
 */
public class ScmtIntegrationEbisDao {
    public String getScmtToken() {
        String token = "";
        RequestAPI api = new RequestAPI();
        ConnUtil connUtil = new ConnUtil();
        try {
          APIConfig apiConfig = new APIConfig();
          apiConfig = connUtil.getApiParam("get_eai_token_scmt");
          FormBody formBody = (new FormBody.Builder()).add("grant_type", apiConfig.getGrantType()).add("client_id", apiConfig.getClientId()).add("client_secret", apiConfig.getClientSecret()).build();
          String response = "";
          response = api.sendPostEaiToken(apiConfig, (RequestBody)formBody);
          JSONParser parse = new JSONParser();
          JSONObject data_obj = (JSONObject)parse.parse(response);
          token = data_obj.get("access_token").toString();
        } catch (Exception e) {
          LogUtil.error(getClass().getName(), e, "Trace error here: " + e.getMessage());
        } 
        return token;
    }
    
    public JSONObject getInstallNteJson() {
        DataSource ds = (DataSource)AppUtil.getApplicationContext().getBean("setupDataSource");
        String query = "SELECT FROM app";
        JSONObject data = null;
        return data;
    }
}
