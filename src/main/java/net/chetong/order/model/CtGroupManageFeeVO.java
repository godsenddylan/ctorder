package net.chetong.order.model; 

import java.math.BigDecimal;

/**
 * 团队管理费（团队佣金）设置
 * @author wufj@chetong.net
 *         2015年12月2日 下午1:39:09
 */
public class CtGroupManageFeeVO implements java.io.Serializable {
	private static final long serialVersionUID = 4003988816211217222L;
	private Long id;	/*--  --*/
	private Long userId;	/*-- 机构id --*/
	private String ruleName;	/*-- 规则名称 --*/
	private String orderType;	/*-- 订单分类  1 内部订单  2 外部订单 --*/
	private BigDecimal baseCommission;	/*-- 基础费佣金 --*/
	private BigDecimal travelCommission;	/*-- 远程作业费佣金 --*/
	private BigDecimal extraCommission;	/*-- 附加费佣金 --*/
	private BigDecimal rewardCommission;	/*-- 买家奖励佣金 --*/
	private String ext1;	/*-- 1-默认  0-不默认 --*/
	private String ext2;	/*--  --*/
	private String ext3;	/*--  --*/
	private String ext4;	/*--  --*/
	private String ext5;	/*--  --*/
	private String ckBaseType;	/*-- 查勘佣金类型 1-比例 2-固定金额 --*/
	private BigDecimal ckBaseRate;	/*-- 查勘佣金比例 --*/
	private BigDecimal ckBaseTeamMoney;	/*-- 查勘团队固定金额 --*/
	private BigDecimal ckBasePersonMoney;	/*-- 查勘车童固定金额 --*/
	private String dsBaseType;	/*-- 定损佣金类型 --*/
	private BigDecimal dsBaseRate;	/*-- 定损佣金比例 --*/
	private BigDecimal dsBaseTeamMoney;	/*-- 定损团队固定金额 --*/
	private BigDecimal dsBasePersonMoney;	/*-- 定损车童固定金额 --*/
	private String qtBaseType;	/*-- 其他佣金类型 --*/
	private BigDecimal qtBaseRate;	/*-- 其他佣金比例 --*/
	private BigDecimal qtBaseTeamMoney;	/*-- 其他团队固定金额 --*/
	private BigDecimal qtBasePersonMoney;	/*-- 其他车童固定金额 --*/
	private BigDecimal ckAddTeamMoney;	/*-- 查勘团队追加固定金额 --*/
	private BigDecimal ckAddPersonMoney;	/*-- 查勘车童追加固定金额 --*/
	private BigDecimal dsAddTeamMoney;	/*-- 定损团队追加固定金额 --*/
	private BigDecimal dsAddPersonMoney;	/*-- 定损车童追加固定金额 --*/
	private BigDecimal qtAddTeamMoney;	/*-- 其他团队追加固定金额 --*/
	private BigDecimal qtAddPersonMoney;	/*-- 其他车童追加固定金额 --*/
	private BigDecimal cargoCommission;/*货运险团队管理费*/

	public CtGroupManageFeeVO() {}
	
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
	 * getRuleName.
	 */
	public String getRuleName(){
		return ruleName;
	}
	
	/**
   * setRuleName.
   */
  
	public void setRuleName(String ruleName){
		this.ruleName = ruleName;
	}

		
	/**
	 * getOrderType.
	 */
	public String getOrderType(){
		return orderType;
	}
	
	/**
   * setOrderType.
   */
  
	public void setOrderType(String orderType){
		this.orderType = orderType;
	}

		
	/**
	 * getBaseCommission.
	 */
	public BigDecimal getBaseCommission(){
		return baseCommission;
	}
	
	/**
   * setBaseCommission.
   */
  
	public void setBaseCommission(BigDecimal baseCommission){
		this.baseCommission = baseCommission;
	}

		
	/**
	 * getTravelCommission.
	 */
	public BigDecimal getTravelCommission(){
		return travelCommission;
	}
	
	/**
   * setTravelCommission.
   */
  
	public void setTravelCommission(BigDecimal travelCommission){
		this.travelCommission = travelCommission;
	}

		
	/**
	 * getExtraCommission.
	 */
	public BigDecimal getExtraCommission(){
		return extraCommission;
	}
	
	/**
   * setExtraCommission.
   */
  
	public void setExtraCommission(BigDecimal extraCommission){
		this.extraCommission = extraCommission;
	}

		
	/**
	 * getRewardCommission.
	 */
	public BigDecimal getRewardCommission(){
		return rewardCommission;
	}
	
	/**
   * setRewardCommission.
   */
  
	public void setRewardCommission(BigDecimal rewardCommission){
		this.rewardCommission = rewardCommission;
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
	 * getExt4.
	 */
	public String getExt4(){
		return ext4;
	}
	
	/**
   * setExt4.
   */
  
	public void setExt4(String ext4){
		this.ext4 = ext4;
	}

		
	/**
	 * getExt5.
	 */
	public String getExt5(){
		return ext5;
	}
	
	/**
   * setExt5.
   */
  
	public void setExt5(String ext5){
		this.ext5 = ext5;
	}

		
	/**
	 * getCkBaseType.
	 */
	public String getCkBaseType(){
		return ckBaseType;
	}
	
	/**
   * setCkBaseType.
   */
  
	public void setCkBaseType(String ckBaseType){
		this.ckBaseType = ckBaseType;
	}

		
	/**
	 * getCkBaseRate.
	 */
	public BigDecimal getCkBaseRate(){
		return ckBaseRate;
	}
	
	/**
   * setCkBaseRate.
   */
  
	public void setCkBaseRate(BigDecimal ckBaseRate){
		this.ckBaseRate = ckBaseRate;
	}

		
	/**
	 * getCkBaseTeamMoney.
	 */
	public BigDecimal getCkBaseTeamMoney(){
		return ckBaseTeamMoney;
	}
	
	/**
   * setCkBaseTeamMoney.
   */
  
	public void setCkBaseTeamMoney(BigDecimal ckBaseTeamMoney){
		this.ckBaseTeamMoney = ckBaseTeamMoney;
	}

		
	/**
	 * getCkBasePersonMoney.
	 */
	public BigDecimal getCkBasePersonMoney(){
		return ckBasePersonMoney;
	}
	
	/**
   * setCkBasePersonMoney.
   */
  
	public void setCkBasePersonMoney(BigDecimal ckBasePersonMoney){
		this.ckBasePersonMoney = ckBasePersonMoney;
	}

		
	/**
	 * getDsBaseType.
	 */
	public String getDsBaseType(){
		return dsBaseType;
	}
	
	/**
   * setDsBaseType.
   */
  
	public void setDsBaseType(String dsBaseType){
		this.dsBaseType = dsBaseType;
	}

		
	/**
	 * getDsBaseRate.
	 */
	public BigDecimal getDsBaseRate(){
		return dsBaseRate;
	}
	
	/**
   * setDsBaseRate.
   */
  
	public void setDsBaseRate(BigDecimal dsBaseRate){
		this.dsBaseRate = dsBaseRate;
	}

		
	/**
	 * getDsBaseTeamMoney.
	 */
	public BigDecimal getDsBaseTeamMoney(){
		return dsBaseTeamMoney;
	}
	
	/**
   * setDsBaseTeamMoney.
   */
  
	public void setDsBaseTeamMoney(BigDecimal dsBaseTeamMoney){
		this.dsBaseTeamMoney = dsBaseTeamMoney;
	}

		
	/**
	 * getDsBasePersonMoney.
	 */
	public BigDecimal getDsBasePersonMoney(){
		return dsBasePersonMoney;
	}
	
	/**
   * setDsBasePersonMoney.
   */
  
	public void setDsBasePersonMoney(BigDecimal dsBasePersonMoney){
		this.dsBasePersonMoney = dsBasePersonMoney;
	}

		
	/**
	 * getQtBaseType.
	 */
	public String getQtBaseType(){
		return qtBaseType;
	}
	
	/**
   * setQtBaseType.
   */
  
	public void setQtBaseType(String qtBaseType){
		this.qtBaseType = qtBaseType;
	}

		
	/**
	 * getQtBaseRate.
	 */
	public BigDecimal getQtBaseRate(){
		return qtBaseRate;
	}
	
	/**
   * setQtBaseRate.
   */
  
	public void setQtBaseRate(BigDecimal qtBaseRate){
		this.qtBaseRate = qtBaseRate;
	}

		
	/**
	 * getQtBaseTeamMoney.
	 */
	public BigDecimal getQtBaseTeamMoney(){
		return qtBaseTeamMoney;
	}
	
	/**
   * setQtBaseTeamMoney.
   */
  
	public void setQtBaseTeamMoney(BigDecimal qtBaseTeamMoney){
		this.qtBaseTeamMoney = qtBaseTeamMoney;
	}

		
	/**
	 * getQtBasePersonMoney.
	 */
	public BigDecimal getQtBasePersonMoney(){
		return qtBasePersonMoney;
	}
	
	/**
   * setQtBasePersonMoney.
   */
  
	public void setQtBasePersonMoney(BigDecimal qtBasePersonMoney){
		this.qtBasePersonMoney = qtBasePersonMoney;
	}

		
	/**
	 * getCkAddTeamMoney.
	 */
	public BigDecimal getCkAddTeamMoney(){
		return ckAddTeamMoney;
	}
	
	/**
   * setCkAddTeamMoney.
   */
  
	public void setCkAddTeamMoney(BigDecimal ckAddTeamMoney){
		this.ckAddTeamMoney = ckAddTeamMoney;
	}

		
	/**
	 * getCkAddPersonMoney.
	 */
	public BigDecimal getCkAddPersonMoney(){
		return ckAddPersonMoney;
	}
	
	/**
   * setCkAddPersonMoney.
   */
  
	public void setCkAddPersonMoney(BigDecimal ckAddPersonMoney){
		this.ckAddPersonMoney = ckAddPersonMoney;
	}

		
	/**
	 * getDsAddTeamMoney.
	 */
	public BigDecimal getDsAddTeamMoney(){
		return dsAddTeamMoney;
	}
	
	/**
   * setDsAddTeamMoney.
   */
  
	public void setDsAddTeamMoney(BigDecimal dsAddTeamMoney){
		this.dsAddTeamMoney = dsAddTeamMoney;
	}

		
	/**
	 * getDsAddPersonMoney.
	 */
	public BigDecimal getDsAddPersonMoney(){
		return dsAddPersonMoney;
	}
	
	/**
   * setDsAddPersonMoney.
   */
  
	public void setDsAddPersonMoney(BigDecimal dsAddPersonMoney){
		this.dsAddPersonMoney = dsAddPersonMoney;
	}

		
	/**
	 * getQtAddTeamMoney.
	 */
	public BigDecimal getQtAddTeamMoney(){
		return qtAddTeamMoney;
	}
	
	/**
   * setQtAddTeamMoney.
   */
  
	public void setQtAddTeamMoney(BigDecimal qtAddTeamMoney){
		this.qtAddTeamMoney = qtAddTeamMoney;
	}

		
	/**
	 * getQtAddPersonMoney.
	 */
	public BigDecimal getQtAddPersonMoney(){
		return qtAddPersonMoney;
	}
	
	/**
   * setQtAddPersonMoney.
   */
  
	public void setQtAddPersonMoney(BigDecimal qtAddPersonMoney){
		this.qtAddPersonMoney = qtAddPersonMoney;
	}

	public BigDecimal getCargoCommission() {
		return cargoCommission;
	}

	public void setCargoCommission(BigDecimal cargoCommission) {
		this.cargoCommission = cargoCommission;
	}
}
