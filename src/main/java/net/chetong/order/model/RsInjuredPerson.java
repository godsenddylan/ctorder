package net.chetong.order.model;
import java.io.Serializable;


/**
 * 
 * 车险人伤伤员表
 * 
 **/
@SuppressWarnings("serial")
public class RsInjuredPerson implements Serializable {

	/****/
	private Long id;

	/** 报案号**/
	private String caseNo;
	
	/**订单号**/
	private String orderNo;
	
	/**服务类型**/
	private Integer serviceId;
	
	/**伤者姓名**/
	private String injuredName;

	/**伤者电话**/
	private String injuredTel;

	/**伤者性别；0：男，1：女**/
	private String injuredSex;

	/**伤者年龄**/
	private Integer injuredAge;

	/**伤者类型**/
	private Integer personType;

	/**医院名称**/
	private String hospitalName;

	/**是否送医；0：否，1：是**/
	private String isSendHospital;

	/**事故类型**/
	private Integer accidentType;

	/**事故责任类型**/
	private Integer accidentRespType;



	public void setId(Long id){
		this.id = id;
	}

	public Long getId(){
		return this.id;
	}

	public void setCaseNo(String caseNo){
		this.caseNo = caseNo;
	}

	public String getCaseNo(){
		return this.caseNo;
	}

	public void setInjuredName(String injuredName){
		this.injuredName = injuredName;
	}

	public String getInjuredName(){
		return this.injuredName;
	}

	public void setInjuredTel(String injuredTel){
		this.injuredTel = injuredTel;
	}

	public String getInjuredTel(){
		return this.injuredTel;
	}

	public void setInjuredSex(String injuredSex){
		this.injuredSex = injuredSex;
	}

	public String getInjuredSex(){
		return this.injuredSex;
	}

	public void setInjuredAge(Integer injuredAge){
		this.injuredAge = injuredAge;
	}

	public Integer getInjuredAge(){
		return this.injuredAge;
	}

	public void setPersonType(Integer personType){
		this.personType = personType;
	}

	public Integer getPersonType(){
		return this.personType;
	}

	public void setHospitalName(String hospitalName){
		this.hospitalName = hospitalName;
	}

	public String getHospitalName(){
		return this.hospitalName;
	}

	public void setIsSendHospital(String isSendHospital){
		this.isSendHospital = isSendHospital;
	}

	public String getIsSendHospital(){
		return this.isSendHospital;
	}

	public void setAccidentType(Integer accidentType){
		this.accidentType = accidentType;
	}

	public Integer getAccidentType(){
		return this.accidentType;
	}

	public void setAccidentRespType(Integer accidentRespType){
		this.accidentRespType = accidentRespType;
	}

	public Integer getAccidentRespType(){
		return this.accidentRespType;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}
	
}
