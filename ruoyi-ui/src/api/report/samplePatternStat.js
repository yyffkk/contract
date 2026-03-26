import request from '@/utils/request'

export function listSamplePatternStats() {
  return request({
    url: '/report/samplePatternStat/list',
    method: 'get'
  })
}
