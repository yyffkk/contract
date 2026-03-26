package com.ruoyi.pm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目账款关联对象 pm_project_payment
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public class PmProjectPayment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Long projectId;

    /** 账款ID */
    @Excel(name = "账款ID")
    private Long paymentId;

    /** 账款名称 */
    @Excel(name = "账款名称")
    private String paymentName;

    /** 账款金额 */
    @Excel(name = "账款金额")
    private BigDecimal paymentAmount;

    /** 账款类型(1收款 2付款) */
    @Excel(name = "账款类型(1收款 2付款)")
    private String paymentType;

    /** 账款日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "账款日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date paymentDate;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setProjectId(Long projectId) 
    {
        this.projectId = projectId;
    }

    public Long getProjectId() 
    {
        return projectId;
    }

    public void setPaymentId(Long paymentId) 
    {
        this.paymentId = paymentId;
    }

    public Long getPaymentId() 
    {
        return paymentId;
    }

    public void setPaymentName(String paymentName) 
    {
        this.paymentName = paymentName;
    }

    public String getPaymentName() 
    {
        return paymentName;
    }

    public void setPaymentAmount(BigDecimal paymentAmount) 
    {
        this.paymentAmount = paymentAmount;
    }

    public BigDecimal getPaymentAmount() 
    {
        return paymentAmount;
    }

    public void setPaymentType(String paymentType) 
    {
        this.paymentType = paymentType;
    }

    public String getPaymentType() 
    {
        return paymentType;
    }

    public void setPaymentDate(Date paymentDate) 
    {
        this.paymentDate = paymentDate;
    }

    public Date getPaymentDate() 
    {
        return paymentDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("projectId", getProjectId())
            .append("paymentId", getPaymentId())
            .append("paymentName", getPaymentName())
            .append("paymentAmount", getPaymentAmount())
            .append("paymentType", getPaymentType())
            .append("paymentDate", getPaymentDate())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .toString();
    }
}
