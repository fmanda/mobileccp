package com.ts.mobileccp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ts.mobileccp.db.entity.ARInv
import com.ts.mobileccp.db.entity.ARInvDao
import com.ts.mobileccp.db.entity.CCPMark
import com.ts.mobileccp.db.entity.CCPMarkDao
import com.ts.mobileccp.db.entity.CCPSch
import com.ts.mobileccp.db.entity.Customer
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.LoginInfo
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.Inventory
import com.ts.mobileccp.db.entity.InventoryDao
import com.ts.mobileccp.db.entity.PriceLevel
import com.ts.mobileccp.db.entity.SalesOrder
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.SalesOrderItem
import com.ts.mobileccp.db.entity.Setting
import com.ts.mobileccp.db.entity.SettingDao
import com.ts.mobileccp.db.entity.Visit
import com.ts.mobileccp.db.entity.VisitDao
import com.ts.mobileccp.db.entity.VisitPlan

//import androidx.room.TypeConverters

@Database(
    entities = [
        Customer::class,
        Inventory::class,
        PriceLevel::class,
        SalesOrder::class,
        SalesOrderItem::class,
        LoginInfo::class,
        Visit::class,
        CCPMark::class,
        CCPSch::class,
        VisitPlan::class,
        ARInv::class,
        Setting::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun visitDao(): VisitDao
    abstract fun salesOrderDao(): SalesOrderDao
    abstract fun loginInfoDao(): LoginInfoDao
    abstract fun ccpMarkDAO(): CCPMarkDao
    abstract fun arInvDao(): ARInvDao
    abstract fun settingDao(): SettingDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

