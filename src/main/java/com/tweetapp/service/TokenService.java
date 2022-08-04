package com.tweetapp.service;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Service
public class TokenService {

    public static final String TOKEN_SECRET = "s4T2zOIWHMM1sxq";
    private static Logger log = LogManager.getLogger(TokenService.class);

    public String createToken(ObjectId userId) throws UnsupportedEncodingException {
    	 log.info("Creating JWT Token using create Token method");  
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            String token = JWT.create()
                    .withClaim("userId", userId.toString())
                    .withClaim("createdAt", new Date())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
        	 log.info("Token Signing Failed");  
            exception.printStackTrace();
            
        }
        return null;
    }
    

    public String getUserIdFromToken(String token) throws UnsupportedEncodingException {
    	 log.info("Fetching User from the token using getUserIdFromToken method");  
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            return jwt.getClaim("userId").asString();
        } catch (Exception exception) {
        	 log.info("Exception Thrown---->Token Verification Failed");  
            return null;
        }
    }

    public boolean isTokenValid(String token) throws UnsupportedEncodingException{
    	 log.info("Checking The Token If its Valid");  
        String userId = this.getUserIdFromToken(token);
        return userId != null;
    }
}