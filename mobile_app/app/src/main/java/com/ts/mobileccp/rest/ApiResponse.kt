package com.ts.mobileccp.rest

import androidx.room.PrimaryKey
import java.util.UUID


data class CustomerDeliveryResponse(
    val shipid: Int,
    val shipname: String,
    val shipaddress: String,
    val shipcity: String,
    val shipphone: String,
    val shiphp: String,
    val partnerid: Int,
    val partnername: String,
    val partneraddress: String,
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

data class VisitMarkResponse(
    val id: Int,
    val markname: String
)

data class PlanMarkResponse(
    val id: Int,
    val markname: String
)


data class ARInvResponse(
    val invno: String,
    val invdate: String,
    val amount: Double?,
    val settle: Double?,
    val remain: Double?,
    val shipname: String?,
    val partnername: String?,
    val shipid: Int?,
    val salid: String?
)

data class CheckServerResponse(
    val msg:String
)

data class VisitPlanResponse(
    val id: String,
    val notr: String,
    val datetr: String,
    val salid: String,
    val dabin: String,
    val entity: String,
    val status: Int,
    val items: List<VisitPlanItemResponse>
)


data class VisitPlanItemResponse(
    val visitplan_id: String,
    val partnerid: Int,
    val planmark_id: Int
)


data class VisitRouteResponse(
    val id: String,
    val dabin: String,
    val routename: String,
    val items: List<VisitRouteItemsResponse>
)


data class VisitRouteItemsResponse(
    val visitroute_id: UUID,
    val partnerid: Int
)
