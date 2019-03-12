package com.yzxIM.data.db;


public class UserInfo {
	private int _id;
	private String userId;  //用户ID
	private String userName; //用户名
	private int categoryId;  //用户分类 个人
	private String portrailUrl; //头像地址
	private int updateTime; //创建时间
	private String userSettings; //用户设置信息
	
	public UserInfo(String userId, String userName, int categoryId,
			String portrailUrl, int updateTime, String userSettings) {
		this.userId = userId;
		this.userName = userName;
		this.categoryId = categoryId;
		this.portrailUrl = portrailUrl;
		this.updateTime = updateTime;
		this.userSettings = userSettings;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getPortrailUrl() {
		return portrailUrl;
	}

	public void setPortrailUrl(String portrailUrl) {
		this.portrailUrl = portrailUrl;
	}

	public int getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(int updateTime) {
		this.updateTime = updateTime;
	}

	public String getUserSettings() {
		return userSettings;
	}

	public void setUserSettings(String userSettings) {
		this.userSettings = userSettings;
	}

	
	
}
