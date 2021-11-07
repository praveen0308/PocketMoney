package com.sampurna.pocketmoney.shopping.repository

import androidx.lifecycle.MutableLiveData
import com.sampurna.pocketmoney.shopping.model.CustomerOrder
import com.sampurna.pocketmoney.shopping.model.DiscountModel
import com.sampurna.pocketmoney.shopping.network.ShoppingApiService
import com.sampurna.pocketmoney.utils.myEnums.PaymentEnum
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class CheckoutRepository @Inject constructor(
    val shoppingApiService: ShoppingApiService
) {

/*
    var productOldPrice = 0.0
    var totalAmount = 0.0
    var tax = 0.0

    var shippingCharge = 0.0
    var saving = productOldPrice - totalAmount
    var grandTotal = (totalAmount + shippingCharge + tax) - discountAmount
*/

    var selectedAddressId = 0
    var appliedCouponCode = MutableLiveData("")
    var isFixed = MutableLiveData(false)
    var appliedDiscount = MutableLiveData(0.0)


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