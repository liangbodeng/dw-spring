package com.example.rest;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.example.auth.User;

import io.dropwizard.auth.Auth;

@Path("/upperEcho")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class UpperEchoResource {
	public static final Logger LOG = LoggerFactory.getLogger(UpperEchoResource.class);

	public UpperEchoResource() {
	}

	@GET
	@Timed
	public List<String> upperEchoN(@Auth User user,
			@QueryParam("text") String text,
			@QueryParam("n") @DefaultValue("1") Integer n
	) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("upperEchoN - text={} n={}", text, n);
		}

		return Collections.nCopies(n, text.toUpperCase());
	}
}
