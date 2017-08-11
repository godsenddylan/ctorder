package net.chetong.order.model;

import java.math.BigDecimal;

public class PrGuidePriceDetailVO {
    private Long id;

    private Long guideId;

    private String priceType;

    private BigDecimal priceMoney;

    private String priceMode;

    private BigDecimal startVal;

    private BigDecimal endVal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGuideId() {
        return guideId;
    }

    public void setGuideId(Long guideId) {
        this.guideId = guideId;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType == null ? null : priceType.trim();
    }

    public BigDecimal getPriceMoney() {
        return priceMoney;
    }

    public void setPriceMoney(BigDecimal priceMoney) {
        this.priceMoney = priceMoney;
    }

    public String getPriceMode() {
        return priceMode;
    }

    public void setPriceMode(String priceMode) {
        this.priceMode = priceMode == null ? null : priceMode.trim();
    }

    public BigDecimal getStartVal() {
        return startVal;
    }

    public void setStartVal(BigDecimal startVal) {
        this.startVal = startVal;
    }

    public BigDecimal getEndVal() {
        return endVal;
    }

    public void setEndVal(BigDecimal endVal) {
        this.endVal = endVal;
    }
}