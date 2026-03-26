package com.ruoyi.invoice.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
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
}
