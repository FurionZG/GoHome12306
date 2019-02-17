package com.gohome.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.gohome.bean.LoginResult;
import com.gohome.bean.Users;

import net.sf.json.JSONObject;

/**
 * 登录工具类
 *
 */
public class LoginUtils {
	private static Logger logger = Logger.getLogger(LoginUtils.class);

	/**
	 * 验证用户名密码是否正确
	 * 
	 * @param u
	 * @param checkCode
	 * @param client
	 * @return
	 */
	private CloseableHttpClient client ;
	
	public LoginUtils(CloseableHttpClient client) {
		super();
		this.client = client;
	}

	public   LoginResult checkUser(Users u, String checkCode, LoginResult loginResult) {
		// 准备URL
		logger.info("正在验证账号密码...");
		
		String URL = IOUtils.getPropertyValue("loginURL");
		HttpPost loginPost = new HttpPost(URL);
		// 伪造请求头
		loginPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		loginPost.setHeader("Host", "kyfw.12306.cn");
		// 设置Post表单实体
		Map<String, String> form = new HashMap<String, String>();
		form.put("username", u.getUsername());
		form.put("password", u.getPwd());
		form.put("appid", "otn");
		form.put("answer", checkCode);
		loginPost.setEntity(FormatUtils.setPostEntityFromMap(form));
		CloseableHttpResponse response = null;
		// 发送Post请求
		try {
			response = client.execute(loginPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
				//System.out.println(result);
				int result_code =result.getInt("result_code");
				if(result_code==0) {
					logger.info("账号密码正确...");
					loginResult.setCheckUser(true);
					loginResult.setClient(client);
					return loginResult;
				}
				if(result_code==1) {
					loginResult.setCheckUser(false);
					loginResult.setClient(client);
					return loginResult;
				}
				
			} else {
				logger.error("连接错误...");
			}

		} catch (Exception e) {
			e.printStackTrace();
			logger.error("网络错误，错误信息:[" + e.toString() + "]");
		}
		return loginResult;
	}

	/**
	 * 获取登录Token
	 * 
	 * @param client
	 * @return
	 */
	public  LoginResult getToken(LoginResult loginResult) {
		// 准备URL
		logger.info("正在获取Token...");
		
		String URL = IOUtils.getPropertyValue("getToken");
		HttpPost getTokentPost = new HttpPost(URL);
		// 伪造请求头
		getTokentPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		getTokentPost.setHeader("Host", "kyfw.12306.cn");
		// 设置Post表单实体
		Map<String, String> form = new HashMap<String, String>();
		form.put("appid", "otn");
		getTokentPost.setEntity(FormatUtils.setPostEntityFromMap(form));
		CloseableHttpResponse response = null;
		// 发送Post请求
		try {
			response = client.execute(getTokentPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				logger.info("获取Token成功...");
				Map<String,String> resultMap = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
				loginResult.setNewapptk(resultMap.get("newapptk"));
				loginResult.setGetToken(true);
				loginResult.setClient(client);
				return loginResult;
			}
		} catch (Exception e) {
			logger.error("网络错误，错误信息:[" + e.toString() + "]");
		}
		return loginResult;
	}
	/**
	 * 登录
	 * @param loginResult
	 * @return
	 */

	public  LoginResult checkToken(LoginResult loginResult) {
		logger.info("正在验证Token...");
		
		String URL = IOUtils.getPropertyValue("checkToken");
		HttpPost checkTokentPost = new HttpPost(URL);
		// 伪造请求头
		checkTokentPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		checkTokentPost.setHeader("Host", "kyfw.12306.cn");
		// 设置Post表单实体
		Map<String, String> form = new HashMap<String, String>();
		form.put("tk", loginResult.getNewapptk());
		checkTokentPost.setEntity(FormatUtils.setPostEntityFromMap(form));
		CloseableHttpResponse response = null;
		try {
			response=client.execute(checkTokentPost);
			//System.out.println(response.getStatusLine().getStatusCode());
			if(response.getStatusLine().getStatusCode()==200) {
				Map resultMap = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
				if((Integer)resultMap.get("result_code")==0) {
					loginResult.setCheckToken(true);
					loginResult.setApptk((String)resultMap.get("apptk"));
					loginResult.setUsername((String)resultMap.get("username"));
					loginResult.setClient(client);
					logger.info("登陆成功,用户名:"+loginResult.getUsername());
					return loginResult;
				}else {
					logger.error("登录错误，请稍后重试...");
					System.exit(0);
				}
			}
		} catch (Exception e) {
			logger.error("网络错误，错误信息:[" + e.toString() + "]");
		} 
		return loginResult;
	}
	
}
