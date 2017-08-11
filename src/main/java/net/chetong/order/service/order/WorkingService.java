package net.chetong.order.service.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GetCarDataRst;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GetCheckCdeRst;
import org.springframework.ui.ModelMap;

import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FhCarModelVO;
import net.chetong.order.model.FhDamageModelVO;
import net.chetong.order.model.FhLeaveModelVO;
import net.chetong.order.model.FhPartModelVO;
import net.chetong.order.model.FhRepairModelVO;
import net.chetong.order.model.FmOrderVO;
import net.chetong.order.model.OrderFlowVO;
import net.chetong.order.model.WorkingVO;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.exception.ProcessException;



public interface WorkingService {

	/**
	 * 查询订单作业信息
	 * @param orderNo
	 * @param orderType 
	 * @return
	 */
	Map<String, Object> queryOrderWorkingDetail(String orderNo, String orderType) throws Exception;

	/**
	 * 查询留言信息
	 * @param orderNo
	 * @return
	 */
	List<FhLeaveModelVO> queryLeaveMessageByOrderNo(String orderNo) throws Exception;

	/**
	 * 查询审核信息
	 * @param orderNo
	 * @return
	 */
	List<FhAuditModelVO> queryAuditMessageByOrderNo(String orderNo) throws Exception;

	/**
	 * 查询买家卖家
	 * @param orderNo
	 * @return
	 */
	Map<String, Long> queryBuyerAndSeller(String orderNo) throws Exception;

	/**
	 * 查询作业信息
	 * @param orderNo
	 * @param orderType
	 * @return
	 */
	ResultVO<Object> queryWorkingModel(String orderNo, String orderType,String userId) throws Exception;
	
	/**
	 * 查询车辆信息
	 * @param guid
	 * @return
	 */
	List<FhCarModelVO> queryCarModelList(String guid,String isMain) throws Exception;
	
	/**
	 * 查询换件项目
	 * @param taskId
	 * @param guid
	 * @return
	 */
	List<FhPartModelVO> queryPartByIdAndGuid(String taskId,String guid) throws Exception;
	
	/**
	 * 查询维修项目
	 * @param taskId
	 * @param guid
	 * @return
	 */
	List<FhRepairModelVO> queryRepairByIdAndGuid(String taskId, String guid) throws Exception;
	
	/**
	 * 查询物损信息
	 * @param orderCode
	 * @return
	 */
	List<FhDamageModelVO> queryDamageByOrderCode(String orderCode) throws Exception;

	/**
	 * 保存作业信息
	 * @param workingInfo
	 * @return
	 * @throws Exception 
	 */
	ResultVO<Object> save(WorkingVO workingInfo) throws Exception;
	
	/**
	 * 
	 * @param orderVO
	 * @param lossAmount
	 * @param overFee
	 * @return
	 * @throws ProcessException
	 */
	BigDecimal saveOverCostDetail(FmOrderVO orderVO, String lossAmount) throws ProcessException;
	
	/**
	 * 计算超额附加费
	 * @param orderVO
	 * @param lossAmount
	 * @return
	 */
	public String computeOverFee(FmOrderVO orderVO, String lossAmount) throws Exception;

	/**
	 * 
	 * @author wufj@chetong.net
	 *         2016年3月22日 下午1:51:25
	 * @param params
	 * @return
	 */
	ResultVO<Object> computeOverFeeForShow(ModelMap params);
	
	public BigDecimal computeGuideOverFee(FmOrderVO orderVO, String lossAmount) throws ProcessException;

	ResultVO<Object> getSellerUserInfo(String orderNo);

	/**
	 * 获取永城校验车辆信息的验证码
	 * @author luoqiao@chetong.net
	 * @time 2016-11-16 14:20:18
	 * @param params 
	 *             orderNo 订单号
	 *             carMark 车牌号
	 * @return
	 * @throws Exception
	 */
	ResultVO<Map<String, Object>> getCheckCdeForCheckCarMark(ModelMap params);

	/**
	 * 获取永城车辆信息
	 * @author luoqiao@chetong.net
	 * @time 2016-11-16 15:45:21
	 * @param params 
	 *             orderNo 订单号
	 *             carMark 车牌号
	 * @return
	 * @throws Exception
	 */
	ResultVO<Map<String, Object>> getYCCarDataForCheckCarMark(ModelMap params); 
	
	/**
	 * 查询订单的流程信息
	 * 
	 * @param modelMap
	 * @return
	 * @throws Exception
	 */
	public ResultVO<List<OrderFlowVO>> queryOrderFlowVO(ModelMap modelMap);
}
