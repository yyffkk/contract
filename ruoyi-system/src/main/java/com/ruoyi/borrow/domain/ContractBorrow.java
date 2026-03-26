package com.ruoyi.borrow.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 合同借阅对象 contract_borrow
 * 
 * @author ruoyi
 * @date 2026-03-26
 */
public class ContractBorrow extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关联合同ID */
    @Excel(name = "关联合同ID")
    private Long contractId;

    /** 借阅单号 */
    @Excel(name = "借阅单号")
    private String borrowNo;

    /** 借阅人 */
    @Excel(name = "借阅人")
    private String borrower;

    /** 借阅部门 */
    @Excel(name = "借阅部门")
    private String borrowDepartment;

    /** 借阅日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "借阅日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date borrowDate;

    /** 预计归还日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预计归还日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date expectedReturnDate;

    /** 实际归还日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "实际归还日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date actualReturnDate;

    /** 借阅原因 */
    @Excel(name = "借阅原因")
    private String borrowReason;

    /** 合同名称 */
    @Excel(name = "合同名称")
    private String contractName;

    /** 合同编号 */
    @Excel(name = "合同编号")
    private String contractNumber;

    /** 合同类型 */
    @Excel(name = "合同类型")
    private String contractType;

    /** 合同金额 */
    @Excel(name = "合同金额")
    private BigDecimal contractAmount;

    /** 我方主体 */
    @Excel(name = "我方主体")
    private String ourParty;

    /** 对方主体 */
    @Excel(name = "对方主体")
    private String otherParty;

    /** 状态 (borrowing:借阅中, returned:已归还, overdue:已逾期) */
    @Excel(name = "状态 (borrowing:借阅中, returned:已归还, overdue:已逾期)")
    private String status;

    /** 审批状态 (pending:审批中, approved:已通过, rejected:已拒绝) */
    @Excel(name = "审批状态 (pending:审批中, approved:已通过, rejected:已拒绝)")
    private String approvalStatus;

    /** 删除标志 (0存在 2删除) */
    private String delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setContractId(Long contractId) 
    {
        this.contractId = contractId;
    }

    public Long getContractId() 
    {
        return contractId;
    }

    public void setBorrowNo(String borrowNo) 
    {
        this.borrowNo = borrowNo;
    }

    public String getBorrowNo() 
    {
        return borrowNo;
    }

    public void setBorrower(String borrower) 
    {
        this.borrower = borrower;
    }

    public String getBorrower() 
    {
        return borrower;
    }

    public void setBorrowDepartment(String borrowDepartment) 
    {
        this.borrowDepartment = borrowDepartment;
    }

    public String getBorrowDepartment() 
    {
        return borrowDepartment;
    }

    public void setBorrowDate(Date borrowDate) 
    {
        this.borrowDate = borrowDate;
    }

    public Date getBorrowDate() 
    {
        return borrowDate;
    }

    public void setExpectedReturnDate(Date expectedReturnDate) 
    {
        this.expectedReturnDate = expectedReturnDate;
    }

    public Date getExpectedReturnDate() 
    {
        return expectedReturnDate;
    }

    public void setActualReturnDate(Date actualReturnDate) 
    {
        this.actualReturnDate = actualReturnDate;
    }

    public Date getActualReturnDate() 
    {
        return actualReturnDate;
    }

    public void setBorrowReason(String borrowReason) 
    {
        this.borrowReason = borrowReason;
    }

    public String getBorrowReason() 
    {
        return borrowReason;
    }

    public void setContractName(String contractName) 
    {
        this.contractName = contractName;
    }

    public String getContractName() 
    {
        return contractName;
    }

    public void setContractNumber(String contractNumber) 
    {
        this.contractNumber = contractNumber;
    }

    public String getContractNumber() 
    {
        return contractNumber;
    }

    public void setContractType(String contractType) 
    {
        this.contractType = contractType;
    }

    public String getContractType() 
    {
        return contractType;
    }

    public void setContractAmount(BigDecimal contractAmount) 
    {
        this.contractAmount = contractAmount;
    }

    public BigDecimal getContractAmount() 
    {
        return contractAmount;
    }

    public void setOurParty(String ourParty) 
    {
        this.ourParty = ourParty;
    }

    public String getOurParty() 
    {
        return ourParty;
    }

    public void setOtherParty(String otherParty) 
    {
        this.otherParty = otherParty;
    }

    public String getOtherParty() 
    {
        return otherParty;
    }

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setApprovalStatus(String approvalStatus) 
    {
        this.approvalStatus = approvalStatus;
    }

    public String getApprovalStatus() 
    {
        return approvalStatus;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("contractId", getContractId())
            .append("borrowNo", getBorrowNo())
            .append("borrower", getBorrower())
            .append("borrowDepartment", getBorrowDepartment())
            .append("borrowDate", getBorrowDate())
            .append("expectedReturnDate", getExpectedReturnDate())
            .append("actualReturnDate", getActualReturnDate())
            .append("borrowReason", getBorrowReason())
            .append("contractName", getContractName())
            .append("contractNumber", getContractNumber())
            .append("contractType", getContractType())
            .append("contractAmount", getContractAmount())
            .append("ourParty", getOurParty())
            .append("otherParty", getOtherParty())
            .append("status", getStatus())
            .append("approvalStatus", getApprovalStatus())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
