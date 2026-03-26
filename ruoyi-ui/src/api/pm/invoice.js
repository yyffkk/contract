import request from '@/utils/request'

// 查询项目发票关联列表
export function listInvoice(query) {
  return request({
    url: '/pm/invoice/list',
    method: 'get',
    params: query
  })
}

// 查询项目发票关联详细
export function getInvoice(id) {
  return request({
    url: '/pm/invoice/' + id,
    method: 'get'
  })
}

// 新增项目发票关联
export function addInvoice(data) {
  return request({
    url: '/pm/invoice',
    method: 'post',
    data: data
  })
}

// 修改项目发票关联
export function updateInvoice(data) {
  return request({
    url: '/pm/invoice',
    method: 'put',
    data: data
  })
}

// 删除项目发票关联
export function delInvoice(id) {
  return request({
    url: '/pm/invoice/' + id,
    method: 'delete'
  })
}
