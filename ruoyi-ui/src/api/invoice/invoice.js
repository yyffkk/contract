import request from '@/utils/request'

export function listInvoice(query) {
  return request({ url: '/invoice/invoice/list', method: 'get', params: query })
}

export function getInvoice(id) {
  return request({ url: '/invoice/invoice/' + id, method: 'get' })
}

export function addInvoice(data) {
  return request({ url: '/invoice/invoice', method: 'post', data })
}

export function updateInvoice(data) {
  return request({ url: '/invoice/invoice', method: 'put', data })
}

export function submitInvoiceApproval(data) {
  return request({ url: '/invoice/invoice/submitApproval', method: 'post', data })
}

export function approveInvoice(data) {
  return request({ url: '/invoice/invoice/approve', method: 'post', data })
}

export function listInvoiceLogs(id) {
  return request({ url: '/invoice/invoice/' + id + '/logs', method: 'get' })
}

export function importInvoice(data) {
  return request({
    url: '/invoice/invoice/importData',
    method: 'post',
    data,
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

export function importTemplate() {
  return request({ url: '/invoice/invoice/importTemplate', method: 'get', responseType: 'blob' })
}

export function delInvoice(id) {
  return request({ url: '/invoice/invoice/' + id, method: 'delete' })
}
