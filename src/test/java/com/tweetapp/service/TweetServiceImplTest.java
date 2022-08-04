package com.tweetapp.service;

import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.NestedServletException;

import com.tweetapp.constant.Constants;
import com.tweetapp.exception.TweetNotFoundException;
import com.tweetapp.kafka.ConsumerService;
import com.tweetapp.kafka.ProducerService;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.repository.TweetRepository;
import com.tweetapp.repository.UserRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TweetServiceImplTest {

	@InjectMocks
	TweetServiceImpl tweetServiceImpl;
	@Mock
	TweetRepository tweetRepository;
	@Mock
	UserRepository userRepository;
	@Mock
	ProducerService producerService;
	@Mock
	ConsumerService consumerService;

	@Test
	void testGetAllTweets() {
		List<Tweet> tweets = createTweetList();
		when(tweetRepository.findAll()).thenReturn(tweets);
		assertEquals(tweets, tweetServiceImpl.getAllTweets());
	}

	@Test
	void testGetTweetsByUser() {
		List<Tweet> tweets = createTweetList();
		when(tweetRepository.findByUserLoginId(tweets.get(0).getUser().getLoginId())).thenReturn(tweets);
		assertEquals(tweets, tweetServiceImpl.getTweetsByUser(tweets.get(0).getUser().getLoginId()));
	}

	@Test
	void testPostTweet() {
		Tweet tweet = createTweet();
		User user = createUser();

		when(userRepository.findByLoginId("madhu_gana")).thenReturn(user);
		tweet.setUser(user);
		when(tweetRepository.save(tweet)).thenReturn(tweet);
		assertEquals(tweet, tweetServiceImpl.postTweet("madhu_gana", tweet));
	}

	@Test
	void testUpdateTweet() {
		Tweet tweet = createTweet();
		User user = createUser();

		when(userRepository.findByLoginId("madhu_gana")).thenReturn(user);
		tweet.setUser(user);
		when(tweetRepository.save(tweet)).thenReturn(tweet);
		assertEquals(tweet, tweetServiceImpl.updateTweet("madhu_gana", "1", tweet));
	}

	@Test
	void testDelteTweet() {
		Tweet tweet = createTweet();

		tweetServiceImpl.delteTweet("madhu_gana", tweet.getTweetId());

		verify(tweetRepository, times(1)).deleteById(tweet.getTweetId());
	}

	@Test
	void testAddLike() {

		Tweet tweet = createTweet();
		Optional<Tweet> optTweet = Optional.of(tweet);
		when(tweetRepository.findById("1")).thenReturn(optTweet);

		assertTrue(optTweet.isPresent());

		tweetServiceImpl.addLike("madhu_gana", tweet.getTweetId());
		verify(tweetRepository, times(1)).save(tweet);

	}

	@Test
	void testAddLikeElseBlock() {

		Tweet tweet = createTweet();
		Optional<Tweet> optTweet = Optional.empty();
		when(tweetRepository.findById("1")).thenReturn(optTweet);

		tweetServiceImpl.addLike("madhu_gana", tweet.getTweetId());
		verify(tweetRepository, times(0)).save(tweet);

	}

	@Test
	void testReplyTweet() throws TweetNotFoundException {
		Tweet tweet = createTweet();
		Optional<Tweet> optTweet = Optional.of(tweet);
		when(tweetRepository.findById("1")).thenReturn(optTweet);
		tweetServiceImpl.replyTweet("madhu_gana", "1", optTweet.get());
		verify(tweetRepository, times(1)).save(tweet);

	}

	@Test
	void testReplyTweetElseBlock() throws TweetNotFoundException {
		Tweet tweet = createTweet();
		Optional<Tweet> optTweet = Optional.empty();
		when(tweetRepository.findById("1")).thenReturn(optTweet);

		Exception exception = assertThrows(TweetNotFoundException.class, () -> {

			tweetServiceImpl.replyTweet("madhu_gana", "1", null);

		});
		String expectedMessage = Constants.TWEET_NOT_FOUND;
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	void testPostTweetKafka() {
		Tweet tweet = createTweet();
		User user = createUser();
		when(userRepository.findByLoginId("madhu_gana")).thenReturn(user);
		tweet.setUser(user);
		when(consumerService.consumeTweet(tweet)).thenReturn(tweet);
		assertEquals(tweetServiceImpl.postTweetKafka("madhu_gana", tweet), tweet);

	}

	private Tweet createTweet() {
		Date date1 = new Date(2017, 10, 1);
		User user = new User();
		// user.setId("id");
		List<Tweet> replies = new ArrayList<Tweet>();
		user.setFirstName("madhu");
		user.setLastName("gana");
		user.setContactNumber("9487925396");
		user.setEmail("madhusparkle99@gmail.com");
		user.setConfirmPasword("123");
		user.setPassword("123");
		user.setLoginId("madhu_gana");

		Tweet tweet = new Tweet();
		tweet.setLikes(10);
		tweet.setTweetId("1");
		tweet.setTweetMsg("Test Tweet");
		tweet.setTweetTag("Tweet tag");
		tweet.setPostTime(date1);
		tweet.setUser(user);
		tweet.setReplies(replies);
		return tweet;
	}

	private List<Tweet> createTweetList() {
		Tweet tweet1 = createTweet();
		Tweet tweet2 = createTweet();
		List<Tweet> tweets = new ArrayList<Tweet>();
		tweets.add(tweet1);
		tweets.add(tweet2);
		return tweets;
	}

	private User createUser() {
		User user = new User();
		user.setFirstName("madhu");
		user.setLastName("gana");
		user.setContactNumber("9487925396");
		user.setEmail("madhusparkle99@gmail.com");
		user.setConfirmPasword("123");
		user.setPassword("123");
		user.setLoginId("madhu_gana");
		return user;
	}

}
