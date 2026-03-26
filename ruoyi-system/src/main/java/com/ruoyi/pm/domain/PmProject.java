package com.ruoyi.pm.domain;

import java.math.BigDecimal;
import java.util.List;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 项目信息对象 pm_project
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public class PmProject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 项目ID */
    private Long projectId;

    /** 项目名称 */
    @Excel(name = "项目名称")
    private String projectName;

    /** 项目编码 */
    @Excel(name = "项目编码")
    private String projectCode;

    /** 负责人ID列表 */
    @Excel(name = "负责人ID列表")
    private String personInCharge;

    /** 负责人姓名列表 */
    @Excel(name = "负责人姓名列表")
    private String personInChargeNames;

    /** 开始日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 结束日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "结束日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 状态(0启用 1停用) */
    @Excel(name = "状态(0启用 1停用)")
    private String status;

    /** 可见范围(0所有人 1指定) */
    @Excel(name = "可见范围(0所有人 1指定)")
    private String visibleScope;

    /** 可见用户ID列表 */
    @Excel(name = "可见用户ID列表")
    private String visibleUsers;

    /** 收入合同总额 */
    @Excel(name = "收入合同总额")
    private BigDecimal incomeContractTotal;

    /** 支出合同总额 */
    @Excel(name = "支出合同总额")
    private BigDecimal expenseContractTotal;

    /** 利润 */
    @Excel(name = "利润")
    private BigDecimal profit;

    /** 收款总额 */
    @Excel(name = "收款总额")
    private BigDecimal receiveTotal;

    /** 付款总额 */
    @Excel(name = "付款总额")
    private BigDecimal payTotal;

    /** 现金流 */
    @Excel(name = "现金流")
    private BigDecimal cashFlow;

    /** 开票总额 */
    @Excel(name = "开票总额")
    private BigDecimal invoiceIssueTotal;

    /** 收票总额 */
    @Excel(name = "收票总额")
    private BigDecimal invoiceReceiveTotal;

    /** 来源 */
    @Excel(name = "来源")
    private String source;

    /** 关联合同ID（仅前后端交互使用） */
    private Long relatedContractId;

    /** 关联合同名称（仅前后端交互使用） */
    private String relatedContractName;

    /** 关联合同编号（仅前后端交互使用） */
    private String relatedContractNumber;

    /** 关联合同金额（仅前后端交互使用） */
    private BigDecimal relatedContractAmount;

    /** 关联合同类型（仅前后端交互使用） */
    private String relatedContractType;

    /** 删除标志 */
    private String delFlag;

    /** 项目附件信息 */
    private List<PmProjectAttachment> pmProjectAttachmentList;

    public void setProjectId(Long projectId) 
    {
        this.projectId = projectId;
    }

    public Long getProjectId() 
    {
        return projectId;
    }

    public void setProjectName(String projectName) 
    {
        this.projectName = projectName;
    }

    public String getProjectName() 
    {
        return projectName;
    }

    public void setProjectCode(String projectCode) 
    {
        this.projectCode = projectCode;
    }

    public String getProjectCode() 
    {
        return projectCode;
    }

    public void setPersonInCharge(String personInCharge) 
    {
        this.personInCharge = personInCharge;
    }

    public String getPersonInCharge() 
    {
        return personInCharge;
    }

    public void setPersonInChargeNames(String personInChargeNames) 
    {
        this.personInChargeNames = personInChargeNames;
    }

    public String getPersonInChargeNames() 
    {
        return personInChargeNames;
    }

    public void setStartDate(Date startDate) 
    {
        this.startDate = startDate;
    }

    public Date getStartDate() 
    {
        return startDate;
    }

    public void setEndDate(Date endDate) 
    {
        this.endDate = endDate;
    }

    public Date getEndDate() 
    {
        return endDate;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setVisibleScope(String visibleScope) 
    {
        this.visibleScope = visibleScope;
    }

    public String getVisibleScope() 
    {
        return visibleScope;
    }

    public void setVisibleUsers(String visibleUsers) 
    {
        this.visibleUsers = visibleUsers;
    }

    public String getVisibleUsers() 
    {
        return visibleUsers;
    }

    public void setIncomeContractTotal(BigDecimal incomeContractTotal) 
    {
        this.incomeContractTotal = incomeContractTotal;
    }

    public BigDecimal getIncomeContractTotal() 
    {
        return incomeContractTotal;
    }

    public void setExpenseContractTotal(BigDecimal expenseContractTotal) 
    {
        this.expenseContractTotal = expenseContractTotal;
    }

    public BigDecimal getExpenseContractTotal() 
    {
        return expenseContractTotal;
    }

    public void setProfit(BigDecimal profit) 
    {
        this.profit = profit;
    }

    public BigDecimal getProfit() 
    {
        return profit;
    }

    public void setReceiveTotal(BigDecimal receiveTotal) 
    {
        this.receiveTotal = receiveTotal;
    }

    public BigDecimal getReceiveTotal() 
    {
        return receiveTotal;
    }

    public void setPayTotal(BigDecimal payTotal) 
    {
        this.payTotal = payTotal;
    }

    public BigDecimal getPayTotal() 
    {
        return payTotal;
    }

    public void setCashFlow(BigDecimal cashFlow) 
    {
        this.cashFlow = cashFlow;
    }

    public BigDecimal getCashFlow() 
    {
        return cashFlow;
    }

    public void setInvoiceIssueTotal(BigDecimal invoiceIssueTotal) 
    {
        this.invoiceIssueTotal = invoiceIssueTotal;
    }

    public BigDecimal getInvoiceIssueTotal() 
    {
        return invoiceIssueTotal;
    }

    public void setInvoiceReceiveTotal(BigDecimal invoiceReceiveTotal) 
    {
        this.invoiceReceiveTotal = invoiceReceiveTotal;
    }

    public BigDecimal getInvoiceReceiveTotal() 
    {
        return invoiceReceiveTotal;
    }

    public void setSource(String source) 
    {
        this.source = source;
    }

    public String getSource() 
    {
        return source;
    }

    public void setRelatedContractId(Long relatedContractId)
    {
        this.relatedContractId = relatedContractId;
    }

    public Long getRelatedContractId()
    {
        return relatedContractId;
    }

    public void setRelatedContractName(String relatedContractName)
    {
        this.relatedContractName = relatedContractName;
    }

    public String getRelatedContractName()
    {
        return relatedContractName;
    }

    public void setRelatedContractNumber(String relatedContractNumber)
    {
        this.relatedContractNumber = relatedContractNumber;
    }

    public String getRelatedContractNumber()
    {
        return relatedContractNumber;
    }

    public void setRelatedContractAmount(BigDecimal relatedContractAmount)
    {
        this.relatedContractAmount = relatedContractAmount;
    }

    public BigDecimal getRelatedContractAmount()
    {
        return relatedContractAmount;
    }

    public void setRelatedContractType(String relatedContractType)
    {
        this.relatedContractType = relatedContractType;
    }

    public String getRelatedContractType()
    {
        return relatedContractType;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public List<PmProjectAttachment> getPmProjectAttachmentList()
    {
        return pmProjectAttachmentList;
    }

    public void setPmProjectAttachmentList(List<PmProjectAttachment> pmProjectAttachmentList)
    {
        this.pmProjectAttachmentList = pmProjectAttachmentList;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("projectId", getProjectId())
            .append("projectName", getProjectName())
            .append("projectCode", getProjectCode())
            .append("personInCharge", getPersonInCharge())
            .append("personInChargeNames", getPersonInChargeNames())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("status", getStatus())
            .append("visibleScope", getVisibleScope())
            .append("visibleUsers", getVisibleUsers())
            .append("incomeContractTotal", getIncomeContractTotal())
            .append("expenseContractTotal", getExpenseContractTotal())
            .append("profit", getProfit())
            .append("receiveTotal", getReceiveTotal())
            .append("payTotal", getPayTotal())
            .append("cashFlow", getCashFlow())
            .append("invoiceIssueTotal", getInvoiceIssueTotal())
            .append("invoiceReceiveTotal", getInvoiceReceiveTotal())
            .append("remark", getRemark())
            .append("source", getSource())
            .append("relatedContractId", getRelatedContractId())
            .append("relatedContractName", getRelatedContractName())
            .append("relatedContractNumber", getRelatedContractNumber())
            .append("relatedContractAmount", getRelatedContractAmount())
            .append("relatedContractType", getRelatedContractType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .append("pmProjectAttachmentList", getPmProjectAttachmentList())
            .toString();
    }
}
