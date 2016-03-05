package com.example.auth;

public class PrefixedToken {
	private String prefix;
	private String token;

	public PrefixedToken() {
	}

	public PrefixedToken(String prefix, String token) {
		this.prefix = prefix;
		this.token = token;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
