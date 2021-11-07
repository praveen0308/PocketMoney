package com.sampurna.pocketmoney.shopping.network

import com.sampurna.pocketmoney.shopping.model.OrderListItem
import com.sampurna.pocketmoney.shopping.model.orderModule.ModelOrderDetails
import retrofit2.http.GET
import retrofit2.http.Query

interface OrderApiService {

    @GET("Order/GetOrderListByUserID")
    suspend fun getOrderListByUserID(
        @Query("userID") userID: String
    ):List<OrderListItem>

    @GET("Order/GetOrderDetails")
    suspend fun getOrderDetails(
        @Query("orderNumber") orderNo: String
    ): ModelOrderDetails

    @GET("Order/GetOrderItemList")
    suspend fun getOrderItemList(
        @Query("orderNumber") orderNo: String
    ): ModelOrderDetails

    @GET("Order/TrackOrder")
    suspend fun getOrderTracking(
        @Query("orderNumber") orderNo: String
    ): ModelOrderDetails

    @GET("Order/CancelOrderItem")
    suspend fun cancelOrderItem(
        @Query("orderNumber") orderNo: String,
        @Query("itemId") itemId: String
    ): Boolean

}