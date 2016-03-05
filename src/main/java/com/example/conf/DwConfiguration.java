package com.example.conf;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class DwConfiguration extends Configuration {
	@Valid
	@NotNull
	private DataSourceFactory database = new DataSourceFactory();

	@Valid
	@NotNull
	@JsonProperty
	private AuthConfiguration auth;

	@Valid
	@JsonProperty
	private String hbm2ddlAuto;

	public DwConfiguration() {
	}

	public DataSourceFactory getDatabase() {
		return this.database;
	}

	public AuthConfiguration getAuth() {
		return auth;
	}

	public String getHbm2ddlAuto() {
		return hbm2ddlAuto != null ? hbm2ddlAuto : "none";
	}
}
