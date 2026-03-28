package com.ruoyi.borrow.controller;

import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.borrow.domain.ContractBorrow;
import com.ruoyi.borrow.service.IContractBorrowService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 合同借阅Controller
 * 
 * @author ruoyi
 * @date 2026-03-26
 */
@RestController
@RequestMapping("/borrow/borrow")
public class ContractBorrowController extends BaseController
{
    @Autowired
    private IContractBorrowService contractBorrowService;

    /**
     * 查询合同借阅列表
     */
    @PreAuthorize("@ss.hasPermi('borrow:borrow:list')")
    @GetMapping("/list")
    public TableDataInfo list(ContractBorrow contractBorrow)
    {
        startPage();
        List<ContractBorrow> list = contractBorrowService.selectContractBorrowList(contractBorrow);
        return getDataTable(list);
    }

    /**
     * 导出合同借阅列表
     */
    @PreAuthorize("@ss.hasPermi('borrow:borrow:export')")
    @Log(title = "合同借阅", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ContractBorrow contractBorrow)
    {
        List<ContractBorrow> list = contractBorrowService.selectContractBorrowList(contractBorrow);
        ExcelUtil<ContractBorrow> util = new ExcelUtil<ContractBorrow>(ContractBorrow.class);
        util.exportExcel(response, list, "合同借阅数据");
    }

    /**
     * 获取合同借阅详细信息
     */
    @PreAuthorize("@ss.hasPermi('borrow:borrow:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(contractBorrowService.selectContractBorrowById(id));
    }

    /**
     * 新增合同借阅
     */
    @PreAuthorize("@ss.hasPermi('borrow:borrow:add')")
    @Log(title = "合同借阅", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ContractBorrow contractBorrow)
    {
        return toAjax(contractBorrowService.insertContractBorrow(contractBorrow));
    }

    /**
     * 修改合同借阅
     */
    @PreAuthorize("@ss.hasPermi('borrow:borrow:edit')")
    @Log(title = "合同借阅", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ContractBorrow contractBorrow)
    {
        return toAjax(contractBorrowService.updateContractBorrow(contractBorrow));
    }

    /**
     * 提交借阅审批
     */
    @PreAuthorize("@ss.hasPermi('borrow:borrow:edit')")
    @Log(title = "合同借阅", businessType = BusinessType.UPDATE)
    @PostMapping("/submitApproval")
    public AjaxResult submitApproval(@RequestBody Map<String, Object> payload)
    {
        Long id = Long.valueOf(payload.get("id").toString());
        String approver = payload.get("approver") == null ? null : payload.get("approver").toString();
        String handler = payload.get("handler") == null ? null : payload.get("handler").toString();
        String remark = payload.get("remark") == null ? null : payload.get("remark").toString();
        return toAjax(contractBorrowService.submitApproval(id, approver, handler, remark));
    }

    /**
     * 借阅审批
     */
    @PreAuthorize("@ss.hasPermi('borrow:borrow:edit')")
    @Log(title = "合同借阅", businessType = BusinessType.UPDATE)
    @PostMapping("/approve")
    public AjaxResult approve(@RequestBody Map<String, Object> payload)
    {
        Long id = Long.valueOf(payload.get("id").toString());
        String action = payload.get("action") == null ? null : payload.get("action").toString();
        String remark = payload.get("remark") == null ? null : payload.get("remark").toString();
        return toAjax(contractBorrowService.handleApproval(id, action, remark));
    }

    /**
     * 登记归还
     */
    @PreAuthorize("@ss.hasPermi('borrow:borrow:edit')")
    @Log(title = "合同借阅", businessType = BusinessType.UPDATE)
    @PostMapping("/return")
    public AjaxResult markReturned(@RequestBody Map<String, Object> payload)
    {
        Long id = Long.valueOf(payload.get("id").toString());
        String remark = payload.get("remark") == null ? null : payload.get("remark").toString();
        return toAjax(contractBorrowService.markReturned(id, remark));
    }

    /**
     * 删除合同借阅
     */
    @PreAuthorize("@ss.hasPermi('borrow:borrow:remove')")
    @Log(title = "合同借阅", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(contractBorrowService.deleteContractBorrowByIds(ids));
    }
}
