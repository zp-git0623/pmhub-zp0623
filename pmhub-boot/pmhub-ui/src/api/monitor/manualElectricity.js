import request from '@/utils/request'

export function listManualElectricity(query) {
  return request({
    url: '/monitor/manualElectricity/list',
    method: 'get',
    params: query
  })
}

export function getManualElectricity(recordId) {
  return request({
    url: '/monitor/manualElectricity/' + recordId,
    method: 'get'
  })
}

export function addManualElectricity(data) {
  return request({
    url: '/monitor/manualElectricity',
    method: 'post',
    data: data
  })
}

export function updateManualElectricity(data) {
  return request({
    url: '/monitor/manualElectricity',
    method: 'put',
    data: data
  })
}

export function printManualElectricityData(recordIds) {
  return request({
    url: '/monitor/manualElectricity/printData',
    method: 'post',
    data: recordIds
  })
}
