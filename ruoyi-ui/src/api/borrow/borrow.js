import request from '@/utils/request'

// 查询合同借阅列表
export function listBorrow(query) {
  return request({
    url: '/borrow/borrow/list',
    method: 'get',
    params: query
  })
}

// 查询合同借阅详细
export function getBorrow(id) {
  return request({
    url: '/borrow/borrow/' + id,
    method: 'get'
  })
}

// 新增合同借阅
export function addBorrow(data) {
  return request({
    url: '/borrow/borrow',
    method: 'post',
    data: data
  })
}

// 修改合同借阅
export function updateBorrow(data) {
  return request({
    url: '/borrow/borrow',
    method: 'put',
    data: data
  })
}

// 删除合同借阅
export function delBorrow(id) {
  return request({
    url: '/borrow/borrow/' + id,
    method: 'delete'
  })
}

// 提交借阅审批
export function submitBorrowApproval(data) {
  return request({
    url: '/borrow/borrow/submitApproval',
    method: 'post',
    data
  })
}

// 借阅审批
export function approveBorrow(data) {
  return request({
    url: '/borrow/borrow/approve',
    method: 'post',
    data
  })
}

// 登记归还
export function returnBorrow(data) {
  return request({
    url: '/borrow/borrow/return',
    method: 'post',
    data
  })
}
