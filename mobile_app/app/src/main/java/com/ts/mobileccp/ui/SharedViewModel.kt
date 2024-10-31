package com.ts.mobileccp.ui

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.ARInvDao
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.InventoryDao
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.VisitDao
import com.ts.mobileccp.global.AppVariable
import com.ts.mobileccp.rest.ApiRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SharedViewModel(application: Application) : AndroidViewModel(application) {
    val bottomMenuVisible = MutableLiveData<Boolean?>()
    val selectedNavItem = MutableLiveData<Int>()

    private val repository = ApiRepository(application)
    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(application).loginInfoDao()
    private val customerDao: CustomerDao = AppDatabase.getInstance(application).customerDao()
    private val inventoryDao: InventoryDao = AppDatabase.getInstance(application).inventoryDao()
    private val visitDao: VisitDao = AppDatabase.getInstance(application).visitDao()
    private val salesorderDao: SalesOrderDao = AppDatabase.getInstance(application).salesOrderDao()
    private val arInvDao: ARInvDao = AppDatabase.getInstance(application).arInvDao()

    val isDownloadProcessing = MutableLiveData<Boolean>().apply { value = false }
    val isUploadProcessing = MutableLiveData<Boolean>().apply { value = false }

    val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())

    val last_download = MutableLiveData<String?>().apply { postValue(AppVariable.loginInfo.last_download) }
    val last_upload = MutableLiveData<String?>().apply { postValue(AppVariable.loginInfo.last_upload) }

    val customerCount: LiveData<Int?> = customerDao.getCustomerDeliveryCount()
    val inventoryCount: LiveData<Int?> = inventoryDao.getRowCount()

    val visittoupload: LiveData<Int?> = visitDao.getCountToUpload()
    val salestoupload: LiveData<Int?> = salesorderDao.getCountToUpload()
    val arInvCount: LiveData<Int?> = arInvDao.getARInvCount()


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
        repository.saveVisitMarkFromRest()

        //update last update
        AppVariable.loginInfo.last_download = dateFormat.format(Date())
        loginInfoDao.upsert(AppVariable.loginInfo)
        last_download.postValue(AppVariable.loginInfo.last_download)

        isDownloadProcessing.postValue(false)
        Toast.makeText(getApplication(), "Download Data Berhasil", Toast.LENGTH_LONG).show()
    }

    private suspend fun uploadData() {
        repository.fetchAndPostVisitRoute()
        repository.fetchAndPostVisitPlan()
        repository.fetchAndPostOrders()
        repository.fetchAndPostVisitImg() //before visit updating status to 1
        repository.fetchAndPostVisit()

        repository.saveARRemainFromRest()
        repository.saveVisitRouteFromRest()
        repository.saveVisitPlanFromRest()

        AppVariable.loginInfo.last_upload = dateFormat.format(Date())
        loginInfoDao.upsert(AppVariable.loginInfo)
        last_upload.postValue(AppVariable.loginInfo.last_upload)

        isUploadProcessing.postValue(false)
        Toast.makeText(getApplication(), "Upload Data Berhasil", Toast.LENGTH_LONG).show()
    }
}
