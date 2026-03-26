package com.ruoyi.contract.service.impl;

import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.contract.domain.BizContractOperateLog;
import com.ruoyi.contract.mapper.BizContractOperateLogMapper;
import com.ruoyi.contract.service.IBizContractOperateLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BizContractOperateLogServiceImpl implements IBizContractOperateLogService {

    @Autowired
    private BizContractOperateLogMapper operateLogMapper;

    @Override
    public List<BizContractOperateLog> selectByContract(Long contractId, String contractType) {
        return operateLogMapper.selectByContract(contractId, contractType);
    }

    @Override
    public int insert(BizContractOperateLog log) {
        if (log.getOperateTime() == null) {
            log.setOperateTime(DateUtils.getNowDate());
        }
        if (log.getOperator() == null || log.getOperator().trim().isEmpty()) {
            log.setOperator(SecurityUtils.getUsername());
        }
        log.setCreateBy(log.getOperator());
        log.setUpdateBy(log.getOperator());
        return operateLogMapper.insert(log);
    }

    @Override
    public void addSystemLog(Long contractId, String contractType, String action, String detail) {
        BizContractOperateLog log = new BizContractOperateLog();
        log.setContractId(contractId);
        log.setContractType(contractType);
        log.setAction(action);
        log.setDetail(detail);
        log.setOperator(SecurityUtils.getUsername());
        insert(log);
    }
}
