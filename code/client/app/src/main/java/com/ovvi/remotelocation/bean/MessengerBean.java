package com.ovvi.remotelocation.bean;

import android.os.Messenger;

public class MessengerBean {
	public Messenger messenger;
	public int targetId;
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
            return true;  
        }
        if(obj == null) {
            return false;  
        }
        if(!(obj instanceof MessengerBean)) {
            return false;  
        }
        final MessengerBean bean = (MessengerBean)obj;  
        if(!this.messenger.equals(bean.messenger)) {
            return false;  
        }
        if(this.targetId != bean.targetId) {
            return false;  
        }
        return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((null == messenger) ? 0 : messenger.hashCode());
		result = prime * result + targetId;
		return result;
	}
}
