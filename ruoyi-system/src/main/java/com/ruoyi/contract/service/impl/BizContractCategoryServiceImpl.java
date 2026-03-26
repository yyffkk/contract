package com.ruoyi.contract.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.contract.mapper.BizContractCategoryMapper;
import com.ruoyi.contract.domain.BizContractCategory;
import com.ruoyi.contract.service.IBizContractCategoryService;

/**
 * 合同分类Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@Service
public class BizContractCategoryServiceImpl implements IBizContractCategoryService 
{
    @Autowired
    private BizContractCategoryMapper bizContractCategoryMapper;

    /**
     * 查询合同分类
     * 
     * @param categoryId 合同分类主键
     * @return 合同分类
     */
    @Override
    public BizContractCategory selectBizContractCategoryByCategoryId(Long categoryId)
    {
        return bizContractCategoryMapper.selectBizContractCategoryByCategoryId(categoryId);
    }

    /**
     * 查询合同分类列表
     * 
     * @param bizContractCategory 合同分类
     * @return 合同分类
     */
    @Override
    public List<BizContractCategory> selectBizContractCategoryList(BizContractCategory bizContractCategory)
    {
        return bizContractCategoryMapper.selectBizContractCategoryList(bizContractCategory);
    }

    /**
     * 新增合同分类
     * 
     * @param bizContractCategory 合同分类
     * @return 结果
     */
    @Override
    public int insertBizContractCategory(BizContractCategory bizContractCategory)
    {
        bizContractCategory.setCreateTime(DateUtils.getNowDate());
        return bizContractCategoryMapper.insertBizContractCategory(bizContractCategory);
    }

    /**
     * 修改合同分类
     * 
     * @param bizContractCategory 合同分类
     * @return 结果
     */
    @Override
    public int updateBizContractCategory(BizContractCategory bizContractCategory)
    {
        bizContractCategory.setUpdateTime(DateUtils.getNowDate());
        return bizContractCategoryMapper.updateBizContractCategory(bizContractCategory);
    }

    /**
     * 批量删除合同分类
     * 
     * @param categoryIds 需要删除的合同分类主键
     * @return 结果
     */
    @Override
    public int deleteBizContractCategoryByCategoryIds(Long[] categoryIds)
    {
        return bizContractCategoryMapper.deleteBizContractCategoryByCategoryIds(categoryIds);
    }

    /**
     * 删除合同分类信息
     * 
     * @param categoryId 合同分类主键
     * @return 结果
     */
    @Override
    public int deleteBizContractCategoryByCategoryId(Long categoryId)
    {
        return bizContractCategoryMapper.deleteBizContractCategoryByCategoryId(categoryId);
    }
}
