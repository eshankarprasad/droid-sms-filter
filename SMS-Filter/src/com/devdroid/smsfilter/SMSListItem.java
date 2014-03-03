package com.devdroid.smsfilter;

public class SMSListItem {
	
	private SMS sms;

	public SMS getSms() {
		return sms;
	}

	public void setSms(SMS sms) {
		this.sms = sms;
	}

	@Override
	public String toString() {
		return "SMSListItem [sms=" + sms + "]";
	}
}
