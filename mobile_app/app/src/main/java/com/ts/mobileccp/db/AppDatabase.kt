package com.ts.mobileccp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ts.mobileccp.db.entity.ARInv
import com.ts.mobileccp.db.entity.ARInvDao
import com.ts.mobileccp.db.entity.Customer
import com.ts.mobileccp.db.entity.VisitMark
import com.ts.mobileccp.db.entity.VisitMarkDao
import com.ts.mobileccp.db.entity.PlanMark
import com.ts.mobileccp.db.entity.CustomerDelivery
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
import com.ts.mobileccp.db.entity.VisitPlanDao
import com.ts.mobileccp.db.entity.VisitPlanItem
import com.ts.mobileccp.db.entity.VisitRoute
import com.ts.mobileccp.db.entity.VisitRouteDao
import com.ts.mobileccp.db.entity.VisitRouteItem

//import androidx.room.TypeConverters

@Database(
    entities = [
        CustomerDelivery::class,
        Inventory::class,
        PriceLevel::class,
        SalesOrder::class,
        SalesOrderItem::class,
        LoginInfo::class,
        VisitMark::class,
        PlanMark::class,
        VisitRoute::class,
        VisitRouteItem::class,
        VisitPlan::class,
        VisitPlanItem::class,
        Visit::class,
        ARInv::class,
        Setting::class
    ],
    views = [
        Customer::class
    ],
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun inventoryDao(): InventoryDao

    abstract fun loginInfoDao(): LoginInfoDao
    abstract fun arInvDao(): ARInvDao
    abstract fun settingDao(): SettingDao

    abstract fun visitMarkDao(): VisitMarkDao
    abstract fun visitRouteDao(): VisitRouteDao
    abstract fun visitPlanDao(): VisitPlanDao
    abstract fun visitDao(): VisitDao

    abstract fun salesOrderDao(): SalesOrderDao

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

