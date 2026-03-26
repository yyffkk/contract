package com.ruoyi.contract.service;

import java.util.List;
import com.ruoyi.contract.domain.BizContractContent2;

/**
 * 用印审批单Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IBizContractContent2Service 
{
    /**
     * 查询用印审批单
     * 
     * @param id 用印审批单主键
     * @return 用印审批单
     */
    public BizContractContent2 selectBizContractContent2ById(Long id);

    /**
     * 查询用印审批单列表
     * 
     * @param bizContractContent2 用印审批单
     * @return 用印审批单集合
     */
    public List<BizContractContent2> selectBizContractContent2List(BizContractContent2 bizContractContent2);

    /**
     * 新增用印审批单
     * 
     * @param bizContractContent2 用印审批单
     * @return 结果
     */
    public int insertBizContractContent2(BizContractContent2 bizContractContent2);

    /**
     * 修改用印审批单
     * 
     * @param bizContractContent2 用印审批单
     * @return 结果
     */
    public int updateBizContractContent2(BizContractContent2 bizContractContent2);

    /**
     * 批量删除用印审批单
     * 
     * @param ids 需要删除的用印审批单主键集合
     * @return 结果
     */
    public int deleteBizContractContent2ByIds(Long[] ids);

    /**
     * 删除用印审批单信息
     * 
     * @param id 用印审批单主键
     * @return 结果
     */
    public int deleteBizContractContent2ById(Long id);
}
