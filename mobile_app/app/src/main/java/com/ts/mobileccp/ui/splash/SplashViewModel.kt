package com.ts.mobileccp.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.global.AppVariable


class SplashViewModel(application: Application) : AndroidViewModel(application) {
    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(application).loginInfoDao()

    fun checkLogin() : Boolean{
        val loginInfo = loginInfoDao.getLoginInfo()

        if (loginInfo != null){
            if (loginInfo.salid.toString().isNotEmpty()){
                AppVariable.loginInfo = loginInfo
            }
            return true
        }
        return false
    }

}


class SplashViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
