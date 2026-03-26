package com.ruoyi.contract.mapper;

import java.util.List;
import com.ruoyi.contract.domain.BizContractContent;

/**
 * 智能合同审批Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-14
 */
public interface BizContractContentMapper 
{
    /**
     * 查询智能合同审批
     * 
     * @param id 智能合同审批主键
     * @return 智能合同审批
     */
    public BizContractContent selectBizContractContentById(Long id);

    /**
     * 查询智能合同审批列表
     * 
     * @param bizContractContent 智能合同审批
     * @return 智能合同审批集合
     */
    public List<BizContractContent> selectBizContractContentList(BizContractContent bizContractContent);

    /**
     * 新增智能合同审批
     * 
     * @param bizContractContent 智能合同审批
     * @return 结果
     */
    public int insertBizContractContent(BizContractContent bizContractContent);

    /**
     * 修改智能合同审批
     * 
     * @param bizContractContent 智能合同审批
     * @return 结果
     */
    public int updateBizContractContent(BizContractContent bizContractContent);

    /**
     * 删除智能合同审批
     * 
     * @param id 智能合同审批主键
     * @return 结果
     */
    public int deleteBizContractContentById(Long id);

    /**
     * 批量删除智能合同审批
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteBizContractContentByIds(Long[] ids);

    /**
     * 获取指定日期的最大流水号
     *
     * @param date 日期（格式：yyyyMMdd）
     * @return 最大流水号
     */
    public Integer selectMaxSerialNumber(String date);


}
