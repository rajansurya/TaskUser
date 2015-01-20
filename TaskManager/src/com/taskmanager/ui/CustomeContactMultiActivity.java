package com.taskmanager.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taskmanager.app.R;
import com.taskmanager.app.common.DownloadImageTask;
import com.taskmanager.app.common.NonProfilePhotoPlaceHolder;
import com.taskmanager.database.DBAdapter;
import com.taskmanager.dto.Contact;

public class CustomeContactMultiActivity extends Activity implements OnItemClickListener{

	List<Contact> contactList = new ArrayList<Contact>();
	List<Contact> searchResults = new ArrayList<Contact>();

	List<String> name1 = new ArrayList<String>();
	List<String> phno1 = new ArrayList<String>();
	MyAdapter ma ;
	Button select;
	ImageView done;
	ImageView cancel, search_icon, cancel_search;
	EditText searchBox;
	private CopyOnWriteArraySet<Contact> selectedNumber;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custome_contact);
		//       updateContactList(getContentResolver());
		searchBox=(EditText)findViewById(R.id.searchBox);
		done=(ImageView)findViewById(R.id.done);
		cancel=(ImageView)findViewById(R.id.cancel);
		search_icon = (ImageView) findViewById(R.id.search_icon);
		cancel_search = (ImageView) findViewById(R.id.cancel_search);

		selectedNumber=new CopyOnWriteArraySet<Contact>();

		getAllContacts(CustomeContactMultiActivity.this.getContentResolver());

		/*new Thread(new Runnable() {

		@Override
		public void run() {
			 getAllContacts(CustomeContactMultiActivity.this.getContentResolver());

		}
	}).start();*/
		cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
		//		Intent intent = new Intent(CustomeContactMultiActivity.this,AllTaskActivity.class);
			//	intent.putExtra("changedAssigneeNum", "");
			//	intent.putExtra("changedAssigneeName", "");

				//startActivityForResult(intent, Activity.RESULT_OK);
		//		startActivity(intent);
			//	setResult(Activity.RESULT_OK, intent);
				finish();
				overridePendingTransition(0, R.anim.slide_out_to_bottom);

			}
		});
		done.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				StringBuilder checkedcontacts= new StringBuilder();
				StringBuilder numbers= new StringBuilder();
				if(ma instanceof MyAdapter ){
					MyAdapter adapter=(MyAdapter)ma;
					System.out.println(".............."+adapter.mCheckStates.size());
					if(selectedNumber.size()>0)
					{
					for (Contact  number : selectedNumber) {
						String name=number.getName();
						if(name!=null){
							name=name.replace(",", "");
						}
						checkedcontacts.append(name);
						checkedcontacts.append(",");
						numbers.append(number.getNumber());
						numbers.append(",");
					}
					numbers.replace(numbers.length()-1, numbers.length(), "");
					checkedcontacts.replace(checkedcontacts.length()-1, checkedcontacts.length(), "");
					/*for(int i = 0; i < selectedNumber.size(); i++)

                    {
                	checkedcontacts.append(selectedNumber.element());
                    checkedcontacts.append("\n");
                    numbers.append(searchResults.get(i).getNumber());
                    numbers.append(",");
                    if(adapter.mCheckStates.get(i)==true)
                    {
                    	//List<String> numbers=searchResults.get(i).getNumbers();
                    		checkedcontacts.append(searchResults.get(i).getName());
                            checkedcontacts.append("\n");
                            numbers.append(searchResults.get(i).getNumber());
                            numbers.append(",");



                    }
                    else
                    {
                      //  System.out.println("Not Checked......"+name1.get(i).toString());   Here is... Adfg,Dtgvg,Thvg,Tug

                    }


                }*/

					Intent intent = new Intent();
					intent.putExtra("changedAssigneeNum", numbers.toString());
					intent.putExtra("changedAssigneeName", checkedcontacts.toString());
					System.out.println("Here is the contact..... " + checkedcontacts.toString() );
					/*Intent intent = new Intent(Intent.ACTION_PICK,
						Contacts.CONTENT_URI);*/
					//startActivityForResult(intent, Activity.RESULT_OK);
					setResult(Activity.RESULT_OK, intent);
					finish();
					overridePendingTransition(0, R.anim.slide_out_to_bottom);
					}
					else
					{
							Toast.makeText(
									getApplicationContext(),
									getResources().getString(
											R.string.blank_contact_toast),
											Toast.LENGTH_LONG).show();
					}
					//dialog.dismiss();
					//Toast.makeText(CustomeContactActivity.this, checkedcontacts.toString(),1000).show();
				}
			}
		});
		ListView lv= (ListView) findViewById(R.id.lv);
		ma = new MyAdapter(searchResults);
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
		/*  select = (Button) findViewById(R.id.);*/



		/*select.setOnClickListener(new OnClickListener()
        {

            @Override
            public void onClick(View v) {


                  StringBuilder checkedcontacts= new StringBuilder();
                System.out.println(".............."+ma.mCheckStates.size());
                for(int i = 0; i < searchResults.size(); i++)

                    {
                    if(ma.mCheckStates.get(i)==true)
                    {
                         checkedcontacts.append(searchResults.get(i).getName());
                         checkedcontacts.append("\n");

                    }
                    else
                    {
                      //  System.out.println("Not Checked......"+name1.get(i).toString());
                    }


                }

                Toast.makeText(CustomeContactActivity.this, selectedNumber.toString(),1000).show();
            }       
        });
		 */

		cancel_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				searchBox.setText("");
				search_icon.setVisibility(View.VISIBLE);
				//searchResults.clear();
			}
		});

	}
	
	@Override
	public void onBackPressed() {
		overridePendingTransition(0, R.anim.slide_out_to_bottom);
		super.onBackPressed();
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
	class MyAdapter extends BaseAdapter //implements CompoundButton.OnCheckedChangeListener
	{  //private SparseBooleanArray mCheckStates;
		private SparseBooleanArray mCheckStates;
		LayoutInflater mInflater;
		List<Contact> contacts;
		TextView tv1,tv;
		//		ImageView reg_img;
		CheckBox cb;
		MyAdapter(List<Contact> contacts){
			this();

			this.contacts=contacts;
			mCheckStates = new SparseBooleanArray(contacts.size());
		}
		MyAdapter()
		{
			//  mCheckStates = new SparseBooleanArray(name1.size());
			mInflater = (LayoutInflater)CustomeContactMultiActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			// TODO Auto-generated method stub
			final Contact contact=contacts.get(position);
			View vi=convertView;
			if(convertView==null)
				vi = mInflater.inflate(R.layout.row, null); 
			TextView tv= (TextView) vi.findViewById(R.id.textView1);
			TextView tv1 = (TextView) vi.findViewById(R.id.textView2);
			ImageView reg_img = (ImageView) vi.findViewById(R.id.regStatus);
			ImageView contactIcon = (ImageView)vi.findViewById(R.id.contactIcon);
			LinearLayout linearLayout=(LinearLayout) vi.findViewById(R.id.rowlinear);
			
			contactIcon.setBackgroundResource(NonProfilePhotoPlaceHolder.getPhotoPlaceHolder(contact.getName()));
			DownloadImageTask.setImage(CustomeContactMultiActivity.this, contact.getNumber(), contactIcon, true);
			
			if(position%2==0){
				// tv.setBackgroundResource(R.color.grey);
			}else{
				tv.setBackgroundResource(0);
			}
			
			// tv1= (TextView) vi.findViewById(R.id.textView2);
			
			cb = (CheckBox) vi.findViewById(R.id.checkBox_id);
			cb.setTag(position);
			cb.setChecked(contact.isSelected());
			cb.setVisibility(View.VISIBLE);
			tv.setText(contact.getName());
			tv1.setText(contact.getNumber());

			System.out.println("Status is ..... " + contact.getRegStatus() + "    >>>>  Mobile number.."+contact.getNumber()) ;

			if(contact.getRegStatus().equalsIgnoreCase("REGISTERED") )
			{
				reg_img.setBackgroundResource(R.drawable.contacts);
			}else{
				reg_img.setBackgroundResource(0);
			}

			linearLayout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					//	Toast toast=Toast.makeText(CustomeContactMultiActivity.this, contact.getNumber(),Toast.LENGTH_SHORT);
					//	toast.setGravity(Gravity.CENTER, 0, 0);
					//	toast.show();
				}
			});

			cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					mCheckStates.put((Integer)buttonView.getTag(), isChecked);
					Contact contact=contacts.get((Integer)buttonView.getTag());


					if(isChecked)	{
						contact.setSelected(true);
						/*Contact contact2=new Contact();
						contact2.setName(contact.getName());
						contact2.setNumber(contact.getNumber())*/
						selectedNumber.add(contact);


					}else{
						contact.setSelected(false);
						selectedNumber.remove(contact);

					}
				}
			});

			return vi;
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
				initialValues.put("REG_STATUS", "N");
				adapter.insertRecordsInDB("USERS_CONTACT", null, initialValues);
				System.out.println("Here are the numbers .................."+phoneNumber); 
			}

			phones.close();
			adapter.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
     }*/

}