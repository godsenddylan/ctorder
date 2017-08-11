package net.chetong.order.model;
import java.io.Serializable;


/**
 * 
 * 费用表
 * 
 **/
@SuppressWarnings("serial")
public class RsOrderCost implements Serializable {

	/****/
	private Long id;

	/**订单号**/
	private String orderNo;

	/**机构应付费用**/
	private java.math.BigDecimal payMoney;

	/**车童服务费**/
	private java.math.BigDecimal serviceMoney;

	/**团队服务费**/
	private java.math.BigDecimal groupMoney;

	/**通道费**/
	private java.math.BigDecimal channelMoney;

	/**创建人**/
	private Long creator;

	/**创建时间**/
	private String createTime;



	public void setId(Long id){
		this.id = id;
	}

	public Long getId(){
		return this.id;
	}

	public void setOrderNo(String orderNo){
		this.orderNo = orderNo;
	}

	public String getOrderNo(){
		return this.orderNo;
	}

	public void setPayMoney(java.math.BigDecimal payMoney){
		this.payMoney = payMoney;
	}

	public java.math.BigDecimal getPayMoney(){
		return this.payMoney;
	}

	public void setServiceMoney(java.math.BigDecimal serviceMoney){
		this.serviceMoney = serviceMoney;
	}

	public java.math.BigDecimal getServiceMoney(){
		return this.serviceMoney;
	}

	public void setGroupMoney(java.math.BigDecimal groupMoney){
		this.groupMoney = groupMoney;
	}

	public java.math.BigDecimal getGroupMoney(){
		return this.groupMoney;
	}

	public void setChannelMoney(java.math.BigDecimal channelMoney){
		this.channelMoney = channelMoney;
	}

	public java.math.BigDecimal getChannelMoney(){
		return this.channelMoney;
	}

	public void setCreator(Long creator){
		this.creator = creator;
	}

	public Long getCreator(){
		return this.creator;
	}

	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}

	public String getCreateTime(){
		return this.createTime;
	}

}
