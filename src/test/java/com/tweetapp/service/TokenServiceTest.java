package com.tweetapp.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.tweetapp.model.User;



@SpringBootTest
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

	@InjectMocks
	TokenService tokenService;
	
	@Mock
	TokenService ttService;
	

	@Test
	void testCreateToken() throws UnsupportedEncodingException  {
			User user = new User();
			user.setFirstName("madhu");
			user.setLastName("gana");
			user.setContactNumber("9487925396");
			user.setEmail("madhusparkle99@gmail.com");
			user.setConfirmPasword("123");
			user.setPassword("123");
			user.setLoginId("madhu_gana");
			user.setId(new ObjectId("62e3ce223528ce670288be2a"));
			
		assertNotNull(tokenService.createToken(user.getId()));
	}

	@Test
	void testIsTokenValid() throws UnsupportedEncodingException {

		assertEquals(true, tokenService.isTokenValid(
				"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVkQXQiOjE2NTkxNzY5MzgsInVzZXJJZCI6IjYyZTNjZWY1MzUyOGNlNjcwMjg4YmUyYiJ9.gJV2mR5f_c8KL0kcG-72NfD4SzZribzEwpcM3Q-p4ag"));
	}

	@Test
	void testIsTokenValidNullBlock() throws UnsupportedEncodingException,JWTDecodeException {
		// lenient().when(ttService.getUserIdFromToken("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJjcmVhdGVkQXQiOjE2NTkxNzY5MzgsInVzZXJJZCI6IjYyZTNjZWY1MzUyOGNlNjcwMjg4YmUyYiJ9.gJV2mR5f_c8KL0kcG-72NfD4SzZribzEwpcM3Q-p4ag")).thenReturn(null);
		
		assertEquals(false, tokenService.isTokenValid("eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ.eyJjcmVhdGVkQXQiOjE2NTkxNzY5MzgsInVzZXJJZCI6IjYyZTNjZWY1MzUyOGNlNjcwMjg4YmUyYiJ.gJV2mR5f_c8KL0kcG-72NfD4SzZribzEwpcM3Q-p4a"));
	}

}
