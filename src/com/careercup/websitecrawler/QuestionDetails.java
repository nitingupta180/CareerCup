package com.careercup.websitecrawler;

import android.graphics.Bitmap;

public class QuestionDetails {
	private String mTotalAnswers;
	private String mQuestionText;
	private String mAuthor;
	private String mTimePosted;
	private String mQuestionUrl;
	private Bitmap mCompanyIcon;
	
	public QuestionDetails(String answer, String question, String author, String time, String url, Bitmap icon) {
		mTotalAnswers = answer;
		mQuestionText = question;
		mAuthor = author;
		mTimePosted = time;
		mQuestionUrl = url;
		mCompanyIcon = icon;
	}
	
	public String getTotalAnswers() {
		return mTotalAnswers;
	}
	
	public String getQuestionText() {
		return mQuestionText;
	}
	
	public String getAuthorName() {
		return mAuthor;
	}
	
	public String getTimePosted() {
		return mTimePosted;
	}
	
	public String getQuestionUrl() {
		return mQuestionUrl;
	}
	
	public Bitmap getCompanyIcon() {
		return mCompanyIcon;
	}
}
