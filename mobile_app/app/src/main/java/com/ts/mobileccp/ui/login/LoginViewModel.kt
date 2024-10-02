package com.ts.mobileccp.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.rest.ApiRepository
import kotlinx.coroutines.launch


class LoginViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ApiRepository(application)
    private val _isLogin = MutableLiveData<Boolean?>()
    val isLogin: LiveData<Boolean?> = _isLogin

    private val _isLoading = MutableLiveData<Boolean?>()
    val isLoading: LiveData<Boolean?> = _isLoading


    init {
        _isLoading.value = false
    }

    fun login(username:String, password:String)= viewModelScope.launch {
        _isLoading.value = true
        _isLogin.value = repository.loginSalesman(username,password)
        _isLoading.value = false
    }
}


class LoginViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
