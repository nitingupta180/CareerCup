package com.careercup.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.careercup.R;
import com.careercup.analytics.Action;
import com.careercup.analytics.AnalyticsGoogle;
import com.careercup.analytics.IAnalytics;
import com.careercup.data.AnswerAdapter;
import com.careercup.utils.Constants;
import com.careercup.utils.Utils;
import com.careercup.websitecrawler.AnswerDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class AnswerActivity extends Activity {

	private final String TAG = AnswerActivity.class.getSimpleName();
	
	private Context mContext;
	List<AnswerDetails> mAnswers = new ArrayList<AnswerDetails>();
	AnswerAdapter mAnswerListAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.configureActionBarStyle(this);
		setContentView(R.layout.activity_answer_header);
		mContext = getApplicationContext();
		
		IAnalytics checkin = AnalyticsGoogle.getAnalytic();
        checkin.analyticsCareerCupKPI(Action.ANSEWER_ACTIVITY, "onCreate()", 0);
		
		//Enable action bar icon to toggle navigation drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
		
		Intent intent = getIntent();
		String totalAnswers = intent.getStringExtra(Constants.TOTAL_ANSWERS);
		String questionText = intent.getStringExtra(Constants.QUESTION_TEXT);
		String questionPosterName = intent.getStringExtra(Constants.QUESTION_POSTER_NAME);
		String questionPostingTime = intent.getStringExtra(Constants.QUESTION_POSTING_TIME);
		String questionUrl = intent.getStringExtra(Constants.SINGLE_QUESTION_URL);
		byte[] companyIcon = intent.getByteArrayExtra(Constants.COMPANY_ICON);
		Bitmap companyLogo = BitmapFactory.decodeByteArray(companyIcon, 0, companyIcon.length);
		
		
		ListView answers = (ListView) findViewById(R.id.listView);
		
		View headerView = getLayoutInflater().inflate(R.layout.fragment_question_list_item_layout, null);
		((TextView) headerView.findViewById(R.id.tvNumAnswers)).setText(totalAnswers);
		((TextView) headerView.findViewById(R.id.tvQuestionDetail)).setText(questionText);
		((TextView) headerView.findViewById(R.id.tvPosterName)).setText(questionPosterName);
		((TextView) headerView.findViewById(R.id.tvPostingTime)).setText(questionPostingTime);
		((ImageView) headerView.findViewById(R.id.ivCompanyIcon)).setImageBitmap(companyLogo);

		answers.addHeaderView(headerView);
		
		View footerView = getLayoutInflater().inflate(R.layout.bottom_banner_ad, null);
		AdView mAdView = (AdView) footerView.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);
		
		answers.addFooterView(footerView);
		
		mAnswerListAdapter = new AnswerAdapter(this, mAnswers);
		answers.setAdapter(mAnswerListAdapter);
		
		new LoadAnswers().execute(questionUrl);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.question_detail, menu);
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
	
	private class LoadAnswers extends AsyncTask<String, Void, Void> {

		boolean isErrorCase = false;
		
		@Override
		protected Void doInBackground(String... params) {
			//Log.i(TAG, "Loading Answers...");
			String url = params[0];
			isErrorCase = false;
			try {
				Connection connection = Jsoup.connect(url).userAgent(Constants.USER_AGENT).timeout(10000);
				Document doc = connection.get();
				Elements listTags = doc.select("div [id^=commentThread]");
				int skipCounter = 0;
				for(Element element : listTags) {
					if(skipCounter < 2) {
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
			
			AnswerDetails answer = new AnswerDetails(netVotes, answerText, postingDetails, true);
			mAnswers.add(answer);
			
			Elements childComments = element.select("div[class=childComment]");
			for(Element subComment : childComments) {
				getCommentObject(subComment);
			}
		}
		
		private void getCommentObject(Element comment) {
			String netVotes = comment.select("div[class=votesNetQuestion]").text() + " " + comment.select("div[class=votesCountQuestion]").text();
			
			String tempCommentText = comment.select("div[class=commentBody]").get(0).text();
			
			Elements authorDetails = comment.select("span[class=author]");
			String authorName = "- ";
			Elements nameLink = authorDetails.select("a[href^=/user?id=]");
			if(nameLink.size() > 0) {
				authorName += nameLink.first().text();
			} else {
				authorName = authorDetails.get(0).text();
			}
			String date = " " + authorDetails.select("abbr").text();
			String postingDetails = authorName + date;
			
			String commentText = tempCommentText.substring(tempCommentText.indexOf(netVotes) + netVotes.length(), tempCommentText.indexOf(authorDetails.text()));
			
			AnswerDetails answer = new AnswerDetails(netVotes, commentText, postingDetails, false);
			mAnswers.add(answer);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(mContext, "Loading Answers...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(isErrorCase) {
				Toast.makeText(mContext, Constants.ERROR_TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "New Answers Added!!!", Toast.LENGTH_SHORT).show();
			}
			mAnswerListAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
		
	}
}
