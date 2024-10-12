package com.ts.mobileccp.rest

import com.ts.mobileccp.global.AppVariable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {

    private val BASE_URL = AppVariable.apiurl

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // Log request and response body
    }

//    private val client = OkHttpClient.Builder()
//        .addInterceptor(loggingInterceptor)
//        .build()

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(15, TimeUnit.SECONDS) // Set connection timeout to 30 seconds
        .readTimeout(60, TimeUnit.SECONDS)    // Set read timeout to 30 seconds
        .writeTimeout(30, TimeUnit.SECONDS)   // Set write timeout to 30 seconds
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}