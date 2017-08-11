package net.chetong.order.model;

import java.util.Date;

/**
 * ClassName: FmHandoutVO 
 * @Description: TODO
 * @author zhouchushu
 * @date 2016年6月3日 下午2:18:12
 */
public class FmHandoutVO implements java.io.Serializable {

	private Long id; /*--  --*/
	private Long orderId; /*-- orderId --*/
	private String stat; /*-- 状�?? 0 无响�? 1 - 抢单成功 2 - 抢单失败 3 - 拒单 4-撤单 5响应订单 --*/
	private Long buyerUserId; /*-- 买方 --*/
	private String buyerUserType; /*-- 买方用户类型 0 - 个人 1 - 机构 --*/
	private Long sellerUserId; /*-- 卖方 --*/
	private String sellerUserType; /*-- 卖方用户类型 0 - 个人 1 - 机构 --*/
	private Long groupUserId; /*-- 当前车童的group user id --*/
	private Date modTime; /*-- 操作时间 --*/
	private String workDistance; /*-- 里程 --*/
	private String baseMoney; /*--  --*/
	private String travelMoney; /*--  --*/
	private String ext1; /*-- 扩展字段1 --*/
	private String ext2; /*-- 扩展字段2 --*/
	private String ext3; /*-- 扩展字段3 --*/
	private String arrivalHours; /*-- 几小时内到达 --*/
	private Date arrivalTime; /*-- 车童预计到达时间 --*/
	private String isNego; /*-- 是否议价车童 1-�? 0-�? --*/
	private Long negoId; /*-- 议价信息表id --*/
	private Long commiId; /*-- 团队车童佣金id --*/
	private String baseChannelMoney; /*-- 基础费的通道�? --*/
	private String remoteChannelMoney; /*-- 远程作业费的通道�? --*/
	private String baseInvoiMoney; /*-- 基础费的�?票费 --*/
	private String remoteInvoiMoney; /*-- 远程作业费的�?票费 --*/
	private String baseGroupManageMoney;
	private String remoteGroupManageMoney;
	private String insuranceMoney;
	private String financeMoney;
	private String guideBaseMoney; /*--指导价基础费--*/

	/**
	 * 构�?�函�?.
	 */
	public FmHandoutVO() {
	}

	/**
	 * Getter/Setter方法.
	 */

	/**
	 * getId.
	 */
	public Long getId() {
		return id;
	}

	/**
	 * setId.
	 */

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * getOrderId.
	 */
	public Long getOrderId() {
		return orderId;
	}

	/**
	 * setOrderId.
	 */

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	/**
	 * getStat.
	 */
	public String getStat() {
		return stat;
	}

	/**
	 * setStat.
	 */

	public void setStat(String stat) {
		this.stat = stat;
	}

	/**
	 * getBuyerUserId.
	 */
	public Long getBuyerUserId() {
		return buyerUserId;
	}

	/**
	 * setBuyerUserId.
	 */

	public void setBuyerUserId(Long buyerUserId) {
		this.buyerUserId = buyerUserId;
	}

	/**
	 * getBuyerUserType.
	 */
	public String getBuyerUserType() {
		return buyerUserType;
	}

	/**
	 * setBuyerUserType.
	 */

	public void setBuyerUserType(String buyerUserType) {
		this.buyerUserType = buyerUserType;
	}

	/**
	 * getSellerUserId.
	 */
	public Long getSellerUserId() {
		return sellerUserId;
	}

	/**
	 * setSellerUserId.
	 */

	public void setSellerUserId(Long sellerUserId) {
		this.sellerUserId = sellerUserId;
	}

	/**
	 * getSellerUserType.
	 */
	public String getSellerUserType() {
		return sellerUserType;
	}

	/**
	 * setSellerUserType.
	 */

	public void setSellerUserType(String sellerUserType) {
		this.sellerUserType = sellerUserType;
	}

	public Long getGroupUserId() {
		return groupUserId;
	}

	public void setGroupUserId(Long groupUserId) {
		this.groupUserId = groupUserId;
	}

	/**
	 * getModTime.
	 */
	public Date getModTime() {
		return modTime;
	}

	/**
	 * setModTime.
	 */

	public void setModTime(Date modTime) {
		this.modTime = modTime;
	}

	/**
	 * getWorkDistance.
	 */
	public String getWorkDistance() {
		return workDistance;
	}

	/**
	 * setWorkDistance.
	 */

	public void setWorkDistance(String workDistance) {
		this.workDistance = workDistance;
	}

	/**
	 * getBaseMoney.
	 */
	public String getBaseMoney() {
		return baseMoney;
	}

	/**
	 * setBaseMoney.
	 */

	public void setBaseMoney(String baseMoney) {
		this.baseMoney = baseMoney;
	}

	/**
	 * getTravelMoney.
	 */
	public String getTravelMoney() {
		return travelMoney;
	}

	/**
	 * setTravelMoney.
	 */

	public void setTravelMoney(String travelMoney) {
		this.travelMoney = travelMoney;
	}

	/**
	 * getExt1.
	 */
	public String getExt1() {
		return ext1;
	}

	/**
	 * setExt1.
	 */

	public void setExt1(String ext1) {
		this.ext1 = ext1;
	}

	/**
	 * getExt2.
	 */
	public String getExt2() {
		return ext2;
	}

	/**
	 * setExt2.
	 */

	public void setExt2(String ext2) {
		this.ext2 = ext2;
	}

	/**
	 * getExt3.
	 */
	public String getExt3() {
		return ext3;
	}

	/**
	 * setExt3.
	 */

	public void setExt3(String ext3) {
		this.ext3 = ext3;
	}

	/**
	 * getArrivalHours.
	 */
	public String getArrivalHours() {
		return arrivalHours;
	}

	/**
	 * setArrivalHours.
	 */

	public void setArrivalHours(String arrivalHours) {
		this.arrivalHours = arrivalHours;
	}

	/**
	 * getArrivalTime.
	 */
	public Date getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * setArrivalTime.
	 */

	public void setArrivalTime(Date arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	/**
	 * getIsNego.
	 */
	public String getIsNego() {
		return isNego;
	}

	/**
	 * setIsNego.
	 */

	public void setIsNego(String isNego) {
		this.isNego = isNego;
	}

	/**
	 * getNegoId.
	 */
	public Long getNegoId() {
		return negoId;
	}

	/**
	 * setNegoId.
	 */

	public void setNegoId(Long negoId) {
		this.negoId = negoId;
	}

	/**
	 * getCommiId.
	 */
	public Long getCommiId() {
		return commiId;
	}

	/**
	 * setCommiId.
	 */

	public void setCommiId(Long commiId) {
		this.commiId = commiId;
	}

	/**
	 * getBaseChannelMoney.
	 */
	public String getBaseChannelMoney() {
		return baseChannelMoney;
	}

	/**
	 * setBaseChannelMoney.
	 */

	public void setBaseChannelMoney(String baseChannelMoney) {
		this.baseChannelMoney = baseChannelMoney;
	}

	/**
	 * getRemoteChannelMoney.
	 */
	public String getRemoteChannelMoney() {
		return remoteChannelMoney;
	}

	/**
	 * setRemoteChannelMoney.
	 */

	public void setRemoteChannelMoney(String remoteChannelMoney) {
		this.remoteChannelMoney = remoteChannelMoney;
	}

	/**
	 * getBaseInvoiMoney.
	 */
	public String getBaseInvoiMoney() {
		return baseInvoiMoney;
	}

	/**
	 * setBaseInvoiMoney.
	 */

	public void setBaseInvoiMoney(String baseInvoiMoney) {
		this.baseInvoiMoney = baseInvoiMoney;
	}

	/**
	 * getRemoteInvoiMoney.
	 */
	public String getRemoteInvoiMoney() {
		return remoteInvoiMoney;
	}

	/**
	 * setRemoteInvoiMoney.
	 */

	public void setRemoteInvoiMoney(String remoteInvoiMoney) {
		this.remoteInvoiMoney = remoteInvoiMoney;
	}

	public String getBaseGroupManageMoney() {
		return baseGroupManageMoney;
	}

	public void setBaseGroupManageMoney(String baseGroupManageMoney) {
		this.baseGroupManageMoney = baseGroupManageMoney;
	}

	public String getRemoteGroupManageMoney() {
		return remoteGroupManageMoney;
	}

	public void setRemoteGroupManageMoney(String remoteGroupManageMoney) {
		this.remoteGroupManageMoney = remoteGroupManageMoney;
	}

	public String getInsuranceMoney() {
		return insuranceMoney;
	}

	public void setInsuranceMoney(String insuranceMoney) {
		this.insuranceMoney = insuranceMoney;
	}

	public String getFinanceMoney() {
		return financeMoney;
	}

	public void setFinanceMoney(String financeMoney) {
		this.financeMoney = financeMoney;
	}

	public String getGuideBaseMoney() {
		return guideBaseMoney;
	}

	public void setGuideBaseMoney(String guideBaseMoney) {
		this.guideBaseMoney = guideBaseMoney;
	}
	
	
}
