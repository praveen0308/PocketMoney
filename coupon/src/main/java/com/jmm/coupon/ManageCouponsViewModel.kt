package com.jmm.coupon

import androidx.lifecycle.*
import com.jmm.coupon.GenerateCoupon.Companion.CHECKING_WALLET_BALANCE
import com.jmm.coupon.GenerateCoupon.Companion.CHECKSUM_RECEIVED
import com.jmm.coupon.GenerateCoupon.Companion.ERROR
import com.jmm.coupon.GenerateCoupon.Companion.FAILED
import com.jmm.coupon.GenerateCoupon.Companion.GENERATING_COUPON
import com.jmm.coupon.GenerateCoupon.Companion.INITIATING_TRANSACTION
import com.jmm.coupon.GenerateCoupon.Companion.INSUFFICIENT_BALANCE
import com.jmm.coupon.GenerateCoupon.Companion.PENDING
import com.jmm.coupon.GenerateCoupon.Companion.PROCESSING
import com.jmm.coupon.GenerateCoupon.Companion.SUCCESSFUL
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.WalletType
import com.jmm.model.serviceModels.PaymentGatewayTransactionModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.repository.CustomerRepository
import com.jmm.repository.PaytmRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.WalletRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ManageCouponsViewModel @Inject constructor(
    private val customerRepository: CustomerRepository,
    val userPreferencesRepository: UserPreferencesRepository,
    private val paytmRepository: PaytmRepository,
    private val walletRepository: WalletRepository
): ViewModel(){

    val userId = userPreferencesRepository.userId.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()
    val progressStatus = MutableLiveData<Int>()
    var transactionToken = ""
    var amountPayable = 0.0
    var selectedPaymentMethod = PaymentEnum.WALLET

    val message = MutableLiveData<String>()

    val noOfCoupons = MutableLiveData(1)
    lateinit var paytmResponseModel: PaytmResponseModel
    fun incrementNoOfCoupons(){
        if (noOfCoupons.value!! < 10){
            noOfCoupons.postValue(noOfCoupons.value!!+1)
        }else{
            message.postValue("Maximum 10 coupons can be generated at one time !!!")
        }
    }
    fun decrementNoOfCoupons(){
        if (noOfCoupons.value!! > 1){
            noOfCoupons.postValue(noOfCoupons.value!!-1)
        }else{
            message.postValue("Minimum 1 coupon must be generated !!!")
        }
    }

    private val _generateCouponResponse = MutableLiveData<Resource<Int>>()
    val generateCouponResponse: LiveData<Resource<Int>> = _generateCouponResponse

    fun generateNewCoupons(userId: String,walletId:Int,count:Int) {
        viewModelScope.launch {
            customerRepository
                .generateNewCoupon(userId,walletId, count)
                .onStart {
                    progressStatus.postValue(GENERATING_COUPON)
                    _generateCouponResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        progressStatus.postValue(ERROR)
                        _generateCouponResponse.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while generating coupon !!!")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    if(selectedPaymentMethod==PaymentEnum.PAYTM){
                        addPaymentTransactionDetail(
                            PaymentGatewayTransactionModel(
                                UserId = userId,
                                OrderId = paytmResponseModel.ORDERID,
                                ReferenceTransactionId = paytmResponseModel.ORDERID,
                                ServiceTypeId = 1,
                                WalletTypeId = 2,
                                TxnAmount = paytmResponseModel.TXNAMOUNT,
                                Currency = paytmResponseModel.CURRENCY,
                                TransactionTypeId = 1,
                                IsCredit = false,
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
                        progressStatus.postValue(SUCCESSFUL)
                    }
                    _generateCouponResponse.postValue(Resource.Success(response))
                }
        }

    }

    private val _addPaymentTransResponse = MutableLiveData<Resource<String>>()
    val addPaymentTransResponse : LiveData<Resource<String>> = _addPaymentTransResponse

    fun addPaymentTransactionDetail(paymentGatewayTransactionModel: PaymentGatewayTransactionModel) {
        viewModelScope.launch {
            paytmRepository
                .addPaymentTransactionDetails(paymentGatewayTransactionModel)
                .onStart {
                    progressStatus.postValue(PROCESSING)
                    _addPaymentTransResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        progressStatus.postValue(ERROR)
                        _addPaymentTransResponse.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while adding payment transaction detail !!!")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    if (paytmResponseModel.STATUS == "SUCCESS"){
                        Timber.d("Payment Gateway response was successful.")
                        progressStatus.postValue(SUCCESSFUL)
                        _addPaymentTransResponse.postValue(Resource.Success(response))
                    }else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE"){
                        Timber.d("Payment Gateway response was failed.")
                        progressStatus.postValue(FAILED)
                        _addPaymentTransResponse.postValue(Resource.Error("Payment failed !!!"))

                    }else if (paytmResponseModel.STATUS == "PENDING"){
                        Timber.d("Payment Gateway response was pending.")
                        progressStatus.postValue(PENDING)
                    }
                }
        }
    }


    private val _walletBalance = MutableLiveData<Resource<Double>>()
    val walletBalance : LiveData<Resource<Double>> = _walletBalance

    fun getWalletBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    progressStatus.postValue(CHECKING_WALLET_BALANCE)
                    _walletBalance.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        progressStatus.postValue(ERROR)
                        _walletBalance.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while fetching wallet balance")
                        Timber.e("Exception : $it")

                    }
                }
                .collect { _balance->
                    if(_balance<amountPayable){
                        progressStatus.postValue(INSUFFICIENT_BALANCE)
                        _walletBalance.postValue(Resource.Error("Insufficient Wallet Balance !!!"))
                        Timber.d("Insufficient Wallet Balance !!!")
                    }else{
                        generateNewCoupons(userId,WalletType.Wallet.id,noOfCoupons.value!!)

                    }
                    _walletBalance.postValue(Resource.Success(_balance))
                }
        }
    }
    private val _pCash = MutableLiveData<Resource<Double>>()
    val pCash: LiveData<Resource<Double>> = _pCash

    fun getPCashBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    progressStatus.postValue(CHECKING_WALLET_BALANCE)
                    _pCash.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _pCash.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(ERROR)
                        Timber.d("Error occurred while fetching wallet balance")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { _balance->
                    if(_balance<amountPayable){
                        progressStatus.postValue(INSUFFICIENT_BALANCE)
                        _pCash.postValue(Resource.Error("Insufficient Wallet Balance !!!"))
                    }else{
                        generateNewCoupons(userId,WalletType.PCash.id,noOfCoupons.value!!)
                    }
                    _pCash.postValue(Resource.Success(_balance))
                }
        }

    }


    private val _checkSum = MutableLiveData<Resource<String>>()
    var checkSum : LiveData<Resource<String>> = _checkSum

    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {
        viewModelScope.launch {
            paytmRepository
                .initiateTransactionApi(paytmRequestData)
                .onStart {
                    progressStatus.postValue(INITIATING_TRANSACTION)
                    _checkSum.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _checkSum.postValue(Resource.Error("Something went wrong !!!"))
                        progressStatus.postValue(ERROR)
                        Timber.d("Error occurred while generation of checksum.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    progressStatus.postValue(CHECKSUM_RECEIVED)
                    transactionToken = response
                    _checkSum.postValue(Resource.Success(response))
                }
        }

    }

}