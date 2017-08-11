package com.chetong.ctwechat.service;

public interface PushMessageService {
	Long savePushMsg4Wechat(Long userId, String orderNo, String orderType, String content, String createBy);
	
	Long sendPushMsg4Wechat(Long userId, String orderNo, String orderType, String content, String createBy);
}
