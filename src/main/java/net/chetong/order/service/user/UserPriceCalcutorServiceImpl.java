package net.chetong.order.service.user;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.chetong.order.model.CtAdjustPriceAreaVO;
import net.chetong.order.model.CtAdjustPriceDetailVO;
import net.chetong.order.model.CtAdjustPriceVO;
import net.chetong.order.model.CtGroupManageFeeVO;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtTakePaymentVO;
import net.chetong.order.model.CtThirdApplyInfoVO;
import net.chetong.order.model.CtUserLevelVO;
import net.chetong.order.model.HyWorkPriceConfig;
import net.chetong.order.model.PdServiceChannelTaxVO;
import net.chetong.order.model.PrGuidePriceDetailVO;
import net.chetong.order.model.PrGuidePriceInfoVO;
import net.chetong.order.model.PrInvoiceAreaVO;
import net.chetong.order.model.PrNegoPriceDetailVO;
import net.chetong.order.model.PrRuleDetailVO;
import net.chetong.order.model.PrRuleInfoVO;
import net.chetong.order.model.PrSettingVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.common.GroupService;
import net.chetong.order.service.common.TakePaymentService;
import net.chetong.order.util.Config;
import net.chetong.order.util.Constants;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.ctenum.ChannelCostType;
import net.chetong.order.util.ctenum.PrSettingType;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.ctenum.SpecialTime;
import net.chetong.order.util.exception.ProcessException;

/**
 * 用户金额计算器222
 * @author wufj@chetong.net
 */
@Service("userPriceCalcutorService")
public class UserPriceCalcutorServiceImpl extends BaseService implements UserPriceCalcutorService{

	//指导价基础费用类型
	public static final String GUIDE_PRICE_TYPE_BASEFEE = "1";
	//指导价超额附加费类型
	public static final String GUIDE_PRICE_TYPE_OVER_FEE = "2";
	//指导价追加单费用类型
	public static final String GUIDE_PRICE_TYPE_ADD_FEE = "3";
	
	public static final String ORG_PRICE = "1";
	public static final String OLD_PRICE = "0";
	public static final String WORK_PRICE = "2";
	

    private static List<String> spAreas = new ArrayList<String>();
    
    static {
        spAreas.add("440300"); // 深圳
        spAreas.add("330200"); // 宁波
       // spAreas.add("210200"); // 大连  /*modify by yinjm*/
    }
    
	
	
	@Resource
	private CommonService commonService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private GroupService groupService;
	
	@Resource
	private TakePaymentService takePaymentService;
	
	/**
     * 追加费通道费最小值
     */
    @Value("${additionalChannelMin}")
    private Long additionalChannelMin;
    /**
     * 追加费通道费最大值
     */
    @Value("${additionalChannelMax}")
    private Long additionalChannelMax;
	
	/**
	 * 计算基础议价
	 * @param negoId 议价信息id
	 * @return 基础议价
	 */
	@Override
	public BigDecimal calculateBaseNegoPrice(Long negoId) {
		log.info("计算车童的基础议价开始:"+negoId);
		//查询议价 基础费
		PrNegoPriceDetailVO queryNegoBasePriceExample = new PrNegoPriceDetailVO();
		queryNegoBasePriceExample.setNegoId(negoId);
		queryNegoBasePriceExample.setPriceType("1"); //首任务费用
		List<PrNegoPriceDetailVO> negoBasePriceList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPrNegoPriceDetail", queryNegoBasePriceExample);
		
		BigDecimal negoBasePrice = negoBasePriceList.get(0).getPriceMoney();
		log.info("车童的议价基础费:"+negoId+"/"+negoBasePrice);
		return negoBasePrice;
	}
	
	/**
	 * 计算基础议价
	 * @param buyerId 买家id
	 * @param sellerGroupUserId 卖家团队用户id
	 * @return 基础议价
	 */
	@Override
	public BigDecimal calculateBaseNegoPrice(Long buyerId, Long groupUserId, String workProvCode, String workCityCode, String workCountyCode, String subjectType) {
		Long negoId = this.queryPrNegoId(workProvCode, workCityCode, workCountyCode, buyerId, groupUserId, subjectType);
		if(negoId==null){
			return null;
		}
		return calculateBaseNegoPrice(negoId);
	}

	/**
	 * 查询计算车童远程议价
	 * @param distanceDecimal 距离
	 * @param negoId 议价信息id
	 * @return 远程议价
	 */
	@Override
	public BigDecimal calculateRemoteNegoPrice(BigDecimal distanceDecimal, Long negoId) {
		log.info("计算车童的远程议价开始:");
		//查询议价远程作业费
		PrNegoPriceDetailVO queryNegoRemotePriceExample = new PrNegoPriceDetailVO();
		queryNegoRemotePriceExample.setNegoId(negoId);
		queryNegoRemotePriceExample.setPriceType("3"); //3- 远程作业费
		List<PrNegoPriceDetailVO> negoRemotePriceList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPrNegoPriceDetail", queryNegoRemotePriceExample);
		
		//计算远程作业费
		BigDecimal negoRemoteMoney = BigDecimal.ZERO;
		for(int j = 0; j < negoRemotePriceList.size(); j++){
			PrNegoPriceDetailVO ppd = negoRemotePriceList.get(j);
			
			if(distanceDecimal.compareTo(ppd.getEndVal()) > 0){
				if("1".equals(ppd.getPriceMode())){ //固定金额
					negoRemoteMoney = negoRemoteMoney.add(ppd.getPriceMoney());
				}else{ //按单价算
					BigDecimal difMoney = ppd.getPriceMoney().multiply(ppd.getEndVal().subtract(ppd.getStartVal()));
					negoRemoteMoney = negoRemoteMoney.add(difMoney.multiply(new BigDecimal(2)));
				}
			}else if(distanceDecimal.compareTo(ppd.getStartVal()) > 0 && distanceDecimal.compareTo(ppd.getEndVal()) <= 0){
				if("1".equals(ppd.getPriceMode())){ //固定金额
					negoRemoteMoney = negoRemoteMoney.add(ppd.getPriceMoney());
				}else{ //按单价算
					BigDecimal difMoney = ppd.getPriceMoney().multiply(distanceDecimal.subtract(ppd.getStartVal()));
					negoRemoteMoney = negoRemoteMoney.add(difMoney.multiply(new BigDecimal(2)));
				}
			}
		}
		log.info("车童的议价远程作业费:"+negoId+"/"+negoRemoteMoney);
		return negoRemoteMoney;
	}
	
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
	@Override
	public BigDecimal calculateRemoteNegoPrice(BigDecimal distanceDecimal, Long buyerId, Long groupUserId, String workProvCode, String workCityCode, String workCountyCode, String subjectType){
		Long negoId = this.queryPrNegoId(workProvCode, workCityCode, workCountyCode, buyerId, groupUserId, subjectType);
		if(negoId==null){
			return null;
		}
		return calculateRemoteNegoPrice(distanceDecimal, negoId);
	}

	/**
	 * 计算基础自主调价
	 * @param orderType 订单类型
	 * @param userId 用户id
	 * @return 
	 * 		adjustedMoney:基础自主调价
	 * 		isFixedPrice:是否是一口价
	 */
	@Override
	public Map<String, Object> calculateBaseAdjustPrice(String orderType, Long userId) {
		log.info("车童基础自主调价查询计算开始："+userId);
		//查询车童自主调价配置
		CtAdjustPriceVO adjustBasePriceExample = new CtAdjustPriceVO();
		adjustBasePriceExample.setOrderType(orderType); //订单类型  1查勘 2定损 3物损
		adjustBasePriceExample.setCostType("1"); //1-基础费 2-远程作业费
		adjustBasePriceExample.setUserId(userId);
		List<CtAdjustPriceVO> adjustPriceList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryCtAdjustPrice", adjustBasePriceExample);
		
		BigDecimal adjustedMoney = null;
		if(adjustPriceList.size() > 0 && adjustPriceList.get(0).getBaseCost() != null){
			CtAdjustPriceVO adPrice = adjustPriceList.get(0);
			SpecialTime realTime = this.commonService.getSpecialTime(Integer.valueOf(adPrice.getNightStartTime()), Integer.valueOf(adPrice.getNightEndTime()));
			adjustedMoney = adPrice.getBaseCost();
			
			if(SpecialTime.Holiday.equals(realTime)){ //今天为节假日
				adjustedMoney = adPrice.getHolidayCost();
			}else if(SpecialTime.Spring.equals(realTime)){ //春节
				adjustedMoney = adPrice.getSpringCost();
			}else if(SpecialTime.Week.equals(realTime)){ //1-周日  7-周六
				adjustedMoney = adPrice.getWeekendCost();
			}else if(SpecialTime.Night.equals(realTime)){//夜间
				adjustedMoney = adPrice.getNightCost();
			}
			log.info("车童基础自主调价查询计算结束："+adjustedMoney);
			Map<String, Object> result = new HashMap<>();
			result.put("adjustedMoney", adjustedMoney);
			result.put("isFixedPrice", adPrice.getExt2());
			return result;
		}
		log.info("车童基础自主调价查询计算结束(没有基础调价)：");
		return null;
	}

	/**
	 * 计算基础自主调价
	 * @author wufj@chetong.net
	 *         2015年12月2日 上午11:00:19
	 * @param adjustPriceId 自主调价id
	 * @return 基础自主调价
	 * 		adjustedMoney :调价
	 *      isFixedPrice：是否一口价
	 */
	@Override
	public Map<String, Object> calculateBaseAdjustPrice(Long adjustPriceId) {
		log.info("车童基础自主调价查询计算开始："+adjustPriceId);
		//查询车童自主调价配置
		CtAdjustPriceVO adjustBasePriceExample = new CtAdjustPriceVO();
		adjustBasePriceExample.setId(adjustPriceId); 
		CtAdjustPriceVO adPrice = this.commExeSqlDAO.queryForObject("sqlmap_user_price.queryCtAdjustPrice", adjustBasePriceExample);
		
		BigDecimal adjustedMoney = null;
		if(adPrice != null){
			SpecialTime realTime = this.commonService.getSpecialTime(Integer.valueOf(adPrice.getNightStartTime()), Integer.valueOf(adPrice.getNightEndTime()));
			adjustedMoney = adPrice.getBaseCost();
			
			if(SpecialTime.Holiday.equals(realTime)){ //今天为节假日
				adjustedMoney = adPrice.getHolidayCost();
			}else if(SpecialTime.Spring.equals(realTime)){ //春节
				adjustedMoney = adPrice.getSpringCost();
			}else if(SpecialTime.Week.equals(realTime)){ //1-周日  7-周六
				adjustedMoney = adPrice.getWeekendCost();
			}else if(SpecialTime.Night.equals(realTime)){//夜间
				adjustedMoney = adPrice.getNightCost();
			}
			log.info("车童基础自主调价查询计算结束："+adjustedMoney);
			Map<String, Object> result = new HashMap<>();
			result.put("adjustedMoney", adjustedMoney);
			result.put("isFixedPrice", adPrice.getExt2());
			return result;
		}
		log.info("车童基础自主调价查询计算结束(没有基础调价)：");
		return null;
	}
	
	/**
	 * 计算远程自主调价
	 * @param orderType 订单类型
	 * @param userId 用户id（车童）
	 * @param distanceDecimal 距离
	 * @return 远程自主调价
	 */
	@Override
	public BigDecimal calculateRemoteAdjustPrice(String orderType, Long userId, BigDecimal distanceDecimal) {
		log.info("查询计算远程自主调价开始："+userId);
		BigDecimal adjustRemoteMoney = BigDecimal.ZERO;
		//计算车童自主调价的远程作业费
		CtAdjustPriceVO adjustRemotePriceExample = new CtAdjustPriceVO();
		adjustRemotePriceExample.setOrderType(orderType); //订单类型  1查勘 2定损 3物损
		adjustRemotePriceExample.setCostType("2"); //2 - 远程作业费
		adjustRemotePriceExample.setUserId(userId);
		List<CtAdjustPriceVO> adjustRemotePriceList =  this.commExeSqlDAO.queryForList("sqlmap_user_price.queryCtAdjustPrice", adjustRemotePriceExample);
		if(adjustRemotePriceList.size() > 0){ //有远程作业费调价
			//查询作业费调价详情
			CtAdjustPriceDetailVO queryRemotePriceDetailExample = new CtAdjustPriceDetailVO();
			queryRemotePriceDetailExample.setAdjustId(adjustRemotePriceList.get(0).getId());
			queryRemotePriceDetailExample.setCostType("2"); //2-远程作业费
			List<CtAdjustPriceDetailVO> remotePriceDetailList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryCtAdjustPriceDetail", queryRemotePriceDetailExample);
			
			String costMode = "1"; //1-固定金额模式
			for(int j = 0; j < remotePriceDetailList.size(); j++){
				CtAdjustPriceDetailVO remotePrice = remotePriceDetailList.get(j);
				costMode = remotePrice.getCostMode();
				if(distanceDecimal.compareTo(remotePrice.getEndVal()) > 0){
					if("1".equals(remotePrice.getCostMode())){ //1-固定金额模式
						adjustRemoteMoney = adjustRemoteMoney.add(remotePrice.getMoney());
					}else{
						BigDecimal difDis = remotePrice.getEndVal().subtract(remotePrice.getStartVal());
						adjustRemoteMoney = adjustRemoteMoney.add(remotePrice.getMoney().multiply(difDis));
					}
				}else if(distanceDecimal.compareTo(remotePrice.getStartVal()) > 0 && distanceDecimal.compareTo(remotePrice.getEndVal()) <= 0){
					if("1".equals(remotePrice.getCostMode())){ //1- 固定金额模式
						adjustRemoteMoney = adjustRemoteMoney.add(remotePrice.getMoney());
					}else{
						BigDecimal difDis = distanceDecimal.subtract(remotePrice.getStartVal());
						adjustRemoteMoney = adjustRemoteMoney.add(remotePrice.getMoney().multiply(difDis));
					}
				}
			}
			
			//调价后远程作业费
			if("1".equals(costMode)){ //1-固定金额模式
				adjustRemoteMoney = adjustRemoteMoney.setScale(2,BigDecimal.ROUND_HALF_UP);
			}else{ //比例模式
				adjustRemoteMoney = adjustRemoteMoney.multiply(new BigDecimal(2)).setScale(2,BigDecimal.ROUND_HALF_UP);
			}
		}
		log.info("查询计算远程自主调价结束："+adjustRemoteMoney);
		return adjustRemoteMoney;
	}
	
	/**
	 * 计算远程自主调价
	 * @author wufj@chetong.net
	 *         2015年12月2日 上午10:59:22
	 * @param adjustPriceId
	 * @param distanceDecimal
	 * @return 远程自主调价
	 */
	@Override
	public BigDecimal calculateRemoteAdjustPrice(Long adjustPriceId, BigDecimal distanceDecimal) {
		log.info("查询计算远程自主调价开始："+adjustPriceId);
		BigDecimal adjustRemoteMoney = BigDecimal.ZERO;
		//查询作业费调价详情
		CtAdjustPriceDetailVO queryRemotePriceDetailExample = new CtAdjustPriceDetailVO();
		queryRemotePriceDetailExample.setAdjustId(adjustPriceId);
		List<CtAdjustPriceDetailVO> remotePriceDetailList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryCtAdjustPriceDetail", queryRemotePriceDetailExample);
		
		String costMode = "1"; //1-固定金额模式
		for(int j = 0; j < remotePriceDetailList.size(); j++){
			CtAdjustPriceDetailVO remotePrice = remotePriceDetailList.get(j);
			costMode = remotePrice.getCostMode();
			if(distanceDecimal.compareTo(remotePrice.getEndVal()) > 0){
				if("1".equals(remotePrice.getCostMode())){ //1-固定金额模式
					adjustRemoteMoney = adjustRemoteMoney.add(remotePrice.getMoney());
				}else{
					BigDecimal difDis = remotePrice.getEndVal().subtract(remotePrice.getStartVal());
					adjustRemoteMoney = adjustRemoteMoney.add(remotePrice.getMoney().multiply(difDis));
				}
			}else if(distanceDecimal.compareTo(remotePrice.getStartVal()) > 0 && distanceDecimal.compareTo(remotePrice.getEndVal()) <= 0){
				if("1".equals(remotePrice.getCostMode())){ //1- 固定金额模式
					adjustRemoteMoney = adjustRemoteMoney.add(remotePrice.getMoney());
				}else{
					BigDecimal difDis = distanceDecimal.subtract(remotePrice.getStartVal());
					adjustRemoteMoney = adjustRemoteMoney.add(remotePrice.getMoney().multiply(difDis));
				}
			}
		}
		
		//调价后远程作业费
		if("1".equals(costMode)){ //1-固定金额模式
			adjustRemoteMoney = adjustRemoteMoney.setScale(2,BigDecimal.ROUND_HALF_UP);
		}else{ //比例模式
			adjustRemoteMoney = adjustRemoteMoney.multiply(new BigDecimal(2)).setScale(2,BigDecimal.ROUND_HALF_UP);
		}
		log.info("查询计算远程自主调价结束："+adjustRemoteMoney);
		return adjustRemoteMoney;
	}

	/**
	 * 计算基础指导价
	 * @param priceRuleId 指导价规则id
	 * @return 基础指导价
	 */
	@Override
	public BigDecimal calculateBaseGuidePrice(Long priceRuleId) {
		log.info("查询计算基础地区指导价开始："+priceRuleId);
		//查询指导价基础费配置
		PrRuleDetailVO queryRulePriceBaseExample = new PrRuleDetailVO();
		queryRulePriceBaseExample.setRuleId(priceRuleId);
		queryRulePriceBaseExample.setCostType("1"); //1-基础费
		
		List<PrRuleDetailVO> rulePriceBaseList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPrRuleDetail", queryRulePriceBaseExample);
		
		PrRuleDetailVO rulePriceBaseInfo = rulePriceBaseList.get(0);
		BigDecimal basemoney = rulePriceBaseInfo.getBaseCost();
		
		//计算指导价基础费
		SpecialTime realTime = this.commonService.getSpecialTime(Integer.valueOf(rulePriceBaseInfo.getNightStart()), Integer.valueOf(rulePriceBaseInfo.getNightEnd()));
		if(SpecialTime.Holiday.equals(realTime)){ //今天为节假日
			basemoney = rulePriceBaseInfo.getHolidayCost();
		}else if(SpecialTime.Spring.equals(realTime)){ //春节
			basemoney = rulePriceBaseInfo.getSpringCost();
		}else if(SpecialTime.Week.equals(realTime)){ //1-周日  7-周六
			basemoney = rulePriceBaseInfo.getWeekendCost();
		}else if(SpecialTime.Night.equals(realTime)){//夜间
			basemoney = rulePriceBaseInfo.getNightCost();
		}
		log.info("查询计算基础地区指导价结束：realTime"+realTime+"/basemoney"+basemoney);
		return basemoney;
	}

	/**
	 * 计算基础指导价
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @param subjectType  服务类型（1-查勘 2-定损 3-其他）
	 * @return 基础指导价
	 */
	@Override
	public BigDecimal calculateBaseGuidePrice(String workProvCode, String workCityCode, String subjectType) {
		PrRuleInfoVO queryPrRuleInfo = queryPrRuleInfo(workProvCode, workCityCode, subjectType);
		if(queryPrRuleInfo==null){
			return null;
		}
		return calculateBaseGuidePrice(queryPrRuleInfo.getId());
	}

	/**
	 * 计算远程指导价
	 * @param priceRuleId 指导价规则id
	 * @param distanceDecimal 距离
	 * @return 远程指导价
	 */
	@Override
	public BigDecimal calculateRemoteGuidePrice(Long priceRuleId, BigDecimal distanceDecimal) {
		log.info("查询计算远程地区指导价开始："+priceRuleId);
		//查询远程作业费配置信息
		PrRuleDetailVO queryRulePriceRemoteExample = new PrRuleDetailVO();
		queryRulePriceRemoteExample.setRuleId(priceRuleId);
		queryRulePriceRemoteExample.setCostType("2"); //2-远程作业费
		List<PrRuleDetailVO> rulePriceRemoteList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPrRuleDetail", queryRulePriceRemoteExample);
		
		BigDecimal remotePriceRuleMoney = BigDecimal.ZERO;
		String remoteValType = "0";
		for(int j = 0; j < rulePriceRemoteList.size(); j++){
			PrRuleDetailVO psbvd = rulePriceRemoteList.get(j);
			remoteValType = psbvd.getValType();
			
			if(distanceDecimal.compareTo(psbvd.getEndValue()) > 0){ //大于结束点
				if("0".equals(psbvd.getValType())){ // 0 - 固定金额
					remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney());
				}else if("1".equals(psbvd.getValType())){ //1 - 单价
					BigDecimal disDif = psbvd.getEndValue().subtract(psbvd.getStartValue());
					remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney().multiply(disDif));
				}
			}else if(distanceDecimal.compareTo(psbvd.getStartValue()) > 0 && distanceDecimal.compareTo(psbvd.getEndValue()) < 0){ //大于开始点 小于结束点
				if("0".equals(psbvd.getValType())){ // 0 - 固定金额
					remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney());
				}else if("1".equals(psbvd.getValType())){ //1 - 单价
					BigDecimal disDif = distanceDecimal.subtract(psbvd.getStartValue());
					remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney().multiply(disDif));
				}
			}else if(distanceDecimal.compareTo(psbvd.getStartValue()) == 0){
				if("3".equals(psbvd.getStartSign())){
					if("0".equals(psbvd.getValType())){ // 0 - 固定金额
						remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney());
					}else if("1".equals(psbvd.getValType())){ //1 - 单价
						BigDecimal disDif = distanceDecimal.subtract(psbvd.getStartValue());
						remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney().multiply(disDif));
					}
				}
			}else if(distanceDecimal.compareTo(psbvd.getEndValue()) == 0){
				if("3".equals(psbvd.getEndSign())){
					if("0".equals(psbvd.getValType())){
						remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney());
					}else if("1".equals(psbvd.getValType())){
						BigDecimal disDif = distanceDecimal.subtract(psbvd.getStartValue());
						remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney().multiply(disDif));
					}
				}else if("2".equals(psbvd.getEndSign())){
					if("0".equals(psbvd.getValType())){ // 0 - 固定金额
						remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney());
					}else if("1".equals(psbvd.getValType())){ //1 - 单价
						BigDecimal disDif = psbvd.getEndValue().subtract(psbvd.getStartValue());
						remotePriceRuleMoney = remotePriceRuleMoney.add(psbvd.getMoney().multiply(disDif));
					}
				}
			}
		}
		
		//差旅费计算
		if("0".equals(remoteValType)){ //固定金额
			remotePriceRuleMoney = remotePriceRuleMoney.setScale(2, BigDecimal.ROUND_HALF_UP);
		}else{ //比例
			remotePriceRuleMoney = remotePriceRuleMoney.multiply(new BigDecimal("2")).setScale(2, BigDecimal.ROUND_HALF_UP);
		}
		log.info("查询计算远程地区指导价结束："+remotePriceRuleMoney);
		return remotePriceRuleMoney;
	}

	/**
	 * 计算远程指导价
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @param subjectType  服务类型（1-查勘 2-定损 3-其他）
	 * @param distanceDecimal 距离
	 * @return 远程指导价
	 */
	@Override
	public BigDecimal calculateRemoteGuidePrice(String workProvCode, String workCityCode, String subjectType,BigDecimal distanceDecimal) {
		PrRuleInfoVO queryPrRuleInfo = queryPrRuleInfo(workProvCode, workCityCode, subjectType);
		if(queryPrRuleInfo==null){
			return null;
		}
		return calculateRemoteGuidePrice(queryPrRuleInfo.getId(), distanceDecimal);
	}

	/**
	 * 计算通道费
	 * @param channelTax 通道费设置  调用方法queryServiceChannelTax可得
	 * @param originalMonery 原始金额
	 * @return 通道费金额
	 */
	@Override
	public BigDecimal calculateChannelMoney(PdServiceChannelTaxVO channelTax, BigDecimal originalMonery) {
		log.info("calculateChannelMoney:"+originalMonery);
		BigDecimal channelMoney = BigDecimal.ZERO;
		if(channelTax!=null){
			if("0".equals(channelTax.getChannelMode())){ //0-固定金额
				if(originalMonery.compareTo(BigDecimal.ZERO) > 0){
					channelMoney = channelTax.getChannel();
				}
			}else if("1".equals(channelTax.getChannelMode())){
				channelMoney = originalMonery.multiply(channelTax.getChannel()).divide(new BigDecimal("100"));
			}
		}
		log.info("calculateChannelMoney:"+channelMoney);
		return channelMoney;
	}

	/**
	 * 计算通道费
	 * @param channelTax 通道费设置  调用方法queryServiceChannelTax可得
	 * @param originalMonery 原始金额
	 * @param workProvCode 地区省
	 * @param groupUserId  团队id 没有团队传null值
	 * @param costType 通道费类型
	 * @return 通道费金额
	 */
	@Override
	public BigDecimal calculateChannelMoney(BigDecimal originalMonery, String workProvCode, Long buyerId, ChannelCostType costType, ServiceId... serviceIds) {
		PdServiceChannelTaxVO channelTax = this.queryServiceChannelTax(workProvCode, buyerId, costType, serviceIds);
		if(channelTax==null){
			return null;
		}
		return calculateChannelMoney(channelTax, originalMonery);
	}

	/**
	 * 查询开票费率
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @return 开票费率
	 */
	@Override
	public BigDecimal queryInvoice(String workProvCode, String workCityCode, ServiceId serviceId) {
		log.info("查询开票费率开始："+workCityCode);
		//查询市的开票费率
		PrInvoiceAreaVO queryInvoiceExample = new PrInvoiceAreaVO();
		queryInvoiceExample.setProvCode(workProvCode);
		queryInvoiceExample.setCityCode(workCityCode);
		queryInvoiceExample.setIsDefault("0");
		queryInvoiceExample.setServiceId(serviceId.getValue());
		List<PrInvoiceAreaVO> invoiceAreaList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPrInvoiceArea", queryInvoiceExample);
		
		//没有市的开票费率，查询省的开票费率
		if(invoiceAreaList.size() <= 0){
			PrInvoiceAreaVO queryProvInvoiceExample = new PrInvoiceAreaVO();
			queryProvInvoiceExample.setProvCode(workProvCode);
			queryProvInvoiceExample.setCityCode("000000");
			queryProvInvoiceExample.setIsDefault("0");
			queryInvoiceExample.setServiceId(serviceId.getValue());
			invoiceAreaList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPrInvoiceArea",queryProvInvoiceExample);
		}
		if(invoiceAreaList.size()<=0){
			log.info("没有查询到开票费率");
			return BigDecimal.ZERO;
		}
		log.info("查询到开票费率："+invoiceAreaList.get(0).getInvoiceRate());
		return invoiceAreaList.get(0).getInvoiceRate();
	}
	
	/**
	 * 计算开票费率
	 * @author wufj@chetong.net
	 *         2015年12月7日 下午3:07:09
	 * @param originalMoney 原始金额
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @return
	 */
	@Override
	public BigDecimal calculateInvoiceMoney(BigDecimal originalBaseMoney,BigDecimal originalRemoteMoney, String workProvCode,String workCityCode){
		BigDecimal invoiceRate = this.queryInvoice(workProvCode, workCityCode, ServiceId.CAR);
		if(invoiceRate==null){
			return null;
		}
		return originalBaseMoney.add(originalRemoteMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(originalBaseMoney).subtract(originalRemoteMoney);
	}

	/**
	 * 查询通道费设置
	 * @param workProvCode 地区省
	 * @param groupUserId  团队id 没有团队传null值
	 * @param costType 通道费类型
	 */
	@Override
	public PdServiceChannelTaxVO queryServiceChannelTax(String workProvCode, Long buyerId, ChannelCostType costType, ServiceId... serviceIds) {
		log.info("查询通道费开始:groupUserId:"+buyerId+"/costType"+costType);
		List<PdServiceChannelTaxVO> channelList = new ArrayList<>();
		
		Long serviceId;
		if(serviceIds.length==0){
			serviceId = 1L;
		}else{
			serviceId = Long.valueOf(serviceIds[0].getValue());
		}
		
		//如果有团队 查询针对团队的通道费计算
		if(buyerId!=null){
			//查询针对团队 通道费设置信息
			PdServiceChannelTaxVO queryBaseChannelPersonExample = new PdServiceChannelTaxVO();
			queryBaseChannelPersonExample.setExt1("2"); //2-外部订单 新价格体系没有内部订单
			queryBaseChannelPersonExample.setExt2("2");//2-针对团队
			queryBaseChannelPersonExample.setExt3(buyerId.toString());
			queryBaseChannelPersonExample.setCostType(costType.getValue()); //通道费类型 1:基础费 2差旅费 3超额附加费 4买家奖励（货运险为追加费）
			queryBaseChannelPersonExample.setServiceId(serviceId);
			channelList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPdServiceChannelTaxVO", queryBaseChannelPersonExample);
		}
		
		//如果针对团队的通道费为空 查询针对地区的通道费设置
		if(channelList.size() <= 0){
			PdServiceChannelTaxVO queryBaseChannelAreaExample = new PdServiceChannelTaxVO();
			queryBaseChannelAreaExample.setExt1("2"); //2-外部订单 新价格体系没有内部订单
			queryBaseChannelAreaExample.setExt2("1"); //1-针对区域
			queryBaseChannelAreaExample.setProvCode(workProvCode);
			queryBaseChannelAreaExample.setCostType(costType.getValue()); //通道费类型 1:基础费 2差旅费 3超额附加费 4买家奖励（货运险为追加费）
			queryBaseChannelAreaExample.setServiceId(serviceId);
			channelList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPdServiceChannelTaxVO", queryBaseChannelAreaExample);
		}
		
		//现只有货运险有全国配置
		if (channelList.size()<=0 && serviceId.equals("5")) {
			PdServiceChannelTaxVO queryBaseChannelAreaExample = new PdServiceChannelTaxVO();
			queryBaseChannelAreaExample.setExt1("2"); //2-外部订单 新价格体系没有内部订单
			queryBaseChannelAreaExample.setExt2("1"); //1-针对区域
			queryBaseChannelAreaExample.setProvCode("000000");
			queryBaseChannelAreaExample.setCostType(costType.getValue()); //通道费类型 1:基础费 2差旅费 3超额附加费 4买家奖励（货运险为追加费）
			queryBaseChannelAreaExample.setServiceId(serviceId);
			channelList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryPdServiceChannelTaxVO", queryBaseChannelAreaExample);
		}
		
		//返回结果
		if(channelList.size()>0){
			log.info("查询到通道费："+channelList.get(0));
			return channelList.get(0);
		}
		log.info("没有查询到通道费");
		return null;
	}

	/**
	 * 查询指导价信息
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @param subjectType  服务类型（1-查勘 2-定损 3-其他）
	 * @return 指导价信息
	 */
	@Override
	public PrRuleInfoVO queryPrRuleInfo(String workProvCode, String workCityCode, String subjectType) {
		//查询区域价格指导，地区基础指导价与出险地有关 ，全部车童相同
		Map<String, Object> rulePriceAreaParamsMap = new HashMap<String, Object>();
		rulePriceAreaParamsMap.put("proveCode", workProvCode);
		rulePriceAreaParamsMap.put("subjectId", subjectType);
		rulePriceAreaParamsMap.put("cityCode", workCityCode);
		
		PrRuleInfoVO rulePriceInfo = this.commExeSqlDAO.queryForObject("sqlmap_user_price.queryRulePriceInfo", rulePriceAreaParamsMap);
		if(rulePriceInfo==null){//处理重庆开县情况
			rulePriceAreaParamsMap.put("cityCode", null);	
			rulePriceInfo = this.commExeSqlDAO.queryForObject("sqlmap_user_price.queryRulePriceInfo", rulePriceAreaParamsMap);
		}
		
		return rulePriceInfo;
	}

	/**
	 * 查询议价信息id
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @param workCountyCode 区县代码
	 * @param buyerId 买家
	 * @param subjectType 服务类型
	 * @return 议价信息id
	 */
	@Override
	public Long queryPrNegoId(String workProvCode, String workCityCode, String workCountyCode, Long buyerId, Long groupUserId, String subjectType) {
		log.info("查询买家的出险地的议价议价信息:buyerId:"+buyerId);
		log.info("-------------派单议价查询-------------------------------->provCode"+workProvCode);
		log.info("-------------派单议价查询-------------------------------->cityCode"+workCityCode);
		log.info("-------------派单议价查询-------------------------------->countyCode"+workCountyCode);
		log.info("-------------派单议价查询-------------------------------->buyerUserId"+buyerId.toString());
		log.info("-------------派单议价查询-------------------------------->groupUserId"+groupUserId.toString());
		log.info("-------------派单议价查询-------------------------------->subjectType"+subjectType);
		
		List<Long> negoPriceList = new ArrayList<Long>();
		//查询出险地(省市县)的团队议价信息 （通过议价关联）
		if(!StringUtil.isNullOrEmpty(workCountyCode)){
			Map<String, String> queryTeamParamsMap = new HashMap<String, String>();
			queryTeamParamsMap.put("provCode", workProvCode);
			queryTeamParamsMap.put("cityCode", workCityCode);
			queryTeamParamsMap.put("countyCode", workCountyCode);
			queryTeamParamsMap.put("buyerUserId", buyerId.toString());
			queryTeamParamsMap.put("groupUserId", groupUserId.toString());
			queryTeamParamsMap.put("subjectType", subjectType);//服务类型（1-查勘 2-定损 3-其他）
			negoPriceList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryWorkTeamNegoPriceList", queryTeamParamsMap);
		}
		
		//查询市的团队议价信息
		if(negoPriceList.size()<=0){
			Map<String, String> queryCityTeamParamsMap = new HashMap<String, String>();
			queryCityTeamParamsMap.put("provCode", workProvCode);
			queryCityTeamParamsMap.put("cityCode", workCityCode);
			queryCityTeamParamsMap.put("countyCode", "000000");
			queryCityTeamParamsMap.put("buyerUserId", buyerId.toString());
			queryCityTeamParamsMap.put("groupUserId", groupUserId.toString());
			queryCityTeamParamsMap.put("subjectType", subjectType);//服务类型（1-查勘 2-定损 3-其他）
			negoPriceList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryWorkTeamNegoPriceList", queryCityTeamParamsMap);
		}
		
		if(negoPriceList.size() <= 0&&!StringUtil.isNullOrEmpty(workCountyCode)){
			//查询县的默认议价信息
			Map<String, String> queryTeamDefaultParamsMap = new HashMap<String, String>();
			queryTeamDefaultParamsMap.put("provCode", workProvCode);
			queryTeamDefaultParamsMap.put("cityCode", workCityCode);
			queryTeamDefaultParamsMap.put("countyCode", workCountyCode);
			queryTeamDefaultParamsMap.put("groupUserId", groupUserId.toString());
			queryTeamDefaultParamsMap.put("subjectType", subjectType);//服务类型（1-查勘 2-定损 3-其他）
			queryTeamDefaultParamsMap.put("isDefault", "1"); //1-默认
			negoPriceList  = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryWorkTeamNegoPriceList", queryTeamDefaultParamsMap);
		}
		
		if (negoPriceList.size()<=0) {
			//查询市的默认议价信息
			Map<String, String> queryTeamCityDefaultParamsMap = new HashMap<String, String>();
			log.info(queryTeamCityDefaultParamsMap);
			queryTeamCityDefaultParamsMap.put("provCode", workProvCode);
			queryTeamCityDefaultParamsMap.put("cityCode", workCityCode);
			queryTeamCityDefaultParamsMap.put("groupUserId", groupUserId.toString());
			queryTeamCityDefaultParamsMap.put("countyCode", "000000");
			queryTeamCityDefaultParamsMap.put("subjectType", subjectType);//服务类型（1-查勘 2-定损 3-其他）
			queryTeamCityDefaultParamsMap.put("isDefault", "1"); //1-默认
			negoPriceList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryWorkTeamNegoPriceList", queryTeamCityDefaultParamsMap);
		}
		log.info("---------------------------------派单议价查询(默认)-----------议价信息（条）："+negoPriceList.size());
		if(negoPriceList.size()>0){
			log.info("查询到买家在出险地的议价信息："+negoPriceList.get(0));
			return negoPriceList.get(0);
		}
		log.info("没有查询到买家在出险地的议价信息");
		return null;
	}

	/**
	 * 查询某地是否允许自主调价  
	 * 		只有该地允许自主调价才能调价
	 * @param workProvCode 省代码
	 * @param workCityCode 市代码
	 * @return 是否允许
	 */
	@Override
	public boolean queryAreaIsAdjustPrice(String workProvCode, String workCityCode){
		log.info("查询地区是否允许自主调价："+workProvCode+"/"+workCityCode);
		CtAdjustPriceAreaVO queryAdjustPriceAreaExample = new CtAdjustPriceAreaVO();
		queryAdjustPriceAreaExample.setProvCode(workProvCode);
		queryAdjustPriceAreaExample.setCityCode(workCityCode);
		List<CtAdjustPriceAreaVO> adjustPriceAreaList = this.commExeSqlDAO.queryForList("sqlmap_user_price.queryCtAdjustPriceArea", queryAdjustPriceAreaExample);
		if(adjustPriceAreaList.size()>0){
			log.info("允许自主调价");
			return true;
		}
		log.info("不允许自主调价");
		return false;
	}
	
	/**
	 * 查询风险基金(单独扣除的)
	 * @author wufj@chetong.net
	 *         2015年12月2日 上午11:31:48
	 * @return 风险基金
	 */
	@Override
	public BigDecimal queryCarInsuranceMoney(){
		log.info("queryInsuranceMoney查询风险基金开始");
		BigDecimal insuranceMoney = null;
		PrSettingVO insuranceSettingExample = new PrSettingVO();
		insuranceSettingExample.setSettingType("4"); //4-风险基金
		List<PrSettingVO> insuranceMoneyList = commExeSqlDAO.queryForList("sqlmap_user_price.queryPrSetting",insuranceSettingExample);
		if(insuranceMoneyList.size() > 0){
			insuranceMoney = insuranceMoneyList.get(0).getSettingValue();
		}
		log.info("queryInsuranceMoney查询风险基金为："+insuranceMoney);
		return insuranceMoney;
	}
	
	/**
	 * 
	 * @author wufj@chetong.net
	 *         2016年1月27日 下午8:11:20
	 * @return
	 */
	@Override
	public BigDecimal queryPrSetting(PrSettingType type){
		log.info("queryPrSetting查询prsetting开始");
		BigDecimal prSettingValue = null;
		PrSettingVO prSettingExample = new PrSettingVO();
		prSettingExample.setSettingType(type.getValue());
		List<PrSettingVO> prSettingList = commExeSqlDAO.queryForList("sqlmap_user_price.queryPrSetting",prSettingExample);
		if(prSettingList.size() > 0){
			prSettingValue = prSettingList.get(0).getSettingValue();
		}
		log.info("queryPrSetting："+prSettingValue);
		return prSettingValue;
	}
	
	/**
	 * 查询财务费率（比例计算）
	 * @author wufj@chetong.net
	 *         2015年12月2日 下午1:52:20
	 * @return 财务费率
	 */
	@Override
	public BigDecimal queryCarFinanceRate(){
		log.info("queryFinanceRate查询财务费率开始");
		BigDecimal financeRate = BigDecimal.ZERO;
		PrSettingVO financeRateSettingExample = new PrSettingVO();
		financeRateSettingExample.setSettingType("1"); //1-财务费率
		List<PrSettingVO> financeRateList = commExeSqlDAO.queryForList("sqlmap_user_price.queryPrSetting",financeRateSettingExample);
		if(financeRateList.size() > 0){
			financeRate = financeRateList.get(0).getSettingValue();
		}
		log.info("queryFinanceRate查询财务费率为："+financeRate);
		return financeRate;
	}
	
	/**
	 * 计算财务费
	 * @author wufj@chetong.net
	 *         2016年1月20日 下午5:42:41
	 * @param originalFee
	 * @return 财务费
	 */
	@Override
	public BigDecimal calculateCarFinanceFee(BigDecimal originalFee){
		BigDecimal financeRate = this.queryCarFinanceRate();
		return originalFee.multiply(financeRate); 
	}
	
	/**
	 * 查询团队管理费配置
	 * @author wufj@chetong.net
	 *         2015年12月2日 下午3:26:02
	 * @param userId 车童userid
	 * @param sellerGroupUserId 车童团队userid
	 * @return
	 */
	@Override
	public CtGroupManageFeeVO queryGroupManageMoney(Long userId, Long sellerGroupUserId, Long buyerUserId, ServiceId serviceId){
		log.info(userId+"卖家加入团队："+sellerGroupUserId);
		//查询团队的管理费配置   关联卖家的
		CtGroupManageFeeVO groupManageFee = null;
		
		Map<String, Object> paramMap = new HashMap<>();
		paramMap.put("sellerGroupUserId", sellerGroupUserId);
		paramMap.put("userId", userId);
		paramMap.put("serviceId", serviceId.getValue());
		paramMap.put("buyerUserId", buyerUserId);
		
		//优先查询有指定机构指定车童的配置
		groupManageFee = commExeSqlDAO.queryForObject("sqlmap_user_price.queryCtGroupManageFeeByUserIdAndBuyUserId", paramMap);
		
		//其次查询无机构有指定车童配置
		if (groupManageFee==null) {
			groupManageFee = commExeSqlDAO.queryForObject("sqlmap_user_price.queryCtGroupManageFeeByUserId", paramMap);
		}
		
		//以上都没有 使用默认的分配规则
		if(groupManageFee == null){
			CtGroupManageFeeVO queryDefaultManageFeeExample = new CtGroupManageFeeVO();
			queryDefaultManageFeeExample.setUserId(sellerGroupUserId);
			queryDefaultManageFeeExample.setExt1("1"); //1 - 默认
			//queryDefaultManageFeeExample.setOrderType("2");//外部订单
			List<CtGroupManageFeeVO> defaultGroupManageFeeList = commExeSqlDAO.queryForList("sqlmap_user_price.queryCtGroupManageFee", queryDefaultManageFeeExample);
			if(defaultGroupManageFeeList.size() > 0){
				groupManageFee = defaultGroupManageFeeList.get(0);
			}
		}
		return groupManageFee;
	}
	
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
	 * @param groupUserIdArg 请传递数据，不管数据数量
	 * @return Map<Struing,Object> 
	 *           baseMoney:基础费（基本）; remoteMoney:远程作业费（基本）; buyerBaseMoney:基础费（买家）;  buyerRemoteMoney:远程作业费（买家）;
	 *           sellerBaseMoney：基础费（卖家）；sellerRemoteMoney：远程作业费（卖家）；baseChannelMoney：基础通道费；remoteChannelMoney：远程通道费；
	 *           baseInvoiceMoney：基础开票费；remoteInvoiceMoney：远程开票费；baseGroupManageMoney:基础团队管理费;remoteGroupManageMoney:远程团队管理费
	 *           insuranceMoney:风险基金;financeMoney:财务费
	 */
	@Override
	public Map<String, Object> calculateCarPrice(Long sellerId,Long buyerId,
			String provCode, String cityCode,String countyCode, BigDecimal distanceDecimal,String subjectType,Map<String,Object> priceTypeInfo, Long... groupUserIdArg){
		log.info("UserPriceCalcutorServiceImpl.calculatePrice/计算费用开始:");
		Map<String, Object> resultMap;
		try {
			resultMap = new HashMap<>();
			
			BigDecimal guideBaseMoney = null; //指导价基础费
			BigDecimal baseMoney = null;//基础费（基本）
			BigDecimal remoteMoney = null;//远程作业费（基本）
			BigDecimal buyerBaseMoney = null;//基础费（买家）
			BigDecimal buyerRemoteMoney = null;//远程作业费（买家）
			BigDecimal sellerBaseMoney = null;//基础费（卖家）
			BigDecimal sellerRemoteMoney = null;//远程作业费（卖家）
			BigDecimal baseChannelMoney = null;//基础通道费
			BigDecimal remoteChannelMoney = null;//远程通道费
			BigDecimal baseInvoiceMoney = null;//基础开票费
			BigDecimal remoteInvoiceMoney = null;//远程开票费
			BigDecimal baseGroupManageMoney = null;//基础团队管理费
			BigDecimal remoteGroupManageMoney = null;//远程团队管理费
			BigDecimal insuranceMoney = null;//风险基金
			BigDecimal financeMoney = null;//财务费
			
			//查询结算信息
			String priceType = (String) priceTypeInfo.get("priceType");
			BigDecimal guideBaseFee = null;
			if(ORG_PRICE.equals(priceType)){
				//查询指导价通道费
				Map<String,Object> guidePriceInfo = (Map<String, Object>) priceTypeInfo.get("guidePriceInfo");
				guideBaseFee = (BigDecimal) guidePriceInfo.get("guideBaseFee");
			}
			
			Long groupUserId = null;
			//如果调用者自己传了groupUserId,则直接使用调用者传递的（可能为空，为空就使用空值），否则数据库查询数据
			if(groupUserIdArg.length==1){
				groupUserId = groupUserIdArg[0];
			}else{
				//查询用户团队
				CtGroupVO userGroup = userService.queryUserGroupByUserId(sellerId);
				if(userGroup!=null){
					groupUserId = userGroup.getUserId();
				}
			}
			
			/**
			 * 计算费用（基本）
			 */
			//议价
			Long negoId =  0L;
			if(groupUserId!=null){
				negoId = this.queryPrNegoId(provCode, cityCode, countyCode, buyerId, groupUserId, subjectType);
				//基础费
				baseMoney = calculateBaseNegoPrice(buyerId, groupUserId, provCode, cityCode, countyCode, subjectType);
				//远程作业费
				remoteMoney = calculateRemoteNegoPrice(distanceDecimal, buyerId, groupUserId, provCode, cityCode, countyCode, subjectType);
			}
			//查询出险地区是否允许自主调价
			boolean isAdjustPrice = this.queryAreaIsAdjustPrice(provCode, cityCode);
			if(baseMoney==null&&isAdjustPrice&&remoteMoney==null){ //自主调价
				//基础费
				Map<String, Object> baseAdjustPriceMap = calculateBaseAdjustPrice(subjectType, sellerId);
				if(baseAdjustPriceMap!=null){
					baseMoney = (BigDecimal) baseAdjustPriceMap.get("adjustedMoney");
					remoteMoney = BigDecimal.ZERO;
					//远程作业费
					if("0".equals(baseAdjustPriceMap.get("isFixedPrice"))){  //不是一口价才计算远程作业费
						remoteMoney = calculateRemoteAdjustPrice(subjectType, sellerId, distanceDecimal);
					}
				}
			}
			if(baseMoney==null&&remoteMoney==null){ //地区指导价
				//基础费
				baseMoney = calculateBaseGuidePrice(provCode, cityCode, subjectType);
				//远程作业费
				remoteMoney = calculateRemoteGuidePrice(provCode, cityCode, subjectType, distanceDecimal);
			}
			
			/**
			 * 计算车童的金额  扣除风险基金（基础费）、团队管理费（详细）、财务费（详细）
			 */
			sellerBaseMoney = baseMoney;
			sellerRemoteMoney =remoteMoney;
			//风险基金
			insuranceMoney = queryCarInsuranceMoney();
			if(insuranceMoney!=null){
				if(sellerBaseMoney!=null&&sellerBaseMoney.compareTo(insuranceMoney)<=0){   //只有当基础费用大于风险基金时才扣风险基金
					insuranceMoney=BigDecimal.ZERO;
				}
				sellerBaseMoney = sellerBaseMoney.subtract(insuranceMoney);
			}
			
			//财务费
			BigDecimal financeRate = queryCarFinanceRate();
			if(financeRate!=null){
				BigDecimal baseFinanceMoney = sellerBaseMoney.multiply(financeRate);  //基础财务费
				BigDecimal remoteFinanceMoney = sellerRemoteMoney.multiply(financeRate); //远程财务费
				financeMoney = baseFinanceMoney.add(remoteFinanceMoney); //财务费
				
				//车童费用扣除财务费
				sellerBaseMoney = sellerBaseMoney.subtract(baseFinanceMoney); 
				sellerRemoteMoney = sellerRemoteMoney.subtract(remoteFinanceMoney);
			}
			
			//团队管理费
			Long groupManageFeeId = 0L;
			if(groupUserId!=null){
				CtGroupManageFeeVO groupManageFee = queryGroupManageMoney(sellerId, groupUserId,buyerId, ServiceId.CAR);
				if(groupManageFee!=null){
					//基础费
					if(groupManageFee.getBaseCommission()!=null){
						baseGroupManageMoney = sellerBaseMoney.multiply(groupManageFee.getBaseCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						sellerBaseMoney = sellerBaseMoney.subtract(baseGroupManageMoney);
					}
					//远程作业费
					if(groupManageFee.getTravelCommission()!=null){
						remoteGroupManageMoney = sellerRemoteMoney.multiply(groupManageFee.getTravelCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						sellerRemoteMoney = sellerRemoteMoney.subtract(remoteGroupManageMoney);
					}
					groupManageFeeId = groupManageFee.getId();
				}
			}
			
			/**
			 * 计算机构的金额  加上通道费（详细）、开票费（详细）
			 */
			//计算通道费
			if(ORG_PRICE.equals(priceType)){
				guideBaseMoney = guideBaseFee;
			}else{
				baseChannelMoney = calculateChannelMoney(baseMoney, provCode, buyerId, ChannelCostType.BASE);
				remoteChannelMoney = calculateChannelMoney(remoteMoney, provCode, buyerId, ChannelCostType.REMOTE);
				//计算开票费
				BigDecimal invoice = queryInvoice(provCode, cityCode, ServiceId.CARGO);
				baseInvoiceMoney = baseMoney.add(baseChannelMoney).divide(BigDecimal.ONE.subtract(invoice), 2, BigDecimal.ROUND_HALF_UP).subtract(baseMoney).subtract(baseChannelMoney);
				remoteInvoiceMoney = remoteMoney.add(remoteChannelMoney).divide(BigDecimal.ONE.subtract(invoice), 2, BigDecimal.ROUND_HALF_UP).subtract(remoteMoney).subtract(remoteChannelMoney);
				
				//计算买家费用
				buyerBaseMoney = baseMoney.add(baseChannelMoney).add(baseInvoiceMoney);
				buyerRemoteMoney = remoteMoney.add(remoteChannelMoney).add(remoteInvoiceMoney);
				
			}
			
			
			
			/**
			 * 封装返回结果
			 */
			resultMap.put("priceType", priceType);
			resultMap.put("groupUserId", groupUserId);
			resultMap.put("guideBaseMoney", guideBaseMoney == null?BigDecimal.ZERO:guideBaseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			resultMap.put("negoId", negoId==null?0:negoId);
			resultMap.put("baseMoney", baseMoney==null?BigDecimal.ZERO:baseMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			resultMap.put("remoteMoney", remoteMoney==null?BigDecimal.ZERO:remoteMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
			resultMap.put("buyerBaseMoney", buyerBaseMoney==null?BigDecimal.ZERO:buyerBaseMoney);
			resultMap.put("buyerRemoteMoney", buyerRemoteMoney==null?BigDecimal.ZERO:buyerRemoteMoney);
			resultMap.put("sellerBaseMoney", sellerBaseMoney==null?BigDecimal.ZERO:sellerBaseMoney);
			resultMap.put("sellerRemoteMoney", sellerRemoteMoney==null?BigDecimal.ZERO:sellerRemoteMoney);
			resultMap.put("baseChannelMoney", baseChannelMoney==null?BigDecimal.ZERO:baseChannelMoney);
			resultMap.put("remoteChannelMoney", remoteChannelMoney==null?BigDecimal.ZERO:remoteChannelMoney);
			resultMap.put("baseInvoiceMoney", baseInvoiceMoney==null?BigDecimal.ZERO:baseInvoiceMoney);
			resultMap.put("remoteInvoiceMoney", remoteInvoiceMoney==null?BigDecimal.ZERO:remoteInvoiceMoney);
			resultMap.put("baseGroupManageMoney", baseGroupManageMoney==null?BigDecimal.ZERO:baseGroupManageMoney);
			resultMap.put("remoteGroupManageMoney", remoteGroupManageMoney==null?BigDecimal.ZERO:remoteGroupManageMoney);
			resultMap.put("insuranceMoney", insuranceMoney==null?BigDecimal.ZERO:insuranceMoney);
			resultMap.put("financeMoney", financeMoney==null?BigDecimal.ZERO:financeMoney);
			resultMap.put("distanceDecimal", distanceDecimal==null?BigDecimal.ZERO:distanceDecimal.setScale(2, BigDecimal.ROUND_HALF_UP));
			resultMap.put("groupManageFeeId", groupManageFeeId);
		} catch (Exception e) {
			log.error("车险订单费用计算错误:",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("车险订单费用计算错误", e);
		}
		
		log.info("UserPriceCalcutorServiceImpl.calculatePrice/计算费用结束:"+resultMap);
		return resultMap;
	}

	/**
	 * 货运险-买家费用计算
	 * @author wufj@chetong.net
	 *         2016年1月5日 下午1:42:26
	 * @param orderFee
	 * @param workProvCode
	 * @param workCityCode
	 * @param buyerId
	 * @return
	 */
	@Override
	public Map<String, BigDecimal> calculateHyBuyerFee(BigDecimal orderFee, String workProvCode, String workCityCode, CtGroupVO buyer) {
		Map<String, BigDecimal> result = new HashMap<String,BigDecimal>();
		Long buyerId = buyer.getUserId();
		/**买家**/
 		//1.通道费
		BigDecimal channelMoney = this.calculateChannelMoney(orderFee, workProvCode, buyerId, ChannelCostType.HY_BASE_FEE, ServiceId.CARGO);
		
		//通道费收取界限设置
		if (channelMoney.compareTo(BigDecimal.valueOf(additionalChannelMin))<0) {
			channelMoney = BigDecimal.valueOf(additionalChannelMin);
		}
		if(channelMoney.compareTo(BigDecimal.valueOf(additionalChannelMax))>0){
			channelMoney = BigDecimal.valueOf(additionalChannelMax);
		}
		
		
		channelMoney = channelMoney==null?BigDecimal.ZERO:channelMoney;
		result.put("channelMoney", channelMoney);
		//2.开票费
		BigDecimal invoiceRate = this.queryInvoice(workProvCode, workCityCode, ServiceId.CARGO);
		BigDecimal invoiceMoney = orderFee.divide(BigDecimal.ONE.subtract(invoiceRate)).subtract(orderFee);
		result.put("invoiceMoney", invoiceMoney);
		//3.作业地机构网络建设费用
		BigDecimal hyWorkPrice = this.queryHyWorkPrice(workProvCode, workCityCode, buyer);
		result.put("workPrice", hyWorkPrice);
		
		//买家费用  订单费用+通道+开票费
		result.put("buyerMoney", orderFee.add(channelMoney).add(invoiceMoney).add(hyWorkPrice==null?BigDecimal.ZERO:hyWorkPrice));
		
		return result;
	}
	
	/**
	 * 查询货运险作业地网络建设费用
	 * @param provCode 作业地省代码
	 * @param cityCode	  作业地市代码
	 * @return
	 */
	private BigDecimal queryHyWorkPrice(String provCode, String cityCode, CtGroupVO buyer){
		//作业地网络建设费不区分本异地单
		HyWorkPriceConfig config = new HyWorkPriceConfig();
		config.setProvCode(provCode);
		if(Config.SPECIAL_CITYS_CODE.contains(cityCode)){
			//如果是特殊市,按市查询
			config.setProvCode(null);
			config.setCityCode(cityCode);
		}
		config = this.commExeSqlDAO.queryForObject("hy_work_price_config.selectByAreaCode", config);
		if(config==null) return BigDecimal.ZERO;
		return config.getWorkPrice()==null?BigDecimal.ZERO:config.getWorkPrice();
	}
	
	/**
	 * 货运险-卖家费用计算
	 * @author wufj@chetong.net
	 *         2016年1月5日 下午1:42:13
	 * @param orderFee
	 * @param userId
	 * @param sellerGroupUserId
	 * @param hasCargo 是否具有货运险资质
	 * @return
	 */
	@Override
	public Map<String, BigDecimal> calculateHySellerFee(BigDecimal orderFee, Long userId, Long sellerGroupUserId, String hasCargo, Long buyerUserId) {
		Map<String, BigDecimal> result = new HashMap<String,BigDecimal>();
		/**车童**/
		BigDecimal sellerMoney = orderFee;
		//1.风险基金
		BigDecimal insuranceMoney = calculateHyInsureFee(sellerMoney, hasCargo);
		sellerMoney = sellerMoney.subtract(insuranceMoney);
		result.put("insuranceMoney", insuranceMoney);
		
		//2.财务费
		BigDecimal financeRate = this.queryPrSetting(PrSettingType.HY_FINANCE);
		BigDecimal financeMoney = sellerMoney.multiply(financeRate);
		sellerMoney = sellerMoney.subtract(financeMoney);
		result.put("financeMoney", financeMoney);
		
		/**团队**/
		//3.团队管理费
		BigDecimal manageMoney = BigDecimal.ZERO; 
		if(sellerGroupUserId!=null){
			CtGroupManageFeeVO groupManageMoney = this.queryGroupManageMoney(userId, sellerGroupUserId, buyerUserId, ServiceId.CARGO);
			if(groupManageMoney!=null){
				BigDecimal cargoCommission = groupManageMoney.getCargoCommission();
				cargoCommission = (cargoCommission==null?BigDecimal.ZERO:cargoCommission);
				manageMoney = sellerMoney.multiply(cargoCommission).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
				sellerMoney = sellerMoney.subtract(manageMoney);
			}
		}
		result.put("manageMoney", manageMoney);
		
		//卖家费用  订单费用-风险基金-财务费-团队管理费
		result.put("sellerMoney", sellerMoney.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		return result;
	}
	
	/**
	 * 计算货运险风险基金
	 * @author wufj@chetong.net
	 *         2016年5月19日 下午2:52:13
	 * @param sellerMoney
	 * @param hasCargo
	 * @return
	 */
	private BigDecimal calculateHyInsureFee(BigDecimal sellerMoney, String hasCargo){
		BigDecimal insuranceMoney = this.queryPrSetting(PrSettingType.HY_INSURANCE);
		if(insuranceMoney!=null&&sellerMoney!=null){
			if("1".equals(hasCargo)){
				//具有货运险资质,只收配置的金额
				if(sellerMoney.compareTo(insuranceMoney)<=0){   //只有当基础费用大于风险基金时才扣风险基金
					insuranceMoney=BigDecimal.ZERO;
				}
			}else{
				//没有货运险资质,收取百分比
				insuranceMoney = sellerMoney.multiply(Config.CAR_2_CARGO_INSURE_RATE);
				insuranceMoney = insuranceMoney.setScale(2, BigDecimal.ROUND_HALF_UP);
			}
		}else{
			insuranceMoney = BigDecimal.ZERO;
		}
		return insuranceMoney;
	}
	
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
	@Override
	public BigDecimal calculateEntrustFee(Long applyUserId, Long grantUserId, String grantType, String serviceId) throws ProcessException{
		//查询出委托关系 
		Map<String,Object> thirdApplyMap = new HashMap<String,Object>();
		thirdApplyMap.put("applyIdA", applyUserId);
		thirdApplyMap.put("grantIdC", grantUserId);
		thirdApplyMap.put("serviceId", serviceId);
		thirdApplyMap.put("grantType", grantType);
		thirdApplyMap.put("status", "2");
		CtThirdApplyInfoVO thirdApplyInfoVO = commExeSqlDAO.queryForObject("third_apply_info.queryThirdApplyInfo", thirdApplyMap);
		if(StringUtil.isNullOrEmpty(thirdApplyInfoVO)){
			throw ProcessCodeEnum.FAIL.buildProcessException("当前用户无此单审批权限！");
		}
		
		BigDecimal auditOrderMoney = BigDecimal.ZERO;
		
		//一级委托 C-->A  A收钱  C付钱
		if("1".equals(thirdApplyInfoVO.getLevel())){
			//审核费用
			String auditMoneyCA = StringUtil.isNullOrEmpty(thirdApplyInfoVO.getC2aFee())?"0":thirdApplyInfoVO.getC2aFee();
			BigDecimal auditFeeCA = new BigDecimal(auditMoneyCA).setScale(2, BigDecimal.ROUND_HALF_UP);
			auditOrderMoney = auditFeeCA;
		}//二级委托 C-->B-->A  C付款给B B付款给A  各种的委托费可以不一样
		else if("2".equals(thirdApplyInfoVO.getLevel())){
			String auditMoneyCB = StringUtil.isNullOrEmpty(thirdApplyInfoVO.getC2bFee())?"0":thirdApplyInfoVO.getC2bFee();
			String auditMoneyBA = StringUtil.isNullOrEmpty(thirdApplyInfoVO.getB2aFee())?"0":thirdApplyInfoVO.getB2aFee();
			BigDecimal auditFeeCB = new BigDecimal(auditMoneyCB).setScale(2, BigDecimal.ROUND_HALF_UP);
			BigDecimal auditFeeBA = new BigDecimal(auditMoneyBA).setScale(2, BigDecimal.ROUND_HALF_UP);
			auditOrderMoney = auditFeeBA;
		}
		return auditOrderMoney;
	}
	
	/**
	 * 查询保证金比例
	 * @author wufj@chetong.net
	 *         2016年1月20日 下午6:21:39
	 * @return
	 */
	public BigDecimal queryCarBondRate(){
		BigDecimal bondRate = BigDecimal.ZERO;
		PrSettingVO bondRateSettingExample = new PrSettingVO();
		bondRateSettingExample.setSettingType("3"); //3-保证金
		List<PrSettingVO> bondRateList = commExeSqlDAO.queryForList("sqlmap_user_price.queryPrSetting",bondRateSettingExample);
		if(bondRateList.size() > 0){
			bondRate =bondRateList.get(0).getSettingValue();
		}
		return  bondRate;
	}

	/** (non-Javadoc)
	 * @Description: 根据费用类型查询相关团队管理费
	 * @param feeParam
	 * @return
	 * @author zhouchushu
	 * @date 2016年2月18日 下午4:51:29
	 * @see net.chetong.order.service.user.UserPriceCalcutorService#queryGroupManageFeeByType(java.util.Map)
	 */
	@Override
	public Map<String, BigDecimal> queryGroupManageFeeByType(Map<String, BigDecimal> feeParam,Long sellerUserId,Long groupUserId, Long buyerUserId) {
		Map<String,BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		if(null == feeParam || 0 == feeParam.size()
				||null == sellerUserId || null == groupUserId){
			return resultMap;
		}
		CtGroupManageFeeVO groupManageFeeVO = this.queryGroupManageMoney(sellerUserId, groupUserId, buyerUserId, ServiceId.CAR);
		for (Entry<String, BigDecimal> entry: feeParam.entrySet()) {
			String costType = entry.getKey();
			BigDecimal money = entry.getValue();
			switch (costType) {
			case Constants.FEE_OVER_TEAM:
				BigDecimal overTeamFee = BigDecimal.ZERO;
				if(!StringUtil.isNullOrEmpty(groupManageFeeVO)){
					//超额附加费
					if(!StringUtil.isNullOrEmpty(groupManageFeeVO.getExtraCommission())){
						overTeamFee = money.multiply(groupManageFeeVO.getExtraCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						resultMap.put(Constants.FEE_OVER_TEAM, overTeamFee);
					}
				}
				break;
			case Constants.FEE_REMOTE_TEAM:
				BigDecimal remoteTeamFee = BigDecimal.ZERO;
				if(!StringUtil.isNullOrEmpty(groupManageFeeVO)){
					//超额附加费
					if(!StringUtil.isNullOrEmpty(groupManageFeeVO.getTravelCommission())){
						remoteTeamFee = money.multiply(groupManageFeeVO.getTravelCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						resultMap.put(Constants.FEE_REMOTE_TEAM, remoteTeamFee);
					}
				}
				break;
			case Constants.FEE_BASE_TEAM:
				BigDecimal baseTeamFee = BigDecimal.ZERO;
				if(!StringUtil.isNullOrEmpty(groupManageFeeVO)){
					//超额附加费
					if(!StringUtil.isNullOrEmpty(groupManageFeeVO.getBaseCommission())){
						baseTeamFee = money.multiply(groupManageFeeVO.getBaseCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						resultMap.put(Constants.FEE_BASE_TEAM, baseTeamFee);
					}
				}
				break;
			default:
				break;
			}
		}
		return resultMap;
	}
	
	/**
	 * 根据id团队管理费
	 * @param feeParam
	 * @param manageId
	 * @return
	 */
	@Override
	public Map<String, BigDecimal> queryGroupManageFeeByManageId(Map<String, BigDecimal> feeParam, String manageId) {
		Map<String,BigDecimal> resultMap = new HashMap<String, BigDecimal>();
		if(null == feeParam || 0 == feeParam.size()){
			return resultMap;
		}
		CtGroupManageFeeVO groupManageFeeVO = this.commExeSqlDAO.queryForObject("sqlmap_user_price.queryCtGroupManageFeeByKey", Long.valueOf(manageId));
		for (Entry<String, BigDecimal> entry: feeParam.entrySet()) {
			String costType = entry.getKey();
			BigDecimal money = entry.getValue();
			switch (costType) {
			case Constants.FEE_OVER_TEAM:
				BigDecimal overTeamFee = BigDecimal.ZERO;
				if(!StringUtil.isNullOrEmpty(groupManageFeeVO)){
					//超额附加费
					if(!StringUtil.isNullOrEmpty(groupManageFeeVO.getExtraCommission())){
						overTeamFee = money.multiply(groupManageFeeVO.getExtraCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						resultMap.put(Constants.FEE_OVER_TEAM, overTeamFee);
					}
				}
				break;
			case Constants.FEE_REMOTE_TEAM:
				BigDecimal remoteTeamFee = BigDecimal.ZERO;
				if(!StringUtil.isNullOrEmpty(groupManageFeeVO)){
					//超额附加费
					if(!StringUtil.isNullOrEmpty(groupManageFeeVO.getTravelCommission())){
						remoteTeamFee = money.multiply(groupManageFeeVO.getTravelCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						resultMap.put(Constants.FEE_REMOTE_TEAM, remoteTeamFee);
					}
				}
				break;
			case Constants.FEE_BASE_TEAM:
				BigDecimal baseTeamFee = BigDecimal.ZERO;
				if(!StringUtil.isNullOrEmpty(groupManageFeeVO)){
					//超额附加费
					if(!StringUtil.isNullOrEmpty(groupManageFeeVO.getBaseCommission())){
						baseTeamFee = money.multiply(groupManageFeeVO.getBaseCommission()).divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
						resultMap.put(Constants.FEE_BASE_TEAM, baseTeamFee);
					}
				}
				break;
			default:
				break;
			}
		}
		return resultMap;
	}

	/** (non-Javadoc)
	 * @Description: 查询结算方式
	 * @param buyerId
	 * @param adCode
	 * @return
	 * @author zhouchushu
	 * @date 2016年5月30日 上午11:00:21
	 * @see net.chetong.order.service.user.UserPriceCalcutorService#checkPriceType(java.lang.String, java.lang.String)
	 */
	@Override
	public Map<String, Object> checkPriceType(Long buyerId, String adCode) throws Exception{
		
		try {
			Map<String,Object> priceInfo = new HashMap<String,Object>();
			 
			//区域码转换
			String workProvCode = adCode.substring(0,2)+"0000";
			String workCityCode = adCode.substring(0,4)+"00";
			String workCountyCode = null;
			//如果百度地图传的市编码,则加01
			if(workCityCode.equals(adCode)){
				workCountyCode = adCode.substring(0,4)+"01";
			}else{
				workCountyCode = adCode;
			}
			
			
			//查询买家
			CtGroupVO buyer = commExeSqlDAO.queryForObject("ct_group.queryByUserId", buyerId);
			//查询是否异地单
			boolean isOtherPlaceOrder = commonService.queryIsOtherPlaceOrder(buyer, workProvCode, workCityCode);
			priceInfo.put("isRemote", isOtherPlaceOrder?"1":"0");
			
			if(false == isOtherPlaceOrder){
				priceInfo.put("priceType", "0");
				
				CtTakePaymentVO ctTakePayment = takePaymentService.queryCtTakePayment(buyer.getUserId(), ServiceId.CAR);
				if(null != ctTakePayment && null != ctTakePayment.getPayerUserId() && ctTakePayment.getPayerUserId() > 0){
					priceInfo.put("payerUserId", ctTakePayment.getPayerUserId());
				}else{
					priceInfo.put("payerUserId", buyerId);
				}
				
				return priceInfo;
			}
			
			
			
			//判断派单机构是否设置了作业地结算
			
			//查询作业地分公司
			CtUserLevelVO subOrg = querySubOrgByWorkPlace(buyer,workProvCode,workCityCode);
			if(null != subOrg){
				String isWorkPrice = subOrg.getIsWorkPrice();
				if("1".equals(isWorkPrice)){
					//有分公司则按作业地结算
					//查询支付方
					priceInfo.put("priceType", "2");
					CtGroupVO newBuyer = commExeSqlDAO.queryForObject("ct_group.queryByUserId", subOrg.getUserId());
					priceInfo.put("buyerUserId", subOrg.getUserId());
					priceInfo.put("buyerUserName", newBuyer.getOrgName());
					priceInfo.put("buyerMobile", newBuyer.getConnTel1());
					priceInfo.put("isRemote", "0");
					CtTakePaymentVO ctTakePayment = takePaymentService.queryCtTakePayment(subOrg.getUserId(), ServiceId.CAR);
					if(null != ctTakePayment && null != ctTakePayment.getPayerUserId() && ctTakePayment.getPayerUserId() > 0){
						priceInfo.put("payerUserId", ctTakePayment.getPayerUserId());
					}else{
						priceInfo.put("payerUserId", subOrg.getUserId());
					}
					return priceInfo;
				}
			}
				
			
			//查询指导价基础价
			Map<String,Object> guidePriceInfo = queryGuidePriceInfo(workProvCode,workCityCode,workCountyCode,GUIDE_PRICE_TYPE_BASEFEE,"1","1");
			if(null == guidePriceInfo){
				priceInfo.put("priceType", "0");
				CtTakePaymentVO ctTakePayment = takePaymentService.queryCtTakePayment(buyer.getUserId(), ServiceId.CAR);
				if(null != ctTakePayment && null != ctTakePayment.getPayerUserId() && ctTakePayment.getPayerUserId() > 0){
					priceInfo.put("payerUserId", ctTakePayment.getPayerUserId());
				}else{
					priceInfo.put("payerUserId", buyerId);
				}
				return priceInfo;
			}
			
			//新结算方式
			priceInfo.put("priceType", "1");
			priceInfo.put("guidePriceInfo", guidePriceInfo);
			CtTakePaymentVO ctTakePayment = takePaymentService.queryCtTakePayment(buyer.getUserId(), ServiceId.CAR);
			if(null != ctTakePayment && null != ctTakePayment.getPayerUserId() && ctTakePayment.getPayerUserId() > 0){
				priceInfo.put("payerUserId", ctTakePayment.getPayerUserId());
			}else{
				priceInfo.put("payerUserId", buyerId);
			}
			
			return priceInfo;
		} catch (Exception e) {
			log.error("查询订单结算方式错误：",e);
			throw e;
			
		}
	}

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
	public Map<String, Object> queryGuidePriceInfo(String workProvCode, String workCityCode, String workCountyCode,String guidePriceType,String serviceId,String state) throws Exception {
		Map<String,Object> priceInfoMap = new HashMap<String,Object>();
		//查询指导费信息
		PrGuidePriceInfoVO priceInfo = queryGuidePrice(workProvCode,workCityCode,workCountyCode,serviceId,state);
		if(null == priceInfo){
			return null;
		}
		if(guidePriceType.equals(GUIDE_PRICE_TYPE_BASEFEE)){
			if(null == priceInfo.getFirstPrice()){
				return null;
			}
			BigDecimal guideBaseFee = priceInfo.getFirstPrice();
			priceInfoMap.put("guideBaseFee", guideBaseFee);
		}else if(guidePriceType.equals(GUIDE_PRICE_TYPE_ADD_FEE)){
			if(null == priceInfo.getAppendPrice()){
				return null;
			}
			BigDecimal guideAddFeePCT = priceInfo.getAppendPrice();
			priceInfoMap.put("guideAddFeePCT", guideAddFeePCT);
		}else if(guidePriceType.equals(GUIDE_PRICE_TYPE_OVER_FEE)){
			List<PrGuidePriceDetailVO> guideOverFeeList = commExeSqlDAO.queryForList("sqlmap_pr_guide_price_detail.selectByGuideId", priceInfo.getId());
			priceInfoMap.put("guideOverFeeList", guideOverFeeList);
		}else{
			return null;
		}
		
		CtGroupVO ctGroupVO = queryWorkPlaceManageOrg(workProvCode, workCityCode);
		
		priceInfoMap.put("orgUserId", ctGroupVO.getUserId());
		return priceInfoMap;
		
	}

	/**
	 * @Description: 查询作业地车险管理机构
	 * @param workProvCode
	 * @param workCityCode
	 * @return
	 * @return CtGroupVO
	 * @author zhouchushu
	 * @date 2016年6月7日 下午4:35:02
	 */
	public CtGroupVO queryWorkPlaceManageOrg(String workProvCode, String workCityCode) {
		//查询作业地车险机构
		List<CtGroupVO> manageOrgsTemp = null;
		CtGroupVO ctGroupParam = new CtGroupVO();
		ctGroupParam.setProvCode(workProvCode);
		ctGroupParam.setIsManageOrg("1");
		manageOrgsTemp = commExeSqlDAO.queryForList("ct_group.queryCtGroup", ctGroupParam);

		if(null == manageOrgsTemp||0 == manageOrgsTemp.size()){
			throw ProcessCodeEnum.NO_ORG_ERR.buildProcessException("("+workProvCode+","+workCityCode+")作业地无车险机构！！");
		}
		
		List<CtGroupVO> manageOrgs = new ArrayList<CtGroupVO>();
		for (CtGroupVO ctGroup : manageOrgsTemp) {
			//如果是特殊城市
			if(spAreas.contains(workCityCode)){
				if(workCityCode.equals(ctGroup.getCityCode())){
					manageOrgs.add(ctGroup);
				}
			}else if(workProvCode.equals(ctGroup.getProvCode())){
				if(!spAreas.contains(ctGroup.getCityCode())){
					manageOrgs.add(ctGroup);
				}
			}
		}
		if(null == manageOrgs||1 != manageOrgs.size()){
			throw ProcessCodeEnum.NO_ORG_ERR.buildProcessException("("+workProvCode+","+workCityCode+")作业地无车险机构或多个！！！");
		}
		
		CtGroupVO ctGroupVO = manageOrgs.get(0);
		return ctGroupVO;
	}
	

	/**
	 * @Description: 查询指导价信息
	 * @param workProvCode
	 * @param workCityCode
	 * @param workCountyCode
	 * @return
	 * @return BigDecimal
	 * @author zhouchushu
	 * @date 2016年5月31日 上午11:08:29
	 */
	public PrGuidePriceInfoVO queryGuidePrice(String workProvCode, String workCityCode, String workCountryCode,String serviceId,String state) {
		Map<String,String> params = new HashMap<String,String>();
		params.put("provCode", workProvCode);
		params.put("cityCode", workCityCode);
		params.put("countryCode", workCountryCode);
		params.put("serviceId", serviceId);
		if("1".equals(state)){
			params.put("state", state);
		}
		PrGuidePriceInfoVO guidePriceInfoVO = commExeSqlDAO.queryForObject("sqlmap_pr_guide_price_info.queryGuidePrice", params);
		return guidePriceInfoVO;
	}

	/**
	 * @Description: 查询作业地机构
	 * @param buyer
	 * @param workProvCode
	 * @param workCityCode
	 * @return
	 * @return CtGroupVO
	 * @author zhouchushu
	 * @date 2016年5月30日 下午4:37:42
	 */
	public CtUserLevelVO querySubOrgByWorkPlace(CtGroupVO buyer, String workProvCode, String workCityCode) {
		List<CtUserLevelVO> broOrgs = commExeSqlDAO.queryForList("sqlmap_ct_user_level.queryBroOrg", buyer.getUserId());
		//市机构
		for (CtUserLevelVO broOrg : broOrgs) {
			if(workCityCode.equals(broOrg.getExt2())){
				return broOrg;
			}
		}
		//省机构
		for (CtUserLevelVO broOrg : broOrgs) {
			if(workProvCode.equals(broOrg.getExt1())&&StringUtils.isBlank(broOrg.getExt2())){
				return broOrg;
			}
		}
		
		return null;
	}

	/**
	 * @Description: 查询机构的结算信息
	 * @param buyer
	 * @return
	 * @return boolean
	 * @author zhouchushu
	 * @date 2016年5月30日 下午2:33:30
	 */
	public boolean checkIsWorkPrice(CtGroupVO buyer) {
		CtUserLevelVO ctUserLevelVO = commExeSqlDAO.queryForObject("sqlmap_ct_user_level.selectByUserId", buyer.getUserId());
		if(null == ctUserLevelVO){
			return false;
		}
		
		if(StringUtils.isBlank(ctUserLevelVO.getIsWorkPrice())){
			return false;
		}
		
		String isWorkPrice = ctUserLevelVO.getIsWorkPrice();
		
		if("1".equals(isWorkPrice)){
			return true;
		}else{
			return false;
		}
		
	}
	
	
}
