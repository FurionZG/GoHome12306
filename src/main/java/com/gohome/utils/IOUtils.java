package com.gohome.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Properties;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
/**
 * 流工具类
 *
 */
public class IOUtils {
	private static Logger logger = Logger.getLogger(IOUtils.class);

	/**
	 * 获取站点对应编码信息的方法
	 */
	public static BiMap<String,String> getStationCode() {
		CloseableHttpClient getStationCode = HttpClients.createDefault();
		HttpGet get = ConnectionUtils.setReqHeader(new HttpGet(getPropertyValue("getStationCode")));
		logger.info("正在请求站点编码...");
		CloseableHttpResponse response = null;
		BiMap<String, String> stationCode = HashBiMap.create();// Google提供的一一对应的Map
		try {
			response = getStationCode.execute(get);
			if (response.getStatusLine().getStatusCode() == 200) {
				logger.info("正在解析站点编码...");
				String tmp = EntityUtils.toString(response.getEntity(), "utf-8");
				// 解析js文件放入map
				String tmp1 = tmp.substring(tmp.indexOf("'") + 1, tmp.length() - 2);
				String[] CityStationCodes = tmp1.split("\\@");
				for (String ss : CityStationCodes) {
					String key = ss.substring(ss.indexOf("|") + 1, ss.indexOf("|", ss.indexOf("|") + 1) + 1)
							.replace("|", "");
					String value = ss.substring(ss.indexOf("|", ss.indexOf("|") + 1) + 1,
							ss.indexOf("|", ss.indexOf("|", ss.indexOf("|") + 1) + 1) + 1).replace("|", "");
					stationCode.put(key, value);
				}
				//System.out.println(stationCode);
				logger.info("站点编码解析成功...");
			} else {
				logger.error("站点信息请求失败...");
				return null;
			}
		} catch (Exception e) {
			logger.error("连接失败，错误信息:[" + e.toString() + "]");
		}

		return stationCode;

	}

	

	/**
	 * 读取URL配置文件
	 * 
	 */
	public static String getPropertyValue(String key) {
		Properties pro = new Properties();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("src/main/resources/URLconfig.properties"));
			pro.load(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String value = pro.getProperty(key);
		return value;
	}
	/**
	 * 读取用户配置文件
	 * @return
	 */
	public static Properties getUserConfig() {
		Properties pro = new Properties();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader("src/main/resources/User.properties"));
			pro.load(reader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return pro;
	}
}
