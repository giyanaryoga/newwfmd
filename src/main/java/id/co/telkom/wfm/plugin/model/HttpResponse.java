/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.model;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class HttpResponse {
    private final int code;
    private final String body;
    
    public HttpResponse(int code, String body){
        this.code = code;
        this.body = body;
    }

    public int getCode() {
        return code;
    }

    public String getBody() {
        return body;
    }
}
