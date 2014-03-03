package com.devdroid.smsfilter;

public class Contact {
	private String name;
	private String contactNumber;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getContactNumber() {
		return contactNumber;
	}
	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}
	@Override
	public String toString() {
		return "Contact [name=" + name + ", contactNumber=" + contactNumber
				+ "]";
	}
}
