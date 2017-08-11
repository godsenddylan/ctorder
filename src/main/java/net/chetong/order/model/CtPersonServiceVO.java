
package net.chetong.order.model; 

import java.util.Date;

public class CtPersonServiceVO implements java.io.Serializable {

	/**
	 * 属性定义.
	 */
	
	private Long id;	/*--  --*/
	private Long userId;	/*--  --*/
	private Long serviceId;	/*--  --*/
	private String auditStat;	/*-- 加盟服务审核状态 0 - 审核通过(已加盟 )   1 - 审核退回(未通过)     2待审核(认证中)  3 加盟退出 --*/
	private String stat;	/*-- 服务状态 0 - 停用 1 - 启用（后台操作） 2 未启用 --*/
	private String isService;	/*-- 当前是否对外提供  0 - 暂停 1 - 公开（前台服务人操作） --*/
	private String isExam;	/*-- 是否考试 0 - 否 1 - 是 --*/
	private Long maxExamId;	/*-- 最高成绩id --*/
	private String provCode;	/*-- 省份 --*/
	private String cityCode;	/*-- 地市 --*/
	private String areaCode;	/*-- 区县 --*/
	private String provDesc;	/*-- 省份 --*/
	private String cityDesc;	/*-- 地市 --*/
	private String areaDesc;	/*-- 区县 --*/
	private String serviceRank;	/*-- 服务级别 1- 一级 2 - 二级 3 - 三级 4 - 四级 5 - 五级 --*/
	private String reviewRank;	/*-- 评价均值  1- 一级 2 - 二级 3 - 三级 4 - 四级 5 - 五级 --*/
	private String ability;	/*-- 能力评价  1- 一级 2 - 二级 3 - 三级 4 - 四级 5 - 五级 --*/
	private Integer serviceCount;	/*-- 服务总次数 --*/
	private Date applyTime;	/*-- 加盟申请时间 --*/
	private Date auditTime;	/*-- 最近一次加盟审核时间 --*/
	private Date startTime;	/*-- 服务首次启用时间 --*/
	private Date modTime;	/*-- 状态改变时间 --*/
	private Long operId;	/*-- 操作人id --*/
	private String recommendName;	/*-- 推荐人姓名 --*/
	private String recommendMobile;	/*-- 推荐人手机号 --*/
	private String ext1;	/*-- 信用积分 --*/
	private String ext2;	/*-- 工作年限 --*/
	private String ext3;	/*-- 已支付奖励  1-已支付 --*/
	private String goodProfessional;	/*--  --*/
	private String isMgr;	/*-- 是否项目经理 1-是 0-否 --*/

	/**
	 * 构造函数.
	 */
	public CtPersonServiceVO() {}
	
	
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
	 * getServiceId.
	 */
	public Long getServiceId(){
		return serviceId;
	}
	
	/**
   * setServiceId.
   */
  
	public void setServiceId(Long serviceId){
		this.serviceId = serviceId;
	}

		
	/**
	 * getAuditStat.
	 */
	public String getAuditStat(){
		return auditStat;
	}
	
	/**
   * setAuditStat.
   */
  
	public void setAuditStat(String auditStat){
		this.auditStat = auditStat;
	}

		
	/**
	 * getStat.
	 */
	public String getStat(){
		return stat;
	}
	
	/**
   * setStat.
   */
  
	public void setStat(String stat){
		this.stat = stat;
	}

		
	/**
	 * getIsService.
	 */
	public String getIsService(){
		return isService;
	}
	
	/**
   * setIsService.
   */
  
	public void setIsService(String isService){
		this.isService = isService;
	}

		
	/**
	 * getIsExam.
	 */
	public String getIsExam(){
		return isExam;
	}
	
	/**
   * setIsExam.
   */
  
	public void setIsExam(String isExam){
		this.isExam = isExam;
	}

		
	/**
	 * getMaxExamId.
	 */
	public Long getMaxExamId(){
		return maxExamId;
	}
	
	/**
   * setMaxExamId.
   */
  
	public void setMaxExamId(Long maxExamId){
		this.maxExamId = maxExamId;
	}

		
	/**
	 * getProvCode.
	 */
	public String getProvCode(){
		return provCode;
	}
	
	/**
   * setProvCode.
   */
  
	public void setProvCode(String provCode){
		this.provCode = provCode;
	}

		
	/**
	 * getCityCode.
	 */
	public String getCityCode(){
		return cityCode;
	}
	
	/**
   * setCityCode.
   */
  
	public void setCityCode(String cityCode){
		this.cityCode = cityCode;
	}

		
	/**
	 * getAreaCode.
	 */
	public String getAreaCode(){
		return areaCode;
	}
	
	/**
   * setAreaCode.
   */
  
	public void setAreaCode(String areaCode){
		this.areaCode = areaCode;
	}

		
	/**
	 * getProvDesc.
	 */
	public String getProvDesc(){
		return provDesc;
	}
	
	/**
   * setProvDesc.
   */
  
	public void setProvDesc(String provDesc){
		this.provDesc = provDesc;
	}

		
	/**
	 * getCityDesc.
	 */
	public String getCityDesc(){
		return cityDesc;
	}
	
	/**
   * setCityDesc.
   */
  
	public void setCityDesc(String cityDesc){
		this.cityDesc = cityDesc;
	}

		
	/**
	 * getAreaDesc.
	 */
	public String getAreaDesc(){
		return areaDesc;
	}
	
	/**
   * setAreaDesc.
   */
  
	public void setAreaDesc(String areaDesc){
		this.areaDesc = areaDesc;
	}

		
	/**
	 * getServiceRank.
	 */
	public String getServiceRank(){
		return serviceRank;
	}
	
	/**
   * setServiceRank.
   */
  
	public void setServiceRank(String serviceRank){
		this.serviceRank = serviceRank;
	}

		
	/**
	 * getReviewRank.
	 */
	public String getReviewRank(){
		return reviewRank;
	}
	
	/**
   * setReviewRank.
   */
  
	public void setReviewRank(String reviewRank){
		this.reviewRank = reviewRank;
	}

		
	/**
	 * getAbility.
	 */
	public String getAbility(){
		return ability;
	}
	
	/**
   * setAbility.
   */
  
	public void setAbility(String ability){
		this.ability = ability;
	}

		
	/**
	 * getServiceCount.
	 */
	public Integer getServiceCount(){
		return serviceCount;
	}
	
	/**
   * setServiceCount.
   */
  
	public void setServiceCount(Integer serviceCount){
		this.serviceCount = serviceCount;
	}

		
	/**
	 * getApplyTime.
	 */
	public Date getApplyTime(){
		return applyTime;
	}
	
	/**
   * setApplyTime.
   */
  
	public void setApplyTime(Date applyTime){
		this.applyTime = applyTime;
	}

		
	/**
	 * getAuditTime.
	 */
	public Date getAuditTime(){
		return auditTime;
	}
	
	/**
   * setAuditTime.
   */
  
	public void setAuditTime(Date auditTime){
		this.auditTime = auditTime;
	}

		
	/**
	 * getStartTime.
	 */
	public Date getStartTime(){
		return startTime;
	}
	
	/**
   * setStartTime.
   */
  
	public void setStartTime(Date startTime){
		this.startTime = startTime;
	}

		
	/**
	 * getModTime.
	 */
	public Date getModTime(){
		return modTime;
	}
	
	/**
   * setModTime.
   */
  
	public void setModTime(Date modTime){
		this.modTime = modTime;
	}

		
	/**
	 * getOperId.
	 */
	public Long getOperId(){
		return operId;
	}
	
	/**
   * setOperId.
   */
  
	public void setOperId(Long operId){
		this.operId = operId;
	}

		
	/**
	 * getRecommendName.
	 */
	public String getRecommendName(){
		return recommendName;
	}
	
	/**
   * setRecommendName.
   */
  
	public void setRecommendName(String recommendName){
		this.recommendName = recommendName;
	}

		
	/**
	 * getRecommendMobile.
	 */
	public String getRecommendMobile(){
		return recommendMobile;
	}
	
	/**
   * setRecommendMobile.
   */
  
	public void setRecommendMobile(String recommendMobile){
		this.recommendMobile = recommendMobile;
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

		
	/**
	 * getGoodProfessional.
	 */
	public String getGoodProfessional(){
		return goodProfessional;
	}
	
	/**
   * setGoodProfessional.
   */
  
	public void setGoodProfessional(String goodProfessional){
		this.goodProfessional = goodProfessional;
	}

		
	/**
	 * getIsMgr.
	 */
	public String getIsMgr(){
		return isMgr;
	}
	
	/**
   * setIsMgr.
   */
  
	public void setIsMgr(String isMgr){
		this.isMgr = isMgr;
	}

}
