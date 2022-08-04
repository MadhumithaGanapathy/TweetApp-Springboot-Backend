package com.tweetapp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.bind.annotation.CrossOrigin;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author MADHU
 *Tweet Application -Springboot Rest API
 *DB-MongoDB
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableSwagger2
@CrossOrigin

public class TweetApplication {
	static Logger log = LogManager.getLogger(TweetApplication.class);

	public static void main(String[] args) {

		log.info("Started TWEET APP application -BACKEND(SPRINGBOOT)");
		SpringApplication.run(TweetApplication.class, args);
	}

}
