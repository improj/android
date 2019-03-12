package com.yzx.listenerInterface;

import com.yzxtcp.data.UcsReason;

public interface ForwardingListener {
	public void onCallForwardingIndicatorChanged(UcsReason reason);
}
