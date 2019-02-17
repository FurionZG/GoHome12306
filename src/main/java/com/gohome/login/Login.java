package com.gohome.login;

import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.gohome.bean.CheckVCodeResult;
import com.gohome.bean.LoginResult;
import com.gohome.bean.Users;
import com.gohome.utils.LoginUtils;
import com.gohome.vcode.IdentifyVCode;

import net.sf.json.JSONObject;

/**
 * 登录逻辑
 *
 */
public class Login {
	// 日志实例
	private static Logger logger = Logger.getLogger(Login.class);
	// 登录返回结果实例
	private LoginResult loginResult = new LoginResult();
	private CloseableHttpClient client;

	public Login(CloseableHttpClient client) {
		super();
		this.client = client;
	}

	public LoginResult login(Users u) {
		long start = System.currentTimeMillis();
		CheckVCodeResult checkResult = new CheckVCodeResult();
		checkResult = IdentifyVCode.autoIdentifyVCode(client);
		client = checkResult.getClient();

		LoginUtils loginUtils = new LoginUtils(client);
		loginResult = loginUtils.checkUser(u, checkResult.getCheckVCode(), loginResult);
		if (loginResult.isCheckUser()) {
			loginResult = loginUtils.getToken(loginResult);
			if (loginResult.isGetToken()) {
				loginResult = loginUtils.checkToken(loginResult);
				if (loginResult.isCheckToken()) {
					loginResult.setClient(client);
					long end = System.currentTimeMillis();
					logger.info("登录逻辑已完成，耗时："+((end-start)/1000)+"s");
					return loginResult;
				}
			}
		
		} else {
			logger.error("登录失败，多次尝试会导致账号禁止登陆，请重新检查用户名和密码...");
			System.exit(0);
		}
		return null;
	}

}
