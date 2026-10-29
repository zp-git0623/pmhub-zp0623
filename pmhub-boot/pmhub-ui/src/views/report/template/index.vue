<template>
  <div class="app-container">
    <el-form v-show="showSearch" ref="queryForm" :model="queryParams" size="small" :inline="true">
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="报表类型" prop="reportCode">
        <el-select v-model="queryParams.reportCode" placeholder="全部" clearable>
          <el-option v-for="item in providers" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="全部" clearable>
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-hasPermi="['report:template:add']" type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd">新增模板</el-button>
      </el-col>
      <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
    </el-row>
    <el-alert class="template-tip" title="模板绑定报表字段；在设计器中拖入字段、调整位置并保存，业务页面即可选用启用的模板打印。" type="info" :closable="false" />

    <el-table v-loading="loading" :data="templateList">
      <el-table-column label="模板名称" prop="templateName" min-width="180" show-overflow-tooltip />
      <el-table-column label="报表类型" min-width="160">
        <template slot-scope="scope">{{ providerName(scope.row.reportCode) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="100" align="center">
        <template slot-scope="scope"><el-tag :type="scope.row.status === '0' ? 'success' : 'info'">{{ scope.row.status === '0' ? '启用' : '停用' }}</el-tag></template>
      </el-table-column>
      <el-table-column label="备注" prop="remark" min-width="160" show-overflow-tooltip />
      <el-table-column label="创建时间" prop="createTime" width="170" />
      <el-table-column label="操作" width="280" align="center">
        <template slot-scope="scope">
          <el-button v-hasPermi="['report:template:edit']" type="text" size="mini" icon="el-icon-edit-outline" @click="handleDesign(scope.row)">设计</el-button>
          <el-button v-hasPermi="['report:template:query']" type="text" size="mini" icon="el-icon-view" @click="handlePreview(scope.row)">预览</el-button>
          <el-button v-hasPermi="['report:template:edit']" type="text" size="mini" icon="el-icon-setting" @click="handleEdit(scope.row)">设置</el-button>
          <el-button v-hasPermi="['report:template:remove']" type="text" size="mini" icon="el-icon-delete" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="form.templateId ? '模板设置' : '新增模板'" :visible.sync="formVisible" width="550px" append-to-body>
      <el-form ref="templateForm" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" maxlength="100" show-word-limit placeholder="例如：手工电表登记单" />
        </el-form-item>
        <el-form-item label="报表类型" prop="reportCode">
          <el-select v-model="form.reportCode" :disabled="!!form.templateId" style="width: 100%">
            <el-option v-for="item in providers" :key="item.code" :label="item.name" :value="item.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status"><el-radio label="0">启用</el-radio><el-radio label="1">停用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" maxlength="500" show-word-limit />
        </el-form-item>
      </el-form>
      <span slot="footer">
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="submitForm">保存</el-button>
      </span>
    </el-dialog>
    <report-template-designer ref="designer" @saved="getList" />
    <report-print-preview ref="preview" />
  </div>
</template>

<script>
import { addReportTemplate, deleteReportTemplate, getReportTemplate, listReportTemplate, updateReportTemplate } from '@/api/report/template'
import { defaultReportTemplate, reportProviders, sampleReportData } from '@/utils/reportPrint'
import ReportPrintPreview from '@/components/ReportPrintPreview'
import ReportTemplateDesigner from './designer'

export default {
  name: 'ReportTemplate',
  components: { ReportPrintPreview, ReportTemplateDesigner },
  data() {
    return {
      providers: reportProviders,
      loading: false,
      saving: false,
      showSearch: true,
      templateList: [],
      total: 0,
      queryParams: { pageNum: 1, pageSize: 10, templateName: undefined, reportCode: undefined, status: undefined },
      formVisible: false,
      form: {},
      rules: {
        templateName: [{ required: true, whitespace: true, message: '请输入模板名称', trigger: 'blur' }],
        reportCode: [{ required: true, message: '请选择报表类型', trigger: 'change' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    providerName(code) {
      const provider = this.providers.find(item => item.code === code)
      return provider ? provider.name : code
    },
    getList() {
      this.loading = true
      return listReportTemplate(this.queryParams).then(response => {
        this.templateList = response.rows
        this.total = response.total
      }).finally(() => { this.loading = false })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    handleAdd() {
      this.form = { templateName: '', reportCode: 'manualElectricity', status: '0', remark: '' }
      this.formVisible = true
      this.$nextTick(() => this.$refs.templateForm.clearValidate())
    },
    async handleEdit(row) {
      const response = await getReportTemplate(row.templateId)
      this.form = response.data
      this.formVisible = true
      this.$nextTick(() => this.$refs.templateForm.clearValidate())
    },
    handleDesign(row) {
      this.$refs.designer.open(row.templateId)
    },
    async handlePreview(row) {
      const response = await getReportTemplate(row.templateId)
      this.$refs.preview.openSample(response.data.templateJson, sampleReportData(response.data.reportCode))
    },
    submitForm() {
      this.$refs.templateForm.validate(async valid => {
        if (!valid) return
        this.saving = true
        try {
          const data = { ...this.form }
          if (!data.templateId) data.templateJson = defaultReportTemplate(data.reportCode)
          await (data.templateId ? updateReportTemplate(data) : addReportTemplate(data))
          this.$modal.msgSuccess('保存成功，可通过“设计”调整字段位置')
          this.formVisible = false
          this.getList()
        } finally {
          this.saving = false
        }
      })
    },
    handleDelete(row) {
      this.$modal.confirm('确认删除模板“' + row.templateName + '”？').then(() => {
        return deleteReportTemplate(row.templateId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
.template-tip {
  margin-bottom: 16px;
}
</style>
