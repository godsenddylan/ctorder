package net.chetong.order.model.orgimgpull;

import net.chetong.order.util.AppConst;

public class CaseImgsRequest {
	private String limitTime;//请求方调用接口的频率 单位：秒
	private String version;//此次请求的版本次
	private String lastTime;//请求请求数据的时间
	private String areaNo;
	private String isSpecialArea="0";

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getAreaNo() {
		return areaNo;
	}

	public void setAreaNo(String areaNo) {
		this.areaNo = areaNo;
	}

	public String getLimitTime() {
		return limitTime;
	}

	public void setLimitTime(String limitTime) {
		this.limitTime = limitTime;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
	
	public String getIsSpecialArea() {
		if(AppConst.specialAreas.contains(areaNo)){
			return "1";
		}
		return isSpecialArea;
	}

	public void setIsSpecialArea(String isSpecialArea) {
		this.isSpecialArea = isSpecialArea;
	}
}
