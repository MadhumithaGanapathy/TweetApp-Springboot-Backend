package com.tweetapp.service;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.NestedServletException;

import com.tweetapp.constant.Constants;
import com.tweetapp.exception.InvalidCredentialsException;
import com.tweetapp.exception.PasswordMismatchException;
import com.tweetapp.exception.UserAlreadyExist;
import com.tweetapp.exception.UserNotFoundException;
import com.tweetapp.kafka.ProducerService;
import com.tweetapp.model.ForgotPassword;
import com.tweetapp.model.User;
import com.tweetapp.model.UserResponse;
import com.tweetapp.repository.UserRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	@InjectMocks
	UserServiceImpl userServiceImpl;
	@Mock
	UserRepository userRepository;
	@Mock
	TokenService tokenService;
	@Mock
	ProducerService producerService;

	@Test
	void testRegisterUser() {
		User user = createUser();
		lenient().when(userRepository.save(null)).thenReturn(user);
		assertEquals("User Added Successfully", userServiceImpl.registerUser(user));
	}

	@Test
	void testGetAllUsers() {
		List<User> users = createUsersList();

		when(userRepository.findAll()).thenReturn(users);
		assertEquals(users, userServiceImpl.getAllUsers());

	}

	@Test
	void testGetUserByName() throws UserNotFoundException {
		List<User> users = createUsersList();
		when(userRepository.findByLoginIdContaining("madhu")).thenReturn(users);
		assertEquals(users, userServiceImpl.getUserByName("madhu"));

	}

	@Test
	void testGetUserByNameExceptionBlock() throws UserNotFoundException {
		List<User> users = null;
		when(userRepository.findByLoginIdContaining("madhu")).thenReturn(users);
		Exception exception = assertThrows(UserNotFoundException.class, () -> {
			userServiceImpl.getUserByName("madhu");

		});
		String expectedMessage = Constants.USER_NOT_FOUND;
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	void testNewUser() throws UserAlreadyExist {
		User user = createUser();
		user.setLoginId(null);
		lenient().when(userRepository.save(user)).thenReturn(user);
		assertEquals(user, userServiceImpl.newUser(user));
	}

	@Test
	void testNewUserExceptionBlock() throws UserAlreadyExist {
		User user = createUser();
		lenient().when(userRepository.findByLoginId("madhu_gana")).thenReturn(user);

		Exception exception = assertThrows(UserAlreadyExist.class, () -> {
			userServiceImpl.newUser(user);

		});
		String expectedMessage = Constants.USER_ALREADY_EXIST;
		String actualMessage = exception.getMessage();
		assertTrue(actualMessage.contains(expectedMessage));

	}

	@Test
	void testLoginUser() throws UnsupportedEncodingException {
		User user = createUser();
		UserResponse userResponse = new UserResponse();
		userResponse.setUser(user);
		userResponse.setLoginStatus("Success");
		userResponse.setErrorMessage("No error");
		userResponse.setToken("token");
		lenient().when(tokenService.createToken(user.getId())).thenReturn("token");
		when(userRepository.findByLoginId("madhu_gana")).thenReturn(user);
		assertEquals(userResponse.getToken(), userServiceImpl.loginUser("madhu_gana", "123").getToken());
	}

	@Test
	void testLoginUserElseBlock() throws UnsupportedEncodingException {
		User user = createUser();
		when(userRepository.findByLoginId("madhu_gana")).thenReturn(user);

		userServiceImpl.loginUser("madhu_gana", "789");

	}

	@Test
	void testLoginUserException() throws UnsupportedEncodingException {
		User user = null;
		when(userRepository.findByLoginId("madhu_gana")).thenReturn(user);

		userServiceImpl.loginUser("madhu_gana", "789");

	}

	@Test
	void testForgotPassword() throws PasswordMismatchException, UserNotFoundException {
		User user = createUser();
		ForgotPassword forgotPassword = new ForgotPassword();
		forgotPassword.setPassword("456");
		forgotPassword.setConfirmPassword("456");
		when(userRepository.findByLoginId("madhu_gana")).thenReturn(user);
		assertEquals(Constants.PASSWORD_CHANGED, userServiceImpl.forgotPassword("madhu_gana", forgotPassword));
	}

	@Test
	void testForgotPasswordElseBlock() throws PasswordMismatchException, UserNotFoundException {
		User user = createUser();
		ForgotPassword forgotPassword = new ForgotPassword();
		forgotPassword.setPassword("123");
		forgotPassword.setConfirmPassword("456");
		when(userRepository.findByLoginId("madhu_gana")).thenReturn(user);
		assertEquals(Constants.PASSWORD_MISMATCH, userServiceImpl.forgotPassword("madhu_gana", forgotPassword));

	}

	@Test
	void testForgotPasswordExceptionBlock() throws PasswordMismatchException, UserNotFoundException {
		User user = createUser();
		ForgotPassword forgotPassword = new ForgotPassword();
		forgotPassword.setPassword("123");
		forgotPassword.setConfirmPassword("456");
		when(userRepository.findByLoginId("madhu_gana")).thenReturn(null);
		assertEquals(Constants.USER_NOT_FOUND, userServiceImpl.forgotPassword("madhu_gana", forgotPassword));

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

	private List<User> createUsersList() {
		User user1 = createUser();
		User user2 = createUser();
		List<User> users = new ArrayList<>();
		users.add(user2);
		users.add(user1);
		return users;
	}
}
