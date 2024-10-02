package com.ts.mobileccp.rest

data class CustomerResponse(
    val id: String,
    val nama: String,
    val nik: String,
    val phone: String,
    val alamat: String,
    val kecamatan: String,
    val kelurahan: String,
    val uploaded: Int
)

data class ProductResponse(
    val id: String,
    val sku: String,
    val principal: String,
    val merk: String,
    val nama: String,
    val uom_1: String,
    val uom_2: String,
    val uom_3: String,
    val trad_uom_1: String,
    val trad_uom_2: String,
    val trad_uom_3: String,
    val konv_1: Int,
    val konv_2: Int,
    val konv_3: Int,
    val sellprice_1: Double,
    val sellprice_2: Double,
    val sellprice_3: Double,
    val trad_sellprice_1: Double,
    val trad_sellprice_2: Double,
    val trad_sellprice_3: Double
)

data class LoginInfoResponse(
    val id: String,
    val kode: String,
    val nama: String,
    val project_code: String,
    val username: String,
    val password: String,
    val img: String,
    val token: String
)