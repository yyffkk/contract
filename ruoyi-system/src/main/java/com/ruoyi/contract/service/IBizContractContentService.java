package com.ruoyi.contract.service;

import java.util.List;
import com.ruoyi.contract.domain.BizContractContent;
import com.ruoyi.contract.domain.BizContractOperateLog;
import org.springframework.web.multipart.MultipartFile;

/**
 * 智能合同审批Service接口
 *
 * @author ruoyi
 * @date 2026-03-14
 */
public interface IBizContractContentService
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
     * 批量删除智能合同审批
     *
     * @param ids 需要删除的智能合同审批主键集合
     * @return 结果
     */
    public int deleteBizContractContentByIds(Long[] ids);

    /**
     * 删除智能合同审批信息
     *
     * @param id 智能合同审批主键
     * @return 结果
     */
    public int deleteBizContractContentById(Long id);

    void importFromExcel(MultipartFile file);

    void handleApproval(Long contractId, String action);

    void signContract(Long contractId, Boolean autoArchive);

    void finishContract(Long contractId, String result, String finalStatus);

    List<BizContractOperateLog> selectOperateLogs(Long contractId);

    void addOperateLog(BizContractOperateLog log);
}
