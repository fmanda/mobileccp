package com.ts.mobileccp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ts.mobileccp.db.entity.Customer
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.LoginInfo
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.Product
import com.ts.mobileccp.db.entity.ProductDao
import com.ts.mobileccp.db.entity.SalesOrder
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.SalesOrderItem
import com.ts.mobileccp.db.entity.User
import com.ts.mobileccp.db.entity.UserDao
import com.ts.mobileccp.db.entity.Visit

//import androidx.room.TypeConverters

@Database(
    entities = [
        Customer::class,
        User::class,
        Product::class,
        SalesOrder::class,
        SalesOrderItem::class,
        LoginInfo::class,
        Visit::class
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customerDao(): CustomerDao
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun salesOrderDao(): SalesOrderDao
    abstract fun loginInfoDao(): LoginInfoDao

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


//
//
//object DBProvider {
//    private var instance: AppDatabase? = null
//
//    fun getDatabase(context: Context): AppDatabase {
//        return instance ?: synchronized(this) {
//            val newInstance = Room.databaseBuilder(
//                context.applicationContext,
//                AppDatabase::class.java,
//                "mobility_db"
//            ).build()
//            instance = newInstance
//            newInstance
//        }
//    }
//}