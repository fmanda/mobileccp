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
import com.ts.mobileccp.db.entity.SalesOrderWithItems
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "visitroute")
data class VisitRoute(
    @PrimaryKey val id: UUID,
    val dabin: String,
    val routename: String,
    val uploaded: Int = 0
)

@Entity(
    tableName = "visitrouteitem",
    foreignKeys = [ForeignKey(
        entity = VisitPlan::class,
        parentColumns = ["id"],
        childColumns = ["visitroute_id"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [
        Index("visitroute_id")
    ]
)
data class VisitRouteItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val visitroute_id: UUID,
    val partnerid: Int
)


data class VisitRouteWithItem(
    @Embedded val visitroute: VisitRoute,

    @Relation(
        parentColumn = "id",
        entityColumn = "visitroute_id"
    )
    val items: List<VisitRouteItem>
)

@Serializable
data class JSONVisitRoute(
    val id: String,
    val dabin: String,
    val routename: String,
    val items: List<JSONVisitRouteItem>
)


@Serializable
data class JSONVisitRouteItem(
    val visitroute_id: String,
    val partnerid: Int
)


@Dao
interface VisitRouteDao {
    @Upsert
    suspend fun upsert(visitroute: VisitRoute)

    @Query("DELETE FROM visitrouteitem where visitroute_id = :visitrouteid")
    suspend fun deleteItems(visitrouteid: UUID)

    @Insert
    suspend fun insertItems(visitrouteitems: List<VisitRouteItem>)

    @Query("SELECT * FROM visitroute WHERE id = :id")
    suspend fun getById(id: UUID): VisitRoute?


    @Query("UPDATE visitroute SET uploaded = 1 WHERE uploaded = 0")
    suspend fun updateStatusUpload()

    @Query("DELETE FROM visitroute")
    suspend fun clearVisitRoute()

    @Query("SELECT COUNT(*) FROM visitroute where uploaded=0")
    fun getCountToUpload(): LiveData<Int?>

    @Query("SELECT * FROM visitroute WHERE uploaded=0")
    fun getVisitRouteForUpload(): List<VisitRouteWithItem>
}
