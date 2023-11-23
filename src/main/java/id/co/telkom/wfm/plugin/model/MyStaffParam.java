/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.model;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class MyStaffParam {
    private String parent;
    private String wonum;
    private String task;
    private String siteid;
    //Update task attribute
    private String assetAttrId;
    private String value;
    private String changeDate;
    private String changeBy;
    private String modifiedBy;
    private String detailactcode;

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getWonum() {
        return wonum;
    }

    public void setWonum(String wonum) {
        this.wonum = wonum;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getAssetAttrId() {
        return assetAttrId;
    }

    public void setAssetAttrId(String assetAttrId) {
        this.assetAttrId = assetAttrId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChangeDate() {
        return changeDate;
    }

    public void setChangeDate(String changeDate) {
        this.changeDate = changeDate;
    }

    public String getChangeBy() {
        return changeBy;
    }

    public void setChangeBy(String changeBy) {
        this.changeBy = changeBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public String getDetailactcode() {
        return detailactcode;
    }

    public void setDetailactcode(String detailactcode) {
        this.detailactcode = detailactcode;
    }

    public String getSiteid() {
        return siteid;
    }

    public void setSiteid(String siteid) {
        this.siteid = siteid;
    }

}
