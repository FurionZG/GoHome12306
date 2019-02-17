package com.gohome.book;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.gohome.bean.BookResult;
import com.gohome.bean.CheckVCodeResult;
import com.gohome.bean.LoginResult;
import com.gohome.bean.Train;
import com.gohome.bean.Users;
import com.gohome.login.Login;
import com.gohome.utils.BookUtils;
import com.gohome.utils.ConnectionUtils;
import com.gohome.utils.IOUtils;
import com.gohome.utils.LoginUtils;
import com.gohome.utils.VCodeUtils;
import com.gohome.vcode.IdentifyVCode;
import com.google.common.collect.BiMap;

/**
 * 订票逻辑 该逻辑最为复杂，故将流程记录 1.该流程是在用户单击车次的“预订”按钮时触发的，获取预订确认页面，先要判断用户是否登录，
 * POST请求的地址是：https://kyfw.12306.cn/otn/login/checkUser，这个请求无参数。
 * 2.然后通过判断得到的JSON信息中的data.flag属性是否为true判断用户是否已登录。
 * 3.接着再根据对应列车查询时所获得的secretStr字符与用户输入的查询信息POST请求https://kyfw.12306.cn/otn/leftTicket/submitOrderRequest，判断用户是否可以访问预定确认画面，通过得到JSON信息的status属性判断是否允许访问，如果为true说明可以访问。
 * 4.最后依据旅行类型为单程（dc）POST跳转获取单程车票的预订确认画面：https://kyfw.12306.cn/otn/confirmPassenger/initDc。如果登录用户不进行上述判断，直接POST请求https://kyfw.12306.cn/otn/confirmPassenger/initDc提示非法请求，只有成功获取预订确认页面后才能进行下一步的操作。
 * 5.从请求订单的确认画面还可以得到获取当前登录用户常用联系人的链接地址为：https://kyfw.12306.cn/otn/confirmPassenger/getPassengerDTOs。
 */
public class Book {

	// 日志实例
	private static Logger logger = Logger.getLogger(Book.class);

	// 订票返回结果实例
	private static BookResult bookResult = new BookResult();
	// 站点编码
	private static BiMap<String, String> stationCode = IOUtils.getStationCode();
	private CloseableHttpClient client;

	public Book(CloseableHttpClient client) {
		super();
		this.client = client;
	}

	public BookResult bookTickets(Train chooseTrain, Users u) {
		logger.info("您选择的车次是" + chooseTrain.getNum());
		long start = System.currentTimeMillis();
		// 设置绑定会话的客户端
		bookResult.setClient(client);
		// 检查登录状态
		bookResult = BookUtils.checkUserStatus(bookResult);
		if (bookResult.isCheckUser()) {
			// 检查是否有未完成订单
			bookResult = BookUtils.submitOrder(bookResult, chooseTrain, stationCode);
		} else {
			logger.info("用户未登录...");
			System.exit(0);
		}
		if (bookResult.isSubmitOrder()) {
			// 从InitDc获取必要信息
			bookResult = BookUtils.getInitDc(bookResult);
			// System.out.println(bookResult.getInitDcInfo());
		} else {
			logger.info("有未完成订单...");
			System.exit(0);
		}
		if (bookResult.isInitDc()) {
			// 获取乘客信息
			bookResult = BookUtils.getPassenger(bookResult);
		} else {
			logger.info("InitDC请求失败...");
			System.exit(0);
		}
		if (bookResult.isPassenger()) {
			bookResult = BookUtils.checkOrderInfo(bookResult, u);
		} else {
			logger.info("乘客信息请求失败...");
			System.exit(0);
		}
		if (bookResult.isCheckOrderInfo()) {
			// bookResult=BookUtils.getQueueCount(bookResult);
			bookResult = BookUtils.getConfirmSingleForQueue(bookResult);
		} else {
			logger.info("订单信息检查错误...");
			System.exit(0);
		}
		if (bookResult.isConfirmSingleForQueue()) {
			bookResult = BookUtils.getQueryOrderTime(bookResult);
		} else {
			logger.info("下单失败...");
			System.exit(0);
		}
		if (bookResult.isQueryOrderTime()) {
			bookResult = BookUtils.getResultOrderForQueue(bookResult);
		} else {
			logger.info("查询排队时间失败...");
			System.exit(0);
		}
		if (bookResult.isResultOrderForQueue()) {
			bookResult = BookUtils.getOrderMsg(bookResult);
		}
		if (bookResult.isFinish()) {
			logger.info("恭喜您，订票成功，请在30分钟内登录12306完成支付！");
		}
		long end = System.currentTimeMillis();
		logger.info("订票逻辑已完成，耗时：" + ((end - start) / 1000) + "s");

		return bookResult;

	}

}
