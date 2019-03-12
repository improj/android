package com.yzxtcp.tools.provider;

import java.util.HashMap;
import java.util.Map;
/**
 * 保存对应msdId的IPrivider
 * @author zhuqian
 */
public class ProviderHandler {
	private static final Map<String, IProvider> mProviders;
	static{
		mProviders = new HashMap<String, IProvider>();
	}
	/**
	 * 添加Provider
	 * @param msgId 消息id
	 * @param provider IProvider对象
	 */
	public static synchronized void addProvider(String msgId,IProvider provider){
		mProviders.put(msgId, provider);
	}
	/**
	 * 返回指定msgId的IProvider
	 * @param msgId 消息id
	 * @return IProvider对象
	 */
	public static synchronized  IProvider getProvider(String msgId){
		if(mProviders.containsKey(msgId)){
			return mProviders.get(msgId);
		}else{
			return null;
		}
	}
	
	/**
	 * 移除指定msgId的IProvider
	 * @param msgId 消息id
	 * @return IProvider对象
	 */
	public static synchronized  IProvider removeProvider(String msgId){
		if(mProviders.containsKey(msgId)){
			return mProviders.remove(msgId);
		}else{
			return null;
		}
	}
}
