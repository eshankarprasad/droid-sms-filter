package com.devdroid.smsfilter;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CustomMessageAdapter extends BaseAdapter   implements OnClickListener {
    
    /*********** Declare Used Variables *********/
    private Activity activity;
    private List<SMSListItem> messageItems;
    private static LayoutInflater inflater=null;
    List<String> mapKeyList;
    Map<String, List<SMSListItem>> map;
    public Resources res;
    int i=0;
     
    /*************  CustomAdapter Constructor 
     * @param map *****************/
    public CustomMessageAdapter(Activity activity, List<SMSListItem> messageItems, List<String> mapKeyList, Map<String, List<SMSListItem>> map, Resources res) {
         
           /********** Take passed values **********/
            this.activity = activity;
            this.messageItems = messageItems;
            this.res = res;
            this.mapKeyList = mapKeyList;
            this.map = map;
         
            /***********  Layout inflator to call external xml layout () ***********/
             inflater = ( LayoutInflater ) this.activity.
                                         getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
    }
 
    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
         
        if(messageItems.size() <= 0) {
            return 1;
        }
        
        return messageItems.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
     
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
         
        public TextView textDateTime;
        public TextView textBody;
    }
 
    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {
         
        View mConvertView = convertView;
        ViewHolder holder;
         
        if(convertView == null){
             
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            mConvertView = inflater.inflate(R.layout.messeges_listview_item, null);
             
            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.textDateTime = (TextView) mConvertView.findViewById(R.id.textview_datetime);
            holder.textBody = (TextView)mConvertView.findViewById(R.id.textview_body);
             
           /************  Set holder with LayoutInflater ************/
            mConvertView.setTag(holder);
        }
        else { 
            holder = (ViewHolder) mConvertView.getTag();
        }
         
        if(messageItems.size() <= 0) {
            holder.textDateTime.setText("No Data");
        }
        else {
            
        	/***** Get each Model object from Arraylist ********/
        	SMSListItem tempValues = null;
            tempValues = (SMSListItem) messageItems.get(position);
             
            /************  Set Model values in Holder elements ***********/

             holder.textDateTime.setText(tempValues.getSms().getDate() + "");
             String body = tempValues.getSms().getBody();
             holder.textBody.setText(body);
              
             /******** Set Item Click Listner for LayoutInflater for each row *******/
             mConvertView.setOnLongClickListener(new OnItemLongClickListener(tempValues.getSms()));
        }
        return mConvertView;
    }
     
    @Override
    public void onClick(View v) {
            Log.v("CustomAdapter", "=====Row button clicked=====");
    }
     
    /********* Called when Item click in ListView ************/
    private class OnItemLongClickListener  implements OnLongClickListener{           
        private SMS mSms;
         
        OnItemLongClickListener(SMS sms){
             mSms = sms;
        }

		@Override
		public boolean onLongClick(View arg0) {
			final MainActivity maiActivity = (MainActivity) activity;
			final Dialog dialogOptions = new Dialog(maiActivity);
			//dialogOptions.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialogOptions.setContentView(R.layout.dialog_listview_message_options);
			dialogOptions.setTitle("".equals(mSms.getPerson()) ? mSms.getAddress() : mSms.getPerson());
			ListView listview = (ListView) dialogOptions.findViewById(R.id.listview_options);
			String[] options = {"Share","Delete"};
			
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(maiActivity,
			          android.R.layout.simple_list_item_2, android.R.id.text1, options);
			listview.setAdapter(adapter);
			listview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					switch (position) {
					case 0:
						dialogOptions.dismiss();

					    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
				        sharingIntent.setType("text/plain");
				        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sent using " + res.getString(R.string.app_name));
				        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, mSms.getBody() + "\n\n...sent by " + res.getString(R.string.promo_text));
				        activity.startActivityForResult(Intent.createChooser(sharingIntent, "Share messege by..."), 0);
						break;
					case 1:
						dialogOptions.dismiss();
						new AlertDialog.Builder(activity)
					    .setTitle("Delete Messege")
					    .setMessage("Are you sure you want to delete this messege?")
					    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
					        public void onClick(DialogInterface dialog, int which) { 
					        	dialog.cancel();
					        	deleteSMS(maiActivity, CustomMessageAdapter.this, mSms.getId(), mSms.getAddress());
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

          	//****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****//*
         	//maiActivity.onItemLongClick();
			return false;
		}
		
		private void deleteSMS(Context context, CustomMessageAdapter adapter, long mId, String mAddress) {
		    try {
		    	AppSession session = (AppSession) activity.getApplication();
		    	Map<String, List<SMSListItem>> sessionMap = session.getMap();
		    	
		        Uri uriSms = Uri.parse("content://sms/inbox");
		        Cursor c = context.getContentResolver().query(uriSms,
		            new String[] { "_id", "thread_id", "address",
		                "person", "date", "body" }, null, null, null);

		        if (c != null && c.moveToFirst()) {
		            do {
		                long id = c.getLong(0);
		                //long threadId = c.getLong(1);
		                String address = c.getString(2);
		                //String body = c.getString(5);

		                if (id == mId && address.contains(mAddress)) {
		                    context.getContentResolver().delete(
		                        Uri.parse("content://sms/" + id), null, null);
		                    // deleting messege from current list
		                    for(int i=0; i<messageItems.size(); i++) {
		                    	SMS sms = messageItems.get(i).getSms();
		                    	
                    			if(address.contains(sms.getAddress()) && id == sms.getId()) {
		                    		messageItems.remove(i);
		                    		adapter.notifyDataSetChanged();
		                    		if(messageItems.size() <= 0) {
		                    			mapKeyList.remove(sms.getAddress());
		                    			
		                    			// removing the contact from session
		                    			sessionMap.remove(sms.getAddress());
		                    			
		                    			activity.onBackPressed();
		                    		} else {
		                    			map.put(sms.getAddress(), messageItems);
		                    			
		                    			// removing the sms from session
		                    			List<SMSListItem> list = sessionMap.get(sms.getAddress());
		                    			for(int j=0; j<list.size(); j++) {
		                    				SMS sessionSms = list.get(j).getSms();
		                    				if(address.contains(sms.getAddress()) && id == sessionSms.getId()) {
		                    					list.remove(j);
		                    				}
		                    			}
		                    			
		                    			//session.setMap(map);
		                    		}
		                    	}
		                    }
		                    break;
		                } 
		            } while (c.moveToNext());
		        }
		    } catch (Exception e) {
		        Log.d("Could not delete SMS from inbox: ", e.getMessage());
		    }
		}
    }  
}
