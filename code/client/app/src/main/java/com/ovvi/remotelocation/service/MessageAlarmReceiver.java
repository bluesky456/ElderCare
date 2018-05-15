package com.ovvi.remotelocation.service;

import com.ovvi.remotelocation.constants.Constant;
import com.ovvi.remotelocation.utils.LogUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MessageAlarmReceiver extends BroadcastReceiver {
	private final static String TAG = "MessageAlarmReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		LogUtils.d(TAG, "MessageAlarmReceiver:onReceive():action:" + intent.getAction());
		if (Constant.ACTION_MESSAGE_SERVICE_WAKEUP.equals(intent.getAction())) {
			Intent intentService = new Intent(context, MessageService.class);
			intentService.setAction(Constant.ACTION_ENABLE_MESSAGE_SERVICE);
			context.startService(intentService);
		}
		
	}
}
