package com.chetong.ctwechat.service;

import java.util.Map;

import com.chetong.aic.entity.ResultVO;


public interface SysSmsService {
	// 都不要加【车童网】.
	ResultVO<Object> sendTemplateSms(String mobile, String templateNo, Map<String, String> map);
	
	ResultVO<Object> sendSms(String mobile, String content);
	
	ResultVO<Object> sendSms(String mobile, String content, String smsType, String senderId);
	
	ResultVO<Object> sendEmaySms(String mobile, String content);

	ResultVO<Object> sendEmaySms(String mobile, String content, String smsType, String senderId);

	ResultVO<Object> sendTencentSms(String mobile, String content);

	ResultVO<Object> sendTencentSms(String mobile, String content, String smsType, String senderId);
}
