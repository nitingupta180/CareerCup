package com.careercup.websitecrawler;

import android.graphics.Bitmap;

public class SalaryDetails {
	private String mCompanyName;
	private String mSalaryText;
	private String mAuthorDetails;
	private Bitmap mCompanyLogo;
	private int mSalaryType;
	
	public SalaryDetails(String company, String salary, String author, Bitmap logo, int salaryType) {
		mCompanyName = company;
		mSalaryText = salary;
		mAuthorDetails = author;
		mCompanyLogo = logo;
		mSalaryType = salaryType;
	}

	public String getCompanyName() {
		return mCompanyName;
	}

	public String getSalaryText() {
		return mSalaryText;
	}

	public String getAuthorDetails() {
		return mAuthorDetails;
	}

	public Bitmap getCompanyLogo() {
		return mCompanyLogo;
	}
	
	public int getSalaryType() {
		return mSalaryType;
	}
}
