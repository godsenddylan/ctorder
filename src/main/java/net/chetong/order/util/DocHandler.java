package net.chetong.order.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.tencentpic.fhpic.model.AuditModel;
import com.tencentpic.fhpic.model.CarModel;
import com.tencentpic.fhpic.model.DamageModel;
import com.tencentpic.fhpic.model.LeaveModel;
import com.tencentpic.fhpic.model.PartModel;

import freemarker.template.Configuration;
import freemarker.template.Template;
import net.chetong.order.model.FhAuditModelVO;
import net.chetong.order.model.FhCarModelVO;
import net.chetong.order.model.FhDamageModelVO;
import net.chetong.order.model.FhLeaveModelVO;
import net.chetong.order.model.FhLossModelVO;
import net.chetong.order.model.FhPartModelVO;
import net.chetong.order.model.FhRepairModelVO;
import net.chetong.order.model.FhSurveyModelVO;
import net.chetong.order.service.common.BaseService;

/**
 * 案件下载 生成word文件
 * @author wufj@chetong.net
 *         2015年12月14日 下午3:47:57
 */
public class DocHandler {
	private static Logger log = LogManager.getLogger(BaseService.class);
    private Configuration configuration=null;

    public DocHandler() throws Exception{
        configuration=new Configuration();
        configuration.setDefaultEncoding("UTF-8");
        File tplPath = new File(Thread.currentThread().getContextClassLoader().getResource("/templates").getPath());
        configuration.setDirectoryForTemplateLoading(tplPath);
    }

    /**
     * 描述文件写入信息
     */
    private void writeDesc(String file,String content){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
    	Date now = new Date();
        content=sdf.format(now) + "："+content.substring(content.lastIndexOf("/")+1)+"\r\n";
        BufferedWriter out=null;
        try {
            out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,true)));
            out.write(content);
            out.flush();
        } catch (Exception e) {
            log.error("写入描述文件信息失败",e);
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *  创建查勘现场文档  -1代表没有现场查勘 ，-2代表生成文档是出错
     */
    public String createSurveyDoc(String baseUrl,Map<String,Object> result,String reportNo,String listItemPath){
        Map<String,Object> dataMap=getSurveyData(result);
        if(dataMap==null){
            return "-1";
        }
        Writer out=null;
        String path=baseUrl+reportNo+"-"+dataMap.get("carMark").toString().replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "-")+"(现场查勘记录单).doc";
        try {
            Template tpl=configuration.getTemplate("ck2.ftl");
            path = path.replaceAll("\\\\", "/");
            File outFile=new File(path);
            out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
            tpl.process(dataMap,out);
        } catch (Exception e) {
        	log.error("下载案件详情生成查勘doc失败",e);
            //生成错误信息
            writeDesc(listItemPath,path+" -- 生成失败！");
            return "-2";
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeDesc(listItemPath,path+" -- 生成成功！");
        return path;
    }

    /**
     * 创建定损单文档
     */
    public List<String> createLossDoc(String baseUrl,Map<String,Object> result,String reportNo,String listItemPath){
        List<String> listPath=new ArrayList<String>();
        List<FhLossModelVO> lossArr = (List<FhLossModelVO>)result.get("lossListInfo");
        if(lossArr==null || lossArr.size()<1){
            return listPath;
        }
        for(int i=0;i<lossArr.size();i++){
        	FhLossModelVO item=lossArr.get(i);
                if(item.getIsSubject().equals("3")){//物损
                    createDamageDoc(item,result,baseUrl,reportNo,listItemPath);
                }else if(item.getIsSubject().equals("2")){//三者定损
                    createSZLossDoc(item, result, baseUrl, reportNo,listItemPath);
                }else if(item.getIsSubject().equals("1")){//标的定损
                    createMainLossDoc(item,result,baseUrl,reportNo,listItemPath);
                }
        }
        return listPath;
    }

    private String createDamageDoc(FhLossModelVO item,Map<String,Object> result,String baseUrl,String reportNo,String listItemPath){
        Map<String,Object> dataMap=getDamageData(item, result);
        Writer out=null;
        String path=baseUrl+reportNo+"-"+item.getCarMark().toString().replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "-")+"(物损定损单).doc";
        try {
            Template tpl=configuration.getTemplate("ws2.ftl");
            path = path.replaceAll("\\\\", "/");
            File outFile=new File(path);
            out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
            tpl.process(dataMap,out);
        } catch (Exception e) {
        	log.error("下载案件详情生成物损doc失败" ,e);
            //生成错误信息
            writeDesc(listItemPath,path+" -- 生成失败！");
            return "-2";
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeDesc(listItemPath,path+" -- 生成成功！");
        return path;
    }

    private Map<String,Object> getDamageData(FhLossModelVO item,Map<String,Object> result){
        Map<String,Object> dataMap=new HashMap<String,Object>();
        dataMap.put("serviceName",item.getServiceName()== null ? "":item.getServiceName());
        dataMap.put("principalName",item.getPrincipalName()== null ? "":item.getPrincipalName());
        dataMap.put("principalTime",item.getPrincipalTime()== null ? "":item.getPrincipalTime());

        dataMap.put("contactName",item.getContactName()== null ? "":item.getContactName());
        dataMap.put("contactPhone",item.getContactPhone()== null ? "":item.getContactPhone());
        dataMap.put("principalAuth",item.getPrincipalAuth()== null ? "":item.getPrincipalAuth());

        dataMap.put("addressPlace",item.getAddressPlace()== null ? "":item.getAddressPlace());

        dataMap.put("principalInfo",item.getPrincipalInfo()== null ? "":item.getPrincipalInfo());

        dataMap.put("lossTotal","0");
        
        BigDecimal auditAmount = BigDecimal.ZERO;

        List<FhDamageModelVO> damageArr = (List<FhDamageModelVO>)result.get("damageList");
        List<DamageModel> damageInfoList=new ArrayList<DamageModel>();
        if(damageArr!=null && damageArr.size()>0){
            double lossTotal=0;
            for(int i=0;i<damageArr.size();i++){
            	FhDamageModelVO model=damageArr.get(i);
                DamageModel damageModel=new DamageModel();
                damageModel.setId(i);
                damageModel.setProject(model.getProject() ==null?"":model.getProject());
                damageModel.setStandard(model.getStandard() ==null?"":model.getStandard());
                damageModel.setNum(model.getNum() ==null?"":model.getNum());
                damageModel.setUnit(model.getUnit() ==null?"":model.getUnit());
                damageModel.setPrice(model.getPrice() ==null?"":model.getPrice());
                damageModel.setSubtotal(model.getSubtotal() ==null?"":model.getSubtotal());
                if(damageModel.getSubtotal()==null || damageModel.getSubtotal().equals("")){
                    lossTotal+=0;
                }else{
                    lossTotal+=Double.parseDouble(damageModel.getSubtotal());
                }
                damageModel.setAuditPrice(model.getAuditPrice() == null ?"":model.getAuditPrice());
                damageModel.setExplain(model.getExplain() ==null?"":model.getExplain());
                if(null != model.getAuditPrice()&&!"".equals(model.getAuditPrice())){
                	auditAmount = auditAmount.add(new BigDecimal(model.getAuditPrice()));
                }
                damageInfoList.add(damageModel);
            }
            dataMap.put("lossTotal",lossTotal);
            dataMap.put("auditAmount", auditAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        dataMap.put("table1",damageInfoList);
        //审核信息
        List<AuditModel> preAuditHeader=new ArrayList<AuditModel>();
        AuditModel preAuditModel=new AuditModel();
        preAuditModel.setAuditResult("0");
        preAuditHeader.add(preAuditModel);
        dataMap.put("preAuditHeader",preAuditHeader);
        List<FhAuditModelVO> preAuditContent=new ArrayList<FhAuditModelVO>();

        List<FhAuditModelVO> auditInfoList=new ArrayList<FhAuditModelVO>();
        List<FhAuditModelVO> auditArr = (List<FhAuditModelVO>)result.get("auditList");
        if(auditArr!=null && auditArr.size()>0){
            for (int i=0;i<auditArr.size();i++){
            	FhAuditModelVO model=auditArr.get(i);
                if(item.getOrderCode().equals(model.getOrderCode())){
                    if(model.getAuditType().equals("2")){
                        auditInfoList.add(model);
                    }else{
                        preAuditContent.add(model);
                    }
                }
            }
        }
        dataMap.put("preAuditContent",preAuditContent);
        dataMap.put("table2",auditInfoList);
        //留言信息
        List<LeaveModel> leaveInfoList=new ArrayList<LeaveModel>();
        List<FhLeaveModelVO> leaveArr = (List<FhLeaveModelVO>)result.get("leaveList");
        if(leaveArr!=null && leaveArr.size()>0){
            for (int i=0;i<leaveArr.size();i++){
            	FhLeaveModelVO model=leaveArr.get(i);
                if(item.getOrderCode().equals(model.getReserved())){
                    LeaveModel leaveModel=new LeaveModel();
                    leaveModel.setName(model.getName() ==null?"":model.getName());
                    leaveModel.setInsertTime(model.getInsertTime() ==null?"":model.getInsertTime());
                    leaveModel.setDetail(model.getDetail() ==null?"":model.getDetail());
                    leaveInfoList.add(leaveModel);
                }
            }
        }
        dataMap.put("table3",leaveInfoList);
        return dataMap;
    }

    private String createSZLossDoc(FhLossModelVO item,Map<String,Object> result,String baseUrl,String reportNo,String listItemPath){
        Map<String,Object> dataMap=getLossData(item,result);
        Writer out=null;
        String path=baseUrl+reportNo+"-"+item.getCarMark().toString().replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "-")+"(三者定损单).doc";
        try {
            Template tpl=configuration.getTemplate("ds2.ftl");
            path = path.replaceAll("\\\\", "/");
            File outFile=new File(path);
            out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
            tpl.process(dataMap,out);
        } catch (Exception e) {
        	log.error("下载案件详情生成三者定损doc失败",e);
            //生成错误信息
            writeDesc(listItemPath,path+" -- 生成失败！");
            return "-2";
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeDesc(listItemPath,path+" -- 生成成功！");
        return path;
    }

    private String createMainLossDoc(FhLossModelVO item,Map<String,Object> result,String baseUrl,String reportNo,String listItemPath){
        Map<String,Object> dataMap=getLossData(item,result);
        Writer out=null;
        String path=baseUrl+reportNo+"-"+item.getCarMark().toString().replaceAll("[^a-zA-Z0-9\u4e00-\u9fa5]", "-")+"(标的定损单).doc";
        try {
            Template tpl=configuration.getTemplate("ds2.ftl");
            path = path.replaceAll("\\\\", "/");
            File outFile=new File(path);
            out=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outFile),"UTF-8"));
            tpl.process(dataMap,out);
        } catch (Exception e) {
        	log.error("下载案件详情生成标的定损订单失败",e);
            //生成错误信息
            writeDesc(listItemPath,path+" -- 生成失败！");
            return "-2";
        }finally {
            try {
                if(out!=null){
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writeDesc(listItemPath,path+" -- 生成成功！");
        return path;
    }

    private Map<String,Object> getLossData(FhLossModelVO item,Map<String,Object> result){
        Map<String,Object> dataMap=new HashMap<String,Object>();
        dataMap.put("serviceName",item.getServiceName()== null ? "":item.getServiceName());
        dataMap.put("principalName",item.getPrincipalName()== null ? "":item.getPrincipalName());
        dataMap.put("principalTime",item.getPrincipalTime()== null ? "":item.getPrincipalTime());

        dataMap.put("contactName",item.getContactName()== null ? "":item.getContactName());
        dataMap.put("contactPhone",item.getContactPhone()== null ? "":item.getContactPhone());
        dataMap.put("principalAuth",item.getPrincipalAuth()== null ? "":item.getPrincipalAuth());

        dataMap.put("addressPlace",item.getAddressPlace()== null ? "":item.getAddressPlace());

        dataMap.put("principalInfo",item.getPrincipalInfo()== null ? "":item.getPrincipalInfo());

        dataMap.put("carMark",item.getCarMark()== null ? "":item.getCarMark());
        dataMap.put("vehicleModel",item.getVehicleModel()== null ? "":item.getVehicleModel());

        dataMap.put("repairFtName",item.getRepairFtName()== null ? "":item.getRepairFtName());
        
        //核损金额
        BigDecimal auditAmount = BigDecimal.ZERO;
        
        //2015-7-29 维修厂资质信息显示文字，不显示编号
        //dataMap.put("repairFtType",item.getRepairFtType()== null ? "":item.getRepairFtType());
        String repairFtType = item.getRepairFtType();
        if(StringUtil.isNullOrEmpty(repairFtType)){
        	dataMap.put("repairFtType", "");
        }
        List<AppDict> appDict = new AppConst().getAppDict();
        for (AppDict adict : appDict) {
			if ("repairFtType".equals(adict.getCode())) {
				if(repairFtType!=null&&repairFtType.equals(adict.getValue())){
					dataMap.put("repairFtType", adict.getName()==null?"":adict.getName());
				}
			}
		}

        dataMap.put("lossAddress",item.getLossAddress()== null ? "":item.getLossAddress());

        dataMap.put("managementFee",item.getManagementFee()== null ? "":item.getManagementFee());
        dataMap.put("remnant",item.getRemnant()== null ? "0":item.getRemnant());
        dataMap.put("partSubtotal","0");

        dataMap.put("repairSubtotal","0");

        dataMap.put("lossTotal","0");
        
        //配件信息
        List<FhPartModelVO> partArr = (List<FhPartModelVO>)result.get("partList"+item.getId());
        List<PartModel> partInfoList=new ArrayList<PartModel>();
        BigDecimal partSubtotalD = BigDecimal.ZERO;
        if(partArr!=null && partArr.size()>0){
            double partSubtotal=0;
            for(int i=0;i<partArr.size();i++){
            	FhPartModelVO model=partArr.get(i);
                PartModel partModel=new PartModel();
                partModel.setId(i+1);
                partModel.setPartName(model.getPartName());
                partModel.setPartCode(model.getPartCode() == null ? "":model.getPartCode());
                partModel.setInsertTime(model.getInsertTime());
                partModel.setPartPrice(model.getPartPrice());
                partModel.setPartNum(model.getPartNum());
                partModel.setAuditPrice(model.getAuditPrice());//核价金额
                partModel.setComplexPrice(new BigDecimal((partModel.getPartPrice().intValue()) * (partModel.getPartNum())));
                partSubtotal+=(partModel.getComplexPrice()==null?Double.valueOf(0):partModel.getComplexPrice().doubleValue());
                partModel.setReamrk(model.getRemark() == null ? "":model.getRemark());
                partInfoList.add(partModel);
                auditAmount = auditAmount.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
            }
            //换件小计需要减去残值金额
            BigDecimal managementFeePCT = item.getManagementFee()== null ? BigDecimal.ZERO:item.getManagementFee().divide(new BigDecimal(100));
            
          //定损小计
			partSubtotalD = new BigDecimal(partSubtotal)
            		.multiply(BigDecimal.ONE.add(managementFeePCT))
            		.subtract(item.getRemnant()== null ? BigDecimal.ZERO:item.getRemnant());
            
            //换件小计需要减去残值金额
            BigDecimal partSubtotalM = auditAmount
            		.multiply(BigDecimal.ONE.add(managementFeePCT))
            		.subtract(item.getRemnant()== null ? BigDecimal.ZERO:item.getRemnant());
            auditAmount = auditAmount
            		.multiply(BigDecimal.ONE.add(managementFeePCT))
            		.subtract(item.getRemnant()== null ? BigDecimal.ZERO:item.getRemnant());
            
            
            dataMap.put("partSubtotal",partSubtotalM);
        }
        dataMap.put("table1",partInfoList);
        //维修信息
        List<FhRepairModelVO> repairArr = (List<FhRepairModelVO>)result.get("repairList"+item.getId());
        BigDecimal repairSubtotalD = BigDecimal.ZERO;
        BigDecimal repairSubtotalM = BigDecimal.ZERO;
        if(repairArr!=null && repairArr.size()>0){
            for(int i=0;i<repairArr.size();i++){
            	FhRepairModelVO model=repairArr.get(i);
                String repairType=model.getRepairType();
                if(repairType.equals("cz")){
                    dataMap.put("czRepairName",model.getRepairName()== null ? "":model.getRepairName());
                    //2015-7-9 bug修改
                    dataMap.put("czRepairAmount",model.getRepairAmount() == null ? "":model.getRepairAmount());
                    dataMap.put("czRemark",model.getRemark() == null ? "":model.getRemark());
                    dataMap.put("czAuditPrice", model.getAuditPrice()==null?"":model.getAuditPrice());
                    auditAmount = auditAmount.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalM = repairSubtotalM.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalD = repairSubtotalD.add(model.getRepairAmount()==null?BigDecimal.ZERO:model.getRepairAmount());
                }else if(repairType.equals("bj")){
                    dataMap.put("bjRepairName",model.getRepairName()== null ? "":model.getRepairName());
                    dataMap.put("bjRepairAmount",model.getRepairAmount()== null ? "":model.getRepairAmount());
                    dataMap.put("bjRemark",model.getRemark() == null ? "":model.getRemark());
                    dataMap.put("bjAuditPrice", model.getAuditPrice()==null?"":model.getAuditPrice());
                    auditAmount = auditAmount.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalM = repairSubtotalM.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalD = repairSubtotalD.add(model.getRepairAmount()==null?BigDecimal.ZERO:model.getRepairAmount());
                }else if(repairType.equals("yq")){
                    dataMap.put("yqRepairName",model.getRepairName()== null ? "":model.getRepairName());
                    dataMap.put("yqRepairAmount",model.getRepairAmount()== null ? "":model.getRepairAmount());
                    dataMap.put("yqRemark",model.getRemark()== null ? "":model.getRemark());
                    dataMap.put("yqAuditPrice", model.getAuditPrice()==null?"":model.getAuditPrice());
                    auditAmount = auditAmount.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalM = repairSubtotalM.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalD = repairSubtotalD.add(model.getRepairAmount()==null?BigDecimal.ZERO:model.getRepairAmount());
                }else if(repairType.equals("jx")){
                    dataMap.put("jxRepairName",model.getRepairName()== null ? "":model.getRepairName());
                    dataMap.put("jxRepairAmount",model.getRepairAmount()== null ? "":model.getRepairAmount());
                    dataMap.put("jxRemark",model.getRemark() == null ? "":model.getRemark());
                    dataMap.put("jxAuditPrice", model.getAuditPrice()==null?"":model.getAuditPrice());
                    auditAmount = auditAmount.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalM = repairSubtotalM.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalD = repairSubtotalD.add(model.getRepairAmount()==null?BigDecimal.ZERO:model.getRepairAmount());
                }else if(repairType.equals("dg")){
                    dataMap.put("dgRepairName",model.getRepairName()== null ? "":model.getRepairName());
                    dataMap.put("dgRepairAmount",model.getRepairAmount()== null ? "":model.getRepairAmount());
                    dataMap.put("dgRemark",model.getRemark() == null ? "":model.getRemark());
                    dataMap.put("dgAuditPrice", model.getAuditPrice()==null?"":model.getAuditPrice());
                    auditAmount = auditAmount.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalM = repairSubtotalM.add(model.getAuditPrice()==null?BigDecimal.ZERO:model.getAuditPrice());
                    repairSubtotalD = repairSubtotalD.add(model.getRepairAmount()==null?BigDecimal.ZERO:model.getRepairAmount());
                }
            }
            dataMap.put("repairSubtotal",repairSubtotalM);
            dataMap.put("auditAmount",auditAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        double lossTotal=partSubtotalD.doubleValue()+repairSubtotalD.doubleValue();
        dataMap.put("lossTotal",lossTotal);
        //审核信息
        List<AuditModel> preAuditHeader=new ArrayList<AuditModel>();
        AuditModel preAuditModel=new AuditModel();
        preAuditModel.setAuditResult("0");
        preAuditHeader.add(preAuditModel);
        dataMap.put("preAuditHeader",preAuditHeader);
        List<FhAuditModelVO> preAuditContent=new ArrayList<FhAuditModelVO>();

        List<FhAuditModelVO> auditInfoList=new ArrayList<FhAuditModelVO>();
        List<FhAuditModelVO> auditArr = (List<FhAuditModelVO>)result.get("auditList");
        if(auditArr!=null && auditArr.size()>0){
            for (int i=0;i<auditArr.size();i++){
            	FhAuditModelVO model=auditArr.get(i);
                if(item.getOrderCode().equals(model.getOrderCode())){
                    if(model.getAuditType().equals("2")){
                        auditInfoList.add(model);
                    }else{
                        preAuditContent.add(model);
                    }
                }
            }
        }
        dataMap.put("preAuditContent",preAuditContent);
        dataMap.put("table2",auditInfoList);
        //留言信息
        List<LeaveModel> leaveInfoList=new ArrayList<LeaveModel>();
        List<FhLeaveModelVO> leaveArr = (List<FhLeaveModelVO>)result.get("leaveList");
        if(leaveArr!=null && leaveArr.size()>0){
            for (int i=0;i<leaveArr.size();i++){
            	FhLeaveModelVO model=leaveArr.get(i);
                if(item.getOrderCode().equals(model.getReserved())){
                    LeaveModel leaveModel=new LeaveModel();
                    leaveModel.setName(model.getName() ==null?"":model.getName());
                    leaveModel.setInsertTime(model.getInsertTime() ==null?"":model.getInsertTime());
                    leaveModel.setDetail(model.getDetail() ==null?"":model.getDetail());
                    leaveInfoList.add(leaveModel);
                }
            }
        }
        dataMap.put("table3",leaveInfoList);
        return dataMap;
    }

    private Map<String,Object> getSurveyData(Map<String,Object> result){
    	
        FhSurveyModelVO surveyModel = (FhSurveyModelVO)result.get("surveyInfo");
        if(surveyModel==null){
            return null;
        }
        Map<String,Object> dataMap=new HashMap<String,Object>();
        dataMap.put("serviceName",surveyModel.getServiceName()== null ? "":surveyModel.getServiceName());
        dataMap.put("principalName",surveyModel.getPrincipalName()== null ? "":surveyModel.getPrincipalName());
        dataMap.put("principalTime",surveyModel.getPrincipalTime()== null ? "":surveyModel.getPrincipalTime());

        dataMap.put("contactName",surveyModel.getContactName()== null ? "":surveyModel.getContactName());
        dataMap.put("contactPhone",surveyModel.getContactPhone()== null ? "":surveyModel.getContactPhone());
        dataMap.put("principalAuth",surveyModel.getPrincipalAuth()== null ? "":surveyModel.getPrincipalAuth());

        dataMap.put("addressPlace",surveyModel.getAddressPlace()== null ? "":surveyModel.getAddressPlace());

        dataMap.put("principalInfo",surveyModel.getPrincipalInfo()== null ? "":surveyModel.getPrincipalInfo());

        dataMap.put("carMark",surveyModel.getCarMark()== null ? "":surveyModel.getCarMark());
        dataMap.put("driverName",surveyModel.getDriverName()== null ? "":surveyModel.getDriverName());
        dataMap.put("driverPhone",surveyModel.getDriverPhone()== null ? "":surveyModel.getDriverPhone());

        dataMap.put("isDriverCard",keyToValueByEffective(surveyModel.getIsDriverCard()));
        dataMap.put("isCarmodel",keyToValueByCheck(surveyModel.getIsCarmodel()));

        dataMap.put("isDrivingLicense",keyToValueByEffective(surveyModel.getIsDrivingLicense()));
        dataMap.put("isVin",keyToValueByCheck(surveyModel.getIsVin()));

        dataMap.put("accidentType",getValueByAppDict("accidentType",surveyModel.getAccidentType()));
        dataMap.put("accidentDuty",getValueByAppDict("accidentDuty",surveyModel.getAccidentDuty()));

        dataMap.put("isInjured",isNo(surveyModel.getIsInjured()));
        dataMap.put("injuredReason",surveyModel.getInjuredReason()== null ? "":surveyModel.getInjuredReason());

        dataMap.put("isLoss",isNo(surveyModel.getIsLoss()));
        dataMap.put("damageReason",surveyModel.getDamageReason()== null ? "":surveyModel.getDamageReason());

        dataMap.put("isCali",isNo(surveyModel.getIsCali()));

        dataMap.put("surveyPlace",surveyModel.getSurveyPlace()== null ? "":surveyModel.getSurveyPlace());

        dataMap.put("surveyDespType",getValueByAppDict("surveyDespType",surveyModel.getSurveyDespType()));
        dataMap.put("surveyDesp",surveyModel.getSurveyDesp()== null ? "":surveyModel.getSurveyDesp());

        dataMap.put("accountName",surveyModel.getAccountName()== null ? "":surveyModel.getAccountName());
        dataMap.put("bank",surveyModel.getBank()== null ? "":surveyModel.getBank());
        dataMap.put("insuredAccount",surveyModel.getInsuredAccount()== null ? "":surveyModel.getInsuredAccount());

        //三者车信息
        List<CarModel> threeInfoList=new ArrayList<CarModel>();
        List<FhCarModelVO> threeArr = (List<FhCarModelVO>)result.get("threeCarInfo");
        if(threeArr!=null && threeArr.size()>0){
            for(int i=0;i<threeArr.size();i++){
            	FhCarModelVO item=threeArr.get(i);
                CarModel carModel=new CarModel();
                carModel.setCarmark(item.getCarmark() ==null?"":item.getCarmark());
                carModel.setDrivername(item.getDrivername() ==null?"":item.getDrivername());
                carModel.setDriverphone(item.getDriverphone() ==null?"":item.getDriverphone());
                threeInfoList.add(carModel);
            }
        }
        dataMap.put("table1",threeInfoList);
        //审核信息
        List<AuditModel> preAuditHeader=new ArrayList<AuditModel>();
        AuditModel preAuditModel=new AuditModel();
        preAuditModel.setAuditResult("0");
        preAuditHeader.add(preAuditModel);
        dataMap.put("preAuditHeader",preAuditHeader);
        List<FhAuditModelVO> preAuditContent=new ArrayList<FhAuditModelVO>();

        List<FhAuditModelVO> auditInfoList=new ArrayList<FhAuditModelVO>();
        List<FhAuditModelVO> auditArr = (List<FhAuditModelVO>)result.get("auditList");
        if(auditArr!=null && auditArr.size()>0){
            for (int i=0;i<auditArr.size();i++){
            	FhAuditModelVO item=auditArr.get(i);
                if(item.getOrderCode().equals(surveyModel.getOrderCode())){
                    if(item.getAuditType().equals("2")){
                        auditInfoList.add(item);
                    }else{
                        preAuditContent.add(item);
                    }
                }
            }
        }
        dataMap.put("preAuditContent",preAuditContent);
        dataMap.put("table2",auditInfoList);
        //留言信息
        List<LeaveModel> leaveInfoList=new ArrayList<LeaveModel>();
        List<FhLeaveModelVO> leaveArr = (List<FhLeaveModelVO>)result.get("leaveList");
        if(leaveArr!=null && leaveArr.size()>0){
            for (int i=0;i<leaveArr.size();i++){
            	FhLeaveModelVO item=leaveArr.get(i);
                if(item.getReserved().equals(surveyModel.getOrderCode())){
                    LeaveModel leaveModel=new LeaveModel();
                    leaveModel.setName(item.getName()== null ? "":item.getName());
                    leaveModel.setInsertTime(item.getInsertTime()== null ? "":item.getInsertTime());
                    leaveModel.setDetail(item.getDetail()== null ? "":item.getDetail());
                    leaveInfoList.add(leaveModel);
                }
            }
        }
        dataMap.put("table3",leaveInfoList);
        return dataMap;
    }

    private String isNo(String key){
        if(key==null){
            return "";
        }else if(key.equals("0")){
            return "否";
        }else if(key.equals("1")){
            return "是";
        }
        return "";
    }

    /**
     * 检测是否有效
     */
    private String keyToValueByEffective(String key){
        if(key==null){
            return "";
        }else if(key.equals("1")){
            return "未验";
        }else if(key.equals("2")){
            return "有效";
        }else if(key.equals("3")){
            return "无效";
        }
        return "";
    }

    /**
     * 检测是否相符
     */
    private String keyToValueByCheck(String key){
        if(key==null){
            return "";
        }else if(key.equals("1")){
            return "未验";
        }else if(key.equals("2")){
            return "相符";
        }else if(key.equals("3")){
            return "不符";
        }
        return "";
    }

    /**
     * 根据字典获取值
     */
    private String getValueByAppDict(String key,String value){
        for(int i=0;i<AppConst.appDictList.size();i++){
            if(AppConst.appDictList.get(i).getCode().equals(key)){
                if(AppConst.appDictList.get(i).getValue().equals(value)){
                    return AppConst.appDictList.get(i).getName();
                }
            }
        }
        return "";
    }

    private String getValue(JSONObject container,String key){
        if(container.containsKey(key)){
            String result=container.getString(key);
            return result==null?"":result;
        }
        return "";
    }

    private BigDecimal getValueForBigDecimal(JSONObject container,String key){
        if(container.containsKey(key)){
            String result=container.getString(key);
            return result==null?new BigDecimal(0):new BigDecimal(result);
        }
        return new BigDecimal(0);
    }

    private int getValueForInt(JSONObject container,String key){
        if(container.containsKey(key)){
            String result=container.getString(key);
            return result==null?0:Integer.parseInt(result);
        }
        return 0;
    }

    public static void main(String[] args) {


    }


}
