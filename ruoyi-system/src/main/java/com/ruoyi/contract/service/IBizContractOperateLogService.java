package com.ruoyi.contract.service;

import com.ruoyi.contract.domain.BizContractOperateLog;

import java.util.List;

public interface IBizContractOperateLogService {
    List<BizContractOperateLog> selectByContract(Long contractId, String contractType);
    int insert(BizContractOperateLog log);
    void addSystemLog(Long contractId, String contractType, String action, String detail);
}
