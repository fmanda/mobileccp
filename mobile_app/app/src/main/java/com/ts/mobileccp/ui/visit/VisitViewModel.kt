package com.ts.mobileccp.ui.visit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.CCPMark

class VisitViewModel(application: Application) : AndroidViewModel(application) {
    private val ccpMarkDao = AppDatabase.getInstance(application).ccpMarkDAO()
    val listCCPMark = ccpMarkDao.getListCCPMark()
    var listCCPSch = ccpMarkDao.getListCCPSCH()

}

class VisitViewModelFactory(private val application: Application): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitViewModel::class.java)){
            return VisitViewModel(application) as T
        }
        throw IllegalArgumentException("Invalid Model Class")
    }
}
