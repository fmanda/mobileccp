package com.ts.mobileccp.rest

import com.ts.mobileccp.db.entity.CCPSch
import com.ts.mobileccp.db.entity.JSONCCP
import com.ts.mobileccp.db.entity.JSONCCPDet
import com.ts.mobileccp.db.entity.JSONSalesOrder
import com.ts.mobileccp.db.entity.JSONVisit
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
    suspend fun getCustomer(@Path("areano") areano: String) : List<CustomerResponse>

    @GET("inventoryarea/{areano}")
    suspend fun getInventory(@Path("areano") areano: String) : List<InventoryResponse>

    @GET("pricelevelarea/{areano}")
    suspend fun getPriceLevel(@Path("areano") areano: String) : List<PriceLevelResponse>

    @GET("customer/{id}")
    suspend fun getCustomerByID(@Path("id") id: String): CustomerResponse


    @POST("batchsalesorder")
    suspend fun postOrders(@Body orders: List<JSONSalesOrder>): Response<Unit>


    @POST("batchnewccp")
    suspend fun postVisits(@Body orders: List<JSONCCPDet>): Response<Unit>

    @GET("login_salesman/{username}/{password}")
    suspend fun loginSalesman(@Path("username") username: String, @Path("password") password: String): Response<LoginInfoResponse>

    @GET("ccpmark")
    suspend fun getCCPMark(): List<CCPMarkResponse>

    @GET("ccpsch")
    suspend fun getCCPSCH(): List<CCPSCHResponse>

    @Multipart
    @POST("uploadimg")
    fun uploadFile(
        @Part file: MultipartBody.Part
    ): Call<String>
}