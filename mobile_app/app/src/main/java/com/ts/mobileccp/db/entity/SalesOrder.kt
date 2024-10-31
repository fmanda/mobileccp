package com.ts.mobileccp.db.entity
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.Upsert
import kotlinx.parcelize.Parcelize
import java.util.UUID
import kotlinx.serialization.Serializable

@Entity(tableName = "salesorder")
data class SalesOrder(
    @PrimaryKey val id: UUID,
    val orderno: String,
    val orderdate: String,
    val shipid: Int,
    val salid: String,
    val areano: String,
    val dpp: Double,
    val ppn: Double,
    val amt: Double,
    val latitude: Double?,
    val longitude: Double?,
    val uploaded: Int = 0
)

@Entity(
    tableName = "salesorderitem",
    foreignKeys = [ForeignKey(
        entity = SalesOrder::class,
        parentColumns = ["id"],
        childColumns = ["salesorder_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("salesorder_id")
    ]
)
data class SalesOrderItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val salesorder_id: UUID,
    val partno: String,
    val qty: Int,
    val price: Double,
    val discount: Double,
    val dpp: Double,
    val ppn: Double,
    val amt: Double
)

data class TmpSalesOrder(
    var id: UUID?,
    var customerDelivery: CustomerDelivery?
){
    constructor(): this(
        null,
        null
    )
}

data class TmpSalesOrderItem(
    var partno: String,
    var invname: String,
    var qty: Int,
    var price: Double,
    var discount: Double,
    var dpp: Double,
    var ppn: Double,
    var amt: Double
){
    fun calcDPP(): Double{
        return this.qty * this.price
    }
    fun calcPPN(): Double{
        return this.calcDPP()*0.11
    }
    fun calcAmt(): Double{
        return this.calcDPP()+this.calcPPN()
    }
}

@Dao
interface SalesOrderDao {
    @Upsert
    suspend fun upsert(salesOrder: SalesOrder)

    @Query("DELETE FROM salesorderitem where salesorder_id = :orderid")
    suspend fun deleteItems(orderid: UUID)

    @Insert
    suspend fun insertItems(salesOrderItems: List<SalesOrderItem>)

    @Query("SELECT * FROM salesorder WHERE id = :id")
    suspend fun getById(id: UUID): SalesOrder?


    @Query("SELECT a.salesorder_id, a.partno, b.invname,  a.qty, a.price, a.discount, a.dpp, a.ppn, a.amt, 'false' as expanded " +
            " FROM salesorderitem a inner join inventory b on a.partno = b.partno WHERE salesorder_id = :id")
    suspend fun getByTmpItemsId(id: UUID): List<TmpSalesOrderItem>?

    @Query(
        "select * from (\n" +
                "    SELECT a.id, a.orderno, a.orderdate, b.shipname as customer, b.shipaddress as alamat, a.amt, a.longitude, a.latitude, a.uploaded, 'false' as isexpanded \n" +
                "    FROM salesorder a \n" +
                "    inner join customerdelivery b on a.shipid=b.shipid\n" +
//                "    union all\n" +
//                "    SELECT a.id, a.visitno, a.visitdate, b.shipname as customer, b.shipaddress, 0, a.longitude, a.latitude, a.uploaded, 'false' as isexpanded \n" +
//                "    FROM visit a \n" +
//                "    inner join customer b on a.shipid=b.shipid\n" +
                " )order by orderdate desc limit 10"
    )
    fun getLatestActivity(): LiveData<List<LastActivityQuery>>


    @Query("""SELECT SUM(amt) as sumSO, count(*) as countSO FROM salesorder where strftime('%Y-%m-%d', orderdate) = date('now', 'localtime')""")
    fun getTodaySales(): LiveData<SalesOrderSumCount?>

    @Query("""
        SELECT SUM(amt) as sumSO, count(*) as countSO  FROM salesorder
        WHERE strftime('%Y-%m-%d', orderdate) >= date('now', 'weekday 0', '-7 days', 'localtime')
        AND strftime('%Y-%m-%d', orderdate) <= date('now', 'weekday 0', '-1 day', 'localtime')
        AND strftime('%Y-%m-%d', orderdate) <= date('now', 'localtime')
    """)
    fun getWeeklySales(): LiveData<SalesOrderSumCount?>


    @Query("""
        SELECT SUM(amt) as sumSO, count(*) as countSO  FROM salesorder
        WHERE strftime('%Y-%m', orderdate) = strftime('%Y-%m', 'now')
    """)
    fun getMonthlySales(): LiveData<SalesOrderSumCount?>

    @Query(
        "SELECT a.id, a.orderno, a.orderdate, b.shipname as customer, b.shipaddress as alamat, a.amt, a.longitude, a.latitude, a.uploaded, 'false' as isexpanded " +
                "FROM salesorder a " +
                "inner join customerdelivery b on a.shipid=b.shipid order by a.orderdate desc limit 300"
    )
    fun getLast300Sales(): LiveData<List<LastActivityQuery>>

    @Query(
        "SELECT a.id, a.orderno, a.orderdate, b.shipname as customer, b.shipaddress as alamat, a.amt, a.longitude, a.latitude,  a.uploaded, 'false' as isexpanded " +
                "FROM salesorder a " +
                "inner join customerdelivery b on a.shipid=b.shipid " +
                "WHERE b.shipname LIKE :query " +
                "order by a.orderdate desc limit 300"
    )
    fun searchLast300Sales(query: String): LiveData<List<LastActivityQuery>>

    @Query("SELECT * FROM salesorder WHERE uploaded=0")
    fun getSalesOrderForUpload(): List<SalesOrderWithItems>

    @Query("SELECT * FROM salesorder WHERE id=:id")
    fun getSalesOrderForUploadFilterID(id: UUID): List<SalesOrderWithItems>


    @Query("UPDATE salesorder SET uploaded = 1 WHERE uploaded = 0")
    suspend fun updateStatusUploadSO()

    @Query("UPDATE salesorder SET uploaded = 1 WHERE id = :id")
    suspend fun updateStatusUploadSOByID(id:UUID)

    @Query("DELETE FROM salesorder")
    suspend fun clearSalesOrder()

    @Query("DELETE FROM arinv")
    suspend fun clearAR()

    @Query("SELECT COUNT(*) FROM salesorder where uploaded = 0")
    fun getCountOrderToUpload(): LiveData<Int?>

    @Query("SELECT COUNT(*) FROM salesorder where uploaded='0'")
    fun getCountToUpload(): LiveData<Int?>
}


@Parcelize
data class LastActivityQuery(
    var id: UUID,
    var orderno: String? = null,
    var orderdate: String? = null,
    var customer: String? = null,
    var alamat: String?=null,
    var amt: Double? = 0.0,
    var latitude: Double?,
    var longitude: Double?,
    var uploaded: Int,
    var isexpanded: Boolean
) : Parcelable

data class SalesOrderSumCount(
    val sumSO: Double,
    val countSO: Double
)

data class SalesOrderWithItems(
    @Embedded val order: SalesOrder,

    @Relation(
        parentColumn = "id",
        entityColumn = "salesorder_id"
    )
    val items: List<SalesOrderItem>
)

@Serializable
data class JSONSalesOrder(
    val id: String,
    val orderno: String,
    val orderdate: String,
    val shipid: Int,
    val salid: String,
    val entity: String,
    val dpp: Double,
    val ppn: Double,
    val amt: Double,
    val latitude: Double?,
    val longitude: Double?,
    val items: List<JSONSalesOrderItem>
)


@Serializable
data class JSONSalesOrderItem(
    val salesorder_id: String,
    val partno: String,
    val qty: Int,
    val price: Double,
    val discount: Double,
    val dpp: Double,
    val ppn: Double,
    val amt: Double
)

