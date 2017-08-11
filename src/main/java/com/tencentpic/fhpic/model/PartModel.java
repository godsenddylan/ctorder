package com.tencentpic.fhpic.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class PartModel extends BaseModel implements Serializable {
    private Integer id;

    private String partName;

    private String partCode;

    private String insertTime;

    private String guid;

    private Integer lossId;

    private BigDecimal marketPrice;

    private BigDecimal sPrice;

    private BigDecimal complexPrice;

    private String reamrk;

    private Integer partId;

    private Integer partNum;

	private Short isManual;

    private BigDecimal partPrice;
    
    private BigDecimal auditPrice;/*核价金额*/

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName == null ? null : partName.trim();
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode == null ? null : partCode.trim();
    }

    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime == null ? null : insertTime.trim();
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid == null ? null : guid.trim();
    }

    public Integer getLossId() {
        return lossId;
    }

    public void setLossId(Integer lossId) {
        this.lossId = lossId;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    public BigDecimal getsPrice() {
        return sPrice;
    }

    public void setsPrice(BigDecimal sPrice) {
        this.sPrice = sPrice;
    }

    public BigDecimal getComplexPrice() {
        return complexPrice;
    }

    public void setComplexPrice(BigDecimal complexPrice) {
        this.complexPrice = complexPrice;
    }

    public String getReamrk() {
        return reamrk;
    }

    public void setReamrk(String reamrk) {
        this.reamrk = reamrk == null ? null : reamrk.trim();
    }

    public Integer getPartId() {
        return partId;
    }

    public void setPartId(Integer partId) {
        this.partId = partId;
    }

    public Integer getPartNum() {
        return partNum;
    }

    public void setPartNum(Integer partNum) {
        this.partNum = partNum;
    }

    public Short getIsManual() {
        return isManual;
    }

    public void setIsManual(Short isManual) {
        this.isManual = isManual;
    }

    public BigDecimal getPartPrice() {
        return partPrice;
    }

    public void setPartPrice(BigDecimal partPrice) {
        this.partPrice = partPrice;
    }
    
    public BigDecimal getAuditPrice() {
		return auditPrice;
	}

	public void setAuditPrice(BigDecimal auditPrice) {
		this.auditPrice = auditPrice;
	}

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        PartModel other = (PartModel) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getPartName() == null ? other.getPartName() == null : this.getPartName().equals(other.getPartName()))
            && (this.getPartCode() == null ? other.getPartCode() == null : this.getPartCode().equals(other.getPartCode()))
            && (this.getInsertTime() == null ? other.getInsertTime() == null : this.getInsertTime().equals(other.getInsertTime()))
            && (this.getGuid() == null ? other.getGuid() == null : this.getGuid().equals(other.getGuid()))
            && (this.getLossId() == null ? other.getLossId() == null : this.getLossId().equals(other.getLossId()))
            && (this.getMarketPrice() == null ? other.getMarketPrice() == null : this.getMarketPrice().equals(other.getMarketPrice()))
            && (this.getsPrice() == null ? other.getsPrice() == null : this.getsPrice().equals(other.getsPrice()))
            && (this.getComplexPrice() == null ? other.getComplexPrice() == null : this.getComplexPrice().equals(other.getComplexPrice()))
            && (this.getReamrk() == null ? other.getReamrk() == null : this.getReamrk().equals(other.getReamrk()))
            && (this.getPartId() == null ? other.getPartId() == null : this.getPartId().equals(other.getPartId()))
            && (this.getPartNum() == null ? other.getPartNum() == null : this.getPartNum().equals(other.getPartNum()))
            && (this.getIsManual() == null ? other.getIsManual() == null : this.getIsManual().equals(other.getIsManual()))
            && (this.getPartPrice() == null ? other.getPartPrice() == null : this.getPartPrice().equals(other.getPartPrice()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getPartName() == null) ? 0 : getPartName().hashCode());
        result = prime * result + ((getPartCode() == null) ? 0 : getPartCode().hashCode());
        result = prime * result + ((getInsertTime() == null) ? 0 : getInsertTime().hashCode());
        result = prime * result + ((getGuid() == null) ? 0 : getGuid().hashCode());
        result = prime * result + ((getLossId() == null) ? 0 : getLossId().hashCode());
        result = prime * result + ((getMarketPrice() == null) ? 0 : getMarketPrice().hashCode());
        result = prime * result + ((getsPrice() == null) ? 0 : getsPrice().hashCode());
        result = prime * result + ((getComplexPrice() == null) ? 0 : getComplexPrice().hashCode());
        result = prime * result + ((getReamrk() == null) ? 0 : getReamrk().hashCode());
        result = prime * result + ((getPartId() == null) ? 0 : getPartId().hashCode());
        result = prime * result + ((getPartNum() == null) ? 0 : getPartNum().hashCode());
        result = prime * result + ((getIsManual() == null) ? 0 : getIsManual().hashCode());
        result = prime * result + ((getPartPrice() == null) ? 0 : getPartPrice().hashCode());
        return result;
    }
}