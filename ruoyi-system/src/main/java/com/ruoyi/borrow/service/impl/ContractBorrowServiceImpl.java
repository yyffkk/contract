package com.ruoyi.borrow.service.impl;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.utils.ApprovalFlowUtils;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.borrow.mapper.ContractBorrowMapper;
import com.ruoyi.borrow.domain.ContractBorrow;
import com.ruoyi.borrow.service.IContractBorrowService;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;

/**
 * 合同借阅Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-26
 */
@Service
public class ContractBorrowServiceImpl implements IContractBorrowService 
{
    @Autowired
    private ContractBorrowMapper contractBorrowMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDeptService sysDeptService;

    /**
     * 查询合同借阅
     * 
     * @param id 合同借阅主键
     * @return 合同借阅
     */
    @Override
    public ContractBorrow selectContractBorrowById(Long id)
    {
        return contractBorrowMapper.selectContractBorrowById(id);
    }

    /**
     * 查询合同借阅列表
     * 
     * @param contractBorrow 合同借阅
     * @return 合同借阅
     */
    @Override
    public List<ContractBorrow> selectContractBorrowList(ContractBorrow contractBorrow)
    {
        return contractBorrowMapper.selectContractBorrowList(contractBorrow);
    }

    /**
     * 新增合同借阅
     * 
     * @param contractBorrow 合同借阅
     * @return 结果
     */
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

    /**
     * 修改合同借阅
     * 
     * @param contractBorrow 合同借阅
     * @return 结果
     */
    @Override
    public int updateContractBorrow(ContractBorrow contractBorrow)
    {
        contractBorrow.setUpdateTime(DateUtils.getNowDate());
        return contractBorrowMapper.updateContractBorrow(contractBorrow);
    }

    /**
     * 批量删除合同借阅
     * 
     * @param ids 需要删除的合同借阅主键
     * @return 结果
     */
    @Override
    public int deleteContractBorrowByIds(Long[] ids)
    {
        return contractBorrowMapper.deleteContractBorrowByIds(ids);
    }

    /**
     * 删除合同借阅信息
     * 
     * @param id 合同借阅主键
     * @return 结果
     */
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
        String currentUser = SecurityUtils.getUsername();
        SysUser user = sysUserService.selectUserByUserName(currentUser);
        String directLeader = ApprovalFlowUtils.resolveDirectLeaderUserName(user, sysDeptService, sysUserService);
        String normalizedApprover = normalizeAssignee(approver, "审批人");
        String normalizedHandler = normalizeAssignee(handler, "办理人");
        ensureDistinctFlowUsers(currentUser, directLeader, normalizedApprover, normalizedHandler);
        entity.setApprovalStatus("pending");
        entity.setStatus("draft");
        entity.setDirectLeader(directLeader);
        entity.setApprover(normalizedApprover);
        entity.setHandler(normalizedHandler);
        entity.setCurrentApprovalNode("directLeader");
        entity.setRemark(appendRemark(entity.getRemark(), "审批申请", buildApplyRemark(entity, remark, directLeader, normalizedApprover, normalizedHandler)));
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
        String currentNode = ApprovalFlowUtils.normalizeNode(entity.getCurrentApprovalNode());
        String currentUser = SecurityUtils.getUsername();
        validateCurrentNodeOperator(entity, currentNode, currentUser);
        if ("agree".equals(action))
        {
            if ("directLeader".equals(currentNode))
            {
                entity.setCurrentApprovalNode("approver");
                entity.setRemark(appendRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark(currentNode, action, remark, entity.getApprover())));
            }
            else if ("approver".equals(currentNode))
            {
                entity.setCurrentApprovalNode("handler");
                entity.setRemark(appendRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark(currentNode, action, remark, entity.getHandler())));
            }
            else if ("handler".equals(currentNode))
            {
                entity.setApprovalStatus("approved");
                entity.setStatus(resolveBorrowStatus(entity));
                entity.setCurrentApprovalNode("finished");
                entity.setRemark(appendRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark(currentNode, action, remark, null)));
            }
            else
            {
                throw new ServiceException("当前审批节点无效");
            }
        }
        else if ("reject".equals(action))
        {
            entity.setApprovalStatus("rejected");
            entity.setStatus("draft");
            entity.setCurrentApprovalNode("rejected");
            entity.setRemark(appendRemark(entity.getRemark(), "审批结果", buildNodeApprovalRemark(currentNode, action, remark, null)));
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
        if (expected != null)
        {
            Date now = DateUtils.getNowDate();
            if (now.after(expected))
            {
                return "overdue";
            }
        }
        return "borrowing";
    }

    private String buildApplyRemark(ContractBorrow entity, String remark, String directLeader, String approver, String handler)
    {
        String currentUser = SecurityUtils.getUsername();
        SysUser user = sysUserService.selectUserByUserName(currentUser);
        String applicantName = user != null ? user.getNickName() : currentUser;
        StringBuilder sb = new StringBuilder();
        sb.append("借阅申请，申请人：").append(applicantName);
        sb.append("，借阅单号：").append(StringUtils.defaultString(entity.getBorrowNo(), "-"));
        sb.append("，直接主管：").append(resolveDisplayName(directLeader));
        sb.append("，审批人：").append(resolveDisplayName(approver));
        sb.append("，办理人：").append(resolveDisplayName(handler));
        if (StringUtils.isNotBlank(remark))
        {
            sb.append("，说明：").append(remark.trim());
        }
        return sb.toString();
    }

    private String buildNodeApprovalRemark(String node, String action, String remark, String nextUser)
    {
        String currentUser = SecurityUtils.getUsername();
        SysUser user = sysUserService.selectUserByUserName(currentUser);
        String approverName = user != null ? user.getNickName() : currentUser;
        String nodeName = getNodeName(node);
        String actionName = "agree".equals(action) ? "审批通过" : "审批驳回";
        StringBuilder sb = new StringBuilder();
        sb.append(nodeName).append(actionName).append("，处理人：").append(approverName);
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

    private String normalizeAssignee(String userName, String roleName)
    {
        if (StringUtils.isBlank(userName))
        {
            throw new ServiceException(roleName + "不能为空");
        }
        SysUser user = sysUserService.selectUserByUserName(userName.trim());
        if (user == null)
        {
            throw new ServiceException(roleName + "不存在：" + userName);
        }
        return user.getUserName();
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

    private void validateCurrentNodeOperator(ContractBorrow entity, String currentNode, String currentUser)
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
        if ("directLeader".equals(node)) return "直接主管";
        if ("approver".equals(node)) return "审批人";
        if ("handler".equals(node)) return "办理人";
        if ("finished".equals(node)) return "已完成";
        if ("rejected".equals(node)) return "已驳回";
        return "审批节点";
    }

    private String resolveDisplayName(String userName)
    {
        return ApprovalFlowUtils.resolveDisplayNameByUserName(userName, sysUserService);
    }

    private String appendRemark(String oldRemark, String tag, String content)
    {
        if (StringUtils.isBlank(content))
        {
            return oldRemark;
        }
        String piece = "[" + tag + "] " + content;
        if (StringUtils.isBlank(oldRemark))
        {
            return piece;
        }
        return oldRemark + "；" + piece;
    }
}
