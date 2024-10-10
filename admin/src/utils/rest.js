import axios from 'axios'
import { Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'

const rest = axios.create({
  baseURL: process.env.VUE_APP_BASE_URL,
  timeout: 5000 // request timeout
})

// request interceptor
rest.interceptors.request.use(
  config => {
    // console.log(rest);
    // do something before request is sent
    if (store.getters.token) {
      config.headers['Authorization'] = getToken()
    }
    // console.log(config);
    if (config.method === 'post') {
      // config.headers['Content-Type'] = 'multipart/form-data'
      config.headers['Content-Type'] = 'undefined'
      // 2020-7-19 : with CORS site, post aplication/json will make twice post (before options).. fuck
      // console.log('post bro');
    }

    return config
  },
  error => {
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
rest.interceptors.response.use(
  response => {
    return response
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

export default rest
