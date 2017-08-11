package net.chetong.order.util.ctenum;

/**
 * 调度排班表用户类型
 * Copyright (c) 2017,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * @date 2017年2月17日
 */
public enum RosterType {
	/**车童**/
	CT("0"),
	/**团队长->协调人**/
	TEAM("1"),
	/**受阻人**/
	SUFFOCATER("2"),
	/**机构负责人**/
	ORG_PERSON("3");
	
	private String value;
	private RosterType(String value){
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
