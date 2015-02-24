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
import com.careercup.utils.Constants;
import com.careercup.websitecrawler.SalaryDetails;

public class SalaryAdapter extends BaseAdapter {

	private Context mContext;
	private List<SalaryDetails> mSalaryList;
	
	public SalaryAdapter(Context context, List<SalaryDetails> salaryList) {
		mContext = context;
		mSalaryList = salaryList;
	}

	@Override
	public int getCount() {
		return mSalaryList.size();
	}

	@Override
	public Object getItem(int position) {
		return mSalaryList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		SalaryHolder holder = null;
		
		if(row == null) {
			LayoutInflater inflator = ((Activity) mContext).getLayoutInflater();
			row = inflator.inflate(R.layout.fragment_salary_list_item_layout, parent, false);
			holder = new SalaryHolder();
			holder.companyIcon = (ImageView) row.findViewById(R.id.ivCompanyIcon);
			holder.companyName = (TextView) row.findViewById(R.id.tvCompanyName);
			holder.salaryText = (TextView) row.findViewById(R.id.tvSalaryDetail);
			holder.authorDetails = (TextView) row.findViewById(R.id.tvAuthorDetails);
			row.setTag(holder);
		} else {
			holder = (SalaryHolder) row.getTag();
		}
		
		SalaryDetails salary = mSalaryList.get(position);
		
		if(salary.getSalaryType() == Constants.SALARY_TYPE.TYPE_SECTON_HEADER.ordinal()) {
			holder.companyIcon.setVisibility(View.GONE);
			holder.salaryText.setVisibility(View.GONE);
			holder.authorDetails.setVisibility(View.GONE);
			holder.companyName.setVisibility(View.VISIBLE);
			
			holder.companyName.setText(salary.getCompanyName());
		} else {
			holder.companyIcon.setVisibility(View.VISIBLE);
			holder.salaryText.setVisibility(View.VISIBLE);
			holder.authorDetails.setVisibility(View.VISIBLE);
			holder.companyName.setVisibility(View.GONE);
			
			holder.companyIcon.setImageBitmap(salary.getCompanyLogo());
			holder.salaryText.setText(salary.getSalaryText());
			holder.authorDetails.setText(salary.getAuthorDetails());
		}
		
		return row;
	}
	
	static class SalaryHolder {
		ImageView companyIcon;
		TextView companyName;
		TextView salaryText;
		TextView authorDetails;
	}
}
