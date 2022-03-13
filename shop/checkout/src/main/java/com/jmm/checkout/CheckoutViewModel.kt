package com.jmm.checkout

import androidx.lifecycle.*
import com.jmm.model.myEnums.PaymentEnum
import com.jmm.model.myEnums.PaymentModes
import com.jmm.model.myEnums.PaymentStatus
import com.jmm.model.myEnums.WalletType
import com.jmm.model.serviceModels.PaymentGatewayTransactionModel
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.model.shopping_models.CartModel
import com.jmm.model.shopping_models.CustomerOrder
import com.jmm.model.shopping_models.DiscountModel
import com.jmm.model.shopping_models.ModelAddress
import com.jmm.repository.MailMessagingRepository
import com.jmm.repository.PaytmRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.WalletRepository
import com.jmm.repository.shopping_repo.AddressRepository
import com.jmm.repository.shopping_repo.CartRepository
import com.jmm.repository.shopping_repo.CheckoutRepository
import com.jmm.repository.shopping_repo.OrderRepository
import com.jmm.util.Resource
import com.jmm.util.identify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val checkoutRepository: CheckoutRepository,
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository,
    private val addressRepository: AddressRepository,
    private val walletRepository: WalletRepository,
    private val paytmRepository: PaytmRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val mailMessagingRepository: MailMessagingRepository
) : ViewModel() {

    lateinit var paytmResponseModel: PaytmResponseModel
    lateinit var customerOrder: CustomerOrder

    val userID = userPreferencesRepository.userId.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    val appliedCoupon :MutableLiveData<DiscountModel> = MutableLiveData(DiscountModel())
    val isValid :MutableLiveData<Boolean> = MutableLiveData(false)
    var isCouponApplied = false

//    val cartPageState: MutableLiveData<CartPageState> = MutableLiveData(CartPageState.Idle)
    val pageState: MutableLiveData<CheckoutPageState> = MutableLiveData(CheckoutPageState.Idle)
    private var orderNumber = ""
    fun getCartItems(userID: String) {
        viewModelScope.launch {

            cartRepository.getCartItems(userID)
                .onStart {
                    pageState.postValue(CheckoutPageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CheckoutPageState.Error(exception.identify()))
                    }
                }
                .collect { cartItems ->
                    if (cartItems.isEmpty()) pageState.postValue(CheckoutPageState.EmptyCart)
                    else {
                        pageState.postValue(CheckoutPageState.ReceivedCartItems(cartItems))
                        populatePrices(cartItems)
                    }

                }
        }
    }

    fun changeCartItemQuantity(type: Int, itemID: Int, userID: String) {
        viewModelScope.launch {

            cartRepository.changeItemQuantity(type, itemID, userID)
                .onStart {
                    pageState.postValue(CheckoutPageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CheckoutPageState.Error(exception.identify()))
                    }
                }
                .collect {
                    getCartItems(userID)
                }
        }
    }

    var shippingAddressList = mutableListOf<ModelAddress>()

    private val _customerAddressList: MutableLiveData<Resource<List<ModelAddress>>> =
        MutableLiveData()
    val customerAddressList: LiveData<Resource<List<ModelAddress>>> = _customerAddressList

    fun getCustomerAddressList(userId: String) {
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
                .collect { response ->
                    shippingAddressList.clear()
                    shippingAddressList.addAll(response)


                    if (response.isEmpty()){
                        pageState.postValue(CheckoutPageState.NoShippingAddressAvailable)
                        selectedAddressId.postValue(0)
                        isValid.postValue(false)
                    }
                    else {
                        shippingAddressList[0].isSelected = true
                        setShippingCharge(40.0)
                        _customerAddressList.postValue(Resource.Success(response))
                        selectedAddressId.postValue(response[0].AddressID)
                        isValid.postValue(true)

                    }

                }
        }
    }

    var selectedAddressId = MutableLiveData(0)

    val noOfItems = MutableLiveData(0)
    val originalAmount = MutableLiveData(0.0)
    val savingAmount = MutableLiveData(0.0)
    val totalAmount = MutableLiveData(0.0)
    val shippingCharge = MutableLiveData(0.0)
    var appliedDiscount = MutableLiveData(0.0)
    var tax = MutableLiveData(0.0)
    var grandTotalAmount = MutableLiveData(0.0)

    fun setShippingCharge(amount: Double) {
        shippingCharge.postValue(amount)
        grandTotalAmount.postValue(totalAmount.value!! + amount)
    }

    fun removeDiscount(){
        appliedCoupon.postValue(DiscountModel())
        appliedDiscount.postValue(0.0)
        grandTotalAmount.postValue(totalAmount.value)

    }
    fun setDiscount() {
        appliedCoupon.value?.let {

            appliedDiscount.postValue(it.Amount)

            val discountAmount = if (it.IsFixed) {
                it.Amount
            } else {
                (it.Amount * totalAmount.value!!) / 100
            }
            grandTotalAmount.postValue(totalAmount.value!! - discountAmount)
            return
        }

        grandTotalAmount.postValue(totalAmount.value)

    }

    private fun populatePrices(cartItems: List<CartModel>) {
        var itemQuantity = 0
        var productOldPrice = 0.0
        val saving: Double
        var totalPrice = 0.0

        for (item in cartItems) {
            itemQuantity += item.Quantity
            productOldPrice += item.Old_Price * item.Quantity
            totalPrice += item.Price * item.Quantity
        }

        saving = productOldPrice - totalPrice

        noOfItems.postValue(itemQuantity)
        originalAmount.postValue(productOldPrice)
        savingAmount.postValue(saving)
        totalAmount.postValue(totalPrice)
        grandTotalAmount.postValue(totalPrice+shippingCharge.value!!+tax.value!! - appliedDiscount.value!!)
    }

    fun addPaymentTransactionDetail(paymentGatewayTransactionModel: PaymentGatewayTransactionModel) {
        viewModelScope.launch {
            paytmRepository
                .addPaymentTransactionDetails(paymentGatewayTransactionModel)
                .onStart {
                    pageState.postValue(CheckoutPageState.Processing())
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CheckoutPageState.Error(exception.identify()))
                        Timber.d("Error occurred while adding PaymentTransactionDetail.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {
                    if (paytmResponseModel.STATUS == "SUCCESS") {
                        updatePaymentStatus(orderNumber, PaymentStatus.Paid.id)
                    } else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE") {
                        updatePaymentStatus(orderNumber, PaymentStatus.Failed.id)

                    }
                }
        }
    }
    fun createCustomerOrder(customerOrder: CustomerOrder) {
        viewModelScope.launch {
            checkoutRepository
                .createCustomerOrder(customerOrder)
                .onStart {
                    pageState.postValue(CheckoutPageState.Processing("Ordering..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CheckoutPageState.Error(exception.identify()))
                        Timber.d("Error occurred while creating order.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->

                    orderNumber = response
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
                                "Thank you for shopping with PocketMoney, your order placed successfully. Your order number is  $orderNumber you can track your order using PocketMoney, click https//wwww.pocketmoney.net.in"
                            sendWhatsappMessage(customerOrder.UserID!!, message1)
                        }
                    }
                }
        }

    }
    fun getWalletBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    pageState.postValue(CheckoutPageState.Processing())
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CheckoutPageState.Error(exception.identify()))
                        Timber.d("Error occurred while fetching wallet balance.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { _balance->
                    if(_balance<grandTotalAmount.value!!){
                        pageState.postValue(CheckoutPageState.InsufficientBalance)
                        Timber.d("Insufficient Wallet Balance !!!")
                    }else{
                        customerOrder = CustomerOrder(
                            ShippingAddressId = selectedAddressId.value,
                            UserID = userId,
                            Total = totalAmount.value,
                            Discount = appliedDiscount.value,
                            Shipping = shippingCharge.value,
                            Tax = tax.value,
                            GrandTotal = grandTotalAmount.value,
                            Promo = appliedCoupon.value!!.Code,
                            PaymentStatusId = PaymentStatus.Paid.id,
                            WalletTypeId = WalletType.Wallet.id,
                            PaymentMode = PaymentModes.Wallet.id
                        )
                        createCustomerOrder(customerOrder)
                    }
                }
        }

    }
    fun getPCashBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    pageState.postValue(CheckoutPageState.Processing())
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CheckoutPageState.Error(exception.identify()))
                        Timber.d("Error occurred while fetching wallet balance.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { _balance->
                    if(_balance<grandTotalAmount.value!!){
                        pageState.postValue(CheckoutPageState.InsufficientBalance)
                        Timber.d("Insufficient Wallet Balance !!!")
                    }else{
                        customerOrder = CustomerOrder(
                            ShippingAddressId = selectedAddressId.value,
                            UserID = userId,
                            Total = totalAmount.value,
                            Discount = appliedDiscount.value,
                            Shipping = shippingCharge.value,
                            Tax = tax.value,
                            GrandTotal = grandTotalAmount.value,
                            Promo = appliedCoupon.value!!.Code,
                            PaymentStatusId = PaymentStatus.Paid.id,
                            WalletTypeId = WalletType.PCash.id,
                            PaymentMode = PaymentModes.PCash.id
                        )
                        createCustomerOrder(customerOrder)
                    }
                }
        }

    }
    private fun updatePaymentStatus(orderNumber:String, paymentStatusId:Int) {
        viewModelScope.launch {
            checkoutRepository
                .updatePaymentStatus(orderNumber, paymentStatusId)
                .onStart {
                    pageState.postValue(CheckoutPageState.Processing())
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CheckoutPageState.Error(exception.identify()))
                        Timber.d("Error occurred while updating payment status.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->

                    if (paytmResponseModel.STATUS == "SUCCESS") {
                        val message1 =
                            "Thank you for shopping with Pocket Money, your order placed successfully. Your order number is  $orderNumber you can track your order using pocketmoney, click https//wwww.pocketmoney.net.in"
                        sendWhatsappMessage(userID.value!!, message1)
                    } else if (paytmResponseModel.STATUS == "FAILED" || paytmResponseModel.STATUS == "FAILURE") {
                        val message =
                            "Thank you for shopping with pocketmoney, your order payment has been failed. please revisit pocketmoney to place order again, click https//wwww.pocketmoney.net.in"
                        sendWhatsappMessage(userID.value!!, message)
                    }
                }
        }
    }

    private fun sendWhatsappMessage(mobileNumber:String, message:String) {

        viewModelScope.launch {
            mailMessagingRepository
                .sendWhatsappMessage(mobileNumber, message)
                .onStart {
                    pageState.postValue(CheckoutPageState.Processing())
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CheckoutPageState.Error(exception.identify()))
                        Timber.d("Error occurred while sending whatsapp message.")
                        Timber.e("Exception : $it")

                    }
                }
                .collect { response->
                    pageState.postValue(CheckoutPageState.OrderSuccessful(orderNumber))
                    /*if (response) pageState.postValue(CheckoutPageState.MessageSent)
                    else pageState.postValue(CheckoutPageState.MessageFailed)*/
                }
        }
    }
    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {
        viewModelScope.launch {
            paytmRepository
                .initiateTransactionApi(paytmRequestData)
                .onStart {
                    pageState.postValue(CheckoutPageState.InitiatingTransaction)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(CheckoutPageState.Error(exception.identify()))
                        Timber.d("Error occurred while generating checksum.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    pageState.postValue(CheckoutPageState.ReceivedChecksum(response))
                }
        }

    }
}



sealed class CheckoutPageState {
    object Idle : CheckoutPageState()
    object Loading : CheckoutPageState()
    object EmptyCart : CheckoutPageState()
    object NoShippingAddressAvailable : CheckoutPageState()
    data class ReceivedCartItems(val cartItems: List<CartModel>) : CheckoutPageState()
    data class Error(val msg: String) : CheckoutPageState()
    data class Processing(val msg: String="Processing...") : CheckoutPageState()

    object InsufficientBalance : CheckoutPageState()

    object InitiatingTransaction : CheckoutPageState()

    data class ReceivedChecksum(val checksum:String) : CheckoutPageState()
    object RequestingGateway : CheckoutPageState()
    object CancelledGateway : CheckoutPageState()
    data class ReceivedGatewayResponse(val paytmResponseModel: PaytmResponseModel) : CheckoutPageState()

    data class ReceivedUpdatedPaymentStatus(val status: Boolean) : CheckoutPageState()

    data class OrderSuccessful(val orderNumber: String) : CheckoutPageState()



}