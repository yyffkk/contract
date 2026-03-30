package com.ruoyi.web.controller.flow;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.flow.domain.ApprovalFlowDefinition;
import com.ruoyi.flow.service.IApprovalFlowConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/approval/flow")
public class ApprovalFlowController extends BaseController {
    @Autowired
    private IApprovalFlowConfigService approvalFlowConfigService;

    @GetMapping("/{businessType}")
    public AjaxResult getInfo(@PathVariable String businessType) {
        return success(approvalFlowConfigService.getFlow(businessType));
    }

    @Log(title = "审批流设置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult save(@RequestBody ApprovalFlowDefinition definition) {
        return success(approvalFlowConfigService.saveFlow(definition, getUsername()));
    }
}
