package com.ruoyi.flow.service;

import com.ruoyi.flow.domain.ApprovalFlowDefinition;

import java.util.List;

public interface IApprovalFlowConfigService {
    ApprovalFlowDefinition getFlow(String businessType);

    ApprovalFlowDefinition saveFlow(ApprovalFlowDefinition definition, String operator);

    List<String> resolveAssignees(String businessType);

    String getNodeName(String businessType, String nodeKey);
}
