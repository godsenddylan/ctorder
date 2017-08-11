package net.chetong.order.service.hycase;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.tencentpic.qcloud.PicCloud;

import net.chetong.order.common.interceptor.SubmitRepeatVerify;
import net.chetong.order.model.CtGroupVO;
import net.chetong.order.model.HyCaseTemplate;
import net.chetong.order.model.HyImageVO;
import net.chetong.order.model.HyOrderCaseVO;
import net.chetong.order.model.HyOrderTaskHaulwayVO;
import net.chetong.order.model.HyOrderTaskVO;
import net.chetong.order.model.HyVoiceVO;
import net.chetong.order.model.ParaPhotoGraphyVO;
import net.chetong.order.model.form.ReportModel;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.service.common.CommonService;
import net.chetong.order.service.hyorder.HyOrderService;
import net.chetong.order.service.user.UserPriceCalcutorService;
import net.chetong.order.service.user.UserService;
import net.chetong.order.util.Constants;
import net.chetong.order.util.CtFileUtil;
import net.chetong.order.util.ProcessCodeEnum;
import net.chetong.order.util.ResultVO;
import net.chetong.order.util.StringUtil;
import net.chetong.order.util.TencentDownloadUtil;
import net.chetong.order.util.ctenum.AreaType;
import net.chetong.order.util.exception.ProcessException;

/**
 * 货运险-案件信息
 * @author wufj@chetong.net
 *         2015年12月29日 下午4:32:31
 */
@Service("hyCaseService")
public class HyCaseServiceImpl extends BaseService implements HyCaseService{
	
	@Resource
	private UserPriceCalcutorService userPriceCalcutorService;
	@Resource
	private CommonService commonService;
	@Resource
	private UserService userService;
	@Resource
	private HyOrderService hyOrderService;
	
	/**
	 * 货运险-添加或更新订单信息
	 * @author wufj@chetong.net
	 *         2015年12月28日 下午2:58:55
	 * @param modelMap
	 * @return
	 */
	@Transactional
	@Override
	@SubmitRepeatVerify
	public ResultVO<Object> hyReport(ReportModel reportFormModel, String token) throws ProcessException{
		try {
			ResultVO<Object> resultVO = new ResultVO<Object>();
			
			//当前登录用户userId
			String curUserId = reportFormModel.getLoginUserId();
			curUserId = userService.queryCurRealUser(Long.valueOf(curUserId)).getId().toString();
			String isAfresh = reportFormModel.getIsAfresh();
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String now = sdf.format(new Date());
			
			HyOrderTaskVO hyOrderTaskVO = reportFormModel.getHyOrderTaskVO();
			
			//1.案件信息处理
			HyOrderCaseVO hyOrderCaseVO = reportFormModel.getHyOrderCaseVO();
			Long contractUserId = hyOrderCaseVO.getContractUserId();
			//不是重派单才添加案件信息
			if(!"1".equals(isAfresh)){
				//查询案件是否已经存在且不是同一机构的案件
				HyOrderCaseVO caseTemp = this.commExeSqlDAO.queryForObject("sqlmap_hy_order_case.queryCaseByCaseNo", hyOrderCaseVO.getCaseNo());
				if(caseTemp!=null){
					//查询案件是否已经被别的用户使用
					List<HyOrderTaskVO> hyOrderTaskList =  this.commExeSqlDAO.queryForList("sqlmap_hy_order_task.queryTaskInfoByCaseNo", caseTemp.getCaseNo());
					if(hyOrderTaskList.size()>0){
						HyOrderTaskVO hyOrderTask = hyOrderTaskList.get(0);
						boolean isOneEntrustName = hyOrderCaseVO.getEntrustUserName()!=null&&hyOrderCaseVO.getEntrustUserName().equals(caseTemp.getEntrustUserName());
						boolean isOneContractUserId = contractUserId!=null&&contractUserId.equals(hyOrderTask.getBuyerUserId());
						boolean isOneBuyerId = curUserId.equals(hyOrderTask.getBuyerUserId().toString());
						if(!isOneEntrustName&&!isOneContractUserId&&!isOneBuyerId){
							ProcessCodeEnum.CASE_003.buildResultVO(resultVO);
							return resultVO;
						}
					}
				}else{
					hyOrderCaseVO.setInsurerLinkman(hyOrderTaskVO.getInsurerLinkman());
					hyOrderCaseVO.setInsurerLinktel(hyOrderTaskVO.getInsurerLinktel());
					hyOrderCaseVO.setInsuredLinkman(hyOrderTaskVO.getInsuredLinkman());
					hyOrderCaseVO.setInsuredLinktel(hyOrderTaskVO.getInsuredLinktel());
					hyOrderCaseVO.setCreatedBy(curUserId);
					hyOrderCaseVO.setCreateTime(now);
					this.commExeSqlDAO.insertVO("sqlmap_hy_order_case.insertSelective", hyOrderCaseVO);
				}
			}
			
			//2.处理案件任务信息
			hyOrderTaskVO.setCaseNo(hyOrderCaseVO.getCaseNo());
			hyOrderTaskVO.setExpressAddress(null);
			//是否填写合约委托人：填写则买家为合约委托人，否则买家为当前登录账户的总账户
			if(!StringUtil.isNullOrEmpty(contractUserId)){
				hyOrderTaskVO.setIsEntrust("1");
				hyOrderTaskVO.setBuyerUserId(hyOrderCaseVO.getContractUserId());
				hyOrderTaskVO.setBuyerUserName(hyOrderCaseVO.getContractUserName());
			}else{
				hyOrderTaskVO.setIsEntrust("0");
				//查询当前登录账户的总账户（ct_group机构）
				CtGroupVO curGroup = userService.queryTopGroup(Long.valueOf(curUserId));
				hyOrderTaskVO.setBuyerUserId(Long.valueOf(curUserId));
				hyOrderTaskVO.setBuyerUserName(curGroup.getOrgName());
			}
			if(!"1".equals(isAfresh)){   //重派订单
				hyOrderTaskVO.setId(null);
				hyOrderTaskVO.setCreatedBy(curUserId);
				hyOrderTaskVO.setCreateTime(now);
				this.commExeSqlDAO.insertVO("sqlmap_hy_order_task.insertSelective", hyOrderTaskVO);
				this.commExeSqlDAO.updateVO("sqlmap_hy_order_case.updateByCaseNo", hyOrderCaseVO);   //add by jiemin  更新重新派单保险公司
			}else{
				hyOrderTaskVO.setUpdatedBy(curUserId);
				this.commExeSqlDAO.updateVO("sqlmap_hy_order_task.updateByPrimaryKeySelective", hyOrderTaskVO);
				this.commExeSqlDAO.updateVO("sqlmap_hy_order_case.updateByCaseNo", hyOrderCaseVO);   //add by jiemin  更新重新派单保险公司
			}
			Long taskId = hyOrderTaskVO.getId();
			
			//3.保存任务运输路线信息
			//先删除原有任务路线信息
			this.commExeSqlDAO.deleteVO("sqlmap_hy_order_task_haulway.deleteByTaskId", taskId);
			List<HyOrderTaskHaulwayVO> taskHaulwayList = reportFormModel.getTaskHaulwayList();
			for (int i = 0; i < taskHaulwayList.size(); i++) {
				HyOrderTaskHaulwayVO hyOrderTaskHaulwayVO = taskHaulwayList.get(i);
				hyOrderTaskHaulwayVO.setTaskId(hyOrderTaskVO.getId());//设置任务id信息
				this.commExeSqlDAO.insertVO("sqlmap_hy_order_task_haulway.insertSelective", hyOrderTaskHaulwayVO);
			}
			
			Map<String, Object> resultMap = new HashMap<String, Object>();
			resultMap.put("caseNo", hyOrderCaseVO.getCaseNo());
			resultMap.put("caseId", hyOrderCaseVO.getId());
			resultMap.put("taskId", taskId);
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, resultMap);
			return resultVO;
		} catch (Exception e) {
			log.error("货运险报案出错",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险报案出错",e);
		}
	}

	/**
	 * 货运险-获取报案信息
	 * @author wufj@chetong.net
	 *         2016年1月5日 下午5:38:16
	 * @param modelMap
	 * @return
	 */
	@Override
	public Object queryReportInfoByCaseNo(ModelMap params) {
		ReportModel reportModel = new ReportModel();
		ResultVO<Object> resultVO = new ResultVO<Object>();
		String caseNo = params.get("caseNo").toString();
		String loginUserId = (String) params.get("loginUserId");
		String contractUserId = (String) params.get("contractUserId");
		
		//1.获取案件信息
	    HyOrderCaseVO hyOrderCaseVO = this.commExeSqlDAO.queryForObject("sqlmap_hy_order_case.queryCaseByCaseNo", caseNo);
	    
	    if(hyOrderCaseVO!=null){
			reportModel.setHyOrderCaseVO(hyOrderCaseVO);
			
			Object orderNo = params.get("orderNo");
			if(StringUtil.isNullOrEmpty(orderNo)){
				//非重派
				HyOrderTaskVO hyOrderTaskVO = new HyOrderTaskVO();
				hyOrderTaskVO.setInsuredLinkman(hyOrderCaseVO.getInsuredLinkman());
				hyOrderTaskVO.setInsuredLinktel(hyOrderCaseVO.getInsuredLinktel());
				hyOrderTaskVO.setInsurerLinkman(hyOrderCaseVO.getInsurerLinkman());
				hyOrderTaskVO.setInsurerLinktel(hyOrderCaseVO.getInsurerLinktel());
				reportModel.setHyOrderTaskVO(hyOrderTaskVO);
			}else{
				//重派订单
				//2.获取任务信息
				List<HyOrderTaskVO> hyOrderTaskVOList = this.commExeSqlDAO.queryForList("sqlmap_hy_order_task.queryTaskByCaseNo", caseNo);
				if(hyOrderTaskVOList.size()>0){
					reportModel.setHyOrderTaskVO(hyOrderTaskVOList.get(0));
					//3.获取任务运输信息
					if(hyOrderTaskVOList.get(0)!=null){
						List<HyOrderTaskHaulwayVO> haulwayList = this.commExeSqlDAO.queryForList("sqlmap_hy_order_task_haulway.queryHaulwayByTaskId", hyOrderTaskVOList.get(0).getId());
						reportModel.setTaskHaulwayList(haulwayList);
					}
				}
			}
		}

	    ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, reportModel);
	    return resultVO;
	}
	
	/**
	 * 货运险-获取用户实际应付的金额
	 * @author wufj@chetong.net
	 *         2016年1月7日 下午6:11:14
	 * @param modelMap
	 * @return
	 */
	@Override
	public Object queryBuyerReapMoney(ModelMap modelMap) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		//获取买家信息
		Long buyerId = Long.valueOf(modelMap.get("buyerId").toString());
		CtGroupVO buyer = userService.queryTopGroup(buyerId);
		
		BigDecimal orderFee = new BigDecimal(modelMap.get("orderMoney").toString());
		String provDesc = modelMap.get("provDesc").toString();
		String cityDesc = modelMap.get("cityDesc").toString();
		//根据省市name获得code
		String provCode = commonService.getAreaCodeByAreaName(provDesc);
		String cityCode = null;
		if(provCode != null){ //查询市
		 cityCode = commonService.getAreaCodeByAreaName(cityDesc,AreaType.CITY,provCode);
		}
		Map<String, BigDecimal> buyerFee = this.userPriceCalcutorService.calculateHyBuyerFee(orderFee, provCode, cityCode, buyer);
	
		ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, buyerFee.get("buyerMoney"));
		return resultVO;
	}

	/**
	 * 获取案件模板
	 *         2016年1月15日 上午10:03:52
	 * @param modelMap
	 * @return
	 */
	@Override
	public Object getCaseTemplate(ModelMap modelMap) {
		ResultVO<Object> resultVO = new ResultVO<Object>();
		String orgId = modelMap.get("orgId").toString();
		String caseNo = null;
		if(!StringUtil.isNullOrEmpty(modelMap.get("caseNo"))){
		caseNo = modelMap.get("caseNo").toString();
		}
		String fileLevel = "2";
		modelMap.put("fileLevel", fileLevel);
		modelMap.put("caseNo", caseNo);
		modelMap.put("orgId", orgId);
		try{
		List<HyCaseTemplate> hyCaseTemplate = this.commExeSqlDAO.queryForList("sqlmap_hy_order_case.queryHyCaseTemplate", modelMap);
		if(null == hyCaseTemplate || 0 == hyCaseTemplate.size()){
			fileLevel = "1";
			modelMap.put("fileLevel", fileLevel);
			modelMap.remove("caseNo");
			hyCaseTemplate = this.commExeSqlDAO.queryForList("sqlmap_hy_order_case.queryHyCaseTemplate", modelMap);
			if(null == hyCaseTemplate || 0 == hyCaseTemplate.size()){
				fileLevel = "0";
				modelMap.put("fileLevel", fileLevel);
				modelMap.remove("caseNo");
				modelMap.remove("orgId");
				hyCaseTemplate = this.commExeSqlDAO.queryForList("sqlmap_hy_order_case.queryHyCaseTemplate", modelMap);
				
			}
		}
		Map<String,Object> resultMap = new HashMap<String,Object>();
		resultMap.put("hyCaseTemplate", hyCaseTemplate);
		ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, hyCaseTemplate);
	    return resultVO;
		} catch (Exception e) {
			log.error("获取案件模板失败:"+orgId+"---"+caseNo+":"+e);
			throw ProcessCodeEnum.FAIL.buildProcessException("获取案件模板异常", e);
		}
	}
	
	/**
	 * 下载案件详情
	 * @author wufj@chetong.net
	 *         2016年1月15日 上午10:42:08
	 * @param modelMap
	 * @param request
	 * @param response
	 */
	@Override
	public ResultVO<Object> downloadCase(ModelMap modelMap, HttpServletRequest request, HttpServletResponse response) throws ProcessException{
		log.info("下载案件详情开始："+modelMap);
		//下载文件夹
		StringBuilder downPath = new StringBuilder(request.getSession().getServletContext().getRealPath("/")).append(Constants.DOWNLOAD_TEMP_DIR);
        //String downPath = "f:/"+"downloadCarCase/";
        String uuid = UUID.randomUUID().toString();
        String fileTempPath = downPath.append(uuid).toString();
        String zipFileName=fileTempPath+".zip";
		
		try {
			//创建案件目录
			CtFileUtil.isDir(fileTempPath);
			
//			//1.下载图片
//			List<HyImageVO> hyImageVOList = this.commExeSqlDAO.queryForList("sqlmap_hy_image.queryHyImages", modelMap);
//			//创建图片目录
//			String imgTempPath = fileTempPath+"/图片文件/";
//			CtFileUtil.isDir(imgTempPath);
//			for (int i = 0; i < hyImageVOList.size(); i++) {
//				HyImageVO hyImageVO = hyImageVOList.get(i);
//				String link = hyImageVO.getLink();
//				String picName = "pic"+i+".jpg";
//				//下载图片到本地服务器
//				PicCloud pc = new PicCloud(Constants.APP_ID, Constants.SECRET_ID, Constants.SECRET_KEY);
//				pc.Download(link, imgTempPath+picName);
//			}
			//创建图片目录
			String imgTempPath = fileTempPath+"/图片文件/";
			CtFileUtil.isDir(imgTempPath);
			String caseNo = (String) modelMap.get("caseNo");
			this.createPhotoTabDirs(imgTempPath,caseNo);
			
			//2.下载语音文件
			List<HyVoiceVO> voicoList = this.commExeSqlDAO.queryForList("sqlmap_hy_voice.queryHyVoiceList", modelMap);
			//创建语音目录
			String cosTempPath = fileTempPath+"/语音文件/";
			CtFileUtil.isDir(cosTempPath);
			for (int i = 0; i < voicoList.size(); i++) {
				HyVoiceVO voice = voicoList.get(i);
				String fileLink = voice.getFileLink();
				TencentDownloadUtil.downloadByUrl(voice.getFileLink(), cosTempPath+voice.getRecordTime().replaceAll("[^0-9]", "")+fileLink.substring(fileLink.lastIndexOf("."), fileLink.length()));
			}
			
			//打zip包
			CtFileUtil.zip(fileTempPath,zipFileName);
			
			//放入响应流中
			CtFileUtil.exportFile(response,new File(zipFileName),true);
			
			ResultVO<Object> resultVO = new ResultVO<>();
			ProcessCodeEnum.SUCCESS.buildResultVO(resultVO, uuid);
            return resultVO;
		} catch (Exception e) {
			log.error("货运险下载案件详情出错",e);
			throw ProcessCodeEnum.FAIL.buildProcessException("货运险下载案件详情出错",e);
		} finally {
			//清除之前的下载文件
			CtFileUtil.delDir(new File(fileTempPath));
			CtFileUtil.delDir(new File(zipFileName));
		}
	}

	/**
	 * @Description: 生成图片文件夹
	 * @param imgTempPath
	 * @param caseNo
	 * @return void
	 * @author zhouchushu
	 * @date 2016年4月6日 上午9:55:29
	 */
	private void createPhotoTabDirs(String imgTempPath, String caseNo) {
		List<HyOrderTaskVO> orders = commExeSqlDAO.queryForList("sqlmap_hy_order_task.queryTaskInfoByCaseNo", caseNo);
		for (HyOrderTaskVO order : orders) {
			//查询图片信息
			List<HyImageVO> imgList = commExeSqlDAO.queryForList("sqlmap_hy_image.queryHyImagesByOrderNo", order.getOrderNo());
			Map<Long,List<HyImageVO>> imgMap = imgSortOut(imgList);
			//查询订单图表
			String orderFilePath = imgTempPath+"/"+order.getOrderNo()+"/";
			CtFileUtil.isDir(orderFilePath);
			String transportType = order.getTransportType();
			String[] transportCode = transportType.split(",");
			for (String parentCode : transportCode) {
				//第一级
				String parent = "0"+parentCode+"0000";
				ParaPhotoGraphyVO photoGraphyVO = commExeSqlDAO.queryForObject("sqlmap_para_photography.selectByPhotoCode", parent);
				String path = orderFilePath+"/"+photoGraphyVO.getPhotoName()+"/";
				CtFileUtil.isDir(path);
				createPhotoTab(imgMap,parent,path);
			}
		}
		
	}

	/**
	 * @Description: 生成标签文件夹
	 * @param imgMap
	 * @param parent
	 * @return void
	 * @author zhouchushu
	 * @date 2016年4月6日 下午4:25:34
	 */
	private void createPhotoTab(Map<Long, List<HyImageVO>> imgMap, String photoCode,String imgTempPath) {

		List<ParaPhotoGraphyVO> secPhotoList = commExeSqlDAO.queryForList("sqlmap_para_photography.queryParaPhotoGraphyByParentCode", photoCode);
		for (ParaPhotoGraphyVO secPhoto : secPhotoList) {
			String secPath = imgTempPath+"/"+secPhoto.getPhotoTypeName()+"/";
			CtFileUtil.isDir(secPath);
			List<ParaPhotoGraphyVO> thirdPhotoList = commExeSqlDAO.queryForList("sqlmap_para_photography.queryParaPhotoGraphyByParentCode", secPhoto.getPhotoCode());
			for (ParaPhotoGraphyVO thrPhoto : thirdPhotoList) {
				List<HyImageVO> hyImageVOList = imgMap.get(thrPhoto.getParaId());
				String thrPath = secPath+"/"+thrPhoto.getPhotoName()+"/";
				CtFileUtil.isDir(thrPath);
				if(hyImageVOList != null && hyImageVOList.size() > 0 ){
					for (int i = 0; i < hyImageVOList.size(); i++) {
						HyImageVO hyImageVO = hyImageVOList.get(i);
						String link = hyImageVO.getLink();
						String picName = "pic"+i+".jpg";
						//下载图片到本地服务器
						PicCloud pc = new PicCloud(Constants.APP_ID, Constants.SECRET_ID, Constants.SECRET_KEY);
						pc.Download(link, thrPath+picName);
					}
					
				}
				
			}
		}

	}

	/**
	 * @Description: 图片根据标签id分类
	 * @param imgList
	 * @return
	 * @return Map<String,List<HyImageVO>>
	 * @author zhouchushu
	 * @date 2016年4月6日 下午2:12:25
	 */
	private Map<Long, List<HyImageVO>> imgSortOut(List<HyImageVO> imgList) {
		Map<Long, List<HyImageVO>> imgMap = new HashMap<Long, List<HyImageVO>>();
		for (HyImageVO hyImageVO : imgList) {
			if(null == imgMap.get(hyImageVO.getTagId())){
				List<HyImageVO> imgs = new ArrayList<HyImageVO>();
				imgs.add(hyImageVO);
				imgMap.put(hyImageVO.getTagId(), imgs);
			}else{
				List<HyImageVO> imgs = imgMap.get(hyImageVO.getTagId());
				imgs.add(hyImageVO);
			}
		}
		return imgMap;
	}
}
