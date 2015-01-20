package com.taskmanager.app.common;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.WeakHashMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class DownloadImageTask extends AsyncTask<Long, Void, BitmapDrawable> {
	private View view;
	private ProgressBar progressBar;
	private String phoneNumber;
	private Context context;
	private boolean circular;
	
	private static WeakHashMap<String, BitmapDrawable> cacheCircularPhotoHashMap;
	private static WeakHashMap<String, BitmapDrawable> cacheSquirePhotoHashMap;

	public static void setImage(Context context, String phoneNumber, View view, boolean circular){
		setImage(context, phoneNumber, view, null, circular);
	}
	
	public static void setImage(Context context, String phoneNumber, View view, ProgressBar progressBar, boolean circular){
		if(phoneNumber == null){
			return;
		}
		if(cacheCircularPhotoHashMap == null){
			cacheCircularPhotoHashMap = new WeakHashMap<String, BitmapDrawable>();
		}
		if(cacheSquirePhotoHashMap == null){
			cacheSquirePhotoHashMap = new WeakHashMap<String, BitmapDrawable>();
		}
		
		if(circular){
			if(cacheCircularPhotoHashMap.containsKey(phoneNumber)){
				updateUI(cacheCircularPhotoHashMap.get(phoneNumber), phoneNumber, view, progressBar, circular);
			} else {
				new DownloadImageTask(context, phoneNumber, view, progressBar, circular).execute();
			}
		} else {
			if(cacheSquirePhotoHashMap.containsKey(phoneNumber)){
				updateUI(cacheSquirePhotoHashMap.get(phoneNumber), phoneNumber, view, progressBar, circular);
			} else {
				new DownloadImageTask(context, phoneNumber, view, progressBar, circular).execute();
			}
		}
	}
	
	private DownloadImageTask(Context context, String phoneNumber, View view, ProgressBar progressBar, boolean circular) {
		this.phoneNumber = phoneNumber;
		this.view = view;
		this.progressBar = progressBar;
		this.context = context; 
		this.circular = circular;
	}

	protected BitmapDrawable doInBackground(Long... urls) {
		if(phoneNumber == null){
			return null;
		}
		
		BitmapDrawable drawable = null;
		try {
//			InputStream in = new java.net.URL(url).openStream();
			Bitmap bitmap = BitmapFactory.decodeStream(getPhotoInputStream(context, phoneNumber));
			if(circular){
				drawable = new BitmapDrawable(context.getResources(), getCroppedBitmap(bitmap));
			} 
			else {
				drawable = new BitmapDrawable(context.getResources(), bitmap);
			}
		} catch (Exception e) {
//			Log.e("Error", e.getMessage());
			e.printStackTrace();
		}
		return drawable;
	}

	protected void onPostExecute(BitmapDrawable drawable) {
		updateCache(drawable, phoneNumber, circular);
		updateUI(drawable, phoneNumber, view, progressBar, circular);
	}
	
	@SuppressWarnings("deprecation")
	private static void updateUI(BitmapDrawable drawable, String phoneNumber, View view, ProgressBar progressBar, boolean circular){
//		if(drawable != null){
			if(view instanceof ImageView){
				((ImageView)view).setImageDrawable(drawable != null ? drawable : view.getBackground());
			} else {
				view.setBackgroundDrawable(drawable != null ? drawable : view.getBackground());
			}
//		}
		if(progressBar != null){
			progressBar.setVisibility(View.INVISIBLE);
		}
	}

	private static void updateCache(BitmapDrawable drawable, String phoneNumber, boolean circular){
		if(circular){
			if(cacheCircularPhotoHashMap != null && cacheCircularPhotoHashMap.containsKey(phoneNumber) == false){
				cacheCircularPhotoHashMap.put(phoneNumber, drawable);
			}
		} else {
			if(cacheSquirePhotoHashMap != null && cacheSquirePhotoHashMap.containsKey(phoneNumber) == false){
				cacheSquirePhotoHashMap.put(phoneNumber, drawable);
			}
		}
	}

	private InputStream getPhotoInputStream(Context context, String phoneNumber) {
		long contactId = -1000;
		Uri phoneUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		ContentResolver cr = context.getContentResolver();
		Cursor contact = cr.query(phoneUri, new String[] { ContactsContract.Contacts._ID }, null, null, null);

		if (contact.moveToFirst()) {
			contactId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
		}

		Uri contactUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
		Uri photoUri = Uri.withAppendedPath(contactUri, Contacts.Photo.CONTENT_DIRECTORY);
		Cursor cursor = context.getContentResolver().query(photoUri, new String[] {Contacts.Photo.PHOTO}, null, null, null);
		if (cursor == null) {
			return null;
		}
		try {
			if (cursor.moveToFirst()) {
				byte[] data = cursor.getBlob(0);
				if (data != null) {
					return new ByteArrayInputStream(data);
				}
			}
		} finally {
			cursor.close();
		}
		return null;
	}

	private Bitmap getCroppedBitmap(Bitmap bitmap) {
	    Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
	    Canvas canvas = new Canvas(output);

	    final int color = 0xff424242;
	    final Paint paint = new Paint();
	    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

	    paint.setAntiAlias(true);
	    paint.setDither(true);
	    canvas.drawARGB(0, 0, 0, 0);
	    paint.setColor(color);
	    // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	    canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
	    paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	    canvas.drawBitmap(bitmap, rect, rect, paint);
	    //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
	    //return _bmp;
	    
	    //Outer Stroke 
//	    paint.setColor(Color.parseColor("#c9d2d7"));
//		paint.setStyle(Paint.Style.STROKE);
//		paint.setStrokeWidth(5);
//		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
	    
	    return output;
	}
}
