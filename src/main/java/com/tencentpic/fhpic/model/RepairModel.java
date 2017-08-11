package com.tencentpic.fhpic.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class RepairModel extends BaseModel implements Serializable {
    private Integer id;

    private String repairName;

    private BigDecimal repairAmount;

    private String insertTime;

    private String guid;

    private Integer lossId;

    private String repairType;

    private String repairCode;

    private String repairWhour;

    private BigDecimal referAmount;

    private String remark;

    private Integer repairId;

    private Short isManual;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRepairName() {
        return repairName;
    }

    public void setRepairName(String repairName) {
        this.repairName = repairName == null ? null : repairName.trim();
    }

    public BigDecimal getRepairAmount() {
        return repairAmount;
    }

    public void setRepairAmount(BigDecimal repairAmount) {
        this.repairAmount = repairAmount;
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

    public String getRepairType() {
        return repairType;
    }

    public void setRepairType(String repairType) {
        this.repairType = repairType == null ? null : repairType.trim();
    }

    public String getRepairCode() {
        return repairCode;
    }

    public void setRepairCode(String repairCode) {
        this.repairCode = repairCode == null ? null : repairCode.trim();
    }

    public String getRepairWhour() {
        return repairWhour;
    }

    public void setRepairWhour(String repairWhour) {
        this.repairWhour = repairWhour == null ? null : repairWhour.trim();
    }

    public BigDecimal getReferAmount() {
        return referAmount;
    }

    public void setReferAmount(BigDecimal referAmount) {
        this.referAmount = referAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getRepairId() {
        return repairId;
    }

    public void setRepairId(Integer repairId) {
        this.repairId = repairId;
    }

    public Short getIsManual() {
        return isManual;
    }

    public void setIsManual(Short isManual) {
        this.isManual = isManual;
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
        RepairModel other = (RepairModel) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getRepairName() == null ? other.getRepairName() == null : this.getRepairName().equals(other.getRepairName()))
            && (this.getRepairAmount() == null ? other.getRepairAmount() == null : this.getRepairAmount().equals(other.getRepairAmount()))
            && (this.getInsertTime() == null ? other.getInsertTime() == null : this.getInsertTime().equals(other.getInsertTime()))
            && (this.getGuid() == null ? other.getGuid() == null : this.getGuid().equals(other.getGuid()))
            && (this.getLossId() == null ? other.getLossId() == null : this.getLossId().equals(other.getLossId()))
            && (this.getRepairType() == null ? other.getRepairType() == null : this.getRepairType().equals(other.getRepairType()))
            && (this.getRepairCode() == null ? other.getRepairCode() == null : this.getRepairCode().equals(other.getRepairCode()))
            && (this.getRepairWhour() == null ? other.getRepairWhour() == null : this.getRepairWhour().equals(other.getRepairWhour()))
            && (this.getReferAmount() == null ? other.getReferAmount() == null : this.getReferAmount().equals(other.getReferAmount()))
            && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
            && (this.getRepairId() == null ? other.getRepairId() == null : this.getRepairId().equals(other.getRepairId()))
            && (this.getIsManual() == null ? other.getIsManual() == null : this.getIsManual().equals(other.getIsManual()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRepairName() == null) ? 0 : getRepairName().hashCode());
        result = prime * result + ((getRepairAmount() == null) ? 0 : getRepairAmount().hashCode());
        result = prime * result + ((getInsertTime() == null) ? 0 : getInsertTime().hashCode());
        result = prime * result + ((getGuid() == null) ? 0 : getGuid().hashCode());
        result = prime * result + ((getLossId() == null) ? 0 : getLossId().hashCode());
        result = prime * result + ((getRepairType() == null) ? 0 : getRepairType().hashCode());
        result = prime * result + ((getRepairCode() == null) ? 0 : getRepairCode().hashCode());
        result = prime * result + ((getRepairWhour() == null) ? 0 : getRepairWhour().hashCode());
        result = prime * result + ((getReferAmount() == null) ? 0 : getReferAmount().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getRepairId() == null) ? 0 : getRepairId().hashCode());
        result = prime * result + ((getIsManual() == null) ? 0 : getIsManual().hashCode());
        return result;
    }
}