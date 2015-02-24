package com.careercup.data;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.careercup.R;
import com.careercup.websitecrawler.QuestionDetails;

public class QuestionPreviewAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<QuestionDetails> mQuestionList;
	
	public QuestionPreviewAdapter(Context context, List<QuestionDetails> data) {
		mContext = context;
		mQuestionList = data;
	}

	@Override
	public int getCount() {
		return mQuestionList.size();
	}

	@Override
	public Object getItem(int position) {
		return mQuestionList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		QuestionPreviewViewHolder holder = null;
		
		if(row == null) {
			LayoutInflater inflator = ((Activity) mContext).getLayoutInflater();
			row = inflator.inflate(R.layout.fragment_question_list_item_layout, parent, false);
			holder = new QuestionPreviewViewHolder();
			holder.companyIcon = (ImageView) row.findViewById(R.id.ivCompanyIcon);
			holder.totalAnswers = (TextView) row.findViewById(R.id.tvNumAnswers);
			holder.questionText = (TextView) row.findViewById(R.id.tvQuestionDetail);
			holder.posterName = (TextView) row.findViewById(R.id.tvPosterName);
			holder.timePosted = (TextView) row.findViewById(R.id.tvPostingTime);
			row.setTag(holder);
		} else {
			holder = (QuestionPreviewViewHolder) row.getTag();
		}
		
		QuestionDetails question = mQuestionList.get(position);
		
		if(question.getCompanyIcon() != null) {
			holder.companyIcon.setImageBitmap(question.getCompanyIcon());
		}
		holder.totalAnswers.setText(question.getTotalAnswers());
		holder.questionText.setText(question.getQuestionText());
		holder.posterName.setText(question.getAuthorName());
		holder.timePosted.setText(question.getTimePosted());
		
		return row;
	}
	
	static class QuestionPreviewViewHolder {
		ImageView companyIcon;
		TextView totalAnswers;
		TextView questionText;
		TextView posterName;
		TextView timePosted;
	}
}
