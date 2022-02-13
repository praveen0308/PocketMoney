package com.jmm.play_recharge

import androidx.lifecycle.*
import com.jmm.repository.*
import com.jmm.model.serviceModels.MobileRechargeModel
import com.jmm.model.serviceModels.PaymentGatewayTransactionModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.WalletType
import com.jmm.util.identify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PlayRechargeViewModel @Inject constructor(
    private val rechargeRepository: RechargeRepository,
    private val walletRepository: WalletRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val serviceRepository: ServiceRepository,
    private val paytmRepository: PaytmRepository
):ViewModel(){
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    var rechargeAmount = MutableLiveData<Int>()
    val rechargeMobileNo  = MutableLiveData<String>()
    lateinit var recharge : MobileRechargeModel
    lateinit var rechargeApiResponse : MobileRechargeModel
    val playRechargePageState: MutableLiveData<PlayRechargePageState> = MutableLiveData(
        PlayRechargePageState.Idle
    )
    lateinit var paytmResponseModel: PaytmResponseModel
    val pageState:MutableLiveData<PlayRechargePageState> = MutableLiveData(PlayRechargePageState.Idle)

    fun getWalletBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    pageState.postValue(PlayRechargePageState.Processing("Checking wallet balance..."))

                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(PlayRechargePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> getWalletBalance")
                        Timber.e("Exception >>> ${exception.message}")

                    }
                }
                .collect { _balance->
                    if(_balance<rechargeAmount.value!!){
                        pageState.postValue(PlayRechargePageState.InsufficientBalance)
                    }else{
                        recharge = MobileRechargeModel()
                        recharge.UserID = userId
                        recharge.MobileNo = rechargeMobileNo.value!!
                        recharge.ServiceTypeID = 1
                        recharge.WalletTypeID = WalletType.Wallet.id
                        recharge.OperatorCode = "11"
                        recharge.RechargeAmt = rechargeAmount.value!!.toDouble()
                        recharge.ServiceField1 = ""
                        recharge.ServiceProviderID = 3
                        recharge.Status = "Received"
                        recharge.TransTypeID = 20

                        addUsedServiceDetail(recharge)

                    }

                }
        }
    }


    fun getPCashBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    pageState.postValue(PlayRechargePageState.Processing("Checking wallet balance..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(PlayRechargePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> getPCashBalance")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { _balance->
                    if(_balance<rechargeAmount.value!!){
                        pageState.postValue(PlayRechargePageState.InsufficientBalance)
                    }else{
                        recharge = MobileRechargeModel()
                        recharge.UserID = userId
                        recharge.MobileNo = rechargeMobileNo.value!!
                        recharge.ServiceTypeID = 1
                        recharge.WalletTypeID = WalletType.PCash.id
                        recharge.OperatorCode = "11"
                        recharge.RechargeAmt = rechargeAmount.value!!.toDouble()
                        recharge.ServiceField1 = ""
                        recharge.ServiceProviderID = 3
                        recharge.Status = "Received"
                        recharge.TransTypeID = 20

                        addUsedServiceDetail(recharge)
                    }
                }
        }

    }

    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {
        viewModelScope.launch {
            paytmRepository
                .initiateTransactionApi(paytmRequestData)
                .onStart {
                    pageState.postValue(PlayRechargePageState.Processing("Initiating transaction..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(PlayRechargePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> initiateTransactionApi")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response->
                    pageState.postValue(PlayRechargePageState.ReceivedCheckSum(response))
//                    transactionToken = response

                }
        }

    }

    //    step 1
    fun addUsedServiceDetail(usedServiceDetailModel: MobileRechargeModel) {
        viewModelScope.launch {
            serviceRepository
                .addUsedServiceDetail(usedServiceDetailModel)
                .onStart {
                    pageState.postValue(PlayRechargePageState.Processing("Processing..."))

                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(PlayRechargePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> addUsedServiceDetail")
                        Timber.e("Exception >>> ${exception.message}")

                    }
                }
                .collect { response->
                    if (response>0){
                        getUsedServiceRequestId(usedServiceDetailModel.UserID!!,rechargeMobileNo.value!!)
                    }

                }
        }

    }

    // step 2

    fun getUsedServiceRequestId(userId: String,mobileNo: String) {
        viewModelScope.launch {
            serviceRepository
                .getUsedServiceRequestId(userId, mobileNo)
                .onStart {
                    pageState.postValue(PlayRechargePageState.Processing("Processing..."))

                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(PlayRechargePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> getUsedServiceRequestId")
                        Timber.e("Exception >>> ${exception.message}")

                    }
                }
                .collect { response->
                    recharge.RequestID = response

                    if (serviceRepository.selectedPaymentMethod== PaymentEnum.PAYTM){
                        addPaymentTransactionDetail(
                            PaymentGatewayTransactionModel(
                                UserId = userId,
                                OrderId = paytmResponseModel.ORDERID,
                                ReferenceTransactionId = response,   // request id
                                ServiceTypeId = 1,
                                WalletTypeId = WalletType.OnlinePayment.id,
                                TxnAmount = paytmResponseModel.TXNAMOUNT,
                                Currency = paytmResponseModel.CURRENCY,
                                TransactionTypeId = 20,
                                IsCredit =  false,
                                TxnId = paytmResponseModel.TXNID,
                                Status = paytmResponseModel.STATUS,
                                RespCode = paytmResponseModel.RESPCODE,
                                RespMsg = paytmResponseModel.RESPMSG,
                                BankTxnId = paytmResponseModel.BANKTXNID,
                                BankName = paytmResponseModel.GATEWAYNAME,
                                PaymentMode = paytmResponseModel.PAYMENTMODE
                            )
                        )
                    }else{
                        callSampurnaRechargeService(recharge)
                    }
                }
        }

    }


    // step 3

    fun callSampurnaRechargeService(mobileRechargeModel: MobileRechargeModel) {
        viewModelScope.launch {
            serviceRepository
                .callSamupurnaPlayRechargeService(mobileRechargeModel)
                .onStart {
                    pageState.postValue(PlayRechargePageState.Processing("Requesting recharge..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(PlayRechargePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> callSampurnaRechargeService")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response->
                    rechargeApiResponse = response
                    when(response.Status){
                        "SUCCESS"->{
                            if (serviceRepository.selectedPaymentMethod== PaymentEnum.PAYTM){
                                if (paytmResponseModel.PAYMENTMODE != "UPI"){
                                    val amountDeduct = (paytmResponseModel.TXNAMOUNT?.toDouble() ?: 0.0) * 2 / 100
                                    walletChargeDeduct(mobileRechargeModel.UserID!!,
                                        WalletType.PCash.id,amountDeduct,recharge.RequestID!!,18)
                                }else{
                                    pageState.postValue(
                                        PlayRechargePageState.RechargeSuccessful(
                                            rechargeApiResponse
                                        )
                                    )

                                }
                            }else{
                                pageState.postValue(
                                    PlayRechargePageState.RechargeSuccessful(
                                        rechargeApiResponse
                                    )
                                )
                            }

                        }
                        "FAILED"->{
                            pageState.postValue(
                                PlayRechargePageState.RechargeFailed(
                                    rechargeApiResponse
                                )
                            )
                        }
                        "PENDING"->{
                            pageState.postValue(
                                PlayRechargePageState.RechargePending(
                                    rechargeApiResponse
                                )
                            )
                        }
                    }


                }
        }

    }

    fun addPaymentTransactionDetail(paymentGatewayTransactionModel: PaymentGatewayTransactionModel) {
        viewModelScope.launch {
            paytmRepository
                .addPaymentTransactionDetails(paymentGatewayTransactionModel)
                .onStart {
                    pageState.postValue(PlayRechargePageState.Processing("Processing..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(PlayRechargePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> addPaymentTransactionDetail")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response->
                    if (paytmResponseModel.STATUS == "SUCCESS"){
                        Timber.d("Payment Gateway response was successful.")
                        callSampurnaRechargeService(recharge)

                    }else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE"){
                        Timber.d("Payment Gateway response was failed.")
                        pageState.postValue(PlayRechargePageState.RechargeFailed(rechargeApiResponse))
                    }

                }
        }
    }

    fun walletChargeDeduct(userId: String,walletId:Int,amount:Double,requestId:String,serviceId:Int) {

        viewModelScope.launch {
            walletRepository
                .walletChargeDeduction(userId, walletId, amount, requestId, serviceId)
                .onStart {
                    pageState.postValue(PlayRechargePageState.Processing("Processing..."))

                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(PlayRechargePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> walletChargeDeduct")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { response->
                    pageState.postValue(PlayRechargePageState.RechargeSuccessful(rechargeApiResponse))

                }
        }
    }
}



sealed class PlayRechargePageState{
    object Idle : PlayRechargePageState()
    object Loading : PlayRechargePageState()
    data class Error(val msg:String): PlayRechargePageState()
    data class Processing(val msg:String): PlayRechargePageState()


    object InsufficientBalance : PlayRechargePageState()

    data class ReceivedCheckSum(val token: String) : PlayRechargePageState()
    object RequestingGateway : PlayRechargePageState()
    object WaitingForGatewayResponse : PlayRechargePageState()
    data class ReceivedGatewayResponse(val response: PaytmResponseModel) : PlayRechargePageState()

    object SubmittedPaymentTransDetail : PlayRechargePageState()

    data class RechargeSuccessful(val response: MobileRechargeModel): PlayRechargePageState()
    data class RechargeFailed(val response: MobileRechargeModel): PlayRechargePageState()
    data class RechargePending(val response: MobileRechargeModel): PlayRechargePageState()

    /*object MessageSent : PlayRechargePageState()
    object MessageFailed : PlayRechargePageState()*/


}