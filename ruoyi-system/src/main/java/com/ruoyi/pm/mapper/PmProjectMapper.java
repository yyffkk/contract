package com.ruoyi.pm.mapper;

import java.util.List;
import com.ruoyi.pm.domain.PmProject;
import com.ruoyi.pm.domain.PmProjectAttachment;

/**
 * 项目信息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public interface PmProjectMapper 
{
    /**
     * 查询项目信息
     * 
     * @param projectId 项目信息主键
     * @return 项目信息
     */
    public PmProject selectPmProjectByProjectId(Long projectId);

    /**
     * 查询项目信息列表
     * 
     * @param pmProject 项目信息
     * @return 项目信息集合
     */
    public List<PmProject> selectPmProjectList(PmProject pmProject);

    /**
     * 新增项目信息
     * 
     * @param pmProject 项目信息
     * @return 结果
     */
    public int insertPmProject(PmProject pmProject);

    /**
     * 修改项目信息
     * 
     * @param pmProject 项目信息
     * @return 结果
     */
    public int updatePmProject(PmProject pmProject);

    /**
     * 删除项目信息
     * 
     * @param projectId 项目信息主键
     * @return 结果
     */
    public int deletePmProjectByProjectId(Long projectId);

    /**
     * 批量删除项目信息
     * 
     * @param projectIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmProjectByProjectIds(Long[] projectIds);

    /**
     * 批量删除项目附件
     * 
     * @param projectIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deletePmProjectAttachmentByAttachmentIds(Long[] projectIds);
    
    /**
     * 批量新增项目附件
     * 
     * @param pmProjectAttachmentList 项目附件列表
     * @return 结果
     */
    public int batchPmProjectAttachment(List<PmProjectAttachment> pmProjectAttachmentList);
    

    /**
     * 通过项目信息主键删除项目附件信息
     * 
     * @param projectId 项目信息ID
     * @return 结果
     */
    public int deletePmProjectAttachmentByAttachmentId(Long projectId);
}
