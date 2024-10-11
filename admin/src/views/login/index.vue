<template>
  <div class="login-container">
    <!-- <div class="header">
      <img src="@/assets/logo/ts_100.png" alt="TS">
    </div> -->
    <div class="login">
      <el-card>
        <div class="header" >
          <img src="@/assets/logo/ts_100.png" alt="TS" >
          <h2>Mobility Webmin</h2>
        </div>

        <el-form
          ref="loginForm"
          class="login-form"
          :model="loginForm"
          :rules="loginRules"
          @submit.native.prevent="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              ref="username"
              v-model="loginForm.username"
              placeholder="Username"
              name="username"
              type="text"
              tabindex="1"
              auto-complete="on"
              prefix-icon="el-icon-user"
            />
          </el-form-item>
          <el-form-item prop="password">
            <el-input
              :key="passwordType"
              ref="password"
              v-model="loginForm.password"
              :type="passwordType"
              placeholder="Password"
              name="password"
              tabindex="2"
              auto-complete="on"
              @keyup.enter.native="handleLogin"
              prefix-icon="el-icon-lock"
            >
            </el-input>
            <!-- <span class="show-pwd" @click="showPwd">
              <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
            </span> -->
          </el-form-item>
          <el-form-item>
            <el-button
              :loading="loading"
              class="login-button"
              type="primary"
              native-type="handleLogin"
              block
            >Login</el-button>
          </el-form-item>
          <a class="forgot-password" href="">Forgot password?</a>
        </el-form>
      </el-card>



        <!-- <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form" auto-complete="on" label-position="left">
          <div class="title-container">
            <h3 class="title">Silahkan Login</h3>
          </div>

          <el-form-item prop="username">
            <span class="svg-container">
              <svg-icon icon-class="user" />
            </span>
            <el-input
              ref="username"
              v-model="loginForm.username"
              placeholder="Username"
              name="username"
              type="text"
              tabindex="1"
              auto-complete="on"
            />
          </el-form-item>

          <el-form-item prop="password">
            <span class="svg-container">
              <svg-icon icon-class="password" />
            </span>
            <el-input
              :key="passwordType"
              ref="password"
              v-model="loginForm.password"
              :type="passwordType"
              placeholder="Password"
              name="password"
              tabindex="2"
              auto-complete="on"
              @keyup.enter.native="handleLogin"
            />
            <span class="show-pwd" @click="showPwd">
              <svg-icon :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" />
            </span>
          </el-form-item>
          <el-button :loading="loading" type="primary" style="width:100%;margin-bottom:30px;" @click.native.prevent="handleLogin">Login</el-button>
        </el-form> -->

    </div>
  </div>
</template>

<script>
import { Message } from 'element-ui'
// import { validUsername } from '@/utils/validate'

export default {
  name: 'Login',
  data() {
    const validateUsername = (rule, value, callback) => {
      // if (!validUsername(value)) {
      //   callback(new Error('Please enter the correct user name'))
      // } else {
      callback()
      // }
    }
    const validatePassword = (rule, value, callback) => {
      // if (value.length < 6) {
      //   callback(new Error('The password can not be less than 6 digits'))
      // } else {
      callback()
      // }
    }
    return {
      loginForm: {
        username: '0219890035',
        password: '0219890035'
      },
      loginRules: {
        username: [{ required: true, trigger: 'blur', validator: validateUsername }],
        password: [{ required: true, trigger: 'blur', validator: validatePassword }]
      },
      loading: false,
      passwordType: 'password',
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  methods: {
    showPwd() {
      if (this.passwordType === 'password') {
        this.passwordType = ''
      } else {
        this.passwordType = 'password'
      }
      this.$nextTick(() => {
        this.$refs.password.focus()
      })
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          this.$store.dispatch('user/login', this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || '/' })
            this.loading = false
          }).catch(() => {
            Message({
              message: 'Gagal Login',
              type: 'error',
              duration: 5 * 1000
            })
            this.loading = false
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>



<style scoped lang="scss">
  $bg:#304156;
  $light_gray:#fff;
  $cursor: #fff;

  .login-container {
    font-family: Roboto, sans-serif;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
    // text-align: center;
    color: #2c3e50;
    margin: 0;
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    background-color: $bg;
  }
  body {
    margin: 0;
    padding: 0;
    background: #102a43;
    background-image: url("https://uploads.codesandbox.io/uploads/user/c3fb8e8a-35ea-4232-b5d6-0f3c5373510b/LVs7-dots.png");
    background-size: contain;
  }
  .footer,
  .header {
    padding: 20px 20px;
    color: #f0f4f8;
    display: flex;
    flex-direction: column;
    align-items: center;
    h1,
    h2,
    h3 {
      color: #102a43;
      padding: 0;
      margin: 20px;
    }
    .links {
      display: flex;
      font-family: "Open Sans";
      span {
        padding: 0 10px;
        font-size: 18px;
        border-right: 1px solid #9fb3c8;
        &:last-child {
          border-right: none;
        }
      }
      text-align: center;
    }
    .version {
      font-family: "Open Sans";
      padding: 0 10px;
      color: #9fb3c8;
      font-size: 12px;
      margin-top: 5px;
    }
  }
  .header {
    padding: 10px 20px;
    .logo {
      font-family: "Open Sans";
      letter-spacing: 3px;
      padding-top: 15px;
      padding-bottom: 15px;
    }
    .logo .part-2 {
      color: #d64545;
    }
  }
  .login {
    flex: 1;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .login-button {
    width: 100%;
    margin-top: 40px;
  }
  .login-form {
    width: 290px;
  }
  .forgot-password {
    margin-top: 10px;
  }
  $teal: rgb(0, 124, 137);
  .el-button--primary {
    background: $teal;
    border-color: $teal;

    &:hover,
    &.active,
    &:focus {
      background: lighten($teal, 7);
      border-color: lighten($teal, 7);
    }
  }
  .login .el-input__inner:hover {
    border-color: $teal;
  }
  .login .el-input__prefix {
    background: rgb(238, 237, 234);
    left: 0;
    height: calc(100% - 2px);
    left: 1px;
    top: 1px;
    border-radius: 3px;
    .el-input__icon {
      width: 30px;
    }
  }
  .login .el-input input {
    padding-left: 35px;
  }
  .login .el-card {
    padding-top: 0;
    padding-bottom: 30px;
  }
  h2 {
    font-family: "Open Sans";
    letter-spacing: 1px;
    font-family: Roboto, sans-serif;
    padding-bottom: 20px;
  }
  a {
    color: $teal;
    text-decoration: none;
    &:hover,
    &:active,
    &:focus {
      color: lighten($teal, 7);
    }
  }
  .login .el-card {
    width: 340px;
    display: flex;
    justify-content: center;
  }
</style>
