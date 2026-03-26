package com.ruoyi.invoice.service;

import java.util.List;
import com.ruoyi.invoice.domain.ContractInvoice;

/**
 * 发票信息Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IContractInvoiceService 
{
    /**
     * 查询发票信息
     * 
     * @param id 发票信息主键
     * @return 发票信息
     */
    public ContractInvoice selectContractInvoiceById(Long id);

    /**
     * 查询发票信息列表
     * 
     * @param contractInvoice 发票信息
     * @return 发票信息集合
     */
    public List<ContractInvoice> selectContractInvoiceList(ContractInvoice contractInvoice);

    /**
     * 新增发票信息
     * 
     * @param contractInvoice 发票信息
     * @return 结果
     */
    public int insertContractInvoice(ContractInvoice contractInvoice);

    /**
     * 修改发票信息
     * 
     * @param contractInvoice 发票信息
     * @return 结果
     */
    public int updateContractInvoice(ContractInvoice contractInvoice);

    /**
     * 批量删除发票信息
     * 
     * @param ids 需要删除的发票信息主键集合
     * @return 结果
     */
    public int deleteContractInvoiceByIds(Long[] ids);

    /**
     * 删除发票信息信息
     * 
     * @param id 发票信息主键
     * @return 结果
     */
    public int deleteContractInvoiceById(Long id);
}
