package com.ts.mobileccp.ui.visit

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.ARInv
import com.ts.mobileccp.db.entity.ARInvDao
import com.ts.mobileccp.db.entity.Customer
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.LastVisit
import com.ts.mobileccp.db.entity.Visit
import com.ts.mobileccp.db.entity.VisitDao
import com.ts.mobileccp.rest.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class VisitViewModel(application: Application) : AndroidViewModel(application) {
    private val ccpMarkDao = AppDatabase.getInstance(application).ccpMarkDAO()
    val customerDao: CustomerDao = AppDatabase.getInstance(application).customerDao()
    val arinvDao = AppDatabase.getInstance(application).arInvDao()

    val listCCPMark = ccpMarkDao.getListCCPMark()
    var listCCPSch = ccpMarkDao.getListCCPSCH()

    private val repository = ApiRepository(application)
    val dialogSaveState = MutableLiveData<Boolean>().apply { value = false }

    val visitDao: VisitDao = AppDatabase.getInstance(application).visitDao()
    val isRestProcessing = MutableLiveData<Boolean>().apply { value = false }
    val visit = MutableLiveData<Visit>().apply { value = null }
    val customer = MutableLiveData<Customer>().apply { value = null }

    fun saveVisit(visit: Visit){
        viewModelScope.launch(Dispatchers.IO) {
            visitDao.upsertVisit(visit)
            dialogSaveState.postValue(true)
        }
    }

    fun syncData(filterID: UUID)= viewModelScope.launch {
        isRestProcessing.postValue(true)
        doSyncVisitByID(filterID)
    }

    private suspend fun doSyncVisitByID(filterID: UUID) {
        repository.fetchAndPostVisitImg(filterID)
        repository.fetchAndPostVisit(filterID)

        isRestProcessing.postValue(false)
        Toast.makeText(getApplication(), "Data Visit berhasil di upload", Toast.LENGTH_LONG).show()
    }

    val listLatestVisits: LiveData<List<LastVisit>> = visitDao.getLatestVisit()
    fun searchLatestVisits(query: String): LiveData<List<LastVisit>> {
        return visitDao.searchLastVisits("%$query%")
    }

    fun loadVisit(filterID: UUID) {
        viewModelScope.launch(Dispatchers.IO) {
            val tmp = visitDao.getById(filterID)
            tmp?.let{
                val cust = customerDao.getById(it.shipid)
                visit.postValue(tmp)
                customer.postValue(cust)
            }
        }
    }

    fun visitByCustomer(customerID:Int){
        viewModelScope.launch(Dispatchers.IO) {
            val cust = customerDao.getById(customerID)
            customer.postValue(cust)
        }
    }

    fun getARInvByCustomer(shipID:Int): LiveData<List<ARInv>?>{
        return arinvDao.getARInvByCust(shipID)
    }
}

class VisitViewModelFactory(private val application: Application): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitViewModel::class.java)){
            return VisitViewModel(application) as T
        }
        throw IllegalArgumentException("Invalid Model Class")
    }
}
