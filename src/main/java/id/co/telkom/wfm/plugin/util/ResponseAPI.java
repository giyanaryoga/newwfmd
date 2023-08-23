/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.util;

import org.json.simple.JSONObject;

/**
 *
 * @author ASUS
 */
public class ResponseAPI {
    public JSONObject response(int statusCode, String status, String message, String data) {
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("code", statusCode);
        jsonResponse.put("status", status);
        jsonResponse.put("message", message);
        jsonResponse.put("data", data);
        
        return jsonResponse;
    }
}
