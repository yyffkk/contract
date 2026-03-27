package com.ruoyi.invoice.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 发票信息对象 contract_invoice
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public class ContractInvoice extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关联合同ID */
    @Excel(name = "关联合同ID")
    private Long contractId;

    /** 发票代码 */
    @Excel(name = "发票代码")
    private String invoiceCode;

    /** 发票号码 */
    @Excel(name = "发票号码")
    private String invoiceNumber;

    /** 开票日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开票日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date invoiceDate;

    /** 发票金额 */
    @Excel(name = "发票金额")
    private BigDecimal invoiceAmount;

    /** 发票类型（vat:增值税专用, normal:普通发票） */
    @Excel(name = "发票类型", readConverterExp = "v=at:增值税专用,,n=ormal:普通发票")
    private String invoiceType;

    /** 发票状态（no_invoice:未开票, invoiced:已开票, voided:已作废） */
    @Excel(name = "发票状态", readConverterExp = "n=o_invoice:未开票,,i=nvoiced:已开票,,v=oided:已作废")
    private String invoiceStatus;

    /** 税率 */
    @Excel(name = "税率")
    private BigDecimal taxRate;

    /** 税额 */
    @Excel(name = "税额")
    private BigDecimal taxAmount;

    /** 购买方名称 */
    @Excel(name = "购买方名称")
    private String purchaserName;

    /** 购买方税号 */
    @Excel(name = "购买方税号")
    private String purchaserTaxNo;

    /** 销售方名称 */
    @Excel(name = "销售方名称")
    private String sellerName;

    /** 销售方税号 */
    @Excel(name = "销售方税号")
    private String sellerTaxNo;

    /** 相对方名称 */
    @Excel(name = "相对方名称")
    private String counterpartyName;

    /** 发起人 */
    @Excel(name = "发起人")
    private String initiator;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date applyTime;

    /** 所属部门 */
    @Excel(name = "所属部门")
    private String department;

    /** 开票内容 */
    @Excel(name = "开票内容")
    private String invoiceContent;

    /** 不含税金额 */
    @Excel(name = "不含税金额")
    private BigDecimal untaxedAmount;

    /** 所属项目 */
    @Excel(name = "所属项目")
    private String project;

    /** 款项类型（收入/支出） */
    @Excel(name = "发票分类", readConverterExp = "支出=进项,收入=销项")
    private String amountType;

    /** 关联合同名称 */
    @Excel(name = "关联合同名称")
    private String relatedContractName;

    /** 关联合同编号 */
    @Excel(name = "关联合同编号")
    private String relatedContractNumber;

    /** 审批状态 */
    private String approvalStatus;

    /** 审批人 */
    private String approver;

    /** 抄送人 */
    private String cc;

    /** 提交审批人 */
    private String submitter;

    /** 提交审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date submitTime;

    /** 审批完成时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date approveTime;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setId(Long id) { this.id = id; }
    public Long getId() { return id; }
    public void setContractId(Long contractId) { this.contractId = contractId; }
    public Long getContractId() { return contractId; }
    public void setInvoiceCode(String invoiceCode) { this.invoiceCode = invoiceCode; }
    public String getInvoiceCode() { return invoiceCode; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceDate(Date invoiceDate) { this.invoiceDate = invoiceDate; }
    public Date getInvoiceDate() { return invoiceDate; }
    public void setInvoiceAmount(BigDecimal invoiceAmount) { this.invoiceAmount = invoiceAmount; }
    public BigDecimal getInvoiceAmount() { return invoiceAmount; }
    public void setInvoiceType(String invoiceType) { this.invoiceType = invoiceType; }
    public String getInvoiceType() { return invoiceType; }
    public void setInvoiceStatus(String invoiceStatus) { this.invoiceStatus = invoiceStatus; }
    public String getInvoiceStatus() { return invoiceStatus; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setPurchaserName(String purchaserName) { this.purchaserName = purchaserName; }
    public String getPurchaserName() { return purchaserName; }
    public void setPurchaserTaxNo(String purchaserTaxNo) { this.purchaserTaxNo = purchaserTaxNo; }
    public String getPurchaserTaxNo() { return purchaserTaxNo; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }
    public String getSellerName() { return sellerName; }
    public void setSellerTaxNo(String sellerTaxNo) { this.sellerTaxNo = sellerTaxNo; }
    public String getSellerTaxNo() { return sellerTaxNo; }
    public void setCounterpartyName(String counterpartyName) { this.counterpartyName = counterpartyName; }
    public String getCounterpartyName() { return counterpartyName; }
    public void setInitiator(String initiator) { this.initiator = initiator; }
    public String getInitiator() { return initiator; }
    public void setApplyTime(Date applyTime) { this.applyTime = applyTime; }
    public Date getApplyTime() { return applyTime; }
    public void setDepartment(String department) { this.department = department; }
    public String getDepartment() { return department; }
    public void setInvoiceContent(String invoiceContent) { this.invoiceContent = invoiceContent; }
    public String getInvoiceContent() { return invoiceContent; }
    public void setUntaxedAmount(BigDecimal untaxedAmount) { this.untaxedAmount = untaxedAmount; }
    public BigDecimal getUntaxedAmount() { return untaxedAmount; }
    public void setProject(String project) { this.project = project; }
    public String getProject() { return project; }
    public void setAmountType(String amountType) { this.amountType = amountType; }
    public String getAmountType() { return amountType; }
    public void setRelatedContractName(String relatedContractName) { this.relatedContractName = relatedContractName; }
    public String getRelatedContractName() { return relatedContractName; }
    public void setRelatedContractNumber(String relatedContractNumber) { this.relatedContractNumber = relatedContractNumber; }
    public String getRelatedContractNumber() { return relatedContractNumber; }
    public void setApprovalStatus(String approvalStatus) { this.approvalStatus = approvalStatus; }
    public String getApprovalStatus() { return approvalStatus; }
    public void setDirectLeader(String directLeader) { this.directLeader = directLeader; }
    public String getDirectLeader() { return directLeader; }
    public void setApprover(String approver) { this.approver = approver; }
    public String getApprover() { return approver; }
    public void setHandler(String handler) { this.handler = handler; }
    public String getHandler() { return handler; }
    public void setCurrentApprovalNode(String currentApprovalNode) { this.currentApprovalNode = currentApprovalNode; }
    public String getCurrentApprovalNode() { return currentApprovalNode; }
    public void setCc(String cc) { this.cc = cc; }
    public String getCc() { return cc; }
    public void setSubmitter(String submitter) { this.submitter = submitter; }
    public String getSubmitter() { return submitter; }
    public void setSubmitTime(Date submitTime) { this.submitTime = submitTime; }
    public Date getSubmitTime() { return submitTime; }
    public void setApproveTime(Date approveTime) { this.approveTime = approveTime; }
    public Date getApproveTime() { return approveTime; }
    public void setDelFlag(String delFlag) { this.delFlag = delFlag; }
    public String getDelFlag() { return delFlag; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("contractId", getContractId())
            .append("invoiceCode", getInvoiceCode())
            .append("invoiceNumber", getInvoiceNumber())
            .append("invoiceDate", getInvoiceDate())
            .append("invoiceAmount", getInvoiceAmount())
            .append("invoiceType", getInvoiceType())
            .append("invoiceStatus", getInvoiceStatus())
            .append("taxRate", getTaxRate())
            .append("taxAmount", getTaxAmount())
            .append("purchaserName", getPurchaserName())
            .append("purchaserTaxNo", getPurchaserTaxNo())
            .append("sellerName", getSellerName())
            .append("sellerTaxNo", getSellerTaxNo())
            .append("project", getProject())
            .append("amountType", getAmountType())
            .append("relatedContractName", getRelatedContractName())
            .append("relatedContractNumber", getRelatedContractNumber())
            .append("approvalStatus", getApprovalStatus())
            .append("directLeader", getDirectLeader())
            .append("approver", getApprover())
            .append("handler", getHandler())
            .append("currentApprovalNode", getCurrentApprovalNode())
            .append("cc", getCc())
            .append("submitter", getSubmitter())
            .append("submitTime", getSubmitTime())
            .append("approveTime", getApproveTime())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
