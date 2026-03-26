package com.ruoyi.account.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.ruoyi.account.domain.ContractAccount;

/**
 * 账款信息Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IContractAccountService 
{
    /**
     * 查询账款信息
     * 
     * @param id 账款信息主键
     * @return 账款信息
     */
    public ContractAccount selectContractAccountById(Long id);

    /**
     * 查询账款信息列表
     * 
     * @param contractAccount 账款信息
     * @return 账款信息集合
     */
    public List<ContractAccount> selectContractAccountList(ContractAccount contractAccount);

    /**
     * 新增账款信息
     * 
     * @param contractAccount 账款信息
     * @return 结果
     */
    public int insertContractAccount(ContractAccount contractAccount);

    /**
     * 修改账款信息
     * 
     * @param contractAccount 账款信息
     * @return 结果
     */
    public int updateContractAccount(ContractAccount contractAccount);

    /**
     * 批量删除账款信息
     * 
     * @param ids 需要删除的账款信息主键集合
     * @return 结果
     */
    public int deleteContractAccountByIds(Long[] ids);

    /**
     * 删除账款信息信息
     * 
     * @param id 账款信息主键
     * @return 结果
     */
    public int deleteContractAccountById(Long id);

    /**
     * 批量导入账款信息
     *
     * @param file Excel 文件
     * @return 导入数量
     */
    public int importData(MultipartFile file);

    /**
     * 提交收/付款审批
     *
     * @param id 账款ID
     * @param applyType 申请类型 receive/pay
     * @param remark 审批说明
     * @return 结果
     */
    public int submitApproval(Long id, String applyType, String remark);

    /**
     * 审批账款申请
     *
     * @param id 账款ID
     * @param action 审批动作 agree/reject
     * @param remark 审批意见
     * @return 结果
     */
    public int handleApproval(Long id, String action, String remark);
}
