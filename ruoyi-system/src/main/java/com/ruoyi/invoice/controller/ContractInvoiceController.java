package com.ruoyi.invoice.controller;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.invoice.domain.ContractInvoice;
import com.ruoyi.invoice.service.IContractInvoiceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 发票信息Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/invoice/invoice")
public class ContractInvoiceController extends BaseController
{
    @Autowired
    private IContractInvoiceService contractInvoiceService;

    /**
     * 查询发票信息列表
     */
    @PreAuthorize("@ss.hasPermi('invoice:invoice:list')")
    @GetMapping("/list")
    public TableDataInfo list(ContractInvoice contractInvoice)
    {
        startPage();
        List<ContractInvoice> list = contractInvoiceService.selectContractInvoiceList(contractInvoice);
        return getDataTable(list);
    }

    /**
     * 导出发票信息列表
     */
    @PreAuthorize("@ss.hasPermi('invoice:invoice:export')")
    @Log(title = "发票信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ContractInvoice contractInvoice)
    {
        List<ContractInvoice> list = contractInvoiceService.selectContractInvoiceList(contractInvoice);
        ExcelUtil<ContractInvoice> util = new ExcelUtil<ContractInvoice>(ContractInvoice.class);
        util.exportExcel(response, list, "发票信息数据");
    }

    /**
     * 导入发票信息
     */
    @PreAuthorize("@ss.hasPermi('invoice:invoice:import')")
    @Log(title = "发票信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, @RequestParam(value = "updateSupport", required = false) boolean updateSupport) throws Exception
    {
        ExcelUtil<ContractInvoice> util = new ExcelUtil<ContractInvoice>(ContractInvoice.class);
        List<ContractInvoice> invoiceList = util.importExcel(file.getInputStream());
        String operName = getUsername();
        String message = contractInvoiceService.importContractInvoice(invoiceList, operName);
        return success(message);
    }

    /**
     * 下载导入模板
     */
    @PreAuthorize("@ss.hasPermi('invoice:invoice:import')")
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException
    {
        ClassPathResource resource = new ClassPathResource("templates/发票批量导入2024-2025(1).xlsx");
        String fileName = URLEncoder.encode("发票批量导入2024-2025(1).xlsx", StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);
        response.setContentLengthLong(resource.contentLength());
        try (InputStream inputStream = new BufferedInputStream(resource.getInputStream()))
        {
            FileCopyUtils.copy(inputStream, response.getOutputStream());
            response.flushBuffer();
        }
    }

    /**
     * 获取发票信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('invoice:invoice:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(contractInvoiceService.selectContractInvoiceById(id));
    }

    /**
     * 新增发票信息
     */
    @PreAuthorize("@ss.hasPermi('invoice:invoice:add')")
    @Log(title = "发票信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ContractInvoice contractInvoice)
    {
        return toAjax(contractInvoiceService.insertContractInvoice(contractInvoice));
    }

    /**
     * 修改发票信息
     */
    @PreAuthorize("@ss.hasPermi('invoice:invoice:edit')")
    @Log(title = "发票信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ContractInvoice contractInvoice)
    {
        return toAjax(contractInvoiceService.updateContractInvoice(contractInvoice));
    }

    /**
     * 删除发票信息
     */
    @PreAuthorize("@ss.hasPermi('invoice:invoice:remove')")
    @Log(title = "发票信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(contractInvoiceService.deleteContractInvoiceByIds(ids));
    }
}
