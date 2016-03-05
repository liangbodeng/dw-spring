package com.example.auth;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;
import com.google.common.net.HttpHeaders;

import io.dropwizard.auth.AuthFactory;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class DwAuthFactory extends AuthFactory<PrefixedToken, User> {
	private static final Logger LOG = LoggerFactory.getLogger(DwAuthFactory.class);

	@Context
	private HttpServletRequest request;

	public DwAuthFactory(Authenticator<PrefixedToken, User> dwAuthenticator) {
		super(dwAuthenticator);
	}

	@Override
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public AuthFactory<PrefixedToken, User> clone(boolean required) {
		return new DwAuthFactory(authenticator());
	}

	public Class<User> getGeneratedClass() {
		return User.class;
	}

	@Override
	public User provide() {
		try {
			final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
			if (header != null) {
				final int separator = header.indexOf(' ');
				if (separator > 0) {
					String prefix = header.substring(0, separator);
					String token = header.substring(separator + 1);
					PrefixedToken prefixedToken = new PrefixedToken(prefix, token);
					final Optional<User> result = authenticator().authenticate(prefixedToken);
					if (result.isPresent()) {
						return result.get();
					}
				}
			}
		} catch (AuthenticationException ex) {
			LOG.warn("Error while authenticating credentials", ex);
			throw new InternalServerErrorException();
		}

		throw new UserException(UserException.PERMISSION_DENIED, "Permission Denied.");
	}
}
