package com.ts.mobileccp.rest

data class CustomerResponse(
    val shipid: Int,
    val shipname: String,
    val shipaddress: String,
    val shipcity: String,
    val shipphone: String,
    val shiphp: String,
    val partnerid: Int,
    val partnername: String,
    val pricelevel: Int,
    val areano: String,
    val areaname: String,
    val npsn: String,
    val jenjang: String
)

data class InventoryResponse(
    val invid: Int,
    val partno: String,
    val invname: String,
    val description: String,
    val invgrp: String,
    val pclass8name: String
)

data class PriceLevelResponse(
    val invid: Int,
    val partno: String,
    val pricelevel: Int,
    val pricelevelname: String,
    val price: Double
)

data class LoginInfoResponse(
    val salid: String,
    val salname: String,
    val areano: String,
    var areaname: String,
    var entity: String,
    val username: String,
    val password: String,
    val img: String,
    val token: String
)