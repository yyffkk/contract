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
import com.ruoyi.pm.domain.PmProjectPayment;
import com.ruoyi.pm.service.IPmProjectPaymentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目账款关联Controller
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/pm/payment")
public class PmProjectPaymentController extends BaseController
{
    @Autowired
    private IPmProjectPaymentService pmProjectPaymentService;

    /**
     * 查询项目账款关联列表
     */
    @PreAuthorize("@ss.hasPermi('pm:payment:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmProjectPayment pmProjectPayment)
    {
        startPage();
        List<PmProjectPayment> list = pmProjectPaymentService.selectPmProjectPaymentList(pmProjectPayment);
        return getDataTable(list);
    }

    /**
     * 导出项目账款关联列表
     */
    @PreAuthorize("@ss.hasPermi('pm:payment:export')")
    @Log(title = "项目账款关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PmProjectPayment pmProjectPayment)
    {
        List<PmProjectPayment> list = pmProjectPaymentService.selectPmProjectPaymentList(pmProjectPayment);
        ExcelUtil<PmProjectPayment> util = new ExcelUtil<PmProjectPayment>(PmProjectPayment.class);
        util.exportExcel(response, list, "项目账款关联数据");
    }

    /**
     * 获取项目账款关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('pm:payment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(pmProjectPaymentService.selectPmProjectPaymentById(id));
    }

    /**
     * 新增项目账款关联
     */
    @PreAuthorize("@ss.hasPermi('pm:payment:add')")
    @Log(title = "项目账款关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmProjectPayment pmProjectPayment)
    {
        return toAjax(pmProjectPaymentService.insertPmProjectPayment(pmProjectPayment));
    }

    /**
     * 修改项目账款关联
     */
    @PreAuthorize("@ss.hasPermi('pm:payment:edit')")
    @Log(title = "项目账款关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmProjectPayment pmProjectPayment)
    {
        return toAjax(pmProjectPaymentService.updatePmProjectPayment(pmProjectPayment));
    }

    /**
     * 删除项目账款关联
     */
    @PreAuthorize("@ss.hasPermi('pm:payment:remove')")
    @Log(title = "项目账款关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(pmProjectPaymentService.deletePmProjectPaymentByIds(ids));
    }
}
