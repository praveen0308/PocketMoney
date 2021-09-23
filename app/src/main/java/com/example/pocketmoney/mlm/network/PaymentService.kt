package com.example.pocketmoney.mlm.network

import com.example.pocketmoney.mlm.model.payoutmodels.BankModel
import com.example.pocketmoney.mlm.model.payoutmodels.Beneficiary
import com.example.pocketmoney.mlm.model.payoutmodels.PayoutCustomer
import com.example.pocketmoney.mlm.model.payoutmodels.PayoutTransaction
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface PaymentService {
    @POST("Payment/AddPayoutCustomer")
    suspend fun addPayoutCustomer(
        @Body customer : PayoutCustomer
    ): Int

    @POST("Payment/SearchPayoutCustomer")
    suspend fun searchPayoutCustomer(
        @Query("customerid") customerId : String
    ): PayoutCustomer

    @POST("Payment/GetBeneficiaryDetails")
    suspend fun getBeneficiaryDetails(
        @Query("customerid") customerId : String,
        @Query("typeid") typeId : Int
    ): List<Beneficiary>

    @POST("Payment/FetchPayoutCustomerTransaction")
    suspend fun fetchPayoutCustomerTransaction(
        @Query("customerid") customerId : String,
        @Query("transType") transType : Int
    ): List<PayoutTransaction>

    @POST("Payment/AddBeneficiary")
    suspend fun addBeneficiary(
        @Body beneficiary: Beneficiary
    ): Int

    @POST("Payment/InitiateBankTransfer")
    suspend fun initiateBankTransfer(
        @Query("BeneficiaryID") beneficiaryId : String,
        @Body paytmRequestData: PaytmRequestData
    ): Int

    @POST("Payment/InitiateWalletTransfer")
    suspend fun initiateWalletTransfer(
        @Query("BeneficiaryID") beneficiaryId : String,
        @Body paytmRequestData: PaytmRequestData
    ): Int

    @POST("Payment/InitiateUPITransfer")
    suspend fun initiateUPITransfer(
        @Query("BeneficiaryID") beneficiaryId : String,
        @Body paytmRequestData: PaytmRequestData
    ): Int

    @GET("Payment/GetBankIFSC")
    suspend fun getBankIFSC(): List<BankModel>

}