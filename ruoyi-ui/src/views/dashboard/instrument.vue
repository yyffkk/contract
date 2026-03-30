<template>
  <div class="instrument-page">
    <div class="hero-section">
      <div>
        <div class="hero-title">仪表盘</div>
        <div class="hero-desc">汇总本年度合同、账款、开票核心数据，便于快速查看经营执行情况</div>
      </div>
      <div class="hero-tag">{{ currentYear }} 年</div>
    </div>

    <el-row :gutter="16" class="summary-row">
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card income">
          <div class="metric-label">本年度收款金额</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.yearReceiveAmount) }}</div>
          <div class="metric-sub">按账款日期统计</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card pay">
          <div class="metric-label">本年度付款金额</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.yearPayAmount) }}</div>
          <div class="metric-sub">按账款日期统计</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card invoice">
          <div class="metric-label">本年度开票金额</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.yearInvoiceAmount) }}</div>
          <div class="metric-sub">按发票日期统计</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="6">
        <div class="metric-card total">
          <div class="metric-label">合同总金额</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.contractTotalAmount) }}</div>
          <div class="metric-sub">按合同列表汇总</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="progress-row">
      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-header">
            <span>收款进度</span>
            <span class="panel-total">总金额：¥ {{ formatMoney(metrics.contractTotalAmount) }}</span>
          </div>
          <div class="progress-grid single-column">
            <div class="progress-stat receive-done">
              <div class="stat-title">已收金额</div>
              <div class="stat-amount">¥ {{ formatMoney(metrics.receivedAmount) }}</div>
              <div class="stat-percent">占比 {{ formatPercent(metrics.receivedRatio) }}</div>
            </div>
            <div class="progress-stat receive-pending">
              <div class="stat-title">待收金额</div>
              <div class="stat-amount">¥ {{ formatMoney(metrics.pendingReceiveAmount) }}</div>
              <div class="stat-percent">占比 {{ formatPercent(metrics.pendingReceiveRatio) }}</div>
            </div>
          </div>
          <div class="progress-block">
            <div class="progress-label">已收 / 总金额</div>
            <el-progress :percentage="toPercent(metrics.receivedRatio)" :stroke-width="16" color="#67C23A" />
          </div>
          <div class="progress-block">
            <div class="progress-label">待收 / 总金额</div>
            <el-progress :percentage="toPercent(metrics.pendingReceiveRatio)" :stroke-width="16" color="#409EFF" />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-header">
            <span>付款进度</span>
            <span class="panel-total">总金额：¥ {{ formatMoney(metrics.contractTotalAmount) }}</span>
          </div>
          <div class="progress-grid single-column">
            <div class="progress-stat pay-done">
              <div class="stat-title">已付金额</div>
              <div class="stat-amount">¥ {{ formatMoney(metrics.paidAmount) }}</div>
              <div class="stat-percent">占比 {{ formatPercent(metrics.paidRatio) }}</div>
            </div>
            <div class="progress-stat pay-pending">
              <div class="stat-title">待付金额</div>
              <div class="stat-amount">¥ {{ formatMoney(metrics.pendingPayAmount) }}</div>
              <div class="stat-percent">占比 {{ formatPercent(metrics.pendingPayRatio) }}</div>
            </div>
          </div>
          <div class="progress-block">
            <div class="progress-label">已付 / 总金额</div>
            <el-progress :percentage="toPercent(metrics.paidRatio)" :stroke-width="16" color="#E6A23C" />
          </div>
          <div class="progress-block">
            <div class="progress-label">待付 / 总金额</div>
            <el-progress :percentage="toPercent(metrics.pendingPayRatio)" :stroke-width="16" color="#F56C6C" />
          </div>
        </el-card>
      </el-col>

      <el-col :xs="24" :lg="8">
        <el-card shadow="never" class="panel-card">
          <div slot="header" class="panel-header">
            <span>开票进度</span>
            <span class="panel-total">总金额：¥ {{ formatMoney(metrics.contractTotalAmount) }}</span>
          </div>
          <div class="progress-grid single-column">
            <div class="progress-stat invoice-done">
              <div class="stat-title">已开票金额</div>
              <div class="stat-amount">¥ {{ formatMoney(metrics.issuedAmount) }}</div>
              <div class="stat-percent">占比 {{ formatPercent(metrics.issuedRatio) }}</div>
            </div>
            <div class="progress-stat invoice-pending">
              <div class="stat-title">待开票金额</div>
              <div class="stat-amount">¥ {{ formatMoney(metrics.pendingIssuedAmount) }}</div>
              <div class="stat-percent">占比 {{ formatPercent(metrics.pendingIssuedRatio) }}</div>
            </div>
          </div>
          <div class="progress-block">
            <div class="progress-label">已开票 / 总金额</div>
            <el-progress :percentage="toPercent(metrics.issuedRatio)" :stroke-width="16" color="#8E44FD" />
          </div>
          <div class="progress-block">
            <div class="progress-label">待开票 / 总金额</div>
            <el-progress :percentage="toPercent(metrics.pendingIssuedRatio)" :stroke-width="16" color="#13C2C2" />
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="detail-card">
      <div slot="header" class="panel-header">
        <span>统计口径说明</span>
        <el-button type="text" icon="el-icon-refresh" :loading="loading" @click="loadDashboardData">刷新</el-button>
      </div>
      <div v-loading="loading" class="detail-list">
        <div class="detail-item">1. 本年度收款/付款金额：按账款列表的 <strong>账款日期</strong> 落在本年度统计。</div>
        <div class="detail-item">2. 本年度开票金额：按发票列表的 <strong>发票日期</strong> 落在本年度统计。</div>
        <div class="detail-item">3. 待收/待付、已收/已付：按合同管理列表中的相关金额字段汇总。</div>
        <div class="detail-item">4. 占比口径：各项金额 ÷ 合同总金额。</div>
        <div class="detail-item">5. 当前加载条数：合同 {{ metrics.contractCount }} 条，账款 {{ metrics.accountCount }} 条，发票 {{ metrics.invoiceCount }} 条。</div>
      </div>
    </el-card>
  </div>
</template>

<script>
import { listContractContent } from '@/api/contract/contract'
import { listAccount } from '@/api/account/account'
import { listInvoice } from '@/api/invoice/invoice'

const createMetrics = () => ({
  yearReceiveAmount: 0,
  yearPayAmount: 0,
  yearInvoiceAmount: 0,
  contractTotalAmount: 0,
  receivedAmount: 0,
  pendingReceiveAmount: 0,
  paidAmount: 0,
  pendingPayAmount: 0,
  issuedAmount: 0,
  pendingIssuedAmount: 0,
  receivedRatio: 0,
  pendingReceiveRatio: 0,
  paidRatio: 0,
  pendingPayRatio: 0,
  issuedRatio: 0,
  pendingIssuedRatio: 0,
  contractCount: 0,
  accountCount: 0,
  invoiceCount: 0
})

export default {
  name: 'InstrumentDashboard',
  data() {
    return {
      loading: false,
      currentYear: new Date().getFullYear(),
      metrics: createMetrics()
    }
  },
  created() {
    this.loadDashboardData()
  },
  methods: {
    async loadDashboardData() {
      this.loading = true
      try {
        const [contractRes, accountRes, invoiceRes] = await Promise.all([
          listContractContent({ pageNum: 1, pageSize: 1000 }),
          listAccount({ pageNum: 1, pageSize: 1000 }),
          listInvoice({ pageNum: 1, pageSize: 1000 })
        ])

        const contractRows = (contractRes && contractRes.rows) || []
        const accountRows = (accountRes && accountRes.rows) || []
        const invoiceRows = (invoiceRes && invoiceRes.rows) || []

        const contractTotalAmount = this.sumBy(contractRows, 'totalAmount')
        const receivedAmount = this.sumBy(contractRows, 'receivedPaid')
        const pendingReceiveAmount = this.sumBy(contractRows, 'pendingReceivedPaid')
        const paidAmount = this.sumBy(contractRows, 'otherPaymentAmount')
        const pendingPayAmount = this.sumBy(contractRows, 'pendingIssuedReceived')
        const issuedAmount = this.sumBy(contractRows, 'issuedReceived')
        const pendingIssuedAmount = this.sumBy(contractRows, 'pendingIssuedReceived')

        const yearAccountRows = accountRows.filter(item => this.isCurrentYear(item.accountDate || item.createTime || item.updateTime))
        const yearInvoiceRows = invoiceRows.filter(item => this.isCurrentYear(item.invoiceDate || item.createTime || item.applyTime || item.updateTime))

        const yearReceiveAmount = yearAccountRows.filter(item => item.amountType === 'income').reduce((sum, item) => sum + this.toNumber(item.amount), 0)
        const yearPayAmount = yearAccountRows.filter(item => item.amountType === 'expense').reduce((sum, item) => sum + this.toNumber(item.amount), 0)
        const yearInvoiceAmount = yearInvoiceRows.reduce((sum, item) => sum + this.toNumber(item.invoiceAmount), 0)

        this.metrics = {
          yearReceiveAmount,
          yearPayAmount,
          yearInvoiceAmount,
          contractTotalAmount,
          receivedAmount,
          pendingReceiveAmount,
          paidAmount,
          pendingPayAmount,
          issuedAmount,
          pendingIssuedAmount,
          receivedRatio: this.calcRatio(receivedAmount, contractTotalAmount),
          pendingReceiveRatio: this.calcRatio(pendingReceiveAmount, contractTotalAmount),
          paidRatio: this.calcRatio(paidAmount, contractTotalAmount),
          pendingPayRatio: this.calcRatio(pendingPayAmount, contractTotalAmount),
          issuedRatio: this.calcRatio(issuedAmount, contractTotalAmount),
          pendingIssuedRatio: this.calcRatio(pendingIssuedAmount, contractTotalAmount),
          contractCount: contractRows.length,
          accountCount: accountRows.length,
          invoiceCount: invoiceRows.length
        }
      } catch (error) {
        this.$message.error((error && error.message) || '仪表盘数据加载失败')
      } finally {
        this.loading = false
      }
    },
    sumBy(rows, field) {
      return (rows || []).reduce((sum, item) => sum + this.toNumber(item && item[field]), 0)
    },
    toNumber(value) {
      if (value === null || value === undefined || value === '') return 0
      if (typeof value === 'number') return isNaN(value) ? 0 : value
      const cleaned = String(value).replace(/[¥,，\s]/g, '')
      const num = Number(cleaned)
      return isNaN(num) ? 0 : num
    },
    isCurrentYear(value) {
      if (!value) return false
      const date = new Date(value)
      if (isNaN(date.getTime())) return false
      return date.getFullYear() === this.currentYear
    },
    calcRatio(part, total) {
      if (!total) return 0
      return part / total
    },
    toPercent(ratio) {
      const value = Number((ratio || 0) * 100)
      if (value <= 0) return 0
      if (value >= 100) return 100
      return Number(value.toFixed(2))
    },
    formatPercent(ratio) {
      return `${((ratio || 0) * 100).toFixed(2)}%`
    },
    formatMoney(value) {
      const num = this.toNumber(value)
      return num.toLocaleString('zh-CN', { minimumFractionDigits: 2, maximumFractionDigits: 2 })
    }
  }
}
</script>

<style scoped>
.instrument-page {
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f7fb 0%, #eef3fb 100%);
}
.hero-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px 28px;
  border-radius: 18px;
  background: linear-gradient(135deg, #1d62f0 0%, #5a8dff 100%);
  color: #fff;
  margin-bottom: 18px;
  box-shadow: 0 10px 30px rgba(64, 108, 255, 0.22);
}
.hero-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 8px;
}
.hero-desc {
  font-size: 14px;
  opacity: 0.92;
}
.hero-tag {
  padding: 10px 16px;
  border-radius: 999px;
  background: rgba(255,255,255,0.18);
  font-weight: 600;
}
.summary-row, .progress-row {
  margin-bottom: 2px;
}
.metric-card, .detail-card {
  border-radius: 18px;
  border: none;
  background: #fff;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
}
.panel-card {
  border-radius: 18px;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.06);
  border: none;
  margin-bottom: 16px;
}
.metric-card {
  padding: 22px;
  margin-bottom: 16px;
  min-height: 150px;
}
.metric-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 14px;
}
.metric-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2d3d;
  line-height: 1.2;
}
.metric-sub {
  margin-top: 12px;
  color: #909399;
  font-size: 12px;
}
.metric-card.income { border-top: 4px solid #67C23A; }
.metric-card.pay { border-top: 4px solid #F56C6C; }
.metric-card.invoice { border-top: 4px solid #E6A23C; }
.metric-card.total { border-top: 4px solid #409EFF; }
.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
.panel-total {
  color: #909399;
  font-size: 12px;
}
.progress-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
  margin-bottom: 20px;
}
.single-column {
  grid-template-columns: 1fr;
}
.progress-stat {
  border-radius: 14px;
  padding: 18px;
  color: #1f2d3d;
}
.receive-done { background: #edf9f0; }
.receive-pending { background: #edf5ff; }
.pay-done { background: #fff5e9; }
.pay-pending { background: #fff0f0; }
.invoice-done { background: #f4edff; }
.invoice-pending { background: #ebfbfb; }
.stat-title {
  font-size: 13px;
  color: #606266;
  margin-bottom: 8px;
}
.stat-amount {
  font-size: 24px;
  font-weight: 700;
  margin-bottom: 6px;
}
.stat-percent {
  font-size: 12px;
  color: #909399;
}
.progress-block {
  margin-bottom: 18px;
}
.progress-label {
  margin-bottom: 8px;
  color: #606266;
  font-size: 13px;
}
.detail-list {
  line-height: 1.9;
  color: #606266;
}
.detail-item {
  padding: 6px 0;
}
@media (max-width: 768px) {
  .hero-section {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .progress-grid {
    grid-template-columns: 1fr;
  }
}
</style>
