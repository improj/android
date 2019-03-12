package com.yzxIM.data.db;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.yzxIM.data.CategoryId;
import com.yzxIM.data.MSGTYPE;
import com.yzxIM.protocol.packet.PacketData.RequestCmd;
import com.yzxIM.tools.FileFilter;
import com.yzxtcp.tools.CustomLog;
import com.yzxtcp.tools.FileTools;
import com.yzxtcp.tools.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 消息类
 * 
 * @author zhuqian
 */
public class ChatMessage implements Parcelable {
	private int id;
	private String msgid; // 消息ID
	private String targetId; // 接收用户名
	private String senderId;// 发送方用户名
	private String nickName; // 发送方昵称
	private int categoryId;// 消息分类 1单聊，2群聊， 3，讨论组
	private boolean isFromMyself;// 消息方向 发送或者接收
	private long sendTime;// 发送时间
	private long receiveTime;// 接收时间
	private int msgType;// 消息类型 1，文本，2图片，3语音
	private String content = "";// 消息类容 图片为小图路径 语音为录音长度
	private int readStatus;// 消息是否已读状态
	private int sendStatus;// 消息发送状态 1发送中，2，发送成功，3，发送失败
	private String path; // 大图片 语音 视频 的存储路径
	private String parentID; // 会话ID(群组或讨论组 ID号 单聊用户ID)
	private String extmsg; // 扩展消息对象
	private String extmsg2; // 扩展消息对象暂未用

	/**
	 * 消息发送中
	 */
	public final static int MSG_STATUS_INPROCESS = 1; 
	/**
	 * 消息发送成功
	 */
	public final static int MSG_STATUS_SUCCESS = 2;
	/**
	 * 消息发送失败
	 */
	public final static int MSG_STATUS_FAIL = 3;
	/**
	 * 消息状态已读
	 */
	public final static int MSG_STATUS_READED = 4;
	/**
	 * 消息未读
	 */
	public final static int MSG_STATUS_UNREAD = 5; 
	/**
	 * 重发状态
	 */
	public final static int MSG_STATUS_RETRY = 6;
	/**
	 * 消息状态网络错误
	 */
	public final static int MSG_STATUS_NETERROR = 7;
	/**
	 * 发送消息超时
	 */
	public final static int MSG_STATUS_TIMEOUT = 8; 

	protected ChatMessage() {
		this.sendTime = System.currentTimeMillis();
		this.receiveTime = 0;
		this.readStatus = MSG_STATUS_READED;
		this.sendStatus = MSG_STATUS_INPROCESS;
	}

	protected ChatMessage(String msgid, String targetId, String senderId,
			CategoryId categoryId, boolean isFromMyself, long sendTime,
			long receiveTime, MSGTYPE msgType, String content, int readStatus,
			int sendStatus, String path, String parentID) {
		super();
		this.msgid = msgid;
		this.targetId = targetId;
		this.senderId = senderId;
		this.categoryId = categoryId.ordinal();
		this.isFromMyself = isFromMyself;
		this.sendTime = sendTime;
		this.receiveTime = receiveTime;
		this.msgType = msgType.ordinal();
		this.content = content;
		this.readStatus = readStatus;
		this.sendStatus = sendStatus;
		this.path = path;
		this.parentID = parentID;
	}
	/**
	 * 获取消息序列号
	 * @return
	 */
	public int getId() {
		return id;
	}
	/**
	 * 设置消息序列号
	 * @param id
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * 获取消息id
	 * @return
	 */
	public String getMsgid() {
		return msgid;
	}
	/**
	 * 设置消息id
	 * @param msgid
	 */
	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}
	/**
	 * 获取消息目标id
	 * @return
	 */
	public String getTargetId() {
		return targetId;
	}
	/**
	 * 设置消息目标id
	 * @param targetId
	 * @return
	 */
	public ChatMessage setTargetId(String targetId) {
		this.targetId = targetId;
		return this;
	}
	/**
	 * 获取发送方id
	 * @return
	 */
	public String getSenderId() {
		return senderId;
	}
	/**
	 * 设置放送方id
	 * @param senderId
	 * @return
	 */
	public ChatMessage setSenderId(String senderId) {
		this.senderId = senderId;
		return this;
	}
	/**
	 * 获取消息所属会话类型
	 * @return
	 */
	public CategoryId getCategoryId() {
		return CategoryId.valueof(categoryId);
	}
	/**
	 * 设置消息所属会话类型
	 * @param categoryId
	 * @return
	 */
	public ChatMessage setCategoryId(CategoryId categoryId) {
		this.categoryId = categoryId.ordinal();
		return this;
	}
	/**
	 * 设置消息所属会话类型
	 * @param categoryId
	 * @return
	 */
	public ChatMessage setCategoryId(int categoryId) {
		this.categoryId = categoryId;
		return this;
	}
	/**
	 * 设置消息是否自己发送
	 * @param isFromMyself
	 * @return
	 */
	public ChatMessage setIsFromMyself(boolean isFromMyself) {
		this.isFromMyself = isFromMyself;
		return this;
	}
	/**
	 * 获取消息是否自己发送
	 * @return
	 */
	public boolean getIsFromMyself() {
		return isFromMyself;
	}
	/**
	 * 设置消息是否自己发送
	 * @param isFromMyself
	 */
	public void setFromMyself(boolean isFromMyself) {
		this.isFromMyself = isFromMyself;
	}
	/**
	 * 获取发送时间
	 * @return
	 */
	public long getSendTime() {
		return sendTime;
	}
	/**
	 * 设置发送时间
	 * @param sendTime
	 * @return
	 */
	public ChatMessage setSendTime(long sendTime) {
		this.sendTime = sendTime;
		return this;
	}
	/**
	 * 获取消息接收时间
	 * @return
	 */
	public long getReceiveTime() {
		return receiveTime;
	}
	/**
	 * 设置消息接收时间
	 * @param l
	 * @return
	 */
	public ChatMessage setReceiveTime(long l) {
		this.receiveTime = l;
		return this;
	}
	/**
	 * 获取消息类型
	 * @return
	 */
	public MSGTYPE getMsgType() {
		return MSGTYPE.valueof(msgType);
	}
	/**
	 * 设置消息类型
	 * @param msgType
	 * @return
	 */
	public ChatMessage setMsgType(MSGTYPE msgType) {
		this.msgType = msgType.ordinal();
		return this;
	}
	/**
	 * 设置消息类型
	 * @param msgType
	 * @return
	 */
	public ChatMessage setMsgType(int msgType) {
		this.msgType = msgType;
		return this;
	}
	/**
	 * 获取消息内容
	 * @return
	 */
	public String getContent() {
		return content;
	}
	/**
	 * 设置消息内容
	 * @param content
	 * @return
	 */
	public ChatMessage setContent(String content) {
		this.content = content;
		return this;
	}
	/**
	 * 获取消息读取状态
	 * @return
	 */
	public int getReadStatus() {
		return readStatus;
	}
	/**
	 * 设置消息读取状态
	 * @param readStatus
	 * @return
	 */
	public ChatMessage setReadStatus(int readStatus) {
		this.readStatus = readStatus;
		return this;
	}
	/**
	 * 设置语言消息读取状态
	 * @param readStatus
	 * @return
	 */
	public ChatMessage setVoiceReadStatus(int readStatus) {
		this.readStatus = readStatus;
		DBManager.getInstance().updateMessageReadStatus(getMsgid(),
				getParentID(), getCategoryId(), readStatus);
		return this;
	}
	/**
	 * 获取消息发送状态
	 * @return
	 */
	public int getSendStatus() {
		return sendStatus;
	}
	/**
	 * 设置消息发送状态
	 * @param sendStatus
	 * @return
	 */
	public ChatMessage setSendStatus(int sendStatus) {
		this.sendStatus = sendStatus;
		return this;
	}
	/**
	 * 设置消息发送状态并更新到数据库
	 * @param sendStatus
	 * @return
	 */
	public ChatMessage setSendStatusTodb(int sendStatus) {
		this.sendStatus = sendStatus;
		DBManager.getInstance().updateMessageSendStatus(getMsgid(),
				getParentID(), getCategoryId(), sendStatus);
		return this;
	}
	/**
	 * 获取文件路径
	 * @return
	 */
	public String getPath() {
		return path;
	}
	/**
	 * 设置文件路径
	 * @param path
	 * @return
	 */
	public ChatMessage setPath(String path) {
		this.path = path;
		return this;
	}
	/**
	 * 获取所属会话的id
	 * @return
	 */
	public String getParentID() {
		return parentID;
	}
	/**
	 * 设置所属会话的id
	 * @param parentID
	 * @return
	 */
	public ChatMessage setParentID(String parentID) {
		this.parentID = parentID;
		return this;
	}
	/**
	 * 获取昵称
	 * @return
	 */
	public String getNickName() {
		return nickName;
	}

	/**
	 * 设置昵称
	 * @param nickName
	 * @return
	 */
	public ChatMessage setNickName(String nickName) {
		this.nickName = nickName;
		return this;
	}
	/**
	 * 设置地图对象
	 * @param map
	 * @return
	 */
	public ChatMessage setLocationMapMsg(LocationMapMsg map) {
		createMapJson(map);
		return this;
	}
	/**
	 * 设置地图对象
	 * @param coordinate
	 * @param latitude
	 * @param longitude
	 * @param detailAddr
	 * @param thumbnailPath
	 * @return
	 */
	public ChatMessage setLocationMapMsg(String coordinate, double latitude,
			double longitude, String detailAddr, String thumbnailPath) {
		LocationMapMsg map = new LocationMapMsg(coordinate, latitude,
				longitude, detailAddr, thumbnailPath);
		createMapJson(map);
		return this;
	}
	/**
	 * 设置地图对象
	 * @param latitude
	 * @param longitude
	 * @param detailAddr
	 * @param thumbnailPath
	 * @return
	 */
	public ChatMessage setLocationMapMsg(double latitude, double longitude,
			String detailAddr, String thumbnailPath) {
		LocationMapMsg map = new LocationMapMsg(latitude, longitude,
				detailAddr, thumbnailPath);
		createMapJson(map);
		return this;
	}
	/**
	 * 获取地图对象
	 * @return
	 */
	public LocationMapMsg getLocationMapMsg() {
		return getMapMsgByJson(extmsg);
	}
	/**
	 * 设置自定义消息对象
	 * @param msg
	 * @return
	 * @throws Exception
	 */
	public ChatMessage setCustomMsg(CustomMsg msg) throws Exception {
		if (msg == null) {
			throw new Exception("msg is null");
		}
		this.content = new String(msg.getContent(), 0, msg.getLen());
		return this;
	}
	/**
	 * 获取自定义消息对象
	 * @return
	 */
	public CustomMsg getCustomMsg() {
		CustomMsg msg = null;
		try {
			if (this.getMsgType() == MSGTYPE.MSG_DATA_CUSTOMMSG) {
				msg = new CustomMsg(content.getBytes(), content.length());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return msg;
	}

	public ChatMessage setExtMessage(String extMsg) {
		this.extmsg = extMsg;
		return this;
	}

	public String getExtMessage2() {
		return this.extmsg2;
	}

	public ChatMessage setExtMessage2(String extMsg2) {
		this.extmsg2 = extMsg2;
		return this;
	}

	public String getExtMessage() {
		return this.extmsg;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	private void createMapJson(LocationMapMsg mapMsg) {
		JSONObject map = new JSONObject();
		try {
			map.put("coordinate", mapMsg.getCoordinate());
			map.put("lat", mapMsg.getLatitude());
			map.put("lng", mapMsg.getLongitude());
			map.put("address", mapMsg.getDetailAddr());
			this.content = mapMsg.getThumbnailPath();
			this.extmsg = map.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private LocationMapMsg getMapMsgByJson(String extmsg) {
		LocationMapMsg mapMsg = new LocationMapMsg();

		if (this.getMsgType() != MSGTYPE.MSG_DATA_LOCALMAP) {
			return mapMsg;
		}

		try {
			JSONObject jObject = new JSONObject(extmsg);
			if (jObject.has("coordinate")) {
				mapMsg.setCoordinate(jObject.getString("coordinate"));
			}
			if (jObject.has("lat")) {
				mapMsg.setLatitude(jObject.getDouble("lat"));
			}
			if (jObject.has("lng")) {
				mapMsg.setLongitude(jObject.getDouble("lng"));
			}
			if (jObject.has("address")) {
				mapMsg.setDetailAddr(jObject.getString("address"));
			}

			mapMsg.setThumbnailPath(content);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return mapMsg;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(targetId);
		dest.writeInt(msgType);
		dest.writeString(content);
		dest.writeString(path);
		dest.writeString(extmsg);
	}

	public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
		@Override
		public ChatMessage[] newArray(int size) {
			return new ChatMessage[size];
		}

		@Override
		public ChatMessage createFromParcel(Parcel in) {
			return new ChatMessage(in);
		}
	};

	protected ChatMessage(Parcel in) {
		targetId = in.readString();
		msgType = in.readInt();
		content = in.readString();
		path = in.readString();
		extmsg = in.readString();
	}

	public void sendMessage(Handler handler) {
		Bundle data = new Bundle();
		Message msg = handler.obtainMessage(RequestCmd.REQ_SEND_MSG.ordinal());
		data.putParcelable("chatmsg", this);
		msg.setData(data);
		handler.sendMessage(msg);
	}

	/**
	 * 判断消息对象是否合法
	 * 
	 */
	public boolean isValidMessage() {
		if (StringUtils.isEmpty(this.targetId)||
				StringUtils.isEmpty(this.content)||
				this.msgType == 0||
				this.getMsgType() == MSGTYPE.MSG_DATA_NONE) {
			//以上为必须设置的参数
			return false;
			
		}else if(!isTargetValid(this.targetId)){//非法的TargetID
			CustomLog.e("targetId不合法");
			return false;
		}else if (this.getMsgType() == MSGTYPE.MSG_DATA_IMAGE ) {
			if(StringUtils.isEmpty(this.path)){
				CustomLog.e("发送图片路径null");
				return false;
			}
			FileFilter imageFilter = new FileFilter(this.path);
			if(!imageFilter.format(FileFilter.IMAGE_FORMAT)){
				CustomLog.e("format 发送图片格式不合法");
				return false;
			}
			//获取文件长度，单位为KB 大于20M不让发送
			long filesize = Long.parseLong(FileTools.getFileSize(this.path));
			if(filesize == 0 || filesize > 20*1024){ //文件不存在或大于20M
				CustomLog.e("发送图片过大");
				return false;
			}
		} else if (this.getMsgType() == MSGTYPE.MSG_DATA_VOICE) {
			if (StringUtils.isEmpty(this.path)) {
				CustomLog.e("发送语音路径null");
				return false;
			}else if(!TextUtils.isDigitsOnly(content)){
				return false;
			}
			FileFilter imageFilter = new FileFilter(this.path);
			if(!imageFilter.format(FileFilter.VOICE_FORMAT)){
				CustomLog.e("发送语音文件格式不合法");
				return false;
			}
		}else if(this.getMsgType() == MSGTYPE.MSG_DATA_LOCALMAP){
			LocationMapMsg lMapMsg = getLocationMapMsg();
			if(TextUtils.isEmpty(lMapMsg.getThumbnailPath())){
				return false;
			}else if(!FileTools.isPic(lMapMsg.getThumbnailPath())){
				CustomLog.e("发送地图文件格式不合法");
				return false;
			}
			long filesize = Long.parseLong(FileTools.
					getFileSize(lMapMsg.getThumbnailPath()));
			if(filesize == 0 || filesize > 60){ //文件不存在或大于60k
				CustomLog.e("发送地图文件过大");
				return false;
			}
		}

		return true;
	}
	/**
	 *判断targetId是否合法：纯数字和英文是正确的id，其它非法
	 *
	 */
	private boolean isTargetValid(String TargetID){
		Pattern pattern = Pattern.compile("^[A-Za-z0-9\\-]+$");
		Matcher matcher = pattern.matcher(TargetID);
		return matcher.matches();
	}
	/**
	 * 删除当前消息
	 * 
	 */
	public void deleteMessage(){
		List<ChatMessage> msgs = new ArrayList<ChatMessage>();
		msgs.add(this);
		 DBManager.getInstance().deleteMessages(
				 targetId, getCategoryId(), msgs);
	}
	
}
