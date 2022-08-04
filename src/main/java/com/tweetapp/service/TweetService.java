package com.tweetapp.service;

import java.util.List;

import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.model.Tweet;

public interface TweetService {

	List<Tweet> getAllTweets();

	List<Tweet> getTweetsByUser(String username);

	Tweet postTweet(String username, Tweet tweet);

	Tweet updateTweet(String username, String id, Tweet tweet);

	void delteTweet(String username, String  id);

	void addLike(String username, String  id);

	Tweet replyTweet(String username, String id, Tweet reply) throws TweetNotFoundException;

	Tweet postTweetKafka(String username, Tweet tweet);

}