<template>
  <div class="app-container borrow-manage-page">
    <div class="page-header">
      <div>
        <h2 class="page-title">鍚堝悓鍊熼槄</h2>
        <p class="page-desc">缁熶竴绠＄悊鍚堝悓鍊熼槄鐢宠銆佸鎵规祦杞€佸綊杩樼櫥璁颁笌瓒呮湡璺熻釜</p>
      </div>
    </div>

    <el-card shadow="never" class="scope-card tab-card">
      <div class="scope-wrap">
        <div class="scope-left">
          <span class="scope-label">鍊熼槄鐘舵€?/span>
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="鍏ㄩ儴" name="all" />
            <el-tab-pane label="瀹℃壒涓? name="pending" />
            <el-tab-pane label="鍊熼槄涓? name="borrowing" />
            <el-tab-pane label="宸插綊杩? name="returned" />
            <el-tab-pane label="宸查€炬湡" name="overdue" />
          </el-tabs>
        </div>
        <div class="scope-right">
          <el-tag size="small" type="primary">{{ activeTabLabel }}</el-tag>
          <el-tag size="small" effect="plain">鍏?{{ total }} 鏉?/el-tag>
        </div>
      </div>
    </el-card>

    <el-card shadow="never" class="search-card">
      <div class="search-header">
        <div class="search-title"><i class="el-icon-search"></i><span>绛涢€夋潯浠?/span></div>
        <el-button type="text" @click="showSearch = !showSearch">{{ showSearch ? '鏀惰捣' : '灞曞紑' }}<i :class="showSearch ? 'el-icon-arrow-up' : 'el-icon-arrow-down'" /></el-button>
      </div>
      <el-form v-show="showSearch" :model="queryParams" ref="queryForm" size="small" label-width="90px" class="query-form">
        <div class="form-grid">
          <el-form-item label="鍊熼槄鍗曞彿" prop="borrowNo"><el-input v-model="queryParams.borrowNo" clearable placeholder="璇疯緭鍏ュ€熼槄鍗曞彿" /></el-form-item>
          <el-form-item label="鍊熼槄浜? prop="borrower"><el-input v-model="queryParams.borrower" clearable placeholder="璇疯緭鍏ュ€熼槄浜? /></el-form-item>
          <el-form-item label="鍊熼槄閮ㄩ棬" prop="borrowDepartment"><el-input v-model="queryParams.borrowDepartment" clearable placeholder="璇疯緭鍏ュ€熼槄閮ㄩ棬" /></el-form-item>
          <el-form-item label="鍚堝悓鍚嶇О" prop="contractName"><el-input v-model="queryParams.contractName" clearable placeholder="璇疯緭鍏ュ悎鍚屽悕绉? /></el-form-item>
          <el-form-item label="鍚堝悓缂栧彿" prop="contractNumber"><el-input v-model="queryParams.contractNumber" clearable placeholder="璇疯緭鍏ュ悎鍚岀紪鍙? /></el-form-item>
          <el-form-item label="鍊熼槄鏃ユ湡" prop="borrowDate"><el-date-picker v-model="queryParams.borrowDate" type="date" value-format="yyyy-MM-dd" style="width:100%" placeholder="璇烽€夋嫨鍊熼槄鏃ユ湡" /></el-form-item>
          <el-form-item label="棰勮褰掕繕" prop="expectedReturnDate"><el-date-picker v-model="queryParams.expectedReturnDate" type="date" value-format="yyyy-MM-dd" style="width:100%" placeholder="璇烽€夋嫨棰勮褰掕繕鏃ユ湡" /></el-form-item>
          <el-form-item label="瀹℃壒鐘舵€? prop="approvalStatus"><el-select v-model="queryParams.approvalStatus" clearable style="width:100%"><el-option label="鑽夌" value="draft"/><el-option label="瀹℃壒涓? value="pending"/><el-option label="瀹℃壒閫氳繃" value="approved"/><el-option label="瀹℃壒椹冲洖" value="rejected"/></el-select></el-form-item>
        </div>
        <div class="search-actions"><el-button type="primary" icon="el-icon-search" @click="handleQuery">鏌ヨ</el-button><el-button icon="el-icon-refresh" @click="resetQuery">閲嶇疆</el-button></div>
      </el-form>
    </el-card>

    <div class="toolbar">
      <div class="toolbar-left">
        <el-button type="primary" icon="el-icon-plus" @click="handleAddBorrow">鍙戣捣鍊熼槄</el-button>
        <el-button plain icon="el-icon-setting" @click="openFlowSetting">瀹℃壒娴佽缃?/el-button>
      </div>
      <div class="toolbar-tip">
        <el-alert title="寤鸿鍏堥€夋嫨鍚堝悓锛屽啀濉啓鍊熼槄淇℃伅骞舵彁浜ゅ鎵癸紱瀹℃壒閫氳繃鍚庤繘鍏ュ€熼槄涓€? type="info" :closable="false" show-icon />
      </div>
    </div>

    <el-table v-loading="loading" :data="borrowList" @selection-change="handleSelectionChange" border stripe class="modern-table" header-cell-class-name="table-header-gray">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="鍊熼槄鍗曞彿" prop="borrowNo" width="140" align="center" />
      <el-table-column label="鍚堝悓鍚嶇О" prop="contractName" min-width="180" show-overflow-tooltip />
      <el-table-column label="鍚堝悓缂栧彿" prop="contractNumber" width="160" show-overflow-tooltip />
      <el-table-column label="鍊熼槄浜? prop="borrower" width="110" align="center" />
      <el-table-column label="鍊熼槄閮ㄩ棬" prop="borrowDepartment" min-width="120" align="center" show-overflow-tooltip />
      <el-table-column label="鍊熼槄鏃ユ湡" width="120" align="center"><template slot-scope="scope">{{ parseDate(scope.row.borrowDate) }}</template></el-table-column>
      <el-table-column label="棰勮褰掕繕" width="120" align="center"><template slot-scope="scope">{{ parseDate(scope.row.expectedReturnDate) }}</template></el-table-column>
      <el-table-column label="鍚堝悓閲戦" width="130" align="right"><template slot-scope="scope">楼 {{ formatMoney(scope.row.contractAmount) }}</template></el-table-column>
      <el-table-column label="鍊熼槄鐘舵€? width="110" align="center"><template slot-scope="scope"><el-tag :type="getBorrowStatusMeta(scope.row).type" size="small">{{ getBorrowStatusMeta(scope.row).label }}</el-tag></template></el-table-column>
      <el-table-column label="瀹℃壒鐘舵€? width="110" align="center"><template slot-scope="scope"><el-tag :type="getApprovalStatusMeta(scope.row.approvalStatus).type" size="small">{{ getApprovalStatusMeta(scope.row.approvalStatus).label }}</el-tag></template></el-table-column>
      <el-table-column label="鎿嶄綔" align="center" fixed="right" width="320">
        <template slot-scope="scope">
          <el-button size="mini" type="text" class="action-btn" @click="handleDetail(scope.row)">璇︽儏</el-button>
          <el-button v-if="showApprovalEntry(scope.row)" size="mini" type="text" class="action-btn" @click="openApprovalDrawer(scope.row)">鍙戣捣瀹℃壒</el-button>
          <el-button v-if="canHandleCurrentNode(scope.row)" size="mini" type="text" class="action-btn" @click="openApproveDialog(scope.row, 'agree')">閫氳繃</el-button>
          <el-button v-if="canHandleCurrentNode(scope.row)" size="mini" type="text" class="action-btn danger-text" @click="openApproveDialog(scope.row, 'reject')">椹冲洖</el-button>
          <el-button v-if="showReturnEntry(scope.row)" size="mini" type="text" class="action-btn" @click="openReturnDialog(scope.row)">鐧昏褰掕繕</el-button>
          <el-button size="mini" type="text" class="action-btn danger-text" @click="handleDelete(scope.row)">鍒犻櫎</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" class="page-pagination" />

    <el-dialog title="閫夋嫨鍚堝悓" :visible.sync="contractDialogVisible" width="980px" append-to-body custom-class="beauty-dialog large-dialog">
      <el-form :model="contractQueryParams" ref="contractQueryForm" class="contract-query-form">
        <div class="form-grid contract-grid">
          <el-form-item label="鍚堝悓鍚嶇О"><el-input v-model="contractQueryParams.contractName" clearable /></el-form-item>
          <el-form-item label="鍚堝悓缂栧彿"><el-input v-model="contractQueryParams.contractNumber" clearable /></el-form-item>
          <el-form-item label="瀵规柟涓讳綋"><el-input v-model="contractQueryParams.otherPartyName" clearable /></el-form-item>
          <el-form-item label="鎴戞柟涓讳綋"><el-input v-model="contractQueryParams.myPartyName" clearable /></el-form-item>
        </div>
        <div class="search-actions"><el-button type="primary" @click="loadContractList">鏌ヨ</el-button><el-button @click="resetContractQuery">閲嶇疆</el-button></div>
      </el-form>
      <div class="dialog-table-wrap">
        <el-table v-loading="contractLoading" :data="contractList" border stripe highlight-current-row>
          <el-table-column width="60" align="center">
            <template slot-scope="scope">
              <el-radio v-model="selectedContractId" :label="scope.row.id" @change="selectContractRow(scope.row)"><span style="display:none">{{ scope.row.id }}</span></el-radio>
            </template>
          </el-table-column>
          <el-table-column label="鍚堝悓鍚嶇О" prop="contractName" min-width="200" show-overflow-tooltip/>
          <el-table-column label="鍚堝悓缂栧彿" prop="contractNumber" width="160"/>
          <el-table-column label="鍚堝悓绫诲瀷" prop="category" width="130" show-overflow-tooltip/>
          <el-table-column label="瀵规柟涓讳綋" prop="otherPartyName" min-width="160" show-overflow-tooltip/>
          <el-table-column label="鎴戞柟涓讳綋" prop="myPartyName" min-width="160" show-overflow-tooltip/>
          <el-table-column label="鍚堝悓閲戦" prop="totalAmount" width="130"><template slot-scope="scope">楼 {{ formatMoney(scope.row.totalAmount) }}</template></el-table-column>
        </el-table>
      </div>
      <pagination v-show="contractTotal > 0" :total="contractTotal" :page.sync="contractQueryParams.pageNum" :limit.sync="contractQueryParams.pageSize" @pagination="loadContractList" class="page-pagination contract-pagination" />
      <div slot="footer" class="dialog-footer"><el-button @click="contractDialogVisible = false">鍙栨秷</el-button><el-button type="primary" :disabled="!selectedContractId" @click="confirmContractSelection">纭骞剁户缁?/el-button></div>
    </el-dialog>

    <el-dialog :title="dialogTitle" :visible.sync="open" width="920px" append-to-body custom-class="beauty-dialog borrow-dialog">
      <div class="borrow-layout">
        <div class="borrow-side-card" v-if="selectedContract || form.contractName">
          <div class="side-card-title">鍚堝悓淇℃伅</div>
          <div class="side-info-item"><span>鍚堝悓鍚嶇О</span><strong>{{ (selectedContract && selectedContract.contractName) || form.contractName || '-' }}</strong></div>
          <div class="side-info-item"><span>鍚堝悓缂栧彿</span><strong>{{ (selectedContract && selectedContract.contractNumber) || form.contractNumber || '-' }}</strong></div>
          <div class="side-info-item"><span>鍚堝悓绫诲瀷</span><strong>{{ (selectedContract && selectedContract.category) || form.contractType || '-' }}</strong></div>
          <div class="side-info-item"><span>鎴戞柟涓讳綋</span><strong>{{ (selectedContract && selectedContract.myPartyName) || form.ourParty || '-' }}</strong></div>
          <div class="side-info-item"><span>瀵规柟涓讳綋</span><strong>{{ (selectedContract && selectedContract.otherPartyName) || form.otherParty || '-' }}</strong></div>
          <div class="side-info-item"><span>鍚堝悓閲戦</span><strong>楼 {{ formatMoney((selectedContract && selectedContract.totalAmount) || form.contractAmount) }}</strong></div>
        </div>
        <div class="borrow-form-panel">
          <div class="borrow-panel-header">
            <div class="panel-title">{{ dialogTitle }}</div>
            <div class="panel-desc">鍏堥€夋嫨鍚堝悓锛屽啀濉啓鍊熼槄浜恒€佸€熼槄閮ㄩ棬銆佸€熼槄鍘熷洜鍜岄璁″綊杩樻棩鏈?/div>
          </div>
          <el-form :model="form" ref="form" :rules="rules" label-width="110px" class="dialog-form">
            <div class="dialog-grid">
              <el-form-item label="鍊熼槄鍗曞彿" prop="borrowNo"><el-input v-model="form.borrowNo" /></el-form-item>
              <el-form-item label="鍊熼槄浜? prop="borrower"><el-input v-model="form.borrower" /></el-form-item>
              <el-form-item label="鍊熼槄閮ㄩ棬" prop="borrowDepartment"><el-input v-model="form.borrowDepartment" /></el-form-item>
              <el-form-item label="鍊熼槄鏃ユ湡" prop="borrowDate"><el-date-picker v-model="form.borrowDate" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
              <el-form-item label="棰勮褰掕繕鏃ユ湡" prop="expectedReturnDate"><el-date-picker v-model="form.expectedReturnDate" type="date" value-format="yyyy-MM-dd" style="width:100%" /></el-form-item>
              <el-form-item label="鍊熼槄鐘舵€? prop="status"><el-select v-model="form.status" style="width:100%"><el-option label="鑽夌" value="draft"/><el-option label="鍊熼槄涓? value="borrowing"/><el-option label="宸插綊杩? value="returned"/><el-option label="宸查€炬湡" value="overdue"/></el-select></el-form-item>
            </div>
            <el-form-item label="鍊熼槄鍘熷洜" prop="borrowReason"><el-input v-model="form.borrowReason" type="textarea" :rows="4" /></el-form-item>
            <el-form-item label="澶囨敞" prop="remark"><el-input v-model="form.remark" type="textarea" :rows="3" /></el-form-item>
          </el-form>
        </div>
      </div>
      <div slot="footer" class="dialog-footer"><el-button @click="open = false">鍙栨秷</el-button><el-button type="primary" :loading="submitLoading" @click="submitForm">淇濆瓨鍊熼槄鍗?/el-button></div>
    </el-dialog>

    <el-drawer title="鍊熼槄璇︽儏" :visible.sync="detailDrawerVisible" direction="rtl" size="560px" custom-class="detail-drawer">
      <div class="detail-drawer-body" v-if="detailData">
        <div class="detail-summary-card">
          <div class="detail-summary-title">{{ detailData.contractName || '鍊熼槄璇︽儏' }}</div>
          <div class="detail-summary-desc">鍊熼槄鍗曞彿锛歿{ detailData.borrowNo || '-' }}</div>
          <el-tag :type="getApprovalStatusMeta(detailData.approvalStatus).type" size="small">{{ getApprovalStatusMeta(detailData.approvalStatus).label }}</el-tag>
        </div>
        <div class="detail-grid">
          <div class="detail-item"><span>鍊熼槄浜?/span><strong>{{ detailData.borrower || '-' }}</strong></div>
          <div class="detail-item"><span>鍊熼槄閮ㄩ棬</span><strong>{{ detailData.borrowDepartment || '-' }}</strong></div>
          <div class="detail-item"><span>鍊熼槄鏃ユ湡</span><strong>{{ parseDate(detailData.borrowDate) }}</strong></div>
          <div class="detail-item"><span>棰勮褰掕繕</span><strong>{{ parseDate(detailData.expectedReturnDate) }}</strong></div>
          <div class="detail-item"><span>瀹為檯褰掕繕</span><strong>{{ parseDate(detailData.actualReturnDate) }}</strong></div>
          <div class="detail-item"><span>鍊熼槄鐘舵€?/span><strong>{{ getBorrowStatusMeta(detailData).label }}</strong></div>
        </div>
        <el-card shadow="never" class="timeline-card">
          <div slot="header">鍊熼槄瀹℃壒鏃堕棿绾?/div>
          <el-timeline>
            <el-timeline-item v-for="(item, index) in borrowTimeline" :key="index" :timestamp="item.time" :type="item.type">
              <div class="timeline-item-title">{{ item.title }}</div>
              <div class="timeline-item-desc">{{ item.desc }}</div>
            </el-timeline-item>
          </el-timeline>
        </el-card>
        <el-card shadow="never" class="detail-remark-card">
          <div slot="header">鍊熼槄璇存槑 / 瀹℃壒璁板綍</div>
          <div class="detail-remark-content">{{ detailData.remark || '鏆傛棤澶囨敞' }}</div>
        </el-card>
      </div>
    </el-drawer>

    <el-drawer :title="approvalDrawerTitle" :visible.sync="approvalDrawerVisible" direction="rtl" size="520px" custom-class="approval-drawer">
      <div class="approval-drawer-body" v-if="selectedRow">
        <div class="approval-header-card">
          <div class="approval-header-title">鍚堝悓鍊熼槄瀹℃壒</div>
          <div class="approval-header-desc">按审批流设置中的串行节点顺序流转。</div>
        </div>
        <div class="approval-summary">
          <div class="summary-item"><span>鍚堝悓</span><strong>{{ selectedRow.contractName || '-' }}</strong></div>
          <div class="summary-item"><span>鍚堝悓缂栧彿</span><strong>{{ selectedRow.contractNumber || '-' }}</strong></div>
          <div class="summary-item"><span>鍊熼槄浜?/span><strong>{{ selectedRow.borrower || '-' }}</strong></div>
          <div class="summary-item"><span>棰勮褰掕繕</span><strong>{{ parseDate(selectedRow.expectedReturnDate) }}</strong></div>
        </div>
        <el-form :model="approvalForm" ref="approvalForm" :rules="approvalRules" label-width="90px" class="approval-form-card">
          <el-form-item label="瀹℃壒娴?>
            <el-input value="鎸夊鎵规祦璁剧疆鑷姩鍖归厤鑺傜偣澶勭悊浜? disabled />
          </el-form-item>
          <el-alert title="鍊熼槄瀹℃壒灏嗕弗鏍兼寜鐓р€滃鎵规祦璁剧疆鈥濅腑鐨勮妭鐐归『搴忔墽琛屻€? type="info" :closable="false" show-icon style="margin-bottom: 18px;" />
          <el-form-item label="瀹℃壒璇存槑" prop="remark"><el-input v-model="approvalForm.remark" type="textarea" :rows="5" placeholder="璇疯緭鍏ュ鎵硅鏄? /></el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer"><el-button @click="approvalDrawerVisible = false">鍙栨秷</el-button><el-button type="primary" :loading="submitLoading" @click="submitApproval">鎻愪氦瀹℃壒</el-button></div>
    </el-drawer>

    <el-dialog :title="approveActionTitle" :visible.sync="approveActionDialogVisible" width="620px" append-to-body custom-class="beauty-dialog">
      <div class="approval-summary" v-if="selectedRow">
        <div class="summary-item"><span>鍚堝悓</span><strong>{{ selectedRow.contractName || '-' }}</strong></div>
        <div class="summary-item"><span>鍊熼槄鍗曞彿</span><strong>{{ selectedRow.borrowNo || '-' }}</strong></div>
        <div class="summary-item"><span>鍊熼槄浜?/span><strong>{{ selectedRow.borrower || '-' }}</strong></div>
        <div class="summary-item"><span>褰撳墠瀹℃壒</span><strong>{{ getApprovalStatusMeta(selectedRow.approvalStatus).label }}</strong></div>
      </div>
      <el-form :model="approveActionForm" ref="approveActionForm" label-width="100px">
        <el-form-item :label="selectedApproveAction === 'agree' ? '閫氳繃鎰忚' : '椹冲洖鎰忚'"><el-input v-model="approveActionForm.remark" type="textarea" :rows="4" /></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer"><el-button @click="approveActionDialogVisible = false">鍙栨秷</el-button><el-button :type="selectedApproveAction === 'agree' ? 'primary' : 'danger'" :loading="submitLoading" @click="submitApproveAction">纭</el-button></div>
    </el-dialog>

    <el-dialog title="鐧昏褰掕繕" :visible.sync="returnDialogVisible" width="560px" append-to-body custom-class="beauty-dialog">
      <el-form :model="returnForm" ref="returnForm" :rules="returnRules" label-width="100px">
        <el-form-item label="褰掕繕璇存槑" prop="remark"><el-input v-model="returnForm.remark" type="textarea" :rows="4" placeholder="璇疯緭鍏ュ綊杩樿鏄? /></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer"><el-button @click="returnDialogVisible = false">鍙栨秷</el-button><el-button type="primary" :loading="submitLoading" @click="submitReturn">纭褰掕繕</el-button></div>
    </el-dialog>
    <approval-flow-setting :visible.sync="flowSettingVisible" business-type="borrow" title="鍊熼槄绠＄悊" @saved="handleFlowSaved" />
  </div>
</template>

<script>
import { listBorrow, getBorrow, delBorrow, addBorrow, updateBorrow, submitBorrowApproval, approveBorrow, returnBorrow } from '@/api/borrow/borrow'
import { listContractContent } from '@/api/contract/contract'
import ApprovalFlowSetting from '@/components/ApprovalFlowSetting'

const createQueryParams = () => ({ pageNum: 1, pageSize: 10, borrowNo: null, borrower: null, borrowDepartment: null, borrowDate: null, expectedReturnDate: null, contractName: null, contractNumber: null, approvalStatus: null })
const createForm = () => ({ id: null, contractId: null, borrowNo: '', borrower: '', borrowDepartment: '', borrowDate: '', expectedReturnDate: '', actualReturnDate: '', borrowReason: '', contractName: '', contractNumber: '', contractType: '', contractAmount: '', ourParty: '', otherParty: '', status: 'draft', approvalStatus: 'draft', remark: '', delFlag: '0' })

export default {
  name: 'Borrow',
  components: { ApprovalFlowSetting },
  data() {
    return {
      loading: false, total: 0, showSearch: true, activeTab: 'all', ids: [], borrowList: [],
      queryParams: createQueryParams(), open: false, dialogTitle: '鍙戣捣鍊熼槄', submitLoading: false,
      form: createForm(),
      rules: {
        contractId: [{ required: true, message: '璇烽€夋嫨鍚堝悓', trigger: 'change' }],
        borrowNo: [{ required: true, message: '璇疯緭鍏ュ€熼槄鍗曞彿', trigger: 'blur' }],
        borrower: [{ required: true, message: '璇疯緭鍏ュ€熼槄浜?, trigger: 'blur' }],
        borrowDepartment: [{ required: true, message: '璇疯緭鍏ュ€熼槄閮ㄩ棬', trigger: 'blur' }],
        borrowDate: [{ required: true, message: '璇烽€夋嫨鍊熼槄鏃ユ湡', trigger: 'change' }],
        expectedReturnDate: [{ required: true, message: '璇烽€夋嫨棰勮褰掕繕鏃ユ湡', trigger: 'change' }],
        borrowReason: [{ required: true, message: '璇疯緭鍏ュ€熼槄鍘熷洜', trigger: 'blur' }]
      },
      contractDialogVisible: false, contractLoading: false, contractList: [], contractTotal: 0,
      contractQueryParams: { pageNum: 1, pageSize: 10, contractName: '', contractNumber: '', otherPartyName: '', myPartyName: '' },
      selectedContractId: null, selectedContract: null,
      detailDrawerVisible: false, detailData: null,
      approvalDrawerVisible: false, approvalDrawerTitle: '鎻愪氦鍊熼槄瀹℃壒', approvalForm: { directLeaderDisplay: '鎸夊鎵规祦璁剧疆鑷姩鍖归厤', approver: '', handler: '', remark: '' }, approvalRules: { remark: [{ required: true, message: '璇疯緭鍏ュ鎵硅鏄?, trigger: 'blur' }] },
      approveActionDialogVisible: false, approveActionTitle: '瀹℃壒鎿嶄綔', selectedApproveAction: 'agree', approveActionForm: { remark: '' },
      returnDialogVisible: false, returnForm: { remark: '' }, returnRules: { remark: [{ required: true, message: '璇疯緭鍏ュ綊杩樿鏄?, trigger: 'blur' }] },
      selectedRow: null,
      flowSettingVisible: false
    }
  },
  computed: {
    activeTabLabel() { return { all: '鍏ㄩ儴', pending: '瀹℃壒涓?, borrowing: '鍊熼槄涓?, returned: '宸插綊杩?, overdue: '宸查€炬湡' }[this.activeTab] },
    borrowTimeline() {
      if (!this.detailData) return []
      const items = []
      items.push({ title: '鍒涘缓鍊熼槄鍗?, desc: '鍊熼槄鐢宠宸插垱寤?, time: this.parseDateTime(this.detailData.createTime || this.detailData.borrowDate), type: 'primary' })
      const remark = this.detailData.remark || ''
      if (remark.includes('[瀹℃壒鐢宠]')) items.push({ title: '鎻愪氦瀹℃壒', desc: this.extractRemarkSection(remark, '瀹℃壒鐢宠'), time: this.parseDateTime(this.detailData.updateTime || this.detailData.borrowDate), type: 'warning' })
      if (remark.includes('[瀹℃壒缁撴灉]')) {
        const desc = this.extractRemarkSection(remark, '瀹℃壒缁撴灉')
        items.push({ title: desc.includes('椹冲洖') ? '瀹℃壒椹冲洖' : '瀹℃壒閫氳繃', desc, time: this.parseDateTime(this.detailData.updateTime || this.detailData.borrowDate), type: desc.includes('椹冲洖') ? 'danger' : 'success' })
      }
      if (this.detailData.actualReturnDate) items.push({ title: '鐧昏褰掕繕', desc: '鍚堝悓鍊熼槄宸插綊杩?, time: this.parseDateTime(this.detailData.actualReturnDate), type: 'success' })
      return items
    }
  },
  created() { this.getList() },
  methods: {
    openFlowSetting() { this.flowSettingVisible = true },
    handleFlowSaved() { this.getList() },
    handleTabClick(tab) {
      this.activeTab = tab.name
      this.queryParams.pageNum = 1
      this.getList()
    },
    getList() {
      this.loading = true
      listBorrow(this.queryParams).then(response => {
        let rows = response.rows || []
        if (this.activeTab === 'pending') rows = rows.filter(item => item.approvalStatus === 'pending')
        if (this.activeTab === 'borrowing') rows = rows.filter(item => this.getBorrowStatusMeta(item).key === 'borrowing')
        if (this.activeTab === 'returned') rows = rows.filter(item => this.getBorrowStatusMeta(item).key === 'returned')
        if (this.activeTab === 'overdue') rows = rows.filter(item => this.getBorrowStatusMeta(item).key === 'overdue')
        this.borrowList = rows
        this.total = response.total || rows.length
      }).finally(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams = createQueryParams(); this.activeTab = 'all'; this.getList() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.id); this.selectedRow = selection.length === 1 ? selection[0] : null },
    handleAddBorrow() {
      this.selectedContract = null
      this.selectedContractId = null
      this.contractQueryParams.pageNum = 1
      this.contractDialogVisible = true
      this.loadContractList()
    },
    loadContractList() {
      this.contractLoading = true
      listContractContent(this.contractQueryParams).then(res => {
        this.contractList = res.rows || []
        this.contractTotal = res.total || 0
      }).finally(() => { this.contractLoading = false })
    },
    resetContractQuery() {
      this.contractQueryParams = { pageNum: 1, pageSize: 10, contractName: '', contractNumber: '', otherPartyName: '', myPartyName: '' }
      this.loadContractList()
    },
    selectContractRow(row) { this.selectedContract = row },
    confirmContractSelection() {
      if (!this.selectedContract) return this.$message.warning('璇峰厛閫夋嫨涓€涓悎鍚?)
      this.form = {
        ...createForm(),
        contractId: this.selectedContract.id,
        contractName: this.selectedContract.contractName || '',
        contractNumber: this.selectedContract.contractNumber || '',
        contractType: this.selectedContract.category || '',
        contractAmount: this.selectedContract.totalAmount || '',
        ourParty: this.selectedContract.myPartyName || '',
        otherParty: this.selectedContract.otherPartyName || '',
        borrowNo: `BORROW-${Date.now()}`,
        borrowDate: this.formatDateValue(new Date())
      }
      this.contractDialogVisible = false
      this.dialogTitle = '鍙戣捣鍊熼槄'
      this.open = true
      this.$nextTick(() => { this.$refs.form && this.$refs.form.clearValidate() })
    },
    handleDetail(row) {
      getBorrow(row.id).then(res => {
        this.detailData = res.data || row
        this.detailDrawerVisible = true
      })
    },
    handleDelete(row) {
      const id = row && row.id ? row.id : this.ids[0]
      if (!id) return this.$message.warning('璇烽€夋嫨瑕佸垹闄ょ殑鏁版嵁')
      this.$modal.confirm('鏄惁纭鍒犻櫎閫変腑鐨勫€熼槄璁板綍锛?).then(() => delBorrow(id)).then(() => {
        this.$modal.msgSuccess('鍒犻櫎鎴愬姛')
        this.getList()
      }).catch(() => {})
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        this.submitLoading = true
        const request = this.form.id ? updateBorrow(this.form) : addBorrow(this.form)
        request.then(() => {
          this.$modal.msgSuccess(this.form.id ? '淇敼鎴愬姛' : '鏂板鎴愬姛')
          this.open = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    showApprovalEntry(row) { return row && row.approvalStatus !== 'pending' && row.approvalStatus !== 'approved' },
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
    showReturnEntry(row) {
      const statusKey = this.getBorrowStatusMeta(row).key
      return row && row.approvalStatus === 'approved' && (statusKey === 'borrowing' || statusKey === 'overdue')
    },
    openApprovalDrawer(row) {
      this.selectedRow = row
      this.approvalForm = { directLeaderDisplay: '鎸夊鎵规祦璁剧疆鑷姩鍖归厤', approver: '', handler: '', remark: '' }
      this.approvalDrawerVisible = true
      this.$nextTick(() => { this.$refs.approvalForm && this.$refs.approvalForm.clearValidate() })
    },
    submitApproval() {
      this.$refs.approvalForm.validate(valid => {
        if (!valid || !this.selectedRow) return
        this.submitLoading = true
        submitBorrowApproval({ id: this.selectedRow.id, remark: this.approvalForm.remark }).then(() => {
          this.$message.success('鍊熼槄瀹℃壒宸叉彁浜?)
          this.approvalDrawerVisible = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    openApproveDialog(row, action) {
      this.selectedRow = row
      this.selectedApproveAction = action
      this.approveActionTitle = action === 'agree' ? '瀹℃壒閫氳繃' : '瀹℃壒椹冲洖'
      this.approveActionForm = { remark: '' }
      this.approveActionDialogVisible = true
    },
    submitApproveAction() {
      if (!this.selectedRow) return
      this.submitLoading = true
      approveBorrow({ id: this.selectedRow.id, action: this.selectedApproveAction, remark: this.approveActionForm.remark }).then(() => {
        this.$message.success(this.selectedApproveAction === 'agree' ? '瀹℃壒宸查€氳繃' : '瀹℃壒宸查┏鍥?)
        this.approveActionDialogVisible = false
        this.getList()
      }).finally(() => { this.submitLoading = false })
    },
    openReturnDialog(row) {
      this.selectedRow = row
      this.returnForm = { remark: '' }
      this.returnDialogVisible = true
      this.$nextTick(() => { this.$refs.returnForm && this.$refs.returnForm.clearValidate() })
    },
    submitReturn() {
      this.$refs.returnForm.validate(valid => {
        if (!valid || !this.selectedRow) return
        this.submitLoading = true
        returnBorrow({ id: this.selectedRow.id, remark: this.returnForm.remark }).then(() => {
          this.$message.success('宸插畬鎴愬綊杩樼櫥璁?)
          this.returnDialogVisible = false
          this.getList()
        }).finally(() => { this.submitLoading = false })
      })
    },
    getApprovalStatusMeta(status) {
      return ({ draft: { label: '鑽夌', type: 'info' }, pending: { label: '瀹℃壒涓?, type: 'warning' }, approved: { label: '瀹℃壒閫氳繃', type: 'success' }, rejected: { label: '瀹℃壒椹冲洖', type: 'danger' } })[status] || { label: '鑽夌', type: 'info' }
    },
    getBorrowStatusMeta(row) {
      if (!row) return { key: 'draft', label: '鑽夌', type: 'info' }
      if (row.actualReturnDate || row.status === 'returned') return { key: 'returned', label: '宸插綊杩?, type: 'success' }
      if (row.approvalStatus !== 'approved') return { key: 'draft', label: '寰呭€熷嚭', type: 'info' }
      if (row.expectedReturnDate) {
        const due = new Date(row.expectedReturnDate)
        const now = new Date()
        const dueOnly = new Date(due.getFullYear(), due.getMonth(), due.getDate())
        const todayOnly = new Date(now.getFullYear(), now.getMonth(), now.getDate())
        if (todayOnly > dueOnly) return { key: 'overdue', label: '宸查€炬湡', type: 'danger' }
      }
      return { key: 'borrowing', label: '鍊熼槄涓?, type: 'warning' }
    },
    extractRemarkSection(remark, tag) {
      const match = remark.match(new RegExp(`\\[${tag}\\]([^锛沒+)`))
      return match ? match[1].trim() : `${tag}璁板綍`
    },
    formatMoney(value) { if (value === null || value === undefined || value === '') return '0.00'; const num = Number(value); return isNaN(num) ? value : num.toFixed(2) },
    parseDate(value) { if (!value) return '-'; const d = new Date(value); if (isNaN(d.getTime())) return value; return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}` },
    parseDateTime(value) { if (!value) return '-'; const d = new Date(value); if (isNaN(d.getTime())) return value; return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}` },
    formatDateValue(date) { return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}` }
  }
}
</script>

<style scoped lang="scss">
.borrow-manage-page { padding: 20px; background: linear-gradient(180deg, #f6f8fb 0%, #f4f6fa 100%); min-height: 100vh; }
.page-header { margin-bottom: 20px; }
.page-title { font-size: 24px; font-weight: 700; margin: 0 0 6px; color: #1f2d3d; }
.page-desc { margin: 0; color: #909399; font-size: 13px; }
.scope-card, .tab-card, .search-card { border: 1px solid #ebeef5; border-radius: 16px; box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04); margin-bottom: 18px; }
.scope-wrap, .scope-left, .scope-right, .search-header, .toolbar-left { display: flex; align-items: center; gap: 12px; flex-wrap: wrap; }
.scope-wrap, .search-header { justify-content: space-between; }
.scope-label { font-size: 14px; font-weight: 600; color: #303133; }
.tab-card { ::v-deep .el-tabs__nav-wrap::after { height: 1px; background-color: #eef1f5; } ::v-deep .el-tabs__item.is-active { font-weight: 700; color: #409eff; } }
.search-title { display: flex; align-items: center; gap: 8px; color: #303133; font-weight: 600; }
.search-title i { color: #409eff; }
.form-grid, .dialog-grid { display: grid; grid-template-columns: repeat(2, 1fr); gap: 0 16px; }
.query-form .form-grid { grid-template-columns: repeat(4, 1fr); }
.query-form ::v-deep .el-form-item { margin-bottom: 16px; }
.search-actions { display: flex; gap: 10px; flex-wrap: wrap; }
.toolbar { margin-bottom: 18px; display: flex; gap: 10px; align-items: flex-start; justify-content: space-between; flex-wrap: wrap; }
.toolbar-tip { flex: 1; min-width: 260px; }
.modern-table { width: 100%; background: #fff; border: 1px solid #ebeef5; border-radius: 16px; overflow: hidden; box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04); }
.table-header-gray { background: #f8fafc !important; color: #606266; font-weight: 700; }
.modern-table ::v-deep .el-table__row:hover > td { background-color: #f5faff !important; }
.modern-table ::v-deep td, .modern-table ::v-deep th { padding: 12px 0; }
.page-pagination { margin-top: 20px; padding: 0 16px 16px; display: flex; justify-content: flex-end; }
.contract-pagination { margin-top: 12px; padding-bottom: 0; }
.borrow-layout { display: flex; gap: 18px; }
.borrow-side-card { width: 280px; padding: 18px; border-radius: 16px; background: linear-gradient(180deg, #f8fbff 0%, #f5f7fa 100%); border: 1px solid #e8eef6; box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05); }
.side-card-title { font-size: 16px; font-weight: 700; margin-bottom: 14px; color: #303133; }
.side-info-item { padding: 10px 0; border-bottom: 1px dashed #dfe6ee; display: flex; flex-direction: column; }
.side-info-item:last-child { border-bottom: none; }
.side-info-item span { font-size: 12px; color: #909399; margin-bottom: 4px; }
.side-info-item strong { font-size: 14px; color: #303133; word-break: break-all; }
.borrow-form-panel { flex: 1; }
.borrow-panel-header { margin-bottom: 18px; }
.panel-title { font-size: 18px; font-weight: 700; color: #303133; }
.panel-desc { font-size: 12px; color: #909399; margin-top: 4px; }
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
.timeline-card, .detail-remark-card, .approval-form-card { border-radius: 16px; border: 1px solid #ebeef5; margin-bottom: 18px; }
.timeline-item-title { font-weight: 700; color: #303133; margin-bottom: 4px; }
.timeline-item-desc, .detail-remark-content { color: #606266; line-height: 1.8; white-space: pre-wrap; }
.drawer-footer, .dialog-footer { display: flex; justify-content: flex-end; gap: 10px; padding: 14px 24px 20px; border-top: 1px solid #f0f2f5; background: #fff; }
.action-btn { font-size: 14px; font-weight: 600; }
.danger-text { color: #f56c6c; }
.dialog-table-wrap { max-height: 460px; overflow: auto; }
::v-deep .beauty-dialog { .el-dialog { border-radius: 18px; overflow: hidden; } .el-dialog__header { padding: 20px 24px 10px; border-bottom: 1px solid #f0f2f5; } .el-dialog__title { font-size: 18px; font-weight: 700; color: #303133; } .el-dialog__body { padding: 20px 24px; background: #f7f9fc; } .el-dialog__footer { padding: 14px 24px 20px; border-top: 1px solid #f0f2f5; background: #fff; } }
::v-deep .el-card__body { padding: 18px 20px; }
::v-deep .el-input__inner, ::v-deep .el-textarea__inner, ::v-deep .el-button { border-radius: 10px; }
@media (max-width: 1200px) { .query-form .form-grid { grid-template-columns: repeat(3, 1fr); } }
@media (max-width: 1000px) { .dialog-grid, .contract-grid, .detail-grid, .approval-summary { grid-template-columns: 1fr; } .borrow-layout { flex-direction: column; } .borrow-side-card { width: 100%; } }
@media (max-width: 768px) { .borrow-manage-page { padding: 12px; } .toolbar { flex-direction: column; align-items: stretch; } .scope-wrap, .search-header { align-items: flex-start; } .query-form .form-grid { grid-template-columns: 1fr; } }
</style>

