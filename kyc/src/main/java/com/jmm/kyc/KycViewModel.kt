package com.jmm.kyc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.jmm.model.CustomerKYCModel
import com.jmm.model.VerifyPanServiceResponse
import com.jmm.repository.KycRepository
import com.jmm.repository.PanVerificationRepository
import com.jmm.repository.UserPreferencesRepository
import com.jmm.util.identify
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class KycViewModel @Inject constructor(
    private val panVerificationRepository: PanVerificationRepository,
    private val kycRepository: KycRepository,
    private val userPreferencesRepository: UserPreferencesRepository,


    ) : ViewModel() {
    val userId = userPreferencesRepository.userId.asLiveData()
    var selectedImage = ""

    // Address details
    var address1 = ""
    var address2 = ""
    var pincode = ""
    var state = ""
    var city = ""

    lateinit var selectedDocumentType : DocumentType
    val verifyPanPageState: MutableLiveData<KycPageState> = MutableLiveData(KycPageState.Idle)
    val uploadDocumentPageState: MutableLiveData<KycPageState> = MutableLiveData(KycPageState.Idle)

    fun verifyPanNumber(panNumber: String) {
        viewModelScope.launch {
            panVerificationRepository
                .verifyPanNumber(panNumber)
                .onStart {
                    verifyPanPageState.postValue(KycPageState.Processing("Verifying PAN..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        verifyPanPageState.postValue(KycPageState.Error(exception.identify()))
                        Timber.d("Error caused by >>>> verifyPanNumber")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {
                    if (it.success!!) {
                        verifyPanPageState.postValue(KycPageState.PanVerified(it))
                    } else {
                        verifyPanPageState.postValue(KycPageState.InvalidPan)
                    }
                }
        }
    }

    fun updateCustomerPanKycDetail(userId: String, panNumber: String, panName: String) {
        viewModelScope.launch {
            kycRepository
                .addCustomerKycPanDetail(userId, panNumber, panName)
                .onStart {
                    verifyPanPageState.postValue(KycPageState.Processing("Updating PAN details..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        verifyPanPageState.postValue(KycPageState.Error(exception.identify()))
                        Timber.d("Error caused by >>>> updateCustomerPanKycDetail")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {

                    verifyPanPageState.postValue(KycPageState.PanUpdatedSuccessfully)

                }
        }
    }

    fun uploadCustomerDocument(customerKYCModel: CustomerKYCModel) {
        viewModelScope.launch {
            kycRepository
                .addCustomerDetailDocument(customerKYCModel)
                .onStart {
                    uploadDocumentPageState.postValue(KycPageState.Processing("Uploading document..."))
                }
                .catch { exception ->
                    exception.message?.let {
                        uploadDocumentPageState.postValue(KycPageState.Error(exception.identify()))
                        Timber.d("Error caused by >>>> uploadCustomerDocument")
                        Timber.e("Exception : $it")
                    }
                }
                .collect {

                    uploadDocumentPageState.postValue(KycPageState.DocumentUpdatedSuccessfully)

                }
        }
    }
}

sealed class KycPageState {
    object Idle : KycPageState()
    object Loading : KycPageState()
    data class Error(val msg: String) : KycPageState()
    data class Processing(val msg: String) : KycPageState()
    object InvalidPan : KycPageState()
    data class PanVerified(val response: VerifyPanServiceResponse) : KycPageState()
    object PanUpdatedSuccessfully : KycPageState()
    object DocumentUpdatedSuccessfully : KycPageState()
}