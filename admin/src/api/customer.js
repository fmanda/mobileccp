import request from '@/utils/rest'
import { getProjectCode } from '@/utils/auth'

export function getCustomer(id) {
  return request({
    url: 'customer/' + id.toString(),
    method: 'get'
  })
}

export function getListCustomer(filtertxt) {
  let _url = 'searchcustomer/' + getProjectCode() ;
  if (filtertxt && filtertxt != '') _url = _url  + '/' + filtertxt;

  return request({
    url: _url,
    method: 'get'
  })
}


export function getListNewCustomer(filtertxt) {
  let _url = 'searchnewcustomer/' + getProjectCode() ;
  if (filtertxt && filtertxt != '') _url = _url  + '/' + filtertxt;

  return request({
    url: _url,
    method: 'get'
  })
}