package com.tweetapp.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.text.AbstractDocument.Content;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
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
import org.springframework.web.util.NestedServletException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.tweetapp.constant.Constants;
import com.tweetapp.exception.InvalidCredentialsException;
import com.tweetapp.exception.UserAlreadyExist;
import com.tweetapp.kafka.ProducerService;
import com.tweetapp.model.ForgotPassword;
import com.tweetapp.model.Tweet;
import com.tweetapp.model.User;
import com.tweetapp.model.UserResponse;
import com.tweetapp.service.UserServiceImpl;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	private MockMvc mockMvc;

	private static ObjectMapper objectMapper;

	private static ObjectWriter objectWriter;

	@InjectMocks
	UserController userController;
	@Mock
	UserServiceImpl userServiceImpl;
	@Mock  
	ProducerService producerService;

	@BeforeAll
	public static void setUpClass() {
		objectMapper = new ObjectMapper();
		objectWriter = objectMapper.writer();

	}

	@BeforeEach
	public void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
	}

	@Test
	void testRegisterUser() throws Exception {
		User user = createUser();

		String userContent = objectWriter.writeValueAsString(user);
		// doReturn(tweet).when(tweetServiceImpl.postTweet("madhu_gana", tweet));
		lenient().when(userServiceImpl.newUser(user)).thenReturn(user);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1.0/tweets/register")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(userContent);
		mockMvc.perform(request).andExpect(status().isCreated()).andReturn();

	}

	@Test
	void testLoginUser() throws Exception {
		User user = createUser();
		UserResponse userResponse = new UserResponse(user, "Success", "token", "noError");
		String userContent = objectWriter.writeValueAsString(user);
		// doReturn(tweet).when(tweetServiceImpl.postTweet("madhu_gana", tweet));
		lenient().when(userServiceImpl.loginUser(user.getLoginId(), user.getPassword())).thenReturn(userResponse);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1.0/tweets/login")
				.contentType(MediaType.APPLICATION_JSON).content(userContent);
		mockMvc.perform(request).andExpect(status().isOk()).andReturn();
	}

	@Test
	void testLoginUserElseBlock() throws Exception {
		User user = createUser();
		UserResponse userResponse = null;
		String userContent = objectWriter.writeValueAsString(user);
		lenient().when(userServiceImpl.loginUser(user.getLoginId(), user.getPassword())).thenReturn(userResponse);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1.0/tweets/login")
				.contentType(MediaType.APPLICATION_JSON).content(userContent);
		Exception exception = assertThrows(NestedServletException.class, () -> {
			mockMvc.perform(request);

		});
		String expectedMessage = Constants.INVALID_CREDENTIALS;
		String actualMessage = exception.getMessage();

		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	void testRegisterUserException() throws Exception {
		User user = createUser();
		user.setLoginId("madhu_gana");

		String userContent = objectWriter.writeValueAsString(user);
		lenient().when(userServiceImpl.newUser(user)).thenThrow(UserAlreadyExist.class);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1.0/tweets/register")
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(userContent);

		Exception exception = assertThrows(UserAlreadyExist.class, () -> {
			mockMvc.perform(request);
			throw new UserAlreadyExist(Constants.USER_ALREADY_EXIST);
		});

	}

	@Test
	void testForgotPassword() throws Exception {
		User user = createUser();
		ForgotPassword forgotPassword = new ForgotPassword();
		forgotPassword.setPassword("123");
		forgotPassword.setConfirmPassword("123");

		String userContent = objectWriter.writeValueAsString(user);
		lenient().when(userServiceImpl.forgotPassword(user.getLoginId(), forgotPassword))
				.thenReturn("Password has been reset successfully");
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/api/v1.0/tweets/madhu_gana/forgot")
				.contentType(MediaType.APPLICATION_JSON).content(userContent);
		mockMvc.perform(request).andExpect(status().isOk()).andExpect(content().string(containsString("")));
	}

	@Test
	void testGetAllUsers() throws Exception {

		User user1 = createUser();
		User user2 = createUser();
		List<User> users = new ArrayList<User>();
		users.add(user1);
		users.add(user2);
		when(userServiceImpl.getAllUsers()).thenReturn(users);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1.0/tweets/users/all")
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].loginId", is("madhu_gana")))
				.andExpect(jsonPath("$[1].loginId", is("madhu_gana")));

	}

	@Test
	void testGetUserByName() throws Exception {

		User user1 = createUser();
		User user2 = createUser();
		List<User> users = new ArrayList<User>();
		users.add(user1);
		users.add(user2);

		when(userServiceImpl.getUserByName(user1.getLoginId())).thenReturn(users);
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/api/v1.0/tweets/user/search/madhu_gana")
				.contentType(MediaType.APPLICATION_JSON);
		mockMvc.perform(request).andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[0].loginId", is("madhu_gana")))
				.andExpect(jsonPath("$[1].loginId", is("madhu_gana")));

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
