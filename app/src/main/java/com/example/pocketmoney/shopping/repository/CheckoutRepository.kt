package com.example.pocketmoney.shopping.repository

import com.example.pocketmoney.shopping.model.CustomerOrder
import com.example.pocketmoney.shopping.model.DiscountModel
import com.example.pocketmoney.shopping.network.ShoppingApiService
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CheckoutRepository @Inject constructor(
    val shoppingApiService: ShoppingApiService
) {


    var itemQuantity = 0
    var productOldPrice = 0.0
    var totalAmount = 0.0
    var tax = 0.0
    var discountAmount = 0.0
    var shippingCharge = 0.0
    var saving = productOldPrice - totalAmount
    var grandTotal = (totalAmount + shippingCharge + tax) - discountAmount

    var selectedAddressId = 0
    var couponCode = ""

    var selectedPaymentMethod = PaymentEnum.WALLET

    suspend fun createCustomerOrder(
        customerOrder: CustomerOrder
    ): Flow<String> {
        return flow {
            val response = shoppingApiService.createCustomerOrder(customerOrder)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }


    suspend fun updatePaymentStatus(orderNumber: String,paymentStatusId:Int): Flow<Boolean> {
        return flow {
            val response = shoppingApiService.updatePaymentStatus(orderNumber, paymentStatusId)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun validateCoupon(couponCode:String): Flow<Boolean> {
        return flow {
            val response = shoppingApiService.validateCouponCode(couponCode)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }

   suspend fun getDiscountDetails(couponCode:String): Flow<DiscountModel> {
        return flow {
            val response = shoppingApiService.getDiscountDetails(couponCode)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }




}