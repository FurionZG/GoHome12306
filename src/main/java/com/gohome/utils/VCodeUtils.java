package com.gohome.utils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.gohome.bean.VCode;
import com.gohome.vcode.IdentifyVCode;
import com.sun.javafx.fxml.builder.URLBuilder;

import net.sf.json.JSONObject;

/**
 * 验证码处理类
 *
 */
public class VCodeUtils {
	private static Logger logger = Logger.getLogger(VCodeUtils.class);

	
	/**
	 * 获取验证码的方法
	 */
	public static VCode getVCode(CloseableHttpClient client) {
		// 准备参数
		VCode vcode = new VCode();
		vcode.setTimestamp("" + System.currentTimeMillis());
		// 生成检查时间戳的回调参数 格式为jQuery1910XXXXXXXXXXXXXXXX
		Random r = new Random();
		StringBuffer cbpara = new StringBuffer("jQuery1910");
		for (int i = 0; i < 16; i++) {
			cbpara.append(r.nextInt(9));
		}
		vcode.setCallbackParameter(cbpara.toString() + "_" + vcode.getTimestamp());
		// 准备URL
		URI uri = null;
		try {
			URIBuilder urib = new URIBuilder("https://kyfw.12306.cn/passport/captcha/captcha-image64");
			urib.setParameter("login_site", "E");
			urib.setParameter("module", "login");
			urib.setParameter("rand", "sjrand");
			urib.setParameter(vcode.getTimestamp(), "");
			urib.setParameter("callback", vcode.getCallbackParameter());
			urib.setParameter("_", vcode.getTimestamp());
			uri = urib.build();
			// System.out.println(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		HttpGet getVCode = ConnectionUtils.setReqHeader(new HttpGet(uri));
		HttpResponse response = null;
		try {
			logger.info("正在请求验证码...");
			response = client.execute(getVCode);
			String VCodeBase64Str = FormatUtils.getVCodeBase64Str(EntityUtils.toString(response.getEntity(), "utf-8"));
			if (null != VCodeBase64Str) {
				vcode.setVcode(VCodeBase64Str);
				vcode.setClient(client);
				return vcode;
			} else {
				throw new Exception();
			}
		} catch (Exception e) {
			logger.error("请求失败，错误信息:[" + e.toString() + "]");
		}
		return null;
	}

	public static String markVCode(String vcode) {
		logger.info("正在识别验证码...");
		CloseableHttpClient client = ConnectionUtils.getClient(15000);
		// 创建Post请求数据
		String postURL = IOUtils.getPropertyValue("identifyVCode");
		HttpPost markVCode = new HttpPost(postURL);
		// 伪造请求头
		markVCode.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		markVCode.addHeader("Content-Type", "application/json;charset=UTF-8");
		// 设置请求实体
		JSONObject json = new JSONObject();
		json.put("base64", vcode);
		StringEntity postEntity = new StringEntity(json.toString(), "utf-8");
		postEntity.setContentEncoding("utf-8");
		markVCode.setEntity(postEntity);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(markVCode);
			if (response.getStatusLine().getStatusCode() == 200) {
				// 该网站需要发送验证码到http://60.205.200.159/api，返回的json字符串中有一个check码
				// 再将check码发送到http://check.huochepiao.360.cn/img_vcode 返回自动识别结果
				Map tmp = (Map) JSONObject.fromObject(EntityUtils.toString(response.getEntity(), "utf-8"));
				String check = (String) tmp.get("check");
				boolean success = (boolean) tmp.get("success");
				// 判断返回信息中的success是否为true
				if (success) {
					String checkURL = IOUtils.getPropertyValue("getIndentifyResult");
					// 创建Post请求数据
					HttpPost checkPost = new HttpPost(checkURL);
					// 伪造请求头
					checkPost.addHeader("User-Agent",
							"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
					checkPost.addHeader("Content-Type", "application/json;charset=UTF-8");
					JSONObject checkJson = new JSONObject();
					// 设置请求实体
					checkJson.put("img_buf", vcode);
					checkJson.put("type", "D");
					checkJson.put("logon", 1);
					checkJson.put("check", check);
					checkJson.put("=", "");
					StringEntity checkEntity = new StringEntity(checkJson.toString(), "utf-8");
					checkEntity.setContentEncoding("utf-8");
					checkPost.setEntity(checkEntity);
					// 向http://check.huochepiao.360.cn/img_vcode 发送Post请求
					response = client.execute(checkPost);
					if (response.getStatusLine().getStatusCode() == 200) {
						Map<String, String> resultMap = (Map) JSONObject
								.fromObject(EntityUtils.toString(response.getEntity()));
						String result = resultMap.get("res").replace("(", "").replace(")", "");
						return result;
					}
				}
			}
		} catch (Exception e) {
			logger.error("验证码识别失败，错误信息:[" + e.toString() + "]");
			// e.printStackTrace();
		}
		return null;
	}

	public static String markVCodeSpare(String vcode) {
		String URL  = IOUtils.getPropertyValue("spareIdentifyVCode");
		CloseableHttpClient client = ConnectionUtils.getClient(15000);
		// 创建Post请求数据
		HttpPost markVCode = new HttpPost(URL);
		// 伪造请求头
		markVCode.addHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		markVCode.addHeader("Content-Type", "application/json;charset=UTF-8");
		Map<String, String> form = new HashMap<String, String>();
		
		
		return null;
		
	}
	public static CloseableHttpClient checkVCode(VCode vcode, String result) {
		logger.info("正在校验验证码...");
		//System.out.println(result);
		String URL = IOUtils.getPropertyValue("checkVCode");
		URI checkURL = null;
		URIBuilder urib;
		CloseableHttpClient client = vcode.getClient();
		try {
			//构建校验验证码的URL
			urib = new URIBuilder(URL);
			urib.setParameter("callback", vcode.getCallbackParameter());
			urib.setParameter("answer", result);
			urib.setParameter("rand", "sjrand");
			urib.setParameter("login_site", "E");
			urib.setParameter("_", vcode.getTimestamp());
			checkURL = urib.build();
			//System.out.println(checkURL);
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		}
		HttpGet checkGet = ConnectionUtils.setReqHeader(new HttpGet(checkURL));
		CloseableHttpResponse response = null;
		try {
			//请求校验验证码
			
			response = client.execute(checkGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				String resultCode = EntityUtils.toString(response.getEntity());
				Map<String,String> resultMap = (Map)JSONObject.fromObject(resultCode.substring(resultCode.indexOf("(")+1, resultCode.length()-2));
				//返回JSON中result_code的值如果等于4，说明验证成功，则返回绑定Cookie的客户端实例
				//因为Cookie中有jsessionid的值，订票的时候需要用到
				if(resultMap.get("result_code").equals("4")) {
					logger.info("验证码验证成功...");
					return client;
				}else {
					logger.error("验证码校验失败，正在重新请求...");
					//因为第三方网站一个IP一秒钟只能请求一次，所以这里休眠1.5s
					Thread.sleep(1500);
					IdentifyVCode.autoIdentifyVCode(client);
				}
				
			}
		} catch (Exception e) {
			logger.error("校验码校验异常，错误信息:[" + e.toString() + "]");
			e.printStackTrace();
		}
		return client;

	}
}
