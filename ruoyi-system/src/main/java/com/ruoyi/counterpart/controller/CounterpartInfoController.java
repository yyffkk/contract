package com.ruoyi.counterpart.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.sun.deploy.net.URLEncoder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.counterpart.domain.CounterpartInfo;
import com.ruoyi.counterpart.service.ICounterpartInfoService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 相对方信息Controller
 * 
 * @author ruoyi
 * @date 2026-03-17
 */
@RestController
@RequestMapping("/counterpart/counterpart")
public class CounterpartInfoController extends BaseController
{
    @Autowired
    private ICounterpartInfoService counterpartInfoService;

    /**
     * 查询相对方信息列表
     */
    @PreAuthorize("@ss.hasPermi('counterpart:counterpart:list')")
    @GetMapping("/list")
    public TableDataInfo list(CounterpartInfo counterpartInfo)
    {
        startPage();
        List<CounterpartInfo> list = counterpartInfoService.selectCounterpartInfoList(counterpartInfo);
        return getDataTable(list);
    }

    /**
     * 导出相对方信息列表
     */
    @PreAuthorize("@ss.hasPermi('counterpart:counterpart:export')")
    @Log(title = "相对方信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, CounterpartInfo counterpartInfo)
    {
        List<CounterpartInfo> list = counterpartInfoService.selectCounterpartInfoList(counterpartInfo);
        ExcelUtil<CounterpartInfo> util = new ExcelUtil<CounterpartInfo>(CounterpartInfo.class);
        util.exportExcel(response, list, "相对方信息数据");
    }

    /**
     * 获取相对方信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('counterpart:counterpart:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(counterpartInfoService.selectCounterpartInfoById(id));
    }

    /**
     * 新增相对方信息
     */
    @PreAuthorize("@ss.hasPermi('counterpart:counterpart:add')")
    @Log(title = "相对方信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody CounterpartInfo counterpartInfo)
    {
        return toAjax(counterpartInfoService.insertCounterpartInfo(counterpartInfo));
    }

    /**
     * 修改相对方信息
     */
    @PreAuthorize("@ss.hasPermi('counterpart:counterpart:edit')")
    @Log(title = "相对方信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody CounterpartInfo counterpartInfo)
    {
        return toAjax(counterpartInfoService.updateCounterpartInfo(counterpartInfo));
    }

    /**
     * 删除相对方信息
     */
    @PreAuthorize("@ss.hasPermi('counterpart:counterpart:remove')")
    @Log(title = "相对方信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(counterpartInfoService.deleteCounterpartInfoByIds(ids));
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        String fileName = URLEncoder.encode("相对方导入模板", String.valueOf(StandardCharsets.UTF_8)).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook();
             OutputStream out = response.getOutputStream()) {

            Sheet sheet = workbook.createSheet("相对方信息");

            // 创建表头行
            Row headerRow = sheet.createRow(0);
            String[] headers = {"相对方名称", "所属员工工号", "开户银行", "开户名称", "银行账户", "纳税人识别号", "地址", "电话"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // 创建示例行（可选）
            Row exampleRow = sheet.createRow(1);
            exampleRow.createCell(0).setCellValue("宁波柏顺服饰有限公司");
            // 其他留空

            // 自动列宽（可选）
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
        }
    }

    /**
     * 导入数据
     */
    @PostMapping("/importData")
    public AjaxResult importData(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("请选择要上传的文件");
        }

        if (!file.getOriginalFilename().toLowerCase().endsWith(".xlsx")) {
            return AjaxResult.error("仅支持 .xlsx 格式文件");
        }

        List<CounterpartInfo> list = new ArrayList<>();
        Workbook workbook = null;

        try {
            workbook = new XSSFWorkbook(file.getInputStream());
            Sheet sheet = workbook.getSheetAt(0);

            // 从第1行开始读（第0行是表头）
            for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row == null) continue;

                CounterpartInfo entity = new CounterpartInfo();

                // 读取各列（注意：POI 列索引从 0 开始）
                entity.setCounterpartName(getCellValue(row.getCell(0)));
                entity.setEmployeeId(getCellValue(row.getCell(1)));
                entity.setBankName(getCellValue(row.getCell(2)));
                entity.setAccountName(getCellValue(row.getCell(3)));
                entity.setBankAccount(getCellValue(row.getCell(4)));
                entity.setTaxId(getCellValue(row.getCell(5)));
                entity.setAddress(getCellValue(row.getCell(6)));
                entity.setPhone(getCellValue(row.getCell(7)));

                // 跳过空行（至少“相对方名称”要有值）
                if (entity.getCounterpartName() == null || entity.getCounterpartName().trim().isEmpty()) {
                    continue;
                }

                list.add(entity);
            }

            // 批量保存（假设你有批量插入方法）
            if (!list.isEmpty()) {
                counterpartInfoService.batchInsert(list);
                return AjaxResult.success("导入成功", list.size());
            } else {
                return AjaxResult.warn("未读取到有效数据");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("导入失败：" + e.getMessage());
        } finally {
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException ignored) {}
            }
        }
    }

    // 辅助方法：安全获取单元格值
    private String getCellValue(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                // 防止数字被转成科学计数法（如银行账号）
                cell.setCellType(CellType.STRING);
                return cell.getStringCellValue().trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
