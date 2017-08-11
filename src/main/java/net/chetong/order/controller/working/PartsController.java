package net.chetong.order.controller.working;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.FeeInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.InsInfo;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASurveryFee;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASurveryFeeQuery;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASurveryIns;
import org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASurveryInsQuery;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.estar.app.appsrvyycbx.domain.BankLocationsVO;
import com.estar.app.appsrvyycbx.domain.BankTypeInfoVO;
import com.estar.app.appsrvyycbx.domain.CarTypeInfoVO;
import com.estar.app.appsrvyycbx.domain.DataVO;
import com.estar.app.appsrvyycbx.domain.DirFitInfoVO;
import com.estar.app.appsrvyycbx.domain.PdaCarTypeQueryVO;
import com.estar.app.appsrvyycbx.domain.PdaDirFitQueryVO;
import com.estar.app.appsrvyycbx.domain.PdaFitListQueryVO;
import com.estar.app.appsrvyycbx.domain.ReturnBankLocationsVO;
import com.estar.app.appsrvyycbx.domain.ReturnBankTypeInfoVO;
import com.estar.app.appsrvyycbx.domain.ReturnCarTypeInfoVO;
import com.estar.app.appsrvyycbx.domain.ReturnDirFitInfoVO;
import com.estar.app.appsrvyycbx.domain.ReturnFitInfoVO;
import com.estar.app.appsrvyycbx.domain.ReturnStandardareasInfoVO;
import com.estar.app.appsrvyycbx.domain.StandardareasInfoVO;
import com.estar.app.appsrvyycbx.domain.SubmitCarFitChangeDetailInfoVO;
//import com.estar.edp.utils.XMLBean;
import com.thoughtworks.xstream.XStream;

import net.chetong.order.controller.Base.BaseController;
import net.chetong.order.service.async.AsyncInvokeUtil;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.util.Config;
import net.chetong.order.util.Constants;
import net.chetong.order.util.HttpSendUtil;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;

@Controller
@RequestMapping("/parts")
public class PartsController extends BaseController {
	
	@Resource
	private CommonService commonService;
	
	/***
	 * 获取车型信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryCarTypeInfo")
	@ResponseBody
	public Object queryCarTypeInfo(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<List<CarTypeInfoVO>> rstVO = new ResultVO<List<CarTypeInfoVO>>();
		DataVO dvo = new DataVO();
		PdaCarTypeQueryVO pdaCarQryVO = new PdaCarTypeQueryVO();
		pdaCarQryVO.setComDptID((String)modelMap.get("comDptID"));
		pdaCarQryVO.setDataFrm((String)modelMap.get("dataFrm"));
		pdaCarQryVO.setDptID((String)modelMap.get("dptID"));
		pdaCarQryVO.setQryInfo((String)modelMap.get("qryInfo"));
		pdaCarQryVO.setQryType((String)modelMap.get("qryType"));
		pdaCarQryVO.setUserID((String)modelMap.get("ycUserID"));
		List<Object> list = new ArrayList<Object>();
		list.add(pdaCarQryVO);
		dvo.setMethod("pdaCarTypeQuery");
		dvo.setAction("pAndroidCarTypeQuery");
		dvo.setList(list);
		XStream x = new XStream();
		String xmlString = x.toXML(dvo);
		String rstXmlStr = HttpSendUtil.postByXml(Config.YC_CAR_TYPE_URL,xmlString);
		DataVO rstDvo = (DataVO)x.fromXML(rstXmlStr);
		List<CarTypeInfoVO> carTypeList = new ArrayList<CarTypeInfoVO>();
		if("1".equals(rstDvo.getResultCde())){
			List<Object> rtnCarTypeList = rstDvo.getList();
			for(int i=0;i<rtnCarTypeList.size();i++){
				ReturnCarTypeInfoVO rtnTypeVO = (ReturnCarTypeInfoVO)rtnCarTypeList.get(i);
				if("1".equals(rtnTypeVO.getCode())){
					carTypeList.addAll(rtnTypeVO.getCarTypeInfoVO());
				}
			}
			rstVO.setResultCode(Constants.SUCCESS);
		}else{
			rstVO.setResultCode(Constants.ERROR);
		}
		rstVO.setResultMsg(rstDvo.getMessage());
		rstVO.setResultObject(carTypeList);
		return rstVO;
	}
	
	/***
	 * 获取配件项目信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryPartInfo")
	@ResponseBody
	public Object queryPartInfo(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<List<DirFitInfoVO>> rstVO = new ResultVO<List<DirFitInfoVO>>();
    	try{
    		DataVO dvo = new DataVO();
    		PdaDirFitQueryVO fitQueryVO = new PdaDirFitQueryVO();
    		fitQueryVO.setComDptID((String)modelMap.get("comDptID"));//保险公司代码
    		fitQueryVO.setFitCarId((String)modelMap.get("fitCarId"));//车型代码
    		fitQueryVO.setFitArea((String)modelMap.get("fitArea"));//价格区域代码
    		fitQueryVO.setUserID((String)modelMap.get("ycUserID"));//查勘员代码
    		fitQueryVO.setDptID((String)modelMap.get("dptID"));//查勘员机构代码
    		fitQueryVO.setDataFrm((String)modelMap.get("dataFrm"));
    		List<Object> list = new ArrayList<Object>();
    		list.add(fitQueryVO);
    		dvo.setMethod("pdaDirFit");
    		dvo.setAction("pAndroidDirFit");
    		dvo.setList(list);
    		XStream x = new XStream();
    		String xmlString = x.toXML(dvo);
    		String rstXmlStr = HttpSendUtil.postByXml(Config.YC_CAR_TYPE_URL,xmlString);
    		DataVO rstDvo = (DataVO)x.fromXML(rstXmlStr);
    		List<DirFitInfoVO> dirFitList = new ArrayList<DirFitInfoVO>();
    		if("1".equals(rstDvo.getResultCde())){
    			List<Object> rtnDirFitList = rstDvo.getList();
    			for(int i=0;i<rtnDirFitList.size();i++){
    				ReturnDirFitInfoVO rtnVO = (ReturnDirFitInfoVO)rtnDirFitList.get(i);
    				if("1".equals(rtnVO.getCode())){
    					dirFitList.addAll(rtnVO.getDirFitInfoVOList());
    				}
    			}
    			rstVO.setResultCode(Constants.SUCCESS);
    		}else{
    			rstVO.setResultCode(Constants.ERROR);
    		}
    		rstVO.setResultMsg(rstDvo.getMessage());
			rstVO.setResultObject(dirFitList);
    	}catch(Exception e ){
    		rstVO.setResultCode(Constants.ERROR);
    		rstVO.setResultMsg("获取配件信息异常！");
    	}
		return rstVO;
	}
	
	/***
	 * 获取配件明细项目信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryPartDetailInfo")
	@ResponseBody
	public Object queryPartDetailInfo(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<List<SubmitCarFitChangeDetailInfoVO>> rstVO = new ResultVO<List<SubmitCarFitChangeDetailInfoVO>>();
    	try{
    		DataVO dvo = new DataVO();
    		PdaFitListQueryVO fitListQueryVO = new PdaFitListQueryVO();
    		fitListQueryVO.setComDptID((String)modelMap.get("comDptID"));//保险公司代码
    		fitListQueryVO.setFitCarId((String)modelMap.get("fitCarId"));//车型代码"
    		fitListQueryVO.setFitArea((String)modelMap.get("fitArea"));//价格区域代码
    		fitListQueryVO.setParented((String)modelMap.get("parented"));//父配件代码（顶级的配件项目
    		fitListQueryVO.setUserID((String)modelMap.get("ycUserID"));//查勘员代码
    		fitListQueryVO.setDptID((String)modelMap.get("dptID"));//查勘员机构代码
    		fitListQueryVO.setDataFrm((String)modelMap.get("dataFrm"));
    		List<Object> list = new ArrayList<Object>();
    		list.add(fitListQueryVO);
    		dvo.setMethod("pdaFitList");
    		dvo.setAction("pAndroidFitList");
    		dvo.setList(list);
    		XStream x = new XStream();
    		String xmlString = x.toXML(dvo);
    		String rstXmlStr = HttpSendUtil.postByXml(Config.YC_CAR_TYPE_URL,xmlString);
    		DataVO rstDvo = (DataVO)x.fromXML(rstXmlStr);
    		List<SubmitCarFitChangeDetailInfoVO> dirFitList = new ArrayList<SubmitCarFitChangeDetailInfoVO>();
    		if("1".equals(rstDvo.getResultCde())){
    			List<Object> rtnDirFitList = rstDvo.getList();
    			for(int i=0;i<rtnDirFitList.size();i++){
    				ReturnFitInfoVO rtnVO = (ReturnFitInfoVO)rtnDirFitList.get(i);
    				if("1".equals(rtnVO.getCode())){
    					dirFitList.addAll(rtnVO.getSubmitCarFitChangeDetailInfoVOList());
    				}
    			}
    			rstVO.setResultCode(Constants.SUCCESS);
    		}else{
    			rstVO.setResultCode(Constants.ERROR);
    		}
    		rstVO.setResultMsg(rstDvo.getMessage());
    		rstVO.setResultObject(dirFitList);
    	}catch(Exception e ){
    		rstVO.setResultCode(Constants.ERROR);
    		rstVO.setResultMsg("获取配件明细信息异常！");
    	}
		return rstVO;
	}
	
	
	/***
	 * 获取配件项目信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 
	@SuppressWarnings("rawtypes")
	@RequestMapping("/queryPartInfo2")
	@ResponseBody
	public Object queryPartInfo(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<List<DirFitInfoVO>> rstVO = new ResultVO<List<DirFitInfoVO>>();
    	try{
    		
    		XMLBean root=new XMLBean("root");
			XMLBean head=new XMLBean("head");
			head.setAttribute("function", "qPDADirFitList");
			head.setAttribute("method", "qDirFitList");
			head.setAttribute("sercode",Config.YC_MOBILE_SERCODE);
			root.elementPut(head);
			
			XMLBean QueryInfo =new XMLBean("QueryInfo");
			QueryInfo.setAttribute("comDptID",(String)modelMap.get("comDptID"));//保险公司代码
			QueryInfo.setAttribute("fitCarId",(String)modelMap.get("fitCarId"));//车型代码"
			QueryInfo.setAttribute("fitArea",(String)modelMap.get("fitArea"));//价格区域代码
			QueryInfo.setAttribute("userID",(String)modelMap.get("ycUserID"));//查勘员代码
			QueryInfo.setAttribute("dptID",(String)modelMap.get("dptID"));//查勘员机构代码
			QueryInfo.setAttribute("dataFrm",(String)modelMap.get("dataFrm"));
			root.elementPut(QueryInfo);
			XMLBean xml = HttpSendUtil.sendData(Config.YC_PART_URL,root);
			if(null != xml){
				XMLBean result =xml.getElement("result");
				rstVO.setResultCode(Constants.SUCCESS);
				rstVO.setResultMsg(result.getAttribute("message"));
				
				XMLBean QueryList=xml.getElement("QueryList");
				int rows=0;
				List list=null;
				if (QueryList!=null) {
					rows=QueryList.getInt("rows");
					list=QueryList.getListElement();
				}
				List<DirFitInfoVO> dirFitInfoVOList=new ArrayList<DirFitInfoVO>();
				for (int i = 0; i <rows; i++) {
					XMLBean listBean=(XMLBean)list.get(i);
					DirFitInfoVO dirFitInfoVO=new DirFitInfoVO();
					dirFitInfoVO.setFtnCde2(listBean.getAttribute("ftnCde2"));
					dirFitInfoVO.setFtnNme(listBean.getAttribute("ftnNme"));
					dirFitInfoVOList.add(dirFitInfoVO);
				}
				rstVO.setResultObject(dirFitInfoVOList);
			}
    	}catch(Exception e ){
    		rstVO.setResultCode(Constants.ERROR);
    		rstVO.setResultMsg("获取配件信息异常！");
    	}
		return rstVO;
	}
	*/
	/***
	 * 获取配件明细项目信息
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 
	@SuppressWarnings("rawtypes")
	@RequestMapping("/queryPartDetailInfo2")
	@ResponseBody
	public Object queryPartDetailInfo(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<List<SubmitCarFitChangeDetailInfoVO>> rstVO = new ResultVO<List<SubmitCarFitChangeDetailInfoVO>>();
    	try{
    		XMLBean root=new XMLBean("root");
			XMLBean head=new XMLBean("head");
			head.setAttribute("function", "qPDAFitList");
			head.setAttribute("method", "qFitList");
			head.setAttribute("sercode",Config.YC_MOBILE_SERCODE);
			head.setAttribute("reqPage", "10");
			root.elementPut(head);
			XMLBean QueryInfo =new XMLBean("QueryInfo");
			QueryInfo.setAttribute("comDptID",(String)modelMap.get("comDptID"));//保险公司代码
			QueryInfo.setAttribute("fitCarId",(String)modelMap.get("fitCarId"));//车型代码"
			QueryInfo.setAttribute("fitArea",(String)modelMap.get("fitArea"));//价格区域代码
			QueryInfo.setAttribute("parented",(String)modelMap.get("parented"));//父配件代码（顶级的配件项目
			QueryInfo.setAttribute("userID",(String)modelMap.get("ycUserID"));//查勘员代码
			QueryInfo.setAttribute("dptID",(String)modelMap.get("dptID"));//查勘员机构代码
			QueryInfo.setAttribute("dataFrm",(String)modelMap.get("dataFrm"));
			root.elementPut(QueryInfo);
			XMLBean xml = HttpSendUtil.sendData(Config.YC_PART_DETAIL_URL,root);
			if(null != xml){
				XMLBean result =xml.getElement("result");
				rstVO.setResultCode(Constants.SUCCESS);
				rstVO.setResultMsg(result.getAttribute("message"));
				
				XMLBean QueryList=xml.getElement("QueryList"); 
				int rows=0;
				List list=null;
				if(QueryList!=null){
					rows=QueryList.getInt("rows");
					list=QueryList.getListElement();
				}
				
				List<SubmitCarFitChangeDetailInfoVO> submitCarFitChangeDetailInfoVOList=new ArrayList<SubmitCarFitChangeDetailInfoVO>();
				
				for (int i = 0; i < rows; i++) {
					XMLBean listBean=(XMLBean)list.get(i);
					
					SubmitCarFitChangeDetailInfoVO submitCarFitChangeDetailInfoVO = new SubmitCarFitChangeDetailInfoVO();
					submitCarFitChangeDetailInfoVO.setFitNO(listBean.getAttribute("ftnCde"));//配件代码
					submitCarFitChangeDetailInfoVO.setFitName(listBean.getAttribute("ftnNme"));//配件名称
					submitCarFitChangeDetailInfoVO.setIniFtnsCde(listBean.getAttribute("ftnOldCde"));//原厂代码
					submitCarFitChangeDetailInfoVO.setSpecialPrice(listBean.getFloat("ftnPrc1"));//价格1--专修价
					submitCarFitChangeDetailInfoVO.setMarketPrice(listBean.getFloat("ftnPrc2"));//价格2--市场价
					submitCarFitChangeDetailInfoVO.setSetPrice(listBean.getFloat("ftnPrc3"));//价格3--配套价
					submitCarFitChangeDetailInfoVO.setFtnMemo(listBean.getAttribute("ftnMemo"));
					if(listBean.getAttribute("indID").equals("-999")){
						submitCarFitChangeDetailInfoVO.setSelfDefine("0");
					}else{
						submitCarFitChangeDetailInfoVO.setSelfDefine("1");
					}
					submitCarFitChangeDetailInfoVOList.add(submitCarFitChangeDetailInfoVO);
				}
				rstVO.setResultObject(submitCarFitChangeDetailInfoVOList);
			}
    	}catch(Exception e ){
    		rstVO.setResultCode(Constants.ERROR);
    		rstVO.setResultMsg("获取配件明细信息异常！");
    	}
		return rstVO;
	}
	*/
	/***
	 * 银行类型查询
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryBankTypeInfo")
	@ResponseBody
	public Object queryBankTypeInfo(@RequestBody ModelMap modelMap) throws Exception{
		
		ResultVO<List<BankTypeInfoVO>> rstVO = new ResultVO<List<BankTypeInfoVO>>();
		DataVO dvo = new DataVO();
		BankTypeInfoVO bankQryVO = new BankTypeInfoVO();
		List<Object> list = new ArrayList<Object>();
		list.add(bankQryVO);
		dvo.setMethod("pdaBankTypeQuery");
		dvo.setAction("qAndroidBanks");
		dvo.setList(list);
		XStream x = new XStream();
		String xmlString = x.toXML(dvo);
		String rstXmlStr = HttpSendUtil.postByXml(Config.YC_CAR_TYPE_URL,xmlString);
		DataVO rstDvo = (DataVO)x.fromXML(rstXmlStr);
		List<BankTypeInfoVO> bankTypeList = new ArrayList<BankTypeInfoVO>();
		if("1".equals(rstDvo.getResultCde())){
			List<Object> rtnBankTypeList = rstDvo.getList();
			for(int i=0;i<rtnBankTypeList.size();i++){
				ReturnBankTypeInfoVO rtnTypeVO = (ReturnBankTypeInfoVO)rtnBankTypeList.get(i);
				if("1".equals(rtnTypeVO.getCode())){
					bankTypeList.addAll(rtnTypeVO.getbList());
				}
			}
			rstVO.setResultCode(Constants.SUCCESS);
		}else{
			rstVO.setResultCode(Constants.ERROR);
		}
		rstVO.setResultMsg(rstDvo.getMessage());
		rstVO.setResultObject(bankTypeList);
		return rstVO;
	}
	
	/***
	 * 银行区域查询
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryBankRegionInfo")
	@ResponseBody
	public Object queryBankRegionInfo(@RequestBody ModelMap modelMap) throws Exception{
		
		ResultVO<List<StandardareasInfoVO>> rstVO = new ResultVO<List<StandardareasInfoVO>>();
		DataVO dvo = new DataVO();
		StandardareasInfoVO sdQryVO = new StandardareasInfoVO();
		List<Object> list = new ArrayList<Object>();
		list.add(sdQryVO);
		dvo.setMethod("pdaArdareasTypeQuery");
		dvo.setAction("QAndroidStandardareas");
		dvo.setList(list);
		XStream x = new XStream();
		String xmlString = x.toXML(dvo);
		String rstXmlStr = HttpSendUtil.postByXml(Config.YC_CAR_TYPE_URL,xmlString);
		DataVO rstDvo = (DataVO)x.fromXML(rstXmlStr);
		List<StandardareasInfoVO> bankRegionList = new ArrayList<StandardareasInfoVO >();
		if("1".equals(rstDvo.getResultCde())){
			List<Object> rtnBankRegionList = rstDvo.getList();
			for(int i=0;i<rtnBankRegionList.size();i++){
				ReturnStandardareasInfoVO  rtnTypeVO = (ReturnStandardareasInfoVO)rtnBankRegionList.get(i);
				if("1".equals(rtnTypeVO.getCode())){
					bankRegionList.addAll(rtnTypeVO.getList());
				}
			}
			rstVO.setResultCode(Constants.SUCCESS);
		}else{
			rstVO.setResultCode(Constants.ERROR);
		}
		rstVO.setResultMsg(rstDvo.getMessage());
		rstVO.setResultObject(bankRegionList);
		return rstVO;
	}
	
	/***
	 * 开户行信息查询
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/queryBankLocationsInfo")
	@ResponseBody
	public Object queryBankLocationsInfo(@RequestBody ModelMap modelMap) throws Exception{
		
		ResultVO<List<BankLocationsVO>> rstVO = new ResultVO<List<BankLocationsVO>>();
		String bankCode = (String)modelMap.get("bankCode"); //银行类型代码
		String ardareasCode = (String)modelMap.get("ardareasCode"); // 银行区域代码
		
		DataVO dvo = new DataVO();
		BankLocationsVO qryVO = new BankLocationsVO ();
		qryVO.setBankCode(bankCode);
		qryVO.setArdareasCode(ardareasCode);
		List<Object> list = new ArrayList<Object>();
		list.add(qryVO);
		dvo.setMethod("pdaBankLocations");
		dvo.setAction("QAndroidBankLocations");
		dvo.setList(list);
		XStream x = new XStream();
		String xmlString = x.toXML(dvo);
		String rstXmlStr = HttpSendUtil.postByXml(Config.YC_CAR_TYPE_URL,xmlString);
		DataVO rstDvo = (DataVO)x.fromXML(rstXmlStr);
		List<BankLocationsVO> bankLocationsList = new ArrayList<BankLocationsVO >();
		if("1".equals(rstDvo.getResultCde())){
			List<Object> rtnBankLocationsList = rstDvo.getList();
			for(int i=0;i<rtnBankLocationsList.size();i++){
				ReturnBankLocationsVO  rtnVO = (ReturnBankLocationsVO)rtnBankLocationsList.get(i);
				if("1".equals(rtnVO.getCode())){
					bankLocationsList.addAll(rtnVO.getLocationsList());
				}
			}
			rstVO.setResultCode(Constants.SUCCESS);
		}else{
			rstVO.setResultCode(Constants.ERROR);
		}
		rstVO.setResultMsg(rstDvo.getMessage());
		rstVO.setResultObject(bankLocationsList);
		return rstVO;
	}
	
	/***
	 * 获取险种代码列表
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/getInsList")
	@ResponseBody
	public Object getInsList(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<Object> resultVO = new ResultVO<Object>();
		String reportNo = (String)modelMap.get("reportNo"); //报案号
		String orderNo = (String)modelMap.get("orderNo"); // 任务ID
		if(StringUtil.isNullOrEmpty(reportNo)||StringUtil.isNullOrEmpty(orderNo)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("报案号["+reportNo+"]或订单号["+orderNo+"]为空");
			return resultVO;
		}
		String cmpTaskId = commonService.getCompanyTaskId(orderNo, "1");//永诚
		if(StringUtil.isNullOrEmpty(cmpTaskId)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("订单号["+orderNo+"]未查询到对应的保险公司任务ID");
			return resultVO;
		}
		boolean bumpFlag = commonService.isBumpFlagForYC(reportNo);
		PDASurveryInsQuery req = new PDASurveryInsQuery();
		req.setTaskId(cmpTaskId);
		req.setRptNO(reportNo);
		req.setBumpflag(bumpFlag?"1":"0");
		PDASurveryIns rst = AsyncInvokeUtil.getInsList(req);
		List<Map<String,String>> insList = new ArrayList<Map<String,String>>();
		if("1".equals(String.valueOf(rst.getCode()))){
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg(rst.getMessage());
			InsInfo[] insArr = rst.getInsList();
			for(int i=0;i<insArr.length;i++){
				InsInfo ins = insArr[i];
				Map<String,String> insMap = new HashMap<String,String>();
				insMap.put("value", ins.getInsCode());
				insMap.put("text", ins.getInsName());
				insList.add(insMap);
			}
		}else{
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg(rst.getMessage());
		}
		resultVO.setResultObject(insList);
		return resultVO;
	}
	
	/***
	 * 获取费用类型列表
	 * @param modelMap
	 * @return
	 * @throws Exception
	 * @author wufeng@chetong.net
	 */
	@RequestMapping("/getFeeTypeList")
	@ResponseBody
	public Object getFeeTypeList(@RequestBody ModelMap modelMap) throws Exception{
		ResultVO<Object> resultVO = new ResultVO<Object>();
		String reportNo = (String)modelMap.get("reportNo"); //报案号
		String orderNo = (String)modelMap.get("orderNo"); // 任务ID
		if(StringUtil.isNullOrEmpty(reportNo)||StringUtil.isNullOrEmpty(orderNo)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("报案号["+reportNo+"]或订单号["+orderNo+"]为空");
			return resultVO;
		}
		String cmpTaskId = commonService.getCompanyTaskId(orderNo, "1");//永诚
		if(StringUtil.isNullOrEmpty(cmpTaskId)){
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg("订单号["+orderNo+"]未查询到对应的保险公司任务ID");
			return resultVO;
		}
		boolean bumpFlag = commonService.isBumpFlagForYC(reportNo);
		PDASurveryFeeQuery req = new PDASurveryFeeQuery();
		req.setTaskId(cmpTaskId);
		req.setRptNO(reportNo);
		req.setBumpflag(bumpFlag?"1":"0");
		PDASurveryFee rst = AsyncInvokeUtil.getFeeTypeList(req);
		
		List<Map<String,String>> feeTypeList = new ArrayList<Map<String,String>>();
		if("1".equals(String.valueOf(rst.getCode()))){
			resultVO.setResultCode(Constants.SUCCESS);
			resultVO.setResultMsg(rst.getMessage());
			FeeInfo[] feeArr = rst.getFeeList();
			for(int i=0;i<feeArr.length;i++){
				FeeInfo ins = feeArr[i];
				//公估费除外
				if("0780005".equals(ins.getFeeCode())){
					continue;
				}
				Map<String,String> insMap = new HashMap<String,String>();
				insMap.put("value", ins.getFeeCode());
				insMap.put("text", ins.getFeeName());
				feeTypeList.add(insMap);
			}
		}else{
			resultVO.setResultCode(Constants.ERROR);
			resultVO.setResultMsg(rst.getMessage());
		}
		resultVO.setResultObject(feeTypeList);
		return resultVO;
	}
	
}
