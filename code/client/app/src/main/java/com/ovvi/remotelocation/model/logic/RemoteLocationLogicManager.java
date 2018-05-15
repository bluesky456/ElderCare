package com.ovvi.remotelocation.model.logic;

import android.content.Context;

public class RemoteLocationLogicManager {

	Context context;
	private RemoteLocationLogicPolicy remoteLocationLogicPolicy;
	static RemoteLocationLogicManager instance = null;
	public static RemoteLocationLogicManager getInstance(Context context) {
		if (null == instance) {
			instance = new RemoteLocationLogicManager(context.getApplicationContext());
		}
		return instance;
	}
	
	private RemoteLocationLogicManager(Context context) {
		this.context = context;
	}
	
	public RemoteLocationLogicPolicy getRemoteLocationLogicPolicy() {
		if (null == remoteLocationLogicPolicy) {
			remoteLocationLogicPolicy = new RemoteLocationLogicPolicy(context);
		}

		return remoteLocationLogicPolicy;
	}
}
