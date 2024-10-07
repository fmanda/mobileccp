package com.ts.mobileccp.db.entity
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert


@Entity(tableName = "ccpmark")
data class CCPMark(
    @PrimaryKey val mark: Int,
    val markname: String
)

@Entity(tableName = "ccpsch")
data class CCPSch(
    @PrimaryKey val ccpsch: Int,
    val ccpschname: String
)

@Dao
interface CCPMarkDao {
    @Upsert
    suspend fun insertListCCPMark(ccpMark: List<CCPMark>)

    @Upsert
    suspend fun insertListCCPSCH(ccpMark: List<CCPSch>)

    @Query("SELECT * FROM ccpmark")
    fun getListCCPMark(): LiveData<List<CCPMark>>

    @Query("SELECT * FROM ccpsch")
    fun getListCCPSCH(): LiveData<List<CCPSch>>

    @Query("DELETE FROM ccpmark")
    suspend fun clearCCPMark()

    @Query("DELETE FROM ccpsch")
    suspend fun clearCCPSCH()

}

