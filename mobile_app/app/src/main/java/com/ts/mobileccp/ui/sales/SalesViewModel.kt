package com.ts.mobileccp.ui.sales
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.LastActivityQuery
import com.ts.mobileccp.db.entity.Inventory
import com.ts.mobileccp.db.entity.InventoryDao
import com.ts.mobileccp.db.entity.InventoryLookup
import com.ts.mobileccp.db.entity.SalesOrder
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.SalesOrderItem
import com.ts.mobileccp.db.entity.TmpSalesOrder
import com.ts.mobileccp.db.entity.TmpSalesOrderItem
import com.ts.mobileccp.db.entity.Visit
import com.ts.mobileccp.rest.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID


class SalesViewModel(application: Application) : AndroidViewModel(application) {
    private val _app : Application = application
    private val inventoryDao: InventoryDao = AppDatabase.getInstance(application).inventoryDao()
    val salesOrderDao: SalesOrderDao = AppDatabase.getInstance(application).salesOrderDao()
    val customerDao: CustomerDao = AppDatabase.getInstance(application).customerDao()

    private val repository = ApiRepository(application)
    val isRestProcessing = MutableLiveData<Boolean>().apply { value = false }

    val editedSO = MutableLiveData<TmpSalesOrder>().apply {  value = TmpSalesOrder() }
    val editedSOItems = MutableLiveData<List<TmpSalesOrderItem>>().apply {  value = emptyList() }

    val products: LiveData<List<Inventory>> = inventoryDao.getListInventory()

    fun lookupProducts(pricelevel: Int, query: String, category: String): LiveData<List<InventoryLookup>> {
        return inventoryDao.lookupProducts(pricelevel,"%$query%", "%$category%")
    }

    fun loadMerk(): LiveData<List<String>> {
        return inventoryDao.getListMerk()
    }

    fun saveOrder(salesOrder: SalesOrder, soItems: List<SalesOrderItem>):Boolean{
        viewModelScope.launch(Dispatchers.IO) {
            salesOrderDao.upsert(salesOrder)
            salesOrderDao.deleteItems(salesOrder.id)
            salesOrderDao.insertItems(soItems)
        }
        return true
    }

    fun saveVisit(visit: Visit):Boolean{
        viewModelScope.launch(Dispatchers.IO) {
            salesOrderDao.insertVisit(visit)
        }
        return true
    }

    val listLatestActivities: LiveData<List<LastActivityQuery>> = salesOrderDao.getLast300Sales()

    fun searchLatestOrder(query: String): LiveData<List<LastActivityQuery>> {
        return salesOrderDao.searchLast300Sales("%$query%")
    }

    private suspend fun doSyncSalesByID(filterID: UUID) {
        repository.fetchAndPostOrdersByID(filterID)
        repository.fetchAndPostVisitByID(filterID)

        isRestProcessing.postValue(false)
        Toast.makeText(_app, "Sinkronisasi Data Berhasil", Toast.LENGTH_LONG).show()
    }

    fun syncData(filterID: UUID)= viewModelScope.launch {
        isRestProcessing.postValue(true)
        doSyncSalesByID(filterID)
    }

    fun loadTmpSalesOrder(filterID: UUID) {
        viewModelScope.launch(Dispatchers.IO) {

            val dbSO = salesOrderDao.getById(filterID)
            val dbcust = dbSO?.shipid?.let { customerDao.getById(it) }
            val dbSOItems = salesOrderDao.getByTmpItemsId(filterID)

            // Post the updates to LiveData from background thread
            editedSO.postValue(TmpSalesOrder(dbSO?.id, dbcust))
            if (dbSOItems != null) {
                editedSOItems.postValue(dbSOItems)
            }

            //error :
//            editedSO.value = TmpSalesOrder(dbSO?.id, dbcust)
//            if (dbSOItems != null) {
//                editedSOItems.value = dbSOItems
//            }
        }
    }
}

class SalesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SalesViewModel::class.java)) {
            return SalesViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}