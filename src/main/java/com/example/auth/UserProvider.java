package com.example.auth;

public interface UserProvider {
	User findByApiKey(String apiKey);
}
