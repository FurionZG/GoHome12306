package com.gohome.bean;

import java.util.Arrays;

/**
 * 因为InitDc中信息较多，单独开发一个类
 * 
 * @author 四爷
 *
 */
public class InitDC {
	private String trainDateTime = "";
	private String fromStationTelecode = "";
	private String leftTicketStr = "";
	private String purposeCodes = "";
	private String stationTrainCode = "";
	private String toStationTelecode = "";
	private String trainLocation = "";
	private String trainNo = "";
	private String[] leftDetails;
	private String keyCheckIsChange = "";
	
	@Override
	public String toString() {
		return "InitDC [trainDateTime=" + trainDateTime + ", fromStationTelecode=" + fromStationTelecode
				+ ", leftTicketStr=" + leftTicketStr + ", purposeCodes=" + purposeCodes + ", stationTrainCode="
				+ stationTrainCode + ", toStationTelecode=" + toStationTelecode + ", trainLocation=" + trainLocation
				+ ", trainNo=" + trainNo + ", leftDetails=" + Arrays.toString(leftDetails) + ", keyCheckIsChange="
				+ keyCheckIsChange + "]";
	}
	public String getTrainDateTime() {
		return trainDateTime;
	}
	public void setTrainDateTime(String trainDateTime) {
		this.trainDateTime = trainDateTime;
	}
	public String getFromStationTelecode() {
		return fromStationTelecode;
	}
	public void setFromStationTelecode(String fromStationTelecode) {
		this.fromStationTelecode = fromStationTelecode;
	}
	public String getLeftTicketStr() {
		return leftTicketStr;
	}
	public void setLeftTicketStr(String leftTicketStr) {
		this.leftTicketStr = leftTicketStr;
	}
	public String getPurposeCodes() {
		return purposeCodes;
	}
	public void setPurposeCodes(String purposeCodes) {
		this.purposeCodes = purposeCodes;
	}
	public String getStationTrainCode() {
		return stationTrainCode;
	}
	public void setStationTrainCode(String stationTrainCode) {
		this.stationTrainCode = stationTrainCode;
	}
	public String getToStationTelecode() {
		return toStationTelecode;
	}
	public void setToStationTelecode(String toStationTelecode) {
		this.toStationTelecode = toStationTelecode;
	}
	public String getTrainLocation() {
		return trainLocation;
	}
	public void setTrainLocation(String trainLocation) {
		this.trainLocation = trainLocation;
	}
	public String getTrainNo() {
		return trainNo;
	}
	public void setTrainNo(String trainNo) {
		this.trainNo = trainNo;
	}
	public String[] getLeftDetails() {
		return leftDetails;
	}
	public void setLeftDetails(String[] leftDetails) {
		this.leftDetails = leftDetails;
	}
	public String getKeyCheckIsChange() {
		return keyCheckIsChange;
	}
	public void setKeyCheckIsChange(String keyCheckIsChange) {
		this.keyCheckIsChange = keyCheckIsChange;
	}
}
