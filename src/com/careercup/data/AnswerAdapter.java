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
import com.careercup.websitecrawler.AnswerDetails;

public class AnswerAdapter extends BaseAdapter {

	private Context mContext;
	private List<AnswerDetails> mAnswerList;
	
	public AnswerAdapter(Context context, List<AnswerDetails> answers) {
		mContext = context;
		mAnswerList = answers;
	}
	
	@Override
	public int getCount() {
		return mAnswerList.size();
	}

	@Override
	public Object getItem(int position) {
		return mAnswerList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		AnswerViewHolder holder = null;
		
		if(row == null) {
			LayoutInflater inflator = ((Activity) mContext).getLayoutInflater();
			row = inflator.inflate(R.layout.activity_answer_list, parent, false);
			holder = new AnswerViewHolder();
			holder.netVotes = (TextView) row.findViewById(R.id.tvNetVotes);
			holder.text = (TextView) row.findViewById(R.id.tvAnswerText);
			holder.postingDetails = (TextView) row.findViewById(R.id.tvPostingDetails);
			row.setTag(holder);
		} else {
			holder = (AnswerViewHolder) row.getTag();
		}
		
		AnswerDetails answer = mAnswerList.get(position);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		if(!answer.isAnswer()) {
			layoutParams.setMargins(50, 0, 0, 0);
		} else {
			layoutParams.setMargins(0, 0, 0, 0);
		}
		row.findViewById(R.id.rlMain).setLayoutParams(layoutParams);
		
		holder.netVotes.setText(answer.getNetVotes());
		holder.text.setText(answer.getText());
		holder.postingDetails.setText(answer.getPostingDetails());
		
		return row;
	}
	
	static class AnswerViewHolder {
		TextView netVotes;
		TextView text;
		TextView postingDetails;
	}
}
