package com.ruoyi.pm.mapper;

import java.util.List;
import com.ruoyi.pm.domain.PmProjectInvoice;

/**
 * 项目发票关联Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public interface PmProjectInvoiceMapper 
{
    /**
     * 查询项目发票关联
     * 
     * @param id 项目发票关联主键
     * @return 项目发票关联
     */
    public PmProjectInvoice selectPmProjectInvoiceById(Long id);

    /**
     * 查询项目发票关联列表
     * 
     * @param pmProjectInvoice 项目发票关联
     * @return 项目发票关联集合
     */
    public List<PmProjectInvoice> selectPmProjectInvoiceList(PmProjectInvoice pmProjectInvoice);

    /**
     * 新增项目发票关联
     * 
     * @param pmProjectInvoice 项目发票关联
     * @return 结果
     */
    public int insertPmProjectInvoice(PmProjectInvoice pmProjectInvoice);

    /**
     * 修改项目发票关联
     * 
     * @param pmProjectInvoice 项目发票关联
     * @return 结果
     */
    public int updatePmProjectInvoice(PmProjectInvoice pmProjectInvoice);

    /**
     * 删除项目发票关联
     * 
     * @param id 项目发票关联主键
     * @return 结果
     */
    public int deletePmProjectInvoiceById(Long id);

    /**
     * 批量删除项目发票关联
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmProjectInvoiceByIds(Long[] ids);
}
