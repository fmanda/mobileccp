import request from '@/utils/rest'
import { getProjectCode } from '@/utils/auth'

export function getCustomer(id) {
  return request({
    url: 'customer/' + id.toString(),
    method: 'get'
  })
}

export function getListCustomer(filtertxt) {
  let _url = 'customerentity/' + getProjectCode() ;
  if (filtertxt && filtertxt != '') _url = _url  + '/' + filtertxt;

  return request({
    url: _url,
    method: 'get'
  })
}
