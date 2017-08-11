package net.chetong.order.model;

import java.math.BigDecimal;
import java.util.Date;

public class HyWorkPriceConfig {
	private Long id;
	private Long orgUserId;
	private String provCode;
	private String cityCode;
	private String provDesc;
	private String cityDesc;
	private BigDecimal workPrice;
	private Long createBy;
	private Date createTime;
	private Long updateBy;
	private Date updateTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrgUserId() {
		return orgUserId;
	}

	public void setOrgUserId(Long orgUserId) {
		this.orgUserId = orgUserId;
	}

	public String getProvCode() {
		return provCode;
	}

	public void setProvCode(String provCode) {
		this.provCode = provCode == null ? null : provCode.trim();
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode == null ? null : cityCode.trim();
	}

	public String getProvDesc() {
		return provDesc;
	}

	public void setProvDesc(String provDesc) {
		this.provDesc = provDesc == null ? null : provDesc.trim();
	}

	public String getCityDesc() {
		return cityDesc;
	}

	public void setCityDesc(String cityDesc) {
		this.cityDesc = cityDesc == null ? null : cityDesc.trim();
	}

	public BigDecimal getWorkPrice() {
		return workPrice;
	}

	public void setWorkPrice(BigDecimal workPrice) {
		this.workPrice = workPrice;
	}

	public Long getCreateBy() {
		return createBy;
	}

	public void setCreateBy(Long createBy) {
		this.createBy = createBy;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Long getUpdateBy() {
		return updateBy;
	}

	public void setUpdateBy(Long updateBy) {
		this.updateBy = updateBy;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
}