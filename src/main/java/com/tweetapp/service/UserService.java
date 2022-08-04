package com.tweetapp.service;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.tweetapp.controller.UserController;
import com.tweetapp.exception.PasswordMismatchException;
import com.tweetapp.exception.UserAlreadyExist;
import com.tweetapp.exception.UserNotFoundException;
import com.tweetapp.model.ForgotPassword;
import com.tweetapp.model.User;
import com.tweetapp.model.UserResponse;

public interface UserService {

	String registerUser(User user);

	List<User> getAllUsers();

	List<User> getUserByName(String username) throws UserNotFoundException;

	User newUser(User user) throws UserAlreadyExist;

	UserResponse loginUser(String loginId, String password) throws UnsupportedEncodingException;

	String forgotPassword(String username, ForgotPassword forgotPassword) throws PasswordMismatchException, UserNotFoundException;

}