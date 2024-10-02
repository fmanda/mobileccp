package com.ts.mobileccp.db.entity

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import java.text.NumberFormat

@Entity(tableName = "product")
data class Product(
    @PrimaryKey val sku: String,
    val principal: String,
    val merk: String?,
    val nama: String,
    val uom_1: String?,
    val uom_2: String?,
    val uom_3: String?,
    val trad_uom_1: String?,
    val trad_uom_2: String?,
    val trad_uom_3: String?,
    val konv_1: Int?,
    val konv_2: Int?,
    val konv_3: Int?,
    val sellprice_1: Double?,
    val sellprice_2: Double?,
    val sellprice_3: Double?,
    val trad_sellprice_1: Double?,
    val trad_sellprice_2: Double?,
    val trad_sellprice_3: Double?
)


data class ProductLookup(
    val sku: String,
    val principal: String,
    val merk: String?,
    val nama: String,
    val uom_1: String?,
    val uom_2: String?,
    val uom_3: String?,
    val trad_uom_1: String?,
    val trad_uom_2: String?,
    val trad_uom_3: String?,
    val konv_1: Int?,
    val konv_2: Int?,
    val konv_3: Int?,
    var sellprice_1: Double?,
    var sellprice_2: Double?,
    var sellprice_3: Double?,
    val trad_sellprice_1: Double?,
    val trad_sellprice_2: Double?,
    val trad_sellprice_3: Double?,
    var expanded: Boolean
){
    fun getUOM(idx:Int, isTrad:Boolean):String? {
        if (!isTrad) {
            when (idx) {
                1 -> return uom_1
                2 -> return uom_2
                3 -> return uom_3
            }
        } else {
            when (idx) {
                1 -> return trad_uom_1
                2 -> return trad_uom_2
                3 -> return trad_uom_3
            }
        }
        return null
    }

    fun getUOMPrice(idx:Int, isTrad:Boolean):Double? {
        if (!isTrad) {
            when (idx) {
                1 -> return sellprice_1
                2 -> return sellprice_2
                3 -> return sellprice_3
            }
        } else {
            when (idx) {
                1 -> return trad_sellprice_1
                2 -> return trad_sellprice_2
                3 -> return trad_sellprice_3
            }
        }
        return null
    }

    fun getUOMDesc(idx: Int, isTrad: Boolean, formatter: NumberFormat):String{
        return getUOM(idx, isTrad) + "/Rp " + formatter.format(getUOMPrice(idx, isTrad))
    }

}


@Dao
interface ProductDao {
    @Query("SELECT * FROM product WHERE sku = :sku")
    suspend fun getById(sku: String): Product?

    @Query("SELECT * FROM product")
    fun getListProduct(): LiveData<List<Product>>  //autosync / reactive / async

    @Query("SELECT * FROM product")
    fun getProducts(): List<Product>  //autosync / reactive / async

    @Upsert
    suspend fun insertList(product: List<Product>)

    @Query("SELECT COUNT(*) FROM product")
    suspend fun getRowCount(): Int

//    @Query("SELECT * FROM product WHERE nama LIKE :query  or merk LIKE :query COLLATE NOCASE")
//    fun searchProducts(query: String): LiveData<List<Product>>

    @Query(
        "SELECT *, false as expanded FROM product WHERE (nama LIKE :query  or merk LIKE :query) and merk LIKE :merk COLLATE NOCASE"
    )
    fun lookupProducts(query: String, merk: String): LiveData<List<ProductLookup>>


    @Query("DELETE FROM product")
    suspend fun clearProduct()

    @Query("select ' All Merk ' union all select distinct merk from product ")
    fun getListMerk(): LiveData<List<String>>
}
