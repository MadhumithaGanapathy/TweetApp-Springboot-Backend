package com.tweetapp;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
class TweetApplicationTest {

	
	@Test
	void contextLoads() {
		TweetApplication.main(new String[] {});
	}

}
