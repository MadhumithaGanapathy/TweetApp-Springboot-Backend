package com.tweetapp.model;

import org.springframework.stereotype.Component;

@Component
public class UserResponse {

	private User user;
	private String loginStatus;
	private String token;
	private String ErrorMessage;
	
	public UserResponse() {
		
	}

	public UserResponse(User user, String loginStatus, String token, String errorMessage) {
		super();
		this.user = user;
		this.loginStatus = loginStatus;
		this.token = token;
		ErrorMessage = errorMessage;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getErrorMessage() {
		return ErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		ErrorMessage = errorMessage;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(String loginStatus) {
		this.loginStatus = loginStatus;
	}

}