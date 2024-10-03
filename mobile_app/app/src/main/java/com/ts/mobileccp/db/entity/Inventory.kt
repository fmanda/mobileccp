package com.ts.mobileccp.db.entity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import java.text.NumberFormat

@Entity(tableName = "inventory")
data class Inventory(
    @PrimaryKey val invid: Int,
    val partno: String,
    val invname: String?,
    val description: String?,
    val invgrp: String?,
    val pclass8name: String?
)

@Entity(tableName = "pricelevel", primaryKeys = ["invid", "pricelevel"])
data class PriceLevel(
    val invid: Int,
    val partno: String?,
    val pricelevel: Int,
    val pricelevelname: String?,
    val price: Double
)


data class InventoryLookup(
    val invid: Int,
    val partno: String,
    val invname: String,
    val description: String,
    val invgrp: String?,
    val pclass8name: String?,
    val pricelevel: Int?,
    val pricelevelname: String?,
    val price: Double,
    var expanded: Boolean
){

    fun getPrice(formatter: NumberFormat):String{
        return  "Rp " + formatter.format(this.price)
    }

}


@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory WHERE invid = :invid")
    suspend fun getById(invid: Int): Inventory?

    @Query("SELECT * FROM inventory WHERE partno = :partno")
    suspend fun getByPartNo(partno: String): Inventory?

    @Query("SELECT * FROM inventory")
    fun getListInventory(): LiveData<List<Inventory>>  //autosync / reactive / async

    @Query("SELECT * FROM inventory")
    fun getInventories(): List<Inventory>  //autosync / reactive / async

    @Upsert
    suspend fun insertList(inventory: List<Inventory>)

    @Upsert
    suspend fun insertPricelevels(pricelevels: List<PriceLevel>)

    @Query("SELECT COUNT(*) FROM inventory")
    fun getRowCount(): LiveData<Int?>

//    @Query("SELECT * FROM product WHERE nama LIKE :query  or merk LIKE :query COLLATE NOCASE")
//    fun searchProducts(query: String): LiveData<List<Product>>

    @Query(
        "SELECT a.*, b.pricelevel, b.pricelevelname, b.price, false as expanded " +
        " FROM inventory a " +
        " inner join pricelevel b on a.invid = b.invid and b.pricelevel = :pricelevel" +
        " WHERE (a.description LIKE :query) and pclass8name LIKE :pclass8name COLLATE NOCASE"
    )
    fun lookupProducts(pricelevel:Int, query: String, pclass8name: String): LiveData<List<InventoryLookup>>


    @Query("DELETE FROM inventory")
    suspend fun clearProduct()

    @Query("select ' All Kategori ' union all select distinct pclass8name from inventory ")
    fun getListMerk(): LiveData<List<String>>
}
