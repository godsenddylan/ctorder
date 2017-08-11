package net.chetong.order.service.user;

import java.math.BigDecimal;
import java.util.Map;

import net.chetong.order.model.CtGroupManageFeeVO;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.PdServiceChannelTaxVO;
import net.chetong.order.model.PrRuleInfoVO;
import net.chetong.order.util.ctenum.ChannelCostType;
import net.chetong.order.util.ctenum.PrSettingType;
import net.chetong.order.util.ctenum.ServiceId;

/**
 * 用户金额计算器
 * @author wufj@chetong.net
 */
public interface UserPriceCalcutorService {
	
	/**
	 * 查询计算基础
	 * @param negoId 议价信息id
	 * @return 基础议价
	 */
	public BigDecimal calculateBaseNegoPrice(Long negoId);
	
	/**
	 * 计算基础议价
	 * @param buyerId 买家id
	 * @param workProvCode 出险地省
	 * @param workCityCode 出险地市
	 * @param workCountyCode 出险地区县
	 * @param subjectType 订单类型
	 * @return
	 */
	public BigDecimal calculateBaseNegoPrice(Long buyerId, Long groupUserId, String workProvCode, String workCityCode, String workCountyCode, String subjectType);
	
	/**
	 * 计算远程议价
	 * @param distanceDecimal 距离
	 * @param negoId 议价信息id
	 * @return 远程议价
	 */
	public BigDecimal calculateRemoteNegoPrice(BigDecimal distanceDecimal, Long negoId);
	
	/**
	 * 计算远程议价
	 * @param distanceDecimal 距离
	 * @param buyerId 买家id
	 * @param workProvCode 出险地省
	 * @param workCityCode 出险地市
	 * @param workCountyCode 出险地区县
	 * @param subjectType 订单类型
	 * @return 远程议价
	 */
	public BigDecimal calculateRemoteNegoPrice(BigDecimal distanceDecimal, Long buyerId,Long groupUserId, String workProvCode, String workCityCode, String workCountyCode, String subjectType);
	
	/**
	 * 计算基础自主调价
	 * @param orderType 订单类型
	 * @param userId 用户id
	 * @return 基础自主调价
	 *//*
	public BigDecimal calculateBaseAdjustPrice(String orderType, Long userId);*/
	
	/**
	 * 计算基础自主调价
	 * @param orderType 订单类型
	 * @param userId 用户id
	 * @return 基础自主调价
	 *        adjustedMoney :调价
	 *        isFixedPrice：是否一口价
	 */
	public Map<String, Object> calculateBaseAdjustPrice(String orderType, Long userId);
	
	/**
	 * 计算基础自主调价
	 * @author wufj@chetong.net
	 *         2015年12月2日 上午11:00:19
	 * @param adjustPriceId 自主调价id
	 * @return 基础自主调价
	 *  	adjustedMoney :调价
	 *      isFixedPrice：是否一口价
	 */
	public Map<String, Object> calculateBaseAdjustPrice(Long adjustPriceId);
	
	/**
	 * 计算远程自主调价
	 * @param orderType 订单类型
	 * @param userId 用户id（车童）
	 * @param distanceDecimal 距离
	 * @return 远程自主调价
	 */
	public BigDecimal calculateRemoteAdjustPrice(String orderType, Long userId ,BigDecimal distanceDecimal);
	
	/**
	 * 计算远程自主调价
	 * @author wufj@chetong.net
	 *         2015年12月2日 上午10:59:22
	 * @param adjustPriceId 自主调价信息id
	 * @param distanceDecimal 距离
	 * @return 远程自主调价
	 */
	public BigDecimal calculateRemoteAdjustPrice(Long adjustPriceId, BigDecimal distanceDecimal);
	
	/**
	 * 计算基础指导价
	 * @param priceRuleId 指导价规则id
	 * @return 基础指导价
	 */
	public BigDecimal calculateBaseGuidePrice(Long priceRuleId);
	
	/**
	 * 计算基础指导价
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @param subjectType  服务类型（1-查勘 2-定损 3-其他）
	 * @return 基础指导价
	 */
	public BigDecimal calculateBaseGuidePrice(String workProvCode, String workCityCode, String subjectType);
	
	/**
	 * 计算远程指导价
	 * @param priceRuleId 指导价规则id
	 * @param distanceDecimal 距离
	 * @return 远程指导价
	 */
	public BigDecimal calculateRemoteGuidePrice(Long priceRuleId, BigDecimal distanceDecimal);
	
	/**
	 * 计算远程指导价
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @param subjectType  服务类型（1-查勘 2-定损 3-其他）
	 * @param distanceDecimal 距离
	 * @return 远程指导价
	 */
	public BigDecimal calculateRemoteGuidePrice(String workProvCode, String workCityCode, String subjectType, BigDecimal distanceDecimal);
	
	/**
	 * 计算通道费
	 * @param channelTax 通道费设置  调用方法queryServiceChannelTax可得
	 * @param originalMonery 原始金额
	 * @return 通道费金额
	 */
	public BigDecimal calculateChannelMoney(PdServiceChannelTaxVO channelTax, BigDecimal originalMonery);
	
	/**
	 * 计算通道费
	 * @param channelTax 通道费设置  调用方法queryServiceChannelTax可得
	 * @param originalMonery 原始金额
	 * @param workProvCode 地区省
	 * @param groupUserId  团队id 没有团队传null值
	 * @param costType 通道费类型
	 * @return 通道费金额
	 */
	public BigDecimal calculateChannelMoney(BigDecimal originalMonery, String workProvCode, Long buyerId, ChannelCostType costType, ServiceId... serviceIds);
	
	/**
	 * 查询开票费率
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @return 开票费率
	 */
	public BigDecimal queryInvoice(String workProvCode,String workCityCode, ServiceId ServiceId);
	
	/**
	 * 计算开票费
	 * @author wufj@chetong.net
	 *         2015年12月7日 下午3:14:58
	 * @param originalBaseMoney 原始基础费
	 * @param originalRemoteMoney 原始远程作业费
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @return 开票费
	 */
	public BigDecimal calculateInvoiceMoney(BigDecimal originalBaseMoney,BigDecimal originalRemoteMoney, String workProvCode,String workCityCode);
	
	/**
	 * 查询通道费设置
	 * @param workProvCode 地区省
	 * @param groupUserId  团队id 没有团队传null值
	 * @param costType 通道费类型
	 */
	public PdServiceChannelTaxVO queryServiceChannelTax(String workProvCode ,Long buyerId ,ChannelCostType costType, ServiceId... serviceId);
	
	/**
	 * 查询指导价信息
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @param String  服务类型（1-查勘 2-定损 3-其他）
	 * @return 指导价信息
	 */
	public PrRuleInfoVO queryPrRuleInfo(String workProvCode, String workCityCode, String subjectType);
	
	/**
	 * 查询议价信息id
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @param workCountyCode 区县代码
	 * @param buyerId 买家
	 * @param subjectType 服务类型
	 * @return 议价信息id
	 */
	public Long queryPrNegoId(String workProvCode, String workCityCode, String workCountyCode, Long buyerId, Long groupUserId, String subjectType);
	
	/**
	 * 查询某地是否允许自主调价  
	 * 		只有该地允许自主调价才能调价
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @return 是否允许
	 */
	public boolean queryAreaIsAdjustPrice(String workProvCode, String workCityCode);
	
	/**
	 * 查询风险基金(单独扣除的)
	 * @author wufj@chetong.net
	 *         2015年12月2日 上午11:31:48
	 * @return 风险基金
	 */
	public BigDecimal queryCarInsuranceMoney();
	
	/**
	 * 查询财务费率（比例计算）
	 * @author wufj@chetong.net
	 *         2015年12月2日 下午1:52:20
	 * @return 财务费率
	 */
	public BigDecimal queryCarFinanceRate();
	
    /**
     * 计算财务费
     * @author wufj@chetong.net
     *         2016年1月20日 下午5:38:48
     * @param originalFee 订单金额
     * @return 财务费
     */
	public BigDecimal calculateCarFinanceFee(BigDecimal originalFee);
	
	/**
	 * 查询团队管理费配置
	 * @author wufj@chetong.net
	 *         2015年12月2日 下午3:26:02
	 * @param userId 车童userid
	 * @param sellerGroupUserId 车童团队userid
	 * @return
	 */
	public CtGroupManageFeeVO queryGroupManageMoney(Long userId, Long sellerGroupUserId, Long buyerUserId, ServiceId serviceId);
	
	/**
	 * 计算费用
	 * @author wufj@chetong.net
	 *         2015年12月3日 上午9:41:25
	 * @param sellerId 卖家userid
	 * @param buyerId 买家userid
	 * @param provCode 省代码
	 * @param cityCode 市代码
	 * @param countyCode 县代码
	 * @param distanceDecimal 距离
	 * @param subjectType 服务类型（1 查勘 2定损 3物损）
	 * @return Map<Struing,Object> 
	 *          baseMoney:基础费（基本）; remoteMoney:远程作业费（基本）; buyerBaseMoney:基础费（买家）;  buyerRemoteMoney:远程作业费（买家）;
	 *           sellerBaseMoney：基础费（卖家）；sellerRemoteMoney：远程作业费（卖家）；baseChannelMoney：基础通道费；remoteChannelMoney：远程通道费；
	 *           baseInvoiceMoney：基础开票费；remoteInvoiceMoney：远程开票费；baseGroupManageMoney:基础团队管理费;remoteGroupManageMoney:远程团队管理费
	 *           insuranceMoney:风险基金;financeMoney:财务费
	 */
	public Map<String, Object> calculateCarPrice(Long sellerId,Long buyerId, String provCode, String cityCode,String countyCode, BigDecimal distanceDecimal,String subjectType,Map<String,Object> priceTypeInfo, Long... groupUserIdArg);

	/**
	 * 货运险-费用计算（买家）
	 * @author wufj@chetong.net
	 *         2016年1月5日 下午1:58:28
	 * @param orderFee 原始订单金额
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @param buyerId 买家userId
	 * @return
	 * 		channelMoney: 通道费 invoiceMoney：开票费 buyerMoney：买家应付金额
	 */
	public Map<String, BigDecimal> calculateHyBuyerFee(BigDecimal orderFee, String workProvCode, String workCityCode, CtGroupVO buyer);
	
	/**
	 * 货运险-费用计算（卖家）
	 * @author wufj@chetong.net
	 *         2016年1月5日 下午1:58:13
	 * @param orderFee 原始订单金额
	 * @param userId 卖家userid
	 * @param sellerGroupUserId 卖家团队userId
	 * @param hasCargo 是否具有货运险资质
	 * @param buyerUserId 买家用户id
	 * @return 
	 * 		InsuranceMoney：风险基金 financeMoney：财务费 sellerMoney：车童应得金额 manageMoney：团队应得管理费
	 */
	public Map<String, BigDecimal> calculateHySellerFee(BigDecimal orderFee, Long sellerId, Long sellerGroupUserId, String hasCargo, Long buyerUserId);
	
	/**
	 * 委托费用计算
	 * @author wufj@chetong.net
	 *         2016年2月29日 上午11:31:07
	 * @param applyUserId   被委托人
	 * @param grantUserId   委托人
	 * @param grantType     委托类型 1 派单 2 审核 3 待支付
	 * @param serviceId   服务类型 1 车险  2货运险
	 * @return
	 */
	public BigDecimal calculateEntrustFee(Long applyUserId, Long grantUserId, String grantType, String serviceId);
	
	/**
	 * 查询保证金
	 * @author wufj@chetong.net
	 *         2016年1月20日 下午6:26:19
	 * @return
	 */
	public BigDecimal queryCarBondRate();
	
	/**
	 * 查询prsetting
	 * @author wufj@chetong.net
	 *         2016年1月27日 下午8:12:43
	 * @param type
	 * @return
	 */
	public BigDecimal queryPrSetting(PrSettingType type);

	/**
	 * @Description: 根据费用类型查询相关团队管理费
	 * @param feeParam map<费用类型,费用金额>
	 * @return
	 * @return Map<String,BigDecimal>
	 * @author zhouchushu
	 * @param groupUserId 
	 * @param sellerUserId 
	 * @date 2016年2月18日 下午4:49:06
	 */
	public Map<String, BigDecimal> queryGroupManageFeeByType(Map<String, BigDecimal> feeParam, Long sellerUserId, Long groupUserId, Long buyerUserId);
	
	/**
	 * 根据id团队管理费
	 * @param feeParam
	 * @param manageId
	 * @return
	 */
	public Map<String, BigDecimal> queryGroupManageFeeByManageId(Map<String, BigDecimal> feeParam, String manageId);

	/**
	 * @Description: 查询结算方式
	 * @param buyerId
	 * @param adCode
	 * @return
	 * @return Map<String,Object>
	 * @author zhouchushu
	 * @throws Exception 
	 * @date 2016年5月30日 上午11:00:04
	 */
	public Map<String, Object> checkPriceType(Long buyerId, String adCode) throws Exception;
	
	
	/**
	 * @Description: 指导价信息
	 * @param workProvCode
	 * @param workCityCode
	 * @param workCountyCode
	 * @return
	 * @return Map<String,Object>
	 * @author zhouchushu
	 * @date 2016年5月31日 上午10:09:51
	 */
	public Map<String, Object> queryGuidePriceInfo(String workProvCode, String workCityCode, String workCountyCode,String guidPriceType,String serviceId,String state) throws Exception; 

	/**
	 * @Description: 查询作业地车险管理机构
	 * @param workProvCode
	 * @param workCityCode
	 * @return
	 * @return CtGroupVO
	 * @author zhouchushu
	 * @date 2016年6月7日 下午4:35:02
	 */
	public CtGroupVO queryWorkPlaceManageOrg(String workProvCode, String workCityCode);
}
