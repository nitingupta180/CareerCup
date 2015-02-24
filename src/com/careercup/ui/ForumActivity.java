package com.careercup.ui;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.careercup.R;
import com.careercup.analytics.Action;
import com.careercup.analytics.AnalyticsGoogle;
import com.careercup.analytics.IAnalytics;
import com.careercup.data.ForumPostAdapter;
import com.careercup.utils.Constants;
import com.careercup.utils.Utils;
import com.careercup.websitecrawler.ForumPostDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ForumActivity extends Activity {

	private final String TAG = ForumActivity.class.getSimpleName();
	
	private Context mContext;
	List<ForumPostDetails> mForumPostDetails = new LinkedList<ForumPostDetails>();
	ForumPostAdapter mForumPostAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.configureActionBarStyle(this);
		setContentView(R.layout.activity_answer_header);
		mContext = getApplicationContext();

		IAnalytics checkin = AnalyticsGoogle.getAnalytic();
        checkin.analyticsCareerCupKPI(Action.FORUM_ACTIVITY, "onCreate()", 0);
		
		
		
		//Enable action bar icon to toggle navigation drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        Intent intent = getIntent();
		String totalAnswers = intent.getStringExtra(Constants.TOTAL_ANSWERS);
		String forumHeaderText = intent.getStringExtra(Constants.FORUM_HEADER);
		String forumText = intent.getStringExtra(Constants.FORUM_TEXT);
		String forumPosterName = intent.getStringExtra(Constants.FORUM_POSTER_NAME);
		String forumPostingTime = intent.getStringExtra(Constants.FORUM_POSTING_TIME);
		String forumUrl = intent.getStringExtra(Constants.SINGLE_FORUM_URL);
		
		ListView forumPost = (ListView) findViewById(R.id.listView);
		
		View headerView = getLayoutInflater().inflate(R.layout.fragment_forum_list_item_layout, null);
		((TextView) headerView.findViewById(R.id.tvNumAnswers)).setText(totalAnswers);
		((TextView) headerView.findViewById(R.id.tvForumHeader)).setText(forumHeaderText);
		((TextView) headerView.findViewById(R.id.tvForumText)).setText(forumText);
		((TextView) headerView.findViewById(R.id.tvPosterName)).setText(forumPosterName);
		((TextView) headerView.findViewById(R.id.tvPostingTime)).setText(forumPostingTime);
		
		forumPost.addHeaderView(headerView);
		
		View footerView = getLayoutInflater().inflate(R.layout.bottom_banner_ad, null);
		AdView mAdView = (AdView) footerView.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		
		forumPost.addFooterView(footerView);
		
		mForumPostAdapter = new ForumPostAdapter(this, mForumPostDetails);
		forumPost.setAdapter(mForumPostAdapter);
		
		new LoadForumAnswers().execute(forumUrl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.forum, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if(id == android.R.id.home) {
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class LoadForumAnswers extends AsyncTask<String, Void, Void> {

		boolean isErrorCase = false;
		
		@Override
		protected Void doInBackground(String... params) {
			//Log.i(TAG, "Loading forum answers...");
			String url = params[0];
			isErrorCase = false;
			try {
				Connection connection = Jsoup.connect(url).userAgent(Constants.USER_AGENT).timeout(10000);
				Document doc = connection.get();
				Elements listTags = doc.select("div [id^=commentThread]");
				int skipCounter = 0;
				for(Element element : listTags) {
					if(skipCounter < 1) {
						skipCounter++;
						continue;
					}
					getAnswerObject(element);
				}
			} catch (IOException e) {
				isErrorCase = true;
				//Log.e(TAG, "Some error occurred while connecting");
			}
			return null;
		}
		
		private void getAnswerObject(Element element) {
			Elements comment = element.select("div[class=comment]");
			
			String netVotes = comment.select("div[class=votesWrapper]").text();
			
			Elements answerBody = comment.select("div[class=commentBody]");
			String tempAnswerText = answerBody.get(0).text();
			
			Elements authorDetails = answerBody.select("span[class=author]");
			String answerText = tempAnswerText.substring(0, tempAnswerText.indexOf(authorDetails.text()));
			
			String authorName = "- ";
			Elements nameLink = authorDetails.select("a[href^=/user?id=]");
			if(nameLink.size() > 0) {
				authorName += nameLink.first().text();
			} else {
				authorName = authorDetails.get(0).ownText();
			}
			String date = " " + authorDetails.select("abbr").text();
			String postingDetails = authorName + date;
			
			ForumPostDetails answer = new ForumPostDetails(netVotes, answerText, postingDetails, true);
			mForumPostDetails.add(answer);
			
			Elements childComments = element.select("div[class=childComment]");
			for(Element subComment : childComments) {
				getCommentObject(subComment);
			}
		}

		private void getCommentObject(Element comment) {
			String netVotes = comment.select("div[class=votesNetQuestion]").text() + " " + comment.select("div[class=votesCountQuestion]").text();
			
			String commentText = comment.select("div[class=commentBody] p").text();
			
			Elements authorDetails = comment.select("span[class=author]");
			String authorName = "- ";
			Elements nameLink = authorDetails.select("a[href^=/user?id=]");
			if(nameLink.size() > 0) {
				authorName += nameLink.first().text();
			} else {
				authorName = authorDetails.get(0).ownText();
			}
			String date = " " + authorDetails.select("abbr").text();
			String postingDetails = authorName + date;
			
			ForumPostDetails answer = new ForumPostDetails(netVotes, commentText, postingDetails, false);
			mForumPostDetails.add(answer);
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(mContext, "Loading Forum Answers...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(isErrorCase) {
				Toast.makeText(mContext, Constants.ERROR_TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "New Forum Answers Added!!!", Toast.LENGTH_SHORT).show();
			}
			mForumPostAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
		
	}
}
