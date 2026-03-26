<template>
    <div class="contract-form-container">
        <!-- 页面头部 -->
        <div class="page-header" style="text-align: center;">
            <h1 class="title">用印审批单</h1>
        </div>

        <!-- 主要表单容器 -->
        <el-card shadow="hover" style="margin: 20px 0; border-radius: 12px;">
            <el-form :model="form" ref="form" label-width="140px" size="medium" :rules="rules" style="padding: 30px;">
                <!-- 用印部门 -->
                <el-form-item label="用印部门" prop="department">
                    <el-select v-model="form.department" placeholder="请选择" style="width: 100%;">
                        <el-option label="销售部" value="sales"></el-option>
                        <el-option label="采购部" value="purchase"></el-option>
                        <el-option label="行政部" value="admin"></el-option>
                    </el-select>
                </el-form-item>

                <!-- 经办人 -->
                <el-form-item label="经办人" prop="handler">
                    <el-input v-model="form.handler" placeholder="请填写经办人" style="width: 100%;" />
                </el-form-item>

                <!-- 日期 -->
                <el-form-item label="日期" prop="date">
                    <el-date-picker v-model="form.date" type="date" placeholder="请选择" style="width: 100%;"
                        value-format="yyyy-MM-dd" format="yyyy-MM-dd" />
                </el-form-item>

                <!-- 用印文件名称 -->
                <el-form-item label="用印文件名称" prop="fileName">
                    <el-input v-model="form.fileName" placeholder="请输入" style="width: 100%;" />
                </el-form-item>

                <!-- 对方单位名称 -->
                <el-form-item label="对方单位名称" prop="opponentUnit">
                    <el-input v-model="form.opponentUnit" placeholder="请输入全称" style="width: 100%;" />
                </el-form-item>

                <!-- 文件份数 -->
                <el-form-item label="文件份数" prop="fileCount">
                    <el-input v-model.number="form.fileCount" placeholder="请填写文件份数" style="width: 100%;" />
                </el-form-item>

                <!-- 文件类别 -->
                <el-form-item label="文件类别" prop="fileCategory">
                    <el-select v-model="form.fileCategory" placeholder="请选择" style="width: 100%;">
                        <el-option label="合同" value="contract"></el-option>
                        <el-option label="协议" value="agreement"></el-option>
                        <el-option label="函件" value="letter"></el-option>
                    </el-select>
                </el-form-item>

                <!-- 印章类别 -->
                <el-form-item label="印章类别" prop="sealCategory">
                    <el-select v-model="form.sealCategory" placeholder="请选择" style="width: 100%;">
                        <el-option label="公章" value="company"></el-option>
                        <el-option label="合同章" value="contract"></el-option>
                        <el-option label="财务章" value="finance"></el-option>
                    </el-select>
                </el-form-item>

                <!-- 印章主体 -->
                <el-form-item label="印章主体" prop="sealSubject">
                    <el-select v-model="form.sealSubject" placeholder="请选择" style="width: 100%;">
                        <el-option label="公司" value="company"></el-option>
                        <el-option label="分公司" value="branch"></el-option>
                        <el-option label="项目部" value="project"></el-option>
                    </el-select>
                </el-form-item>

                <!-- 备注 -->
                <el-form-item label="备注" prop="remark">
                    <el-input v-model="form.remark" type="textarea" :rows="4" placeholder="请填写备注"
                        style="width: 100%;" />
                </el-form-item>

                <!-- 用印文件上传 -->
                <el-form-item label="用印文件" prop="files">
                    <el-upload action="" :auto-upload="false" :on-change="handleFileChange"
                        accept=".pdf,.doc,.docx,.jpg,.png" multiple drag :limit="5" :on-exceed="handleExceed"
                        :file-list="fileList" :on-remove="handleRemove" style="width: 100%;">
                        <div class="upload-zone secondary">
                            <i class="el-icon-upload" style="font-size: 32px; color: #606266;"></i>
                            <p style="margin-top: 10px; font-size: 14px; color: #606266;">+ 添加文件</p>
                            <p style="font-size: 12px; color: #909399;">支持 PDF, DOC, JPG, PNG</p>
                        </div>
                    </el-upload>
                </el-form-item>

                <!-- 签署方式 -->
                <el-form-item label="签署方式" prop="signMethod">
                    <el-select v-model="form.signMethod" placeholder="请选择" style="width: 100%;">
                        <el-option label="纸质签署" value="paper"></el-option>
                        <el-option label="电子签署" value="electronic"></el-option>
                    </el-select>
                </el-form-item>


                <!-- 文件份数（重复） -->
                <el-form-item label="文件份数" prop="fileCount2">
                    <el-input v-model.number="form.fileCount2" placeholder="请输入" style="width: 100%;" />
                </el-form-item>

                <!-- 印章类型 -->
                <el-form-item label="印章类型" prop="sealType">
                    <el-select v-model="form.sealType" placeholder="请选择" style="width: 100%;">
                        <el-option label="正式" value="official"></el-option>
                        <el-option label="临时" value="temporary"></el-option>
                    </el-select>
                </el-form-item>

                <!-- 印章名称 -->
                <el-form-item label="印章名称" prop="sealName">
                    <el-input v-model="form.sealName" placeholder="请输入" style="width: 100%;" />
                </el-form-item>

                <!-- 印章所属主体 -->
                <el-form-item label="印章所属主体" prop="sealOwner">
                    <el-input v-model="form.sealOwner" placeholder="请输入" style="width: 100%;" />
                </el-form-item>



                <!-- 流程 -->
                <div class="section-title">审批流程</div>
                <el-form-item>
                    <div style="color: #909399; font-size: 14px; margin-bottom: 16px;">
                        当前条件没有设置审批人，报告错误信息给管理员<span style="color: #409eff;">斯旭</span>
                    </div>
                </el-form-item>

                <!-- 底部按钮 -->
                <div style="text-align: right; margin-top: 30px;">
                    <el-button type="primary" @click="submitForm">提交</el-button>
                </div>
            </el-form>
        </el-card>
    </div>
</template>

<script>
import { addContractContent2 } from '@/api/contract/contract.js'; // ✅ 新增导入
export default {
    name: 'SealApproval',
    data() {
        return {
            form: {
                department: '',
                handler: '',
                date: null,
                fileName: '',
                opponentUnit: '',
                fileCount: '',
                fileCategory: '',
                sealCategory: '',
                sealSubject: '',
                remark: '',
                files: [],
                signMethod: '',
                fileCount2: '',
                sealType: '',
                sealName: '',
                sealOwner: ''

            },
            fileList: [],
            chatList: [],
            rules: {
                department: [{ required: true, message: '请选择用印部门', trigger: 'change' }],
                handler: [{ required: true, message: '请输入经办人', trigger: 'blur' }],
                date: [{ required: true, message: '请选择日期', trigger: 'change' }],
                fileName: [{ required: true, message: '请输入用印文件名称', trigger: 'blur' }],
                opponentUnit: [{ required: true, message: '请输入对方单位名称', trigger: 'blur' }],
                fileCount: [{ required: true, message: '请输入文件份数', trigger: 'blur' }],
                fileCategory: [{ required: true, message: '请选择文件类别', trigger: 'change' }],
                sealCategory: [{ required: true, message: '请选择印章类别', trigger: 'change' }],
                sealSubject: [{ required: true, message: '请选择印章主体', trigger: 'change' }],
                signMethod: [{ required: true, message: '请选择签署方式', trigger: 'change' }],
                sealType: [{ required: true, message: '请选择印章类型', trigger: 'change' }]
            }
        };
    },
    methods: {
        handleFileChange(file) {
            if (!this.fileList.some(item => item.name === file.name)) {
                this.fileList.push(file);
            }
        },
        handleRemove(file) {
            this.fileList = this.fileList.filter(item => item.name !== file.name);
        },
        handleExceed() {
            this.$message.warning('最多只能上传 5 个文件');
        },
        addChat() {
            this.$prompt('请输入聊天对象', '添加聊天', {
                confirmButtonText: '确定',
                cancelButtonText: '取消'
            }).then(({ value }) => {
                if (value && value.trim()) {
                    this.chatList.push(value.trim()); // ✅ 正确
                }
            });
        },
        removeChat(index) {
            this.chatList.splice(index, 1); // ✅ 正确
        },
        resetForm() {
            this.$refs.form.resetFields();
            this.fileList = [];
            this.chatList = []; // ✅ 正确
        },
        // submitForm 方法重写
        async submitForm() {
            this.$refs['form'].validate(async (valid) => {
                if (!valid) {
                    this.$message.error('请检查必填项！');
                    return;
                }

                const formData = new FormData();

                // 📌 文件处理：第一个文件当主文件，其余当附件
                if (this.fileList.length > 0) {
                    formData.append('file', this.fileList[0].raw); // 主文件
                    for (let i = 1; i < this.fileList.length; i++) {
                        formData.append('attachments', this.fileList[i].raw); // 附件
                    }
                }

                // 📌 表单字段（全部转字符串）
                const fields = [
                    'department', 'handler', 'date', 'fileName', 'opponentUnit',
                    'fileCount', 'fileCategory', 'sealCategory', 'sealSubject',
                    'remark', 'signMethod',  'sealType', 'sealName', 'sealOwner'
                ];

                fields.forEach(key => {
                    const val = this.form[key];
                    if (val != null && val !== '') {
                        formData.append(key, String(val));
                    }
                });

                try {
                    await addContractContent2(formData); // 👈 走 request.js
                    this.$message.success('提交成功！');
                    this.resetForm(); // 重置表单
                } catch (err) {
                    this.$message.error('提交失败：' + (err.response?.data?.msg || '网络错误'));
                    console.error('提交错误:', err);
                }
            });
        }
    }
};
</script>

<style scoped>
.contract-form-container {
    padding: 20px;
    background-color: #f9fafa;
    min-height: 100vh;
}

.page-header {
    text-align: left;
    margin-bottom: 20px;
}

.title {
    font-size: 28px;
    font-weight: 600;
    color: #303133;
    margin: 0;
}

.subtitle {
    font-size: 14px;
    color: #909399;
    margin: 10px 0 0;
}

/* 上传区域 */
.upload-zone {
    width: 100%;
    min-height: 100px;
    border: 2px dashed #ebeef5;
    border-radius: 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: #fafafa;
    cursor: pointer;
    transition: all 0.3s;
    text-align: center;
    padding: 20px;
}

.upload-zone:hover {
    border-color: #409eff;
    background-color: #f0f7ff;
}

/* 分组标题 */
.section-title {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin: 25px 0 10px;
    padding-bottom: 5px;
    border-bottom: 1px solid #ebeef5;
}

/* 表单标签 */
.el-form-item__label {
    font-weight: 500;
    color: #303133;
}

/* 输入框 */
.el-input,
.el-textarea__inner {
    border-radius: 6px;
    border: 1px solid #dcdfe6;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
}

/* 按钮 */
.el-button {
    padding: 10px 20px;
    border-radius: 6px;
    font-weight: 500;
}

.el-button--primary {
    background-color: #409eff;
    border-color: #409eff;
    color: white;
}

.el-button--primary:hover {
    background-color: #66b1ff;
}
</style>