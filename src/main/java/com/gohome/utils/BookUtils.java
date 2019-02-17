package com.gohome.utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.CookieStore;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.gohome.bean.BookResult;
import com.gohome.bean.InitDC;
import com.gohome.bean.OrderMsg;
import com.gohome.bean.QueryTimeResult;
import com.gohome.bean.Train;
import com.gohome.bean.Users;
import com.google.common.collect.BiMap;

import net.sf.json.JSONNull;
import net.sf.json.JSONObject;

/**
 * 订票工具类
 */
public class BookUtils {

	private static Logger logger = Logger.getLogger(BookUtils.class);

	/**
	 * 检查是否登录
	 * 
	 * @param client
	 * @return
	 */

	public static BookResult checkUserStatus(BookResult bookResult) {
		logger.info("正在检查用户是否登录...");
		String URL = IOUtils.getPropertyValue("checkLoginStatus");

		HttpPost checkUserPost = new HttpPost(URL);
		Map<String, String> form = new HashMap<String, String>();
		form.put("_json_att", "");
		checkUserPost.setEntity(FormatUtils.setPostEntityFromMap(form));
		CloseableHttpResponse response = null;
		try {
			response = bookResult.getClient().execute(checkUserPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
				//System.out.println(result);
				JSONObject data = result.getJSONObject("data");
				if (result.getBoolean("status") && data.getBoolean("flag")) {
					bookResult.setCheckUser(true);

				}
				return bookResult;
			}
		} catch (Exception e) {
			logger.error("请求失败，错误信息:[" + e.toString() + "]");
		}
		return bookResult;
	}

	/**
	 * 提交订单请求 检查该用户是否有未完成订单，如果有则返回false
	 * 
	 * @param bookResult
	 * @return
	 */
	public static BookResult submitOrder(BookResult bookResult, Train chooseTrain, BiMap<String, String> stationCode) {
		logger.info("正在检查是否有未完成订单...");
		String URL = IOUtils.getPropertyValue("submitOrderRequestURL");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String back_train_date = sdf.format(new Date());
		HttpPost submitOrderPost = new HttpPost(URL);
		submitOrderPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		submitOrderPost.setHeader("Host", "kyfw.12306.cn");
		submitOrderPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		submitOrderPost.setHeader("X-Requested-With", "XMLHttpRequest");
		submitOrderPost.setHeader("Referer", "https://kyfw.12306.cn/otn/leftTicket/init");
		submitOrderPost.setHeader("Accept-Encoding", "gzip, deflate, br");

		Map<String, String> form = new HashMap<String, String>();
		try {
			form.put("secretStr", URLDecoder.decode(chooseTrain.getSecretStr(), "UTF-8"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		form.put("train_date", chooseTrain.getStartDate());
		form.put("back_train_date", back_train_date);
		form.put("tour_flag", "dc");
		form.put("purpose_codes", "ADULT");
		form.put("query_from_station_name", stationCode.inverse().get(chooseTrain.getFindFrom()));
		form.put("query_to_station_name", stationCode.inverse().get(chooseTrain.getFindTo()));
		form.put("undefined", "");
		submitOrderPost.setEntity(FormatUtils.setPostEntityFromMap(form));
		CloseableHttpResponse response = null;
		try {
			response = bookResult.getClient().execute(submitOrderPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
				//System.out.println(result);
				if (result.getBoolean("status")) {
					bookResult.setSubmitOrder(true);
					return bookResult;
				} else {
					logger.info(result.get("messages"));
					System.exit(0);
				}
			}
		} catch (Exception e) {
			logger.error("请求失败，错误信息:[" + e.toString() + "]");
		}
		return bookResult;

	}

	public static BookResult getInitDc(BookResult bookResult) {
		logger.info("正在请求InitDc...");
		String URL = IOUtils.getPropertyValue("initDcURL");
		HttpPost initDcPost = new HttpPost(URL);

		initDcPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		initDcPost.setHeader("Host", "kyfw.12306.cn");
		initDcPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		initDcPost.setHeader("X-Requested-With", "XMLHttpRequest");

		Map<String, String> form = new HashMap<>();
		form.put("_json_att", "");
		initDcPost.setEntity(FormatUtils.setPostEntityFromMap(form));
		CloseableHttpResponse response = null;
		try {
			response = bookResult.getClient().execute(initDcPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				// 获取globalRepeatSubmitToken 和 ticketInfoForPassengerForm
				// 这里说明一下，该请求会返回一个HTML页面，页面中的globalRepeatSubmitToken和 ticketInfoForPassengerForm
				// 是请求其他页面的必要参数
				String[] html = EntityUtils.toString(response.getEntity()).split("\n");
				for (String line : html) {
					if (line.contains("globalRepeatSubmitToken")) {
						bookResult.setGlobalRepeatSubmitToken(line.substring(line.indexOf("'") + 1, line.length() - 2));
						//System.out.println(bookResult.getGlobalRepeatSubmitToken());
					}
					if (line.contains("var ticketInfoForPassengerForm")) {
						String ticketInfo = line.substring(line.indexOf("{"), line.length() - 1);
						bookResult.setInitDcInfo(FormatUtils.formatInitDc(ticketInfo));
					}
				}
				bookResult.setInitDc(true);
				return bookResult;
			}
		} catch (Exception e) {
			logger.error("请求失败，错误信息:[" + e.toString() + "]");
		}
		return bookResult;
	}

	public static BookResult getPassenger(BookResult bookResult) {
		logger.info("正在请求乘客信息...");
		String URL = IOUtils.getPropertyValue("getPassenger");
		HttpPost getPassengerPost = new HttpPost(URL);

		getPassengerPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		getPassengerPost.setHeader("Host", "kyfw.12306.cn");
		getPassengerPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		getPassengerPost.setHeader("X-Requested-With", "XMLHttpRequest");

		Map<String, String> form = new HashMap<>();
		form.put("_json_att", "");
		form.put("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
		getPassengerPost.setEntity(FormatUtils.setPostEntityFromMap(form));

		CloseableHttpResponse response = null;
		try {
			response = bookResult.getClient().execute(getPassengerPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
				System.out.println(result);
				if (result.getBoolean("status") && result.getInt("httpstatus") == 200) {
					bookResult.setPassenger(true);
				}
				return bookResult;
			}
		} catch (Exception e) {
			logger.error("请求失败，错误信息:[" + e.toString() + "]");
		}
		return bookResult;
	}

	public static BookResult checkOrderInfo(BookResult bookResult, Users u) {
		logger.info("正在检查订单信息...");
		String URL = IOUtils.getPropertyValue("checkOrderInfo");

		HttpPost checkOrderInfoPost = new HttpPost(URL);
		checkOrderInfoPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		checkOrderInfoPost.setHeader("Host", "kyfw.12306.cn");
		checkOrderInfoPost.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		// 拼接passengerTicketStr
		String passengerTicketStr = StringUtils
				.join(new String[] { u.getSeatType(), "0", "1", u.getName(), "1", u.getId(), u.getTelNum(), "N" }, ",");
		// 拼接oldPassengerStr
		String oldPassengerStr = StringUtils.join(new String[] { u.getName(), "1", u.getId(), "1_" }, ",");
		// 准备表单数据
		bookResult.setPassengerTicketStr(passengerTicketStr);
		bookResult.setOldPassengerStr(oldPassengerStr);
		Map<String, String> form = new HashMap<>();
		//System.out.println(bookResult.getGlobalRepeatSubmitToken());
		form.put("cancel_flag", "2");
		form.put("bed_level_order_num", "000000000000000000000000000000");
		form.put("passengerTicketStr", passengerTicketStr);
		form.put("oldPassengerStr", oldPassengerStr);
		form.put("tour_flag", "dc");
		form.put("randCode", "");
		form.put("whatsSelect", "1");
		form.put("_json_att", "");
		form.put("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
		checkOrderInfoPost.setEntity(FormatUtils.setPostEntityFromMap(form));
		// 开始请求
		CloseableHttpResponse response = null;
		try {
			response = bookResult.getClient().execute(checkOrderInfoPost);
			if (response.getStatusLine().getStatusCode() == 200) {
				String responseText = EntityUtils.toString(response.getEntity());
				System.out.println(responseText);
				JSONObject result = JSONObject.fromObject(responseText);
				JSONObject data  =result.getJSONObject("data");
				boolean submitStatus=data.getBoolean("submitStatus");
				boolean status = result.getBoolean("status");
				if (status&&submitStatus) {
					bookResult.setCheckOrderInfo(true);
					return bookResult;
				} else {
					logger.error("订单信息错误...");
					System.exit(0);
				}

			}
		} catch (Exception e) {
			logger.error("请求失败，错误信息:[" + e.toString() + "]");
		}
		return bookResult;
	}

	/**
	 * 该方法为请求请求是否可以加入队列，但响应结果没有用，舍弃请求该页面
	 * 
	 * @param bookResult
	 * @return
	 */

	public static BookResult getQueueCount(BookResult bookResult) {
		// logger.info("正在进入队列...");
		// String URL = IOUtils.getPropertyValue("checkOrderInfo");
		// HttpPost queueCountPost = new HttpPost(URL);
		//
		// queueCountPost.setHeader("User-Agent",
		// "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko)
		// Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400
		// QQBrowser/10.3.2864.400");
		// queueCountPost.setHeader("Host", "kyfw.12306.cn");
		// queueCountPost.setHeader("Content-Type", "application/x-www-form-urlencoded;
		// charset=UTF-8");
		// queueCountPost.setHeader("X-Requested-With", "XMLHttpRequest");
		// String trainDateGMT = " Mon Feb 13 2019 00:00:00 GMT+0800";
		// // 创建请求数据
		// Map<String, String> form = new HashMap<>();
		// form.put("_json_att", "");
		// form.put("from_station_telecode",
		// bookResult.getInitDcInfo().getFromStationTelecode());
		// form.put("leftTicket", bookResult.getInitDcInfo().getLeftTicketStr());
		// form.put("purpose_codes", bookResult.getInitDcInfo().getPurposeCodes());
		// form.put("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
		// form.put("seatType", "O");
		// form.put("stationTrainCode",
		// bookResult.getInitDcInfo().getStationTrainCode());
		// form.put("toStationTelecode",
		// bookResult.getInitDcInfo().getToStationTelecode());
		// form.put("train_date", trainDateGMT);
		// form.put("train_location", bookResult.getInitDcInfo().getTrainLocation());
		// form.put("train_no", bookResult.getInitDcInfo().getTrainNo());
		// queueCountPost.setEntity(FormatUtils.setPostEntityFromMap(form));
		// CloseableHttpResponse response = null;
		//
		// try {
		// response = bookResult.getClient().execute(queueCountPost);
		// if (response.getStatusLine().getStatusCode() == 200) {
		// System.out.println(EntityUtils.toString(response.getEntity()));
		//
		// }
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return bookResult;
	}

	/**
	 * 该方法为请求进入购票队列
	 * 
	 * @param bookResult
	 * @return
	 */
	public static BookResult getConfirmSingleForQueue(BookResult bookResult) {
		logger.info("正在下单...");
		String URL = IOUtils.getPropertyValue("confirmSingleForQueueURL");
		HttpPost confirmSingleForQueueRequest = new HttpPost(URL);

		// 伪造请求头
		confirmSingleForQueueRequest.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		confirmSingleForQueueRequest.setHeader("Host", "kyfw.12306.cn");
		confirmSingleForQueueRequest.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		confirmSingleForQueueRequest.setHeader("X-Requested-With", "XMLHttpRequest");
		// 准备请求数据
		Map<String, String> form = new HashMap<>();

		form.put("_json_att", "");
		form.put("choose_seats", "");
		form.put("dwAll", "N");
		form.put("key_check_isChange", bookResult.getInitDcInfo().getKeyCheckIsChange());
		form.put("leftTicketStr", bookResult.getInitDcInfo().getLeftTicketStr());
		form.put("oldPassengerStr", bookResult.getOldPassengerStr());
		form.put("passengerTicketStr", bookResult.getPassengerTicketStr());
		form.put("purpose_codes", bookResult.getInitDcInfo().getPurposeCodes());
		form.put("randCode", "");
		form.put("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
		form.put("roomType", "00");
		form.put("seatDetailType", "000");
		form.put("train_location", bookResult.getInitDcInfo().getTrainLocation());
		form.put("whatsSelect", "1");
		confirmSingleForQueueRequest.setEntity(FormatUtils.setPostEntityFromMap(form));
		CloseableHttpResponse response = null;
		try {
			response = bookResult.getClient().execute(confirmSingleForQueueRequest);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
//				System.out.println(result);
				JSONObject data = result.getJSONObject("data");
				boolean status = result.getBoolean("status");
				boolean submitStatus = data.getBoolean("submitStatus");
				if (status && submitStatus) {
					bookResult.setConfirmSingleForQueue(true);
				}

			}
		} catch (Exception e) {
			logger.error("请求失败，错误信息:[" + e.toString() + "]");
		}
		return bookResult;
	}

	/**
	 * 该方法为请求排队时间
	 * 
	 * @param bookResult
	 * @return
	 */
	public static BookResult getQueryOrderTime(BookResult bookResult) {
		logger.info("正在查询排队时间...");
		String URL = IOUtils.getPropertyValue("queryOrderTime");
		HttpPost getQueryOrderTime = new HttpPost(URL);

		// 伪造请求头
		getQueryOrderTime.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		getQueryOrderTime.setHeader("Host", "kyfw.12306.cn");
		getQueryOrderTime.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		getQueryOrderTime.setHeader("X-Requested-With", "XMLHttpRequest");
		// 准备请求数据
		Map<String, String> form = new HashMap<>();
		form.put("_json_att", "");
		form.put("random", String.valueOf(System.currentTimeMillis()));
		form.put("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
		form.put("tourFlag", "dc");
		getQueryOrderTime.setEntity(FormatUtils.setPostEntityFromMap(form));
		QueryTimeResult queryTimeResult = new QueryTimeResult();
		while (!(queryTimeResult = getQueryOrderTimeMethod(getQueryOrderTime, bookResult)).isOK()) {
			try {
				Thread.sleep(4000);// 每4秒刷新一次排队信息
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		logger.info("订票成功,订单号：" + queryTimeResult.getOrderId());
		bookResult.setQueryOrderTime(true);
		bookResult.setQueryTimeResult(queryTimeResult);
		return bookResult;
	}

	/**
	 * 该方法为请求排队时间的方法体，因为要循环请求，所以单独拿出来作为一个方法
	 * 
	 * @param getQueryOrderTime
	 * @param bookResult
	 * @return
	 */
	public static QueryTimeResult getQueryOrderTimeMethod(HttpPost getQueryOrderTime, BookResult bookResult) {
		QueryTimeResult queryTimeResult = new QueryTimeResult();

		CloseableHttpResponse response = null;
		try {
			response = bookResult.getClient().execute(getQueryOrderTime);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
				//System.out.println(result);
				JSONObject data = result.getJSONObject("data");
				boolean status = result.getBoolean("status");
				boolean queryOrderWaitTimeStatus = data.getBoolean("queryOrderWaitTimeStatus");
				if (status && queryOrderWaitTimeStatus) {
					queryTimeResult.setWaitCount(data.getInt("waitCount"));
					queryTimeResult.setWaitTime(data.getInt("waitTime"));
					String msg = String.format("等待时间%d,等待人数%d", queryTimeResult.getWaitTime(),
							queryTimeResult.getWaitCount());
					logger.info(msg);
					// json处理null比较麻烦，需要将null转换成JSONObject比较
					// 这里json null比较还是有错
					Object o = data.get("orderId");
					if (o instanceof JSONNull) {
					} else {
						queryTimeResult.setOK(true);
						queryTimeResult.setOrderId(data.getString("orderId"));
						return queryTimeResult;
					}
					// 出现错误时提示错误信息
					if (data.containsKey("errorcode")) {
						logger.error(data.getString("msg"));
						System.exit(0);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			// logger.error("请求失败，错误信息:[" + e.toString() + "]");
		}
		return queryTimeResult;
	}

	/**
	 * 请求resultOrderForQueue页面，虽然该页面不会返回任何信息，但是如果不请求该页面，不能请求后面的订单信息页面
	 * 
	 * @param bookResult
	 * @return
	 */
	public static BookResult getResultOrderForQueue(BookResult bookResult) {
		String URL = IOUtils.getPropertyValue("resultOrderForQueue");
		HttpPost getResultOrderForQueue = new HttpPost(URL);
		// 伪造请求头
		getResultOrderForQueue.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		getResultOrderForQueue.setHeader("Host", "kyfw.12306.cn");
		getResultOrderForQueue.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		getResultOrderForQueue.setHeader("X-Requested-With", "XMLHttpRequest");
		// 准备请求数据
		Map<String, String> form = new HashMap<>();
		form.put("_json_att", "");
		form.put("orderSequence_no", bookResult.getQueryTimeResult().getOrderId());
		form.put("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
		getResultOrderForQueue.setEntity(FormatUtils.setPostEntityFromMap(form));
		HttpResponse response = null;
		try {
			response = bookResult.getClient().execute(getResultOrderForQueue);
			if (response.getStatusLine().getStatusCode() == 200) {
				JSONObject result = JSONObject.fromObject(EntityUtils.toString(response.getEntity()));
				//System.out.println(result);
				JSONObject data = result.getJSONObject("data");
				boolean status = result.getBoolean("status");
				boolean submitStatus = data.getBoolean("submitStatus");
				if (status && submitStatus) {
					bookResult.setResultOrderForQueue(true);
					return bookResult;
				}
			}
		} catch (Exception e) {
			logger.error("请求失败，错误信息:[" + e.toString() + "]");
		}
		return bookResult;
	}

	/**
	 * 获取订单信息
	 * 该方法中的请求地址URL需要拼接上当前时间的时间戳
	 * 
	 * @param bookResult
	 * @return
	 */
	public static BookResult getOrderMsg(BookResult bookResult) {
		String URL = IOUtils.getPropertyValue("bookResult")+String.valueOf(System.currentTimeMillis());
		HttpPost order = new HttpPost(URL);
		// 伪造请求头
		order.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.26 Safari/537.36 Core/1.63.6788.400 QQBrowser/10.3.2864.400");
		order.setHeader("Host", "kyfw.12306.cn");
		order.setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		// 准备请求数据
		Map<String, String> form = new HashMap<>();
		form.put("_json_att", "");
		form.put("REPEAT_SUBMIT_TOKEN", bookResult.getGlobalRepeatSubmitToken());
		order.setEntity(FormatUtils.setPostEntityFromMap(form));
		HttpResponse response = null;
		try {
			response=bookResult.getClient().execute(order);
			if(response.getStatusLine().getStatusCode()==200) {
				String context = EntityUtils.toString(response.getEntity());
				//System.out.println(context);
				String[] html=context.split("\n");
				String resultMap=null;
				for(String line:html) {
					if(line.contains("var passangerTicketList")){
						resultMap=line.substring(line.indexOf("["), line.length()-1);
	                }
				}
				OrderMsg msg = new OrderMsg();
				msg=FormatUtils.formatBookResult(resultMap);
				if(null!=msg) {
					bookResult.setFinish(true);
					bookResult.setOrderMsg(msg);
					return bookResult;
				}
			}
		} catch (Exception e) {
			logger.error("请求失败，错误信息:[" + e.toString() + "]");
		} 
		
		return bookResult;
	}

}
