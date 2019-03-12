package com.yzxIM.data.db;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

import com.yzxIM.data.CategoryId;

public class DiscussionInfo implements Parcelable{

	private int _id;
	private String discussionId; //讨论组ID
	private String discussionName; //讨论组名字
	private int categoryId;     //聊天类型 1,单聊，2,群组，3,讨论组
	private int memberCount;    //讨论组成员数量
	private String ownerId;    //群组ID号
	private String discussionMembers; //包含成员id，名字，头像
	private String disscussionSettings; //讨论组设置信息，json
	private long createTime;   //讨论组创建时间
	
	public DiscussionInfo(){
		
	}
	
	public DiscussionInfo(String discussionId, String discussionName,
			CategoryId categoryId, int memberCount, String ownerId,
			String discussionMembers, String disscussionSettings, long createTime) {
		super();
		this.discussionId = discussionId;
		this.discussionName = discussionName;
		this.categoryId = categoryId.ordinal();
		this.memberCount = memberCount;
		this.ownerId = ownerId;
		this.discussionMembers = discussionMembers;
		this.disscussionSettings = disscussionSettings;
		this.createTime = createTime;
	}

	public String getDiscussionId() {
		return discussionId;
	}

	public void setDiscussionId(String discussionId) {
		this.discussionId = discussionId;
	}

	public String getDiscussionName() {
		return  discussionName;
	}

	
	public void setDiscussionName(String discussionName) {
		this.discussionName = discussionName;
	}
	
	public CategoryId getCategoryId() {
		return CategoryId.valueof(categoryId);
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	
	public void setCategoryId(CategoryId categoryId) {
		this.categoryId = categoryId.ordinal();
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public String getOwnerId() {
		return ownerId;
	}

	
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getDiscussionMembers() {
		return discussionMembers;
	}

	public void setDiscussionMembers(String discussionMembers) {
		this.discussionMembers = discussionMembers;
	}

	public String getDisscussionSettings() {
		return disscussionSettings;
	}

	public void setDisscussionSettings(String disscussionSettings) {
		this.disscussionSettings = disscussionSettings;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(discussionId);
		dest.writeString(discussionName);
		dest.writeInt(categoryId);
		dest.writeInt(memberCount);
		dest.writeString(ownerId);
		dest.writeString(discussionMembers);
		dest.writeString(disscussionSettings);
		dest.writeLong(createTime);
	}
	
	
	public static final Creator<DiscussionInfo> CREATOR = new Creator<DiscussionInfo>() {
		@Override
		public DiscussionInfo[] newArray(int size) {
			return new DiscussionInfo[size];
		}

		@Override
		public DiscussionInfo createFromParcel(Parcel in) {
			return new DiscussionInfo(in);
		}
	};

	public DiscussionInfo(Parcel in) {
		discussionId = in.readString();
		discussionName = in.readString();
		categoryId = in.readInt();
		memberCount = in.readInt();
		ownerId = in.readString();
		discussionMembers = in.readString();
		disscussionSettings = in.readString();
		createTime = in.readLong();
	}
	
}
