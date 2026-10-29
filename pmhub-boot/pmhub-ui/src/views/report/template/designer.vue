<template>
  <el-dialog
    :title="'模板设计 · ' + (template.templateName || '')"
    :visible.sync="visible"
    fullscreen
    append-to-body
    destroy-on-close
    custom-class="report-designer-dialog"
    :before-close="beforeClose"
    @opened="initializeDesigner"
    @closed="clearDesigner"
  >
    <div class="designer-toolbar">
      <el-button type="primary" size="small" icon="el-icon-check" :loading="saving" :disabled="!ready" @click="save">保存模板</el-button>
      <el-button size="small" icon="el-icon-view" :disabled="!ready" @click="preview">示例预览</el-button>
      <el-button-group>
        <el-button size="small" :disabled="!ready" @click="setPaper('A4')">A4</el-button>
        <el-button size="small" :disabled="!ready" @click="setPaper('A5')">A5</el-button>
        <el-button size="small" :disabled="!ready" @click="rotatePaper">旋转纸张</el-button>
      </el-button-group>
      <el-button-group>
        <el-button size="small" :disabled="!ready" @click="undo">撤销</el-button>
        <el-button size="small" :disabled="!ready" @click="redo">重做</el-button>
      </el-button-group>
      <el-select v-model="zoom" size="small" class="zoom-select" :disabled="!ready" @change="setZoom">
        <el-option v-for="value in [0.5, 0.75, 1, 1.25]" :key="value" :label="value * 100 + '%'" :value="value" />
      </el-select>
      <el-tag :type="dirty ? 'warning' : 'success'">{{ dirty ? '有未保存修改' : '已保存' }}</el-tag>
    </div>
    <div v-loading="!ready" class="designer-layout">
      <aside ref="palette" class="designer-palette">
        <h4>业务字段</h4>
        <p>拖入画布，选中后在右侧调整样式。</p>
        <div v-for="item in fields" :key="item.tid" class="field-item ep-draggable-item" :tid="item.tid">{{ item.title }}</div>
        <h4>基础元素</h4>
        <div v-for="item in basicElements" :key="item.tid" class="field-item ep-draggable-item" :tid="item.tid">{{ item.title }}</div>
      </aside>
      <main class="designer-workspace">
        <div :id="paginationId" class="designer-pagination" />
        <div :id="canvasId" class="designer-canvas" />
      </main>
      <aside class="designer-settings">
        <h4>元素属性</h4>
        <div :id="settingsId" />
      </aside>
    </div>
    <report-print-preview ref="preview" />
  </el-dialog>
</template>

<script>
import { getReportTemplate, updateReportTemplate } from '@/api/report/template'
import { basicElements, buildFieldPalette, createPrintTemplate, reportFields, sampleReportData } from '@/utils/reportPrint'
import ReportPrintPreview from '@/components/ReportPrintPreview'

export default {
  name: 'ReportTemplateDesigner',
  components: { ReportPrintPreview },
  data() {
    return {
      visible: false,
      ready: false,
      saving: false,
      dirty: false,
      zoom: 1,
      template: {},
      fields: [],
      basicElements,
      canvasId: 'report-canvas-' + this._uid,
      settingsId: 'report-settings-' + this._uid,
      paginationId: 'report-pagination-' + this._uid
    }
  },
  created() {
    this.printTemplate = null
  },
  beforeDestroy() {
    this.printTemplate = null
  },
  methods: {
    async open(templateId) {
      const response = await getReportTemplate(templateId)
      this.template = response.data
      this.fields = reportFields(this.template.reportCode)
      this.dirty = false
      this.ready = false
      this.zoom = 1
      this.visible = true
    },
    initializeDesigner() {
      try {
        this.printTemplate = createPrintTemplate(this.template.templateJson, {
          settingContainer: '#' + this.settingsId,
          paginationContainer: '#' + this.paginationId,
          dataMode: 1,
          history: true,
          onDataChanged: () => { if (this.ready) this.dirty = true }
        })
        this.printTemplate.design('#' + this.canvasId)
        buildFieldPalette(this.$refs.palette)
        this.ready = true
      } catch (error) {
        this.$modal.msgError('设计器初始化失败：' + error.message)
      }
    },
    setPaper(paper) {
      this.printTemplate.setPaper(paper)
      this.dirty = true
    },
    rotatePaper() {
      this.printTemplate.rotatePaper()
      this.dirty = true
    },
    setZoom(value) {
      this.printTemplate.zoom(value)
    },
    undo() {
      this.printTemplate.undo()
    },
    redo() {
      this.printTemplate.redo()
    },
    async save() {
      this.saving = true
      try {
        const templateJson = JSON.stringify(this.printTemplate.getJson())
        await updateReportTemplate({ ...this.template, templateJson })
        this.template.templateJson = templateJson
        this.dirty = JSON.stringify(this.printTemplate.getJson()) !== templateJson
        this.$modal.msgSuccess('模板已保存')
        this.$emit('saved')
      } finally {
        this.saving = false
      }
    },
    preview() {
      this.$refs.preview.openSample(this.printTemplate.getJson(), sampleReportData(this.template.reportCode))
    },
    beforeClose(done) {
      if (this.saving) return
      if (!this.dirty) return done()
      this.$confirm('模板修改尚未保存，确认关闭？', '提示', { type: 'warning' }).then(() => done()).catch(() => {})
    },
    clearDesigner() {
      this.ready = false
      this.printTemplate = null
    }
  }
}
</script>

<style>
.report-designer-dialog .el-dialog__body {
  padding: 10px 20px 20px;
}
.report-designer-dialog .designer-toolbar {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 14px;
}
.report-designer-dialog .zoom-select {
  width: 95px;
}
.report-designer-dialog .designer-layout {
  display: grid;
  grid-template-columns: 175px minmax(400px, 1fr) 290px;
  height: calc(100vh - 155px);
  min-height: 480px;
  border: 1px solid #dfe4ed;
}
.report-designer-dialog .designer-palette,
.report-designer-dialog .designer-settings {
  padding: 12px;
  overflow: auto;
  background: #fff;
}
.report-designer-dialog h4 {
  margin: 8px 0 16px;
}
.report-designer-dialog .designer-palette p {
  color: #909399;
  font-size: 12px;
  line-height: 1.6;
}
.report-designer-dialog .field-item {
  margin-bottom: 10px;
  padding: 10px;
  border: 1px solid #dfe4ed;
  border-radius: 4px;
  cursor: grab;
}
.report-designer-dialog .field-item:hover {
  color: #409eff;
  border-color: #409eff;
}
.report-designer-dialog .designer-workspace {
  padding: 16px;
  overflow: auto;
  background: #eef1f6;
}
.report-designer-dialog .designer-canvas {
  width: max-content;
  min-width: 100%;
}
.report-designer-dialog .hiprint-printPaper {
  background: #fff;
}
.report-designer-dialog .designer-settings input,
.report-designer-dialog .designer-settings select {
  max-width: 100%;
}
</style>
