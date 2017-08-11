package net.chetong.order.model;

public class InsuranceType {
	
	private String serviceId;
	
	private String serviceName;
	
	private String auditStat; // 加盟服务审核状态 0 - 审核通过(已加盟 )   1 - 审核退回(未通过)     2待审核(认证中)  3 加盟退出

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getAuditStat() {
		return auditStat;
	}

	public void setAuditStat(String auditStat) {
		this.auditStat = auditStat;
	}


}
