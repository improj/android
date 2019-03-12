package com.yzxIM.data.db;


public class VersionInfo {
	private int _id;
	private String databaseVersion;
	private String sdkVersion;
	private int createTime;
	
	public VersionInfo(String databaseVersion, String sdkVersion, int createTime) {
		super();
		this.databaseVersion = databaseVersion;
		this.sdkVersion = sdkVersion;
		this.createTime = createTime;
	}

	public String getDatabaseVersion() {
		return databaseVersion;
	}

	public void setDatabaseVersion(String databaseVersion) {
		this.databaseVersion = databaseVersion;
	}

	public String getSdkVersion() {
		return sdkVersion;
	}

	public void setSdkVersion(String sdkVersion) {
		this.sdkVersion = sdkVersion;
	}

	public int getCreateTime() {
		return createTime;
	}

	public void setCreateTime(int createTime) {
		this.createTime = createTime;
	}
	
	
}
