package com.ts.mobileccp.rest

import com.ts.mobileccp.db.entity.JSONVisit
import com.ts.mobileccp.db.entity.JSONSalesOrder
import com.ts.mobileccp.db.entity.JSONVisitPlan
import com.ts.mobileccp.db.entity.JSONVisitRoute
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


interface ApiService {
    @GET("customerarea/{areano}")
    suspend fun getCustomer(@Path("areano") areano: String) : List<CustomerDeliveryResponse>

    @GET("inventoryarea/{areano}")
    suspend fun getInventory(@Path("areano") areano: String) : List<InventoryResponse>

    @GET("pricelevelarea/{areano}")
    suspend fun getPriceLevel(@Path("areano") areano: String) : List<PriceLevelResponse>

    @POST("batchsalesorder")
    suspend fun postOrders(@Body orders: List<JSONSalesOrder>): Response<Unit>


    @POST("batchvisit")
    suspend fun postVisits(@Body orders: List<JSONVisit>): Response<Unit>

    @GET("login_salesman/{username}/{password}")
    suspend fun loginSalesman(@Path("username") username: String, @Path("password") password: String): Response<LoginInfoResponse>

    @GET("visitmark")
    suspend fun getVisitmark(): List<VisitMarkResponse>

    @GET("planmark")
    suspend fun getPlanMark(): List<PlanMarkResponse>

    @Multipart
    @POST("uploadimg")
    fun uploadFile(
        @Part file: MultipartBody.Part
    ): Call<String>

    @GET("visitbysal/{salid}")
    suspend fun getVisitPlan(@Path("salid") salid: String) : List<VisitPlanResponse>

    @GET("visitroutebydabin/{dabin}")
    suspend fun getVisitRoute(@Path("dabin") dabin: String) : List<VisitRouteResponse>

    @GET("arremain/{salid}")
    suspend fun getARRemain(@Path("salid") salid: String) : List<ARInvResponse>

    @GET("check")
    suspend fun check(): Response<CheckServerResponse>

    @POST("batchvisitroute")
    suspend fun postVisitRoutes(@Body plans: List<JSONVisitRoute>): Response<Unit>

    @POST("batchvisitplan")
    suspend fun postVisitPlans(@Body plans: List<JSONVisitPlan>): Response<Unit>

}