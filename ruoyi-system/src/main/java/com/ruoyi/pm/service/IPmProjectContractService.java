package com.ruoyi.pm.service;

import java.util.List;
import com.ruoyi.pm.domain.PmProjectContract;

/**
 * 项目合同关联Service接口
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public interface IPmProjectContractService 
{
    /**
     * 查询项目合同关联
     * 
     * @param id 项目合同关联主键
     * @return 项目合同关联
     */
    public PmProjectContract selectPmProjectContractById(Long id);

    /**
     * 查询项目合同关联列表
     * 
     * @param pmProjectContract 项目合同关联
     * @return 项目合同关联集合
     */
    public List<PmProjectContract> selectPmProjectContractList(PmProjectContract pmProjectContract);

    /**
     * 新增项目合同关联
     * 
     * @param pmProjectContract 项目合同关联
     * @return 结果
     */
    public int insertPmProjectContract(PmProjectContract pmProjectContract);

    /**
     * 修改项目合同关联
     * 
     * @param pmProjectContract 项目合同关联
     * @return 结果
     */
    public int updatePmProjectContract(PmProjectContract pmProjectContract);

    /**
     * 批量删除项目合同关联
     * 
     * @param ids 需要删除的项目合同关联主键集合
     * @return 结果
     */
    public int deletePmProjectContractByIds(Long[] ids);

    /**
     * 删除项目合同关联信息
     * 
     * @param id 项目合同关联主键
     * @return 结果
     */
    public int deletePmProjectContractById(Long id);

    /**
     * 按项目ID删除项目合同关联
     *
     * @param projectId 项目ID
     * @return 结果
     */
    public int deletePmProjectContractByProjectId(Long projectId);
}
