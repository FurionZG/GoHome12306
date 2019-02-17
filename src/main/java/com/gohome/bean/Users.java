package com.gohome.bean;

public class Users {
	private static Users user;

	public static Users getInstance() { // 单例生成用户
		if (user == null) {
			user = new Users();
		}
		return user;
	}

	private String username; // 12306用户名
	private String pwd; // 12306密码
	private String name; // 乘车人用户名
	private String id; // 乘车人身份证
	private String telNum; // 语音通知及接收短信手机号
	private String seatType; // 席别
	private String seatNum; // 座号，同12306，不一定可以选到希望的座位
	private String rideDate; // 乘车日期 格式为2017.01.01
	private String departure; // 查询始发站
	private String terminus; // 查询终点站
	private String startTime; // 最早乘车时间
	private String endTime; // 最晚乘车时间


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTelNum() {
		return telNum;
	}

	public void setTelNum(String telNum) {
		this.telNum = telNum;
	}

	public String getSeatType() {
		return seatType;
	}

	public void setSeatType(String seatType) {
		this.seatType = seatType;
	}

	public String getSeatNum() {
		return seatNum;
	}

	public void setSeatNum(String seatNum) {
		this.seatNum = seatNum;
	}

	public String getRideDate() {
		return rideDate;
	}

	public void setRideDate(String rideDate) {
		this.rideDate = rideDate;
	}

	public String getDeparture() {
		return departure;
	}

	public void setDeparture(String departure) {
		this.departure = departure;
	}

	public String getTerminus() {
		return terminus;
	}

	public void setTerminus(String terminus) {
		this.terminus = terminus;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}
