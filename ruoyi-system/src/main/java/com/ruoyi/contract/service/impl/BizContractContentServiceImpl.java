package com.ruoyi.contract.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.contract.util.ContractNumberGenerator;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.contract.mapper.BizContractContentMapper;
import com.ruoyi.contract.domain.BizContractContent;
import com.ruoyi.contract.domain.BizContractOperateLog;
import com.ruoyi.contract.service.IBizContractContentService;
import com.ruoyi.contract.service.IBizContractOperateLogService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 智能合同审批Service业务层处理
 *
 * @author ruoyi
 * @date 2026-03-14
 */
@Service
public class BizContractContentServiceImpl implements IBizContractContentService
{
    @Autowired
    private BizContractContentMapper bizContractContentMapper;

    @Autowired
    private IBizContractOperateLogService operateLogService;

    /**
     * 查询智能合同审批
     *
     * @param id 智能合同审批主键
     * @return 智能合同审批
     */
    @Override
    public BizContractContent selectBizContractContentById(Long id)
    {
        return bizContractContentMapper.selectBizContractContentById(id);
    }

    /**
     * 查询智能合同审批列表
     *
     * @param bizContractContent 智能合同审批
     * @return 智能合同审批
     */
    @Override
    public List<BizContractContent> selectBizContractContentList(BizContractContent bizContractContent)
    {
        return bizContractContentMapper.selectBizContractContentList(bizContractContent);
    }

    /**
     * 新增智能合同审批
     *
     * @param bizContractContent 智能合同审批
     * @return 结果
     */
    @Override
    public int insertBizContractContent(BizContractContent bizContractContent)
    {
        // 如果合同编号为空，则自动生成
        if (StringUtils.isEmpty(bizContractContent.getContractNumber()))
        {
            // 获取当前日期（格式：yyyyMMdd）
            String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            // 查询当日最大流水号
            Integer maxSerialNumber = bizContractContentMapper.selectMaxSerialNumber(dateStr);

            // 生成合同编号
            String contractNumber = ContractNumberGenerator.generateContractNumber(maxSerialNumber, dateStr);
            bizContractContent.setContractNumber(contractNumber);
        }
        // 设置归属人为当前登录用户
        if(bizContractContent.getOwner() == null || bizContractContent.getOwner().isEmpty()) {
            bizContractContent.setOwner(SecurityUtils.getUsername());
        }
        bizContractContent.setSignStatus("1");
        bizContractContent.setPerformanceStatus("1");
        bizContractContent.setBorrowStatus("1");
        //暂时生成的我方主体名称
        bizContractContent.setMyPartyName("浙江爱伊美服装有限公司");
        // 生成合同审批编号（使用 UUID）
        bizContractContent.setApprovalNumber(UUID.randomUUID().toString());
        bizContractContent.setCreateTime(DateUtils.getNowDate());
        int rows = bizContractContentMapper.insertBizContractContent(bizContractContent);
        if (rows > 0 && bizContractContent.getId() != null) {
            operateLogService.addSystemLog(bizContractContent.getId(), "approval", "新建合同", "创建合同审批记录");
        }
        return rows;
    }

    /**
     * 修改智能合同审批
     *
     * @param bizContractContent 智能合同审批
     * @return 结果
     */
    @Override
    public int updateBizContractContent(BizContractContent bizContractContent)
    {
        bizContractContent.setUpdateTime(DateUtils.getNowDate());
        int rows = bizContractContentMapper.updateBizContractContent(bizContractContent);
        if (rows > 0 && bizContractContent.getId() != null) {
            operateLogService.addSystemLog(bizContractContent.getId(), "approval", "编辑合同", "更新合同信息");
        }
        return rows;
    }

    /**
     * 批量删除智能合同审批
     *
     * @param ids 需要删除的智能合同审批主键
     * @return 结果
     */
    @Override
    public int deleteBizContractContentByIds(Long[] ids)
    {
        return bizContractContentMapper.deleteBizContractContentByIds(ids);
    }

    /**
     * 删除智能合同审批信息
     *
     * @param id 智能合同审批主键
     * @return 结果
     */
    @Override
    public int deleteBizContractContentById(Long id)
    {
        return bizContractContentMapper.deleteBizContractContentById(id);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 跳过第 0 行：说明文字
            if (rowIterator.hasNext()) rowIterator.next();

            // 第 1 行：表头
            if (!rowIterator.hasNext()) {
                throw new IllegalArgumentException("Excel 格式错误：缺少表头");
            }
            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = buildHeaderMap(headerRow);

            // 我们只关心这 9 个字段，其他列自动忽略
            String[] neededColumns = {
                    "合同标题", "合同编码", "合同对象", "金额", "金额类型",
                    "签订时间", "生效日期", "负责人", "部门"
            };

            for (String col : neededColumns) {
                if (!headerMap.containsKey(col)) {
                    throw new IllegalArgumentException("缺少必填列: " + col);
                }
            }

            int excelRowIndex = 2; // 数据从 Excel 第 3 行开始（索引为 2）
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                if (row == null || isRowEmpty(row)) {
                    excelRowIndex++;
                    continue;
                }
                BizContractContent entity = mapRowToEntity(row, headerMap);
                validateEntity(entity, excelRowIndex + 1); // 显示为第 N 行
                this.insertBizContractContent(entity);
                excelRowIndex++;
            }

        } catch (Exception e) {
            throw new RuntimeException("导入失败：" + e.getMessage(), e);
        }
    }

    @Override
    public void handleApproval(Long contractId, String action) {
        // 1. 校验合同是否存在
        BizContractContent contract = bizContractContentMapper.selectBizContractContentById(contractId);
        if (contract == null) {
            throw new ServiceException("合同不存在");
        }

        // 2. 校验当前状态是否可审批（例如：只能审批“待审批”状态）
        if (!"1".equals(contract.getSignStatus())) {
            throw new ServiceException("当前合同状态不可审批");
        }

        // 3. 根据 action 更新状态
        String newStatus;
        if ("agree".equals(action)) {
            newStatus = "2"; // 已同意
        } else if ("reject".equals(action)) {
            newStatus = "7"; // 已拒绝
        } else {
            throw new ServiceException("无效的审批操作");
        }

        // 4. 更新数据库
        contract.setSignStatus(newStatus);
        contract.setApprovePassTime(new Date()); // 记录审批通过时间（拒绝也可记）
        contract.setUpdateTime(new Date());

        int rows = bizContractContentMapper.updateBizContractContent(contract);
        if (rows == 0) {
            throw new ServiceException("审批失败，请重试");
        }
        operateLogService.addSystemLog(contractId, "approval", "审批合同", "审批结果：" + ("agree".equals(action) ? "通过" : "驳回"));
    }

    @Override
    @Transactional
    public void signContract(Long contractId, Boolean autoArchive) {
        BizContractContent contract = bizContractContentMapper.selectBizContractContentById(contractId);
        if (contract == null) throw new ServiceException("合同不存在");

        // 设置签署状态：自动归档 → "4"，否则 → "3"
        contract.setSignStatus(Boolean.TRUE.equals(autoArchive) ? "4" : "3");
        contract.setAccountStatus("3");
        contract.setUpdateTime(DateUtils.getNowDate());

        bizContractContentMapper.updateBizContractContent(contract);
        operateLogService.addSystemLog(contractId, "approval", "签署合同", Boolean.TRUE.equals(autoArchive) ? "签署完成，自动进入归档确认" : "签署完成，进入待归档");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void finishContract(Long contractId, String result, String finalStatus) {
        BizContractContent contract = bizContractContentMapper.selectBizContractContentById(contractId);
        if (contract == null) {
            throw new ServiceException("合同不存在");
        }
        contract.setPerformanceStatus(StringUtils.isBlank(finalStatus) ? "8" : finalStatus);
        if (StringUtils.isNotBlank(result)) {
            contract.setArchiveRemarks(result);
            contract.setRemark(result);
        }
        contract.setUpdateTime(DateUtils.getNowDate());
        bizContractContentMapper.updateBizContractContent(contract);
        operateLogService.addSystemLog(contractId, "approval", "合同完结", StringUtils.isNotBlank(result) ? result : "合同已完结");
    }

    @Override
    public List<BizContractOperateLog> selectOperateLogs(Long contractId) {
        return operateLogService.selectByContract(contractId, "approval");
    }

    @Override
    public void addOperateLog(BizContractOperateLog log) {
        if (log.getContractType() == null || log.getContractType().trim().isEmpty()) {
            log.setContractType("approval");
        }
        operateLogService.insert(log);
    }

    // 构建表头映射：只记录存在的列名 -> 列索引
    private Map<String, Integer> buildHeaderMap(Row headerRow) {
        Map<String, Integer> map = new HashMap<>();
        for (Cell cell : headerRow) {
            String value = getCellValueAsString(cell).trim();
            if (!value.isEmpty()) {
                map.put(value, cell.getColumnIndex());
            }
        }
        return map;
    }

    private BizContractContent mapRowToEntity(Row row, Map<String, Integer> headerMap) {
        BizContractContent entity = new BizContractContent();

        entity.setContractName(getCellStringValue(row, headerMap.get("合同标题")));
        entity.setContractNumber(getCellStringValue(row, headerMap.get("合同编码")));
        entity.setOtherPartyName(getCellStringValue(row, headerMap.get("合同对象")));

        // 金额
        String amountStr = getCellStringValue(row, headerMap.get("金额"));
        if (StringUtils.hasText(amountStr)) {
            try {
                entity.setTotalAmount(new BigDecimal(amountStr.trim()));
            } catch (NumberFormatException e) {
                throw new RuntimeException("金额必须为数字，当前值: '" + amountStr + "'");
            }
        }

        entity.setAmountType(getCellStringValue(row, headerMap.get("金额类型")));
        entity.setSignDate(parseDateCell(row, headerMap.get("签订时间")));
        entity.setStartDate(parseDateCell(row, headerMap.get("生效日期")));
        entity.setOwner(getCellStringValue(row, headerMap.get("负责人")));
        entity.setDepartment(getCellStringValue(row, headerMap.get("部门")));

        return entity;
    }

    private String getCellStringValue(Row row, Integer colIndex) {
        if (colIndex == null || row == null) return null;
        Cell cell = row.getCell(colIndex);
        return getCellValueAsString(cell);
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                } else {
                    double val = cell.getNumericCellValue();
                    if (val == Math.floor(val)) {
                        return String.valueOf((long) val);
                    } else {
                        return String.valueOf(val);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    private Date parseDateCell(Row row, Integer colIndex) {
        if (colIndex == null || row == null) return null;
        Cell cell = row.getCell(colIndex);
        if (cell == null) return null;

        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue();
                }
                double numericValue = cell.getNumericCellValue();
                if (numericValue > 0) {
                    return DateUtil.getJavaDate(numericValue);
                }
            }

            if (cell.getCellType() == CellType.FORMULA) {
                FormulaEvaluator evaluator = row.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
                CellValue evaluated = evaluator.evaluate(cell);
                if (evaluated != null) {
                    if (evaluated.getCellType() == CellType.NUMERIC) {
                        double numericValue = evaluated.getNumberValue();
                        if (DateUtil.isCellDateFormatted(cell) || numericValue > 0) {
                            return DateUtil.getJavaDate(numericValue);
                        }
                    } else if (evaluated.getCellType() == CellType.STRING) {
                        return parseDateString(evaluated.getStringValue());
                    }
                }
            }

            String text = new DataFormatter().formatCellValue(cell).trim();
            if (StringUtils.isBlank(text)) {
                return null;
            }
            return parseDateString(text);
        } catch (Exception e) {
            throw new RuntimeException("日期格式错误，当前值: " + getCellValueAsString(cell));
        }
    }

    private Date parseDateString(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        String normalized = text.trim()
                .replace("年", "-")
                .replace("月", "-")
                .replace("日", "")
                .replace("/", "-")
                .replace(".", "-");

        String[] patterns = {
                "yyyy-M-d",
                "yyyy-MM-dd",
                "yyyy-M-d HH:mm:ss",
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-M-d HH:mm",
                "yyyy-MM-dd HH:mm"
        };

        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                return sdf.parse(normalized);
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        DataFormatter formatter = new DataFormatter();
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            if (i < 0) continue;
            Cell cell = row.getCell(i);
            if (cell != null && StringUtils.isNotBlank(formatter.formatCellValue(cell))) {
                return false;
            }
        }
        return true;
    }

    private void validateEntity(BizContractContent entity, int rowNumber) {
        if (StringUtils.isEmpty(entity.getContractName())) {
            throw new RuntimeException("第" + rowNumber + "行：合同标题不能为空");
        }
        if (StringUtils.isEmpty(entity.getOtherPartyName())) {
            throw new RuntimeException("第" + rowNumber + "行：合同对象不能为空");
        }
        if (entity.getTotalAmount() == null) {
            throw new RuntimeException("第" + rowNumber + "行：金额不能为空");
        }
        if (StringUtils.isEmpty(entity.getAmountType())) {
            throw new RuntimeException("第" + rowNumber + "行：金额类型不能为空");
        }
        if (entity.getSignDate() == null) {
            throw new RuntimeException("第" + rowNumber + "行：签订时间不能为空");
        }
        if (entity.getStartDate() == null) {
            throw new RuntimeException("第" + rowNumber + "行：生效日期不能为空");
        }
        if (StringUtils.isEmpty(entity.getOwner())) {
            throw new RuntimeException("第" + rowNumber + "行：负责人不能为空");
        }
        if (StringUtils.isEmpty(entity.getDepartment())) {
            throw new RuntimeException("第" + rowNumber + "行：部门不能为空");
        }
    }


}
