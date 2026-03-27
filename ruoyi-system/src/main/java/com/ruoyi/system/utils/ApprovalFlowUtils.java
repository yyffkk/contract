package com.ruoyi.system.utils;

import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.ServiceException;
import com.ruoyi.system.service.ISysDeptService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * 审批流辅助工具
 */
public final class ApprovalFlowUtils {
    private ApprovalFlowUtils() {}

    public static String resolveDirectLeaderUserName(SysUser currentUser, ISysDeptService sysDeptService, ISysUserService sysUserService) {
        if (currentUser == null) {
            throw new ServiceException("当前登录用户不存在，无法发起审批");
        }
        if (currentUser.getDeptId() == null) {
            throw new ServiceException("当前用户未绑定部门，无法确定直接主管");
        }
        SysDept dept = sysDeptService.selectDeptById(currentUser.getDeptId());
        if (dept == null || StringUtils.isBlank(dept.getLeader())) {
            throw new ServiceException("当前用户所在部门未配置负责人，无法确定直接主管");
        }
        String leader = dept.getLeader().trim();
        if (leader.equals(currentUser.getUserName()) || leader.equals(currentUser.getNickName())) {
            throw new ServiceException("部门负责人不能是自己，请调整部门负责人配置");
        }
        SysUser exact = sysUserService.selectUserByUserName(leader);
        if (exact != null) {
            return exact.getUserName();
        }
        SysUser query = new SysUser();
        query.setNickName(leader);
        List<SysUser> users = sysUserService.selectUserList(query);
        if (users == null || users.isEmpty()) {
            throw new ServiceException("未找到直接主管对应的系统用户：" + leader);
        }
        if (users.size() > 1) {
            throw new ServiceException("直接主管【" + leader + "】匹配到多个系统用户，请改为唯一用户名");
        }
        return users.get(0).getUserName();
    }

    public static String resolveDisplayNameByUserName(String userName, ISysUserService sysUserService) {
        if (StringUtils.isBlank(userName)) {
            return userName;
        }
        SysUser user = sysUserService.selectUserByUserName(userName);
        if (user != null && StringUtils.isNotBlank(user.getNickName())) {
            return user.getNickName();
        }
        return userName;
    }

    public static String normalizeNode(String node) {
        return StringUtils.trimToEmpty(node);
    }
}
