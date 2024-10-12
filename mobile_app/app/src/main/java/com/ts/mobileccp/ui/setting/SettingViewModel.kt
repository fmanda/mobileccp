package com.ts.mobileccp.ui.setting
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.CCPMarkDao
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.LoginInfo
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.InventoryDao
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.VisitDao
import com.ts.mobileccp.global.AppVariable
import com.ts.mobileccp.rest.ApiRepository
import com.ts.mobileccp.rest.CustomerResponse
import com.ts.mobileccp.ui.login.LoginActivity
import kotlinx.coroutines.launch


class SettingViewModel(application: Application) : AndroidViewModel(application) {

//    private val context: Context = getApplication<Application>().applicationContext
    private val _data = MutableLiveData<List<CustomerResponse>?>()
    val data: LiveData<List<CustomerResponse>?> get() = _data

    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(application).loginInfoDao()
    private val inventoryDao: InventoryDao = AppDatabase.getInstance(application).inventoryDao()
    private val customerDao: CustomerDao = AppDatabase.getInstance(application).customerDao()
    private val salesOrderDao: SalesOrderDao = AppDatabase.getInstance(application).salesOrderDao()
    private val visitDao: VisitDao = AppDatabase.getInstance(application).visitDao()
    private val ccpMarkDAO: CCPMarkDao = AppDatabase.getInstance(application).ccpMarkDAO()

    fun logOut(context: Context){
        AppVariable.loginInfo = LoginInfo(
            0,
            "",
            "",
            null,
            "",
            "",
            "",
            "",
            "",
            null,
            null
        )
        viewModelScope.launch {
            try {
                loginInfoDao.clearLoginInfo()
                inventoryDao.clearProduct()
                customerDao.clearCustomer()
                salesOrderDao.clearSalesOrder()
                visitDao.clearVisit()
                visitDao.clearVisitPlan()
                ccpMarkDAO.clearCCPMark()
                ccpMarkDAO.clearCCPSCH()

                navigateToLoginScreen(context)

            } catch (_: Exception) {

            }
        }

    }

    private fun navigateToLoginScreen(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }

}

//fix error : Cannot create an instance of class com.fma.mobility.ui.customer.CustomerViewModel
//This factory class creates instances of UserViewModel by passing the required Application parameter to the ViewModel's constructor
class SettingViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            return SettingViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}