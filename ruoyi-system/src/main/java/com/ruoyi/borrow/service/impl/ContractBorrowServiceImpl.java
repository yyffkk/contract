package com.ruoyi.borrow.service.impl;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.borrow.domain.ContractBorrow;
import com.ruoyi.borrow.mapper.ContractBorrowMapper;
import com.ruoyi.borrow.service.IContractBorrowService;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flow.service.IApprovalFlowConfigService;

/**
 * 合同借阅Service业务层处理
 */
@Service
public class ContractBorrowServiceImpl implements IContractBorrowService
{
    @Autowired
    private ContractBorrowMapper contractBorrowMapper;

    @Autowired
    private IApprovalFlowConfigService approvalFlowConfigService;

    @Override
    public ContractBorrow selectContractBorrowById(Long id)
    {
        return contractBorrowMapper.selectContractBorrowById(id);
    }

    @Override
    public List<ContractBorrow> selectContractBorrowList(ContractBorrow contractBorrow)
    {
        return contractBorrowMapper.selectContractBorrowList(contractBorrow);
    }

    @Override
    public int insertContractBorrow(ContractBorrow contractBorrow)
    {
        contractBorrow.setCreateTime(DateUtils.getNowDate());
        if (StringUtils.isBlank(contractBorrow.getApprovalStatus()))
        {
            contractBorrow.setApprovalStatus("draft");
        }
        if (StringUtils.isBlank(contractBorrow.getStatus()))
        {
            contractBorrow.setStatus("draft");
        }
        if (StringUtils.isBlank(contractBorrow.getDelFlag()))
        {
            contractBorrow.setDelFlag("0");
        }
        return contractBorrowMapper.insertContractBorrow(contractBorrow);
    }

    @Override
    public int updateContractBorrow(ContractBorrow contractBorrow)
    {
        contractBorrow.setUpdateTime(DateUtils.getNowDate());
        return contractBorrowMapper.updateContractBorrow(contractBorrow);
    }

    @Override
    public int deleteContractBorrowByIds(Long[] ids)
    {
        return contractBorrowMapper.deleteContractBorrowByIds(ids);
    }

    @Override
    public int deleteContractBorrowById(Long id)
    {
        return contractBorrowMapper.deleteContractBorrowById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitApproval(Long id, String approver, String handler, String remark)
    {
        ContractBorrow entity = getRequired(id);
        if ("approved".equals(entity.getApprovalStatus()))
        {
            throw new ServiceException("该借阅单已审批通过，无需重复提交");
        }
        if ("pending".equals(entity.getApprovalStatus()))
        {
            throw new ServiceException("该借阅单已在审批中");
        }
        List<String> assignees = approvalFlowConfigService.resolveAssignees("borrow");
        String currentUser = SecurityUtils.getUsername();
        entity.setApprovalStatus("pending");
        entity.setStatus("draft");
        entity.setDirectLeader(getFlowAssignee(assignees, 0));
        entity.setApprover(getFlowAssignee(assignees, 1));
        entity.setHandler(getFlowAssignee(assignees, 2));
        entity.setCurrentApprovalNode("node1");
        entity.setRemark(appendRemark(entity.getRemark(), "审批申请", buildApplyRemark(entity, remark, assignees)));
        entity.setUpdateBy(currentUser);
        entity.setUpdateTime(DateUtils.getNowDate());
        return contractBorrowMapper.updateContractBorrow(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int handleApproval(Long id, String action, String remark)
    {
        ContractBorrow entity = getRequired(id);
        if (!"pending".equals(entity.getApprovalStatus()))
        {
            throw new ServiceException("当前借阅单不在审批中");
        }
        String currentNode = StringUtils.trimToEmpty(entity.getCurrentApprovalNode());
        String currentUser = SecurityUtils.getUsername();
        validateCurrentNodeOperator(entity, currentNode, currentUser);
        if ("agree".equals(action))
        {
            String nextNode = nextNodeKey(currentNode);
            String nextUser = getNodeAssignee(entity, nextNode);
            if (StringUtils.isNotBlank(nextNode) && StringUtils.isNotBlank(nextUser))
            {
                entity.setCurrentApprovalNode(nextNode);
                entity.setRemark(appendRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark("borrow", currentNode, action, remark, nextUser)));
            }
            else
            {
                entity.setApprovalStatus("approved");
                entity.setStatus(resolveBorrowStatus(entity));
                entity.setCurrentApprovalNode("finished");
                entity.setRemark(appendRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark("borrow", currentNode, action, remark, null)));
            }
        }
        else if ("reject".equals(action))
        {
            entity.setApprovalStatus("rejected");
            entity.setStatus("draft");
            entity.setCurrentApprovalNode("rejected");
            entity.setRemark(appendRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark("borrow", currentNode, action, remark, null)));
        }
        else
        {
            throw new ServiceException("无效的审批动作");
        }
        entity.setUpdateBy(currentUser);
        entity.setUpdateTime(DateUtils.getNowDate());
        return contractBorrowMapper.updateContractBorrow(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int markReturned(Long id, String remark)
    {
        ContractBorrow entity = getRequired(id);
        if (!"approved".equals(entity.getApprovalStatus()))
        {
            throw new ServiceException("借阅单未审批通过，不能登记归还");
        }
        entity.setActualReturnDate(DateUtils.getNowDate());
        entity.setStatus("returned");
        entity.setRemark(appendRemark(entity.getRemark(), "归还登记", StringUtils.defaultIfBlank(remark, "已登记归还")));
        entity.setUpdateBy(SecurityUtils.getUsername());
        entity.setUpdateTime(DateUtils.getNowDate());
        return contractBorrowMapper.updateContractBorrow(entity);
    }

    private ContractBorrow getRequired(Long id)
    {
        ContractBorrow entity = contractBorrowMapper.selectContractBorrowById(id);
        if (entity == null)
        {
            throw new ServiceException("借阅记录不存在");
        }
        return entity;
    }

    private String resolveBorrowStatus(ContractBorrow entity)
    {
        Date expected = entity.getExpectedReturnDate();
        if (expected != null && DateUtils.getNowDate().after(expected))
        {
            return "overdue";
        }
        return "borrowing";
    }

    private String buildApplyRemark(ContractBorrow entity, String remark, List<String> assignees)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("合同：").append(StringUtils.defaultIfBlank(entity.getContractName(), "-"));
        sb.append("；借阅人：").append(StringUtils.defaultIfBlank(entity.getBorrower(), "-"));
        sb.append("；流程：").append(buildFlowSummary("borrow", assignees));
        if (StringUtils.isNotBlank(remark))
        {
            sb.append("；说明：").append(remark.trim());
        }
        return sb.toString();
    }

    private String buildNodeApprovalRemark(String businessType, String node, String action, String remark, String nextUser)
    {
        StringBuilder sb = new StringBuilder();
        sb.append(getNodeName(businessType, node)).append("agree".equals(action) ? "审批通过" : "审批驳回");
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

    private void validateCurrentNodeOperator(ContractBorrow entity, String currentNode, String currentUser)
    {
        String expectedUser = getNodeAssignee(entity, currentNode);
        if (StringUtils.isBlank(expectedUser))
        {
            throw new ServiceException(getNodeName("borrow", currentNode) + "未配置处理人");
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
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < assignees.size(); i++)
        {
            if (i > 0)
            {
                sb.append(" → ");
            }
            sb.append(getNodeName(businessType, "node" + (i + 1))).append("(").append(resolveDisplayName(assignees.get(i))).append(")");
        }
        return sb.toString();
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

    private String getNodeAssignee(ContractBorrow entity, String nodeKey)
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
        return StringUtils.defaultIfBlank(userName, "-");
    }

    private String appendRemark(String oldRemark, String tag, String content)
    {
        if (StringUtils.isBlank(content))
        {
            return oldRemark;
        }
        String piece = String.format("[%s] %s", tag, content);
        if (StringUtils.isBlank(oldRemark))
        {
            return piece;
        }
        return oldRemark + "；" + piece;
    }
}
