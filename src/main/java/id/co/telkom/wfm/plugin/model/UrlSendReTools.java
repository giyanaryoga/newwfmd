/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.model;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class UrlSendReTools {
    private String urlSendRe;
    private String createCustomer;
    private String apiMinio;
    

    /**
     * @return the urlSendRe
     */
    public String getUrlSendRe() {
        return urlSendRe;
    }

    /**
     * @param urlSendRe the urlSendRe to set
     */
    public void setUrlSendRe(String urlSendRe) {
        urlSendRe = "http://10.60.163.39/service/index.php/Json/insertURL"; //Dev
        this.urlSendRe = urlSendRe;
    }

    /**
     * @return the createCustomer
     */
    public String getCreateCustomer() {
        return createCustomer;
    }

    /**
     * @param createCustomer the createCustomer to set
     */
    public void setCreateCustomer(String createCustomer) {
        createCustomer = "http://10.6.3.135:8028/custLoc"; //Dev
        this.createCustomer = createCustomer;
    }

    /**
     * @return the apiMinio
     */
    public String getApiMinio() {
        return apiMinio;
    }

    /**
     * @param apiMinio the apiMinio to set
     */
    public void setApiMinio(String apiMinio) {
        apiMinio = "https://apiminio.telkom.co.id:9001/api/v1/buckets/oss-transformation/";
        this.apiMinio = apiMinio;
    }
}
