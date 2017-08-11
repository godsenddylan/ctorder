package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.Date;

public class FmOrderDeduct implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*-- id --*/
	private String orderNo;	/*-- 订单编号 --*/
	private BigDecimal totalDeductMoney;	/*-- 总扣款金额 --*/
	private BigDecimal ctDeductMoney;	/*-- 车童扣款金额 --*/
	private BigDecimal teamDeductMoney;	/*-- 团队扣款金额 --*/
	private Date createTime;	/*-- 创建时间 --*/
	private String ext1;	/*-- 扩展字段一 --*/
	private String ext2;	/*-- 扩展字段二 --*/
	private String ext3;	/*-- 扩展字段三 --*/
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOrderNo() {
		return orderNo;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public BigDecimal getTotalDeductMoney() {
		return totalDeductMoney;
	}
	public void setTotalDeductMoney(BigDecimal totalDeductMoney) {
		this.totalDeductMoney = totalDeductMoney;
	}
	public BigDecimal getCtDeductMoney() {
		return ctDeductMoney;
	}
	public void setCtDeductMoney(BigDecimal ctDeductMoney) {
		this.ctDeductMoney = ctDeductMoney;
	}
	public BigDecimal getTeamDeductMoney() {
		return teamDeductMoney;
	}
	public void setTeamDeductMoney(BigDecimal teamDeductMoney) {
		this.teamDeductMoney = teamDeductMoney;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public String getExt1() {
		return ext1;
	}
	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}
	public String getExt2() {
		return ext2;
	}
	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}
	public String getExt3() {
		return ext3;
	}
	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}
}
