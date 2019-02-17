package com.gohome.utils;

import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
/**
 * 连接工具类
 *
 */
public class ConnectionUtils {

	/**
	 * 获取HTTP会话实例
	 */
	public static CloseableHttpClient getClient(int timeOut) {
		Builder config = RequestConfig.custom();
		//配置响应时间
		config.setConnectTimeout(timeOut);
		config.setConnectionRequestTimeout(timeOut);
		config.setSocketTimeout(timeOut);
		RequestConfig requestConfig = config.build();
		//生成会话实例
		CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
		return client;
		
	}
	/**
	 * 伪造Get请求头的方法
	 */
	public static HttpGet setReqHeader(HttpGet get) {
		get.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		get.setHeader("Host", "kyfw.12306.cn");
		get.setHeader("X-Requested-With", "XMLHttpRequest");
		get.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
		return get;

	}
	/**
	 * 伪造Post请求头的方法
	 */
	public static HttpPost setReqHeader(HttpPost post) {
		post.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		post.setHeader("Host", "kyfw.12306.cn");
		post.setHeader("X-Requested-With", "XMLHttpRequest");
		post.setHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
		return post;
	}
}
