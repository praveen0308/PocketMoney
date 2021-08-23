package com.example.pocketmoney.shopping.viewmodel

import androidx.lifecycle.*
import com.example.pocketmoney.mlm.model.serviceModels.PaymentGatewayTransactionModel
import com.example.pocketmoney.mlm.model.serviceModels.PaytmRequestData
import com.example.pocketmoney.mlm.repository.PaytmRepository
import com.example.pocketmoney.mlm.repository.UserPreferencesRepository
import com.example.pocketmoney.mlm.repository.WalletRepository
import com.example.pocketmoney.shopping.model.CartModel
import com.example.pocketmoney.shopping.model.CustomerOrder
import com.example.pocketmoney.shopping.model.ModelAddress
import com.example.pocketmoney.shopping.repository.AddressRepository
import com.example.pocketmoney.shopping.repository.CartRepository
import com.example.pocketmoney.shopping.repository.CheckoutRepository
import com.example.pocketmoney.utils.Resource
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutOrderViewModel @Inject constructor(
    private val addressRepository: AddressRepository,
    private val checkoutRepository: CheckoutRepository,
    private val paytmRepository: PaytmRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val cartRepository: CartRepository,
    private val walletRepository: WalletRepository
): ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()


    val paymentMethod = MutableLiveData(PaymentEnum.WALLET)

    val activeStep = MutableLiveData(0)


    // Order fields
    var totalAmount = 0.0
    var mShippingCharge = 0.0
    var tax = 0.0
    var discountAmount = 0.0
    var discountCoupon = ""
    var grandTotal = 0.0

    fun setActiveStep(step:Int){
        activeStep.postValue(step)
    }

    private val _selectedAddress = MutableLiveData<ModelAddress>()
    val selectedAddress: LiveData<ModelAddress> = _selectedAddress

    fun setSelectedAddress(modelAddress: ModelAddress){
        _selectedAddress.postValue(modelAddress)
    }

    private val _amountPayable = MutableLiveData<Double>()
    val amountPayable: LiveData<Double> = _amountPayable

    fun setPayableAmount(amount:Double){
        _amountPayable.postValue(amount)
    }


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
                    _customerAddressList.postValue(Resource.Success(response))
                }
        }
    }

    private val _orderNumber = MutableLiveData<Resource<String>>()
    val orderNumber : LiveData<Resource<String>> = _orderNumber

    fun createCustomerOrder(customerOrder: CustomerOrder) {

        viewModelScope.launch {

            checkoutRepository
                .createCustomerOrder(customerOrder)
                .onStart {
                    _orderNumber.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _orderNumber.postValue(Resource.Error(it))
                    }
                }
                .collect { status->
                    _orderNumber.postValue(Resource.Success(status))
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
                    _addPaymentTransResponse.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _addPaymentTransResponse.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _addPaymentTransResponse.postValue(Resource.Success(response))
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
                    mShippingCharge = charge
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
                    _checkSum.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _checkSum.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
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
                    _walletBalance.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _walletBalance.postValue(Resource.Error(it))
                    }
                }
                .collect { _balance->
                    _walletBalance.postValue(Resource.Success(_balance))
                }
        }

    }


    fun getPCashBalance(userId: String, roleId: Int) {

        viewModelScope.launch {

            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    _pCash.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _pCash.postValue(Resource.Error(it))
                    }
                }
                .collect { _balance->
                    _pCash.postValue(Resource.Success(_balance))
                }
        }

    }

}