/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.model;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class ActivityTask {
    private int taskId;
//    private int tempStb;
    private String descriptionTask;
    private String correlation;
//    private String pullDropCable;
//    private String revOrder;
//    private String shipDeliv;
//    private String getNte;
//    private String populOAM;
//    private String valVPN;
//    private String valMetroVPN;
//    private String valPeRouter;
//    private String ceJump;
//    private String servTestVPN;
//    private String regSuplychain;
//    private String apprE2ETesting;
//    private String insOnt;
//    private String insStb;
//    private String valVoice;
//    private String valInet;
//    private String valBroad;
//    private String valIptv;
//    private String valDigital;
//    private String corr1;

    /**
     * @return the taskId
     */
    public int getTaskId() {
        return taskId;
    }

    /**
     * @param taskId the taskId to set
     */
    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    /**
     * @return the descriptionTask
     */
    public String getDescriptionTask() {
        return descriptionTask;
    }

    /**
     * @param descriptionTask the descriptionTask to set
     */
    public void setDescriptionTask(String descriptionTask) {
        this.descriptionTask = descriptionTask;
    }

    /**
     * @return the correlation
     */
    public String getCorrelation() {
        return correlation;
    }

    /**
     * @param correlation the correlation to set
     */
    public void setCorrelation(String correlation) {
        this.correlation = correlation;
    }

}
