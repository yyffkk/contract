package com.ruoyi.pm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pm.mapper.PmProjectContractMapper;
import com.ruoyi.pm.domain.PmProjectContract;
import com.ruoyi.pm.service.IPmProjectContractService;

/**
 * 项目合同关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@Service
public class PmProjectContractServiceImpl implements IPmProjectContractService 
{
    @Autowired
    private PmProjectContractMapper pmProjectContractMapper;

    /**
     * 查询项目合同关联
     * 
     * @param id 项目合同关联主键
     * @return 项目合同关联
     */
    @Override
    public PmProjectContract selectPmProjectContractById(Long id)
    {
        return pmProjectContractMapper.selectPmProjectContractById(id);
    }

    /**
     * 查询项目合同关联列表
     * 
     * @param pmProjectContract 项目合同关联
     * @return 项目合同关联
     */
    @Override
    public List<PmProjectContract> selectPmProjectContractList(PmProjectContract pmProjectContract)
    {
        return pmProjectContractMapper.selectPmProjectContractList(pmProjectContract);
    }

    /**
     * 新增项目合同关联
     * 
     * @param pmProjectContract 项目合同关联
     * @return 结果
     */
    @Override
    public int insertPmProjectContract(PmProjectContract pmProjectContract)
    {
        pmProjectContract.setCreateTime(DateUtils.getNowDate());
        return pmProjectContractMapper.insertPmProjectContract(pmProjectContract);
    }

    /**
     * 修改项目合同关联
     * 
     * @param pmProjectContract 项目合同关联
     * @return 结果
     */
    @Override
    public int updatePmProjectContract(PmProjectContract pmProjectContract)
    {
        return pmProjectContractMapper.updatePmProjectContract(pmProjectContract);
    }

    /**
     * 批量删除项目合同关联
     * 
     * @param ids 需要删除的项目合同关联主键
     * @return 结果
     */
    @Override
    public int deletePmProjectContractByIds(Long[] ids)
    {
        return pmProjectContractMapper.deletePmProjectContractByIds(ids);
    }

    /**
     * 删除项目合同关联信息
     * 
     * @param id 项目合同关联主键
     * @return 结果
     */
    @Override
    public int deletePmProjectContractById(Long id)
    {
        return pmProjectContractMapper.deletePmProjectContractById(id);
    }

    /**
     * 按项目ID删除项目合同关联
     *
     * @param projectId 项目ID
     * @return 结果
     */
    @Override
    public int deletePmProjectContractByProjectId(Long projectId)
    {
        return pmProjectContractMapper.deletePmProjectContractByProjectId(projectId);
    }
}
