// import axios from 'axios'
import request from '@/utils/rest'
import { getToken } from '@/utils/auth'

// const customRest = axios.create({
//   baseURL: process.env.VUE_APP_BASE_URL,
//   timeout: 5000 // request timeout
// })



export function getListVisitor(query) {
  return request({
    url: 'visitorbyname/' + query,
    method: 'get'
  })
}

export function getAllVisitor() {
  return request({
    url: 'visitor',
    method: 'get'
  })
}


export function getVisitor(visitorID) {
  return request({
    url: 'visitor/' + visitorID.toString(),
    method: 'get'
  })
}
