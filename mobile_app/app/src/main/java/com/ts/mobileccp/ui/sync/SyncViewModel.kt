package com.ts.mobileccp.ui.sync
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.LoginInfo
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.InventoryDao
import com.ts.mobileccp.db.entity.LastActivityQuery
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.global.AppVariable
import com.ts.mobileccp.rest.ApiRepository
import com.ts.mobileccp.rest.CustomerResponse
import com.ts.mobileccp.ui.login.LoginActivity
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class SyncViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")

    private val _app : Application = application
    private val repository = ApiRepository(application)
    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(application).loginInfoDao()
    private val salesOrderDao: SalesOrderDao = AppDatabase.getInstance(application).salesOrderDao()
    val isRestProcessing = MutableLiveData<Boolean>().apply { value = false }
    val lastUpdate = MutableLiveData<String?>().apply { postValue(AppVariable.loginInfo.last_update) }

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    private val _data = MutableLiveData<List<CustomerResponse>?>()
    val data: LiveData<List<CustomerResponse>?> get() = _data


    fun syncData()= viewModelScope.launch {
        isRestProcessing.postValue(true)
        doSyncAllData()
    }

    private suspend fun doSyncAllData() {
//        repository.fetchAndPostOrders()
//        repository.fetchAndPostVisit()
        repository.saveCustomerFromRest()
//        repository.saveInventoryFromRest()

        //update last update
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.getDefault())
        AppVariable.loginInfo.last_update = dateFormat.format(Date())
        loginInfoDao.upsert(AppVariable.loginInfo)
        lastUpdate.postValue(AppVariable.loginInfo.last_update)

        isRestProcessing.postValue(false)
        Toast.makeText(_app, "Sinkronisasi Data Berhasil", Toast.LENGTH_LONG).show()
    }


}

//fix error : Cannot create an instance of class com.fma.mobility.ui.customer.CustomerViewModel
//This factory class creates instances of UserViewModel by passing the required Application parameter to the ViewModel's constructor
class SettingViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SyncViewModel::class.java)) {
            return SyncViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}