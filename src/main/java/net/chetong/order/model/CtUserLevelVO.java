package net.chetong.order.model;

import java.util.Date;

public class CtUserLevelVO {
    private Long id;

    private Long userId;

    private String userName;

    private Long upUserId;

    private String upUserName;

    private Date addDate;

    private String status;

    private String isWorkPrice;

    private String ext1;

    private String ext2;

    private String ext3;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Long getUpUserId() {
        return upUserId;
    }

    public void setUpUserId(Long upUserId) {
        this.upUserId = upUserId;
    }

    public String getUpUserName() {
        return upUserName;
    }

    public void setUpUserName(String upUserName) {
        this.upUserName = upUserName == null ? null : upUserName.trim();
    }

    public Date getAddDate() {
        return addDate;
    }

    public void setAddDate(Date addDate) {
        this.addDate = addDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status == null ? null : status.trim();
    }

    public String getIsWorkPrice() {
        return isWorkPrice;
    }

    public void setIsWorkPrice(String isWorkPrice) {
        this.isWorkPrice = isWorkPrice == null ? null : isWorkPrice.trim();
    }

    public String getExt1() {
        return ext1;
    }

    public void setExt1(String ext1) {
        this.ext1 = ext1 == null ? null : ext1.trim();
    }

    public String getExt2() {
        return ext2;
    }

    public void setExt2(String ext2) {
        this.ext2 = ext2 == null ? null : ext2.trim();
    }

    public String getExt3() {
        return ext3;
    }

    public void setExt3(String ext3) {
        this.ext3 = ext3 == null ? null : ext3.trim();
    }
}