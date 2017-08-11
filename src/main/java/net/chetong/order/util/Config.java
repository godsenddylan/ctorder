package net.chetong.order.util;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config { 
	/*static ResourceBundle bundle = PropertyResourceBundle.getBundle("config");
	
	public static boolean getSwitch(String switchName){
		if("Y".equals(bundle.getString(switchName))){
			return true;
		}
		return false;
	}*/
	public static boolean JOB_SWITCH;
	public static boolean JOB_SWITCH_YC;
    //查询车童列表循环次数
	public static int QUERY_CT_LOOP;
	//查询车童列表经纬度增量
	public static double QUERY_CT_DECIMAL_INC;
	//查询车童数量
	public static int QUERY_CT_COUNT;
	//一元体验账号ID
	public static String PROMOTION_ONEMONEY_USER_ID;
	//永诚对接系统买家账户账号ID
//	public static String YONGCHENG_USER = bundle.getString("yongcheng_user");
	//#永诚授权定损金额
	public static String YONGCHENG_ALLOW_MONEY;
	
	//永城对接SWDL URL地址
	public static String YC_WSDL_URL_GET_TASKLIST;
	//永城对接SWDL URL地址
	public static String YC_WSDL_URL_SAVE_SURVEY;
	//永城对接SWDL URL地址
	public static String YC_WSDL_URL_SAVE_VEHICLE_LOSS;
	//永城对接SWDL URL地址
	public static String YC_WSDL_URL_SAVE_GOODS_LOSS;
	//永城对接SWDL URL地址
	public static String YC_WSDL_URL_CHECK_LOSS;
	//永城对接SWDL URL地址
	public static String YC_WSDL_URL_CHECK_GOODS_LOSS;
	
	//永城对接SWDL URL地址 获取险种列表
	public static String YC_WSDL_URL_GET_INSLIST;
	//永城对接SWDL URL地址 获取费用类型列表
	public static String YC_WSDL_URL_GET_FEETYPELIST;
	//永城对接，获取查询车辆信息的验证码和查询码
	public static String YC_WSDL_URL_GET_CHECK_CDE;
	//永城对接，获取查询车辆信息
	public static String YC_WSDL_URL_GET_CAR_DATA;
	
	
	//永诚精友车型地址
	public static String YC_CAR_TYPE_URL;
	//永诚精友配件地址
	public static String YC_PART_URL;
	//永诚精友配件明细地址
	public static String YC_PART_DETAIL_URL;
	//永诚精友配件校验码
	public static String YC_MOBILE_SERCODE;
	//上传图片临时路径
	public static String IMG_UPLOAD_TEMP_PATH;
	//百度计算驾车距离api  key
	public static String BAIDU_DIRECTION_KEY;
	//查询车童是否使用多线程模式
	public static String QUERYCT_NEW;
	//下载同时压缩数量
	public static int DOWNLOAD_ZIP_COUNT;
	//下载压缩级别
	public static int DOWNLOAD_ZIP_LEVEL;
	//车险资质车童做货运险订单风险基金比例
	public static BigDecimal CAR_2_CARGO_INSURE_RATE;
	//车童保证金缴纳标准
	public static String BOND_STANDARD_MONEY; 
	//特殊城市
	public static List<String> SPECIAL_CITYS_CODE;
	
	@Value("${job_switch}")
	public void setJOB_SWITCH(String jOB_SWITCH) {
		JOB_SWITCH = "Y".equals(jOB_SWITCH);
	}

	@Value("${job_switch_YC}")
	public void setJOB_SWITCH_YC(String jOB_SWITCH_YC) {
		JOB_SWITCH_YC = "Y".equals(jOB_SWITCH_YC);
	}

	@Value("${query_ct_loop}")
	public void setQUERY_CT_LOOP(int qUERY_CT_LOOP) {
		QUERY_CT_LOOP = qUERY_CT_LOOP;
	}
	
	@Value("${query_ct_decimal_inc}")
	public void setQUERY_CT_DECIMAL_INC(double qUERY_CT_DECIMAL_INC) {
		QUERY_CT_DECIMAL_INC = qUERY_CT_DECIMAL_INC;
	}
	
	@Value("${query_ct_count}")
	public void setQUERY_CT_COUNT(int qUERY_CT_COUNT) {
		QUERY_CT_COUNT = qUERY_CT_COUNT;
	}
	
	@Value("${promotion_onemoney_user_id}")
	public void setPROMOTION_ONEMONEY_USER_ID(String pROMOTION_ONEMONEY_USER_ID) {
		PROMOTION_ONEMONEY_USER_ID = pROMOTION_ONEMONEY_USER_ID;
	}
	
	@Value("${yongcheng_allow_money}")
	public void setYONGCHENG_ALLOW_MONEY(String yONGCHENG_ALLOW_MONEY) {
		YONGCHENG_ALLOW_MONEY = yONGCHENG_ALLOW_MONEY;
	}
	
	@Value("${yc_wsdl_url_get_tasklist}")
	public void setYC_WSDL_URL_GET_TASKLIST(String yC_WSDL_URL_GET_TASKLIST) {
		YC_WSDL_URL_GET_TASKLIST = yC_WSDL_URL_GET_TASKLIST;
	}
	
	@Value("${yc_wsdl_url_save_survey}")
	public void setYC_WSDL_URL_SAVE_SURVEY(String yC_WSDL_URL_SAVE_SURVEY) {
		YC_WSDL_URL_SAVE_SURVEY = yC_WSDL_URL_SAVE_SURVEY;
	}
	
	@Value("${yc_wsdl_url_save_vehicle_loss}")
	public void setYC_WSDL_URL_SAVE_VEHICLE_LOSS(String yC_WSDL_URL_SAVE_VEHICLE_LOSS) {
		YC_WSDL_URL_SAVE_VEHICLE_LOSS = yC_WSDL_URL_SAVE_VEHICLE_LOSS;
	}
	
	@Value("${yc_wsdl_url_save_goods_loss}")
	public void setYC_WSDL_URL_SAVE_GOODS_LOSS(String yC_WSDL_URL_SAVE_GOODS_LOSS) {
		YC_WSDL_URL_SAVE_GOODS_LOSS = yC_WSDL_URL_SAVE_GOODS_LOSS;
	}
	
	@Value("${yc_wsdl_url_check_loss}")
	public void setYC_WSDL_URL_CHECK_LOSS(String yC_WSDL_URL_CHECK_LOSS) {
		YC_WSDL_URL_CHECK_LOSS = yC_WSDL_URL_CHECK_LOSS;
	}
	
	@Value("${yc_wsdl_url_check_goods_loss}")
	public void setYC_WSDL_URL_CHECK_GOODS_LOSS(String yC_WSDL_URL_CHECK_GOODS_LOSS) {
		YC_WSDL_URL_CHECK_GOODS_LOSS = yC_WSDL_URL_CHECK_GOODS_LOSS;
	}
	
	@Value("${yc_wsdl_url_get_inslist}")
	public void setYC_WSDL_URL_GET_INSLIST(String yC_WSDL_URL_GET_INSLIST) {
		YC_WSDL_URL_GET_INSLIST = yC_WSDL_URL_GET_INSLIST;
	}
	
	@Value("${yc_wsdl_url_get_feetypelist}")
	public void setYC_WSDL_URL_GET_FEETYPELIST(String yC_WSDL_URL_GET_FEETYPELIST) {
		YC_WSDL_URL_GET_FEETYPELIST = yC_WSDL_URL_GET_FEETYPELIST;
	}
	
	@Value("${yc_wsdl_url_get_check_cde}")
	public void setYC_WSDL_URL_GET_CHECK_CDE(String yC_WSDL_URL_GET_CHECK_CDE) {
		YC_WSDL_URL_GET_CHECK_CDE = yC_WSDL_URL_GET_CHECK_CDE;
	}
	
	@Value("${yc_wsdl_url_get_car_data}")
	public void setYC_WSDL_URL_GET_CAR_DATA(String yC_WSDL_URL_GET_CAR_DATA) {
		YC_WSDL_URL_GET_CAR_DATA = yC_WSDL_URL_GET_CAR_DATA;
	}
	
	@Value("${yc_car_type_url}")
	public void setYC_CAR_TYPE_URL(String yC_CAR_TYPE_URL) {
		YC_CAR_TYPE_URL = yC_CAR_TYPE_URL;
	}
	
	@Value("${yc_part_url}")
	public void setYC_PART_URL(String yC_PART_URL) {
		YC_PART_URL = yC_PART_URL;
	}
	
	@Value("${yc_part_detail_url}")
	public void setYC_PART_DETAIL_URL(String yC_PART_DETAIL_URL) {
		YC_PART_DETAIL_URL = yC_PART_DETAIL_URL;
	}
	
	@Value("${yc_mobile_sercode}")
	public void setYC_MOBILE_SERCODE(String yC_MOBILE_SERCODE) {
		YC_MOBILE_SERCODE = yC_MOBILE_SERCODE;
	}
	
	@Value("${img_upload_temp_path}")
	public void setIMG_UPLOAD_TEMP_PATH(String iMG_UPLOAD_TEMP_PATH) {
		IMG_UPLOAD_TEMP_PATH = iMG_UPLOAD_TEMP_PATH;
	}
	
	@Value("${baidu_direction_key}")
	public void setBAIDU_DIRECTION_KEY(String bAIDU_DIRECTION_KEY) {
		BAIDU_DIRECTION_KEY = bAIDU_DIRECTION_KEY;
	}
	
	@Value("${queryct_new}")
	public void setQUERYCT_NEW(String qUERYCT_NEW) {
		QUERYCT_NEW = qUERYCT_NEW;
	}
	
	@Value("${download_zip_count}")
	public void setDOWNLOAD_ZIP_COUNT(int dOWNLOAD_ZIP_COUNT) {
		DOWNLOAD_ZIP_COUNT = dOWNLOAD_ZIP_COUNT;
	}
	
	@Value("${download_zip_level}")
	public void setDOWNLOAD_ZIP_LEVEL(int dOWNLOAD_ZIP_LEVEL) {
		DOWNLOAD_ZIP_LEVEL = dOWNLOAD_ZIP_LEVEL;
	}
	
	@Value("${car_2_cargo_insure_rate}")
	public void setCAR_2_CARGO_INSURE_RATE(BigDecimal cAR_2_CARGO_INSURE_RATE) {
		CAR_2_CARGO_INSURE_RATE = cAR_2_CARGO_INSURE_RATE;
	}
	
	@Value("${bond_standard_money}")
	public void setBOND_STANDARD_MONEY(String bOND_STANDARD_MONEY) {
		BOND_STANDARD_MONEY = bOND_STANDARD_MONEY;
	}
	
	@Value("${special_city_code}")
	public void setSPECIAL_CITYS_CODE(String sPECIAL_CITYS_CODE) {
		SPECIAL_CITYS_CODE = Arrays.asList(sPECIAL_CITYS_CODE.split(","));
	}
	
}
