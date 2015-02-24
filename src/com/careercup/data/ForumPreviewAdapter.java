package com.careercup.data;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.careercup.R;
import com.careercup.websitecrawler.ForumDetails;

public class ForumPreviewAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<ForumDetails> mForumList;
	
	public ForumPreviewAdapter(Context context, List<ForumDetails> data) {
		mContext = context;
		mForumList = data;
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
		ForumPreviewViewHolder holder = null;
		if(row == null) {
			LayoutInflater inflator = ((Activity) mContext).getLayoutInflater();
			row = inflator.inflate(R.layout.fragment_forum_list_item_layout, parent, false);
			holder = new ForumPreviewViewHolder();
			holder.totalAnswers = (TextView) row.findViewById(R.id.tvNumAnswers);
			holder.forumHeaderText = (TextView) row.findViewById(R.id.tvForumHeader);
			holder.forumText = (TextView) row.findViewById(R.id.tvForumText);
			holder.posterName = (TextView) row.findViewById(R.id.tvPosterName);
			holder.timePosted = (TextView) row.findViewById(R.id.tvPostingTime);
			row.setTag(holder);
		} else {
			holder = (ForumPreviewViewHolder) row.getTag();
		}
		
		ForumDetails forum = mForumList.get(position);

		holder.totalAnswers.setText(forum.getTotalAnswers());
		holder.forumHeaderText.setText(forum.getForumHeaderText());
		holder.forumText.setText(forum.getForumText());
		holder.posterName.setText(forum.getAuthorName());
		holder.timePosted.setText(forum.getTimePosted());
		
		return row;
	}

	static class ForumPreviewViewHolder {
		TextView totalAnswers;
		TextView forumHeaderText;
		TextView forumText;
		TextView posterName;
		TextView timePosted;
	}
}
