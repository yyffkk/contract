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
import com.ruoyi.pm.domain.PmProject;
import com.ruoyi.pm.service.IPmProjectService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 项目信息Controller
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@RestController
@RequestMapping("/pm/project")
public class PmProjectController extends BaseController
{
    @Autowired
    private IPmProjectService pmProjectService;

    /**
     * 查询项目信息列表
     */
    @PreAuthorize("@ss.hasPermi('pm:project:list')")
    @GetMapping("/list")
    public TableDataInfo list(PmProject pmProject)
    {
        startPage();
        List<PmProject> list = pmProjectService.selectPmProjectList(pmProject);
        return getDataTable(list);
    }

    /**
     * 导出项目信息列表
     */
    @PreAuthorize("@ss.hasPermi('pm:project:export')")
    @Log(title = "项目信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, PmProject pmProject)
    {
        List<PmProject> list = pmProjectService.selectPmProjectList(pmProject);
        ExcelUtil<PmProject> util = new ExcelUtil<PmProject>(PmProject.class);
        util.exportExcel(response, list, "项目信息数据");
    }

    /**
     * 获取项目信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('pm:project:query')")
    @GetMapping(value = "/{projectId}")
    public AjaxResult getInfo(@PathVariable("projectId") Long projectId)
    {
        return success(pmProjectService.selectPmProjectByProjectId(projectId));
    }

    /**
     * 新增项目信息
     */
    @PreAuthorize("@ss.hasPermi('pm:project:add')")
    @Log(title = "项目信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody PmProject pmProject)
    {
        return toAjax(pmProjectService.insertPmProject(pmProject));
    }

    /**
     * 修改项目信息
     */
    @PreAuthorize("@ss.hasPermi('pm:project:edit')")
    @Log(title = "项目信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody PmProject pmProject)
    {
        return toAjax(pmProjectService.updatePmProject(pmProject));
    }

    /**
     * 删除项目信息
     */
    @PreAuthorize("@ss.hasPermi('pm:project:remove')")
    @Log(title = "项目信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{projectIds}")
    public AjaxResult remove(@PathVariable Long[] projectIds)
    {
        return toAjax(pmProjectService.deletePmProjectByProjectIds(projectIds));
    }
}
