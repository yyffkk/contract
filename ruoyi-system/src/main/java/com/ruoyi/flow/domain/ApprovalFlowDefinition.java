package com.ruoyi.flow.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ApprovalFlowDefinition implements Serializable {
    private static final long serialVersionUID = 1L;

    private String businessType;
    private String businessName;
    private List<ApprovalFlowNode> nodes = new ArrayList<>();

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public List<ApprovalFlowNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<ApprovalFlowNode> nodes) {
        this.nodes = nodes;
    }

    public static class ApprovalFlowNode implements Serializable {
        private static final long serialVersionUID = 1L;

        private String nodeKey;
        private String nodeName;
        private String assigneeType;
        private String assigneeValue;
        private String assigneeLabel;

        public String getNodeKey() {
            return nodeKey;
        }

        public void setNodeKey(String nodeKey) {
            this.nodeKey = nodeKey;
        }

        public String getNodeName() {
            return nodeName;
        }

        public void setNodeName(String nodeName) {
            this.nodeName = nodeName;
        }

        public String getAssigneeType() {
            return assigneeType;
        }

        public void setAssigneeType(String assigneeType) {
            this.assigneeType = assigneeType;
        }

        public String getAssigneeValue() {
            return assigneeValue;
        }

        public void setAssigneeValue(String assigneeValue) {
            this.assigneeValue = assigneeValue;
        }

        public String getAssigneeLabel() {
            return assigneeLabel;
        }

        public void setAssigneeLabel(String assigneeLabel) {
            this.assigneeLabel = assigneeLabel;
        }
    }
}
