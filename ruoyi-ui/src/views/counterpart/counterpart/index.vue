<template>
  <div class="app-container counterpart-page">
    <!-- 顶部标题区 -->
    <div class="page-header-card">
      <div class="page-header-left">
        <div class="page-icon">
          <i class="el-icon-office-building"></i>
        </div>
        <div>
          <div class="page-title">相对方管理</div>
          <div class="page-subtitle">统一管理相对方基础信息，支持检索、导入、导出与维护</div>
        </div>
      </div>
      <div class="page-header-right">
        <el-button
          class="header-btn"
          type="primary"
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['counterpart:counterpart:add']"
        >
          新增相对方
        </el-button>
      </div>
    </div>

    <!-- 搜索区 -->
    <el-card shadow="never" class="search-card">
      <div slot="header" class="search-card-header">
        <div class="search-title">
          <i class="el-icon-search"></i>
          <span>筛选查询</span>
        </div>
        <div class="search-actions">
          <el-button type="text" @click="showSearch = !showSearch">
            <i :class="showSearch ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            {{ showSearch ? '收起' : '展开' }}
          </el-button>
        </div>
      </div>

      <el-form
        v-show="showSearch"
        :model="queryParams"
        ref="queryForm"
        size="small"
        label-position="top"
        class="search-form"
      >
        <div class="search-grid">
          <el-form-item label="相对方名称" prop="counterpartName">
            <el-input
              v-model="queryParams.counterpartName"
              placeholder="请输入相对方名称"
              clearable
              prefix-icon="el-icon-user"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>

          <el-form-item label="所属员工工号" prop="employeeId">
            <el-input
              v-model="queryParams.employeeId"
              placeholder="请输入所属员工工号"
              clearable
              prefix-icon="el-icon-postcard"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>

          <el-form-item label="开户银行" prop="bankName">
            <el-input
              v-model="queryParams.bankName"
              placeholder="请输入开户银行"
              clearable
              prefix-icon="el-icon-bank-card"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>

          <el-form-item label="开户名称" prop="accountName">
            <el-input
              v-model="queryParams.accountName"
              placeholder="请输入开户名称"
              clearable
              prefix-icon="el-icon-s-custom"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>

          <el-form-item label="银行账户" prop="bankAccount">
            <el-input
              v-model="queryParams.bankAccount"
              placeholder="请输入银行账户"
              clearable
              prefix-icon="el-icon-credit-card"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>

          <el-form-item label="纳税人识别号" prop="taxId">
            <el-input
              v-model="queryParams.taxId"
              placeholder="请输入纳税人识别号"
              clearable
              prefix-icon="el-icon-tickets"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>

          <el-form-item label="电话" prop="phone">
            <el-input
              v-model="queryParams.phone"
              placeholder="请输入电话"
              clearable
              prefix-icon="el-icon-phone-outline"
              @keyup.enter.native="handleQuery"
            />
          </el-form-item>
        </div>

        <div class="search-footer">
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </div>
      </el-form>
    </el-card>

    <!-- 操作栏 -->
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar-wrap">
        <div class="toolbar-left">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
            v-hasPermi="['counterpart:counterpart:add']"
          >
            新增
          </el-button>

          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['counterpart:counterpart:edit']"
          >
            修改
          </el-button>

          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['counterpart:counterpart:remove']"
          >
            删除
          </el-button>

          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['counterpart:counterpart:export']"
          >
            导出
          </el-button>

          <el-button
            type="info"
            plain
            icon="el-icon-document"
            size="mini"
            @click="handleDownloadTemplate"
          >
            下载模板
          </el-button>

          <el-button
            type="primary"
            plain
            icon="el-icon-upload2"
            size="mini"
            @click="handleImport"
            v-hasPermi="['counterpart:counterpart:import']"
          >
            导入
          </el-button>
        </div>

        <div class="toolbar-right">
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
        </div>
      </div>
    </el-card>

    <!-- 数据表格 -->
    <el-card shadow="never" class="table-card">
      <div slot="header" class="table-card-header">
        <div class="table-title">
          <i class="el-icon-menu"></i>
          <span>相对方列表</span>
        </div>
        <div class="table-summary">
          共 <span class="highlight">{{ total }}</span> 条数据
        </div>
      </div>

      <el-table
        v-loading="loading"
        :data="counterpartList"
        border
        stripe
        class="custom-table"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" fixed="left" />
        <el-table-column label="相对方名称" align="center" prop="counterpartName" min-width="180" show-overflow-tooltip />
        <el-table-column label="所属员工工号" align="center" prop="employeeId" min-width="120" show-overflow-tooltip />
        <el-table-column label="开户银行" align="center" prop="bankName" min-width="160" show-overflow-tooltip />
        <el-table-column label="开户名称" align="center" prop="accountName" min-width="160" show-overflow-tooltip />
        <el-table-column label="银行账户" align="center" prop="bankAccount" min-width="180" show-overflow-tooltip />
        <el-table-column label="纳税人识别号" align="center" prop="taxId" min-width="180" show-overflow-tooltip />
        <el-table-column label="地址" align="center" prop="address" min-width="220" show-overflow-tooltip />
        <el-table-column label="电话" align="center" prop="phone" min-width="140" show-overflow-tooltip />

        <el-table-column
          label="操作"
          align="center"
          fixed="right"
          width="160"
          class-name="small-padding fixed-width"
        >
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              class="action-btn edit-btn"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['counterpart:counterpart:edit']"
            >
              修改
            </el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              class="action-btn delete-btn"
              @click="handleDelete(scope.row)"
              v-hasPermi="['counterpart:counterpart:remove']"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-wrap">
        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </div>
    </el-card>

    <!-- 添加或修改对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="720px"
      append-to-body
      custom-class="custom-dialog"
    >
      <div class="dialog-form-wrap">
        <el-form ref="form" :model="form" :rules="rules" label-position="top" class="dialog-form">
          <div class="dialog-grid">
            <el-form-item label="相对方名称" prop="counterpartName">
              <el-input v-model="form.counterpartName" placeholder="请输入相对方名称" />
            </el-form-item>

            <el-form-item label="所属员工工号" prop="employeeId">
              <el-input v-model="form.employeeId" placeholder="请输入所属员工工号" />
            </el-form-item>

            <el-form-item label="开户银行" prop="bankName">
              <el-input v-model="form.bankName" placeholder="请输入开户银行" />
            </el-form-item>

            <el-form-item label="开户名称" prop="accountName">
              <el-input v-model="form.accountName" placeholder="请输入开户名称" />
            </el-form-item>

            <el-form-item label="银行账户" prop="bankAccount">
              <el-input v-model="form.bankAccount" placeholder="请输入银行账户" />
            </el-form-item>

            <el-form-item label="纳税人识别号" prop="taxId">
              <el-input v-model="form.taxId" placeholder="请输入纳税人识别号" />
            </el-form-item>

            <el-form-item label="电话" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入电话" />
            </el-form-item>

            <el-form-item label="地址" prop="address" class="grid-span-2">
              <el-input
                v-model="form.address"
                type="textarea"
                :rows="3"
                placeholder="请输入地址"
              />
            </el-form-item>
          </div>
        </el-form>
      </div>

      <div slot="footer" class="dialog-footer custom-dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listCounterpart,
  getCounterpart,
  delCounterpart,
  addCounterpart,
  updateCounterpart,
  downloadTemplate,
  importCounterpart
} from "@/api/counterpart/counterpart"

export default {
  name: "Counterpart",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 相对方信息表格数据
      counterpartList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        counterpartName: null,
        employeeId: null,
        bankName: null,
        accountName: null,
        bankAccount: null,
        taxId: null,
        address: null,
        phone: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        counterpartName: [
          { required: true, message: "相对方名称不能为空", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询相对方信息列表 */
    getList() {
      this.loading = true
      listCounterpart(this.queryParams)
        .then(response => {
          this.counterpartList = response.rows
          this.total = response.total
        })
        .finally(() => {
          this.loading = false
        })
    },

    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },

    // 表单重置
    reset() {
      this.form = {
        id: null,
        counterpartName: null,
        employeeId: null,
        bankName: null,
        accountName: null,
        bankAccount: null,
        taxId: null,
        address: null,
        phone: null,
        createTime: null
      }
      this.resetForm("form")
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },

    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },

    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },

    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加相对方信息"
    },

    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row && row.id ? row.id : this.ids
      getCounterpart(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改相对方信息"
      })
    },

    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateCounterpart(this.form).then(() => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addCounterpart(this.form).then(() => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },

    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row && row.id ? row.id : this.ids
      this.$modal.confirm('是否确认删除相对方信息编号为 "' + ids + '" 的数据项？')
        .then(function () {
          return delCounterpart(ids)
        })
        .then(() => {
          this.getList()
          this.$modal.msgSuccess("删除成功")
        })
        .catch(() => {})
    },

    /** 导出按钮操作 */
    handleExport() {
      this.download(
        "counterpart/counterpart/export",
        { ...this.queryParams },
        `counterpart_${new Date().getTime()}.xlsx`
      )
    },

    /** 下载模板 */
    async handleDownloadTemplate() {
      try {
        const blob = await downloadTemplate()
        const url = window.URL.createObjectURL(blob)
        const a = document.createElement("a")
        a.href = url
        a.download = "相对方导入模板.xlsx"
        a.click()
        window.URL.revokeObjectURL(url)
      } catch (err) {
        this.$modal.msgError("下载模板失败：" + (err.msg || err.message))
      }
    },

    /** 导入 */
    handleImport() {
      const fileInput = document.createElement("input")
      fileInput.type = "file"
      fileInput.accept = ".xlsx, .xls"
      fileInput.onchange = async (e) => {
        const file = e.target.files[0]
        if (!file) return

        if (!file.name.endsWith(".xlsx") && !file.name.endsWith(".xls")) {
          this.$modal.msgError("请上传 Excel 文件（.xlsx 或 .xls）")
          return
        }

        this.$modal.loading("正在导入，请稍候...")

        try {
          const response = await importCounterpart(file)
          this.$modal.closeLoading()
          this.$modal.msgSuccess(`导入成功！共 ${response.data || 0} 条数据`)
          this.getList()
        } catch (err) {
          this.$modal.closeLoading()
          const msg = err.response?.data?.msg || err.message || "未知错误"
          this.$modal.msgError("导入失败：" + msg)
        }
      }
      fileInput.click()
    }
  }
}
</script>

<style scoped lang="scss">
.counterpart-page {
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(180deg, #f6f8fb 0%, #f4f6fa 100%);
}

.page-header-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 24px 28px;
  margin-bottom: 20px;
  border-radius: 18px;
  background: linear-gradient(135deg, #409eff 0%, #6aa8ff 55%, #8bb9ff 100%);
  box-shadow: 0 14px 30px rgba(64, 158, 255, 0.18);
}

.page-header-left {
  display: flex;
  align-items: center;
  gap: 16px;
}

.page-icon {
  width: 56px;
  height: 56px;
  border-radius: 16px;
  background: rgba(255, 255, 255, 0.18);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  box-shadow: inset 0 0 0 1px rgba(255, 255, 255, 0.12);
}

.page-title {
  font-size: 24px;
  font-weight: 700;
  color: #fff;
  line-height: 1.2;
}

.page-subtitle {
  margin-top: 6px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.9);
}

.header-btn {
  border-radius: 10px;
  padding: 10px 16px;
  box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08);
}

.search-card,
.toolbar-card,
.table-card {
  border: 1px solid #ebeef5;
  border-radius: 16px;
  margin-bottom: 18px;
  box-shadow: 0 6px 20px rgba(15, 23, 42, 0.04);
}

.search-card-header,
.table-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.search-title,
.table-title {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #303133;
  font-weight: 600;
}

.search-title i,
.table-title i {
  color: #409eff;
  font-size: 16px;
}

.search-form {
  padding-top: 4px;
}

.search-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 8px 16px;
}

.search-footer {
  margin-top: 8px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.toolbar-wrap {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-left {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.table-summary {
  font-size: 13px;
  color: #909399;
}

.highlight {
  color: #409eff;
  font-weight: 700;
}

.custom-table ::v-deep .el-table__header-wrapper th {
  background: #f8fafc;
  color: #606266;
  font-weight: 700;
}

.custom-table ::v-deep .el-table__row:hover > td {
  background: #f5faff !important;
}

.custom-table ::v-deep td,
.custom-table ::v-deep th {
  padding: 12px 0;
}

.action-btn {
  font-weight: 500;
}

.edit-btn {
  color: #409eff;
  margin-right: 10px;
}

.delete-btn {
  color: #f56c6c;
}

.pagination-wrap {
  margin-top: 18px;
  display: flex;
  justify-content: flex-end;
}

.dialog-form-wrap {
  padding-top: 6px;
}

.dialog-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 8px 16px;
}

.grid-span-2 {
  grid-column: span 2;
}

.custom-dialog-footer {
  padding-top: 8px;
}

::v-deep .el-card__header {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f2f5;
}

::v-deep .el-card__body {
  padding: 20px;
}

::v-deep .el-form-item__label {
  font-weight: 600;
  color: #606266;
  padding-bottom: 6px;
}

::v-deep .el-input__inner,
::v-deep .el-textarea__inner,
::v-deep .el-button {
  border-radius: 10px;
}

::v-deep .custom-dialog {
  .el-dialog {
    border-radius: 18px;
    overflow: hidden;
  }

  .el-dialog__header {
    padding: 20px 24px 12px;
    border-bottom: 1px solid #f0f2f5;
  }

  .el-dialog__title {
    font-size: 18px;
    font-weight: 700;
    color: #303133;
  }

  .el-dialog__body {
    padding: 20px 24px 10px;
    background: #f7f9fc;
  }

  .el-dialog__footer {
    padding: 12px 24px 20px;
    background: #fff;
  }
}

@media (max-width: 1400px) {
  .search-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 992px) {
  .page-header-card {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .search-grid,
  .dialog-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 768px) {
  .counterpart-page {
    padding: 12px;
  }

  .search-grid,
  .dialog-grid {
    grid-template-columns: 1fr;
  }

  .grid-span-2 {
    grid-column: span 1;
  }

  .toolbar-wrap {
    flex-direction: column;
    align-items: flex-start;
  }

  .search-footer {
    justify-content: flex-start;
    flex-wrap: wrap;
  }
}
</style>