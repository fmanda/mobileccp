package com.ts.mobileccp.ui.inventory

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.Inventory
import com.ts.mobileccp.db.entity.InventoryLookup

class InventoryViewModel(application: Application) : AndroidViewModel(application) {
    private val inventoryDao = AppDatabase.getInstance(application).inventoryDao()
    fun loadMerk(): LiveData<List<String>> {
        return inventoryDao.getListMerk()
    }

    val priceLevels = inventoryDao.getPriceLevels()

    fun lookupInventory(query: String, category: String): LiveData<List<Inventory>> {
        return inventoryDao.lookupInventory("%$query%", "%$category%")
    }
}


class InventoryViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            return InventoryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}