<template>
  <div class="app-container project-page">
    <div class="page-header"><h2 class="page-title">项目管理</h2></div>

    <div class="nav-cards">
      <el-card v-for="(item, index) in navItems" :key="index" shadow="never" class="nav-card" :class="{ 'active-card': activeTab === item.key }" @click.native="handleNavClick(item)">
        <div class="card-content"><i :class="item.icon" class="card-icon"></i><span class="card-label">{{ item.label }}</span></div>
      </el-card>
    </div>

    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="90px">
        <el-form-item label="项目名称"><el-input v-model="queryParams.projectName" clearable style="width:180px"/></el-form-item>
        <el-form-item label="项目编码"><el-input v-model="queryParams.projectCode" clearable style="width:180px"/></el-form-item>
        <el-form-item><el-button type="primary" @click="handleQuery">搜索</el-button><el-button @click="resetQuery">重置</el-button></el-form-item>
      </el-form>
    </el-card>

    <div class="toolbar">
      <el-button type="primary" icon="el-icon-plus" @click="handleAdd">新建</el-button>
      <el-button type="danger" plain icon="el-icon-delete" :disabled="!ids.length" @click="handleDelete">批量删除</el-button>
    </div>

    <el-table v-loading="loading" :data="projectList" @selection-change="handleSelectionChange" border header-cell-class-name="table-header-gray">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="项目名称" prop="projectName" min-width="180" />
      <el-table-column label="项目编码" prop="projectCode" width="160" />
      <el-table-column label="负责人" prop="personInChargeNames" width="140" />
      <el-table-column label="状态" width="100" align="center"><template slot-scope="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'info'">{{ scope.row.status === '0' ? '启用中' : '停用' }}</el-tag></template></el-table-column>
      <el-table-column label="关联合同" min-width="180"><template slot-scope="scope">{{ scope.row.relatedContractName || '-' }}</template></el-table-column>
      <el-table-column label="操作" width="200" fixed="right" align="center"><template slot-scope="scope"><el-button size="mini" type="text" @click="handleUpdate(scope.row)">编辑</el-button><el-button size="mini" type="text" @click="handleDelete(scope.row)">删除</el-button></template></el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" style="margin-top:20px;justify-content:flex-end;" />

    <el-dialog title="选择合同" :visible.sync="contractDialogVisible" width="980px" append-to-body custom-class="beauty-dialog large-dialog">
      <el-form :model="contractQueryParams" ref="contractQueryForm" class="contract-query-form">
        <div class="form-grid">
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
          <el-table-column label="合同金额" prop="totalAmount" width="130"><template slot-scope="scope">¥ {{ formatAmount(scope.row.totalAmount) }}</template></el-table-column>
        </el-table>
      </div>
      <div slot="footer" class="dialog-footer"><el-button @click="contractDialogVisible = false">取消</el-button><el-button type="primary" :disabled="!selectedContractId" @click="confirmContractSelection">确认并继续</el-button></div>
    </el-dialog>

    <el-dialog :title="formTitle" :visible.sync="formOpen" width="760px" append-to-body>
      <div class="project-dialog-layout">
        <div class="contract-side-card" v-if="selectedContract">
          <div class="side-card-title">已选合同</div>
          <div class="side-info-item"><span>合同名称</span><strong>{{ selectedContract.contractName || '-' }}</strong></div>
          <div class="side-info-item"><span>合同编号</span><strong>{{ selectedContract.contractNumber || '-' }}</strong></div>
          <div class="side-info-item"><span>对方主体</span><strong>{{ selectedContract.otherPartyName || '-' }}</strong></div>
          <div class="side-info-item"><span>我方主体</span><strong>{{ selectedContract.myPartyName || '-' }}</strong></div>
        </div>
        <div class="project-form-panel">
          <el-form ref="form" :model="form" :rules="rules" label-width="100px">
            <div class="form-grid">
              <el-form-item label="项目名称" prop="projectName"><el-input v-model="form.projectName" /></el-form-item>
              <el-form-item label="项目编码" prop="projectCode"><el-input v-model="form.projectCode" placeholder="留空自动生成" /></el-form-item>
              <el-form-item label="负责人" prop="personInCharge"><el-select v-model="form.personInCharge" multiple filterable style="width:100%"><el-option v-for="user in userList" :key="user.userId" :label="user.userName" :value="user.userId"/></el-select></el-form-item>
              <el-form-item label="开始时间"><el-date-picker v-model="form.startDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item>
              <el-form-item label="结束时间"><el-date-picker v-model="form.endDate" type="date" value-format="yyyy-MM-dd" style="width:100%"/></el-form-item>
              <el-form-item label="关联合同"><el-input :value="selectedContract ? selectedContract.contractName : ''" disabled /></el-form-item>
            </div>
            <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="4" /></el-form-item>
          </el-form>
        </div>
      </div>
      <div slot="footer"><el-button @click="formOpen = false">取 消</el-button><el-button type="primary" @click="submitForm">确 定</el-button></div>
    </el-dialog>
  </div>
</template>

<script>
import { listProject, getProject, delProject, addProject, updateProject } from '@/api/pm/project'
import { listUser } from '@/api/system/user'
import { listContractContent } from '@/api/contract/contract'

export default {
  name: 'ProjectManagement',
  data() {
    return {
      activeTab: 'mine', navItems: [{ key: 'mine', label: '我负责的', icon: 'el-icon-user' }, { key: 'all', label: '所有的', icon: 'el-icon-document-copy' }],
      loading: false, total: 0, ids: [], projectList: [], userList: [], formOpen: false, formTitle: '',
      queryParams: { pageNum: 1, pageSize: 10, projectName: undefined, projectCode: undefined },
      form: { projectId: null, projectName: '', projectCode: '', personInCharge: [], startDate: '', endDate: '', remark: '', source: '', relatedContractName: '', relatedContractNumber: '' },
      rules: { projectName: [{ required: true, message: '项目名称不能为空', trigger: 'blur' }], personInCharge: [{ required: true, message: '请选择负责人', trigger: 'change' }] },
      contractDialogVisible: false, contractLoading: false, contractList: [], selectedContractId: null, selectedContract: null,
      contractQueryParams: { contractName: '', contractNumber: '', otherPartyName: '', myPartyName: '' }
    }
  },
  created() { this.getUserList(); this.getList() },
  methods: {
    handleNavClick(item) { this.activeTab = item.key; this.getList() },
    getUserList() { listUser().then(res => { this.userList = res.rows || [] }) },
    getList() { this.loading = true; listProject(this.queryParams).then(res => { this.projectList = res.rows || []; this.total = res.total || 0 }).finally(() => { this.loading = false }) },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, projectName: undefined, projectCode: undefined }; this.getList() },
    handleSelectionChange(selection) { this.ids = selection.map(item => item.projectId) },
    handleAdd() { this.resetFormData(); this.contractDialogVisible = true; this.loadContractList() },
    handleUpdate(row) {
      const id = row && row.projectId ? row.projectId : this.ids[0]
      if (!id) return this.$message.warning('请先选择一条数据')
      getProject(id).then(res => {
        this.form = res.data || {}
        this.form.personInCharge = this.form.personInCharge ? String(this.form.personInCharge).split(',') : []
        this.formOpen = true
        this.formTitle = '修改项目'
      })
    },
    handleDelete(row) { const id = row && row.projectId ? row.projectId : this.ids[0]; if (!id) return this.$message.warning('请选择数据'); delProject(id).then(() => { this.$message.success('删除成功'); this.getList() }) },
    resetFormData() { this.form = { projectId: null, projectName: '', projectCode: '', personInCharge: [], startDate: '', endDate: '', remark: '', source: '', relatedContractId: null, relatedContractName: '', relatedContractNumber: '', relatedContractAmount: null, relatedContractType: '' } },
    loadContractList() { this.contractLoading = true; listContractContent(this.contractQueryParams).then(res => { this.contractList = res.rows || [] }).finally(() => { this.contractLoading = false }) },
    resetContractQuery() { this.contractQueryParams = { contractName: '', contractNumber: '', otherPartyName: '', myPartyName: '' }; this.loadContractList() },
    selectContractRow(row) { this.selectedContract = row },
    confirmContractSelection() {
      if (!this.selectedContract) return this.$message.warning('请先选择一个合同')
      this.contractDialogVisible = false
      this.formOpen = true
      this.formTitle = '新增项目'
      this.form.relatedContractId = this.selectedContract.id || null
      this.form.relatedContractName = this.selectedContract.contractName || ''
      this.form.relatedContractNumber = this.selectedContract.contractNumber || ''
      this.form.relatedContractAmount = this.selectedContract.totalAmount || null
      this.form.relatedContractType = this.selectedContract.amountType || ''
      this.form.source = '合同转项目'
      this.form.remark = this.form.remark || `关联合同：${this.selectedContract.contractName || ''}（${this.selectedContract.contractNumber || ''}）`
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        const payload = { ...this.form }
        if (Array.isArray(payload.personInCharge)) payload.personInCharge = payload.personInCharge.join(',')
        if (payload.projectId) {
          updateProject(payload).then(() => { this.$message.success('修改成功'); this.formOpen = false; this.getList() })
        } else {
          addProject(payload).then(() => { this.$message.success('新增成功'); this.formOpen = false; this.getList() })
        }
      })
    },
    formatAmount(v) { if (v === null || v === undefined || v === '') return '0.00'; const n = Number(v); return isNaN(n) ? v : n.toFixed(2) }
  }
}
</script>

<style scoped lang="scss">
.project-page { padding:20px; background:#f5f7fa; min-height:100vh; }
.page-title { font-size:22px; font-weight:700; margin:0 0 16px; }
.nav-cards { display:grid; grid-template-columns:repeat(2,1fr); gap:15px; margin-bottom:20px; }
.nav-card { cursor:pointer; border:1px solid #e4e7ed; } .active-card { border-color:#409eff; background:#ecf5ff; }
.card-content { display:flex; align-items:center; justify-content:center; padding:18px 0; } .card-icon { margin-right:8px; color:#409eff; }
.search-card,.toolbar { margin-bottom:16px; } .toolbar { display:flex; gap:10px; }
.table-header-gray { background:#f5f7fa !important; color:#606266; font-weight:600; }
.form-grid { display:grid; grid-template-columns:repeat(2,1fr); gap:0 16px; } .search-actions { display:flex; gap:10px; }
.dialog-table-wrap { max-height:460px; overflow:auto; } .dialog-footer { display:flex; justify-content:flex-end; gap:10px; }
.project-dialog-layout { display:flex; gap:18px; } .contract-side-card { width:260px; padding:18px; border-radius:14px; background:linear-gradient(180deg,#f8fbff 0%,#f5f7fa 100%); border:1px solid #e8eef6; }
.side-card-title { font-size:16px; font-weight:700; margin-bottom:14px; } .side-info-item { padding:10px 0; border-bottom:1px dashed #dfe6ee; display:flex; flex-direction:column; } .side-info-item:last-child { border-bottom:none; }
.side-info-item span { font-size:12px; color:#909399; margin-bottom:4px; } .side-info-item strong { font-size:14px; word-break:break-all; }
.project-form-panel { flex:1; }
@media (max-width: 1000px) { .nav-cards,.form-grid { grid-template-columns:1fr; } .project-dialog-layout { flex-direction:column; } .contract-side-card { width:100%; } }
</style>
