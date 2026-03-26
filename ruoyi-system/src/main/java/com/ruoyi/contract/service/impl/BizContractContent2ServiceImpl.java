package com.ruoyi.contract.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.contract.mapper.BizContractContent2Mapper;
import com.ruoyi.contract.domain.BizContractContent2;
import com.ruoyi.contract.service.IBizContractContent2Service;

/**
 * 用印审批单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class BizContractContent2ServiceImpl implements IBizContractContent2Service 
{
    @Autowired
    private BizContractContent2Mapper bizContractContent2Mapper;

    /**
     * 查询用印审批单
     * 
     * @param id 用印审批单主键
     * @return 用印审批单
     */
    @Override
    public BizContractContent2 selectBizContractContent2ById(Long id)
    {
        return bizContractContent2Mapper.selectBizContractContent2ById(id);
    }

    /**
     * 查询用印审批单列表
     * 
     * @param bizContractContent2 用印审批单
     * @return 用印审批单
     */
    @Override
    public List<BizContractContent2> selectBizContractContent2List(BizContractContent2 bizContractContent2)
    {
        return bizContractContent2Mapper.selectBizContractContent2List(bizContractContent2);
    }

    /**
     * 新增用印审批单
     * 
     * @param bizContractContent2 用印审批单
     * @return 结果
     */
    @Override
    public int insertBizContractContent2(BizContractContent2 bizContractContent2)
    {
        bizContractContent2.setCreateTime(DateUtils.getNowDate());
        return bizContractContent2Mapper.insertBizContractContent2(bizContractContent2);
    }

    /**
     * 修改用印审批单
     * 
     * @param bizContractContent2 用印审批单
     * @return 结果
     */
    @Override
    public int updateBizContractContent2(BizContractContent2 bizContractContent2)
    {
        bizContractContent2.setUpdateTime(DateUtils.getNowDate());
        return bizContractContent2Mapper.updateBizContractContent2(bizContractContent2);
    }

    /**
     * 批量删除用印审批单
     * 
     * @param ids 需要删除的用印审批单主键
     * @return 结果
     */
    @Override
    public int deleteBizContractContent2ByIds(Long[] ids)
    {
        return bizContractContent2Mapper.deleteBizContractContent2ByIds(ids);
    }

    /**
     * 删除用印审批单信息
     * 
     * @param id 用印审批单主键
     * @return 结果
     */
    @Override
    public int deleteBizContractContent2ById(Long id)
    {
        return bizContractContent2Mapper.deleteBizContractContent2ById(id);
    }
}
