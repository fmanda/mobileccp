package com.ts.mobileccp.rest


import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.ARInv
import com.ts.mobileccp.db.entity.ARInvDao
import com.ts.mobileccp.db.entity.VisitMark
import com.ts.mobileccp.db.entity.VisitMarkDao
import com.ts.mobileccp.db.entity.PlanMark
import com.ts.mobileccp.db.entity.CustomerDelivery
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.Inventory
import com.ts.mobileccp.db.entity.InventoryDao
import com.ts.mobileccp.db.entity.JSONVisit
import com.ts.mobileccp.db.entity.JSONSalesOrder
import com.ts.mobileccp.db.entity.JSONSalesOrderItem
import com.ts.mobileccp.db.entity.JSONVisitPlan
import com.ts.mobileccp.db.entity.JSONVisitPlanItem
import com.ts.mobileccp.db.entity.JSONVisitRoute
import com.ts.mobileccp.db.entity.JSONVisitRouteItem
import com.ts.mobileccp.db.entity.LoginInfo
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.PriceLevel
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.SalesOrderWithItems
import com.ts.mobileccp.db.entity.Visit
import com.ts.mobileccp.db.entity.VisitDao
import com.ts.mobileccp.db.entity.VisitPlan
import com.ts.mobileccp.db.entity.VisitPlanDao
import com.ts.mobileccp.db.entity.VisitPlanItem
import com.ts.mobileccp.db.entity.VisitPlanWithItem
import com.ts.mobileccp.db.entity.VisitRoute
import com.ts.mobileccp.db.entity.VisitRouteDao
import com.ts.mobileccp.db.entity.VisitRouteItem
import com.ts.mobileccp.db.entity.VisitRouteWithItem
import com.ts.mobileccp.global.AppVariable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ApiRepository(ctx: Context) {
    private val isDebug : Boolean = true

//    private val apiService = RetrofitClient.apiService

    //fma: changed to getter so we can update when base_url updated
    private val apiService: ApiService get() = RetrofitClient.apiService

    private val context = ctx

    private val customerDao: CustomerDao = AppDatabase.getInstance(context).customerDao()
    private val inventoryDao: InventoryDao = AppDatabase.getInstance(context).inventoryDao()
    private val salesOrder: SalesOrderDao = AppDatabase.getInstance(context).salesOrderDao()
    private val visitDao: VisitDao = AppDatabase.getInstance(context).visitDao()
    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(context).loginInfoDao()
    private val visitMarkDao: VisitMarkDao = AppDatabase.getInstance(context).visitMarkDao()
    private val arInvDao: ARInvDao = AppDatabase.getInstance(context).arInvDao()
    private val settingDao = AppDatabase.getInstance(context).settingDao()

    private val visitRouteDao: VisitRouteDao = AppDatabase.getInstance(context).visitRouteDao()
    private val visitPlanDao: VisitPlanDao = AppDatabase.getInstance(context).visitPlanDao()



    suspend fun fetchCustomer(): List<CustomerDeliveryResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCustomer(AppVariable.loginInfo.areano)
                response
            } catch (e: Exception) {
                // Handle exceptions
                null
            }
        }
    }

    suspend fun fetchProducts(): List<InventoryResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getInventory(AppVariable.loginInfo.areano)
                response
            } catch (e: Exception) {
                // Handle exceptions
                null
            }
        }
    }

    suspend fun fetchPriceLevel(): List<PriceLevelResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPriceLevel(AppVariable.loginInfo.areano)
                response
            } catch (e: Exception) {
                // Handle exceptions
                null
            }
        }
    }

    suspend fun fetchVisitPlan(): List<VisitPlanResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getVisitPlan(AppVariable.loginInfo.salid?: "")
                response
            } catch (e: Exception) {
                // Handle exceptions
                null
            }
        }
    }

    suspend fun fetchVisitRoute(): List<VisitRouteResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getVisitRoute(AppVariable.loginInfo.areano?: "")
                response
            } catch (e: Exception) {
                // Handle exceptions
                null
            }
        }
    }

    suspend fun fetchVisitMark(): List<VisitMarkResponse>?{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.getVisitmark()
                response
            } catch (e: Exception){
                null
            }
        }
    }

    suspend fun fetchPlanMark(): List<PlanMarkResponse>?{
        return withContext(Dispatchers.IO){
            try{
                val response = apiService.getPlanMark()
                response
            }catch (e: Exception){
                null
            }
        }
    }

    suspend fun fetchARRemain(): List<ARInvResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getARRemain(AppVariable.loginInfo.salid?: "0")
                response
            } catch (e: Exception) {
                // Handle exceptions
                null
            }
        }
    }

    suspend fun saveVisitMarkFromRest() {
        try {
            val visitmarkResponses = this.fetchVisitMark()
            val planMarkResponses = this.fetchPlanMark()

            val visitMarks = visitmarkResponses?.map{ obj ->
                VisitMark(
                    obj.id,
                    obj.markname
                )
            }

            val planMarks = planMarkResponses?.map{ obj ->
                PlanMark(
                    obj.id,
                    obj.markname
                )
            }

            if (visitMarks != null) visitMarkDao.insertListVisitMark(visitMarks)
            if (planMarks != null) visitMarkDao.insertListPlanMark(planMarks)

        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun saveInventoryFromRest() {
        try {
            val result = this.fetchProducts()
            val inventories = result?.map { apiResponse ->
                Inventory(
                    apiResponse.invid,
                    apiResponse.partno,
                    apiResponse.invname,
                    apiResponse.description,
                    apiResponse.invgrp,
                    apiResponse.pclass8name
                )
            }
            if (inventories != null) {
                inventoryDao.insertList(inventories)
            }

        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun savePriceLevelFromRest() {
        try {
            val result = this.fetchPriceLevel()
            val pricelevels = result?.map { apiResponse ->
                PriceLevel(
                    apiResponse.invid,
                    apiResponse.partno,
                    apiResponse.pricelevel,
                    apiResponse.pricelevelname,
                    apiResponse.price
                )
            }
            if (pricelevels != null) {
                inventoryDao.insertPricelevels(pricelevels)
            }

        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun saveVisitRouteFromRest() {
        try {
            val result = this.fetchVisitRoute()

            result?.let{
                for (visitRouteResponse in it){
                    val visitRoute = VisitRoute(
                        UUID.fromString(visitRouteResponse.id),
                        visitRouteResponse.dabin,
                        visitRouteResponse.routename,
                        1
                    )
                    visitRouteDao.upsert(visitRoute);
                    visitRouteDao.deleteItems(visitRoute.id)

                    val itemResponse = visitRouteResponse.items
                    itemResponse?.let{
                        val visitrouteitems = itemResponse.map { itemResponse ->
                            VisitRouteItem(
                                0,
                                visitRoute.id,
                                itemResponse.partnerid
                            )
                        }
                        visitRouteDao.insertItems(visitrouteitems)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun saveVisitPlanFromRest() {
        try {
            val result = this.fetchVisitPlan()

            result?.let{
                for (visitPlanResponse in it){
                    val visitPlan = VisitPlan(
                        UUID.fromString(visitPlanResponse.id),
                        visitPlanResponse.notr,
                        visitPlanResponse.datetr,
                        visitPlanResponse.salid,
                        visitPlanResponse.dabin,
                        visitPlanResponse.entity,
                        visitPlanResponse.status,
                        1
                    )
                    visitPlanDao.upsert(visitPlan);
                    visitPlanDao.deleteItems(visitPlan.id)

                    val itemResponse = visitPlanResponse.items
                    itemResponse?.let{
                        val visitplanitems = itemResponse.map { itemResponse ->
                            VisitPlanItem(
                                0,
                                visitPlan.id,
                                itemResponse.partnerid,
                                itemResponse.planmark_id
                            )
                        }
                        visitPlanDao.insertItems(visitplanitems)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun saveARRemainFromRest() {
        try {
            val result = this.fetchARRemain()
            val arremains = result?.map { apiResponse ->
                ARInv(
                    apiResponse.invno,
                    apiResponse.invdate,
                    apiResponse.amount,
                    apiResponse.settle,
                    apiResponse.remain,
                    apiResponse.shipname,
                    apiResponse.partnername,
                    apiResponse.shipid,
                    apiResponse.salid
                )
            }

            if (arremains != null) {
                arInvDao.updateAll(arremains)
            }

        } catch (e: Exception) {
            Toast.makeText(context, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
        }
    }


    suspend fun saveCustomerFromRest() {
        try {
            val result = this.fetchCustomer()

            val customerDeliveries = result?.map { apiResponse ->
                CustomerDelivery(
                    apiResponse.shipid,
                    apiResponse.shipname,
                    apiResponse.shipaddress,
                    apiResponse.shipcity,
                    apiResponse.shipphone,
                    apiResponse.shiphp,
                    apiResponse.partnerid,
                    apiResponse.partnername,
                    apiResponse.partneraddress,
                    apiResponse.pricelevel,
                    apiResponse.areano,
                    apiResponse.areaname,
                    apiResponse.npsn,
                    apiResponse.jenjang
                )
            }

            if (customerDeliveries != null) {
                customerDao.insertList(customerDeliveries)
//                callback?.onSuccess("")
//                Toast.makeText(context, "Customer Updated from Server", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
//            callback?.onError(e.message ?: "An error occurred")
            Toast.makeText(context, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
        }
    }

    private fun mapToJSONSalesOrder(soWithItems: SalesOrderWithItems): JSONSalesOrder {
        return JSONSalesOrder(
            id = soWithItems.order.id.toString(),
            orderno = soWithItems.order.orderno,
            orderdate = soWithItems.order.orderdate,
            shipid = soWithItems.order.shipid,
            salid = soWithItems.order.salid,
            entity = AppVariable.loginInfo.entity?:"",
            dpp = soWithItems.order.dpp,
            ppn = soWithItems.order.ppn,
            amt = soWithItems.order.amt,
            latitude = soWithItems.order.latitude,
            longitude = soWithItems.order.longitude,
            items = soWithItems.items.map { item ->
                JSONSalesOrderItem(
                    salesorder_id = item.salesorder_id.toString(),
                    partno = item.partno,
                    qty = item.qty,
                    price = item.price,
                    discount = item.discount,
                    dpp = item.dpp,
                    ppn = item.ppn,
                    amt = item.amt
                )
            }
        )
    }


    private fun mapToJSONVisitRoute(routeWithItems: VisitRouteWithItem): JSONVisitRoute {
        return JSONVisitRoute(
            id = routeWithItems.visitroute.id.toString(),
            dabin = routeWithItems.visitroute.dabin,
            routename = routeWithItems.visitroute.routename,
            items = routeWithItems.items.map { item ->
                JSONVisitRouteItem(
                    visitroute_id = item.visitroute_id.toString(),
                    partnerid = item.partnerid
                )
            }
        )
    }

    private fun mapToJSONVisitPlan(planWithItems: VisitPlanWithItem): JSONVisitPlan {
        return JSONVisitPlan(
            id = planWithItems.visitplan.id.toString(),
            notr = planWithItems.visitplan.notr,
            datetr = planWithItems.visitplan.datetr,
            salid = planWithItems.visitplan.salid,
            dabin = planWithItems.visitplan.dabin,
            entity = planWithItems.visitplan.entity,
            status = planWithItems.visitplan.status,
            items = planWithItems.items.map { item ->
                JSONVisitPlanItem(
                    visitplan_id = item.visitplan_id.toString(),
                    partnerid = item.partnerid,
                    planmark_id = item.planmark_id
                )
            }
        )
    }


    private fun mapToJSONVisit(visit: Visit): JSONVisit {
        return JSONVisit(
            visit.id.toString(),
            visit.shipid,
            visit.visitno,
            visit.visitdate,
            visit.visitmark_id,
            visit.visitplan,
            visit.notes,
            visit.lat,
            visit.lng,
            AppVariable.loginInfo.salid
        )
    }

    suspend fun fetchAndPostVisitRoute() {
        val routesWithItems = withContext(Dispatchers.IO) {
            visitRouteDao.getVisitRouteForUpload()
        }
        val routes = routesWithItems.map { routeWithItems ->
            mapToJSONVisitRoute(routeWithItems)
        }
        try {
            val response = apiService.postVisitRoutes(routes)
            if (response.isSuccessful) {
                salesOrder.updateStatusUploadSO()
            } else {
                Toast.makeText(context, "Failed to post Routes: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting Routes",
                Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun fetchAndPostVisitPlan() {
        val plansWithItems = withContext(Dispatchers.IO) {
            visitPlanDao.getVisitPlanForUpload()
        }
        val plans = plansWithItems.map { planWithItem ->
            mapToJSONVisitPlan(planWithItem)
        }
        try {
            val response = apiService.postVisitPlans(plans)
            if (response.isSuccessful) {
                salesOrder.updateStatusUploadSO()
            } else {
                Toast.makeText(context, "Failed to post Plans: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting Plans",
                Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun fetchAndPostOrders() {
        val ordersWithItems = withContext(Dispatchers.IO) {
            salesOrder.getSalesOrderForUpload()
        }
        val orders = ordersWithItems.map { orderWithItems ->
            mapToJSONSalesOrder(orderWithItems)
        }
        try {
            val response = apiService.postOrders(orders)
            if (response.isSuccessful) {
                salesOrder.updateStatusUploadSO()
            } else {
                Toast.makeText(context, "Failed to post orders: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting orders",
                Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun fetchAndPostVisit(filterID:UUID? = null) {
        val dbvisits = withContext(Dispatchers.IO) {
            if (filterID != null) {
                visitDao.getVisitForUploadFilterID(filterID)
            }else{
                visitDao.getVisitForUpload()
            }
        }

        val visits = dbvisits.map { visit ->
            mapToJSONVisit(visit)
        }

        try {
            val response = apiService.postVisits(visits)
            if (response.isSuccessful) {
                if (filterID != null) {
                    visitDao.updateStatusUploadVisitByID(filterID)
                }else{
                    visitDao.updateStatusUploadVisit()
                }

            } else {
                Toast.makeText(context, "Failed to post visit: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting orders",
                Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun fetchAndPostVisitImg(filterID:UUID? = null) {
        val visits = withContext(Dispatchers.IO) {
            if (filterID != null) {
                visitDao.getVisitForUploadFilterID(filterID)
            }else{
                visitDao.getVisitForUpload()
            }
        }

        for (visit in visits){
            visit.img_uri?.let {
                val uri = Uri.parse(visit.img_uri)

                uploadFileFromUri(uri)
            }

        }

    }

    private fun getOriginalFileName(context: Context, uri: Uri): String? {
        var fileName: String? = null
        val projection = arrayOf(MediaStore.Images.Media.DISPLAY_NAME)

        // Query the MediaStore for the original filename
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                fileName = cursor.getString(nameIndex) // Get the filename
            }
        }

        return fileName // Return the original filename
    }

    private suspend fun uploadFileFromUri(fileUri: Uri) {
        if (!isUriValid(fileUri))  return
        val fileName = getOriginalFileName(context, fileUri) ?: return

        val baseName = fileName.substringBeforeLast(".")
        val extension = fileName.substringAfterLast(".", "")

        val inputStream = context.contentResolver.openInputStream(fileUri)
        val tempFile = withContext(Dispatchers.IO) {
            File(context.cacheDir, "$baseName.$extension")
//            File.createTempFile(fileName.substringBeforeLast("."), "", context.cacheDir)
//            File.createTempFile("tmp", null, context.cacheDir)
        }
        inputStream?.use { input ->
            FileOutputStream(tempFile).use { output ->
                input.copyTo(output)
            }
        }

        val requestFile = tempFile.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

        val call = apiService.uploadFile(body)
        call.enqueue(object : retrofit2.Callback<String> {
            override fun onResponse(call: Call<String>, response: retrofit2.Response<String>) {
                if (response.isSuccessful) {
                    Log.d("Upload", "File uploaded successfully: ${response.body()}")
                } else {
                    Log.e("Upload", "File upload failed: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e("Upload", "Error: ${t.message}")
            }
        })
    }

    private fun isUriValid(fileUri: Uri): Boolean {
        return try {
            context.contentResolver.openInputStream(fileUri)?.use {
                true
            } ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun fetchAndPostOrdersByID(filterID:UUID) {
        val ordersWithItems = withContext(Dispatchers.IO) {
            salesOrder.getSalesOrderForUploadFilterID(filterID)
        }
        val orders = ordersWithItems.map { orderWithItems ->
            mapToJSONSalesOrder(orderWithItems)
        }
        val jsonOrders = Json.encodeToString(orders)
        println("Serialized JSON: $jsonOrders")

        try {
            val response = apiService.postOrders(orders)
            if (response.isSuccessful) {
                salesOrder.updateStatusUploadSOByID(filterID)
            } else {
                Toast.makeText(context, "Failed to post orders: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting orders",
                Toast.LENGTH_SHORT).show()
        }
    }


    suspend fun testAPI(api:String):Boolean{
        val originapi = AppVariable.setting.api_url
        try {
            this.updateBaseURL(api, true)
            val response = apiService.check()

            val resCheck: CheckServerResponse? = response.body()


            if (response.isSuccessful) {
                Toast.makeText(context, resCheck?.msg,
                    Toast.LENGTH_SHORT).show()
                return true
            }else{
                Toast.makeText(context, response.errorBody()?.toString(),
                    Toast.LENGTH_SHORT).show()
                return false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error Testing API : ${e.message?.toString()}",
                Toast.LENGTH_SHORT).show()
            return true
        } finally {
            this.updateBaseURL(originapi, true)
        }
    }

    suspend fun updateBaseURL(api:String, is_test:Boolean=false){
        try {
            var updatedApi = api
            if (!api.endsWith('/')){
                updatedApi = "$api/"
            }

            AppVariable.setting.api_url = updatedApi
            settingDao.upsert(AppVariable.setting)
            RetrofitClient.updateRetrofitBaseURL(api)

            if (!is_test) {
                Toast.makeText(
                    context, "Base API URL Updated",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal Update API URL : ${e.message?.toString()}",
                Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun loginSalesman(username:String, password:String): Boolean {
        var login = false

        try {
            val response = apiService.loginSalesman(username, password)
            if (response.isSuccessful) {

                val resLogin: LoginInfoResponse? = response.body()


                if (resLogin != null) {
                    if (resLogin.salid.isNotEmpty()) {

                        val loginInfo = LoginInfo(
                            0,
                            resLogin.salid,  //temporary username
                            resLogin.salid,  //temporary pass
                            resLogin.salid,
                            resLogin.salname,
                            resLogin.areano,
                            resLogin.areaname,
                            resLogin.entity,
                            resLogin.token,
                            null,
                            null
                        )


                        AppVariable.loginInfo = loginInfo
                        loginInfoDao.upsert(loginInfo)

                        Toast.makeText(context, "Welcome : ${loginInfo.salname}",
                            Toast.LENGTH_SHORT).show()

                        login = true
                    }
                }

            } else {
                Toast.makeText(context, "Gagal Login: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Gagal Login",
                Toast.LENGTH_SHORT).show()
        }

        return login
    }



}

