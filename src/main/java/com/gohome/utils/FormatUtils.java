package com.gohome.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import com.gohome.bean.InitDC;
import com.gohome.bean.OrderMsg;
import com.gohome.bean.Train;
import com.google.common.collect.BiMap;
import com.sun.xml.internal.bind.v2.runtime.output.Encoded;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * 格式化信息工具类
 *
 */
public class FormatUtils {
	private static Logger logger = Logger.getLogger(FormatUtils.class);

	/**
	 * 格式化输出列车信息
	 * 
	 * @param s
	 * @param stationCode
	 * @return
	 */
	public static Map<Integer, Train> formatQueryMessage(String s) {
		JSONObject jsonObject = JSONObject.fromObject(s);
		Map trainMessage = (Map) jsonObject.get("data");
		List<String> tmp = (List) trainMessage.get("result");
		Map<Integer, Train> newMessage = new HashMap();
		for (int i = 0; i < tmp.size(); i++) {
			String[] ss = tmp.get(i).split("\\|");
			Train t = new Train();
			t.setSecretStr(ss[0]);
			t.setTrainCode(ss[2]);
			t.setNum(ss[3]);
			t.setFrom(ss[4]);
			t.setTo(ss[5]);
			t.setFindFrom(ss[6]);
			t.setFindTo(ss[7]);
			t.setStartTime(ss[8]);
			t.setEndTime(ss[9]);
			t.setCostTime(ss[10]);
			t.setCanBuy(ss[11]);
			newMessage.put(i, t);
		}
		return newMessage;
	}
	/**
	 * 输出列车信息
	 * @param newMessage
	 * @param stationCode
	 */
	public static void printTrainMessage(Map<Integer, Train> newMessage,BiMap<String, String> stationCode) {
		System.out.print("序号\t");
		System.out.print("车次\t");
		System.out.print("始发站\t");
		System.out.print("终到站\t");
		System.out.print("查询始发站\t");
		System.out.print("查询终点站\t");
		System.out.print("出发时间\t");
		System.out.print("到站时间\t");
		System.out.print("历时\t");
		System.out.println("可否购票\t");
		for (int i = 0; i < newMessage.size(); i++) {
			System.out.print(i + "\t");
			System.out.print(newMessage.get(i).getNum() + "\t");
			System.out.print(stationCode.inverse().get(newMessage.get(i).getFrom()) + "\t");
			System.out.print(stationCode.inverse().get(newMessage.get(i).getTo()) + "\t");
			System.out.print(stationCode.inverse().get(newMessage.get(i).getFindFrom()) + "\t");
			System.out.print(stationCode.inverse().get(newMessage.get(i).getFindTo()) + "\t");
			System.out.print(newMessage.get(i).getStartTime() + "\t");
			System.out.print(newMessage.get(i).getEndTime() + "\t");
			System.out.print(newMessage.get(i).getCostTime() + "\t");
			System.out.print(newMessage.get(i).getCanBuy() + "\t");
			System.out.println();
		}
	}

	/**
	 * 获取验证码的Base64编码字符串
	 * 
	 * @param responseJSON
	 * @return
	 */
	public static String getVCodeBase64Str(String responseJSON) {
		String tmp = responseJSON.substring(responseJSON.indexOf("(") + 1, responseJSON.length() - 2);
		JSONObject jsonObject = JSONObject.fromObject(tmp);
		Map<String, String> results = (Map) jsonObject;
		if (results.get("result_code").equals("0") && results.get("result_message").equals("生成验证码成功")) {
			return results.get("image");
		}
		return null;
	}

	/**
	 * 将Map转化成Post表单实体
	 * 
	 * @param postDataMap
	 * @return
	 */
	public static UrlEncodedFormEntity setPostEntityFromMap(Map<String, String> postDataMap) {
		List<NameValuePair> postData = new LinkedList<>();
		for (Map.Entry<String, String> entry : postDataMap.entrySet()) {
			BasicNameValuePair param = new BasicNameValuePair(entry.getKey(), entry.getValue());
			postData.add(param);
		}
		try {
			return new UrlEncodedFormEntity(postData, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 格式化InitDc的方法
	 * 
	 * @param ticketInfoForPassengerForm
	 *            返回的html源码
	 * @return
	 */
	public static InitDC formatInitDc(String ticketInfoForPassengerForm) {
		InitDC initDC = new InitDC();

		ticketInfoForPassengerForm.replace("'", "\"");
		JSONObject info = JSONObject.fromObject(ticketInfoForPassengerForm);
		JSONObject orderRequestDTO = info.getJSONObject("orderRequestDTO");
		JSONObject TrainDate = orderRequestDTO.getJSONObject("train_date");
		JSONObject DTO = info.getJSONObject("queryLeftTicketRequestDTO");
		// System.out.println(ticketInfoForPassengerForm);
		initDC.setTrainDateTime((String.valueOf(TrainDate.getLong("time"))));
		initDC.setFromStationTelecode(orderRequestDTO.getString("from_station_telecode"));
		initDC.setLeftTicketStr(info.getString("leftTicketStr"));
		initDC.setPurposeCodes(info.getString("purpose_codes"));
		initDC.setStationTrainCode(orderRequestDTO.getString("station_train_code"));
		initDC.setToStationTelecode(orderRequestDTO.getString("to_station_telecode"));
		initDC.setTrainLocation(info.getString("train_location"));
		initDC.setTrainNo(DTO.getString("train_no"));
		// ["硬卧(62.50元)有票","硬座(16.50元)有票","软卧(96.50元)1张票","无座(16.50元)有票"]
		List<String> tmp = (List<String>) info.get("leftDetails");
		String[] leftDetails = tmp.toArray(new String[tmp.size()]);
		initDC.setLeftDetails(leftDetails);

		initDC.setKeyCheckIsChange(info.getString("key_check_isChange"));

		return initDC;
	}

	public static OrderMsg formatBookResult(String resultMap) {
		if (null != resultMap) {
			resultMap = resultMap.replace("'", "\"");
			JSONArray bookingTicketResultList = JSONArray.fromObject(resultMap);
			JSONObject data = bookingTicketResultList.getJSONObject(0);
			JSONObject passengerDTO = data.getJSONObject("passengerDTO");
			JSONObject stationTrainDTO = data.getJSONObject("stationTrainDTO");
			OrderMsg msg = new OrderMsg();
			msg.setCoachName(data.getString("coach_name"));
			msg.setSeatName(data.getString("seat_name"));
			msg.setSeatTypeName(data.getString("seat_type_name"));
			msg.setStartTrainDate(data.getString("start_train_date_page"));
			msg.setTicketPrice(data.getString("str_ticket_price_page"));
			msg.setTicketTypeName(data.getString("ticket_type_name"));
			msg.setSequenceNo(data.getString("sequence_no"));
			msg.setPassengerIdTypeName(passengerDTO.getString("passenger_id_type_name"));
			msg.setPassengerIdNo(passengerDTO.getString("passenger_id_no"));
			msg.setPassengerName(passengerDTO.getString("passenger_name"));
			msg.setFromStationName(stationTrainDTO.getString("from_station_name"));
			msg.setToStationName(stationTrainDTO.getString("to_station_name"));
			msg.setStationTrainCode(stationTrainDTO.getString("station_train_code"));
			logger.info("正在为您输出订单信息...");
			logger.info("订单号:" + msg.getSequenceNo());
			logger.info("乘车人:" + msg.getPassengerName());
			logger.info("证件号:" + msg.getPassengerIdNo());
			logger.info("出发站:" + msg.getFromStationName());
			logger.info("终点站:" + msg.getToStationName());
			logger.info("车次:" + msg.getStationTrainCode());
			logger.info("开车时间:" + msg.getStartTrainDate());
			logger.info("票价:" + msg.getTicketPrice());
			logger.info("席别:" + msg.getSeatTypeName());
			logger.info("座号:" + msg.getCoachName() + "车" + msg.getSeatName());
			return msg;
		} else {
			logger.info("订单信息查询失败，可能该订单已完成，请登录12306查看，若不存在未完成订单，请重启本系统");
			System.exit(0);
		}
		return null;
	}

}
