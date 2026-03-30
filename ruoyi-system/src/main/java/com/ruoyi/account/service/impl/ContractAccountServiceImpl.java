package com.ruoyi.account.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.account.domain.ContractAccount;
import com.ruoyi.account.mapper.ContractAccountMapper;
import com.ruoyi.account.service.IContractAccountService;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.contract.domain.BizContractContent;
import com.ruoyi.contract.mapper.BizContractContentMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.flow.service.IApprovalFlowConfigService;

/**
 * 账款信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class ContractAccountServiceImpl implements IContractAccountService 
{
    private static final String HEADER_CONTRACT_NUMBER = "合同编码";
    private static final String HEADER_AMOUNT = "金额";
    private static final String HEADER_DATE = "日期";
    private static final String HEADER_REMARK = "备注";
    private static final String HEADER_AMOUNT_TYPE = "金额类型";
    private static final String HEADER_BANK_NAME = "开户银行";
    private static final String HEADER_ACCOUNT_NAME = "开户名称";
    private static final String HEADER_BANK_ACCOUNT = "银行账户";
    private static final String HEADER_TAX_NO = "纳税人识别号";
    private static final String HEADER_ADDRESS = "地址";
    private static final String HEADER_PHONE = "电话";

    @Autowired
    private ContractAccountMapper contractAccountMapper;

    @Autowired
    private BizContractContentMapper bizContractContentMapper;

    @Autowired
    private IApprovalFlowConfigService approvalFlowConfigService;

    @Override
    public ContractAccount selectContractAccountById(Long id)
    {
        return contractAccountMapper.selectContractAccountById(id);
    }

    @Override
    public List<ContractAccount> selectContractAccountList(ContractAccount contractAccount)
    {
        return contractAccountMapper.selectContractAccountList(contractAccount);
    }

    @Override
    public int insertContractAccount(ContractAccount contractAccount)
    {
        contractAccount.setCreateTime(DateUtils.getNowDate());
        return contractAccountMapper.insertContractAccount(contractAccount);
    }

    @Override
    public int updateContractAccount(ContractAccount contractAccount)
    {
        contractAccount.setUpdateTime(DateUtils.getNowDate());
        return contractAccountMapper.updateContractAccount(contractAccount);
    }

    @Override
    public int deleteContractAccountByIds(Long[] ids)
    {
        return contractAccountMapper.deleteContractAccountByIds(ids);
    }

    @Override
    public int deleteContractAccountById(Long id)
    {
        return contractAccountMapper.deleteContractAccountById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitApproval(Long id, String applyType, String approver, String handler, String remark)
    {
        ContractAccount entity = contractAccountMapper.selectContractAccountById(id);
        if (entity == null)
        {
            throw new ServiceException("账款记录不存在");
        }
        validateApplyType(entity, applyType);
        if ("approving".equals(entity.getStatus()))
        {
            throw new ServiceException("该账款已提交审批，请勿重复提交");
        }
        if ("approved".equals(entity.getStatus()))
        {
            throw new ServiceException("该账款已审批通过，无需重复提交");
        }
        List<String> assignees = approvalFlowConfigService.resolveAssignees("account");
        String currentUser = SecurityUtils.getUsername();
        entity.setStatus("approving");
        entity.setDirectLeader(getFlowAssignee(assignees, 0));
        entity.setApprover(getFlowAssignee(assignees, 1));
        entity.setHandler(getFlowAssignee(assignees, 2));
        entity.setCurrentApprovalNode("node1");
        entity.setRemark(mergeRemark(entity.getRemark(), "审批申请", buildApplyRemark(applyType, remark, assignees)));
        entity.setUpdateBy(currentUser);
        entity.setUpdateTime(DateUtils.getNowDate());
        return contractAccountMapper.updateContractAccount(entity);
    }

@Override
    @Transactional(rollbackFor = Exception.class)
    public int handleApproval(Long id, String action, String remark)
    {
        ContractAccount entity = contractAccountMapper.selectContractAccountById(id);
        if (entity == null)
        {
            throw new ServiceException("账款记录不存在");
        }
        if (!"approving".equals(entity.getStatus()))
        {
            throw new ServiceException("当前账款不在审批中，无法执行审批操作");
        }
        String currentNode = StringUtils.trimToEmpty(entity.getCurrentApprovalNode());
        String currentUser = SecurityUtils.getUsername();
        validateCurrentNodeOperator(entity, currentNode, currentUser);
        if ("reject".equals(action))
        {
            entity.setStatus("rejected");
            entity.setCurrentApprovalNode("rejected");
            entity.setRemark(mergeRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark("account", currentNode, action, remark, null)));
        }
        else if ("agree".equals(action))
        {
            String nextNode = nextNodeKey(currentNode);
            String nextUser = getNodeAssignee(entity, nextNode);
            if (StringUtils.isNotBlank(nextNode) && StringUtils.isNotBlank(nextUser))
            {
                entity.setCurrentApprovalNode(nextNode);
                entity.setRemark(mergeRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark("account", currentNode, action, remark, nextUser)));
            }
            else
            {
                entity.setStatus("approved");
                entity.setCurrentApprovalNode("finished");
                entity.setRemark(mergeRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark("account", currentNode, action, remark, null)));
            }
        }
        else
        {
            throw new ServiceException("无效的审批动作");
        }
        entity.setUpdateBy(currentUser);
        entity.setUpdateTime(DateUtils.getNowDate());
        return contractAccountMapper.updateContractAccount(entity);
    }

@Override
    @Transactional(rollbackFor = Exception.class)
    public int importData(MultipartFile file)
    {
        if (file == null || file.isEmpty())
        {
            throw new ServiceException("请选择要上传的文件");
        }

        try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is))
        {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.rowIterator();
            if (!rowIterator.hasNext())
            {
                throw new ServiceException("Excel 格式错误：缺少说明行");
            }
            rowIterator.next();
            if (!rowIterator.hasNext())
            {
                throw new ServiceException("Excel 格式错误：缺少表头");
            }

            Row headerRow = rowIterator.next();
            Map<String, Integer> headerMap = buildHeaderMap(headerRow);
            validateHeader(headerMap);

            List<ContractAccount> list = new ArrayList<>();
            int excelRowNum = 3;
            while (rowIterator.hasNext())
            {
                Row row = rowIterator.next();
                if (row == null || isRowEmpty(row))
                {
                    excelRowNum++;
                    continue;
                }
                list.add(mapRowToEntity(row, headerMap, excelRowNum));
                excelRowNum++;
            }

            if (list.isEmpty())
            {
                throw new ServiceException("未读取到有效数据");
            }

            return contractAccountMapper.batchInsertContractAccount(list);
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

    private Map<String, Integer> buildHeaderMap(Row headerRow)
    {
        Map<String, Integer> map = new LinkedHashMap<>();
        DataFormatter formatter = new DataFormatter();
        for (Cell cell : headerRow)
        {
            map.put(formatter.formatCellValue(cell).trim(), cell.getColumnIndex());
        }
        return map;
    }

    private void validateHeader(Map<String, Integer> headerMap)
    {
        String[] requiredHeaders = { HEADER_CONTRACT_NUMBER, HEADER_AMOUNT, HEADER_DATE };
        for (String header : requiredHeaders)
        {
            if (!headerMap.containsKey(header))
            {
                throw new ServiceException("导入模板缺少字段：" + header);
            }
        }
    }

    private ContractAccount mapRowToEntity(Row row, Map<String, Integer> headerMap, int excelRowNum)
    {
        String contractNumber = getString(row, headerMap.get(HEADER_CONTRACT_NUMBER));
        if (StringUtils.isBlank(contractNumber))
        {
            throw new ServiceException("第 " + excelRowNum + " 行：合同编码不能为空");
        }

        BizContractContent query = new BizContractContent();
        query.setContractNumber(contractNumber.trim());
        List<BizContractContent> contractList = bizContractContentMapper.selectBizContractContentList(query);
        if (contractList == null || contractList.isEmpty())
        {
            throw new ServiceException("第 " + excelRowNum + " 行：未找到合同编码【" + contractNumber + "】对应的合同");
        }
        BizContractContent contract = contractList.get(0);

        BigDecimal amount = getBigDecimal(row, headerMap.get(HEADER_AMOUNT), excelRowNum, HEADER_AMOUNT);
        Date accountDate = getDate(row, headerMap.get(HEADER_DATE), excelRowNum, HEADER_DATE);
        String importAmountType = getString(row, headerMap.get(HEADER_AMOUNT_TYPE));
        String finalAmountType = resolveAmountType(importAmountType, contract.getAmountType());
        String remark = buildRemark(
            getString(row, headerMap.get(HEADER_REMARK)),
            getString(row, headerMap.get(HEADER_BANK_NAME)),
            getString(row, headerMap.get(HEADER_ACCOUNT_NAME)),
            getString(row, headerMap.get(HEADER_BANK_ACCOUNT)),
            getString(row, headerMap.get(HEADER_TAX_NO)),
            getString(row, headerMap.get(HEADER_ADDRESS)),
            getString(row, headerMap.get(HEADER_PHONE)),
            importAmountType
        );

        ContractAccount entity = new ContractAccount();
        entity.setContractId(contract.getId());
        entity.setRelatedContractNumber(contract.getContractNumber());
        entity.setRelatedContractName(contract.getContractName());
        entity.setOurParty(contract.getMyPartyName());
        entity.setOtherParty(contract.getOtherPartyName());
        entity.setAccountDate(accountDate);
        entity.setAmount(amount);
        entity.setAmountType(finalAmountType);
        entity.setAccountName(buildAccountName(contract.getContractName(), finalAmountType));
        entity.setRemark(remark);
        entity.setStatus("pending");
        entity.setDelFlag("0");
        entity.setCreateBy(SecurityUtils.getUsername());
        entity.setCreateTime(DateUtils.getNowDate());
        return entity;
    }

    private String buildAccountName(String contractName, String amountType)
    {
        String prefix = StringUtils.defaultString(contractName, "未命名合同");
        if ("expense".equals(amountType))
        {
            return prefix + "-付款账款";
        }
        if ("refund".equals(amountType))
        {
            return prefix + "-退款账款";
        }
        return prefix + "-收款账款";
    }

    private String resolveAmountType(String importAmountType, String contractAmountType)
    {
        if (StringUtils.contains(importAmountType, "退款"))
        {
            return "expense";
        }
        if (StringUtils.isNotBlank(importAmountType))
        {
            if (StringUtils.containsAny(importAmountType.toLowerCase(), "支出", "付款", "expense", "pay"))
            {
                return "expense";
            }
            if (StringUtils.containsAny(importAmountType.toLowerCase(), "收入", "收款", "income", "receive"))
            {
                return "income";
            }
        }
        if (StringUtils.containsAny(StringUtils.defaultString(contractAmountType).toLowerCase(), "支出", "付款", "expense", "pay"))
        {
            return "expense";
        }
        return "income";
    }

    private String buildRemark(String remark, String bankName, String accountName, String bankAccount, String taxNo,
        String address, String phone, String importAmountType)
    {
        List<String> parts = new ArrayList<>();
        if (StringUtils.isNotBlank(remark))
        {
            parts.add(remark.trim());
        }
        if (StringUtils.contains(importAmountType, "退款"))
        {
            parts.add("导入金额类型：退款");
        }
        if (StringUtils.isNotBlank(bankName))
        {
            parts.add("开户银行：" + bankName.trim());
        }
        if (StringUtils.isNotBlank(accountName))
        {
            parts.add("开户名称：" + accountName.trim());
        }
        if (StringUtils.isNotBlank(bankAccount))
        {
            parts.add("银行账户：" + bankAccount.trim());
        }
        if (StringUtils.isNotBlank(taxNo))
        {
            parts.add("纳税人识别号：" + taxNo.trim());
        }
        if (StringUtils.isNotBlank(address))
        {
            parts.add("地址：" + address.trim());
        }
        if (StringUtils.isNotBlank(phone))
        {
            parts.add("电话：" + phone.trim());
        }
        return String.join("；", parts);
    }

    private String getString(Row row, Integer index)
    {
        if (index == null)
        {
            return null;
        }
        Cell cell = row.getCell(index);
        if (cell == null)
        {
            return null;
        }
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell).trim();
    }

    private BigDecimal getBigDecimal(Row row, Integer index, int excelRowNum, String fieldName)
    {
        String value = getString(row, index);
        if (StringUtils.isBlank(value))
        {
            throw new ServiceException("第 " + excelRowNum + " 行：" + fieldName + "不能为空");
        }
        try
        {
            return new BigDecimal(value.replace(",", ""));
        }
        catch (Exception e)
        {
            throw new ServiceException("第 " + excelRowNum + " 行：" + fieldName + "格式不正确");
        }
    }

    private Date getDate(Row row, Integer index, int excelRowNum, String fieldName)
    {
        Cell cell = row.getCell(index);
        if (cell == null)
        {
            throw new ServiceException("第 " + excelRowNum + " 行：" + fieldName + "不能为空");
        }
        try
        {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell))
            {
                return cell.getDateCellValue();
            }
            String value = new DataFormatter().formatCellValue(cell).trim();
            if (StringUtils.isBlank(value))
            {
                throw new ServiceException("第 " + excelRowNum + " 行：" + fieldName + "不能为空");
            }
            String normalized = value.replace('/', '-');
            return new SimpleDateFormat("yyyy-M-d").parse(normalized);
        }
        catch (ServiceException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new ServiceException("第 " + excelRowNum + " 行：" + fieldName + "格式不正确");
        }
    }

    private boolean isRowEmpty(Row row)
    {
        DataFormatter formatter = new DataFormatter();
        for (Cell cell : row)
        {
            if (StringUtils.isNotBlank(formatter.formatCellValue(cell)))
            {
                return false;
            }
        }
        return true;
    }

    private void validateApplyType(ContractAccount entity, String applyType)
    {
        if (StringUtils.isBlank(applyType))
        {
            throw new ServiceException("申请类型不能为空");
        }
        if ("receive".equals(applyType) && !"income".equals(entity.getAmountType()))
        {
            throw new ServiceException("当前选中记录不是收款类型，不能申请收款");
        }
        if ("pay".equals(applyType) && !"expense".equals(entity.getAmountType()))
        {
            throw new ServiceException("当前选中记录不是付款类型，不能申请付款");
        }
    }

    private String buildApplyRemark(String applyType, String remark, List<String> assignees)
    {
        String typeName = "pay".equals(applyType) ? "付款" : "收款";
        StringBuilder sb = new StringBuilder();
        sb.append(typeName).append("申请，流程：").append(buildFlowSummary("account", assignees));
        if (StringUtils.isNotBlank(remark))
        {
            sb.append("，说明：").append(remark.trim());
        }
        return sb.toString();
    }

    private String buildNodeApprovalRemark(String businessType, String node, String action, String remark, String nextUser)
    {
        String nodeName = getNodeName(businessType, node);
        String actionName = "agree".equals(action) ? "审批通过" : "审批驳回";
        StringBuilder sb = new StringBuilder();
        sb.append(nodeName).append(actionName);
        if (StringUtils.isNotBlank(remark))
        {
            sb.append("，意见：").append(remark.trim());
        }
        if (StringUtils.isNotBlank(nextUser))
        {
            sb.append("，流转至：").append(resolveDisplayName(nextUser));
        }
        return sb.toString();
    }

    private void validateCurrentNodeOperator(ContractAccount entity, String currentNode, String currentUser)
    {
        String expectedUser = getNodeAssignee(entity, currentNode);
        if (StringUtils.isBlank(expectedUser))
        {
            throw new ServiceException(getNodeName("account", currentNode) + "未配置处理人");
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

    private String resolveDisplayName(String userName)
    {
        return StringUtils.defaultIfBlank(userName, "-");
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

    private String getNodeAssignee(ContractAccount entity, String nodeKey)
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

    private String mergeRemark(String oldRemark, String prefix, String content)
    {
        if (StringUtils.isBlank(content))
        {
            return oldRemark;
        }
        String piece = String.format("[%s] %s", prefix, content);
        if (StringUtils.isBlank(oldRemark))
        {
            return piece;
        }
        return oldRemark + "；" + piece;
    }
}
