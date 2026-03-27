package com.ruoyi.invoice.service;

import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.contract.domain.BizContractOperateLog;
import com.ruoyi.invoice.domain.ContractInvoice;

/**
 * 发票信息Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IContractInvoiceService 
{
    public ContractInvoice selectContractInvoiceById(Long id);

    public List<ContractInvoice> selectContractInvoiceList(ContractInvoice contractInvoice);

    public int insertContractInvoice(ContractInvoice contractInvoice);

    public int updateContractInvoice(ContractInvoice contractInvoice);

    public int deleteContractInvoiceByIds(Long[] ids);

    public int deleteContractInvoiceById(Long id);

    public String importContractInvoice(List<ContractInvoice> invoiceList, String operName);

    public String importContractInvoice(MultipartFile file, String operName) throws IOException;

    public int submitApproval(Long id, String approver, String cc, String remark);

    public int handleApproval(Long id, String action, String remark);

    public List<BizContractOperateLog> selectOperateLogs(Long id);
}
