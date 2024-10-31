package com.ts.mobileccp.db.entity

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert


@Entity(tableName = "setting")
data class Setting(
    @PrimaryKey var id: Int = 0, //always 0
    var api_url: String
)

@Dao
interface SettingDao {
    @Query("SELECT * FROM setting  LIMIT 1")
    fun getSetting(): Setting?

    @Upsert
    suspend fun upsert(setting: Setting)
}
