package net.chetong.order.model;
import java.io.Serializable;


/**
 * 
 * 人伤订单表
 * 
 **/
@SuppressWarnings("serial")
public class RsOrder implements Serializable {

	/**订单id**/
	private Long id;

	/**订单号**/
	private String orderNo;

	/**任务明细id**/
	private Long taskDetailId;

	/**订单状态**/
	private String dealStat;

	/**是否重派:0:否，1：是**/
	private String isResend;

	/**服务类型**/
	private Integer serviceId;

	/**服务类别**/
	private Integer subjectId;

	/**报案号**/
	private String caseNo;

	/**买家id**/
	private Long buyerUserId;

	/**买家名称**/
	private String buyerUserName;

	/**买家类型**/
	private String buyerUserType;

	/**是否委托派单**/
	private String isEntrustBy;

	/**派单人id**/
	private Long sendUserId;

	/**派单人名称**/
	private String sendUserName;

	/**派单时间**/
	private java.util.Date sendTime;

	/**是否代支付**/
	private String isPayBy;

	/**支付人id**/
	private Long payUserId;

	/**支付人名称**/
	private String payUserName;

	/**终审人id**/
	private Long auditUserId;

	/**终审人名称**/
	private String auditUserName;

	/**终审时间**/
	private java.util.Date auditTime;

	/**团队id**/
	private Long groupUserId;

	/**团队名称**/
	private String groupUserName;

	/**卖家id**/
	private Long sellerUserId;

	/**卖家名称**/
	private String sellerUserName;

	/**卖家类型**/
	private String sellerUserType;

	/**作业里程**/
	private Float mileage;

	/**作业地址**/
	private String workAddress;

	/**作业地址经度**/
	private Float longitude;

	/**作业地址维度**/
	private Float latitude;
	/**作业地址省编码**/
	private String provCode;
	/**作业地址市编码**/
	private String cityCode;
	/**作业地址区编码**/
	private String areaCode;

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

	public void setTaskDetailId(Long taskDetailId){
		this.taskDetailId = taskDetailId;
	}

	public Long getTaskDetailId(){
		return this.taskDetailId;
	}

	public void setDealStat(String dealStat){
		this.dealStat = dealStat;
	}

	public String getDealStat(){
		return this.dealStat;
	}

	public void setIsResend(String isResend){
		this.isResend = isResend;
	}

	public String getIsResend(){
		return this.isResend;
	}

	public void setServiceId(Integer serviceId){
		this.serviceId = serviceId;
	}

	public Integer getServiceId(){
		return this.serviceId;
	}

	public void setSubjectId(Integer subjectId){
		this.subjectId = subjectId;
	}

	public Integer getSubjectId(){
		return this.subjectId;
	}

	public void setCaseNo(String caseNo){
		this.caseNo = caseNo;
	}

	public String getCaseNo(){
		return this.caseNo;
	}

	public void setBuyerUserId(Long buyerUserId){
		this.buyerUserId = buyerUserId;
	}

	public Long getBuyerUserId(){
		return this.buyerUserId;
	}

	public void setBuyerUserName(String buyerUserName){
		this.buyerUserName = buyerUserName;
	}

	public String getBuyerUserName(){
		return this.buyerUserName;
	}

	public void setBuyerUserType(String buyerUserType){
		this.buyerUserType = buyerUserType;
	}

	public String getBuyerUserType(){
		return this.buyerUserType;
	}

	public void setIsEntrustBy(String isEntrustBy){
		this.isEntrustBy = isEntrustBy;
	}

	public String getIsEntrustBy(){
		return this.isEntrustBy;
	}

	public void setSendUserId(Long sendUserId){
		this.sendUserId = sendUserId;
	}

	public Long getSendUserId(){
		return this.sendUserId;
	}

	public void setSendUserName(String sendUserName){
		this.sendUserName = sendUserName;
	}

	public String getSendUserName(){
		return this.sendUserName;
	}

	public void setSendTime(java.util.Date sendTime){
		this.sendTime = sendTime;
	}

	public java.util.Date getSendTime(){
		return this.sendTime;
	}

	public void setIsPayBy(String isPayBy){
		this.isPayBy = isPayBy;
	}

	public String getIsPayBy(){
		return this.isPayBy;
	}

	public void setPayUserId(Long payUserId){
		this.payUserId = payUserId;
	}

	public Long getPayUserId(){
		return this.payUserId;
	}

	public void setPayUserName(String payUserName){
		this.payUserName = payUserName;
	}

	public String getPayUserName(){
		return this.payUserName;
	}

	public void setAuditUserId(Long auditUserId){
		this.auditUserId = auditUserId;
	}

	public Long getAuditUserId(){
		return this.auditUserId;
	}

	public void setAuditUserName(String auditUserName){
		this.auditUserName = auditUserName;
	}

	public String getAuditUserName(){
		return this.auditUserName;
	}

	public void setAuditTime(java.util.Date auditTime){
		this.auditTime = auditTime;
	}

	public java.util.Date getAuditTime(){
		return this.auditTime;
	}

	public void setGroupUserId(Long groupUserId){
		this.groupUserId = groupUserId;
	}

	public Long getGroupUserId(){
		return this.groupUserId;
	}

	public void setGroupUserName(String groupUserName){
		this.groupUserName = groupUserName;
	}

	public String getGroupUserName(){
		return this.groupUserName;
	}

	public void setSellerUserId(Long sellerUserId){
		this.sellerUserId = sellerUserId;
	}

	public Long getSellerUserId(){
		return this.sellerUserId;
	}

	public void setSellerUserName(String sellerUserName){
		this.sellerUserName = sellerUserName;
	}

	public String getSellerUserName(){
		return this.sellerUserName;
	}

	public void setSellerUserType(String sellerUserType){
		this.sellerUserType = sellerUserType;
	}

	public String getSellerUserType(){
		return this.sellerUserType;
	}

	public void setMileage(Float mileage){
		this.mileage = mileage;
	}

	public Float getMileage(){
		return this.mileage;
	}

	public void setWorkAddress(String workAddress){
		this.workAddress = workAddress;
	}

	public String getWorkAddress(){
		return this.workAddress;
	}

	

	public Float getLongitude() {
		return longitude;
	}

	public void setLongitude(Float longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(Float latitude){
		this.latitude = latitude;
	}

	public Float getLatitude(){
		return this.latitude;
	}

	public String getProvCode() {
		return provCode;
	}

	public void setProvCode(String provCode) {
		this.provCode = provCode;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

}
