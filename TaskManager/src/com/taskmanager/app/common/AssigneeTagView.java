package com.taskmanager.app.common;

import java.io.ByteArrayInputStream;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.PhoneLookup;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.taskmanager.app.R;

public class AssigneeTagView {
	private static float density;

	public static SpannableStringBuilder getAssigneeView(Context context, String assigneeName, String assigneeNumber){
		density = context.getResources().getDisplayMetrics().density;

		View tv = createContactTextView(context, assigneeName, assigneeNumber);
		BitmapDrawable bd = (BitmapDrawable) convertViewToDrawable(tv);
		bd.setBounds(0, 0, bd.getIntrinsicWidth(),bd.getIntrinsicHeight());

		SpannableStringBuilder sb = new SpannableStringBuilder();
		sb.append(assigneeName);
		sb.setSpan(new ImageSpan(bd), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return sb;
	}

	private static View createContactTextView(Context context, String text, String assigneeNumber){
		TextView tv = new TextView(context);
		tv.setTextSize(14 * density);
		tv.setTextColor(Color.BLACK);
		tv.setBackgroundResource(R.drawable.bg_assignee_text);
		tv.setText(text);
		tv.setPadding((int)(10 *density), (int)(-10 *density), 0, 0);
		tv.append(getImageSpannable(context, assigneeNumber));
		return tv;
		
//		LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		View view = inflater.inflate(R.layout.assignee_tag, null);
//		TextView nameTextView = (TextView)view.findViewById(R.id.TagNameTextView);
//		nameTextView.setTextSize(14 * density);
//		ImageView photoImageView = (ImageView)view.findViewById(R.id.TagPhotoImageView);
//		photoImageView.setImageDrawable(getAssigneePhoto(context, assigneeNumber));
//		nameTextView.setText(text);
//		return view;
	}

	private static Object convertViewToDrawable(View view) {
		int spec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		view.measure(spec, spec);
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		Bitmap b = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		c.translate(-view.getScrollX(), -view.getScrollY());
		view.draw(c);
		view.setDrawingCacheEnabled(true);
		Bitmap cacheBmp = view.getDrawingCache();
		Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
		view.destroyDrawingCache();
		return new BitmapDrawable(viewBmp);

	}

	private static Spanned getImageSpannable(final Context context, final String assigneeNumber){
		ImageGetter imageGetter = new ImageGetter() {
			@Override public Drawable getDrawable(String source) {
				Drawable drawable = null;
				drawable = getPhotoDrawable(context, assigneeNumber);
				if(drawable == null){
					drawable = context.getResources().getDrawable(R.drawable.image_bg);
				}
				int intrinsic = 70;
//				drawable.setBounds(0, 0, (int)(drawable.getIntrinsicWidth() *density), (int)(drawable.getIntrinsicHeight() *density));
				drawable.setBounds(0, 0, (int)(intrinsic *density), (int)(intrinsic *density));
				return drawable;
			}
		};
		Spanned spanned = Html.fromHtml("<img src=' '/>", imageGetter, null);
		return spanned;
	}
	
	private static Drawable getAssigneePhoto(final Context context, final String assigneeNumber){
		Drawable drawable = null;
		drawable = getPhotoDrawable(context, assigneeNumber);
		if(drawable == null){
			drawable = context.getResources().getDrawable(R.drawable.image_bg);
		}
		int intrinsic = 70;
		drawable.setBounds(0, 0, (int)(intrinsic *density), (int)(intrinsic *density));
		return drawable;
	}
	
	private static Drawable getPhotoDrawable(Context context, String phoneNumber) {
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
					return Drawable.createFromStream(new ByteArrayInputStream(data), null);
				}
			}
		} finally {
			cursor.close();
		}
		return null;
	}

}
