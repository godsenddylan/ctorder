package net.chetong.order.model.form;

import java.util.List;

import net.chetong.order.model.HyOrderCaseVO;
import net.chetong.order.model.HyOrderTaskHaulwayVO;
import net.chetong.order.model.HyOrderTaskVO;

/**
 * 报案对象
 * @author wufj@chetong.net
 *         2015年12月30日 下午3:44:17
 */
public class ReportModel {
	
	private String loginUserId;//当前登录人id
	private String isAfresh;
	private HyOrderCaseVO hyOrderCaseVO;  //案件信息
	private HyOrderTaskVO hyOrderTaskVO;  //任务信息
	private List<HyOrderTaskHaulwayVO> taskHaulwayList; //任务中的运输路线信息
	
	public String getLoginUserId() {
		return loginUserId;
	}
	public void setLoginUserId(String loginUserId) {
		this.loginUserId = loginUserId;
	}
	public String getIsAfresh() {
		return isAfresh;
	}
	public void setIsAfresh(String isAfresh) {
		this.isAfresh = isAfresh;
	}
	public HyOrderCaseVO getHyOrderCaseVO() {
		return hyOrderCaseVO;
	}
	public void setHyOrderCaseVO(HyOrderCaseVO hyOrderCaseVO) {
		this.hyOrderCaseVO = hyOrderCaseVO;
	}
	public HyOrderTaskVO getHyOrderTaskVO() {
		return hyOrderTaskVO;
	}
	public List<HyOrderTaskHaulwayVO> getTaskHaulwayList() {
		return taskHaulwayList;
	}
	public void setTaskHaulwayList(List<HyOrderTaskHaulwayVO> taskHaulwayList) {
		this.taskHaulwayList = taskHaulwayList;
	}
	public void setHyOrderTaskVO(HyOrderTaskVO hyOrderTaskVO) {
		this.hyOrderTaskVO = hyOrderTaskVO;
	}
}
