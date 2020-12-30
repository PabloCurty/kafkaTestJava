package com.curty.kafkaTestJava.service;

import com.curty.kafkaTestJava.publishingMessages.KafkaTemplates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StartMessage {
    @Autowired
    private KafkaTemplates kafkaTemplates;

    private String topic;

    public void sendMessage(){
        String msg = "test number 1";
        topic = "test_java";
        kafkaTemplates.sendMessage(topic, msg);
    }

}
