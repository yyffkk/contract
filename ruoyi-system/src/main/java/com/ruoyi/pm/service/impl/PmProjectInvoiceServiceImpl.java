package com.ruoyi.pm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.pm.mapper.PmProjectInvoiceMapper;
import com.ruoyi.pm.domain.PmProjectInvoice;
import com.ruoyi.pm.service.IPmProjectInvoiceService;

/**
 * 项目发票关联Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@Service
public class PmProjectInvoiceServiceImpl implements IPmProjectInvoiceService 
{
    @Autowired
    private PmProjectInvoiceMapper pmProjectInvoiceMapper;

    /**
     * 查询项目发票关联
     * 
     * @param id 项目发票关联主键
     * @return 项目发票关联
     */
    @Override
    public PmProjectInvoice selectPmProjectInvoiceById(Long id)
    {
        return pmProjectInvoiceMapper.selectPmProjectInvoiceById(id);
    }

    /**
     * 查询项目发票关联列表
     * 
     * @param pmProjectInvoice 项目发票关联
     * @return 项目发票关联
     */
    @Override
    public List<PmProjectInvoice> selectPmProjectInvoiceList(PmProjectInvoice pmProjectInvoice)
    {
        return pmProjectInvoiceMapper.selectPmProjectInvoiceList(pmProjectInvoice);
    }

    /**
     * 新增项目发票关联
     * 
     * @param pmProjectInvoice 项目发票关联
     * @return 结果
     */
    @Override
    public int insertPmProjectInvoice(PmProjectInvoice pmProjectInvoice)
    {
        pmProjectInvoice.setCreateTime(DateUtils.getNowDate());
        return pmProjectInvoiceMapper.insertPmProjectInvoice(pmProjectInvoice);
    }

    /**
     * 修改项目发票关联
     * 
     * @param pmProjectInvoice 项目发票关联
     * @return 结果
     */
    @Override
    public int updatePmProjectInvoice(PmProjectInvoice pmProjectInvoice)
    {
        return pmProjectInvoiceMapper.updatePmProjectInvoice(pmProjectInvoice);
    }

    /**
     * 批量删除项目发票关联
     * 
     * @param ids 需要删除的项目发票关联主键
     * @return 结果
     */
    @Override
    public int deletePmProjectInvoiceByIds(Long[] ids)
    {
        return pmProjectInvoiceMapper.deletePmProjectInvoiceByIds(ids);
    }

    /**
     * 删除项目发票关联信息
     * 
     * @param id 项目发票关联主键
     * @return 结果
     */
    @Override
    public int deletePmProjectInvoiceById(Long id)
    {
        return pmProjectInvoiceMapper.deletePmProjectInvoiceById(id);
    }
}
