package com.ruoyi.invoice.mapper;

import java.util.List;
import com.ruoyi.invoice.domain.ContractInvoice;

/**
 * 发票信息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface ContractInvoiceMapper 
{
    public ContractInvoice selectContractInvoiceById(Long id);

    public List<ContractInvoice> selectContractInvoiceList(ContractInvoice contractInvoice);

    public int insertContractInvoice(ContractInvoice contractInvoice);

    public int updateContractInvoice(ContractInvoice contractInvoice);

    public int deleteContractInvoiceById(Long id);

    public int deleteContractInvoiceByIds(Long[] ids);
}
