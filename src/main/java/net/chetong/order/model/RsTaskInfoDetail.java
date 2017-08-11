package net.chetong.order.model;
import java.io.Serializable;


/**
 * 
 * 车险人伤任务明细表
 * 
 **/
@SuppressWarnings("serial")
public class RsTaskInfoDetail implements Serializable {

	/****/
	private Long id;

	/**案件号**/
	private String caseNo;

	/**任务id**/
	private Long taskId;

	/**合约委托人id**/
	private Long entrustId;

	/**合约委托人名称**/
	private String entrustName;

	/**现场联系人**/
	private String accidentLinkman;

	/**现场联系人电话**/
	private String accidentLinktel;

	/**对接人名称**/
	private String supportLinkman;

	/**对接人电话**/
	private String supportLinktel;

	/**标的车牌号**/
	private String carNo;

	/**是否授予一次性调解**/
	private String isAllow;

	/**授予金额**/
	private java.math.BigDecimal allowMoney;

	/**伤者姓名**/
	private String injuredName;

	/**伤者电话**/
	private String injuredTel;

	/**医院地址**/
	private String hospitalAddress;

	/**事故经过描述**/
	private String accidentDesc;



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

	public void setTaskId(Long taskId){
		this.taskId = taskId;
	}

	public Long getTaskId(){
		return this.taskId;
	}

	public void setEntrustId(Long entrustId){
		this.entrustId = entrustId;
	}

	public Long getEntrustId(){
		return this.entrustId;
	}

	public void setEntrustName(String entrustName){
		this.entrustName = entrustName;
	}

	public String getEntrustName(){
		return this.entrustName;
	}

	public void setAccidentLinkman(String accidentLinkman){
		this.accidentLinkman = accidentLinkman;
	}

	public String getAccidentLinkman(){
		return this.accidentLinkman;
	}

	public void setAccidentLinktel(String accidentLinktel){
		this.accidentLinktel = accidentLinktel;
	}

	public String getAccidentLinktel(){
		return this.accidentLinktel;
	}

	public void setSupportLinkman(String supportLinkman){
		this.supportLinkman = supportLinkman;
	}

	public String getSupportLinkman(){
		return this.supportLinkman;
	}

	public void setSupportLinktel(String supportLinktel){
		this.supportLinktel = supportLinktel;
	}

	public String getSupportLinktel(){
		return this.supportLinktel;
	}

	public void setCarNo(String carNo){
		this.carNo = carNo;
	}

	public String getCarNo(){
		return this.carNo;
	}

	public void setIsAllow(String isAllow){
		this.isAllow = isAllow;
	}

	public String getIsAllow(){
		return this.isAllow;
	}

	public void setAllowMoney(java.math.BigDecimal allowMoney){
		this.allowMoney = allowMoney;
	}

	public java.math.BigDecimal getAllowMoney(){
		return this.allowMoney;
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

	public void setHospitalAddress(String hospitalAddress){
		this.hospitalAddress = hospitalAddress;
	}

	public String getHospitalAddress(){
		return this.hospitalAddress;
	}

	public void setAccidentDesc(String accidentDesc){
		this.accidentDesc = accidentDesc;
	}

	public String getAccidentDesc(){
		return this.accidentDesc;
	}

}
