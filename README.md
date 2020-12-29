# kafkaTestJava
I created a simple application for publishing and consuming messages using a Java client with Kafka. Just to understand how Apache Kafka works with Spring.  Apache Kafka is a powerful, distributed, fault-tolerant stream processing system.

1. Overview

Apache Kafka is a distributed and fault-tolerant stream processing system.

In this code, I tried to understand the Spring support for Kafka and the level of abstractions it provides over native Kafka Java client APIs.

Spring Kafka brings the simple and typical Spring template programming model with a KafkaTemplate and Message-driven POJOs via @KafkaListener annotation.

2. Installation and Setup

Download and install Kafka.
We also need to add the spring-kafka dependency to our pom.xml:

<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <version>last stable</version>
</dependency>
Our example application will be a Spring Boot application.

This code assumes that the server is started using the default configuration and no server ports are changed.

3. Configuring Topics

Initially, run your command line tools (Like Windows Terminal) to create the topic in Kafka for Windows such as:

$ kafka-topics.bat --bootstrap-server localhost:9092 --topic test_java --create --partitions 3 --replication-factor 1

Before run de previously command, please, start Zookeeper and Kafka, in this order.


4. Producing Messages

To create messages, first, I needed to configure a ProducerFactory which sets the strategy for creating Kafka Producer instances.

Then I needed a KafkaTemplate which wraps a Producer instance and provides convenient methods for sending messages to Kafka topics.

Producer instances are thread-safe and hence using a single instance throughout an application context will give higher performance. Consequently, KakfaTemplate instances are also thread-safe and the use of one instance is recommended.

4.2. Publishing Messages

After I could send messages using the KafkaTemplate class.

The send API returns a ListenableFuture object. If we want to block the sending thread and get the result about the sent message, we can call the get API of the ListenableFuture object. The thread will wait for the result, but it will slow down the producer.

Kafka is a fast stream processing platform. So it's a better idea to handle the results asynchronously so that the subsequent messages do not wait for the result of the previous message. I think could do this through a callback.
