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

5. Consuming Messages

5.1. Consumer Configuration

For consuming messages, I configured a ConsumerFactory and a KafkaListenerContainerFactory. Once these beans are available in the Spring bean factory, POJO based consumers can be configured using @KafkaListener annotation.

@EnableKafka annotation is required on the configuration class to enable detection of @KafkaListener annotation on spring managed beans.

5.2. Consuming Messages

@KafkaListener(topics = "topicName", groupId = "group1")
public void listenGroup(@Payload String message) {
    System.out.println("Received Message in group group1: " + message);
}

Multiple listeners can be implemented for a topic, each with a different group Id. Furthermore, one consumer can listen to messages from various topics:

@KafkaListener(topics = "topic1, topic2", groupId = "group1")

Spring also supports retrieval of one or more message headers using the @Header annotation in the listener:

@KafkaListener(topics = "topicName")
public void listenWithHeaders(
  @Payload String message, 
  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
      System.out.println(
        "Received Message: " + message"
        + "from partition: " + partition);
}

5.3. Consuming Messages from a Specific Partition

As you may have noticed, I had created the topic with only one partition. However, for a topic with multiple partitions, a @KafkaListener can explicitly subscribe to a particular partition of a topic with an initial offset:

@KafkaListener(
  topicPartitions = @TopicPartition(topic = "topicName",
  partitionOffsets = {
    @PartitionOffset(partition = "0", initialOffset = "0"), 
    @PartitionOffset(partition = "3", initialOffset = "0")}),
  containerFactory = "partitionsKafkaListenerContainerFactory")
public void listenToPartition(
  @Payload String message, 
  @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
      System.out.println(
        "Received Message: " + message"
        + "from partition: " + partition);
}
Since the initial offset has been sent to 0 in this listener, all the previously consumed messages from partitions 0 and three will be re-consumed every time this listener is initialized. If setting the offset is not required, we can use the partitions property of @TopicPartition annotation to set only the partitions without the offset:

@KafkaListener(topicPartitions 
  = @TopicPartition(topic = "topicName", partitions = { "0", "1" }))

5.4. Adding Message Filter for Listeners

Listeners can be configured to consume specific types of messages by adding a custom filter. This can be done by setting a RecordFilterStrategy to the KafkaListenerContainerFactory:

@Bean
public ConcurrentKafkaListenerContainerFactory<String, String>
  filterKafkaListenerContainerFactory() {

    ConcurrentKafkaListenerContainerFactory<String, String> factory =
      new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    factory.setRecordFilterStrategy(
      record -> record.value().contains("World"));
    return factory;
}

A listener can then be configured to use this container factory:

@KafkaListener(
  topics = "topicName", 
  containerFactory = "filterKafkaListenerContainerFactory")
public void listenWithFilter(String message) {
    System.out.println("Received Message in filtered listener: " + message);
}

In this listener, all the messages matching the filter will be discarded.

