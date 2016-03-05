package com.example.auth;

public class User {
	private final String apiKey;
	private String username;
	private String fullName;
	private String token;

	public User(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
