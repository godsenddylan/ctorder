package net.chetong.order.model; 

import java.math.BigDecimal;
import java.util.List;

public class FhAuditTemp implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*-- id --*/
	private Long userId;	/*-- 暂存用户id --*/
	private String orderNo;	/*-- 订单号 --*/
	private BigDecimal managementFee2;	/*-- 配件管理费 --*/
	private BigDecimal remnant2;	/*-- 残费 --*/
	private BigDecimal buyerBonus;	/*-- 买家奖励 --*/
	private String auditOpinion;	/*-- 审核内容 --*/
	private String evaluateOpinion;	/*-- 点评内容 --*/
	private Integer starNum;	/*-- 买家奖励 --*/
	private String isValid;	/*-- 是否有效 0-否 1-是 --*/
	private String orderType;	/*-- 1-车险 2-货运险 --*/
	
	private String extraType;/*--特殊费用类型：1-奖励 2-扣款--*/
	private String extraReason;/*--扣款原因类型--*/
	private BigDecimal ctDeductMoney;	/*-- 车童扣款金额 --*/
	private BigDecimal teamDeductMoney;	/*-- 团队扣款金额 --*/
	private String extraExplain;/*--特殊费用说明--*/
	
	private String ext1;	/*-- 备用字段1 --*/
	private String ext2;	/*-- 备用字段2 --*/
	private String ext3;	/*-- 备用字段3 --*/

	private BigDecimal assessmentFee;	/*-- 货运险公估费 暂存字段 --*/
	
	private List<FhAuditTempCost> partList;
	private List<FhAuditTempCost> repairList;
	private List<FhAuditTempCost> damageList;
	private List<FhAuditTempCost> feeList;

	


	/**
	 * 构造函数.
	 */
	public FhAuditTemp() {}
	
	
	/**
	 * Getter/Setter方法.
	 */
		
	/**
	 * getId.
	 */
	public Long getId(){
		return id;
	}
	
	/**
   * setId.
   */
  
	public void setId(Long id){
		this.id = id;
	}

		
	/**
	 * getUserId.
	 */
	public Long getUserId(){
		return userId;
	}
	
	/**
   * setUserId.
   */
  
	public void setUserId(Long userId){
		this.userId = userId;
	}

		
	/**
	 * getOrderNo.
	 */
	public String getOrderNo(){
		return orderNo;
	}
	
	/**
   * setOrderNo.
   */
  
	public void setOrderNo(String orderNo){
		this.orderNo = orderNo;
	}

		
	/**
	 * getManagementFee.
	 */
	public BigDecimal getManagementFee2(){
		return managementFee2;
	}
	
	/**
   * setManagementFee.
   */
  
	public void setManagementFee2(BigDecimal managementFee2){
		this.managementFee2 = managementFee2;
	}

		
	/**
	 * getRemnant.
	 */
	public BigDecimal getRemnant2(){
		return remnant2;
	}
	
	/**
   * setRemnant.
   */
  
	public void setRemnant2(BigDecimal remnant2){
		this.remnant2 = remnant2;
	}

		
	/**
	 * getBuyerBonus.
	 */
	public BigDecimal getBuyerBonus(){
		return buyerBonus;
	}
	
	/**
   * setBuyerBonus.
   */
  
	public void setBuyerBonus(BigDecimal buyerBonus){
		this.buyerBonus = buyerBonus;
	}

		
	/**
	 * getAuditOpinion.
	 */
	public String getAuditOpinion(){
		return auditOpinion;
	}
	
	/**
   * setAuditOpinion.
   */
  
	public void setAuditOpinion(String auditOpinion){
		this.auditOpinion = auditOpinion;
	}

		
	/**
	 * getEvaluateOpinion.
	 */
	public String getEvaluateOpinion(){
		return evaluateOpinion;
	}
	
	/**
   * setEvaluateOpinion.
   */
  
	public void setEvaluateOpinion(String evaluateOpinion){
		this.evaluateOpinion = evaluateOpinion;
	}

		
	/**
	 * getStarNum.
	 */
	public Integer getStarNum(){
		return starNum;
	}
	
	/**
   * setStarNum.
   */
  
	public void setStarNum(Integer starNum){
		this.starNum = starNum;
	}

		
	public String getIsValid() {
		return isValid;
	}


	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}


	/**
	 * getExt1.
	 */
	public String getExt1(){
		return ext1;
	}
	
	/**
   * setExt1.
   */
  
	public void setExt1(String ext1){
		this.ext1 = ext1;
	}

		
	public String getOrderType() {
		return orderType;
	}


	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}


	public String getExtraType() {
		return extraType;
	}


	public void setExtraType(String extraType) {
		this.extraType = extraType;
	}


	public String getExtraReason() {
		return extraReason;
	}


	public void setExtraReason(String extraReason) {
		this.extraReason = extraReason;
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


	public String getExtraExplain() {
		return extraExplain;
	}


	public void setExtraExplain(String extraExplain) {
		this.extraExplain = extraExplain;
	}


	/**
	 * getExt2.
	 */
	public String getExt2(){
		return ext2;
	}
	
	/**
   * setExt2.
   */
  
	public void setExt2(String ext2){
		this.ext2 = ext2;
	}

		
	/**
	 * getExt3.
	 */
	public String getExt3(){
		return ext3;
	}
	
	/**
   * setExt3.
   */
  
	public void setExt3(String ext3){
		this.ext3 = ext3;
	}


	public List<FhAuditTempCost> getPartList() {
		return partList;
	}


	public void setPartList(List<FhAuditTempCost> partList) {
		this.partList = partList;
	}


	public List<FhAuditTempCost> getRepairList() {
		return repairList;
	}


	public void setRepairList(List<FhAuditTempCost> repairList) {
		this.repairList = repairList;
	}


	public List<FhAuditTempCost> getDamageList() {
		return damageList;
	}


	public void setDamageList(List<FhAuditTempCost> damageList) {
		this.damageList = damageList;
	}


	public List<FhAuditTempCost> getFeeList() {
		return feeList;
	}


	public void setFeeList(List<FhAuditTempCost> feeList) {
		this.feeList = feeList;
	}
	
	public BigDecimal getAssessmentFee() {
		return assessmentFee;
	}


	public void setAssessmentFee(BigDecimal assessmentFee) {
		this.assessmentFee = assessmentFee;
	}

}
