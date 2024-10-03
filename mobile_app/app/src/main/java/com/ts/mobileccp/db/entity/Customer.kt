package com.ts.mobileccp.db.entity
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert


@Entity(tableName = "customer")
data class Customer(
    @PrimaryKey val shipid: Int,
    val shipname: String,
    val shipaddress: String?,
    val shipcity: String?,
    val shipphone: String?,
    val shiphp: String?,
    val partnerid: Int,
    val partnername: String,
    val pricelevel: Int?,
    val areano: String?,
    val areaname: String?,
    val npsn: String?,
    val jenjang: String?
)

@Dao
interface CustomerDao {
    @Insert
    suspend fun insert(customer: Customer)

    @Upsert
    suspend fun insertList(customer: List<Customer>)

    @Upsert
    suspend fun upsert(customer: Customer)

    @Query("SELECT * FROM customer WHERE shipid = :id")
    suspend fun getById(id: Int): Customer?

    @Query("SELECT * FROM customer")
    fun getListCustomer(): LiveData<List<Customer>>  //autosync / reactive / async

    @Query("SELECT * FROM customer")
    fun getCustomers(): List<Customer>

    @Query("SELECT * FROM customer WHERE (shipname LIKE :query or partnername LIKE :query) and jenjang LIKE :jenjang COLLATE NOCASE")
    fun searchCustomer(query: String, jenjang: String): LiveData<List<Customer>>


    @Query("SELECT COUNT(*) FROM customer")
    fun getRowCount(): LiveData<Int?>

    @Query("DELETE FROM customer")
    suspend fun clearCustomer()

    @Query("select ' All Jenjang ' union all select distinct jenjang from customer where jenjang <>'' order by 1 ")
    fun getListJenjang(): LiveData<List<String>>
}

