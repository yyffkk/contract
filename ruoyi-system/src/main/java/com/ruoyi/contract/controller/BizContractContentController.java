package com.ruoyi.contract.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.sun.deploy.net.URLEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.contract.domain.BizContractContent;
import com.ruoyi.contract.domain.BizContractOperateLog;
import com.ruoyi.contract.service.IBizContractContentService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 智能合同审批Controller
 *
 * @author ruoyi
 * @date 2026-03-14
 */
@RestController
@RequestMapping("/contract/contractContent")
public class BizContractContentController extends BaseController
{
    @Autowired
    private IBizContractContentService bizContractContentService;
    // ✅ 安全注入 ruoyi.profile 的值
    @Value("${ruoyi.profile}")
    private String uploadProfilePath;  // 值就是 "D:/ruoyi/uploadPath"

    /**
     * 查询智能合同审批列表
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizContractContent bizContractContent)
    {
        startPage();
        List<BizContractContent> list = bizContractContentService.selectBizContractContentList(bizContractContent);
        return getDataTable(list);
    }

    /**
     * 导出智能合同审批列表
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:export')")
    @Log(title = "智能合同审批", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizContractContent bizContractContent)
    {
        List<BizContractContent> list = bizContractContentService.selectBizContractContentList(bizContractContent);
        ExcelUtil<BizContractContent> util = new ExcelUtil<BizContractContent>(BizContractContent.class);
        util.exportExcel(response, list, "智能合同审批数据");
    }

    /**
     * 获取智能合同审批详细信息
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        BizContractContent content = bizContractContentService.selectBizContractContentById(id);
        if (content != null) {
            content.setParams(new java.util.HashMap<>());
            content.getParams().put("operateLogs", bizContractContentService.selectOperateLogs(id));
        }
        return success(content);
    }

    /**
     * 新增智能合同审批（支持文件上传）
     */
//    @PreAuthorize("@ss.hasPermi('contract:contract:add')")
    @Log(title = "智能合同审批", businessType = BusinessType.INSERT)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult add(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "attachments", required = false) MultipartFile[] attachments,
            // 其他字段逐个接收（不能用 @RequestBody）
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String contractName,
            @RequestParam(required = false) String numberMode,
            @RequestParam(required = false) String contractNumber,
            @RequestParam(required = false) String amountType,
            @RequestParam(required = false) BigDecimal totalAmount,
            @RequestParam(required = false) String termType,
            @RequestParam(required = false) String archiver,
            @RequestParam(required = false) String cooperators,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String myPartyName,
            @RequestParam(required = false) String myContact,
            @RequestParam(required = false) String otherPartyName,
            @RequestParam(required = false) String otherContact,
            @RequestParam(required = false) String signMethod,
            @RequestParam(required = false) String signDate, // 注意：日期字符串需转换
            @RequestParam(required = false) String sealType,
            @RequestParam(required = false) String approver,
            @RequestParam(required = false) String cc
    ) {
        // 1. 构造实体对象
        BizContractContent entity = new BizContractContent();
        entity.setCategory(category);
        entity.setContractName(contractName);
        entity.setNumberMode(numberMode);
        entity.setContractNumber(contractNumber);
        entity.setAmountType(amountType);
        entity.setTotalAmount(totalAmount);
        entity.setTermType(termType);
        entity.setArchiver(archiver);
        entity.setCooperators(cooperators);
        entity.setDescription(description);
        entity.setMyPartyName(myPartyName);
        entity.setMyContact(myContact);
        entity.setOtherPartyName(otherPartyName);
        entity.setOtherContact(otherContact);
        entity.setSignMethod(signMethod);
        // 处理日期
        if (StringUtils.isNotBlank(signDate)) {
            entity.setSignDate(DateUtils.parseDate(signDate));
        }
        entity.setSealType(sealType);
        entity.setApprover(approver);
        entity.setCc(cc);
        // 构造合同上传目录：D:/ruoyi/uploadPath/contract/
        String contractDir = uploadProfilePath + "/contract/";
        // 2. 上传主合同文件
        if (file != null && !file.isEmpty()) {
            try {
                // 若依自带的 FileUploadUtils 支持传入完整路径
                String filePath = FileUploadUtils.upload(contractDir, file);
                entity.setContent(filePath); // 存的是相对路径，如 /contract/xxx.pdf
                entity.setFile(filePath);    // 兼容旧页面/旧字段读取
            } catch (Exception e) {
                return error("文件上传失败");
            }
        }

        // 3. 上传附属文件（多个）
        if (attachments != null && attachments.length > 0) {
            List<String> attachmentPaths = new ArrayList<>();
            for (MultipartFile attach : attachments) {
                if (!attach.isEmpty()) {
                    try {
                        // 使用若依自带的 FileUploadUtils 上传
                        String path = FileUploadUtils.upload(contractDir, attach);
                        attachmentPaths.add(path); // path 是相对路径，如 /contract/xxx.pdf
                    } catch (Exception e) {
                        return error("文件上传失败");
                    }
                }
            }

            // 存为 JSON 字符串，例如：["/contract/a.pdf","/contract/b.zip"]
            if (!attachmentPaths.isEmpty()) {
                entity.setAttachments(JSON.toJSONString(attachmentPaths));
            }
        }

        // 4. 调用 Service 保存
        return toAjax(bizContractContentService.insertBizContractContent(entity));
    }

    /**
     * 修改智能合同审批
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:edit')")
    @Log(title = "智能合同审批", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizContractContent bizContractContent)
    {
        return toAjax(bizContractContentService.updateBizContractContent(bizContractContent));
    }

    /**
     * 删除智能合同审批
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:remove')")
    @Log(title = "智能合同审批", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(bizContractContentService.deleteBizContractContentByIds(ids));
    }

    /**
     * 导入数据
     * @param file
     * @return
     */
    @PostMapping("/import")
    public AjaxResult importContracts(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("请选择文件");
        }
        try {
            // 调用 service 解析 Excel 并批量保存
            bizContractContentService.importFromExcel(file);
            return AjaxResult.success();
        } catch (Exception e) {
            return AjaxResult.error("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载合同导入模板
     */
    @Log(title = "智能合同审批", businessType = BusinessType.OTHER)
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 模板文件名
        String templateFileName = "contract_template.xlsx";
        // 完整路径：uploadProfilePath + "/contract_template.xlsx"
        String templatePath = uploadProfilePath + "/" + templateFileName;

        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().write(JSON.toJSONString(AjaxResult.error("模板文件不存在，请联系管理员")));
            return;
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode("合同批量导入模板.xlsx", "UTF-8"));

        // 写入文件流
        try (FileInputStream fis = new FileInputStream(templateFile);
             OutputStream os = response.getOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                os.write(buffer, 0, len);
            }
            os.flush();
        } catch (Exception e) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().write(JSON.toJSONString(AjaxResult.error("下载失败：" + e.getMessage())));
        }
    }

    @PostMapping("/approve/simple")
    @ResponseBody
    public AjaxResult approveSimple(@RequestBody Map<String, Object> payload) {
        // 安全转换：支持 Integer 和 Long 两种类型
        Object contractIdObj = payload.get("contractId");
        if (contractIdObj == null) {
            return AjaxResult.error("合同 ID 不能为空");
        }

        Long contractId;
        if (contractIdObj instanceof Integer) {
            contractId = ((Integer) contractIdObj).longValue();
        } else if (contractIdObj instanceof Long) {
            contractId = (Long) contractIdObj;
        } else {
            return AjaxResult.error("合同 ID 格式错误");
        }

        String action = (String) payload.get("action");

        if (!"agree".equals(action) && !"reject".equals(action)) {
            return AjaxResult.error("操作类型必须为 agree 或 reject");
        }

        // 调用 service 执行审批
        bizContractContentService.handleApproval(contractId, action);

        return AjaxResult.success("操作成功");
    }

    @PostMapping("/sign")
    @PreAuthorize("@ss.hasPermi('contract:contract:edit')")
    public AjaxResult sign(@RequestBody Map<String, Object> payload) {
        Long contractId = Long.valueOf(payload.get("contractId").toString());
        Boolean autoArchive = (Boolean) payload.get("autoArchive");
        bizContractContentService.signContract(contractId, autoArchive);
        return success();
    }

    @GetMapping("/{id}/logs")
    public AjaxResult listLogs(@PathVariable("id") Long id) {
        return success(bizContractContentService.selectOperateLogs(id));
    }

    @PostMapping("/log")
    public AjaxResult addLog(@RequestBody BizContractOperateLog log) {
        bizContractContentService.addOperateLog(log);
        return success();
    }

    @PostMapping("/finish")
    public AjaxResult finish(@RequestBody Map<String, Object> payload) {
        Long contractId = Long.valueOf(payload.get("contractId").toString());
        String result = payload.get("result") == null ? null : payload.get("result").toString();
        String finalStatus = payload.get("finalStatus") == null ? "8" : payload.get("finalStatus").toString();
        bizContractContentService.finishContract(contractId, result, finalStatus);
        return success();
    }

    @PreAuthorize("@ss.hasPermi('contract:contract:edit')")
    @Log(title = "智能合同审批文件上传", businessType = BusinessType.UPDATE)
    @PostMapping(value = "/detail-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AjaxResult uploadDetailFiles(
            @RequestParam("contractId") Long contractId,
            @RequestParam(value = "contentFiles", required = false) MultipartFile[] contentFiles,
            @RequestParam(value = "attachmentFiles", required = false) MultipartFile[] attachmentFiles
    ) {
        BizContractContent entity = bizContractContentService.selectBizContractContentById(contractId);
        if (entity == null) {
            return error("合同不存在");
        }

        String contractDir = uploadProfilePath + "/contract/";

        try {
            if (contentFiles != null && contentFiles.length > 0) {
                List<String> contentPaths = new ArrayList<>();
                if (StringUtils.isNotBlank(entity.getContent())) {
                    try {
                        contentPaths.addAll(JSON.parseArray(entity.getContent(), String.class));
                    } catch (Exception ex) {
                        contentPaths.addAll(Arrays.asList(entity.getContent().split(",")));
                    }
                } else if (StringUtils.isNotBlank(entity.getFile())) {
                    contentPaths.add(entity.getFile());
                }
                for (MultipartFile file : contentFiles) {
                    if (file != null && !file.isEmpty()) {
                        contentPaths.add(FileUploadUtils.upload(contractDir, file));
                    }
                }
                contentPaths.removeIf(StringUtils::isBlank);
                if (!contentPaths.isEmpty()) {
                    entity.setContent(contentPaths.size() == 1 ? contentPaths.get(0) : JSON.toJSONString(contentPaths));
                    entity.setFile(contentPaths.get(0));
                }
            }

            if (attachmentFiles != null && attachmentFiles.length > 0) {
                List<String> attachmentPaths = new ArrayList<>();
                if (StringUtils.isNotBlank(entity.getAttachments())) {
                    try {
                        attachmentPaths.addAll(JSON.parseArray(entity.getAttachments(), String.class));
                    } catch (Exception ex) {
                        attachmentPaths.addAll(Arrays.asList(entity.getAttachments().split(",")));
                    }
                }
                for (MultipartFile file : attachmentFiles) {
                    if (file != null && !file.isEmpty()) {
                        attachmentPaths.add(FileUploadUtils.upload(contractDir, file));
                    }
                }
                entity.setAttachments(JSON.toJSONString(attachmentPaths));
            }

            bizContractContentService.updateBizContractContent(entity);

            BizContractOperateLog log = new BizContractOperateLog();
            log.setContractId(contractId);
            log.setContractType("approval");
            log.setAction("上传合同文件");
            log.setDetail("在详情页上传了合同正文/附件");
            bizContractContentService.addOperateLog(log);
            return success(entity);
        } catch (Exception e) {
            return error("上传失败：" + e.getMessage());
        }
    }

    /**
     * 合同归档
     */
    @PreAuthorize("@ss.hasPermi('contract:contract:edit')")
    @Log(title = "智能合同审批", businessType = BusinessType.UPDATE)
    @PostMapping("/archive")
    public AjaxResult archive(
            @RequestParam("contractId") Long contractId,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(required = false) String archiveNumber,
            @RequestParam(required = false) String remark,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String periodType // 无固定期限 / 固定期限
    ) {
        try {
            BizContractContent entity = bizContractContentService.selectBizContractContentById(contractId);
            if (entity == null) {
                return error("合同不存在");
            }

            // 校验状态：只能归档“待归档”状态的合同
            if (!"3".equals(entity.getSignStatus())) {
                return error("当前合同状态不可归档");
            }

            // 构造上传路径
            String uploadDir = uploadProfilePath + "/archive/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String filePath = null;
            if (file != null && !file.isEmpty()) {
                try {
                    filePath = FileUploadUtils.upload(uploadDir, file);
                } catch (Exception e) {
                    return error("文件上传失败：" + e.getMessage());
                }
            }

            // 更新合同信息
            entity.setArchiveFile(filePath); // 存相对路径，如 /archive/xxx.pdf
            entity.setArchiveId(archiveNumber);
            entity.setRemark(remark);
            entity.setCategory(category);
            entity.setTermType(periodType);
            entity.setArchiveTime(new Date()); // 归档时间
            entity.setSignStatus("5"); // 已归档

            int result = bizContractContentService.updateBizContractContent(entity);
            if (result > 0) {
                BizContractOperateLog log = new BizContractOperateLog();
                log.setContractId(contractId);
                log.setContractType("approval");
                log.setAction("合同归档");
                log.setDetail(StringUtils.isNotBlank(remark) ? remark : "合同已归档");
                bizContractContentService.addOperateLog(log);
                return success();
            } else {
                return error("归档失败");
            }
        } catch (Exception e) {
            return error("归档异常：" + e.getMessage());
        }
    }
}

