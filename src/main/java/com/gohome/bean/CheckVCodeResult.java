package com.gohome.bean;
/**
 * 此类为自动识别验证码的返回类型，包括绑定会话的Client客户端和返回的识别结果
 *
 */

import org.apache.http.impl.client.CloseableHttpClient;

public class CheckVCodeResult {
	private CloseableHttpClient client;
	private String checkVCode;

	public CloseableHttpClient getClient() {
		return client;
	}

	public void setClient(CloseableHttpClient client) {
		this.client = client;
	}

	public String getCheckVCode() {
		return checkVCode;
	}

	public void setCheckVCode(String checkVCode) {
		this.checkVCode = checkVCode;
	}

}

