package com.ts.mobileccp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    val bottomMenuVisible = MutableLiveData<Boolean?>()
    val selectedNavItem = MutableLiveData<Int>()
}
