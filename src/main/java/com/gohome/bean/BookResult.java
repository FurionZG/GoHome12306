package com.gohome.bean;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

public class BookResult {
	private CloseableHttpClient client;
	private boolean checkUser;
	private boolean submitOrder;
	private String globalRepeatSubmitToken;
	private InitDC initDcInfo;
	private boolean initDc;
	private boolean Passenger;
	private boolean checkOrderInfo;
	private String passengerTicketStr;
	private String oldPassengerStr;
	private boolean confirmSingleForQueue;
	private boolean queryOrderTime;
	private QueryTimeResult queryTimeResult;
	private boolean resultOrderForQueue;
	private boolean formatBookResult;

	private OrderMsg orderMsg;

	public OrderMsg getOrderMsg() {
		return orderMsg;
	}

	public void setOrderMsg(OrderMsg orderMsg) {
		this.orderMsg = orderMsg;
	}

	private boolean finish;

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

	public boolean isConfirmSingleForQueue() {
		return confirmSingleForQueue;
	}

	public void setConfirmSingleForQueue(boolean confirmSingleForQueue) {
		this.confirmSingleForQueue = confirmSingleForQueue;
	}

	public boolean isQueryOrderTime() {
		return queryOrderTime;
	}

	public void setQueryOrderTime(boolean queryOrderTime) {
		this.queryOrderTime = queryOrderTime;
	}

	public boolean isResultOrderForQueue() {
		return resultOrderForQueue;
	}

	public void setResultOrderForQueue(boolean resultOrderForQueue) {
		this.resultOrderForQueue = resultOrderForQueue;
	}

	public boolean isFormatBookResult() {
		return formatBookResult;
	}

	public void setFormatBookResult(boolean formatBookResult) {
		this.formatBookResult = formatBookResult;
	}

	public QueryTimeResult getQueryTimeResult() {
		return queryTimeResult;
	}

	public void setQueryTimeResult(QueryTimeResult queryTimeResult) {
		this.queryTimeResult = queryTimeResult;
	}

	public String getPassengerTicketStr() {
		return passengerTicketStr;
	}

	public void setPassengerTicketStr(String passengerTicketStr) {
		this.passengerTicketStr = passengerTicketStr;
	}

	public String getOldPassengerStr() {
		return oldPassengerStr;
	}

	public void setOldPassengerStr(String oldPassengerStr) {
		this.oldPassengerStr = oldPassengerStr;
	}

	public boolean isCheckOrderInfo() {
		return checkOrderInfo;
	}

	public void setCheckOrderInfo(boolean checkOrderInfo) {
		this.checkOrderInfo = checkOrderInfo;
	}

	public boolean isPassenger() {
		return Passenger;
	}

	public void setPassenger(boolean passenger) {
		Passenger = passenger;
	}

	public boolean isInitDc() {
		return initDc;
	}

	public void setInitDc(boolean initDc) {
		this.initDc = initDc;
	}

	public String getGlobalRepeatSubmitToken() {
		return globalRepeatSubmitToken;
	}

	public void setGlobalRepeatSubmitToken(String globalRepeatSubmitToken) {
		this.globalRepeatSubmitToken = globalRepeatSubmitToken;
	}

	public InitDC getInitDcInfo() {
		return initDcInfo;
	}

	public void setInitDcInfo(InitDC initDcInfo) {
		this.initDcInfo = initDcInfo;
	}

	public boolean isSubmitOrder() {
		return submitOrder;
	}

	public void setSubmitOrder(boolean submitOrder) {
		this.submitOrder = submitOrder;
	}

	public CloseableHttpClient getClient() {
		return client;
	}

	public void setClient(CloseableHttpClient client) {
		this.client = client;
	}

	public boolean isCheckUser() {
		return checkUser;
	}

	public void setCheckUser(boolean checkUser) {
		this.checkUser = checkUser;
	}

}
