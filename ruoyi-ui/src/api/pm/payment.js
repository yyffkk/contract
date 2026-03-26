import request from '@/utils/request'

// 查询项目账款关联列表
export function listPayment(query) {
  return request({
    url: '/pm/payment/list',
    method: 'get',
    params: query
  })
}

// 查询项目账款关联详细
export function getPayment(id) {
  return request({
    url: '/pm/payment/' + id,
    method: 'get'
  })
}

// 新增项目账款关联
export function addPayment(data) {
  return request({
    url: '/pm/payment',
    method: 'post',
    data: data
  })
}

// 修改项目账款关联
export function updatePayment(data) {
  return request({
    url: '/pm/payment',
    method: 'put',
    data: data
  })
}

// 删除项目账款关联
export function delPayment(id) {
  return request({
    url: '/pm/payment/' + id,
    method: 'delete'
  })
}
