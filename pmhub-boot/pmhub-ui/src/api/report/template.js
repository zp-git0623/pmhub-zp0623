import request from '@/utils/request'

export function listReportTemplate(params) {
  return request({ url: '/report/template/list', method: 'get', params })
}

export function getReportTemplate(templateId) {
  return request({ url: '/report/template/' + templateId, method: 'get' })
}

export function addReportTemplate(data) {
  return request({ url: '/report/template', method: 'post', data })
}

export function updateReportTemplate(data) {
  return request({ url: '/report/template', method: 'put', data })
}

export function deleteReportTemplate(templateId) {
  return request({ url: '/report/template/' + templateId, method: 'delete' })
}

export function reportTemplateOptions(reportCode) {
  return request({ url: '/report/template/options', method: 'get', params: { reportCode: reportCode }})
}
