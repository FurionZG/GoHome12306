package com.gohome.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.gohome.bean.Train;
import com.gohome.bean.Users;
import com.google.common.collect.BiMap;

public class QueryUtils {

	private static Logger logger = Logger.getLogger(QueryUtils.class);

	/**
	 * 查询符合出发站、终点站、出发日期的列车信息的方法
	 * 
	 * @param u
	 *            用户信息
	 * @return
	 */
	public static Map<Integer, Train> queryTrainMessage(Users u, BiMap<String, String> stationCode) {
		CloseableHttpClient client = ConnectionUtils.getClient(20000);
		String URL = IOUtils.getPropertyValue("queryTrain");
		// 拼凑查询列车信息的URL
		URI queryURL = null;
		URIBuilder queryBulider = null;
		try {
			queryBulider = new URIBuilder("https://kyfw.12306.cn/otn/leftTicket/queryZ");
			queryBulider.setParameter("leftTicketDTO.train_date", u.getRideDate());
			queryBulider.setParameter("leftTicketDTO.from_station", stationCode.get(u.getDeparture()));
			queryBulider.setParameter("leftTicketDTO.to_station", stationCode.get(u.getTerminus()));
			queryBulider.setParameter("purpose_codes", "ADULT");
			queryURL = queryBulider.build();
		} catch (URISyntaxException e) {
			logger.error("构建URL失败，错误信息:[" + e.toString() + "]");
		}
		HttpGet query = ConnectionUtils.setReqHeader(new HttpGet(queryURL));
		//System.out.println(queryURL);
		CloseableHttpResponse response = null;
		Map<Integer, Train> trainMessage = null;
		try {
			response = client.execute(query);
			
			if (response.getStatusLine().getStatusCode() == 200) {
				trainMessage = FormatUtils.formatQueryMessage(EntityUtils.toString(response.getEntity(), "UTF-8"));
			} else {
				logger.error("查询列车信息失败，请检查输入条件...");
				System.exit(0);
			}
		} catch (Exception e) {
			logger.error("查询列车信息失败，错误信息:[" + e.toString() + "]");
			System.exit(0);
		}
		return trainMessage;
	}
}
