package com.tweetapp.controller;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.kafka.ProducerService;
import com.tweetapp.model.Tweet;
import com.tweetapp.service.TweetService;

/**
 * @author MADHU
 * TweetController class has all the end points related to tweet in 
 *
 */
@RestController
@RequestMapping("/api/v1.0/tweets")
public class TweetController {

	private static Logger log = LogManager.getLogger(TweetController.class);
	@Autowired
	TweetService tweetService;
	@Autowired
	ProducerService producerService;

	@GetMapping("/all") // http://localhost:8086/api/v1.0/tweets/all
	public ResponseEntity<List<Tweet>> getAllTweets() {
		log.info("Mapped to Get All Tweets Controller");
		producerService.sendMessage("Started Get All Tweets Controller");
		List<Tweet> tweets = tweetService.getAllTweets();
		producerService.sendMessage("Executed Successfully");
		log.info("Exiting Get All Tweets Controller");
		producerService.sendMessage("Completed Get All Tweets Controller");
		return new ResponseEntity<>(tweets, HttpStatus.OK);
	}

	@GetMapping("/{username}") // http://localhost:8086/api/v1.0/tweets/madhugana
	public ResponseEntity<List<Tweet>> getTweetsByUser(@PathVariable String username) {
		log.info("Mapped to Get Tweets By User Controller for User:"+username);
		producerService.sendMessage("Started Get Tweets By User Controller");
		List<Tweet> userTweets = tweetService.getTweetsByUser(username);
		log.info("Exiting Get Tweets By User Controller");
		producerService.sendMessage("Completed Get Tweets By User Controller");
		return new ResponseEntity<>(userTweets, HttpStatus.OK);
	}

	@PostMapping("/{username}/add") // http://localhost:8086/api/v1.0/tweets/madhugana/add
	public ResponseEntity<Tweet> postTweet(@PathVariable String username, @RequestBody Tweet tweet) {
		log.info("Mapped to post tweet controller --> Posting Tweet by user"+username);
		producerService.sendMessage("Started Post Tweet Controller");
		Tweet tweet2=tweetService.postTweet(username, tweet);
		log.info("Exiting Post Tweet Controller");
		producerService.sendMessage("Completed Post Tweet Controller");
		return new ResponseEntity<>(tweet2, HttpStatus.OK);
	}

	// KAFKA IMPLEMENTATION--> POSTING A TWEET BY USING KAFKA SERIALIZATION CONCEPT

	@PostMapping("/{username}/addkafka") // http://localhost:8086/api/v1.0/tweets/madhu_gana/addkafka
	public ResponseEntity<Tweet> postTweetKafka(@PathVariable String username, @RequestBody Tweet tweet) {
		log.info("Mapped to Post Tweet By User--using  Kafka service ");
		producerService.sendMessage("Started Post Tweet By Kafa Controller");
		Tweet tweet2=tweetService.postTweetKafka(username, tweet);
		log.info("Exiting Post Tweet Kafka Controller");
		producerService.sendMessage("Completed Post Tweet By Kafa Controller");
		return new ResponseEntity<>(tweet2, HttpStatus.OK);
	}

	@PutMapping("/{username}/update/{id}") // http://localhost:8086/api/v1.0/tweets/madhu_gana/update/1
	public ResponseEntity<Tweet> updateTweet(@PathVariable String username, @PathVariable String id,
			@RequestBody Tweet tweet) {
		log.info("Mapped to Update Tweet Controller");
		producerService.sendMessage("Started Update Tweet Controller");
		Tweet tweet2=tweetService.updateTweet(username, id, tweet);
		log.info("Exiting Update Controller");
		producerService.sendMessage("Completed Update Tweet Controller");
		return new ResponseEntity<>(tweet2, HttpStatus.OK);
	}

	@DeleteMapping("/{username}/delete/{id}") // http://localhost:8086/api/v1.0/tweets/madhu_gana/delete/2
	public ResponseEntity<HttpStatus> deleteTweet(@PathVariable String username, @PathVariable String id)

	{	log.info("Mapped To Delete Tweet Controller");
	    producerService.sendMessage("Started Delete Tweet Controller");
		tweetService.delteTweet(username, id);
		log.info("Exiting Delete Tweet controller");
		producerService.sendMessage("Completed Delete Tweet Controller");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/{username}/like/{id}") // http://localhost:8086/api/v1.0/tweets/madhu_gana/like/0
	public ResponseEntity<HttpStatus> dropLike(@PathVariable String username, @PathVariable String id) {
		log.info("Mapped To Drop Like Controller");
		producerService.sendMessage("Started Drop Likes Controller");
		tweetService.addLike(username, id);
		log.info("Exiting Drop Like Controller");
		producerService.sendMessage("Completed Drop Likes Controller");
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/{username}/reply/{id}") //// http://localhost:8086/api/v1.0/tweets/madhu_gana/reply/0
	public ResponseEntity<Tweet> replyTweet(@PathVariable String username, @PathVariable String id,
			@RequestBody Tweet reply) throws TweetNotFoundException {
		log.info("Mapped To Reply Tweet Controller");
		producerService.sendMessage("Started Reply Tweets Controller");
		Tweet tweet=tweetService.replyTweet(username, id, reply);
		log.info("Exiting Reply Tweet Controller");
		producerService.sendMessage("Completed Reply Tweets Controller");
		return new ResponseEntity<>(tweet, HttpStatus.OK);
	}

}
