package com.yzxIM.data.db;


public class GroupInfo {
	private int _id;
	private String groupId; //群ID
	private String groupName; //群名字
	private int categoryId;  //分类 2群
	private int updateTime;  //创建时间
	
	public GroupInfo(String groupId, String groupName, int categoryId,
			int updateTime) {
		super();
		this.groupId = groupId;
		this.groupName = groupName;
		this.categoryId = categoryId;
		this.updateTime = updateTime;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(int updateTime) {
		this.updateTime = updateTime;
	}
	
	
}
