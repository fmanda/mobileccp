package com.ts.mobileccp.db.entity
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert


@Entity(tableName = "visitmark")
data class VisitMark(
    @PrimaryKey val id: Int,
    val markname: String
)

@Entity(tableName = "planmark")
data class PlanMark(
    @PrimaryKey val id: Int,
    val markname: String
)

@Dao
interface VisitMarkDao {
    @Upsert
    suspend fun insertListVisitMark(visitMark: List<VisitMark>)

    @Upsert
    suspend fun insertListPlanMark(ccpMark: List<PlanMark>)

    @Query("SELECT * FROM visitmark")
    fun getListVisitMark(): LiveData<List<VisitMark>>

    @Query("SELECT * FROM planmark")
    fun getListPlanMark(): LiveData<List<PlanMark>>

    @Query("DELETE FROM visitmark")
    suspend fun clearVisitMark()

    @Query("DELETE FROM planmark")
    suspend fun clearPlanMark()

}

