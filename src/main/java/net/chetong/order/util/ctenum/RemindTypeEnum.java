package net.chetong.order.util.ctenum;

import org.apache.commons.lang.StringUtils;

import com.chetong.aic.annotation.EnumInfo;

/**
 * app类型
 * @author Dylan
 * @date 2015年11月25日
 * 1-余额，2-远程作业费，3-超额附加费,4-超重大案件',
 */
@EnumInfo({
	"1",
	"2",
	"3",
	"4"
})
public enum RemindTypeEnum {
	
	BALANCE("1","余额提醒"),
	REMOTE_FEE("2","远程作业费提醒"),
	EXTRA_CHARGE("3","附加费提醒"),
	IMPORTANT_CASE("4","重大案件提醒")
	;
	
	//编码
	private String code;
	
	//内容
	private String desc;
	
	private RemindTypeEnum(String code, String desc) {
		this.code = code;
		this.desc = desc;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public String getDesc() {
		return desc;
	}
	
	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	//通过code获取desc
	public static String getDescByCode(String code){
		if(StringUtils.isBlank(code))
			return null;
		for (RemindTypeEnum userTypeEnum : RemindTypeEnum.values()) {
			if(userTypeEnum.code.equalsIgnoreCase(code))
				return userTypeEnum.desc;
		}
		return null;
	} 

}
