package com.careercup.analytics;

import android.content.Context;

import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.MapBuilder;
import com.google.analytics.tracking.android.Tracker;


public class AnalyticsGoogle implements IAnalytics {

	private static String TAG="AnalyticsGoogle";
    private static final String TRACKER_ID = "UA-59749199-1"; 
    private Tracker mTracker;
    public static IAnalytics mCheckin = null;
	
    AnalyticsGoogle (Context context) {
    	//Log.d(TAG, "GOOGLE ANALYTIVCS:AnalyticsGoogle() ");
		GoogleAnalytics instance = GoogleAnalytics.getInstance(context);
        instance.setDryRun(false);
        instance.getLogger().setLogLevel(LogLevel.INFO);
        mTracker = instance.getTracker(TRACKER_ID);
	}
    
    public static void createAnalytic(Context context) {
        mCheckin = new AnalyticsGoogle(context);
    }
    
    public static IAnalytics getAnalytic() {
     	return mCheckin;
    }
    
	
    private static MapBuilder createEvent(final String category, final String action,	final String label) {
		 //Log.d(TAG, "createEvent: " + "|category=" + category + " | action=" + action + "| label=" + label);
         return MapBuilder.createEvent(category, action, label, null);
    }
	 
    private static MapBuilder createEvent(final String category, final String action, final String label, final long value) {
        //Log.d(TAG, "createEvent: " + "|category=" + category + " | action=" + action + "| label=" + label + "| value=" + value);
		return MapBuilder.createEvent(category, action, label, value);
    }

	
	
	@Override
	public void analyticsCareerCupKPI(String action, String lable, long value) {
        if (mTracker != null) {
        	mTracker.send(createEvent(Category.CAREERCUP_KPI, action, lable).build());
        }
	}

}
