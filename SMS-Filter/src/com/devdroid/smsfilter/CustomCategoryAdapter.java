package com.devdroid.smsfilter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.devdroid.smsfilter.MainActivity.CategoryFragment;

public class CustomCategoryAdapter extends BaseAdapter   implements OnClickListener {
    
    /*********** Declare Used Variables *********/
    private Activity activity;
    private Fragment fragment;
    private List<Category> categoryItems;
    
    private static LayoutInflater inflater=null;
    public Resources res;
    int i=0;
     
    /*************  CustomAdapter Constructor *****************/
    public CustomCategoryAdapter(Activity activity, Fragment fragment, List<Category> categoryItems, Resources res) {
         
           /********** Take passed values **********/
            this.activity = activity;
            this.fragment = fragment;
            this.categoryItems = categoryItems;
            this.res = res;
         
            /***********  Layout inflator to call external xml layout () ***********/
             inflater = ( LayoutInflater ) this.activity.
                                         getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         
    }
 
    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {
         
        if(categoryItems.size() <= 0) {
            return 1;
        }
        
        return categoryItems.size();
    }
 
    public Object getItem(int position) {
        return position;
    }
 
    public long getItemId(int position) {
        return position;
    }
     
    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{
         
        public TextView textCategoryName;
        public TextView textMessegeCount;
    }
 
    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {
         
        View mConvertView = convertView;
        ViewHolder holder;
        
        if(convertView == null){
             
            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            mConvertView = inflater.inflate(R.layout.categories_listview_item, null);
             
            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.textCategoryName = (TextView) mConvertView.findViewById(R.id.textview_category_name);
            holder.textMessegeCount = (TextView)mConvertView.findViewById(R.id.textview_messege_count);
             
           /************  Set holder with LayoutInflater ************/
            mConvertView.setTag(holder);
        }
        else { 
            holder = (ViewHolder) mConvertView.getTag();
        }
         
        if(categoryItems.size() <= 0) {
        	holder.textCategoryName.setText("No category found. Create new category by selecting above button, or go into \"INBOX\" section by swiping left the screen and long press on an SMS item");
            holder.textMessegeCount.setVisibility(View.GONE);
            mConvertView.setOnLongClickListener(null);
            mConvertView.setOnClickListener(null);
        }
        else {
            
        	/***** Get each Model object from Arraylist ********/
        	final Category tempValues = (Category) categoryItems.get(position);
             
            /************  Set Model values in Holder elements ***********/

             holder.textCategoryName.setText(tempValues.getCategoryName());
             holder.textMessegeCount.setText("(" + tempValues.getMessageCount() + ")");
             holder.textMessegeCount.setVisibility(View.VISIBLE);
             /******** Set Item Click Listner for LayoutInflater for each row *******/
             mConvertView.setOnLongClickListener(new OnItemLongClickListener(position, holder));
             mConvertView.setOnClickListener(new OnItemClickListener(position, holder));
        }
        return mConvertView;
    }
     
    @Override
    public void onClick(View v) {
            Log.v("CustomAdapter", "=====Row button clicked=====");
    }
     
    // Called when Item long click in ListView
    private class OnItemLongClickListener  implements OnLongClickListener{           
        private int mPosition;
        private ViewHolder mHolder;
        OnItemLongClickListener(int position, ViewHolder holder){
             mPosition = position;
             mHolder = holder;
        }

		@Override
		public boolean onLongClick(View arg0) {

          	//****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****//*
         	((CategoryFragment) fragment).onCategoryItemLongClick((Category) categoryItems.get(mPosition), categoryItems);
			return false;
		}               
    }  
    
    // Called when Item click in ListView
    private class OnItemClickListener  implements OnClickListener{           
        private int mPosition;
        private ViewHolder mHolder;
        
        OnItemClickListener(int position, ViewHolder holder){
             mPosition = position;
             mHolder = holder;
        }

		@Override
		public void onClick(View v) {
			//((CategoryFragment) fragment).onCategoryItemClick((Category) categoryItems.get(mPosition));
			
			MainActivity maiActivity = (MainActivity) activity;

          	//****  Call  onItemClick Method inside CustomListViewAndroidExample Class ( See Below )****//*
         	maiActivity.onCategoryItemClick((Category) categoryItems.get(mPosition), categoryItems);
		}               
    }
    
    @Override
    public void notifyDataSetChanged() {
    	categoryItems = Utils.getCategories(activity);
    	super.notifyDataSetChanged();
    }
}
