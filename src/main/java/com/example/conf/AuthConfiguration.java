package com.example.conf;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthConfiguration {
	@Valid
	@NotNull
	@JsonProperty
	private String secret;

	@Valid
	@NotNull
	@JsonProperty
	private int expireMins;

	public AuthConfiguration() {
	}

	public String getSecret() {
		return secret;
	}

	public int getExpireMins() {
		return expireMins;
	}
}
