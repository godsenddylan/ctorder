package net.chetong.order.util.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.chetong.order.model.FhInsureDataInfoVO;
import net.chetong.order.service.common.BaseService;
import net.chetong.order.util.StringUtil;

public class ReflectUtils {
	protected static Logger log = LogManager.getLogger(BaseService.class);
	
	// 短日期格式
	public static String DATE_FORMAT = "yyyy-MM-dd";
	// 长日期格式
	public static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static String LONG_FORMAT = "yyyyMMddHHmmss";
	// 当前时间 精确到分钟
	public static String TIME_FORMATM = "yyyy-MM-dd HH:mm";
	
	/** 需要转STRING类型的 */
	static String[] arr = new String[7];
	static {
		arr[0] = "class java.lang.String";
		arr[1] = "class java.lang.Integer";
		arr[2] = "class java.lang.Short";
		arr[3] = "class java.lang.Double";
		arr[4] = "class java.lang.Boolean";
		arr[5] = "class java.util.Date";
		arr[6] = "class java.util.Calendar";
	}
	/** 需要再次解析的类型 */
	static String[] arrOther = new String[45];
	static {
		arrOther[0] = "class org.datacontract.schemas._2004._07.AllTrustService.PDAUser";
		arrOther[1] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CopyReport";
		arrOther[2] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDARptInfo";
		arrOther[3] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDAScene";
		arrOther[4] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PDASceneMainInfo";
		arrOther[5] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.SurveyReportList";
		arrOther[6] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.TaskInfo";
		arrOther[7] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.PDACarMainInfo";
		arrOther[8] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarBaseInfo";
		arrOther[9] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitChangeDetailInfo";
		arrOther[10] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitChangeInfo";
		arrOther[11] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitFeeDetailInfo";
		arrOther[12] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitFeeInfo";
		arrOther[13] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitRepairDetailInfo";
		arrOther[14] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarFitRepairInfo";
		arrOther[15] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.PDAPropMainInfo";
		arrOther[16] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.SubmitPropBaseDetailInfo";
		arrOther[17] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.SubmitPropBaseInfo";
		arrOther[18] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.SubmitPropItemInfo";
		arrOther[19] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDAPropSurvey.SubmitPropItemInfos";
		arrOther[20] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDASceneSurvey.SurveyReport";
		arrOther[21] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDASceneSurvey.ThirdInfos";
		arrOther[22] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.ReturnRst";
		arrOther[23] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDACarSurvey.SubmitCarDamageInfo";
		arrOther[24] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.HistoryRptList";
		arrOther[25] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.PlyInfo";
		arrOther[26] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.HistoryRpt";
		arrOther[27] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CopyDrvInfo";
		arrOther[28] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CopyFeeInfo";
		arrOther[29] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CopyPlyBaseInfo";
		arrOther[30] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CopyRdrInfo";
		arrOther[31] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CopyVhlInfo";
		arrOther[32] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.Force";
		arrOther[33] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CarFitDetailInfo";
		arrOther[34] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CheckLossListReq";
		arrOther[35] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.CheckLossListRst";
		arrOther[36] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.GoodsFitDetailInfo";
		arrOther[37] = "class net.chetong.order.model.FmTaskInfoVO";
		arrOther[38] = "class net.chetong.order.util.ResultVO";
		arrOther[39] = "class net.chetong.order.model.FhPartItemVO";
		arrOther[40] = "class net.chetong.order.model.FhLossItemVO";
		arrOther[41] = "class net.chetong.order.model.FhFeeItemVO";
		arrOther[42] = "class net.chetong.order.model.FhRepairItemVO";
		arrOther[43] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel_PDASceneSurvey.PayInfo";
		arrOther[44] = "class org.datacontract.schemas._2004._07.AllTrustService_ServiceModel.HistoryInfo";
	}
	
	/** 不需要解析的类型并且不需要记录的  */
	static String[] noArr = new String[1];
	static {
		noArr[0] = "class org.apache.axis.description.TypeDesc";
	}
	
	public static String modelToString(Object o) {
		if(StringUtil.isNullOrEmpty(o)){
			return null;
		}
		Class<?> clz = o.getClass();
		StringBuffer sb = new StringBuffer();
		sb.append(clz.getSimpleName()).append("[").append("\n");
		try {
			// 获取实体类的所有属性，返回Field数组
			Field[] fields = o.getClass().getDeclaredFields();
			int size = fields.length;
			for (int i = 0; i < size; i++) {
				Field f = fields[i];
				String name = f.getName();
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				String type = f.getGenericType().toString();
				if(inArray(noArr,type)){
					continue;
				}
				try {
					Method m = clz.getMethod("get" + name);
					Object vo = m.invoke(o);
					if (common(vo)) {
						Object[] arrVo = (Object[]) vo;
						int arrSize = arrVo.length;
						sb.append(name).append(":").append("{");
						for (int j = 0; j < arrSize; j++) {
							String value = getValue(arrVo[j], arrVo[j].getClass().toString());
							sb.append(value);
							if (arrSize - 1 != j) {
								sb.append(",").append("\n");
							}
						}
						sb.append("}");
					} else {
						String value = getValue(vo, type);
						sb.append(name).append(":").append(value);
					}
					if (size - 1 != i) {
						sb.append(",").append("\n");
					}

				} catch (NoSuchMethodException noe) {
					//log.error("获取保险公司信息解析异常1:"+ name);
				}
			}
		} catch (Exception e) {
//			log.error("获取保险公司信息解析异常2:", e);
		}
		sb.append("]");
		return sb.toString();
	}
	
	/***
	 * 递归解析对象，返回解析数据以对象承载
	 * @param o 需解析的对象
	 * @param l 解析后对象的值
	 * @param relation 关联关系  保险公司现以报案号关联（report_no）
	 * @author wufeng@chetong.net
	 */
	public static void modelToListVO(Object o,List<FhInsureDataInfoVO> l,String... relation) {
		try{
			if(StringUtil.isNullOrEmpty(o)){
				return;
			}
			Map<String, Object> oMap = modelToMap(o);
			Set<String> keys = oMap.keySet();
			FhInsureDataInfoVO dataVO = (FhInsureDataInfoVO) oMap.get("dataVO");
			dataVO.setReportNo(relation[0]);
			if(relation.length>1){
				dataVO.setQueryNode(relation[1]+"."+dataVO.getQueryNode());
			}
			l.add(dataVO);
			Boolean flag = (Boolean) oMap.get("flag");
			keys.remove("flag");
			keys.remove("dataVO");
			if (flag!=null && flag) {
				Iterator<String> itr = keys.iterator();
				while (itr.hasNext()) {
					Object io = oMap.get(itr.next());
					if (common(io)) {
						Object[] arrVo = (Object[]) io;
						int arrSize = arrVo.length;
						for (int j = 0; j < arrSize; j++) {
							modelToListVO(arrVo[j], l,relation[0],dataVO.getQueryNode());
						}
					} else {
						modelToListVO(io, l,relation[0],dataVO.getQueryNode());
					}
				}
			}
		}catch(Exception e){
//			log.error("获取保险公司信息解析异常5:", e);
		}
	}
	
	/***
	 * 解析对象 返回承载对象并且返回还需要解析的对象
	 * @param o
	 * @return
	 * @author wufeng@chetong.net
	 */
	private static Map<String,Object> modelToMap(Object o) {
		if(o == null){
			return null;
		}
		Class<?> clz = o.getClass();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		try {
			// 获取实体类的所有属性，返回Field数组
			Field[] fields = o.getClass().getDeclaredFields();
			int size = fields.length;
			FhInsureDataInfoVO dataVO = new FhInsureDataInfoVO();
			dataVO.setCreatedBy("SYSTEM");
			dataVO.setUpdatedBy("SYSTEM");
			dataVO.setObjectName(clz.getSimpleName());
			dataVO.setQueryNode(clz.getSimpleName());
			StringBuffer sb = new StringBuffer();
			Boolean flag = false;
			for (int i = 0; i < size; i++) {
				Field f = fields[i];
				String name = f.getName();
				name = name.substring(0, 1).toUpperCase() + name.substring(1);
				String type = f.getGenericType().toString();
				if(inArray(noArr,type)){
					continue;
				}
				try {
					String value = null;
					Method m = clz.getMethod("get" + name);
					Object vo = m.invoke(o);
					if (common(vo)) {
						value = name + "[]";
						Object[] arrVo = (Object[]) vo;
						int arrSize = arrVo.length;
						for (int j = 0; j < arrSize; j++) {
							String className =  arrVo[j].getClass().toString();
							if(inArray(arrOther,className)){
								resultMap.put(name, vo);
								flag =true;
								break;
							}
						}
					} else if(inArray(arrOther,type)){
						value = name;
						resultMap.put(name, vo);
						flag =true;
					} else {
						value = getValue(vo, type);
					}
					if (size - 1 != i) {
						sb.append(name).append(":").append(value).append(",");
					} else {
						sb.append(name).append(":").append(value);
					}
				} catch (NoSuchMethodException noe) {
//					log.error("获取保险公司信息解析异常6:", name);
				}
				dataVO.setObjectValue(sb.toString());
				resultMap.put("dataVO", dataVO);
				resultMap.put("flag", flag);
			}
			return resultMap;
		} catch (Exception e) {
//			log.error("获取保险公司信息解析异常7:", e);
		}
		return null;
	}
	
	private static String getValue(Object o, String type) {
		if (o == null) {
			return null;
		}
		if (arr[0].equals(type)) {
			return String.valueOf(o);
		} else if (arr[5].equals(type)) {
			Date d = (Date) o;
			return String.valueOf(dateToString(d, null));
		} else if (arr[6].equals(type)) {
			Calendar c = (Calendar) o;
			return String.valueOf(dateToString(c.getTime(), null));
		} else if (inArray(arrOther,type)) {
			return ReflectUtils.modelToString(o);
		} else {
			return String.valueOf(o);
		}
	}

	public static String dateToString(Date formatDate, String paraDateFormat) {
		if (formatDate == null) {
			return null;
		}
		if (paraDateFormat == null || paraDateFormat.trim() == "") {
			paraDateFormat = TIME_FORMAT;
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(paraDateFormat);
		try {
			return simpleDateFormat.format(formatDate);
		} catch (Exception e) {
			return null;
		}
	}

	private static boolean inArray(String[] arr ,String type) {
		boolean flag = false;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equals(type)) {
				return true;
			}
		}
		return flag;
	}

	/**
	 * 判断是不是引用数组类型
	 * 
	 * @param object
	 */
	public static boolean common(Object object) {
		boolean b = (object instanceof Object[]);
		return b;
	}

	/**
	 * 判断是不是数组类型
	 * 
	 * @param object
	 */
	public static boolean ArrayReflectCommon(Object object) {
		boolean b = object.getClass().isArray();
		return b;
	}
	
//	public static void main(String[] arge){
//		
//		System.out.println();
//	}
//	
}
