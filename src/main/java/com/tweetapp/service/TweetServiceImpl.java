package com.tweetapp.service;

import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tweetapp.constant.Constants;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.kafka.ConsumerService;
import com.tweetapp.kafka.ProducerService;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.UserRepository;

@Service
public class TweetServiceImpl implements TweetService {

	@Autowired
	TweetRepository tweetRepo;
	@Autowired
	UserRepository userRepo;
	@Autowired
	ProducerService producerService;
	@Autowired
	ConsumerService consumerService;
	
	private static Logger log = LogManager.getLogger(TweetServiceImpl.class);

	@Override
	public List<Tweet> getAllTweets() {
		log.info("Started Get All Tweets Service Method");
		producerService.sendMessage("Started Get All Tweets Service Method");
		return tweetRepo.findAll();
	}

	@Override
	public List<Tweet> getTweetsByUser(String username) {
		log.info("Started Get Tweets By User Service Method");
		producerService.sendMessage("Started Get Tweets By User Service Method");
		return tweetRepo.findByUserLoginId(username);
	}

	@Override
	public Tweet postTweet(String username, Tweet tweet) {
		log.info("Started Post Tweet Service Method");
		producerService.sendMessage("Started Post Tweet Service Method");
		User user = userRepo.findByLoginId(username);
		tweet.setUser(user);
		return tweetRepo.save(tweet);

	}

	@Override
	public Tweet updateTweet(String username, String id, Tweet tweet) {
		log.info("Started Update Tweet Service Method");
		producerService.sendMessage("Started Update Tweet Service Method");
		User user = userRepo.findByLoginId(username);
		tweet.setUser(user);
		return tweetRepo.save(tweet);

	}

	@Override
	public void delteTweet(String username, String id) {
		log.info("Started Delete Tweet Service Method");
		tweetRepo.deleteById(id);
	}

	@Override
	public void addLike(String username, String id) {
		log.info("Started Add Like Service Method");
		Optional<Tweet> tweet = tweetRepo.findById(id);
		if (tweet.isPresent()) {
			tweet.get().setLikes(tweet.get().getLikes() + 1);
			tweetRepo.save(tweet.get());
		}
	}
	
	@Override
	public Tweet replyTweet(String username, String id, Tweet reply) throws TweetNotFoundException {
		log.info("Started Reply Tweet Service Method");
		Optional<Tweet> originalTweet = tweetRepo.findById(id);

		if (originalTweet.isPresent()) {
			User user = userRepo.findByLoginId(username);
			reply.setUser(user);
			List<Tweet> replies = originalTweet.get().getReplies();
			replies.add(reply);
			tweetRepo.save(originalTweet.get());
		} else {

			throw new TweetNotFoundException(Constants.TWEET_NOT_FOUND);
		}
		return originalTweet.get();

	}

	@Override
	public Tweet postTweetKafka(String username, Tweet tweet) {
		log.info("Started Post Tweet By Using Kafka Service Method");
		User user = userRepo.findByLoginId(username);
		tweet.setUser(user);
		producerService.postTweet(tweet);
		return consumerService.consumeTweet(tweet);
	}

}
