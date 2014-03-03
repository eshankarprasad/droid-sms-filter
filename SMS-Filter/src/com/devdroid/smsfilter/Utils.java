package com.devdroid.smsfilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class Utils {
	public static final String KEY_CONTACT = "sms_filter_device_contact";
	static final String KEY_CATEGORIES = "categories";
	public static final String SEPERATOR_CONTACT_OUTER = "^";
	public static final String SEPERATOR_CONTACT_INNER = "~";
	public static final String SEPERATOR = ":";
	public static String trimContactNumber(String address) {
		
		String contactNumber = address.replaceAll(" ", "");
		String tempNumber = "";
		if(contactNumber.length() >= 10) {
			if(contactNumber.charAt(0) == '0') {
				tempNumber = contactNumber.substring(1);
			} else if(contactNumber.charAt(0) == '+') {
				tempNumber = contactNumber.substring(3);
			} else {
				tempNumber = contactNumber;
			}
		} else {
			tempNumber = contactNumber;
		}
		
		return tempNumber;
	}
	
	public static String getPersonName(String address, List<Contact> list) {
		String name = "";
		for (int i=0; i<list.size(); i++) {
			Contact contact = list.get(i);
			if(address.equals(contact.getContactNumber())) {
				name = contact.getName();
				break;
			}
		}
		
		return name;
	}
	
	public static String getDateTime(Long miliseconds) {
		
		Locale l = Locale.getDefault();
		Date date = new Date(miliseconds);
		SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy HH:mm a", l);
		String dateFormatted = formatter.format(date);
		return dateFormatted.replace("AM", "am").replace("PM", "pm");
	}
	
	public static void hideKeyboard(Activity activity, EditText editText) {
		
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
	
	public static void deleteCategory(Activity activity, Category category) {
		
		String categoriesText = SharedPreferencesUtil.getStringPreferences(activity, Utils.KEY_CATEGORIES);
		String categoryTextToDelete = category.getCategoryName();
		String newCategoriesText = "";
		if(categoriesText.contains(categoryTextToDelete + Utils.SEPERATOR)) {
			newCategoriesText = categoriesText.replace(categoryTextToDelete + Utils.SEPERATOR, "");
		} else if(categoriesText.contains(Utils.SEPERATOR + categoryTextToDelete)) {
			newCategoriesText = categoriesText.replace(Utils.SEPERATOR + categoryTextToDelete, "");
		} else {
			newCategoriesText = categoriesText.replace(categoryTextToDelete, "");
		}
		
		SharedPreferencesUtil.saveStringPreferences(activity, Utils.KEY_CATEGORIES, newCategoriesText);
		
		// deleting numbers associated with the category
		SharedPreferencesUtil.deletePreferences(activity, categoryTextToDelete);
		
		// deleting names associated with number
		String contactText = SharedPreferencesUtil.getStringPreferences(activity, categoryTextToDelete);
		String[] numbers = contactText.split(Utils.SEPERATOR);
		for (int i = 0; i < numbers.length; i++) {
			SharedPreferencesUtil.deletePreferences(activity, numbers[i]);
		}
	}
	
	public static void addContactsWithCategory(Activity activity, String categoryName, List<Contact> selectedContactList) {
		
		String contactsText = "";
		String delimeter = "";
		for(Contact contact: selectedContactList) {
			
			String trimNumber = Utils.trimContactNumber(contact.getContactNumber());
			contactsText = contactsText +  delimeter + trimNumber;
			delimeter = Utils.SEPERATOR;
			
			// Saving name for each number
			SharedPreferencesUtil.saveStringPreferences(activity, trimNumber, contact.getName());
			//Log.d("", trimContactNumber(contact.getContactNumber()) + " : " + contact.getName());
		}
		
		//Log.d("contactsText", contactsText);
		SharedPreferencesUtil.saveStringPreferences(activity, categoryName, contactsText);
	}
	
	public static List<Category> getCategories(Activity activity) {
		
		List<Category> list = new ArrayList<Category>();
		String categories = SharedPreferencesUtil.getStringPreferences(activity, Utils.KEY_CATEGORIES);
		String[] categoryArray = {};
		if(categories.length() != 0) {
			categoryArray = categories.split(Utils.SEPERATOR); 
		}
		
		for (int i = 0; i < categoryArray.length ; i++) {
			
			String associatedNumbersText = SharedPreferencesUtil.getStringPreferences(activity, categoryArray[i]);
			String[] numbers = associatedNumbersText.split(Utils.SEPERATOR);
			Category category = new Category();
			category.setCategoryName(categoryArray[i]);
			category.setMessageCount(numbers.length);
			list.add(category);
		}
		
		return list;
	}
}
