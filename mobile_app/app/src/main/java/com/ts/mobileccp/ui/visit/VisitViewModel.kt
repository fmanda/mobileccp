package com.ts.mobileccp.ui.visit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class VisitViewModel(application: Application) : AndroidViewModel(application) {
    val test = MutableLiveData<String>().apply { value = "test" }

}

class VisitViewModelFactory(private val application: Application): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitViewModel::class.java)){
            return VisitViewModel(application) as T
        }
        throw IllegalArgumentException("Invalid Model Class")
    }
}
