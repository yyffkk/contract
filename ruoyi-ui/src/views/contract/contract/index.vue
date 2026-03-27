<template>
  <div class="app-container">
    <!-- ================= 全局顶部：合同管理标题 & 导航卡片 ================= -->
    <div class="page-header">
      <h2 class="page-title">合同管理</h2>
      <p class="page-desc">统一查看、审批、签署、归档合同与用印申请</p>
    </div>

    <!-- 导航卡片区域 -->
    <div class="nav-cards">
      <el-card
        v-for="(item, index) in navItems"
        :key="index"
        shadow="hover"
        class="nav-card"
        :class="{ 'active-card': navActiveTab === item.key }"
        @click.native="handleNavClick(item)"
      >
        <div class="card-content">
          <div class="card-left">
            <div class="card-icon-wrap">
              <i :class="item.icon" class="card-icon"></i>
            </div>
            <div class="card-texts">
              <span class="card-label">{{ item.label }}</span>
              <span class="card-sub-label">{{ item.desc }}</span>
            </div>
          </div>
          <el-badge :value="item.count" :hidden="item.count === 0" class="card-badge" />
        </div>
      </el-card>
    </div>

    <div v-if="navActiveTab !== 'all' || true" class="detail-list-section fade-in">
      <!-- 1. 顶部标签页 & 单据类型 -->
      <el-card shadow="never" class="header-card">
        <div class="header-content">
          <div class="header-left">
            <el-tabs v-model="listActiveTab" @tab-click="handleListTabClick">
              <el-tab-pane label="全部" name="all_list"></el-tab-pane>
              <el-tab-pane label="审批中" name="approving"></el-tab-pane>
              <el-tab-pane label="签署中" name="signing"></el-tab-pane>
              <el-tab-pane label="归档中" name="archiving"></el-tab-pane>
              <el-tab-pane label="已归档" name="archived"></el-tab-pane>
              <el-tab-pane label="即将到期" name="expiring"></el-tab-pane>
            </el-tabs>
          </div>

          <div class="header-right">
            <el-select
              v-model="documentType"
              placeholder="请选择单据类型"
              size="small"
              style="width: 160px;"
              @change="onDocumentTypeChange"
            >
              <el-option label="智能合同审批" value="approval"></el-option>
              <el-option label="用印申请单" value="seal"></el-option>
            </el-select>
          </div>
        </div>
      </el-card>

      <!-- 2. 高级搜索表单 -->
      <el-card shadow="never" class="search-card">
        <div class="search-card-title">
          <div class="search-title-left">
            <i class="el-icon-search"></i>
            <span>高级搜索</span>
          </div>
          <el-button type="text" @click="toggleSearch" class="expand-btn">
            {{ showSearch ? '收起' : '展开' }}
            <i :class="showSearch ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
          </el-button>
        </div>

        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" label-width="90px">
          <el-form-item label="签署状态" prop="signStatus">
            <el-select v-model="queryParams.signStatus" placeholder="请选择" clearable style="width: 180px;">
              <el-option label="审批中" value="1"></el-option>
              <el-option label="签署中" value="2"></el-option>
              <el-option label="待归档" value="3"></el-option>
              <el-option label="归档确认中" value="4"></el-option>
              <el-option label="已归档" value="5"></el-option>
              <el-option label="已撤销" value="6"></el-option>
              <el-option label="已拒绝" value="7"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="履约状态" prop="performanceStatus">
            <el-select v-model="queryParams.performanceStatus" placeholder="请选择" clearable style="width: 180px;">
              <el-option label="未履约" value="1"></el-option>
              <el-option label="待生效" value="2"></el-option>
              <el-option label="履约中" value="3"></el-option>
              <el-option label="30天内到期" value="4"></el-option>
              <el-option label="7天内到期" value="5"></el-option>
              <el-option label="今日到期" value="6"></el-option>
              <el-option label="已到期" value="7"></el-option>
              <el-option label="已完结" value="8"></el-option>
            </el-select>
          </el-form-item>

          <el-form-item label="合同名称" prop="contractName" v-if="isApproval">
            <el-input v-model="queryParams.contractName" placeholder="请输入" clearable style="width: 180px;" />
          </el-form-item>
          <el-form-item label="审批单名称" prop="name" v-else>
            <el-input v-model="queryParams.name" placeholder="请输入" clearable style="width: 180px;" />
          </el-form-item>

          <el-form-item label="合同编号" prop="contractNumber">
            <el-input v-model="queryParams.contractNumber" placeholder="请输入" clearable style="width: 180px;" />
          </el-form-item>

          <el-form-item label="归属人" prop="owner">
            <el-input v-model="queryParams.owner" placeholder="请输入归属人" clearable style="width: 180px;" />
          </el-form-item>

          <template v-if="showSearch">
            <el-form-item label="对方主体名称" prop="otherPartyName" v-if="isApproval">
              <el-input v-model="queryParams.otherPartyName" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>
            <el-form-item label="对方单位" prop="opponentUnit" v-else>
              <el-input v-model="queryParams.opponentUnit" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="我方主体名称" prop="myPartyName" v-if="isApproval">
              <el-input v-model="queryParams.myPartyName" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="归属部门" prop="department">
              <el-input v-model="queryParams.department" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="合同分类" prop="category">
              <el-input v-model="queryParams.category" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="印章类型" prop="sealType">
              <el-select v-model="queryParams.sealType" placeholder="请选择" clearable style="width: 180px;">
                <el-option label="公章" value="official"></el-option>
                <el-option label="合同章" value="contract"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="签署方式" prop="signMethod">
              <el-select v-model="queryParams.signMethod" placeholder="请选择" clearable style="width: 180px;">
                <el-option label="电子签" value="electronic"></el-option>
                <el-option label="纸质签" value="paper"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="合同提交人" prop="submitter" v-if="isApproval">
              <el-input v-model="queryParams.submitter" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="代提交人" prop="proxySubmitter" v-if="isApproval">
              <el-input v-model="queryParams.proxySubmitter" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="申请时间" prop="applyTime">
              <el-date-picker
                v-model="queryParams.applyTime"
                style="width: 180px"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="选择日期"
              ></el-date-picker>
            </el-form-item>

            <el-form-item label="开始时间" prop="startDate">
              <el-date-picker
                v-model="queryParams.startDate"
                style="width: 180px"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="选择日期"
              ></el-date-picker>
            </el-form-item>

            <el-form-item label="到期时间" prop="endDate">
              <el-date-picker
                v-model="queryParams.endDate"
                style="width: 180px"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="选择日期"
              ></el-date-picker>
            </el-form-item>

            <el-form-item label="发票状态" prop="invoiceStatus" v-if="isApproval">
              <el-select v-model="queryParams.invoiceStatus" placeholder="请选择" clearable style="width: 180px;">
                <el-option label="未开票" value="no_invoice"></el-option>
                <el-option label="已开票" value="invoiced"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="账款状态" prop="accountStatus" v-if="isApproval">
              <el-select v-model="queryParams.accountStatus" placeholder="请选择" clearable style="width: 180px;">
                <el-option label="未付款" value="unpaid"></el-option>
                <el-option label="已付款" value="paid"></el-option>
              </el-select>
            </el-form-item>

            <el-form-item label="项目" prop="project" v-if="isApproval">
              <el-input v-model="queryParams.project" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="合同审批编号" prop="approvalNumber" v-if="isApproval">
              <el-input v-model="queryParams.approvalNumber" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="归档审批编号" prop="archiveApprovalNumber" v-if="isApproval">
              <el-input
                v-model="queryParams.archiveApprovalNumber"
                placeholder="请输入"
                clearable
                style="width: 180px;"
              />
            </el-form-item>

            <el-form-item label="归档编号" prop="archiveId" v-if="isApproval">
              <el-input v-model="queryParams.archiveId" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="归档人" prop="archiver" v-if="isApproval">
              <el-input v-model="queryParams.archiver" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="协同人" prop="cooperators" v-if="isApproval">
              <el-input v-model="queryParams.cooperators" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="合同说明" prop="description" v-if="isApproval">
              <el-input v-model="queryParams.description" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="借阅状态" prop="borrowStatus" v-if="isApproval">
              <el-input v-model="queryParams.borrowStatus" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="归档时间" prop="archiveTime">
              <el-date-picker
                v-model="queryParams.archiveTime"
                style="width: 180px"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="选择日期"
              ></el-date-picker>
            </el-form-item>

            <el-form-item label="审批通过时间" prop="approvePassTime">
              <el-date-picker
                v-model="queryParams.approvePassTime"
                style="width: 180px"
                value-format="yyyy-MM-dd"
                type="date"
                placeholder="选择日期"
              ></el-date-picker>
            </el-form-item>

            <el-form-item label="收款/付款进度" prop="paymentProgress" v-if="isApproval">
              <el-input v-model="queryParams.paymentProgress" placeholder="例如：50%" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="已收/已付" prop="receivedPaid" v-if="isApproval">
              <el-input v-model="queryParams.receivedPaid" placeholder="金额" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="待收/待付" prop="pendingReceivedPaid" v-if="isApproval">
              <el-input
                v-model="queryParams.pendingReceivedPaid"
                placeholder="金额"
                clearable
                style="width: 180px;"
              />
            </el-form-item>

            <el-form-item label="开票/收票进度" prop="invoicingProgress" v-if="isApproval">
              <el-input
                v-model="queryParams.invoicingProgress"
                placeholder="例如：30%"
                clearable
                style="width: 180px;"
              />
            </el-form-item>

            <el-form-item label="已开/已收" prop="issuedReceived" v-if="isApproval">
              <el-input v-model="queryParams.issuedReceived" placeholder="金额" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="待开/待收" prop="pendingIssuedReceived" v-if="isApproval">
              <el-input
                v-model="queryParams.pendingIssuedReceived"
                placeholder="金额"
                clearable
                style="width: 180px;"
              />
            </el-form-item>

            <el-form-item label="其他付款金额" prop="otherPaymentAmount" v-if="isApproval">
              <el-input
                v-model="queryParams.otherPaymentAmount"
                placeholder="金额"
                clearable
                style="width: 180px;"
              />
            </el-form-item>

            <el-form-item label="其他收款金额" prop="otherReceiptAmount" v-if="isApproval">
              <el-input
                v-model="queryParams.otherReceiptAmount"
                placeholder="金额"
                clearable
                style="width: 180px;"
              />
            </el-form-item>

            <el-form-item label="签署备注" prop="signRemarks" v-if="isApproval">
              <el-input v-model="queryParams.signRemarks" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>

            <el-form-item label="归档备注" prop="archiveRemarks" v-if="isApproval">
              <el-input v-model="queryParams.archiveRemarks" placeholder="请输入" clearable style="width: 180px;" />
            </el-form-item>
          </template>

          <el-form-item class="search-actions">
            <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
            <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
          </el-form-item>
        </el-form>
      </el-card>

      <!-- 3. 工具栏按钮 -->
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" icon="el-icon-upload2" @click="handleImport">导入</el-button>

          <el-dropdown split-button type="primary" @command="handleExportCommand">
            导出
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="selected">导出选中</el-dropdown-item>
              <el-dropdown-item command="all">导出全部</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>

          <el-button type="primary" icon="el-icon-download" @click="handleDownloadTemplate">
            下载模板
          </el-button>

          <el-button plain :disabled="single" @click="handleTransfer">转交</el-button>
          <el-button plain :disabled="single" @click="handleUpdate">修改</el-button>
          <el-button plain :disabled="single" @click="handleChangeCategory">更换分类</el-button>
          <el-button plain :disabled="multiple" @click="handleDelete">删除</el-button>
          <el-button plain :disabled="single" @click="handleBatchDownload">批量下载</el-button>
          <el-button plain @click="handleBatchArchive">批量归档</el-button>
        </div>

        <div class="toolbar-right">
          <el-button type="text" icon="el-icon-s-operation" @click="handleColumnSetting">
            显示列
          </el-button>
        </div>
      </div>

      <!-- 4. 数据表格 -->
      <el-card shadow="never" class="table-card">
        <el-table
          v-loading="loading"
          :data="contractList"
          @selection-change="handleSelectionChange"
          border
          class="contract-table"
          header-cell-class-name="table-header-gray"
        >
          <el-table-column type="selection" width="55" align="center" />

          <el-table-column label="合同名称" min-width="180" show-overflow-tooltip v-if="columnSettings.name">
            <template slot-scope="scope">
              <div class="name-cell">
                <div class="name-icon">
                  <i class="el-icon-document"></i>
                </div>
                <div class="name-content">
                  <div class="name-title">{{ isApproval ? scope.row.contractName : scope.row.name }}</div>
                  <div class="name-sub">
                    {{ scope.row.category ? formatCategory(scope.row.category) : '未分类' }}
                  </div>
                </div>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="归属人" width="110" align="center" prop="owner" v-if="columnSettings.owner">
            <template slot-scope="scope">
              <span>{{ scope.row.owner || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="合同编号"
            width="170"
            show-overflow-tooltip
            prop="contractNumber"
            v-if="columnSettings.contractNumber"
          >
            <template slot-scope="scope">
              <div class="copy-cell">
                <span>{{ scope.row.contractNumber || '—' }}</span>
                <i
                  v-if="scope.row.contractNumber"
                  class="el-icon-copy-document copy-icon"
                  @click="copyText(scope.row.contractNumber)"
                ></i>
              </div>
            </template>
          </el-table-column>

          <el-table-column label="签署状态" width="110" align="center" prop="signStatus" v-if="columnSettings.signStatus">
            <template slot-scope="scope">
              <el-tag :type="getSignStatusType(scope.row.signStatus)" size="small" effect="light">
                {{ getSignStatusLabel(scope.row.signStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            label="履约状态"
            width="110"
            align="center"
            prop="performanceStatus"
            v-if="columnSettings.performanceStatus"
          >
            <template slot-scope="scope">
              <el-tag :type="getPerformanceStatusType(scope.row.performanceStatus)" size="small" effect="plain">
                {{ getPerformanceStatusLabel(scope.row.performanceStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column label="金额类型" width="100" align="center" prop="amountType" v-if="columnSettings.amountType">
            <template slot-scope="scope">
              <span>{{ formatAmountType(scope.row.amountType) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="合同总额" width="120" align="center" v-if="columnSettings.totalAmount">
            <template slot-scope="scope">
              <span>{{ scope.row.totalAmount || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="签署日期" width="110" align="center" prop="signDate" v-if="columnSettings.signDate">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.signDate, '{y}-{m}-{d}') || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="期限类型" width="100" align="center" prop="termType" v-if="columnSettings.termType">
            <template slot-scope="scope">
              <span>{{ formatTermType(scope.row.termType) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="开始时间" width="110" align="center" prop="startDate" v-if="columnSettings.startDate">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.startDate, '{y}-{m}-{d}') || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="到期时间" width="110" align="center" prop="endDate" v-if="columnSettings.endDate">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.endDate, '{y}-{m}-{d}') || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="合同分类"
            width="120"
            show-overflow-tooltip
            prop="category"
            v-if="columnSettings.category"
          >
            <template slot-scope="scope">
              <span>{{ formatCategory(scope.row.category) }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="合同说明"
            min-width="150"
            show-overflow-tooltip
            prop="description"
            v-if="columnSettings.description"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.description || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="归档人" width="100" align="center" prop="archiver" v-if="columnSettings.archiver">
            <template slot-scope="scope">
              <span>{{ scope.row.archiver || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="协同人"
            width="120"
            show-overflow-tooltip
            prop="cooperators"
            v-if="columnSettings.cooperators"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.cooperators || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="我方主体名称"
            width="150"
            show-overflow-tooltip
            prop="myPartyName"
            v-if="columnSettings.myPartyName && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.myPartyName || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="对方主体名称"
            width="150"
            show-overflow-tooltip
            prop="otherPartyName"
            v-if="columnSettings.otherPartyName && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.otherPartyName || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="申请时间" width="110" align="center" prop="applyTime" v-if="columnSettings.applyTime">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d}') || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="合同提交人"
            width="110"
            align="center"
            prop="submitter"
            v-if="columnSettings.submitter && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.submitter || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="签署方式" width="110" align="center" prop="signMethod" v-if="columnSettings.signMethod">
            <template slot-scope="scope">
              <span>{{ formatSignMethod(scope.row.signMethod) }}</span>
            </template>
          </el-table-column>

          <el-table-column label="印章类型" width="100" align="center" prop="sealType" v-if="columnSettings.sealType">
            <template slot-scope="scope">
              <span>{{ formatSealType(scope.row.sealType) }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="收款/付款进度"
            width="120"
            align="center"
            prop="paymentProgress"
            v-if="columnSettings.paymentProgress && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.paymentProgress || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="已收/已付"
            width="100"
            align="center"
            prop="receivedPaid"
            v-if="columnSettings.receivedPaid && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.receivedPaid || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="待收/待付"
            width="100"
            align="center"
            prop="pendingReceivedPaid"
            v-if="columnSettings.pendingReceivedPaid && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.pendingReceivedPaid || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="开票/收票进度"
            width="120"
            align="center"
            prop="invoicingProgress"
            v-if="columnSettings.invoicingProgress && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.invoicingProgress || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="已开/已收"
            width="100"
            align="center"
            prop="issuedReceived"
            v-if="columnSettings.issuedReceived && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.issuedReceived || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="待开/待收"
            width="100"
            align="center"
            prop="pendingIssuedReceived"
            v-if="columnSettings.pendingIssuedReceived && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.pendingIssuedReceived || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="签署备注"
            min-width="150"
            show-overflow-tooltip
            prop="signRemarks"
            v-if="columnSettings.signRemarks && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.signRemarks || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="归档备注"
            min-width="150"
            show-overflow-tooltip
            prop="archiveRemarks"
            v-if="columnSettings.archiveRemarks && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.archiveRemarks || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="代提交人"
            width="100"
            align="center"
            prop="proxySubmitter"
            v-if="columnSettings.proxySubmitter"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.proxySubmitter || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="归属部门"
            width="120"
            show-overflow-tooltip
            prop="department"
            v-if="columnSettings.department"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.department || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="归档文件"
            min-width="180"
            show-overflow-tooltip
            prop="archiveFile"
            v-if="columnSettings.archiveFile"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.archiveFile || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="归档时间" width="110" align="center" prop="archiveTime" v-if="columnSettings.archiveTime">
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.archiveTime, '{y}-{m}-{d}') || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="附属文件"
            width="100"
            align="center"
            prop="attachments"
            v-if="columnSettings.attachments"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.attachments ? '有附件' : '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="账款状态"
            width="100"
            align="center"
            prop="accountStatus"
            v-if="columnSettings.accountStatus && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ formatAccountStatus(scope.row.accountStatus) }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="发票状态"
            width="100"
            align="center"
            prop="invoiceStatus"
            v-if="columnSettings.invoiceStatus && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ formatInvoiceStatus(scope.row.invoiceStatus) }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="合同审批编号"
            width="150"
            show-overflow-tooltip
            prop="approvalNumber"
            v-if="columnSettings.approvalNumber && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.approvalNumber || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="项目"
            width="120"
            show-overflow-tooltip
            prop="project"
            v-if="columnSettings.project && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.project || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="审批通过时间"
            width="120"
            align="center"
            prop="approvePassTime"
            v-if="columnSettings.approvePassTime && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.approvePassTime, '{y}-{m}-{d}') || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="借阅状态"
            width="100"
            align="center"
            prop="borrowStatus"
            v-if="columnSettings.borrowStatus && isApproval"
          >
            <template slot-scope="scope">
              <el-tag :type="getBorrowStatusType(scope.row.borrowStatus)" size="small">
                {{ getBorrowStatusLabel(scope.row.borrowStatus) }}
              </el-tag>
            </template>
          </el-table-column>

          <el-table-column
            label="归档编号"
            width="120"
            show-overflow-tooltip
            prop="archiveId"
            v-if="columnSettings.archiveId && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.archiveId || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="归档审批编号"
            width="150"
            show-overflow-tooltip
            prop="archiveApprovalNumber"
            v-if="columnSettings.archiveApprovalNumber && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.archiveApprovalNumber || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="其他付款金额"
            width="120"
            align="center"
            prop="otherPaymentAmount"
            v-if="columnSettings.otherPaymentAmount && isApproval"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.otherPaymentAmount || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="其他收款金额"
            width="120"
            align="center"
            prop="otherReceiptAmount"
            v-if="columnSettings.otherReceiptAmount"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.otherReceiptAmount || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column
            label="合同总额计算方式"
            width="150"
            show-overflow-tooltip
            prop="totalAmountCalculationMethod"
            v-if="columnSettings.totalAmountCalculationMethod"
          >
            <template slot-scope="scope">
              <span>{{ scope.row.totalAmountCalculationMethod || '—' }}</span>
            </template>
          </el-table-column>

          <el-table-column label="操作" width="280" fixed="right" align="center">
            <template slot-scope="scope">
              <div class="action-cell">
                <el-button
                  v-if="scope.row.signStatus === '1'"
                  type="text"
                  class="table-action-primary"
                  @click="handleApprove(scope.row)"
                >
                  去审批
                </el-button>

                <el-button
                  v-else-if="scope.row.signStatus === '2'"
                  type="text"
                  class="table-action-primary"
                  @click="handleSign(scope.row)"
                >
                  签署
                </el-button>

                <el-button
                  v-else-if="scope.row.signStatus === '3' || scope.row.signStatus === '4'"
                  type="text"
                  class="table-action-primary"
                  @click="handleArchive(scope.row)"
                >
                  去归档
                </el-button>

                <el-button
                  v-if="showRecordAccountAction(scope.row)"
                  type="text"
                  style="color:#e6a23c;font-weight:600"
                  @click="handleRecordAccount(scope.row)"
                >
                  录账款
                </el-button>

                <el-button type="text" @click="handleDetail(scope.row)">
                  详情
                </el-button>

                <el-button
                  v-if="scope.row.signStatus === '5' && String(scope.row.performanceStatus) !== '8'"
                  type="text"
                  style="color:#67c23a;font-weight:600"
                  @click="handleFinish(scope.row)"
                >
                  完结
                </el-button>

                <el-dropdown trigger="click" @command="(cmd) => handleCommand(cmd, scope.row)">
                  <span class="el-dropdown-link">
                    更多<i class="el-icon-arrow-down el-icon--right"></i>
                  </span>
                  <el-dropdown-menu slot="dropdown">
                    <template v-if="scope.row.signStatus === '1'">
                      <el-dropdown-item command="edit">修改</el-dropdown-item>
                      <el-dropdown-item command="transfer">转交</el-dropdown-item>
                      <el-dropdown-item command="revoke">撤销</el-dropdown-item>
                      <el-dropdown-item command="delete">删除</el-dropdown-item>
                    </template>
                    <template v-else-if="scope.row.signStatus === '2'">
                      <el-dropdown-item command="edit">修改</el-dropdown-item>
                      <el-dropdown-item command="transfer">转交</el-dropdown-item>
                      <el-dropdown-item command="void">作废</el-dropdown-item>
                      <el-dropdown-item command="delete">删除</el-dropdown-item>
                    </template>
                    <template v-else-if="scope.row.signStatus === '3' || scope.row.signStatus === '4'">
                      <el-dropdown-item command="archive">去归档</el-dropdown-item>
                      <el-dropdown-item v-if="showRecordAccountAction(scope.row)" command="recordAccount">录账款</el-dropdown-item>
                      <el-dropdown-item command="edit">修改</el-dropdown-item>
                      <el-dropdown-item command="transfer">转交</el-dropdown-item>
                      <el-dropdown-item command="delete">删除</el-dropdown-item>
                    </template>
                    <template v-else>
                      <el-dropdown-item v-if="showRecordAccountAction(scope.row)" command="recordAccount">录账款</el-dropdown-item>
                      <el-dropdown-item command="edit">修改</el-dropdown-item>
                      <el-dropdown-item command="transfer">转交</el-dropdown-item>
                      <el-dropdown-item command="delete">删除</el-dropdown-item>
                    </template>
                  </el-dropdown-menu>
                </el-dropdown>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
          class="table-pagination"
        />
      </el-card>
    </div>

    <!-- ★★★★★ 原弹窗组件（新增时使用）★★★★★ -->
    <contract-dialog ref="contractDialogRef" @refresh="getList" />

    <!-- ================= 抽屉：编辑合同/用印申请 ================= -->
    <el-drawer
      :title="isApproval ? '编辑合同' : '编辑用印申请'"
      :visible.sync="editDrawerVisible"
      direction="rtl"
      size="50%"
      :before-close="handleCloseEditDrawer"
      destroy-on-close
      custom-class="edit-drawer"
    >
      <div class="edit-drawer-shell">
      <div class="edit-drawer-content">
        <el-form
          v-if="isApproval"
          ref="editForm"
          :model="editForm"
          :rules="editRules"
          label-width="120px"
          label-position="right"
        >
          <el-form-item label="合同名称" prop="contractName">
            <el-input v-model="editForm.contractName" placeholder="请输入合同名称" />
          </el-form-item>
          <el-form-item label="合同编号" prop="contractNumber">
            <el-input v-model="editForm.contractNumber" placeholder="请输入合同编号" />
          </el-form-item>
          <el-form-item label="签署状态" prop="signStatus">
            <el-select v-model="editForm.signStatus" placeholder="请选择">
              <el-option label="审批中" value="1" />
              <el-option label="签署中" value="2" />
              <el-option label="待归档" value="3" />
              <el-option label="归档确认中" value="4" />
              <el-option label="已归档" value="5" />
              <el-option label="已撤销" value="6" />
              <el-option label="已拒绝" value="7" />
            </el-select>
          </el-form-item>
          <el-form-item label="履约状态" prop="performanceStatus">
            <el-select v-model="editForm.performanceStatus" placeholder="请选择">
              <el-option label="未履约" value="1" />
              <el-option label="待生效" value="2" />
              <el-option label="履约中" value="3" />
              <el-option label="30天内到期" value="4" />
              <el-option label="7天内到期" value="5" />
              <el-option label="今日到期" value="6" />
              <el-option label="已到期" value="7" />
              <el-option label="已完结" value="8" />
            </el-select>
          </el-form-item>
          <el-form-item label="合同总额" prop="totalAmount">
            <el-input v-model="editForm.totalAmount" placeholder="请输入合同总额" />
          </el-form-item>
          <el-form-item label="我方主体名称" prop="myPartyName">
            <el-input v-model="editForm.myPartyName" placeholder="请输入我方主体名称" />
          </el-form-item>
          <el-form-item label="对方主体名称" prop="otherPartyName">
            <el-input v-model="editForm.otherPartyName" placeholder="请输入对方主体名称" />
          </el-form-item>
          <el-form-item label="归属人" prop="owner">
            <el-input v-model="editForm.owner" placeholder="请输入归属人" />
          </el-form-item>
          <el-form-item label="归属部门" prop="department">
            <el-input v-model="editForm.department" placeholder="请输入归属部门" />
          </el-form-item>
          <el-form-item label="开始时间" prop="startDate">
            <el-date-picker
              v-model="editForm.startDate"
              type="date"
              placeholder="选择日期"
              value-format="yyyy-MM-dd"
            />
          </el-form-item>
          <el-form-item label="到期时间" prop="endDate">
            <el-date-picker
              v-model="editForm.endDate"
              type="date"
              placeholder="选择日期"
              value-format="yyyy-MM-dd"
            />
          </el-form-item>
          <el-form-item label="合同说明" prop="description">
            <el-input v-model="editForm.description" type="textarea" :rows="3" placeholder="请输入合同说明" />
          </el-form-item>

          <div class="edit-upload-panel">
            <div class="edit-upload-panel__title">文件上传</div>

            <div class="edit-upload-group">
              <div class="edit-upload-group__header">
                <div>
                  <div class="edit-upload-group__label">合同正文</div>
                  <div class="edit-upload-group__desc">支持 PDF / Word / 图片，多选后随保存一起上传</div>
                </div>
                <el-upload
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  :multiple="true"
                  accept=".pdf,.doc,.docx,.jpg,.jpeg,.png"
                  :on-change="handleDetailContentFileChange"
                >
                  <el-button size="mini" type="primary" plain icon="el-icon-upload2">选择正文文件</el-button>
                </el-upload>
              </div>

              <div v-if="editParsedContentFiles.length" class="edit-upload-existing">
                <div class="edit-upload-existing__title">已上传正文</div>
                <div class="edit-upload-list">
                  <div v-for="(file, index) in editParsedContentFiles" :key="`saved-content-${index}`" class="edit-upload-item existing">
                    <div class="edit-upload-item__left">
                      <i class="el-icon-document"></i>
                      <span>{{ file.name }}</span>
                    </div>
                    <el-button type="text" @click="downloadFile(file)">下载</el-button>
                  </div>
                </div>
              </div>

              <div v-if="detailUploadForm.contentFiles.length" class="edit-upload-list">
                <div v-for="(file, index) in detailUploadForm.contentFiles" :key="`content-${index}`" class="edit-upload-item">
                  <div class="edit-upload-item__left">
                    <i class="el-icon-document"></i>
                    <span>{{ file.name }}</span>
                  </div>
                  <el-button type="text" class="danger-text" @click="removeDetailSelectedFile('contentFiles', index)">删除</el-button>
                </div>
              </div>
              <div v-else class="edit-upload-empty">暂未选择新的正文文件</div>
            </div>

            <div class="edit-upload-group">
              <div class="edit-upload-group__header">
                <div>
                  <div class="edit-upload-group__label">文件附件</div>
                  <div class="edit-upload-group__desc">支持多选，保存修改时一并上传到附件区</div>
                </div>
                <el-upload
                  action="#"
                  :auto-upload="false"
                  :show-file-list="false"
                  :multiple="true"
                  :on-change="handleDetailAttachmentFileChange"
                >
                  <el-button size="mini" type="primary" plain icon="el-icon-folder-add">选择附件</el-button>
                </el-upload>
              </div>

              <div v-if="editParsedAttachments.length" class="edit-upload-existing">
                <div class="edit-upload-existing__title">已上传附件</div>
                <div class="edit-upload-list">
                  <div v-for="(file, index) in editParsedAttachments" :key="`saved-attachment-${index}`" class="edit-upload-item existing">
                    <div class="edit-upload-item__left">
                      <i class="el-icon-paperclip"></i>
                      <span>{{ file.name }}</span>
                    </div>
                    <el-button type="text" @click="downloadFile(file)">下载</el-button>
                  </div>
                </div>
              </div>

              <div v-if="detailUploadForm.attachmentFiles.length" class="edit-upload-list">
                <div v-for="(file, index) in detailUploadForm.attachmentFiles" :key="`attachment-${index}`" class="edit-upload-item">
                  <div class="edit-upload-item__left">
                    <i class="el-icon-paperclip"></i>
                    <span>{{ file.name }}</span>
                  </div>
                  <el-button type="text" class="danger-text" @click="removeDetailSelectedFile('attachmentFiles', index)">删除</el-button>
                </div>
              </div>
              <div v-else class="edit-upload-empty">暂未选择新的附件</div>
            </div>
          </div>
        </el-form>

        <el-form
          v-else
          ref="editForm"
          :model="editForm"
          :rules="editRules"
          label-width="120px"
          label-position="right"
        >
          <el-form-item label="审批单名称" prop="name">
            <el-input v-model="editForm.name" placeholder="请输入审批单名称" />
          </el-form-item>
          <el-form-item label="合同编号" prop="contractNumber">
            <el-input v-model="editForm.contractNumber" placeholder="请输入合同编号" />
          </el-form-item>
          <el-form-item label="签署状态" prop="signStatus">
            <el-select v-model="editForm.signStatus" placeholder="请选择">
              <el-option label="审批中" value="1" />
              <el-option label="签署中" value="2" />
              <el-option label="待归档" value="3" />
              <el-option label="归档确认中" value="4" />
              <el-option label="已归档" value="5" />
              <el-option label="已撤销" value="6" />
              <el-option label="已拒绝" value="7" />
            </el-select>
          </el-form-item>
          <el-form-item label="履约状态" prop="performanceStatus">
            <el-select v-model="editForm.performanceStatus" placeholder="请选择">
              <el-option label="未履约" value="1" />
              <el-option label="待生效" value="2" />
              <el-option label="履约中" value="3" />
              <el-option label="30天内到期" value="4" />
              <el-option label="7天内到期" value="5" />
              <el-option label="今日到期" value="6" />
              <el-option label="已到期" value="7" />
              <el-option label="已完结" value="8" />
            </el-select>
          </el-form-item>
          <el-form-item label="合同总额" prop="totalAmount">
            <el-input v-model="editForm.totalAmount" placeholder="请输入合同总额" />
          </el-form-item>
          <el-form-item label="对方单位" prop="opponentUnit">
            <el-input v-model="editForm.opponentUnit" placeholder="请输入对方单位" />
          </el-form-item>
          <el-form-item label="归属人" prop="owner">
            <el-input v-model="editForm.owner" placeholder="请输入归属人" />
          </el-form-item>
          <el-form-item label="用印部门" prop="department">
            <el-input v-model="editForm.department" placeholder="请输入用印部门" />
          </el-form-item>
          <el-form-item label="经办人" prop="handler">
            <el-input v-model="editForm.handler" placeholder="请输入经办人" />
          </el-form-item>
          <el-form-item label="文件类别" prop="fileCategory">
            <el-input v-model="editForm.fileCategory" placeholder="请输入文件类别" />
          </el-form-item>
          <el-form-item label="创建时间" prop="createTime">
            <el-date-picker
              v-model="editForm.createTime"
              type="datetime"
              placeholder="选择日期时间"
              value-format="yyyy-MM-dd HH:mm:ss"
            />
          </el-form-item>
        </el-form>
      </div>

      <div class="drawer-footer fixed-drawer-footer">
        <el-button @click="closeEditDrawer">取消</el-button>
        <el-button type="primary" @click="submitEditForm">确定</el-button>
      </div>
      </div>
    </el-drawer>

   <!-- 完结抽屉 -->
<el-drawer
  :visible.sync="finishDrawerVisible"
  direction="rtl"
  size="460px"
  :with-header="false"
  custom-class="finish-drawer modern-finish-drawer"
>
  <div class="finish-page">
    <div class="finish-hero">
      <div>
        <h2>合同完结</h2>
        <p>请确认回款、开票、归档等信息后，再执行完结。</p>
      </div>
      <el-tag type="success" effect="dark">完结动作</el-tag>
    </div>

    <div class="finish-contract-card">
      <div class="finish-contract-title">{{ currentDetail.contractName || currentDetail.name || (currentSignData && currentSignData.contractName) || '当前合同' }}</div>
      <div class="finish-contract-meta">
        <span>编号：{{ currentDetail.contractNumber || (currentSignData && currentSignData.contractNumber) || '—' }}</span>
        <span>履约状态：{{ getPerformanceStatusLabel(currentDetail.performanceStatus || '3') }}</span>
      </div>
    </div>

    <el-form :model="finishForm" label-width="92px" class="finish-form-card">
      <el-form-item label="完结状态">
        <el-select v-model="finishForm.finalStatus" style="width:100%">
          <el-option label="已完结" value="8" />
          <el-option label="已到期" value="7" />
        </el-select>
      </el-form-item>
      <el-form-item label="完结说明">
        <el-input
          v-model="finishForm.result"
          type="textarea"
          :rows="6"
          maxlength="300"
          show-word-limit
          placeholder="请填写完结结论、未结事项说明、责任人等"
        />
      </el-form-item>
    </el-form>

    <div class="finish-drawer-footer modern">
      <el-button @click="finishDrawerVisible = false">取消</el-button>
      <el-button type="primary" @click="submitFinish">确认完结</el-button>
    </div>
  </div>
</el-drawer>

    <!-- ================= 抽屉：详情 ================= -->
    <el-drawer
      :title="`合同详情 - ${currentDetail.contractName || currentDetail.name || ''}`"
      :visible.sync="detailDrawerVisible"
      direction="rtl"
      size="50%"
      :before-close="handleCloseDetailDrawer"
      custom-class="detail-drawer"
    >
      <div class="detail-page">
        <el-tabs v-model="detailTab" type="card" class="detail-tabs">
          <el-tab-pane label="详情" name="detail"></el-tab-pane>
          <el-tab-pane label="履约" name="performance"></el-tab-pane>
          <el-tab-pane label="操作记录" name="log"></el-tab-pane>
        </el-tabs>

        <div v-if="detailTab === 'detail'" class="detail-overview-page">
          <div class="detail-hero-card">
            <div class="detail-hero-main">
              <div class="detail-hero-icon"><i class="el-icon-document-copy"></i></div>
              <div class="detail-hero-info">
                <h2>{{ currentDetail.contractName || currentDetail.name || '未命名合同' }}</h2>
                <div class="detail-hero-meta">
                  <span>编号：{{ currentDetail.contractNumber || '—' }}</span>
                  <span>负责人：{{ currentDetail.owner || '—' }}</span>
                  <span>部门：{{ currentDetail.department || '—' }}</span>
                </div>
              </div>
            </div>
            <div class="detail-hero-tags">
              <el-tag size="medium" effect="dark" :type="getStatusTagType(currentDetail)">
                {{ getCurrentDetailStatusText(currentDetail) }}
              </el-tag>
              <el-tag size="medium" effect="plain" :type="getPerformanceStatusType(currentDetail.performanceStatus)">
                {{ getPerformanceStatusLabel(currentDetail.performanceStatus) }}
              </el-tag>
            </div>
          </div>

          <div class="detail-summary-grid">
            <div class="summary-card">
              <div class="summary-card__label">合同金额</div>
              <div class="summary-card__value money">{{ currentDetail.totalAmount ? '¥ ' + currentDetail.totalAmount : '无金额' }}</div>
            </div>
            <div class="summary-card">
              <div class="summary-card__label">合同分类</div>
              <div class="summary-card__value">{{ formatCategory(currentDetail.category) }}</div>
            </div>
            <div class="summary-card">
              <div class="summary-card__label">签署方式</div>
              <div class="summary-card__value">{{ formatSignMethod(currentDetail.signMethod) }}</div>
            </div>
            <div class="summary-card">
              <div class="summary-card__label">印章类型</div>
              <div class="summary-card__value">{{ formatSealType(currentDetail.sealType) }}</div>
            </div>
          </div>

          <el-card shadow="never" class="detail-block detail-premium-card">
            <div slot="header" class="detail-block-title">基本信息</div>
            <el-descriptions :column="2" border class="modern-descriptions">
              <el-descriptions-item label="来源">{{ currentDetail.source || '—' }}</el-descriptions-item>
              <el-descriptions-item label="提交人">{{ currentDetail.submitter || '—' }}</el-descriptions-item>
              <el-descriptions-item label="申请时间">{{ parseTime(currentDetail.applyTime, '{y}-{m}-{d}') || '—' }}</el-descriptions-item>
              <el-descriptions-item label="开始日期">{{ parseTime(currentDetail.startDate, '{y}-{m}-{d}') || '—' }}</el-descriptions-item>
              <el-descriptions-item label="到期日期">{{ parseTime(currentDetail.endDate, '{y}-{m}-{d}') || '—' }}</el-descriptions-item>
              <el-descriptions-item label="说明">{{ currentDetail.description || '—' }}</el-descriptions-item>
            </el-descriptions>
          </el-card>

          <el-card shadow="never" class="detail-block detail-premium-card">
            <div slot="header" class="detail-block-header">
              <span class="detail-block-title">合同正文</span>
            </div>

            <div v-if="parsedContentFiles.length > 0" class="detail-file-grid">
              <div v-for="(file, index) in parsedContentFiles" :key="'content-' + index" class="detail-file-card primary">
                <div class="detail-file-card__left">
                  <i class="el-icon-document"></i>
                  <div>
                    <div class="detail-file-card__name">{{ file.name }}</div>
                    <div class="detail-file-card__path">{{ file.url }}</div>
                  </div>
                </div>
                <div class="detail-file-card__actions">
                  <el-button size="mini" type="text" @click="previewFile(file)">预览</el-button>
                  <el-button size="mini" type="text" @click="downloadFile(file)">下载</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-text">暂无合同正文</div>
          </el-card>

          <el-card shadow="never" class="detail-block detail-premium-card">
            <div slot="header" class="detail-block-header">
              <span class="detail-block-title">文件附件</span>
            </div>

            <div v-if="parsedAttachments.length > 0" class="detail-file-grid">
              <div v-for="(file, index) in parsedAttachments" :key="'attachment-' + index" class="detail-file-card">
                <div class="detail-file-card__left">
                  <i class="el-icon-paperclip"></i>
                  <div>
                    <div class="detail-file-card__name">{{ file.name }}</div>
                    <div class="detail-file-card__path">{{ file.url }}</div>
                  </div>
                </div>
                <div class="detail-file-card__actions">
                  <el-button size="mini" type="text" @click="previewFile(file)">预览</el-button>
                  <el-button size="mini" type="text" @click="downloadFile(file)">下载</el-button>
                </div>
              </div>
            </div>
            <div v-else class="empty-text">暂无附件</div>

          </el-card>
        </div>

        <div v-if="detailTab === 'performance'">
          <el-card shadow="never" class="detail-block performance-board">
            <div slot="header" class="detail-block-title">履约进度总览</div>
            <div class="performance-overview-grid">
              <div class="performance-metric payment">
                <div class="metric-head">
                  <span>回款 / 付款进度</span>
                  <strong>{{ getProgressText(currentDetail.paymentProgress) }}</strong>
                </div>
                <el-progress :percentage="toPercent(currentDetail.paymentProgress)" :stroke-width="14" status="success"></el-progress>
                <div class="metric-footer">
                  <span>已收/已付：{{ formatMoney(currentDetail.receivedPaid) }}</span>
                  <span>待收/待付：{{ formatMoney(currentDetail.pendingReceivedPaid) }}</span>
                </div>
              </div>

              <div class="performance-metric invoice">
                <div class="metric-head">
                  <span>发票进度</span>
                  <strong>{{ getProgressText(currentDetail.invoicingProgress) }}</strong>
                </div>
                <el-progress :percentage="toPercent(currentDetail.invoicingProgress)" :stroke-width="14" color="#e6a23c"></el-progress>
                <div class="metric-footer">
                  <span>已开/已收：{{ formatMoney(currentDetail.issuedReceived) }}</span>
                  <span>待开/待收：{{ formatMoney(currentDetail.pendingIssuedReceived) }}</span>
                </div>
              </div>
            </div>

            <div class="performance-detail-grid">
              <div class="performance-stat-card">
                <label>履约状态</label>
                <div>{{ getPerformanceStatusLabel(currentDetail.performanceStatus) }}</div>
              </div>
              <div class="performance-stat-card">
                <label>账款状态</label>
                <div>{{ formatAccountStatus(currentDetail.accountStatus) }}</div>
              </div>
              <div class="performance-stat-card">
                <label>发票状态</label>
                <div>{{ formatInvoiceStatus(currentDetail.invoiceStatus) }}</div>
              </div>
              <div class="performance-stat-card">
                <label>合同总额</label>
                <div>{{ formatMoney(currentDetail.totalAmount) }}</div>
              </div>
            </div>

            <div class="performance-timeline">
              <div class="timeline-row">
                <span>审批通过</span>
                <strong>{{ parseTime(currentDetail.approvePassTime, '{y}-{m}-{d}') || '—' }}</strong>
              </div>
              <div class="timeline-row">
                <span>签署日期</span>
                <strong>{{ parseTime(currentDetail.signDate, '{y}-{m}-{d}') || '—' }}</strong>
              </div>
              <div class="timeline-row">
                <span>生效日期</span>
                <strong>{{ parseTime(currentDetail.startDate, '{y}-{m}-{d}') || '—' }}</strong>
              </div>
              <div class="timeline-row">
                <span>到期时间</span>
                <strong>{{ parseTime(currentDetail.endDate, '{y}-{m}-{d}') || '—' }}</strong>
              </div>
              <div class="timeline-row">
                <span>完结说明</span>
                <strong>{{ currentDetail.finishRemark || currentDetail.archiveRemarks || '—' }}</strong>
              </div>
            </div>
          </el-card>
        </div>

        <div v-if="detailTab === 'log'">
          <el-card shadow="never" class="detail-block">
            <div v-if="currentDetail.operateLogs && currentDetail.operateLogs.length > 0">
              <div v-for="(log, index) in currentDetail.operateLogs" :key="index" class="log-item">
                <div class="log-left">
                  <div class="log-avatar">
                    {{ (log.operator && log.operator.slice(0, 1)) || '操' }}
                  </div>
                  <div class="log-main">
                    <div class="log-operator">{{ log.operator }}</div>
                    <div class="log-action">{{ log.action }}</div>
                  </div>
                </div>
                <div class="log-time">{{ log.operateTime || log.time }}</div>
              </div>
            </div>
            <div v-else class="empty-panel small">
              <p>暂无操作记录</p>
            </div>
          </el-card>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDrawerVisible = false">关 闭</el-button>
      </div>
    </el-drawer>

    <!-- ================= 抽屉：审批（美化版）================= -->
    <el-drawer
      :visible.sync="approveDrawer"
      direction="rtl"
      size="680px"
      :with-header="false"
      destroy-on-close
      custom-class="modern-approve-drawer"
    >
      <div class="approve-page" v-if="currentApproveData">
        <div class="approve-hero">
          <div class="approve-hero__left">
            <div class="approve-hero__icon">
              <i class="el-icon-document"></i>
            </div>
            <div class="approve-hero__info">
              <h2>{{ currentApproveData.contractName || currentApproveData.name || '未命名合同' }}</h2>
              <div class="approve-hero__meta">
                <span>合同编号：{{ currentApproveData.contractNumber || '—' }}</span>
                <span>提交人：{{ currentApproveData.submitter || currentApproveData.owner || '—' }}</span>
              </div>
            </div>
          </div>
          <div class="approve-hero__right">
            <el-tag :type="getSignStatusType(currentApproveData.signStatus)" effect="dark" size="medium">
              {{ getSignStatusLabel(currentApproveData.signStatus) }}
            </el-tag>
          </div>
        </div>

        <div class="approve-summary-grid">
          <div class="summary-card">
            <div class="summary-card__label">合同总额</div>
            <div class="summary-card__value money">
              {{ currentApproveData.totalAmount ? '¥ ' + currentApproveData.totalAmount : '无金额' }}
            </div>
          </div>
          <div class="summary-card">
            <div class="summary-card__label">签署方式</div>
            <div class="summary-card__value">
              {{ formatSignMethod(currentApproveData.signMethod) }}
            </div>
          </div>
          <div class="summary-card">
            <div class="summary-card__label">合同分类</div>
            <div class="summary-card__value">
              {{ formatCategory(currentApproveData.category) }}
            </div>
          </div>
          <div class="summary-card">
            <div class="summary-card__label">履约状态</div>
            <div class="summary-card__value">
              {{ getPerformanceStatusLabel(currentApproveData.performanceStatus) || '—' }}
            </div>
          </div>
        </div>

        <div class="approve-section">
          <div class="section-title">基本信息</div>
          <el-descriptions :column="2" border class="modern-descriptions">
            <el-descriptions-item label="合同名称">
              {{ currentApproveData.contractName || currentApproveData.name || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="合同编号">
              {{ currentApproveData.contractNumber || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="我方主体">
              {{ currentApproveData.myPartyName || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="对方主体">
              {{ currentApproveData.otherPartyName || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="开始时间">
              {{ parseTime(currentApproveData.startDate, '{y}-{m}-{d}') || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="到期时间">
              {{ parseTime(currentApproveData.endDate, '{y}-{m}-{d}') || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="归属人">
              {{ currentApproveData.owner || '—' }}
            </el-descriptions-item>
            <el-descriptions-item label="归属部门">
              {{ currentApproveData.department || '—' }}
            </el-descriptions-item>
          </el-descriptions>
        </div>

        <div class="approve-section">
          <div class="section-title">合同说明</div>
          <div class="remark-box">
            {{ currentApproveData.description || '暂无说明' }}
          </div>
        </div>

        <div class="approve-section">
          <div class="section-title">审批流程</div>
          <div class="process-card">
            <el-steps :active="2" finish-status="success" align-center>
              <el-step title="发起申请"></el-step>
              <el-step title="当前审批"></el-step>
              <el-step title="抄送归档"></el-step>
            </el-steps>

            <div class="process-timeline">
              <div class="timeline-item done">
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <div class="timeline-title">发起申请</div>
                  <div class="timeline-desc">
                    {{ currentApproveData.submitter || currentApproveData.owner || '我' }}
                  </div>
                </div>
                <div class="timeline-time">已发起</div>
              </div>

              <div class="timeline-item current">
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <div class="timeline-title">待你审批</div>
                  <div class="timeline-desc">请确认合同信息后进行审批</div>
                </div>
                <div class="timeline-time">进行中</div>
              </div>

              <div class="timeline-item">
                <div class="timeline-dot"></div>
                <div class="timeline-content">
                  <div class="timeline-title">抄送节点</div>
                  <div class="timeline-desc">审批结束后自动通知相关人员</div>
                </div>
                <div class="timeline-time">待处理</div>
              </div>
            </div>
          </div>
        </div>

        <div class="approve-action-bar">
          <el-input
            v-model="approveRemark"
            type="textarea"
            :rows="2"
            placeholder="可填写审批意见（选填）"
            class="approve-remark-input"
          />
          <div class="approve-action-buttons">
            <el-button size="medium" @click="approveDrawer = false">取消</el-button>
            <el-button type="danger" size="medium" plain @click="handleApproveAction('reject')">
              驳回
            </el-button>
            <el-button type="primary" size="medium" @click="handleApproveAction('agree')">
              同意审批
            </el-button>
          </div>
        </div>
      </div>
    </el-drawer>

    <!-- ================= 抽屉：录签署信息（美化版）================= -->
    <el-drawer
      :visible.sync="signDrawerVisible"
      direction="rtl"
      size="560px"
      :with-header="false"
      custom-class="modern-sign-drawer"
    >
      <div class="sign-page">
        <div class="sign-header">
          <div>
            <h2>录入签署信息</h2>
            <p>请完善签署信息，提交后将更新当前合同状态</p>
          </div>
          <el-tag type="info" size="medium">
            {{ currentSignData ? getSignStatusLabel(currentSignData.signStatus) : '签署中' }}
          </el-tag>
        </div>

        <div class="sign-contract-card">
          <div class="sign-contract-card__title">
            <i class="el-icon-document"></i>
            <span>{{ (currentSignData && (currentSignData.contractName || currentSignData.name)) || '未命名合同' }}</span>
          </div>

          <div class="sign-info-list">
            <div class="sign-info-item">
              <label>合同编号</label>
              <span>{{ (currentSignData && currentSignData.contractNumber) || '—' }}</span>
            </div>
            <div class="sign-info-item">
              <label>我方主体</label>
              <span>{{ (currentSignData && (currentSignData.myPartyName || currentSignData.ourPartyName)) || '—' }}</span>
            </div>
            <div class="sign-info-item">
              <label>对方主体</label>
              <span>{{ (currentSignData && currentSignData.otherPartyName) || '—' }}</span>
            </div>
            <div class="sign-info-item">
              <label>合同总额</label>
              <span>{{ (currentSignData && currentSignData.totalAmount) ? ('¥ ' + currentSignData.totalAmount) : '无金额' }}</span>
            </div>
          </div>
        </div>

        <div class="sign-form-card">
          <div class="section-title">签署信息</div>
          <el-form ref="signFormRef" :model="signForm" :rules="signRules" label-width="90px" class="modern-sign-form">
            <el-form-item label="印章类型" prop="sealType">
              <el-select v-model="signForm.sealType" placeholder="请选择印章类型" style="width:100%">
                <el-option label="公章" value="official" />
                <el-option label="合同章" value="contract" />
              </el-select>
            </el-form-item>

            <el-form-item label="签署日期" prop="signDate">
              <el-date-picker
                v-model="signForm.signDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择签署日期"
                style="width:100%"
              />
            </el-form-item>

            <el-form-item label="期限类型" prop="termType">
              <el-radio-group v-model="signForm.termType">
                <el-radio label="1">固定期限</el-radio>
                <el-radio label="2">无固定期限</el-radio>
              </el-radio-group>
            </el-form-item>

            <el-form-item label="签署备注" prop="remark">
              <el-input
                v-model="signForm.remark"
                type="textarea"
                :rows="4"
                maxlength="200"
                show-word-limit
                placeholder="请输入签署备注"
              />
            </el-form-item>

            <div class="archive-switch-box">
              <div>
                <div class="archive-switch-box__title">签署后自动归档</div>
                <div class="archive-switch-box__desc">开启后，签署完成将进入归档流程</div>
              </div>
              <el-switch v-model="signForm.autoArchive" />
            </div>
          </el-form>
        </div>

        <div class="sign-footer">
          <el-button @click="signDrawerVisible = false">取消</el-button>
          <el-button type="primary" @click="submitSign">提交签署信息</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 录账款抽屉 -->
    <el-drawer
      :visible.sync="recordAccountDrawerVisible"
      direction="rtl"
      size="560px"
      :with-header="false"
      custom-class="modern-record-account-drawer"
    >
      <div class="record-account-page">
        <div class="record-account-header">
          <div>
            <h2>录账款</h2>
            <p>填写当前合同的账款信息，提交后自动生成账款记录</p>
          </div>
          <el-tag :type="recordAccountForm.amountType === 'expense' ? 'danger' : 'success'" size="medium">
            {{ recordAccountForm.amountType === 'expense' ? '付款' : '收款' }}
          </el-tag>
        </div>

        <div class="record-account-contract-card">
          <div class="record-account-contract-card__title">
            <i class="el-icon-document"></i>
            <span>{{ (currentRecordAccountData && (currentRecordAccountData.contractName || currentRecordAccountData.name)) || '未命名合同' }}</span>
          </div>

          <div class="record-account-info-list">
            <div class="record-account-info-item">
              <label>合同编号</label>
              <span>{{ (currentRecordAccountData && currentRecordAccountData.contractNumber) || '—' }}</span>
            </div>
            <div class="record-account-info-item">
              <label>金额类型</label>
              <span>{{ formatAmountType((currentRecordAccountData && currentRecordAccountData.amountType) || recordAccountForm.amountType) }}</span>
            </div>
            <div class="record-account-info-item">
              <label>我方主体</label>
              <span>{{ (currentRecordAccountData && (currentRecordAccountData.myPartyName || currentRecordAccountData.ourPartyName)) || '—' }}</span>
            </div>
            <div class="record-account-info-item">
              <label>对方主体</label>
              <span>{{ (currentRecordAccountData && (currentRecordAccountData.otherPartyName || currentRecordAccountData.opponentUnit)) || '—' }}</span>
            </div>
          </div>
        </div>

        <div class="record-account-form-card">
          <div class="section-title">账款信息</div>
          <el-form ref="recordAccountFormRef" :model="recordAccountForm" :rules="recordAccountRules" label-width="90px" class="modern-record-account-form">
            <el-form-item label="账款名称" prop="accountName">
              <el-input v-model="recordAccountForm.accountName" placeholder="请输入账款名称" />
            </el-form-item>

            <el-form-item label="账款日期" prop="accountDate">
              <el-date-picker
                v-model="recordAccountForm.accountDate"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择账款日期"
                style="width:100%"
              />
            </el-form-item>

            <el-form-item label="账款金额" prop="amount">
              <el-input v-model="recordAccountForm.amount" placeholder="请输入账款金额">
                <template slot="prepend">¥</template>
              </el-input>
            </el-form-item>

            <el-form-item label="金额类型" prop="amountType">
              <el-select v-model="recordAccountForm.amountType" style="width:100%">
                <el-option label="收入" value="income" />
                <el-option label="支出" value="expense" />
              </el-select>
            </el-form-item>

            <el-form-item label="单据号" prop="orderNo">
              <el-input v-model="recordAccountForm.orderNo" placeholder="请输入单据号" />
            </el-form-item>

            <el-form-item label="状态" prop="status">
              <el-select v-model="recordAccountForm.status" style="width:100%">
                <el-option label="待处理" value="pending" />
                <el-option label="部分完成" value="partial" />
                <el-option label="已完成" value="done" />
              </el-select>
            </el-form-item>

            <el-form-item label="备注" prop="remark">
              <el-input
                v-model="recordAccountForm.remark"
                type="textarea"
                :rows="4"
                maxlength="300"
                show-word-limit
                placeholder="请输入备注"
              />
            </el-form-item>
          </el-form>
        </div>

        <div class="record-account-footer">
          <el-button @click="recordAccountDrawerVisible = false">取消</el-button>
          <el-button type="primary" :loading="recordAccountSubmitting" @click="submitRecordAccount">提交账款</el-button>
        </div>
      </div>
    </el-drawer>

    <!-- 6. 列设置弹窗 -->
    <el-dialog title="显示列设置" :visible.sync="columnDialogVisible" width="400px" append-to-body>
      <div class="column-settings">
        <el-checkbox-group v-model="selectedColumns">
          <div class="column-item" v-for="(item, index) in visibleColumnOptions" :key="index">
            <el-checkbox :label="item.key">{{ item.label }}</el-checkbox>
          </div>
        </el-checkbox-group>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="columnDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveColumnSettings">确定</el-button>
      </div>
    </el-dialog>

    <!-- 7. 新建分类弹窗 -->
    <el-dialog title="新建分类" :visible.sync="categoryDialogVisible" width="400px" append-to-body>
      <el-form ref="categoryForm" :model="categoryForm" :rules="categoryRules" label-width="80px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" autofocus />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="cancelCategory">取 消</el-button>
        <el-button type="primary" @click="submitCategoryForm">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 转交弹窗 -->
    <el-dialog
      title="转交合同"
      :visible.sync="transferDialogVisible"
      width="500px"
      append-to-body
      @close="resetTransferForm"
    >
      <el-form ref="transferForm" :model="transferForm" :rules="transferRules" label-width="80px">
        <el-form-item label="转交员工" prop="userId">
          <el-select
            v-model="transferForm.userId"
            placeholder="请选择"
            filterable
            @change="handleUserChange"
            style="width: 100%;"
          >
            <el-option v-for="user in userList" :key="user.userId || user.id" :label="user.nickName || user.userName || user.name" :value="user.userId || user.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="transferDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitTransfer">确 定</el-button>
      </div>
    </el-dialog>

  <!-- 归档抽屉 -->
<el-drawer
  title="合同归档"
  :visible.sync="open"
  direction="rtl"
  size="560px"
  :before-close="handleCloseArchiveDrawer"
  custom-class="archive-drawer"
>
  <div class="archive-drawer-shell">
    <div class="archive-drawer-header-card">
      <div class="archive-drawer-header-card__icon"><i class="el-icon-folder-checked"></i></div>
      <div class="archive-drawer-header-card__content">
        <div class="archive-drawer-header-card__title">归档信息填写</div>
        <div class="archive-drawer-header-card__desc">请补充盖章文件、期限类型和归档备注，确认后完成合同归档。</div>
      </div>
    </div>

    <div class="archive-drawer-content">
      <el-form ref="form" :model="formData" :rules="rules" label-width="100px" class="archive-form">
        <el-form-item label="盖章文件" prop="file">
          <div class="upload-panel">
            <el-upload
              ref="upload"
              :auto-upload="false"
              :show-file-list="false"
              :on-change="handleFileChange"
              accept=".pdf,.doc,.docx"
            >
              <el-button slot="trigger" size="small" type="primary" icon="el-icon-upload2">选择文件</el-button>
            </el-upload>
            <div class="upload-file-name" v-if="fileName">{{ fileName }}</div>
            <div class="upload-file-placeholder" v-else>暂未选择文件</div>
            <div class="upload-tips">支持 PDF、Word 格式，文件大小不超过 50MB</div>
          </div>
        </el-form-item>

        <el-form-item label="期限类型" prop="periodType">
          <el-select v-model="formData.periodType" placeholder="请选择" style="width: 100%;">
            <el-option label="无固定期限" value="无固定期限" />
            <el-option label="固定期限（如3年）" value="固定期限" />
          </el-select>
        </el-form-item>

        <el-form-item label="所在分类" prop="category">
          <el-input v-model="formData.category" placeholder="默认为财务" />
        </el-form-item>

        <el-form-item label="归档编号">
          <el-input v-model="formData.archiveNumber" placeholder="可不填" />
        </el-form-item>

        <el-form-item label="备注">
          <el-input
            v-model="formData.remark"
            type="textarea"
            :rows="4"
            placeholder="请输入备注信息"
          />
        </el-form-item>
      </el-form>
    </div>

    <div class="archive-drawer-footer">
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" icon="el-icon-check" @click="submitForm">确定归档</el-button>
    </div>
  </div>
</el-drawer>
  </div>
</template>

<script>
import {
  listContractContent,
  listContractContent2,
  getContractContent,
  updateContractContent,
  delContractContent,
  importContractBatch,
  approveContract,
  getSealApplyDetail,
  updateSealApply,
  delContract,
  signContract,
  archiveContract,
  finishContract,
  listContractOperateLogs,
  uploadContractDetailFiles
} from "@/api/contract/contract";
import { addAccount } from "@/api/account/account";
import { listUser } from "@/api/system/user";
import ContractDialog from "@/components/Dialog/dialog.vue";

export default {
  name: "ContractManagement",
  components: { ContractDialog },
  data() {
    return {
      navActiveTab: "mine",
      detailTab: "detail",
      navItems: [
        { key: "mine", label: "我的", desc: "我负责的合同", icon: "el-icon-user", count: 0 },
        { key: "involved", label: "我参与的", desc: "我参与协作的合同", icon: "el-icon-cooperation", count: 0 },
        { key: "dept", label: "我部门的", desc: "部门内全部合同", icon: "el-icon-office-building", count: 0 },
        { key: "pending", label: "待分配的", desc: "待领取和分配", icon: "el-icon-bell", count: 0 },
        { key: "all", label: "所有的", desc: "系统全部数据", icon: "el-icon-document-copy", count: 0 }
      ],
      categoryTree: [],
      defaultProps: { children: "children", label: "label" },
      categoryDialogVisible: false,
      categoryForm: { name: "" },
      categoryRules: { name: [{ required: true, message: "分类名称不能为空", trigger: "blur" }] },

      documentType: "approval",
      listActiveTab: "all_list",
      loading: true,
      detailLoading: false,
      ids: [],
      single: true,
      multiple: true,
      showSearch: false,
      total: 0,
      contractList: [],
      dateRangeCreateTime: [],
      approveRemark: "",

      tabSignStatusMap: {
        approving: "1",
        signing: "2",
        archiving: "3",
        archived: "5",
        expiring: null,
        all_list: undefined
      },

      queryParams: {},

      detailDrawerVisible: false,
      currentDetail: {},

      approveDrawer: false,
      currentApproveData: null,

      editDrawerVisible: false,
      editForm: {},
      editRules: {},

      signDrawerVisible: false,
      currentSignData: null,
      signForm: {
        contractId: null,
        sealType: "",
        signDate: "",
        termType: "",
        remark: "",
        autoArchive: false
      },
      signRules: {
        sealType: [{ required: true, message: "请选择印章类型", trigger: "change" }],
        signDate: [{ required: true, message: "请选择签署日期", trigger: "change" }],
        termType: [{ required: true, message: "请选择期限类型", trigger: "change" }]
      },

      recordAccountDrawerVisible: false,
      currentRecordAccountData: null,
      recordAccountSubmitting: false,
      recordAccountForm: {
        contractId: null,
        accountName: "",
        accountDate: "",
        amount: "",
        amountType: "income",
        orderNo: "",
        ourParty: "",
        otherParty: "",
        relatedContractName: "",
        relatedContractNumber: "",
        remark: "",
        status: "pending"
      },
      recordAccountRules: {
        accountName: [{ required: true, message: "请输入账款名称", trigger: "blur" }],
        accountDate: [{ required: true, message: "请选择账款日期", trigger: "change" }],
        amount: [{ required: true, message: "请输入账款金额", trigger: "blur" }],
        amountType: [{ required: true, message: "请选择金额类型", trigger: "change" }]
      },

      columnDialogVisible: false,
      selectedColumns: [],
      allColumnOptions: {
        approval: [
          { key: "name", label: "名称" },
          { key: "owner", label: "归属人" },
          { key: "contractNumber", label: "编号" },
          { key: "signStatus", label: "签署状态" },
          { key: "performanceStatus", label: "履约状态" },
          { key: "amountType", label: "金额类型" },
          { key: "totalAmount", label: "合同总额" },
          { key: "signDate", label: "签署日期" },
          { key: "termType", label: "期限类型" },
          { key: "startDate", label: "开始时间" },
          { key: "endDate", label: "到期时间" },
          { key: "category", label: "合同分类" },
          { key: "description", label: "合同说明" },
          { key: "archiver", label: "归档人" },
          { key: "cooperators", label: "协同人" },
          { key: "myPartyName", label: "我方主体名称" },
          { key: "otherPartyName", label: "对方主体名称" },
          { key: "applyTime", label: "申请时间" },
          { key: "submitter", label: "合同提交人" },
          { key: "signMethod", label: "签署方式" },
          { key: "sealType", label: "印章类型" },
          { key: "paymentProgress", label: "收款/付款进度" },
          { key: "receivedPaid", label: "已收/已付" },
          { key: "pendingReceivedPaid", label: "待收/待付" },
          { key: "invoicingProgress", label: "开票/收票进度" },
          { key: "issuedReceived", label: "已开/已收" },
          { key: "pendingIssuedReceived", label: "待开/待收" },
          { key: "signRemarks", label: "签署备注" },
          { key: "archiveRemarks", label: "归档备注" },
          { key: "proxySubmitter", label: "代提交人" },
          { key: "department", label: "归属部门" },
          { key: "archiveFile", label: "归档文件" },
          { key: "archiveTime", label: "归档时间" },
          { key: "attachments", label: "附属文件" },
          { key: "accountStatus", label: "账款状态" },
          { key: "invoiceStatus", label: "发票状态" },
          { key: "approvalNumber", label: "合同审批编号" },
          { key: "project", label: "项目" },
          { key: "approvePassTime", label: "审批通过时间" },
          { key: "borrowStatus", label: "借阅状态" },
          { key: "archiveId", label: "归档编号" },
          { key: "archiveApprovalNumber", label: "归档审批编号" },
          { key: "otherPaymentAmount", label: "其他付款金额" },
          { key: "otherReceiptAmount", label: "其他收款金额" },
          { key: "totalAmountCalculationMethod", label: "合同总额计算方式" }
        ],
        seal: [
          { key: "name", label: "审批单名称" },
          { key: "contractNumber", label: "编号" },
          { key: "signStatus", label: "签署状态" },
          { key: "performanceStatus", label: "履约状态" },
          { key: "totalAmount", label: "合同总额" },
          { key: "opponentUnit", label: "对方单位" },
          { key: "owner", label: "归属人" },
          { key: "department", label: "用印部门" },
          { key: "handler", label: "经办人" },
          { key: "fileCategory", label: "文件类别" }
        ]
      },
      defaultColumns: {
        approval: [
          "name",
          "contractNumber",
          "signStatus",
          "performanceStatus",
          "totalAmount",
          "startDate",
          "endDate",
          "otherPartyName"
        ],
        seal: ["name", "contractNumber", "signStatus", "performanceStatus", "totalAmount", "owner"]
      },

      transferDialogVisible: false,
      transferForm: {
        userId: "",
        userName: ""
      },
      transferRules: {
        userId: [{ required: true, message: "请选择转交员工", trigger: "change" }]
      },
      userList: [],
      currentRow: null,

      open: false,
      formData: {
        file: null,
        periodType: null,
        category: null,
        archiveNumber: null,
        remark: ""
      },
      rules: {
        file: [{ required: true, message: "请上传盖章文件", trigger: "change" }],
        periodType: [{ required: true, message: "请选择期限类型", trigger: "change" }],
        category: [{ required: true, message: "请输入所在分类", trigger: "blur" }]
      },
      fileName: "",

      finishDrawerVisible: false,
      finishForm: {
        contractId: null,
        result: "",
        finalStatus: "8"
      },

      detailUploadLoading: false,
      detailUploadForm: {
        contentFiles: [],
        attachmentFiles: []
      }
    };
  },
  computed: {
    isApproval() {
      return this.documentType === "approval";
    },
    visibleColumnOptions() {
      return this.allColumnOptions[this.documentType] || [];
    },
    columnSettings() {
      const settings = {};
      this.visibleColumnOptions.forEach(col => {
        settings[col.key] = this.selectedColumns.includes(col.key);
      });
      return settings;
    },
    parsedContentFiles() {
      return this.normalizeFileList(this.currentDetail.content || this.currentDetail.file);
    },
    parsedAttachments() {
      return this.normalizeFileList(this.currentDetail.attachments);
    },
    editParsedContentFiles() {
      return this.normalizeFileList(this.editForm.content || this.editForm.file);
    },
    editParsedAttachments() {
      return this.normalizeFileList(this.editForm.attachments);
    }
  },
  created() {
    this.queryParams = this.getDefaultQueryParams();
    this.applyRouteQuery();
    this.initColumnSettings();
    if (this.navActiveTab !== "all") {
      this.getList();
    }
  },
  watch: {
    "$route.query": {
      handler() {
        this.applyRouteQuery();
      },
      deep: true
    }
  },
  methods: {
    applyRouteQuery() {
      const query = (this.$route && this.$route.query) || {};
      const nextNav = query.nav;
      const nextTab = query.tab;
      const nextDocumentType = query.documentType;

      if (nextNav && this.navItems.some(item => item.key === nextNav)) {
        this.navActiveTab = nextNav;
      }
      if (nextDocumentType && ["approval", "seal"].includes(nextDocumentType)) {
        this.documentType = nextDocumentType;
      }
      if (nextTab && Object.prototype.hasOwnProperty.call(this.tabSignStatusMap, nextTab)) {
        this.listActiveTab = nextTab;
      }
      if (!this.queryParams || Object.keys(this.queryParams).length === 0) {
        this.queryParams = this.getDefaultQueryParams();
      }
      this.queryParams.signStatus = this.tabSignStatusMap[this.listActiveTab];
    },
    getDefaultQueryParams() {
      return {
        pageNum: 1,
        pageSize: 10,
        contractName: undefined,
        name: undefined,
        contractNumber: undefined,
        signStatus: undefined,
        performanceStatus: undefined,
        owner: undefined,
        otherPartyName: undefined,
        opponentUnit: undefined,
        department: undefined,
        sealType: undefined,
        amountType: undefined,
        signMethod: undefined,
        invoiceStatus: undefined,
        accountStatus: undefined,
        project: undefined,
        handler: undefined,
        fileCategory: undefined,
        createTime: undefined,
        ownerId: undefined,
        involved: undefined,
        deptId: undefined,
        pendingAssign: undefined,
        signDate: undefined,
        termType: undefined,
        archiveApprovalNumber: undefined,
        attachments: undefined,
        totalAmountCalculationMethod: undefined,
        startDate: undefined,
        endDate: undefined,
        category: undefined,
        description: undefined,
        archiver: undefined,
        cooperators: undefined,
        myPartyName: undefined,
        applyTime: undefined,
        submitter: undefined,
        proxySubmitter: undefined,
        paymentProgress: undefined,
        receivedPaid: undefined,
        pendingReceivedPaid: undefined,
        invoicingProgress: undefined,
        issuedReceived: undefined,
        pendingIssuedReceived: undefined,
        signRemarks: undefined,
        archiveRemarks: undefined,
        archiveFile: undefined,
        archiveTime: undefined,
        approvalNumber: undefined,
        approvePassTime: undefined,
        borrowStatus: undefined,
        archiveId: undefined,
        otherPaymentAmount: undefined,
        otherReceiptAmount: undefined
      };
    },

    onDocumentTypeChange() {
      this.resetQuery();
      this.initColumnSettings();
    },

    handleNavClick(item) {
      this.navActiveTab = item.key;
      if (item.key !== "all") {
        this.handleQuery();
      }
    },

    handleListTabClick(tab) {
      this.listActiveTab = tab.name;
      const status = this.tabSignStatusMap[tab.name];
      this.queryParams.signStatus = status;
      this.handleQuery();
    },

    toggleSearch() {
      this.showSearch = !this.showSearch;
    },

    getList() {
      this.loading = true;

      const cleanParams = { ...this.queryParams };
      Object.keys(cleanParams).forEach(key => {
        if (
          cleanParams[key] === undefined ||
          cleanParams[key] === "" ||
          cleanParams[key] === null
        ) {
          delete cleanParams[key];
        }
      });

      const user = (this.$store && this.$store.getters && this.$store.getters.user) || {};

      if (this.navActiveTab === "mine") {
        cleanParams.ownerId = user.userId;
      } else if (this.navActiveTab === "involved") {
        cleanParams.involved = "true";
      } else if (this.navActiveTab === "dept") {
        cleanParams.deptId = user.deptId;
      } else if (this.navActiveTab === "pending") {
        cleanParams.pendingAssign = "true";
      }

      if (this.dateRangeCreateTime && this.dateRangeCreateTime.length === 2) {
        cleanParams.createTimeStart = this.dateRangeCreateTime[0];
        cleanParams.createTimeEnd = this.dateRangeCreateTime[1];
      }

      const requestFunc = this.isApproval ? listContractContent : listContractContent2;
      requestFunc(cleanParams)
        .then(response => {
          const backendRows = response.rows || [];
          let list = backendRows;
          let total = Number(response.total) || 0;

          if (this.listActiveTab === "expiring") {
            const now = new Date();

            list = backendRows.filter(item => {
              if (!item.endDate) return false;
              const end = new Date(item.endDate);
              const diff = (end.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
              return diff >= 0 && diff <= 7;
            });
            // 当前“即将到期”仍是前端本地过滤，total 先按过滤后当前结果显示
            total = list.length;
          }

          this.contractList = list;
          this.total = total;
          this.updateNavCountsByList(this.contractList);
          this.loading = false;
        })
        .catch(() => {
          this.loading = false;
        });
    },

    updateNavCountsByList(list) {
      if (!Array.isArray(list)) return;
      this.navItems = this.navItems.map(item => {
        if (item.key === this.navActiveTab) {
          return { ...item, count: list.length || 0 };
        }
        return item;
      });
    },

    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    resetQuery() {
      if (this.$refs.queryForm) {
        this.$refs.queryForm.resetFields();
      }
      this.dateRangeCreateTime = [];
      this.queryParams = this.getDefaultQueryParams();
      this.listActiveTab = "all_list";
      this.queryParams.signStatus = undefined;
      this.handleQuery();
    },

    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id || item.contractId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },

    handleExportCommand(command) {
      if (command === "selected") {
        if (this.ids.length === 0) {
          this.$message.warning("请先选择要导出的数据");
          return;
        }
        this.$message.info(`导出选中项（共 ${this.ids.length} 条）`);
      } else if (command === "all") {
        this.$message.info("导出全部记录");
      }
    },

    handleBatchDownload() {
      if (!this.ids || this.ids.length === 0) {
        this.$message.warning("请先选择要下载的合同");
        return;
      }
      this.$message.info(`正在批量下载 ${this.ids.length} 份合同...`);
    },

    handleBatchArchive() {
      if (!this.ids || this.ids.length === 0) {
        this.$message.warning("请先选择要归档的合同");
        return;
      }

      this.$confirm(`是否确认批量归档选中的 ${this.ids.length} 份合同？`, "批量归档", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          this.$message.success("批量归档成功");
          this.getList();
          if (this.detailDrawerVisible) this.detailDrawerVisible = false;
        })
        .catch(() => {});
    },

    handleCloseDrawer() {
      this.drawerVisible = false;
    },

    handleUpdate(row) {
      const id = (row && (row.id || row.contractId)) || (this.ids.length > 0 ? this.ids[0] : null);
      if (!id) {
        this.$message.warning("请先选择一条数据");
        return;
      }
      const api = this.isApproval ? getContractContent : getSealApplyDetail;
      api(id)
        .then(res => {
          this.editForm = res.data || {};
          this.editDrawerVisible = true;
        })
        .catch(() => {
          this.$message.error("获取详情失败");
        });
    },

    handleCloseEditDrawer(done) {
      this.$confirm("确认关闭？未保存的内容将丢失")
        .then(() => {
          this.detailUploadForm = { contentFiles: [], attachmentFiles: [] };
          done();
        })
        .catch(() => {});
    },

    closeEditDrawer() {
      this.handleCloseEditDrawer(() => {
        this.editDrawerVisible = false;
      });
    },

    submitEditForm() {
      this.$refs.editForm.validate(async valid => {
        if (!valid) return;
        try {
          const api = this.isApproval ? updateContractContent : updateSealApply;
          await api(this.editForm);
          if (this.isApproval && (this.detailUploadForm.contentFiles.length || this.detailUploadForm.attachmentFiles.length)) {
            const contractId = this.editForm.id || this.editForm.contractId;
            const formData = new FormData();
            formData.append('contractId', contractId);
            this.detailUploadForm.contentFiles.forEach(file => formData.append('contentFiles', file.raw));
            this.detailUploadForm.attachmentFiles.forEach(file => formData.append('attachmentFiles', file.raw));
            this.detailUploadLoading = true;
            await uploadContractDetailFiles(formData);
          }
          this.$message.success("修改成功");
          this.editDrawerVisible = false;
          this.detailUploadForm = { contentFiles: [], attachmentFiles: [] };
          this.getList();
        } catch (e) {
          this.$message.error("修改失败：" + ((e && (e.msg || e.message)) || "请稍后重试"));
        } finally {
          this.detailUploadLoading = false;
        }
      });
    },

    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids;
      if (!ids || (Array.isArray(ids) && ids.length === 0)) {
        this.$message.warning("请选择要删除的数据");
        return;
      }

      const idStr = Array.isArray(ids) ? ids.join(",") : ids;
      this.$confirm("是否确认删除？", "警告", { type: "warning" })
        .then(() => {
          const api = this.isApproval ? delContractContent : delContract;
          return api(idStr);
        })
        .then(() => {
          this.$message.success("删除成功");
          this.getList();
          if (this.detailDrawerVisible) this.detailDrawerVisible = false;
        })
        .catch(() => {});
    },

    async handleDetail(row) {
      this.detailLoading = true;
      this.detailTab = "detail";
      this.detailDrawerVisible = true;
      try {
        const id = row.id || row.contractId;
        const detailResp = this.isApproval ? await getContractContent(id) : await getSealApplyDetail(id);
        const detailData = (detailResp && detailResp.data) || row || {};
        this.currentDetail = { ...row, ...detailData };
        const logResp = await listContractOperateLogs(id).catch(() => ({ data: [] }));
        this.currentDetail.operateLogs = (logResp && logResp.data) || [];
      } finally {
        this.detailLoading = false;
      }
    },

    handleCloseDetailDrawer() {
      this.detailDrawerVisible = false;
      this.currentDetail = {};
      this.detailTab = "detail";
      this.detailUploadForm = { contentFiles: [], attachmentFiles: [] };
    },

    handleEditFromDetail(row) {
      this.detailDrawerVisible = false;
      this.$nextTick(() => {
        this.handleUpdate(row);
      });
    },

    handleApprove(row) {
      this.currentApproveData = {
        ...row
      };
      this.approveRemark = "";
      this.approveDrawer = true;
    },

    handleArchive(row) {
      this.currentDetail = { ...row };
      this.open = true;
      this.formData = {
        file: null,
        periodType: null,
        category: row.category || null,
        archiveNumber: row.archiveId || null,
        remark: ""
      };
      this.fileName = "";
      this.$nextTick(() => {
        if (this.$refs.form) {
          this.$refs.form.clearValidate();
        }
      });
    },

    resetArchiveForm() {
      this.formData = {
        file: null,
        periodType: null,
        category: null,
        archiveNumber: null,
        remark: ""
      };
      this.fileName = "";
      if (this.$refs.upload) {
        this.$refs.upload.clearFiles();
      }
      if (this.$refs.form) {
        this.$refs.form.resetFields();
      }
    },

    handleTransfer(row) {
      const sourceRow = row || this.getSelectedSingleRow();
      if (!sourceRow) {
        this.$message.warning("请先选择一条需要转交的数据");
        return;
      }
      this.currentRow = { ...sourceRow };
      this.transferForm = { userId: "", userName: "" };
      this.transferDialogVisible = true;
      this.loadUserList();
    },

    getSelectedSingleRow() {
      if (this.ids.length !== 1) return null;
      return this.contractList.find(item => (item.id || item.contractId) === this.ids[0]) || null;
    },

    async loadUserList() {
      try {
        const res = await listUser({ status: "0", pageNum: 1, pageSize: 1000 });
        this.userList = res.rows || [];
      } catch (err) {
        console.error("获取用户列表失败:", err);
        this.$message.error("加载人员失败");
      }
    },

    handleUserChange(value) {
      const user = this.userList.find(u => (u.userId || u.id) === value);
      this.transferForm.userName = user ? (user.nickName || user.userName || user.name) : "";
    },

    submitTransfer() {
      this.$refs.transferForm.validate(valid => {
        if (valid) {
          this.$confirm(
            `确定将合同【${this.currentRow.contractName || this.currentRow.name}】转交给【${this.transferForm.userName}】吗？`,
            "提示",
            {
              confirmButtonText: "确定",
              cancelButtonText: "取消",
              type: "warning"
            }
          )
            .then(() => {
              return updateContractContent({
                id: this.currentRow.id || this.currentRow.contractId,
                owner: this.transferForm.userName
              });
            })
            .then(() => {
              this.$message.success("转交成功");
              this.transferDialogVisible = false;
              this.getList();
            })
            .catch(err => {
              if (err !== "cancel") {
                this.$message.error("转交失败");
              }
            });
        }
      });
    },

    resetTransferForm() {
      this.$refs.transferForm && this.$refs.transferForm.resetFields();
      this.transferForm = { userId: "", userName: "" };
    },

    handleSign(row) {
      this.currentSignData = { ...row };
      this.signForm = {
        contractId: row.id || row.contractId,
        sealType: row.sealType || "",
        signDate: row.signDate || "",
        termType: row.termType || "",
        remark: row.signRemarks || "",
        autoArchive: false
      };
      this.signDrawerVisible = true;
    },

    submitSign() {
      this.$refs.signFormRef.validate(async valid => {
        if (!valid) return;
        try {
          await signContract(this.signForm);
          this.$message.success("签署成功");
          this.signDrawerVisible = false;
          this.getList();
        } catch (err) {
          this.$message.error("签署失败：" + ((err && (err.msg || err.message)) || "请稍后重试"));
        }
      });
    },

    handleRevoke(row) {
      this.$confirm(`确定要撤销合同【${row.contractName || row.name}】吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$message.success("撤销成功");
      });
    },

    handleVoid(row) {
      this.$confirm(`确定要作废合同【${row.contractName || row.name}】吗？`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "error"
      }).then(() => {
        this.$message.success("作废成功");
      });
    },

    handleCommand(command, row) {
      switch (command) {
        case "edit":
          this.handleUpdate(row);
          break;
        case "transfer":
          this.handleTransfer(row);
          break;
        case "revoke":
          this.handleRevoke(row);
          break;
        case "void":
          this.handleVoid(row);
          break;
        case "archive":
          this.handleArchive(row);
          break;
        case "recordAccount":
          this.handleRecordAccount(row);
          break;
        case "delete":
          this.handleDelete(row);
          break;
        default:
          break;
      }
    },

    showRecordAccountAction(row) {
      if (!row) return false;
      const amountType = row.amountType;
      const signStatus = String(row.signStatus || '');
      const performanceStatus = String(row.performanceStatus || '');
      if (performanceStatus === '8') return false;
      return amountType !== '无金额' && amountType !== 'none' && signStatus !== '1';
    },

    handleRecordAccount(row) {
      const contractId = row && (row.id || row.contractId);
      if (!row || !contractId) {
        this.$message.warning('缺少合同信息，无法录账款');
        return;
      }
      const amountType = row.amountType === '收入'
        ? 'income'
        : row.amountType === '支出'
          ? 'expense'
          : (row.amountType || 'income');
      const contractName = row.contractName || row.name || '';
      this.currentRecordAccountData = { ...row, id: contractId };
      this.recordAccountForm = {
        contractId,
        accountName: `${contractName}${amountType === 'expense' ? '-付款账款' : '-收款账款'}`,
        accountDate: '',
        amount: '',
        amountType,
        orderNo: '',
        ourParty: row.myPartyName || row.ourPartyName || '',
        otherParty: row.otherPartyName || row.opponentUnit || '',
        relatedContractName: contractName,
        relatedContractNumber: row.contractNumber || '',
        remark: '',
        status: 'pending'
      };
      this.recordAccountDrawerVisible = true;
      this.$nextTick(() => {
        if (this.$refs.recordAccountFormRef) {
          this.$refs.recordAccountFormRef.clearValidate();
        }
      });
    },

    submitRecordAccount() {
      this.$refs.recordAccountFormRef.validate(valid => {
        if (!valid) return;
        this.recordAccountSubmitting = true;
        addAccount(this.recordAccountForm)
          .then(() => {
            this.$message.success('账款新增成功');
            this.recordAccountDrawerVisible = false;
            this.getList();
          })
          .catch(err => {
            this.$message.error('录账款失败：' + ((err && (err.msg || err.message)) || '请稍后重试'));
          })
          .finally(() => {
            this.recordAccountSubmitting = false;
          });
      });
    },

    initColumnSettings() {
      const key = `contractTableColumns_${this.documentType}`;
      const saved = localStorage.getItem(key);
      if (saved) {
        this.selectedColumns = JSON.parse(saved);
      } else {
        this.selectedColumns = [...this.defaultColumns[this.documentType]];
      }
    },

    handleColumnSetting() {
      this.columnDialogVisible = true;
    },

    saveColumnSettings() {
      const key = `contractTableColumns_${this.documentType}`;
      localStorage.setItem(key, JSON.stringify(this.selectedColumns));
      this.columnDialogVisible = false;
      this.$message.success("列设置已保存");
    },

    handleAddCategory() {
      this.categoryForm = { name: "" };
      this.categoryDialogVisible = true;
      this.$nextTick(() => {
        if (this.$refs.categoryForm) this.$refs.categoryForm.clearValidate();
      });
    },

    handleImport() {
      const input = document.createElement("input");
      input.type = "file";
      input.accept = ".xlsx, .xls";
      input.onchange = async e => {
        const file = e.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append("file", file);
        try {
          await importContractBatch(formData);
          this.$message.success("导入成功！");
          this.getList();
        } catch (error) {
          this.$message.error("导入失败：" + ((error && error.msg) || "请检查文件格式"));
        }
      };
      input.click();
    },

    handleDownloadTemplate() {
      const url = process.env.VUE_APP_BASE_API + "/contract/contractContent/downloadTemplate";
      window.open(url, "_blank");
    },

    submitCategoryForm() {
      this.$refs.categoryForm.validate(valid => {
        if (valid) {
          this.$message.success(`分类 "${this.categoryForm.name}" 创建成功`);
          this.categoryDialogVisible = false;
        }
      });
    },

    cancelCategory() {
      this.categoryDialogVisible = false;
      if (this.$refs.categoryForm) this.$refs.categoryForm.resetFields();
    },

    isImageFile(path) {
      if (!path) return false;
      const ext = path.split(".").pop().toLowerCase();
      return ["png", "jpg", "jpeg", "gif", "bmp", "webp"].includes(ext);
    },

    normalizeFileList(value) {
      if (!value) return [];
      let list = value;
      if (typeof value === 'string') {
        try {
          list = JSON.parse(value);
        } catch (e) {
          list = value.includes(',') ? value.split(',') : [value];
        }
      }
      if (!Array.isArray(list)) {
        list = [list];
      }
      return list.filter(Boolean).map(item => {
        const raw = typeof item === 'string' ? item : (item.url || item.path || item.name || '');
        const url = this.getFileUrl(raw);
        return {
          name: this.getFileName(raw),
          url,
          raw
        };
      });
    },

    getFileUrl(relativePath) {
      if (!relativePath) return '';
      if (/^https?:\/\//i.test(relativePath)) return relativePath;
      return process.env.VUE_APP_BASE_API + "/profile" + relativePath;
    },

    getFileName(path) {
      if (!path) return "";
      const cleanPath = typeof path === 'string' ? path : (path.raw || path.url || path.name || '');
      return cleanPath.substring(cleanPath.lastIndexOf("/") + 1);
    },

    downloadFile(file) {
      const relativePath = typeof file === 'string' ? file : (file.raw || file.url || file.path);
      const url = this.getFileUrl(relativePath);
      const link = document.createElement("a");
      link.href = url;
      link.download = this.getFileName(relativePath);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },

    previewImage(src) {
      window.open(src, "_blank");
    },

    viewApproval() {
      if (this.currentDetail.approvalNumber) {
        this.$message.info(`跳转到审批单：${this.currentDetail.approvalNumber}`);
      }
    },

    previewFile(file) {
      const raw = typeof file === "string" ? file : file.raw || file.url || file.path;
      const url = this.getFileUrl(raw);
      if (url) {
        window.open(url, "_blank");
      } else {
        this.$message.warning("无法预览该文件");
      }
    },

    handleDetailContentFileChange(file) {
      if (file && file.raw) {
        this.detailUploadForm.contentFiles.push({
          name: file.name,
          raw: file.raw
        });
      }
    },

    handleDetailAttachmentFileChange(file) {
      if (file && file.raw) {
        this.detailUploadForm.attachmentFiles.push({
          name: file.name,
          raw: file.raw
        });
      }
    },

    removeDetailSelectedFile(field, index) {
      if (!this.detailUploadForm[field]) return;
      this.detailUploadForm[field].splice(index, 1);
    },

    async submitDetailFilesUpload() {
      const contractId = this.currentDetail && (this.currentDetail.id || this.currentDetail.contractId);
      if (!contractId) {
        this.$message.warning('缺少合同ID，无法上传');
        return;
      }
      if (!this.detailUploadForm.contentFiles.length && !this.detailUploadForm.attachmentFiles.length) {
        this.$message.warning('请先选择要上传的文件');
        return;
      }
      const formData = new FormData();
      formData.append('contractId', contractId);
      this.detailUploadForm.contentFiles.forEach(file => formData.append('contentFiles', file.raw));
      this.detailUploadForm.attachmentFiles.forEach(file => formData.append('attachmentFiles', file.raw));

      this.detailUploadLoading = true;
      try {
        await uploadContractDetailFiles(formData);
        this.$message.success('文件上传成功');
        this.detailUploadForm = { contentFiles: [], attachmentFiles: [] };
        await this.handleDetail(this.currentDetail);
      } catch (e) {
        this.$message.error('文件上传失败：' + ((e && (e.msg || e.message)) || '请稍后重试'));
      } finally {
        this.detailUploadLoading = false;
      }
    },

    getTagType(status) {
      const map = {
        rejected: "danger",
        approved: "success",
        pending: "warning",
        draft: "info"
      };
      return map[status];
    },

    getStatusText(status) {
      const map = {
        rejected: "已拒绝",
        approved: "已通过",
        pending: "审批中",
        draft: "草稿",
        signed: "已签署",
        archived: "已归档"
      };
      return map[status];
    },

    getCurrentDetailStatusText(row) {
      if (row.signStatus) return this.getSignStatusLabel(row.signStatus);
      return this.getStatusText(row.status) || "—";
    },

    getStatusTagType(row) {
      if (row.signStatus) return this.getSignStatusType(row.signStatus);
      return this.getTagType(row.status) || "info";
    },

    getSignStatusType(status) {
      const map = {
        "1": "warning",
        "2": "info",
        "3": "warning",
        "4": "warning",
        "5": "success",
        "6": "danger",
        "7": "danger"
      };
      return map[status] || "info";
    },

    getSignStatusLabel(status) {
      const map = {
        "1": "审批中",
        "2": "签署中",
        "3": "待归档",
        "4": "归档确认中",
        "5": "已归档",
        "6": "已撤销",
        "7": "已拒绝"
      };
      return map[status] || (status || "—");
    },

    getPerformanceStatusType(status) {
      const map = {
        "1": "info",
        "2": "info",
        "3": "warning",
        "4": "warning",
        "5": "danger",
        "6": "danger",
        "7": "danger",
        "8": "success"
      };
      return map[status] || "info";
    },

    getPerformanceStatusLabel(status) {
      const map = {
        "1": "未履约",
        "2": "待生效",
        "3": "履约中",
        "4": "30天内到期",
        "5": "7天内到期",
        "6": "今日到期",
        "7": "已到期",
        "8": "已完结"
      };
      return map[status] || (status || "—");
    },

    getBorrowStatusType(status) {
      const map = {
        "1": "success",
        "2": "danger",
        "3": "success"
      };
      return map[status] || "info";
    },

    getBorrowStatusLabel(status) {
      const map = {
        "1": "未借出",
        "2": "借阅中",
        "3": "已归还"
      };
      return map[status] || (status || "—");
    },

    copyText(text) {
      this.$copyText(text)
        .then(() => {
          this.$message.success("复制成功");
        })
        .catch(() => {
          this.$message.error("复制失败");
        });
    },

    handleChangeCategory() {
      if (this.ids.length === 0) {
        this.$message.warning("请先选择合同");
        return;
      }
      this.$message.info("更换分类功能待实现");
    },

    async handleApproveAction(action) {
      const { id, contractId } = this.currentApproveData || {};
      const realId = id || contractId;
      if (!realId) {
        this.$message.warning("合同ID缺失");
        return;
      }

      try {
        await approveContract({
          contractId: realId,
          action,
          remark: this.approveRemark
        });
        this.$message.success(action === "agree" ? "已同意" : "已拒绝");
        this.approveDrawer = false;
        this.approveRemark = "";
        this.getList();
      } catch (err) {
        this.$message.error("操作失败：" + ((err && (err.msg || err.message)) || "请稍后重试"));
      }
    },

    formatSignMethod(val) {
      return {
        paper: "纸质签署",
        electronic: "电子签署"
      }[val] || "—";
    },

    formatSealType(val) {
      return {
        official: "公章",
        contract: "合同章"
      }[val] || (val || "—");
    },

    formatTermType(val) {
      return {
        "1": "固定期限",
        "2": "无固定期限"
      }[val] || "—";
    },

    formatAmountType(val) {
      return {
        income: "收入",
        expense: "支出",
        none: "无金额"
      }[val] || (val || "—");
    },

    formatCategory(val) {
      return {
        sales: "销售合同",
        purchase: "采购合同",
        service: "服务合同",
        other: "其他"
      }[val] || (val || "未分类");
    },

    formatAccountStatus(val) {
      return {
        unpaid: "未付款",
        paid: "已付款"
      }[val] || (val || "—");
    },

    formatInvoiceStatus(val) {
      return {
        no_invoice: "未开票",
        invoiced: "已开票"
      }[val] || (val || "—");
    },

    toPercent(val) {
      if (val === null || val === undefined || val === "") return 0;
      const raw = String(val).replace('%', '');
      const num = Number(raw);
      if (Number.isNaN(num)) return 0;
      return Math.max(0, Math.min(100, num));
    },

    getProgressText(val) {
      return val ? (String(val).includes('%') ? String(val) : `${val}%`) : '0%';
    },

    formatMoney(val) {
      if (val === null || val === undefined || val === '') return '—';
      return `¥ ${val}`;
    },

    submitForm() {
      this.$refs.form.validate(async valid => {
        if (!valid) return;

        const formData = new FormData();
        formData.append("contractId", this.currentDetail.id || this.currentDetail.contractId);

        if (this.formData.file) {
          formData.append("file", this.formData.file);
        }

        formData.append("periodType", this.formData.periodType || "");
        formData.append("category", this.formData.category || "");
        formData.append("archiveNumber", this.formData.archiveNumber || "");
        formData.append("remark", this.formData.remark || "");

        try {
          await archiveContract(formData);
          this.$message.success("归档成功");
          this.open = false;
          this.resetArchiveForm();
          this.getList();
        } catch (e) {
          this.$message.error("归档失败");
        }
      });
    },

    handleFileChange(file) {
      this.fileName = file.name;
      this.formData.file = file.raw;
      if (this.$refs.form) {
        this.$refs.form.validateField("file");
      }
    },

    handleClose() {
      this.handleCloseArchiveDrawer(() => {
        this.open = false;
      });
    },

    handleFinish(row) {
      this.currentDetail = { ...this.currentDetail, ...row };
      this.finishForm = {
        contractId: row.id || row.contractId,
        result: "",
        finalStatus: "8"
      };
      this.finishDrawerVisible = true;
    },

    async submitFinish() {
      try {
        await finishContract(this.finishForm);
        this.$message.success("合同已完结");
        this.finishDrawerVisible = false;
        this.getList();
        const currentId = this.currentDetail && (this.currentDetail.id || this.currentDetail.contractId);
        if (currentId) {
          this.handleDetail(this.currentDetail);
        }
      } catch (e) {
        this.$message.error("完结失败：" + ((e && (e.msg || e.message)) || "请稍后重试"));
      }
    },
    handleCloseArchiveDrawer(done) {
  this.$confirm("确认关闭归档抽屉？未保存内容将丢失", "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  })
    .then(() => {
      this.resetArchiveForm();
      done();
    })
    .catch(() => {});
}
  }
};
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
  background: linear-gradient(180deg, #f6f8fb 0%, #f4f6fa 100%);
  min-height: 100vh;
}

.fade-in {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-header {
  margin-bottom: 20px;

  .page-title {
    font-size: 24px;
    color: #1f2d3d;
    font-weight: 700;
    margin: 0 0 6px 0;
  }

  .page-desc {
    margin: 0;
    color: #909399;
    font-size: 13px;
  }
}

.nav-cards {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
  margin-bottom: 20px;
}

.nav-card {
  cursor: pointer;
  transition: all 0.3s;
  border: 1px solid #e9edf3;
  border-radius: 16px;
  overflow: hidden;

  ::v-deep .el-card__body {
    padding: 18px 16px;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 28px rgba(31, 45, 61, 0.08);
  }
}

.active-card {
  border-color: #409eff;
  background: linear-gradient(180deg, #f6fbff 0%, #eef6ff 100%);
  box-shadow: 0 10px 24px rgba(64, 158, 255, 0.12);

  .card-icon-wrap {
    background: #409eff;
    color: #fff;
  }

  .card-label {
    color: #409eff;
  }
}

.card-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
}

.card-left {
  display: flex;
  align-items: center;
  min-width: 0;
}

.card-icon-wrap {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: #f2f6fc;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
  transition: all 0.3s;
}

.card-icon {
  font-size: 22px;
  color: inherit;
}

.card-texts {
  display: flex;
  flex-direction: column;
  min-width: 0;
}

.card-label {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}

.card-sub-label {
  margin-top: 6px;
  font-size: 12px;
  color: #909399;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.card-badge {
  margin-left: 10px;
}

.detail-list-section {
  margin-top: 2px;
}

.header-card,
.search-card,
.table-card {
  border-radius: 16px;
  border: 1px solid #ebeef5;
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04);
}

.header-card {
  ::v-deep .el-card__body {
    padding: 0 20px;
  }

  .header-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 64px;
  }

  .header-left {
    flex: 1;
    min-width: 0;
  }

  ::v-deep .el-tabs__nav-wrap::after {
    height: 1px;
    background-color: #eef1f5;
  }

  ::v-deep .el-tabs__item {
    height: 56px;
    line-height: 56px;
    font-size: 14px;
    padding: 0 15px;
  }

  ::v-deep .el-tabs__item.is-active {
    font-weight: 700;
    color: #409eff;
  }
}

.search-card {
  margin-top: 15px;

  ::v-deep .el-card__body {
    padding: 18px 20px 12px;
  }

  ::v-deep .el-form-item {
    margin-bottom: 16px;
    margin-right: 10px;
  }

  ::v-deep .el-form--inline .el-form-item {
    vertical-align: top;
  }
}

.search-card-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.search-title-left {
  display: flex;
  align-items: center;
  color: #303133;
  font-weight: 600;
  font-size: 15px;

  i {
    margin-right: 8px;
    color: #409eff;
  }
}

.expand-btn {
  font-weight: 600;
}

.search-actions {
  width: 100%;
  margin-top: 4px;
  margin-bottom: 4px !important;
}

.toolbar {
  margin-top: 15px;
  display: flex;
  gap: 10px;
  align-items: flex-start;
  justify-content: space-between;
  flex-wrap: wrap;
}

.toolbar-left {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.toolbar-right {
  margin-left: auto;
}

.table-card {
  margin-top: 15px;

  ::v-deep .el-card__body {
    padding: 0 0 16px;
  }
}

.contract-table {
  margin-top: 0;
}

.table-header-gray {
  background-color: #f8fafc !important;
  color: #606266;
  font-weight: 700;
}

.name-cell {
  display: flex;
  align-items: center;
  min-width: 0;
}

.name-icon {
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #ecf5ff;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 10px;
  flex-shrink: 0;

  i {
    font-size: 18px;
  }
}

.name-content {
  min-width: 0;
}

.name-title {
  font-weight: 600;
  color: #303133;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.name-sub {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.copy-cell {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
}

.copy-icon {
  cursor: pointer;
  color: #909399;
  font-size: 16px;

  &:hover {
    color: #409eff;
  }
}

.action-cell {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
}

.table-action-primary {
  font-weight: 700;
}

.table-pagination {
  margin-top: 20px;
  padding: 0 16px;
  display: flex;
  justify-content: flex-end;
}

.edit-drawer-content {
  padding: 20px;
  height: 100%;
  overflow-y: auto;

  .el-form {
    max-width: 100%;
  }
}

.edit-upload-panel {
  margin-top: 12px;
  padding: 16px;
  background: #fafcff;
  border: 1px solid #edf1f7;
  border-radius: 14px;
}

.edit-upload-panel__title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.edit-upload-group {
  background: #fff;
  border: 1px solid #edf1f7;
  border-radius: 12px;
  padding: 14px;

  & + .edit-upload-group {
    margin-top: 14px;
  }
}

.edit-upload-group__header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
}

.edit-upload-group__label {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.edit-upload-group__desc {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.edit-upload-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.edit-upload-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  padding: 12px 14px;
  border-radius: 10px;
  background: #f7faff;
  border: 1px solid #e8f3ff;
}

.edit-upload-item__left {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;

  i {
    color: #409eff;
    font-size: 16px;
    flex-shrink: 0;
  }

  span {
    color: #303133;
    word-break: break-all;
  }
}

.edit-upload-empty {
  padding: 12px 14px;
  border-radius: 10px;
  background: #f8fafc;
  color: #909399;
  font-size: 12px;
}

.danger-text {
  color: #f56c6c !important;
}

.edit-drawer {
  ::v-deep .el-drawer__header {
    margin-bottom: 0;
    padding: 18px 20px;
    border-bottom: 1px solid #ebeef5;

    span {
      font-size: 18px;
      font-weight: 700;
    }
  }

  ::v-deep .el-drawer__body {
    background: #f7f9fc;
    padding: 0;
    height: 100%;
    display: flex;
    flex-direction: column;
  }
}

.edit-drawer-shell {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.edit-drawer-content {
  flex: 1;
  overflow-y: auto;
}

.edit-upload-existing {
  margin-bottom: 12px;
}

.edit-upload-existing__title {
  margin-bottom: 8px;
  font-size: 12px;
  color: #909399;
}

.fixed-drawer-footer {
  padding: 14px 20px 18px;
  border-top: 1px solid #ebeef5;
  background: #fff;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.drawer-footer,
.dialog-footer {
  text-align: right;
}

.detail-drawer {
  ::v-deep .el-drawer__header {
    margin-bottom: 0;
    padding: 18px 20px;
    border-bottom: 1px solid #ebeef5;

    span {
      font-weight: 700;
      font-size: 18px;
    }
  }

  ::v-deep .el-drawer__body {
    background: #f7f9fc;
  }
}

.detail-page {
  padding: 20px;
  height: 100%;
  overflow-y: auto;
}

.detail-tabs {
  margin-bottom: 16px;
}

.detail-overview-page {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-hero-card {
  background: linear-gradient(135deg, #409eff 0%, #7ab8ff 100%);
  border-radius: 18px;
  padding: 22px;
  color: #fff;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  box-shadow: 0 14px 30px rgba(64, 158, 255, 0.18);
}

.detail-hero-main {
  display: flex;
  align-items: center;
  gap: 16px;
}

.detail-hero-icon {
  width: 60px;
  height: 60px;
  border-radius: 18px;
  background: rgba(255,255,255,0.18);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
}

.detail-hero-info h2 {
  margin: 0 0 10px;
  font-size: 24px;
}

.detail-hero-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
  font-size: 13px;
  opacity: 0.94;
}

.detail-hero-tags {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.detail-summary-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
}

.detail-premium-card {
  border-radius: 16px;
  border: 1px solid #edf1f7;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.detail-block-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.detail-upload-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.upload-tip-inline {
  margin-bottom: 12px;
  padding: 10px 12px;
  border-radius: 10px;
  background: #f4f8ff;
  color: #409eff;
  font-size: 12px;
}

.detail-file-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 12px;
}

.detail-file-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 14px 16px;
  border: 1px solid #edf1f7;
  border-radius: 14px;
  background: #fbfcfe;

  &.primary {
    background: linear-gradient(135deg, #f7fbff 0%, #eef6ff 100%);
    border-color: #d9ecff;
  }
}

.detail-file-card__left {
  display: flex;
  align-items: center;
  gap: 12px;
  min-width: 0;

  i {
    color: #409eff;
    font-size: 20px;
  }
}

.detail-file-card__name {
  color: #303133;
  font-weight: 600;
}

.detail-file-card__path {
  margin-top: 4px;
  color: #909399;
  font-size: 12px;
  word-break: break-all;
}

.detail-file-card__actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.detail-upload-footer {
  margin-top: 16px;
  text-align: right;
}

.detail-block {
  border-radius: 14px;
  margin-bottom: 16px;

  ::v-deep .el-card__header {
    padding: 14px 18px;
    border-bottom: 1px solid #f0f2f5;
  }

  ::v-deep .el-card__body {
    padding: 18px;
  }
}

.detail-block-title {
  font-weight: 700;
  color: #303133;
}

.file-item {
  padding: 12px 0;
  border-bottom: 1px solid #f0f2f5;
  display: flex;
  align-items: center;
  justify-content: space-between;

  &:last-child {
    border-bottom: none;
  }
}

.file-item-left {
  display: flex;
  align-items: center;
  min-width: 0;
}

.file-icon {
  color: #409eff;
  font-size: 18px;
  margin-right: 10px;
}

.file-main {
  min-width: 0;
}

.file-name {
  font-weight: 500;
  color: #303133;
}

.file-size {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.file-preview-link,
.approval-link {
  color: #409eff;
  text-decoration: none;
}

.empty-text {
  color: #909399;
  padding: 10px 0;
}

.empty-panel {
  min-height: 240px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: #909399;

  &.small {
    min-height: 120px;
  }
}

.empty-panel-icon {
  font-size: 42px;
  opacity: 0.5;
  margin-bottom: 12px;
}

.log-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 0;
  border-bottom: 1px solid #f0f2f5;

  &:last-child {
    border-bottom: none;
  }
}

.log-left {
  display: flex;
  align-items: center;
}

.log-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #ecf5ff;
  color: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  margin-right: 10px;
}

.log-main {
  display: flex;
  flex-direction: column;
}

.log-operator {
  font-weight: 600;
  color: #303133;
}

.log-action {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

.log-time {
  font-size: 12px;
  color: #909399;
}

.modern-approve-drawer {
  .el-drawer__body {
    background: #f6f8fb;
    padding: 0;
    overflow: hidden;
  }
}

.approve-page {
  height: 100%;
  overflow-y: auto;
  padding: 20px 20px 110px;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #d8dde6;
    border-radius: 10px;
  }
}

.approve-hero {
  background: linear-gradient(135deg, #409eff 0%, #66b1ff 100%);
  border-radius: 16px;
  padding: 22px 24px;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 10px 24px rgba(64, 158, 255, 0.18);
  margin-bottom: 18px;

  &__left {
    display: flex;
    align-items: center;
  }

  &__icon {
    width: 56px;
    height: 56px;
    border-radius: 14px;
    background: rgba(255, 255, 255, 0.18);
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    margin-right: 16px;
  }

  &__info h2 {
    margin: 0 0 8px;
    font-size: 22px;
    font-weight: 600;
  }

  &__meta {
    display: flex;
    flex-wrap: wrap;
    gap: 16px;
    font-size: 13px;
    opacity: 0.92;
  }
}

.approve-summary-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px;
  margin-bottom: 18px;
}

.summary-card {
  background: #fff;
  border-radius: 14px;
  padding: 18px 20px;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.05);

  &__label {
    color: #909399;
    font-size: 13px;
    margin-bottom: 8px;
  }

  &__value {
    font-size: 20px;
    font-weight: 600;
    color: #303133;

    &.money {
      color: #e6a23c;
    }
  }
}

.approve-section {
  background: #fff;
  border-radius: 14px;
  padding: 18px;
  margin-bottom: 16px;
  box-shadow: 0 4px 16px rgba(15, 23, 42, 0.05);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 14px;
  position: relative;
  padding-left: 10px;

  &::before {
    content: "";
    position: absolute;
    left: 0;
    top: 4px;
    width: 4px;
    height: 16px;
    border-radius: 3px;
    background: #409eff;
  }
}

.modern-descriptions {
  ::v-deep .el-descriptions__label {
    width: 110px;
    background: #fafbfd;
    color: #606266;
    font-weight: 500;
  }

  ::v-deep .el-descriptions-item__content {
    color: #303133;
  }
}

.remark-box {
  background: #fafbfd;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  padding: 14px 16px;
  line-height: 1.8;
  color: #606266;
  min-height: 80px;
}

.process-card {
  background: #fafbfd;
  border-radius: 12px;
  padding: 18px;
  border: 1px solid #edf1f7;
}

.process-timeline {
  margin-top: 22px;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  position: relative;
  padding: 0 0 18px 0;

  &:last-child {
    padding-bottom: 0;
  }

  .timeline-dot {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    background: #c0c4cc;
    margin-right: 14px;
    margin-top: 6px;
    flex-shrink: 0;
  }

  .timeline-content {
    flex: 1;
  }

  .timeline-title {
    font-size: 15px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 4px;
  }

  .timeline-desc {
    font-size: 13px;
    color: #909399;
  }

  .timeline-time {
    font-size: 12px;
    color: #909399;
    margin-left: 10px;
    white-space: nowrap;
  }

  &.done .timeline-dot {
    background: #67c23a;
  }

  &.current .timeline-dot {
    background: #409eff;
    box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.12);
  }
}

.approve-action-bar {
  position: sticky;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-top: 1px solid #ebeef5;
  padding: 14px 20px;
  display: flex;
  gap: 14px;
  align-items: flex-end;

  .approve-remark-input {
    flex: 1;
  }

  .approve-action-buttons {
    display: flex;
    gap: 10px;
    flex-shrink: 0;
  }
}

.modern-sign-drawer {
  .el-drawer__body {
    background: #f6f8fb;
    padding: 0;
  }
}

.sign-page {
  height: 100%;
  overflow-y: auto;
  padding: 22px 20px 90px;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #d8dde6;
    border-radius: 10px;
  }
}

.sign-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;

  h2 {
    margin: 0;
    font-size: 24px;
    color: #303133;
    font-weight: 600;
  }

  p {
    margin: 8px 0 0;
    font-size: 13px;
    color: #909399;
  }
}

.sign-contract-card,
.sign-form-card {
  background: #fff;
  border-radius: 16px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.05);
  margin-bottom: 16px;
}

.sign-contract-card__title {
  display: flex;
  align-items: center;
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;

  i {
    color: #409eff;
    font-size: 20px;
    margin-right: 10px;
  }
}

.sign-info-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.sign-info-item {
  background: #fafbfd;
  border-radius: 10px;
  padding: 12px 14px;

  label {
    display: block;
    color: #909399;
    font-size: 12px;
    margin-bottom: 6px;
  }

  span {
    color: #303133;
    font-size: 14px;
    font-weight: 500;
    word-break: break-all;
  }
}

.modern-sign-form {
  ::v-deep .el-form-item__label {
    color: #606266;
    font-weight: 500;
  }

  ::v-deep .el-input__inner,
  ::v-deep .el-textarea__inner {
    border-radius: 10px;
  }
}

.archive-switch-box {
  margin-top: 6px;
  padding: 14px 16px;
  background: #f7fbff;
  border: 1px solid #e6f1fc;
  border-radius: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;

  &__title {
    font-size: 14px;
    color: #303133;
    font-weight: 600;
    margin-bottom: 4px;
  }

  &__desc {
    font-size: 12px;
    color: #909399;
  }
}

.sign-footer {
  position: sticky;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-top: 1px solid #ebeef5;
  padding: 14px 20px;
  text-align: right;
}

.column-settings {
  max-height: 400px;
  overflow-y: auto;

  .column-item {
    padding: 8px 0;
    border-bottom: 1px solid #f0f0f0;

    &:last-child {
      border-bottom: none;
    }

    ::v-deep .el-checkbox {
      width: 100%;
      margin-right: 0;

      .el-checkbox__label {
        width: 100%;
        font-size: 14px;
      }
    }
  }
}

/* ===== 归档抽屉 ===== */
.archive-drawer {
  ::v-deep .el-drawer__header {
    margin-bottom: 0;
    padding: 20px 24px;
    border-bottom: 1px solid #ebeef5;
    background: #fff;

    span {
      font-size: 18px;
      font-weight: 700;
      color: #1f2d3d;
    }
  }

  ::v-deep .el-drawer__body {
    background: linear-gradient(180deg, #f7f9fc 0%, #f3f6fb 100%);
    padding: 0;
    height: 100%;
    overflow: hidden;
  }
}

.archive-drawer-shell {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.archive-drawer-header-card {
  margin: 20px 20px 0;
  padding: 18px 20px;
  border-radius: 18px;
  background: linear-gradient(135deg, #eef6ff 0%, #f7fbff 100%);
  border: 1px solid #dceafd;
  display: flex;
  align-items: flex-start;
  gap: 14px;
}

.archive-drawer-header-card__icon {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  background: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  flex-shrink: 0;
}

.archive-drawer-header-card__title {
  font-size: 16px;
  font-weight: 700;
  color: #1f2d3d;
  margin-bottom: 4px;
}

.archive-drawer-header-card__desc {
  font-size: 12px;
  color: #6b7280;
  line-height: 1.7;
}

.archive-drawer-content {
  flex: 1;
  overflow-y: auto;
  padding: 18px 20px;
}

.archive-form {
  padding: 18px 18px 4px;
  border-radius: 18px;
  background: #fff;
  border: 1px solid #ebeef5;
  box-shadow: 0 8px 24px rgba(15, 23, 42, 0.05);
}

.upload-panel {
  padding: 14px 16px;
  border: 1px dashed #cbd5e1;
  border-radius: 14px;
  background: #f8fbff;
}

.upload-file-name {
  margin-top: 10px;
  color: #303133;
  font-weight: 600;
  word-break: break-all;
}

.upload-file-placeholder {
  margin-top: 10px;
  color: #909399;
}

.upload-tips {
  margin-top: 8px;
  color: #909399;
  font-size: 12px;
}

.archive-drawer-footer {
  border-top: 1px solid #ebeef5;
  background: rgba(255, 255, 255, 0.96);
  padding: 16px 20px 20px;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  box-shadow: 0 -8px 24px rgba(15, 23, 42, 0.04);
}

/* ===== 完结抽屉 ===== */
.finish-drawer {
  ::v-deep .el-drawer__header {
    margin-bottom: 0;
    padding: 18px 20px;
    border-bottom: 1px solid #ebeef5;

    span {
      font-size: 18px;
      font-weight: 700;
    }
  }

  ::v-deep .el-drawer__body {
    background: #f7f9fc;
    padding: 0;
    display: flex;
    flex-direction: column;
    height: 100%;
  }
}

.finish-drawer-content {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.finish-drawer-footer {
  border-top: 1px solid #ebeef5;
  background: #fff;
  padding: 14px 20px;
  text-align: right;
}

.contract-content-preview {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 16px 18px;
  border-radius: 14px;
  background: linear-gradient(135deg, #f7fbff 0%, #eef6ff 100%);
  border: 1px solid #d9ecff;
}

.content-preview-main {
  display: flex;
  align-items: center;
  gap: 12px;

  i {
    font-size: 26px;
    color: #409eff;
  }
}

.content-preview-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
}

.content-preview-sub {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}

.content-preview-actions {
  display: flex;
  gap: 10px;
}

.performance-overview-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 16px;
}

.performance-metric {
  background: #fafcff;
  border: 1px solid #edf2f8;
  border-radius: 16px;
  padding: 18px;
}

.metric-head,
.metric-footer,
.performance-stat-card,
.timeline-row,
.finish-contract-meta {
  display: flex;
  justify-content: space-between;
  gap: 12px;
}

.metric-head {
  align-items: center;
  margin-bottom: 14px;

  span {
    color: #606266;
    font-size: 14px;
  }

  strong {
    color: #303133;
    font-size: 20px;
  }
}

.metric-footer {
  margin-top: 12px;
  color: #909399;
  font-size: 12px;
}

.performance-detail-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 14px;
  margin-bottom: 16px;
}

.performance-stat-card {
  flex-direction: column;
  padding: 16px;
  background: #fff;
  border-radius: 14px;
  border: 1px solid #edf1f7;

  label {
    color: #909399;
    font-size: 12px;
    margin-bottom: 6px;
  }

  div {
    color: #303133;
    font-size: 15px;
    font-weight: 600;
  }
}

.performance-timeline {
  background: #fff;
  border: 1px solid #edf1f7;
  border-radius: 14px;
  overflow: hidden;
}

.timeline-row {
  padding: 14px 16px;
  border-bottom: 1px solid #f1f3f6;
  color: #606266;

  &:last-child {
    border-bottom: none;
  }

  strong {
    color: #303133;
    font-weight: 600;
  }
}

.modern-record-account-drawer {
  .el-drawer__body {
    background: #f6f8fb;
    padding: 0;
  }
}

.record-account-page {
  height: 100%;
  overflow-y: auto;
  padding: 22px 20px 90px;

  &::-webkit-scrollbar {
    width: 6px;
  }

  &::-webkit-scrollbar-thumb {
    background: #d8dde6;
    border-radius: 10px;
  }
}

.record-account-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 18px;

  h2 {
    margin: 0;
    font-size: 24px;
    color: #303133;
    font-weight: 600;
  }

  p {
    margin: 8px 0 0;
    font-size: 13px;
    color: #909399;
  }
}

.record-account-contract-card,
.record-account-form-card {
  background: #fff;
  border-radius: 16px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.05);
  margin-bottom: 16px;
}

.record-account-contract-card__title {
  display: flex;
  align-items: center;
  font-size: 17px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 16px;

  i {
    color: #409eff;
    font-size: 20px;
    margin-right: 10px;
  }
}

.record-account-info-list {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.record-account-info-item {
  background: #fafbfd;
  border-radius: 10px;
  padding: 12px 14px;

  label {
    display: block;
    color: #909399;
    font-size: 12px;
    margin-bottom: 6px;
  }

  span {
    color: #303133;
    font-size: 14px;
    font-weight: 500;
    word-break: break-all;
  }
}

.modern-record-account-form {
  ::v-deep .el-form-item__label {
    color: #606266;
    font-weight: 500;
  }

  ::v-deep .el-input__inner,
  ::v-deep .el-textarea__inner {
    border-radius: 10px;
  }
}

.record-account-footer {
  position: sticky;
  bottom: 0;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(8px);
  border-top: 1px solid #ebeef5;
  padding: 14px 20px;
  text-align: right;
}

.modern-finish-drawer {
  .el-drawer__body {
    background: #f6f8fb;
    padding: 0;
  }
}

.finish-page {
  height: 100%;
  overflow-y: auto;
  padding: 22px 20px 24px;
}

.finish-hero {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20px;
  border-radius: 18px;
  background: linear-gradient(135deg, #67c23a 0%, #95d475 100%);
  color: #fff;
  margin-bottom: 16px;

  h2 {
    margin: 0;
    font-size: 24px;
  }

  p {
    margin: 8px 0 0;
    opacity: 0.92;
    font-size: 13px;
  }
}

.finish-contract-card,
.finish-form-card {
  background: #fff;
  border-radius: 16px;
  padding: 18px;
  box-shadow: 0 6px 18px rgba(15, 23, 42, 0.05);
  margin-bottom: 16px;
}

.finish-contract-title {
  font-size: 18px;
  font-weight: 700;
  color: #303133;
  margin-bottom: 10px;
}

.finish-contract-meta {
  flex-wrap: wrap;
  color: #909399;
  font-size: 13px;
}

.finish-drawer-footer.modern {
  padding: 0;
  border-top: none;
  background: transparent;
}

@media (max-width: 1200px) {
  .nav-cards {
    grid-template-columns: repeat(3, 1fr);
  }
}

@media (max-width: 900px) {
  .nav-cards {
    grid-template-columns: repeat(2, 1fr);
  }

  .approve-summary-grid,
  .sign-info-list,
  .record-account-info-list,
  .detail-summary-grid {
    grid-template-columns: 1fr;
  }

  .approve-action-bar {
    flex-direction: column;
    align-items: stretch;

    .approve-action-buttons {
      justify-content: flex-end;
    }
  }

  .approve-hero {
    flex-direction: column;
    align-items: flex-start;
    gap: 14px;
  }

  .toolbar {
    flex-direction: column;
    align-items: stretch;
  }

  .toolbar-right {
    margin-left: 0;
  }
}

@media (max-width: 768px) {
  .nav-cards {
    grid-template-columns: 1fr;
  }

  .header-card .header-content {
    flex-direction: column;
    align-items: stretch;
    padding: 8px 0;
  }

  .search-card ::v-deep .el-form-item {
    display: block;
    margin-right: 0;
  }

  .sign-header,
  .detail-hero-card,
  .detail-file-card,
  .detail-block-header,
  .edit-upload-group__header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }

  .detail-file-card__actions {
    width: 100%;
    justify-content: flex-end;
  }
}
</style>