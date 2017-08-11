package net.chetong.order.model;

import java.io.Serializable;

public class CtPromotionOneMoneyVO implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -390616298006580977L;
	
	private String created_by = null;  //创建人
	private String created_date = null; //创建日期
	private String updated_by = null; //更新人
	private String updated_date = null;//更新日期
	private String id = null;	//表ID
	private String login_name = null;		//委托人ID （ct_user.login_name）
	private String consignor = null;		//委托人ID （ct_group.org_name）
	private String order_type = null;  //订单类型：01=查勘，02=定损，03=物损，99=全部
	private String order_category = null; //订单种类: 01=省外订单 ，02=省内订单
	private String start_time = null;//体验时间段-开始时间
	private String end_time = null; //体验时间段-结束时间
	private String money = null; //体验金额
	private String order_number = null;//体验订单数量
	private String state = null; //状态:01=未启用，02=已启用
	public String getCreated_by() {
		return created_by;
	}
	public void setCreated_by(String created_by) {
		this.created_by = created_by;
	}
	public String getCreated_date() {
		return created_date;
	}
	public void setCreated_date(String created_date) {
		this.created_date = created_date;
	}
	public String getUpdated_by() {
		return updated_by;
	}
	public void setUpdated_by(String updated_by) {
		this.updated_by = updated_by;
	}
	public String getUpdated_date() {
		return updated_date;
	}
	public void setUpdated_date(String updated_date) {
		this.updated_date = updated_date;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLogin_name() {
		return login_name;
	}
	public void setLogin_name(String login_name) {
		this.login_name = login_name;
	}
	public String getOrder_type() {
		return order_type;
	}
	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}
	public String getOrder_category() {
		return order_category;
	}
	public void setOrder_category(String order_category) {
		this.order_category = order_category;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getMoney() {
		return money;
	}
	public void setMoney(String money) {
		this.money = money;
	}
	public String getOrder_number() {
		return order_number;
	}
	public void setOrder_number(String order_number) {
		this.order_number = order_number;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getConsignor() {
		return consignor;
	}
	public void setConsignor(String consignor) {
		this.consignor = consignor;
	}
	
}
