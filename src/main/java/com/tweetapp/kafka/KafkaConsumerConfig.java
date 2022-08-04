package com.tweetapp.kafka;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.tweetapp.model.Tweet;

 
/**
 * @author MADHU
 *Kafka Consumer Config Class--> DeSerializes the recieved String to Tweet Object 
 */
@Configuration
public class KafkaConsumerConfig 
{

  private String bootstrapAddress="localhost:9092";
 
 
  private String groupId="group_id1";

  private String userGroupId="group_id2";
 
  // 1. Consume string data from Kafka
 
  @Bean
  public ConsumerFactory<String, String> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, 
        StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, 
        StringDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    return new DefaultKafkaConsumerFactory<>(props);
  }
 
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, String> 
            kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, String> factory 
      = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    return factory;
  }
 
  // 2. Consume user objects from Kafka
 
  public ConsumerFactory<String, Tweet> userConsumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, userGroupId);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    return new DefaultKafkaConsumerFactory<>(props, 
        new StringDeserializer(), 
        new JsonDeserializer<>(Tweet.class));
  }
 
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Tweet> 
            userKafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Tweet> factory 
      = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(userConsumerFactory());
    return factory;
  }
}

