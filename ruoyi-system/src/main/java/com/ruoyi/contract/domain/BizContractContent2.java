package com.ruoyi.contract.domain;

import java.math.BigDecimal;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 用印审批单对象 biz_contract_content2
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public class BizContractContent2 extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Long id;

    /** 审批单名称 */
    @Excel(name = "审批单名称")
    private String name;

    /** 归属人 */
    @Excel(name = "归属人")
    private String owner;

    /** 正文文件路径（PDF/DOCX） */
    @Excel(name = "正文文件路径", readConverterExp = "P=DF/DOCX")
    private String content;

    /** 合同编号 */
    @Excel(name = "合同编号")
    private String contractNumber;

    /** 签署状态 */
    @Excel(name = "签署状态")
    private String signStatus;

    /** 履约状态 */
    @Excel(name = "履约状态")
    private String performanceStatus;

    /** 金额类型 */
    @Excel(name = "金额类型")
    private String amountType;

    /** 合同总额 */
    @Excel(name = "合同总额")
    private BigDecimal totalAmount;

    /** 签署日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "签署日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date signDate;

    /** 期限类型 */
    @Excel(name = "期限类型")
    private String termType;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date startDate;

    /** 到期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "到期时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date endDate;

    /** 所在分类 */
    @Excel(name = "所在分类")
    private String category;

    /** 合同说明 */
    @Excel(name = "合同说明")
    private String description;

    /** 归档人 */
    @Excel(name = "归档人")
    private String archiver;

    /** 协同人 */
    @Excel(name = "协同人")
    private String cooperators;

    /** 用印部门 */
    @Excel(name = "用印部门")
    private String department;

    /** 经办人 */
    @Excel(name = "经办人")
    private String handler;

    /** 用印日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "用印日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date date;

    /** 用印文件名称 */
    @Excel(name = "用印文件名称")
    private String fileName;

    /** 对方单位名称 */
    @Excel(name = "对方单位名称")
    private String opponentUnit;

    /** 文件份数 */
    @Excel(name = "文件份数")
    private Long fileCount;

    /** 文件类别 */
    @Excel(name = "文件类别")
    private String fileCategory;

    /** 印章类别 */
    @Excel(name = "印章类别")
    private String sealCategory;

    /** 印章主体 */
    @Excel(name = "印章主体")
    private String sealSubject;

    /** 上传文件列表（JSON） */
    @Excel(name = "上传文件列表", readConverterExp = "J=SON")
    private String files;

    /** 签署方式 */
    @Excel(name = "签署方式")
    private String signMethod;

    /** 印章类型 */
    @Excel(name = "印章类型")
    private String sealType;

    /** 印章名称 */
    @Excel(name = "印章名称")
    private String sealName;

    /** 印章所属主体 */
    @Excel(name = "印章所属主体")
    private String sealOwner;

    /** 保证合同编号 */
    @Excel(name = "保证合同编号")
    private String guaranteeContractNo;

    /** 被担保单位名称 */
    @Excel(name = "被担保单位名称")
    private String guaranteedUnit;

    /** 日期区间 */
    @Excel(name = "日期区间")
    private String dateRange;

    /** 银行 */
    @Excel(name = "银行")
    private String bank;

    /** 保证金额（万元） */
    @Excel(name = "保证金额", readConverterExp = "万=元")
    private BigDecimal guaranteeAmount;

    /** 删除标记 */
    private Long delFlag;

    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }

    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }

    public void setOwner(String owner) 
    {
        this.owner = owner;
    }

    public String getOwner() 
    {
        return owner;
    }

    public void setContent(String content) 
    {
        this.content = content;
    }

    public String getContent() 
    {
        return content;
    }

    public void setContractNumber(String contractNumber) 
    {
        this.contractNumber = contractNumber;
    }

    public String getContractNumber() 
    {
        return contractNumber;
    }

    public void setSignStatus(String signStatus) 
    {
        this.signStatus = signStatus;
    }

    public String getSignStatus() 
    {
        return signStatus;
    }

    public void setPerformanceStatus(String performanceStatus) 
    {
        this.performanceStatus = performanceStatus;
    }

    public String getPerformanceStatus() 
    {
        return performanceStatus;
    }

    public void setAmountType(String amountType) 
    {
        this.amountType = amountType;
    }

    public String getAmountType() 
    {
        return amountType;
    }

    public void setTotalAmount(BigDecimal totalAmount) 
    {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getTotalAmount() 
    {
        return totalAmount;
    }

    public void setSignDate(Date signDate) 
    {
        this.signDate = signDate;
    }

    public Date getSignDate() 
    {
        return signDate;
    }

    public void setTermType(String termType) 
    {
        this.termType = termType;
    }

    public String getTermType() 
    {
        return termType;
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

    public void setCategory(String category) 
    {
        this.category = category;
    }

    public String getCategory() 
    {
        return category;
    }

    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }

    public void setArchiver(String archiver) 
    {
        this.archiver = archiver;
    }

    public String getArchiver() 
    {
        return archiver;
    }

    public void setCooperators(String cooperators) 
    {
        this.cooperators = cooperators;
    }

    public String getCooperators() 
    {
        return cooperators;
    }

    public void setDepartment(String department) 
    {
        this.department = department;
    }

    public String getDepartment() 
    {
        return department;
    }

    public void setHandler(String handler) 
    {
        this.handler = handler;
    }

    public String getHandler() 
    {
        return handler;
    }

    public void setDate(Date date) 
    {
        this.date = date;
    }

    public Date getDate() 
    {
        return date;
    }

    public void setFileName(String fileName) 
    {
        this.fileName = fileName;
    }

    public String getFileName() 
    {
        return fileName;
    }

    public void setOpponentUnit(String opponentUnit) 
    {
        this.opponentUnit = opponentUnit;
    }

    public String getOpponentUnit() 
    {
        return opponentUnit;
    }

    public void setFileCount(Long fileCount) 
    {
        this.fileCount = fileCount;
    }

    public Long getFileCount() 
    {
        return fileCount;
    }

    public void setFileCategory(String fileCategory) 
    {
        this.fileCategory = fileCategory;
    }

    public String getFileCategory() 
    {
        return fileCategory;
    }

    public void setSealCategory(String sealCategory) 
    {
        this.sealCategory = sealCategory;
    }

    public String getSealCategory() 
    {
        return sealCategory;
    }

    public void setSealSubject(String sealSubject) 
    {
        this.sealSubject = sealSubject;
    }

    public String getSealSubject() 
    {
        return sealSubject;
    }

    public void setFiles(String files) 
    {
        this.files = files;
    }

    public String getFiles() 
    {
        return files;
    }

    public void setSignMethod(String signMethod) 
    {
        this.signMethod = signMethod;
    }

    public String getSignMethod() 
    {
        return signMethod;
    }

    public void setSealType(String sealType) 
    {
        this.sealType = sealType;
    }

    public String getSealType() 
    {
        return sealType;
    }

    public void setSealName(String sealName) 
    {
        this.sealName = sealName;
    }

    public String getSealName() 
    {
        return sealName;
    }

    public void setSealOwner(String sealOwner) 
    {
        this.sealOwner = sealOwner;
    }

    public String getSealOwner() 
    {
        return sealOwner;
    }

    public void setGuaranteeContractNo(String guaranteeContractNo) 
    {
        this.guaranteeContractNo = guaranteeContractNo;
    }

    public String getGuaranteeContractNo() 
    {
        return guaranteeContractNo;
    }

    public void setGuaranteedUnit(String guaranteedUnit) 
    {
        this.guaranteedUnit = guaranteedUnit;
    }

    public String getGuaranteedUnit() 
    {
        return guaranteedUnit;
    }

    public void setDateRange(String dateRange) 
    {
        this.dateRange = dateRange;
    }

    public String getDateRange() 
    {
        return dateRange;
    }

    public void setBank(String bank) 
    {
        this.bank = bank;
    }

    public String getBank() 
    {
        return bank;
    }

    public void setGuaranteeAmount(BigDecimal guaranteeAmount) 
    {
        this.guaranteeAmount = guaranteeAmount;
    }

    public BigDecimal getGuaranteeAmount() 
    {
        return guaranteeAmount;
    }

    public void setDelFlag(Long delFlag) 
    {
        this.delFlag = delFlag;
    }

    public Long getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("name", getName())
            .append("owner", getOwner())
            .append("content", getContent())
            .append("contractNumber", getContractNumber())
            .append("signStatus", getSignStatus())
            .append("performanceStatus", getPerformanceStatus())
            .append("amountType", getAmountType())
            .append("totalAmount", getTotalAmount())
            .append("signDate", getSignDate())
            .append("termType", getTermType())
            .append("startDate", getStartDate())
            .append("endDate", getEndDate())
            .append("category", getCategory())
            .append("description", getDescription())
            .append("archiver", getArchiver())
            .append("cooperators", getCooperators())
            .append("department", getDepartment())
            .append("handler", getHandler())
            .append("date", getDate())
            .append("fileName", getFileName())
            .append("opponentUnit", getOpponentUnit())
            .append("fileCount", getFileCount())
            .append("fileCategory", getFileCategory())
            .append("sealCategory", getSealCategory())
            .append("sealSubject", getSealSubject())
            .append("remark", getRemark())
            .append("files", getFiles())
            .append("signMethod", getSignMethod())
            .append("sealType", getSealType())
            .append("sealName", getSealName())
            .append("sealOwner", getSealOwner())
            .append("guaranteeContractNo", getGuaranteeContractNo())
            .append("guaranteedUnit", getGuaranteedUnit())
            .append("dateRange", getDateRange())
            .append("bank", getBank())
            .append("guaranteeAmount", getGuaranteeAmount())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("delFlag", getDelFlag())
            .toString();
    }
}
