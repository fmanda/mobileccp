package com.ts.mobileccp.rest

import com.ts.mobileccp.db.entity.JSONCustomer
import com.ts.mobileccp.db.entity.JSONSalesOrder
import com.ts.mobileccp.db.entity.JSONVisit
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface ApiService {
    @GET("customerbyproject/{project_code}")
    suspend fun getCustomer(@Path("project_code") projectCode: String) : List<CustomerResponse>

    @GET("productbyproject/{project_code}")
    suspend fun getProduct(@Path("project_code") projectCode: String) : List<ProductResponse>

    @GET("customer/{id}")
    suspend fun getCustomerByID(@Path("id") id: String): CustomerResponse


    @POST("batchsalesorder")
    suspend fun postOrders(@Body orders: List<JSONSalesOrder>): Response<Unit>

    @POST("batchcustomer")
    suspend fun postCustomers(@Body orders: List<JSONCustomer>): Response<Unit>

    @POST("batchvisit")
    suspend fun postVisits(@Body orders: List<JSONVisit>): Response<Unit>

    @GET("login_salesman/{username}/{password}")
    suspend fun loginSalesman(@Path("username") username: String, @Path("password") password: String): Response<LoginInfoResponse>

}