package com.devdroid.smsfilter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CustomInboxAdapter extends BaseAdapter   implements OnClickListener {
    
    /*********** Declare Used Variables *********/
    private Activity activity;
    private Map<String, List<SMSListItem>> map;
    List<String> keys;
    private static LayoutInflater inflater=null;
    public Resources res;
    int i=0;
     
    /*************  CustomAdapter Constructor *****************/
    public CustomInboxAdapter(Activity activity, Resources res) {
         
           /********** Take passed values **********/
            this.activity = activity;
            this.res = res;
            this.keys = new ArrayList<String>();
            this.map = new LinkedHashMap<String, List<SMSListItem>>();
            /***********  Layout inflator to call external xml layout () ***********/
             inflater = ( LayoutInflater ) this.activity.
                                         getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
    }
 
    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
         
        if(keys.size() <= 0) {
            return 1;
        }
        return keys.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
     
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
    	public TextView textCount;
        public TextView textAddress;
        public TextView textBody;
    }
 
    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {
         
        View mConvertView = convertView;
        ViewHolder holder;
         
        if(convertView == null){
             
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            mConvertView = inflater.inflate(R.layout.inbox_listview_item, null);
             
            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.textCount = (TextView) mConvertView.findViewById(R.id.textview_inbox_messege_count);
            holder.textAddress = (TextView) mConvertView.findViewById(R.id.textview_address);
            holder.textBody = (TextView) mConvertView.findViewById(R.id.textview_body);
            
           /************  Set holder with LayoutInflater ************/
            mConvertView.setTag(holder);
        }
        else { 
            holder = (ViewHolder) mConvertView.getTag();
        }
         
        if(keys.size()<=0) {
            holder.textAddress.setText("No inbox item found");
            holder.textCount.setVisibility(View.GONE);
            holder.textBody.setVisibility(View.GONE);
        }
        else {
            
        	/***** Get each Model object from Arraylist ********/
        	List<SMSListItem> tempList = map.get(keys.get(position));
        	
        	SMSListItem smsListItem = (SMSListItem) tempList.get(0);
             
            /************  Set Model values in Holder elements ***********/

        	if(smsListItem.getSms().getPerson().equals("")) {
        		holder.textAddress.setText(smsListItem.getSms().getAddress());
        	} else {
        		holder.textAddress.setText(smsListItem.getSms().getPerson());
        	}
            String body = smsListItem.getSms().getBody();
            if(body.length() > 20) {
            	holder.textBody.setText(body.substring(0, 19) + "...");
            } else {
            	holder.textBody.setText(body);
            }
            holder.textCount.setText("(" + tempList.size() + ")");
              
            /******** Set Item Click Listner for LayoutInflater for each row *******/
            mConvertView.setOnClickListener(new OnItemClickListener(smsListItem));
            mConvertView.setOnLongClickListener(new OnItemLongClickListener(smsListItem));
        }
        return mConvertView;
    }
     
    @Override
    public void onClick(View v) {
            Log.v("CustomAdapter", "=====Row button clicked=====");
    }
     
    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements OnClickListener{           
        private SMSListItem mListItem;
         
        OnItemClickListener(SMSListItem item){
        	mListItem = item;
        }
         
        @Override
        public void onClick(View arg0) {
        	
        	MainActivity maiActivity = (MainActivity) activity;

          	//****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****//*
         	maiActivity.onInboxItemClick(mListItem.getSms(), keys, map);
        }               
    }
    
    private class OnItemLongClickListener  implements OnLongClickListener{           
        private SMSListItem mListItem;
         
        OnItemLongClickListener(SMSListItem item){
        	mListItem = item;
        }

		@Override
		public boolean onLongClick(View arg0) {
			MainActivity maiActivity = (MainActivity) activity;

          	//****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****//*
         	maiActivity.onInboxItemLongClick(mListItem.getSms(), keys);
			return false;
		}               
    }
    
    @Override
    public void notifyDataSetChanged() {
    	
    	AppSession session = (AppSession) activity.getApplication();
    	map.clear();
    	keys.clear();
		
    	map = new LinkedHashMap<String, List<SMSListItem>>(session.getMap());
		keys.addAll(map.keySet());
		
    	String categoriesText = SharedPreferencesUtil.getStringPreferences(activity, Utils.KEY_CATEGORIES);
    	String[] array = categoriesText.split(Utils.SEPERATOR);
    	List<String> addressInCategory = new ArrayList<String>();
    	for (int i = 0; i < array.length; i++) {
			String addressText = SharedPreferencesUtil.getStringPreferences(activity, array[i]);
			String[] addressArray = addressText.split(Utils.SEPERATOR);
			for (int j = 0; j < addressArray.length; j++) {
				addressInCategory.add(addressArray[j]);
			}
		}
    	
    	for (int i = 0; i < addressInCategory.size(); i++) {
			for(int j=0; j<keys.size(); j++) {
				//Log.d(addressInCategory.get(i), keys.get(j));
				if(addressInCategory.get(i).equals(keys.get(j))) {
					map.remove(keys.get(j));
					keys.remove(j);
				}
			}
		}
    	super.notifyDataSetChanged();
    }
}
