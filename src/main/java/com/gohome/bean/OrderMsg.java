package com.gohome.bean;

/**
 * 该类为订票成功后生成的订单信息类
 * 
 * @author 四爷
 *
 */
public class OrderMsg {
	private String sequenceNo;
	private String passengerIdTypeName;
	private String passengerName;
	private String passengerIdNo;
	private String fromStationName;
	private String toStationName;
	private String stationTrainCode;
	private String startTrainDate;
	private String ticketPrice;
	private String ticketTypeName;
	private String coachName;
	private String seatName;
	private String seatTypeName;

	public String getSequenceNo() {
		return sequenceNo;
	}

	public void setSequenceNo(String sequenceNo) {
		this.sequenceNo = sequenceNo;
	}

	public String getPassengerIdTypeName() {
		return passengerIdTypeName;
	}

	public void setPassengerIdTypeName(String passengerIdTypeName) {
		this.passengerIdTypeName = passengerIdTypeName;
	}

	public String getPassengerName() {
		return passengerName;
	}

	public void setPassengerName(String passengerName) {
		this.passengerName = passengerName;
	}

	public String getPassengerIdNo() {
		return passengerIdNo;
	}

	public void setPassengerIdNo(String passengerIdNo) {
		this.passengerIdNo = passengerIdNo;
	}

	public String getFromStationName() {
		return fromStationName;
	}

	public void setFromStationName(String fromStationName) {
		this.fromStationName = fromStationName;
	}

	public String getToStationName() {
		return toStationName;
	}

	public void setToStationName(String toStationName) {
		this.toStationName = toStationName;
	}

	public String getStationTrainCode() {
		return stationTrainCode;
	}

	public void setStationTrainCode(String stationTrainCode) {
		this.stationTrainCode = stationTrainCode;
	}

	public String getStartTrainDate() {
		return startTrainDate;
	}

	public void setStartTrainDate(String startTrainDate) {
		this.startTrainDate = startTrainDate;
	}

	public String getTicketPrice() {
		return ticketPrice;
	}

	public void setTicketPrice(String ticketPrice) {
		this.ticketPrice = ticketPrice;
	}

	public String getTicketTypeName() {
		return ticketTypeName;
	}

	public void setTicketTypeName(String ticketTypeName) {
		this.ticketTypeName = ticketTypeName;
	}

	public String getCoachName() {
		return coachName;
	}

	public void setCoachName(String coachName) {
		this.coachName = coachName;
	}

	public String getSeatName() {
		return seatName;
	}

	public void setSeatName(String seatName) {
		this.seatName = seatName;
	}

	public String getSeatTypeName() {
		return seatTypeName;
	}

	public void setSeatTypeName(String seatTypeName) {
		this.seatTypeName = seatTypeName;
	}

	@Override
	public String toString() {
		return "OrderMsg [sequenceNo=" + sequenceNo + ", passengerIdTypeName=" + passengerIdTypeName
				+ ", passengerName=" + passengerName + ", passengerIdNo=" + passengerIdNo + ", fromStationName="
				+ fromStationName + ", toStationName=" + toStationName + ", stationTrainCode=" + stationTrainCode
				+ ", startTrainDate=" + startTrainDate + ", ticketPrice=" + ticketPrice + ", ticketTypeName="
				+ ticketTypeName + ", coachName=" + coachName + ", seatName=" + seatName + ", seatTypeName="
				+ seatTypeName + "]";
	}

}
