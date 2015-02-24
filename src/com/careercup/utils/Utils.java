package com.careercup.utils;

import java.io.ByteArrayOutputStream;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.careercup.R;

public class Utils {

	private static final String PREF_LATEST_QUESTION = "PREF_LATEST_QUESTION";
	private static final String PREF_LATEST_FORUM_POST = "PREF_LATEST_FORUM_POST";
	private static final String PREF_LAST_QUESTION_PAGE_SAVED = "PREF_LAST_QUESTION_PAGE_SAVED";
	private static final String PREF_LAST_FORUM_PAGE_SAVED = "PREF_LAST_FORUM_PAGE_SAVED";
	
	public static void configureActionBarStyle(Activity context) {
		ActionBar bar = context.getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
		int titleId = context.getResources().getIdentifier("action_bar_title",
				"id", "android");
		TextView actionBarTitle = (TextView) context.findViewById(titleId);
		actionBarTitle.setTextAppearance(context, R.style.ActionBarTitleStyle);
	}

	public static byte[] getBytesFromBitmap(Bitmap image) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(CompressFormat.PNG, 0, stream);
		return stream.toByteArray();
	}

	public static Bitmap getBitmapFromByteArray(byte[] image) {
		return BitmapFactory.decodeByteArray(image, 0, image.length);
	}

	public static String getLatestQuestionSaved(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		String questionUrl = prefs.getString(PREF_LATEST_QUESTION, null);
		return questionUrl;
	}

	public static String getLatestForumPostSaved(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		String forumPostUrl = prefs.getString(PREF_LATEST_FORUM_POST, null);
		return forumPostUrl;
	}
	
	public static int getLastQuestionPageSaved(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int questionPageNum = prefs.getInt(PREF_LAST_QUESTION_PAGE_SAVED, -1);
		return questionPageNum;
	}
	
	public static int getLastForumPageSaved(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		int forumPageNum = prefs.getInt(PREF_LAST_FORUM_PAGE_SAVED, -1);
		return forumPageNum;
	}

	public static void setLatestQuestionSaved(Context context, String questionUrl) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putString(PREF_LATEST_QUESTION, questionUrl);
		edit.commit();
	}
	
	public static void setLatestForumPostSaved(Context context, String forumPostUrl) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putString(PREF_LATEST_FORUM_POST, forumPostUrl);
		edit.commit();
	}
	
	public static void setLastQuestionPageSaved(Context context, int questionPageNum) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putInt(PREF_LAST_QUESTION_PAGE_SAVED, questionPageNum);
		edit.commit();
	}
	
	public static void setLastForumPageSaved(Context context, int forumPageNum) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		Editor edit = prefs.edit();
		edit.putInt(PREF_LAST_FORUM_PAGE_SAVED, forumPageNum);
		edit.commit();
	}
}
