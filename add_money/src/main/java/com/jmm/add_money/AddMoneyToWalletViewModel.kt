package com.jmm.add_money

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jmm.model.serviceModels.PaytmRequestData
import com.jmm.model.serviceModels.PaytmResponseModel
import com.jmm.repository.PaytmRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.WalletRepository
import com.jmm.util.identify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AddMoneyToWalletViewModel @Inject constructor(

    private val userPreferencesRepository: UserPreferencesRepository,
    private val paytmRepository: PaytmRepository,
    private val walletRepository: WalletRepository

) : ViewModel() {
    val userId = userPreferencesRepository.userId.asLiveData()
    val pageState : MutableLiveData<AddMoneyToWalletPageState> = MutableLiveData(AddMoneyToWalletPageState.Initial)

    fun initiateTransactionApi(paytmRequestData: PaytmRequestData) {
        viewModelScope.launch {
            paytmRepository
                .initiateTransactionApi(paytmRequestData,true)
                .onStart {
                    pageState.postValue(AddMoneyToWalletPageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(AddMoneyToWalletPageState.Error(exception.identify()))
                        Timber.d("Error occurred while generation of checksum.")
                        Timber.e("Exception : $it")
                    }
                }
                .collect { response->
                    pageState.postValue(AddMoneyToWalletPageState.ReceivedChecksum(response))
                }
        }
    }

    fun addMoneyToWallet(data: PaytmResponseModel) {
        viewModelScope.launch {
            walletRepository
                .addMoneyToWallet(data)
                .onStart {
                    pageState.postValue(AddMoneyToWalletPageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        pageState.postValue(AddMoneyToWalletPageState.Error(exception.identify()))
                        Timber.d("Error caused by >>>> addMoneyToWallet")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {
                    if (it){
                        pageState.postValue(AddMoneyToWalletPageState.Success)
                    }
                    else{
                        pageState.postValue(AddMoneyToWalletPageState.Failed)
                    }
                }
        }
    }



}

sealed class AddMoneyToWalletPageState{
    object Initial:AddMoneyToWalletPageState()
    object Loading:AddMoneyToWalletPageState()
    data class Error(val msg:String):AddMoneyToWalletPageState()
    data class Processing(val msg:String):AddMoneyToWalletPageState()
    object Success : AddMoneyToWalletPageState()
    object Failed : AddMoneyToWalletPageState()

    object InitiatingTransaction : AddMoneyToWalletPageState()
    data class ReceivedChecksum(val checksum:String) : AddMoneyToWalletPageState()
    object RequestingGateway : AddMoneyToWalletPageState()
    object CancelledGateway : AddMoneyToWalletPageState()
    data class ReceivedGatewayResponse(val paytmResponseModel: PaytmResponseModel) : AddMoneyToWalletPageState()

}