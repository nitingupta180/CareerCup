package com.careercup.websitecrawler;

public class ForumPostDetails {
	private String mNetVotes;
	private String mText;
	private String mPostingDetails;
	private boolean mIsAnswer;
	
	public ForumPostDetails(String votes, String text, String postingDetails, boolean isAnswer) {
		mNetVotes = votes;
		mText = text;
		mPostingDetails = postingDetails;
		mIsAnswer = isAnswer;
	}
	
	public String getNetVotes() {
		return mNetVotes;
	}
	
	public String getText() {
		return mText;
	}
	
	public String getPostingDetails() {
		return mPostingDetails;
	}
	
	public boolean isAnswer() {
		return mIsAnswer;
	}
}
