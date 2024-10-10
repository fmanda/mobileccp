import request from '@/utils/rest'

export function getUsers() {
  return request({
    url: 'users',
    method: 'get'
  })
}

export function getUser(id) {
  return request({
    url: 'users/' + id.toString(),
    method: 'get'
  })
}

export function delUser(id) {
  return request({
    url: 'users_delete/' + id.toString(),
    method: 'get'
  })
}

export function postUser(data) {
  return request({
    url: 'users',
    method: 'post',
    data
  })
}

export function login(data) {
  return request({
    url: 'login',
    method: 'post',
    data
  })
}

// export function postUser(data) {
//   return request({
//     url: 'users',
//     method: 'post',
//     data
//   })
// }
