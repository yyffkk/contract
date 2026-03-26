import request from '@/utils/request'

// 查询合同分类列表
export function listContract(query) {
  return request({
    url: '/contract/contract/list',
    method: 'get',
    params: query
  })
}

// 查询合同分类详细
export function getContract(categoryId) {
  return request({
    url: '/contract/contract/' + categoryId,
    method: 'get'
  })
}

// 新增合同分类
export function addContract(data) {
  return request({
    url: '/contract/contract',
    method: 'post',
    data: data
  })
}

// 修改合同分类
export function updateContract(data) {
  return request({
    url: '/contract/contract',
    method: 'put',
    data: data
  })
}

// 删除合同分类
export function delContract(categoryId) {
  return request({
    url: '/contract/contract/' + categoryId,
    method: 'delete'
  })
}
