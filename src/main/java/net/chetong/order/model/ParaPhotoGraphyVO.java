package net.chetong.order.model;

import java.util.List;

/**
 * 影像标签类型
 * @author Administrator
 *
 */
public class ParaPhotoGraphyVO {
	private Long paraId;  
	private String trafficCode;	 	//交通工具代码
	private String trafficName;	 	//交通工具名称
	private String photoTypeCode;	//影像大类代码
	private String photoTypeName;	//影像大类名称
	private String photoCode;		//影像代码
	private String photoName;		//影像名称
	private String flag;			//有效标识
	private String remark;			//备注
	private String parent;

	
	public ParaPhotoGraphyVO(String parent) {
		this.parent = parent;
	}
	
	public ParaPhotoGraphyVO(String photoCode,String photoName){
		this.photoCode = photoCode;
		this.photoName = photoName;
	}
	
	public ParaPhotoGraphyVO() {
	}

	public Long getParaId() {
		return paraId;
	}
	public void setParaId(Long paraId) {
		this.paraId = paraId;
	}
	public String getTrafficCode() {
		return trafficCode;
	}
	public void setTrafficCode(String trafficCode) {
		this.trafficCode = trafficCode;
	}
	public String getTrafficName() {
		return trafficName;
	}
	public void setTrafficName(String trafficName) {
		this.trafficName = trafficName;
	}
	public String getPhotoTypeCode() {
		return photoTypeCode;
	}
	public void setPhotoTypeCode(String photoTypeCode) {
		this.photoTypeCode = photoTypeCode;
	}
	public String getPhotoTypeName() {
		return photoTypeName;
	}
	public void setPhotoTypeName(String photoTypeName) {
		this.photoTypeName = photoTypeName;
	}
	public String getPhotoCode() {
		return photoCode;
	}
	public void setPhotoCode(String photoCode) {
		this.photoCode = photoCode;
	}
	public String getPhotoName() {
		return photoName;
	}
	public void setPhotoName(String photoName) {
		this.photoName = photoName;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	
	
}
