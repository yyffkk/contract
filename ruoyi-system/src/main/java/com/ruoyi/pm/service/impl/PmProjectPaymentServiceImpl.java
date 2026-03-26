package com.ruoyi.pm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pm.mapper.PmProjectPaymentMapper;
import com.ruoyi.pm.domain.PmProjectPayment;
import com.ruoyi.pm.service.IPmProjectPaymentService;

/**
 * 项目账款关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@Service
public class PmProjectPaymentServiceImpl implements IPmProjectPaymentService 
{
    @Autowired
    private PmProjectPaymentMapper pmProjectPaymentMapper;

    /**
     * 查询项目账款关联
     * 
     * @param id 项目账款关联主键
     * @return 项目账款关联
     */
    @Override
    public PmProjectPayment selectPmProjectPaymentById(Long id)
    {
        return pmProjectPaymentMapper.selectPmProjectPaymentById(id);
    }

    /**
     * 查询项目账款关联列表
     * 
     * @param pmProjectPayment 项目账款关联
     * @return 项目账款关联
     */
    @Override
    public List<PmProjectPayment> selectPmProjectPaymentList(PmProjectPayment pmProjectPayment)
    {
        return pmProjectPaymentMapper.selectPmProjectPaymentList(pmProjectPayment);
    }

    /**
     * 新增项目账款关联
     * 
     * @param pmProjectPayment 项目账款关联
     * @return 结果
     */
    @Override
    public int insertPmProjectPayment(PmProjectPayment pmProjectPayment)
    {
        pmProjectPayment.setCreateTime(DateUtils.getNowDate());
        return pmProjectPaymentMapper.insertPmProjectPayment(pmProjectPayment);
    }

    /**
     * 修改项目账款关联
     * 
     * @param pmProjectPayment 项目账款关联
     * @return 结果
     */
    @Override
    public int updatePmProjectPayment(PmProjectPayment pmProjectPayment)
    {
        return pmProjectPaymentMapper.updatePmProjectPayment(pmProjectPayment);
    }

    /**
     * 批量删除项目账款关联
     * 
     * @param ids 需要删除的项目账款关联主键
     * @return 结果
     */
    @Override
    public int deletePmProjectPaymentByIds(Long[] ids)
    {
        return pmProjectPaymentMapper.deletePmProjectPaymentByIds(ids);
    }

    /**
     * 删除项目账款关联信息
     * 
     * @param id 项目账款关联主键
     * @return 结果
     */
    @Override
    public int deletePmProjectPaymentById(Long id)
    {
        return pmProjectPaymentMapper.deletePmProjectPaymentById(id);
    }
}
