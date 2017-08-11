package com.tencentpic.fhpic.model;

import java.io.Serializable;

public class AuditModel extends BaseModel implements Serializable {
    private Integer id;

    private String orderCode;

    private String auditTime;

    private String auditOpinion;

    private String auditResult;

    private Integer taskId;

    private String auditType;

    private String extraReward;

    private String evaluateOpinion;

    private String serviceEvaluation;

    private String reply;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode == null ? null : orderCode.trim();
    }

    public String getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(String auditTime) {
        this.auditTime = auditTime == null ? null : auditTime.trim();
    }

    public String getAuditOpinion() {
        return auditOpinion;
    }

    public void setAuditOpinion(String auditOpinion) {
        this.auditOpinion = auditOpinion == null ? null : auditOpinion.trim();
    }

    public String getAuditResult() {
        return auditResult;
    }

    public void setAuditResult(String auditResult) {
        this.auditResult = auditResult == null ? null : auditResult.trim();
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getAuditType() {
        return auditType;
    }

    public void setAuditType(String auditType) {
        this.auditType = auditType == null ? null : auditType.trim();
    }

    public String getExtraReward() {
        return extraReward;
    }

    public void setExtraReward(String extraReward) {
        this.extraReward = extraReward == null ? null : extraReward.trim();
    }

    public String getEvaluateOpinion() {
        return evaluateOpinion;
    }

    public void setEvaluateOpinion(String evaluateOpinion) {
        this.evaluateOpinion = evaluateOpinion == null ? null : evaluateOpinion.trim();
    }

    public String getServiceEvaluation() {
        return serviceEvaluation;
    }

    public void setServiceEvaluation(String serviceEvaluation) {
        this.serviceEvaluation = serviceEvaluation == null ? null : serviceEvaluation.trim();
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply == null ? null : reply.trim();
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
        AuditModel other = (AuditModel) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getOrderCode() == null ? other.getOrderCode() == null : this.getOrderCode().equals(other.getOrderCode()))
            && (this.getAuditTime() == null ? other.getAuditTime() == null : this.getAuditTime().equals(other.getAuditTime()))
            && (this.getAuditOpinion() == null ? other.getAuditOpinion() == null : this.getAuditOpinion().equals(other.getAuditOpinion()))
            && (this.getAuditResult() == null ? other.getAuditResult() == null : this.getAuditResult().equals(other.getAuditResult()))
            && (this.getTaskId() == null ? other.getTaskId() == null : this.getTaskId().equals(other.getTaskId()))
            && (this.getAuditType() == null ? other.getAuditType() == null : this.getAuditType().equals(other.getAuditType()))
            && (this.getExtraReward() == null ? other.getExtraReward() == null : this.getExtraReward().equals(other.getExtraReward()))
            && (this.getEvaluateOpinion() == null ? other.getEvaluateOpinion() == null : this.getEvaluateOpinion().equals(other.getEvaluateOpinion()))
            && (this.getServiceEvaluation() == null ? other.getServiceEvaluation() == null : this.getServiceEvaluation().equals(other.getServiceEvaluation()))
            && (this.getReply() == null ? other.getReply() == null : this.getReply().equals(other.getReply()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getOrderCode() == null) ? 0 : getOrderCode().hashCode());
        result = prime * result + ((getAuditTime() == null) ? 0 : getAuditTime().hashCode());
        result = prime * result + ((getAuditOpinion() == null) ? 0 : getAuditOpinion().hashCode());
        result = prime * result + ((getAuditResult() == null) ? 0 : getAuditResult().hashCode());
        result = prime * result + ((getTaskId() == null) ? 0 : getTaskId().hashCode());
        result = prime * result + ((getAuditType() == null) ? 0 : getAuditType().hashCode());
        result = prime * result + ((getExtraReward() == null) ? 0 : getExtraReward().hashCode());
        result = prime * result + ((getEvaluateOpinion() == null) ? 0 : getEvaluateOpinion().hashCode());
        result = prime * result + ((getServiceEvaluation() == null) ? 0 : getServiceEvaluation().hashCode());
        result = prime * result + ((getReply() == null) ? 0 : getReply().hashCode());
        return result;
    }
}