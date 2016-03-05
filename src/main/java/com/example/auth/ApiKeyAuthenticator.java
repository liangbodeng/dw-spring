package com.example.auth;

import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class ApiKeyAuthenticator implements Authenticator<PrefixedToken, User> {
	private static final String API_KEY_PRFIX = "Api-Key";

	private UserProvider userProvider;

	public ApiKeyAuthenticator(UserProvider userProvider) {
		this.userProvider = userProvider;
	}

	protected boolean canValidate(PrefixedToken prefixedToken) {
		return API_KEY_PRFIX.equalsIgnoreCase(prefixedToken.getPrefix());
	}

	@Override
	public Optional<User> authenticate(PrefixedToken prefixedToken) throws AuthenticationException {
		User user = userProvider.findByApiKey(prefixedToken.getToken());
		if (user != null) {
			return Optional.of(user);
		}

		return Optional.absent();
	}
}
