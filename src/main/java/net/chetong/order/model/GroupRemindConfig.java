package net.chetong.order.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 机构金额监控配置VO对象
 * 
 * GroupRemindConfig
 * 
 * lijq
 * 2016年11月4日 下午4:59:32
 * 
 * @version 1.0.0
 *
 */
public class GroupRemindConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private BigDecimal availableMoney;  //用户余额
	
	private String loginName;  //登录名
	
	private String mobile; //手机号
	
	private String email; //邮箱
	
	private BigDecimal numberConfig; //金额数字
	
	private String orgname; //机构名称

	public BigDecimal getAvailableMoney() {
		return availableMoney;
	}

	public void setAvailableMoney(BigDecimal availableMoney) {
		this.availableMoney = availableMoney;
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public BigDecimal getNumberConfig() {
		return numberConfig;
	}

	public void setNumberConfig(BigDecimal numberConfig) {
		this.numberConfig = numberConfig;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
	

}
