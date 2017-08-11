package net.chetong.order.service.user;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.CtPersonGroupVO;
import net.chetong.order.model.CtUserVO;
import net.chetong.order.model.HyMyEntrustModel;
import net.chetong.order.model.HyOrderTaskVO;
import net.chetong.order.model.MyEntrustQueryPeopleVO;
import net.chetong.order.model.PdServiceChannelTaxVO;
import net.chetong.order.model.PrRuleInfoVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.common.GroupService;
import net.chetong.order.util.BaiduGeocodingUtil;
import net.chetong.order.util.Config;
import net.chetong.order.util.ConstantMap;
import net.chetong.order.util.Constants;
import net.chetong.order.util.DistanceCompute;
import net.chetong.order.util.DistanceComputer;
import net.chetong.order.util.PersonMoneyComparator;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.ctenum.AreaType;
import net.chetong.order.util.ctenum.ChannelCostType;
import net.chetong.order.util.ctenum.ServiceId;
import net.chetong.order.util.exception.ProcessException;

/**
 * 用户
 * @author wufeng@chetong.net
 * @creation 2015年11月4日
 */
@Service("userService")
public class UserServiceImpl extends BaseService implements UserService{
	private static Logger log = LogManager.getLogger(UserServiceImpl.class);
	
	public static final String ORG_PRICE = "1";
	public static final String OLD_PRICE = "0";
	public static final String WORK_PRICE = "2";
	
	@Resource
	private CommonService commonService;
	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;
	@Resource
	private GroupService GroupService;
	
	@Value("${query_hy_ct_decimal_inc}")
	private double QUERY_CT_HY_DECIMAL_INC;
	
	@Value("${query_ct_decimal_inc}")
	private double QUERY_CT_DECIMAL_INC;
	
	/**
	 * 查询用户的团队信息根据用户id
	 * @author wufj@chetong.net
	 *         2015年12月2日 下午4:27:10
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public CtGroupVO queryUserGroupByUserId(Long userId) {
		CtPersonGroupVO personGroupParams = new CtPersonGroupVO();
		personGroupParams.setUserId(Long.valueOf(userId));
		personGroupParams.setExt1("2"); // 个人加入团队
		personGroupParams.setStat("1"); // 已加入
		CtPersonGroupVO personGroup = commExeSqlDAO.queryForObject("ct_person_group.queryCtPersonGroup",
				personGroupParams);
		if (null == personGroup){
			return null;
		}
		
		Long groupId = personGroup.getGroupId();

		// 查询ct_group
		CtGroupVO ctGroupParams = new CtGroupVO();
		ctGroupParams.setId(groupId);
		CtGroupVO ctGroup = commExeSqlDAO.queryForObject("ct_group.queryByKey", ctGroupParams);
		return ctGroup;
//		return commExeSqlDAO.queryForObject("sqlmap_user.queryUserGroupByUserId", userId);
	}
	
	/**
	 * 查询当前真实登录用户根据useid
	 * @author wufj@chetong.net
	 *         2015年12月7日 上午10:36:18
	 * @param userId 用户id
	 * @return  如果是车童个人账号，直接返回车童用户信息
	 *                如果是团队或者机构主账号，直接返回
	 *                如果是团队或者机构子账号，返回主账号信息
	 */
	public CtUserVO queryCurRealUser(Long userId){
		CtUserVO user = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
		if(user==null){
			return null;
		}
		if("0".equals(user.getUserType())){						//个人
			return user;
		}else{
			if("0".equals(user.getIsSub())){						//机构主账户
				return user;
			}else{														//机构子账户
				CtUserVO ctUserVO = new CtUserVO();
				ctUserVO.setId(user.getPid());
				return this.commExeSqlDAO.queryForObject("sqlmap_user.queryUser", ctUserVO);
			}
		}
	}
	
	/**
	 * 查询机构团队总账户
	 * @author wufj@chetong.net
	 *         2016年1月21日 上午10:33:30
	 * @param userId
	 * @return
	 */
	public CtGroupVO queryTopGroup(Long userId){
		CtUserVO curRealUser = this.queryCurRealUser(userId);
		return commExeSqlDAO.queryForObject("sqlmap_user.queryUserGroupByUserId", Long.valueOf(curRealUser.getId()));
	}
	
	/**
	 * 查询用户信息
	 * @author wufj@chetong.net
	 *         2015年12月7日 上午10:50:09
	 * @param userId 用户id
	 * @return
	 */
	@Override
	public CtUserVO queryCtUserByKey(String userId){
		return this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", userId);
	}
	
	/**
	 * 查询车童列表（车险）
	 */
	//@Override
	public List<MyEntrustQueryPeopleVO> queryCtUserListWithSend_old(ModelMap modelMap) throws ProcessException{
		log.info("UserServiceImpl.queryCtUserListWithSend/获取车童列表开始:"+modelMap);
		try {
			String buyerId = (String) modelMap.get("buyerId");
			//是否是泛华总账户
			boolean isFanHua = false;
			CtUserVO param = new CtUserVO();
			param.setPid("11025");
			param.setId(buyerId);
			CtUserVO userVO = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUser", param);
			if("11025".equals(buyerId)||userVO!=null){
				isFanHua = true;
			}
			
			double longitude = Double.valueOf(StringUtil.trimToNull(modelMap.get("longitude")));//经度
			double latitude = Double.valueOf(StringUtil.trimToNull(modelMap.get("latitude")));//纬度
			String provDesc = (String)modelMap.get("provDesc");
			String cityDesc = (String)modelMap.get("cityDesc");
			Object areaDesc = modelMap.get("areaDesc");
			
			String grantUserId = null;//委托人id
			if(modelMap.get("grantUserId")!=null){
				grantUserId = (String)modelMap.get("grantUserId");
			}
			
			//是否授予一次性调解
			String isAllowMediation = String.valueOf(modelMap.get("isAllowMediation"));
			if(StringUtil.isNullOrEmpty(isAllowMediation)){
				isAllowMediation = "0";  //0：否，1：是
				modelMap.put("isAllowMediation",Integer.parseInt(isAllowMediation));
			}
			
			//查询出险地省市县
			String workProvCode = null;
			String workCityCode = null;
			String workCountyCode = null;
			//查询省
			workProvCode = commonService.getAreaCodeByAreaName(provDesc);
			if(workProvCode != null){ //查询市
				workCityCode = commonService.getAreaCodeByAreaName(cityDesc,AreaType.CITY,workProvCode);
				if(workCityCode != null&&!StringUtil.isNullOrEmpty(areaDesc)){ //查询县区
					workCountyCode = commonService.getAreaCodeByAreaName(areaDesc.toString(), AreaType.AREA, workCityCode);
				}
			}
			
			if(StringUtil.isNullOrEmpty(workProvCode)||StringUtil.isNullOrEmpty(workCityCode)){
				throw ProcessCodeEnum.FAIL.buildProcessException("省市代码为空");
			}
			
			//获取真正的买家信息：自己或主账户或委托人
			buyerId = getBuyerInfo(buyerId, grantUserId);
			modelMap.put("buyerId", buyerId);
			
			//查询出险地与买家议价的团队id和议价信息id
			String subjectType = (String) modelMap.get("subjectType");
			List<Map<String, Object>> teamNegoList = this.queryWorkTeamNegoPriceList(workProvCode,workCityCode,workCountyCode,buyerId,subjectType);
			
			//返回的结果数据
			List<MyEntrustQueryPeopleVO> ctUserList = new ArrayList<>();
			
			//与买家有关的配置
			BigDecimal invoiceRate = userPriceCalcutorService.queryInvoice(workProvCode, workCityCode, ServiceId.CAR);//开票费率
			PdServiceChannelTaxVO baseChannelRule = userPriceCalcutorService.queryServiceChannelTax(workProvCode, Long.valueOf(buyerId), ChannelCostType.BASE); //基础通道费设置
			PdServiceChannelTaxVO remoteChannelRule = userPriceCalcutorService.queryServiceChannelTax(workProvCode, Long.valueOf(buyerId), ChannelCostType.REMOTE);//远程通道费设置
			
			//查询议价团队下所有的车童（议价车童）
			for (int i = 0; i < teamNegoList.size(); i++) {
				Map<String, Object> negoPriceInfo = teamNegoList.get(i);
				//查询加入车童的信息
				if(StringUtil.isNullOrEmpty(negoPriceInfo.get("groupId"))){
					continue;
				}
				modelMap.put("groupId", negoPriceInfo.get("groupId"));
				List<MyEntrustQueryPeopleVO> teamCtUserList = commExeSqlDAO.queryForList("sqlmap_user.queryCtUserListWithSend", modelMap);
				
				//如果团队下没有车童，循环下一个团队议价信息
				if(teamCtUserList.size() <= 0){
					continue;
				}
				/**
				 * 计算费用   优先级：团队议价->车童自主报价->地区指导价
				 */
				//计算基础议价，每个团队下的所有车童基础议价会相同，算一次
				Long negoId = (Long) negoPriceInfo.get("negoId");
				BigDecimal baseMoney = userPriceCalcutorService.calculateBaseNegoPrice(negoId);
				//计算基础通道费
				BigDecimal baseChannelMoney = userPriceCalcutorService.calculateChannelMoney(baseChannelRule, baseMoney);
				//计算基础开票费
				BigDecimal baseInvoiceMoney =  baseMoney.add(baseChannelMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(baseMoney).subtract(baseChannelMoney);
				//计算机构看到的基础费
				baseMoney = baseChannelMoney==null?baseMoney:baseMoney.add(baseChannelMoney);
				baseMoney = baseInvoiceMoney==null?baseMoney:baseMoney.add(baseInvoiceMoney);
				
				//计算远程议价，并设置到返回的车童信息中
				for (int j = 0; j < teamCtUserList.size(); j++) {
					MyEntrustQueryPeopleVO peopleVO = teamCtUserList.get(j);
					//调用百度计算距离,计算远程费用需要
					double baiduDistance = DistanceComputer.baiduHttpComput(longitude, latitude, peopleVO.getPersonLongitude(), peopleVO.getPersonLatitude());
					BigDecimal distanceDecimal = new BigDecimal(baiduDistance);
					distanceDecimal = distanceDecimal.divide(new BigDecimal("1000"), 1, BigDecimal.ROUND_HALF_UP);
					//获取距离出错，无法计算远程作业费，排除
					if(distanceDecimal.compareTo(new BigDecimal("9999")) == 0){
						teamCtUserList.remove(j);
						j--;
						continue;
					}
					peopleVO.setDistance(distanceDecimal);
					
					//查询计算远程议价，议价和车童的距离有关，每个车童不一样
					BigDecimal remoteMoney = userPriceCalcutorService.calculateRemoteNegoPrice(distanceDecimal, negoId);
					//计算远程通道费
					BigDecimal remoteChannelMoney = userPriceCalcutorService.calculateChannelMoney(remoteChannelRule, remoteMoney);
					//计算远程开票费
					BigDecimal remoteInvoiceMoney =  remoteMoney.add(remoteChannelMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(remoteMoney).subtract(remoteChannelMoney);
					//计算机构看到的费用
					remoteMoney = remoteChannelMoney==null?remoteMoney:remoteMoney.add(remoteChannelMoney);
					remoteMoney = remoteInvoiceMoney==null?remoteMoney:remoteMoney.add(remoteInvoiceMoney);
					
					peopleVO.setNegoId(peopleVO.getNegoId());
					peopleVO.setIsNego("1"); //设置为议价车童
					peopleVO.setBaseMoney(baseMoney); //设置基础费
					peopleVO.setRemoteMoney(remoteMoney); //设置远程作业费
					peopleVO.setTotalMoney(baseMoney.add(remoteMoney)); //设置总金额
					
					//如果是调度总账户，显示全名
					if(isFanHua){
						peopleVO.setLastName(peopleVO.getLastName()+peopleVO.getFirstName());
						peopleVO.setIsScheduler("1");
					}
				}
				//将车童添加到返回列表中
				ctUserList.addAll(teamCtUserList);
			}
			
			//议价车童不足要求个数 用非议价补
			if(ctUserList.size() < Config.QUERY_CT_COUNT){ 
				//还差的车童个数
				int num = Config.QUERY_CT_COUNT-ctUserList.size();
				//查询非议价的剩余个数的车童
				List<MyEntrustQueryPeopleVO> chetongTopTenList = this.queryCtTopN(modelMap, ctUserList, num);
				 
				//查询出险地区是否允许自主调价
				boolean isAdjustPrice = userPriceCalcutorService.queryAreaIsAdjustPrice(workProvCode, workCityCode);
				
				/**
				 * 计算非议价车童金额
				 */
				//查询区域价格指导，地区基础指导价与出险地有关 ，全部车童相同
				PrRuleInfoVO queryPrRuleInfo = userPriceCalcutorService.queryPrRuleInfo(workProvCode, workCityCode, subjectType);
				BigDecimal baseGuidMoney = null;
				//计算基础指导价
				if(queryPrRuleInfo!=null){
					baseGuidMoney = userPriceCalcutorService.calculateBaseGuidePrice(queryPrRuleInfo.getId());
				}
				
				//分别计算远程作业费或自主调价
				for(int i = 0; i < chetongTopTenList.size(); i++){
					BigDecimal baseMoney = null;
					BigDecimal remoteMoney = null;
					MyEntrustQueryPeopleVO peopleVO = chetongTopTenList.get(i);
					//调用百度接口 查询驾车距离 计算远程作业费用
					double baiduDistance = DistanceComputer.baiduHttpComput(longitude, latitude, peopleVO.getPersonLongitude(), peopleVO.getPersonLatitude());
					BigDecimal distanceDecimal = new BigDecimal(baiduDistance);
					distanceDecimal = distanceDecimal.divide(new BigDecimal("1000"), 1, BigDecimal.ROUND_HALF_UP);
					if(distanceDecimal.compareTo(new BigDecimal("9999")) == 0){
						chetongTopTenList.remove(i);
						i--;
						continue;
					}
					peopleVO.setDistance(distanceDecimal);
					//自主调价
					if(isAdjustPrice){ //如果出险地区允许自主调价
						//计算车童的自主调价基础费
						Map<String, Object> baseAdjustPriceMap = userPriceCalcutorService.calculateBaseAdjustPrice(subjectType, peopleVO.getUserId());
						if(baseAdjustPriceMap!=null){
							baseMoney = (BigDecimal) baseAdjustPriceMap.get("adjustedMoney");
							if("1".equals((String)baseAdjustPriceMap.get("isFixedPrice"))){  //是否一口价
								peopleVO.setIsFixedPrice("1");
								remoteMoney = BigDecimal.ZERO;
							}else{ //非一口价，才计算远程附加费
								peopleVO.setIsFixedPrice("0");
								//计算车童的自主调价远程作业费
								remoteMoney = userPriceCalcutorService.calculateRemoteAdjustPrice(subjectType, peopleVO.getUserId(),distanceDecimal);
							}
						}
						
						
					}
					
					//没有自主调价，看地区指导价
					if(baseMoney==null||remoteMoney==null){ 
						if(queryPrRuleInfo!=null){  //地区指导价也为空，三个价格信息都没有，移除
							//设置基础费
							baseMoney = baseGuidMoney;
							//计算远程指导价
							remoteMoney = userPriceCalcutorService.calculateRemoteGuidePrice(queryPrRuleInfo.getId(), distanceDecimal);
						}
					}
					
					//三种价格模式都没有
					if(baseMoney==null||remoteMoney==null){
						log.warn("该用户没有价格信息："+peopleVO.getUserId());
						chetongTopTenList.remove(i);
						i--;
						continue;
					}
					
					//计算基础通道费
					BigDecimal baseChannelMoney = userPriceCalcutorService.calculateChannelMoney(baseChannelRule, baseMoney);
					//计算基础开票费
					BigDecimal baseInvoiceMoney =  baseMoney.add(baseChannelMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(baseMoney).subtract(baseChannelMoney);
					//计算机构看到的费用
					baseMoney = baseChannelMoney==null?baseMoney:baseMoney.add(baseChannelMoney);
					baseMoney = baseInvoiceMoney==null?baseMoney:baseMoney.add(baseInvoiceMoney);
					
					//计算远程通道费
					BigDecimal remoteChannelMoney = userPriceCalcutorService.calculateChannelMoney(remoteChannelRule, remoteMoney);
					//计算远程开票费
					BigDecimal remoteInvoiceMoney =  remoteMoney.add(remoteChannelMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(remoteMoney).subtract(remoteChannelMoney);
					//计算机构看到的费用
					remoteMoney = remoteChannelMoney==null?remoteMoney:remoteMoney.add(remoteChannelMoney);
					remoteMoney = remoteInvoiceMoney==null?remoteMoney:remoteMoney.add(remoteInvoiceMoney);
					
					peopleVO.setIsNego("0");//设置为非议价车童
					peopleVO.setBaseMoney(baseMoney); // 基础费
					peopleVO.setRemoteMoney(remoteMoney);//远程作业费
					peopleVO.setTotalMoney(baseMoney.add(remoteMoney)); //总金额
					
					//如果是调度总账户，显示全名
					if(isFanHua){
						peopleVO.setLastName(peopleVO.getLastName()+peopleVO.getFirstName());
						peopleVO.setIsScheduler("1");
					}
				}
				//将非议价的车童加入到列表中去
				ctUserList.addAll(chetongTopTenList);
			}
			
			//按服务费排序
			if("1".equals(modelMap.get("serviceMoneyOrder"))){
				Collections.sort(ctUserList, new PersonMoneyComparator());
			}
			
			log.info("UserServiceImpl.queryCtUserListWithSend/获取车童列表结束:"+ctUserList.size());
			return ctUserList;
		} catch (Exception e) {
			log.error("UserController.queryCtUserListWithSend查询车童出错",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("UserController.queryCtUserListWithSend查询车童出错:",e);
		}
	}
	
	@Override
	public List<MyEntrustQueryPeopleVO> queryCtUserListWithSend(ModelMap modelMap) throws Exception{
		log.info("UserServiceImpl.queryCtUserListWithSend/获取车童列表开始:"+modelMap);
		try {
			String buyerId = (String) modelMap.get("buyerId");
			//是否是泛华总账户
			boolean isFanHua = false;
			CtUserVO param = new CtUserVO();
			param.setPid("11025");
			param.setId(buyerId);
			CtUserVO userVO = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUser", param);
			if("11025".equals(buyerId)||userVO!=null){
				isFanHua = true;
			}
			
			String longitude = StringUtil.trimToNull(modelMap.get("longitude"));//经度
			String latitude = StringUtil.trimToNull(modelMap.get("latitude"));//纬度
			
			//百度adCode
			String adCode = BaiduGeocodingUtil.getAdCode(longitude, latitude);
			if(StringUtils.isBlank(adCode)){
				throw ProcessCodeEnum.FAIL.buildProcessException("百度经纬度查询编码错误："+longitude+":"+latitude);
			}
			String provDesc = (String)modelMap.get("provDesc");
			String cityDesc = (String)modelMap.get("cityDesc");
			Object areaDesc = modelMap.get("areaDesc");
			
			String grantUserId = null;//委托人id
			if(modelMap.get("grantUserId")!=null){
				grantUserId = (String)modelMap.get("grantUserId");
			}
			
			//查询出险地省市县
			String workProvCode = null;
			String workCityCode = null;
			String workCountyCode = null;
			
			workProvCode = adCode.substring(0,2)+"0000";
			workCityCode = adCode.substring(0,4)+"00";
			//如果百度地图传的市编码,则加01
			if(workCityCode.equals(adCode)){
				workCountyCode = adCode.substring(0,4)+"01";
			}else{
				workCountyCode = adCode;
			}
			
			
			//获取真正的买家信息：自己或主账户或委托人
			buyerId = getBuyerInfo(buyerId, grantUserId);
			modelMap.put("buyerId", buyerId);
			
			//查询结算信息
			Map<String,Object> priceInfo = userPriceCalcutorService.checkPriceType(Long.valueOf(buyerId),adCode);
			
			String priceType = (String) priceInfo.get("priceType");
			
			BigDecimal guideBaseFee = null;
			if(ORG_PRICE.equals(priceType)){
				//查询指导价通道费
				Map<String,Object> guidePriceInfo = (Map<String, Object>) priceInfo.get("guidePriceInfo");
				guideBaseFee = (BigDecimal) guidePriceInfo.get("guideBaseFee");
			}else if(WORK_PRICE.equals(priceType)){
				Long buyerUserId = (Long) priceInfo.get("buyerUserId");
				buyerId = buyerUserId.toString();
			}
			
			
			//查询出险地与买家议价的团队id和议价信息id
			String subjectType = (String) modelMap.get("subjectType");
			List<Map<String, Object>> teamNegoList = this.queryWorkTeamNegoPriceList(workProvCode,workCityCode,workCountyCode,buyerId,subjectType);
			
			//返回的结果数据
			List<MyEntrustQueryPeopleVO> ctUserList = new ArrayList<>();
			
			//与买家有关的配置
			BigDecimal invoiceRate = userPriceCalcutorService.queryInvoice(workProvCode, workCityCode, ServiceId.CAR);//开票费率
			PdServiceChannelTaxVO baseChannelRule = userPriceCalcutorService.queryServiceChannelTax(workProvCode, Long.valueOf(buyerId), ChannelCostType.BASE); //基础通道费设置
			PdServiceChannelTaxVO remoteChannelRule = userPriceCalcutorService.queryServiceChannelTax(workProvCode, Long.valueOf(buyerId), ChannelCostType.REMOTE);//远程通道费设置
			
			//查询议价团队下所有的车童（议价车童）
			for (int i = 0; i < teamNegoList.size(); i++) {
				Map<String, Object> negoPriceInfo = teamNegoList.get(i);
				//查询加入车童的信息
				modelMap.put("groupId", negoPriceInfo.get("groupId"));
				List<MyEntrustQueryPeopleVO> teamCtUserList = commExeSqlDAO.queryForList("sqlmap_user.queryCtUserListWithSend", modelMap);
				
				//如果团队下没有车童，循环下一个团队议价信息
				if(teamCtUserList.size() <= 0){
					continue;
				}
				
				//计算基础议价，每个团队下的所有车童基础议价会相同，算一次
				Long negoId = (Long) negoPriceInfo.get("negoId");
				
				BigDecimal baseMoney = userPriceCalcutorService.calculateBaseNegoPrice(negoId);
				//车童议价
				BigDecimal ctMoney = baseMoney;
				
				if(!ORG_PRICE.equals(priceType)){
					//计算基础通道费
					BigDecimal baseChannelMoney = userPriceCalcutorService.calculateChannelMoney(baseChannelRule, baseMoney);
					//计算基础开票费
					BigDecimal baseInvoiceMoney =  baseMoney.add(baseChannelMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(baseMoney).subtract(baseChannelMoney);
					//计算机构看到的基础费
					baseMoney = baseChannelMoney==null?baseMoney:baseMoney.add(baseChannelMoney);
					baseMoney = baseInvoiceMoney==null?baseMoney:baseMoney.add(baseInvoiceMoney);
				}
				
				for (int j = 0; j < teamCtUserList.size(); j++) {
					MyEntrustQueryPeopleVO peopleVO = teamCtUserList.get(j);
					peopleVO.setCtMoney(ctMoney);
					peopleVO.setBaseMoney(baseMoney); //设置基础费
					if(ORG_PRICE.equals(priceType)){
						peopleVO.setGuideBaseMoney(guideBaseFee);
					}
					peopleVO.setIsNego("1");
					peopleVO.setNegoId(negoId);
				}
				//将车童添加到返回列表中
				ctUserList.addAll(teamCtUserList);
			}
			
			//议价车童不足要求个数 用非议价补
			boolean isAdjustPrice = false;
			BigDecimal baseGuidMoney = null;
			PrRuleInfoVO queryPrRuleInfo = null;
			if(ctUserList.size() < Config.QUERY_CT_COUNT){ 
				//还差的车童个数
				int num = Config.QUERY_CT_COUNT-ctUserList.size();
				//查询非议价的剩余个数的车童
				List<MyEntrustQueryPeopleVO> chetongTopTenList = this.queryCtTopN(modelMap, ctUserList, num);
				
				//查询出险地区是否允许自主调价
				isAdjustPrice = userPriceCalcutorService.queryAreaIsAdjustPrice(workProvCode, workCityCode);
				
				//查询区域价格指导，地区基础指导价与出险地有关 ，全部车童相同
				queryPrRuleInfo = userPriceCalcutorService.queryPrRuleInfo(workProvCode, workCityCode, subjectType);
				//计算基础指导价
				if(queryPrRuleInfo!=null){
					baseGuidMoney = userPriceCalcutorService.calculateBaseGuidePrice(queryPrRuleInfo.getId());
				}
				//将非议价的车童加入到列表中去
				ctUserList.addAll(chetongTopTenList);
			}
			
			if(ctUserList.size()>400){
				log.error("警告，查询车童过多");
			}
			
			//计算驾车距离
			DistanceCompute.compute(ctUserList,longitude,latitude);
			
			for (int i = 0; i < ctUserList.size(); i++) {
				MyEntrustQueryPeopleVO peopleVO = ctUserList.get(i);
				if("1".equals(peopleVO.getIsNego())){
					if(ORG_PRICE.equals(priceType)){
						remoteNegoPrice(peopleVO, peopleVO.getNegoId(), peopleVO.getGuideBaseMoney());
					}else{
						remoteNegoPrice(peopleVO, peopleVO.getNegoId(), remoteChannelRule, invoiceRate, peopleVO.getBaseMoney());
					}
					peopleVO.setIsNego("1");
				}else{
					if(ORG_PRICE.equals(priceType)){
						notRemoteNegoPrice(peopleVO, isAdjustPrice, subjectType, baseGuidMoney, queryPrRuleInfo,guideBaseFee);
					}else{
						notRemoteNegoPrice(peopleVO, isAdjustPrice, subjectType, baseGuidMoney, queryPrRuleInfo, baseChannelRule, invoiceRate, remoteChannelRule);
					}
					peopleVO.setIsNego("0");
				}
				//如果是调度总账户，显示全名
				if(isFanHua){
					peopleVO.setLastName(peopleVO.getLastName()+peopleVO.getFirstName());
					peopleVO.setIsScheduler("1");
				}
				if("9999000".equals(peopleVO.getDistance().toString())){
					continue;
				}
			}
			
			//按服务费排序
			if("1".equals(modelMap.get("serviceMoneyOrder"))){
				Collections.sort(ctUserList, new PersonMoneyComparator());
			}
			
			log.info("UserServiceImpl.queryCtUserListWithSend/获取车童列表结束:"+ctUserList.size());
			return ctUserList;
		} catch (Exception e) {
			log.error("UserController.queryCtUserListWithSend查询车童出错",e);
			throw e;
		}
	}
	
	/**
	 * 多线程使用计算远程议价
	 * @author wufj@chetong.net
	 *         2016年3月14日 下午6:21:32
	 * @param peopleVO
	 * @param negoId
	 * @param remoteChannelRule
	 * @param invoiceRate
	 * @param baseMoney
	 */
	private void remoteNegoPrice(MyEntrustQueryPeopleVO peopleVO, Long negoId, PdServiceChannelTaxVO remoteChannelRule, BigDecimal invoiceRate, BigDecimal baseMoney){
		//查询计算远程议价，议价和车童的距离有关，每个车童不一样
		BigDecimal remoteMoney = userPriceCalcutorService.calculateRemoteNegoPrice(peopleVO.getDistance(), negoId);
		peopleVO.setCtMoney(peopleVO.getCtMoney().add(remoteMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
		//计算远程通道费
		BigDecimal remoteChannelMoney = userPriceCalcutorService.calculateChannelMoney(remoteChannelRule, remoteMoney);
		//计算远程开票费
		BigDecimal remoteInvoiceMoney =  remoteMoney.add(remoteChannelMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(remoteMoney).subtract(remoteChannelMoney);
		//计算机构看到的费用
		remoteMoney = remoteChannelMoney==null?remoteMoney:remoteMoney.add(remoteChannelMoney);
		remoteMoney = remoteInvoiceMoney==null?remoteMoney:remoteMoney.add(remoteInvoiceMoney);
		
		peopleVO.setRemoteMoney(remoteMoney); //设置远程作业费
		peopleVO.setTotalMoney(baseMoney.add(remoteMoney)); //设置总金额
	}
	
	/**
	 * 多线程使用计算远程议价
	 * @author wufj@chetong.net
	 *         2016年3月14日 下午6:21:32
	 * @param peopleVO
	 * @param negoId
	 * @param remoteChannelRule
	 * @param invoiceRate
	 * @param baseMoney
	 */
	private void remoteNegoPrice(MyEntrustQueryPeopleVO peopleVO, Long negoId, BigDecimal baseMoney){
		//查询计算远程议价，议价和车童的距离有关，每个车童不一样
		BigDecimal remoteMoney = userPriceCalcutorService.calculateRemoteNegoPrice(peopleVO.getDistance(), negoId);
		//计算机构看到的费用
		peopleVO.setRemoteMoney(remoteMoney); //设置远程作业费
		peopleVO.setTotalMoney(baseMoney); //设置总金额
		peopleVO.setCtMoney(peopleVO.getCtMoney().add(remoteMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
	}
	
	/**
	 * 多线程使用计算非议价远程作业费
	 * @author wufj@chetong.net
	 *         2016年3月14日 下午6:21:56
	 * @param peopleVO
	 * @param isAdjustPrice
	 * @param subjectType
	 * @param baseGuidMoney
	 * @param queryPrRuleInfo
	 * @param baseChannelRule
	 * @param invoiceRate
	 * @param remoteChannelRule
	 */
	private void notRemoteNegoPrice(MyEntrustQueryPeopleVO peopleVO, boolean isAdjustPrice, String subjectType, BigDecimal baseGuidMoney, PrRuleInfoVO queryPrRuleInfo, PdServiceChannelTaxVO baseChannelRule, BigDecimal invoiceRate, PdServiceChannelTaxVO remoteChannelRule){
		BigDecimal baseMoney = null;
		BigDecimal remoteMoney = null;
		BigDecimal distanceDecimal = peopleVO.getDistance();
		
		//如果出险地区允许自主调价
		if(isAdjustPrice){
			//计算车童的自主调价基础费
			Map<String, Object> baseAdjustPriceMap = userPriceCalcutorService.calculateBaseAdjustPrice(subjectType, peopleVO.getUserId());
			if(baseAdjustPriceMap!=null){
				baseMoney = (BigDecimal) baseAdjustPriceMap.get("adjustedMoney");
				if("1".equals((String)baseAdjustPriceMap.get("isFixedPrice"))){  //是否一口价
					peopleVO.setIsFixedPrice("1");
					remoteMoney = BigDecimal.ZERO;
				}else{ //非一口价，才计算远程附加费
					peopleVO.setIsFixedPrice("0");
					//计算车童的自主调价远程作业费
					remoteMoney = userPriceCalcutorService.calculateRemoteAdjustPrice(subjectType, peopleVO.getUserId(),distanceDecimal);
				}
			}
		}
		
		//没有自主调价，看地区指导价
		if(baseMoney==null||remoteMoney==null){
			if(queryPrRuleInfo!=null){  //地区指导价也为空，三个价格信息都没有，移除
				//设置基础费
				baseMoney = baseGuidMoney;
				//计算远程指导价
				remoteMoney = userPriceCalcutorService.calculateRemoteGuidePrice(queryPrRuleInfo.getId(), distanceDecimal);
			}
		}
		
		//三种定价模式都没有，返回0
		if(baseMoney==null||remoteMoney==null){
			log.warn("该用户没有价格信息："+peopleVO.getUserId());
			baseMoney = BigDecimal.ZERO;
			//计算远程指导价
			remoteMoney = BigDecimal.ZERO;
		}
		peopleVO.setCtMoney(baseMoney.add(remoteMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
		
		//计算基础通道费
		BigDecimal baseChannelMoney = userPriceCalcutorService.calculateChannelMoney(baseChannelRule, baseMoney);
		//计算基础开票费
		BigDecimal baseInvoiceMoney =  baseMoney.add(baseChannelMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(baseMoney).subtract(baseChannelMoney);
		//计算机构看到的费用
		baseMoney = baseChannelMoney==null?baseMoney:baseMoney.add(baseChannelMoney);
		baseMoney = baseInvoiceMoney==null?baseMoney:baseMoney.add(baseInvoiceMoney);
		
		//计算远程通道费
		BigDecimal remoteChannelMoney = userPriceCalcutorService.calculateChannelMoney(remoteChannelRule, remoteMoney);
		//计算远程开票费
		BigDecimal remoteInvoiceMoney =  remoteMoney.add(remoteChannelMoney).divide(BigDecimal.ONE.subtract(invoiceRate), 2, BigDecimal.ROUND_HALF_UP).subtract(remoteMoney).subtract(remoteChannelMoney);
		//计算机构看到的费用
		remoteMoney = remoteChannelMoney==null?remoteMoney:remoteMoney.add(remoteChannelMoney);
		remoteMoney = remoteInvoiceMoney==null?remoteMoney:remoteMoney.add(remoteInvoiceMoney);
		
		peopleVO.setIsNego("0");//设置为非议价车童
		peopleVO.setBaseMoney(baseMoney); // 基础费
		peopleVO.setRemoteMoney(remoteMoney);//远程作业费
		peopleVO.setTotalMoney(baseMoney.add(remoteMoney)); //总金额
	}
	
	
	private void notRemoteNegoPrice(MyEntrustQueryPeopleVO peopleVO, boolean isAdjustPrice, String subjectType, BigDecimal baseGuidMoney, PrRuleInfoVO queryPrRuleInfo,BigDecimal guideBaseFee){
		BigDecimal baseMoney = null;
		BigDecimal remoteMoney = null;
		BigDecimal distanceDecimal = peopleVO.getDistance();
		
		//如果出险地区允许自主调价
		if(isAdjustPrice){
			//计算车童的自主调价基础费
			Map<String, Object> baseAdjustPriceMap = userPriceCalcutorService.calculateBaseAdjustPrice(subjectType, peopleVO.getUserId());
			if(baseAdjustPriceMap!=null){
				baseMoney = (BigDecimal) baseAdjustPriceMap.get("adjustedMoney");
				if("1".equals((String)baseAdjustPriceMap.get("isFixedPrice"))){  //是否一口价
					peopleVO.setIsFixedPrice("1");
					remoteMoney = BigDecimal.ZERO;
				}else{ //非一口价，才计算远程附加费
					peopleVO.setIsFixedPrice("0");
					//计算车童的自主调价远程作业费
					remoteMoney = userPriceCalcutorService.calculateRemoteAdjustPrice(subjectType, peopleVO.getUserId(),distanceDecimal);
				}
			}
		}
		
		//没有自主调价，看地区指导价
		if(baseMoney==null||remoteMoney==null){
			if(queryPrRuleInfo!=null){  //地区指导价也为空，三个价格信息都没有，移除
				//设置基础费
				baseMoney = baseGuidMoney;
				//计算远程指导价
				remoteMoney = userPriceCalcutorService.calculateRemoteGuidePrice(queryPrRuleInfo.getId(), distanceDecimal);
			}
		}
		
		//三种定价模式都没有，返回0
		if(baseMoney==null||remoteMoney==null){
			log.warn("该用户没有价格信息："+peopleVO.getUserId());
			baseMoney = BigDecimal.ZERO;
			//计算远程指导价
			remoteMoney = BigDecimal.ZERO;
		}
		
		
		peopleVO.setIsNego("0");//设置为非议价车童
		peopleVO.setGuideBaseMoney(guideBaseFee);
		peopleVO.setBaseMoney(baseMoney); // 基础费
		peopleVO.setRemoteMoney(remoteMoney);//远程作业费
		peopleVO.setTotalMoney(guideBaseFee); //总金额
		peopleVO.setCtMoney(baseMoney.add(remoteMoney).setScale(2, BigDecimal.ROUND_HALF_UP));
	}
	
	/**
	 * 查询出险地的议价团队的议价信息
	 * @param workProvCode 省code
	 * @param workCityCode 市code
	 * @param workCountyCode 区code
	 * @param buyeId 买家id
	 * @param subjectType 服务类型（1-查勘 2-定损 3-其他）
	 * @return 议价信息
	 */
	private List<Map<String, Object>> queryWorkTeamNegoPriceList(String workProvCode,String workCityCode,String workCountyCode,String buyeId,String subjectType){
		log.info("查询买家的出险地的议价团队的议价信息buyid:"+buyeId);
		log.info("-------------查人议价查询-------------------------------->provCode"+workProvCode);
		log.info("-------------查人议价查询-------------------------------->cityCode"+workCityCode);
		log.info("-------------查人议价查询-------------------------------->countyCode"+workCountyCode);
		log.info("-------------查人议价查询-------------------------------->buyerUserId"+buyeId);
		log.info("-------------查人议价查询-------------------------------->subjectType"+subjectType);
		
		List<Map<String, Object>> queryWorkTeamCtUserIdNegoPriceList = new ArrayList<Map<String, Object>>();
		//查询出险地(省市县)的团队议价信息 （通过议价关联）
		if(!StringUtil.isNullOrEmpty(workCountyCode)){
			Map<String, String> queryTeamParamsMap = new HashMap<String, String>();
			queryTeamParamsMap.put("provCode", workProvCode);
			queryTeamParamsMap.put("cityCode", workCityCode);
			queryTeamParamsMap.put("countyCode", workCountyCode);
			queryTeamParamsMap.put("buyerUserId", buyeId);
			queryTeamParamsMap.put("subjectType", subjectType);//服务类型（1-查勘 2-定损 3-其他）
			queryWorkTeamCtUserIdNegoPriceList = this.commExeSqlDAO.queryForList("sqlmap_user.queryWorkTeamCtUserIdNegoPriceList", queryTeamParamsMap);
		}
		
		//查询市的团队议价信息
		List<Map<String, Object>> queryWorkTeamCtUserIdNegoPriceListTemp1 = new ArrayList<Map<String, Object>>(); 
		Map<String, String> queryCityTeamParamsMap = new HashMap<String, String>();
		queryCityTeamParamsMap.put("provCode", workProvCode);
		queryCityTeamParamsMap.put("cityCode", workCityCode);
		queryCityTeamParamsMap.put("countyCode", "000000");
		queryCityTeamParamsMap.put("buyerUserId", buyeId);
		queryCityTeamParamsMap.put("subjectType", subjectType);//服务类型（1-查勘 2-定损 3-其他）
		queryWorkTeamCtUserIdNegoPriceListTemp1 = this.commExeSqlDAO.queryForList("sqlmap_user.queryWorkTeamCtUserIdNegoPriceList", queryCityTeamParamsMap);
		log.info("查询买家在出险地非默认的议价团队list大小"+queryWorkTeamCtUserIdNegoPriceListTemp1.size());
		queryWorkTeamCtUserIdNegoPriceList.addAll(queryWorkTeamCtUserIdNegoPriceListTemp1);
		
		//查询县的默认议价信息
		if(!StringUtil.isNullOrEmpty(workCountyCode)){
			List<Map<String, Object>> queryWorkTeamCtUserIdNegoPriceListTemp2 = new ArrayList<Map<String, Object>>(); 
			Map<String, String> queryTeamDefaultParamsMap = new HashMap<String, String>();
			queryTeamDefaultParamsMap.put("provCode", workProvCode);
			queryTeamDefaultParamsMap.put("cityCode", workCityCode);
			queryTeamDefaultParamsMap.put("countyCode", workCountyCode);
			queryTeamDefaultParamsMap.put("subjectType", subjectType);//服务类型（1-查勘 2-定损 3-其他）
			queryTeamDefaultParamsMap.put("isDefault", "1"); //1-默认
			queryWorkTeamCtUserIdNegoPriceListTemp2 = this.commExeSqlDAO.queryForList("sqlmap_user.queryWorkTeamCtUserIdNegoPriceList", queryTeamDefaultParamsMap);
			queryWorkTeamCtUserIdNegoPriceList.addAll(queryWorkTeamCtUserIdNegoPriceListTemp2);
		}
		
		//查询市的默认议价信息
		List<Map<String, Object>> queryWorkTeamCtUserIdNegoPriceListTemp3 = new ArrayList<Map<String, Object>>(); 
		Map<String, String> queryTeamCityDefaultParamsMap = new HashMap<String, String>();
		queryTeamCityDefaultParamsMap.put("provCode", workProvCode);
		queryTeamCityDefaultParamsMap.put("cityCode", workCityCode);
		queryTeamCityDefaultParamsMap.put("countyCode", "000000");
		queryTeamCityDefaultParamsMap.put("subjectType", subjectType);//服务类型（1-查勘 2-定损 3-其他）
		queryTeamCityDefaultParamsMap.put("isDefault", "1"); //1-默认
		queryWorkTeamCtUserIdNegoPriceListTemp3 = this.commExeSqlDAO.queryForList("sqlmap_user.queryWorkTeamCtUserIdNegoPriceList", queryTeamCityDefaultParamsMap);
		queryWorkTeamCtUserIdNegoPriceList.addAll(queryWorkTeamCtUserIdNegoPriceListTemp3);
		
		//筛选重复 非默认优先
		Map<Long,Map<String, Object>> priceTemp = new HashMap<Long,Map<String, Object>>();
		for (Map<String, Object> map : queryWorkTeamCtUserIdNegoPriceList) {
			Long groupUserId = (Long) map.get("groupUserId");
			String isDefault = (String) map.get("isDefault");
			if(!priceTemp.containsKey(groupUserId)){
				priceTemp.put(groupUserId, map);
			}
			if(priceTemp.containsKey(groupUserId)&&"0".equals(isDefault)){
				priceTemp.put(groupUserId, map);
			}
		}
		
		List<Map<String, Object>> queryWorkTeamCtUserIdNegoPriceFinal = new ArrayList<Map<String, Object>>();
		for(Entry<Long, Map<String, Object>> e :priceTemp.entrySet()){
			queryWorkTeamCtUserIdNegoPriceFinal.add(e.getValue());
		}
		
		log.info("查询买家在出险地的议价信息list大小:"+queryWorkTeamCtUserIdNegoPriceFinal.size());
		return queryWorkTeamCtUserIdNegoPriceFinal;
	}
	
	/**
	 * 根据派单人id获取真正的买家信息
	 * @param userId 派单人id（登录PC的用户id）
	 * @param grantUserId 委托人id（登录PC的用户id）
	 * @return 买家信息
	 */
	@Override
	public String getBuyerInfo(String userId, String grantUserId){
		String buyerId = userId;
		log.info("根据派单人id获取真正的买家信息:"+userId);
		//如果是委托下单 则买家为委托方
		if(StringUtils.isNotBlank(grantUserId)&&!"0".equals(grantUserId)){
			buyerId = grantUserId;
		}
		//查询派单人信息
		CtUserVO user = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUserByKey", buyerId);
		//如果派单账号为子账号，查询父账号
		if(user != null){
			buyerId = user.getId();
			if("1".equals(user.getIsSub())){ //子账户
				buyerId = user.getPid();
			}
		}
		log.info("真的买家为："+buyerId);
		return buyerId;
	} 
	
	/**
	 * 查询非议价的前N个车童
	 * @param modelMap 查询条件
	 * @param pList 需要排除的议价车童
	 * @param num 查询数量
	 * @return 车童信息列表
	 */
	private List<MyEntrustQueryPeopleVO> queryCtTopN(ModelMap modelMap, List<MyEntrustQueryPeopleVO> pList, int num){
		log.info("需查询非议价的车童:"+num);
		String exceptChetong = "0";
		for(int i = 0; pList != null && i < pList.size(); i++){
			exceptChetong += "," + pList.get(i).getUserId();
		}
		modelMap.put("exceptChetong", exceptChetong);
		modelMap.put("num", num);
		
		double longSubt=Double.valueOf(modelMap.get("longitude").toString());
		double longPlus=Double.valueOf(modelMap.get("longitude").toString());
	
		double latitudeSubt=Double.valueOf(modelMap.get("latitude").toString());
		double latitudePlus=Double.valueOf(modelMap.get("latitude").toString());
		
		List<MyEntrustQueryPeopleVO> list=null;
		for (int j = 1; j <= Config.QUERY_CT_LOOP; j++) {
			//经度的每次增量
			longSubt=longSubt-Config.QUERY_CT_DECIMAL_INC;
			longPlus=longPlus+Config.QUERY_CT_DECIMAL_INC;
			//纬度的每次增量
			latitudeSubt=latitudeSubt-Config.QUERY_CT_DECIMAL_INC;
			latitudePlus=latitudePlus+Config.QUERY_CT_DECIMAL_INC;
			//维度递减传值到form
			modelMap.put("latitudeSubt", latitudeSubt);
			modelMap.put("latitudePlus", latitudePlus);
			//经度递减传值到form
			modelMap.put("longitudeSubt", longSubt);
			modelMap.put("longitudePlus", longPlus);
			//查询车童
			list = commExeSqlDAO.queryForList("sqlmap_user.queryCtUserListWithSendLimitN", modelMap);
			//如果车童人数>=10跳出返回车童信息
			if(list.size()>=num){
				break;
			}
		}
		log.info("已查询非议价的车童:"+list.size());
		return list;
	}
	
	/**
	 * 派单查询车童列表（货运险）
	 * @author wufj@chetong.net
	 *         2016年1月4日 上午9:35:15
	 * @param modelMap
	 * @return
	 * @throws ProcessException
	 */
	@Override
	public List<MyEntrustQueryPeopleVO> queryHyUserListWithSend(ModelMap modelMap) throws ProcessException {
		log.info("UserServiceImpl.queryHyUserListWithSend/获取车童列表开始:"+modelMap);
		try {
			String buyerId = (String) modelMap.get("buyerId");
			//是否是泛华账户
			boolean isFanHua = false;
			CtUserVO param = new CtUserVO();
			param.setPid("11025");
			param.setId(buyerId);
			CtUserVO userVO = this.commExeSqlDAO.queryForObject("sqlmap_user.queryUser", param);
			if("11025".equals(buyerId)||userVO!=null){
				isFanHua = true;
			}
			
			HyOrderTaskVO hyTask = this.commExeSqlDAO.queryForObject("sqlmap_hy_order_task.selectByPrimaryKey", modelMap.get("taskId"));
			BigDecimal realMoney = hyTask.getRealMoney();
			modelMap.put("num", Config.QUERY_CT_COUNT);
			modelMap.put("serviceId", ServiceId.CARGO.getValue());
			String longitude = modelMap.get("longitude").toString();
			String latitude = modelMap.get("latitude").toString();
			double longitudeSubt = Double.valueOf(longitude)-QUERY_CT_HY_DECIMAL_INC;
			double longitudePlus = Double.valueOf(longitude)+QUERY_CT_HY_DECIMAL_INC;
			double latitudeSubt = Double.valueOf(latitude)-QUERY_CT_HY_DECIMAL_INC;
			double latitudePlus = Double.valueOf(latitude)+QUERY_CT_HY_DECIMAL_INC;
			modelMap.put("longitudeSubt", longitudeSubt);
			modelMap.put("longitudePlus", longitudePlus);
			modelMap.put("latitudeSubt", latitudeSubt);
			modelMap.put("latitudePlus", latitudePlus);
			
			//车童列表
			List<MyEntrustQueryPeopleVO> ctUserList = new ArrayList<MyEntrustQueryPeopleVO>();
			//查询货运险车童
			List<MyEntrustQueryPeopleVO> hYCtUserList = commExeSqlDAO.queryForList("sqlmap_user.queryHyUserListWithSend", modelMap);
			log.warn("查询货运险车童数量:"+hYCtUserList.size());
			
			//将货运险车童加入车童列表
			ctUserList.addAll(hYCtUserList);
			//货运险车童数量不足则查询车险车童
			if(ctUserList.size() < Config.QUERY_CT_COUNT){
				//查询车险车童
				longitudeSubt = Double.valueOf(longitude)-QUERY_CT_DECIMAL_INC;
				longitudePlus = Double.valueOf(longitude)+QUERY_CT_DECIMAL_INC;
				latitudeSubt = Double.valueOf(latitude)-QUERY_CT_DECIMAL_INC;
				latitudePlus = Double.valueOf(latitude)+QUERY_CT_DECIMAL_INC;
				modelMap.put("serviceId", ServiceId.CAR.getValue());
				modelMap.put("longitudeSubt", longitudeSubt);
				modelMap.put("longitudePlus", longitudePlus);
				modelMap.put("latitudeSubt", latitudeSubt);
				modelMap.put("latitudePlus", latitudePlus);
				modelMap.put("num", Config.QUERY_CT_COUNT-ctUserList.size());
				modelMap.put("exceptChetong", hYCtUserList);
				List<MyEntrustQueryPeopleVO> carCtUserList = commExeSqlDAO.queryForList("sqlmap_user.queryHyUserListWithSend", modelMap);
				log.warn("查询非货运险车童数量："+carCtUserList.size());
				ctUserList.addAll(carCtUserList);
			}
			
			if(200<ctUserList.size()){
				log.warn("异常参数:");
				throw ProcessCodeEnum.FAIL.buildProcessException("查询车童异常，查询过多不正常");
			}
			
			//计算驾车距离
			DistanceCompute.compute(ctUserList, longitude, latitude);
			
			for (int i = 0; i < ctUserList.size(); i++) {
				MyEntrustQueryPeopleVO peopleVO = ctUserList.get(i);
				peopleVO.setTotalMoney(realMoney);
				//如果是调度总账户，显示全名
				if(isFanHua){
					peopleVO.setLastName(peopleVO.getLastName()+peopleVO.getFirstName());
					peopleVO.setIsScheduler("1");
				}
			}
			return ctUserList;
		} catch (Exception e) {
			log.error("货运险查询车童出错",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险查询车童出错",e);
		}
	}

	@Transactional
	public void updateUserMoney(String userId, BigDecimal realTotalMoney) {

		CtUserVO userVO = this.queryCtUserByKey(userId);
		BigDecimal userMoney = (new BigDecimal(userVO.getUserMoney())).subtract(realTotalMoney);
		BigDecimal availableMoney = (new BigDecimal(userVO.getAvailableMoney()).subtract(realTotalMoney));

		userVO.setUserMoney(String.valueOf(userMoney));
		userVO.setAvailableMoney(String.valueOf(availableMoney));

		this.commExeSqlDAO.updateVO("sqlmap_user.updateByKeyNotNull", userVO);
	}
	
	/**
	 * 货运险订单导出excel
	 * 我的委托 机构
	 * 
	 */
	@Override
	public void exportMyEntrustHYForGroup(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
			throws ProcessException {
		log.info("导出我的委托列表开始:"+modelMap.get("userId"));
		try {
			//查询当前登陆人信息
			CtUserVO user = this.queryCurRealUser(Long.valueOf(modelMap.get("userId").toString()));
			modelMap.put("userId", user.getId());
			String helpAudit = (String)modelMap.get("helpAudit");
			String beSended = (String)modelMap.get("beSended");
			String helpSend = (String)modelMap.get("helpSend");
			
			if(StringUtils.isBlank(helpSend)){
				helpSend = "0";
			}
			if(StringUtils.isBlank(beSended)){
				beSended = "0";
			}
			if(StringUtils.isBlank(helpAudit)){
				helpAudit = "0";
			}
			if("1".equals(helpAudit)||"1".equals(beSended)||"1".equals(helpSend)){
				modelMap.put("showEntrust", 1);
			}
			List<HyMyEntrustModel> list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyEntrustToExcelExportHYForGroup", modelMap);
			
			//获取导出模板
			ClassPathResource resource = new ClassPathResource("/templates/myWorkingZHCXListTemplatesHY.xls");
			
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
			
			HSSFWorkbook workbook = new HSSFWorkbook(resource.getInputStream());
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			for(int i = 0; i < list.size(); i++){
				HSSFRow row = sheet.createRow(i + 1);
				String caseNo = list.get(i).getCaseNo();
				//报案号	
				HSSFCell caseNoCell = row.createCell(0);
				caseNoCell.setCellValue(caseNo);
				//订单编号
				HSSFCell orderNoCell = row.createCell(1);
				orderNoCell.setCellValue(list.get(i).getOrderNo());
				//订单类型 1派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回 09审核通过
				
				//买家账号
				HSSFCell buyerLoginNameCell = row.createCell(2);
				buyerLoginNameCell.setCellValue(list.get(i).getBuyerLoginName());
				//买家名称
				HSSFCell buyerNameCell = row.createCell(3);
				buyerNameCell.setCellValue(list.get(i).getBuyerName());
				//买家电话
				HSSFCell buyerMobileCell = row.createCell(4);
				buyerMobileCell.setCellValue(list.get(i).getBuyerMobile());
				//服务人姓名
				HSSFCell sellerCell = row.createCell(5);
				String sex = list.get(i).getSellerSex();
				if(null!=list.get(i).getSellerName()){
					if("0".equals(sex)){
						sellerCell.setCellValue(list.get(i).getSellerName()+"先生");	
					}else if("1".equals(sex)){
						sellerCell.setCellValue(list.get(i).getSellerName()+"女士");					
					}else{
						sellerCell.setCellValue(list.get(i).getSellerName()+"先生/女士");								
					}
				}else{
					sellerCell.setCellValue("--");	
				}
				//服务人电话
				HSSFCell sellerMobileCell = row.createCell(6);
				sellerMobileCell.setCellValue(list.get(i).getSellerMobile());
				//服务类型
				HSSFCell serviceCell = row.createCell(7);
				serviceCell.setCellValue("货运公估");
				//合约委托人名字	
				HSSFCell entrustNameCell = row.createCell(8);
				entrustNameCell.setCellValue(list.get(i).getEntrustName());
				//合约委托人电话
				HSSFCell entrustMobileCell = row.createCell(9);
				entrustMobileCell.setCellValue(list.get(i).getEntrustMobile());	
				
				//出险地点 
				HSSFCell acccidentAddressCell = row.createCell(10);
				acccidentAddressCell.setCellValue(list.get(i).getAccidentAddress());
				//接单地点
				HSSFCell ctAddressCell = row.createCell(11);
				ctAddressCell.setCellValue(list.get(i).getCtAddress());
				//派单时间
				HSSFCell getTimeCell = row.createCell(12);
				getTimeCell.setCellValue(sdf.format(list.get(i).getGetTime()));
				//审核人
				HSSFCell auditName = row.createCell(13);
				if(null!=list.get(i).getAuditName()){
					auditName.setCellValue(list.get(i).getAuditName());
				}else{
					auditName.setCellValue("--");
				}
				//订单状态 00派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回 09审核通过
				HSSFCell dealStatCell = row.createCell(14);
				String dealStatLabel = ConstantMap.getDealStatLabel(list.get(i).getDealStat());
				dealStatCell.setCellValue(dealStatLabel);
			
				//买家支付金额
				HSSFCell buyerMoneyCell = row.createCell(15);
				if(null!=list.get(i).getBuyerMoney()){
					buyerMoneyCell.setCellValue(Double.parseDouble(list.get(i).getBuyerMoney()));
				}else{
					buyerMoneyCell.setCellValue("--");
				}
				//卖家支付金额
				HSSFCell sellerMoneyCell = row.createCell(16);
				if(null!=list.get(i).getSellerMoney()){
					sellerMoneyCell.setCellValue(Double.parseDouble(list.get(i).getSellerMoney()));
				}else{
					sellerMoneyCell.setCellValue("--");
				}
				//团队获得佣金
				HSSFCell groupMoneyCell = row.createCell(17);
				if(null!=list.get(i).getGroupMoney()){
					groupMoneyCell.setCellValue(Double.parseDouble(list.get(i).getGroupMoney()));
				}else{
					groupMoneyCell.setCellValue("--");
				}			
			}
			
			long date = new Date().getTime();
			String fileName = "hy_buyer_entrust_export_"+sdf.format(date)+".xls";
			
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.flush();
			out.close();	
		} catch (Exception e) {
			Object userId = modelMap.get("userId");
			log.error("导出我的委托列表异常userId:"+userId,e);
			ProcessCodeEnum.FAIL.buildProcessException("导出我的委托列表异常userId："+userId,e);
		}
		
	}

	/**
	 * 货运险订单导出excel（按报案号）
	 * 我的委托机构
	 * 
	 */
	@Override
	public void exportMyEntrustWithCaseNoHYForGroup(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
			throws ProcessException {
		log.info("======================= 导出我的委托列表(报案号) 开始========================="+modelMap.get("userId"));
		try {
			//查询当前登陆人信息
			CtUserVO user = this.queryCurRealUser(Long.valueOf(modelMap.get("userId").toString()));
			modelMap.put("userId", user.getId());
			String helpAudit = (String)modelMap.get("helpAudit");
			String beSended = (String)modelMap.get("beSended");
			String helpSend = (String)modelMap.get("helpSend");
			
			if(StringUtils.isBlank(helpSend)){
				helpSend = "0";
			}
			if(StringUtils.isBlank(beSended)){
				beSended = "0";
			}
			if(StringUtils.isBlank(helpAudit)){
				helpAudit = "0";
			}
			if("1".equals(helpAudit)||"1".equals(beSended)||"1".equals(helpSend)){
				modelMap.put("showEntrust", 1);
			}
			List<HyMyEntrustModel> list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyEntrustToExcelExportHYForGroup", modelMap);
			
			//获取导出模板
			ClassPathResource resource = new ClassPathResource("/templates/myWorkingZHCXListTemplatesHY.xls");
			
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
			
			HSSFWorkbook workbook = new HSSFWorkbook(resource.getInputStream());
			HSSFSheet sheet = workbook.getSheetAt(0);
			

			String lastCaseNo = null; //重复报案号
			int last = 1;   //重复报案号最后一个单元行号
			
			for(int i = 0; i < list.size(); i++){
				HSSFRow row = sheet.createRow(i + 1);
				String caseNo = list.get(i).getCaseNo();
				if(0==i){
					lastCaseNo = caseNo;
				}
				//案件号
				HSSFCell caseNoCell = row.createCell(0);
				caseNoCell.setCellValue(caseNo);
				if( null != lastCaseNo && !lastCaseNo.isEmpty() && !lastCaseNo.equals(caseNo) && 0 != i){
					sheet.addMergedRegion(new CellRangeAddress(last, i, 0, 0));
					last = i+1;
					lastCaseNo = caseNo;
				}
				if( null != lastCaseNo && !lastCaseNo.isEmpty() && i == list.size()-1){
					sheet.addMergedRegion(new CellRangeAddress(last, i+1, 0, 0));
				}
				//订单编号
				HSSFCell orderNoCell = row.createCell(1);
				orderNoCell.setCellValue(list.get(i).getOrderNo());
				//订单类型 1派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回 09审核通过
				
				//买家账号
				HSSFCell buyerLoginNameCell = row.createCell(2);
				buyerLoginNameCell.setCellValue(list.get(i).getBuyerLoginName());
				//买家名称
				HSSFCell buyerNameCell = row.createCell(3);
				buyerNameCell.setCellValue(list.get(i).getBuyerName());
				//买家电话
				HSSFCell buyerMobileCell = row.createCell(4);
				buyerMobileCell.setCellValue(list.get(i).getBuyerMobile());
				//服务人姓名
				HSSFCell sellerCell = row.createCell(5);
				String sex = list.get(i).getSellerSex();
				if(null!=list.get(i).getSellerName()){
					if("0".equals(sex)){
						sellerCell.setCellValue(list.get(i).getSellerName()+"先生");	
					}else if("1".equals(sex)){
						sellerCell.setCellValue(list.get(i).getSellerName()+"女士");					
					}else{
						sellerCell.setCellValue(list.get(i).getSellerName()+"先生/女士");								
					}
				}else{
					sellerCell.setCellValue("--");	
				}
				//服务人电话
				HSSFCell sellerMobileCell = row.createCell(6);
				sellerMobileCell.setCellValue(list.get(i).getSellerMobile());
				//服务类型
				HSSFCell serviceCell = row.createCell(7);
				serviceCell.setCellValue("货运公估");
				//合约委托人名字	
				HSSFCell entrustNameCell = row.createCell(8);
				entrustNameCell.setCellValue(list.get(i).getEntrustName());
				//合约委托人电话
				HSSFCell entrustMobileCell = row.createCell(9);
				entrustMobileCell.setCellValue(list.get(i).getEntrustMobile());	
				
				//出险地点 
				HSSFCell acccidentAddressCell = row.createCell(10);
				acccidentAddressCell.setCellValue(list.get(i).getAccidentAddress());
				//接单地点
				HSSFCell ctAddressCell = row.createCell(11);
				ctAddressCell.setCellValue(list.get(i).getCtAddress());
				//派单时间
				HSSFCell getTimeCell = row.createCell(12);
				getTimeCell.setCellValue(sdf.format(list.get(i).getGetTime()));
				//审核人
				HSSFCell auditName = row.createCell(13);
				if(null!=list.get(i).getAuditName()){
					auditName.setCellValue(list.get(i).getAuditName());
				}else{
					auditName.setCellValue("--");
				}
				//订单状态 00派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回 09审核通过
				HSSFCell dealStatCell = row.createCell(14);
				String dealStatLabel = ConstantMap.getDealStatLabel(list.get(i).getDealStat());
				dealStatCell.setCellValue(dealStatLabel);
			
				//买家支付金额
				HSSFCell buyerMoneyCell = row.createCell(15);
				if(null!=list.get(i).getBuyerMoney()){
					buyerMoneyCell.setCellValue(Double.parseDouble(list.get(i).getBuyerMoney()));
				}else{
					buyerMoneyCell.setCellValue("--");
				}
				//卖家支付金额
				HSSFCell sellerMoneyCell = row.createCell(16);
				if(null!=list.get(i).getSellerMoney()){
					sellerMoneyCell.setCellValue(Double.parseDouble(list.get(i).getSellerMoney()));
				}else{
					sellerMoneyCell.setCellValue("--");
				}
				//团队获得佣金
				HSSFCell groupMoneyCell = row.createCell(17);
				if(null!=list.get(i).getGroupMoney()){
					groupMoneyCell.setCellValue(Double.parseDouble(list.get(i).getGroupMoney()));
				}else{
					groupMoneyCell.setCellValue("--");
				}			
			}
			
			long date = new Date().getTime();
			String fileName = "hy_buyer_entrust_export_"+sdf.format(date)+".xls";
			
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			Object userId = modelMap.get("userId");
			log.error("导出我的委托列表异常userId:"+userId,e);
			ProcessCodeEnum.FAIL.buildProcessException("导出我的委托列表异常userId："+userId,e);
		}
	}

	/**
	 * 货运险订单导出excel
	 * 我的作业(团队和车童个人)
	 * 
	 */
	@Override
	public void exportMyEntrustHY(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
			throws ProcessException {
		log.info("导出我的委托列表开始:"+modelMap.get("userId"));
		try {
			//查询当前登陆人信息
			CtUserVO user = this.queryCurRealUser(Long.valueOf(modelMap.get("userId").toString()));
			modelMap.put("userId", user.getId());
			
			List<HyMyEntrustModel> list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyEntrustToExcelExportHY", modelMap);
			
			//获取导出模板
			ClassPathResource resource = new ClassPathResource("/templates/myWorkingZHCXListTemplatesHY.xls");
			
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
			
			HSSFWorkbook workbook = new HSSFWorkbook(resource.getInputStream());
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			for(int i = 0; i < list.size(); i++){
				HSSFRow row = sheet.createRow(i + 1);
				String caseNo = list.get(i).getCaseNo();
				//报案号	
				HSSFCell caseNoCell = row.createCell(0);
				caseNoCell.setCellValue(caseNo);
				//订单编号
				HSSFCell orderNoCell = row.createCell(1);
				orderNoCell.setCellValue(list.get(i).getOrderNo());
				//订单类型 1派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回 09审核通过
				
				//买家账号
				HSSFCell buyerLoginNameCell = row.createCell(2);
				buyerLoginNameCell.setCellValue(list.get(i).getBuyerLoginName());
				//买家名称
				HSSFCell buyerNameCell = row.createCell(3);
				buyerNameCell.setCellValue(list.get(i).getBuyerName());
				//买家电话
				HSSFCell buyerMobileCell = row.createCell(4);
				buyerMobileCell.setCellValue(list.get(i).getBuyerMobile());
				//服务人姓名
				HSSFCell sellerCell = row.createCell(5);
				String sex = list.get(i).getSellerSex();
				if(null!=list.get(i).getSellerName()){
					if("0".equals(sex)){
						sellerCell.setCellValue(list.get(i).getSellerName()+"先生");	
					}else if("1".equals(sex)){
						sellerCell.setCellValue(list.get(i).getSellerName()+"女士");					
					}else{
						sellerCell.setCellValue(list.get(i).getSellerName()+"先生/女士");								
					}
				}else{
					sellerCell.setCellValue("--");	
				}
				//服务人电话
				HSSFCell sellerMobileCell = row.createCell(6);
				sellerMobileCell.setCellValue(list.get(i).getSellerMobile());
				//服务类型
				HSSFCell serviceCell = row.createCell(7);
				serviceCell.setCellValue("货运公估");
				//合约委托人名字	
				HSSFCell entrustNameCell = row.createCell(8);
				entrustNameCell.setCellValue(list.get(i).getEntrustName());
				//合约委托人电话
				HSSFCell entrustMobileCell = row.createCell(9);
				entrustMobileCell.setCellValue(list.get(i).getEntrustMobile());	
				
				//出险地点 
				HSSFCell acccidentAddressCell = row.createCell(10);
				acccidentAddressCell.setCellValue(list.get(i).getAccidentAddress());
				//接单地点
				HSSFCell ctAddressCell = row.createCell(11);
				ctAddressCell.setCellValue(list.get(i).getCtAddress());
				//派单时间
				HSSFCell getTimeCell = row.createCell(12);
				getTimeCell.setCellValue(sdf.format(list.get(i).getGetTime()));
				//审核人
				HSSFCell auditName = row.createCell(13);
				if(null!=list.get(i).getAuditName()){
					auditName.setCellValue(list.get(i).getAuditName());
				}else{
					auditName.setCellValue("--");
				}
				//订单状态 00派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回 09审核通过
				HSSFCell dealStatCell = row.createCell(14);
				String dealStatLabel = ConstantMap.getDealStatLabel(list.get(i).getDealStat());
				dealStatCell.setCellValue(dealStatLabel);
			
				//买家支付金额
				HSSFCell buyerMoneyCell = row.createCell(15);
				if(null!=list.get(i).getBuyerMoney()){
					buyerMoneyCell.setCellValue(Double.parseDouble(list.get(i).getBuyerMoney()));
				}else{
					buyerMoneyCell.setCellValue("--");
				}
				//卖家支付金额
				HSSFCell sellerMoneyCell = row.createCell(16);
				if(null!=list.get(i).getSellerMoney()){
					sellerMoneyCell.setCellValue(Double.parseDouble(list.get(i).getSellerMoney()));
				}else{
					sellerMoneyCell.setCellValue("--");
				}
				//团队获得佣金
				HSSFCell groupMoneyCell = row.createCell(17);
				if(null!=list.get(i).getGroupMoney()){
					groupMoneyCell.setCellValue(Double.parseDouble(list.get(i).getGroupMoney()));
				}else{
					groupMoneyCell.setCellValue("--");
				}			
			}
			
			long date = new Date().getTime();
			String fileName = "hy_buyer_entrust_export_"+sdf.format(date)+".xls";
			
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.flush();
			out.close();	
		} catch (Exception e) {
			Object userId = modelMap.get("userId");
			log.error("导出我的委托列表异常userId:"+userId,e);
			ProcessCodeEnum.FAIL.buildProcessException("导出我的委托列表异常userId："+userId,e);
		}
		
	}

	/**
	 * 货运险订单导出excel（按报案号）
	 * 我的作业(团队和车童个人)
	 * 
	 */
	@Override
	public void exportMyEntrustWithCaseNoHY(ModelMap modelMap, HttpServletResponse response, HttpServletRequest request)
			throws ProcessException {
		log.info("======================= 导出我的委托列表(报案号) 开始========================="+modelMap.get("userId"));
		try {
			//查询当前登陆人信息
			CtUserVO user = this.queryCurRealUser(Long.valueOf(modelMap.get("userId").toString()));
			modelMap.put("userId", user.getId());
			
			List<HyMyEntrustModel> list = this.commExeSqlDAO.queryForList("sqlmap_user.queryMyEntrustToExcelExportHY", modelMap);
			
			//获取导出模板
			ClassPathResource resource = new ClassPathResource("/templates/myWorkingZHCXListTemplatesHY.xls");
			
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd_HH-mm-ss" );
			
			HSSFWorkbook workbook = new HSSFWorkbook(resource.getInputStream());
			HSSFSheet sheet = workbook.getSheetAt(0);
			

			String lastCaseNo = null; //重复报案号
			int last = 1;   //重复报案号最后一个单元行号
			
			for(int i = 0; i < list.size(); i++){
				HSSFRow row = sheet.createRow(i + 1);
				String caseNo = list.get(i).getCaseNo();
				if(0==i){
					lastCaseNo = caseNo;
				}
				//案件号
				HSSFCell caseNoCell = row.createCell(0);
				caseNoCell.setCellValue(caseNo);
				if( null != lastCaseNo && !lastCaseNo.isEmpty() && !lastCaseNo.equals(caseNo) && 0 != i){
					sheet.addMergedRegion(new CellRangeAddress(last, i, 0, 0));
					last = i+1;
					lastCaseNo = caseNo;
				}
				if( null != lastCaseNo && !lastCaseNo.isEmpty() && i == list.size()-1){
					sheet.addMergedRegion(new CellRangeAddress(last, i+1, 0, 0));
				}
				//订单编号
				HSSFCell orderNoCell = row.createCell(1);
				orderNoCell.setCellValue(list.get(i).getOrderNo());
				//订单类型 1派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回 09审核通过
				
				//买家账号
				HSSFCell buyerLoginNameCell = row.createCell(2);
				buyerLoginNameCell.setCellValue(list.get(i).getBuyerLoginName());
				//买家名称
				HSSFCell buyerNameCell = row.createCell(3);
				buyerNameCell.setCellValue(list.get(i).getBuyerName());
				//买家电话
				HSSFCell buyerMobileCell = row.createCell(4);
				buyerMobileCell.setCellValue(list.get(i).getBuyerMobile());
				//服务人姓名
				HSSFCell sellerCell = row.createCell(5);
				String sex = list.get(i).getSellerSex();
				if(null!=list.get(i).getSellerName()){
					if("0".equals(sex)){
						sellerCell.setCellValue(list.get(i).getSellerName()+"先生");	
					}else if("1".equals(sex)){
						sellerCell.setCellValue(list.get(i).getSellerName()+"女士");					
					}else{
						sellerCell.setCellValue(list.get(i).getSellerName()+"先生/女士");								
					}
				}else{
					sellerCell.setCellValue("--");	
				}
				//服务人电话
				HSSFCell sellerMobileCell = row.createCell(6);
				sellerMobileCell.setCellValue(list.get(i).getSellerMobile());
				//服务类型
				HSSFCell serviceCell = row.createCell(7);
				serviceCell.setCellValue("货运公估");
				//合约委托人名字	
				HSSFCell entrustNameCell = row.createCell(8);
				entrustNameCell.setCellValue(list.get(i).getEntrustName());
				//合约委托人电话
				HSSFCell entrustMobileCell = row.createCell(9);
				entrustMobileCell.setCellValue(list.get(i).getEntrustMobile());	
				
				//出险地点 
				HSSFCell acccidentAddressCell = row.createCell(10);
				acccidentAddressCell.setCellValue(list.get(i).getAccidentAddress());
				//接单地点
				HSSFCell ctAddressCell = row.createCell(11);
				ctAddressCell.setCellValue(list.get(i).getCtAddress());
				//派单时间
				HSSFCell getTimeCell = row.createCell(12);
				getTimeCell.setCellValue(sdf.format(list.get(i).getGetTime()));
				//审核人
				HSSFCell auditName = row.createCell(13);
				if(null!=list.get(i).getAuditName()){
					auditName.setCellValue(list.get(i).getAuditName());
				}else{
					auditName.setCellValue("--");
				}
				//订单状态 00派单中 01无响应 02注销 03撤单 04作业中 05待初审 06初审退回 07待审核 08已退回 09审核通过
				HSSFCell dealStatCell = row.createCell(14);
				String dealStatLabel = ConstantMap.getDealStatLabel(list.get(i).getDealStat());
				dealStatCell.setCellValue(dealStatLabel);
			
				//买家支付金额
				HSSFCell buyerMoneyCell = row.createCell(15);
				if(null!=list.get(i).getBuyerMoney()){
					buyerMoneyCell.setCellValue(Double.parseDouble(list.get(i).getBuyerMoney()));
				}else{
					buyerMoneyCell.setCellValue("--");
				}
				//卖家支付金额
				HSSFCell sellerMoneyCell = row.createCell(16);
				if(null!=list.get(i).getSellerMoney()){
					sellerMoneyCell.setCellValue(Double.parseDouble(list.get(i).getSellerMoney()));
				}else{
					sellerMoneyCell.setCellValue("--");
				}
				//团队获得佣金
				HSSFCell groupMoneyCell = row.createCell(17);
				if(null!=list.get(i).getGroupMoney()){
					groupMoneyCell.setCellValue(Double.parseDouble(list.get(i).getGroupMoney()));
				}else{
					groupMoneyCell.setCellValue("--");
				}			
			}
			
			long date = new Date().getTime();
			String fileName = "hy_buyer_entrust_export_"+sdf.format(date)+".xls";
			
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/vnd.ms-excel;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			OutputStream out = response.getOutputStream();
			workbook.write(out);
			out.flush();
			out.close();
		} catch (Exception e) {
			Object userId = modelMap.get("userId");
			log.error("导出我的委托列表异常userId:"+userId,e);
			ProcessCodeEnum.FAIL.buildProcessException("导出我的委托列表异常userId："+userId,e);
		}
	}

	/** (non-Javadoc)
	 * @Description: 查询订单导入中的团队
	 * @param orderNo
	 * @return
	 * @author zhouchushu
	 * @date 2016年3月9日 下午3:58:26
	 * @see net.chetong.order.service.user.UserService#queryImportOrderGroup(java.lang.String)
	 */
	@Override
	public CtGroupVO queryImportOrderGroup(String orderNo) {
		return commExeSqlDAO.queryForObject("ct_group.queryImportOrderGroup", orderNo);
	}	
	
	@Override
	public List<Map<String, Object>>  queryTheLoginId() {
		return commExeSqlDAO.queryForList("fm_order.queryTheLoginId",null);
	}	
}
