package com.taskmanager.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmanager.app.R;
import com.taskmanager.app.common.DownloadImageTask;
import com.taskmanager.app.common.NonProfilePhotoPlaceHolder;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.Contact;

public class CustomeContactActivity extends Activity implements OnItemClickListener{

	List<Contact> contactList = new ArrayList<Contact>();
	List<Contact> searchResults = new ArrayList<Contact>();

	List<String> name1 = new ArrayList<String>();
	List<String> phno1 = new ArrayList<String>();
	BaseAdapter ma ;
	Button select;
	ImageView done;
	ImageView cancel, search_icon, cancel_search;
	EditText searchBox;
	private ConcurrentLinkedQueue<String> selectedNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custome_contact);
		//	updateContactList(getContentResolver());
		searchBox=(EditText)findViewById(R.id.searchBox);
		done=(ImageView)findViewById(R.id.done);
		cancel=(ImageView)findViewById(R.id.cancel);
		search_icon = (ImageView) findViewById(R.id.search_icon);
		cancel_search = (ImageView) findViewById(R.id.cancel_search);
		done.setVisibility(View.INVISIBLE);
		getAllContacts(CustomeContactActivity.this.getContentResolver());

		selectedNumber=new ConcurrentLinkedQueue<String>();
		/*new Thread(new Runnable() {

			@Override
			public void run() {
				getAllContacts(CustomeContactActivity.this.getContentResolver());

			}
		}).start();
		 */		cancel.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 /*Intent intent = new Intent(CustomeContactActivity.this,AllTaskActivity.class);
             intent.putExtra("changedAssigneeNum", "");
             intent.putExtra("changedAssigneeName", "");

				//startActivityForResult(intent, Activity.RESULT_OK);
				setResult(Activity.RESULT_OK, intent);*/
				 finish();


			 }
		 });

		 cancel_search.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 // TODO Auto-generated method stub
				 searchBox.setText("");
				 search_icon.setVisibility(View.VISIBLE);
				 //searchResults.clear();
			 }
		 });

		 ListView lv= (ListView) findViewById(R.id.lv);
		 ma = new MyAdapterRadio(searchResults);

		 /*if(savedInstanceState.getBoolean("CHANGE")){
        	 ma = new MyAdapterRadio(searchResults);
        }else{
        	 ma = new MyAdapter(searchResults);
        }*/

		 //  ma = new MyAdapterRadio(searchResults);
		 lv.setAdapter(ma);
		 lv.setOnItemClickListener(this); 
		 lv.setItemsCanFocus(false);
		 lv.setTextFilterEnabled(true);
		 // adding
		 searchBox.setOnClickListener(new OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 searchBox.setCursorVisible(true);	
			 }
		 });

		 searchBox.addTextChangedListener(new TextWatcher() {

			 @Override
			 public void onTextChanged(CharSequence s, int start, int before, int count) {
				 String searchString=searchBox.getText().toString();
				 int textLength=searchString.length();

				 if(s.length()>0)
				 {
					 search_icon.setVisibility(View.GONE);
				 }
				 else
				 {
					 search_icon.setVisibility(View.VISIBLE);
				 }

				 //clear the initial data set
				 searchResults.clear();

				 for(int i=0;i<contactList.size();i++)
				 {
						//Contact contact=contactList.get(i);
						String playerName=contactList.get(i).getName().toUpperCase();
						//if(textLength<=playerName.length()){
							//compare the String in EditText with Names in the ArrayList
							if(playerName.indexOf(searchString.toUpperCase())!=-1){					    	
								searchResults.add(contactList.get(i));
							}
						//}
					}

				 ma.notifyDataSetChanged();

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

	HashMap<Integer, Boolean> map=new HashMap<Integer, Boolean>();
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		// ma.toggle(arg2);

	}
	public  void getAllContacts(ContentResolver cr) {
		try {
			DBAdapter adapter=DBAdapter.getInstance(this);
			adapter.openDataBase();
			contactList=adapter.getContacList();
			searchResults.addAll(contactList);
			adapter.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private Contact isNameExist(String name,List<Contact> list) {

		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			Contact contact = (Contact) iterator.next();
			if(name.equalsIgnoreCase(contact.getName())){
				return contact;
			}


		}
		return null;
	}

	class MyAdapterRadio extends BaseAdapter //implements CompoundButton.OnCheckedChangeListener
	{  //private SparseBooleanArray mCheckStates;
		private SparseBooleanArray mCheckStates;
		LayoutInflater mInflater;
		List<Contact> contacts;
		TextView tv1,tv;
		//	CheckBox cb;
		MyAdapterRadio(List<Contact> contacts){
			this();

			this.contacts=contacts;
			mCheckStates = new SparseBooleanArray(contacts.size());
		}
		MyAdapterRadio()
		{
			//  mCheckStates = new SparseBooleanArray(name1.size());
			mInflater = (LayoutInflater)CustomeContactActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return contacts.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub

			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final Contact contact=contacts.get(position);
			View vi=convertView;
			if(convertView==null)
				vi = mInflater.inflate(R.layout.row, null); 
			TextView tv= (TextView) vi.findViewById(R.id.textView1);
			TextView tv1 = (TextView) vi.findViewById(R.id.textView2);
			ImageView reg_img = (ImageView) vi.findViewById(R.id.regStatus);
			ImageView contactIcon = (ImageView)vi.findViewById(R.id.contactIcon);
			//			RadioButton radio_btn = (RadioButton) vi.findViewById(R.id.radioButton_id);
			LinearLayout linearLayout=(LinearLayout) vi.findViewById(R.id.rowlinear);
			linearLayout.setTag(position);
			//	radio_btn.setVisibility(View.VISIBLE);
			//	ImageView regStatus = (ImageView) vi.findViewById(R.id.regStatus);

			tv.setText(contact.getName());
			tv1.setText(contact.getNumber());

			if(contact.getRegStatus().equalsIgnoreCase("REGISTERED"))
			{
				reg_img.setBackgroundResource(R.drawable.contacts);
			}else{
				reg_img.setBackgroundResource(0);
			}

			contactIcon.setBackgroundResource(NonProfilePhotoPlaceHolder.getPhotoPlaceHolder(contact.getName()));
			DownloadImageTask.setImage(CustomeContactActivity.this, contact.getNumber(), contactIcon, true);
			
			linearLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final Contact contact=contacts.get((Integer)v.getTag());
					Intent intent = new Intent(CustomeContactActivity.this,AllTaskActivity.class);
					intent.putExtra("changedAssigneeNum", contact.getNumber());
					intent.putExtra("changedAssigneeName", contact.getName());

					setResult(Activity.RESULT_OK, intent);
					finish();

				}
			});


			return vi;
		}

	} 

	/*public  void updateContactList(ContentResolver cr) {
		try {
			DBAdapter adapter=DBAdapter.getInstance(this);
			adapter.openDataBase();
			Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);
			while (phones.moveToNext())
			{
				String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				ContentValues initialValues = new ContentValues();
				initialValues.put("NAME", name);
				initialValues.put("MOBILE_NUMBER", phoneNumber);
				//initialValues.put("REG_STATUS", "N");
				adapter.insertRecordsInDB("USERS_CONTACT", null, initialValues);
				System.out.println(".................."+phoneNumber); 
			}

			phones.close();
			adapter.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	 */}