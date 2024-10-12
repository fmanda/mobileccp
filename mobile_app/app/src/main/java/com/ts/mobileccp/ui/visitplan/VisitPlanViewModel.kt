package com.ts.mobileccp.ui.visitplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.LastVisitPlan
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale

class VisitPlanViewModel(application: Application) : AndroidViewModel(application) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dt = dateFormat.format(Date())
    private val visitDao = AppDatabase.getInstance(application).visitDao()

    fun getVisitPlans(filter:String):LiveData<List<LastVisitPlan>>{
        return visitDao.getVisitPlanByDate(dt, "%$filter%")
    }
}

class VisitPlanViewModelFactory(private val application: Application):ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VisitPlanViewModel::class.java))
            return VisitPlanViewModel(application) as T
        throw IllegalArgumentException("Invalid Model Class")
    }
}