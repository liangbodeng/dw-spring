package com.example.rest;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.example.auth.User;
import com.example.auth.UserProvider;
import com.google.common.base.Optional;
import com.example.auth.JwtAuthenticator;
import com.example.auth.UserException;

import io.dropwizard.auth.AuthenticationException;

@Path("/user")
public class UserResource {
	public static final Logger LOG = LoggerFactory.getLogger(UserResource.class);

	private UserProvider userProvider;
	private JwtAuthenticator jwtAuthenticator;

	public UserResource(UserProvider userProvider, JwtAuthenticator jwtAuthenticator) {
		this.userProvider = userProvider;
		this.jwtAuthenticator = jwtAuthenticator;
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public User login(
			@Context HttpServletRequest request,
			@FormParam("apiKey") String apiKey
	) throws UserException {
		User user = userProvider.findByApiKey(apiKey);
		if (user == null) {
			throw new UserException(UserException.PERMISSION_DENIED, "Could find user with the specified API key");
		}

		user.setToken(jwtAuthenticator.createJwt(user.getUsername(), user.getFullName()));
		return user;
	}

	@POST
	@Path("/refreshToken")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	@Timed
	public User refreshToken(
			@FormParam("token") String token
	) {
		try {
			Optional<User> result = jwtAuthenticator.authenticate(token);
			if (result.isPresent()) {
				User user = result.get();
				user.setToken(jwtAuthenticator.createJwt(user.getUsername(), user.getFullName()));
				return user;
			}
		} catch (AuthenticationException e) {
			LOG.warn("Error while authenticating token", e);
			throw new InternalServerErrorException();
		}

		return null;
	}

	private boolean isRequestFromLocal(HttpServletRequest request) {
		InetAddress addr = null;
		try {
			addr = InetAddress.getByName(request.getRemoteAddr());
		} catch (UnknownHostException e) {
			return false;
		}

		// Check if the address is a valid special local or loop back
		if (addr.isAnyLocalAddress() || addr.isLoopbackAddress()) {
			return true;
		}

		// Check if the address is defined on any interface
		try {
			return NetworkInterface.getByInetAddress(addr) != null;
		} catch (SocketException e) {
			return false;
		}
	}
}
