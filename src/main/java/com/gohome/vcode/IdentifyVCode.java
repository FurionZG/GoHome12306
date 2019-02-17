package com.gohome.vcode;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;

import com.gohome.bean.CheckVCodeResult;
import com.gohome.bean.VCode;
import com.gohome.utils.VCodeUtils;
/**
 * 获取并自动识别验证码逻辑
 * @author 四爷
 *
 */
public class IdentifyVCode {
	private static Logger logger = Logger.getLogger(IdentifyVCode.class);
 
	public static CheckVCodeResult autoIdentifyVCode(CloseableHttpClient client) {
		VCode vcode = VCodeUtils.getVCode(client);
		String VCodeStrBase64 = vcode.getVcode();
		if (null == VCodeStrBase64) {
			logger.error("验证码获取失败，正在重新获取验证码...");
			autoIdentifyVCode(client);
		}
		String result = VCodeUtils.markVCode(VCodeStrBase64);
		if (null == result) {
			logger.error("验证码识别失败，正在重新获取验证码...");
			autoIdentifyVCode(client);
		}
		client = VCodeUtils.checkVCode(vcode,result);
		CheckVCodeResult checkResult = new CheckVCodeResult();
		checkResult.setCheckVCode(result);
		checkResult.setClient(client);
		return checkResult;
	}
}
