<template>
  <div class="app-container account-manage-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">璐︽绠＄悊</h2>
        <p class="page-desc">缁熶竴绠＄悊璐︽璁板綍銆佽处娆捐鍒掋€佹敹浠樻鐢宠涓庡鎵规祦杞?/p>
      </div>
    </div>

    <div class="nav-panel">
      <div class="nav-card" :class="{ active: currentNav === 'account' }" @click="handleNavClick('account')">
        <div class="nav-card-icon account"><i class="el-icon-document"></i></div>
        <div class="nav-card-content"><div class="nav-card-title">璐︽璁板綍</div><div class="nav-card-desc">鏀朵粯娆剧敵璇枫€佸鎵硅窡韪€佸埌璐︾姸鎬佺鐞?/div></div>
      </div>
      <div class="nav-card" :class="{ active: currentNav === 'plan' }" @click="handleNavClick('plan')">
        <div class="nav-card-icon plan"><i class="el-icon-date"></i></div>
        <div class="nav-card-content"><div class="nav-card-title">璐︽璁″垝</div><div class="nav-card-desc">璐︽璁″垝缂栨帓銆佹墽琛岃窡韪笌璁″垝缁存姢</div></div>
      </div>
    </div>

    <el-card shadow="never" class="scope-card">
      <div class="scope-wrap">
        <div class="scope-left">
          <span class="scope-label">鏌ョ湅鑼冨洿</span>
          <el-radio-group v-model="scopeFilter" size="small" @change="handleScopeChange">
            <el-radio-button label="mine">鎴戠殑</el-radio-button>
            <el-radio-button label="dept">鎴戦儴闂ㄧ殑</el-radio-button>
            <el-radio-button label="all">鎵€鏈夌殑</el-radio-button>
          </el-radio-group>
        </div>
        <div class="scope-right">
          <el-tag size="small" type="primary">{{ currentNav === 'account' ? '璐︽璁板綍' : '璐︽璁″垝' }}</el-tag>
          <el-tag size="small" effect="plain">{{ scopeFilterLabel }}</el-tag>
          <el-tag v-if="currentNav === 'account' && warningType !== 'all'" size="small" type="danger">{{ warningTypeLabel }}</el-tag>
        </div>
      </div>
    </el-card>

    <div class="main-content">
      <el-card shadow="never" class="tab-card">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="鍏ㄩ儴" name="all"></el-tab-pane>
          <el-tab-pane :label="currentNav === 'account' ? '鏀舵' : '鏀舵璁″垝'" name="receive"></el-tab-pane>
          <el-tab-pane :label="currentNav === 'account' ? '浠樻' : '浠樻璁″垝'" name="pay"></el-tab-pane>
        </el-tabs>
        <div v-if="currentNav === 'account'" class="warning-filter-bar">
          <el-radio-group v-model="warningType" size="small" @change="handleWarningTypeChange">
            <el-radio-button label="all">鍏ㄩ儴</el-radio-button>
            <el-radio-button label="receive_overdue">鏀舵宸查€炬湡</el-radio-button>
            <el-radio-button label="receive_overdue_3d">鏀舵閫炬湡1-3澶?/el-radio-button>
            <el-radio-button label="receive_overdue_7d">鏀舵閫炬湡4-7澶?/el-radio-button>
            <el-radio-button label="receive_overdue_30d">鏀舵閫炬湡8-30澶?/el-radio-button>
            <el-radio-button label="receive_overdue_30p">鏀舵閫炬湡30澶╀互涓?/el-radio-button>
            <el-radio-button label="pay_overdue">浠樻宸查€炬湡</el-radio-button>
            <el-radio-button label="pay_overdue_3d">浠樻閫炬湡1-3澶?/el-radio-button>
            <el-radio-button label="pay_overdue_7d">浠樻閫炬湡4-7澶?/el-radio-button>
            <el-radio-button label="pay_overdue_30d">浠樻閫炬湡8-30澶?/el-radio-button>
            <el-radio-button label="pay_overdue_30p">浠樻閫炬湡30澶╀互涓?/el-radio-button>
            <el-radio-button label="receive_due_today">鏀舵浠婃棩鍒版湡</el-radio-button>
            <el-radio-button label="receive_due_1d">鏀舵鏄庢棩鍒版湡</el-radio-button>
            <el-radio-button label="receive_due_3d">鏀舵2-3澶╁唴</el-radio-button>
            <el-radio-button label="receive_due_7d">鏀舵4-7澶╁唴</el-radio-button>
            <el-radio-button label="receive_due_15d">鏀舵8-15澶╁唴</el-radio-button>
            <el-radio-button label="receive_due_30d">鏀舵16-30澶╁唴</el-radio-button>
            <el-radio-button label="pay_due_today">浠樻浠婃棩鍒版湡</el-radio-button>
            <el-radio-button label="pay_due_1d">浠樻鏄庢棩鍒版湡</el-radio-button>
            <el-radio-button label="pay_due_3d">浠樻2-3澶╁唴</el-radio-button>
            <el-radio-button label="pay_due_7d">浠樻4-7澶╁唴</el-radio-button>
            <el-radio-button label="pay_due_15d">浠樻8-15澶╁唴</el-radio-button>
            <el-radio-button label="pay_due_30d">浠樻16-30澶╁唴</el-radio-button>
            <el-radio-button label="normal">姝ｅ父</el-radio-button>
          </el-radio-group>
        </div>
      </el-card>

      <el-card shadow="never" class="search-card">
        <div class="search-header">
          <div class="search-title"><i class="el-icon-search"></i><span>绛涢€夋潯浠?/span></div>
          <el-button type="text" @click="showSearch = !showSearch">{{ showSearch ? '鏀惰捣' : '灞曞紑' }}<i :class="showSearch ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i></el-button>
        </div>
        <el-form v-show="showSearch" :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="90px" class="query-form">
          <template v-if="currentNav === 'account'">
            <el-form-item label="鍚堝悓缂栧彿" prop="contractId"><el-input v-model="queryParams.contractId" clearable style="width:180px"/></el-form-item>
            <el-form-item label="鍗曟嵁鍙? prop="orderNo"><el-input v-model="queryParams.orderNo" clearable style="width:180px"/></el-form-item>
            <el-form-item label="閲戦绫诲瀷" prop="amountType"><el-select v-model="queryParams.amountType" clearable style="width:180px"><el-option label="鏀跺叆" value="income"/><el-option label="鏀嚭" value="expense"/></el-select></el-form-item>
            <el-form-item label="涓讳綋鍚嶇О" prop="partyName"><el-input v-model="queryParams.partyName" clearable style="width:180px"/></el-form-item>
          </template>
          <template v-else>
            <el-form-item label="璁″垝鍚嶇О" prop="planName"><el-input v-model="queryParams.planName" clearable style="width:180px"/></el-form-item>
            <el-form-item label="璁″垝缂栧彿" prop="planNo"><el-input v-model="queryParams.planNo" clearable style="width:180px"/></el-form-item>
          </template>
          <el-form-item><el-button type="primary" @click="handleQuery">鎼滅储</el-button><el-button @click="resetQuery">閲嶇疆</el-button></el-form-item>
        </el-form>
      </el-card>

      <div class="toolbar">
        <div class="toolbar-left">
          <template v-if="currentNav === 'account'">
            <el-button type="primary" icon="el-icon-s-promotion" @click="handleApplyReceive" v-if="activeTab !== 'pay'">鐢宠鏀舵</el-button>
            <el-button type="success" icon="el-icon-s-promotion" @click="handleApplyPay" v-if="activeTab !== 'receive'">鐢宠浠樻</el-button>
            <el-button plain icon="el-icon-setting" @click="openFlowSetting">瀹℃壒娴佽缃?/el-button>
            <el-button type="info" plain icon="el-icon-download" :loading="downloadLoading" @click="handleDownloadTemplate">涓嬭浇妯℃澘</el-button>
            <el-button type="primary" plain icon="el-icon-upload2" :loading="importLoading" @click="handleImport">瀵煎叆</el-button>
            <input ref="importInput" type="file" accept=".xls,.xlsx" style="display:none" @change="handleImportFileChange" />
          </template>
          <template v-else>
            <el-button type="primary" icon="el-icon-plus" @click="handleAddPlan">娣诲姞璐︽璁″垝</el-button>
          </template>
        </div>
        <div v-if="currentNav === 'account'" class="toolbar-tip">
          <el-alert title="鐢宠鏀舵/浠樻鍓嶏紝璇峰厛鍦ㄤ笅鏂瑰垪琛ㄥ嬀閫変竴鏉¤处娆捐褰曘€? type="info" :closable="false" show-icon />
        </div>
      </div>

      <el-table v-if="currentNav === 'account'" v-loading="loading" :data="accountList" @selection-change="handleSelectionChange" border header-cell-class-name="table-header-gray" class="account-table">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="鍏宠仈鍚堝悓鍚嶇О" prop="relatedContractName" min-width="180" show-overflow-tooltip />
        <el-table-column label="璐︽鏃ユ湡" width="120" align="center"><template slot-scope="scope">{{ parseTime(scope.row.accountDate) }}</template></el-table-column>
        <el-table-column label="棰勮鐘舵€? width="120" align="center"><template slot-scope="scope"><el-tag :type="getWarningMeta(scope.row).tagType" size="small">{{ getWarningMeta(scope.row).label }}</el-tag></template></el-table-column>
        <el-table-column label="璐︽閲戦" width="140" align="right"><template slot-scope="scope">楼{{ formatAmount(scope.row.amount) }}</template></el-table-column>
        <el-table-column label="閲戦绫诲瀷" width="100" align="center"><template slot-scope="scope"><el-tag :type="scope.row.amountType === 'income' ? 'success' : 'danger'" size="small">{{ scope.row.amountType === 'income' ? '鏀跺叆' : '鏀嚭' }}</el-tag></template></el-table-column>
        <el-table-column label="鍗曟嵁鍙? prop="orderNo" width="140"/>
        <el-table-column label="鍏宠仈鍚堝悓缂栧彿" prop="relatedContractNumber" width="160"/>
        <el-table-column label="鐘舵€? width="120" align="center"><template slot-scope="scope"><el-tag :type="getAccountStatusType(scope.row.status)" size="small">{{ getAccountStatusLabel(scope.row.status) }}</el-tag></template></el-table-column>
        <el-table-column label="褰撳墠鑺傜偣" width="120" align="center"><template slot-scope="scope"><el-tag :type="getCurrentNodeMeta(scope.row).type" size="small">{{ getCurrentNodeMeta(scope.row).label }}</el-tag></template></el-table-column>
        <el-table-column label="褰撳墠澶勭悊浜? min-width="130" align="center"><template slot-scope="scope">{{ getCurrentNodeAssignee(scope.row) }}</template></el-table-column>
        <el-table-column label="鎿嶄綔" width="360" fixed="right" align="center" class-name="action-column">
          <template slot-scope="scope">
            <el-button size="mini" type="text" class="action-btn" @click="handleDetail(scope.row)">璇︽儏</el-button>
            <el-button v-if="showApprovalEntry(scope.row)" size="mini" type="text" class="action-btn" @click="openApprovalDrawer(scope.row, scope.row.amountType === 'expense' ? 'pay' : 'receive')">鍙戣捣瀹℃壒</el-button>
            <el-button v-if="canHandleCurrentNode(scope.row)" size="mini" type="text" class="action-btn" @click="openApproveActionDialog(scope.row, 'agree')">閫氳繃</el-button>
            <el-button v-if="canHandleCurrentNode(scope.row)" size="mini" type="text" class="action-btn danger-text" @click="openApproveActionDialog(scope.row, 'reject')">椹冲洖</el-button>
            <el-button size="mini" type="text" class="action-btn danger-text" @click="handleDelete(scope.row)">鍒犻櫎</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-table v-else v-loading="loading" :data="planList" border header-cell-class-name="table-header-gray">
        <el-table-column label="璁″垝鍚嶇О" prop="planName" min-width="180"/>
        <el-table-column label="璁″垝缂栧彿" prop="planNo" width="140"/>
        <el-table-column label="璁″垝绫诲瀷" width="120" align="center"><template slot-scope="scope"><el-tag :type="scope.row.planType === 'receive' ? 'success' : 'danger'">{{ scope.row.planType === 'receive' ? '鏀舵璁″垝' : '浠樻璁″垝' }}</el-tag></template></el-table-column>
        <el-table-column label="璁″垝閲戦" width="140" align="right"><template slot-scope="scope">楼{{ formatAmount(scope.row.planAmount) }}</template></el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" class="page-pagination" />
    </div>

    <el-drawer title="璐︽璇︽儏" :visible.sync="detailDrawerVisible" direction="rtl" size="560px" custom-class="detail-drawer">
      <div class="detail-drawer-body" v-if="detailData">
        <div class="detail-summary-card">
          <div class="detail-summary-title">{{ detailData.relatedContractName || '璐︽淇℃伅' }}</div>
          <div class="detail-summary-desc">鍏宠仈鍚堝悓缂栧彿锛歿{ detailData.relatedContractNumber || '-' }}</div>
          <el-tag :type="getAccountStatusType(detailData.status)" size="small">{{ getAccountStatusLabel(detailData.status) }}</el-tag>
        </div>
        <div class="detail-grid">
          <div class="detail-item"><span>璐︽鍚嶇О</span><strong>{{ detailData.accountName || '-' }}</strong></div>
          <div class="detail-item"><span>璐︽鏃ユ湡</span><strong>{{ parseTime(detailData.accountDate) }}</strong></div>
          <div class="detail-item"><span>璐︽閲戦</span><strong>楼 {{ formatAmount(detailData.amount) }}</strong></div>
          <div class="detail-item"><span>閲戦绫诲瀷</span><strong>{{ detailData.amountType === 'income' ? '鏀跺叆' : '鏀嚭' }}</strong></div>
          <div class="detail-item"><span>鍗曟嵁鍙?/span><strong>{{ detailData.orderNo || '-' }}</strong></div>
          <div class="detail-item"><span>鎴戞柟涓讳綋</span><strong>{{ detailData.ourParty || '-' }}</strong></div>
          <div class="detail-item"><span>瀵规柟涓讳綋</span><strong>{{ detailData.otherParty || '-' }}</strong></div>
          <div class="detail-item"><span>棰勮鐘舵€?/span><strong>{{ getWarningMeta(detailData).label }}</strong></div>
        </div>
        <el-card shadow="never" class="timeline-card">
          <div slot="header">瀹℃壒鏃堕棿绾?/div>
          <el-timeline>
            <el-timeline-item v-for="(item, index) in approvalTimeline" :key="index" :timestamp="item.time" :type="item.type">
              <div class="timeline-item-title">{{ item.title }}</div>
              <div class="timeline-item-desc">{{ item.desc }}</div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
        <el-card shadow="never" class="detail-remark-card">
          <div slot="header">澶囨敞 / 瀹℃壒璁板綍</div>
          <div class="detail-remark-content">{{ detailData.remark || '鏆傛棤澶囨敞' }}</div>
        </el-card>
      </div>
    </el-drawer>

    <el-drawer :title="approvalDrawerTitle" :visible.sync="approvalDrawerVisible" direction="rtl" size="520px" custom-class="approval-drawer">
      <div class="approval-drawer-body" v-if="selectedRow">
        <div class="approval-header-card">
          <div class="approval-header-title">{{ selectedApplyType === 'pay' ? '浠樻瀹℃壒娴佺▼' : '鏀舵瀹℃壒娴佺▼' }}</div>
          <div class="approval-header-desc">鎸夊鎵规祦璁剧疆涓殑涓茶鑺傜偣椤哄簭娴佽浆銆?/div>
        </div>
        <div class="approval-summary approval-summary-extended">
          <div class="summary-item"><span>鐢宠鏃ユ湡</span><strong>{{ parseTime(selectedRow.accountDate) }}</strong></div>
          <div class="summary-item"><span>閲戦绫诲瀷</span><strong>{{ selectedRow.amountType === 'income' ? '鏀跺叆' : selectedRow.amountType === 'expense' ? '鏀嚭' : (selectedRow.amountType || '-') }}</strong></div>
          <div class="summary-item"><span>閲戦</span><strong>楼 {{ formatAmount(selectedRow.amount) }}</strong></div>
          <div class="summary-item"><span>褰掑睘浜?/span><strong>{{ selectedRow.owner || '-' }}</strong></div>
          <div class="summary-item"><span>鍏宠仈鍚堝悓</span><strong>{{ selectedRow.relatedContractName || '-' }}{{ selectedRow.relatedContractNumber ? ' / ' + selectedRow.relatedContractNumber : '' }}</strong></div>
          <div class="summary-item"><span>渚涘簲鍟?/span><strong>{{ selectedRow.supplierName || selectedRow.otherParty || '-' }}</strong></div>
          <div class="summary-item summary-item-attachment"><span>闄勪欢</span>
            <div class="attachment-list" v-if="getAttachmentList(selectedRow.attachments).length">
              <el-tag v-for="(file, index) in getAttachmentList(selectedRow.attachments)" :key="index" size="mini" effect="plain">{{ getAttachmentName(file) }}</el-tag>
            </div>
            <strong v-else>-</strong>
          </div>
          <div class="summary-item"><span>鐢宠绫诲瀷</span><strong>{{ selectedApplyType === 'pay' ? '浠樻鐢宠' : '鏀舵鐢宠' }}</strong></div>
        </div>
        <el-form :model="approvalForm" ref="approvalForm" :rules="approvalRules" label-width="90px" class="approval-form-card">
          <el-form-item label="瀹℃壒娴?>
            <el-input value="鎸夊鎵规祦璁剧疆鑷姩鍖归厤鑺傜偣澶勭悊浜? disabled />
          </el-form-item>
          <el-alert title="璐︽瀹℃壒灏嗕弗鏍兼寜鐓р€滃鎵规祦璁剧疆鈥濅腑鐨勮妭鐐归『搴忔墽琛屻€? type="info" :closable="false" show-icon style="margin-bottom: 18px;" />
          <el-form-item label="瀹℃壒璇存槑" prop="remark">
            <el-input v-model="approvalForm.remark" type="textarea" :rows="5" placeholder="璇疯緭鍏ュ鎵硅鏄庛€佷粯娆句緷鎹垨鏀舵璇存槑" />
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer">
        <el-button @click="approvalDrawerVisible = false">鍙栨秷</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submitApprovalFlow">鎻愪氦瀹℃壒</el-button>
      </div>
    </el-drawer>

    <el-dialog :title="approveActionTitle" :visible.sync="approveActionDialogVisible" width="620px" append-to-body custom-class="beauty-dialog">
      <div class="approval-summary" v-if="selectedRow">
        <div class="summary-item"><span>鍚堝悓</span><strong>{{ selectedRow.relatedContractName || '-' }}</strong></div>
        <div class="summary-item"><span>鍗曟嵁鍙?/span><strong>{{ selectedRow.orderNo || '-' }}</strong></div>
        <div class="summary-item"><span>閲戦</span><strong>楼 {{ formatAmount(selectedRow.amount) }}</strong></div>
        <div class="summary-item"><span>褰撳墠鐘舵€?/span><strong>{{ getAccountStatusLabel(selectedRow.status) }}</strong></div>
      </div>
      <el-form :model="approveActionForm" ref="approveActionForm" label-width="100px">
        <el-form-item :label="selectedApproveAction === 'agree' ? '閫氳繃鎰忚' : '椹冲洖鎰忚'">
          <el-input v-model="approveActionForm.remark" type="textarea" :rows="4" :placeholder="selectedApproveAction === 'agree' ? '璇疯緭鍏ュ鎵归€氳繃鎰忚锛堝彲閫夛級' : '璇疯緭鍏ラ┏鍥炲師鍥狅紙寤鸿濉啓锛?" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer"><el-button @click="approveActionDialogVisible = false">鍙栨秷</el-button><el-button :type="selectedApproveAction === 'agree' ? 'primary' : 'danger'" :loading="submitLoading" @click="submitApproveAction">纭</el-button></div>
    </el-dialog>
    <approval-flow-setting :visible.sync="flowSettingVisible" business-type="account" title="璐︽绠＄悊" @saved="handleFlowSaved" />
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import { listAccount, delAccount, importAccount, downloadAccountTemplate, submitAccountApproval, approveAccount } from '@/api/account/account'
import { listUser } from '@/api/system/user'
import ApprovalFlowSetting from '@/components/ApprovalFlowSetting'

export default {
  name: 'Account',
  components: { ApprovalFlowSetting },
  data() {
    return {
      currentNav: 'account', scopeFilter: 'mine', activeTab: 'all', warningType: 'all', showSearch: true, loading: false, total: 0,
      ids: [], selectedRows: [], selectedRow: null, selectedApplyType: 'receive', selectedApproveAction: 'agree',
      accountList: [], planList: [{ id: 1, planName: '绀轰緥鏀舵璁″垝', planNo: 'PLAN001', planType: 'receive', planAmount: 100000 }],
      userOptions: [],
      queryParams: { pageNum: 1, pageSize: 10, contractId: null, orderNo: null, amountType: null, partyName: null, warningType: 'all', planName: null, planNo: null, scope: 'mine' },
      detailDrawerVisible: false, detailData: null,
      approvalDrawerVisible: false, approvalDrawerTitle: '鎻愪氦瀹℃壒鐢宠',
      approveActionDialogVisible: false, approveActionTitle: '瀹℃壒鎿嶄綔',
      importLoading: false, downloadLoading: false, submitLoading: false,
      approvalForm: { directLeaderDisplay: '', approver: '', handler: '', remark: '' },
      approveActionForm: { remark: '' },
      flowSettingVisible: false,
      approvalRules: {
        remark: [{ required: true, message: '请输入审批说明', trigger: 'blur' }]
      }
    }
  },
  computed: {
    ...mapGetters(['name', 'nickName']),
    scopeFilterLabel() { return { mine: '鎴戠殑', dept: '鎴戦儴闂ㄧ殑', all: '鎵€鏈夌殑' }[this.scopeFilter] },
    warningTypeLabel() { return { all: '全部', receive_overdue: '收款已逾期', receive_overdue_3d: '收款逾期1-3天', receive_overdue_7d: '收款逾期4-7天', receive_overdue_30d: '收款逾期8-30天', receive_overdue_30p: '收款逾期30天以上', pay_overdue: '付款已逾期', pay_overdue_3d: '付款逾期1-3天', pay_overdue_7d: '付款逾期4-7天', pay_overdue_30d: '付款逾期8-30天', pay_overdue_30p: '付款逾期30天以上', receive_due_today: '收款今日到期', receive_due_1d: '收款明日到期', receive_due_3d: '收款2-3天内到期', receive_due_7d: '收款4-7天内到期', receive_due_15d: '收款8-15天内到期', receive_due_30d: '收款16-30天内到期', pay_due_today: '付款今日到期', pay_due_1d: '付款明日到期', pay_due_3d: '付款2-3天内到期', pay_due_7d: '付款4-7天内到期', pay_due_15d: '付款8-15天内到期', pay_due_30d: '付款16-30天内到期', normal: '正常' }[this.warningType] },
    approvalTimeline() {
      if (!this.detailData) return []
      const remark = this.detailData.remark || ''
      const items = []
      items.push({ title: '鍒涘缓璐︽', desc: '璐︽璁板綍宸插垱寤?, time: this.parseTime(this.detailData.createTime || this.detailData.accountDate), type: 'primary' })
      if (remark.includes('[瀹℃壒鐢宠]')) items.push({ title: '鎻愪氦瀹℃壒', desc: this.extractRemarkSection(remark, '瀹℃壒鐢宠'), time: this.parseTime(this.detailData.updateTime || this.detailData.accountDate), type: 'warning' })
      if (remark.includes('[瀹℃壒缁撴灉]')) {
        const pieces = remark.split('；').filter(item => item.includes('[审批结果]'))
        pieces.forEach(piece => {
          const desc = piece.replace('[瀹℃壒缁撴灉]', '').trim()
          items.push({ title: desc.includes('椹冲洖') ? '瀹℃壒椹冲洖' : '瀹℃壒閫氳繃', desc, time: this.parseTime(this.detailData.updateTime || this.detailData.accountDate), type: desc.includes('椹冲洖') ? 'danger' : 'success' })
        })
      }
      return items
    }
  },
  created() { this.applyRouteQuery(); this.getList(); this.getUserOptions() },
  watch: { '$route.query': { deep: true, handler() { this.applyRouteQuery() } } },
  methods: {
    openFlowSetting() { this.flowSettingVisible = true },
    handleFlowSaved() { this.getList() },
    applyRouteQuery() {
      const query = (this.$route && this.$route.query) || {}
      if (query.nav && ['account', 'plan'].includes(query.nav)) this.currentNav = query.nav
      if (query.scope && ['mine', 'dept', 'all'].includes(query.scope)) this.scopeFilter = query.scope
      if (query.warningType) {
        this.warningType = query.warningType
        this.queryParams.warningType = query.warningType
      }
      if (this.warningType !== 'all') this.activeTab = this.warningType.indexOf('receive') > -1 ? 'receive' : 'pay'
    },
    handleNavClick(nav) { this.currentNav = nav; this.activeTab = 'all'; this.getList() },
    handleScopeChange() { this.queryParams.scope = this.scopeFilter; this.getList() },
    handleTabClick(tab) { this.activeTab = tab.name; this.getList() },
    handleWarningTypeChange(val) {
      this.queryParams.warningType = val
      if (val === 'all' || val === 'normal') this.activeTab = 'all'
      else this.activeTab = val.indexOf('receive') > -1 ? 'receive' : 'pay'
      this.getList()
    },
    handleQuery() { this.queryParams.pageNum = 1; this.queryParams.warningType = this.warningType; this.getList() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, contractId: null, orderNo: null, amountType: null, partyName: null, warningType: 'all', planName: null, planNo: null, scope: this.scopeFilter }; this.warningType = 'all'; this.activeTab = 'all'; this.getList() },
    handleSelectionChange(selection) { this.selectedRows = selection; this.ids = selection.map(item => item.id); this.selectedRow = selection.length === 1 ? selection[0] : null },
    getUserOptions() {
      listUser({ status: '0', pageNum: 1, pageSize: 1000 }).then(res => {
        this.userOptions = res.rows || []
      }).catch(() => {
        this.userOptions = []
      })
    },
    getList() {
      if (this.currentNav !== 'account') { this.total = this.planList.length; return }
      this.loading = true
      this.queryParams.warningType = this.warningType
      listAccount(this.queryParams).then(res => {
        let rows = res.rows || []
        if (this.activeTab === 'receive') rows = rows.filter(i => i.amountType === 'income')
        if (this.activeTab === 'pay') rows = rows.filter(i => i.amountType === 'expense')
        this.accountList = rows
        this.total = res.total !== undefined ? res.total : rows.length
      }).finally(() => { this.loading = false })
    },
    getSingleSelectedRow() {
      if (this.selectedRows.length !== 1) {
        this.$message.warning('请先在下方列表中勾选一条账款记录')
        return null
      }
      return this.selectedRows[0]
    },
    handleApplyReceive() {
      const row = this.getSingleSelectedRow()
      if (!row) return
      if (row.amountType !== 'income') return this.$message.warning('当前选中记录不是收入类型，不能申请收款')
      this.openApprovalDrawer(row, 'receive')
    },
    handleApplyPay() {
      const row = this.getSingleSelectedRow()
      if (!row) return
      if (row.amountType !== 'expense') return this.$message.warning('当前选中记录不是支出类型，不能申请付款')
      this.openApprovalDrawer(row, 'pay')
    },
    handleDetail(row) {
      this.detailData = { ...row }
      this.detailDrawerVisible = true
    },
    showApprovalEntry(row) {
      return row && row.status !== 'approving' && row.status !== 'approved'
    },
    canHandleCurrentNode(row) {
      if (!row || row.status !== 'approving') return false
      const assignee = this.getCurrentNodeAssigneeUserName(row)
      return !!assignee && [this.name, this.nickName].includes(assignee)
    },
    getCurrentNodeAssigneeUserName(row) {
      if (!row) return ''
      if (row.currentApprovalNode === 'node1') return row.directLeader || ''
      if (row.currentApprovalNode === 'node2') return row.approver || ''
      if (row.currentApprovalNode === 'node3') return row.handler || ''
      return ''
    },
    getCurrentNodeAssignee(row) {
      const assignee = this.getCurrentNodeAssigneeUserName(row)
      return assignee || '-'
    },
    getCurrentNodeMeta(row) {
      const node = row && row.currentApprovalNode
      return ({ node1: { label: '审批节点1', type: 'warning' }, node2: { label: '审批节点2', type: 'warning' }, node3: { label: '审批节点3', type: 'primary' }, finished: { label: '已完成', type: 'success' }, rejected: { label: '已驳回', type: 'danger' } })[node] || { label: '-', type: 'info' }
    },
    openApprovalDrawer(row, applyType) {
      this.selectedRow = row
      this.selectedApplyType = applyType
      this.approvalDrawerTitle = applyType === 'pay' ? '提交付款审批' : '提交收款审批'
      this.approvalForm = { directLeaderDisplay: '按审批流设置自动匹配', approver: '', handler: '', remark: '' }
      this.approvalDrawerVisible = true
      this.$nextTick(() => { this.$refs.approvalForm && this.$refs.approvalForm.clearValidate() })
    },
    submitApprovalFlow() {
      this.$refs.approvalForm.validate(valid => {
        if (!valid || !this.selectedRow) return
        this.submitLoading = true
        submitAccountApproval({ id: this.selectedRow.id, applyType: this.selectedApplyType, remark: this.approvalForm.remark }).then(() => {
          this.$message.success(this.selectedApplyType === 'pay' ? '付款审批已提交' : '收款审批已提交')
          this.approvalDrawerVisible = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    openApproveActionDialog(row, action) {
      this.selectedRow = row
      this.selectedApproveAction = action
      this.approveActionTitle = action === 'agree' ? '审批通过' : '审批驳回'
      this.approveActionForm = { remark: '' }
      this.approveActionDialogVisible = true
    },
    submitApproveAction() {
      if (!this.selectedRow) return
      this.submitLoading = true
      approveAccount({ id: this.selectedRow.id, action: this.selectedApproveAction, remark: this.approveActionForm.remark }).then(() => {
        this.$message.success(this.selectedApproveAction === 'agree' ? '审批已通过' : '审批已驳回')
        this.approveActionDialogVisible = false
        this.getList()
      }).finally(() => { this.submitLoading = false })
    },
    handleAddPlan() { this.$message.info('账款计划新增保留') },
    handleImport() {
      const input = this.$refs.importInput
      if (!input) return
      input.value = ''
      input.click()
    },
    handleImportFileChange(event) {
      const file = event.target.files && event.target.files[0]
      if (!file) return
      const fileName = file.name.toLowerCase()
      if (!fileName.endsWith('.xls') && !fileName.endsWith('.xlsx')) {
        this.$message.error('请选择 Excel 文件')
        event.target.value = ''
        return
      }
      this.importLoading = true
      importAccount(file).then(res => {
        const count = typeof res === 'object' && res !== null ? (res.data || 0) : 0
        this.$message.success(`导入成功，共导入 ${count} 条数据`)
        this.getList()
      }).catch(err => {
        const msg = err && err.message ? err.message : '导入失败'
        this.$message.error(msg)
      }).finally(() => {
        this.importLoading = false
        event.target.value = ''
      })
    },
    handleDownloadTemplate() {
      this.downloadLoading = true
      downloadAccountTemplate().then(data => {
        const blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
        const url = window.URL.createObjectURL(blob)
        const link = document.createElement('a')
        link.href = url
        link.download = '账款导入模板.xlsx'
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
        this.$message.success('模板下载成功')
      }).catch(err => {
        const msg = err && err.message ? err.message : '模板下载失败'
        this.$message.error(msg)
      }).finally(() => {
        this.downloadLoading = false
      })
    },
    handleDelete(row) {
      const id = row && row.id ? row.id : this.ids[0]
      if (!id) return this.$message.warning('请选择数据')
      delAccount(id).then(() => { this.$message.success('删除成功'); this.getList() })
    },
    getWarningMeta(row) {
      if (!row) return { key: 'normal', label: '姝ｅ父', tagType: 'success' }
      if (row.status === 'approved' || row.status === 'done') return { key: 'normal', label: '姝ｅ父', tagType: 'success' }
      if (!row.accountDate) return { key: 'normal', label: '姝ｅ父', tagType: 'success' }
      const target = new Date(row.accountDate)
      const today = new Date()
      const current = new Date(today.getFullYear(), today.getMonth(), today.getDate())
      const due = new Date(target.getFullYear(), target.getMonth(), target.getDate())
      const diff = Math.floor((due - current) / 86400000)
      const receive = row.amountType === 'income'
      const prefix = receive ? '鏀舵' : '浠樻'
      if (diff < 0) {
        const overdueDays = Math.abs(diff)
        if (overdueDays <= 3) return { key: receive ? 'receive_overdue_3d' : 'pay_overdue_3d', label: `${prefix}閫炬湡1-3澶ー, tagType: 'danger' }
        if (overdueDays <= 7) return { key: receive ? 'receive_overdue_7d' : 'pay_overdue_7d', label: `${prefix}閫炬湡4-7澶ー, tagType: 'danger' }
        if (overdueDays <= 30) return { key: receive ? 'receive_overdue_30d' : 'pay_overdue_30d', label: `${prefix}閫炬湡8-30澶ー, tagType: 'danger' }
        return { key: receive ? 'receive_overdue_30p' : 'pay_overdue_30p', label: `${prefix}逾期30天以上`, tagType: 'danger' }
      }
      if (diff === 0) return { key: receive ? 'receive_due_today' : 'pay_due_today', label: `${prefix}浠婃棩鍒版湡`, tagType: 'warning' }
      if (diff === 1) return { key: receive ? 'receive_due_1d' : 'pay_due_1d', label: `${prefix}鏄庢棩鍒版湡`, tagType: 'warning' }
      if (diff <= 3) return { key: receive ? 'receive_due_3d' : 'pay_due_3d', label: `${prefix}2-3澶╁唴`, tagType: 'warning' }
      if (diff <= 7) return { key: receive ? 'receive_due_7d' : 'pay_due_7d', label: `${prefix}4-7澶╁唴`, tagType: 'warning' }
      if (diff <= 15) return { key: receive ? 'receive_due_15d' : 'pay_due_15d', label: `${prefix}8-15澶╁唴`, tagType: 'warning' }
      if (diff <= 30) return { key: receive ? 'receive_due_30d' : 'pay_due_30d', label: `${prefix}16-30澶╁唴`, tagType: 'warning' }
      return { key: 'normal', label: '姝ｅ父', tagType: 'success' }
    },
    extractRemarkSection(remark, tag) {
      const match = remark.match(new RegExp(`\\[${tag}\\]([^锛沒+)`))
      return match ? match[1].trim() : `${tag}璁板綍`
    },
    formatUserOption(user) {
      if (!user) return '-'
      const label = user.nickName || user.userName || '-'
      return user.userName && user.nickName ? `${label}（${user.userName}）` : label
    },
    getAttachmentList(value) {
      if (!value) return []
      if (Array.isArray(value)) return value.filter(Boolean)
      const text = String(value).trim()
      if (!text) return []
      try {
        const parsed = JSON.parse(text)
        if (Array.isArray(parsed)) return parsed.filter(Boolean)
      } catch (e) {}
      return text.split(',').map(item => item.trim()).filter(Boolean)
    },
    getAttachmentName(file) {
      if (!file) return '-'
      const value = String(file)
      const normalized = value.replace(/\\/g, '/')
      const parts = normalized.split('/')
      return parts[parts.length - 1] || value
    },
    formatAmount(v) { if (v === null || v === undefined || v === '') return '0.00'; const n = Number(v); return isNaN(n) ? v : n.toFixed(2) },
    parseTime(v) { if (!v) return '-'; const d = new Date(v); if (isNaN(d.getTime())) return v; return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}` },
    getAccountStatusType(status) { return ({ pending: 'info', approving: 'warning', approved: 'success', rejected: 'danger', partial: 'warning', done: 'success' })[status] || 'info' },
    getAccountStatusLabel(status) { return ({ pending: '寰呭鐞?, approving: '瀹℃壒涓?, approved: '瀹℃壒閫氳繃', rejected: '瀹℃壒椹冲洖', partial: '閮ㄥ垎瀹屾垚', done: '宸插畬鎴? })[status] || '寰呭鐞? }
  }
}
</script>

<style scoped lang="scss">
.account-manage-page { padding: 20px; background: linear-gradient(180deg, #f6f8fb 0%, #f4f6fa 100%); min-height: 100vh; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 24px; font-weight: 700; margin: 0 0 6px; color: #1f2d3d; }
.page-desc { margin: 0; color: #909399; font-size: 13px; }
.nav-panel { display: grid; grid-template-columns: repeat(2, 1fr); gap: 15px; margin-bottom: 20px; }
.nav-card { background: #fff; border: 1px solid #e9edf3; border-radius: 16px; padding: 18px; display: flex; align-items: center; cursor: pointer; transition: all 0.3s ease; box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04); &:hover { transform: translateY(-4px); box-shadow: 0 12px 28px rgba(31, 45, 61, 0.08); } &.active { border-color: #409eff; background: linear-gradient(180deg, #f6fbff 0%, #eef6ff 100%); box-shadow: 0 10px 24px rgba(64, 158, 255, 0.12); } }
.nav-card-icon { width: 46px; height: 46px; border-radius: 14px; display: flex; align-items: center; justify-content: center; margin-right: 12px; color: #fff; font-size: 22px; flex-shrink: 0; }
.nav-card-icon.account { background: linear-gradient(135deg, #409eff, #67c3ff); }
.nav-card-icon.plan { background: linear-gradient(135deg, #67c23a, #95d475); }
.nav-card-content { min-width: 0; }
.nav-card-title { font-size: 16px; font-weight: 600; color: #303133; margin-bottom: 4px; }
.nav-card-desc { font-size: 12px; color: #909399; line-height: 1.5; }
.scope-card, .tab-card, .search-card { border: 1px solid #ebeef5; border-radius: 16px; box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04); margin-bottom: 18px; }
.main-content > .el-table { width: 100%; margin-bottom: 18px; background: #fff; border: 1px solid #ebeef5; border-radius: 16px; overflow: hidden; box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04); }
.scope-wrap, .scope-left, .scope-right, .toolbar-left, .search-header { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.scope-wrap, .search-header { justify-content: space-between; }
.scope-label { font-size: 14px; font-weight: 600; color: #303133; }
.tab-card { ::v-deep .el-tabs__nav-wrap::after { height: 1px; background-color: #eef1f5; } ::v-deep .el-tabs__item.is-active { font-weight: 700; color: #409eff; } }
.warning-filter-bar { margin-top: 8px; }
.search-title { display: flex; align-items: center; gap: 8px; color: #303133; font-weight: 600; }
.search-title i { color: #409eff; }
.query-form ::v-deep .el-form-item { margin-bottom: 16px; margin-right: 10px; }
.toolbar { margin-bottom: 18px; display: flex; gap: 10px; align-items: flex-start; justify-content: space-between; flex-wrap: wrap; }
.toolbar-tip { flex: 1; min-width: 260px; }
.main-content > .el-table::v-deep .el-table__body-wrapper { border-radius: 0 0 16px 16px; }
.table-header-gray { background: #f8fafc !important; color: #606266; font-weight: 700; }
::v-deep .el-table__row:hover > td { background: #f5faff !important; }
::v-deep .el-table td, ::v-deep .el-table th { padding: 12px 0; }
.page-pagination { margin-top: 20px; padding: 0 16px 16px; display: flex; justify-content: flex-end; }
.action-btn { font-size: 14px; font-weight: 600; }
.account-table ::v-deep .action-column .cell { line-height: 2; }
.detail-drawer ::v-deep .el-drawer__header, .approval-drawer ::v-deep .el-drawer__header { margin-bottom: 0; padding: 20px 24px; border-bottom: 1px solid #ebeef5; }
.detail-drawer ::v-deep .el-drawer__body, .approval-drawer ::v-deep .el-drawer__body { background: #f7f9fc; padding: 0; display: flex; flex-direction: column; height: 100%; }
.detail-drawer-body, .approval-drawer-body { flex: 1; overflow: auto; padding: 20px 24px; }
.detail-summary-card, .approval-header-card { padding: 18px 20px; border-radius: 16px; background: linear-gradient(135deg, #eef6ff 0%, #f7fbff 100%); border: 1px solid #dceafd; margin-bottom: 18px; }
.detail-summary-title, .approval-header-title { font-size: 18px; font-weight: 700; color: #1f2d3d; margin-bottom: 6px; }
.detail-summary-desc, .approval-header-desc { color: #6b7280; font-size: 12px; line-height: 1.7; }
.detail-grid, .approval-summary { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px 16px; margin-bottom: 18px; }
.detail-item, .summary-item { display: flex; flex-direction: column; padding: 14px 16px; background: #fff; border-radius: 14px; border: 1px solid #ebeef5; }
.detail-item span, .summary-item span { font-size: 12px; color: #909399; margin-bottom: 6px; }
 .detail-item strong, .summary-item strong { color: #303133; word-break: break-all; }
.approval-summary-extended { grid-template-columns: repeat(2, 1fr); }
.summary-item-attachment { grid-column: 1 / -1; }
.attachment-list { display: flex; flex-wrap: wrap; gap: 8px; }
.timeline-card, .detail-remark-card, .approval-form-card { border-radius: 16px; border: 1px solid #ebeef5; margin-bottom: 18px; }
.timeline-item-title { font-weight: 700; color: #303133; margin-bottom: 4px; }
.timeline-item-desc, .detail-remark-content { color: #606266; line-height: 1.8; white-space: pre-wrap; }
.drawer-footer, .dialog-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 24px 20px; border-top: 1px solid #f0f2f5; background: #fff; }
.danger-text { color: #f56c6c; }
::v-deep .beauty-dialog { .el-dialog { border-radius: 18px; overflow: hidden; } .el-dialog__header { padding: 20px 24px 10px; border-bottom: 1px solid #f0f2f5; } .el-dialog__title { font-size: 18px; font-weight: 700; color: #303133; } .el-dialog__body { padding: 20px 24px; background: #f7f9fc; } .el-dialog__footer { padding: 14px 24px 20px; border-top: 1px solid #f0f2f5; background: #fff; } }
::v-deep .el-card__body { padding: 18px 20px; }
::v-deep .el-input__inner, ::v-deep .el-textarea__inner, ::v-deep .el-button { border-radius: 10px; }
@media (max-width: 1000px) { .nav-panel, .detail-grid, .approval-summary { grid-template-columns: 1fr; } }
@media (max-width: 768px) { .toolbar { flex-direction: column; align-items: stretch; } .scope-wrap, .search-header { align-items: flex-start; } }
</style>


