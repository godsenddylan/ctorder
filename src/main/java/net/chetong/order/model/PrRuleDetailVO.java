package net.chetong.order.model; 

import java.math.BigDecimal;

/**
 * 指导价规则详细信息
 */
public class PrRuleDetailVO implements java.io.Serializable {
	private static final long serialVersionUID = -9116574385106297504L;
	
	private Long id;	/*-- id --*/
	private Long ruleId;	/*-- 规则信息id --*/
	private String costType;	/*-- 费用类型 1-基础费 2-远程作业费 3超额附加费 --*/
	private String valType;	/*-- 计费方式 0 - 固定金额 1 - 单价 2 - 比例  --*/
	private BigDecimal money;	/*-- 金额或比例 --*/
	private BigDecimal startValue;	/*-- 开始值 --*/
	private String startSign;	/*-- 开始值标记 0 - 大于 1 - 大于等于 2 - 小于 3 - 小于等于 --*/
	private BigDecimal endValue;	/*-- 结束值 --*/
	private String endSign;	/*-- 结束值标记   0 - 大于 1 - 大于等于 2 - 小于 3 - 小于等于 --*/
	private BigDecimal baseCost;	/*-- 首次任务指导价 --*/
	private BigDecimal addBaseCost;	/*-- 追加任务指导价 --*/
	private BigDecimal nightCost;	/*-- 夜间指导价 --*/
	private String nightStart;	/*-- 夜间开始时间 --*/
	private String nightEnd;	/*-- 夜间结束时间 --*/
	private BigDecimal weekendCost;	/*-- 周六日指导价 --*/
	private BigDecimal holidayCost;	/*-- 节假日指导价 --*/
	private BigDecimal springCost;	/*-- 春节指导价 --*/
	private BigDecimal baseMaxCost;	/*-- 普通时间调价最大值 --*/
	private BigDecimal addBaseMaxCost;	/*-- 普通时间追加任务最大值 --*/
	private BigDecimal nightMaxCost;	/*-- 夜间调价最大值 --*/
	private BigDecimal weekendMaxCost;	/*-- 周六日调价最大值 --*/
	private BigDecimal holidayMaxCost;	/*-- 节假日调价最大值 --*/
	private BigDecimal springMaxCost;	/*-- 春节调价最大值 --*/
	private String baseStat;	/*-- 首次任务平均价统计周期 --*/
	private String addBaseStat;	/*-- 追加任务平均价统计周期 --*/
	private String nightStat;	/*-- 夜间任务平均价统计周期 --*/
	private String weekendStat;	/*-- 周末任务平均价统计周期 --*/
	private String holidayStat;	/*-- 法定假日平均价统计周期 --*/
	private String springStat;	/*-- 春节均价统计周期 --*/

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
	 * getRuleId.
	 */
	public Long getRuleId(){
		return ruleId;
	}
	
	/**
   * setRuleId.
   */
  
	public void setRuleId(Long ruleId){
		this.ruleId = ruleId;
	}

		
	/**
	 * getCostType.
	 */
	public String getCostType(){
		return costType;
	}
	
	/**
   * setCostType.
   */
  
	public void setCostType(String costType){
		this.costType = costType;
	}

		
	/**
	 * getValType.
	 */
	public String getValType(){
		return valType;
	}
	
	/**
   * setValType.
   */
  
	public void setValType(String valType){
		this.valType = valType;
	}

		
	/**
	 * getMoney.
	 */
	public BigDecimal getMoney(){
		return money;
	}
	
	/**
   * setMoney.
   */
  
	public void setMoney(BigDecimal money){
		this.money = money;
	}

		
	/**
	 * getStartValue.
	 */
	public BigDecimal getStartValue(){
		return startValue;
	}
	
	/**
   * setStartValue.
   */
  
	public void setStartValue(BigDecimal startValue){
		this.startValue = startValue;
	}

		
	/**
	 * getStartSign.
	 */
	public String getStartSign(){
		return startSign;
	}
	
	/**
   * setStartSign.
   */
  
	public void setStartSign(String startSign){
		this.startSign = startSign;
	}

		
	/**
	 * getEndValue.
	 */
	public BigDecimal getEndValue(){
		return endValue;
	}
	
	/**
   * setEndValue.
   */
  
	public void setEndValue(BigDecimal endValue){
		this.endValue = endValue;
	}

		
	/**
	 * getEndSign.
	 */
	public String getEndSign(){
		return endSign;
	}
	
	/**
   * setEndSign.
   */
  
	public void setEndSign(String endSign){
		this.endSign = endSign;
	}

		
	/**
	 * getBaseCost.
	 */
	public BigDecimal getBaseCost(){
		return baseCost;
	}
	
	/**
   * setBaseCost.
   */
  
	public void setBaseCost(BigDecimal baseCost){
		this.baseCost = baseCost;
	}

		
	/**
	 * getAddBaseCost.
	 */
	public BigDecimal getAddBaseCost(){
		return addBaseCost;
	}
	
	/**
   * setAddBaseCost.
   */
  
	public void setAddBaseCost(BigDecimal addBaseCost){
		this.addBaseCost = addBaseCost;
	}

		
	/**
	 * getNightCost.
	 */
	public BigDecimal getNightCost(){
		return nightCost;
	}
	
	/**
   * setNightCost.
   */
  
	public void setNightCost(BigDecimal nightCost){
		this.nightCost = nightCost;
	}

		
	/**
	 * getNightStart.
	 */
	public String getNightStart(){
		return nightStart;
	}
	
	/**
   * setNightStart.
   */
  
	public void setNightStart(String nightStart){
		this.nightStart = nightStart;
	}

		
	/**
	 * getNightEnd.
	 */
	public String getNightEnd(){
		return nightEnd;
	}
	
	/**
   * setNightEnd.
   */
  
	public void setNightEnd(String nightEnd){
		this.nightEnd = nightEnd;
	}

		
	/**
	 * getWeekendCost.
	 */
	public BigDecimal getWeekendCost(){
		return weekendCost;
	}
	
	/**
   * setWeekendCost.
   */
  
	public void setWeekendCost(BigDecimal weekendCost){
		this.weekendCost = weekendCost;
	}

		
	/**
	 * getHolidayCost.
	 */
	public BigDecimal getHolidayCost(){
		return holidayCost;
	}
	
	/**
   * setHolidayCost.
   */
  
	public void setHolidayCost(BigDecimal holidayCost){
		this.holidayCost = holidayCost;
	}

		
	/**
	 * getSpringCost.
	 */
	public BigDecimal getSpringCost(){
		return springCost;
	}
	
	/**
   * setSpringCost.
   */
  
	public void setSpringCost(BigDecimal springCost){
		this.springCost = springCost;
	}

		
	/**
	 * getBaseMaxCost.
	 */
	public BigDecimal getBaseMaxCost(){
		return baseMaxCost;
	}
	
	/**
   * setBaseMaxCost.
   */
  
	public void setBaseMaxCost(BigDecimal baseMaxCost){
		this.baseMaxCost = baseMaxCost;
	}

		
	/**
	 * getAddBaseMaxCost.
	 */
	public BigDecimal getAddBaseMaxCost(){
		return addBaseMaxCost;
	}
	
	/**
   * setAddBaseMaxCost.
   */
  
	public void setAddBaseMaxCost(BigDecimal addBaseMaxCost){
		this.addBaseMaxCost = addBaseMaxCost;
	}

		
	/**
	 * getNightMaxCost.
	 */
	public BigDecimal getNightMaxCost(){
		return nightMaxCost;
	}
	
	/**
   * setNightMaxCost.
   */
  
	public void setNightMaxCost(BigDecimal nightMaxCost){
		this.nightMaxCost = nightMaxCost;
	}

		
	/**
	 * getWeekendMaxCost.
	 */
	public BigDecimal getWeekendMaxCost(){
		return weekendMaxCost;
	}
	
	/**
   * setWeekendMaxCost.
   */
  
	public void setWeekendMaxCost(BigDecimal weekendMaxCost){
		this.weekendMaxCost = weekendMaxCost;
	}

		
	/**
	 * getHolidayMaxCost.
	 */
	public BigDecimal getHolidayMaxCost(){
		return holidayMaxCost;
	}
	
	/**
   * setHolidayMaxCost.
   */
  
	public void setHolidayMaxCost(BigDecimal holidayMaxCost){
		this.holidayMaxCost = holidayMaxCost;
	}

		
	/**
	 * getSpringMaxCost.
	 */
	public BigDecimal getSpringMaxCost(){
		return springMaxCost;
	}
	
	/**
   * setSpringMaxCost.
   */
  
	public void setSpringMaxCost(BigDecimal springMaxCost){
		this.springMaxCost = springMaxCost;
	}

		
	/**
	 * getBaseStat.
	 */
	public String getBaseStat(){
		return baseStat;
	}
	
	/**
   * setBaseStat.
   */
  
	public void setBaseStat(String baseStat){
		this.baseStat = baseStat;
	}

		
	/**
	 * getAddBaseStat.
	 */
	public String getAddBaseStat(){
		return addBaseStat;
	}
	
	/**
   * setAddBaseStat.
   */
  
	public void setAddBaseStat(String addBaseStat){
		this.addBaseStat = addBaseStat;
	}

		
	/**
	 * getNightStat.
	 */
	public String getNightStat(){
		return nightStat;
	}
	
	/**
   * setNightStat.
   */
  
	public void setNightStat(String nightStat){
		this.nightStat = nightStat;
	}

		
	/**
	 * getWeekendStat.
	 */
	public String getWeekendStat(){
		return weekendStat;
	}
	
	/**
   * setWeekendStat.
   */
  
	public void setWeekendStat(String weekendStat){
		this.weekendStat = weekendStat;
	}

		
	/**
	 * getHolidayStat.
	 */
	public String getHolidayStat(){
		return holidayStat;
	}
	
	/**
   * setHolidayStat.
   */
  
	public void setHolidayStat(String holidayStat){
		this.holidayStat = holidayStat;
	}

		
	/**
	 * getSpringStat.
	 */
	public String getSpringStat(){
		return springStat;
	}
	
	/**
   * setSpringStat.
   */
  
	public void setSpringStat(String springStat){
		this.springStat = springStat;
	}

}
