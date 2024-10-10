import Cookies from 'js-cookie'

const TokenKey = 'token'

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}


export function setProjectCode(project_code) {
  return Cookies.set('project_code',project_code)
}

export function setProjectName(project_name) {
  return Cookies.set('project_name',project_name)
}



export function getProjectCode() {
  return Cookies.get('project_code')
}

export function getProjectName() {
  return Cookies.get('project_name')
}