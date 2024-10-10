// import { login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken, setProjectCode, setProjectName } from '@/utils/auth'
import { resetRouter } from '@/router'
import { login } from '@/api/users'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    department_id: 0,
    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif'
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_DEPARTMENT_ID: (state, department_id) => {
    state.department_id = department_id
  },
  SET_PROJECT_CODE: (state, project_code) => {
    state.project_code = project_code
  }
}

const actions = {

  login({ commit }, userInfo) {
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      login({ username: username.trim(), password: password }).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        commit('SET_NAME', data.user.username)
        // commit('SET_DEPARTMENT_ID', data.user.department_id)

        // commit('SET_PROJECT_CODE', data.user.project_code)

        setToken(data.token)
        console.log('here')
        setProjectCode(data.user.project_code)
        console.log('here2')
        setProjectName(data.user.project_name)
        console.log('here3')

        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  loginTemp({ commit }, userInfo) {
    // const { username, password } = userInfo;
    return new Promise((resolve, reject) => {
      const { data } = {
        data: {
          token: 'admin-token'
        }
      }
      commit('SET_TOKEN', data.token)
      setToken(data.token)
      resolve();
    })
  },

  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      removeToken() // must remove  token  first
      resetRouter()
      commit('RESET_STATE')
      resolve()
    })
  },

  // getInfo({ commit, state }) {
  //   return new Promise((resolve, reject) => {
  //     const { data } = {
  //       data: {
  //         roles: ['admin'],
  //         introduction: 'I am a super administrator',
  //         avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',
  //         name: 'Super Admin'
  //       }
  //     }
  //     const { name, avatar } = data
  //     commit('SET_NAME', name)
  //     commit('SET_AVATAR', avatar)
  //     resolve(data)
  //   })
  // },

  // get user info
  // getInfo({ commit, state }) {
  //   return new Promise((resolve, reject) => {
  //     getInfo(state.token).then(response => {
  //       const { data } = response
  //
  //       if (!data) {
  //         return reject('Verification failed, please Login again.')
  //       }
  //
  //       const { name, avatar } = data
  //
  //       commit('SET_NAME', name)
  //       commit('SET_AVATAR', avatar)
  //       resolve(data)
  //     }).catch(error => {
  //       reject(error)
  //     })
  //   })
  // },

  // user logout
  // logout({ commit, state }) {
  //   return new Promise((resolve, reject) => {
  //     logout(state.token).then(() => {
  //       removeToken() // must remove  token  first
  //       resetRouter()
  //       commit('RESET_STATE')
  //       resolve()
  //     }).catch(error => {
  //       reject(error)
  //     })
  //   })
  // },
  //
  // // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
