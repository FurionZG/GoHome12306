package com.gohome.main;

import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.junit.Test;

import com.gohome.bean.BookResult;
import com.gohome.bean.LoginResult;
import com.gohome.bean.Train;
import com.gohome.bean.Users;
import com.gohome.book.Book;
import com.gohome.call.Call;
import com.gohome.login.Login;
import com.gohome.query.QueryTrain;
import com.gohome.utils.ConnectionUtils;
import com.gohome.utils.FormatUtils;
import com.gohome.utils.IOUtils;
import com.google.common.collect.BiMap;

import net.sf.json.JSONObject;

public class Main {
	static {
		final Logger logger1 = Logger.getLogger(Main.class);
		logger1.info("欢迎使用GoHome自动订票工具V1.05");
		logger1.info("系统正在初始化...");

	}
	// 火车站编码实例
	private static BiMap<String, String> stationCode = IOUtils.getStationCode();
	// 日志实例
	private static Logger logger = Logger.getLogger(Main.class);
	// 绑定会话的客户端实例

	public static void main(String[] args) {
		logger.info("系统初始化成功...");
		long startAll = System.currentTimeMillis();
		Properties conf = IOUtils.getUserConfig();
		Users u = Users.getInstance();
		u.setTerminus(conf.getProperty("terminus"));
		u.setDeparture(conf.getProperty("departure"));
		u.setRideDate(conf.getProperty("rideDate"));
		u.setUsername(conf.getProperty("username"));
		u.setName(conf.getProperty("name"));
		u.setTelNum(conf.getProperty("telNum"));
		u.setSeatType(conf.getProperty("seatType"));
		u.setPwd(conf.getProperty("pwd"));
		u.setId(conf.getProperty("id"));
		logger.info("正在查询列车信息...");
		Map<Integer, Train> trainMessage = QueryTrain.queryTrainMessage(u, stationCode);
		logger.info("列车信息查询成功，正在解析...");
		FormatUtils.printTrainMessage(trainMessage,stationCode);
		logger.info("请选择您要订的车次序号：");
		Scanner sc = new Scanner(System.in);
		Integer num = sc.nextInt();
		Train chooseTrain = trainMessage.get(num);
		long queryCount =0;
		// 车次不开放售票，则重新选择
		while(chooseTrain.getCanBuy().equals("IS_TIME_NOT_BUY")) {
			logger.error("该车次暂不开放售票，请选择其他车次！");
			trainMessage = QueryTrain.queryTrainMessage(u, stationCode);
			logger.info("请选择您要订的车次序号：");
			num = sc.nextInt();
			chooseTrain = trainMessage.get(num);
		}
		//循环刷票
		if(chooseTrain.getCanBuy().equals("N")) {
			logger.info("该车暂无余票，正在重复查询");
		}
		while(chooseTrain.getCanBuy().equals("N")) {
			long start = System.currentTimeMillis();
			trainMessage=QueryTrain.queryTrainMessage(u, stationCode);
			for(Map.Entry<Integer, Train> entity:trainMessage.entrySet()) {
				if(chooseTrain.getNum().equals(entity.getValue().getNum())){
					chooseTrain=entity.getValue();
				}
			}
			long end = System.currentTimeMillis();
			queryCount++;
			logger.info("第"+queryCount+"次查询，耗时:"+(end-start)+"ms");
		}
		chooseTrain.setStartDate(u.getRideDate());
		CloseableHttpClient client = ConnectionUtils.getClient(15000);
		// 实现登录
		LoginResult loginResult = new Login(client).login(u);
		client = loginResult.getClient();
		// 实现订票
		BookResult bookResult = new Book(client).bookTickets(chooseTrain, u);
		Call call = new Call();
		String result = call.callMsg(u.getTelNum(), bookResult.getOrderMsg().getStationTrainCode());
		JSONObject msg = JSONObject.fromObject(result);
		if(msg.getString("return_code").equals("00000")) {
			logger.info("语音通知成功...");
		}else {
			logger.error("语音通知失败...");
		}
		long endAll=System.currentTimeMillis();
		logger.info("购票成功，系统自动关闭，总计用时："+((endAll-startAll)/1000)+"s，欢迎您再次使用！");
	}

}
