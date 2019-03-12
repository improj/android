package com.yzxtcp.tcp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import android.text.TextUtils;
import com.yzxtcp.data.UserData;
import com.yzxtcp.tcp.identity.IConnectPlicy;
import com.yzxtcp.tcp.identity.impl.SingleConnectPlicy;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.TCPLog;
import com.yzxtcp.tools.tcp.packet.DataPacket;
import com.yzxtcp.tools.tcp.packet.IGGBaseRequest;
import com.yzxtcp.tools.tcp.packet.PackContent;


/**
 * tcp控制类
 * @author zhuqian
 *
 */
public class TCPManager {
	
	private static final int CONNECTED = 0;
	
	private static final int CONNECTING = 1;
	
	private static final int DISCONNECT = 2;
	
	private volatile int connectStatus = DISCONNECT;
	
	private TcpConnection tcpConnection;
	
	//必须配置的连接策略，如Proxy连接策略
	private IConnectPlicy connectPlicy;
	//单链接策略(默认必须有)
	private IConnectPlicy singleConnectPlicy;
	
	/**
	 * 设置连接策略
	 * @param connectPlicy
	 */
	public void setConnectPlicy(IConnectPlicy connectPlicy) {
		this.connectPlicy = connectPlicy;
	}

	private Map<String,ImageUploader> imgUploaders = new HashMap<String,ImageUploader>();
	public TCPManager(TcpConnection tcpConnection){
		this.tcpConnection = tcpConnection;
		this.singleConnectPlicy = new SingleConnectPlicy(this.tcpConnection);
	}
	public TcpConnection getTcpConnection() {
		return tcpConnection;
	}
	/**
	 * 连接
	 * @return
	 */
	private boolean connect(){
		if(connectStatus == CONNECTING){
			TCPLog.d("当前正在连接，不需要连接");
			return true;
		}
		connectStatus = CONNECTING;
		if (tcpConnection.isConnection()) {
			//如果已经连接，先断开
			TCPLog.d("tcp 已经连接成功，断开");
			tcpConnection.shutdown();
		}
		String ip = UserData.getCSAddress();
		/*if(StringUtils.isEmpty(ip)){
			ip = "im3.onccop.com:80";
		}*/
		if(TextUtils.isEmpty(ip)){
			//用户为配置IP使用CPS获取的IP
			if(this.connectPlicy == null){
				throw new RuntimeException("cps proxy... connectPlicy is null ?... ");
			}
			if(this.connectPlicy.connectPlicy(UserData.getPorxyIP())){
				connectStatus = CONNECTED;
				return true;
			}else{
				connectStatus = DISCONNECT;
				return false;
			}
		}else{
			//用户设置了CS地址，直接单链接策略
			if(singleConnectPlicy.connectPlicy(ip)){
				connectStatus = CONNECTED;
				return true;
			}else{
				connectStatus = DISCONNECT;
				return false;
			}
		}
	}
	
	/**
	 * @Description 重连和连接统一入口
	 * @return	true:成功；false：失败	
	 * @date 2016-5-25 上午9:49:56 
	 * @author xhb  
	 * @return boolean    返回类型
	 */
	public boolean reconnect(){
		if(connectStatus != CONNECTED){
			return connect();
		}else{
			TCPLog.d("当前已经连接成功，不需要重连...");
			return true;
		}
	}
	
	public void dissConnect(){
		if(connectStatus != DISCONNECT){
			tcpConnection.shutdown();
			connectStatus = DISCONNECT;
		}else{
			TCPLog.d("当前已经断开，不需要断开...");
		}
	}
	
	/**
	 * 发送数据包(发送心跳/协议版本/TCP认证)
	 * 
	 * @param packet
	 */
	public PackContent sendPacket(int cmd, IGGBaseRequest request) {
		//登录请求，不需要验证
		if (isConnect()) {
			final DataPacket dataPack = new DataPacket() {
			};
			PackContent content = new PackContent();
			content = (PackContent) request
					.packet(cmd, request, content, 0, "");
			if(content == null){
				return null;
			}
			dataPack.buf = content.pack_content;
			if (dataPack.buf != null) {
				if (TextUtils.isEmpty(content.tImgPackSize)) {
					tcpConnection.sendPacket(dataPack);
				} else {
					//IGGUploadMsgImgRequest
					try {
						//反射获取值
						Class clazz = request.getClass();
						Field pf = clazz.getField("pcClientMsgId");
						String iClientMsgId = (String) pf.get(request);
						TCPLog.d("TCPManager send image iClientMsgId = "+iClientMsgId);
						ImageUploader imageUploader = new ImageUploader(iClientMsgId,this, tcpConnection);
						imgUploaders.put(iClientMsgId, imageUploader);
						imageUploader.uploadImage(content);
					} catch (Exception e) {
						e.printStackTrace();
						TCPLog.d("gen pcClientMsgId error: "+e.getMessage());
					}
					
				}
			} else {
				CustomLog.e("content.pack_content is null");
			}
			return content;
		} else {
			TCPLog.d("当前已经断开，发送数据包失败");
			return null;
		}
	}
	
	/**
	 * @author zhangbin
	 * @2015-6-25
	 * @@param cmd
	 * @@param VoipBuf
	 * @descript:VOIP数据包发送 
	 */
	public boolean sendPacket(int cmd, byte[] voipBuf) {
		if (tcpConnection.isConnection()) {
			final DataPacket dataPack = new DataPacket() {
				
			};
			if(dataPack != null){
				dataPack.buf = new byte[voipBuf.length];
				System.arraycopy(voipBuf, 0, dataPack.buf, 0, voipBuf.length);
				tcpConnection.sendPacket(dataPack);
				return true;
			} 
		}else{
			// TODO 判断下当前网络，如果有网络进行重连
			TCPLog.d("sendPacket voip socket 断开!!!!");
		}
		return false;
	}
	public interface DisConnectCallBack{
		void onDisConnectResult();
	}

	public boolean isConnect() {
		return connectStatus == CONNECTED;
	}
	/**
	 * 返回指定消息id的图片上传对象
	 * @param iClientMsgId
	 * @return
	 */
	public ImageUploader getImageUploader(String iClientMsgId){
		if(imgUploaders.containsKey(iClientMsgId)){
			return imgUploaders.get(iClientMsgId);
		}
		return null;
	}
	
	/**
	 * 移除图片下载
	 * @param imageUploader
	 * @return
	 */
	public boolean removeImageUploader(String iClientMsgId){
		return imgUploaders.remove(iClientMsgId) != null;
	}
}
