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
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flow.service.IApprovalFlowConfigService;
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
    private IApprovalFlowConfigService approvalFlowConfigService;

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
        normalizeInvoice(contractInvoice);
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
        List<String> assignees = approvalFlowConfigService.resolveAssignees("invoice");
        String currentUser = SecurityUtils.getUsername();
        entity.setApprovalStatus("pending");
        entity.setDirectLeader(getFlowAssignee(assignees, 0));
        entity.setApprover(getFlowAssignee(assignees, 1));
        entity.setHandler(getFlowAssignee(assignees, 2));
        entity.setCurrentApprovalNode("node1");
        entity.setCc(StringUtils.trimToNull(cc));
        entity.setSubmitter(resolveNickName(currentUser));
        entity.setSubmitTime(DateUtils.getNowDate());
        entity.setApproveTime(null);
        entity.setUpdateBy(currentUser);
        entity.setUpdateTime(DateUtils.getNowDate());
        int rows = contractInvoiceMapper.updateContractInvoice(entity);
        if (rows > 0)
        {
            operateLogService.addSystemLog(id, LOG_TYPE, "提交审批", buildSubmitLogDetail(entity, remark, assignees));
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
        String currentNode = StringUtils.trimToEmpty(entity.getCurrentApprovalNode());
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
            String nextNode = nextNodeKey(currentNode);
            String nextUser = getNodeAssignee(entity, nextNode);
            if (StringUtils.isNotBlank(nextNode) && StringUtils.isNotBlank(nextUser))
            {
                entity.setCurrentApprovalNode(nextNode);
            }
            else
            {
                entity.setApprovalStatus("approved");
                entity.setCurrentApprovalNode("finished");
                entity.setApproveTime(DateUtils.getNowDate());
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

    private String buildSubmitLogDetail(ContractInvoice entity, String remark, List<String> assignees)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("发起人：").append(StringUtils.defaultString(entity.getSubmitter(), "-")).append("；");
        sb.append("流程：").append(buildFlowSummary("invoice", assignees)).append("；");
        sb.append("抄送人：").append(StringUtils.defaultString(entity.getCc(), "-")).append("；");
        sb.append("说明：").append(StringUtils.isNotBlank(remark) ? remark.trim() : "提交发票审批");
        return sb.toString();
    }

    private String buildApproveLogDetail(ContractInvoice entity, String currentNode, String action, String remark)
    {
        String nodeName = getNodeName("invoice", currentNode);
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
            String nextUser = getNodeAssignee(entity, nextNodeKey(currentNode));
            if (StringUtils.isNotBlank(nextUser))
            {
                sb.append("；流转至：").append(resolveDisplayName(nextUser));
            }
        }
        return sb.toString();
    }

    private void validateCurrentNodeOperator(ContractInvoice entity, String currentNode, String currentUser)
    {
        String expectedUser = getNodeAssignee(entity, currentNode);
        if (StringUtils.isBlank(expectedUser))
        {
            throw new ServiceException(getNodeName("invoice", currentNode) + "未配置处理人");
        }
        if (!StringUtils.equals(expectedUser, currentUser))
        {
            throw new ServiceException("当前节点应由【" + resolveDisplayName(expectedUser) + "】处理");
        }
    }

    private String getNodeName(String businessType, String node)
    {
        return approvalFlowConfigService.getNodeName(businessType, node);
    }

    private String buildFlowSummary(String businessType, List<String> assignees)
    {
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < assignees.size(); i++)
        {
            parts.add(getNodeName(businessType, "node" + (i + 1)) + "(" + resolveDisplayName(assignees.get(i)) + ")");
        }
        return String.join(" → ", parts);
    }

    private String getFlowAssignee(List<String> assignees, int index)
    {
        return index < assignees.size() ? assignees.get(index) : null;
    }

    private String nextNodeKey(String currentNode)
    {
        if ("node1".equals(currentNode))
        {
            return "node2";
        }
        if ("node2".equals(currentNode))
        {
            return "node3";
        }
        return null;
    }

    private String getNodeAssignee(ContractInvoice entity, String nodeKey)
    {
        if ("node1".equals(nodeKey))
        {
            return entity.getDirectLeader();
        }
        if ("node2".equals(nodeKey))
        {
            return entity.getApprover();
        }
        if ("node3".equals(nodeKey))
        {
            return entity.getHandler();
        }
        return null;
    }

    private String resolveDisplayName(String userName)
    {
        return StringUtils.defaultIfBlank(resolveNickName(userName), "-");
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

