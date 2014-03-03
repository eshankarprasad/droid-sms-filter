package com.devdroid.smsfilter;

public class Category {

	private String categoryName;
	private int addressCount;
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public int getMessageCount() {
		return addressCount;
	}
	public void setMessageCount(int messageCount) {
		this.addressCount = messageCount;
	}
	@Override
	public String toString() {
		return "Category [categoryName=" + categoryName + ", addressCount="
				+ addressCount + "]";
	}
	
}
