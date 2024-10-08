package com.ts.mobileccp.ui.home

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.LastActivityQuery
import com.ts.mobileccp.db.entity.LastVisit
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.SalesOrderSumCount
import com.ts.mobileccp.db.entity.VisitDao
import com.ts.mobileccp.global.AppVariable
import com.ts.mobileccp.rest.ApiRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _app : Application = application
    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(application).loginInfoDao()
    private val salesOrderDao: SalesOrderDao = AppDatabase.getInstance(application).salesOrderDao()
    private val visitDao: VisitDao = AppDatabase.getInstance(application).visitDao()
    val isRestProcessing = MutableLiveData<Boolean>().apply { value = false }
    val lastUpdate = MutableLiveData<String?>().apply { postValue(AppVariable.loginInfo.last_download) }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    val listLatestVisit: LiveData<List<LastVisit>> = visitDao.getLatestVisitDashboard()

    val todaySales: LiveData<SalesOrderSumCount?> = salesOrderDao.getTodaySales()
    val weeklySales: LiveData<SalesOrderSumCount?> = salesOrderDao.getWeeklySales()
    val monthlySales: LiveData<SalesOrderSumCount?> = salesOrderDao.getMonthlySales()
    val ordersToUpload: LiveData<Int?> = salesOrderDao.getCountOrderToUpload()

    private val repository = ApiRepository(application)

    val syncStatus = MutableLiveData<Int>()  //0: default, 1:processed


    fun syncData()= viewModelScope.launch {
        isRestProcessing.postValue(true)
        doSyncAllData()
    }

    private suspend fun doSyncAllData() {
        repository.fetchAndPostOrders()
        repository.fetchAndPostVisit()
        repository.saveCustomerFromRest()
        repository.saveInventoryFromRest()

        //update last update
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
        AppVariable.loginInfo.last_download = dateFormat.format(Date())
        loginInfoDao.upsert(AppVariable.loginInfo)
        lastUpdate.postValue(AppVariable.loginInfo.last_download)


        isRestProcessing.postValue(false)
        Toast.makeText(_app, "Sinkronisasi Data Berhasil", Toast.LENGTH_LONG).show()
    }

}
class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
