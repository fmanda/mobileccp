package com.ts.mobileccp.db.entity
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert


@Entity(tableName = "arinv")
data class ARInv(
    @PrimaryKey val invno: String,
    val invdate: String,
    val amount: Double?,
    val settle: Double?,
    val remain: Double?,
    val shipname: String?,
    val partnername: String?,
    val shipid: Int?,
    val salid: String?
)

@Dao
interface ARInvDao {
    @Upsert
    suspend fun insertARInv(arinvs: List<ARInv>)

    @Query("DELETE FROM arinv")
    suspend fun deleteAll()

    @Query("SELECT count(*) FROM arinv")
    fun getARInvCount(): LiveData<Int?>

    @Transaction
    suspend fun updateAll(arinvs: List<ARInv>) {
        deleteAll()
        insertARInv(arinvs)
    }

    @Query("SELECT sum(remain) FROM arinv")
    fun getRemain(): LiveData<Double?>

    @Query("SELECT a.* FROM arinv a inner join customer b on a.shipid = b.shipid where b.partnerid = :customerid")
    fun getARInvByCust(customerid: Int): LiveData<List<ARInv>?>

    @Query("SELECT * FROM arinv")
    fun getARInvList(): LiveData<List<ARInv>?>

    @Query("SELECT * FROM arinv where (shipname like :filter or partnername like :filter)")
    fun searchARInvList(filter: String): LiveData<List<ARInv>?>

}

