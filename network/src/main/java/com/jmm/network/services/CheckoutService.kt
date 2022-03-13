package com.jmm.network.services


import com.jmm.model.shopping_models.DiscountCouponModel
import retrofit2.http.GET
import retrofit2.http.Query

interface CheckoutService {
    @GET("Checkout/GetDiscountCouponList")
    suspend fun getDiscountCouponList(
        @Query("userid") userId: String,
        @Query("roleid") roleId: Int
    ): List<DiscountCouponModel>
}
