/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.kafka;

import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.joget.apps.app.model.AppDefinition;
import org.joget.apps.app.service.AppUtil;
import org.joget.commons.util.PluginThread;

/**
 *
 * @author Giyanaryoga Puguh
 */
public class KafkaProducerTool {
    public void generateMessage(String kafkaRes, String topik, String kunci){
        //Define param
        String bootstrapServers = "10.62.61.102:19092";
        String topic = topik;
        String key = kunci;
        String message = kafkaRes;
        Properties producerProperties = getClientConfig(bootstrapServers);
        //Set classloader for OSGI
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLaoder = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(this.getClass().getClassLoader());
            //Start producer thread
            ProducerRunnable producerRunnable = new ProducerRunnable(producerProperties, topic, key, message);
            PluginThread producerThread = new PluginThread(producerRunnable);
            producerThread.start();
        }finally {
            //Reset classloader
            currentThread.setContextClassLoader(threadContextClassLaoder);
        }
    }
    
    //get connection properties
    public Properties getClientConfig(String bootstrapServers){
        Properties configs = new Properties();
        //Common properties
        configs.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //Producer properties
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        return configs;
    }
    
    public void generateConfluentMessage(String kafkaRes, String topik, String kunci){
        //Define param
        String topic = topik;
        String key = kunci;
        String message = kafkaRes;
        Properties producerProperties = getConfluentClientConfig();
        //Set classloader for OSGI
        Thread currentThread = Thread.currentThread();
        ClassLoader threadContextClassLaoder = currentThread.getContextClassLoader();
        try {
            currentThread.setContextClassLoader(this.getClass().getClassLoader());
            //Start producer thread
            ProducerRunnable producerRunnable = new ProducerRunnable(producerProperties, topic, key, message);
            PluginThread producerThread = new PluginThread(producerRunnable);
            producerThread.start();
        } finally {
            //Reset classloader
            currentThread.setContextClassLoader(threadContextClassLaoder);
        }
    }
    
    public Properties getConfluentClientConfig() {
        Properties configs = new Properties();
        //Common properties
        AppDefinition AppDef = AppUtil.getCurrentAppDefinition();
        String kafkaBs = "#envVariable.kafkaBs#";
        kafkaBs = AppUtil.processHashVariable(kafkaBs, null, null, null, AppDef);
        configs.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaBs);
        //Producer properties
        configs.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        configs.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //Get SASL Config
        String kafkaSec = "#envVariable.kafkaSec#";
        String kafkaTsLoc = "#envVariable.kafkaTsLoc#";
        String kafkaTsPwd = "#envVariable.kafkaTsPwd#";
        String kafkaMec = "#envVariable.kafkaMec#";
        String kafkaJaas = "#envVariable.kafkaJaas#";
        kafkaSec = AppUtil.processHashVariable(kafkaSec, null, null, null, AppDef);
        kafkaTsLoc = AppUtil.processHashVariable(kafkaTsLoc, null, null, null, AppDef);
        kafkaTsPwd = AppUtil.processHashVariable(kafkaTsPwd, null, null, null, AppDef);
        kafkaMec = AppUtil.processHashVariable(kafkaMec, null, null, null, AppDef);
        kafkaJaas = AppUtil.processHashVariable(kafkaJaas, null, null, null, AppDef);
        //SASL
        configs.put("security.protocol", kafkaSec);
        configs.put("ssl.truststore.location", kafkaTsLoc);
        configs.put("ssl.truststore.password", kafkaTsPwd);
        configs.put("sasl.mechanism", kafkaMec);
        configs.put("sasl.jaas.config", kafkaJaas);  
        return configs;
    }
}
