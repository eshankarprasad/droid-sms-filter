package com.devdroid.smsfilter;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class CustomCategoryAssociatedContactAdapter extends BaseAdapter   implements OnClickListener {
    
    /*********** Declare Used Variables *********/
    private Activity activity;
    private Fragment fragment;
    private List<Contact> contactItems;
    private static LayoutInflater inflater = null;
    public Resources res;
    int i=0;
     
    /*************  CustomAdapter Constructor *****************/
    public CustomCategoryAssociatedContactAdapter(Activity activity, Fragment fragment, List<Contact> contactItems, Resources res) {
         
           /********** Take passed values **********/
            this.activity = activity;
            this.fragment = fragment;
            this.contactItems = contactItems;
            this.res = res;
         
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
         
        public TextView textContactName;
        public TextView textContactNumber;
    }
 
    // Depends upon data size called for each row , Create each ListView row
    public View getView(final int position, View convertView, ViewGroup parent) {
         
        ViewHolder holder;
        if(convertView == null) {
             
            // Inflate tabitem.xml file for each row ( Defined below )
        	convertView = inflater.inflate(R.layout.category_associated_contact_listview_item, null);
             
            // View Holder Object to contain tabitem.xml file elements
            holder = new ViewHolder();
            holder.textContactName = (TextView) convertView.findViewById(R.id.textview_contact_name);
            holder.textContactNumber = (TextView) convertView.findViewById(R.id.textview_contact_number);
            // Set holder with LayoutInflater
            convertView.setTag(holder);
        } else { 
            holder = (ViewHolder) convertView.getTag();
        }
         
        if(contactItems.size() <= 0) {
            holder.textContactName.setText("No Data");
        }
        else {
            
        	// Get each Model object from Arraylist
            final Contact tempValue = (Contact) contactItems.get(position);
            
            if(tempValue.getName().equals(tempValue.getContactNumber())) {
            	// Set Model values in Holder elements
                holder.textContactName.setText(tempValue.getName());
                holder.textContactNumber.setVisibility(View.GONE);
            } else {
            	// Set Model values in Holder elements
                holder.textContactName.setText(tempValue.getName());
                holder.textContactNumber.setText(tempValue.getContactNumber());
                holder.textContactNumber.setVisibility(View.VISIBLE);
            }
            
            // Set Item Click Listner for LayoutInflater for each row
            convertView.setOnLongClickListener(new OnItemLongClickListener(this, tempValue, contactItems));
        }
        return convertView;
    }
     
    /********* Called when Item click in ListView ************/
    private class OnItemLongClickListener  implements OnLongClickListener {           
        private CustomCategoryAssociatedContactAdapter mAdapter;
        private Contact mContact;
        private List<Contact> mList;

        public OnItemLongClickListener(CustomCategoryAssociatedContactAdapter customCategoryAssociatedContactAdapter, Contact contact, List<Contact> list) {
        	mAdapter = customCategoryAssociatedContactAdapter;
        	mContact = contact;
        	mList = list;
        }
        @Override
		public boolean onLongClick(View v) {
        	
        	MainActivity mainActivity = (MainActivity) activity;
        	new AlertDialog.Builder(mainActivity)
		    .setTitle("Delete Contact")
		    .setMessage("Are you sure you want to delete from this category?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	dialog.cancel();
		        	for (int i=0; i<mList.size(); i++) {
		        		Contact contact = mList.get(i);
						if(contact.getContactNumber().equals(mContact.getContactNumber())) {
							mList.remove(i);
							break;
						}
					}
		        	mAdapter.notifyDataSetChanged();
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		            dialog.cancel();
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		    .show();
        	
			return false;
		}
    }

	@Override
	public void onClick(View v) {
		
	}   
}
