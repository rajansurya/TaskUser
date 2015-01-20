package com.taskmanager.background;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.taskmanager.app.R;
import com.taskmanager.bean.OTPValidateDTO;
import com.taskmanager.ui.AllTaskActivity;
import com.taskmanager.util.ApplicationUtil;

/**
 * receiver class to notified when SMS receivedT
 * @author mayankb
 *
 */
public class SMSReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) 
	{
		System.out.println("SMS receiver called....");
		//---get the SMS message passed in---
		Bundle bundle = intent.getExtras();       
		SmsMessage[] msgs = null;
		if (bundle != null)
		{
			//---retrieve the SMS message received---
			Object[] pdus = (Object[]) bundle.get("pdus");
			msgs = new SmsMessage[pdus.length];           
			for (int i=0; i<msgs.length; i++)
			{
				msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);               
//				if(msgs[i].getOriginatingAddress().trim().contains(ApplicationConstant.SentNum) && msgs[i].getMessageBody().toString().trim().equals(ApplicationConstant.OTPSent))
				{
					//		progressDialog = ProgressDialog.show(RegistrationActivity.this, "", getResources().getString(R.string.progress));
					SyncModule syncObj = new SyncModule(context);
					OTPValidateDTO responseDTO = new OTPValidateDTO();
					try {
						responseDTO = ApplicationUtil.getInstance().getSyncServer(context).getOTPValidate();
						if(responseDTO.getOTP_status().trim().equalsIgnoreCase(context.getResources().getString(R.string.success)))
						{
							syncObj.saveValidateOTPResponse(responseDTO);
							String validationStatus = responseDTO.getOTP_status();
							System.out.println("Validation Status is--->"+validationStatus);
							if(validationStatus.trim().equalsIgnoreCase(context.getResources().getString(R.string.success)))
							{
								/*Intent serIntent = new Intent(context, BGSyncService.class);
								context.startService(serIntent);*/
								//					progressHandler1.sendEmptyMessage(0);
								Intent activityIntent = new Intent(context, AllTaskActivity.class);
								activityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(activityIntent);
																
}
							else
							{
								//					progressHandler.sendEmptyMessage(0);
								syncObj.resendOTP();
							}
						}
						else
						{
							System.out.println("Validation Failed");
						}
					} catch (Exception e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					syncObj = null;




				}
			}
			//---display the new SMS message---
			//			Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
		}                        

	}


}