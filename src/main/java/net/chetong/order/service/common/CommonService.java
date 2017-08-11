package net.chetong.order.service.common;

import java.util.List;
import java.util.Map;

import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.ParaKeyValue;
import net.chetong.order.model.SysUserConfigVO;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.ctenum.AreaType;
import net.chetong.order.util.ctenum.SpecialTime;
import net.chetong.order.util.exception.ProcessException;


/**
 * 公共的服务方法
 */
public interface CommonService {
	
	/**
	 * 查询订单是否参与活动
	 * @param params promotion_type =01 一元体验
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public Map queryPromotionOrderRelation(Map params);
	
	/**
	 * 根据市和地区的描述获取数据库中的市和地区编码
	 * 	  如：深圳 > 440300
	 * @param desc  市地区的中文名
	 * @param areaType 地区类型 
	 * @param parentCode 市的省编码
	 * @return 市或地区编码
	 */
	public String getAreaCodeByAreaName(String areaName,AreaType areaType,String parentCode);
	
	/**
	 * 查询省的编码 根据省的中文名
	 * @param provName 省的名称
	 * @return 省的编码
	 */
	public String getAreaCodeByAreaName(String provName);
	
	/**获取现在时间是否是特殊时间
	 * 		春节、节假日、周末、夜间
	 *	@param startTime 夜间规则开始时间
	 *	@param endTime 夜间规则结束时间
	 *	@param 特殊时间类型
	 */
	public SpecialTime getSpecialTime(int startTime,int endTime);
	
	/***
	 * 是否有权限
	 * @param userId
	 * @param orderNo
	 * @param stateArr
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> isHasAuthority(String userId,String orderNo,String[] stateArr) throws ProcessException ;
	
	/**
	 * 是否有作业权限
	 * @param userId
	 * @param orderNo
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public ResultVO<Object> isHasAuthorityWorking(String userId,String orderNo) throws ProcessException ;
	
	/***
	 * 根据对象名称获取数据返回map对象
	 * @param reportNo
	 * @param objectName
	 * @return
	 * @author wufeng@chetong.net
	 */
	public List<Map<String,String>> queryInsureDataByObjName(String reportNo,String objectName,String queryNode);

	/**
	 * @Description: 查询订单是否是异地单
	 * @param buyerGroup
	 * @param provCode
	 * @param cityCode
	 * @return
	 * @return boolean
	 * @author zhouchushu
	 * @date 2016年3月21日 上午11:22:00
	 */
	public boolean queryIsOtherPlaceOrder(CtGroupVO buyerGroup, String provCode, String cityCode);
	
	/**
	 * 判断一个订单是否是旧价格订单
	 * @author wufj@chetong.net
	 *         2016年3月30日 下午5:21:12
	 * @param orderVO
	 * @return
	 */
	public boolean isOldPriceOrder(FmOrderVO orderVO) throws Exception;
	
	public boolean isYcCase(String reportNo)  throws ProcessException ;
	
	/**
	 * 获取保险公司系统账号与本系统对接账号配置信息
	 * @param companyCode
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public List<SysUserConfigVO> getUserConfigListByCompany(String companyCode) throws ProcessException ;
	
	/**
	 * 根据保险公司用户代码及保险公司代码获取对接账号配置信息
	 * @param companyCode
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public SysUserConfigVO getUserConfigByUserCode(String userCode ,String companyCode) throws ProcessException ;
	
	/**
	 * 查通用的key-value对象.
	 * @param type
	 * @return
	 */
	public List<ParaKeyValue> queryParaKeyValue(String type);
	
	
/**
	 * 判断永诚案件是否是互碰自赔
	 * @param reportNo
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public boolean isBumpFlagForYC(String reportNo) throws ProcessException;
	
	/**
	 * 根据任务ID获取对应的保险公司任务ID
	 * @param reportNo
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public String getCompanyTaskId(String orderNo,String source) throws ProcessException;
	
	/***
	 * 校验用户的数据权限
	 * @param userId
	 * @param orderNo
	 * @return
	 * @throws ProcessException
	 * @author wufeng@chetong.net
	 */
	public boolean verifyUserDataAuthority(Long userId ,String orderNo) throws ProcessException;

	public boolean verifyCompany2GroupDataAuthority(Long uId, String orderNo) throws ProcessException;
	
}

