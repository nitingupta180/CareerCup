package com.careercup.ui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.careercup.R;
import com.careercup.analytics.AnalyticsGoogle;
import com.careercup.database.CareerCupDB;
import com.careercup.utils.Constants;
import com.careercup.utils.Utils;


public class CareerCupHomeActivity extends FragmentActivity implements OnItemClickListener {

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String mTitle;
	private ArrayList<String> mDrawerKeys;
	private Map<String, DrawerItem> mDrawerListItems;
	private DrawerAdapter mDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.configureActionBarStyle(this);
        CareerCupDB db = new CareerCupDB(getApplicationContext());
        setContentView(R.layout.activity_career_cup_home);
        
        AnalyticsGoogle.createAnalytic(getApplicationContext());
        
        //mTitle = getActionBar().getTitle();
        mDrawerListItems = new LinkedHashMap<String, DrawerItem>();
        mDrawerKeys = new ArrayList<String>();
        mDrawerKeys.add(getString(R.string.questions));
        mDrawerKeys.add(getString(R.string.forum));
        mDrawerKeys.add(getString(R.string.salaries));
        //mDrawerKeys.add(getString(R.string.resume_tips));
        //mDrawerKeys.add(getString(R.string.rss));
        
        mDrawerListItems.put(getString(R.string.questions), new DrawerItem(getString(R.string.questions)));
        mDrawerListItems.put(getString(R.string.forum), new DrawerItem(getString(R.string.forum)));
        mDrawerListItems.put(getString(R.string.salaries), new DrawerItem(getString(R.string.salaries)));
        //mDrawerListItems.put(getString(R.string.resume_tips), new DrawerItem(getString(R.string.resume_tips)));
        //mDrawerListItems.put(getString(R.string.rss), new DrawerItem(getString(R.string.rss)));
        
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        
        //Set custom shadow when drawer opens
        //mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        //Set up Drawer's listview with items
        mDrawerAdapter = new DrawerAdapter(this);
        mDrawerList.setAdapter(mDrawerAdapter);
        
        //Enable action bar icon to toggle navigation drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        //Set ActionBarToggle to sliding drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, 
        		R.string.drawer_open, R.string.drawer_close) {
        	
        	@Override
            public void onDrawerClosed(View view) {
        		if(mTitle != null && mTitle.length() > 0) {
        			getActionBar().setTitle(mTitle);
        		} else {
        			mTitle = getString(R.string.app_name);
        			getActionBar().setTitle(mTitle);
        		}
        		setActionBarIcon();
                invalidateOptionsMenu();
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(getString(R.string.app_name));
                getActionBar().setIcon(R.drawable.ic_launcher);
                invalidateOptionsMenu();
            }
        };
        mDrawerToggle.syncState();
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        
        mDrawerList.setOnItemClickListener(this);
        
        if(savedInstanceState == null) {
        	mTitle = getString(R.string.questions);
        	getActionBar().setTitle(mTitle);
        	setActionBarIcon();
        	mDrawerList.setItemChecked(0, true);
    		openFragment(0);
        }
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    }
    
    
    private void setActionBarIcon() {
    	if(mTitle.equals(getString(R.string.questions))) {
    		getActionBar().setIcon(R.drawable.ic_question_selected);
    	} else if(mTitle.equals(getString(R.string.forum))){
    		getActionBar().setIcon(R.drawable.ic_forum_selected);
    	} else if(mTitle.equals(getString(R.string.salaries))){
    		getActionBar().setIcon(R.drawable.ic_salary_selected);
    	}
    }
    
    private void openFragment(int index) {
    	if(mDrawerLayout.isDrawerOpen(mDrawerList)) {
    		mDrawerLayout.closeDrawer(mDrawerList);
    	}
    	
    	Fragment fragment = null;
    	Bundle args = null;
    	String fragmentTag = null;
    	
    	if(index == 0) {
            fragment = new QuestionsFragment();
            mTitle = getString(R.string.questions);
            args = new Bundle();
            args.putString(Constants.FRAGMENT_TITLE, mTitle);
            fragment.setArguments(args);
            fragmentTag = Constants.TAG_FRAGMENT_QUESTIONS;
    	} else if(index == 1) {
            fragment = new ForumFragment();
            mTitle = getString(R.string.forum);
            args = new Bundle();
            args.putString(Constants.FRAGMENT_TITLE, mTitle);
            fragment.setArguments(args);
            fragmentTag = Constants.TAG_FRAGMENT_FORUM;
    	} else if(index == 2) {
            fragment = new SalaryFragment();
            mTitle = getString(R.string.salaries);
            args = new Bundle();
            args.putString(Constants.FRAGMENT_TITLE, mTitle);
            fragment.setArguments(args);
            fragmentTag = Constants.TAG_FRAGMENT_SALARIES;
    	}/* else if(index == 3) {
            fragment = new ResumeTipsFragment();
            args = new Bundle();
            args.putString(Constants.FRAGMENT_TITLE, mCurrentHeader);
            fragment.setArguments(args);
            mTitle = getString(R.string.resume_tips);
            fragmentTag = Constants.TAG_FRAGMENT_RESUME_TIPS;
    	} else if(index == 4) {
            fragment = new RSSFragment();
            args = new Bundle();
            args.putString(Constants.FRAGMENT_TITLE, mCurrentHeader);
            fragment.setArguments(args);
            mTitle = getString(R.string.rss);
            fragmentTag = Constants.TAG_FRAGMENT_RSS;
    	}*/
    	
    	FragmentManager fragmentManager = getFragmentManager();
    	if(fragment != null && fragmentManager.findFragmentByTag(fragmentTag) == null) {
    		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment, fragmentTag).commit();
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.career_cup_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	if(mDrawerToggle.onOptionsItemSelected(item)) {
    		return true;
    	}
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (mDrawerKeys != null) {
            mDrawerKeys.clear();
            mDrawerKeys = null;
        }

        if (mDrawerListItems != null) {
            mDrawerListItems.clear();
            mDrawerListItems = null;
        }
    }
    
    private static class DrawerItem {
        public String drawerTitle = null;

        public DrawerItem(String title) {
        	drawerTitle = title;
        }
    }
    
    public class DrawerAdapter extends BaseAdapter {
    	
    	public DrawerAdapter(Context context) {
    		
        }
    	
        @Override
        public int getCount() {
            return mDrawerKeys.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
        	final String currentHeader = mDrawerKeys.get(position);

            if(convertView == null) {
            	convertView = getLayoutInflater().inflate(R.layout.drawer_list_item, null);
            }
        	
            TextView tvTitle = (TextView) convertView.findViewById(R.id.text1);
        	if (tvTitle != null) {
        		tvTitle.setText(currentHeader);
            }
        	
        	ImageView icon = (ImageView) convertView.findViewById(R.id.icon2);
        	if(currentHeader.equalsIgnoreCase(getString(R.string.questions))) {
        		if(((ListView) parent).isItemChecked(position)) {
        			tvTitle.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            		icon.setImageResource(R.drawable.ic_question_selected);
            	} else {
            		tvTitle.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            		icon.setImageResource(R.drawable.ic_question);
            	}
        	} else if(currentHeader.equalsIgnoreCase(getString(R.string.forum))) {
        		if(((ListView) parent).isItemChecked(position)) {
        			tvTitle.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            		icon.setImageResource(R.drawable.ic_forum_selected);
            	} else {
            		tvTitle.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            		icon.setImageResource(R.drawable.ic_forum);
            	}
        	} else if(currentHeader.equalsIgnoreCase(getString(R.string.salaries))) {
        		if(((ListView) parent).isItemChecked(position)) {
        			tvTitle.setTypeface(Typeface.create("sans-serif", Typeface.BOLD));
            		icon.setImageResource(R.drawable.ic_salary_selected);
            	} else {
            		tvTitle.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            		icon.setImageResource(R.drawable.ic_salary);
            	}
        	}
        	icon.setVisibility(View.VISIBLE);
        	
            return convertView;
        }
    }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mDrawerList.setItemChecked(position, true);
		openFragment(position);
	}
}
