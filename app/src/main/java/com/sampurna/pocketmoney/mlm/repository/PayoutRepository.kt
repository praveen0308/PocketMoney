package com.sampurna.pocketmoney.mlm.repository

import android.util.Log
import com.sampurna.pocketmoney.mlm.model.payoutmodels.*
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.network.PaymentService
import com.sampurna.pocketmoney.mlm.ui.payouts.PayoutTransferMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PayoutRepository @Inject constructor(val paymentService: PaymentService){


    suspend fun addPayoutCustomer(customer:PayoutCustomer): Flow<Int> {
        return flow {
            val response = paymentService.addPayoutCustomer(customer)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun searchPayoutCustomer(customerId:String): Flow<PayoutCustomer?> {
        return flow {
            val response = paymentService.searchPayoutCustomer(customerId)
            Log.d("PayoutRepo","Search customer response : $response")
//            Timber.d("Search customer response : $response")
            emit(response.body())
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getBeneficiaryDetails(customerId:String,typeId:Int): Flow<List<Beneficiary>> {
        return flow {
            val response = paymentService.getBeneficiaryDetails(customerId, typeId)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun fetchPayoutCustomerTransactions(customerId:String,transType:Int): Flow<List<PayoutTransaction>> {
        return flow {
            val response = paymentService.fetchPayoutCustomerTransaction(customerId, transType)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun addNewBeneficiary(beneficiary: Beneficiary): Flow<Int> {
        return flow {
            val response = paymentService.addBeneficiary(beneficiary)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun initiatePayoutTransfer(
        beneficiaryId: String,
        paytmRequestData: PaytmRequestData
    ): Flow<PayoutTransactionResponse> {
        return flow {

            val response = when (paytmRequestData.transfermode) {
                PayoutTransferMode.BankTransfer -> paymentService.initiateBankTransfer(
                    beneficiaryId,
                    paytmRequestData
                )
                PayoutTransferMode.UpiTransfer -> paymentService.initiateUPITransfer(
                    beneficiaryId,
                    paytmRequestData
                )
                PayoutTransferMode.PaytmWalletTransfer -> paymentService.initiateWalletTransfer(
                    beneficiaryId,
                    paytmRequestData
                )
                else -> paymentService.initiateBankTransfer(beneficiaryId, paytmRequestData)
            }

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

/*    suspend fun initiateBankTransfer(beneficiaryId: String,paytmRequestData: PaytmRequestData): Flow<PayoutTransactionResponse> {
        return flow {
            val response = paymentService.initiateBankTransfer(beneficiaryId, paytmRequestData)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun initiateWalletTransfer(beneficiaryId: String,paytmRequestData: PaytmRequestData): Flow<PayoutTransactionResponse> {
        return flow {
            val response = paymentService.initiateWalletTransfer(beneficiaryId, paytmRequestData)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }

    suspend fun initiateUpiTransfer(beneficiaryId: String,paytmRequestData: PaytmRequestData): Flow<PayoutTransactionResponse> {
        return flow {
            val response = paymentService.initiateUPITransfer(beneficiaryId, paytmRequestData)

            emit(response)
        }.flowOn(Dispatchers.IO)
    }*/

    suspend fun getBankIFSC(): Flow<List<BankModel>> {
        return flow {
            val response = paymentService.getBankIFSC()

            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}