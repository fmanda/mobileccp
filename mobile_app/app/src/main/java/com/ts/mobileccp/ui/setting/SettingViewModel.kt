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
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.LoginInfo
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.InventoryDao
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.global.AppVariable
import com.ts.mobileccp.rest.ApiRepository
import com.ts.mobileccp.rest.CustomerResponse
import com.ts.mobileccp.ui.login.LoginActivity
import kotlinx.coroutines.launch


class SettingViewModel(application: Application) : AndroidViewModel(application) {
    @SuppressLint("StaticFieldLeak")
    private val context: Context = getApplication<Application>().applicationContext

    private val repository = ApiRepository(application)

    private val _data = MutableLiveData<List<CustomerResponse>?>()
    val data: LiveData<List<CustomerResponse>?> get() = _data

    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(application).loginInfoDao()
    private val inventoryDao: InventoryDao = AppDatabase.getInstance(application).productDao()
    private val customerDao: CustomerDao = AppDatabase.getInstance(application).customerDao()
    private val salesOrderDao: SalesOrderDao = AppDatabase.getInstance(application).salesOrderDao()

    fun logOut(){
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
            null
        )
        viewModelScope.launch {
            try {
                loginInfoDao.clearLoginInfo();
                inventoryDao.clearProduct();
                customerDao.clearCustomer();
                salesOrderDao.clearSalesOrder();
                navigateToLoginScreen()

            } catch (_: Exception) {

            }
        }

    }

    private fun navigateToLoginScreen() {
        // Assuming you're using the Navigation Component
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        context.startActivity(intent)
    }


//    private val customerDao: CustomerDao = AppDatabase.getInstance(application).customerDao()
//    private val productDao: ProductDao = AppDatabase.getInstance(application).productDao()

    fun getListCustomerRest() {
        viewModelScope.launch {
            try {
                val result = repository.fetchCustomer()
                _data.postValue(result)
            } catch (e: Exception) {
                _data.postValue(null) // Handle error
            }
        }
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