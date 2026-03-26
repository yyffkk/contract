package com.ruoyi.account.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.account.domain.ContractAccount;
import com.ruoyi.account.service.IContractAccountService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;

/**
 * 账款信息Controller
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@RestController
@RequestMapping("/account/account")
public class ContractAccountController extends BaseController
{
    @Autowired
    private IContractAccountService contractAccountService;

    /**
     * 查询账款信息列表
     */
    @PreAuthorize("@ss.hasPermi('account:account:list')")
    @GetMapping("/list")
    public TableDataInfo list(ContractAccount contractAccount)
    {
        startPage();
        List<ContractAccount> list = contractAccountService.selectContractAccountList(contractAccount);
        return getDataTable(list);
    }

    /**
     * 导出账款信息列表
     */
    @PreAuthorize("@ss.hasPermi('account:account:export')")
    @Log(title = "账款信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ContractAccount contractAccount)
    {
        List<ContractAccount> list = contractAccountService.selectContractAccountList(contractAccount);
        ExcelUtil<ContractAccount> util = new ExcelUtil<ContractAccount>(ContractAccount.class);
        util.exportExcel(response, list, "账款信息数据");
    }

    /**
     * 下载导入模板（保持原 Excel 格式与红字说明）
     */
    @PreAuthorize("@ss.hasPermi('account:account:export')")
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws IOException
    {
        ClassPathResource resource = new ClassPathResource("templates/account/account_import_template.xlsx");
        if (!resource.exists())
        {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().write("{\"code\":500,\"msg\":\"模板文件不存在，请联系管理员\"}");
            return;
        }

        String fileName = URLEncoder.encode("账款导入模板", StandardCharsets.UTF_8.name()).replaceAll("\\+", "%20");
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        try (InputStream in = resource.getInputStream())
        {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = in.read(buffer)) != -1)
            {
                response.getOutputStream().write(buffer, 0, len);
            }
            response.getOutputStream().flush();
        }
    }

    /**
     * 导入账款信息
     */
    @PreAuthorize("@ss.hasPermi('account:account:add')")
    @Log(title = "账款信息", businessType = BusinessType.IMPORT)
    @PostMapping("/importData")
    public AjaxResult importData(@RequestParam("file") MultipartFile file)
    {
        int count = contractAccountService.importData(file);
        return AjaxResult.success("导入成功", count);
    }

    /**
     * 获取账款信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('account:account:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(contractAccountService.selectContractAccountById(id));
    }

    /**
     * 新增账款信息
     */
    @PreAuthorize("@ss.hasPermi('account:account:add')")
    @Log(title = "账款信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ContractAccount contractAccount)
    {
        return toAjax(contractAccountService.insertContractAccount(contractAccount));
    }

    /**
     * 修改账款信息
     */
    @PreAuthorize("@ss.hasPermi('account:account:edit')")
    @Log(title = "账款信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ContractAccount contractAccount)
    {
        return toAjax(contractAccountService.updateContractAccount(contractAccount));
    }

    /**
     * 提交收付款审批
     */
    @PreAuthorize("@ss.hasPermi('account:account:edit')")
    @Log(title = "账款审批申请", businessType = BusinessType.UPDATE)
    @PostMapping("/submitApproval")
    public AjaxResult submitApproval(@RequestBody Map<String, Object> payload)
    {
        Long id = Long.valueOf(payload.get("id").toString());
        String applyType = payload.get("applyType") == null ? null : payload.get("applyType").toString();
        String remark = payload.get("remark") == null ? null : payload.get("remark").toString();
        return toAjax(contractAccountService.submitApproval(id, applyType, remark));
    }

    /**
     * 执行账款审批
     */
    @PreAuthorize("@ss.hasPermi('account:account:edit')")
    @Log(title = "账款审批", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public AjaxResult approve(@RequestBody Map<String, Object> payload)
    {
        Long id = Long.valueOf(payload.get("id").toString());
        String action = payload.get("action") == null ? null : payload.get("action").toString();
        String remark = payload.get("remark") == null ? null : payload.get("remark").toString();
        return toAjax(contractAccountService.handleApproval(id, action, remark));
    }

    /**
     * 删除账款信息
     */
    @PreAuthorize("@ss.hasPermi('account:account:remove')")
    @Log(title = "账款信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(contractAccountService.deleteContractAccountByIds(ids));
    }
}
