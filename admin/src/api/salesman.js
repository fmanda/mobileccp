import request from '@/utils/rest'
import { getProjectCode } from '@/utils/auth'

export function getSalesman() {
  return request({
    url: 'salesman/' + getProjectCode() ,
    method: 'get'
  })
}
