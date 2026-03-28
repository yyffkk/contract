package com.ruoyi.borrow.service;

import java.util.List;
import com.ruoyi.borrow.domain.ContractBorrow;

/**
 * 合同借阅Service接口
 * 
 * @author ruoyi
 * @date 2026-03-26
 */
public interface IContractBorrowService 
{
    /**
     * 查询合同借阅
     * 
     * @param id 合同借阅主键
     * @return 合同借阅
     */
    public ContractBorrow selectContractBorrowById(Long id);

    /**
     * 查询合同借阅列表
     * 
     * @param contractBorrow 合同借阅
     * @return 合同借阅集合
     */
    public List<ContractBorrow> selectContractBorrowList(ContractBorrow contractBorrow);

    /**
     * 新增合同借阅
     * 
     * @param contractBorrow 合同借阅
     * @return 结果
     */
    public int insertContractBorrow(ContractBorrow contractBorrow);

    /**
     * 修改合同借阅
     * 
     * @param contractBorrow 合同借阅
     * @return 结果
     */
    public int updateContractBorrow(ContractBorrow contractBorrow);

    /**
     * 批量删除合同借阅
     * 
     * @param ids 需要删除的合同借阅主键集合
     * @return 结果
     */
    public int deleteContractBorrowByIds(Long[] ids);

    /**
     * 删除合同借阅信息
     * 
     * @param id 合同借阅主键
     * @return 结果
     */
    public int deleteContractBorrowById(Long id);

    /**
     * 提交借阅审批
     *
     * @param id 借阅ID
     * @param approver 审批人
     * @param handler 办理人
     * @param remark 审批说明
     * @return 结果
     */
    public int submitApproval(Long id, String approver, String handler, String remark);

    /**
     * 审批借阅申请
     *
     * @param id 借阅ID
     * @param action agree/reject
     * @param remark 审批意见
     * @return 结果
     */
    public int handleApproval(Long id, String action, String remark);

    /**
     * 登记归还
     *
     * @param id 借阅ID
     * @param remark 归还备注
     * @return 结果
     */
    public int markReturned(Long id, String remark);
}
