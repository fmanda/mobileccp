package com.ts.mobileccp.db.entity

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import java.util.UUID


@Entity(tableName = "logininfo")
data class LoginInfo(
    @PrimaryKey var id: Int = 0, //always 0
    var username: String,
    var password: String,
    var salid: String?,
    var salname: String?,
    var areano: String,
    var areaname: String?,
    var entity: String?,
    var token: String?,
    var last_update: String?
)


@Dao
interface LoginInfoDao {
    @Query("SELECT * FROM logininfo  LIMIT 1")
    fun getLoginInfo(): LoginInfo?

    @Upsert
    suspend fun upsert(loginInfo: LoginInfo)

    @Query("DELETE FROM logininfo")
    suspend fun clearLoginInfo()
}
