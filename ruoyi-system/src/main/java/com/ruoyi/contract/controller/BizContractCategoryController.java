package com.ruoyi.contract.controller;

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
import com.ruoyi.contract.domain.BizContractCategory;
import com.ruoyi.contract.service.IBizContractCategoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 合同分类Controller
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/contract/category")
public class BizContractCategoryController extends BaseController
{
    @Autowired
    private IBizContractCategoryService bizContractCategoryService;

    /**
     * 查询合同分类列表
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizContractCategory bizContractCategory)
    {
        startPage();
        List<BizContractCategory> list = bizContractCategoryService.selectBizContractCategoryList(bizContractCategory);
        return getDataTable(list);
    }

    /**
     * 导出合同分类列表
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:export')")
    @Log(title = "合同分类", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizContractCategory bizContractCategory)
    {
        List<BizContractCategory> list = bizContractCategoryService.selectBizContractCategoryList(bizContractCategory);
        ExcelUtil<BizContractCategory> util = new ExcelUtil<BizContractCategory>(BizContractCategory.class);
        util.exportExcel(response, list, "合同分类数据");
    }

    /**
     * 获取合同分类详细信息
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:query')")
    @GetMapping(value = "/{categoryId}")
    public AjaxResult getInfo(@PathVariable("categoryId") Long categoryId)
    {
        return success(bizContractCategoryService.selectBizContractCategoryByCategoryId(categoryId));
    }

    /**
     * 新增合同分类
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:add')")
    @Log(title = "合同分类", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizContractCategory bizContractCategory)
    {
        return toAjax(bizContractCategoryService.insertBizContractCategory(bizContractCategory));
    }

    /**
     * 修改合同分类
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:edit')")
    @Log(title = "合同分类", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizContractCategory bizContractCategory)
    {
        return toAjax(bizContractCategoryService.updateBizContractCategory(bizContractCategory));
    }

    /**
     * 删除合同分类
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:remove')")
    @Log(title = "合同分类", businessType = BusinessType.DELETE)
	@DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds)
    {
        return toAjax(bizContractCategoryService.deleteBizContractCategoryByCategoryIds(categoryIds));
    }
}
