package com.sampurna.pocketmoney.shopping.viewmodel

import androidx.lifecycle.*
import com.sampurna.pocketmoney.common.MailMessagingRepository
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.sampurna.pocketmoney.mlm.model.serviceModels.PaytmResponseModel
import com.sampurna.pocketmoney.mlm.repository.PaytmRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.mlm.repository.WalletRepository
import com.sampurna.pocketmoney.shopping.model.CartModel
import com.sampurna.pocketmoney.shopping.model.CustomerOrder
import com.sampurna.pocketmoney.shopping.model.DiscountModel
import com.sampurna.pocketmoney.shopping.model.ModelAddress
import com.sampurna.pocketmoney.shopping.repository.AddressRepository
import com.sampurna.pocketmoney.shopping.repository.CartRepository
import com.sampurna.pocketmoney.shopping.repository.CheckoutRepository
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.ADDING_PAYMENT_TRANS_DETAIL
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.CHECKING_WALLET_BALANCE
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.CHECKSUM_RECEIVED
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.CREATING_ORDER_NUMBER
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.ERROR
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.INITIATING_TRANSACTION
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.INSUFFICIENT_BALANCE
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.MESSAGE_SENT
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.SENDING_WHATSAPP_MESSAGE
import com.sampurna.pocketmoney.shopping.ui.checkoutorder.NewCheckout.Companion.UPDATING_PAYMENT_STATUS
import com.sampurna.pocketmoney.utils.Resource
import com.sampurna.pocketmoney.utils.myEnums.PaymentEnum
import com.sampurna.pocketmoney.utils.myEnums.PaymentModes
import com.sampurna.pocketmoney.utils.myEnums.PaymentStatus
import com.sampurna.pocketmoney.utils.myEnums.WalletType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CheckoutOrderViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val checkoutRepository: CheckoutRepository,
    private val paytmRepository: PaytmRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val cartRepository: CartRepository,
    private val walletRepository: WalletRepository,
    private val mailMessagingRepository: MailMessagingRepository

): ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val paymentMethod = MutableLiveData(PaymentEnum.WALLET)

    val activeStep = MutableLiveData(0)

    lateinit var paytmResponseModel: PaytmResponseModel
    lateinit var customerOrder: CustomerOrder

    var itemQuantity = 0
    var productOldPrice = 0.0
    var totalAmount = 0.0
    var tax = 0.0

    var mShippingCharge = 0.0
    var saving = 0.0
    var grandTotal =0.0
    var discountAmount =0.0

    var selectedAddressId = MutableLiveData(0)

    val discountCoupon = ""

    var mOrderNumber = ""

    var mCartItemList= mutableListOf<CartModel>()

    var progressStatus = MutableLiveData<Int>()
    var transactionToken = ""
    fun setActiveStep(step:Int){
        activeStep.postValue(step)
    }

    private val _selectedAddress = MutableLiveData<ModelAddress>()
    val selectedAddress: LiveData<ModelAddress> = _selectedAddress

    fun setSelectedAddress(modelAddress: ModelAddress){
        _selectedAddress.postValue(modelAddress)
    }

    val amountPayable = MutableLiveData<Double>()


    fun setPayableAmount(amount:Double){

    }


    var shippingAddressList = mutableListOf<ModelAddress>()

    private val _customerAddressList: MutableLiveData<Resource<List<ModelAddress>>> = MutableLiveData()
    val customerAddressList: LiveData<Resource<List<ModelAddress>>> = _customerAddressList

    fun getCustomerAddressList(userId: String){
        viewModelScope.launch {
            addressRepository.getCustomerAddressByUserId(userId)
                .onStart {
                    _customerAddressList.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _customerAddressList.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    shippingAddressList.clear()
                    shippingAddressList.addAll(response)
                    _customerAddressList.postValue(Resource.Success(response))
                }
        }
    }

    private val _orderNumber = MutableLiveData<Resource<String>>()
    var orderNumber : LiveData<Resource<String>> = _orderNumber

    fun createCustomerOrder(customerOrder: CustomerOrder) {
        viewModelScope.launch {
            checkoutRepository
                .createCustomerOrder(customerOrder)
                .onStart {
                    progressStatus.postValue(CREATING_ORDER_NUMBER)
                    _orderNumber.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        progressStatus.postValue(ERROR)
                        _orderNumber.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while creating order.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    _orderNumber.postValue(Resource.Success(response))
                    if (response != null) {
                        mOrderNumber = response
                        when (checkoutRepository.selectedPaymentMethod) {
                            PaymentEnum.PAYTM -> {
                                addPaymentTransactionDetail(
                                    PaymentGatewayTransactionModel(
                                        UserId = customerOrder.UserID,
                                        OrderId = paytmResponseModel.ORDERID,
                                        ReferenceTransactionId = response,
                                        ServiceTypeId = 1,
                                        WalletTypeId = WalletType.OnlinePayment.id,
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


                            }
                            else -> {
                                val message1 =
                                    "Thank you for shopping with PocketMoney, your order placed successfully. Your order number is  $mOrderNumber you can track your order using PocketMoney, click https//wwww.pocketmoney.net.in"
                                sendWhatsappMessage(customerOrder.UserID!!, message1)
                            }
                        }
                    } else {
                        progressStatus.postValue(ERROR)
                        _orderNumber.postValue(Resource.Error("Something went wrong. Try Again !!!"))
                    }
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
                    progressStatus.postValue(ADDING_PAYMENT_TRANS_DETAIL)
                    _addPaymentTransResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        progressStatus.postValue(ERROR)
                        _addPaymentTransResponse.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while adding PaymentTransactionDetail.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->

                    _addPaymentTransResponse.postValue(Resource.Success(response))
                    if (paytmResponseModel.STATUS == "SUCCESS") {
                        updatePaymentStatus(mOrderNumber, PaymentStatus.Paid.id)
                    } else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE") {
                        updatePaymentStatus(mOrderNumber, PaymentStatus.Failed.id)

                    }
                }
        }
    }

    private val _shippingCharge = MutableLiveData<Resource<Double>>()
    val shippingCharge : LiveData<Resource<Double>> = _shippingCharge

    fun getShippingCharge(addressId:Int,userId: String) {
        viewModelScope.launch {
            addressRepository
                .getShippingCharge(addressId,userId)
                .onStart {
                    _shippingCharge.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _shippingCharge.postValue(Resource.Error(it))
                    }
                }
                .collect { charge->
                    _shippingCharge.postValue(Resource.Success(charge))
                }
        }

    }

    private val _cartItems: MutableLiveData<Resource<List<CartModel>>> = MutableLiveData()
    val cartItems: LiveData<Resource<List<CartModel>>> = _cartItems
    fun getCartItems(userID:String){
        viewModelScope.launch {

            cartRepository.getCartItems(userID)
                .onStart {
                    _cartItems.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _cartItems.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _cartItems.postValue(Resource.Success(response))
                }
        }
    }

    private val _cartItemQuantity: MutableLiveData<Resource<Int>> = MutableLiveData()
    val cartItemQuantity: LiveData<Resource<Int>> = _cartItemQuantity

    fun changeCartItemQuantity(type:Int,itemID:Int,userID:String){
        viewModelScope.launch {

            cartRepository.changeItemQuantity(type, itemID, userID)
                .onStart {
                    _cartItemQuantity.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _cartItemQuantity.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _cartItemQuantity.postValue(Resource.Success(response))
                }
        }
    }



    private val _checkSum = MutableLiveData<Resource<String>>()
    val checkSum : LiveData<Resource<String>> = _checkSum

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
                        Timber.d("Error occurred while generating checksum.")
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


    private val _walletBalance = MutableLiveData<Resource<Double>>()
    val walletBalance : LiveData<Resource<Double>> = _walletBalance

    private val _pCash = MutableLiveData<Resource<Double>>()
    val pCash: LiveData<Resource<Double>> = _pCash

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
                        Timber.d("Error occurred while fetching wallet balance.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { _balance->
                    if(_balance<grandTotal){
                        progressStatus.postValue(INSUFFICIENT_BALANCE)
                        _walletBalance.postValue(Resource.Error("Insufficient Wallet Balance !!!"))
                        Timber.d("Insufficient Wallet Balance !!!")
                    }else{
                        customerOrder = CustomerOrder(
                            ShippingAddressId = selectedAddressId.value,
                            UserID = userId,
                            Total = grandTotal,
                            Discount = discountAmount,
                            Shipping = mShippingCharge,
                            Tax = tax,
                            GrandTotal = grandTotal,
                            Promo = discountCoupon,
                            PaymentStatusId = PaymentStatus.Paid.id,
                            WalletTypeId = WalletType.Wallet.id,
                            PaymentMode = PaymentModes.Wallet.id
                        )
                        createCustomerOrder(customerOrder)
                    }
                    _walletBalance.postValue(Resource.Success(_balance))
                }
        }

    }


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
                        progressStatus.postValue(ERROR)
                        _pCash.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while fetching wallet balance.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { _balance->
                    if(_balance<grandTotal){
                        progressStatus.postValue(INSUFFICIENT_BALANCE)
                        _walletBalance.postValue(Resource.Error("Insufficient Wallet Balance !!!"))
                        Timber.d("Insufficient Wallet Balance !!!")
                    }else{
                        customerOrder = CustomerOrder(
                            ShippingAddressId = selectedAddressId.value,
                            UserID = userId,
                            Total = grandTotal,
                            Discount = discountAmount,
                            Shipping = mShippingCharge,
                            Tax = tax,
                            GrandTotal = grandTotal,
                            Promo = discountCoupon,
                            PaymentStatusId = PaymentStatus.Paid.id,
                            WalletTypeId = WalletType.PCash.id,
                            PaymentMode = PaymentModes.PCash.id
                        )
                        createCustomerOrder(customerOrder)
                    }
                    _pCash.postValue(Resource.Success(_balance))
                }
        }

    }


    private val _isPaymentStatusUpdated = MutableLiveData<Resource<Boolean>>()
    val isPaymentStatusUpdated : LiveData<Resource<Boolean>> = _isPaymentStatusUpdated

    fun updatePaymentStatus(orderNumber:String,paymentStatusId:Int) {
        viewModelScope.launch {
            checkoutRepository
                .updatePaymentStatus(orderNumber, paymentStatusId)
                .onStart {
                    progressStatus.postValue(UPDATING_PAYMENT_STATUS)
                    _isPaymentStatusUpdated.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        progressStatus.postValue(ERROR)
                        _isPaymentStatusUpdated.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while updating payment status.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    _isPaymentStatusUpdated.postValue(Resource.Success(response))
                    if (paytmResponseModel.STATUS == "SUCCESS") {
                        val message1 =
                            "Thank you for shopping with Pocket Money, your order placed successfully. Your order number is  $mOrderNumber you can track your order using pocketmoney, click https//wwww.pocketmoney.net.in"
                        sendWhatsappMessage(userId.value!!, message1)
                    } else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE") {
                        val message =
                            "Thank you for shopping with pocketmoney, your order payment has been failed. please revisit pocketmoney to place order again, click https//wwww.pocketmoney.net.in"
                        sendWhatsappMessage(userId.value!!, message)
                    }
                }
        }
    }

    private val _isMessageSent = MutableLiveData<Resource<Boolean>>()
    val isMessageSent : LiveData<Resource<Boolean>> = _isMessageSent

    fun sendWhatsappMessage(mobileNumber:String,message:String) {

        viewModelScope.launch {
            mailMessagingRepository
                .sendWhatsappMessage(mobileNumber, message)
                .onStart {
                    progressStatus.postValue(SENDING_WHATSAPP_MESSAGE)
                    _isMessageSent.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        progressStatus.postValue(ERROR)
                        _isMessageSent.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error occurred while sending whatsapp message.")
                        Timber.e("Exception : $it")

                    }
                }
                .collect { response->
                    progressStatus.postValue(MESSAGE_SENT)
                    _isMessageSent.postValue(Resource.Success(response))
                }
        }
    }

    private val _isValidCoupon = MutableLiveData<Resource<Boolean>>()
    val isValidCoupon : LiveData<Resource<Boolean>> = _isValidCoupon

    fun validateCouponCode(couponCode:String) {

        viewModelScope.launch {
            checkoutRepository
                .validateCoupon(couponCode)
                .onStart {
                    _isValidCoupon.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isValidCoupon.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _isValidCoupon.postValue(Resource.Success(response))
                }
        }
    }


    private val _couponDetail = MutableLiveData<Resource<DiscountModel>>()
    val couponDetail : LiveData<Resource<DiscountModel>> = _couponDetail

    fun getCouponDetails(couponCode:String) {

        viewModelScope.launch {
            checkoutRepository
                .getDiscountDetails(couponCode)
                .onStart {
                    _couponDetail.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _couponDetail.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _couponDetail.postValue(Resource.Success(response))
                }
        }
    }

}