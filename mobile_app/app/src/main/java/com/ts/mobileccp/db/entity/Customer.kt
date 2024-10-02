package com.ts.mobileccp.db.entity
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.serialization.Serializable
import java.util.UUID


@Entity(tableName = "customer")
data class Customer(
    @PrimaryKey val id: UUID,
    val nama: String,
    val nik: String,
    val phone: String,
    val alamat: String,
    val kecamatan: String,
    val kelurahan: String,
    val uploaded: Int
)


@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int
)


@Dao
interface CustomerDao {
    @Insert
    suspend fun insert(customer: Customer)

    @Upsert
    suspend fun insertList(customer: List<Customer>)

    @Upsert
    suspend fun upsert(customer: Customer)

    @Query("SELECT * FROM customer WHERE id = :id")
    suspend fun getById(id: UUID): Customer?

    @Query("SELECT * FROM customer")
    fun getListCustomer(): LiveData<List<Customer>>  //autosync / reactive / async

    @Query("SELECT * FROM customer")
    fun getCustomers(): List<Customer>

    @Query("SELECT * FROM customer WHERE nama LIKE :query and kelurahan LIKE :kelurahan COLLATE NOCASE")
    fun searchCustomer(query: String, kelurahan: String): LiveData<List<Customer>>


    @Query("SELECT COUNT(*) FROM customer")
    suspend fun getRowCount(): Int

    @Query("DELETE FROM customer")
    suspend fun clearCustomer()

    @Query("select ' All Kelurahan ' union all select distinct kelurahan from customer where kelurahan <>'' order by 1 ")
    fun getListKelurahan(): LiveData<List<String>>

    @Query("SELECT * FROM customer WHERE uploaded=0")
    fun getCustomersForUpload(): List<Customer>
}


@Dao
interface UserDao {

    @Insert
    suspend fun insert(user: User)


    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users")
    suspend fun getAllUsers(): List<User>


    @Query("DELETE FROM users")
    suspend fun clearUsers()
}


@Serializable
data class JSONCustomer(
    val id: String,
    val project_code: String,
    val nama: String,
    val nik: String,
    val phone: String,
    val alamat: String,
    val kecamatan: String,
    val kelurahan: String,
    val last_updated: String,
    val is_new: Int
)

