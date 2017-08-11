package net.chetong.order.model;
import java.io.Serializable;


/**
 * 
 * 简易流程作业信息
 * 
 **/
@SuppressWarnings("serial")
public class FmSimpleWork implements Serializable {

	/**表id**/
	private Long id;

	/**提交作业人id(车童id)**/
	private String userId;

	/**报案号**/
	private String caseNo;

	/**订单号**/
	private String orderNo;

	/**估损金额**/
	private java.math.BigDecimal lossMoney;

	/**车型**/
	private String carType;

	/**责任类型：1-私了，2-无责。**/
	private String duty;

	/**服务类型：1-查勘、2-定损（包括物损）**/
	private String subjectType;

	/**服务类型文字描述：1-查勘、2-定损（包括物损）**/
	private String subjectTypeDesc;

	/**每种服务类型下的选项内容：
查勘类型(1)
1）现场已勘，标的估损金额______ 车型______（需点选配件库）
2）已勘销案:(1)私了(2)无责 
3）拒赔 
4）现场定损
5）其他________(手动填写)。
定损类型,包含物损(2)：
1) 定损
2）已勘销案（1）私了 （2）无责 
3）拒赔 
4）其他________(手动填写)**/
	private String subjectOption;

	/**每种服务类型下的选项内容文字描述**/
	private String subjectOptionDesc;

	/**提交创建时间**/
	private String createTime;

	/**作业更新时间**/
	private String updateTime;



	public void setId(Long id){
		this.id = id;
	}

	public Long getId(){
		return this.id;
	}

	public void setUserId(String userId){
		this.userId = userId;
	}

	public String getUserId(){
		return this.userId;
	}

	public void setCaseNo(String caseNo){
		this.caseNo = caseNo;
	}

	public String getCaseNo(){
		return this.caseNo;
	}

	public void setOrderNo(String orderNo){
		this.orderNo = orderNo;
	}

	public String getOrderNo(){
		return this.orderNo;
	}

	public void setLossMoney(java.math.BigDecimal lossMoney){
		this.lossMoney = lossMoney;
	}

	public java.math.BigDecimal getLossMoney(){
		return this.lossMoney;
	}

	public void setCarType(String carType){
		this.carType = carType;
	}

	public String getCarType(){
		return this.carType;
	}

	public void setDuty(String duty){
		this.duty = duty;
	}

	public String getDuty(){
		return this.duty;
	}

	public void setSubjectType(String subjectType){
		this.subjectType = subjectType;
	}

	public String getSubjectType(){
		return this.subjectType;
	}

	public void setSubjectTypeDesc(String subjectTypeDesc){
		this.subjectTypeDesc = subjectTypeDesc;
	}

	public String getSubjectTypeDesc(){
		return this.subjectTypeDesc;
	}

	public void setSubjectOption(String subjectOption){
		this.subjectOption = subjectOption;
	}

	public String getSubjectOption(){
		return this.subjectOption;
	}

	public void setSubjectOptionDesc(String subjectOptionDesc){
		this.subjectOptionDesc = subjectOptionDesc;
	}

	public String getSubjectOptionDesc(){
		return this.subjectOptionDesc;
	}

	public void setCreateTime(String createTime){
		this.createTime = createTime;
	}

	public String getCreateTime(){
		return this.createTime;
	}

	public void setUpdateTime(String updateTime){
		this.updateTime = updateTime;
	}

	public String getUpdateTime(){
		return this.updateTime;
	}

}
