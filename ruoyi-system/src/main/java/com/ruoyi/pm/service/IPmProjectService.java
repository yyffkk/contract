package com.ruoyi.pm.service;

import java.util.List;
import com.ruoyi.pm.domain.PmProject;

/**
 * 项目信息Service接口
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
public interface IPmProjectService 
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
     * 批量删除项目信息
     * 
     * @param projectIds 需要删除的项目信息主键集合
     * @return 结果
     */
    public int deletePmProjectByProjectIds(Long[] projectIds);

    /**
     * 删除项目信息信息
     * 
     * @param projectId 项目信息主键
     * @return 结果
     */
    public int deletePmProjectByProjectId(Long projectId);
}
