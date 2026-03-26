package com.ruoyi.counterpart.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 相对方信息对象 counterpart_info
 * 
 * @author ruoyi
 * @date 2026-03-17
 */
public class CounterpartInfo extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 相对方名称 */
    @Excel(name = "相对方名称")
    private String counterpartName;

    /** 所属员工工号 */
    @Excel(name = "所属员工工号")
    private String employeeId;

    /** 开户银行 */
    @Excel(name = "开户银行")
    private String bankName;

    /** 开户名称 */
    @Excel(name = "开户名称")
    private String accountName;

    /** 银行账户 */
    @Excel(name = "银行账户")
    private String bankAccount;

    /** 纳税人识别号 */
    @Excel(name = "纳税人识别号")
    private String taxId;

    /** 地址 */
    @Excel(name = "地址")
    private String address;

    /** 电话 */
    @Excel(name = "电话")
    private String phone;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setCounterpartName(String counterpartName) 
    {
        this.counterpartName = counterpartName;
    }

    public String getCounterpartName() 
    {
        return counterpartName;
    }

    public void setEmployeeId(String employeeId) 
    {
        this.employeeId = employeeId;
    }

    public String getEmployeeId() 
    {
        return employeeId;
    }

    public void setBankName(String bankName) 
    {
        this.bankName = bankName;
    }

    public String getBankName() 
    {
        return bankName;
    }

    public void setAccountName(String accountName) 
    {
        this.accountName = accountName;
    }

    public String getAccountName() 
    {
        return accountName;
    }

    public void setBankAccount(String bankAccount) 
    {
        this.bankAccount = bankAccount;
    }

    public String getBankAccount() 
    {
        return bankAccount;
    }

    public void setTaxId(String taxId) 
    {
        this.taxId = taxId;
    }

    public String getTaxId() 
    {
        return taxId;
    }

    public void setAddress(String address) 
    {
        this.address = address;
    }

    public String getAddress() 
    {
        return address;
    }

    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("counterpartName", getCounterpartName())
            .append("employeeId", getEmployeeId())
            .append("bankName", getBankName())
            .append("accountName", getAccountName())
            .append("bankAccount", getBankAccount())
            .append("taxId", getTaxId())
            .append("address", getAddress())
            .append("phone", getPhone())
            .append("createTime", getCreateTime())
            .toString();
    }
}
