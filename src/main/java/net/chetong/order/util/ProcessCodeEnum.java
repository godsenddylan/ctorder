package net.chetong.order.util;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import net.chetong.order.util.exception.ProcessException;
import net.chetong.order.util.page.domain.PageList;

/**
 * 接口调用统一应答码（包括错误码）
 * @author Dylan
 *
 */
public enum ProcessCodeEnum {
	/**
	 * 全局应答码(resultVO返回给页面的信息)
	 */
	SUCCESS("0000", "成功"),
	FAIL("9999", "失败"),
	TOKENFAIL("3333","登陆超时，请重新登陆"),
	
	REQUEST_PARAM_NULL("P001","必要参数为空"),
	
	/**
	 * 案件信息为空
	 */
	CASE_001("A001", "案件信息为空"),
	/**
	 * 报案号为空
	 */
	CASE_002("A002", "报案号为空"),
	/**报案号已经存在**/
	CASE_003("A003","此报案号已被使用，请重写报案号！"),
	
	
	/**
	 * 用户
	 */
	USR_001("U001","用户未登陆"),
	USR_003("U003","用户信息不存在"),
	
	/**
	 * 团队
	 */
	GRO_001("G001","无此机构或团队信息"),
	
	
	/**
	 * 数据库操作异常
	 */
	DATA_ERR("D999","系统数据处理异常"),
	
	/**
	 * 业务类异常
	 */
	PROCESS_ERR("P999","业务处理异常"),
	SUBMIT_REPEAT("P001","重复提交"),
	DEL_INJURED_PERSON_ERR("P002","不能删除已有一次性调解的伤者信息"),
	/**
	 * 内容管理类
	 */
	CMS_001("C001","广告信息不存在"),
	
	/**
	 * 查询地区出错
	 */
	QUERY_AREA_CODE_ERR("Q001","查询地区出错"),
	
	//order有关的异常
	/**
	 * 没有相应的服务内容
	 */
	ORDER_NO_SERVICE("O001","没有相应的服务内容"),
	
	/**安全码不正确**/
	VERIFY_CODE_ERR("V001","安全码不正确"),
	
	/**导入订单审核有没成功的**/
	IMPORT_HAVE_ERR("I001","以下订单审核失败"),
	
	/**审核订单必须在07状态**/
	ORDER_NO_AUDIT_STATE("O001","审核的订单非待审核状态"),
	ORDER_NO_CANCEL("O002","订单状态不可注销"),
	ORDER_NO_WORK("O003","没有作业权限或订单状态不对"),
	
	/**审核异常**/
	AUDIT_PAYER_NO_MONEY("AU001","作业地机构金额不足"),
	AUDIT_ENTRUST_NO_MONEY("AU002","代委托支付人金额不足"),
	AUDIT_NO_PERMISSION("AU003","当前用户没有此单审批权限"),
	AUDIT_NO_AUDIT_STATE("AU004","审核的订单非待审核状态"),
	
	/**下载案件详情没有已审核通过的订单**/
	DOWNLOAD_NO_AUDITED("D001","没有已审核通过的订单"),
	
	/**派单不存在案件信息**/
	SEND_NO_CASE("S001","不存在案件信息"),
	SEND_NO_AMOUNT("S002","派单账户余额不足,请先充值"),
	TAKE_NO_AMOUNT("S003","代支付账户余额不足,请先充值"),
	
	WORK_ERR_001("E001","驾驶证校验错误，请重新输入!"),
	WORK_ERR_002("E002","驾驶证不能为空!"),
	WORK_ERR_003("E003","车牌号校验错误，请重新输入！"),
	WORK_ERR_004("E004","车牌号不能为空！"),
	
	
	/**作业地无车险机构**/
	NO_ORG_ERR("E005","作业地无车险机构"),
	
	/**永诚校验 当定损总额为0时 必须录入一条为0的费用信息或修理项目或配件项目**/
	YC_ERR_010("E010","定损金额为0，请录入一条金额为0的修理或费用信息!"),
	YC_ERR_011("E011","定损金额为0，请录入一条金额为0的财物或费用信息"),
	YC_ERR_012("E012","定损提交前校验异常"),
	YC_ERR_013("E013","校验提交定损失败：未获取到定损信息"),
	YC_ERR_014("E014","校验提交定损失败：无此类定损类型"),
	
	ORDER_MOVE_REPEAT("Q001","订单已经迁移，不能再次迁移"),
	ORDER_MOVE_STATE_ERROR("Q002","迁移定损单时，必须先迁移查勘单"),
	
	ERROR_REMIND_CONFIG("R001", "您已配过此金额区间,或金额在已配置的区间内"),
	IMPORTAMT_CASE_REMIND_ERR("R002", "重大案件提醒异常"),
	MONEY_ERROR("R003", "起始金额不能大于或等于截止金额"),
	
	TRANSFORM_NO_ORDER("TRNAS001","此订单不可转派"),
	TRANSFORM_NO_PERMISSION("TRNAS002","您没有此单转派权限"),
	TRACK_LINK_OVERDUE("T001", "轨迹链接已过期"),
	TRACK_INFO_ISNULL("T002", "轨迹链接已过期")
	;
	
	//应答码
	private String code;
	
	//应答内容
	private String message;
	
	private ProcessCodeEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	//构造处理异常信息
	public ProcessException buildProcessException() {
		return new ProcessException(code, message);
	}
	
	public ProcessException buildProcessException(String msgAppend) {
		return new ProcessException(code, message + " -> " + msgAppend);
	}
	
	public ProcessException buildProcessException(Throwable e) {
		return new ProcessException(code, message, e);
	}
	
	public ProcessException buildProcessException(String msgAppend, Throwable e) {
		return new ProcessException(code, message + " -> " + msgAppend, e);
	}
	
	public <T> ProcessException buildProcessException(String msgAppend, Throwable e,ResultVO<T> resultVO) {
		resultVO.setResultCode(code);
		resultVO.setResultMsg(msgAppend);
		return new ProcessException(code, message + " -> " + msgAppend, e);
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> void buildResultVO(ResultVO<T> resultVO, T... arg) {
		try {
			T result = ArrayUtils.isNotEmpty(arg) ? arg[0] : null;
			resultVO.setResultObject(result);
			resultVO.setResultCode(code);
			resultVO.setResultMsg(message);
			if(result instanceof PageList && null != result){
				resultVO.setPaginator(((PageList<T>)result).getPaginator());
			}
		} catch (Exception e) {
			throw new ProcessException(PROCESS_ERR.code, PROCESS_ERR.message, e);
		}
		
	}
	
	//通过code获取enum
	public static ProcessCodeEnum getEnumByCode(String code){
		if(StringUtils.isBlank(code))
			return null;
		for (ProcessCodeEnum processCodeEnum : ProcessCodeEnum.values()) {
			if(processCodeEnum.code.equalsIgnoreCase(code))
				return processCodeEnum;
		}
		return null;
	}
	
	
	@SafeVarargs
	@SuppressWarnings("unchecked")
	public final <T> ResultVO<T> buildResultVOR(T... arg) {
		
		ResultVO<T> resultVO = new ResultVO<T>();
		
		try {
			T result = ArrayUtils.isNotEmpty(arg) ? arg[0] : null;
			resultVO.setResultObject(result);
			resultVO.setResultCode(code);
			resultVO.setResultMsg(message);
			if(result instanceof PageList && null != result){
				resultVO.setPaginator(((PageList<T>)result).getPaginator());
			}
		} catch (Exception e) {
			throw PROCESS_ERR.buildProcessException(e);
		}
		
		return resultVO;
		
	}
	
	//通过code获取message
	public static String getDescByCode(String code){
		if(StringUtils.isBlank(code))
			return null;
		for (ProcessCodeEnum processCodeEnum : ProcessCodeEnum.values()) {
			if(processCodeEnum.code.equalsIgnoreCase(code))
				return processCodeEnum.message;
		}
		return null;
	}

}
