package com.yzx.listenerInterface;  

import java.util.ArrayList;

import android.content.Context;

/**
 * @Title VoipListenerManager   
 * @Description  voip监听管理类
 * @Company yunzhixun  
 * @author xhb
 * @date 2016-9-29 下午3:20:29
 */
public class VoipListenerManager {
	private static VoipListenerManager voipListenerManager;
	private static ArrayList<ConnectionListener> connectionListeners = new ArrayList<ConnectionListener>();//联接状态监听器
	private static ArrayList<CallStateListener> callStateListenerList = new ArrayList<CallStateListener>();
	private static ForwardingListener mForwardingListener;
	private static PreviewImgUrlListener mPreviewImgUrlListener;
	
	private VoipListenerManager() {}
	
	public static VoipListenerManager getInstance() {
		if(voipListenerManager == null) {
			synchronized (VoipListenerManager.class) {
				if(voipListenerManager == null) {
					voipListenerManager = new VoipListenerManager();
				}
			}
		}
		return voipListenerManager;
	}
	
	public void addConnectionListener(ConnectionListener connectionListener) {
			connectionListeners.add(connectionListener);
	}
	
	public ArrayList<ConnectionListener> getConnectionListener() {
		return connectionListeners;
	}
	
	public void removeConnectionListener(ConnectionListener connectionListener) {
		connectionListeners.remove(connectionListener);
	}
	
	public ArrayList<CallStateListener> getCallStateListener() {
		return callStateListenerList;
	}
	
	public void addCallStateListener(CallStateListener csl){
		callStateListenerList.add(csl);
	}
	
	public void removeCallStateListener(CallStateListener csl){
		callStateListenerList.remove(csl);
	}
	
	public void setForwardingListener(ForwardingListener forwardingListener) {
		mForwardingListener = forwardingListener;
	}
	
	public ForwardingListener getForwardingListener() {
		return mForwardingListener;
	}
	
	public void setPreviewImgUrlListener(PreviewImgUrlListener previewImgUrlListener) {
		mPreviewImgUrlListener = previewImgUrlListener;
	}
	
	public PreviewImgUrlListener getPreviewImgUrlListener() {
		return mPreviewImgUrlListener;
	}
}
  
