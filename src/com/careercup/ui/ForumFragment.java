package com.careercup.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
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
import com.careercup.data.ForumPreviewAdapter;
import com.careercup.database.CareerCupDB;
import com.careercup.utils.Constants;
import com.careercup.utils.Utils;
import com.careercup.websitecrawler.ForumDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ForumFragment extends Fragment {
	
	private final String TAG = ForumFragment.class.getSimpleName();
	
	private Context mContext;
    List<ForumDetails> mForums = new ArrayList<ForumDetails>();
    ForumPreviewAdapter mForumListAdapter;
    SwipeRefreshLayout mSwipeToRefresh;
    private int mNextPageNumToLoad = 1;
	private boolean mIsPageLoading = false;
	private String mLastSavedForumUrl = null;
	private boolean mIsFragmentCreated = true;
	private HashSet<String> forumSet = new HashSet<String>();
    
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
        checkin.analyticsCareerCupKPI(Action.FORUM_FRAGMENT, "onCreateView()", 0);
        
		mForumListAdapter = new ForumPreviewAdapter(getActivity(), mForums);
		
		mLastSavedForumUrl = Utils.getLatestForumPostSaved(mContext);
		
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
				mLastSavedForumUrl = Utils.getLatestForumPostSaved(mContext);
				forumSet.clear();
				if(!mIsPageLoading) {
					new ForumsPreview().execute(Constants.FORUM_PAGES_URL + 1);
				}
			}
		});
		
		ListView forums = (ListView) root.findViewById(R.id.listView);
		forums.setAdapter(mForumListAdapter);
		
		forums.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				int count = view.getCount();
				if(scrollState == SCROLL_STATE_IDLE) {
					if(!mIsPageLoading && view.getLastVisiblePosition() >= count - Constants.LOAD_THRESHOLD_VISIBLE_FORUMS) {
						//Log.i(TAG, "Scolled to Refresh content");
						mLastSavedForumUrl = Utils.getLatestForumPostSaved(mContext);
						new ForumsPreview().execute(Constants.FORUM_PAGES_URL + mNextPageNumToLoad);
						mIsPageLoading = true;
					}
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
		
		forums.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ForumDetails forumDetails = mForums.get(position);
				Intent intent = new Intent(getActivity().getApplicationContext(), ForumActivity.class);
				intent.putExtra(Constants.TOTAL_ANSWERS, forumDetails.getTotalAnswers());
				intent.putExtra(Constants.FORUM_HEADER, forumDetails.getForumHeaderText());
				intent.putExtra(Constants.FORUM_TEXT, forumDetails.getForumText());
				intent.putExtra(Constants.FORUM_POSTER_NAME, forumDetails.getAuthorName());
				intent.putExtra(Constants.FORUM_POSTING_TIME, forumDetails.getTimePosted());
				intent.putExtra(Constants.SINGLE_FORUM_URL, forumDetails.getForumUrl());
				//Log.i(TAG, "Starting Forum Activity");
				startActivity(intent);
			}
		});
		
		if(mIsFragmentCreated) {
			//Log.i(TAG, "Fragment is created");
			CareerCupDB db = new CareerCupDB(mContext);
			ArrayList<ForumDetails> forumList = db.getSavedForumPosts();
			if(forumList.size() > 0) {
				//Log.i(TAG, "Populating content from DB");
				mForums.addAll(0, forumList);
				mForumListAdapter.notifyDataSetChanged();
				for(ForumDetails f : forumList) {
					forumSet.add(f.getForumUrl());
				}
				int lastPageSaved = Utils.getLastForumPageSaved(mContext);
				//Log.i(TAG, "Last saved page number - " + lastPageSaved);
				mNextPageNumToLoad = lastPageSaved + 1;
			} else {
				//Log.i(TAG, "Getting content (first time operation only)");
				new ForumsPreview().execute(Constants.FORUM_PAGES_URL + mNextPageNumToLoad);
			}
			mIsFragmentCreated = false;
		}
		return root;
	}
	
	private class ForumsPreview extends AsyncTask<String, Void, Void> {

		boolean isErrorCase = false;
		
		@Override
		protected Void doInBackground(String... params) {
			//Log.i(TAG, "Loading more forum article");
			String url = params[0];
			mIsPageLoading = true;
			isErrorCase = false;
			boolean loadPreviousPage = false;
			try {
				Connection connection = Jsoup.connect(url).userAgent(Constants.USER_AGENT).timeout(10000);
				Document doc = connection.get();
				Elements listTags = doc.select("ul li");
				
				List<ForumDetails> forumList = new ArrayList<ForumDetails>();
				for(Element item : listTags) {
					if(item.text().contains("Answers") || item.text().contains("Answer")) {
						ForumDetails forum = getForumObject(item);
						if(forumSet.size() > 0 && forumSet.contains(forum.getForumUrl())) {
							loadPreviousPage = true;
						}
						forumList.add(forum);
					}
				}
				
				if(loadPreviousPage) {
					//Log.i(TAG, "Loading previous page (content has been changed");
					int pageNum = mNextPageNumToLoad - 1;
					if(pageNum < 1) {
						pageNum = 1;
					}
					//Log.i(TAG, "Previous page number - " + pageNum);
					Connection conn = Jsoup.connect(Constants.FORUM_PAGES_URL + pageNum).userAgent(Constants.USER_AGENT).timeout(10000);
					Document document = conn.get();
					Elements listTag = document.select("ul li");
					
					List<ForumDetails> fList = new ArrayList<ForumDetails>();
					for(Element item : listTags) {
						if(item.text().contains("Answers") || item.text().contains("Answer")) {
							ForumDetails forum = getForumObject(item);
							fList.add(forum);
						}
					}
					
					forumList.addAll(0, fList);
					forumSet.clear();
					fList.clear();
					fList = null;
				}
				
				if(mLastSavedForumUrl == null) {
					//Log.i(TAG, "Loading forums for the first time only");
					CareerCupDB db = new CareerCupDB(mContext);
					db.deleteForumsTableContent();
					db.addForumsToDB(forumList);
					db.close();
					Utils.setLatestForumPostSaved(mContext, forumList.get(0).getForumUrl());
					Utils.setLastForumPageSaved(mContext, mNextPageNumToLoad);
				} else if(mSwipeToRefresh.isRefreshing() && !mLastSavedForumUrl.equalsIgnoreCase(forumList.get(0).getForumUrl())) {
					//Log.i(TAG, "Loading forums for swipe to refresh action");
					CareerCupDB db = new CareerCupDB(mContext);
					db.deleteForumsTableContent();
					db.addForumsToDB(forumList);
					db.close();
					Utils.setLastForumPageSaved(mContext, 1);
					Utils.setLatestForumPostSaved(mContext, forumList.get(0).getForumUrl());
				}
				
				if(mSwipeToRefresh.isRefreshing()) {
					if(!mLastSavedForumUrl.equalsIgnoreCase(forumList.get(0).getForumUrl())) {
						mForums.clear();
						mForums.addAll(forumList);
						mNextPageNumToLoad = 2;
					}
					forumSet.clear();
				} else {
					mNextPageNumToLoad++;
					if(loadPreviousPage) {
						mForums.clear();
					}
					mForums.addAll(forumList);
				}
				forumList.clear();
				forumList = null;
			} catch (IOException e) {
				isErrorCase = true;
				//Log.e(TAG, "Some error occurred while connecting");
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(mContext, "Loading New Forum Posts...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(isErrorCase) {
				Toast.makeText(mContext, Constants.ERROR_TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "New Forum Posts Added!!!", Toast.LENGTH_SHORT).show();
			}
			if(mSwipeToRefresh.isRefreshing()) {
				//Log.i(TAG, "Stopping swipe to refresh animation");
				mSwipeToRefresh.setRefreshing(false);
			}
			mForumListAdapter.notifyDataSetChanged();
			mIsPageLoading = false;
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
		
		private ForumDetails getForumObject(Element item) {
			Elements rating = item.select("span[class=rating]");
			String totalAnswers = rating.text();
			String forumUrl = rating.select("a").first().attr("abs:href");
			
			Elements author = item.select("span[class=author]");
			String posterName = "- " + author.select("a").get(0).text();
			String postingTime = " " + author.select("abbr").text();
			
			Elements forumHeader = item.select("span[class=entry] a");
			String forumHeaderText = forumHeader.select("strong").text();
			String forumText = cleanPreserveLineBreaks(forumHeader.select("p").html());
			
			ForumDetails forum = new ForumDetails(totalAnswers, forumHeaderText, forumText, posterName, postingTime, forumUrl);
			return forum;
		}
		
		private String cleanPreserveLineBreaks(String bodyHtml) {
		    String prettyPrintedBodyFragment = Jsoup.clean(bodyHtml, "", Whitelist.none().addTags("br", "p"), new OutputSettings().prettyPrint(true));
		    return Jsoup.clean(prettyPrintedBodyFragment, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
		}
		
	}
}
