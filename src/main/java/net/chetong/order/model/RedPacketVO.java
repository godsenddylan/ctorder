package net.chetong.order.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

/**
 * app 车童红包记录
 * @author wfj
 *  2015-08-14
 */
@XmlRootElement
@JsonSerialize(include = Inclusion.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class RedPacketVO implements java.io.Serializable{
private static final long serialVersionUID = 1L;
	
	private Long id;//ID
	private Long userId;//获得红包的车童ID
	private String amount;//红包金额
	private Date getTime;//获取红包的时间
	private Long configId;//红包批次id
	private String isSuccess;//是否成功获得红包
	private String configType;
	private String att_url;
	private Long orderId;
	private String configBatch;
	private String configIsActive;
	
	
	public String getConfigType() {
		return configType;
	}
	public void setConfigType(String configType) {
		this.configType = configType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public Date getGetTime() {
		return getTime;
	}
	public void setGetTime(Date getTime) {
		this.getTime = getTime;
	}
	public Long getConfigId() {
		return configId;
	}
	public void setConfigId(Long configId) {
		this.configId = configId;
	}
	public String getIsSuccess() {
		return isSuccess;
	}
	public void setIsSuccess(String isSuccess) {
		this.isSuccess = isSuccess;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getAtt_url() {
		return att_url;
	}
	public void setAtt_url(String att_url) {
		this.att_url = att_url;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	public String getConfigBatch() {
		return configBatch;
	}
	public void setConfigBatch(String configBatch) {
		this.configBatch = configBatch;
	}
	public String getConfigIsActive() {
		return configIsActive;
	}
	public void setConfigIsActive(String configIsActive) {
		this.configIsActive = configIsActive;
	}
	
}
