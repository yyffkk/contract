package com.ruoyi.pm.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import com.ruoyi.common.utils.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import com.ruoyi.pm.domain.PmProjectAttachment;
import com.ruoyi.pm.domain.PmProjectContract;
import com.ruoyi.pm.mapper.PmProjectMapper;
import com.ruoyi.pm.domain.PmProject;
import com.ruoyi.pm.service.IPmProjectService;
import com.ruoyi.pm.service.IPmProjectContractService;

/**
 * 项目信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-13
 */
@Service
public class PmProjectServiceImpl implements IPmProjectService 
{
    @Autowired
    private PmProjectMapper pmProjectMapper;

    @Autowired
    private IPmProjectContractService pmProjectContractService;

    /**
     * 查询项目信息
     * 
     * @param projectId 项目信息主键
     * @return 项目信息
     */
    @Override
    public PmProject selectPmProjectByProjectId(Long projectId)
    {
        return pmProjectMapper.selectPmProjectByProjectId(projectId);
    }

    /**
     * 查询项目信息列表
     * 
     * @param pmProject 项目信息
     * @return 项目信息
     */
    @Override
    public List<PmProject> selectPmProjectList(PmProject pmProject)
    {
        return pmProjectMapper.selectPmProjectList(pmProject);
    }

    /**
     * 新增项目信息
     * 
     * @param pmProject 项目信息
     * @return 结果
     */
    @Transactional
    @Override
    public int insertPmProject(PmProject pmProject)
    {
        pmProject.setCreateTime(DateUtils.getNowDate());
        int rows = pmProjectMapper.insertPmProject(pmProject);
        insertPmProjectAttachment(pmProject);
        saveProjectContractRelation(pmProject);
        return rows;
    }

    /**
     * 修改项目信息
     * 
     * @param pmProject 项目信息
     * @return 结果
     */
    @Transactional
    @Override
    public int updatePmProject(PmProject pmProject)
    {
        pmProject.setUpdateTime(DateUtils.getNowDate());
        pmProjectMapper.deletePmProjectAttachmentByAttachmentId(pmProject.getProjectId());
        pmProjectContractService.deletePmProjectContractByProjectId(pmProject.getProjectId());
        insertPmProjectAttachment(pmProject);
        int rows = pmProjectMapper.updatePmProject(pmProject);
        saveProjectContractRelation(pmProject);
        return rows;
    }

    /**
     * 批量删除项目信息
     * 
     * @param projectIds 需要删除的项目信息主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePmProjectByProjectIds(Long[] projectIds)
    {
        pmProjectMapper.deletePmProjectAttachmentByAttachmentIds(projectIds);
        for (Long projectId : projectIds)
        {
            pmProjectContractService.deletePmProjectContractByProjectId(projectId);
        }
        return pmProjectMapper.deletePmProjectByProjectIds(projectIds);
    }

    /**
     * 删除项目信息信息
     * 
     * @param projectId 项目信息主键
     * @return 结果
     */
    @Transactional
    @Override
    public int deletePmProjectByProjectId(Long projectId)
    {
        pmProjectMapper.deletePmProjectAttachmentByAttachmentId(projectId);
        pmProjectContractService.deletePmProjectContractByProjectId(projectId);
        return pmProjectMapper.deletePmProjectByProjectId(projectId);
    }

    /**
     * 新增项目附件信息
     * 
     * @param pmProject 项目信息对象
     */
    public void insertPmProjectAttachment(PmProject pmProject)
    {
        List<PmProjectAttachment> pmProjectAttachmentList = pmProject.getPmProjectAttachmentList();
        Long projectId = pmProject.getProjectId();
        if (StringUtils.isNotNull(pmProjectAttachmentList))
        {
            List<PmProjectAttachment> list = new ArrayList<PmProjectAttachment>();
            for (PmProjectAttachment pmProjectAttachment : pmProjectAttachmentList)
            {
                pmProjectAttachment.setAttachmentId(projectId);
                list.add(pmProjectAttachment);
            }
            if (list.size() > 0)
            {
                pmProjectMapper.batchPmProjectAttachment(list);
            }
        }
    }

    /**
     * 保存项目与合同关系
     */
    private void saveProjectContractRelation(PmProject pmProject)
    {
        if (pmProject == null || pmProject.getProjectId() == null || pmProject.getRelatedContractId() == null)
        {
            return;
        }

        PmProjectContract relation = new PmProjectContract();
        relation.setProjectId(pmProject.getProjectId());
        relation.setContractId(pmProject.getRelatedContractId());
        relation.setContractName(pmProject.getRelatedContractName());
        relation.setContractCode(pmProject.getRelatedContractNumber());
        relation.setContractAmount(pmProject.getRelatedContractAmount());

        String relatedContractType = pmProject.getRelatedContractType();
        if ("income".equalsIgnoreCase(relatedContractType) || "收入".equals(relatedContractType))
        {
            relation.setContractType("1");
        }
        else if ("expense".equalsIgnoreCase(relatedContractType) || "支出".equals(relatedContractType))
        {
            relation.setContractType("2");
        }
        else
        {
            relation.setContractType(relatedContractType);
        }

        relation.setCreateBy(pmProject.getCreateBy());
        pmProjectContractService.insertPmProjectContract(relation);
    }
}
