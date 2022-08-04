package com.tweetapp.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.tweetapp.model.Tweet;
import com.tweetapp.repository.TweetRepository;

import lombok.extern.slf4j.Slf4j;

 
/**
 * @author MADHU
 *Consumer Service has 2 mehtods to consume 
 *one consumes from the Tweet objects
 *another consumes the logs
 */
@Service
@Slf4j
public class ConsumerService 
{
  @Autowired
  TweetRepository tweetRepository;
   
  @KafkaListener(topics = "TweetLogs", 
      groupId = "group_id1")
  public void consume(String message) {
	  log.info(message);
    
  }
 
  @KafkaListener(topics = "TweetMessage", 
      groupId = "group_id2",
      containerFactory = "userKafkaListenerContainerFactory")
  public Tweet consumeTweet(Tweet tweet) {
  	tweetRepository.save(tweet);
  	log.info("Consumed message "+tweet.toString());
  	
  	return tweet;
      
  }
}




