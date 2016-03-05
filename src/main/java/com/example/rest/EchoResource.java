package com.example.rest;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

@Path("/echo")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
public class EchoResource {
	public static final Logger LOG = LoggerFactory.getLogger(EchoResource.class);

	public EchoResource() {
	}

	@GET
	@Timed
	public List<String> echoN(
			@QueryParam("text") String text,
			@QueryParam("n") @DefaultValue("1") Integer n
	) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("echoN - text={} n={}", text, n);
		}

		return Collections.nCopies(n, text);
	}
}
