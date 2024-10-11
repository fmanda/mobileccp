import request from '@/utils/rest'


export function getSalesOrderWeek() {
  return request({
    url: 'salesdashboardweek',
    method: 'get'
  })
}

export function getSalesOrderMonth() {
  return request({
    url: 'salesdashboardmonth',
    method: 'get'
  })
}

export function getSalesOrderSalesman() {
  return request({
    url: 'salesdashboardsalesman',
    method: 'get'
  })
}
