import request from '@/utils/request'

// 查询项目合同关联列表
export function listContract(query) {
  return request({
    url: '/pm/contract/list',
    method: 'get',
    params: query
  })
}

// 查询项目合同关联详细
export function getContract(id) {
  return request({
    url: '/pm/contract/' + id,
    method: 'get'
  })
}

// 新增项目合同关联
export function addContract(data) {
  return request({
    url: '/pm/contract',
    method: 'post',
    data: data
  })
}

// 修改项目合同关联
export function updateContract(data) {
  return request({
    url: '/pm/contract',
    method: 'put',
    data: data
  })
}

// 删除项目合同关联
export function delContract(id) {
  return request({
    url: '/pm/contract/' + id,
    method: 'delete'
  })
}
