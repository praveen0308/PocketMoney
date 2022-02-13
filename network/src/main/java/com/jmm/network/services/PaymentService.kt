package com.jmm.network.services

import com.jmm.model.payoutmodels.*
import com.jmm.model.serviceModels.PaytmRequestData
import retrofit2.Response
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
    ): Response<PayoutCustomer?>

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
    ): PayoutTransactionResponse

    @POST("Payment/InitiateWalletTransfer")
    suspend fun initiateWalletTransfer(
        @Query("BeneficiaryID") beneficiaryId : String,
        @Body paytmRequestData: PaytmRequestData
    ): PayoutTransactionResponse

    @POST("Payment/InitiateUPITransfer")
    suspend fun initiateUPITransfer(
        @Query("BeneficiaryID") beneficiaryId : String,
        @Body paytmRequestData: PaytmRequestData
    ): PayoutTransactionResponse

    @GET("Payment/GetBankIFSC")
    suspend fun getBankIFSC(): List<BankModel>

}