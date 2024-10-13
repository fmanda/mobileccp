package com.ts.mobileccp.ui.arinv

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.ARInv
import com.ts.mobileccp.db.entity.ARInvDao
import com.ts.mobileccp.db.entity.LastVisit

class ARInvViewModel(application: Application) : AndroidViewModel(application) {
    val arinvDao: ARInvDao = AppDatabase.getInstance(application).arInvDao()
    val arInvList = arinvDao.getARInvList()

    fun searchARList(query: String): LiveData<List<ARInv>?> {
        return arinvDao.searchARInvList("%$query%")
    }


}

class ARInvViewModelFactory(private val application: Application):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {



        if (modelClass.isAssignableFrom(ARInvViewModel::class.java)){
            return ARInvViewModel(application) as T
        }
        throw IllegalArgumentException("Invalid Type Class")
    }
}