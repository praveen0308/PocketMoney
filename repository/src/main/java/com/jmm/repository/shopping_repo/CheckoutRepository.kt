package com.jmm.repository.shopping_repo

import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.shopping_models.CustomerOrder
import com.jmm.model.shopping_models.DiscountCouponModel
import com.jmm.model.shopping_models.DiscountModel
import com.jmm.network.services.CheckoutService
import com.jmm.network.services.ShoppingApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CheckoutRepository @Inject constructor(
    private val shoppingApiService: ShoppingApiService,
    private val checkoutService: CheckoutService
) {

    var selectedPaymentMethod = PaymentEnum.WALLET

    suspend fun createCustomerOrder(
        customerOrder: CustomerOrder
    ): Flow<String> {
        return flow {
            val response = shoppingApiService.createCustomerOrder(customerOrder)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun updatePaymentStatus(orderNumber: String, paymentStatusId: Int): Flow<Boolean> {
        return flow {
            val response = shoppingApiService.updatePaymentStatus(orderNumber, paymentStatusId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun validateCoupon(couponCode: String): Flow<Boolean> {
        return flow {
            val response = shoppingApiService.validateCouponCode(couponCode)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getDiscountDetails(couponCode: String): Flow<DiscountModel> {
        return flow {
            val response = shoppingApiService.getDiscountDetails(couponCode)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCouponDiscountList(userId:String,roleId:Int): Flow<List<DiscountCouponModel>> {
        return flow {
            val response = checkoutService.getDiscountCouponList(userId, roleId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

}