package com.example.auth;

import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class DwAuthenticator implements Authenticator<PrefixedToken, User> {
	private JwtAuthenticator jwtAuthenticator;
	private ApiKeyAuthenticator apiKeyAuthenticator;

	public DwAuthenticator(JwtAuthenticator jwtAuthenticator, ApiKeyAuthenticator apiKeyAuthenticator) {
		this.jwtAuthenticator = jwtAuthenticator;
		this.apiKeyAuthenticator = apiKeyAuthenticator;
	}

	@Override
	public Optional<User> authenticate(PrefixedToken prefixedToken) throws AuthenticationException {
		if (jwtAuthenticator.canValidate(prefixedToken)) {
			return jwtAuthenticator.authenticate(prefixedToken);
		} else if (apiKeyAuthenticator.canValidate(prefixedToken)) {
			return apiKeyAuthenticator.authenticate(prefixedToken);
		}
		return Optional.absent();
	}
}
