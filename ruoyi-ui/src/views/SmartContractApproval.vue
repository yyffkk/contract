<template>
  <div class="smart-contract-page">
    <div class="hero-section">
      <div class="hero-left">
        <div class="hero-title">智能合同审批</div>
        <div class="hero-subtitle">上传合同、完善主体信息、配置审批流程，一次完成发起</div>
      </div>
      <div class="hero-right">
        <div class="hero-stat-card">
          <span>审批模式</span>
          <strong>合同智能发起</strong>
        </div>
      </div>
    </div>

    <el-form ref="form" :model="form" :rules="rules" label-width="110px" class="page-form">
      <div class="page-grid">
        <div class="main-panel">
          <div class="section-card upload-card">
            <div class="section-header">
              <div class="section-title">合同文件</div>
              <div class="section-desc">先上传主合同，再补充附属文件</div>
            </div>

            <el-form-item label="合同文件" prop="file" class="full-row">
              <el-upload
                action=""
                drag
                :auto-upload="false"
                :limit="1"
                accept=".pdf,.doc,.docx"
                :file-list="fileList"
                :on-change="handleChangeFile"
                :on-remove="handleRemove"
                :on-exceed="handleExceed"
                class="modern-upload"
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">点击上传或将合同拖到此处</div>
                <div class="el-upload__tip">支持 PDF / DOC / DOCX，仅限 1 个文件</div>
              </el-upload>
            </el-form-item>

            <el-form-item label="附属文件" class="full-row">
              <el-upload
                action=""
                drag
                multiple
                :auto-upload="false"
                :limit="10"
                accept=".pdf,.jpg,.png,.zip"
                :file-list="attachmentList"
                :on-change="handleAttachmentChange"
                :on-remove="handleAttachmentRemove"
                :on-exceed="handleAttachmentExceed"
                class="modern-upload light"
              >
                <i class="el-icon-folder-add"></i>
                <div class="el-upload__text">可继续上传补充附件</div>
                <div class="el-upload__tip">支持 PDF / JPG / PNG / ZIP，最多 10 个</div>
              </el-upload>
            </el-form-item>
          </div>

          <div class="section-card">
            <div class="section-header">
              <div class="section-title">基础信息</div>
              <div class="section-desc">定义合同分类、编号与金额属性</div>
            </div>

            <div class="form-grid">
              <el-form-item label="合同分类" prop="category">
                <el-select v-model="form.category" placeholder="请选择" style="width: 100%">
                  <el-option label="销售合同" value="sales" />
                  <el-option label="采购合同" value="purchase" />
                  <el-option label="服务合同" value="service" />
                  <el-option label="其他" value="other" />
                </el-select>
              </el-form-item>

              <el-form-item label="合同名称" prop="contractName">
                <el-input v-model="form.contractName" placeholder="请输入合同名称" />
              </el-form-item>

              <el-form-item label="编号方式" prop="numberMode">
                <el-radio-group v-model="form.numberMode">
                  <el-radio label="manual">手动填写</el-radio>
                  <el-radio label="auto">自动生成</el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item v-if="form.numberMode === 'manual'" label="合同编号" prop="contractNumber">
                <el-input v-model="form.contractNumber" placeholder="请输入合同编号" />
              </el-form-item>

              <el-form-item label="金额类型" prop="amountType">
                <el-select v-model="form.amountType" placeholder="请选择" style="width: 100%">
                  <el-option label="收入" value="income" />
                  <el-option label="支出" value="expense" />
                  <el-option label="无金额" value="none" />
                </el-select>
              </el-form-item>

              <el-form-item v-if="form.amountType !== 'none' && form.amountType" label="合同总额" prop="totalAmount">
                <el-input v-model="form.totalAmount" placeholder="请输入合同总额">
                  <template slot="prepend">¥</template>
                </el-input>
              </el-form-item>

              <el-form-item label="期限类型" prop="termType">
                <el-select v-model="form.termType" placeholder="请选择" style="width: 100%">
                  <el-option label="固定期限" value="1" />
                  <el-option label="无固定期限" value="2" />
                </el-select>
              </el-form-item>

              <el-form-item label="签署方式" prop="signMethod">
                <el-select v-model="form.signMethod" placeholder="请选择" style="width: 100%">
                  <el-option label="纸质签署" value="paper" />
                  <el-option label="电子签署" value="electronic" />
                </el-select>
              </el-form-item>

              <el-form-item label="签署日期" prop="signDate">
                <el-date-picker v-model="form.signDate" type="date" value-format="yyyy-MM-dd" placeholder="请选择" style="width: 100%" />
              </el-form-item>

              <el-form-item label="印章类型" prop="sealType">
                <el-input v-model="form.sealType" placeholder="请输入印章类型" />
              </el-form-item>
            </div>

            <el-form-item label="合同说明" prop="description" class="full-row">
              <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入合同说明、要点或补充说明" />
            </el-form-item>
          </div>

          <div class="section-card">
            <div class="section-header">
              <div class="section-title">主体信息</div>
              <div class="section-desc">完善我方与对方主体及联系人</div>
            </div>

            <div class="dual-section">
              <div class="sub-panel">
                <div class="sub-title">我方信息</div>
                <div class="form-grid single-column">
                  <el-form-item label="我方主体" prop="myPartyName">
                    <el-input v-model="form.myPartyName" placeholder="请输入我方主体名称" />
                  </el-form-item>
                  <el-form-item label="我方联系人" prop="myContact">
                    <el-input v-model="form.myContact" placeholder="请输入我方联系人" />
                  </el-form-item>
                  <el-form-item label="归档人" prop="archiver">
                    <el-input v-model="form.archiver" placeholder="请输入归档人" />
                  </el-form-item>
                  <el-form-item label="协同人" prop="cooperators">
                    <el-input v-model="form.cooperators" placeholder="多个协同人用逗号分隔" />
                  </el-form-item>
                </div>
              </div>

              <div class="sub-panel">
                <div class="sub-title">对方信息</div>
                <div class="form-grid single-column">
                  <el-form-item label="对方主体" prop="otherPartyName">
                    <el-input v-model="form.otherPartyName" placeholder="请输入对方主体名称" />
                  </el-form-item>
                  <el-form-item label="对方联系人" prop="otherContact">
                    <el-input v-model="form.otherContact" placeholder="请输入对方联系人" />
                  </el-form-item>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="side-panel">
          <div class="section-card sticky-card">
            <div class="section-header compact">
              <div class="section-title">审批流程</div>
              <div class="section-desc">提交前确认审批链路</div>
            </div>

            <el-form-item label="审批人" prop="approver" label-width="80px">
              <el-input v-model="form.approver" placeholder="多个审批人用逗号分隔" />
            </el-form-item>
            <el-form-item label="抄送人" prop="cc" label-width="80px">
              <el-input v-model="form.cc" placeholder="多个抄送人用逗号分隔" />
            </el-form-item>

            <div class="summary-box">
              <div class="summary-item"><span>合同名称</span><strong>{{ form.contractName || '-' }}</strong></div>
              <div class="summary-item"><span>金额类型</span><strong>{{ amountTypeLabel }}</strong></div>
              <div class="summary-item"><span>合同总额</span><strong>{{ form.amountType !== 'none' && form.totalAmount ? `¥ ${form.totalAmount}` : '-' }}</strong></div>
              <div class="summary-item"><span>签署方式</span><strong>{{ signMethodLabel }}</strong></div>
              <div class="summary-item"><span>我方主体</span><strong>{{ form.myPartyName || '-' }}</strong></div>
              <div class="summary-item"><span>对方主体</span><strong>{{ form.otherPartyName || '-' }}</strong></div>
            </div>

            <div class="action-bar">
              <el-button @click="resetForm">重置</el-button>
              <el-button type="primary" :loading="submitting" @click="submitForm">保存并提交</el-button>
            </div>
          </div>
        </div>
      </div>
    </el-form>
  </div>
</template>

<script>
import { addContractContent } from '@/api/contract/contract.js'

export default {
  name: 'SmartContractApproval',
  data() {
    return {
      submitting: false,
      form: {
        file: null,
        category: '',
        contractName: '',
        numberMode: 'auto',
        contractNumber: '',
        amountType: '',
        totalAmount: '',
        termType: '',
        archiver: '',
        cooperators: '',
        description: '',
        myPartyName: '',
        myContact: '',
        otherPartyName: '',
        otherContact: '',
        signMethod: 'paper',
        signDate: null,
        sealType: '',
        approver: '',
        cc: ''
      },
      fileList: [],
      attachmentList: [],
      rules: {
        contractName: [{ required: true, message: '请输入合同名称', trigger: 'blur' }],
        category: [{ required: true, message: '请选择合同分类', trigger: 'change' }],
        numberMode: [{ required: true, message: '请选择编号方式', trigger: 'change' }],
        amountType: [{ required: true, message: '请选择金额类型', trigger: 'change' }],
        totalAmount: [{ required: true, message: '请输入合同总额', trigger: 'blur' }],
        termType: [{ required: true, message: '请选择期限类型', trigger: 'change' }],
        myPartyName: [{ required: true, message: '请输入我方主体名称', trigger: 'blur' }],
        otherPartyName: [{ required: true, message: '请输入对方主体名称', trigger: 'blur' }],
        signMethod: [{ required: true, message: '请选择签署方式', trigger: 'change' }]
      }
    }
  },
  computed: {
    amountTypeLabel() {
      return { income: '收入', expense: '支出', none: '无金额' }[this.form.amountType] || '-'
    },
    signMethodLabel() {
      return { paper: '纸质签署', electronic: '电子签署' }[this.form.signMethod] || '-'
    }
  },
  methods: {
    handleChangeFile(file) {
      this.fileList = [file]
      this.form.file = file.raw
    },
    handleRemove() {
      this.fileList = []
      this.form.file = null
    },
    handleAttachmentChange(file) {
      if (!this.attachmentList.some(item => item.uid === file.uid)) this.attachmentList.push(file)
    },
    handleAttachmentRemove(file) {
      this.attachmentList = this.attachmentList.filter(item => item.uid !== file.uid)
    },
    handleExceed() {
      this.$message.warning('最多只能上传一个合同文件')
    },
    handleAttachmentExceed() {
      this.$message.warning('最多只能上传 10 个附件')
    },
    resetForm() {
      this.$refs.form && this.$refs.form.resetFields()
      this.fileList = []
      this.attachmentList = []
      this.form.file = null
    },
    submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return
        if (this.form.amountType === 'none') this.form.totalAmount = ''
        const formData = new FormData()
        if (this.form.file) formData.append('file', this.form.file)
        this.attachmentList.forEach(f => formData.append('attachments', f.raw))
        const fields = ['category', 'contractName', 'numberMode', 'contractNumber', 'amountType', 'totalAmount', 'termType', 'archiver', 'cooperators', 'description', 'myPartyName', 'myContact', 'otherPartyName', 'otherContact', 'signMethod', 'signDate', 'sealType', 'approver', 'cc']
        fields.forEach(key => {
          const val = this.form[key]
          if (val !== null && val !== undefined && val !== '') formData.append(key, val)
        })
        try {
          this.submitting = true
          await addContractContent(formData)
          this.$message.success('合同提交成功')
          this.resetForm()
        } catch (e) {
          this.$message.error('提交失败，请稍后重试')
        } finally {
          this.submitting = false
        }
      })
    }
  }
}
</script>

<style scoped lang="scss">
.smart-contract-page { min-height: 100vh; padding: 20px; background: #f3f6fb; }
.hero-section { display:flex; justify-content:space-between; align-items:center; padding:24px 28px; margin-bottom:18px; border-radius:18px; background:linear-gradient(135deg,#409eff 0%,#6aa8ff 50%,#8bb9ff 100%); box-shadow:0 10px 30px rgba(64,158,255,.18); }
.hero-title { font-size:28px; font-weight:700; color:#fff; margin-bottom:6px; }
.hero-subtitle { font-size:13px; color:rgba(255,255,255,.92); }
.hero-stat-card { padding:14px 18px; border-radius:14px; background:rgba(255,255,255,.18); color:#fff; display:flex; flex-direction:column; }
.hero-stat-card span { font-size:12px; opacity:.85; margin-bottom:4px; }
.hero-stat-card strong { font-size:16px; }
.page-grid { display:grid; grid-template-columns: minmax(0, 1fr) 340px; gap:18px; }
.section-card { background:#fff; border-radius:16px; box-shadow:0 8px 24px rgba(31,45,61,.06); padding:18px 20px; margin-bottom:18px; }
.section-header { display:flex; justify-content:space-between; align-items:center; margin-bottom:18px; gap:12px; }
.section-header.compact { margin-bottom:14px; }
.section-title { font-size:16px; font-weight:700; color:#303133; }
.section-desc { font-size:12px; color:#909399; }
.form-grid { display:grid; grid-template-columns:repeat(2,1fr); gap:0 16px; }
.single-column { grid-template-columns:1fr; }
.full-row { width:100%; }
.dual-section { display:grid; grid-template-columns:repeat(2,1fr); gap:16px; }
.sub-panel { border:1px solid #edf0f5; border-radius:14px; padding:16px; background:#fafcff; }
.sub-title { font-size:15px; font-weight:700; color:#303133; margin-bottom:14px; }
.sticky-card { position:sticky; top:20px; }
.summary-box { margin-top:12px; padding:14px; border-radius:14px; background:linear-gradient(180deg,#f8fbff 0%,#f5f7fa 100%); border:1px solid #e8eef6; }
.summary-item { display:flex; justify-content:space-between; gap:12px; padding:9px 0; border-bottom:1px dashed #dfe6ee; }
.summary-item:last-child { border-bottom:none; }
.summary-item span { font-size:12px; color:#909399; }
.summary-item strong { color:#303133; font-size:14px; max-width:55%; text-align:right; word-break:break-all; }
.action-bar { margin-top:16px; display:flex; justify-content:flex-end; gap:10px; }
::v-deep .modern-upload .el-upload { width:100%; }
::v-deep .modern-upload .el-upload-dragger { width:100%; border-radius:16px; border:2px dashed #d8e6ff; background:#f8fbff; }
::v-deep .modern-upload.light .el-upload-dragger { border-style:dashed; background:#fbfcfe; }
::v-deep .modern-upload .el-icon-upload, ::v-deep .modern-upload .el-icon-folder-add { font-size:42px; color:#409eff; margin-top:20px; }
::v-deep .el-form-item { margin-bottom:16px; }
@media (max-width: 1200px) { .page-grid { grid-template-columns:1fr; } .sticky-card { position:static; } }
@media (max-width: 768px) { .hero-section, .dual-section, .form-grid { grid-template-columns:1fr; flex-direction:column; } }
</style>
