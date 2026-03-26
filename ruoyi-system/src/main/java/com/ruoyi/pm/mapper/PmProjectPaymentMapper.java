package com.ruoyi.pm.mapper;

import java.util.List;
import com.ruoyi.pm.domain.PmProjectPayment;

/**
 * 项目账款关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public interface PmProjectPaymentMapper 
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
     * 删除项目账款关联
     * 
     * @param id 项目账款关联主键
     * @return 结果
     */
    public int deletePmProjectPaymentById(Long id);

    /**
     * 批量删除项目账款关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmProjectPaymentByIds(Long[] ids);
}
