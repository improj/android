package com.yzxIM.data.db;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * 地图对象
 * 
 * @author zhuqian
 */
public class LocationMapMsg implements Parcelable{
	private String coordinate = "default"; // 坐标类型
	private double latitude = 116.404269; // 经度
	private double longitude = 39.915378; // 维度
	private String detailAddr = ""; // 详细地址
	private String thumbnailPath = ""; // 地图缩略图路径

	public LocationMapMsg() {

	}

	public LocationMapMsg(String coordinate,
			double latitude, double longitude, String detailAddr,
			String thumbnailPath) {
		this.coordinate = coordinate;
		this.latitude = latitude;
		this.longitude = longitude;
		this.detailAddr = detailAddr;
		this.thumbnailPath = thumbnailPath;
	}
	
	public LocationMapMsg(double latitude, double longitude, 
			String detailAddr, String thumbnailPath) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.detailAddr = detailAddr;
		this.thumbnailPath = thumbnailPath;
	}
	/**
	 * 获取坐标类型
	 * 
	 * @return
	 */
	public String getCoordinate() {
		return coordinate;
	}
	/**
	 * 设置坐标类型
	 * 
	 * @param coordinate
	 * @return
	 */
	public LocationMapMsg setCoordinate(String coordinate) {
		this.coordinate = coordinate;
		return this;
	}
	/**
	 * 获取经度
	 * @return
	 */
	public double getLatitude() {
		return latitude;
	}
	/**
	 * 设置经度
	 * 
	 * @param latitude
	 * @return
	 */
	public LocationMapMsg setLatitude(double latitude) {
		this.latitude = latitude;
		return this;
	}
	/**
	 * 获取纬度
	 * 
	 * @return
	 */
	public double getLongitude() {
		return longitude;
	}
	/**
	 * 设置纬度
	 * 
	 * @param longitude
	 * @return
	 */
	public LocationMapMsg setLongitude(double longitude) {
		this.longitude = longitude;
		return this;
	}
	/**
	 * 获取详细地址
	 * 
	 * @return
	 */
	public String getDetailAddr() {
		return detailAddr;
	}
	/**
	 * 设置详细地址
	 * 
	 * @param detailAddr
	 * @return
	 */
	public LocationMapMsg setDetailAddr(String detailAddr) {
		this.detailAddr = detailAddr;
		return this;
	}
	/**
	 * 获取地图截图路径
	 * 
	 * @return
	 */
	public String getThumbnailPath() {
		return thumbnailPath;
	}
	/**
	 * 设置地图截图路径
	 * 
	 * @param thumbnailPath
	 * @return
	 */
	public LocationMapMsg setThumbnailPath(String thumbnailPath) {
		this.thumbnailPath = thumbnailPath;
		return this;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(coordinate);
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);
		dest.writeString(detailAddr);
		dest.writeString(thumbnailPath);
	}
	
	public static final Creator<LocationMapMsg> CREATOR = new Creator<LocationMapMsg>() {
		@Override
		public LocationMapMsg[] newArray(int size) {
			return new LocationMapMsg[size];
		}

		@Override
		public LocationMapMsg createFromParcel(Parcel in) {
			return new LocationMapMsg(in);
		}
	};

	public LocationMapMsg(Parcel in) {
		coordinate = in.readString();
		latitude = in.readDouble();
		longitude = in.readDouble();
		detailAddr = in.readString();
		thumbnailPath = in.readString();
	}

}
