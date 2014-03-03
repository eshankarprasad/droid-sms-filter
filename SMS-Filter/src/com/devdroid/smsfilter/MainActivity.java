package com.devdroid.smsfilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.AdView;

public class MainActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;
	ListView listViewMesseges;
	ListView listViewInbox;
	ListView listViewCategoryInbox;
	ListView listViewCategory;
	List<Contact> contactList;
	int currentPageSelectedIndex;
	private AdView adView;
	
	@Override
	protected void onDestroy() {
		if(adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		String deviceContactsText = SharedPreferencesUtil.getStringPreferences(this, Utils.KEY_CONTACT);
		AppSession session = (AppSession) getApplication();
		
		if(deviceContactsText.length() > 0) {
			String contactText = SharedPreferencesUtil.getStringPreferences(this, Utils.KEY_CONTACT);			
			String[] contactArray = contactText.split("\\" + Utils.SEPERATOR_CONTACT_OUTER);
    		List<Contact> contactList = new ArrayList<Contact>();
    		for (int i = 0; i < contactArray.length; i++) {
				String[] contact = contactArray[i].split(Utils.SEPERATOR_CONTACT_INNER);
				if(contact.length > 0) {
					//Log.d(contact[0], contact[1]);
					Contact contactObj = new Contact();
					contactObj.setName(contact[0]);
					contactObj.setContactNumber(contact[1]);
					contactList.add(contactObj);
				}
			}

    		if(contactList.size() > 0) {
	    		session.setPhoneContacts(contactList);
    		}
    		this.contactList = contactList;
			new FetchDeviceSMSTask().execute();
		} else {
			new FetchDeviceContactTask().execute();
		}
		
		/*// Create an ad.
		adView = (AdView) findViewById(R.id.adView);

		// Create an ad request.
		AdRequest adRequest = new AdRequest();
		// Fill out ad request.

		// Start loading the ad in the background.
		adView.loadAd(adRequest);
		
		adView.setAdListener(new AdListener() {     
	        public void onReceiveAd(Ad arg0) {
	        	adView.setVisibility(View.VISIBLE);
	        }           
	        public void onPresentScreen(Ad arg0) {
	        	
	        }           
	        public void onLeaveApplication(Ad arg0) {

	        }

	        public void onFailedToReceiveAd(Ad arg0, ErrorCode arg1) {
	        	Log.e("AdView", "onFailedToReceiveAd");
	        	adView.setVisibility(View.GONE);
	        }

	        public void onDismissScreen(Ad arg0) {

	        }
		});*/
	}
	
	@Override
	protected void onResume() {
		
		String deviceContactsText = SharedPreferencesUtil.getStringPreferences(this, Utils.KEY_CONTACT);
		AppSession session = (AppSession) getApplication();
		
		if(session.getMap() == null) {
			
			if(deviceContactsText.length() > 0) {
				String contactText = SharedPreferencesUtil.getStringPreferences(this, Utils.KEY_CONTACT);			
				String[] contactArray = contactText.split("\\" + Utils.SEPERATOR_CONTACT_OUTER);
	    		List<Contact> contactList = new ArrayList<Contact>();
	    		for (int i = 0; i < contactArray.length; i++) {
					String[] contact = contactArray[i].split(Utils.SEPERATOR_CONTACT_INNER);
					if(contact.length > 0) {
						Contact contactObj = new Contact();
						contactObj.setName(contact[0]);
						contactObj.setContactNumber(contact[1]);
						contactList.add(contactObj);
					}
				}

	    		if(contactList.size() > 0) {
		    		session.setPhoneContacts(contactList);
	    		}
	    		this.contactList = contactList;
				new FetchDeviceSMSTask().execute();
			} else {
				new FetchDeviceContactTask().execute();
			}
		}
		
		super.onResume();
	}
	
	private class FetchDeviceContactTask extends AsyncTask<String, Void, List<Contact>> {
		
		ProgressDialog progress;
		private String contactText;
		
		public FetchDeviceContactTask() {
			contactText = "";
		}
		
	    @Override
	    protected List<Contact> doInBackground(String... urls) {
	    	
	    	List<Contact> responseList = new ArrayList<Contact>();
			ContentResolver cr = getContentResolver();
	        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "display_name asc");
	        
			if (cur.getCount() > 0) {
				String seperator = "";
				while (cur.moveToNext()) {
					String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
					//String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
					
					if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
						Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
										new String[] { id }, null);
						while (pCur.moveToNext()) {
							
							String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
							//Log.d(contactName, phoneNo);
							Contact contact = new Contact();
							contact.setName(contactName);
							contact.setContactNumber(Utils.trimContactNumber(contactNumber));
							responseList.add(contact);
							
							contactText = contactText + seperator + contactName + Utils.SEPERATOR_CONTACT_INNER + Utils.trimContactNumber(contactNumber);
							seperator = Utils.SEPERATOR_CONTACT_OUTER;
						}
						pCur.close();
					}
				}
			}
			return responseList;
	    }

	    @Override
	    protected void onPostExecute(List<Contact> response) {
	    	
	    	if(response.size() > 0 && !contactText.equals("")) {
	    		AppSession session = (AppSession) getApplication();
	    		session.setPhoneContacts(response);
	    		MainActivity.this.contactList = response;
	    		SharedPreferencesUtil.saveStringPreferences(MainActivity.this, Utils.KEY_CONTACT, contactText);
	    		new FetchDeviceSMSTask().execute();
	    	}
	    	
	    	if(progress != null) {
	    		progress.dismiss();
	    		progress = null;
	    	}
	    }

	    @Override
	    protected void onCancelled() {
	    	super.onCancelled();
	    	if(progress != null) {
	    		progress.dismiss();
	    		progress = null;
	    	}
	    }
	    
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = ProgressDialog.show(MainActivity.this, "Configuring Application", "Please wait while applicatin setup itself for first time only. This may take a while...");
		}
	}
	
private class FetchDeviceSMSTask extends AsyncTask<String, Void, Map<String, List<SMSListItem>>> {
		
		ProgressDialog progress;
		public FetchDeviceSMSTask() {
			
		}
		
	    @Override
	    protected Map<String, List<SMSListItem>> doInBackground(String... urls) {
	    	// Start fetching sms inbox items
			Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, "date desc");
			/*
			 _id, thread_id, address, person, date, date_sent, protocol, read,
			 status, type, reply_path_present, subject, body, service_center,
			 locked, error_code, seen, deletable, hidden, group_id, group_type,
			 delivery_date, app_id, msg_id, callback_number, reserved, pri,
			 teleservice_id, link_url, 
			 */
			//String columns = "";
			
			List<SMS> inboxAllItems = new ArrayList<SMS>();
			List<String> addresses = new ArrayList<String>();
			Map<String, List<SMSListItem>> map = new LinkedHashMap<String, List<SMSListItem>>();
			
			if(cursor.moveToFirst()) {
				
				/*for(int i=0; i<cursor.getColumnCount(); i++) {
					columns += cursor.getColumnName(i) + " : " + cursor.getType(i) + "\n";
				}*/
				
				do {
					String address = Utils.trimContactNumber(cursor.getString(cursor.getColumnIndex("address")));
					String date = Utils.getDateTime(cursor.getLong(cursor.getColumnIndex("date")));
					if(!addresses.contains(address)) {
						addresses.add(address);
					}
					
					SMS sms = new SMS();
					
					sms.setId(cursor.getLong(cursor.getColumnIndex("_id")));
					sms.setAddress(address);
					sms.setBody(cursor.getString(cursor.getColumnIndex("body")));
					sms.setDate(date);
					sms.setDateSent(cursor.getLong(cursor.getColumnIndex("date_sent")));
					sms.setPerson(Utils.getPersonName(address, MainActivity.this.contactList));
					inboxAllItems.add(sms);
				} while(cursor.moveToNext());
				
				for(String address : addresses) {
					
					List<SMSListItem> smsList = new ArrayList<SMSListItem>();
					for(SMS sms : inboxAllItems) {
						
						if(address.equals(sms.getAddress())) {
							SMSListItem item = new SMSListItem();
							item.setSms(sms);
							smsList.add(item);
						}
					}
					
					map.put(address, smsList);
				}
			}
			// End fetching sms inbox items
			
			return map;
	    }

	    @Override
	    protected void onPostExecute(Map<String, List<SMSListItem>> response) {
	    	
	    	if(response.size() > 0) {
	    		AppSession session = (AppSession) getApplication();
	    		session.setMap(response);
	    		proceedLoading();
	    	}
	    	
	    	if(progress != null) {
	    		progress.dismiss();
	    		progress = null;
	    	}
	    }

	    @Override
	    protected void onCancelled() {
	    	super.onCancelled();
	    	if(progress != null) {
	    		progress.dismiss();
	    		progress = null;
	    	}
	    }
	    
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress = ProgressDialog.show(MainActivity.this, "Reading SMS", "Please wait...");
		}
	}
	
	private void proceedLoading() {
		
		AppSession session = (AppSession) getApplication();
		contactList = session.getPhoneContacts();
		
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		
		listViewMesseges = (ListView) findViewById(R.id.listview_messages);
		listViewCategoryInbox = (ListView) findViewById(R.id.listview_categories_inbox);
		listViewCategory = (ListView) findViewById(R.id.listview_categories);
		listViewInbox = (ListView) findViewById(R.id.listview_inbox);
		
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				currentPageSelectedIndex = arg0;
				
				switch (currentPageSelectedIndex) {
				case 0:
					
					if(listViewInbox == null) {
						listViewInbox = (ListView) findViewById(R.id.listview_inbox);
					}
					//listViewInbox.setVisibility(View.VISIBLE);
					((BaseAdapter)listViewInbox.getAdapter()).notifyDataSetChanged();
					
					/*if(listViewMesseges == null) {
						listViewMesseges = (ListView) findViewById(R.id.listview_messages);
					}
					listViewMesseges.setVisibility(View.GONE);*/
					
					break;
					
				case 1:
					
					if(listViewCategory == null) {
						listViewCategory = (ListView) findViewById(R.id.listview_categories);
					}
					//listViewCategory.setVisibility(View.VISIBLE);
					((BaseAdapter)listViewCategory.getAdapter()).notifyDataSetChanged();
					
					if(listViewCategoryInbox == null) {
						listViewCategoryInbox = (ListView) findViewById(R.id.listview_categories_inbox);
					}
					
					if(listViewCategoryInbox.getAdapter() != null) {
						((BaseAdapter)listViewCategoryInbox.getAdapter()).notifyDataSetChanged();
					}
					//listViewCategoryInbox.setVisibility(View.GONE);
					
					/*
					ListView listViewCategoryMesseges = (ListView) findViewById(R.id.listview_categories_messeges);
					listViewCategoryMesseges.setVisibility(View.GONE);
					View relativeLayout = (View) findViewById(R.id.relative_layout_add_category);
					relativeLayout.setVisibility(View.VISIBLE);*/
					break;

				default:
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}
		});
		
		AppRater.app_launched(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			/*Fragment fragment = new DummySectionFragment();
			Bundle args = new Bundle();
			args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);*/
			//Log.d("Get Item", "getItem");
			Fragment fragment = null;
			switch (position) {
			case 0:
				fragment = null;
				fragment = new InboxFragment();
				break;
			case 1:
				fragment = null;
				fragment = new CategoryFragment();
				break;
			default:
				fragment = null;
				fragment = new AboutFragment();
				break;
			}
			
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section_inbox).toUpperCase(l);
			case 1:
				return getString(R.string.title_section_categories).toUpperCase(l);
			case 2:
				return getString(R.string.title_section_about).toUpperCase(l);
			}
			return null;
		}
	}
	

	public static class AboutFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public AboutFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_list_about, container, false);
			ListView listViewAbout = (ListView) rootView.findViewById(R.id.listview_about);
			String[] array = {"Purchase Ad-free", "Rate this application"};
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
			          android.R.layout.simple_list_item_2, android.R.id.text1, array);
			listViewAbout.setAdapter(adapter);
			listViewAbout.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					switch (arg2) {
					case 1:
						AppRater.showRateDialog(getActivity(), null);
						break;

					default:
						break;
					}
				}
			});
			return rootView;
		}
	}
	
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	/*public static class AboutFragment extends Fragment {
		*//**
		 * The fragment argument representing the section number for this
		 * fragment.
		 *//*
		public static final String ARG_SECTION_NUMBER = "section_number";

		public AboutFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
			TextView dummyTextView = (TextView) rootView .findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
			return rootView;
		}
	}*/

	/**
	 * A Inbox fragment representing a section of the app, but that simply
	 * displays meassage senders list.
	 */
	public static class InboxFragment extends Fragment {
		
		ListView listViewInbox;
		ListView listViewMesseges;
		List<Contact> contactList;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_list_inbox,
					container, false);
			
			listViewInbox = (ListView) rootView.findViewById(R.id.listview_inbox);
			listViewMesseges = (ListView) rootView.findViewById(R.id.listview_messages);

			listViewInbox.setVisibility(View.VISIBLE);
			listViewMesseges.setVisibility(View.GONE);
			
			/******** Take some data in Arraylist ( CustomListViewValuesArr ) ***********/
			Resources res = getActivity().getResources();
			/**************** Create Custom Adapter *********/
			CustomInboxAdapter adapter = new CustomInboxAdapter(getActivity(), res);
			listViewInbox.setAdapter(adapter);
		
			AppSession session = (AppSession) getActivity().getApplication();
			if(session.getMap() == null) {
				((MainActivity) getActivity()).onResume();
			} else {
				adapter.notifyDataSetChanged();
			}
			
			// EDITED Code
			return rootView;
		}
	}
	
	/**
	 * A Inbox fragment representing a section of the app, but that simply
	 * displays meassage senders list.
	 */
	public static class CategoryFragment extends Fragment {
		
		private ListView listViewCategory;
		private View viewAddCategory;
		private List<Contact> selectedContactList;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			
			View rootView = inflater.inflate(R.layout.fragment_list_categories,
					container, false);
			
			selectedContactList = new ArrayList<Contact>();
			listViewCategory = (ListView) rootView.findViewById(R.id.listview_categories);
			viewAddCategory = (View) rootView.findViewById(R.id.relative_layout_add_category);
			viewAddCategory.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showAddCategoryDialog();
				}
			});
			
			loadCategoryListData();
			
			// EDITED Code
			return rootView;
		}
		
		public void loadCategoryListData() {
			Resources res = getActivity().getResources();
			CustomCategoryAdapter adapter = new CustomCategoryAdapter(getActivity(), this, Utils.getCategories(getActivity()), res);
			listViewCategory.setAdapter(adapter);
		}
		
		private void showAddCategoryDialog() {
			
			final Dialog dialog = new Dialog(getActivity());
			//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.dialog_add_category);
			dialog.setTitle("Add New Category");
			dialog.show();
			selectedContactList.clear();
			final EditText editTextCategoryName = (EditText) dialog.findViewById(R.id.edittext_category_name);
			
			// Displaying keyboard
			editTextCategoryName.requestFocus();
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(editTextCategoryName, InputMethodManager.SHOW_IMPLICIT);
			
			editTextCategoryName.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					editTextCategoryName.setError(null);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			
			ImageButton buttonSave = (ImageButton) dialog.findViewById(R.id.button_save);
			buttonSave.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Utils.hideKeyboard(getActivity(), editTextCategoryName);
					
					String categoryName = editTextCategoryName.getText().toString().trim();
					if(categoryName.equals("") || selectedContactList.size() <= 0) {
						if(categoryName.equals("")) {
							editTextCategoryName.setError("Requiered field");
						} else {
							Toast.makeText(getActivity(), "Select atleast one contact", Toast.LENGTH_SHORT).show();
						}
					} else {
						String olderCategoriesText = SharedPreferencesUtil.getStringPreferences(getActivity(), Utils.KEY_CATEGORIES);
						if(olderCategoriesText.equals("")) {
							// Creating category first time
							SharedPreferencesUtil.saveStringPreferences(getActivity(), Utils.KEY_CATEGORIES, categoryName);
							Utils.addContactsWithCategory(getActivity(), categoryName, selectedContactList);
						} else {
							Locale l = Locale.getDefault();
							if(olderCategoriesText.toLowerCase(l).contains(categoryName.toLowerCase(l))) {
								editTextCategoryName.setError("Category already exists");
								return;
							} else {
								// Creating category other than first time
								SharedPreferencesUtil.saveStringPreferences(getActivity(), Utils.KEY_CATEGORIES, olderCategoriesText + Utils.SEPERATOR + categoryName);
								Utils.addContactsWithCategory(getActivity(), categoryName, selectedContactList);
							}
						}
						
						loadCategoryListData();
						dialog.dismiss();
					}
				}
			});
			
			final ListView associaltedContactListView = (ListView) dialog.findViewById(R.id.listview_contact_list);
			View viewSelectContact = dialog.findViewById(R.id.relative_layout_add_contact);
			viewSelectContact.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Utils.hideKeyboard(getActivity(), editTextCategoryName);
					showSelectContactDialog(associaltedContactListView);
				}
			});
		}
		
		private void showEditCategoryDialog(final Category category) {
			
			String savedAssociatedNumbers = SharedPreferencesUtil.getStringPreferences(getActivity(), category.getCategoryName());
			selectedContactList.clear();
			String[] numbers = savedAssociatedNumbers.split(Utils.SEPERATOR);
			for (int i = 0; i < numbers.length; i++) {
				String name = SharedPreferencesUtil.getStringPreferences(getActivity(), numbers[i]);
				Contact contact = new Contact();
				contact.setName(name);
				contact.setContactNumber(numbers[i]);
				selectedContactList.add(contact);
			}
			
			final Dialog dialog = new Dialog(getActivity());
			//dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(R.layout.dialog_add_category);
			dialog.setTitle("Edit Category : " + category.getCategoryName());
			dialog.show();
			
			final EditText editTextCategoryName = (EditText) dialog.findViewById(R.id.edittext_category_name);
			editTextCategoryName.setText(category.getCategoryName());
			editTextCategoryName.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					editTextCategoryName.setError(null);
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {}
				
				@Override
				public void afterTextChanged(Editable s) {}
			});
			ImageButton buttonSave = (ImageButton) dialog.findViewById(R.id.button_save);
			buttonSave.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					Utils.hideKeyboard(getActivity(), editTextCategoryName);
					
					String newCategoryName = editTextCategoryName.getText().toString().trim();
					if(newCategoryName.equals("") || selectedContactList.size() <= 0) {
						if(newCategoryName.equals("")) {
							editTextCategoryName.setError("Requiered field");
						} else {
							Toast.makeText(getActivity(), "Select atleast one contact", Toast.LENGTH_SHORT).show();
						}
					} else {
						String olderCategoriesText = SharedPreferencesUtil.getStringPreferences(getActivity(), Utils.KEY_CATEGORIES);
						if(newCategoryName.equalsIgnoreCase(category.getCategoryName())) {
							// No changes made in name, hence do nothing for category name
							
							// removing older category contacts
							Utils.deleteCategory(getActivity(), category);
							
							// Creating category other than first time
							SharedPreferencesUtil.saveStringPreferences(getActivity(), Utils.KEY_CATEGORIES, olderCategoriesText);
							
							// adding new category contacts
							Utils.addContactsWithCategory(getActivity(), newCategoryName, selectedContactList);
						} else {
							Locale l = Locale.getDefault();
							if(olderCategoriesText.toLowerCase(l).contains(newCategoryName.toLowerCase(l))) {
								editTextCategoryName.setError("Category already exists");
								return;
							} else {
								
								String newCategoriesText = olderCategoriesText.replace(category.getCategoryName(), newCategoryName);
								
								// removing older category one
								Utils.deleteCategory(getActivity(), category);
								
								// Creating category other than first time
								SharedPreferencesUtil.saveStringPreferences(getActivity(), Utils.KEY_CATEGORIES, newCategoriesText);
								
								// adding new category contacts
								Utils.addContactsWithCategory(getActivity(), newCategoryName, selectedContactList);
							}
						}
						
						loadCategoryListData();
						dialog.dismiss();
					}
				}
			});
			
			// Refreshing associated contact listview
			final ListView associaltedContactListView = (ListView) dialog.findViewById(R.id.listview_contact_list);
			CustomCategoryAssociatedContactAdapter adapter = new CustomCategoryAssociatedContactAdapter(getActivity(), 
					CategoryFragment.this, selectedContactList, getActivity().getResources());
			associaltedContactListView.setAdapter(adapter);	
			
			View viewSelectContact = dialog.findViewById(R.id.relative_layout_add_contact);
			viewSelectContact.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showSelectContactDialog(associaltedContactListView);
				}
			});
		}
		
		private void showSelectContactDialog(final ListView associaltedContactListView) {

			AppSession session = (AppSession) getActivity().getApplication();
			List<Contact> phoneContacts = session.getPhoneContacts();
			
			if(phoneContacts == null) {
				(new FetchContactsTask(associaltedContactListView)).execute();
			} else {
				showContacts(phoneContacts, associaltedContactListView);
			}
		}
		
		public void showContacts(final List<Contact> phoneContacts, final ListView associaltedContactListView) {
			
			final Dialog contactDialog = new Dialog(getActivity());
			contactDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			contactDialog.setContentView(R.layout.dialog_select_contact);
			final ListView phoneContactListview = (ListView) contactDialog.findViewById(R.id.listview_phone_contact_list);
			final Resources res = getActivity().getResources();
			CustomPhoneContactAdapter adapter = new CustomPhoneContactAdapter(getActivity(), this, phoneContacts, selectedContactList, res);
			phoneContactListview.setAdapter(adapter);
			
			contactDialog.show();
			ImageButton doneButton = (ImageButton) contactDialog.findViewById(R.id.button_done);
			doneButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					contactDialog.dismiss();
					CustomCategoryAssociatedContactAdapter adapter = new CustomCategoryAssociatedContactAdapter(getActivity(), 
							CategoryFragment.this, selectedContactList, getActivity().getResources());
					associaltedContactListView.setAdapter(adapter);					
				}
			});
			
			ImageButton refreshButton = (ImageButton) contactDialog.findViewById(R.id.button_refresh);
			refreshButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					new AlertDialog.Builder(getActivity())
				    .setTitle("Refresh Contact List")
				    .setMessage("Are you sure want to refresh this contacct list?")
				    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	dialog.cancel();
				        	contactDialog.dismiss();
				        	(new FetchContactsTask(associaltedContactListView)).execute();
				        }
				     })
				    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            dialog.cancel();
				        }
				     })
				    .setIcon(android.R.drawable.ic_dialog_alert)
				    .show();
				}
			});
			
			EditText edittextSearch = (EditText) contactDialog.findViewById(R.id.edittext_search);
			edittextSearch.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					if(s.toString().trim().length() > 0) {
						Locale l = Locale.getDefault();
						List<Contact> list = new ArrayList<Contact>();
						for(Contact contact: phoneContacts) {
							if(contact.getName().toLowerCase(l).contains(s.toString().toLowerCase(l))) {
								list.add(contact);
							} else if(contact.getContactNumber().toLowerCase(l).contains(s.toString().toLowerCase(l))) {
								list.add(contact);
							}
						}
						
						CustomPhoneContactAdapter adapter = new CustomPhoneContactAdapter(getActivity(), CategoryFragment.this, list, selectedContactList, res);
						phoneContactListview.setAdapter(adapter);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
					// TODO Auto-generated method stub
				}
				
				@Override
				public void afterTextChanged(Editable s) {
					// TODO Auto-generated method stub
				}
			});
		}
		
		public void setSelectedList(List<Contact> selectedList) {
			this.selectedContactList = selectedList;
		}
		
		public void onCategoryItemLongClick(final Category category, final List<Category> categoryItems) {
			
			final Dialog dialogOptions = new Dialog(getActivity());
			//dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogOptions.setContentView(R.layout.dialog_listview_options_category);
			dialogOptions.setTitle(category.getCategoryName());
			ListView listviewOptions = (ListView) dialogOptions.findViewById(R.id.listview_options_category);
			String[] options = {"Edit", "Delete"};
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
			          android.R.layout.simple_list_item_2, android.R.id.text1, options);
			listviewOptions.setAdapter(adapter);
			listviewOptions.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					switch (position) {
					case 0:
						dialogOptions.dismiss();
						showEditCategoryDialog(category);
						break;
					case 1:
						dialogOptions.dismiss();
						new AlertDialog.Builder(getActivity())
					    .setTitle("Delete Category")
					    .setMessage("Are you sure want to delete this category?")
					    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) { 
					        	dialog.cancel();
					        	Utils.deleteCategory(getActivity(), category);
					        	for(int i=0; i<categoryItems.size(); i++) {
					        		if(category.getCategoryName().equals(categoryItems.get(i).getCategoryName())) {
					        			categoryItems.remove(i);
					        			break;
					        		}
					        	}
					        	((BaseAdapter)listViewCategory.getAdapter()).notifyDataSetChanged();
					        }
					     })
					    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) { 
					            dialog.cancel();
					        }
					     })
					    .setIcon(android.R.drawable.ic_dialog_alert)
					    .show();
						break;
					default:
						break;
					}
				}
			});
			
			dialogOptions.show();
		}
		
		private class FetchContactsTask extends AsyncTask<String, Void, List<Contact>> {
			
			ProgressDialog progress;
			ListView contactListView;
			String contactText;
			
			public FetchContactsTask(final ListView listView) {
				contactListView = listView;
				contactText = "";
			}
		    @Override
		    protected List<Contact> doInBackground(String... urls) {
		    	
		    	List<Contact> responseList = new ArrayList<Contact>();
				ContentResolver cr = getActivity().getContentResolver();
		        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, "display_name asc");
		        
				if (cur.getCount() > 0) {
					
					String seperator = "";
					while (cur.moveToNext()) {
						String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
						//String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
						
						if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
							Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
											new String[] { id }, null);
							
							while (pCur.moveToNext()) {
								
								String contactNumber = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								String contactName = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
								//Log.d(contactName, phoneNo);
								Contact contact = new Contact();
								contact.setName(contactName);
								contact.setContactNumber(Utils.trimContactNumber(contactNumber));
								responseList.add(contact);
								
								contactText = contactText + seperator + contactName + Utils.SEPERATOR_CONTACT_INNER + Utils.trimContactNumber(contactNumber);
								seperator = Utils.SEPERATOR_CONTACT_OUTER;
							}
							pCur.close();
						}
					}
				}
				return responseList;
		    }

		    @Override
		    protected void onPostExecute(List<Contact> response) {
		    	
		    	if(response.size() > 0) {
		    		
		    		SharedPreferencesUtil.saveStringPreferences(getActivity(), Utils.KEY_CONTACT, contactText);
		    		AppSession session = (AppSession) getActivity().getApplication();
		    		session.setPhoneContacts(response);
		    		showContacts(response, contactListView);
		    	}
		    	
		    	if(progress != null) {
		    		progress.dismiss();
		    		progress = null;
		    	}
		    }

		    @Override
		    protected void onCancelled() {
		    	super.onCancelled();
		    	if(progress != null) {
		    		progress.dismiss();
		    		progress = null;
		    	}
		    }
		    
			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progress = ProgressDialog.show(getActivity(), "Scaning device contacts", "Please wait...");
			}
		}
	}
	
	public void onCategoryInboxItemClick(SMS sms, List<String> mapKeyList, Map<String, List<SMSListItem>> map) {

		TextView textviewCategoryName = (TextView) findViewById(R.id.textview_category_name_label);
		textviewCategoryName.setVisibility(View.GONE);
		ListView listviewhidden = (ListView) findViewById(R.id.listview_categories_inbox);
		listviewhidden.setVisibility(View.GONE);
		
		ListView listview = (ListView) findViewById(R.id.listview_categories_messeges);
		listview.setVisibility(View.VISIBLE);
		
		TextView textviewFrom = (TextView) findViewById(R.id.textview_category_from);
		textviewFrom.setVisibility(View.VISIBLE);
		
		textviewFrom.setText("Messages from " + ("".equals(sms.getPerson()) ? sms.getAddress() : sms.getPerson()));
		loadMesseges(listview, sms, mapKeyList, map);
	}
	
	public void onInboxItemClick(SMS sms, List<String> mapKeyList, Map<String, List<SMSListItem>> map) {
		
		ListView listviewhidden = (ListView) findViewById(R.id.listview_inbox);
		listviewhidden.setVisibility(View.GONE);
		
		ListView listview = (ListView) findViewById(R.id.listview_messages);
		listview.setVisibility(View.VISIBLE);
		
		TextView textviewFrom = (TextView) findViewById(R.id.textview_inbox_from);
		textviewFrom.setVisibility(View.VISIBLE);
		
		textviewFrom.setText("Messages from " + ("".equals(sms.getPerson()) ? sms.getAddress() : sms.getPerson()));
		
		loadMesseges(listview, sms, mapKeyList, map);
	}
	
	public void onInboxItemLongClick(final SMS sms, final List<String> keys) {
		
		final Dialog dialogOptions = new Dialog(this);
		//dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogOptions.setContentView(R.layout.dialog_listview_options_inbox);
		dialogOptions.setTitle("".equals(sms.getPerson()) ? sms.getAddress() : sms.getPerson());
		ListView listviewOptions = (ListView) dialogOptions.findViewById(R.id.listview_options_inbox);
		String[] options = {"Add to category", "Delete messege thread"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		          android.R.layout.simple_list_item_2, android.R.id.text1, options);
		listviewOptions.setAdapter(adapter);
		listviewOptions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					dialogOptions.dismiss();
					addToCategory(sms);
					break;
				case 1:
					dialogOptions.dismiss();
					new AlertDialog.Builder(MainActivity.this)
				    .setTitle("Delete Messege Thread")
				    .setMessage("Are you sure want to delete all messeseges of " + ("".equals(sms.getPerson()) ? sms.getAddress() : sms.getPerson()) + " from device?\nNote: The action will be undone!")
				    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	dialog.cancel();
				        	if(listViewInbox == null) {
				    			listViewInbox = (ListView) findViewById(R.id.listview_inbox);
				            }
				        	deleteMessegeThread(sms, keys, listViewInbox);
				        }
				     })
				    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            dialog.cancel();
				        }
				     })
				    .setIcon(android.R.drawable.ic_dialog_alert)
				    .show();
					break;
				default:
					break;
				}
			}
		});
		
		dialogOptions.show();
	}
	
	protected void addToCategory(final SMS sms) {
		
		final Dialog dialogOptions = new Dialog(this);
		//dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogOptions.setContentView(R.layout.dialog_listview_options_add_to_category);
		dialogOptions.setTitle("".equals(sms.getPerson()) ? sms.getAddress() : sms.getPerson());
		ListView listviewOptions = (ListView) dialogOptions.findViewById(R.id.listview_options_add_to_category);
		String categoryText = SharedPreferencesUtil.getStringPreferences(MainActivity.this, Utils.KEY_CATEGORIES);
		final String[] array = categoryText.split(Utils.SEPERATOR);
		final View layoutButtonAddNewCategory = dialogOptions.findViewById(R.id.layout_button_add_new_category);
		final View layoutEditTextAddNewCategory = dialogOptions.findViewById(R.id.layout_edittext_new_category);
		final EditText editTextAddNewCategory = (EditText) dialogOptions.findViewById(R.id.edittext_new_category);
		editTextAddNewCategory.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				editTextAddNewCategory.setError(null);
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}
			@Override
			public void afterTextChanged(Editable s) {}
		});
		
		layoutButtonAddNewCategory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				layoutButtonAddNewCategory.setVisibility(View.GONE);
				layoutEditTextAddNewCategory.setVisibility(View.VISIBLE);
				
				// Displaying keyboard
				editTextAddNewCategory.requestFocus();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(editTextAddNewCategory, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		
		ImageButton buttonAddNewCategory = (ImageButton) dialogOptions.findViewById(R.id.button_add_new_category);
		buttonAddNewCategory.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				editTextAddNewCategory.setError(null);
				
				String categoryName = editTextAddNewCategory.getText().toString().trim(); 
				if(categoryName.equals("")) {
					editTextAddNewCategory.setError("Required field");
					return;
				}
				
				String savedCategoriesText = SharedPreferencesUtil.getStringPreferences(MainActivity.this, Utils.KEY_CATEGORIES);
				String[] array = savedCategoriesText.split(Utils.SEPERATOR);
				boolean categoryExist = false;
				for (int i = 0; i < array.length; i++) {
					if(categoryName.equalsIgnoreCase(array[i])) {
						categoryExist = true;
						break;
					}
				}
				
				if(categoryExist) {
					editTextAddNewCategory.setError("Category already exist");
					return;
				}
				
				if("".equals(savedCategoriesText)) {
					SharedPreferencesUtil.saveStringPreferences(MainActivity.this, Utils.KEY_CATEGORIES, categoryName);
				} else {
					savedCategoriesText +=  Utils.SEPERATOR + categoryName;
					SharedPreferencesUtil.saveStringPreferences(MainActivity.this, Utils.KEY_CATEGORIES, savedCategoriesText);
				}
				
				SharedPreferencesUtil.saveStringPreferences(MainActivity.this, categoryName, sms.getAddress());
				SharedPreferencesUtil.saveStringPreferences(MainActivity.this, sms.getAddress(), "".equals(sms.getPerson()) ? sms.getAddress() : sms.getPerson());
				
				if(listViewInbox == null) {
					listViewInbox = (ListView) findViewById(R.id.listview_inbox);
				}
				((BaseAdapter) listViewInbox.getAdapter()).notifyDataSetChanged();
				
				dialogOptions.dismiss();
			}
		});
		
		if(array.length >= 0) {
			if(array[0].equals("")) {
				listviewOptions.setVisibility(View.GONE);
			} else {
				listviewOptions.setVisibility(View.VISIBLE);
			}
		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		          android.R.layout.simple_list_item_2, android.R.id.text1, array);
		listviewOptions.setAdapter(adapter);
		listviewOptions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				dialogOptions.dismiss();
				//String categoriesText = SharedPreferencesUtil.getStringPreferences(MainActivity.this, Utils.KEY_CATEGORIES);
				String associatedContactText = SharedPreferencesUtil.getStringPreferences(MainActivity.this, array[position]);
				
				if(associatedContactText.contains(sms.getAddress())) { 
				
					new AlertDialog.Builder(MainActivity.this)
				    .setTitle("Error")
				    .setMessage("Selected contact already added in " + array[position] + " category.")
				    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	dialog.cancel();
				        }
				     })
				    .setIcon(android.R.drawable.ic_dialog_alert)
				    .show();
				} else {
					associatedContactText = associatedContactText + Utils.SEPERATOR + sms.getAddress();
					
					// Saving address to selected category
					SharedPreferencesUtil.saveStringPreferences(MainActivity.this, array[position], associatedContactText);
					
					// adding new category contacts
					SharedPreferencesUtil.saveStringPreferences(MainActivity.this, sms.getAddress(), ("".equals(sms.getPerson()) ? sms.getAddress() : sms.getPerson()));
					
					if(listViewInbox == null) {
						listViewInbox = (ListView) findViewById(R.id.listview_inbox);
					}
					((BaseAdapter) listViewInbox.getAdapter()).notifyDataSetChanged();
				}
			}
		});
		
		dialogOptions.show();
		
	}

	public void onCategoryInboxItemLongClick(final SMS sms, final List<String> keys, final Category category, final List<Category> categoryItems) {
		
		final Dialog dialogOptions = new Dialog(this);
		//dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogOptions.setContentView(R.layout.dialog_listview_options_inbox);
		dialogOptions.setTitle("".equals(sms.getPerson()) ? sms.getAddress() : sms.getPerson());
		ListView listviewOptions = (ListView) dialogOptions.findViewById(R.id.listview_options_inbox);
		String[] options = {"Remove from category", "Delete messege thread"};
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		          android.R.layout.simple_list_item_2, android.R.id.text1, options);
		listviewOptions.setAdapter(adapter);
		listviewOptions.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					dialogOptions.dismiss();
					removeNumberFromCategory(sms, category, keys, categoryItems);
					break;
				case 1:
					dialogOptions.dismiss();
					new AlertDialog.Builder(MainActivity.this)
				    .setTitle("Delete Messege Thread")
				    .setMessage("Are you sure want to delete all messeseges of " + ("".equals(sms.getPerson()) ? sms.getAddress() : sms.getPerson())  + " from device?\nNote: The action will be undone!")
				    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				        	dialog.cancel();
				        	if(listViewCategoryInbox == null) {
				    			listViewCategoryInbox = (ListView) findViewById(R.id.listview_categories_inbox);
				            }
				        	deleteMessegeThread(sms, keys, listViewCategoryInbox);
				        }
				     })
				    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
				        public void onClick(DialogInterface dialog, int which) { 
				            dialog.cancel();
				        }
				     })
				    .setIcon(android.R.drawable.ic_dialog_alert)
				    .show();
					break;
				default:
					break;
				}
			}
		});
		
		dialogOptions.show();
	}
	
	private void removeNumberFromCategory(SMS sms, final Category category,final List<String> keys, final List<Category> categoryItems) {
		
		String categoryText = SharedPreferencesUtil.getStringPreferences(MainActivity.this, category.getCategoryName());
		if(categoryText.contains(Utils.SEPERATOR + sms.getAddress())) {
			categoryText = categoryText.replace(Utils.SEPERATOR + sms.getAddress(), "");
		} else if(categoryText.contains(sms.getAddress() + Utils.SEPERATOR)) {
			categoryText = categoryText.replace(sms.getAddress() + Utils.SEPERATOR, "");
		} else {
			categoryText = categoryText.replace(sms.getAddress(), "");
		}

		if(categoryText.equals("")) {
			// No more contact number remaining in this category, so delete this category
			new AlertDialog.Builder(MainActivity.this)
		    .setTitle("Delete " + category.getCategoryName())
		    .setMessage("This action will delete the category. Are you sure want to continue deleting?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	dialog.cancel();
		        	Utils.deleteCategory(MainActivity.this, category);
		        	// deleting category from listview
		        	for(int i=0; i<categoryItems.size(); i++) {
		        		if(category.getCategoryName().equals(categoryItems.get(i).getCategoryName())) {
		        			categoryItems.remove(i);
		        			break;
		        		}
		        	}
		        	onBackPressed();
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            dialog.cancel();
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .show();
		} else {
			// Saving modified category text
			SharedPreferencesUtil.saveStringPreferences(MainActivity.this, category.getCategoryName(), categoryText);
			
			// deleting category from listview
			for(int i=0; i<keys.size(); i++) {
        		if(sms.getAddress().equals(keys.get(i))) {
        			keys.remove(i);
        			break;
        		}
        	}
			
			// Updating category count after deleting number from them
			for(int i=0; i<categoryItems.size(); i++) {
        		if(category.getCategoryName().equals(categoryItems.get(i).getCategoryName())) {
        			Category c = categoryItems.get(i);
        			c.setMessageCount(c.getMessageCount() - 1);
        			categoryItems.remove(i);
        			categoryItems.add(i, category);
        			break;
        		}
        	}
			((BaseAdapter)listViewCategoryInbox.getAdapter()).notifyDataSetChanged();
		}
	}
	
	private void deleteMessegeThread(SMS sms, List<String> keys, ListView listview) {

		/*keys.remove(sms.getAddress());
        ((BaseAdapter) listview.getAdapter()).notifyDataSetChanged();*/
        
	    try {
	        Uri uriSms = Uri.parse("content://sms/inbox");
	        Cursor c = getContentResolver().query(uriSms,
	            new String[] { "_id", "thread_id", "address",
	                "person", "date", "body" }, null, null, null);

	        if (c != null && c.moveToFirst()) {
	            do {
	                long id = c.getLong(0);
	                //long threadId = c.getLong(1);
	                String address = c.getString(2);
	                //String body = c.getString(5);

	                if (id == sms.getId() && address.contains(sms.getAddress())) {
	                    getContentResolver().delete(
	                        Uri.parse("content://sms/" + id), null, null);
	                    
	                    // deleting messeges from session
	                    AppSession session = (AppSession) getApplication();
	                    Map<String, List<SMSListItem>> map = session.getMap();
	                    map.remove(sms.getAddress());
	                    
	                    // deleting messege from current list
	                    keys.remove(sms.getAddress());
	                    ((BaseAdapter) listview.getAdapter()).notifyDataSetChanged();
	                    break;
	                } 
	            } while (c.moveToNext());
	        }
	    } catch (Exception e) {
	        Log.d("Could not delete SMS from inbox: ", e.getMessage());
	    }
	}
	
	private void loadMesseges(ListView listview, SMS sms, List<String> mapKeyList, Map<String, List<SMSListItem>> map) {
		
		Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, "date desc");
		
		if(cursor.moveToFirst()) {
			
			List<SMSListItem> messegeItems = new ArrayList<SMSListItem>();
			
			do {
				if(cursor.getString(cursor.getColumnIndex("address")).contains(sms.getAddress())) {
				
					String address = Utils.trimContactNumber(cursor.getString(cursor.getColumnIndex("address")));
					String date = Utils.getDateTime(cursor.getLong(cursor.getColumnIndex("date")));
					SMS smsObj = new SMS();
					smsObj.setId(cursor.getLong(cursor.getColumnIndex("_id")));
					smsObj.setAddress(address);
					smsObj.setPerson(Utils.getPersonName(address, contactList));
					smsObj.setBody(cursor.getString(cursor.getColumnIndex("body")));
					smsObj.setDate(date);
					smsObj.setDateSent(cursor.getLong(cursor.getColumnIndex("date_sent")));
					
					SMSListItem smsListItem = new SMSListItem();
					smsListItem.setSms(smsObj);
					
					messegeItems.add(smsListItem);
				}
			} while(cursor.moveToNext());
										
			Resources res = getResources();
			CustomMessegeAdapter mAdapter = new CustomMessegeAdapter(MainActivity.this, messegeItems, mapKeyList, map, res);
			listview.setAdapter(mAdapter);
		}
	}
	
	public void onCategoryItemClick(Category category, List<Category> categoryItems) {
				
		View relativeLayout = (View) findViewById(R.id.relative_layout_add_category);
		relativeLayout.setVisibility(View.GONE);
		ListView listViewCategory = (ListView) findViewById(R.id.listview_categories);
		listViewCategory.setVisibility(View.GONE);
		if(listViewCategoryInbox == null) {
			listViewCategoryInbox = (ListView) findViewById(R.id.listview_categories_inbox);
		}
		listViewCategoryInbox.setVisibility(View.VISIBLE);
		
		TextView textviewCategoryName = (TextView) findViewById(R.id.textview_category_name_label);
		textviewCategoryName.setVisibility(View.VISIBLE);
		textviewCategoryName.setText("Messages in \"" + category.getCategoryName() + "\" category");
		
		Resources res = getResources();
		CustomCategoryInboxAdapter adapter = new CustomCategoryInboxAdapter(this, category, res, categoryItems);
		listViewCategoryInbox.setAdapter(adapter);
		adapter.notifyDataSetChanged();
	}
	
	@Override
	public void onBackPressed() {

		listViewMesseges = listViewMesseges == null ? (ListView) findViewById(R.id.listview_messages) : listViewMesseges;
		//Log.d("", (listViewMesseges != null) + "&&" + (listViewMesseges.getVisibility() == View.VISIBLE) + "&&" + (currentPageSelectedIndex == 0));
		if(listViewMesseges != null && listViewMesseges.getVisibility() == View.VISIBLE && currentPageSelectedIndex == 0) {
			if(listViewInbox == null) {
				listViewInbox = (ListView) findViewById(R.id.listview_inbox);
			}
			listViewInbox.setVisibility(View.VISIBLE);
			listViewMesseges.setVisibility(View.GONE);
			
			TextView textviewFrom = (TextView) findViewById(R.id.textview_inbox_from);
			textviewFrom.setVisibility(View.GONE);
			
			((BaseAdapter) listViewInbox.getAdapter()).notifyDataSetChanged();
			return;
		}
		
		ListView listViewCategoryInbox = (ListView) findViewById(R.id.listview_categories_inbox);
		if(listViewCategoryInbox != null && listViewCategoryInbox.getVisibility() == View.VISIBLE && currentPageSelectedIndex == 1) {
			ListView listViewCategories = (ListView) findViewById(R.id.listview_categories);
			listViewCategories.setVisibility(View.VISIBLE);
			TextView textviewCategoryName = (TextView) findViewById(R.id.textview_category_name_label);
			textviewCategoryName.setVisibility(View.GONE);
			listViewCategoryInbox.setVisibility(View.GONE);
			View relativeLayout = (View) findViewById(R.id.relative_layout_add_category);
			relativeLayout.setVisibility(View.VISIBLE);
			((BaseAdapter) listViewCategories.getAdapter()).notifyDataSetChanged();
			return;
		}
		
		ListView listViewCategoryMesseges = (ListView) findViewById(R.id.listview_categories_messeges);
		if(listViewCategoryMesseges != null && listViewCategoryMesseges.getVisibility() == View.VISIBLE && currentPageSelectedIndex == 1) {
			TextView textviewCategoryName = (TextView) findViewById(R.id.textview_category_name_label);
			textviewCategoryName.setVisibility(View.VISIBLE);
			listViewCategoryInbox.setVisibility(View.VISIBLE);
			listViewCategoryMesseges.setVisibility(View.GONE);
			TextView textviewFrom = (TextView) findViewById(R.id.textview_category_from);
			textviewFrom.setVisibility(View.GONE);
			((BaseAdapter) listViewCategoryInbox.getAdapter()).notifyDataSetChanged();
			return;
		}
		
		new AlertDialog.Builder(MainActivity.this)
	    .setTitle("Exit")
	    .setMessage("Are you sure want to exit from the Apllication")
	    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	        	dialog.cancel();
	        	MainActivity.super.onBackPressed();
	        	MainActivity.this.finish();
	        }
	     })
	    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            dialog.cancel();
	        }
	     })
	    .setIcon(android.R.drawable.ic_dialog_alert)
	    .show();
	}
}