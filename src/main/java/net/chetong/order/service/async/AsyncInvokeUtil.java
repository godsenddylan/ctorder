package net.chetong.order.service.async;

import javax.xml.rpc.ServiceException;

import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CheckLossListReq;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CheckLossListRst;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.NewTaskList;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDAGetNewTask;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASceneMainInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASurveryFee;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASurveryFeeQuery;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASurveryIns;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASurveryInsQuery;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.ReturnRst;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.PDACarMainInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.PDAPropMainInfo;
import org.tempuri.GetCheckGoodsLossListImpl.claims.GetCheckGoodsLossListLocator;
import org.tempuri.GetCheckLossListImpl.claims.GetCheckLossListLocator;
import org.tempuri.GetFeeListImpl.claims.GetFeeListLocator;
import org.tempuri.GetInsListImpl.claims.GetInsListLocator;
import org.tempuri.GetPDATaskListImpl.claims.GetPDATaskListLocator;
import org.tempuri.SaveSRVVehicleImpl.claims.SaveSRVVehicleLocator;
import org.tempuri.SaveSurveryImpl.claims.SaveSurveyLocator;
import org.tempuri.SaveSurveyGoodsImpl.claims.SaveSurveyGoodsLocator;

import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.Config;
import net.chetong.order.util.StringUtil;

public class AsyncInvokeUtil extends BaseService {
	
	/***********************************************永诚接口配置************************************************************/
	
	/**
	 * 获取案件任务信息接口
	 * @return
	 * @throws ServiceException
	 * @author wufeng@chetong.net
	 */
	public static org.tempuri.GetPDATaskListImpl.claims.ISurvey getPDATaskListISurvey() throws ServiceException{
		GetPDATaskListLocator sl = new GetPDATaskListLocator();
		sl.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_GET_TASKLIST);
		return sl.getSOAPEventSource();
	}
	
	/**
	 * 保存查勘接口
	 * @return
	 * @throws ServiceException
	 * @author wufeng@chetong.net
	 */
	public static org.tempuri.SaveSurveryImpl.claims.ISurvey getSaveSurveyISurvey() throws ServiceException{
		SaveSurveyLocator sl = new SaveSurveyLocator();
		sl.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_SAVE_SURVEY);
		return sl.getSOAPEventSource();
	}
	
	/**
	 * 获取车定损接口
	 * @return
	 * @throws ServiceException
	 * @author wufeng@chetong.net
	 */
	public static org.tempuri.SaveSRVVehicleImpl.claims.ISurvey getSaveSRVVehicleISurvey() throws ServiceException{
		SaveSRVVehicleLocator sl = new SaveSRVVehicleLocator();
		sl.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_SAVE_VEHICLE_LOSS);
		return sl.getSOAPEventSource();
	}
	
	/**
	 * 保存物损接口
	 * @return
	 * @throws ServiceException
	 * @author wufeng@chetong.net
	 */
	public static org.tempuri.SaveSurveyGoodsImpl.claims.ISurvey getSaveSurveyGoodsISurvey() throws ServiceException{
		SaveSurveyGoodsLocator sl = new SaveSurveyGoodsLocator();
		sl.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_SAVE_GOODS_LOSS);
		return sl.getSOAPEventSource();
	}
	
	/**
	 * 获取车定损审核接口
	 * @return
	 * @throws ServiceException
	 * @author wufeng@chetong.net
	 */
	public static org.tempuri.GetCheckLossListImpl.claims.ISurvey getCheckLossListISurvey() throws ServiceException{
		GetCheckLossListLocator sl = new GetCheckLossListLocator();
		sl.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_CHECK_LOSS);
		return sl.getSOAPEventSource();
	}
	
	/**
	 * 获取物损审核接口
	 * @return
	 * @throws ServiceException
	 * @author wufeng@chetong.net
	 */
	public static org.tempuri.GetCheckGoodsLossListImpl.claims.ISurvey getCheckGoodsLossListISurvey() throws ServiceException{
		GetCheckGoodsLossListLocator sl = new GetCheckGoodsLossListLocator();
		sl.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_CHECK_GOODS_LOSS);
		return sl.getSOAPEventSource();
	}
	
	/**
	 * 获取险种代码列表
	 * @return
	 * @throws ServiceException
	 * @author wufeng@chetong.net
	 */
	public static org.tempuri.GetInsListImpl.claims.ISurvey getInsListISurvey() throws ServiceException{
		GetInsListLocator sl = new GetInsListLocator();
		sl.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_GET_INSLIST);
		return sl.getSOAPEventSource();
	}
	
	/**
	 * 获取费用列表
	 * @return
	 * @throws ServiceException
	 * @author wufeng@chetong.net
	 */
	public static org.tempuri.GetFeeListImpl.claims.ISurvey getFeeListImplISurvey() throws ServiceException{
		GetFeeListLocator sl = new GetFeeListLocator();
		sl.setSOAPEventSourceEndpointAddress(Config.YC_WSDL_URL_GET_FEETYPELIST);
		return sl.getSOAPEventSource();
	}
	
	/***********************************************  永诚接口配置    END ************************************************************/
	
	
	/***
	 * 获取永诚案件任务列表信息
	 * @param pdaNewTask
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static NewTaskList getYcTasks(PDAGetNewTask pdaNewTask){
		try{
			org.tempuri.GetPDATaskListImpl.claims.ISurvey is = getPDATaskListISurvey();
			NewTaskList newTaskList = is.getPDATaskList(pdaNewTask);
			return newTaskList;
		}catch(Exception e){
			log.error("调用永诚获取任务列表接口异常：",e);
			return null;
		}
	}
	
	/***
	 * 异步发送现场查勘信息给永诚
	 * @param survery
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static ReturnRst sendSurveyToYc(PDASceneMainInfo survery){
		try{
			org.tempuri.SaveSurveryImpl.claims.ISurvey is = getSaveSurveyISurvey();
			ReturnRst returnRst = is.saveSurvery(survery);
			if(StringUtil.isNullOrEmpty(returnRst)){
				returnRst = new ReturnRst(-1, "异步发送现场查勘信息给永诚失败");
			}
			return returnRst;
		}catch(Exception e){
			log.error("异步发送现场查勘信息给永诚异常：",e);
			return new ReturnRst(-1, "异步发送现场查勘信息给永诚异常");
		}
	}
	
	/***
	 * 异步发送标的车(三者车)定损信息给永诚
	 * @param survery
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static ReturnRst sendLossToYc(PDACarMainInfo pdaCarMainInfo){
		try{
			org.tempuri.SaveSRVVehicleImpl.claims.ISurvey is = getSaveSRVVehicleISurvey();
			ReturnRst returnRst = is.saveSRVVehicle(pdaCarMainInfo);
			if(StringUtil.isNullOrEmpty(returnRst)){
				returnRst = new ReturnRst(-1, "异步发送定损信息给永诚失败");
			}
			return returnRst;
		}catch(Exception e){
			log.error("异步发送定损信息给永诚异常：",e);
			return new ReturnRst(-1, "异步发送定损信息给永诚异常:"+e);
		}
	}
	
	/***
	 * 异步本车(三者车财)财物定损信息给永诚
	 * @param survery
	 * @return
	 * @author wufeng@chetong.net
	 */
	public static ReturnRst sendLossGoodsToYc(PDAPropMainInfo propMainInfo){
		try{
			org.tempuri.SaveSurveyGoodsImpl.claims.ISurvey is = getSaveSurveyGoodsISurvey();
			ReturnRst returnRst = is.saveSurveyGoods(propMainInfo);
			if(StringUtil.isNullOrEmpty(returnRst)){
				returnRst = new ReturnRst(-1, "异步本车(三者车财)财物定损信息给永诚失败");
			}
			return returnRst;
		}catch(Exception e){
			log.error("异步本车(三者车财)财物定损信息给永诚异常：",e);
			return new ReturnRst(-1, "异步本车(三者车财)财物定损信息给永诚异常:"+e);
		}
	}
	
	/***
	 * 获取永诚车定损（三者、标的）审核信息
	 * @param req
	 * @author wufeng@chetong.net
	 */
	public static CheckLossListRst getCheckLossList(CheckLossListReq req){
		try{
			org.tempuri.GetCheckLossListImpl.claims.ISurvey is = getCheckLossListISurvey();
			CheckLossListRst rst = is.getCheckLossList(req);
			return rst;
		}catch(Exception e){
			log.error("获取永诚车定损（三者、标的）审核信息异常：",e);
			return null;
		}
	}
	
	/***
	 * 获取永诚物损（三者、标的）审核信息
	 * @param req
	 * @author wufeng@chetong.net
	 */
	public static CheckLossListRst getCheckGoodsLossList(CheckLossListReq req){
		try{
			org.tempuri.GetCheckGoodsLossListImpl.claims.ISurvey is = getCheckGoodsLossListISurvey();
			CheckLossListRst rst = is.getCheckGoodsLossList(req);
			return rst;
		}catch(Exception e){
			log.error("获取永诚物损（三者、标的）审核信息异常：",e);
			return null;
		}
	}
	
	/***
	 * 获取险种代码列表
	 * @param req
	 * @author wufeng@chetong.net
	 */
	public static PDASurveryIns getInsList(PDASurveryInsQuery req){
		try{
			org.tempuri.GetInsListImpl.claims.ISurvey is = getInsListISurvey();
			PDASurveryIns rst = is.getInsList(req);
			return rst;
		}catch(Exception e){
			log.error("获取永诚险种代码列表异常：",e);
			return null;
		}
	}
	
	/***
	 * 获取费用类型列表
	 * @param req
	 * @author wufeng@chetong.net
	 */
	public static PDASurveryFee getFeeTypeList(PDASurveryFeeQuery req){
		try{
			org.tempuri.GetFeeListImpl.claims.ISurvey is = getFeeListImplISurvey();
			PDASurveryFee rst = is.getFeeList(req);
			return rst;
		}catch(Exception e){
			log.error("获取永诚费用类型列表异常：",e);
			return null;
		}
	}
	
}
