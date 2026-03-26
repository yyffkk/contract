package com.ruoyi.pm.service;

import java.util.List;
import com.ruoyi.pm.domain.PmProjectPayment;

/**
 * 项目账款关联Service接口
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public interface IPmProjectPaymentService 
{
    /**
     * 查询项目账款关联
     * 
     * @param id 项目账款关联主键
     * @return 项目账款关联
     */
    public PmProjectPayment selectPmProjectPaymentById(Long id);

    /**
     * 查询项目账款关联列表
     * 
     * @param pmProjectPayment 项目账款关联
     * @return 项目账款关联集合
     */
    public List<PmProjectPayment> selectPmProjectPaymentList(PmProjectPayment pmProjectPayment);

    /**
     * 新增项目账款关联
     * 
     * @param pmProjectPayment 项目账款关联
     * @return 结果
     */
    public int insertPmProjectPayment(PmProjectPayment pmProjectPayment);

    /**
     * 修改项目账款关联
     * 
     * @param pmProjectPayment 项目账款关联
     * @return 结果
     */
    public int updatePmProjectPayment(PmProjectPayment pmProjectPayment);

    /**
     * 批量删除项目账款关联
     * 
     * @param ids 需要删除的项目账款关联主键集合
     * @return 结果
     */
    public int deletePmProjectPaymentByIds(Long[] ids);

    /**
     * 删除项目账款关联信息
     * 
     * @param id 项目账款关联主键
     * @return 结果
     */
    public int deletePmProjectPaymentById(Long id);
}
