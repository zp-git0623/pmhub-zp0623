import $ from 'jquery'
import { hiprint, defaultElementTypeProvider as DefaultElementTypeProvider, disAutoConnect } from 'vue-plugin-hiprint'

export const reportProviders = [{ code: 'manualElectricity', name: '手工电表登记' }]

const fields = [
  { field: 'recordId', title: '登记编号' },
  { field: 'recordDate', title: '登记日期' },
  { field: 'areaName', title: '区域名称' },
  { field: 'energyKwh', title: '电能用量（kWh）' },
  { field: 'remark', title: '备注' },
  { field: 'createBy', title: '登记人' },
  { field: 'createTime', title: '登记时间' }
]

export const basicElements = [
  { tid: 'defaultModule.text', title: '固定文字' },
  { tid: 'defaultModule.hline', title: '横线' },
  { tid: 'defaultModule.vline', title: '竖线' },
  { tid: 'defaultModule.rect', title: '边框' }
]

disAutoConnect()

let initialized = false

function initialize() {
  if (initialized) return
  hiprint.init({
    providers: [new DefaultElementTypeProvider(), {
      addElementTypes(manager) {
        manager.removePrintElementTypes('manualElectricity')
        manager.addPrintElementTypes('manualElectricity', [
          new hiprint.PrintElementTypeGroup('手工电表登记', fields.map(item => ({
            ...item, tid: 'manualElectricity.' + item.field, type: 'text', testData: item.title
          })))
        ])
      }
    }]
  })
  initialized = true
}

export function reportFields(reportCode) {
  return reportCode === 'manualElectricity'
    ? fields.map(item => ({ ...item, tid: reportCode + '.' + item.field }))
    : []
}

export function sampleReportData(reportCode) {
  if (reportCode !== 'manualElectricity') throw new Error('暂不支持该报表类型')
  return [{
    recordId: 1,
    recordDate: '2026-10-29',
    areaName: '一号车间（示例）',
    energyKwh: 1234.56,
    remark: '此处为示例数据，正式打印以勾选记录为准。',
    createBy: '登记员',
    createTime: '2026-10-29 09:00:00'
  }]
}

export function createPrintTemplate(templateJson, options = {}) {
  initialize()
  const template = typeof templateJson === 'string' ? JSON.parse(templateJson) : templateJson
  if (!template || !Array.isArray(template.panels) || !template.panels.length) {
    throw new Error('打印模板缺少有效页面，请重新设计并保存')
  }
  return new hiprint.PrintTemplate({ template, ...options })
}

export function buildFieldPalette(element) {
  initialize()
  hiprint.PrintElementTypeManager.buildByHtml($(element).find('[tid]'))
}

export function renderPrintPreview(template, data, container) {
  $(container).empty().append(template.getHtml(data))
}

export function defaultReportTemplate(reportCode) {
  initialize()
  if (reportCode !== 'manualElectricity') throw new Error('暂不支持该报表类型')
  const template = new hiprint.PrintTemplate()
  const panel = template.addPrintPanel({ width: 210, height: 297, paperHeader: 0, paperFooter: 820 })
  panel.addPrintText({ options: {
    top: 40, left: 50, width: 495, height: 30,
    title: '手工电表登记单', fontSize: 20, fontWeight: 'bold', textAlign: 'center'
  }})
  const positions = [
    ['recordDate', 105], ['areaName', 150], ['energyKwh', 195],
    ['remark', 240], ['createBy', 315], ['createTime', 360]
  ]
  positions.forEach(([field, top]) => {
    const definition = fields.find(item => item.field === field)
    panel.addPrintText({ options: {
      top, left: 50, width: 130, height: 24, title: definition.title + '：', fontSize: 12
    }})
    panel.addPrintText({
      printElementType: { tid: reportCode + '.' + field, title: definition.title, field, type: 'text' },
      options: { top, left: 185, width: 345, height: field === 'remark' ? 60 : 24, field, title: definition.title, fontSize: 12 }
    })
  })
  return JSON.stringify(template.getJson())
}
