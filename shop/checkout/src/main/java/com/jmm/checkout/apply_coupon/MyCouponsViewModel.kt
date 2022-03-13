package com.jmm.checkout.apply_coupon

import androidx.lifecycle.*
import com.jmm.model.shopping_models.DiscountCouponModel
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.shopping_repo.CheckoutRepository
import com.jmm.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyCouponsViewModel @Inject constructor(
    private val checkoutRepository: CheckoutRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {
    val userID = userPreferencesRepository.userId.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()
    private val _coupons = MutableLiveData<Resource<List<DiscountCouponModel>>>()
    val coupons: LiveData<Resource<List<DiscountCouponModel>>> = _coupons

    fun getCoupons(userId:String,roleId:Int) {
        viewModelScope.launch {
            checkoutRepository
                .getCouponDiscountList(userId,roleId)
                .onStart {
                    _coupons.postValue(Resource.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        _coupons.postValue(Resource.Error("Something went wrong !!!"))
                        Timber.d("Error caused by >>>> getCoupons")
                        Timber.e("Exception : $it")
                    }
                }

                .collect {
                    _coupons.postValue(Resource.Success(it))
                }
        }
    }
}