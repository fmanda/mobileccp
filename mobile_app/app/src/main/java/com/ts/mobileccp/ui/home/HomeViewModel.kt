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
import com.ts.mobileccp.db.entity.ARInvDao
import com.ts.mobileccp.db.entity.LastActivityQuery
import com.ts.mobileccp.db.entity.LastVisit
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.SalesOrderSumCount
import com.ts.mobileccp.db.entity.VisitDao
import com.ts.mobileccp.db.entity.VisitDashboard
import com.ts.mobileccp.global.AppVariable
import com.ts.mobileccp.rest.ApiRepository
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val _app : Application = getApplication()
    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(application).loginInfoDao()
    private val salesOrderDao: SalesOrderDao = AppDatabase.getInstance(application).salesOrderDao()
    private val arInvDao: ARInvDao = AppDatabase.getInstance(application).arInvDao()

    private val visitDao: VisitDao = AppDatabase.getInstance(application).visitDao()
    val isRestProcessing = MutableLiveData<Boolean>().apply { value = false }
    val lastUpdate = MutableLiveData<String?>().apply { postValue(AppVariable.loginInfo.last_download) }




    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text


    val todayVisit: LiveData<VisitDashboard?> = visitDao.getDashboardCount()
    val monthlySales: LiveData<SalesOrderSumCount?> = salesOrderDao.getMonthlySales()
    val arbalance: LiveData<Double?> = arInvDao.getRemain()


    val ordersToUpload: LiveData<Int?> = salesOrderDao.getCountOrderToUpload()

    private val repository = ApiRepository(application)

    val syncStatus = MutableLiveData<Int>()  //0: default, 1:processed

    val listLatestVisit: LiveData<List<LastVisit>> = visitDao.getLatestVisitDashboard()


}
class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
