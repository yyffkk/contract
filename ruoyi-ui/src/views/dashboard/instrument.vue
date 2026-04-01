<template>
  <div class="instrument-page">
    <div class="hero-section">
      <div class="hero-left">
        <div class="hero-title">仪表盘</div>
        <div class="hero-desc">按年份汇总合同、收付款、进销项开票情况，用更直观的图形看业务执行进度</div>
      </div>
      <div class="hero-right">
        <el-select v-model="selectedYear" size="small" class="year-select" @change="handleYearChange">
          <el-option v-for="year in yearOptions" :key="year" :label="`${year} 年`" :value="year" />
        </el-select>
        <el-button size="small" type="primary" plain icon="el-icon-refresh" :loading="loading" @click="loadDashboardData">刷新数据</el-button>
      </div>
    </div>

    <el-row :gutter="16" class="metric-row">
      <el-col :xs="24" :sm="12" :lg="4">
        <div class="metric-card contract-income">
          <div class="metric-label">年度合同总额（收入）</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.contractIncomeAmount) }}</div>
          <div class="metric-sub">合同类型：收入</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="4">
        <div class="metric-card contract-expense">
          <div class="metric-label">年度合同总额（支出）</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.contractExpenseAmount) }}</div>
          <div class="metric-sub">合同类型：支出</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="4">
        <div class="metric-card receive">
          <div class="metric-label">年度收款金额</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.yearReceiveAmount) }}</div>
          <div class="metric-sub">账款类型：收入</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="4">
        <div class="metric-card pay">
          <div class="metric-label">年度付款金额</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.yearPayAmount) }}</div>
          <div class="metric-sub">账款类型：支出</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="4">
        <div class="metric-card output-invoice">
          <div class="metric-label">年度开票金额（支出）</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.yearOutputInvoiceAmount) }}</div>
          <div class="metric-sub">发票分类：销项 / 支出</div>
        </div>
      </el-col>
      <el-col :xs="24" :sm="12" :lg="4">
        <div class="metric-card input-invoice">
          <div class="metric-label">年度开票金额（收入）</div>
          <div class="metric-value">¥ {{ formatMoney(metrics.yearInputInvoiceAmount) }}</div>
          <div class="metric-sub">发票分类：进项 / 收入</div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="chart-row">
      <el-col :xs="24" :md="12" :xl="8">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="chart-header">
            <span>年度收付款结构</span>
            <span class="chart-header-note">{{ selectedYear }} 年</span>
          </div>
          <div ref="cashFlowChart" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12" :xl="8">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="chart-header">
            <span>年度进销项开票结构</span>
            <span class="chart-header-note">{{ selectedYear }} 年</span>
          </div>
          <div ref="invoiceChart" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12" :xl="8">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="chart-header">
            <span>收款执行进度</span>
            <span class="chart-header-note">相对年度合同总额（收入）</span>
          </div>
          <div ref="receiveProgressChart" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12" :xl="8">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="chart-header">
            <span>付款执行进度</span>
            <span class="chart-header-note">相对年度合同总额（支出）</span>
          </div>
          <div ref="payProgressChart" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12" :xl="8">
        <el-card shadow="never" class="chart-card">
          <div slot="header" class="chart-header">
            <span>开票执行进度</span>
            <span class="chart-header-note">相对年度合同总额</span>
          </div>
          <div ref="invoiceProgressChart" class="chart-box"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12" :xl="8">
        <el-card shadow="never" class="chart-card summary-card">
          <div slot="header" class="chart-header">
            <span>年度数据摘要</span>
            <span class="chart-header-note">{{ selectedYear }} 年</span>
          </div>
          <div class="summary-list">
            <div class="summary-item">
              <span>合同数量</span>
              <strong>{{ metrics.contractCount }}</strong>
            </div>
            <div class="summary-item">
              <span>账款数量</span>
              <strong>{{ metrics.accountCount }}</strong>
            </div>
            <div class="summary-item">
              <span>发票数量</span>
              <strong>{{ metrics.invoiceCount }}</strong>
            </div>
            <div class="summary-item">
              <span>已收占比</span>
              <strong>{{ formatPercent(metrics.receivedRatio) }}</strong>
            </div>
            <div class="summary-item">
              <span>已付占比</span>
              <strong>{{ formatPercent(metrics.paidRatio) }}</strong>
            </div>
            <div class="summary-item">
              <span>收入开票覆盖率</span>
              <strong>{{ formatPercent(metrics.incomeInvoiceRatio) }}</strong>
            </div>
            <div class="summary-item">
              <span>支出开票覆盖率</span>
              <strong>{{ formatPercent(metrics.expenseInvoiceRatio) }}</strong>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" class="detail-card">
      <div slot="header" class="chart-header">
        <span>统计说明</span>
        <span class="chart-header-note">当前年份：{{ selectedYear }}</span>
      </div>
      <div class="detail-list">
        <div class="detail-item">1. 合同总额：按合同申请时间 / 创建时间归属到所选年份后汇总，并拆分为收入合同总额、支出合同总额。</div>
        <div class="detail-item">2. 收款 / 付款：按账款日期归属年份，收入计收款，支出计付款。</div>
        <div class="detail-item">3. 发票：按发票日期归属年份，支出计销项，收入计进项（按你当前业务口径）。</div>
        <div class="detail-item">4. 年度数据摘要中：已收占比 = 年度收款金额 / 年度合同总额（收入）；已付占比 = 年度付款金额 / 年度合同总额（支出）。</div>
        <div class="detail-item">5. 收入开票覆盖率 = 年度开票金额（收入） / 年度合同总额（收入）；支出开票覆盖率 = 年度开票金额（支出） / 年度合同总额（支出）。</div>
      </div>
    </el-card>
  </div>
</template>

<script>
import * as echarts from 'echarts'
import { listContractContent } from '@/api/contract/contract'
import { listAccount } from '@/api/account/account'
import { listInvoice } from '@/api/invoice/invoice'
import { getInvoiceBizType } from '@/utils/invoiceType'

const createMetrics = () => ({
  contractTotalAmount: 0,
  contractIncomeAmount: 0,
  contractExpenseAmount: 0,
  yearReceiveAmount: 0,
  yearPayAmount: 0,
  yearInputInvoiceAmount: 0,
  yearOutputInvoiceAmount: 0,
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
  incomeInvoiceRatio: 0,
  expenseInvoiceRatio: 0,
  contractCount: 0,
  accountCount: 0,
  invoiceCount: 0
})

export default {
  name: 'InstrumentDashboard',
  data() {
    return {
      loading: false,
      selectedYear: new Date().getFullYear(),
      yearOptions: [new Date().getFullYear()],
      metrics: createMetrics(),
      charts: {}
    }
  },
  created() {
    this.loadDashboardData()
  },
  mounted() {
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    window.removeEventListener('resize', this.handleResize)
    Object.keys(this.charts).forEach(key => {
      if (this.charts[key]) this.charts[key].dispose()
    })
  },
  methods: {
    async loadDashboardData() {
      this.loading = true
      try {
        const [contractRes, accountRes, invoiceRes] = await Promise.all([
          listContractContent({ pageNum: 1, pageSize: 3000 }),
          listAccount({ pageNum: 1, pageSize: 3000 }),
          listInvoice({ pageNum: 1, pageSize: 3000 })
        ])

        const contractRows = (contractRes && contractRes.rows) || []
        const accountRows = (accountRes && accountRes.rows) || []
        const invoiceRows = (invoiceRes && invoiceRes.rows) || []

        this.initYearOptions(contractRows, accountRows, invoiceRows)

        const yearContractRows = contractRows.filter(item => this.getYear(item.applyTime || item.createTime || item.startDate || item.updateTime) === this.selectedYear)
        const yearAccountRows = accountRows.filter(item => this.getYear(item.accountDate || item.createTime || item.updateTime) === this.selectedYear)
        const yearInvoiceRows = invoiceRows.filter(item => this.getYear(item.invoiceDate || item.createTime || item.applyTime || item.updateTime) === this.selectedYear)

        const contractTotalAmount = this.sumBy(yearContractRows, 'totalAmount')
        const contractIncomeAmount = yearContractRows
          .filter(item => item.amountType === '收入')
          .reduce((sum, item) => sum + this.toNumber(item.totalAmount), 0)
        const contractExpenseAmount = yearContractRows
          .filter(item => item.amountType === '支出')
          .reduce((sum, item) => sum + this.toNumber(item.totalAmount), 0)
        const receivedAmount = this.sumBy(yearContractRows, 'receivedPaid')
        const pendingReceiveAmount = this.sumBy(yearContractRows, 'pendingReceivedPaid')
        const paidAmount = this.sumBy(yearContractRows, 'otherPaymentAmount')
        const pendingPayAmount = this.sumBy(yearContractRows, 'pendingIssuedReceived')
        const issuedAmount = this.sumBy(yearContractRows, 'issuedReceived')
        const pendingIssuedAmount = this.sumBy(yearContractRows, 'pendingIssuedReceived')

        const yearReceiveAmount = yearAccountRows
          .filter(item => item.amountType === 'income')
          .reduce((sum, item) => sum + this.toNumber(item.amount), 0)

        const yearPayAmount = yearAccountRows
          .filter(item => item.amountType === 'expense')
          .reduce((sum, item) => sum + this.toNumber(item.amount), 0)

        const yearOutputInvoiceAmount = yearInvoiceRows
          .filter(item => (item.invoiceBizType || this.getInvoiceBizType(item.amountType).key) === 'output')
          .reduce((sum, item) => sum + this.toNumber(item.invoiceAmount), 0)

        const yearInputInvoiceAmount = yearInvoiceRows
          .filter(item => (item.invoiceBizType || this.getInvoiceBizType(item.amountType).key) === 'input')
          .reduce((sum, item) => sum + this.toNumber(item.invoiceAmount), 0)

        this.metrics = {
          contractTotalAmount,
          contractIncomeAmount,
          contractExpenseAmount,
          yearReceiveAmount,
          yearPayAmount,
          yearInputInvoiceAmount,
          yearOutputInvoiceAmount,
          receivedAmount,
          pendingReceiveAmount,
          paidAmount,
          pendingPayAmount,
          issuedAmount,
          pendingIssuedAmount,
          receivedRatio: this.calcRatio(yearReceiveAmount, contractIncomeAmount),
          pendingReceiveRatio: this.calcRatio(pendingReceiveAmount, contractIncomeAmount),
          paidRatio: this.calcRatio(yearPayAmount, contractExpenseAmount),
          pendingPayRatio: this.calcRatio(pendingPayAmount, contractExpenseAmount),
          issuedRatio: this.calcRatio(issuedAmount, contractTotalAmount),
          pendingIssuedRatio: this.calcRatio(pendingIssuedAmount, contractTotalAmount),
          incomeInvoiceRatio: this.calcRatio(yearInputInvoiceAmount, contractIncomeAmount),
          expenseInvoiceRatio: this.calcRatio(yearOutputInvoiceAmount, contractExpenseAmount),
          contractCount: yearContractRows.length,
          accountCount: yearAccountRows.length,
          invoiceCount: yearInvoiceRows.length
        }

        this.$nextTick(() => this.renderCharts())
      } catch (error) {
        this.$message.error((error && error.message) || '仪表盘数据加载失败')
      } finally {
        this.loading = false
      }
    },
    handleYearChange() {
      this.loadDashboardData()
    },
    initYearOptions(contractRows, accountRows, invoiceRows) {
      const years = []
      ;[...contractRows, ...accountRows, ...invoiceRows].forEach(item => {
        const year = this.getYear(
          item.accountDate || item.invoiceDate || item.applyTime || item.createTime || item.startDate || item.updateTime
        )
        if (year) years.push(year)
      })
      const uniqueYears = Array.from(new Set(years)).sort((a, b) => b - a)
      this.yearOptions = uniqueYears.length ? uniqueYears : [new Date().getFullYear()]
      if (!this.yearOptions.includes(this.selectedYear)) {
        this.selectedYear = this.yearOptions[0]
      }
    },
    getYear(value) {
      if (!value) return null
      const date = new Date(value)
      if (isNaN(date.getTime())) return null
      return date.getFullYear()
    },
    getInvoiceBizType,
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
    },
    getChartInstance(refName) {
      const dom = this.$refs[refName]
      if (!dom) return null
      if (this.charts[refName]) {
        this.charts[refName].dispose()
      }
      this.charts[refName] = echarts.init(dom)
      return this.charts[refName]
    },
    createDonutOption(title, valueA, valueB, labelA, labelB, colors) {
      const safeA = this.toNumber(valueA)
      const safeB = this.toNumber(valueB)
      const total = safeA + safeB
      const displayData = total === 0 ? [
        { value: 1, name: '暂无数据', itemStyle: { color: '#e5e9f2' } }
      ] : [
        { value: safeA, name: labelA },
        { value: safeB, name: labelB }
      ]
      return {
        color: colors,
        tooltip: {
          trigger: 'item',
          formatter: params => total === 0 ? '暂无数据' : `${params.name}<br/>金额：¥ ${this.formatMoney(params.value)}<br/>占比：${params.percent}%`
        },
        legend: {
          bottom: 0,
          icon: 'circle',
          textStyle: { color: '#606266' }
        },
        graphic: total === 0 ? [{
          type: 'text',
          left: 'center',
          top: '42%',
          style: {
            text: '暂无数据',
            textAlign: 'center',
            fill: '#909399',
            fontSize: 16,
            fontWeight: 600
          }
        }] : [{
          type: 'group',
          left: 'center',
          top: '34%',
          children: [
            {
              type: 'text',
              style: {
                text: title,
                textAlign: 'center',
                fill: '#909399',
                fontSize: 14
              },
              left: -36,
              top: 0
            },
            {
              type: 'text',
              style: {
                text: `¥ ${this.formatMoney(total)}`,
                textAlign: 'center',
                fill: '#1f2d3d',
                fontSize: 18,
                fontWeight: 700
              },
              left: -64,
              top: 24
            }
          ]
        }],
        series: [{
          type: 'pie',
          radius: ['50%', '76%'],
          center: ['50%', '42%'],
          avoidLabelOverlap: true,
          minAngle: total === 0 ? 0 : 8,
          label: {
            show: total !== 0,
            formatter: params => `${params.name}\n¥ ${this.formatMoney(params.value)}\n${params.percent}%`,
            color: '#303133',
            fontSize: 12,
            lineHeight: 18
          },
          labelLine: {
            show: total !== 0,
            length: 16,
            length2: 10
          },
          data: displayData
        }]
      }
    },
    renderCharts() {
      const cashFlowChart = this.getChartInstance('cashFlowChart')
      const invoiceChart = this.getChartInstance('invoiceChart')
      const receiveProgressChart = this.getChartInstance('receiveProgressChart')
      const payProgressChart = this.getChartInstance('payProgressChart')
      const invoiceProgressChart = this.getChartInstance('invoiceProgressChart')
      if (!cashFlowChart || !invoiceChart || !receiveProgressChart || !payProgressChart || !invoiceProgressChart) return

      cashFlowChart.setOption(this.createDonutOption(
        '收付款',
        this.metrics.yearReceiveAmount,
        this.metrics.yearPayAmount,
        '收款',
        '付款',
        ['#3ecf8e', '#ff7a7a']
      ))

      invoiceChart.setOption(this.createDonutOption(
        '进销项',
        this.metrics.yearOutputInvoiceAmount,
        this.metrics.yearInputInvoiceAmount,
        '销项开票',
        '进项开票',
        ['#8b5cf6', '#f59e0b']
      ))

      receiveProgressChart.setOption(this.createDonutOption(
        '收款进度',
        this.metrics.receivedAmount,
        this.metrics.pendingReceiveAmount,
        '已收',
        '待收',
        ['#22c55e', '#60a5fa']
      ))

      payProgressChart.setOption(this.createDonutOption(
        '付款进度',
        this.metrics.paidAmount,
        this.metrics.pendingPayAmount,
        '已付',
        '待付',
        ['#fb923c', '#f87171']
      ))

      invoiceProgressChart.setOption(this.createDonutOption(
        '开票进度',
        this.metrics.issuedAmount,
        this.metrics.pendingIssuedAmount,
        '已开票',
        '待开票',
        ['#7c3aed', '#14b8a6']
      ))
    },
    handleResize() {
      Object.keys(this.charts).forEach(key => {
        if (this.charts[key]) this.charts[key].resize()
      })
    }
  }
}
</script>

<style scoped>
.instrument-page {
  padding: 20px;
  min-height: 100vh;
  background:
    radial-gradient(circle at top right, rgba(108, 99, 255, 0.12), transparent 28%),
    radial-gradient(circle at top left, rgba(56, 189, 248, 0.14), transparent 24%),
    linear-gradient(180deg, #f5f7fb 0%, #eef3fb 100%);
}
.hero-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 26px 28px;
  border-radius: 22px;
  margin-bottom: 18px;
  background: linear-gradient(135deg, #1d4ed8 0%, #4338ca 45%, #7c3aed 100%);
  color: #fff;
  box-shadow: 0 18px 40px rgba(67, 56, 202, 0.22);
}
.hero-left {
  max-width: 760px;
}
.hero-title {
  font-size: 30px;
  font-weight: 700;
  margin-bottom: 8px;
}
.hero-desc {
  font-size: 14px;
  opacity: 0.94;
  line-height: 1.8;
}
.hero-right {
  display: flex;
  align-items: center;
  gap: 10px;
}
.year-select {
  width: 140px;
}
.metric-row,
.chart-row {
  margin-bottom: 2px;
}
.metric-card,
.detail-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(8px);
  border-radius: 20px;
  padding: 22px;
  margin-bottom: 16px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.8);
}
.metric-card.contract-income { border-top: 4px solid #3b82f6; }
.metric-card.contract-expense { border-top: 4px solid #0f766e; }
.metric-card.receive { border-top: 4px solid #22c55e; }
.metric-card.pay { border-top: 4px solid #ef4444; }
.metric-card.output-invoice { border-top: 4px solid #8b5cf6; }
.metric-card.input-invoice { border-top: 4px solid #f59e0b; }
.metric-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 14px;
}
.metric-value {
  font-size: 26px;
  line-height: 1.2;
  font-weight: 700;
  color: #111827;
}
.metric-sub {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}
.chart-card {
  border-radius: 22px;
  margin-bottom: 16px;
  border: none;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.06);
  overflow: hidden;
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
}
.chart-header-note {
  font-size: 12px;
  color: #909399;
}
.chart-box {
  height: 320px;
}
.summary-card {
  min-height: 384px;
}
.summary-list {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
}
.summary-item {
  padding: 18px;
  border-radius: 16px;
  background: linear-gradient(180deg, #f8fbff 0%, #f4f7fc 100%);
  border: 1px solid #edf2f7;
  display: flex;
  flex-direction: column;
}
.summary-item span {
  color: #909399;
  font-size: 12px;
  margin-bottom: 8px;
}
.summary-item strong {
  font-size: 22px;
  color: #111827;
}
.detail-card {
  line-height: 1.9;
}
.detail-list {
  color: #606266;
}
.detail-item {
  padding: 4px 0;
}
@media (max-width: 992px) {
  .hero-section {
    flex-direction: column;
    align-items: flex-start;
  }
  .hero-right {
    width: 100%;
    flex-wrap: wrap;
  }
}
@media (max-width: 768px) {
  .instrument-page {
    padding: 12px;
  }
  .summary-list {
    grid-template-columns: 1fr;
  }
  .chart-box {
    height: 280px;
  }
}
</style>
