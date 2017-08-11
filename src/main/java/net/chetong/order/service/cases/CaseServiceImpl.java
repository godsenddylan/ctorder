package net.chetong.order.service.cases;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.tencentpic.qcloud.PicCloud;

import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FhCarModelVO;
import net.chetong.order.model.FhDamageModelVO;
import net.chetong.order.model.FhLeaveModelVO;
import net.chetong.order.model.FhLossImageVO;
import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhPartModelVO;
import net.chetong.order.model.FhRepairModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.model.FmOrderCase;
import net.chetong.order.model.FmOrderCaseVO;
import net.chetong.order.model.RsOrder;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.CtFileUtil;
import net.chetong.order.util.DocHandler;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.exception.DaoException;
import net.chetong.order.util.exception.ProcessException;

@Service("caseService")
public class CaseServiceImpl extends BaseService implements CaseService {

//	private static Logger log = LogManager.getLogger(CaseServiceImpl.class);

	@Resource
	private UserService userService;
	
	@Resource
	private ImageService imageservice;

	/**
	 * 获取报案信息
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public ResultVO<FmOrderCaseVO> queryCaseInfo(Map params) throws ProcessException {
		log.info("获取报案信息：" + params);
		ResultVO<FmOrderCaseVO> resultVO = new ResultVO<FmOrderCaseVO>();
		try {
			FmOrderCaseVO caseVO = commExeSqlDAO.queryForObject("fm_order_case.queryCaseInfoList", params);
			if (StringUtil.isNullOrEmpty(caseVO)) {
				ProcessCodeEnum.FAIL.buildResultVO(resultVO, caseVO);
			} else {
				ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, caseVO);
			}
		} catch (DaoException e) {
			log.info("获取报案信息异常：" + e);
			ProcessCodeEnum.FAIL.buildResultVO(resultVO);
			throw ProcessCodeEnum.DATA_ERR.buildProcessException(e);
		}
		return resultVO;
	}

	@Override
	public List<FmOrderCaseVO> queryCase(Map<String, String> params) {
		return commExeSqlDAO.queryForList("fm_order_case.queryCaseInfoList", params);
	}	

	@Override
	public List<FmOrderCaseVO> queryCaseByCaseNo(Map<String, String> params) {
		return commExeSqlDAO.queryForList("fm_order_case.querySingleCaseInfoList", params);
	}
	
	
	
	@Transactional
	public void updateCaseStatus(String caseId, String status) {
		
		FmOrderCaseVO orderCase = new FmOrderCaseVO();		
		orderCase.setId(caseId);
		orderCase.setStatus(status);
		commExeSqlDAO.updateVO("fm_order_case.updateCase", orderCase);
	}
	    
	
	/**
	 * 下载案件详情
	 * @author wufj@chetong.net 2015年12月9日 下午2:54:19
	 * @param modelMap
	 * @param request
	 * @param response
	 */
	@Override
	public ResultVO<Object> downloadCase(ModelMap modelMap, HttpServletRequest request) throws ProcessException{
		log.info("下载案件详情开始："+modelMap);
	 	String caseNo=StringUtil.trimToNull(modelMap.get("caseNo"));
        String isGroupMan=StringUtil.trimToNull(modelMap.get("isGroupMan"));//是否团队管理者
        //下载临时文件夹
        StringBuilder downPath = new StringBuilder(request.getSession().getServletContext().getRealPath("/")).append(Constants.DOWNLOAD_TEMP_DIR);
        //String downPath = "f:/"+"downloadCarCase/";
        String uuid = UUID.randomUUID().toString();
        String fileTempPath = downPath.append(uuid).toString();
        String zipFileName=fileTempPath+".zip";
        
        try {
        	ResultVO<Object> resultVO = new ResultVO<Object>();
            if(StringUtil.isNullOrEmpty(caseNo)){  //报案号不为空
            	ProcessCodeEnum.REQUEST_PARAM_NULL.buildResultVO(resultVO);
                return resultVO;
            }
            
            Map<String,Object> result=this.queryAuditedOrderByCaseNo(caseNo, isGroupMan);
            if(result==null || !(Boolean)result.get("success")){
                throw ProcessCodeEnum.FAIL.buildProcessException("下载案件详情失败");
            }
            //如果没有审核通过的订单，直接返回不可下载
            if(result.get("surveyInfo")==null&&result.get("lossListInfo")==null&&result.get("damageList")==null && result.get("rsOrderListInfo") == null){
            	ProcessCodeEnum.DOWNLOAD_NO_AUDITED.buildResultVO(resultVO);
            	return resultVO;
            }
            
			//创建案件目录
			CtFileUtil.isDir(fileTempPath);
			
            //生成查勘文档
            DocHandler docHandler=new DocHandler();

            //创建清单文件
            String listItemPath=fileTempPath+"/文档生成信息.txt";
            List<FhLossImageVO> imgArr=(List<FhLossImageVO>)result.get("imgList");
            List<Map<String, Object>> carList=(List<Map<String, Object>>)result.get("carList");
            
            //从腾讯云下载图片到本地服务器
            Long beforeTimeAll = System.currentTimeMillis();
            Long beforeTime = System.currentTimeMillis();
//            this.imageservice.downFmCaseImages(fileTempPath, imgArr, carList);
            this.imageservice.downFmCaseImagesThread(fileTempPath, imgArr, carList);
            Long afterTime = System.currentTimeMillis();
            log.warn("图片下载时间耗时:"+(afterTime-beforeTime)/1000);
            
             //下载人伤图片
	        this.handleRsImage(caseNo,fileTempPath);
            File listItem=new File(listItemPath);
            if(!listItem.exists()){
                boolean c=listItem.createNewFile();
            }
            docHandler.createSurveyDoc(fileTempPath+"/",result,caseNo,listItemPath);
            docHandler.createLossDoc(fileTempPath+"/",result,caseNo,listItemPath);

            //打包文件
            
            CtFileUtil.zip(fileTempPath,zipFileName);
            Long afterTimeAll = System.currentTimeMillis();
            log.warn("图片+压缩下载时间总耗时:"+(afterTimeAll-beforeTimeAll)/1000);
            ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, uuid);
            return resultVO;
        } catch (Exception e) {
            log.error("导出案件详情失败",e);
            throw ProcessCodeEnum.FAIL.buildProcessException("导出案件详情失败",e);
        }finally {
        	//清除之前的下载文件
        	CtFileUtil.delDir(new File(fileTempPath));
		}
	}
	
	 /**
	 * 查询一个案件下所有的订单
	 * @author wufj@chetong.net
	 *         2015年12月14日 上午9:14:18
	 * @param reportmap
	 * @return
	 */
    private Map<String, Object> queryAuditedOrderByCaseNo(String caseNo, String isGroupMan) {
		Map<String, Object> map = new HashMap<String, Object>();
		String guid = null;
		try {
			Map<String, String> rawParams = new HashMap<String, String>();
			rawParams.put("caseNo", caseNo);
			if("0".equals(isGroupMan)){
				rawParams.put("taskState", "9");
			}
			// 查询案件下所有已经审核通过的订单
			FhSurveyModelVO surveyModel = commExeSqlDAO.queryForObject("sqlmap_work_model.querySurveyModelInCase", rawParams); //查勘单
			List<FhLossModelVO> lossModelList = commExeSqlDAO.queryForList("sqlmap_work_model.queryLossModelInCase", rawParams);//定损单
			List<RsOrder> rsOrderList = this.commExeSqlDAO.queryForList("renshang_sqlmap_rs_order.getAuditOrderByCaseNo",caseNo); //人伤订单
			List<String> orderCodeArr = new ArrayList<String>();
			//处理查勘单信息
			if (surveyModel != null) {
				guid = surveyModel.getGuid();
				map.put("surveyInfo", surveyModel);
				orderCodeArr.add(surveyModel.getOrderCode());
				// 查询三者车辆
				Map<String, String> carParams = new HashMap<String, String>();
				carParams.put("guid", surveyModel.getGuid());
				carParams.put("isMain", "0");
				List<FhCarModelVO> carList = commExeSqlDAO.queryForList("sqlmap_fh_car_model.queryCarModelList", carParams);
				map.put("threeCarInfo", carList);
			}
			//处理定损单信息
			if (lossModelList!=null && lossModelList.size()>0) {
				map.put("lossListInfo", lossModelList);
				List<String> damageArr = new ArrayList<String>();
				List<Long> lossArr = new ArrayList<Long>();
				for (FhLossModelVO lossModel : lossModelList) {
					guid = lossModel.getGuid();
					String isSubject = lossModel.getIsSubject();
					if (isSubject != null && "3".equals(isSubject)) {
						damageArr.add(lossModel.getOrderCode());
					} else {
						lossArr.add(lossModel.getId());
						//将每个订单的查询配件、维修项目信息与订单关联起来，订单对应自己的查询配件和维修项目
						HashMap<String, String> queryPartsParam = new HashMap<String, String>();
						Long lossId = lossModel.getId();
						queryPartsParam.put("taskId", lossId.toString());
						queryPartsParam.put("guid", lossModel.getGuid());
						//换件项目
						List<FhPartModelVO> partList = commExeSqlDAO.queryForList("sqlmap_fh_part_model.queryPartByIdAndGuid", queryPartsParam);
						//维修项目
						List<FhRepairModelVO> repairList = commExeSqlDAO.queryForList("sqlmap_fh_repair_model.queryRepairByIdAndGuid", queryPartsParam);
						map.put("partList"+lossId, partList);
						map.put("repairList"+lossId, repairList);
					}
					orderCodeArr.add(lossModel.getOrderCode());
				}
				
				// 查询物损项目
				if (damageArr.size() > 0) {
					List<FhDamageModelVO> damageList = commExeSqlDAO.queryForList("sqlmap_fh_damage_model.findDamageByOrderCode", damageArr);
					map.put("damageList", damageList);
				}
			}
			if (orderCodeArr.size() > 0) {
				// 查询留言
				List<FhLeaveModelVO> leaveList = commExeSqlDAO.queryForList("sqlmap_fh_leave_model.queryLeaveMessageByOrderNos", orderCodeArr);
				map.put("leaveList", leaveList);
				// 查询审核信息
				List<FhAuditModelVO> auditList = commExeSqlDAO.queryForList("sqlmap_fh_audit_model.queryAuditMessageByOrderNos", orderCodeArr);
				map.put("auditList", auditList);
			}
			if (guid != null) {
				//查询订单图片信息
				Map<String, Object> paramMap = new HashMap<>();
				paramMap.put("guid", guid);
				paramMap.put("ids", null);
				List<FhLossImageVO> Imglist = commExeSqlDAO.queryForList("sqlmap_image.queryImageByGuid", paramMap);
				map.put("imgList", Imglist);
				// 查询已经生成订单的三者车
				List<Map<String, Object>> carList = commExeSqlDAO.queryForList("sqlmap_fh_loss_model.queryAllCarByGuid", guid);
				map.put("carList", carList);
			}
			
			if(null != rsOrderList && rsOrderList.size() > 0){
				map.put("rsOrderListInfo",rsOrderList);
			}
			
			map.put("success", true);
		} catch (Exception e) {
			log.error("查询案件下订单出错",e);
			map.put("success", false);
		}
		return map;
	}

	/** (non-Javadoc)
	 * @Description: 查询案件信息
	 * @param paraMap
	 * @return
	 * @author zhouchushu
	 * @date 2016年1月29日 下午8:49:14
	 * @see net.chetong.order.service.cases.CaseService#querySimpleCaseByCaseNo(java.util.Map)
	 */
	@Override
	public List<FmOrderCase> querySimpleCaseByCaseNo(Map<String, String> paraMap) {
		return commExeSqlDAO.queryForList("fm_order_case.querySimpleCaseByCaseNo", paraMap);
	}

	private  void handleRsImage(String caseNo,String filePath){
		String hospitalStr = "医院探视";
		String mediateStr = "一次性调解";
		List<Map<String,Object>> allOrderList = this.commExeSqlDAO.queryForList("renshang_sqlmap_rs_order.getAllOrderByCaseNo",caseNo);
		if(null != allOrderList && allOrderList.size() > 0){
			//伤者归类
			Map<String,List<String>> personMap = new HashMap<String,List<String>>();
			//标签对应路径
			Map<String,String> tagNameMap = new HashMap<String,String>();
			//伤者对应的图片集合
			Map<String,List<Map<String,String>>> personImageMap = new HashMap<String,List<Map<String,String>>>();
			String nameStr = null;
			String subjectId = null;
			String orderNo = null;
			Map<String,String> paramMap = new HashMap<String,String>();
			int tagId ;
			String tagName = null;
			paramMap.put("caseNo", caseNo);
			for(Map<String,Object> map : allOrderList){
				 subjectId = String.valueOf(map.get("subjectId"));
				 orderNo = String.valueOf(map.get("orderNo"));
				 nameStr = (String)map.get("injuredName")+"_"+orderNo;
				 if(Constants.RS_SUBJECT_HOSPITAL.equals(subjectId)){
					 if(personMap.containsKey(hospitalStr)){
						 List<String> hosNameList = personMap.get(hospitalStr);
						 hosNameList.add(nameStr);
						 personMap.put(hospitalStr, hosNameList);
					 }else{
						 List<String> list = Arrays.asList(new String[]{nameStr});
						 personMap.put(hospitalStr,new ArrayList<String>(list));
					 }
				 }else if(Constants.RS_SUBJECT_MEDIATE.equals(subjectId)){
					 if(personMap.containsKey(mediateStr)){
						 List<String> mediateNameList = personMap.get(mediateStr);
						 mediateNameList.add(nameStr);
						 personMap.put(mediateStr,mediateNameList);
					 }else{
						 List<String> list = Arrays.asList(new String[]{nameStr});
						 personMap.put(mediateStr,new ArrayList<String>(list));
					 }
				 }
				 paramMap.put("orderNo", orderNo);
				 List<Map<String,String>> imageList = this.commExeSqlDAO.queryForList("renshang_sqlmap_rs_order.getMediaImageOfOrder",paramMap);
				 for(Map<String,String> imageMap : imageList){
					 tagId = Integer.parseInt(String.valueOf(imageMap.get("tagId")));
					 if(tagNameMap.containsKey(String.valueOf(tagId))){
						 imageMap.put("tagName",tagNameMap.get(String.valueOf(tagId)));
					 }else{
						  tagName = this.commExeSqlDAO.queryForObject("renshang_sqlmap_rs_order.getImagePathTagName",tagId);
						  tagNameMap.put(String.valueOf(tagId),tagName+"/");
						  imageMap.put("tagName",tagName+"/");
					 }
				 }
				 personImageMap.put(nameStr,imageList);
			}
			
			downLoadRsImage(personMap,personImageMap,filePath);
		}
	}
	  
	@SuppressWarnings("unused")
	private void downLoadRsImage(Map<String,List<String>> personMap,Map<String,List<Map<String,String>>> personImageMap,String filePath){
		try{
			String topDir = null;
			String tagDir = null;
			List<String> personList = new ArrayList<String>();
			List<Map<String,String>> imageList = new ArrayList<Map<String,String>>();
			String imageUrl = null;
			String fileName = null;
			 PicCloud pc = new PicCloud(Constants.APP_ID, Constants.SECRET_ID, Constants.SECRET_KEY);
			for(Map.Entry<String,List<String>> entry : personMap.entrySet()){
				 topDir=new String((filePath+"/"+entry.getKey()+"/").getBytes(), "utf-8");
				 personList = entry.getValue();
				 for(String personName:personList){
					 imageList = personImageMap.get(personName);
					 for(Map<String,String> imageMap : imageList){
						 imageUrl = String.valueOf(imageMap.get("imageUrl"));
						 tagDir = topDir +personName + "/" + String.valueOf(imageMap.get("tagName"));
						 fileName = tagDir + String.valueOf(imageMap.get("fileName"));
						 CtFileUtil.isDir(tagDir);
						 pc.Download(imageUrl, fileName);
					 }
				 }
			}
		}catch(Exception e){
			throw new  ProcessException(ProcessCodeEnum.FAIL.getCode(),"下载人伤照片失败");
		}	
	}
	
}
