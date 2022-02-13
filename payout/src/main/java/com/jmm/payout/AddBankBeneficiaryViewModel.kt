package com.jmm.payout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jmm.model.payoutmodels.BankModel
import com.jmm.model.payoutmodels.Beneficiary
import com.jmm.repository.PayoutRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddBankBeneficiaryViewModel @Inject constructor(
    private val payoutRepository: PayoutRepository
) : ViewModel(){

    private val _banks = MutableLiveData<Resource<List<BankModel>>>()
    val banks: LiveData<Resource<List<BankModel>>> = _banks

    fun getBanks() {
        viewModelScope.launch {
            payoutRepository
                .getBankIFSC()
                .onStart {
                    _banks.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _banks.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _banks.postValue(Resource.Success(response))

                }
        }

    }

    private val _isBeneficiaryAdded = MutableLiveData<Resource<Int>>()
    val isBeneficiaryAdded: LiveData<Resource<Int>> = _isBeneficiaryAdded

    fun addBeneficiary(beneficiary: Beneficiary) {
        viewModelScope.launch {
            payoutRepository
                .addNewBeneficiary(beneficiary)
                .onStart {
                    _isBeneficiaryAdded.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _isBeneficiaryAdded.postValue(Resource.Error(it))
                    }
                }
                .collect { response->
                    _isBeneficiaryAdded.postValue(Resource.Success(response))

                }
        }

    }
}