<template>
  <div class="dashboard-container">
    <div class="header">
      <i class="el-icon-document" style="font-size: 24px; color: #409EFF; margin-right: 10px;"></i>
      <span class="greeting">Hi, 俞泽北</span>
    </div>

    <div class="action-cards">
      <div class="action-card-simple" ref="uploadCard" @click="toggleUploadMenu">
        <div class="card-content">
          <div class="icon-wrapper">
            <i class="el-icon-plus" style="font-size: 32px; color: #409EFF;"></i>
          </div>
          <div class="card-title">上传文件发起</div>
        </div>

        <div v-if="showUploadMenu" class="simple-dropdown">
          <div class="dropdown-header">选择审批模板</div>
          <div class="dropdown-item" @click.stop="handleSmartContract">
            <i class="el-icon-s-check"></i> 智能合同审批
          </div>
          <div class="dropdown-item" @click.stop="handlePrintApproval">
            <i class="el-icon-printer"></i> 用印审批单
          </div>
        </div>
      </div>

      <div class="action-card-simple" @click="handleTemplate">
        <div class="card-content">
          <div class="icon-wrapper">
            <i class="el-icon-edit" style="font-size: 32px; color: #409EFF;"></i>
          </div>
          <div class="card-title">模板发起合同</div>
        </div>
      </div>

      <div class="action-card-simple" @click="handleImport">
        <div class="card-content">
          <div class="icon-wrapper">
            <i class="el-icon-star-on" style="font-size: 32px; color: #409EFF;"></i>
          </div>
          <div class="card-title">导入历史合同</div>
        </div>
      </div>

      <div class="action-card-simple" @click="handleBorrow">
        <div class="card-content">
          <div class="icon-wrapper">
            <i class="el-icon-minus" style="font-size: 32px; color: #409EFF;"></i>
          </div>
          <div class="card-title">借阅申请</div>
        </div>
      </div>
    </div>

    <div class="todo-section">
      <h3 class="section-title">待办任务</h3>
      <div class="stats-row">
        <div class="stat-item clickable" v-for="(item, index) in todoStats" :key="index" @click="goToContractTodo(item)">
          <div class="stat-number">{{ item.count }}</div>
          <div class="stat-label">{{ item.label }}</div>
        </div>
      </div>
    </div>

    <div class="warning-section">
      <h3 class="section-title">账款计划预警</h3>
      <div class="warning-group" v-for="group in warningGroups" :key="group.scope">
        <div class="group-title">{{ group.label }}</div>
        <div class="warning-row">
          <div class="warning-item clickable" v-for="item in group.items" :key="item.key" @click="goToAccountWarning(group.scope, item)">
            <div class="warning-number">{{ item.count }}</div>
            <div class="warning-label">{{ item.label }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Dashboard',
  data() {
    return {
      showUploadMenu: false,
      todoStats: [
        { count: 1, label: '审批中', tab: 'approving' },
        { count: 2, label: '签署中', tab: 'signing' },
        { count: 0, label: '归档中', tab: 'archiving' },
        { count: 0, label: '已归档', tab: 'archived' },
        { count: 0, label: '将到期', tab: 'expiring' }
      ],
      warningGroups: [
        {
          scope: 'mine',
          label: '我的',
          items: [
            { key: 'receive_overdue', label: '收款已逾期', count: 0 },
            { key: 'pay_overdue', label: '付款已逾期', count: 0 },
            { key: 'receive_expiring', label: '收款将到期', count: 0 },
            { key: 'pay_expiring', label: '付款将到期', count: 0 }
          ]
        },
        {
          scope: 'all',
          label: '所有的',
          items: [
            { key: 'receive_overdue', label: '收款已逾期', count: 0 },
            { key: 'pay_overdue', label: '付款已逾期', count: 0 },
            { key: 'receive_expiring', label: '收款将到期', count: 0 },
            { key: 'pay_expiring', label: '付款将到期', count: 0 }
          ]
        }
      ]
    }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside)
  },
  beforeDestroy() {
    document.removeEventListener('click', this.handleClickOutside)
  },
  methods: {
    toggleUploadMenu() {
      this.showUploadMenu = !this.showUploadMenu
    },
    handleClickOutside(event) {
      if (!this.$refs.uploadCard) return
      const cardElement = this.$refs.uploadCard
      if (cardElement && !cardElement.contains(event.target)) {
        this.showUploadMenu = false
      }
    },
    handleSmartContract() {
      this.$router.push('/contract/SmartContractApproval')
      this.showUploadMenu = false
    },
    handlePrintApproval() {
      this.$router.push('/contract/SealApproval')
      this.showUploadMenu = false
    },
    handleTemplate() {
      this.$message.info('点击了【模板发起合同】')
    },
    handleImport() {
      this.$message.info('点击了【导入历史合同】')
    },
    handleBorrow() {
      this.$message.info('点击了【借阅申请】')
    },
    goToContractTodo(item) {
      this.$router.push({
        path: '/contract/contract',
        query: {
          nav: 'mine',
          tab: item.tab,
          documentType: 'approval'
        }
      })
    },
    goToAccountWarning(scope, item) {
      this.$router.push({
        path: '/contract/account',
        query: {
          nav: 'account',
          scope,
          warningType: item.key
        }
      })
    }
  }
}
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100vh;
}
.header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  font-size: 18px;
  color: #303133;
}
.greeting { font-weight: 500; }
.action-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 30px;
}
.action-card-simple, .stat-item, .warning-item {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.05);
}
.action-card-simple {
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all .3s;
  position: relative;
  min-height: 140px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.action-card-simple:hover, .clickable:hover {
  transform: translateY(-4px);
  box-shadow: 0 4px 20px 0 rgba(0,0,0,.1);
}
.card-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}
.icon-wrapper {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 10px;
}
.card-title, .section-title { color: #303133; font-weight: 600; }
.todo-section, .warning-section { margin-bottom: 28px; }
.stats-row, .warning-row {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
}
.warning-row { grid-template-columns: repeat(4, 1fr); }
.stat-item, .warning-item {
  padding: 18px;
  text-align: center;
  transition: all .3s;
}
.stat-number, .warning-number {
  font-size: 26px;
  font-weight: 700;
  color: #409EFF;
  margin-bottom: 8px;
}
.stat-label, .warning-label, .group-title { color: #606266; }
.group-title { margin: 12px 0; font-weight: 600; }
.simple-dropdown {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0,0,0,.12);
  z-index: 10;
  padding: 8px 0;
}
.dropdown-header { padding: 10px 16px; color: #909399; font-size: 12px; }
.dropdown-item { padding: 12px 16px; cursor: pointer; }
.dropdown-item:hover { background: #f5f7fa; }
.clickable { cursor: pointer; }
@media (max-width: 1200px) {
  .action-cards, .stats-row, .warning-row { grid-template-columns: repeat(2, 1fr); }
}
@media (max-width: 768px) {
  .action-cards, .stats-row, .warning-row { grid-template-columns: 1fr; }
}
</style>
