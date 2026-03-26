package com.ruoyi.pm.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pm.domain.PmProjectInvoice;
import com.ruoyi.pm.service.IPmProjectInvoiceService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目发票关联Controller
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/pm/invoice")
public class PmProjectInvoiceController extends BaseController
{
    @Autowired
    private IPmProjectInvoiceService pmProjectInvoiceService;

    /**
     * 查询项目发票关联列表
     */
    @PreAuthorize("@ss.hasPermi('pm:invoice:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmProjectInvoice pmProjectInvoice)
    {
        startPage();
        List<PmProjectInvoice> list = pmProjectInvoiceService.selectPmProjectInvoiceList(pmProjectInvoice);
        return getDataTable(list);
    }

    /**
     * 导出项目发票关联列表
     */
    @PreAuthorize("@ss.hasPermi('pm:invoice:export')")
    @Log(title = "项目发票关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PmProjectInvoice pmProjectInvoice)
    {
        List<PmProjectInvoice> list = pmProjectInvoiceService.selectPmProjectInvoiceList(pmProjectInvoice);
        ExcelUtil<PmProjectInvoice> util = new ExcelUtil<PmProjectInvoice>(PmProjectInvoice.class);
        util.exportExcel(response, list, "项目发票关联数据");
    }

    /**
     * 获取项目发票关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('pm:invoice:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(pmProjectInvoiceService.selectPmProjectInvoiceById(id));
    }

    /**
     * 新增项目发票关联
     */
    @PreAuthorize("@ss.hasPermi('pm:invoice:add')")
    @Log(title = "项目发票关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmProjectInvoice pmProjectInvoice)
    {
        return toAjax(pmProjectInvoiceService.insertPmProjectInvoice(pmProjectInvoice));
    }

    /**
     * 修改项目发票关联
     */
    @PreAuthorize("@ss.hasPermi('pm:invoice:edit')")
    @Log(title = "项目发票关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmProjectInvoice pmProjectInvoice)
    {
        return toAjax(pmProjectInvoiceService.updatePmProjectInvoice(pmProjectInvoice));
    }

    /**
     * 删除项目发票关联
     */
    @PreAuthorize("@ss.hasPermi('pm:invoice:remove')")
    @Log(title = "项目发票关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(pmProjectInvoiceService.deletePmProjectInvoiceByIds(ids));
    }
}
