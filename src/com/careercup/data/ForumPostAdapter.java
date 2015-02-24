package com.careercup.data;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.careercup.R;
import com.careercup.websitecrawler.ForumPostDetails;

public class ForumPostAdapter extends BaseAdapter {

	private Context mContext;
	private List<ForumPostDetails> mForumList;
	
	public ForumPostAdapter(Context context, List<ForumPostDetails> forumList) {
		mContext = context;
		mForumList = forumList;
	}
	
	@Override
	public int getCount() {
		return mForumList.size();
	}

	@Override
	public Object getItem(int position) {
		return mForumList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		ForumPostViewHolder holder = null;
		if(row == null) {
			LayoutInflater inflator = ((Activity) mContext).getLayoutInflater();
			row = inflator.inflate(R.layout.activity_answer_list, parent, false);
			holder = new ForumPostViewHolder();
			holder.netVotes = (TextView) row.findViewById(R.id.tvNetVotes);
			holder.text = (TextView) row.findViewById(R.id.tvAnswerText);
			holder.postingDetails = (TextView) row.findViewById(R.id.tvPostingDetails);
			row.setTag(holder);
		} else {
			holder = (ForumPostViewHolder) row.getTag();
		}
		
		ForumPostDetails forum = mForumList.get(position);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if(!forum.isAnswer()) {
			layoutParams.setMargins(50, 0, 0, 0);
		} else {
			layoutParams.setMargins(0, 0, 0, 0);
		}
		row.findViewById(R.id.rlMain).setLayoutParams(layoutParams);
		
		holder.netVotes.setText(forum.getNetVotes());
		holder.text.setText(forum.getText());
		holder.postingDetails.setText(forum.getPostingDetails());
		
		return row;
	}
	
	static class ForumPostViewHolder {
		TextView netVotes;
		TextView text;
		TextView postingDetails;
	}

}
