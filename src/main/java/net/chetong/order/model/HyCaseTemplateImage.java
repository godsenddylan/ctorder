package net.chetong.order.model;

import java.util.Date;

public class HyCaseTemplateImage {

	/**
	 * 模板图片信息
	 * 
	 * @author 2016年01月15日 下午3:44:17
	 */
	private String id;// ID
	private String templateId;// 图片所属模板id
	private String url;// 腾讯云图片地址
	private String page;// 图片的顺序（如果分页，此为页的顺序）
	private String imageSize;// 图片大小

	public String getImageSize() {
		return imageSize;
	}
	public void setImageSize(String imageSize) {
		this.imageSize = imageSize;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTemplateId() {
		return templateId;
	}
	public void setTemplateId(String templateId) {
		this.templateId = templateId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getPage() {
		return page;
	}
	public void setPage(String page) {
		this.page = page;
	}

	

}
