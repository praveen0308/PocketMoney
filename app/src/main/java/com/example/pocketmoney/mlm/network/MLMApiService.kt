package com.example.pocketmoney.mlm.network

import com.example.pocketmoney.mlm.model.*
import com.example.pocketmoney.mlm.model.mlmModels.*
import com.example.pocketmoney.mlm.model.serviceModels.*
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.http.*

interface MLMApiService {

    // Account
    @GET("Account/UserLogin")
    suspend fun doLogin(
        @Query("username") userName: String,
        @Query("password") password: String
    ): UserModel


    @GET("Account/IsUserAccountActive")
    suspend fun getUserAccountStatus(
        @Query("id") id: String
    ): Boolean


    @GET("Account/ValidateDuplicate")
    suspend fun validateDuplicateAccount(
        @Query("tblName") tblName: String,
        @Query("colName") colName: String,
        @Query("value") value: String,
    ): Boolean

    @GET("Account/GetUserMenus")
    suspend fun getUserMenus(
        @Query("userId") userId: String
    ): List<UserMenu>


    @GET("")
    suspend fun getOperatorList(
        @Query("operatorOf") operatorOf: String
    ): List<ModelOperator>

    // Wallet
    @POST("Wallet/GetAllTransactionHistory")
    suspend fun getAllTransactionHistory(
        @Body requestModel: CustomerRequestModel1
    ): List<TransactionModel>


    @POST("Wallet/GetTransactionHistory")
    suspend fun getTransactionHistory(
        @Body requestModel1: CustomerRequestModel1
    ): List<TransactionModel>


    @GET("Wallet/GetWalletBalance")
    suspend fun getWalletBalance(
        @Query("userId") userId: Double,
        @Query("roleId") roleId: Int,
        @Query("walletType") walletType: Int
    ): Double

    @GET("Wallet/GetTransactionTypes")
    suspend fun getTransactionTypes(): List<TransactionTypeModel>


    //Customer
    @GET("Customer/GetCustomerDetails")
    suspend fun getCustomerDetails(
        @Query("userId") userId: String
    ): CustomerDetailResponse

    @POST("Customer/GetCustomerGrowth")
    suspend fun getCustomerGrowth(
        @Body requestModel1: CustomerRequestModel1
    ): CustomerGrowthResponse


    @POST("Customer/GetGrowthCommission")
    suspend fun getGrowthCommission(
        @Body requestModel: GrowthComissionRequestModel
    ): GrowthCommissionResponse

    @POST("Customer/GetCustomerDirectCommssion")
    suspend fun getCustomerDirectCommission(
        @Body requestModel: GrowthComissionRequestModel
    ): List<CommissionHistoryModel>

    @POST("Customer/GetCustomerServiceCommssion")
    suspend fun getCustomerServiceCommission(
        @Body requestModel: GrowthComissionRequestModel
    ): List<CommissionHistoryModel>

    @POST("Customer/GetCustomerUpdateCommssion")
    suspend fun getCustomerUpdateCommission(
        @Body requestModel: GrowthComissionRequestModel
    ): List<CommissionHistoryModel>

    @POST("Customer/GetCustomerShoppingCommission")
    suspend fun getCustomerShoppingCommission(
        @Body requestModel: GrowthComissionRequestModel
    ): List<CommissionHistoryModel>

    @POST("Customer/GetCouponList")
    suspend fun getCouponList(
        @Query("userId") userId: String,
        @Query("roleId") roleId: Int,
        @Query("from") fromDate: String,
        @Query("to") toDate: String,
    ): List<CouponModel>


    @POST("Customer/GetComplainsHistory")
    suspend fun getComplaintHistory(
        @Query("userId") userId: String,
        @Query("roleId") roleId: Int,
        @Query("from") fromDate: String,
        @Query("to") toDate: String,
        @Query("filter") filter: String,
        @Query("condition") condition: String,
    ): List<CustomerComplaintModel>


    @GET("Customer/GetSponsorName")
    suspend fun getSponsorName(
        @Query("id") sponsorId: String
    ): String

    @GET("Customer/GetUserName")
    suspend fun getUserName(
        @Query("id") id: String
    ): String

    @POST("Customer/GetCustomerProfileInfo")
    suspend fun getCustomerProfileInfo(
        @Query("id") id: String
    ): CustomerProfileModel

    @POST("Customer/AddCustomerDetails")
    suspend fun addCustomerDetails(
        @Body customerDetail: ModelCustomerDetail
    ): Boolean


    //Recharge
    @POST("Recharge/FetchMobileCircleOperator")
    suspend fun fetchOperatorNCircleOfMobile(
        @Query("mobileno") mobileNumber: String
    ): MobileCircleOperator

    @POST("Recharge/FetchSimplePlan")
    suspend fun fetchMobileSimplePlan(
        @Query("circle") circle: String,
        @Query("mobileOperator") mobileOperator: String
    ): SimplePlanResponse

    @POST("Recharge/FetchSpecialPlan")
    suspend fun fetchMobileSpecialPlan(
        @Query("mobileno") mobileNumber: String,
        @Query("mobileOperator") mobileOperator: String
    ): List<MobileOperatorPlan>


//    @POST("Recharge/FetchSpecialPlan")
//    suspend fun fetchMobileSpecialPlan(
//            @Query("mobileno") mobileNumber: String,
//            @Query("mobileOperator") mobileOperator:String
//    ): List<SpecialPlan>


    @GET("Recharge/GetServiceCircle")
    suspend fun getMobileServiceCircle(
        @Query("providerId") providerId: Int
    ): List<IdNameModel>

    @GET("Recharge/GetServiceOperator")
    suspend fun getMobileServiceOperator(
        @Query("ServiceTypeId") serviceTypeId: Int,
        @Query("ServiceProviderId") serviceProviderId: Int,
        @Query("CircleCode") circleCode: String?
    ): List<IdNameModel>

    @POST("Payment/InitiateTransactionAPI")
    suspend fun initiateTransactionApi(
        @Body paytmRequestData: PaytmRequestData
    ): String

    @Headers("Content-Type: application/json")
    @POST("Wallet/TransferB2BBalance")
    suspend fun b2bWalletTransfer(
        @Body requestData : JsonObject
    ):Int


}

