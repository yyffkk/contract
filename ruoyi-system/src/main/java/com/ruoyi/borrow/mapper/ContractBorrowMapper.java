package com.ruoyi.borrow.mapper;

import java.util.List;
import com.ruoyi.borrow.domain.ContractBorrow;

/**
 * 合同借阅Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-26
 */
public interface ContractBorrowMapper 
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
     * 删除合同借阅
     * 
     * @param id 合同借阅主键
     * @return 结果
     */
    public int deleteContractBorrowById(Long id);

    /**
     * 批量删除合同借阅
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteContractBorrowByIds(Long[] ids);
}
