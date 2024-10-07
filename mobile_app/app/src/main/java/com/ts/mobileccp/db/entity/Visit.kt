package com.ts.mobileccp.db.entity
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import java.util.UUID
import kotlinx.serialization.Serializable



@Entity(tableName = "visit")
data class Visit(
    @PrimaryKey val id: String,
    val shipid: Int,
    val visitdate: String,
    val mark: Int,
    val ccpsch: Int,
    val ccptype: Int,
    val lat: Double?,
    val lng: Double?,
    val uploaded: Int
)


@Serializable
data class JSONVisit(
    val id: String,
    val shipid: Int,
    val visitdate: String,
    val mark: Int,
    val ccpsch: Int,
    val ccptype: Int,
    val lat: Double?,
    val lng: Double?
)


@Dao
interface VisitDao {
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

}