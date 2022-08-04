package com.tweetapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tweetapp.kafka.ConsumerService;
import com.tweetapp.kafka.ProducerService;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.service.TweetServiceImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TweetControllerTest {

	private MockMvc mockMvc;

	private static ObjectMapper objectMapper;

	private static ObjectWriter objectWriter;
	@InjectMocks
	TweetController tweetController;
	@Mock
	TweetServiceImpl tweetServiceImpl;
	@Mock
	ProducerService producerService;
	@Mock
	ConsumerService consumerService;

	@BeforeAll
	public static void setUpClass() {
		objectMapper = new ObjectMapper();
		objectWriter = objectMapper.writer();

	}

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(tweetController).build();
	}

	@Test
	void testGetAllTweets() throws Exception {
		List<Tweet> tweets = createTweetList();
		// String tweetsContent = objectWriter.writeValueAsString(tweets);
		when(tweetServiceImpl.getAllTweets()).thenReturn(tweets);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1.0/tweets/all")
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].tweetId", is("1"))).andExpect(jsonPath("$[1].tweetId", is("1")));

	}

	@Test
	void testGetTweetsByUser() throws Exception {
		List<Tweet> tweets = createTweetList();
		User user = tweets.get(0).getUser();
		when(tweetServiceImpl.getTweetsByUser(user.getLoginId())).thenReturn(tweets);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1.0/tweets/madhu_gana")
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].tweetId", is("1"))).andExpect(jsonPath("$[1].tweetId", is("1")));

	}

	@Test
	void testPostTweet() throws Exception {
		Tweet tweet = createTweet();
		String tweetContent = objectWriter.writeValueAsString(tweet);
		// doReturn(tweet).when(tweetServiceImpl.postTweet("madhu_gana", tweet));
		lenient().when(tweetServiceImpl.postTweet("madhu_gana", tweet)).thenReturn(tweet);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1.0/tweets/madhu_gana/add")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(tweetContent);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();

	}

	@Test
	void testPostTweetKafka() throws Exception {
		Tweet tweet = createTweet();
		String tweetContent = objectWriter.writeValueAsString(tweet);

		lenient().when(tweetServiceImpl.postTweet("madhu_gana", tweet)).thenReturn(tweet);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1.0/tweets/madhugana/addkafka")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(tweetContent);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();

	}

//
	@Test
	void testUpdateTweet() throws Exception {
		Tweet tweet = createTweet();
		String tweetContent = objectWriter.writeValueAsString(tweet);
		lenient().when(tweetServiceImpl.updateTweet("madhu_gana", "1", tweet)).thenReturn(tweet);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/api/v1.0/tweets/madhugana/update/1")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(tweetContent);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();

	}

	@Test
	void testDeleteTweet() throws Exception {

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.delete("/api/v1.0/tweets/madhu_gana/delete/2")
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();
	}

	@Test
	void testDropLike() throws Exception {
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.put("/api/v1.0/tweets/madhugana/like/0")
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();
	}

	@Test
	void testReplyTweet() throws Exception {
		Tweet tweet = createTweet();
		String tweetContent = objectWriter.writeValueAsString(tweet);
		// doReturn(tweet).when(tweetServiceImpl.postTweet("madhu_gana", tweet));
		lenient().when(tweetServiceImpl.replyTweet("madhu_gana", "1", tweet)).thenReturn(tweet);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1.0/tweets/madhugana/reply/0")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(tweetContent);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();

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

}
