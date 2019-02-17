package com.gohome.query;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.chainsaw.Main;

import com.gohome.bean.Train;
import com.gohome.bean.Users;
import com.gohome.utils.ConnectionUtils;
import com.gohome.utils.FormatUtils;
import com.gohome.utils.IOUtils;
import com.gohome.utils.QueryUtils;
import com.google.common.collect.BiMap;

/**
 * 查询逻辑
 * 
 * @author 四爷
 *
 */
public class QueryTrain {
	// 日志实例
	private static Logger logger = Logger.getLogger(QueryTrain.class);
	// 查询计数
	private BigInteger queryConut = new BigInteger("0");
	// 会话实例
	private static CloseableHttpClient client = ConnectionUtils.getClient(20000);

	public static Map<Integer, Train> queryTrainMessage(Users u, BiMap<String, String> stationCode) {
		Map<Integer, Train> message=QueryUtils.queryTrainMessage(u, stationCode);
		return message;
	}

}
