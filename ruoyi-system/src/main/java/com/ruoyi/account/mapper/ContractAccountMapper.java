package com.ruoyi.account.mapper;

import java.util.List;
import com.ruoyi.account.domain.ContractAccount;

/**
 * 账款信息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface ContractAccountMapper 
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
     * 删除账款信息
     * 
     * @param id 账款信息主键
     * @return 结果
     */
    public int deleteContractAccountById(Long id);

    /**
     * 批量删除账款信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteContractAccountByIds(Long[] ids);

    /**
     * 批量新增账款信息
     *
     * @param list 账款信息列表
     * @return 结果
     */
    public int batchInsertContractAccount(List<ContractAccount> list);
}
