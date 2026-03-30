package com.ruoyi.flow.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.flow.domain.ApprovalFlowDefinition;
import com.ruoyi.flow.domain.ApprovalFlowDefinition.ApprovalFlowNode;
import com.ruoyi.flow.service.IApprovalFlowConfigService;
import com.ruoyi.system.domain.SysConfig;
import com.ruoyi.system.mapper.SysConfigMapper;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ApprovalFlowConfigServiceImpl implements IApprovalFlowConfigService {
    private static final Map<String, String> BUSINESS_NAME_MAP = new HashMap<>();
    private static final List<String> SLOT_KEYS = Arrays.asList("node1", "node2", "node3");

    static {
        BUSINESS_NAME_MAP.put("invoice", "发票管理");
        BUSINESS_NAME_MAP.put("borrow", "借阅管理");
        BUSINESS_NAME_MAP.put("account", "账款管理");
    }

    @Autowired
    private SysConfigMapper sysConfigMapper;

    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public ApprovalFlowDefinition getFlow(String businessType) {
        validateBusinessType(businessType);
        SysConfig config = sysConfigMapper.checkConfigKeyUnique(buildConfigKey(businessType));
        if (config == null || StringUtils.isBlank(config.getConfigValue())) {
            return buildDefaultDefinition(businessType);
        }
        try {
            ApprovalFlowDefinition definition = objectMapper.readValue(config.getConfigValue(), new TypeReference<ApprovalFlowDefinition>() {});
            normalizeDefinition(definition, businessType);
            return definition;
        } catch (Exception e) {
            throw new ServiceException("审批流配置解析失败，请检查配置内容");
        }
    }

    @Override
    public ApprovalFlowDefinition saveFlow(ApprovalFlowDefinition definition, String operator) {
        if (definition == null) {
            throw new ServiceException("审批流配置不能为空");
        }
        String businessType = StringUtils.trimToEmpty(definition.getBusinessType());
        validateBusinessType(businessType);
        normalizeDefinition(definition, businessType);
        validateNodes(definition.getNodes());
        String configValue = toJson(definition);
        String configKey = buildConfigKey(businessType);
        SysConfig existed = sysConfigMapper.checkConfigKeyUnique(configKey);
        if (existed == null) {
            SysConfig config = new SysConfig();
            config.setConfigName(BUSINESS_NAME_MAP.get(businessType) + "审批流设置");
            config.setConfigKey(configKey);
            config.setConfigValue(configValue);
            config.setConfigType("N");
            config.setCreateBy(operator);
            config.setRemark("页面审批流设置生成");
            sysConfigMapper.insertConfig(config);
        } else {
            existed.setConfigName(BUSINESS_NAME_MAP.get(businessType) + "审批流设置");
            existed.setConfigValue(configValue);
            existed.setConfigType("N");
            existed.setUpdateBy(operator);
            existed.setRemark("页面审批流设置更新");
            sysConfigMapper.updateConfig(existed);
        }
        return definition;
    }

    @Override
    public List<String> resolveAssignees(String businessType) {
        ApprovalFlowDefinition definition = getFlow(businessType);
        validateNodes(definition.getNodes());
        List<String> assignees = new ArrayList<>();
        for (ApprovalFlowNode node : definition.getNodes()) {
            assignees.add(resolveNodeAssignee(node));
        }
        return assignees;
    }

    @Override
    public String getNodeName(String businessType, String nodeKey) {
        if (StringUtils.isBlank(nodeKey)) {
            return "-";
        }
        ApprovalFlowDefinition definition = getFlow(businessType);
        for (ApprovalFlowNode node : definition.getNodes()) {
            if (StringUtils.equals(node.getNodeKey(), nodeKey)) {
                return StringUtils.defaultIfBlank(node.getNodeName(), nodeKey);
            }
        }
        if (StringUtils.equalsAny(nodeKey, "finished", "done")) {
            return "已完成";
        }
        if (StringUtils.equals(nodeKey, "rejected")) {
            return "已驳回";
        }
        return nodeKey;
    }

    private String resolveNodeAssignee(ApprovalFlowNode node) {
        if (StringUtils.equals(node.getAssigneeType(), "user")) {
            SysUser user = sysUserService.selectUserByUserName(node.getAssigneeValue());
            if (user == null) {
                throw new ServiceException("审批节点【" + node.getNodeName() + "】指定用户不存在：" + node.getAssigneeValue());
            }
            return user.getUserName();
        }
        if (StringUtils.equals(node.getAssigneeType(), "role")) {
            Long roleId;
            try {
                roleId = Long.valueOf(node.getAssigneeValue());
            } catch (Exception e) {
                throw new ServiceException("审批节点【" + node.getNodeName() + "】角色配置无效");
            }
            SysRole role = sysRoleService.selectRoleById(roleId);
            if (role == null) {
                throw new ServiceException("审批节点【" + node.getNodeName() + "】角色不存在");
            }
            SysUser query = new SysUser();
            query.setRoleId(roleId);
            query.setStatus("0");
            List<SysUser> users = sysUserService.selectUserList(query);
            if (users == null || users.isEmpty()) {
                throw new ServiceException("审批节点【" + node.getNodeName() + "】对应角色下没有可用用户");
            }
            return users.get(0).getUserName();
        }
        throw new ServiceException("审批节点【" + node.getNodeName() + "】处理人类型不支持");
    }

    private void validateBusinessType(String businessType) {
        if (!BUSINESS_NAME_MAP.containsKey(businessType)) {
            throw new ServiceException("不支持的审批业务类型：" + businessType);
        }
    }

    private ApprovalFlowDefinition buildDefaultDefinition(String businessType) {
        ApprovalFlowDefinition definition = new ApprovalFlowDefinition();
        definition.setBusinessType(businessType);
        definition.setBusinessName(BUSINESS_NAME_MAP.get(businessType));
        definition.setNodes(new ArrayList<>());
        return definition;
    }

    private void normalizeDefinition(ApprovalFlowDefinition definition, String businessType) {
        if (definition == null) {
            definition = new ApprovalFlowDefinition();
        }
        definition.setBusinessType(businessType);
        definition.setBusinessName(BUSINESS_NAME_MAP.get(businessType));
        if (definition.getNodes() == null) {
            definition.setNodes(new ArrayList<>());
        }
        for (int i = 0; i < definition.getNodes().size(); i++) {
            ApprovalFlowNode node = definition.getNodes().get(i);
            node.setNodeKey(SLOT_KEYS.get(i));
            node.setNodeName(StringUtils.defaultIfBlank(node.getNodeName(), "审批节点" + (i + 1)));
            node.setAssigneeType(StringUtils.trimToEmpty(node.getAssigneeType()));
            node.setAssigneeValue(StringUtils.trimToEmpty(node.getAssigneeValue()));
            node.setAssigneeLabel(StringUtils.defaultIfBlank(node.getAssigneeLabel(), node.getAssigneeValue()));
        }
    }

    private void validateNodes(List<ApprovalFlowNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new ServiceException("请至少配置一个审批节点");
        }
        if (nodes.size() > 3) {
            throw new ServiceException("当前版本最多支持配置 3 个审批节点");
        }
        for (ApprovalFlowNode node : nodes) {
            if (StringUtils.isBlank(node.getNodeName())) {
                throw new ServiceException("审批节点名称不能为空");
            }
            if (!StringUtils.equalsAny(node.getAssigneeType(), "user", "role")) {
                throw new ServiceException("审批节点【" + node.getNodeName() + "】必须选择用户或角色");
            }
            if (StringUtils.isBlank(node.getAssigneeValue())) {
                throw new ServiceException("审批节点【" + node.getNodeName() + "】必须指定处理人");
            }
        }
    }

    private String buildConfigKey(String businessType) {
        return "approval.flow." + businessType;
    }

    private String toJson(ApprovalFlowDefinition definition) {
        try {
            return objectMapper.writeValueAsString(definition);
        } catch (JsonProcessingException e) {
            throw new ServiceException("审批流配置保存失败");
        }
    }
}
