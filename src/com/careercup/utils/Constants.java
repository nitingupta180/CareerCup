package com.careercup.utils;

public class Constants {

	final public static String BASE_URL = "http://www.careercup.com";
	final public static String QUESTION_URL = BASE_URL + "/page";
	final public static String QUESTION_PAGES_URL = QUESTION_URL + "?n=";
	final public static String FORUM_URL = BASE_URL + "/forum";
	final public static String FORUM_PAGES_URL = FORUM_URL + "?n=";
	final public static String SALARY_URL = BASE_URL + "/salary";
	// final public static String RESUME_TIPS_URL = BASE_URL + "/resume";

	final public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.111 Safari/537.36";

	final public static String FRAGMENT_TITLE = "frag_title";
	final public static String TAG_FRAGMENT_QUESTIONS = "fragment_questions";
	final public static String TAG_FRAGMENT_FORUM = "fragment_forum";
	final public static String TAG_FRAGMENT_SALARIES = "fragment_salaries";
	final public static String TAG_FRAGMENT_RESUME_TIPS = "fragment_resume_tips";
	final public static String TAG_FRAGMENT_RSS = "fragment_rss";
	
	final public static int LOAD_THRESHOLD_VISIBLE_QUESTIONS = 10;
	final public static int LOAD_THRESHOLD_VISIBLE_FORUMS = 7;
	
	// Flags for Question
	final public static String TOTAL_ANSWERS = "TOTAL_ANSWERS";
	final public static String QUESTION_TEXT = "QUESTION_TEXT";
	final public static String QUESTION_POSTER_NAME = "QUESTION_POSTER_NAME";
	final public static String QUESTION_POSTING_TIME = "QUESTION_POSTING_TIME";
	final public static String SINGLE_QUESTION_URL = "SINGLE_QUESTION_URL";
	final public static String COMPANY_ICON = "COMPANY_ICON";
	
	// Flags for Forum
	final public static String FORUM_HEADER = "FORUM_HEADER";
	final public static String FORUM_TEXT = "FORUM_TEXT";
	final public static String FORUM_POSTER_NAME = "FORUM_POSTER_NAME";
	final public static String FORUM_POSTING_TIME = "FORUM_POSTING_TIME";
	final public static String SINGLE_FORUM_URL = "SINGLE_FORUM_URL";
	
	final public static String ERROR_TOAST_MESSAGE = "Connection Lost. Please try again.";
	
	// Enumerator for Salary
	public static enum SALARY_TYPE {
		TYPE_SECTON_HEADER,
		TYPE_SALARY_INFO
	}
}
