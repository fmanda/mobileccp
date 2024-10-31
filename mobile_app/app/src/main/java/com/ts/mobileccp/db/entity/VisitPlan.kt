package com.ts.mobileccp.db.entity
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
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "visitplan")
data class VisitPlan(
    @PrimaryKey val id: UUID,
    val notr: String,
    val datetr: String,
    val salid: String,
    val dabin: String,
    val entity: String,
    val status: Int,
    val uploaded: Int = 0
)

@Entity(
    tableName = "visitplanitem",
    foreignKeys = [ForeignKey(
        entity = VisitPlan::class,
        parentColumns = ["id"],
        childColumns = ["visitplan_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("visitplan_id")
    ]
)
data class VisitPlanItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val visitplan_id: UUID,
    val partnerid: Int,
    val planmark_id: Int
)


data class VisitPlanWithItem(
    @Embedded val visitplan: VisitPlan,

    @Relation(
        parentColumn = "id",
        entityColumn = "visitplan_id"
    )
    val items: List<VisitPlanItem>
)

@Serializable
data class JSONVisitPlan(
    val id: String,
    val notr: String,
    val datetr: String,
    val salid: String,
    val dabin: String,
    val entity: String,
    val status: Int,
    val items: List<JSONVisitPlanItem>
)


@Serializable
data class JSONVisitPlanItem(
    val visitplan_id: String,
    val partnerid: Int,
    val planmark_id: Int
)


data class LastVisitPlan(
    val id: UUID,
    val datetr: String,
    val partnerid: Int,
    val partnername: String,
    val partneraddress: String,
)


@Dao
interface VisitPlanDao {
    @Upsert
    suspend fun upsert(visitplan: VisitPlan)

    @Query("DELETE FROM visitplanitem where visitplan_id = :visitplanid")
    suspend fun deleteItems(visitplanid: UUID)

    @Insert
    suspend fun insertItems(visitplanitems: List<VisitPlanItem>)

    @Query("SELECT * FROM visitplan WHERE id = :id")
    suspend fun getById(id: UUID): VisitPlan?


    @Query("UPDATE visitplan SET uploaded = 1 WHERE uploaded = 0")
    suspend fun updateStatusUpload()

    @Query("DELETE FROM visitplan")
    suspend fun clearVisitPlan()

    @Query("SELECT COUNT(*) FROM visitplan where uploaded=0")
    fun getCountToUpload(): LiveData<Int?>

    @Query("SELECT a.id, a.datetr, b.partnerid, c.partnername, c.partneraddress " +
            "FROM visitplan a " +
            "inner join  visitplanitem b on a.id = b.visitplan_id " +
            "inner join customer c on b.partnerid = c.partnerid " +
            "where a.datetr>=:plandate " +
            "and (c.partnername like :filter ) COLLATE NOCASE")
    fun getVisitPlanByDate(plandate: String, filter:String): LiveData<List<LastVisitPlan>>

    @Query("SELECT * FROM visitplan WHERE uploaded=0")
    fun getVisitPlanForUpload(): List<VisitPlanWithItem>
}
