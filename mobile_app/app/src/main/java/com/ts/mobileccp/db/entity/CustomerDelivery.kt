package com.ts.mobileccp.db.entity
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.DatabaseView
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert


@Entity(tableName = "customerdelivery")
data class CustomerDelivery(
    @PrimaryKey val shipid: Int,
    val shipname: String,
    val shipaddress: String?,
    val shipcity: String?,
    val shipphone: String?,
    val shiphp: String?,
    val partnerid: Int,
    val partnername: String,
    val partneraddress: String,
    val pricelevel: Int?,
    val areano: String?,
    val areaname: String?,
    val npsn: String?,
    val jenjang: String?
)

@DatabaseView("SELECT DISTINCT partnerid, partnername, partneraddress FROM customerdelivery")
data class Customer(
    val partnerid: Int,
    val partnername: String,
    val partneraddress: String,
    val npsn: String?,
    val jenjang: String?
)

@Dao
interface CustomerDao {
    @Insert
    suspend fun insert(customerDelivery: CustomerDelivery)

    @Upsert
    suspend fun insertList(customerDelivery: List<CustomerDelivery>)

    @Upsert
    suspend fun upsert(customerDelivery: CustomerDelivery)

    @Query("SELECT * FROM customerdelivery WHERE shipid = :id")
    suspend fun getById(id: Int): CustomerDelivery?

    @Query("SELECT * FROM customerdelivery")
    fun getListCustomerDelivery(): LiveData<List<CustomerDelivery>>  //autosync / reactive / async


    @Query("SELECT * FROM customerdelivery WHERE (shipname LIKE :query or partnername LIKE :query) and jenjang LIKE :jenjang COLLATE NOCASE")
    fun searchCustomerDelivery(query: String, jenjang: String): LiveData<List<CustomerDelivery>>


    @Query("SELECT COUNT(*) FROM customerdelivery")
    fun getCustomerDeliveryCount(): LiveData<Int?>

    @Query("DELETE FROM customerdelivery")
    suspend fun clearCustomerDelivery()

    @Query("select ' All Jenjang ' union all select distinct jenjang from customerdelivery where jenjang <>'' order by 1 ")
    fun getListJenjang(): LiveData<List<String>>

    @Query("SELECT * FROM Customer")
    fun getPartners(): LiveData<List<Customer>>


}

