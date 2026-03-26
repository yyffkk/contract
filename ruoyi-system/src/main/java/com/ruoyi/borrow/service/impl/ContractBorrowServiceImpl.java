package com.ruoyi.borrow.service.impl;

import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.borrow.mapper.ContractBorrowMapper;
import com.ruoyi.borrow.domain.ContractBorrow;
import com.ruoyi.borrow.service.IContractBorrowService;

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
    public int submitApproval(Long id, String remark)
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
        entity.setApprovalStatus("pending");
        entity.setStatus("draft");
        entity.setRemark(appendRemark(entity.getRemark(), "审批申请", StringUtils.defaultIfBlank(remark, "提交借阅审批")));
        entity.setUpdateBy(SecurityUtils.getUsername());
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
        if ("agree".equals(action))
        {
            entity.setApprovalStatus("approved");
            entity.setStatus(resolveBorrowStatus(entity));
            entity.setRemark(appendRemark(entity.getRemark(), "审批结果", StringUtils.defaultIfBlank(remark, "审批通过")));
        }
        else if ("reject".equals(action))
        {
            entity.setApprovalStatus("rejected");
            entity.setStatus("draft");
            entity.setRemark(appendRemark(entity.getRemark(), "审批结果", StringUtils.defaultIfBlank(remark, "审批驳回")));
        }
        else
        {
            throw new ServiceException("无效的审批动作");
        }
        entity.setUpdateBy(SecurityUtils.getUsername());
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
