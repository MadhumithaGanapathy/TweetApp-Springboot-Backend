package com.tweetapp.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tweetapp.constant.Constants;
import com.tweetapp.controller.UserController;
import com.tweetapp.exception.InvalidCredentialsException;
import com.tweetapp.exception.PasswordMismatchException;
import com.tweetapp.exception.UserAlreadyExist;
import com.tweetapp.exception.UserNotFoundException;
import com.tweetapp.kafka.ProducerService;
import com.tweetapp.model.ForgotPassword;
import com.tweetapp.model.User;
import com.tweetapp.model.UserResponse;
import com.tweetapp.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service

public class UserServiceImpl implements UserService {
	private static Logger log = LogManager.getLogger(UserServiceImpl.class);
	@Autowired
	private UserRepository repo;
	@Autowired
	private TokenService tokenService;
	@Autowired
	private ProducerService producerService;

	public String registerUser(User user) {

		log.info("Started Register User Method");
		//producerService.sendMessage("Started Register User Method");
		repo.save(user);
		return "User Added Successfully";

	}

	@Override
	public List<User> getAllUsers() {
		log.info("Started Get All Users Method");
		//producerService.sendMessage("Started Get All Users Method");
		return repo.findAll();
	}

	@Override
	public List<User> getUserByName(String username) throws UserNotFoundException {
		log.info("Started Get User By Name Method");
		//producerService.sendMessage("Started Get User By Name Method");
		if (repo.findByLoginIdContaining(username) == null) {
			log.info("Exception Thrown---> The user with the given name does not Exist");
			throw new UserNotFoundException(Constants.USER_NOT_FOUND);
		}
		return repo.findByLoginIdContaining(username);
	}

	@Override
	public User newUser(User user) throws UserAlreadyExist {
		if (repo.findByLoginId(user.getLoginId()) != null) {
			log.info("Exception Thrown---> The username is Already Taken");
			throw new UserAlreadyExist(Constants.USER_ALREADY_EXIST);
		}

		return repo.save(user);

	}

	@Override
	public UserResponse loginUser(String loginId, String password) throws UnsupportedEncodingException {
		log.info("Started Login User Service Method");
		//producerService.sendMessage("Started Login User Service Method");
		UserResponse userResponse = new UserResponse();
		try {
			User user = repo.findByLoginId(loginId);
			if (user != null) {
				if (user.getPassword().equals(password)) {
					log.info("Login Successful");

					userResponse.setUser(user);
					userResponse.setLoginStatus(Constants.SUCCESS);
					userResponse.setErrorMessage(null);
					userResponse.setToken(tokenService.createToken(user.getId()));
				} else {
					log.info("Exception Thrown---> The password was incorrect");

					throw new InvalidCredentialsException(Constants.INCORRECT_PASSWORD);

				}
			} else {
				log.info("Exception Thrown---> The username was incorrect");
				throw new InvalidCredentialsException(Constants.INCORRECT_USERNNAME);
			}
		} catch (InvalidCredentialsException e) {
			userResponse.setErrorMessage(Constants.INVALID_CREDENTIALS);
			userResponse.setLoginStatus(Constants.FAIL);

		}

		return userResponse;
	}

	@Override
	public String forgotPassword(String username, ForgotPassword forgotPassword)
			throws PasswordMismatchException, UserNotFoundException {

		log.info("Started Forgot password Service Method");
		//producerService.sendMessage("Started Forgot password Service Method");
		try {
			User user = repo.findByLoginId(username);
			if (user != null) {
				if (forgotPassword.getPassword().equals(forgotPassword.getConfirmPassword())) {
					user.setPassword(forgotPassword.getPassword());
					user.setConfirmPasword(forgotPassword.getConfirmPassword());
					repo.save(user);
					log.info("Password has been changed successfuly");
					producerService.sendMessage("Password has been changed successfuly");

					return Constants.PASSWORD_CHANGED;
				} else {
					log.info("Exception Thrown---> The passwords are not matched");
					throw new PasswordMismatchException(Constants.PASSWORD_MISMATCH);
				}
			} else {
				log.info("Exception Thrown---> The user does not exist");
				throw new UserNotFoundException(Constants.USER_NOT_FOUND);
			}
		} catch (Exception e) {
			return e.getMessage();

		}

	}

}
