package com.tweetapp.kafka;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.tweetapp.model.Tweet;
import com.tweetapp.repository.TweetRepository;

@Service

public class ProducerService {

	public static final String topic = "TweetMessage";
	public static final String tweetMessage = "TweetLogs";
	private static final Logger log = LogManager.getLogger(ProducerService.class);


	
	
	
	//1. General topic with a string payload
	   
	 
	    private String topicName = "TweetLogs";
	   
	  @Autowired
	    private KafkaTemplate<String, String> kafkaTemplate;
	 
	  //2. Topic with Tweet object payload
	     
	
	    private String userTopicName="TweetMessage";
	     
	    @Autowired
	    private KafkaTemplate<String, Tweet> userKafkaTemplate;
	   
	  public void sendMessage(String message) 
	  {
		  log.info("Sending Message to the consumer.....");  
	   kafkaTemplate.send(topicName, message);
	     
	    
	  }
	   
	  public void postTweet(Tweet tweet) 
	  {
		  log.info("Sending Tweet to the consumer.....");  
	  userKafkaTemplate.send(userTopicName, tweet);
	     
	   
	  }
	}


