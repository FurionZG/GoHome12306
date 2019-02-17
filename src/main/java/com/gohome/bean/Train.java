package com.gohome.bean;

public class Train {
	private String secretStr;
	private String trainCode;
	private String num;
	private String from;
	private String to;
	private String findFrom;
	private String findTo;
	private String startTime;
	private String endTime;
	private String costTime;
	private String canBuy;
	private String startDate;
	

	

	@Override
	public String toString() {
		return "Train [secretStr=" + secretStr + ", trainCode=" + trainCode + ", num=" + num + ", from=" + from
				+ ", to=" + to + ", findFrom=" + findFrom + ", findTo=" + findTo + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", costTime=" + costTime + ", canBuy=" + canBuy + ", startDate=" + startDate
				+ "]";
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}

	public String getSecretStr() {
		return secretStr;
	}

	public void setSecretStr(String secretStr) {
		this.secretStr = secretStr;
	}

	public String getTrainCode() {
		return trainCode;
	}

	public void setTrainCode(String trainCode) {
		this.trainCode = trainCode;
	}

	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFindFrom() {
		return findFrom;
	}

	public void setFindFrom(String findFrom) {
		this.findFrom = findFrom;
	}

	public String getFindTo() {
		return findTo;
	}

	public void setFindTo(String findTo) {
		this.findTo = findTo;
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

	public String getCostTime() {
		return costTime;
	}

	public void setCostTime(String costTime) {
		this.costTime = costTime;
	}

	public String getCanBuy() {
		return canBuy;
	}

	public void setCanBuy(String canBuy) {
		this.canBuy = canBuy;
	}

	public Train() {
		super();
	}

}
