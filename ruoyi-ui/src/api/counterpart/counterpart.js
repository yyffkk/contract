import request from '@/utils/request'

// 查询相对方信息列表
export function listCounterpart(query) {
  return request({
    url: '/counterpart/counterpart/list',
    method: 'get',
    params: query
  })
}

// 查询相对方信息详细
export function getCounterpart(id) {
  return request({
    url: '/counterpart/counterpart/' + id,
    method: 'get'
  })
}

// 新增相对方信息
export function addCounterpart(data) {
  return request({
    url: '/counterpart/counterpart',
    method: 'post',
    data: data
  })
}

// 修改相对方信息
export function updateCounterpart(data) {
  return request({
    url: '/counterpart/counterpart',
    method: 'put',
    data: data
  })
}

// 删除相对方信息
export function delCounterpart(id) {
  return request({
    url: '/counterpart/counterpart/' + id,
    method: 'delete'
  })
}

// 👇👇👇 新增：导入数据 👇👇👇
export function importCounterpart(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request({
    url: '/counterpart/counterpart/importData',
    method: 'post',
    headers: { 'Content-Type': 'multipart/form-data' },
    data: formData
  })
}

// 👇👇👇 新增：下载模板 👇👇👇
export function downloadTemplate() {
  return request({
    url: '/counterpart/counterpart/downloadTemplate',
    method: 'get',
    responseType: 'blob' // 关键！用于下载文件
  })
}
