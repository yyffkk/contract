package com.ruoyi.invoice.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.utils.ApprovalFlowUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.contract.domain.BizContractContent;
import com.ruoyi.contract.domain.BizContractOperateLog;
import com.ruoyi.contract.mapper.BizContractContentMapper;
import com.ruoyi.contract.service.IBizContractOperateLogService;
import com.ruoyi.invoice.domain.ContractInvoice;
import com.ruoyi.invoice.mapper.ContractInvoiceMapper;
import com.ruoyi.invoice.service.IContractInvoiceService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 发票信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class ContractInvoiceServiceImpl implements IContractInvoiceService 
{
    private static final String LOG_TYPE = "invoice";

    @Autowired
    private ContractInvoiceMapper contractInvoiceMapper;

    @Autowired
    private IBizContractOperateLogService operateLogService;

    @Autowired
    private BizContractContentMapper bizContractContentMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDeptService sysDeptService;

    @Override
    public ContractInvoice selectContractInvoiceById(Long id)
    {
        return contractInvoiceMapper.selectContractInvoiceById(id);
    }

    @Override
    public List<ContractInvoice> selectContractInvoiceList(ContractInvoice contractInvoice)
    {
        return contractInvoiceMapper.selectContractInvoiceList(contractInvoice);
    }

    @Override
    public int insertContractInvoice(ContractInvoice contractInvoice)
    {
        contractInvoice.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isBlank(contractInvoice.getApprovalStatus()))
        {
            contractInvoice.setApprovalStatus("draft");
        }
        if (StringUtils.isBlank(contractInvoice.getDelFlag()))
        {
            contractInvoice.setDelFlag("0");
        }
        int rows = contractInvoiceMapper.insertContractInvoice(contractInvoice);
        if (rows > 0 && contractInvoice.getId() != null)
        {
            operateLogService.addSystemLog(contractInvoice.getId(), LOG_TYPE, "新建发票", "创建发票记录");
        }
        return rows;
    }

    @Override
    public int updateContractInvoice(ContractInvoice contractInvoice)
    {
        ContractInvoice current = getRequired(contractInvoice.getId());
        if ("pending".equals(current.getApprovalStatus()))
        {
            throw new ServiceException("审批中的发票不允许修改");
        }
        if ("approved".equals(current.getApprovalStatus()))
        {
            throw new ServiceException("审批通过的发票不允许修改");
        }
        contractInvoice.setApprovalStatus(current.getApprovalStatus());
        contractInvoice.setDirectLeader(current.getDirectLeader());
        contractInvoice.setApprover(current.getApprover());
        contractInvoice.setHandler(current.getHandler());
        contractInvoice.setCurrentApprovalNode(current.getCurrentApprovalNode());
        contractInvoice.setCc(current.getCc());
        contractInvoice.setSubmitter(current.getSubmitter());
        contractInvoice.setSubmitTime(current.getSubmitTime());
        contractInvoice.setApproveTime(current.getApproveTime());
        contractInvoice.setUpdateTime(DateUtils.getNowDate());
        int rows = contractInvoiceMapper.updateContractInvoice(contractInvoice);
        if (rows > 0 && contractInvoice.getId() != null)
        {
            operateLogService.addSystemLog(contractInvoice.getId(), LOG_TYPE, "编辑发票", "更新发票信息");
        }
        return rows;
    }

    @Override
    public int deleteContractInvoiceByIds(Long[] ids)
    {
        return contractInvoiceMapper.deleteContractInvoiceByIds(ids);
    }

    @Override
    public int deleteContractInvoiceById(Long id)
    {
        return contractInvoiceMapper.deleteContractInvoiceById(id);
    }

    @Override
    public String importContractInvoice(List<ContractInvoice> invoiceList, String operName)
    {
        if (invoiceList == null || invoiceList.isEmpty())
        {
            return "导入失败：发票数据不能为空！";
        }
        int successNum = 0;
        int failureNum = 0;
        StringBuilder failureMsg = new StringBuilder();
        int rowNum = 1;
        for (ContractInvoice invoice : invoiceList)
        {
            try
            {
                prepareImportedInvoice(invoice, operName);
                contractInvoiceMapper.insertContractInvoice(invoice);
                if (invoice.getId() != null)
                {
                    operateLogService.addSystemLog(invoice.getId(), LOG_TYPE, "导入发票", "通过模板导入发票记录");
                }
                successNum++;
            }
            catch (Exception e)
            {
                failureNum++;
                failureMsg.append("<br/>第").append(rowNum).append("行导入失败：")
                        .append(e.getMessage());
            }
            rowNum++;
        }
        if (failureNum > 0)
        {
            failureMsg.insert(0, "很抱歉，导入成功 " + successNum + " 条，失败 " + failureNum + " 条，错误如下：");
            return failureMsg.toString();
        }
        return "恭喜您，数据已全部导入成功！共 " + successNum + " 条。";
    }

    @Override
    public String importContractInvoice(MultipartFile file, String operName) throws IOException
    {
        List<ContractInvoice> invoiceList = parseInvoiceExcel(file);
        return importContractInvoice(invoiceList, operName);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitApproval(Long id, String approver, String handler, String cc, String remark)
    {
        ContractInvoice entity = getRequired(id);
        if ("pending".equals(entity.getApprovalStatus()))
        {
            throw new ServiceException("该发票已在审批中");
        }
        if ("approved".equals(entity.getApprovalStatus()))
        {
            throw new ServiceException("该发票已审批通过，无需重复提交");
        }
        String currentUser = SecurityUtils.getUsername();
        SysUser current = sysUserService.selectUserByUserName(currentUser);
        String directLeader = ApprovalFlowUtils.resolveDirectLeaderUserName(current, sysDeptService, sysUserService);
        String normalizedApprover = normalizeAssignee(approver, "审批人");
        String normalizedHandler = normalizeAssignee(handler, "办理人");
        if (StringUtils.isBlank(normalizedApprover) || StringUtils.isBlank(normalizedHandler))
        {
            String[] autoFlowUsers = resolveAutoFlowUsers(entity, currentUser, directLeader, normalizedApprover, normalizedHandler);
            normalizedApprover = autoFlowUsers[0];
            normalizedHandler = autoFlowUsers[1];
        }
        ensureDistinctFlowUsers(currentUser, directLeader, normalizedApprover, normalizedHandler);
        entity.setApprovalStatus("pending");
        entity.setDirectLeader(directLeader);
        entity.setApprover(normalizedApprover);
        entity.setHandler(normalizedHandler);
        entity.setCurrentApprovalNode("directLeader");
        entity.setCc(StringUtils.trimToNull(cc));
        entity.setSubmitter(resolveNickName(currentUser));
        entity.setSubmitTime(DateUtils.getNowDate());
        entity.setApproveTime(null);
        entity.setUpdateBy(currentUser);
        entity.setUpdateTime(DateUtils.getNowDate());
        int rows = contractInvoiceMapper.updateContractInvoice(entity);
        if (rows > 0)
        {
            operateLogService.addSystemLog(id, LOG_TYPE, "提交审批", buildSubmitLogDetail(entity, remark));
        }
        return rows;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleApproval(Long id, String action, String remark)
    {
        ContractInvoice entity = getRequired(id);
        if (!"pending".equals(entity.getApprovalStatus()))
        {
            throw new ServiceException("当前发票不在审批中");
        }
        String currentNode = ApprovalFlowUtils.normalizeNode(entity.getCurrentApprovalNode());
        String currentUser = SecurityUtils.getUsername();
        validateCurrentNodeOperator(entity, currentNode, currentUser);
        if ("reject".equals(action))
        {
            entity.setApprovalStatus("rejected");
            entity.setCurrentApprovalNode("rejected");
            entity.setApproveTime(DateUtils.getNowDate());
        }
        else if ("agree".equals(action))
        {
            if ("directLeader".equals(currentNode))
            {
                entity.setCurrentApprovalNode("approver");
            }
            else if ("approver".equals(currentNode))
            {
                entity.setCurrentApprovalNode("handler");
            }
            else if ("handler".equals(currentNode))
            {
                entity.setApprovalStatus("approved");
                entity.setCurrentApprovalNode("finished");
                entity.setApproveTime(DateUtils.getNowDate());
            }
            else
            {
                throw new ServiceException("当前审批节点无效");
            }
        }
        else
        {
            throw new ServiceException("无效的审批动作");
        }
        entity.setUpdateBy(currentUser);
        entity.setUpdateTime(DateUtils.getNowDate());
        int rows = contractInvoiceMapper.updateContractInvoice(entity);
        if (rows > 0)
        {
            operateLogService.addSystemLog(id, LOG_TYPE, "审批发票", buildApproveLogDetail(entity, currentNode, action, remark));
        }
        return rows;
    }

    @Override
    public List<BizContractOperateLog> selectOperateLogs(Long id)
    {
        return operateLogService.selectByContract(id, LOG_TYPE);
    }

    private ContractInvoice getRequired(Long id)
    {
        ContractInvoice entity = contractInvoiceMapper.selectContractInvoiceById(id);
        if (entity == null)
        {
            throw new ServiceException("发票记录不存在");
        }
        return entity;
    }

    private void normalizeInvoice(ContractInvoice invoice)
    {
        if (invoice == null)
        {
            return;
        }
        if (StringUtils.isBlank(invoice.getInvoiceType()))
        {
            invoice.setInvoiceType("normal");
        }
        else if ("增值税专用发票".equals(invoice.getInvoiceType()) || "专票".equals(invoice.getInvoiceType()) || "vat".equalsIgnoreCase(invoice.getInvoiceType()))
        {
            invoice.setInvoiceType("vat");
        }
        else if ("普通发票".equals(invoice.getInvoiceType()) || "普票".equals(invoice.getInvoiceType()) || "normal".equalsIgnoreCase(invoice.getInvoiceType()))
        {
            invoice.setInvoiceType("normal");
        }

        if (StringUtils.isBlank(invoice.getInvoiceStatus()))
        {
            invoice.setInvoiceStatus("invoiced");
        }
        else if ("未开票".equals(invoice.getInvoiceStatus()) || "no_invoice".equalsIgnoreCase(invoice.getInvoiceStatus()))
        {
            invoice.setInvoiceStatus("no_invoice");
        }
        else if ("已开票".equals(invoice.getInvoiceStatus()) || "invoiced".equalsIgnoreCase(invoice.getInvoiceStatus()))
        {
            invoice.setInvoiceStatus("invoiced");
        }
        else if ("已作废".equals(invoice.getInvoiceStatus()) || "voided".equalsIgnoreCase(invoice.getInvoiceStatus()))
        {
            invoice.setInvoiceStatus("voided");
        }

        if (StringUtils.isBlank(invoice.getAmountType()))
        {
            invoice.setAmountType("收入");
        }
        else if ("进项".equals(invoice.getAmountType()) || "支出".equals(invoice.getAmountType()))
        {
            invoice.setAmountType("支出");
        }
        else if ("销项".equals(invoice.getAmountType()) || "收入".equals(invoice.getAmountType()))
        {
            invoice.setAmountType("收入");
        }
    }

    private void prepareImportedInvoice(ContractInvoice invoice, String operName)
    {
        bindContractByNumber(invoice);
        normalizeInvoice(invoice);
        if (invoice.getInvoiceDate() == null)
        {
            throw new ServiceException("开票日期不能为空");
        }
        if (invoice.getInvoiceAmount() == null)
        {
            throw new ServiceException("发票金额不能为空");
        }
        if (StringUtils.isBlank(invoice.getPurchaserName()))
        {
            throw new ServiceException("发票抬头不能为空");
        }
        if (StringUtils.isBlank(invoice.getInvoiceContent()))
        {
            throw new ServiceException("开票内容不能为空");
        }
        if (StringUtils.isBlank(invoice.getRelatedContractNumber()))
        {
            throw new ServiceException("合同编码不能为空");
        }
        if (invoice.getUntaxedAmount() == null && invoice.getInvoiceAmount() != null)
        {
            BigDecimal taxAmount = invoice.getTaxAmount() == null ? BigDecimal.ZERO : invoice.getTaxAmount();
            invoice.setUntaxedAmount(invoice.getInvoiceAmount().subtract(taxAmount));
        }
        invoice.setCreateBy(operName);
        invoice.setCreateTime(DateUtils.getNowDate());
        invoice.setDelFlag("0");
        invoice.setApprovalStatus("draft");
    }

    private List<ContractInvoice> parseInvoiceExcel(MultipartFile file) throws IOException
    {
        try (InputStream inputStream = file.getInputStream(); Workbook workbook = WorkbookFactory.create(inputStream))
        {
            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null)
            {
                throw new ServiceException("Excel 中没有可读取的工作表");
            }
            int headerRowNum = findHeaderRow(sheet);
            if (headerRowNum < 0)
            {
                throw new ServiceException("未识别到表头，请使用系统模板或保留中文列标题");
            }
            Map<String, Integer> headerMap = buildHeaderMap(sheet.getRow(headerRowNum));
            List<ContractInvoice> list = new ArrayList<>();
            for (int i = headerRowNum + 1; i <= sheet.getLastRowNum(); i++)
            {
                Row row = sheet.getRow(i);
                if (isRowEmpty(row))
                {
                    continue;
                }
                ContractInvoice invoice = new ContractInvoice();
                invoice.setAmountType(getCellString(row, headerMap, aliases("发票分类", "分类", "进销项", "发票类别")));
                invoice.setCounterpartyName(getCellString(row, headerMap, aliases("相对方名称", "对方名称", "客户名称", "供应商名称")));
                invoice.setPurchaserName(getCellString(row, headerMap, aliases("发票抬头", "购买方名称", "购方名称", "抬头")));
                invoice.setPurchaserTaxNo(getCellString(row, headerMap, aliases("纳税人识别号", "购买方税号", "购方税号", "税号")));
                invoice.setBankName(getCellString(row, headerMap, aliases("开户银行", "银行", "开户行")));
                invoice.setBankAccount(getCellString(row, headerMap, aliases("银行账号", "账号", "开户账号")));
                invoice.setBankAddress(getCellString(row, headerMap, aliases("地址", "开户地址", "开户地址信息")));
                invoice.setBankPhone(getCellString(row, headerMap, aliases("电话", "联系电话")));
                invoice.setSellerName(getCellString(row, headerMap, aliases("销售方名称", "销方名称")));
                invoice.setSellerTaxNo(getCellString(row, headerMap, aliases("销售方税号", "销方税号")));
                invoice.setInvoiceCode(getCellString(row, headerMap, aliases("发票代码", "票据代码")));
                invoice.setInvoiceNumber(getCellString(row, headerMap, aliases("发票号码", "票据号码")));
                invoice.setInvoiceDate(getCellDate(row, headerMap, aliases("开票日期", "发票日期"), i + 1));
                invoice.setInvoiceAmount(getCellDecimal(row, headerMap, aliases("发票金额", "价税合计", "金额"), i + 1));
                invoice.setTaxRate(getCellDecimal(row, headerMap, aliases("税率"), i + 1));
                invoice.setTaxAmount(getCellDecimal(row, headerMap, aliases("税额"), i + 1));
                invoice.setUntaxedAmount(getCellDecimal(row, headerMap, aliases("不含税金额", "金额不含税"), i + 1));
                invoice.setInvoiceType(getCellString(row, headerMap, aliases("发票类型", "票种")));
                invoice.setInvoiceStatus(getCellString(row, headerMap, aliases("发票状态", "状态")));
                invoice.setInvoiceContent(getCellString(row, headerMap, aliases("开票内容", "货物或应税劳务名称", "内容")));
                invoice.setProject(getCellString(row, headerMap, aliases("所属项目", "项目")));
                invoice.setRelatedContractName(getCellString(row, headerMap, aliases("关联合同名称", "合同名称")));
                invoice.setRelatedContractNumber(getCellString(row, headerMap, aliases("合同编码", "关联合同编号", "合同编号")));
                invoice.setRemark(getCellString(row, headerMap, aliases("备注", "说明")));
                list.add(invoice);
            }
            if (list.isEmpty())
            {
                throw new ServiceException("模板中没有可导入的数据，请从表头下一行开始填写后再导入");
            }
            return list;
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("导入失败：" + e.getMessage());
        }
    }

    private int findHeaderRow(Sheet sheet)
    {
        int maxCheck = Math.min(sheet.getLastRowNum(), 5);
        for (int i = 0; i <= maxCheck; i++)
        {
            Row row = sheet.getRow(i);
            if (row == null)
            {
                continue;
            }
            Map<String, Integer> headerMap = buildHeaderMap(row);
            if (containsAnyHeader(headerMap, aliases("发票金额", "价税合计", "金额"))
                    && containsAnyHeader(headerMap, aliases("开票日期", "发票日期"))
                    && (containsAnyHeader(headerMap, aliases("发票抬头", "购买方名称", "购方名称"))
                    || containsAnyHeader(headerMap, aliases("发票号码", "票据号码"))
                    || containsAnyHeader(headerMap, aliases("合同编码", "关联合同编号", "合同编号"))))
            {
                return i;
            }
        }
        return -1;
    }

    private Map<String, Integer> buildHeaderMap(Row row)
    {
        Map<String, Integer> map = new HashMap<>();
        if (row == null)
        {
            return map;
        }
        for (Cell cell : row)
        {
            String value = normalizeHeader(readCellAsString(cell));
            if (StringUtils.isNotBlank(value))
            {
                map.put(value, cell.getColumnIndex());
            }
        }
        return map;
    }

    private boolean containsAnyHeader(Map<String, Integer> headerMap, List<String> aliases)
    {
        for (String alias : aliases)
        {
            if (headerMap.containsKey(normalizeHeader(alias)))
            {
                return true;
            }
        }
        return false;
    }

    private List<String> aliases(String... values)
    {
        return Arrays.asList(values);
    }

    private String getCellString(Row row, Map<String, Integer> headerMap, List<String> aliases)
    {
        Integer index = findColumnIndex(headerMap, aliases);
        if (index == null || row == null)
        {
            return null;
        }
        return StringUtils.trimToNull(readCellAsString(row.getCell(index)));
    }

    private BigDecimal getCellDecimal(Row row, Map<String, Integer> headerMap, List<String> aliases, int excelRowNum)
    {
        String value = getCellString(row, headerMap, aliases);
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        value = value.replace("¥", "").replace(",", "").replace("%", "").trim();
        try
        {
            return new BigDecimal(value);
        }
        catch (Exception e)
        {
            throw new ServiceException("第" + excelRowNum + "行数值格式错误：" + value);
        }
    }

    private Date getCellDate(Row row, Map<String, Integer> headerMap, List<String> aliases, int excelRowNum)
    {
        Integer index = findColumnIndex(headerMap, aliases);
        if (index == null || row == null)
        {
            return null;
        }
        Cell cell = row.getCell(index);
        if (cell == null)
        {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell))
        {
            return cell.getDateCellValue();
        }
        String value = StringUtils.trimToNull(readCellAsString(cell));
        if (value == null)
        {
            return null;
        }
        List<String> patterns = Arrays.asList("yyyy-MM-dd", "yyyy/M/d", "yyyy/M/dd", "yyyy-MM-dd HH:mm:ss", "yyyy/M/d H:m:s");
        for (String pattern : patterns)
        {
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.CHINA);
                sdf.setLenient(false);
                return sdf.parse(value);
            }
            catch (Exception ignored)
            {
            }
        }
        throw new ServiceException("第" + excelRowNum + "行日期格式错误：" + value);
    }

    private Integer findColumnIndex(Map<String, Integer> headerMap, List<String> aliases)
    {
        for (String alias : aliases)
        {
            Integer index = headerMap.get(normalizeHeader(alias));
            if (index != null)
            {
                return index;
            }
        }
        return null;
    }

    private String normalizeHeader(String value)
    {
        if (value == null)
        {
            return null;
        }
        return value.replace("\n", "").replace("\r", "").replace(" ", "").replace("　", "").trim();
    }

    private boolean isRowEmpty(Row row)
    {
        if (row == null)
        {
            return true;
        }
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++)
        {
            if (i < 0)
            {
                continue;
            }
            if (StringUtils.isNotBlank(readCellAsString(row.getCell(i))))
            {
                return false;
            }
        }
        return true;
    }

    private String readCellAsString(Cell cell)
    {
        if (cell == null)
        {
            return "";
        }
        if (cell.getCellType() == CellType.NUMERIC)
        {
            if (DateUtil.isCellDateFormatted(cell))
            {
                return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
            }
            return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
        }
        if (cell.getCellType() == CellType.BOOLEAN)
        {
            return String.valueOf(cell.getBooleanCellValue());
        }
        if (cell.getCellType() == CellType.FORMULA)
        {
            try
            {
                return cell.getStringCellValue();
            }
            catch (Exception e)
            {
                try
                {
                    return BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros().toPlainString();
                }
                catch (Exception ex)
                {
                    return "";
                }
            }
        }
        return cell.toString();
    }

    private void bindContractByNumber(ContractInvoice invoice)
    {
        if (invoice == null || StringUtils.isBlank(invoice.getRelatedContractNumber()))
        {
            return;
        }
        BizContractContent query = new BizContractContent();
        query.setContractNumber(invoice.getRelatedContractNumber().trim());
        List<BizContractContent> contracts = bizContractContentMapper.selectBizContractContentList(query);
        if (contracts == null || contracts.isEmpty())
        {
            throw new ServiceException("未找到对应合同编码：" + invoice.getRelatedContractNumber());
        }
        BizContractContent contract = contracts.get(0);
        invoice.setContractId(contract.getId());
        if (StringUtils.isBlank(invoice.getRelatedContractName()))
        {
            invoice.setRelatedContractName(contract.getContractName());
        }
        if (StringUtils.isBlank(invoice.getCounterpartyName()))
        {
            invoice.setCounterpartyName(contract.getOtherPartyName());
        }
        if (StringUtils.isBlank(invoice.getProject()))
        {
            invoice.setProject(contract.getProject());
        }
        if (StringUtils.isBlank(invoice.getAmountType()))
        {
            invoice.setAmountType(contract.getAmountType());
        }
        if (StringUtils.isBlank(invoice.getInvoiceContent()))
        {
            invoice.setInvoiceContent(contract.getContent());
        }
    }

    private String buildSubmitLogDetail(ContractInvoice entity, String remark)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("发起人：").append(StringUtils.defaultString(entity.getSubmitter(), "-")).append("；");
        sb.append("直接主管：").append(resolveDisplayName(entity.getDirectLeader())).append("；");
        sb.append("审批人：").append(resolveDisplayName(entity.getApprover())).append("；");
        sb.append("办理人：").append(resolveDisplayName(entity.getHandler())).append("；");
        sb.append("抄送人：").append(StringUtils.defaultString(entity.getCc(), "-")).append("；");
        if (StringUtils.isNotBlank(remark))
        {
            sb.append("说明：").append(remark.trim());
        }
        else
        {
            sb.append("说明：提交发票审批");
        }
        return sb.toString();
    }

    private String buildApproveLogDetail(ContractInvoice entity, String currentNode, String action, String remark)
    {
        String nodeName = getNodeName(currentNode);
        String actionName = "agree".equals(action) ? "审批通过" : "审批驳回";
        String approverName = resolveNickName(SecurityUtils.getUsername());
        StringBuilder sb = new StringBuilder();
        sb.append(nodeName).append(actionName).append("；处理人：").append(approverName);
        if (StringUtils.isNotBlank(remark))
        {
            sb.append("；意见：").append(remark.trim());
        }
        if ("agree".equals(action))
        {
            String nextUser = null;
            if ("directLeader".equals(currentNode))
            {
                nextUser = entity.getApprover();
            }
            else if ("approver".equals(currentNode))
            {
                nextUser = entity.getHandler();
            }
            if (StringUtils.isNotBlank(nextUser))
            {
                sb.append("；流转至：").append(resolveDisplayName(nextUser));
            }
        }
        return sb.toString();
    }

    private String normalizeAssignee(String userName, String roleName)
    {
        if (StringUtils.isBlank(userName))
        {
            return null;
        }
        SysUser user = sysUserService.selectUserByUserName(userName.trim());
        if (user == null)
        {
            throw new ServiceException(roleName + "不存在：" + userName);
        }
        return user.getUserName();
    }

    private String[] resolveAutoFlowUsers(ContractInvoice entity, String applicant, String directLeader, String approver, String handler)
    {
        String resolvedApprover = approver;
        String resolvedHandler = handler;
        BizContractContent contract = entity.getContractId() == null ? null : bizContractContentMapper.selectBizContractContentById(entity.getContractId());
        if (StringUtils.isBlank(resolvedApprover))
        {
            resolvedApprover = resolveContractOwnerUserName(contract);
            if (!isUsableFlowUser(resolvedApprover, applicant, directLeader))
            {
                resolvedApprover = resolveUpperLeaderUserName(directLeader, applicant, directLeader);
            }
        }
        if (StringUtils.isBlank(resolvedHandler))
        {
            resolvedHandler = resolveUpperLeaderUserName(resolvedApprover, applicant, directLeader, resolvedApprover);
            if (!isUsableFlowUser(resolvedHandler, applicant, directLeader, resolvedApprover))
            {
                String contractOwner = resolveContractOwnerUserName(contract);
                if (isUsableFlowUser(contractOwner, applicant, directLeader, resolvedApprover))
                {
                    resolvedHandler = contractOwner;
                }
            }
        }
        if (StringUtils.isBlank(resolvedApprover))
        {
            throw new ServiceException("未能自动确定审批人，请检查合同归属人或上级主管配置");
        }
        if (StringUtils.isBlank(resolvedHandler))
        {
            throw new ServiceException("未能自动确定办理人，请检查审批链上级主管配置");
        }
        return new String[] { resolvedApprover, resolvedHandler };
    }

    private String resolveContractOwnerUserName(BizContractContent contract)
    {
        if (contract == null || StringUtils.isBlank(contract.getOwner()))
        {
            return null;
        }
        return resolveUserNameByUserNameOrNickName(contract.getOwner().trim());
    }

    private String resolveUpperLeaderUserName(String baseUserName, String... excludes)
    {
        if (StringUtils.isBlank(baseUserName))
        {
            return null;
        }
        SysUser baseUser = sysUserService.selectUserByUserName(baseUserName);
        if (baseUser == null)
        {
            return null;
        }
        try
        {
            String leaderUserName = ApprovalFlowUtils.resolveDirectLeaderUserName(baseUser, sysDeptService, sysUserService);
            return isUsableFlowUser(leaderUserName, excludes) ? leaderUserName : null;
        }
        catch (Exception ex)
        {
            return null;
        }
    }

    private String resolveUserNameByUserNameOrNickName(String value)
    {
        if (StringUtils.isBlank(value))
        {
            return null;
        }
        SysUser exact = sysUserService.selectUserByUserName(value);
        if (exact != null)
        {
            return exact.getUserName();
        }
        SysUser query = new SysUser();
        query.setNickName(value);
        List<SysUser> users = sysUserService.selectUserList(query);
        if (users == null || users.isEmpty())
        {
            return null;
        }
        if (users.size() > 1)
        {
            throw new ServiceException("合同归属人【" + value + "】匹配到多个系统用户，请改为唯一用户名");
        }
        return users.get(0).getUserName();
    }

    private boolean isUsableFlowUser(String userName, String... excludes)
    {
        if (StringUtils.isBlank(userName))
        {
            return false;
        }
        if (excludes == null)
        {
            return true;
        }
        for (String exclude : excludes)
        {
            if (StringUtils.equals(userName, exclude))
            {
                return false;
            }
        }
        return true;
    }

    private void ensureDistinctFlowUsers(String applicant, String directLeader, String approver, String handler)
    {
        if (StringUtils.equalsAny(applicant, approver, handler))
        {
            throw new ServiceException("审批人和办理人不能与申请人相同");
        }
        if (StringUtils.equals(directLeader, approver))
        {
            throw new ServiceException("审批人不能与直接主管相同");
        }
        if (StringUtils.equalsAny(handler, directLeader, approver))
        {
            throw new ServiceException("办理人不能与直接主管或审批人相同");
        }
    }

    private void validateCurrentNodeOperator(ContractInvoice entity, String currentNode, String currentUser)
    {
        String expectedUser = null;
        if ("directLeader".equals(currentNode))
        {
            expectedUser = entity.getDirectLeader();
        }
        else if ("approver".equals(currentNode))
        {
            expectedUser = entity.getApprover();
        }
        else if ("handler".equals(currentNode))
        {
            expectedUser = entity.getHandler();
        }
        else
        {
            throw new ServiceException("当前审批节点无效");
        }
        if (StringUtils.isBlank(expectedUser))
        {
            throw new ServiceException(getNodeName(currentNode) + "未配置处理人");
        }
        if (!StringUtils.equals(expectedUser, currentUser))
        {
            throw new ServiceException("当前节点应由【" + resolveDisplayName(expectedUser) + "】处理");
        }
    }

    private String getNodeName(String node)
    {
        if ("directLeader".equals(node))
        {
            return "直接主管";
        }
        if ("approver".equals(node))
        {
            return "审批人";
        }
        if ("handler".equals(node))
        {
            return "办理人";
        }
        if ("finished".equals(node))
        {
            return "已完成";
        }
        if ("rejected".equals(node))
        {
            return "已驳回";
        }
        return "审批节点";
    }

    private String resolveDisplayName(String userName)
    {
        return ApprovalFlowUtils.resolveDisplayNameByUserName(userName, sysUserService);
    }

    private String resolveNickName(String userName)
    {
        if (StringUtils.isBlank(userName))
        {
            return userName;
        }
        SysUser user = sysUserService.selectUserByUserName(userName);
        if (user != null && StringUtils.isNotBlank(user.getNickName()))
        {
            return user.getNickName();
        }
        return userName;
    }
}
