package com.ruoyi.invoice.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.invoice.mapper.ContractInvoiceMapper;
import com.ruoyi.invoice.domain.ContractInvoice;
import com.ruoyi.invoice.service.IContractInvoiceService;

/**
 * 发票信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class ContractInvoiceServiceImpl implements IContractInvoiceService 
{
    @Autowired
    private ContractInvoiceMapper contractInvoiceMapper;

    /**
     * 查询发票信息
     * 
     * @param id 发票信息主键
     * @return 发票信息
     */
    @Override
    public ContractInvoice selectContractInvoiceById(Long id)
    {
        return contractInvoiceMapper.selectContractInvoiceById(id);
    }

    /**
     * 查询发票信息列表
     * 
     * @param contractInvoice 发票信息
     * @return 发票信息
     */
    @Override
    public List<ContractInvoice> selectContractInvoiceList(ContractInvoice contractInvoice)
    {
        return contractInvoiceMapper.selectContractInvoiceList(contractInvoice);
    }

    /**
     * 新增发票信息
     * 
     * @param contractInvoice 发票信息
     * @return 结果
     */
    @Override
    public int insertContractInvoice(ContractInvoice contractInvoice)
    {
        contractInvoice.setCreateTime(DateUtils.getNowDate());
        return contractInvoiceMapper.insertContractInvoice(contractInvoice);
    }

    /**
     * 修改发票信息
     * 
     * @param contractInvoice 发票信息
     * @return 结果
     */
    @Override
    public int updateContractInvoice(ContractInvoice contractInvoice)
    {
        contractInvoice.setUpdateTime(DateUtils.getNowDate());
        return contractInvoiceMapper.updateContractInvoice(contractInvoice);
    }

    /**
     * 批量删除发票信息
     * 
     * @param ids 需要删除的发票信息主键
     * @return 结果
     */
    @Override
    public int deleteContractInvoiceByIds(Long[] ids)
    {
        return contractInvoiceMapper.deleteContractInvoiceByIds(ids);
    }

    /**
     * 删除发票信息信息
     * 
     * @param id 发票信息主键
     * @return 结果
     */
    @Override
    public int deleteContractInvoiceById(Long id)
    {
        return contractInvoiceMapper.deleteContractInvoiceById(id);
    }

    @Override
    public String importContractInvoice(List<ContractInvoice> invoiceList, String operName)
    {
        if (invoiceList == null || invoiceList.isEmpty())
        {
            return "导入失败：发票数据不能为空！";
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder failureMsg = new StringBuilder();
        for (ContractInvoice invoice : invoiceList)
        {
            try
            {
                normalizeInvoice(invoice);
                invoice.setCreateBy(operName);
                invoice.setCreateTime(DateUtils.getNowDate());
                invoice.setDelFlag("0");
                contractInvoiceMapper.insertContractInvoice(invoice);
                successNum++;
            }
            catch (Exception e)
            {
                failureNum++;
                failureMsg.append("<br/>第").append(successNum + failureNum).append("条导入失败：")
                        .append(e.getMessage());
            }
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入成功 " + successNum + " 条，失败 " + failureNum + " 条，错误如下：");
            return failureMsg.toString();
        }
        return "恭喜您，数据已全部导入成功！共 " + successNum + " 条。";
    }

    private void normalizeInvoice(ContractInvoice invoice)
    {
        if (invoice == null)
        {
            return;
        }
        if (StringUtils.isBlank(invoice.getInvoiceType()))
        {
            invoice.setInvoiceType("normal");
        }
        else if ("增值税专用发票".equals(invoice.getInvoiceType()))
        {
            invoice.setInvoiceType("vat");
        }
        else if ("普通发票".equals(invoice.getInvoiceType()))
        {
            invoice.setInvoiceType("normal");
        }

        if (StringUtils.isBlank(invoice.getInvoiceStatus()))
        {
            invoice.setInvoiceStatus("invoiced");
        }
        else if ("未开票".equals(invoice.getInvoiceStatus()))
        {
            invoice.setInvoiceStatus("no_invoice");
        }
        else if ("已开票".equals(invoice.getInvoiceStatus()))
        {
            invoice.setInvoiceStatus("invoiced");
        }
        else if ("已作废".equals(invoice.getInvoiceStatus()))
        {
            invoice.setInvoiceStatus("voided");
        }

        if (StringUtils.isBlank(invoice.getAmountType()))
        {
            invoice.setAmountType("收入");
        }
        else if ("进项".equals(invoice.getAmountType()))
        {
            invoice.setAmountType("支出");
        }
        else if ("销项".equals(invoice.getAmountType()))
        {
            invoice.setAmountType("收入");
        }
    }
}
