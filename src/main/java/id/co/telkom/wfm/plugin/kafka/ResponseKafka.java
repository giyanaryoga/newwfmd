/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.kafka;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class ResponseKafka {
    public void MilestoneEbis(String response, String siteid) {
        KafkaProducerTool kafkaProducerTool = new KafkaProducerTool();
        String siteIdReplace = siteid.replaceAll("-", "_").toLowerCase();
        String topic = "usrwfm_milestone_enterprise_"+siteIdReplace;
        kafkaProducerTool.generateConfluentMessage(response, topic, "");
    }
    
    public void FalloutIncident(String response, String siteid) {
        KafkaProducerTool kafkaProducerTool = new KafkaProducerTool();
        String siteIdReplace = siteid.replaceAll("-", "_").toLowerCase();
        String topic = "usrwfm_fallout_incident_"+siteIdReplace;
        kafkaProducerTool.generateConfluentMessage(response, topic, "");
    }
    
    public void InstallDismantleScmt(String response, String siteid) {
        KafkaProducerTool kafkaProducerTool = new KafkaProducerTool();
        String siteIdReplace = siteid.replaceAll("-", "_").toLowerCase();
        String topic = "usrwfm_newscmt_install_enterprise_"+siteIdReplace;
        kafkaProducerTool.generateConfluentMessage(response, topic, "");
    }
    
    public void IntegrationHistory(String response) {
        KafkaProducerTool kafkaProducerTool = new KafkaProducerTool();
        String topic = "usrwfm_new_wfm_integration_history";
        kafkaProducerTool.generateConfluentMessage(response, topic, "");
    }
    
    public void MilestoneEbisDev(String response, String siteid) {
        KafkaProducerTool kafkaProducerTool = new KafkaProducerTool();
        String topic = "WFM_MILESTONE_ENTERPRISE_"+siteid;
        kafkaProducerTool.generateMessage(response, topic, "");
    }
    
    public void FalloutIncidentDev(String response, String siteid) {
        KafkaProducerTool kafkaProducerTool = new KafkaProducerTool();
        String topic = "WFM_FALLOUT_INCIDENT_"+siteid;
        kafkaProducerTool.generateMessage(response, topic, "");
    }
    
    public void InstallDismantleScmtDev(String response, String siteid) {
        KafkaProducerTool kafkaProducerTool = new KafkaProducerTool();
        String topic = "WFM_NEWSCMT_INSTALL_ENTERPRISE_"+siteid;
        kafkaProducerTool.generateMessage(response, topic, "");
    }
    
    public void IntegrationHistoryDev(String response) {
        KafkaProducerTool kafkaProducerTool = new KafkaProducerTool();
        String topic = "WFM_INTEGRATION_HISTORY";
        kafkaProducerTool.generateMessage(response, topic, "");
    }
}
