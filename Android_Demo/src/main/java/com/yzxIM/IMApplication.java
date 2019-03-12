package com.yzxIM;

import com.yzxtcp.UCSManager;

import android.app.Application;

public class IMApplication extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		UCSManager.init(this);
		IMManager.getInstance(this);
	}
}
