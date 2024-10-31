package com.ts.mobileccp.db.entity
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
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
    val visitno: String,
    val visitdate: String,
    val visitmark_id: Int,
    val visitplan: String?,
    val notes: String?,
    val lat: Double?,
    val lng: Double?,
    val uploaded: Int,
    val img_uri: String?
)


@Serializable
data class JSONVisit(
    val id: String,
    val shipid: Int,
    val visitno: String,
    val visitdate: String,
    val visitmark_id: Int,
    val visitplan: String?,
    val notes: String?,
    val lat: Double?,
    val lng: Double?,
    val salid: String?
)


@Dao
interface VisitDao {

    @Query("SELECT * FROM visit WHERE id = :id")
    suspend fun getById(id: UUID): Visit?

    @Query("SELECT * FROM visit WHERE uploaded=0")
    fun getVisitForUpload(): List<Visit>

    @Query("SELECT * FROM visit WHERE id=:id")
    fun getVisitForUploadFilterID(id: UUID): List<Visit>





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


    @Query(
        "SELECT a.id, a.visitdate, b.shipname , b.shipaddress, c.markname, a.lat, a.lng, a.uploaded, 'false' as isexpanded  \n" +
        "    FROM visit a \n" +
        "    inner join customerdelivery b on a.shipid=b.shipid \n" +
        "    left join visitmark c on a.visitmark_id = c.id \n" +
        " order by visitdate desc limit 300"
    )
    fun getLatestVisit(): LiveData<List<LastVisit>>

    @Query(
        "SELECT a.id, a.visitdate, b.shipname, b.shipaddress, c.markname, a.lat, a.lng, a.uploaded,  'false' as isexpanded  \n" +
        "    FROM visit a \n" +
        "    inner join customerdelivery b on a.shipid=b.shipid \n" +
        "    left join visitmark c on a.visitmark_id = c.id \n" +
        " order by visitdate desc limit 300"
    )
    fun getLatestVisitDashboard(): LiveData<List<LastVisit>>

    @Query(
        "SELECT a.id, a.visitdate, b.shipname , b.shipaddress, c.markname as ccpsch, a.lat, a.lng, a.uploaded ,  'false' as isexpanded \n" +
        "    FROM visit a \n" +
        "    inner join customerdelivery b on a.shipid=b.shipid \n" +
        "    left join visitmark c on a.visitmark_id = c.id \n" +
        "    WHERE b.shipname LIKE :query " +
        "    or b.partnername LIKE :query " +
        " order by visitdate desc limit 300"
    )
    fun searchLastVisits(query: String): LiveData<List<LastVisit>>


    @Query("SELECT COUNT(*) FROM visit where uploaded='0'")
    fun getCountToUpload(): LiveData<Int?>

    @Query("select\n" +
            "(select count(*) from visitplan WHERE strftime('%Y-%m-%d', datetr) >= date('now', 'weekday 0', '-7 days', 'localtime')\n" +
            "        AND strftime('%Y-%m-%d', datetr) <= date('now', 'weekday 0', '-1 day', 'localtime')\n" +
            "        AND strftime('%Y-%m-%d', datetr) <= date('now', 'localtime')) as countplan, \n" +
            "(select count(*) from visit WHERE strftime('%Y-%m-%d', visitdate) >= date('now', 'weekday 0', '-7 days', 'localtime')\n" +
            "        AND strftime('%Y-%m-%d', visitdate) <= date('now', 'weekday 0', '-1 day', 'localtime')\n" +
            "        AND strftime('%Y-%m-%d', visitdate) <= date('now', 'localtime')) as countvisit")
    fun getDashboardCount(): LiveData<VisitDashboard?>
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
    var uploaded: Int,
    var isexpanded: Boolean
) : Parcelable


data class VisitDashboard(
    val countvisit: Int,
    val countplan: Int
)
