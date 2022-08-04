package com.tweetapp.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tweetapp.constant.Constants;
import com.tweetapp.exception.InvalidCredentialsException;
import com.tweetapp.exception.PasswordMismatchException;
import com.tweetapp.exception.UserAlreadyExist;
import com.tweetapp.exception.UserNotFoundException;
import com.tweetapp.kafka.ProducerService;
import com.tweetapp.model.ForgotPassword;
import com.tweetapp.model.User;
import com.tweetapp.model.UserResponse;
import com.tweetapp.service.UserService;

/**
 * @author MADHU
 *UserController Class implementation has all the end points related to User API of Tweet Application
 */ 
@RestController
@RequestMapping("/api/v1.0/tweets")

public class UserController {
	
	private static Logger log = LogManager.getLogger(UserController.class);
	
	@Autowired
	private UserService service;
	@Autowired
	private ProducerService producerService;
	

	@PostMapping("/register") // http://localhost:8080/api/v1.0/tweets/register
	ResponseEntity<User> registerUser(@RequestBody User user) throws UserAlreadyExist {
		log.info("Mapped to Register User Controller");
		producerService.sendMessage("Started Register User Controller");
		try {
			User user2=service.newUser(user);
			log.info("Exiting Register Controller");
			producerService.sendMessage("Completed Register User Controller");
	
			return new ResponseEntity<>(user2, HttpStatus.CREATED);

		} catch (UserAlreadyExist e) {
			log.error("Exception thrown-->USERNAME ALREADY TAKEN ");
			producerService.sendMessage("THROWING EXCEPTION");
			throw new UserAlreadyExist(Constants.USER_ALREADY_EXIST);
		}

	}

	@PostMapping("/login") // http://localhost:8080/api/v1.0/tweets/login
	ResponseEntity<UserResponse> loginUser(@RequestBody User user, HttpServletRequest request)
			throws InvalidCredentialsException, UnsupportedEncodingException {
		log.info("Mapped to Login Controller");
		producerService.sendMessage("Started login Controller");
		try {
			
			UserResponse status = service.loginUser(user.getLoginId(), user.getPassword());
			if (status != null) {
				request.getSession().setAttribute("user", user.getLoginId());
				log.info("Exiting Login Controller");
				producerService.sendMessage("Completed Login Controller");
				return new ResponseEntity<>(status, HttpStatus.OK);
				
			} else {
				log.info("Exception thrown--->The username or password is incorrect");
				throw new InvalidCredentialsException(Constants.INVALID_CREDENTIALS);
			}
		} catch (InvalidCredentialsException e) {
			log.info("Exception thrown--->The username or password is incorrect");
			throw new InvalidCredentialsException(Constants.INVALID_CREDENTIALS);

		}

	}

	@PostMapping("/{username}/forgot") // http://localhost:8080/api/v1.0/tweets/madhugana/forgot
	ResponseEntity<String> forgotPassword(@PathVariable String username, @RequestBody ForgotPassword forgotPassword)
			throws PasswordMismatchException, UserNotFoundException {
		log.info("Mapped to Forgot Password Controller");
		producerService.sendMessage("Started Forgot Password Controller");
		String message=service.forgotPassword(username, forgotPassword);
		log.info("Exiting Forgot Password Controller");
		producerService.sendMessage("Completed Forgot Password Controller");
		return new ResponseEntity<>(message, HttpStatus.OK);
	}

	@GetMapping("/users/all")
	ResponseEntity<List<User>> getAllUsers()// http://localhost:8080/api/v1.0/tweets/users/all
	{	log.info("Mapped to Get All Users Controller");
	producerService.sendMessage("Started Get All Users Controller");
		List<User> users = service.getAllUsers();
		log.info("Exiting Get All Users Controller");
		producerService.sendMessage("Completed Get All Users Controller");
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@GetMapping("/user/search/{username}") // http://localhost:8080/api/v1.0/tweets/user/search/madhu
	ResponseEntity<List<User>> getUserByName(@PathVariable String username) throws UserNotFoundException {
		log.info("Mapped to Search by UserName Controller");
		producerService.sendMessage("Started Get User By Name Controller");
		log.debug("Searching for users with name: "+username);
		List<User> users = service.getUserByName(username);
		log.info("Exiting Get User By Name Controller");
		producerService.sendMessage("Completed Get User By Name Controller");
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

}
