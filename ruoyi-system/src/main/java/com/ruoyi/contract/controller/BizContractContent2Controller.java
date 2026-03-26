package com.ruoyi.contract.controller;

import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.contract.domain.BizContractContent2;
import com.ruoyi.contract.service.IBizContractContent2Service;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用印审批单Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/contract/contractContent2")
public class BizContractContent2Controller extends BaseController
{
    @Autowired
    private IBizContractContent2Service bizContractContent2Service;
    // ✅ 安全注入 ruoyi.profile 的值
    @Value("${ruoyi.profile}")
    private String uploadProfilePath;  // 值就是 "D:/ruoyi/uploadPath"
    /**
     * 查询用印审批单列表
     */
    @PreAuthorize("@ss.hasPermi('contract:contractContent2:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizContractContent2 bizContractContent2)
    {
        startPage();
        List<BizContractContent2> list = bizContractContent2Service.selectBizContractContent2List(bizContractContent2);
        return getDataTable(list);
    }

    /**
     * 导出用印审批单列表
     */
    @PreAuthorize("@ss.hasPermi('contract:contractContent2:export')")
    @Log(title = "用印审批单", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizContractContent2 bizContractContent2)
    {
        List<BizContractContent2> list = bizContractContent2Service.selectBizContractContent2List(bizContractContent2);
        ExcelUtil<BizContractContent2> util = new ExcelUtil<BizContractContent2>(BizContractContent2.class);
        util.exportExcel(response, list, "用印审批单数据");
    }

    /**
     * 获取用印审批单详细信息
     */
    @PreAuthorize("@ss.hasPermi('contract:contractContent2:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(bizContractContent2Service.selectBizContractContent2ById(id));
    }

    /**
     * 新增用印审批单
     */
    // 替换原来的 @PostMapping 方法
    @PreAuthorize("@ss.hasPermi('contract:contractContent2:add')")
    @Log(title = "用印审批单", businessType = BusinessType.INSERT)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE) // 👈 必须加
    public AjaxResult add(
            // 文件字段
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments,

            // 表单字段（全部用 @RequestParam）
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String handler,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) String opponentUnit,
            @RequestParam(required = false) Long fileCount,
            @RequestParam(required = false) String fileCategory,
            @RequestParam(required = false) String sealCategory,
            @RequestParam(required = false) String sealSubject,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String signMethod,
            @RequestParam(required = false) String sealType,
            @RequestParam(required = false) String sealName,
            @RequestParam(required = false) String sealOwner
    ) {
        BizContractContent2 entity = new BizContractContent2();
        // 设置字段（注意日期转换）
        entity.setDepartment(department);
        entity.setHandler(handler);
        entity.setDate(StringUtils.isNotBlank(date) ? DateUtils.parseDate(date) : null);
        entity.setFileName(fileName);
        entity.setOpponentUnit(opponentUnit);
        entity.setFileCount(fileCount);
        entity.setFileCategory(fileCategory);
        entity.setSealCategory(sealCategory);
        entity.setSealSubject(sealSubject);
        entity.setRemark(remark);
        entity.setSignMethod(signMethod);
        entity.setSealType(sealType);
        entity.setSealName(sealName);
        entity.setSealOwner(sealOwner);

        // 构造合同上传目录：D:/ruoyi/uploadPath/contract/
        String contractDir = uploadProfilePath + "/contract/";
        // 2. 上传主合同文件
        if (file != null && !file.isEmpty()) {
            try {
                // 若依自带的 FileUploadUtils 支持传入完整路径
                String filePath = FileUploadUtils.upload(contractDir, file);
                entity.setContent(filePath); // 存的是相对路径，如 /contract/xxx.pdf
            } catch (Exception e) {
                return error("文件上传失败");
            }
        }

        return toAjax(bizContractContent2Service.insertBizContractContent2(entity));
    }

    /**
     * 修改用印审批单
     */
    @PreAuthorize("@ss.hasPermi('contract:contractContent2:edit')")
    @Log(title = "用印审批单", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizContractContent2 bizContractContent2)
    {
        return toAjax(bizContractContent2Service.updateBizContractContent2(bizContractContent2));
    }

    /**
     * 删除用印审批单
     */
    @PreAuthorize("@ss.hasPermi('contract:contractContent2:remove')")
    @Log(title = "用印审批单", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizContractContent2Service.deleteBizContractContent2ByIds(ids));
    }
}
