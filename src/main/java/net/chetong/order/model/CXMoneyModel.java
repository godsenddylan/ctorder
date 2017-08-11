package net.chetong.order.model;

import java.math.BigDecimal;

/**
 * 车险费用信息
 */
public class CXMoneyModel {
	private String priceType;
	//买家支付金额总和
	private BigDecimal payMoney;
	//团队获取金额总和
	private BigDecimal groupMoney;
	//车童服务费总和
	private BigDecimal serviceMoney;
	
	//指导价基础费
	private BigDecimal guideBaseMoney;
	
	//基础费
	private BigDecimal baseMoney;
	//远程作业费
	private BigDecimal remoteMoney;
	//基础通道费
	private BigDecimal baseChannelMoney;
	//远程通道费
	private BigDecimal remoteChannelMoney;
	//基础开票费
	private BigDecimal baseInvoiceMoney;
	//远程开票费
	private BigDecimal remoteInvoiceMoney;
	
	//通道费
	private BigDecimal channelMoney;
	//开票费
	private BigDecimal invoiceMoney;
	// 基础费的通道费和开票费扣费
	private BigDecimal baseSubtract;
	// 远程作业费的通道费和开票费扣费
	private BigDecimal remoteSubtract;
	
	// 基础团队管理费
	private BigDecimal baseGroupManageMoney;
	// 远程团队管理费
	private BigDecimal remoteGroupManageMoney;
	
	// 风险基金
	private BigDecimal insuranceMoney;
	// 财务费
	private BigDecimal financeMoney;
	
	//调度费
	private BigDecimal dispatchMoney;
	
	public String getPriceType() {
		return priceType;
	}
	
	public void setPriceType(String priceType) {
		this.priceType = priceType;
	}
	
	public BigDecimal getPayMoney() {
		if("1".equals(priceType)){
			payMoney = this.getGuideBaseMoney();
		}else{
			payMoney = this.getBaseMoney().add(this.getBaseSubtract()).add(this.getRemoteMoney()).add(this.getRemoteSubtract());
		}
		return payMoney;
	}
	public void setPayMoney(BigDecimal payMoney) {
		this.payMoney = payMoney;
	}
	public BigDecimal getGroupMoney() {
		groupMoney = getBaseGroupManageMoney().add(getRemoteGroupManageMoney());
		return groupMoney;
	}
	public void setGroupMoney(BigDecimal groupMoney) {
		this.groupMoney = groupMoney;
	}
	public BigDecimal getServiceMoney() {
		serviceMoney = getBaseMoney().add(getRemoteMoney()).subtract(getInsuranceMoney()).subtract(getFinanceMoney())
				.subtract(getGroupMoney());
		return serviceMoney;
	}
	public void setServiceMoney(BigDecimal serviceMoney) {
		this.serviceMoney = serviceMoney;
	}
	public BigDecimal getGuideBaseMoney() {
		return guideBaseMoney;
	}
	public void setGuideBaseMoney(BigDecimal guideBaseMoney) {
		this.guideBaseMoney = guideBaseMoney;
	}
	public BigDecimal getBaseMoney() {
		if(baseMoney==null){
			baseMoney = BigDecimal.ZERO;
		}
		return baseMoney;
	}
	public void setBaseMoney(BigDecimal baseMoney) {
		this.baseMoney = baseMoney;
	}
	public BigDecimal getRemoteMoney() {
		if(remoteMoney==null){
			remoteMoney = BigDecimal.ZERO;
		}
		return remoteMoney;
	}
	public void setRemoteMoney(BigDecimal remoteMoney) {
		this.remoteMoney = remoteMoney;
	}
	public BigDecimal getBaseChannelMoney() {
		if(baseChannelMoney==null){
			baseChannelMoney = BigDecimal.ZERO;
		}
		return baseChannelMoney;
	}
	public void setBaseChannelMoney(BigDecimal baseChannelMoney) {
		this.baseChannelMoney = baseChannelMoney;
	}
	public BigDecimal getRemoteChannelMoney() {
		if(remoteChannelMoney==null){
			remoteChannelMoney = BigDecimal.ZERO;
		}
		return remoteChannelMoney;
	}
	public void setRemoteChannelMoney(BigDecimal remoteChannelMoney) {
		this.remoteChannelMoney = remoteChannelMoney;
	}
	public BigDecimal getBaseInvoiceMoney() {
		if(baseInvoiceMoney==null){
			baseInvoiceMoney = BigDecimal.ZERO;
		}
		return baseInvoiceMoney;
	}
	public void setBaseInvoiceMoney(BigDecimal baseInvoiceMoney) {
		this.baseInvoiceMoney = baseInvoiceMoney;
	}
	public BigDecimal getRemoteInvoiceMoney() {
		if(remoteInvoiceMoney==null){
			remoteInvoiceMoney = BigDecimal.ZERO;
		}
		return remoteInvoiceMoney;
	}
	public void setRemoteInvoiceMoney(BigDecimal remoteInvoiceMoney) {
		this.remoteInvoiceMoney = remoteInvoiceMoney;
	}
	public BigDecimal getChannelMoney() {
		channelMoney = baseChannelMoney.add(remoteChannelMoney);
		return channelMoney;
	}
	public void setChannelMoney(BigDecimal channelMoney) {
		this.channelMoney = channelMoney;
	}
	public BigDecimal getInvoiceMoney() {
		 invoiceMoney = baseInvoiceMoney.add(remoteInvoiceMoney);
		return invoiceMoney;
	}
	public void setInvoiceMoney(BigDecimal invoiceMoney) {
		this.invoiceMoney = invoiceMoney;
	}
	public BigDecimal getBaseSubtract() {
		baseSubtract  = getBaseChannelMoney().add(getBaseInvoiceMoney());
		return baseSubtract;
	}
	public void setBaseSubtract(BigDecimal baseSubtract) {
		this.baseSubtract = baseSubtract;
	}
	public BigDecimal getRemoteSubtract() {
		remoteSubtract = getRemoteChannelMoney().add(getRemoteInvoiceMoney());
		return remoteSubtract;
	}
	public void setRemoteSubtract(BigDecimal remoteSubtract) {
		this.remoteSubtract = remoteSubtract;
	}
	public BigDecimal getBaseGroupManageMoney() {
		if(baseGroupManageMoney==null){
			baseGroupManageMoney = BigDecimal.ZERO;
		}
		return baseGroupManageMoney;
	}
	public void setBaseGroupManageMoney(BigDecimal baseGroupManageMoney) {
		this.baseGroupManageMoney = baseGroupManageMoney;
	}
	public BigDecimal getRemoteGroupManageMoney() {
		if(remoteGroupManageMoney==null){
			remoteGroupManageMoney = BigDecimal.ZERO;
		}
		return remoteGroupManageMoney;
	}
	public void setRemoteGroupManageMoney(BigDecimal remoteGroupManageMoney) {
		this.remoteGroupManageMoney = remoteGroupManageMoney;
	}
	public BigDecimal getInsuranceMoney() {
		if(insuranceMoney==null){
			insuranceMoney = BigDecimal.ZERO;
		}
		return insuranceMoney;
	}
	public void setInsuranceMoney(BigDecimal insuranceMoney) {
		this.insuranceMoney = insuranceMoney;
	}
	public BigDecimal getFinanceMoney() {
		if(financeMoney==null){
			financeMoney = BigDecimal.ZERO;
		}
		return financeMoney;
	}
	public void setFinanceMoney(BigDecimal financeMoney) {
		this.financeMoney = financeMoney;
	}
	public BigDecimal getDispatchMoney() {
		if(dispatchMoney==null){
			dispatchMoney = BigDecimal.ZERO;
		}
		return dispatchMoney;
	}
	public void setDispatchMoney(BigDecimal dispatchMoney) {
		this.dispatchMoney = dispatchMoney;
	}
}
