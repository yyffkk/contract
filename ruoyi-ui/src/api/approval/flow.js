import request from '@/utils/request'

export function getApprovalFlow(businessType) {
  return request({
    url: `/approval/flow/${businessType}`,
    method: 'get'
  })
}

export function saveApprovalFlow(data) {
  return request({
    url: '/approval/flow',
    method: 'put',
    data
  })
}
