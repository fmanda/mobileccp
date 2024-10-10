import request from '@/utils/rest'

export function test() {
  return request({
    url: '/',
    method: 'get'
  })
}
