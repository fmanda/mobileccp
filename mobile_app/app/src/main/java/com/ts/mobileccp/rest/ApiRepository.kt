package com.ts.mobileccp.rest

import android.content.Context
import android.widget.Toast
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.CCPMark
import com.ts.mobileccp.db.entity.CCPMarkDao
import com.ts.mobileccp.db.entity.CCPSch
import com.ts.mobileccp.db.entity.Customer
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.JSONSalesOrder
import com.ts.mobileccp.db.entity.JSONSalesOrderItem
import com.ts.mobileccp.db.entity.JSONVisit
import com.ts.mobileccp.db.entity.LoginInfo
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.Inventory
import com.ts.mobileccp.db.entity.InventoryDao
import com.ts.mobileccp.db.entity.PriceLevel
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.SalesOrderWithItems
import com.ts.mobileccp.db.entity.Visit
import com.ts.mobileccp.db.entity.VisitDao
import com.ts.mobileccp.global.AppVariable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.UUID

class ApiRepository(ctx: Context) {
    private val isDebug : Boolean = true
    private val apiService = RetrofitClient.apiService
    private val context = ctx

    private val customerDao: CustomerDao = AppDatabase.getInstance(context).customerDao()
    private val inventoryDao: InventoryDao = AppDatabase.getInstance(context).inventoryDao()
    private val salesOrder: SalesOrderDao = AppDatabase.getInstance(context).salesOrderDao()
    private val visitDao: VisitDao = AppDatabase.getInstance(context).visitDao()
    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(context).loginInfoDao()
    private val ccpMarkDao: CCPMarkDao = AppDatabase.getInstance(context).ccpMarkDAO()


    suspend fun fetchCustomerByID(id: String): CustomerResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCustomerByID(id)
                response
            } catch (e: Exception) {
                // Handle exceptions
                null
            }
        }
    }

    suspend fun fetchCustomer(): List<CustomerResponse>? {
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


    suspend fun fetchCCPMark(): List<CCPMarkResponse>?{
        return withContext(Dispatchers.IO){
            try {
                val response = apiService.getCCPMark()
                response
            } catch (e: Exception){
                null
            }
        }
    }

    suspend fun fetchCCPSCH(): List<CCPSCHResponse>?{
        return withContext(Dispatchers.IO){
            try{
                val response = apiService.getCCPSCH()
                response
            }catch (e: Exception){
                null
            }
        }
    }

    suspend fun saveCCHMarkFromRest() {
        try {
            val ccpmarksResp = this.fetchCCPMark()
            val ccpschResp = this.fetchCCPSCH()

            val ccpmark = ccpmarksResp?.map{ obj ->
                CCPMark(
                    obj.mark,
                    obj.markname
                )
            }

            val ccpsch = ccpschResp?.map{ obj ->
                CCPSch(
                    obj.ccpsch,
                    obj.ccpschname
                )
            }

            if (ccpmark != null) ccpMarkDao.insertListCCPMark(ccpmark)
            if (ccpsch != null) ccpMarkDao.insertListCCPSCH(ccpsch)

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
//                Toast.makeText(context, "Products Updated from Server", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
//            callback?.onError(e.message ?: "An error occurred")
            Toast.makeText(context, e.message ?: "An error occurred", Toast.LENGTH_LONG).show()
        }
    }

    suspend fun saveCustomerFromRest() {
        try {
            val result = this.fetchCustomer()

            val customers = result?.map { apiResponse ->
                Customer(
                    apiResponse.shipid,
                    apiResponse.shipname,
                    apiResponse.shipaddress,
                    apiResponse.shipcity,
                    apiResponse.shipphone,
                    apiResponse.shiphp,
                    apiResponse.partnerid,
                    apiResponse.partnername,
                    apiResponse.pricelevel,
                    apiResponse.areano,
                    apiResponse.areaname,
                    apiResponse.npsn,
                    apiResponse.jenjang
                )
            }

            if (customers != null) {
                customerDao.insertList(customers)
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
            areano = soWithItems.order.areano,
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

    private fun mapToJSONVisit(visit: Visit): JSONVisit {
        return JSONVisit(
            id = visit.id.toString(),
            shipid = visit.shipid,
            visitdate = visit.visitdate,
            mark = visit.mark,
            ccpsch = visit.ccpsch,
            ccptype = visit.ccptype,
            lat = visit.lat,
            lng = visit.lng,
        )
    }

    suspend fun fetchAndPostOrders() {
        val ordersWithItems = withContext(Dispatchers.IO) {
            salesOrder.getSalesOrderForUpload()
        }
        val orders = ordersWithItems.map { orderWithItems ->
            mapToJSONSalesOrder(orderWithItems)
        }
//        val jsonOrders = Json.encodeToString(orders)
//        println("Serialized JSON: $jsonOrders")

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

    suspend fun fetchAndPostVisit() {
        val dbvisits = withContext(Dispatchers.IO) {
            visitDao.getVisitForUpload()
        }
        val visits = dbvisits.map { obj ->
            mapToJSONVisit(obj)
        }
//        val jsonVisit = Json.encodeToString(visits)
//        println("Serialized JSON: $jsonVisit")

        try {
            val response = apiService.postVisits(visits)
            if (response.isSuccessful) {
                visitDao.updateStatusUploadVisit()
            } else {
                Toast.makeText(context, "Failed to post visits: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting visits",
                Toast.LENGTH_SHORT).show()
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

    suspend fun fetchAndPostVisitByID(filterID:UUID) {
        val dbvisits = withContext(Dispatchers.IO) {
            visitDao.getVisitForUploadFilterID(filterID)
        }

        val visits = dbvisits.map { visit ->
            mapToJSONVisit(visit)
        }

        try {
            val response = apiService.postVisits(visits)
            if (response.isSuccessful) {
                visitDao.updateStatusUploadVisitByID(filterID)
            } else {
                Toast.makeText(context, "Failed to post orders: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting orders",
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

