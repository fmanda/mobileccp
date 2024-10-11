import request from '@/utils/rest'
import { getProjectCode } from '@/utils/auth'

export function getProduct(id) {
  return request({
    url: 'product/' + id.toString(),
    method: 'get'
  })
}

export function getListProduct(filtertxt) {
  let _url = 'inventoryall';
  if (filtertxt && filtertxt != '') _url = _url  + '/' + filtertxt;

  return request({
    url: _url,
    method: 'get'
  })
}
