package com.example.auth;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class KeyGenTest {
	@Test
	public void testKeyGen() throws Exception {
		KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		String s = new String(Base64.getEncoder().encode(keyPair.getPrivate().getEncoded()));
		System.out.println(s);

		JwtAuthenticator jwtAuthenticator = new JwtAuthenticator(s, 30);
		assertNotNull(jwtAuthenticator);
	}
}
