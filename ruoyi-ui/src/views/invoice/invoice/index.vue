<template>
  <div class="app-container invoice-manage-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">发票管理</h2>
        <p class="page-desc">统一管理进销项发票，支持录入、发起审批、审批处理与日志跟踪</p>
      </div>
    </div>

    <el-card shadow="never" class="scope-card tab-card">
      <div class="scope-wrap">
        <div class="scope-left">
          <span class="scope-label">发票分类</span>
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="全部" name="all" />
            <el-tab-pane label="进项" name="input" />
            <el-tab-pane label="销项" name="output" />
          </el-tabs>
        </div>
        <div class="scope-right">
          <el-tag size="small" type="primary">{{ activeTabLabel }}</el-tag>
          <el-tag v-if="queryParams.approvalStatus" size="small" effect="plain">{{ getApprovalStatusMeta(queryParams.approvalStatus).label }}</el-tag>
          <el-tag size="small" effect="plain">共 {{ total }} 条</el-tag>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="search-card">
      <div class="search-header">
        <div class="search-title"><i class="el-icon-search" /><span>筛选条件</span></div>
        <el-button type="text" @click="showSearch = !showSearch">{{ showSearch ? '收起' : '展开' }}<i :class="showSearch ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" /></el-button>
      </div>
      <el-form v-show="showSearch" :model="queryParams" ref="queryForm" size="small" label-width="90px" class="query-form">
        <div class="form-grid">
          <el-form-item label="相对方名称" prop="counterpartyName"><el-input v-model="queryParams.counterpartyName" placeholder="请输入相对方名称" clearable /></el-form-item>
          <el-form-item label="发起人" prop="initiator"><el-input v-model="queryParams.initiator" placeholder="请输入发起人" clearable /></el-form-item>
          <el-form-item label="申请时间" prop="applyTime"><el-date-picker v-model="queryParams.applyTime" type="datetime" value-format="yyyy-MM-dd HH:mm:ss" placeholder="请选择申请时间" style="width:100%" /></el-form-item>
          <el-form-item label="所属部门" prop="department"><el-input v-model="queryParams.department" placeholder="请输入所属部门" clearable /></el-form-item>
          <el-form-item label="发票日期" prop="invoiceDate"><el-date-picker v-model="queryParams.invoiceDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择发票日期" style="width:100%" /></el-form-item>
          <el-form-item label="发票抬头" prop="purchaserName"><el-input v-model="queryParams.purchaserName" placeholder="请输入发票抬头" clearable /></el-form-item>
          <el-form-item label="纳税人识别号" prop="purchaserTaxNo"><el-input v-model="queryParams.purchaserTaxNo" placeholder="请输入纳税人识别号" clearable /></el-form-item>
          <el-form-item label="开户银行" prop="bankName"><el-input v-model="queryParams.bankName" placeholder="请输入开户银行" clearable /></el-form-item>
          <el-form-item label="银行账号" prop="bankAccount"><el-input v-model="queryParams.bankAccount" placeholder="请输入银行账号" clearable /></el-form-item>
          <el-form-item label="开票内容" prop="invoiceContent"><el-input v-model="queryParams.invoiceContent" placeholder="请输入开票内容" clearable /></el-form-item>
          <el-form-item label="发票金额" prop="invoiceAmount"><el-input v-model="queryParams.invoiceAmount" placeholder="请输入发票金额" clearable /></el-form-item>
          <el-form-item label="税率" prop="taxRate"><el-input v-model="queryParams.taxRate" placeholder="请输入税率，如13" clearable /></el-form-item>
          <el-form-item label="税额" prop="taxAmount"><el-input v-model="queryParams.taxAmount" placeholder="请输入税额" clearable /></el-form-item>
          <el-form-item label="所属项目" prop="project"><el-input v-model="queryParams.project" placeholder="请输入所属项目" clearable /></el-form-item>
          <el-form-item label="关联合同" prop="relatedContractName"><el-input v-model="queryParams.relatedContractName" placeholder="请输入关联合同" clearable /></el-form-item>
          <el-form-item label="审批状态" prop="approvalStatus">
            <el-select v-model="queryParams.approvalStatus" clearable style="width:100%">
              <el-option label="草稿" value="draft" />
              <el-option label="审批中" value="pending" />
              <el-option label="审批通过" value="approved" />
              <el-option label="审批驳回" value="rejected" />
            </el-select>
          </el-form-item>
        </div>
        <div class="search-actions"><el-button type="primary" icon="el-icon-search" @click="handleQuery">查询</el-button><el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button></div>
      </el-form>
    </el-card>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" icon="el-icon-plus" @click="handleAddInvoice">录发票</el-button>
        <el-button type="success" plain icon="el-icon-upload2" @click="handleImport">导入</el-button>
        <el-button plain icon="el-icon-setting" @click="openFlowSetting">审批流设置</el-button>
        <el-button plain icon="el-icon-download" @click="handleDownloadTemplate">下载模板</el-button>
      </div>
      <div class="toolbar-tip">
        <el-alert title="录发票时先选择合同。草稿/驳回可再次发起审批，审批中可直接通过/驳回并查看审批日志。" type="info" :closable="false" show-icon />
      </div>
    </div>

    <el-table v-loading="loading" :data="invoiceList" @selection-change="handleSelectionChange" border stripe class="modern-table" header-cell-class-name="table-header-gray">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="合同编码" align="center" prop="relatedContractNumber" min-width="150" show-overflow-tooltip />
      <el-table-column label="发票分类" align="center" width="100">
        <template slot-scope="scope"><el-tag size="small" effect="plain" :type="scope.row.amountType === '支出' ? 'warning' : 'success'">{{ scope.row.amountType === '支出' ? '进项' : scope.row.amountType === '收入' ? '销项' : '-' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="发票类型" align="center" prop="invoiceType" width="120">
        <template slot-scope="scope"><span>{{ formatInvoiceType(scope.row.invoiceType) }}</span></template>
      </el-table-column>
      <el-table-column label="发票号码" align="center" prop="invoiceNumber" min-width="150" show-overflow-tooltip />
      <el-table-column label="发票金额" align="center" prop="invoiceAmount" width="120"><template slot-scope="scope"><span class="money-text">¥ {{ formatMoney(scope.row.invoiceAmount) }}</span></template></el-table-column>
      <el-table-column label="税额" align="center" prop="taxAmount" width="120"><template slot-scope="scope"><span>¥ {{ formatMoney(scope.row.taxAmount) }}</span></template></el-table-column>
      <el-table-column label="税率" align="center" prop="taxRate" width="100"><template slot-scope="scope"><span>{{ formatTaxRate(scope.row.taxRate) }}</span></template></el-table-column>
      <el-table-column label="发票日期" align="center" prop="invoiceDate" width="120"><template slot-scope="scope"><span>{{ parseTime(scope.row.invoiceDate) }}</span></template></el-table-column>
      <el-table-column label="发票状态" align="center" width="110"><template slot-scope="scope"><el-tag :type="getInvoiceStatusMeta(scope.row.invoiceStatus).type" size="small">{{ getInvoiceStatusMeta(scope.row.invoiceStatus).label }}</el-tag></template></el-table-column>
      <el-table-column label="审批状态" align="center" width="110"><template slot-scope="scope"><el-tag :type="getApprovalStatusMeta(scope.row.approvalStatus).type" size="small">{{ getApprovalStatusMeta(scope.row.approvalStatus).label }}</el-tag></template></el-table-column>
      <el-table-column label="当前节点" align="center" width="110"><template slot-scope="scope"><el-tag size="small" effect="plain" :type="getCurrentNodeMeta(scope.row.currentApprovalNode).type">{{ getCurrentNodeMeta(scope.row.currentApprovalNode).label }}</el-tag></template></el-table-column>
      <el-table-column label="开票内容" align="center" prop="invoiceContent" min-width="180" show-overflow-tooltip />
      <el-table-column label="发票抬头" align="center" prop="purchaserName" min-width="180" show-overflow-tooltip />
      <el-table-column label="纳税人识别号" align="center" prop="purchaserTaxNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="开户银行" align="center" prop="bankName" min-width="180" show-overflow-tooltip />
      <el-table-column label="银行账号" align="center" prop="bankAccount" min-width="180" show-overflow-tooltip />
      <el-table-column label="地址" align="center" prop="bankAddress" min-width="180" show-overflow-tooltip />
      <el-table-column label="电话" align="center" prop="bankPhone" min-width="140" show-overflow-tooltip />
      <el-table-column label="备注" align="center" prop="remark" min-width="180" show-overflow-tooltip />
      <el-table-column label="操作" align="center" fixed="right" width="310">
        <template slot-scope="scope">
          <el-button size="mini" type="text" @click="openLogDrawer(scope.row)">日志</el-button>
          <el-button v-if="canEdit(scope.row)" size="mini" type="text" @click="handleUpdate(scope.row)">修改</el-button>
          <el-button v-if="canSubmitApproval(scope.row)" size="mini" type="text" @click="openApprovalDrawer(scope.row)">发起审批</el-button>
          <el-button v-if="canHandleCurrentNode(scope.row)" size="mini" type="text" @click="openApproveDialog(scope.row, 'agree')">通过</el-button>
          <el-button v-if="canHandleCurrentNode(scope.row)" size="mini" type="text" class="danger-text" @click="openApproveDialog(scope.row, 'reject')">驳回</el-button>
          <el-button size="mini" type="text" class="danger-text" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" class="page-pagination" />

    <el-dialog title="选择合同" :visible.sync="contractDialogVisible" width="980px" append-to-body custom-class="beauty-dialog large-dialog">
      <el-form :model="contractQueryParams" ref="contractQueryForm" class="contract-query-form">
        <div class="form-grid contract-grid">
          <el-form-item label="合同名称"><el-input v-model="contractQueryParams.contractName" clearable /></el-form-item>
          <el-form-item label="合同编号"><el-input v-model="contractQueryParams.contractNumber" clearable /></el-form-item>
          <el-form-item label="对方主体"><el-input v-model="contractQueryParams.otherPartyName" clearable /></el-form-item>
          <el-form-item label="我方主体"><el-input v-model="contractQueryParams.myPartyName" clearable /></el-form-item>
        </div>
        <div class="search-actions"><el-button type="primary" @click="loadContractList">查询</el-button><el-button @click="resetContractQuery">重置</el-button></div>
      </el-form>
      <div class="dialog-table-wrap">
        <el-table v-loading="contractLoading" :data="contractList" border stripe highlight-current-row>
          <el-table-column width="60" align="center"><template slot-scope="scope"><el-radio v-model="selectedContractId" :label="scope.row.id" @change="selectContractRow(scope.row)"><span style="display:none">{{ scope.row.id }}</span></el-radio></template></el-table-column>
          <el-table-column label="合同名称" prop="contractName" min-width="200" show-overflow-tooltip/>
          <el-table-column label="合同编号" prop="contractNumber" width="160"/>
          <el-table-column label="对方主体" prop="otherPartyName" min-width="160" show-overflow-tooltip/>
          <el-table-column label="我方主体" prop="myPartyName" min-width="160" show-overflow-tooltip/>
          <el-table-column label="合同金额" prop="totalAmount" width="130"><template slot-scope="scope">¥ {{ formatMoney(scope.row.totalAmount) }}</template></el-table-column>
          <el-table-column label="归属人" prop="owner" width="100"/>
        </el-table>
      </div>
      <pagination v-show="contractTotal > 0" :total="contractTotal" :page.sync="contractQueryParams.pageNum" :limit.sync="contractQueryParams.pageSize" @pagination="loadContractList" class="page-pagination contract-pagination" />
      <div slot="footer" class="dialog-footer"><el-button @click="contractDialogVisible = false">取消</el-button><el-button type="primary" :disabled="!selectedContractId" @click="confirmContractSelection">确认并继续</el-button></div>
    </el-dialog>

    <el-dialog :title="dialogTitle" :visible.sync="open" width="920px" append-to-body custom-class="beauty-dialog invoice-dialog">
      <div class="invoice-layout">
        <div class="invoice-side-card" v-if="selectedContract || form.relatedContractName">
          <div class="side-card-title">合同信息</div>
          <div class="side-info-item"><span>合同名称</span><strong>{{ (selectedContract && selectedContract.contractName) || form.relatedContractName || '-' }}</strong></div>
          <div class="side-info-item"><span>合同编号</span><strong>{{ (selectedContract && selectedContract.contractNumber) || form.relatedContractNumber || '-' }}</strong></div>
          <div class="side-info-item"><span>对方主体</span><strong>{{ (selectedContract && selectedContract.otherPartyName) || form.counterpartyName || '-' }}</strong></div>
          <div class="side-info-item"><span>我方主体</span><strong>{{ (selectedContract && selectedContract.myPartyName) || form.sellerName || '-' }}</strong></div>
          <div class="side-info-item"><span>合同总额</span><strong>¥ {{ formatMoney((selectedContract && selectedContract.totalAmount) || '') }}</strong></div>
        </div>
        <div class="invoice-form-panel">
          <div class="invoice-panel-header"><div class="panel-title">{{ dialogTitle }}</div><div class="panel-desc">先选择合同，再填写发票明细；审批相关信息在列表页处理</div></div>
          <el-form :model="form" ref="form" :rules="rules" label-width="110px" class="dialog-form">
            <div class="dialog-grid">
              <el-form-item label="发票分类" prop="amountType"><el-select v-model="form.amountType" style="width:100%"><el-option label="进项" value="支出" /><el-option label="销项" value="收入" /></el-select></el-form-item>
              <el-form-item label="发票类型" prop="invoiceType"><el-select v-model="form.invoiceType" style="width:100%" clearable><el-option label="增值税专用发票" value="vat" /><el-option label="普通发票" value="normal" /></el-select></el-form-item>
              <el-form-item label="发票代码" prop="invoiceCode"><el-input v-model="form.invoiceCode" /></el-form-item>
              <el-form-item label="发票号码" prop="invoiceNumber"><el-input v-model="form.invoiceNumber" /></el-form-item>
              <el-form-item label="开票日期" prop="invoiceDate"><el-date-picker v-model="form.invoiceDate" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
              <el-form-item label="发票状态" prop="invoiceStatus"><el-select v-model="form.invoiceStatus" style="width:100%"><el-option label="未开票" value="no_invoice" /><el-option label="已开票" value="invoiced" /><el-option label="已作废" value="voided" /></el-select></el-form-item>
              <el-form-item label="发票金额" prop="invoiceAmount"><el-input v-model="form.invoiceAmount"><template slot="prepend">¥</template></el-input></el-form-item>
              <el-form-item label="税率" prop="taxRate"><el-input v-model="form.taxRate"><template slot="append">%</template></el-input></el-form-item>
              <el-form-item label="税额" prop="taxAmount"><el-input v-model="form.taxAmount"><template slot="prepend">¥</template></el-input></el-form-item>
              <el-form-item label="不含税金额" prop="untaxedAmount"><el-input v-model="form.untaxedAmount"><template slot="prepend">¥</template></el-input></el-form-item>
              <el-form-item label="发票抬头" prop="purchaserName"><el-input v-model="form.purchaserName" /></el-form-item>
              <el-form-item label="纳税人识别号" prop="purchaserTaxNo"><el-input v-model="form.purchaserTaxNo" /></el-form-item>
              <el-form-item label="开户银行" prop="bankName"><el-input v-model="form.bankName" /></el-form-item>
              <el-form-item label="银行账号" prop="bankAccount"><el-input v-model="form.bankAccount" /></el-form-item>
              <el-form-item label="地址" prop="bankAddress"><el-input v-model="form.bankAddress" /></el-form-item>
              <el-form-item label="电话" prop="bankPhone"><el-input v-model="form.bankPhone" /></el-form-item>
              <el-form-item label="销售方名称" prop="sellerName"><el-input v-model="form.sellerName" /></el-form-item>
              <el-form-item label="销售方税号" prop="sellerTaxNo"><el-input v-model="form.sellerTaxNo" /></el-form-item>
              <el-form-item label="所属项目" prop="project"><el-input v-model="form.project" /></el-form-item>
              <el-form-item label="相对方名称" prop="counterpartyName"><el-input v-model="form.counterpartyName" /></el-form-item>
            </div>
            <el-form-item label="开票内容" prop="invoiceContent"><el-input v-model="form.invoiceContent" type="textarea" :rows="3" /></el-form-item>
            <el-form-item label="备注" prop="remark"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
          </el-form>
        </div>
      </div>
      <div slot="footer" class="dialog-footer"><el-button @click="open = false">取消</el-button><el-button type="primary" :loading="submitLoading" @click="submitForm">保存发票</el-button></div>
    </el-dialog>

    <el-drawer title="发票审批日志" :visible.sync="logDrawerVisible" direction="rtl" size="520px" custom-class="approval-drawer">
      <div class="approval-drawer-body" v-if="selectedRow">
        <div class="approval-header-card">
          <div class="approval-header-title">{{ selectedRow.relatedContractName || '发票审批日志' }}</div>
          <div class="approval-header-desc">发票号码：{{ selectedRow.invoiceNumber || '-' }} ｜ 审批状态：{{ getApprovalStatusMeta(selectedRow.approvalStatus).label }}</div>
        </div>
        <el-timeline v-if="invoiceLogs.length > 0">
          <el-timeline-item v-for="item in invoiceLogs" :key="item.id" :timestamp="parseTime(item.operateTime, '{y}-{m}-{d} {h}:{i}:{s}')" :type="getLogType(item.action)">
            <div class="timeline-item-title">{{ item.action }}</div>
            <div class="timeline-item-desc">{{ item.detail || '-' }}</div>
            <div class="timeline-item-desc light-text">操作人：{{ item.operator || '-' }}</div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无审批日志" />
      </div>
    </el-drawer>

    <el-drawer title="发起发票审批" :visible.sync="approvalDrawerVisible" direction="rtl" size="520px" custom-class="approval-drawer">
      <div class="approval-drawer-body" v-if="selectedRow">
        <div class="approval-header-card">
          <div class="approval-header-title">发票审批流程</div>
          <div class="approval-header-desc">按审批流设置中的串行节点顺序流转。</div>
        </div>
        <div class="approval-summary approval-summary-extended">
          <div class="summary-item"><span>开票日期</span><strong>{{ parseTime(selectedRow.invoiceDate) }}</strong></div>
          <div class="summary-item"><span>发票分类</span><strong>{{ selectedRow.amountType === '支出' ? '进项' : selectedRow.amountType === '收入' ? '销项' : (selectedRow.amountType || '-') }}</strong></div>
          <div class="summary-item"><span>发票金额</span><strong>¥ {{ formatMoney(selectedRow.invoiceAmount) }}</strong></div>
          <div class="summary-item"><span>发票状态</span><strong>{{ selectedRow.invoiceStatus === 'invoiced' ? '已开票' : selectedRow.invoiceStatus === 'no_invoice' ? '未开票' : selectedRow.invoiceStatus === 'voided' ? '已作废' : (selectedRow.invoiceStatus || '-') }}</strong></div>
          <div class="summary-item"><span>关联合同</span><strong>{{ selectedRow.relatedContractName || '-' }}{{ selectedRow.relatedContractNumber ? ' / ' + selectedRow.relatedContractNumber : '' }}</strong></div>
          <div class="summary-item"><span>发票号码</span><strong>{{ selectedRow.invoiceNumber || '-' }}</strong></div>
          <div class="summary-item"><span>发票抬头</span><strong>{{ selectedRow.purchaserName || '-' }}</strong></div>
          <div class="summary-item"><span>开票内容</span><strong>{{ selectedRow.invoiceContent || '-' }}</strong></div>
        </div>
        <el-form :model="approvalForm" ref="approvalForm" :rules="approvalRules" label-width="90px" class="approval-form-card">
          <el-form-item label="审批流">
            <el-input value="按审批流设置自动匹配节点处理人" disabled />
          </el-form-item>
          <el-alert title="发票审批将严格按照“审批流设置”中的节点顺序执行。" type="info" :closable="false" show-icon style="margin-bottom: 18px;" />
          <el-form-item label="审批说明" prop="remark"><el-input v-model="approvalForm.remark" type="textarea" :rows="5" placeholder="请输入审批说明、开票依据或业务说明" /></el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer"><el-button @click="approvalDrawerVisible = false">取消</el-button><el-button type="primary" :loading="submitLoading" @click="submitApproval">提交审批</el-button></div>
    </el-drawer>

    <el-dialog :title="approveActionTitle" :visible.sync="approveActionDialogVisible" width="620px" append-to-body custom-class="beauty-dialog">
      <div class="approval-summary approval-summary-extended" v-if="selectedRow">
        <div class="summary-item"><span>关联合同</span><strong>{{ selectedRow.relatedContractName || '-' }}</strong></div>
        <div class="summary-item"><span>发票号码</span><strong>{{ selectedRow.invoiceNumber || '-' }}</strong></div>
        <div class="summary-item"><span>发票金额</span><strong>¥ {{ formatMoney(selectedRow.invoiceAmount) }}</strong></div>
        <div class="summary-item"><span>当前节点</span><strong>{{ getCurrentNodeMeta(selectedRow.currentApprovalNode).label }}</strong></div>
        <div class="summary-item"><span>审批状态</span><strong>{{ getApprovalStatusMeta(selectedRow.approvalStatus).label }}</strong></div>
        <div class="summary-item"><span>发票状态</span><strong>{{ getInvoiceStatusMeta(selectedRow.invoiceStatus).label }}</strong></div>
      </div>
      <el-form :model="approveActionForm" ref="approveActionForm" label-width="100px">
        <el-form-item :label="selectedApproveAction === 'agree' ? '通过意见' : '驳回意见'"><el-input v-model="approveActionForm.remark" type="textarea" :rows="4" :placeholder="selectedApproveAction === 'agree' ? '请输入审批通过意见（可选）' : '请输入驳回原因（建议填写）'" /></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer"><el-button @click="approveActionDialogVisible = false">取消</el-button><el-button :type="selectedApproveAction === 'agree' ? 'primary' : 'danger'" :loading="submitLoading" @click="submitApproveAction">确认</el-button></div>
    </el-dialog>
    <approval-flow-setting :visible.sync="flowSettingVisible" business-type="invoice" title="发票管理" @saved="handleFlowSaved" />
  </div>
</template>

<script>
import { listInvoice, getInvoice, delInvoice, addInvoice, updateInvoice, importTemplate, importInvoice, submitInvoiceApproval, approveInvoice, listInvoiceLogs } from '@/api/invoice/invoice'
import { listContractContent } from '@/api/contract/contract'
import { getToken } from '@/utils/auth'
import ApprovalFlowSetting from '@/components/ApprovalFlowSetting'

const createQueryParams = () => ({
  pageNum: 1,
  pageSize: 10,
  counterpartyName: null,
  initiator: null,
  applyTime: null,
  department: null,
  invoiceDate: null,
  purchaserName: null,
  purchaserTaxNo: null,
  bankName: null,
  bankAccount: null,
  invoiceContent: null,
  invoiceAmount: null,
  taxRate: null,
  taxAmount: null,
  project: null,
  relatedContractName: null,
  amountType: null,
  approvalStatus: null
})

const createForm = () => ({
  id: null,
  contractId: null,
  invoiceCode: '',
  invoiceNumber: '',
  invoiceDate: '',
  invoiceAmount: '',
  invoiceType: 'normal',
  invoiceStatus: 'invoiced',
  taxRate: '',
  taxAmount: '',
  purchaserName: '',
  purchaserTaxNo: '',
  bankName: '',
  bankAccount: '',
  bankAddress: '',
  bankPhone: '',
  sellerName: '',
  sellerTaxNo: '',
  counterpartyName: '',
  invoiceContent: '',
  untaxedAmount: '',
  project: '',
  amountType: '支出',
  relatedContractName: '',
  relatedContractNumber: '',
  remark: ''
})

export default {
  name: 'Invoice',
  components: { ApprovalFlowSetting },
  data() {
    return {
      loading: false,
      ids: [],
      total: 0,
      invoiceList: [],
      activeTab: 'all',
      showSearch: true,
      queryParams: createQueryParams(),
      open: false,
      dialogTitle: '录发票',
      submitLoading: false,
      form: createForm(),
      rules: {
        contractId: [{ required: true, message: '请选择合同', trigger: 'change' }],
        amountType: [{ required: true, message: '请选择发票分类', trigger: 'change' }],
        invoiceDate: [{ required: true, message: '请选择开票日期', trigger: 'change' }],
        invoiceAmount: [{ required: true, message: '请输入发票金额', trigger: 'blur' }],
        purchaserName: [{ required: true, message: '请输入发票抬头', trigger: 'blur' }],
        invoiceContent: [{ required: true, message: '请输入开票内容', trigger: 'blur' }]
      },
      contractDialogVisible: false,
      contractLoading: false,
      contractList: [],
      contractTotal: 0,
      selectedContractId: null,
      selectedContract: null,
      contractQueryParams: { pageNum: 1, pageSize: 10, contractName: '', contractNumber: '', otherPartyName: '', myPartyName: '' },
      pendingDirection: null,
      selectedRow: null,
      approvalDrawerVisible: false,
      approvalForm: { directLeaderDisplay: '', approverDisplay: '', handlerDisplay: '', remark: '' },
      approvalRules: {
        remark: [{ required: true, message: '请输入审批说明', trigger: 'blur' }]
      },
      approveActionDialogVisible: false,
      approveActionTitle: '审批操作',
      selectedApproveAction: 'agree',
      approveActionForm: { remark: '' },
      logDrawerVisible: false,
      invoiceLogs: [],
      flowSettingVisible: false,
      upload: {
        isUploading: false,
        headers: { Authorization: 'Bearer ' + getToken() },
        url: process.env.VUE_APP_BASE_API + '/invoice/invoice/importData'
      }
    }
  },
  computed: {
    activeTabLabel() {
      return { all: '全部', input: '进项', output: '销项' }[this.activeTab]
    }
  },
  created() {
    this.getList()
  },
  methods: {
    openFlowSetting() { this.flowSettingVisible = true },
    handleFlowSaved() { this.getList() },
    handleTabClick(tab) {
      this.activeTab = tab.name
      const typeMap = { all: null, input: '支出', output: '收入' }
      this.queryParams.amountType = typeMap[this.activeTab]
      this.queryParams.pageNum = 1
      this.getList()
    },
    getList() {
      this.loading = true
      listInvoice(this.queryParams).then(response => {
        this.invoiceList = response.rows || []
        this.total = response.total || 0
      }).finally(() => {
        this.loading = false
      })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() {
      this.$refs.queryForm && this.$refs.queryForm.resetFields()
      this.queryParams = createQueryParams()
      this.queryParams.amountType = this.activeTab === 'input' ? '支出' : this.activeTab === 'output' ? '收入' : null
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.selectedRow = selection.length === 1 ? selection[0] : null
    },
    canEdit(row) {
      return row && row.approvalStatus !== 'pending' && row.approvalStatus !== 'approved'
    },
    canSubmitApproval(row) {
      return row && row.approvalStatus !== 'pending' && row.approvalStatus !== 'approved'
    },
    getCurrentNodeUser(row) {
      if (!row) return ''
      if (row.currentApprovalNode === 'node1') return row.directLeader || ''
      if (row.currentApprovalNode === 'node2') return row.approver || ''
      if (row.currentApprovalNode === 'node3') return row.handler || ''
      return ''
    },
    canHandleCurrentNode(row) {
      if (!row || row.approvalStatus !== 'pending') return false
      const currentUser = this.$store && this.$store.getters ? this.$store.getters.name : ''
      return !!currentUser && this.getCurrentNodeUser(row) === currentUser
    },
    getCurrentNodeLabel(row) {
      const node = row && row.currentApprovalNode
      return ({ node1: '审批节点1', node2: '审批节点2', node3: '审批节点3', finished: '已完成', rejected: '已驳回' })[node] || '-'
    },
    handleAddInvoice() {
      const defaultDirection = this.activeTab === 'input' ? '支出' : this.activeTab === 'output' ? '收入' : null
      this.openContractDialog(defaultDirection)
    },
    openContractDialog(direction = null) {
      this.pendingDirection = direction
      this.contractDialogVisible = true
      this.selectedContract = null
      this.selectedContractId = null
      this.contractQueryParams.pageNum = 1
      this.loadContractList()
    },
    loadContractList() {
      this.contractLoading = true
      listContractContent(this.contractQueryParams).then(res => {
        this.contractList = res.rows || []
        this.contractTotal = res.total || 0
      }).finally(() => {
        this.contractLoading = false
      })
    },
    resetContractQuery() {
      this.contractQueryParams = { pageNum: 1, pageSize: 10, contractName: '', contractNumber: '', otherPartyName: '', myPartyName: '' }
      this.loadContractList()
    },
    selectContractRow(row) { this.selectedContract = row },
    confirmContractSelection() {
      if (!this.selectedContract) return this.$message.warning('请先选择一个合同')
      const direction = this.pendingDirection || '收入'
      this.contractDialogVisible = false
      this.dialogTitle = direction === '支出' ? '录进项发票' : direction === '收入' ? '录销项发票' : '录发票'
      this.form = {
        ...createForm(),
        contractId: this.selectedContract.id,
        amountType: direction,
        relatedContractName: this.selectedContract.contractName || '',
        relatedContractNumber: this.selectedContract.contractNumber || '',
        purchaserName: direction === '收入' ? (this.selectedContract.otherPartyName || '') : (this.selectedContract.myPartyName || ''),
        sellerName: direction === '收入' ? (this.selectedContract.myPartyName || '') : (this.selectedContract.otherPartyName || ''),
        counterpartyName: this.selectedContract.otherPartyName || '',
        project: this.selectedContract.project || ''
      }
      this.open = true
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },
    handleUpdate(row) {
      const id = row && row.id ? row.id : this.ids[0]
      if (!id) return this.$message.warning('请先选择一条数据')
      getInvoice(id).then(response => {
        const data = response.data || {}
        this.form = { ...createForm(), ...data }
        this.selectedContract = { id: data.contractId, contractName: data.relatedContractName, contractNumber: data.relatedContractNumber, otherPartyName: data.counterpartyName, myPartyName: data.sellerName }
        this.dialogTitle = '修改发票信息'
        this.open = true
        this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
      })
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const request = this.form.id ? updateInvoice(this.form) : addInvoice(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id ? '修改成功' : '新增成功')
          this.open = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    openApprovalDrawer(row) {
      this.selectedRow = row
      this.approvalForm = { directLeaderDisplay: '按审批流设置自动匹配', approverDisplay: '', handlerDisplay: '', remark: '' }
      this.approvalDrawerVisible = true
      this.$nextTick(() => { this.$refs.approvalForm && this.$refs.approvalForm.clearValidate() })
    },
    submitApproval() {
      this.$refs.approvalForm.validate(valid => {
        if (!valid || !this.selectedRow) return
        this.submitLoading = true
        submitInvoiceApproval({ id: this.selectedRow.id, remark: this.approvalForm.remark }).then(() => {
          this.$message.success('发票审批已提交')
          this.approvalDrawerVisible = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    openApproveDialog(row, action) {
      this.selectedRow = row
      this.selectedApproveAction = action
      this.approveActionTitle = action === 'agree' ? '审批通过' : '审批驳回'
      this.approveActionForm = { remark: '' }
      this.approveActionDialogVisible = true
    },
    submitApproveAction() {
      if (!this.selectedRow) return
      this.submitLoading = true
      approveInvoice({ id: this.selectedRow.id, action: this.selectedApproveAction, remark: this.approveActionForm.remark }).then(() => {
        this.$message.success(this.selectedApproveAction === 'agree' ? '审批已通过' : '审批已驳回')
        this.approveActionDialogVisible = false
        this.getList()
      }).finally(() => { this.submitLoading = false })
    },
    openLogDrawer(row) {
      this.selectedRow = row
      this.invoiceLogs = []
      this.logDrawerVisible = true
      listInvoiceLogs(row.id).then(res => {
        const logs = res.data || []
        this.invoiceLogs = logs.slice().sort((a, b) => {
          const timeA = a && a.operateTime ? new Date(a.operateTime).getTime() : 0
          const timeB = b && b.operateTime ? new Date(b.operateTime).getTime() : 0
          return timeA - timeB
        })
      })
    },
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids
      if (!ids || (Array.isArray(ids) && ids.length === 0)) return this.$message.warning('请选择要删除的数据')
      this.$modal.confirm('是否确认删除选中的发票信息？').then(() => delInvoice(Array.isArray(ids) ? ids.join(',') : ids)).then(() => {
        this.getList()
        this.$modal.msgSuccess('删除成功')
      }).catch(() => {})
    },
    handleImport() {
      const input = document.createElement('input')
      input.type = 'file'
      input.accept = '.xlsx,.xls'
      input.onchange = async event => {
        const file = event.target.files && event.target.files[0]
        if (!file) return
        const fileName = (file.name || '').toLowerCase()
        if (!(fileName.endsWith('.xls') || fileName.endsWith('.xlsx'))) {
          return this.$message.error('请选择 xls 或 xlsx 格式的文件')
        }
        const formData = new FormData()
        formData.append('file', file)
        this.upload.isUploading = true
        try {
          const res = await importInvoice(formData)
          this.$message.success((res && res.msg) || '导入完成')
          this.getList()
        } catch (error) {
          this.$message.error((error && (error.msg || error.message)) || '导入失败，请检查 Excel 内容')
        } finally {
          this.upload.isUploading = false
        }
      }
      input.click()
    },
    async handleDownloadTemplate() {
      try {
        const res = await importTemplate()
        const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const link = document.createElement('a')
        link.href = window.URL.createObjectURL(blob)
        link.download = '发票批量导入2024-2025(1).xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(link.href)
      } catch (error) {
        this.$message.error('模板下载失败，请稍后重试')
      }
    },
    getApprovalStatusMeta(status) {
      return ({ draft: { label: '草稿', type: 'info' }, pending: { label: '审批中', type: 'warning' }, approved: { label: '审批通过', type: 'success' }, rejected: { label: '审批驳回', type: 'danger' } })[status] || { label: '草稿', type: 'info' }
    },
    getInvoiceStatusMeta(status) {
      return ({ no_invoice: { label: '未开票', type: 'info' }, invoiced: { label: '已开票', type: 'success' }, voided: { label: '已作废', type: 'danger' } })[status] || { label: '-', type: 'info' }
    },
    getCurrentNodeMeta(node) {
      return ({ node1: { label: '审批节点1', type: 'warning' }, node2: { label: '审批节点2', type: 'warning' }, node3: { label: '审批节点3', type: 'primary' }, finished: { label: '已完成', type: 'success' }, rejected: { label: '已驳回', type: 'danger' } })[node] || { label: '-', type: 'info' }
    },
    getLogType(action) {
      if (!action) return 'primary'
      if (action.includes('驳回')) return 'danger'
      if (action.includes('审批') || action.includes('提交')) return 'warning'
      if (action.includes('通过')) return 'success'
      return 'primary'
    },
    formatInvoiceType(value) {
      return ({ vat: '增值税专票', normal: '普通发票' })[value] || value || '-'
    },
    formatTaxRate(value) {
      if (value === null || value === undefined || value === '') return '-'
      const text = String(value)
      return text.includes('%') ? text : `${text}%`
    },
    parseTime(time, format = '{y}-{m}-{d}') {
      if (!time) return '-'
      const date = new Date(time)
      if (isNaN(date.getTime())) return time
      const y = date.getFullYear()
      const m = String(date.getMonth() + 1).padStart(2, '0')
      const d = String(date.getDate()).padStart(2, '0')
      const h = String(date.getHours()).padStart(2, '0')
      const i = String(date.getMinutes()).padStart(2, '0')
      const s = String(date.getSeconds()).padStart(2, '0')
      return format.replace('{y}', y).replace('{m}', m).replace('{d}', d).replace('{h}', h).replace('{i}', i).replace('{s}', s)
    },
    formatMoney(value) {
      if (value === null || value === undefined || value === '') return '0.00'
      const num = Number(value)
      if (isNaN(num)) return value
      return num.toFixed(2)
    }
  }
}
</script>

<style scoped lang="scss">
.invoice-manage-page { padding: 20px; background: linear-gradient(180deg, #f6f8fb 0%, #f4f6fa 100%); min-height: 100vh; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 24px; font-weight: 700; margin: 0 0 6px; color: #1f2d3d; }
.page-desc { margin: 0; color: #909399; font-size: 13px; }
.scope-card, .search-card, .main-content-card, .tab-card { border: 1px solid #ebeef5; border-radius: 16px; box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04); margin-bottom: 18px; }
.scope-wrap, .scope-left, .scope-right, .search-header, .toolbar-left { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.scope-wrap, .search-header { justify-content: space-between; }
.scope-left { flex: 1; }
.scope-label { font-size: 14px; font-weight: 600; color: #303133; }
.tab-card { ::v-deep .el-card__body { padding-bottom: 10px; } ::v-deep .el-tabs__header { margin: 0; } ::v-deep .el-tabs__nav-wrap::after { height: 1px; background-color: #eef1f5; } ::v-deep .el-tabs__item.is-active { font-weight: 700; color: #409eff; } }
.search-title { display: flex; align-items: center; gap: 8px; color: #303133; font-weight: 600; }
.search-title i { color: #409eff; }
.query-form { margin-top: 10px; }
.form-grid, .dialog-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 0 16px; }
.query-form .form-grid { grid-template-columns: repeat(4, 1fr); }
.query-form ::v-deep .el-form-item { margin-bottom: 16px; }
.search-actions { display: flex; gap: 10px; flex-wrap: wrap; }
.toolbar { margin-bottom: 18px; display: flex; gap: 10px; align-items: flex-start; justify-content: space-between; flex-wrap: wrap; }
.toolbar-tip { flex: 1; min-width: 260px; }
.modern-table { width: 100%; background: #fff; border: 1px solid #ebeef5; border-radius: 16px; overflow: hidden; box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04); }
.table-header-gray { background: #f8fafc !important; color: #606266; font-weight: 700; }
.money-text { color: #00b96b; font-weight: 700; }
.danger-text { color: #f56c6c; }
.light-text { color: #909399; margin-top: 6px; }
.modern-table ::v-deep .el-table__row:hover > td { background-color: #f5faff !important; }
.modern-table ::v-deep td, .modern-table ::v-deep th { padding: 12px 0; }
.page-pagination { margin-top: 20px; padding: 0 16px 16px; display: flex; justify-content: flex-end; }
.contract-pagination { margin-top: 12px; padding-bottom: 0; }
.dialog-table-wrap { max-height: 460px; overflow: auto; }
.invoice-layout { display: flex; gap: 18px; }
.invoice-side-card { width: 280px; padding: 18px; border-radius: 16px; background: linear-gradient(180deg, #f8fbff 0%, #f5f7fa 100%); border: 1px solid #e8eef6; box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05); }
.side-card-title { font-size: 16px; font-weight: 700; margin-bottom: 14px; color: #303133; }
.side-info-item { padding: 10px 0; border-bottom: 1px dashed #dfe6ee; display: flex; flex-direction: column; }
.side-info-item:last-child { border-bottom: none; }
.side-info-item span { font-size: 12px; color: #909399; margin-bottom: 4px; }
.side-info-item strong { font-size: 14px; color: #303133; word-break: break-all; }
.invoice-form-panel { flex: 1; }
.invoice-panel-header { margin-bottom: 18px; }
.panel-title { font-size: 18px; font-weight: 700; color: #303133; }
.panel-desc { font-size: 12px; color: #909399; margin-top: 4px; }
.approval-drawer ::v-deep .el-drawer__header { margin-bottom: 0; padding: 20px 24px; border-bottom: 1px solid #ebeef5; }
.approval-drawer ::v-deep .el-drawer__body { background: #f7f9fc; padding: 0; display: flex; flex-direction: column; height: 100%; }
.approval-drawer-body { flex: 1; overflow: auto; padding: 20px 24px; }
.approval-header-card { padding: 18px 20px; border-radius: 16px; background: linear-gradient(135deg, #eef6ff 0%, #f7fbff 100%); border: 1px solid #dceafd; margin-bottom: 18px; }
.approval-header-title { font-size: 18px; font-weight: 700; color: #1f2d3d; margin-bottom: 6px; }
.approval-header-desc { color: #6b7280; font-size: 12px; line-height: 1.7; }
.approval-summary { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px 16px; margin-bottom: 18px; }
.summary-item { display: flex; flex-direction: column; padding: 14px 16px; background: #fff; border-radius: 14px; border: 1px solid #ebeef5; }
.summary-item span { font-size: 12px; color: #909399; margin-bottom: 6px; }
.summary-item strong { color: #303133; word-break: break-all; }
.approval-form-card { border-radius: 16px; border: 1px solid #ebeef5; margin-bottom: 18px; }
.timeline-item-title { font-weight: 700; color: #303133; margin-bottom: 4px; }
.timeline-item-desc { color: #606266; line-height: 1.8; white-space: pre-wrap; }
.drawer-footer, .dialog-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 24px 20px; border-top: 1px solid #f0f2f5; background: #fff; }
::v-deep .beauty-dialog { .el-dialog { border-radius: 18px; overflow: hidden; } .el-dialog__header { padding: 20px 24px 10px; border-bottom: 1px solid #f0f2f5; } .el-dialog__title { font-size: 18px; font-weight: 700; color: #303133; } .el-dialog__body { padding: 20px 24px; background: #f7f9fc; } .el-dialog__footer { padding: 14px 24px 20px; border-top: 1px solid #f0f2f5; background: #fff; } }
::v-deep .el-card__body { padding: 18px 20px; }
::v-deep .el-input__inner, ::v-deep .el-textarea__inner, ::v-deep .el-button { border-radius: 10px; }
@media screen and (max-width: 1200px) { .query-form .form-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 1000px) { .dialog-grid, .contract-grid, .approval-summary { grid-template-columns: 1fr; } .invoice-layout { flex-direction: column; } .invoice-side-card { width: 100%; } }
@media screen and (max-width: 768px) { .invoice-manage-page { padding: 12px; } .query-form .form-grid { grid-template-columns: 1fr; } .toolbar { flex-direction: column; align-items: stretch; } }
</style>
