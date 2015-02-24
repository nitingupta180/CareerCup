package com.careercup.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import com.careercup.utils.Utils;
import com.careercup.websitecrawler.ForumDetails;
import com.careercup.websitecrawler.QuestionDetails;

public class CareerCupDB extends SQLiteOpenHelper {

	private final String TAG = CareerCupDB.class.getSimpleName();

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "CareerCupDB";
	private static final String TABLE_QUESTIONS = "QuestionsTable";
	private static final String TABLE_FORUMS = "ForumsTable";

	// Columns for QuestionsTable
	private static final String KEY_QUESTION_ID = "id";
	private static final String KEY_QUESTION_TOTAL_ANSWERS = "total_answers";
	private static final String KEY_QUESTION_TEXT = "text";
	private static final String KEY_QUESTION_AUTHOR = "author";
	private static final String KEY_QUESTION_POSTING_TIME = "posting_time";
	private static final String KEY_QUESTION_URL = "url";
	private static final String KEY_QUESTION_COMPANY_LOGO = "company_logo";

	// Columns for QuestionsTable
	private static final String KEY_FORUM_ID = "id";
	private static final String KEY_FORUM_TOTAL_ANSWERS = "total_answers";
	private static final String KEY_FORUM_HEADER_TEXT = "header";
	private static final String KEY_FORUM_TEXT = "text";
	private static final String KEY_FORUM_AUTHOR = "author";
	private static final String KEY_FORUM_POSTING_TIME = "posting_time";
	private static final String KEY_FORUM_URL = "url";

	public CareerCupDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Creating Table
	@Override
	public void onCreate(SQLiteDatabase db) {
		//Log.i(TAG, "Creating DB");
		createQuestionsTable(db);
		createForumsTable(db);
	}

	// Upgrading database
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Drop older table if existed
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_QUESTIONS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_FORUMS);
		// Create tables again
		onCreate(db);
	}

	private void createQuestionsTable(SQLiteDatabase db) {
		String CREATE_QUESTIONS_TABLE = "CREATE TABLE " + TABLE_QUESTIONS + "("
				+ KEY_QUESTION_ID + " INTEGER PRIMARY KEY,"
				+ KEY_QUESTION_TOTAL_ANSWERS + " TEXT," + KEY_QUESTION_TEXT
				+ " TEXT," + KEY_QUESTION_AUTHOR + " TEXT,"
				+ KEY_QUESTION_POSTING_TIME + " TEXT," + KEY_QUESTION_URL
				+ " TEXT," + KEY_QUESTION_COMPANY_LOGO + " BLOB" + ")";

		db.execSQL(CREATE_QUESTIONS_TABLE);
	}

	private void createForumsTable(SQLiteDatabase db) {
		String CREATE_FORUMS_TABLE = "CREATE TABLE " + TABLE_FORUMS + "("
				+ KEY_FORUM_ID + " INTEGER PRIMARY KEY,"
				+ KEY_FORUM_TOTAL_ANSWERS + " TEXT," + KEY_FORUM_HEADER_TEXT
				+ " TEXT," + KEY_FORUM_TEXT + " TEXT," + KEY_FORUM_AUTHOR
				+ " TEXT," + KEY_FORUM_POSTING_TIME + " TEXT," + KEY_FORUM_URL
				+ " TEXT" + ")";

		db.execSQL(CREATE_FORUMS_TABLE);
	}
	
	public void deleteQuestionsTableContent() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_QUESTIONS, null, null);
		db.close();
	}
	
	public void deleteForumsTableContent() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_FORUMS, null, null);
		db.close();
	}

	public void addQuestionsToDB(List<QuestionDetails> questions) {
		SQLiteDatabase db = this.getWritableDatabase();
		for (QuestionDetails question : questions) {
			ContentValues values = new ContentValues();

			values.put(KEY_QUESTION_TOTAL_ANSWERS, question.getTotalAnswers());
			values.put(KEY_QUESTION_TEXT, question.getQuestionText());
			values.put(KEY_QUESTION_AUTHOR, question.getAuthorName());
			values.put(KEY_QUESTION_POSTING_TIME, question.getTimePosted());
			values.put(KEY_QUESTION_URL, question.getQuestionUrl());

			Bitmap logo = question.getCompanyIcon();
			byte[] icon = Utils.getBytesFromBitmap(logo);
			values.put(KEY_QUESTION_COMPANY_LOGO, icon);

			// Inserting Row
			db.insert(TABLE_QUESTIONS, null, values);
		}
		// Closing database connection
		db.close();
	}

	public void addForumsToDB(List<ForumDetails> forums) {
		SQLiteDatabase db = this.getWritableDatabase();
		for (ForumDetails forum : forums) {
			ContentValues values = new ContentValues();

			values.put(KEY_FORUM_TOTAL_ANSWERS, forum.getTotalAnswers());
			values.put(KEY_FORUM_HEADER_TEXT, forum.getForumHeaderText());
			values.put(KEY_FORUM_TEXT, forum.getForumText());
			values.put(KEY_FORUM_AUTHOR, forum.getAuthorName());
			values.put(KEY_FORUM_POSTING_TIME, forum.getTimePosted());
			values.put(KEY_FORUM_URL, forum.getForumUrl());

			// Inserting Row
			db.insert(TABLE_FORUMS, null, values);
		}
		// Closing database connection
		db.close();
	}

	public ArrayList<QuestionDetails> getSavedQuestions() {
		ArrayList<QuestionDetails> questions = new ArrayList<QuestionDetails>();

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_QUESTIONS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				String totalAnswers = cursor.getString(1);
				String questionText = cursor.getString(2);
				String author = cursor.getString(3);
				String postingTime = cursor.getString(4);
				String questionUrl = cursor.getString(5);
				byte[] logo = cursor.getBlob(6);
				Bitmap icon = Utils.getBitmapFromByteArray(logo);
				QuestionDetails question = new QuestionDetails(totalAnswers,
						questionText, author, postingTime, questionUrl, icon);

				// Adding question to list
				questions.add(question);
			} while (cursor.moveToNext());
		}
		db.close();
		// return question list
		return questions;
	}

	public ArrayList<ForumDetails> getSavedForumPosts() {
		ArrayList<ForumDetails> forums = new ArrayList<ForumDetails>();

		// Select All Query
		String selectQuery = "SELECT  * FROM " + TABLE_FORUMS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);

		// looping through all rows and adding to list
		if (cursor.moveToFirst()) {
			do {
				String totalAnswers = cursor.getString(1);
				String forumHeader = cursor.getString(2);
				String forumText = cursor.getString(3);
				String author = cursor.getString(4);
				String postingTime = cursor.getString(5);
				String forumUrl = cursor.getString(6);
				ForumDetails forum = new ForumDetails(totalAnswers,
						forumHeader, forumText, author, postingTime, forumUrl);

				// Adding question to list
				forums.add(forum);
			} while (cursor.moveToNext());
		}
		db.close();
		// return question list
		return forums;
	}

}
