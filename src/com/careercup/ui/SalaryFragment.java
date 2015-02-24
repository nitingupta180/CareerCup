package com.careercup.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.careercup.R;
import com.careercup.analytics.Action;
import com.careercup.analytics.AnalyticsGoogle;
import com.careercup.analytics.IAnalytics;
import com.careercup.data.SalaryAdapter;
import com.careercup.utils.Constants;
import com.careercup.websitecrawler.SalaryDetails;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class SalaryFragment extends Fragment {

	private final String TAG = SalaryFragment.class.getSimpleName();
	
	private Context mContext;
	List<SalaryDetails> mSalaryList = new LinkedList<SalaryDetails>();
	SalaryAdapter mSalaryAdapter;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mContext = getActivity().getApplicationContext();
		new LoadSalaryInfo().execute(Constants.SALARY_URL);
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
        checkin.analyticsCareerCupKPI(Action.SALARY_FRAGMENT, "onCreateView()", 0);
        
		mSalaryAdapter = new SalaryAdapter(getActivity(), mSalaryList);
		
		ListView salaries = (ListView) root.findViewById(R.id.listView);
		salaries.setAdapter(mSalaryAdapter);
		return root;
	}
	
	private class LoadSalaryInfo extends AsyncTask<String, Void, Void> {

		boolean isErrorCase = false;
		
		@Override
		protected Void doInBackground(String... params) {
			//Log.i(TAG, "Loading Salaries...");
			String url = params[0];
			isErrorCase = false;
			try {
				Connection connection = Jsoup.connect(url).userAgent(Constants.USER_AGENT).timeout(10000);
				Document doc = connection.get();
				Elements companies = doc.select("div[class=salary_company]");
				for(Element company : companies) {
					String companyName = company.select("h2").text();
					SalaryDetails salaryItem = new SalaryDetails(companyName, "", "", null, Constants.SALARY_TYPE.TYPE_SECTON_HEADER.ordinal());
					mSalaryList.add(salaryItem);
					Elements companySalary = company.select("li");
					for(Element salary : companySalary) {
						getSalary(salary, companyName);
					}
					
				}
			} catch (IOException e) {
				isErrorCase = true;
				//Log.e(TAG, "Some error occurred while connecting");
			}
			
			return null;
		}
		
		private void getSalary(Element salary, String companyName) {
			String salaryText = salary.select("span[class=entry]").text();
			String authorDetails = salary.select("span[class=author]").text();
			String flags = salary.select("span[class=author] a").text();
			salaryText = salaryText.substring(0, salaryText.indexOf(authorDetails));
			//authorDetails = authorDetails.substring(0, authorDetails.indexOf(flags));
			String companyLogoUrl = salary.select("img").first().attr("abs:src");
			Bitmap companyLogo = getBitmapFromUrl(companyLogoUrl);
			
			SalaryDetails salaryItem = new SalaryDetails(companyName, salaryText, authorDetails, companyLogo, Constants.SALARY_TYPE.TYPE_SALARY_INFO.ordinal());
			mSalaryList.add(salaryItem);
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

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			Toast.makeText(mContext, "Loading New Salaries...", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(isErrorCase) {
				Toast.makeText(mContext, Constants.ERROR_TOAST_MESSAGE, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(mContext, "New Salaries Added!!!", Toast.LENGTH_SHORT).show();
			}
			mSalaryAdapter.notifyDataSetChanged();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}
		
	}
}
