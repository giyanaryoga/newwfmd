/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.util;

import org.json.simple.JSONObject;

/**
 *
 * @author User
 */
public class JsonUtil {
    public String getString(JSONObject obj, String key) {
        return obj.get(key) == null ? "" : obj.get(key).toString();
    }
    
    public Long getLong(JSONObject obj, String key) {
        return obj.get(key) == null ? null : ((Number) obj.get(key)).longValue();
    }
    
    public JSONObject getUpdateStatusSuccessResp(String wonum, String status, String message) {
        //Create response
        JSONObject data = new JSONObject();
        data.put("wonum", wonum);
        data.put("status", status);
        JSONObject res = new JSONObject(); 
        res.put("code", 200);
        res.put("message", message);
        res.put("data", data);
        return res;
    }
    
    public JSONObject getUpdateStatusErrorResp(String wonum, String status, String message, int errorCode) {
        //Create response
        JSONObject data = new JSONObject();
        data.put("wonum", wonum);
        data.put("status", status);
        JSONObject res = new JSONObject(); 
        res.put("code", errorCode);
        res.put("message", message);
        res.put("data", data);
        return res;
    }
}
