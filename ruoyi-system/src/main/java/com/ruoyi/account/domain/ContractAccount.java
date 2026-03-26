package com.ruoyi.account.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 账款信息对象 contract_account
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public class ContractAccount extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 关联合同ID */
    @Excel(name = "关联合同ID")
    private Long contractId;

    /** 账款名称 */
    @Excel(name = "账款名称")
    private String accountName;

    /** 账款日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "账款日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date accountDate;

    /** 账款金额 */
    @Excel(name = "账款金额")
    private BigDecimal amount;

    /** 金额类型（fixed:固定金额, variable:浮动金额） */
    @Excel(name = "金额类型", readConverterExp = "f=ixed:固定金额,,v=ariable:浮动金额")
    private String amountType;

    /** 单据号 */
    @Excel(name = "单据号")
    private String orderNo;

    /** 我方主体 */
    @Excel(name = "我方主体")
    private String ourParty;

    /** 对方主体 */
    @Excel(name = "对方主体")
    private String otherParty;

    /** 关联合同名称 */
    @Excel(name = "关联合同名称")
    private String relatedContractName;

    /** 关联合同编号 */
    @Excel(name = "关联合同编号")
    private String relatedContractNumber;

    /** 状态（unpaid:未付款, paid:已付款） */
    @Excel(name = "状态", readConverterExp = "u=npaid:未付款,,p=aid:已付款")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    /** 查询：主体名称 */
    private String partyName;

    /** 查询：预警状态 */
    private String warningType;

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

    public void setAccountName(String accountName) 
    {
        this.accountName = accountName;
    }

    public String getAccountName() 
    {
        return accountName;
    }

    public void setAccountDate(Date accountDate) 
    {
        this.accountDate = accountDate;
    }

    public Date getAccountDate() 
    {
        return accountDate;
    }

    public void setAmount(BigDecimal amount) 
    {
        this.amount = amount;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public void setAmountType(String amountType) 
    {
        this.amountType = amountType;
    }

    public String getAmountType() 
    {
        return amountType;
    }

    public void setOrderNo(String orderNo) 
    {
        this.orderNo = orderNo;
    }

    public String getOrderNo() 
    {
        return orderNo;
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

    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }

    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    public void setPartyName(String partyName)
    {
        this.partyName = partyName;
    }

    public String getPartyName()
    {
        return partyName;
    }

    public void setWarningType(String warningType)
    {
        this.warningType = warningType;
    }

    public String getWarningType()
    {
        return warningType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("contractId", getContractId())
            .append("accountName", getAccountName())
            .append("accountDate", getAccountDate())
            .append("amount", getAmount())
            .append("amountType", getAmountType())
            .append("orderNo", getOrderNo())
            .append("ourParty", getOurParty())
            .append("otherParty", getOtherParty())
            .append("relatedContractName", getRelatedContractName())
            .append("relatedContractNumber", getRelatedContractNumber())
            .append("remark", getRemark())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
