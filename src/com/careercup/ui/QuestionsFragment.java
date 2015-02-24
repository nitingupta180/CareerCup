package com.careercup.ui;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.careercup.R;
import com.careercup.analytics.Action;
import com.careercup.analytics.AnalyticsGoogle;
import com.careercup.analytics.IAnalytics;
import com.careercup.data.QuestionPreviewAdapter;
import com.careercup.database.CareerCupDB;
import com.careercup.utils.Constants;
import com.careercup.utils.Utils;
import com.careercup.websitecrawler.QuestionDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class QuestionsFragment extends Fragment {
	
	private final String TAG = QuestionsFragment.class.getSimpleName();
	
	private Context mContext;
    List<QuestionDetails> mQuestions = new ArrayList<QuestionDetails>();
    QuestionPreviewAdapter mQuestionListAdapter;
    SwipeRefreshLayout mSwipeToRefresh;
    private int mNextPageNumToLoad = 1;
	private boolean mIsPageLoading = false;
	private String mLastSavedQuestionUrl = null;
	private boolean mIsFragmentCreated = true;
	private HashSet<String> questionSet = new HashSet<String>();
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mContext = getActivity().getApplicationContext();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_list_layout, container, false);
		
		AdView mAdView = (AdView) root.findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		IAnalytics checkin = AnalyticsGoogle.getAnalytic();
        checkin.analyticsCareerCupKPI(Action.QUESTIONS_FRAGMENT, "onCreateView()", 0);
        
		mQuestionListAdapter = new QuestionPreviewAdapter(getActivity(), mQuestions);

		mLastSavedQuestionUrl = Utils.getLatestQuestionSaved(mContext);
		
		mSwipeToRefresh = (SwipeRefreshLayout) root.findViewById(R.id.srlPulltoRefresh);
		mSwipeToRefresh.setColorSchemeResources(android.R.color.holo_blue_bright,
											android.R.color.holo_green_light, 
											android.R.color.holo_orange_light, 
											android.R.color.holo_red_light);
		
		mSwipeToRefresh.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				//Log.i(TAG, "Swiped to Refresh content");
				mSwipeToRefresh.setRefreshing(true);
				mLastSavedQuestionUrl = Utils.getLatestQuestionSaved(mContext);
				questionSet.clear();
				if(!mIsPageLoading) {
					new QuestionsPreview().execute(Constants.QUESTION_PAGES_URL + 1);
				}
			}
		});
		
		ListView questions = (ListView) root.findViewById(R.id.listView);
		questions.setAdapter(mQuestionListAdapter);
		
		questions.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int count = view.getCount();
				if(scrollState == SCROLL_STATE_IDLE) {
					if(!mIsPageLoading && view.getLastVisiblePosition() >= count - Constants.LOAD_THRESHOLD_VISIBLE_QUESTIONS) {
						//Log.i(TAG, "Scolled to Refresh content");
						mLastSavedQuestionUrl = Utils.getLatestQuestionSaved(mContext);
						new QuestionsPreview().execute(Constants.QUESTION_PAGES_URL + mNextPageNumToLoad);
						mIsPageLoading = true;
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		
		questions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				QuestionDetails details = mQuestions.get(position);
				Intent intent = new Intent(mContext, AnswerActivity.class);
				intent.putExtra(Constants.TOTAL_ANSWERS, details.getTotalAnswers());
				intent.putExtra(Constants.QUESTION_TEXT, details.getQuestionText());
				intent.putExtra(Constants.QUESTION_POSTER_NAME, details.getAuthorName());
				intent.putExtra(Constants.QUESTION_POSTING_TIME, details.getTimePosted());
				intent.putExtra(Constants.SINGLE_QUESTION_URL, details.getQuestionUrl());
				
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				details.getCompanyIcon().compress(Bitmap.CompressFormat.PNG, 100, stream);
				byte[] companyIcon = stream.toByteArray();
				intent.putExtra(Constants.COMPANY_ICON, companyIcon);
				//Log.i(TAG, "Starting Question Activity");
				startActivity(intent);
			}
		});
		
		if(mIsFragmentCreated) {
			//Log.i(TAG, "Fragment is created");
			CareerCupDB db = new CareerCupDB(mContext);
			ArrayList<QuestionDetails> questionList = db.getSavedQuestions();
			if(questionList.size() > 0) {
				//Log.i(TAG, "Populating content from DB");
				mQuestions.addAll(0, questionList);
				mQuestionListAdapter.notifyDataSetChanged();
				for(QuestionDetails q : questionList) {
					questionSet.add(q.getQuestionUrl());
				}
				int lastPageSaved = Utils.getLastQuestionPageSaved(mContext);
				//Log.i(TAG, "Last saved page number - " + lastPageSaved);
				mNextPageNumToLoad = lastPageSaved + 1;
			} else {
				//Log.i(TAG, "Getting content (first time operation only)");
				new QuestionsPreview().execute(Constants.QUESTION_PAGES_URL + mNextPageNumToLoad);
			}
			mIsFragmentCreated = false;
		}
		return root;
	}
	
	private class QuestionsPreview extends AsyncTask<String, Void, Void> {

		boolean isErrorCase = false;
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(mContext, "Loading New Questions...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(isErrorCase) {
				Toast.makeText(mContext, Constants.ERROR_TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "New Questions Added!!!", Toast.LENGTH_SHORT).show();
			}
			if(mSwipeToRefresh.isRefreshing()) {
				//Log.i(TAG, "Stopping swipe to refresh animation");
				mSwipeToRefresh.setRefreshing(false);
			}
			mQuestionListAdapter.notifyDataSetChanged();
			mIsPageLoading = false;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(String... params) {
			//Log.i(TAG, "Loading more questions");
			String url = params[0];
			mIsPageLoading = true;
			isErrorCase = false;
			boolean loadPreviousPage = false;
			try {
				Connection connection = Jsoup.connect(url).userAgent(Constants.USER_AGENT).timeout(10000);
				Document doc = connection.get();
				Elements listTags = doc.select("ul li");
				
				List<QuestionDetails> questionsList = new ArrayList<QuestionDetails>();
				for(Element item : listTags) {
					if(item.text().contains("Answers") || item.text().contains("Answer")) {
						QuestionDetails question = getQuestionObject(item);
						if(questionSet.size() > 0 && questionSet.contains(question.getQuestionUrl())) {
							loadPreviousPage = true;
						}
						questionsList.add(question);
					}
				}
				if(loadPreviousPage) {
					//Log.i(TAG, "Loading previous page (content has been changed");
					int pageNum = mNextPageNumToLoad - 1;
					if(pageNum < 1) {
						pageNum = 1;
					}
					//Log.i(TAG, "Previous page number - " + pageNum);
					Connection conn = Jsoup.connect(Constants.QUESTION_PAGES_URL + pageNum).userAgent(Constants.USER_AGENT).timeout(10000);
					Document document = conn.get();
					Elements listTag = document.select("ul li");
					
					List<QuestionDetails> qList = new ArrayList<QuestionDetails>();
					for(Element item : listTag) {
						if(item.text().contains("Answers") || item.text().contains("Answer")) {
							QuestionDetails question = getQuestionObject(item);
							qList.add(question);
						}
					}
					questionsList.addAll(0, qList);
					questionSet.clear();
					qList.clear();
					qList = null;
				}
				
				if(mLastSavedQuestionUrl == null) {
					//Log.i(TAG, "Loading questions for the first time only");
					CareerCupDB db = new CareerCupDB(mContext);
					db.deleteQuestionsTableContent();
					db.addQuestionsToDB(questionsList);
					db.close();
					Utils.setLatestQuestionSaved(mContext, questionsList.get(0).getQuestionUrl());
					Utils.setLastQuestionPageSaved(mContext, mNextPageNumToLoad);
				} else if(mSwipeToRefresh.isRefreshing() && !mLastSavedQuestionUrl.equalsIgnoreCase(questionsList.get(0).getQuestionUrl())){
					//Log.i(TAG, "Loading questions for swipe to refresh action");
					CareerCupDB db = new CareerCupDB(mContext);
					db.deleteQuestionsTableContent();
					db.addQuestionsToDB(questionsList);
					db.close();
					Utils.setLastQuestionPageSaved(mContext, 1);
					Utils.setLatestQuestionSaved(mContext, questionsList.get(0).getQuestionUrl());
				}
				
				if(mSwipeToRefresh.isRefreshing()) {
					if(mLastSavedQuestionUrl != null && !mLastSavedQuestionUrl.equalsIgnoreCase(questionsList.get(0).getQuestionUrl())) {
						mQuestions.clear();
						mQuestions.addAll(questionsList);
						mNextPageNumToLoad = 2;
					}
					questionSet.clear();
				} else {
					mNextPageNumToLoad++;
					if(loadPreviousPage) {
						mQuestions.clear();
					}
					mQuestions.addAll(questionsList);
				}
				questionsList.clear();
				questionsList = null;
			} catch (IOException e) {
				isErrorCase = true;
				//Log.e(TAG, "Some error occurred while connecting");
			}
			return null;
		}
		
		private QuestionDetails getQuestionObject(Element item) {
			Elements numAnswers = item.select("span[class=rating]");
			String totalAnswers = numAnswers.text();
			String questionUrl = numAnswers.select("a").first().attr("abs:href");
			
			Elements authorDetails = item.select("span[class=author]");
			String posterName = "- " + authorDetails.select("a").get(0).text();
			String place = authorDetails.get(0).ownText();
			place = place.replace("-", "");
			place = place.replace(" |", "");
			String date = authorDetails.select("abbr").text();
			String postingTime = date + place;
			
			String questionText = cleanPreserveLineBreaks(item.select("span[class=entry] a").get(0).html());
			questionText = TextNode.createFromEncoded(questionText, "").getWholeText();
			
			String companyLogoUrl = item.select("span[class=company] img").first().attr("abs:src");
			Bitmap companyLogo = getBitmapFromUrl(companyLogoUrl);
			
			QuestionDetails question = new QuestionDetails(totalAnswers, questionText, posterName, postingTime, questionUrl, companyLogo);
			return question;
		}
		
		private Bitmap getBitmapFromUrl(String logoUrl) {
			Bitmap icon = null;
			try {
				URL url = new URL(logoUrl);
				icon = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return icon;
		}
		
		private String cleanPreserveLineBreaks(String bodyHtml) {
		    String prettyPrintedBodyFragment = Jsoup.clean(bodyHtml, "", Whitelist.none().addTags("br", "p", "code", "pre"), new OutputSettings().prettyPrint(true));
		    return Jsoup.clean(prettyPrintedBodyFragment, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
		}
	}
}
