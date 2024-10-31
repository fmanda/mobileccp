package com.ts.mobileccp.ui.customer
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.CustomerDelivery
import kotlinx.coroutines.launch

//using AndroidViewModel , so we can access this damn app parameters
class CustomerUpdateViewModel(application: Application) : AndroidViewModel(application) {

    private val customerDao: CustomerDao = AppDatabase.getInstance(application).customerDao()
    fun upsert(cust: CustomerDelivery) = viewModelScope.launch {
        customerDao.upsert(cust)
    }
}

//fix error : Cannot create an instance of class com.fma.mobility.ui.customer.CustomerViewModel
//This factory class creates instances of UserViewModel by passing the required Application parameter to the ViewModel's constructor
class CustomerUpdateViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerUpdateViewModel::class.java)) {
            return CustomerUpdateViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}