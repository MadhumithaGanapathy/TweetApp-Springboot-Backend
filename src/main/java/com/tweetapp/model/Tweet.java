package com.tweetapp.model;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.lang.String;

import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Document(collection = "tweet")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Tweet {

	@Id
	@Indexed(unique = true)
	@JsonProperty("tweetId")
	private String tweetId;
	@JsonProperty("tweetMsg")
	@Size(min = 1, max = 144, message = "Maximum characters allowed for the tweet :144")
	private String tweetMsg;
	@JsonProperty("tweetTag")
	@Size(min = 0, max = 50, message = "Max character limit allowed for the tag:50")
	private String tweetTag;
	@JsonProperty("postTime")
	@CreatedDate
	private Date postTime;

	public Tweet() {

	}

	/**
	 * @return the postTime
	 */
	public Date getPostTime() {
		return postTime;
	}

	/**
	 * @param postTime the postTime to set
	 */
	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

	private User user;
	private long likes;
	private List<Tweet> replies;

	/**
	 * @return the tweetId
	 */
	public String getTweetId() {
		return tweetId;
	}

	/**
	 * @param tweetId the tweetId to set
	 */
	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	/**
	 * @return the tweetMsg
	 */
	public String getTweetMsg() {
		return tweetMsg;
	}

	/**
	 * @param tweetMsg the tweetMsg to set
	 */
	public void setTweetMsg(String tweetMsg) {
		this.tweetMsg = tweetMsg;
	}

	/**
	 * @return the tweetTag
	 */
	public String getTweetTag() {
		return tweetTag;
	}

	/**
	 * @param tweetTag the tweetTag to set
	 */
	public void setTweetTag(String tweetTag) {
		this.tweetTag = tweetTag;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the likes
	 */
	public long getLikes() {
		return likes;
	}

	/**
	 * @param likes the likes to set
	 */
	public void setLikes(long likes) {
		this.likes = likes;
	}

	/**
	 * @return the replies
	 */
	public List<Tweet> getReplies() {
		return replies;
	}

	/**
	 * @param replies the replies to set
	 */
	public void setReplies(List<Tweet> replies) {
		this.replies = replies;
	}



	@Override
	public String toString() {
		return "Tweet [tweetId=" + tweetId + ", tweetMsg=" + tweetMsg + ", tweetTag=" + tweetTag + ", postTime="
				+ postTime + ", user=" + user + ", likes=" + likes + ", replies=" + replies + "]";
	}

}
