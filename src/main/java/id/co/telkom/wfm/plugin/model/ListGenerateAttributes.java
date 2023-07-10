/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.co.telkom.wfm.plugin.model;

/**
 *
 * @author ASUS
 */
public class ListGenerateAttributes {

    // Generate STP NETWORK LOCATION
    private String description;
    private String attrType;
    private String attrName;
    private Integer statusCode;

    // Generate IPv4
    private String gatewayAddress;
    private String serviceIp;
    private String ipDomain;
    private String networkAddress;
    private String reservationId;
    private String vrf;
    private String subnetMask;
    private String netMask;

    // Generate SID Connectivity
    private String id;
    private String  name;
    public Integer statusCode3;
    
    // ===============================
    // Generate STP NETWORK LOCATION
    //================================
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    //===================================
    // Generate IPv4
    //===================================
    public String getGateawayAddress() {
        return gatewayAddress;
    }

    public void setGateawayAddress(String gateawayAddress) {
        this.gatewayAddress = gateawayAddress;
    }

    public String getServiceIp() {
        return serviceIp;
    }

    public void setServiceIp(String serviceIp) {
        this.serviceIp = serviceIp;
    }

    public String getIpDomain() {
        return ipDomain;
    }

    public void setIpDomain(String ipDomain) {
        this.ipDomain = ipDomain;
    }

    public String getNetworkAddress() {
        return networkAddress;
    }

    public void setNetworkAddress(String networkAddress) {
        this.networkAddress = networkAddress;
    }

    public String getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
    
    public String getVrf() {
        return vrf;
    }
    
    public void setVrf(String vrf) {
        this.vrf = vrf;
    }
    
    public String getSubnetMask() {
        return subnetMask;
    }
    
    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }
    
    public String getNetMask() {
        return subnetMask;
    }
    
    public void setNetMask(String netMask) {
        this.netMask = netMask;
    }
    
    // Generate SID Connectivity
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the statusCode3
     */
    public Integer getStatusCode3() {
        return statusCode3;
    }

    /**
     * @param statusCode3 the statusCode3 to set
     */
    public void setStatusCode3(Integer statusCode3) {
        this.statusCode3 = statusCode3;
    }
}
