<template>
  <el-dialog
    :title="title"
    :visible.sync="visible"
    width="90%"
    top="4vh"
    append-to-body
    @closed="clearPreview"
  >
    <div v-loading="loading" class="report-preview">
      <div class="preview-toolbar">
        <template v-if="!sample">
          <span>打印模板</span>
          <el-select v-model="templateId" placeholder="请选择模板" @change="loadTemplate">
            <el-option v-for="item in templates" :key="item.templateId" :label="item.templateName" :value="item.templateId" />
          </el-select>
        </template>
        <el-tag v-else type="warning">示例数据</el-tag>
        <span>共 {{ records.length }} 条记录，每条记录单独生成单据</span>
      </div>
      <el-alert v-if="!loading && !sample && !templates.length" title="暂无可用模板，请联系有模板管理权限的人员创建并启用模板。" type="info" :closable="false" />
      <div ref="preview" class="preview-paper" />
    </div>
    <span slot="footer">
      <el-button @click="visible = false">关闭</el-button>
      <el-button type="primary" icon="el-icon-printer" :disabled="loading || !ready" @click="print">打印</el-button>
    </span>
  </el-dialog>
</template>

<script>
import { getReportTemplate, reportTemplateOptions } from '@/api/report/template'
import { createPrintTemplate, renderPrintPreview } from '@/utils/reportPrint'

export default {
  name: 'ReportPrintPreview',
  data() {
    return {
      visible: false,
      loading: false,
      ready: false,
      sample: false,
      title: '打印预览',
      records: [],
      templates: [],
      templateId: undefined
    }
  },
  created() {
    this.requestVersion = 0
    this.printTemplate = null
  },
  beforeDestroy() {
    this.requestVersion++
    this.printTemplate = null
  },
  methods: {
    async open(reportCode, records) {
      this.clearPreview()
      this.sample = false
      this.title = '打印预览'
      this.records = records
      this.visible = true
      this.loading = true
      const version = ++this.requestVersion
      try {
        const response = await reportTemplateOptions(reportCode)
        if (version !== this.requestVersion || !this.visible) return
        this.templates = response.data
        if (this.templates.length) {
          this.templateId = this.templates[0].templateId
          await this.loadTemplate()
        }
      } finally {
        if (version === this.requestVersion) this.loading = false
      }
    },
    async openSample(templateJson, records) {
      this.clearPreview()
      this.sample = true
      this.title = '模板预览（示例数据）'
      this.records = records
      this.visible = true
      await this.$nextTick()
      this.render(templateJson)
    },
    async loadTemplate() {
      const version = ++this.requestVersion
      this.ready = false
      this.loading = true
      try {
        const response = await getReportTemplate(this.templateId)
        if (version !== this.requestVersion || !this.visible) return
        await this.$nextTick()
        this.render(response.data.templateJson)
      } finally {
        if (version === this.requestVersion) this.loading = false
      }
    },
    render(templateJson) {
      try {
        this.printTemplate = createPrintTemplate(templateJson)
        renderPrintPreview(this.printTemplate, this.records, this.$refs.preview)
        this.ready = true
      } catch (error) {
        this.$modal.msgError('模板预览失败：' + error.message)
      }
    },
    print() {
      if (!this.ready) return
      this.printTemplate.print(this.records)
    },
    clearPreview() {
      this.requestVersion++
      this.ready = false
      this.loading = false
      this.printTemplate = null
      this.templates = []
      this.templateId = undefined
      if (this.$refs.preview) this.$refs.preview.innerHTML = ''
    }
  }
}
</script>

<style scoped>
.preview-toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 16px;
}
.preview-paper {
  max-height: 65vh;
  overflow: auto;
  padding: 20px;
  background: #eef1f6;
}
.preview-paper ::v-deep .hiprint-printPaper {
  margin: 0 auto 20px;
  background: #fff;
  box-shadow: 0 2px 8px #d3d9e0;
}
</style>
