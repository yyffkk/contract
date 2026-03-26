package com.ruoyi.pm.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目发票关联对象 pm_project_invoice
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public class PmProjectInvoice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 项目ID */
    @Excel(name = "项目ID")
    private Long projectId;

    /** 发票ID */
    @Excel(name = "发票ID")
    private Long invoiceId;

    /** 发票代码 */
    @Excel(name = "发票代码")
    private String invoiceCode;

    /** 发票号码 */
    @Excel(name = "发票号码")
    private String invoiceNumber;

    /** 发票金额 */
    @Excel(name = "发票金额")
    private BigDecimal invoiceAmount;

    /** 发票类型(1开票 2收票) */
    @Excel(name = "发票类型(1开票 2收票)")
    private String invoiceType;

    /** 开票日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开票日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;

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

    public void setInvoiceId(Long invoiceId) 
    {
        this.invoiceId = invoiceId;
    }

    public Long getInvoiceId() 
    {
        return invoiceId;
    }

    public void setInvoiceCode(String invoiceCode) 
    {
        this.invoiceCode = invoiceCode;
    }

    public String getInvoiceCode() 
    {
        return invoiceCode;
    }

    public void setInvoiceNumber(String invoiceNumber) 
    {
        this.invoiceNumber = invoiceNumber;
    }

    public String getInvoiceNumber() 
    {
        return invoiceNumber;
    }

    public void setInvoiceAmount(BigDecimal invoiceAmount) 
    {
        this.invoiceAmount = invoiceAmount;
    }

    public BigDecimal getInvoiceAmount() 
    {
        return invoiceAmount;
    }

    public void setInvoiceType(String invoiceType) 
    {
        this.invoiceType = invoiceType;
    }

    public String getInvoiceType() 
    {
        return invoiceType;
    }

    public void setInvoiceDate(Date invoiceDate) 
    {
        this.invoiceDate = invoiceDate;
    }

    public Date getInvoiceDate() 
    {
        return invoiceDate;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("projectId", getProjectId())
            .append("invoiceId", getInvoiceId())
            .append("invoiceCode", getInvoiceCode())
            .append("invoiceNumber", getInvoiceNumber())
            .append("invoiceAmount", getInvoiceAmount())
            .append("invoiceType", getInvoiceType())
            .append("invoiceDate", getInvoiceDate())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .toString();
    }
}
