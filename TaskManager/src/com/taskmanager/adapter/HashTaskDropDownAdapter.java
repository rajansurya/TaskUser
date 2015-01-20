package com.taskmanager.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.taskmanager.app.R;
import com.taskmanager.app.common.CustomTypeFace;

@SuppressLint("DefaultLocale")
public class HashTaskDropDownAdapter extends ArrayAdapter<String> {
	private List<String> mOriginalList;
	private List<String> mHashTaskList;
	private Context mContext;

	private LayoutInflater mInflater;
	private CutomFilter mCutomFilter;
	private String mFilterTextString;
	private int mFilterColorId;
	
	public HashTaskDropDownAdapter(Context context, List<String> list) {
		super(context, R.layout.hash_tag_list_drop_down, list);
		mHashTaskList = mOriginalList = list;
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFilterColorId = context.getResources().getColor(R.color.selected_tab_text_color);
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		String hashTask = mHashTaskList.get(position);
		
		if(view == null){
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.hash_tag_list_drop_down, parent, false);
			holder.textView = (TextView)view.findViewById(R.id.nameTextView);
			holder.textView.setTypeface(CustomTypeFace.getRobotoMedium(mContext));

			view.setTag(holder);
		}

		holder = (ViewHolder)view.getTag();
		holder.textView.setText(hashTask, BufferType.SPANNABLE);
		
		String nameString = holder.textView.getText().toString().toLowerCase();
		SpannableString text = (SpannableString)holder.textView.getText();
		if(mFilterTextString != null && mFilterTextString.length() > 0 && nameString.contains(mFilterTextString)){
			int from = nameString.indexOf(mFilterTextString);
			int to = from + mFilterTextString.length();
			for(;from < to; from++){
				text.setSpan(new ForegroundColorSpan(mFilterColorId), from, from+1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.textView.setText(text, BufferType.SPANNABLE);
			}
		}
		return view;
	} 
	
	private class ViewHolder {
		TextView textView;
	}
	
	@Override
	public int getCount() {
		return mHashTaskList.size();
	}
	
	@Override
	public String getItem(int position) {
		return mHashTaskList.get(position);
	}

	@Override
	public Filter getFilter() {
		if(mCutomFilter == null){
			mCutomFilter = new CutomFilter();
		}
		return mCutomFilter;
	}
	
	private class CutomFilter extends Filter {
		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			if(constraint == null){
				mHashTaskList.clear();
				notifyDataSetChanged();
				return;
			}
			mFilterTextString = constraint.toString().toLowerCase();
			mHashTaskList = (ArrayList<String>)results.values;
			notifyDataSetChanged();
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			ArrayList<String> list = new ArrayList<String>();
			for(String hashTask : mOriginalList){
				if(hashTask.toLowerCase().contains(constraint.toString().toLowerCase())){
					list.add(hashTask);
				}
			}
			FilterResults results = new FilterResults();
			results.values = list;
			results.count = list.size();
			return results;
		}
	}
}
