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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.taskmanager.app.R;
import com.taskmanager.app.common.CustomTypeFace;
import com.taskmanager.app.common.DownloadImageTask;
import com.taskmanager.app.common.NonProfilePhotoPlaceHolder;
import com.taskmanager.dto.Contact;

@SuppressLint("DefaultLocale")
public class ContactListDropDownAdapter extends ArrayAdapter<Contact>{
	private List<Contact> mOriginalList;
	private List<Contact> mContactList;
	private Context mContext;

	private LayoutInflater mInflater;
	private CutomFilter mCutomFilter;
	private String mFilterTextString;
	private int mFilterColorId;
	
	public ContactListDropDownAdapter(Context context, List<Contact> list) {
		super(context, R.layout.contact_list_drop_down, list);
		mContactList = mOriginalList = list;
		mContext = context;
		mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mFilterColorId = context.getResources().getColor(R.color.selected_tab_text_color);
	}
	
	public View getView(int position, View view, ViewGroup parent) {
		ViewHolder holder;
		Contact contact = mContactList.get(position);
		
		if(view == null){
			holder = new ViewHolder();
			view = mInflater.inflate(R.layout.contact_list_drop_down, parent, false);
			holder.contactIcon = (ImageView)view.findViewById(R.id.contactIcon);
			holder.nameTextView = (TextView)view.findViewById(R.id.nameTextView);
			holder.nameTextView.setTypeface(CustomTypeFace.getRobotoMedium(mContext));
			holder.numberTextView = (TextView)view.findViewById(R.id.numberTextView);
			holder.numberTextView.setTypeface(CustomTypeFace.getRobotoLight(mContext));
			
			view.setTag(holder);
		}

		holder = (ViewHolder)view.getTag();
		holder.numberTextView.setText(contact.getNumber());
		holder.nameTextView.setText(contact.getName(), BufferType.SPANNABLE);
		if(contact.getRegStatus().equalsIgnoreCase("REGISTERED") ) {
			holder.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.small_monky, 0);
		}else{ 
			holder.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		
		holder.contactIcon.setBackgroundResource(NonProfilePhotoPlaceHolder.getPhotoPlaceHolder(contact.getName()));
//		holder.contactIcon.setBackgroundDrawable(mContext.getResources().getDrawable(NonProfilePhotoPlaceHolder.getPhotoPlaceHolder(contact.getName())));
		DownloadImageTask.setImage(mContext, contact.getNumber(), holder.contactIcon, true);
		
		String nameString = holder.nameTextView.getText().toString().toLowerCase();
		SpannableString text = (SpannableString)holder.nameTextView.getText();
		if(mFilterTextString != null && mFilterTextString.length() > 0 && nameString.contains(mFilterTextString)){
			int from = nameString.indexOf(mFilterTextString);
			int to = from + mFilterTextString.length();
			for(;from < to; from++){
				text.setSpan(new ForegroundColorSpan(mFilterColorId), from, from+1, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
				holder.nameTextView.setText(text, BufferType.SPANNABLE);
			}
		}
		return view;
	} 
	
	private class ViewHolder {
		ImageView contactIcon;
		TextView nameTextView;
		TextView numberTextView;
	}
	
	@Override
	public int getCount() {
		return mContactList.size();
	}
	
	@Override
	public Contact getItem(int position) {
		return mContactList.get(position);
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
				mContactList.clear();
				notifyDataSetChanged();
				return;
			}
			mFilterTextString = constraint.toString().toLowerCase();
			mContactList = (ArrayList<Contact>)results.values;
			notifyDataSetChanged();
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			ArrayList<Contact> list = new ArrayList<Contact>();
			for(Contact contact : mOriginalList){
				if(contact.getName().toLowerCase().contains(constraint.toString().toLowerCase())){
					list.add(contact);
				}
			}
			FilterResults results = new FilterResults();
			results.values = list;
			results.count = list.size();
			return results;
		}
	}
}
