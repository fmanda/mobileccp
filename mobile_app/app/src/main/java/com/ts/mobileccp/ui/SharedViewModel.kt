package com.ts.mobileccp.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.InventoryDao
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.global.AppVariable
import com.ts.mobileccp.rest.ApiRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    private val _app : Application = application
    val bottomMenuVisible = MutableLiveData<Boolean?>()
    val selectedNavItem = MutableLiveData<Int>()

    private val repository = ApiRepository(application)
    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(application).loginInfoDao()
    private val customerDao: CustomerDao = AppDatabase.getInstance(application).customerDao()
    private val inventoryDao: InventoryDao = AppDatabase.getInstance(application).inventoryDao()
    val isDownloadProcessing = MutableLiveData<Boolean>().apply { value = false }
    val isUploadProcessing = MutableLiveData<Boolean>().apply { value = false }


    val last_download = MutableLiveData<String?>().apply { postValue(AppVariable.loginInfo.last_download) }

    val customerCount: LiveData<Int?> = customerDao.getRowCount()
    val inventoryCount: LiveData<Int?> = inventoryDao.getRowCount()


    fun syncData()= viewModelScope.launch {
        isDownloadProcessing.postValue(true)
        downloadData()
    }

    fun syncUploadData()= viewModelScope.launch {
        isUploadProcessing.postValue(true)
        uploadData()
    }

    private suspend fun downloadData() {
//        repository.fetchAndPostOrders()
//        repository.fetchAndPostVisit()
        repository.saveCustomerFromRest()
        repository.saveInventoryFromRest()
        repository.savePriceLevelFromRest()
        repository.saveCCHMarkFromRest()

        //update last update
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
        AppVariable.loginInfo.last_download = dateFormat.format(Date())
        loginInfoDao.upsert(AppVariable.loginInfo)
        last_download.postValue(AppVariable.loginInfo.last_download)

        isDownloadProcessing.postValue(false)
        Toast.makeText(_app, "Download Data Berhasil", Toast.LENGTH_LONG).show()
    }

    private suspend fun uploadData() {
        repository.fetchAndPostVisit()
        repository.fetchAndPostVisitImg()


        isUploadProcessing.postValue(false)
        Toast.makeText(_app, "Download Data Berhasil", Toast.LENGTH_LONG).show()
    }
}
