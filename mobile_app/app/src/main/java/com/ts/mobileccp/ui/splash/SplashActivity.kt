package com.ts.mobileccp.ui.splash


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.ts.mobileccp.MainActivity
import com.ts.mobileccp.databinding.ActivitySplashBinding
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.SettingDao
import com.ts.mobileccp.global.AppVariable
import com.ts.mobileccp.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {

//    private lateinit var binding: ActivitySplashBinding
//    private lateinit var splashViewModel: SplashViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val factory = SplashViewModelFactory(this.application)
//        splashViewModel = ViewModelProvider(this, factory).get(SplashViewModel::class.java)
//
        var binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            delay(50)
            val isLogin = checkLogin()
            if (checkLogin()){
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            }else{
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
            }
            finish()
        }
    }

    private suspend fun checkLogin() : Boolean{
        return withContext(Dispatchers.IO) {

            val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(this@SplashActivity).loginInfoDao()
            val settingDao: SettingDao = AppDatabase.getInstance(this@SplashActivity).settingDao()

            val loginInfo = loginInfoDao.getLoginInfo()
            val setting = settingDao.getSetting()

            setting?.let{
                AppVariable.setting = setting
            }

            var isLogin = false
            if (loginInfo != null){
                if (loginInfo.salid.toString().isNotEmpty()){
                    AppVariable.loginInfo = loginInfo
                }
                isLogin = true
            }
            isLogin
        }

    }

}

