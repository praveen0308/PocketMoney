package com.sampurna.pocketmoney.mlm.network

import com.sampurna.pocketmoney.mlm.model.TransactionDetailModel
import retrofit2.http.POST
import retrofit2.http.Query

interface WalletService {

    @POST("Wallet/ViewTransactionsDetails")
    suspend fun viewTransactionDetails(
        @Query("id") transactionId: String
    ): TransactionDetailModel
}