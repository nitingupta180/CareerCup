package com.careercup.websitecrawler;

public class ForumDetails {
	private String mTotalAnswers;
	private String mForumHeaderText;
	private String mForumText;
	private String mAuthor;
	private String mTimePosted;
	private String mForumUrl;
	
	public ForumDetails(String answer, String forumHeader, String forumText, String author, String time, String url) {
		mTotalAnswers = answer;
		mForumHeaderText = forumHeader;
		mForumText = forumText;
		mAuthor = author;
		mTimePosted = time;
		mForumUrl = url;
	}
	
	public String getTotalAnswers() {
		return mTotalAnswers;
	}
	
	public String getForumHeaderText() {
		return mForumHeaderText;
	}
	
	public String getForumText() {
		return mForumText;
	}
	
	public String getAuthorName() {
		return mAuthor;
	}
	
	public String getTimePosted() {
		return mTimePosted;
	}
	
	public String getForumUrl() {
		return mForumUrl;
	}
}
