/**  
 * @Title: FmOrderCase.java
 * @Package net.chetong.order.model
 * @Description: TODO
 * @author zhouchushu
 * @date 2016年1月29日 下午5:16:03
 */
package net.chetong.order.model;

import java.util.Date;

/**
 * ClassName: FmOrderCase 
 * @Description: 报案信息表
 * @author zhouchushu
 * @date 2016年1月29日 下午5:16:03
 */
public class FmOrderCase {
	
    private Long id;

    private String caseNo;

    private String status;

    private String caseTime;

    private String isAlert;

    private String accidentTime;

    private String accidentAddress;

    private Long creator;

    private String createTime;

    private String delegateInfo;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo == null ? null : caseNo.trim();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getCaseTime() {
        return caseTime;
    }

    public void setCaseTime(String caseTime) {
        this.caseTime = caseTime;
    }

    public String getIsAlert() {
        return isAlert;
    }

    public void setIsAlert(String isAlert) {
        this.isAlert = isAlert == null ? null : isAlert.trim();
    }

    public String getAccidentTime() {
        return accidentTime;
    }

    public void setAccidentTime(String accidentTime) {
        this.accidentTime = accidentTime;
    }

    public String getAccidentAddress() {
        return accidentAddress;
    }

    public void setAccidentAddress(String accidentAddress) {
        this.accidentAddress = accidentAddress == null ? null : accidentAddress.trim();
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDelegateInfo() {
        return delegateInfo;
    }

    public void setDelegateInfo(String delegateInfo) {
        this.delegateInfo = delegateInfo == null ? null : delegateInfo.trim();
    }
	
	

}
