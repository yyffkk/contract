package com.ruoyi.report.domain;

import java.math.BigDecimal;

public class SamplePatternStat {
    private String designer;
    private BigDecimal publishCount;
    private BigDecimal dailyAvgPublishCount;
    private BigDecimal storedCount;
    private BigDecimal avgCycleDays;

    public String getDesigner() {
        return designer;
    }

    public void setDesigner(String designer) {
        this.designer = designer;
    }

    public BigDecimal getPublishCount() {
        return publishCount;
    }

    public void setPublishCount(BigDecimal publishCount) {
        this.publishCount = publishCount;
    }

    public BigDecimal getDailyAvgPublishCount() {
        return dailyAvgPublishCount;
    }

    public void setDailyAvgPublishCount(BigDecimal dailyAvgPublishCount) {
        this.dailyAvgPublishCount = dailyAvgPublishCount;
    }

    public BigDecimal getStoredCount() {
        return storedCount;
    }

    public void setStoredCount(BigDecimal storedCount) {
        this.storedCount = storedCount;
    }

    public BigDecimal getAvgCycleDays() {
        return avgCycleDays;
    }

    public void setAvgCycleDays(BigDecimal avgCycleDays) {
        this.avgCycleDays = avgCycleDays;
    }
}
