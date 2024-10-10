import request from '@/utils/rest'

export function getDirectory() {
  return request({
    url: 'directory',
    method: 'get'
  })
}
