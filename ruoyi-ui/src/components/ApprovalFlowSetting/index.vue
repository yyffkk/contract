<template>
  <div class="approval-flow-setting">
    <el-dialog :title="`${title}审批流设置`" :visible.sync="visibleInner" width="820px" append-to-body @close="handleClose">
      <div class="flow-header-tip">
        <el-alert title="当前版本支持 1~3 个串行审批节点，可按用户或角色配置。提交审批时将严格按这里的设置流转。" type="info" :closable="false" show-icon />
      </div>

      <div class="flow-tree-wrap">
        <div class="flow-fixed-node start">开始</div>
        <div v-for="(node, index) in form.nodes" :key="node.localKey" class="flow-node-card">
          <div class="flow-line">↓</div>
          <el-card shadow="never">
            <div slot="header" class="flow-node-header">
              <span>审批节点 {{ index + 1 }}</span>
              <el-button v-if="form.nodes.length > 1" type="text" class="danger-text" @click="removeNode(index)">删除</el-button>
            </div>
            <el-form label-width="88px" size="small">
              <el-form-item label="节点名称">
                <el-input v-model="node.nodeName" maxlength="20" placeholder="如：财务审批 / 主管审批" />
              </el-form-item>
              <el-form-item label="处理人类型">
                <el-radio-group v-model="node.assigneeType">
                  <el-radio-button label="user">指定用户</el-radio-button>
                  <el-radio-button label="role">指定角色</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item :label="node.assigneeType === 'role' ? '指定角色' : '指定用户'">
                <el-select v-if="node.assigneeType === 'role'" v-model="node.assigneeValue" filterable clearable placeholder="请选择角色" style="width: 100%" @change="val => handleRoleChange(node, val)">
                  <el-option v-for="item in roleOptions" :key="item.roleId" :label="item.roleName" :value="String(item.roleId)" />
                </el-select>
                <el-select v-else v-model="node.assigneeValue" filterable clearable placeholder="请选择用户" style="width: 100%" @change="val => handleUserChange(node, val)">
                  <el-option v-for="item in userOptions" :key="item.userId" :label="formatUserLabel(item)" :value="item.userName" />
                </el-select>
              </el-form-item>
            </el-form>
          </el-card>
        </div>
        <div class="flow-line">↓</div>
        <div class="flow-fixed-node end">结束</div>
      </div>

      <div class="flow-toolbar">
        <el-button plain icon="el-icon-plus" :disabled="form.nodes.length >= 3" @click="addNode">新增审批节点</el-button>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="visibleInner = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="submit">保存设置</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getApprovalFlow, saveApprovalFlow } from '@/api/approval/flow'
import { listUser } from '@/api/system/user'
import { listRole } from '@/api/system/role'

const createNode = (index = 0) => ({
  localKey: `${Date.now()}_${index}_${Math.random()}`,
  nodeKey: `node${index + 1}`,
  nodeName: `审批节点${index + 1}`,
  assigneeType: 'user',
  assigneeValue: '',
  assigneeLabel: ''
})

export default {
  name: 'ApprovalFlowSetting',
  props: {
    visible: { type: Boolean, default: false },
    businessType: { type: String, required: true },
    title: { type: String, default: '审批流' }
  },
  data() {
    return {
      visibleInner: false,
      submitLoading: false,
      userOptions: [],
      roleOptions: [],
      form: {
        businessType: '',
        businessName: '',
        nodes: [createNode(0)]
      }
    }
  },
  watch: {
    visible: {
      immediate: true,
      handler(val) {
        this.visibleInner = val
        if (val) {
          this.init()
        }
      }
    },
    visibleInner(val) {
      if (!val) {
        this.$emit('update:visible', false)
      }
    }
  },
  methods: {
    init() {
      this.form.businessType = this.businessType
      this.loadOptions()
      this.loadFlow()
    },
    loadOptions() {
      listUser({ status: '0', pageNum: 1, pageSize: 1000 }).then(res => {
        this.userOptions = res.rows || []
      })
      listRole({ status: '0', pageNum: 1, pageSize: 1000 }).then(res => {
        this.roleOptions = res.rows || []
      })
    },
    loadFlow() {
      getApprovalFlow(this.businessType).then(res => {
        const data = (res && res.data) || {}
        const nodes = Array.isArray(data.nodes) && data.nodes.length ? data.nodes : [createNode(0)]
        this.form = {
          businessType: this.businessType,
          businessName: data.businessName || this.title,
          nodes: nodes.map((item, index) => ({ ...createNode(index), ...item, localKey: `${Date.now()}_${index}` }))
        }
      })
    },
    addNode() {
      if (this.form.nodes.length >= 3) return
      this.form.nodes.push(createNode(this.form.nodes.length))
      this.normalizeKeys()
    },
    removeNode(index) {
      this.form.nodes.splice(index, 1)
      if (!this.form.nodes.length) this.form.nodes.push(createNode(0))
      this.normalizeKeys()
    },
    normalizeKeys() {
      this.form.nodes = this.form.nodes.map((item, index) => ({
        ...item,
        nodeKey: `node${index + 1}`,
        nodeName: item.nodeName || `审批节点${index + 1}`
      }))
    },
    handleUserChange(node, val) {
      const target = this.userOptions.find(item => item.userName === val)
      node.assigneeLabel = target ? this.formatUserLabel(target) : ''
    },
    handleRoleChange(node, val) {
      const target = this.roleOptions.find(item => String(item.roleId) === String(val))
      node.assigneeLabel = target ? target.roleName : ''
    },
    formatUserLabel(user) {
      if (!user) return '-'
      return user.nickName && user.userName ? `${user.nickName}（${user.userName}）` : (user.nickName || user.userName || '-')
    },
    validate() {
      if (!this.form.nodes.length) {
        this.$message.warning('请至少配置一个审批节点')
        return false
      }
      for (const node of this.form.nodes) {
        if (!node.nodeName) {
          this.$message.warning('审批节点名称不能为空')
          return false
        }
        if (!node.assigneeType || !node.assigneeValue) {
          this.$message.warning(`请补全节点【${node.nodeName || '未命名节点'}】的处理人配置`)
          return false
        }
      }
      return true
    },
    submit() {
      this.normalizeKeys()
      if (!this.validate()) return
      this.submitLoading = true
      saveApprovalFlow({
        businessType: this.businessType,
        businessName: this.title,
        nodes: this.form.nodes.map(({ localKey, ...rest }) => rest)
      }).then(res => {
        this.$message.success('审批流设置已保存')
        this.$emit('saved', (res && res.data) || this.form)
        this.visibleInner = false
      }).finally(() => {
        this.submitLoading = false
      })
    },
    handleClose() {
      this.$emit('update:visible', false)
    }
  }
}
</script>

<style scoped>
.flow-header-tip { margin-bottom: 16px; }
.flow-tree-wrap { display: flex; flex-direction: column; align-items: center; padding: 8px 0 12px; }
.flow-fixed-node { width: 160px; text-align: center; padding: 12px 16px; border-radius: 12px; font-weight: 600; color: #fff; }
.flow-fixed-node.start { background: linear-gradient(135deg, #67c23a, #85ce61); }
.flow-fixed-node.end { background: linear-gradient(135deg, #909399, #a6a9ad); }
.flow-node-card { width: 100%; max-width: 620px; }
.flow-node-card .el-card { border-radius: 14px; }
.flow-line { font-size: 20px; color: #909399; line-height: 36px; }
.flow-node-header { display: flex; align-items: center; justify-content: space-between; font-weight: 700; }
.flow-toolbar { margin-top: 8px; }
.danger-text { color: #f56c6c; }
</style>
