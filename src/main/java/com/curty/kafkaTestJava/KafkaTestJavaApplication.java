package com.curty.kafkaTestJava;

import com.curty.kafkaTestJava.service.StartMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@SpringBootApplication
public class KafkaTestJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(KafkaTestJavaApplication.class, args);
	}

	@Autowired
	private StartMessage startMessage;

	@Scheduled(cron = "0 38 16 1/1 * ?")
	private void send(){
		startMessage.sendMessage();
	}

}
