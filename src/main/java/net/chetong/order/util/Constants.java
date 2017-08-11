package net.chetong.order.util;

import java.text.SimpleDateFormat;

import org.apache.commons.lang3.time.FastDateFormat;

public class Constants {
	
	/**时间格式化类  yyyy-MM-dd**/
	public static final SimpleDateFormat sdfForDate = new SimpleDateFormat("yyyy-MM-dd");
	/**时间格式化类 yyyy-MM-dd HH:mm:ss**/
	public static final SimpleDateFormat sdfForTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**时间格式化类  yyyy-MM-dd**/
	public static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd");
	/**时间格式化类 yyyy-MM-dd HH:mm:ss**/
	public static final FastDateFormat DATE_TIME_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 腾讯云图片服务器账号安全码
	 */
	public static final String APP_ID = "200681";
    public static final String SECRET_ID = "AKIDgAGfaYIEyJEXaCgtztNLiIqkANlBnrQQ";
	public static final String SECRET_KEY = "jpivTlLgJrqVDq02f1h3oLWdabCMEMWW";
	
	/** ResultVO.resultCode 1=成功*/
	public final static String SUCCESS = "1";  //成功
	/** ResultVO.resultCode -1=成功*/
	public final static String ERROR = "-1";	//失败
	
	/** 标的车 */
	public final static String MAIN_CAR = "1";	 
	/** 三者车 */
	public final static String THIRD_CAR = "2";
	
	/** 车损 */
	public final static String LOSS_TYPE_CAR = "1";	 
	/** 物损 */
	public final static String LOSS_TYPE_GOODS = "2";
	/** 泛华账户*/
	public final static String FAN_HUA_USER_ID = "11025";
	
	//==========订单费用=====================//
	/** 基础费 **/
	public final static String FEE_BASE = "1";
	/** 差旅费(远程作业费) **/
	public final static String FEE_REMOTE = "2";
	/** 超额附加费 **/
	public final static String FEE_OVER = "3";
	/** 买家奖励费 **/
	public final static String FEE_BUYER_BONUS = "4";
	/** 通道费 **/
	public final static String FEE_CHANNEL = "7";
	/**保证金**/
	public final static String FEE_CASH = "9";
	/** 基础（通道费+开票费） **/
	public final static String FEE_BASE_CHANNEL_INVOICE = "16";
	/** 远程（通道费+开票费） **/
	public final static String FEE_REMOTE_CHANNEL_INVOICE = "17";
	/** 超额附加（通道费+开票费） **/
	public final static String FEE_OVER_CHANNEL_INVOICE = "18";
	/** 保险费（风险基金） **/
	public final static String FEE_INSURANCE = "10";
	/** 财务费   **/
	public final static String FEE_FINANCE = "25";
	/** 委托派单费   **/
	public final static String FEE_SEND_ORDER = "11";
	/** 委托审核费  **/
	public final static String FEE_AUDIT_ORDER = "12";
	/** 基础费团队管理费   **/
	public final static String FEE_BASE_TEAM = "22";
	/** 远程作业费团队管理费  **/
	public final static String FEE_REMOTE_TEAM = "23";
	/** 超额附加费团队管理费  **/
	public final static String FEE_OVER_TEAM = "24";
	
	/** 指导价基础费  **/
	public final static String FEE_BASE_GUIDE = "28";
	
	/** 指导价超额附加费  **/
	public final static String FEE_OVER_GUIDE = "29";
	
	/** 指导价基础费的通道费开票费  **/
	public final static String FEE_BASE_CHANNEL_INVOICE_GUIDE = "30";
	
	/** 指导价超额附加费的通道费开票费 **/
	public final static String FEE_OVER_CHANNEL_INVOICE_GUIDE = "31";
	
	/** 车童扣款费 **/
	public final static String FEE_CT_DEDUCT = "32";
	/** 团队扣款费 **/
	public final static String FEE_TEAM_DEDUCT = "33";
	
	/** 收支类型  支出 : - **/
	public final static String BALANCE_TYPE_PAY = "-";
	/** 收支类型  收入 : + **/
	public final static String BALANCE_TYPE_RECEIVE = "+";
	
	/**
	 * 交易类型定义  具体收入支出类型请参考ParametersCommonUtil
	 */
	/** 交易类型：委托派单=01 **/
	public final static String TRADE_TYPE_SEND_ORDER = "01";
	/** 交易类型：委托审核=02 **/
	public final static String TRADE_TYPE_AUDIT_ORDER = "02";
	/** 交易类型：公估服务=03 **/
	public final static String TRADE_TYPE_SERVICE_FEE = "03";
	/** 交易类型：保证金=04 **/
	public final static String TRADE_TYPE_BOND_FEE = "04";
	/** 交易类型： 一元体验=05 **/
	public final static String TRADE_TYPE_PROMOTION_ONE = "05";
	/** 交易类型：机构间结算=06 **/
	public final static String TRADE_TYPE_GUIDE_FEE = "06";
	
	
	
	/**委托审核收入类型**/
	public final static String TRADE_TYPE_ENTRUST_AUDIT_IN = "41";
	/**委托审核支出类型**/
	public final static String TRADE_TYPE_ENTRUST_AUDIT_OUT = "42";
	/**委托派单收入类型**/
	public final static String TRADE_TYPE_ENTRUST_SEND_IN = "43";
	/**委托派单支出类型**/
	public final static String TRADE_TYPE_ENTRUST_SEND_OUT = "44";
	
	/**
	 * 交易流水类型
	 */
	/**公估收入**/
	public final static String TRADE_TYPE_SERVICE_INCOME_FEE="10";
	/**公估支出**/
	public final static String TRADE_TYPE_SERCICE_EXPEND_FEE="11";
	/**公估退款收入**/
	public final static String TRADE_TYPE_SERVICE_REFUND_FEE="12";
	/**团队管理费收入**/
	public final static String TRADE_TYPE_TEAM_INCOME_FEE="24";
	/**代公估支出**/
	public final static String TRADE_TYPE_PAY_SERVICE_FEE="26";
	
	/**
	 * 活动名称类型  
	 * 01:一元体验
	 */
	public final static String PROMOTION_ONEMONEY_01="01" ;//一元体验  
	/** 账户流水类型： 红包收入*/
	public final static String AC_LOG_TYPE_RED_PACKET_INCOME="28";
	
	/** 页面类型：00=默认 **/
	public final static String PAGE_TYPE_DEFAULT = "00";
	/** 页面类型：01=永诚 **/
	public final static String PAGE_TYPE_YONGCHENG = "01";
	
	/** 永诚对接查勘员代码 **/
//	public final static String SURVEY_MAN_CODE = "100050022";
	/** 获取永诚案件间隔时间 **/
	public final static int YC_TIME = 15;
	
	/****任务状态
	/** 任务状态 0:待派单 1:待抢单  2:作业中 3:待审核 4:审核退回(作业中) 5:审核通过(完成) 9：任务注销 **/
	public final static String TASK_STATE_0 = "0";
	/** 任务状态 0:待派单 1:待抢单  2:作业中 3:待审核 4:审核退回(作业中) 5:审核通过(完成) 9：任务注销 **/
	public final static String TASK_STATE_1 = "1";
	/** 任务状态 0:待派单 1:待抢单  2:作业中 3:待审核 4:审核退回(作业中) 5:审核通过(完成) 9：任务注销 **/
	public final static String TASK_STATE_2 = "2";
	/** 任务状态 0:待派单 1:待抢单  2:作业中 3:待审核 4:审核退回(作业中) 5:审核通过(完成) 9：任务注销 **/
	public final static String TASK_STATE_3 = "3";
	/** 任务状态 0:待派单 1:待抢单  2:作业中 3:待审核 4:审核退回(作业中) 5:审核通过(完成) 9：任务注销 **/
	public final static String TASK_STATE_4 = "4";
	/** 任务状态 0:待派单 1:待抢单  2:作业中 3:待审核 4:审核退回(作业中) 5:审核通过(完成) 9：任务注销 **/
	public final static String TASK_STATE_5 = "5";
	/** 任务状态 0:待派单 1:待抢单  2:作业中 3:待审核 4:审核退回(作业中) 5:审核通过(完成) 9：任务注销 **/
	public final static String TASK_STATE_9 = "9";
	
	/**
	 * 订单类型/任务类型
	 */
	/** 订单类型/任务类型  0=查勘 */
	public final static String ORDER_TYPE_SURVEY = "0";
	/** 订单类型/任务类型 1=标的定损 */
	public final static String ORDER_TYPE_MAIN_LOSS = "1";
	/** 订单类型/任务类型 2=三者定损 */
	public final static String ORDER_TYPE_THIRD_LOSS = "2";
	/** 订单类型/任务类型 3=标的物损 */
	public final static String ORDER_TYPE_MAIN_DAMAGE = "3";
	/** 订单类型/任务类型 4=三者物损 */
	public final static String ORDER_TYPE_THIRD_DAMAGE = "4";
	/**货运险**/
	public final static String HY_COMMON="51"; 
	/**人伤-医院探视**/
	public final static String RS_SUBJECT_HOSPITAL="71";
	/**人伤-一次性调解**/
	public final static String RS_SUBJECT_MEDIATE="72";
	/**车险service_id**/
	public final static String INSURANCE_SERVICE_ID = "1";
	/**车险人伤service_id**/
	public final static String RS_SERVICE_ID = "7";
	
	/**下载文件临时目录**/
	public final static String DOWNLOAD_TEMP_DIR="downloadTempDir/"; 
	
	/**新价格体系上线**/
	public final static String NEW_PRICE_ONLINE_TIME="2015-11-14 01:50:00"; 
}