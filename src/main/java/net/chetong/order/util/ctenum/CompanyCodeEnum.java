package net.chetong.order.util.ctenum;

public enum CompanyCodeEnum {
	YCBX("YCBX","永诚保险"),
	CTW("CTW","车童网");
	private String code = null;
	private String name = null;
	private CompanyCodeEnum(String code,String name) {
		this.code =  code;
		this.name =  name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
