package com.example.auth;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Date;

import org.jose4j.base64url.Base64;
import org.jose4j.jwk.RsaJsonWebKey;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.RsaKeyUtil;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Optional;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;

public class JwtAuthenticator implements Authenticator<PrefixedToken, User> {
	private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticator.class);

	private static final String ALGO_ID = AlgorithmIdentifiers.RSA_USING_SHA512;
	private static final String FULL_NAME = "fullName";
	private static final String JWT_PREFIX = "Bearer";
	private static final String ISSUER = "Issuer";
	private static final String AUDIENCE = "Audience";

	public static final int NOT_BEFORE_MINS = 2;
	public static final int ALLOW_CLOCK_SKEW_IN_SECS = 30;

	private final RsaJsonWebKey jsonWebKey;
	private final int expireMins;

	public JwtAuthenticator(String secret, int expireMins) {
		try {
			this.expireMins = expireMins;
			RSAPrivateCrtKey privateKey = (RSAPrivateCrtKey)KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(secret)));
			RSAPublicKey publicKey = new RsaKeyUtil().publicKey(privateKey.getModulus(), privateKey.getPublicExponent());
			jsonWebKey = new RsaJsonWebKey(publicKey);
			jsonWebKey.setPrivateKey(privateKey);
		} catch (InvalidKeySpecException | NoSuchAlgorithmException | JoseException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	protected boolean canValidate(PrefixedToken prefixedToken) {
		return JWT_PREFIX.equalsIgnoreCase(prefixedToken.getPrefix());
	}

	@Override
	public Optional<User> authenticate(PrefixedToken prefixedToken) throws AuthenticationException {
		return authenticate(prefixedToken.getToken());
	}

	public Optional<User> authenticate(String token) throws AuthenticationException {
		try {
			JwtConsumer jwtConsumer = new JwtConsumerBuilder()
					.setRequireExpirationTime()
					.setAllowedClockSkewInSeconds(ALLOW_CLOCK_SKEW_IN_SECS)
					.setRequireSubject()
					.setExpectedIssuer(ISSUER)
					.setExpectedAudience(AUDIENCE)
					.setVerificationKey(jsonWebKey.getKey())
					.build();
			JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
			return validateClaims(jwtClaims);
		} catch (InvalidJwtException e) {
			LOG.info("Now is " + new Date());
			LOG.error(e.getMessage(), e);
			return Optional.absent();
		}
	}

	private Optional<User> validateClaims(JwtClaims claims) throws AuthenticationException {
		try {
			User user = new User(claims.getSubject());
			user.setFullName(claims.getStringClaimValue(FULL_NAME));
			return Optional.of(user);
		} catch (MalformedClaimException e) {
			throw new AuthenticationException(e.getMessage(), e);
		}
	}

	public String createJwt(String username, String fullName) {
		JwtClaims claims = new JwtClaims();
		claims.setIssuer(ISSUER);
		claims.setAudience(AUDIENCE);
		claims.setExpirationTimeMinutesInTheFuture(expireMins);
		claims.setGeneratedJwtId();
		claims.setIssuedAtToNow();
		claims.setNotBeforeMinutesInThePast(NOT_BEFORE_MINS);
		claims.setSubject(username);
		claims.setStringClaim(FULL_NAME, fullName);

		JsonWebSignature jws = new JsonWebSignature();

		jws.setPayload(claims.toJson());
		jws.setKey(jsonWebKey.getPrivateKey());
		jws.setKeyIdHeaderValue(jsonWebKey.getKeyId());
		jws.setAlgorithmHeaderValue(ALGO_ID);

		try {
			return jws.getCompactSerialization();
		} catch (JoseException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
