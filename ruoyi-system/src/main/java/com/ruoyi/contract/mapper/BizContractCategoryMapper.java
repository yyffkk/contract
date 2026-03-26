package com.ruoyi.contract.mapper;

import java.util.List;
import com.ruoyi.contract.domain.BizContractCategory;

/**
 * 合同分类Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public interface BizContractCategoryMapper 
{
    /**
     * 查询合同分类
     * 
     * @param categoryId 合同分类主键
     * @return 合同分类
     */
    public BizContractCategory selectBizContractCategoryByCategoryId(Long categoryId);

    /**
     * 查询合同分类列表
     * 
     * @param bizContractCategory 合同分类
     * @return 合同分类集合
     */
    public List<BizContractCategory> selectBizContractCategoryList(BizContractCategory bizContractCategory);

    /**
     * 新增合同分类
     * 
     * @param bizContractCategory 合同分类
     * @return 结果
     */
    public int insertBizContractCategory(BizContractCategory bizContractCategory);

    /**
     * 修改合同分类
     * 
     * @param bizContractCategory 合同分类
     * @return 结果
     */
    public int updateBizContractCategory(BizContractCategory bizContractCategory);

    /**
     * 删除合同分类
     * 
     * @param categoryId 合同分类主键
     * @return 结果
     */
    public int deleteBizContractCategoryByCategoryId(Long categoryId);

    /**
     * 批量删除合同分类
     * 
     * @param categoryIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizContractCategoryByCategoryIds(Long[] categoryIds);
}
