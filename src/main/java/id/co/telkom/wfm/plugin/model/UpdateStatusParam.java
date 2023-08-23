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
public class UpdateStatusParam {

//    public UpdateStatusParam() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    /**
     * @return the currentDate
     */
    public String getCurrentDate() {
        return currentDate;
    }

    /**
     * @param currentDate the currentDate to set
     */
    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    private String parent;
    private String wonum;
    private String taskId;
    private String siteId;
    private String woStatus;
    private String description;
    private String status;
    private String modifiedBy;
    private String memo;
    private String currentDate;
    
//    private UpdateStatusParam(Builder builder) {
//        this.parent = builder.parent;
//        this.wonum = builder.wonum;
//        this.taskId = builder.taskId;
//        this.siteId = builder.siteId;
//        this.woStatus = builder.woStatus;
//        this.description = builder.description;
//        this.status = builder.status;
//        this.modifiedBy = builder.modifiedBy;
//        this.memo = builder.memo;
//    }
//    
//    public static class Builder {
//        private String parent;
//        private String wonum;
//        private String taskId;
//        private String siteId;
//        private String woStatus;
//        private String description;
//        private String status;
//        private String modifiedBy;
//        private String memo;
//        private String currentDate;
//    }

    /**
     * @return the parent
     */
    public String getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

    /**
     * @return the wonum
     */
    public String getWonum() {
        return wonum;
    }

    /**
     * @param wonum the wonum to set
     */
    public void setWonum(String wonum) {
        this.wonum = wonum;
    }

    /**
     * @return the taskId
     */
    public String getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the siteId
     */
    public String getSiteId() {
        return siteId;
    }

    /**
     * @param siteId the siteId to set
     */
    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    /**
     * @return the woStatus
     */
    public String getWoStatus() {
        return woStatus;
    }

    /**
     * @param woStatus the woStatus to set
     */
    public void setWoStatus(String woStatus) {
        this.woStatus = woStatus;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the modifiedBy
     */
    public String getModifiedBy() {
        return modifiedBy;
    }

    /**
     * @param modifiedBy the modifiedBy to set
     */
    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    /**
     * @return the memo
     */
    public String getMemo() {
        return memo;
    }

    /**
     * @param memo the memo to set
     */
    public void setMemo(String memo) {
        this.memo = memo;
    }
}
