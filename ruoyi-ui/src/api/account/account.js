import request from '@/utils/request'

// 查询账款信息列表
export function listAccount(query) {
  return request({
    url: '/account/account/list',
    method: 'get',
    params: query
  })
}

// 查询账款信息详细
export function getAccount(id) {
  return request({
    url: '/account/account/' + id,
    method: 'get'
  })
}

// 新增账款信息
export function addAccount(data) {
  return request({
    url: '/account/account',
    method: 'post',
    data: data
  })
}



// 修改账款信息
export function updateAccount(data) {
  return request({
    url: '/account/account',
    method: 'put',
    data: data
  })
}

// 提交收付款审批
export function submitAccountApproval(data) {
  return request({
    url: '/account/account/submitApproval',
    method: 'post',
    data
  })
}

// 审批收付款申请
export function approveAccount(data) {
  return request({
    url: '/account/account/approve',
    method: 'post',
    data
  })
}

// 删除账款信息
export function delAccount(id) {
  return request({
    url: '/account/account/' + id,
    method: 'delete'
  })
}

// 导入账款信息
export function importAccount(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/account/account/importData',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: formData
  })
}

// 下载账款导入模板
export function downloadAccountTemplate() {
  return request({
    url: '/account/account/downloadTemplate',
    method: 'get',
    responseType: 'blob'
  })
}
