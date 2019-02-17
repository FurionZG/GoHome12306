package com.gohome.bean;

import org.apache.http.impl.client.CloseableHttpClient;

public class VCode {
	private String vcode;// 验证码
	private String timestamp;// 请求验证码时的时间戳
	private String callbackParameter;// 请求验证码时验证时间戳的回调参数
	private CloseableHttpClient client;

	public CloseableHttpClient getClient() {
		return client;
	}

	public void setClient(CloseableHttpClient client) {
		this.client = client;
	}

	public String getCallbackParameter() {
		return callbackParameter;
	}

	public void setCallbackParameter(String callbackParameter) {
		this.callbackParameter = callbackParameter;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
}
