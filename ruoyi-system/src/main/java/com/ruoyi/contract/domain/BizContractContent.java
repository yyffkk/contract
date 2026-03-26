package com.ruoyi.contract.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 智能合同审批对象 biz_contract_content
 *
 * @author ruoyi
 * @date 2026-03-14
 */
public class BizContractContent extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 合同名称 */
    @Excel(name = "合同标题")
    private String contractName;

    /** 合同编号 */
    @Excel(name = "合同编码")
    private String contractNumber;

    private String category;
    private String numberMode;

    /** 金额类型 */
    @Excel(name = "金额类型")
    private String amountType;

    /** 合同总额（元） */
    @Excel(name = "金额", readConverterExp = "元")
    private BigDecimal totalAmount;

    private String termType;
    private String archiver;
    private String cooperators;
    private String description;
    private String myPartyName;
    private String myContact;

    /** 对方主体名称 */
    @Excel(name = "合同对象")
    private String otherPartyName;

    private String otherContact;
    private String signMethod;

    /** 签署日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签订时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date signDate;

    private String sealType;
    private String approver;
    private String cc;
    private String file;
    private String attachments;

    /** 归属人 */
    @Excel(name = "负责人")
    private String owner;

    private String content;
    private String signStatus;
    private String performanceStatus;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "生效日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    private Date endDate;
    private Date applyTime;
    private String submitter;
    private String paymentProgress;
    private BigDecimal receivedPaid;
    private BigDecimal pendingReceivedPaid;
    private String invoicingProgress;
    private Long issuedReceived;
    private Long pendingIssuedReceived;
    private String controlGroup;
    private String source;
    private String signRemarks;
    private String archiveRemarks;
    private String proxySubmitter;

    /** 归属部门 */
    @Excel(name = "部门")
    private String department;

    private String archiveFile;
    private Date archiveTime;
    private String accountStatus;
    private String invoiceStatus;
    private String approvalNumber;
    private String project;
    private Date approvePassTime;
    private String borrowStatus;
    private String archiveId;
    private String archiveApprovalNumber;
    private String commentMentioned;
    private Long commentCount;
    private BigDecimal otherPaymentAmount;
    private BigDecimal otherReceiptAmount;
    private String totalAmountCalculationMethod;
    private Long delFlag;

    // ========== Getter / Setter ==========

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }

    public String getContractName() {
        return contractName;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setNumberMode(String numberMode) {
        this.numberMode = numberMode;
    }

    public String getNumberMode() {
        return numberMode;
    }

    public void setAmountType(String amountType) {
        this.amountType = amountType;
    }

    public String getAmountType() {
        return amountType;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getTermType() {
        return termType;
    }

    public void setArchiver(String archiver) {
        this.archiver = archiver;
    }

    public String getArchiver() {
        return archiver;
    }

    public void setCooperators(String cooperators) {
        this.cooperators = cooperators;
    }

    public String getCooperators() {
        return cooperators;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setMyPartyName(String myPartyName) {
        this.myPartyName = myPartyName;
    }

    public String getMyPartyName() {
        return myPartyName;
    }

    public void setMyContact(String myContact) {
        this.myContact = myContact;
    }

    public String getMyContact() {
        return myContact;
    }

    public void setOtherPartyName(String otherPartyName) {
        this.otherPartyName = otherPartyName;
    }

    public String getOtherPartyName() {
        return otherPartyName;
    }

    public void setOtherContact(String otherContact) {
        this.otherContact = otherContact;
    }

    public String getOtherContact() {
        return otherContact;
    }

    public void setSignMethod(String signMethod) {
        this.signMethod = signMethod;
    }

    public String getSignMethod() {
        return signMethod;
    }

    public void setSignDate(Date signDate) {
        this.signDate = signDate;
    }

    public Date getSignDate() {
        return signDate;
    }

    public void setSealType(String sealType) {
        this.sealType = sealType;
    }

    public String getSealType() {
        return sealType;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getApprover() {
        return approver;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getCc() {
        return cc;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setSignStatus(String signStatus) {
        this.signStatus = signStatus;
    }

    public String getSignStatus() {
        return signStatus;
    }

    public void setPerformanceStatus(String performanceStatus) {
        this.performanceStatus = performanceStatus;
    }

    public String getPerformanceStatus() {
        return performanceStatus;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setPaymentProgress(String paymentProgress) {
        this.paymentProgress = paymentProgress;
    }

    public String getPaymentProgress() {
        return paymentProgress;
    }

    public void setReceivedPaid(BigDecimal receivedPaid) {
        this.receivedPaid = receivedPaid;
    }

    public BigDecimal getReceivedPaid() {
        return receivedPaid;
    }

    public void setPendingReceivedPaid(BigDecimal pendingReceivedPaid) {
        this.pendingReceivedPaid = pendingReceivedPaid;
    }

    public BigDecimal getPendingReceivedPaid() {
        return pendingReceivedPaid;
    }

    public void setInvoicingProgress(String invoicingProgress) {
        this.invoicingProgress = invoicingProgress;
    }

    public String getInvoicingProgress() {
        return invoicingProgress;
    }

    public void setIssuedReceived(Long issuedReceived) {
        this.issuedReceived = issuedReceived;
    }

    public Long getIssuedReceived() {
        return issuedReceived;
    }

    public void setPendingIssuedReceived(Long pendingIssuedReceived) {
        this.pendingIssuedReceived = pendingIssuedReceived;
    }

    public Long getPendingIssuedReceived() {
        return pendingIssuedReceived;
    }

    public void setControlGroup(String controlGroup) {
        this.controlGroup = controlGroup;
    }

    public String getControlGroup() {
        return controlGroup;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setSignRemarks(String signRemarks) {
        this.signRemarks = signRemarks;
    }

    public String getSignRemarks() {
        return signRemarks;
    }

    public void setArchiveRemarks(String archiveRemarks) {
        this.archiveRemarks = archiveRemarks;
    }

    public String getArchiveRemarks() {
        return archiveRemarks;
    }

    public void setProxySubmitter(String proxySubmitter) {
        this.proxySubmitter = proxySubmitter;
    }

    public String getProxySubmitter() {
        return proxySubmitter;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setArchiveFile(String archiveFile) {
        this.archiveFile = archiveFile;
    }

    public String getArchiveFile() {
        return archiveFile;
    }

    public void setArchiveTime(Date archiveTime) {
        this.archiveTime = archiveTime;
    }

    public Date getArchiveTime() {
        return archiveTime;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setInvoiceStatus(String invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    public String getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setApprovalNumber(String approvalNumber) {
        this.approvalNumber = approvalNumber;
    }

    public String getApprovalNumber() {
        return approvalNumber;
    }

    public void setProject(String project) {
        this.project = project;
    }

    public String getProject() {
        return project;
    }

    public void setApprovePassTime(Date approvePassTime) {
        this.approvePassTime = approvePassTime;
    }

    public Date getApprovePassTime() {
        return approvePassTime;
    }

    public void setBorrowStatus(String borrowStatus) {
        this.borrowStatus = borrowStatus;
    }

    public String getBorrowStatus() {
        return borrowStatus;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveApprovalNumber(String archiveApprovalNumber) {
        this.archiveApprovalNumber = archiveApprovalNumber;
    }

    public String getArchiveApprovalNumber() {
        return archiveApprovalNumber;
    }

    public void setCommentMentioned(String commentMentioned) {
        this.commentMentioned = commentMentioned;
    }

    public String getCommentMentioned() {
        return commentMentioned;
    }

    public void setCommentCount(Long commentCount) {
        this.commentCount = commentCount;
    }

    public Long getCommentCount() {
        return commentCount;
    }

    public void setOtherPaymentAmount(BigDecimal otherPaymentAmount) {
        this.otherPaymentAmount = otherPaymentAmount;
    }

    public BigDecimal getOtherPaymentAmount() {
        return otherPaymentAmount;
    }

    public void setOtherReceiptAmount(BigDecimal otherReceiptAmount) {
        this.otherReceiptAmount = otherReceiptAmount;
    }

    public BigDecimal getOtherReceiptAmount() {
        return otherReceiptAmount;
    }

    public void setTotalAmountCalculationMethod(String totalAmountCalculationMethod) {
        this.totalAmountCalculationMethod = totalAmountCalculationMethod;
    }

    public String getTotalAmountCalculationMethod() {
        return totalAmountCalculationMethod;
    }

    public void setDelFlag(Long delFlag) {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("contractName", getContractName())
                .append("contractNumber", getContractNumber())
                .append("category", getCategory())
                .append("numberMode", getNumberMode())
                .append("amountType", getAmountType())
                .append("totalAmount", getTotalAmount())
                .append("termType", getTermType())
                .append("archiver", getArchiver())
                .append("cooperators", getCooperators())
                .append("description", getDescription())
                .append("myPartyName", getMyPartyName())
                .append("myContact", getMyContact())
                .append("otherPartyName", getOtherPartyName())
                .append("otherContact", getOtherContact())
                .append("signMethod", getSignMethod())
                .append("signDate", getSignDate())
                .append("sealType", getSealType())
                .append("approver", getApprover())
                .append("cc", getCc())
                .append("file", getFile())
                .append("attachments", getAttachments())
                .append("owner", getOwner())
                .append("content", getContent())
                .append("signStatus", getSignStatus())
                .append("performanceStatus", getPerformanceStatus())
                .append("startDate", getStartDate())
                .append("endDate", getEndDate())
                .append("applyTime", getApplyTime())
                .append("submitter", getSubmitter())
                .append("paymentProgress", getPaymentProgress())
                .append("receivedPaid", getReceivedPaid())
                .append("pendingReceivedPaid", getPendingReceivedPaid())
                .append("invoicingProgress", getInvoicingProgress())
                .append("issuedReceived", getIssuedReceived())
                .append("pendingIssuedReceived", getPendingIssuedReceived())
                .append("controlGroup", getControlGroup())
                .append("source", getSource())
                .append("signRemarks", getSignRemarks())
                .append("archiveRemarks", getArchiveRemarks())
                .append("proxySubmitter", getProxySubmitter())
                .append("department", getDepartment())
                .append("archiveFile", getArchiveFile())
                .append("archiveTime", getArchiveTime())
                .append("accountStatus", getAccountStatus())
                .append("invoiceStatus", getInvoiceStatus())
                .append("approvalNumber", getApprovalNumber())
                .append("project", getProject())
                .append("approvePassTime", getApprovePassTime())
                .append("borrowStatus", getBorrowStatus())
                .append("archiveId", getArchiveId())
                .append("archiveApprovalNumber", getArchiveApprovalNumber())
                .append("commentMentioned", getCommentMentioned())
                .append("commentCount", getCommentCount())
                .append("otherPaymentAmount", getOtherPaymentAmount())
                .append("otherReceiptAmount", getOtherReceiptAmount())
                .append("totalAmountCalculationMethod", getTotalAmountCalculationMethod())
                .append("createTime", getCreateTime())
                .append("updateTime", getUpdateTime())
                .append("delFlag", getDelFlag())
                .toString();
    }
}