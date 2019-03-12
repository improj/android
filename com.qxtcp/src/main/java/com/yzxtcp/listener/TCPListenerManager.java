package com.yzxtcp.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yzxtcp.data.UcsReason;
import com.yzxtcp.tools.CustomLog;

public class TCPListenerManager {

	private List<ILoginListener> loginListeners = new ArrayList<ILoginListener>();
	private Map<String, ITcpRecvListener> tcpMapListeners = new HashMap<String, ITcpRecvListener>();
//	private IServiceListener serviceListener;
	private List<IReLoginListener> reLoginListeners = new ArrayList<IReLoginListener>();
	private List<ISdkStatusListener> iSdkStatusListeners = new ArrayList<ISdkStatusListener>();
	private List<OnRecvTransUCSListener> iOnTransListeners = new ArrayList<OnRecvTransUCSListener>();
	
	public static TCPListenerManager imListenerManager;
	public OnRecvPerviewImgTransListener perviewImgTransListener;

	public static TCPListenerManager getInstance() {
		if (imListenerManager == null) {
			synchronized (TCPListenerManager.class) {
				if (imListenerManager == null) {
					imListenerManager = new TCPListenerManager();
				}
			}
		}

		return imListenerManager;
	}

	public void setOnRecvTransUCSListener(OnRecvTransUCSListener listener){
		synchronized (iOnTransListeners) {
			iOnTransListeners.add(listener);
		}
	}
	
	public void removeOnRecvTransUCSListener(OnRecvTransUCSListener listener){
		synchronized (iOnTransListeners) {
			iOnTransListeners.remove(listener);
		}
	}
	
	public void notifyOnRecvTransUCSListener(String userId,String data, String callid, String previewImgUrl){
		synchronized (iOnTransListeners) {
			Iterator<OnRecvTransUCSListener> it = iOnTransListeners.iterator();
			while(it.hasNext()){
				OnRecvTransUCSListener listener = it.next();
				listener.onRecvTranslate(userId, data, callid, previewImgUrl);
			}
		}
	}
	// 登陆监听器
	public void setLoginListener(ILoginListener cl) {
		synchronized (loginListeners) {
			loginListeners.add(cl);
		}
		
	}

	public void notifiLoginListener(UcsReason reason) {
		synchronized (loginListeners){
			for(int i = 0; i < loginListeners.size(); i++){
				loginListeners.get(i).onLogin(reason);
				//延迟100MS用于数据库初始化
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
	}

	public void delLoginListenerList(ILoginListener cl) {
		synchronized (loginListeners){
			loginListeners.remove(cl);
		}
	}

	public void delAllLoginListenerList() {
		synchronized (loginListeners){
			loginListeners.clear();
		}
	}

	// 重登陆监听器
	public void setReLoginListener(IReLoginListener cl) {
		synchronized(reLoginListeners){
			reLoginListeners.add(cl);
		}
	}

	public void notifiReLoginListener(UcsReason reason) {
		synchronized(reLoginListeners){
			for(int i = 0; i < reLoginListeners.size(); i++){
				reLoginListeners.get(i).onReLogin(reason);
			}
		}
	}

	public void delReLoginListenerList(IReLoginListener cl) {
		synchronized(reLoginListeners){
			reLoginListeners.remove(cl);
		}
	}

	public void delAllReLoginListenerList() {
		synchronized(reLoginListeners){
			reLoginListeners.clear();
		}
	}


	// tcp消息回调
	public void setTcpRecvListener(String listenerKey, ITcpRecvListener cl) {
		if (tcpMapListeners.containsKey(listenerKey)){
			CustomLog.d(listenerKey+"已经存在");
		}
		tcpMapListeners.put(listenerKey, cl);
	}
	
	public Set<String> getcpRecvListener(){
		if(tcpMapListeners != null){
			return tcpMapListeners.keySet();
		}
		return null;
	}
	public void notifiTcpRecvListener(String listenerKey, int cmd, byte[] buf) {
		if (tcpMapListeners.containsKey(listenerKey)) {
			tcpMapListeners.get(listenerKey).onRecvMessage(cmd, buf);
		}
	}

	public void delTcpRecvListenerList(String listenerKey,ITcpRecvListener cl) {
		if (tcpMapListeners.containsKey(listenerKey)){
			tcpMapListeners.remove(cl);
		}
		
	}

	public void delAllTcpRecvListenerList() {
		tcpMapListeners.clear();
	}
	
	//service destory listener
//	public void setServiceListener(IServiceListener listener){
//		if(listener != null){
//			serviceListener = listener;
//		}
//	}
//	public void notifiServiceDestory(){
//		if (serviceListener != null) {
//			serviceListener.onServiceDestory();
//		}
//	}
//	
//	public void notifiServiceStart() {
//		if (serviceListener != null) {
//			serviceListener.onServiceStart();
//		}
//	}
	//isdkstatusListener接口配置函数
	public void setISdkStatusListener(ISdkStatusListener listener) {
		synchronized(iSdkStatusListeners){
			iSdkStatusListeners.add(listener);
		}
	}

	public void notifySdkStatus(UcsReason reason) {
		synchronized(iSdkStatusListeners){
			for(int i = 0; i < iSdkStatusListeners.size(); i++){
				iSdkStatusListeners.get(i).onSdkStatus(reason);
			}
		}
	}

	public void delISdkStatusListener(ISdkStatusListener cl) {
		synchronized(iSdkStatusListeners){
			iSdkStatusListeners.remove(cl);
		}
	}

	public void delAllISdkStatusListener() {
		synchronized(iSdkStatusListeners){
			iSdkStatusListeners.clear();
		}
	}
	
	public void setPerviewImgTransListener(OnRecvPerviewImgTransListener perviewImgTransListener) {
		this.perviewImgTransListener = perviewImgTransListener;
	}
	
	public OnRecvPerviewImgTransListener getPerviewImgTransListener() {
		return perviewImgTransListener;
	}

}
