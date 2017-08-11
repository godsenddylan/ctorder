package net.chetong.order.model;
import java.io.Serializable;


/**
 * 
 * 
 * 
 **/
@SuppressWarnings("serial")
public class CtMoneyRemindConfig extends BaseVO implements Serializable {

	/**表id**/
	private String id;

	/**机构id**/
	private String userId;

	/**所设置的与机构账号建立代支付关系的委托人id，多个  用 ， 相隔**/
	private String entrustUserId;

	/**委托人名称**/
	private String entrustUserName;

	/**后台管理人id**/
	private String operaterId;

	/**提醒类型：1-余额，2-远程作业费，3-超额附加费，4-重大案件提醒**/
	private String type;

	/**提醒类型描述**/
	private String typeName;

	/**手机号**/
	private String mobile;

	/**email多个使用  ,  相隔**/
	private String email;

	/**起止金额配置**/
	private java.math.BigDecimal lowMoney;

	/**高额阈值配置**/
	private java.math.BigDecimal highMoney;

	/**阈值配置，便于拓展**/
	private java.math.BigDecimal numberConfig;

	/**微信号,多个用 , 相隔**/
	private String wechatId;

	/**创建者id**/
	private String creatorId;

	/**创建时间**/
	private String createTime;

	/**更新时间**/
	private String updateTime;

	/**是否启用：1-启用，0-不启用**/
	private String isEnabled;
	
	/**
	 * 短信提醒是否启用 1是，0否
	 */
	private String smsIsEnabled;
	
	/**
	 * 邮件提醒是否启用1是，0否
	 */
	private String emailIsEnabled;
	
	/**
	 * 微信推送是否启用1是，0否
	 */
	private String wechatIsEnabled;
	
	/**
	 * 委托人是否选择全部，1-是，0否
	 */
	private String isAll;
	
	
	public String getIsAll() {
		return isAll;
	}

	public void setIsAll(String isAll) {
		this.isAll = isAll;
	}

	public String getSmsIsEnabled() {
		return smsIsEnabled;
	}

	public void setSmsIsEnabled(String smsIsEnabled) {
		this.smsIsEnabled = smsIsEnabled;
	}

	public String getEmailIsEnabled() {
		return emailIsEnabled;
	}

	public void setEmailIsEnabled(String emailIsEnabled) {
		this.emailIsEnabled = emailIsEnabled;
	}

	public String getWechatIsEnabled() {
		return wechatIsEnabled;
	}

	public void setWechatIsEnabled(String wechatIsEnabled) {
		this.wechatIsEnabled = wechatIsEnabled;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return this.id;
	}

	public void setEntrustUserId(String entrustUserId){
		this.entrustUserId = entrustUserId;
	}

	public String getEntrustUserId(){
		return this.entrustUserId;
	}

	public void setEntrustUserName(String entrustUserName){
		this.entrustUserName = entrustUserName;
	}

	public String getEntrustUserName(){
		return this.entrustUserName;
	}

	public void setOperaterId(String operaterId){
		this.operaterId = operaterId;
	}

	public String getOperaterId(){
		return this.operaterId;
	}

	public void setType(String type){
		this.type = type;
	}

	public String getType(){
		return this.type;
	}

	public void setTypeName(String typeName){
		this.typeName = typeName;
	}

	public String getTypeName(){
		return this.typeName;
	}

	public void setMobile(String mobile){
		this.mobile = mobile;
	}

	public String getMobile(){
		return this.mobile;
	}

	public void setEmail(String email){
		this.email = email;
	}

	public String getEmail(){
		return this.email;
	}

	public void setLowMoney(java.math.BigDecimal lowMoney){
		this.lowMoney = lowMoney;
	}

	public java.math.BigDecimal getLowMoney(){
		return this.lowMoney;
	}

	public void setHighMoney(java.math.BigDecimal highMoney){
		this.highMoney = highMoney;
	}

	public java.math.BigDecimal getHighMoney(){
		return this.highMoney;
	}

	public void setNumberConfig(java.math.BigDecimal numberConfig){
		this.numberConfig = numberConfig;
	}

	public java.math.BigDecimal getNumberConfig(){
		return this.numberConfig;
	}

	public void setWechatId(String wechatId){
		this.wechatId = wechatId;
	}

	public String getWechatId(){
		return this.wechatId;
	}

	public void setCreatorId(String creatorId){
		this.creatorId = creatorId;
	}

	public String getCreatorId(){
		return this.creatorId;
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

	public void setIsEnabled(String isEnabled){
		this.isEnabled = isEnabled;
	}

	public String getIsEnabled(){
		return this.isEnabled;
	}

}
