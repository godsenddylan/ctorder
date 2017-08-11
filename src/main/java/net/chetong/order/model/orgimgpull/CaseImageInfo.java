package net.chetong.order.model.orgimgpull;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.chetong.order.model.orgimgpull.CarModel;

/**
 * 车童网获取的图片url对象
 * Copyright (c) ,深圳市车童网络技术有限公司
 * All rights reserved
 * @author wufj
 * 2016年12月29日
 */
public class CaseImageInfo {
	private String caseNo;//报案号
	private String accidentTime;//委托时间
	private String sellerName;//车童姓名
	private String buyerName;//买家名称
	private String carNo;//车牌号
	private List<CarModel> cars = new ArrayList<CarModel>();
	private List<ImageModel> images = new ArrayList<ImageModel>();
	
	private String guid;

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getAccidentTime() {
		return accidentTime;
	}

	public void setAccidentTime(String accidentTime) {
		this.accidentTime = accidentTime;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getCarNo() {
		return carNo;
	}

	public void setCarNo(String carNo) {
		this.carNo = carNo;
	}

	public List<CarModel> getCars() {
		return cars;
	}

	public void setCars(List<CarModel> cars) {
		this.cars = cars;
	}

	public List<ImageModel> getImages() {
		return images;
	}

	public void setImages(List<ImageModel> images) {
		this.images = images;
	}

	@JsonIgnore
	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

}
