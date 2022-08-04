package com.tweetapp.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.tweetapp.model.User;
@Repository
public interface UserRepository extends MongoRepository<User, String> {
	
	List<User> findByFirstNameStartingWith(String name);

	User findByLoginId(String username);

	List<User> findByLoginIdContaining(String username);

}
