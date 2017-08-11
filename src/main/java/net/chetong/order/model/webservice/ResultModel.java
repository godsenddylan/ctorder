package net.chetong.order.model.webservice;

import java.util.List;

public class ResultModel {
	
	String rstCode = null;
	String rstMsg = null;
	List<YcImageVO> restObj = null;
	public String getRstCode() {
		return rstCode;
	}
	public void setRstCode(String rstCode) {
		this.rstCode = rstCode;
	}
	public String getRstMsg() {
		return rstMsg;
	}
	public void setRstMsg(String rstMsg) {
		this.rstMsg = rstMsg;
	}
	public List<YcImageVO> getRestObj() {
		return restObj;
	}
	public void setRestObj(List<YcImageVO> restObj) {
		this.restObj = restObj;
	}
	
}
