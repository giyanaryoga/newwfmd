/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package id.co.telkom.wfm.plugin.kafka;

import java.util.Properties;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
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
}
