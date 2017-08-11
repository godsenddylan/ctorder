package net.chetong.order.model;

import java.io.Serializable;

public class CtPromotionOrderRelationVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5378344296456789022L;
	
	private String created_by = null;  //创建人
	private String created_date = null; //创建日期
	private String updated_by = null; //更新人
	private String updated_date = null;//更新日期
	private String id = null;	//表ID
	private String promotion_id = null;		//活动ID
	private String promotion_type = null;		//活动类型 ;01=一元体验
	private String order_id = null;  //订单id
	private String order_no= null; //订单号
	
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
	public String getPromotion_id() {
		return promotion_id;
	}
	public void setPromotion_id(String promotion_id) {
		this.promotion_id = promotion_id;
	}
	public String getPromotion_type() {
		return promotion_type;
	}
	public void setPromotion_type(String promotion_type) {
		this.promotion_type = promotion_type;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOrder_no() {
		return order_no;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	
	
}
