<template>
  <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body @close="handleClose">
    <el-form ref="form" :model="form" :rules="rules" label-width="110px">
      
      <!-- 基本信息 -->
      <el-divider content-position="left">基本信息</el-divider>
      <el-row>
        <el-col :span="12">
          <el-form-item label="合同编号" prop="contractNo">
            <el-input v-model="form.contractNo" placeholder="请输入合同编号" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="合同名称" prop="contractName">
            <el-input v-model="form.contractName" placeholder="请输入合同名称" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="合同分类" prop="categoryId">
            <!-- 若依 tree-select 组件，如果没有可改用普通 select -->
            <el-tree-select
              v-if="categoryOptions.length > 0"
              v-model="form.categoryId"
              :data="categoryOptions"
              :props="{ value: 'categoryId', label: 'categoryName', children: 'children' }"
              check-strictly
              placeholder="请选择分类"
              style="width: 100%"
            />
            <el-input v-else v-model="form.categoryId" placeholder="暂无分类数据" disabled />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="合同总额" prop="totalAmount">
            <el-input-number v-model="form.totalAmount" :precision="2" :step="1000" :min="0" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>

      <el-row>
        <el-col :span="12">
          <el-form-item label="对方主体" prop="otherSubjectName">
            <el-input v-model="form.otherSubjectName" placeholder="请输入对方主体" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="签署状态" prop="signStatus">
            <el-select v-model="form.signStatus" placeholder="请选择">
              <el-option label="待签署" value="pending" />
              <el-option label="已签署" value="signed" />
              <el-option label="作废" value="void" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      
      <el-row>
        <el-col :span="24">
          <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 附件列表 -->
      <el-divider content-position="left">合同附件</el-divider>
      <el-table :data="form.bizContractFileList" style="width: 100%; margin-bottom: 10px;" max-height="250">
        <el-table-column label="文件类型" align="center" prop="fileType" width="120">
          <template slot-scope="scope">
            <el-select v-model="scope.row.fileType" placeholder="请选择" size="small">
              <el-option label="合同正文" value="main" />
              <el-option label="附件材料" value="attachment" />
              <el-option label="归档文件" value="archive" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="文件名" align="center" prop="fileName">
          <template slot-scope="scope">
            <el-input v-model="scope.row.fileName" placeholder="请输入文件名" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="文件路径" align="center" prop="filePath">
          <template slot-scope="scope">
            <div style="display: flex; align-items: center;">
              <el-input v-model="scope.row.filePath" placeholder="点击上传" disabled size="small" style="margin-right: 5px;" />
              <el-button type="primary" size="mini" icon="el-icon-upload" @click="handleUpload(scope.row)">上传</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="大小(KB)" align="center" prop="fileSize" width="100">
          <template slot-scope="scope">
            <el-input-number v-model="scope.row.fileSize" :min="0" controls-position="right" size="small" style="width: 80px"/>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="80">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-delete" size="small" style="color: #f56c6c;" @click="handleDeleteFile(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-button type="dashed" plain icon="el-icon-plus" @click="handleAddFile" style="margin-bottom: 15px; width: 100%;">添加附件行</el-button>

    </el-form>
    
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { getContract, addContract, updateContract } from "@/api/contract/contract";
import { listCategory } from "@/api/contract/category"; // 若无此接口请注释

export default {
  name: "ContractDialog",
  props: {
    // 如果需要可以从父组件传参，目前主要靠 ref 调用
  },
  data() {
    return {
      open: false,
      title: "",
      form: {},
      rules: {
        contractNo: [{ required: true, message: "合同编号不能为空", trigger: "blur" }],
        contractName: [{ required: true, message: "合同名称不能为空", trigger: "blur" }],
        totalAmount: [{ required: true, message: "合同总额不能为空", trigger: "blur" }]
      },
      categoryOptions: []
    };
  },
  methods: {
    /** 
     * 由父组件调用：显示弹窗 
     * type: '新增' 或 '修改'
     * id: 合同ID
     */
    show(type, id) {
      this.open = true;
      this.title = type;
      
      // 重置表单
      this.form = {
        bizContractFileList: []
      };
      
      // 加载分类树
      if (typeof listCategory === 'function') {
        listCategory().then(response => {
          this.categoryOptions = this.handleTree(response.data, "categoryId");
        }).catch(() => {});
      }

      // 如果是修改，查询详情
      if (id) {
        getContract(id).then(response => {
          this.form = response.data;
          if (!this.form.bizContractFileList) {
            this.form.bizContractFileList = [];
          }
        });
      }
    },
    
    handleAddFile() {
      let row = {
        fileType: "attachment",
        fileName: "",
        filePath: "",
        fileSize: 0,
        delFlag: "0"
      };
      this.form.bizContractFileList.push(row);
    },
    
    handleDeleteFile(index) {
      this.form.bizContractFileList.splice(index, 1);
    },
    
    handleUpload(row) {
      this.$modal.msgWarning("演示模式：模拟上传成功");
      row.fileName = "附件_" + new Date().getTime() + ".pdf";
      row.filePath = "/profile/upload/test.pdf";
      row.fileSize = 512;
    },
    
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const loading = this.$modal.loading("正在提交数据...");
          
          const apiCall = this.form.contractId ? updateContract(this.form) : addContract(this.form);
          
          apiCall.then(response => {
            this.$modal.msgSuccess(this.form.contractId ? "修改成功" : "新增成功");
            this.open = false;
            this.$emit("refresh"); // 通知父组件刷新列表
          }).finally(() => {
            loading.close();
          });
        }
      });
    },
    
    cancel() {
      this.open = false;
    },
    
    handleClose() {
      if (this.$refs.form) {
        this.$refs.form.clearValidate();
      }
    },
    
    // 简单的树形数据处理
    handleTree(data, key) {
      if (!Array.isArray(data)) return [];
      return data.map(item => {
        const node = { ...item };
        if (node.children && node.children.length) {
          node.children = this.handleTree(node.children, key);
        }
        return node;
      });
    }
  }
};
</script>

<style scoped>
</style>