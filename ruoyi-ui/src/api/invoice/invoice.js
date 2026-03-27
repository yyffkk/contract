import request from '@/utils/request'

// 查询发票信息列表
export function listInvoice(query) {
  return request({
    url: '/invoice/invoice/list',
    method: 'get',
    params: query
  })
}

// 查询发票信息详细
export function getInvoice(id) {
  return request({
    url: '/invoice/invoice/' + id,
    method: 'get'
  })
}

// 新增发票信息
export function addInvoice(data) {
  return request({
    url: '/invoice/invoice',
    method: 'post',
    data: data
  })
}

// 修改发票信息
export function updateInvoice(data) {
  return request({
    url: '/invoice/invoice',
    method: 'put',
    data: data
  })
}

// 导入发票信息
export function importInvoice(data) {
  return request({
    url: '/invoice/invoice/importData',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 下载导入模板
export function importTemplate() {
  return request({
    url: '/invoice/invoice/importTemplate',
    method: 'get',
    responseType: 'blob'
  })
}

// 删除发票信息
export function delInvoice(id) {
  return request({
    url: '/invoice/invoice/' + id,
    method: 'delete'
  })
}
