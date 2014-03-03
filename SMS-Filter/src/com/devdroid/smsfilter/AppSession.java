package com.devdroid.smsfilter;

import java.util.List;
import java.util.Map;

import android.app.Application;

public class AppSession extends Application {
	private List<Contact> phoneContacts;
	private Map<String, List<SMSListItem>> map;
	public List<Contact> getPhoneContacts() {
		return phoneContacts;
	}

	public void setPhoneContacts(List<Contact> phoneContacts) {
		this.phoneContacts = phoneContacts;
	}

	public Map<String, List<SMSListItem>> getMap() {
		return map;
	}

	public void setMap(Map<String, List<SMSListItem>> map) {
		this.map = map;
	}
	
}
