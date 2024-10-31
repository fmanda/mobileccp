package com.ts.mobileccp.ui.customer
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.CustomerDelivery
import kotlinx.coroutines.launch


class CustomerViewModel(application: Application) : AndroidViewModel(application) {

    private val customerDao: CustomerDao = AppDatabase.getInstance(application).customerDao()

    //prefer
    val listcustomer: LiveData<List<CustomerDelivery>> = customerDao.getListCustomerDelivery()



    fun searchCustomer(query: String, kelurahan: String): LiveData<List<CustomerDelivery>> {
        return customerDao.searchCustomerDelivery("%$query%", "%$kelurahan%")
    }


    fun insert(example: CustomerDelivery) = viewModelScope.launch {
        customerDao.insert(example)
    }

    fun loadJenjang(): LiveData<List<String>> {
        return customerDao.getListJenjang()
    }
}

//fix error : Cannot create an instance of class com.fma.mobility.ui.customer.CustomerViewModel
//This factory class creates instances of UserViewModel by passing the required Application parameter to the ViewModel's constructor
class CustomerViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CustomerViewModel::class.java)) {
            return CustomerViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}