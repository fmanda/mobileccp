package com.ts.mobileccp.rest

import android.content.Context
import android.widget.Toast
import com.ts.mobileccp.db.AppDatabase
import com.ts.mobileccp.db.entity.Customer
import com.ts.mobileccp.db.entity.CustomerDao
import com.ts.mobileccp.db.entity.JSONCustomer
import com.ts.mobileccp.db.entity.JSONSalesOrder
import com.ts.mobileccp.db.entity.JSONSalesOrderItem
import com.ts.mobileccp.db.entity.JSONVisit
import com.ts.mobileccp.db.entity.LoginInfo
import com.ts.mobileccp.db.entity.LoginInfoDao
import com.ts.mobileccp.db.entity.Product
import com.ts.mobileccp.db.entity.ProductDao
import com.ts.mobileccp.db.entity.SalesOrderDao
import com.ts.mobileccp.db.entity.SalesOrderWithItems
import com.ts.mobileccp.db.entity.Visit
import com.ts.mobileccp.global.AppVariable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ApiRepository(ctx: Context) {
    private val isDebug : Boolean = true
    private val apiService = RetrofitClient.apiService
    private val context = ctx

    private val customerDao: CustomerDao = AppDatabase.getInstance(context).customerDao()
    private val productDao: ProductDao = AppDatabase.getInstance(context).productDao()
    private val salesOrder: SalesOrderDao = AppDatabase.getInstance(context).salesOrderDao()
    private val loginInfoDao: LoginInfoDao = AppDatabase.getInstance(context).loginInfoDao()


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
                val response = apiService.getCustomer(AppVariable.loginInfo.project_code)
                response
            } catch (e: Exception) {
                // Handle exceptions
                null
            }
        }
    }

    suspend fun fetchProducts(): List<ProductResponse>? {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProduct(AppVariable.loginInfo.project_code)
                response
            } catch (e: Exception) {
                // Handle exceptions
                null
            }
        }
    }


    suspend fun saveProductFromRest() {
        try {
            val result = this.fetchProducts()
            val products = result?.map { apiResponse ->
                Product(
                    apiResponse.sku,
                    apiResponse.principal,
                    apiResponse.merk,
                    apiResponse.nama,
                    apiResponse.uom_1,
                    apiResponse.uom_2,
                    apiResponse.uom_3,
                    apiResponse.trad_uom_1,
                    apiResponse.trad_uom_2,
                    apiResponse.trad_uom_3,
                    apiResponse.konv_1,
                    apiResponse.konv_2,
                    apiResponse.konv_3,
                    apiResponse.sellprice_1,
                    apiResponse.sellprice_2,
                    apiResponse.sellprice_3,
                    apiResponse.trad_sellprice_1,
                    apiResponse.trad_sellprice_2,
                    apiResponse.trad_sellprice_3,
                )
            }

            if (products != null) {
                productDao.insertList(products)
//                callback?.onSuccess("")
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
                    UUID.fromString(apiResponse.id),
                    apiResponse.nama,
                    apiResponse.nik,
                    apiResponse.phone,
                    apiResponse.alamat,
                    apiResponse.kecamatan,
                    apiResponse.kelurahan,
                    1
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
            customer_id = soWithItems.order.customer_id.toString(),
            salesman_id = soWithItems.order.salesman_id.toString(),
            project_code = soWithItems.order.project_code,
            dpp = soWithItems.order.dpp,
            ppn = soWithItems.order.ppn,
            amt = soWithItems.order.amt,
            latitude = soWithItems.order.latitude,
            longitude = soWithItems.order.longitude,
            items = soWithItems.items.map { item ->
                JSONSalesOrderItem(
                    salesorder_id = item.salesorder_id.toString(),
                    sku = item.sku,
                    uom = item.uom,
                    qty = item.qty,
                    unitprice = item.unitprice,
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
            visitno = visit.visitno,
            visitdate = visit.visitdate,
            customer_id = visit.customer_id.toString(),
            salesman_id = visit.salesman_id.toString(),
            project_code = visit.project_code,
            latitude = visit.latitude,
            longitude = visit.longitude,
        )
    }

    private fun mapToJSONCustomer(customer: Customer): JSONCustomer {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val dt = dateFormat.format(Date())

        return JSONCustomer(
            id = customer.id.toString(),
            project_code = AppVariable.loginInfo.project_code,
            nama = customer.nama,
            nik = customer.nik,
            phone = customer.phone,
            alamat = customer.alamat,
            kecamatan = customer.kecamatan,
            kelurahan = customer.kelurahan,
            last_updated = dt,
            is_new = 1
        )
    }



    suspend fun fetchAndPostOrders() {
        val ordersWithItems = withContext(Dispatchers.IO) {
            salesOrder.getSalesOrderForUpload()
        }
        val orders = ordersWithItems.map { orderWithItems ->
            mapToJSONSalesOrder(orderWithItems)
        }
        val jsonOrders = Json.encodeToString(orders)
        println("Serialized JSON: $jsonOrders")

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
            salesOrder.getVisitForUpload()
        }

        val visits = dbvisits.map { visit ->
            mapToJSONVisit(visit)
        }

        try {
            val response = apiService.postVisits(visits)
            if (response.isSuccessful) {
                salesOrder.updateStatusUploadVisit()
            } else {
                Toast.makeText(context, "Failed to post orders: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting orders",
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
            salesOrder.getVisitForUploadFilterID(filterID)
        }

        val visits = dbvisits.map { visit ->
            mapToJSONVisit(visit)
        }

        try {
            val response = apiService.postVisits(visits)
            if (response.isSuccessful) {
                salesOrder.updateStatusUploadVisitByID(filterID)
            } else {
                Toast.makeText(context, "Failed to post orders: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting orders",
                Toast.LENGTH_SHORT).show()
        }
    }

    suspend fun fetchAndPostCustomer() {
        val dbcustomer = withContext(Dispatchers.IO) {
            customerDao.getCustomersForUpload()
        }
        val customers = dbcustomer.map { custitem ->
            mapToJSONCustomer(custitem)
        }
//        val jsonCust = Json.encodeToString(customers)
//        println("Serialized JSON: $jsonCust")

        try {
            val response = apiService.postCustomers(customers)
            if (response.isSuccessful) {
                salesOrder.updateStatusUploadSO()
            } else {
                Toast.makeText(context, "Failed to post Customers: ${response.errorBody()?.string()}",
                    Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error posting Customers",
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
                    if (resLogin.id.isNotEmpty()) {

                        val loginInfo = LoginInfo(
                            0,
                            resLogin.username,
                            resLogin.password,
                            UUID.fromString(resLogin.id),
                            resLogin.nama,
                            resLogin.kode,
                            resLogin.project_code,
                            resLogin.token,
                            null
                        )


                        AppVariable.loginInfo = loginInfo
                        loginInfoDao.upsert(loginInfo)

                        Toast.makeText(context, "Welcome : ${loginInfo.salesman}",
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

