package com.ruoyi.invoice.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
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

    @PreAuthorize("@ss.hasPermi('invoice:invoice:list')")
    @GetMapping("/list")
    public TableDataInfo list(ContractInvoice contractInvoice)
    {
        startPage();
        List<ContractInvoice> list = contractInvoiceService.selectContractInvoiceList(contractInvoice);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:export')")
    @Log(title = "发票信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ContractInvoice contractInvoice)
    {
        List<ContractInvoice> list = contractInvoiceService.selectContractInvoiceList(contractInvoice);
        ExcelUtil<ContractInvoice> util = new ExcelUtil<ContractInvoice>(ContractInvoice.class);
        util.exportExcel(response, list, "发票信息数据");
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:import')")
    @Log(title = "发票信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(MultipartFile file, @RequestParam(value = "updateSupport", required = false) boolean updateSupport) throws Exception
    {
        if (file == null || file.isEmpty())
        {
            return error("请选择要导入的 Excel 文件");
        }
        String operName = getUsername();
        String message = contractInvoiceService.importContractInvoice(file, operName);
        return success(message);
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:import')")
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException
    {
        String fileName = URLEncoder.encode("发票导入模板.xlsx", StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + fileName);

        try (Workbook workbook = new XSSFWorkbook(); OutputStream outputStream = response.getOutputStream())
        {
            Sheet sheet = workbook.createSheet("发票导入模板");
            String[] headers = {"发票分类", "相对方名称", "发票抬头", "纳税人识别号", "销售方名称", "销售方税号", "发票代码", "发票号码", "开票日期", "发票金额", "税率", "税额", "不含税金额", "发票类型", "发票状态", "开票内容", "所属项目", "关联合同名称", "关联合同编号", "备注"};

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);

            Row tipRow = sheet.createRow(0);
            tipRow.createCell(0).setCellValue("填写说明：支持第1行说明、第2行表头；发票分类可填 进项/销项 或 支出/收入；日期格式支持 yyyy-MM-dd；金额/税额/税率填写纯数字。关联合同可不填。");

            Row headerRow = sheet.createRow(1);
            for (int i = 0; i < headers.length; i++)
            {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 18 * 256);
            }
            sheet.setColumnWidth(0, 14 * 256);
            sheet.setColumnWidth(1, 24 * 256);
            sheet.setColumnWidth(2, 24 * 256);
            sheet.setColumnWidth(15, 28 * 256);

            workbook.write(outputStream);
            outputStream.flush();
        }
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(contractInvoiceService.selectContractInvoiceById(id));
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:add')")
    @Log(title = "发票信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ContractInvoice contractInvoice)
    {
        return toAjax(contractInvoiceService.insertContractInvoice(contractInvoice));
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:edit')")
    @Log(title = "发票信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ContractInvoice contractInvoice)
    {
        return toAjax(contractInvoiceService.updateContractInvoice(contractInvoice));
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:edit')")
    @Log(title = "发票审批申请", businessType = BusinessType.UPDATE)
    @PostMapping("/submitApproval")
    public AjaxResult submitApproval(@RequestBody Map<String, Object> payload)
    {
        Long id = Long.valueOf(payload.get("id").toString());
        String approver = payload.get("approver") == null ? null : payload.get("approver").toString();
        String handler = payload.get("handler") == null ? null : payload.get("handler").toString();
        String cc = payload.get("cc") == null ? null : payload.get("cc").toString();
        String remark = payload.get("remark") == null ? null : payload.get("remark").toString();
        return toAjax(contractInvoiceService.submitApproval(id, approver, handler, cc, remark));
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:edit')")
    @Log(title = "发票审批", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public AjaxResult approve(@RequestBody Map<String, Object> payload)
    {
        Long id = Long.valueOf(payload.get("id").toString());
        String action = payload.get("action") == null ? null : payload.get("action").toString();
        String remark = payload.get("remark") == null ? null : payload.get("remark").toString();
        return toAjax(contractInvoiceService.handleApproval(id, action, remark));
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:query')")
    @GetMapping("/{id}/logs")
    public AjaxResult listLogs(@PathVariable("id") Long id)
    {
        return success(contractInvoiceService.selectOperateLogs(id));
    }

    @PreAuthorize("@ss.hasPermi('invoice:invoice:remove')")
    @Log(title = "发票信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(contractInvoiceService.deleteContractInvoiceByIds(ids));
    }
}
