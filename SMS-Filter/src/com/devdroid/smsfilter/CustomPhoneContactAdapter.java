package com.devdroid.smsfilter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.devdroid.smsfilter.MainActivity.CategoryFragment;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class CustomPhoneContactAdapter extends BaseAdapter   implements OnClickListener {
    
    /*********** Declare Used Variables *********/
    private Activity activity;
    private Fragment fragment;
    private List<Contact> contactItems;
    private List<Contact> selectedContacts;
    
    private static LayoutInflater inflater=null;
    public Resources res;
    int i=0;
     
    /*************  CustomAdapter Constructor *****************/
    public CustomPhoneContactAdapter(Activity activity, Fragment fragment, List<Contact> contactItems, List<Contact> selectedList, Resources res) {
         
           /********** Take passed values **********/
            this.activity = activity;
            this.fragment = fragment;
            this.contactItems = contactItems;
            this.res = res;
            this.selectedContacts = new ArrayList<Contact>(selectedList);
         
            /***********  Layout inflator to call external xml layout () ***********/
             inflater = ( LayoutInflater ) this.activity.
                                         getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
    }
 
    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
         
        if(contactItems.size() <= 0) {
            return 1;
        }
        
        return contactItems.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
     
    // Create a holder Class to contain inflated xml file elements 
    private static class ViewHolder{
         
        public CheckBox checkContact;
        public TextView textNumber;
    }
 
    // Depends upon data size called for each row , Create each ListView row
    public View getView(final int position, View convertView, ViewGroup parent) {
         
        ViewHolder holder;
        if(convertView == null) {
             
            // Inflate tabitem.xml file for each row ( Defined below )
        	convertView = inflater.inflate(R.layout.phone_contact_listview_item, null);
             
            // View Holder Object to contain tabitem.xml file elements
            holder = new ViewHolder();
            holder.checkContact = (CheckBox) convertView.findViewById(R.id.checkbox_contact);
            holder.textNumber = (TextView) convertView.findViewById(R.id.textview_number);
            // Set holder with LayoutInflater
            convertView.setTag(holder);
        } else { 
            holder = (ViewHolder) convertView.getTag();
        }
         
        if(contactItems.size() <= 0) {
            holder.textNumber.setText("No Data");
            holder.checkContact.setVisibility(View.GONE);
        }
        else {
            
        	// Get each Model object from Arraylist
            final Contact tempValue = (Contact) contactItems.get(position);
             
            // Set Model values in Holder elements
            holder.checkContact.setText(tempValue.getName());
            holder.textNumber.setText(tempValue.getContactNumber());
            
            if(tempValue.getName().equals(tempValue.getContactNumber())) {
            	holder.textNumber.setVisibility(View.GONE);
            } else {
            	holder.textNumber.setVisibility(View.VISIBLE);
            }
            
            holder.checkContact.setChecked(false);
            for(Contact contact: selectedContacts) {
            	            	
            	if(contact.getContactNumber().equals(tempValue.getContactNumber())) {
            		holder.checkContact.setChecked(true);
            		break;
            	}
            }
             
            // Set Item Click Listner for LayoutInflater for each row
            convertView. setOnClickListener(new OnItemClickListener(position, holder));
        }
        return convertView;
    }
     
     
    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
        private ViewHolder mHolder;
         
        OnItemClickListener(int position, ViewHolder viewHolder){
             mPosition = position;
             mHolder = viewHolder;
        }

		@Override
		public void onClick(View arg0) {
			
			/*CategoryFragment categoryFragment = (CategoryFragment) fragment;
			categoryFragment.onDoneButtonClicked();*/
			
			mHolder.checkContact.toggle();
			
			if(mHolder.checkContact.isChecked()) {
				selectedContacts.add(contactItems.get(mPosition));
			} else {
				//selectedContacts.remove(contactItems.get(mPosition));
				String numberToRemove = contactItems.get(mPosition).getContactNumber();
				for(int i=0; i<selectedContacts.size(); i++) {
	            	if(selectedContacts.get(i).getContactNumber().equals(numberToRemove)) {
	            		selectedContacts.remove(i);
	            	}
	            }
			}
			((CategoryFragment) fragment).setSelectedList(selectedContacts);
			//Log.d("selectedContacts" , selectedContacts.toString());
		}               
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}   
}
