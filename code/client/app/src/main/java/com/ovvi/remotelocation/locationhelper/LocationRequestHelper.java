package com.ovvi.remotelocation.locationhelper;

import java.lang.ref.WeakReference;

import com.ovvi.remotelocation.constants.Constant;

import android.os.Handler;
import android.os.Message;

public class LocationRequestHelper implements Handler.Callback {
	
	private Handler mHandler;
	private WeakReference<UiLocationCallback> callback;
    public LocationRequestHelper(UiLocationCallback callback) {
    	mHandler = new Handler(this);
    	this.callback = new WeakReference<UiLocationCallback>(callback);
    }
    
    public Handler getHandler() {
    	return mHandler;
    }
    
    public void release() {
    	if (null != mHandler) {
    		mHandler.removeCallbacksAndMessages(null);
    		mHandler = null;
    	}
	}
    
    public void requestLocation() {
    	if (null != mHandler) {
    		Message message = mHandler.obtainMessage(Constant.MSG_LOCATION_REQUEST_FROM_LOCAL);
    		mHandler.sendMessage(message);			
		}
	}
    
    public void requestLocationDelayed(long milisecends) {
    	if (null != mHandler) {
    		Message message = mHandler.obtainMessage(Constant.MSG_LOCATION_REQUEST_FROM_LOCAL);
    		mHandler.sendMessageDelayed(message, milisecends);
    	}
	}
    
	@Override
	public boolean handleMessage(Message msg) {
		if (null == callback || null == callback.get()) {
			return false;
		}

        switch (msg.what) {
        case Constant.MSG_LOCATION_REQUEST_FROM_LOCAL:
        	callback.get().onRequestLocation();
            break;
        case Constant.MSG_LOCATION_RESPONSE_TO_LOCAL:
        	callback.get().onLocationResponse(msg.obj);
            break;
        }
        return false;
    
	}
}
