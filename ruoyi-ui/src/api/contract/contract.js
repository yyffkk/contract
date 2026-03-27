import request from '@/utils/request'
import { getToken } from '@/utils/auth'

// 查询智能合同审批列表
export function listContractContent(query) {
  return request({
    url: '/contract/contractContent/list',
    method: 'get',
    params: query
  })
}

// 查询智能合同审批详细
export function getContractContent(id) {
  return request({
    url: '/contract/contractContent/' + id,
    method: 'get'
  })
}

// 新增智能合同审批
export function addContractContent(data) {
  return request({
    url: '/contract/contractContent',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': undefined // 👈 关键！让浏览器自动设 multipart/form-data
    }
  })
}

// 批量导入合同（接收 Excel 文件）
export function importContractBatch(data) {
  return request({
    url: '/contract/contractContent/import',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    timeout: 60000 // 防止大文件超时
  });
}

// 修改智能合同审批
export function updateContractContent(data) {
  return request({
    url: '/contract/contractContent',
    method: 'put',
    data: data
  })
}

// 详情页上传合同正文/附件
export async function uploadContractDetailFiles(data) {
  const response = await fetch(process.env.VUE_APP_BASE_API + '/contract/contractContent/detail-files', {
    method: 'POST',
    headers: {
      Authorization: getToken() ? 'Bearer ' + getToken() : undefined
    },
    body: data
  })

  const result = await response.json()

  if (!response.ok) {
    throw new Error(result.msg || ('系统接口' + response.status + '异常'))
  }

  if ((result.code || 200) !== 200) {
    throw new Error(result.msg || '上传失败')
  }

  return result
}

// 删除智能合同审批
export function delContractContent(id) {
  return request({
    url: '/contract/contractContent/' + id,
    method: 'delete'
  })
}

// 查询用印审批单列表
export function listContractContent2(query) {
  return request({
    url: '/contract/contractContent2/list',
    method: 'get',
    params: query
  })
}

// 查询用印审批单详细
export function getContractContent2(id) {
  return request({
    url: '/contract/contractContent2/' + id,
    method: 'get'
  })
}

// 新增用印审批单
export function addContractContent2(data) {
  return request({
    url: '/contract/contractContent2',
    method: 'post',
    data: data,
    headers: {
      'Content-Type': undefined // 👈 关键！让浏览器自动设 multipart/form-data
    }
  })
}

// 修改用印审批单
export function updateContractContent2(data) {
  return request({
    url: '/contract/contractContent2',
    method: 'put',
    data: data
  })
}

// 删除用印审批单
export function delContractContent2(id) {
  return request({
    url: '/contract/contractContent2/' + id,
    method: 'delete'
  })
}

// 简化版审批接口：只传 id + action
export function approveContract(data) {
  return request({
    url: '/contract/contractContent/approve/simple', // 👈 改这里
    method: 'post',
    data // { id: 123, action: 'agree' }
  });
}

// 合同签署接口
export function signContract(data) {
  return request({
    url: '/contract/contractContent/sign',
    method: 'post',
    data // { contractId: 123, autoArchive: true/false }
  })
}

// 归档合同
export function archiveContract(data) {
  return request({
    url: '/contract/contractContent/archive',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

// 完结合同
export function finishContract(data) {
  return request({
    url: '/contract/contractContent/finish',
    method: 'post',
    data
  })
}

// 查询合同操作日志
export function listContractOperateLogs(contractId) {
  return request({
    url: `/contract/contractContent/${contractId}/logs`,
    method: 'get'
  })
}

// 新增合同操作日志
export function addContractOperateLog(data) {
  return request({
    url: '/contract/contractContent/log',
    method: 'post',
    data
  })
}