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
import com.ruoyi.pm.domain.PmProjectContract;
import com.ruoyi.pm.service.IPmProjectContractService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目合同关联Controller
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/pm/contract")
public class PmProjectContractController extends BaseController
{
    @Autowired
    private IPmProjectContractService pmProjectContractService;

    /**
     * 查询项目合同关联列表
     */
    @PreAuthorize("@ss.hasPermi('pm:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmProjectContract pmProjectContract)
    {
        startPage();
        List<PmProjectContract> list = pmProjectContractService.selectPmProjectContractList(pmProjectContract);
        return getDataTable(list);
    }

    /**
     * 导出项目合同关联列表
     */
    @PreAuthorize("@ss.hasPermi('pm:contract:export')")
    @Log(title = "项目合同关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PmProjectContract pmProjectContract)
    {
        List<PmProjectContract> list = pmProjectContractService.selectPmProjectContractList(pmProjectContract);
        ExcelUtil<PmProjectContract> util = new ExcelUtil<PmProjectContract>(PmProjectContract.class);
        util.exportExcel(response, list, "项目合同关联数据");
    }

    /**
     * 获取项目合同关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('pm:contract:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(pmProjectContractService.selectPmProjectContractById(id));
    }

    /**
     * 新增项目合同关联
     */
    @PreAuthorize("@ss.hasPermi('pm:contract:add')")
    @Log(title = "项目合同关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmProjectContract pmProjectContract)
    {
        return toAjax(pmProjectContractService.insertPmProjectContract(pmProjectContract));
    }

    /**
     * 修改项目合同关联
     */
    @PreAuthorize("@ss.hasPermi('pm:contract:edit')")
    @Log(title = "项目合同关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmProjectContract pmProjectContract)
    {
        return toAjax(pmProjectContractService.updatePmProjectContract(pmProjectContract));
    }

    /**
     * 删除项目合同关联
     */
    @PreAuthorize("@ss.hasPermi('pm:contract:remove')")
    @Log(title = "项目合同关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(pmProjectContractService.deletePmProjectContractByIds(ids));
    }
}
