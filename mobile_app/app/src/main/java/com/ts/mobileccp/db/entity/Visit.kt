package com.ts.mobileccp.db.entity
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.parcelize.Parcelize
import java.util.UUID
import kotlinx.serialization.Serializable



@Entity(tableName = "visit")
data class Visit(
    @PrimaryKey val id: UUID,
    val shipid: Int,
    val visitdate: String,
    val mark: Int,
    val ccpsch: Int,
    val ccptype: Int,
    val lat: Double?,
    val lng: Double?,
    val uploaded: Int,
    val img_uri: String?
)

@Entity(tableName = "visitplan", primaryKeys = ["idno", "shipid"])
data class VisitPlan(
    val idno: Int,
    val plandate: String,
    val shipid: Int
)


@Dao
interface VisitDao {

    @Query("SELECT * FROM visit WHERE id = :id")
    suspend fun getById(id: UUID): Visit?

    @Query("SELECT * FROM visit WHERE uploaded=0")
    fun getVisitForUpload(): List<Visit>

    @Query("SELECT * FROM visit WHERE id=:id")
    fun getVisitForUploadFilterID(id: UUID): List<Visit>


    @Query("SELECT * FROM visitplan order by plandate desc limit 1000")
    fun getVisitPlan(): List<VisitPlan>

    @Query("SELECT * FROM visitplan where plandate=:plandate")
    fun getVisitPlanByDate(plandate: String): List<VisitPlan>

    @Query("UPDATE visit SET uploaded = 1 WHERE uploaded = 0")
    suspend fun updateStatusUploadVisit()

    @Query("UPDATE visit SET uploaded = 1 WHERE id = :id")
    suspend fun updateStatusUploadVisitByID(id:UUID)

    @Query("DELETE FROM visit")
    suspend fun clearVisit()

    @Query("SELECT COUNT(*) FROM visit where uploaded = 0")
    fun getCountVisitToUpload(): LiveData<Int?>

    @Upsert
    suspend fun upsertVisit(visit: Visit)

    @Upsert
    suspend fun upsertListVisitPlan(visitplans: List<VisitPlan>)

    @Query(
        "SELECT a.id, a.visitdate, b.shipname , b.shipaddress, c.ccpschname as ccpsch, a.lat, a.lng, a.uploaded \n" +
        "    FROM visit a \n" +
        "    inner join customer b on a.shipid=b.shipid \n" +
        "    left join ccpsch c on a.ccpsch = c.ccpsch \n" +
        " order by visitdate desc limit 300"
    )
    fun getLatestVisit(): LiveData<List<LastVisit>>

    @Query(
        "SELECT a.id, a.visitdate, b.shipname, b.shipaddress, c.ccpschname as ccpsch, a.lat, a.lng, a.uploaded \n" +
        "    FROM visit a \n" +
        "    inner join customer b on a.shipid=b.shipid \n" +
        "    left join ccpsch c on a.ccpsch = c.ccpsch \n" +
        " order by visitdate desc limit 300"
    )
    fun getLatestVisitDashboard(): LiveData<List<LastVisit>>

    @Query(
        "SELECT a.id, a.visitdate, b.shipname , b.shipaddress, c.ccpschname as ccpsch, a.lat, a.lng, a.uploaded \n" +
        "    FROM visit a \n" +
        "    inner join customer b on a.shipid=b.shipid \n" +
        "    left join ccpsch c on a.ccpsch = c.ccpsch \n" +
        "    WHERE b.shipname LIKE :query " +
        "    or b.partnername LIKE :query " +
        " order by visitdate desc limit 300"
    )
    fun searchLastVisits(query: String): LiveData<List<LastVisit>>


    @Query("SELECT COUNT(*) FROM visit where uploaded='0'")
    fun getCountToUpload(): LiveData<Int?>
}


@Parcelize
data class LastVisit(
    var id: UUID,
    var visitdate: String? = null,
    var shipname: String? = null,
    var shipaddress: String? = null,
    var ccpsch: String? = null,
    var lat: Double?,
    var lng: Double?,
    var uploaded: Int
) : Parcelable



@Serializable
data class JSONCCP(
    val id: Int?,
    val notr: String?,
    val datetr: String?,
    val dabin: String?,
    val description: String?,
    val entity: String?,
    val salid: String?,
    val status: Int?,
    val operator: String?,
    val items: List<JSONCCPDet>?
){
    constructor(salid: String, datetr: String): this(
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null
    )
}


@Serializable
data class JSONCCPDet(
    val idno: Int?,
    val shipid: Int?,
    val ccpsch: Int?,
    val remark: String?,
    val ccptype: Int?,
    val mark: Int?,
    val createdate: String?,
    val soqty: Int?,
    val doqty: Int?,
    val retqty: Int?,
    val coll: Double?,
    val lat: Double?,
    val lng: Double?,
    val datetr: String?,
    val salid: String?, //additional
    val uid: String
)
