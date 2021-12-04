package com.sampurna.pocketmoney.mlm.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.mlm.model.*
import com.sampurna.pocketmoney.mlm.repository.AccountRepository
import com.sampurna.pocketmoney.mlm.repository.UserPreferencesRepository
import com.sampurna.pocketmoney.mlm.repository.WalletRepository
import com.sampurna.pocketmoney.utils.ConnectionLiveData
import com.sampurna.pocketmoney.utils.identify
import com.sampurna.pocketmoney.utils.myEnums.MyEnums
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val accountRepository: AccountRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val walletRepository: WalletRepository,
    private val connectionLiveData: ConnectionLiveData

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userType = userPreferencesRepository.isActive.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()

    init {
        connectionLiveData.hasActiveObservers()
    }

    val homePageState: MutableLiveData<HomePageState> = MutableLiveData(HomePageState.Idle)

    fun getWalletBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 1)
                .onStart {
                    homePageState.postValue(HomePageState.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        homePageState.postValue(HomePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> getWalletBalance")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { _balance->
                    homePageState.postValue(HomePageState.GotWalletBalance(_balance))
                }
        }

    }

    fun getPCashBalance(userId: String, roleId: Int) {
        viewModelScope.launch {
            walletRepository
                .getWalletBalance(userId, roleId, 2)
                .onStart {
                    homePageState.postValue(HomePageState.Loading(true))
                }
                .catch { exception ->
                    exception.message?.let {
                        homePageState.postValue(HomePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> getPCashBalance")
                        Timber.e("Exception >>> ${exception.message}")
                    }
                }
                .collect { _balance ->
                    homePageState.postValue(HomePageState.GotPCashBalance(_balance))
                }
        }

    }

    /* private val _isAccountActive = MutableLiveData<Resource<Boolean>>()
     val isAccountActive: LiveData<Resource<Boolean>> = _isAccountActive

     fun checkIsAccountActive(id: String) {
         viewModelScope.launch {
             accountRepository
                 .isUserAccountActive(id)
                 .onStart {
                     _isAccountActive.postValue(Resource.Loading(true))
                 }
                 .catch { exception ->
                     exception.message?.let {
                         _isAccountActive.postValue(Resource.Error(it))
                     }
                 }
                 .collect { response ->
                     _isAccountActive.postValue(Resource.Success(response))
                 }
         }

     }*/

    fun prepareHomeData(): List<HomeParentModel> {

        val dataList: MutableList<HomeParentModel> = ArrayList()


        val services1: MutableList<ModelServiceView> = java.util.ArrayList()
        services1.add(
            ModelServiceView(
                "Add Money",
                R.drawable.ic_add_money,
                RechargeEnum.ADD_MONEY
            )
        )
        services1.add(
            ModelServiceView(
                "History",
                R.drawable.ic_history,
                RechargeEnum.PAYMENT_HISTORY
            )
        )
        services1.add(ModelServiceView("Wallet", R.drawable.ic_wallet, RechargeEnum.WALLET))
        services1.add(
            ModelServiceView(
                "Online Shopping",
                R.drawable.ic_shopping,
                RechargeEnum.SHOPPING
            )
        )


        val servicesCategory1 = ModelServiceCategory("My Pocket", services1)
        val model1 = HomeParentModel(
            MyEnums.SERVICES,
            servicesCategory1
        )

        val services2: MutableList<ModelServiceView> = java.util.ArrayList()
        services2.add(ModelServiceView("Prepaid", R.drawable.ic_prepaid, RechargeEnum.PREPAID))
        services2.add(ModelServiceView("Postpaid", R.drawable.ic_postpaid, RechargeEnum.POSTPAID))
        services2.add(ModelServiceView("DTH", R.drawable.ic_dth, RechargeEnum.DTH))
        services2.add(ModelServiceView("Landline", R.drawable.ic_landline, RechargeEnum.LANDLINE))
        services2.add(
            ModelServiceView(
                "Electricity",
                R.drawable.ic_electricity,
                RechargeEnum.ELECTRICITY
            )
        )
        services2.add(ModelServiceView("Water", R.drawable.ic_water, RechargeEnum.WATER))
        services2.add(ModelServiceView("Gas Cylinder Booking", R.drawable.ic_gas, RechargeEnum.GAS))
        services2.add(
            ModelServiceView(
                "Broadband",
                R.drawable.ic_broadband,
                RechargeEnum.BROADBAND
            )
        )
        services2.add(ModelServiceView("Loans", R.drawable.ic_loan, RechargeEnum.LOAN))
        services2.add(ModelServiceView("DMT", R.drawable.ic_dmt, RechargeEnum.DMT))
        services2.add(
            ModelServiceView(
                "Life Insurance",
                R.drawable.ic_life_insurance,
                RechargeEnum.LIFE_INSURANCE
            )
        )
        services2.add(ModelServiceView("FASTag", R.drawable.ic_fastag, RechargeEnum.FASTAG))

        val servicesCategory2 = ModelServiceCategory("Featured", services2)

        val model2 = HomeParentModel(MyEnums.SERVICES, servicesCategory2)


        val bannerList: MutableList<ModelBanner> = java.util.ArrayList()
        bannerList.add(ModelBanner("First", R.drawable.banner1, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner2, 1))
//        bannerList.add(ModelBanner("First", R.drawable.banner3, 1))
//        bannerList.add(ModelBanner("First", R.drawable.banner4, 1))
//        bannerList.add(ModelBanner("First", R.drawable.banner5, 1))

        val model3 = HomeParentModel(MyEnums.OFFERS, offerBannerList = bannerList)


        // Working Services
        val workingServices: MutableList<ModelServiceView> = java.util.ArrayList()
        workingServices.add(
            ModelServiceView(
                "Mobile Recharge",
                R.drawable.ic_prepaid,
                RechargeEnum.PREPAID
            )
        )
        workingServices.add(ModelServiceView("DTH", R.drawable.ic_dth, RechargeEnum.DTH))
        workingServices.add(
            ModelServiceView(
                "Play Recharge",
                R.drawable.ic_google_play,
                RechargeEnum.GOOGLE_PLAY_RECHARGE
            )
        )
//        workingServices.add(  ModelServiceView("Electricity", R.drawable.ic_electricity, RechargeEnum.ELECTRICITY))
        workingServices.add(
            ModelServiceView(
                "Send Money",
                R.drawable.ic_bank,
                RechargeEnum.SEND_MONEY
            )
        )
//        workingServices.add(ModelServiceView("Paytm Wallet Transfer", R.drawable.ic_paytm_logo, RechargeEnum.PAYTM_WALLET_TRANSFER))

        val workingServiceCategory = ModelServiceCategory("Featured", workingServices)

        val workingServiceCategoryModel = HomeParentModel(MyEnums.SERVICES, workingServiceCategory)


        // Coming Soon
        val comingSoonServices: MutableList<ModelServiceView> = java.util.ArrayList()
        /*comingSoonServices.add(
            ModelServiceView(
                "Postpaid",
                R.drawable.ic_postpaid,
                RechargeEnum.POSTPAID
            )
        )*/
        comingSoonServices.add(
            ModelServiceView(
                "Landline",
                R.drawable.ic_landline,
                RechargeEnum.LANDLINE
            )
        )
        /*comingSoonServices.add(
            ModelServiceView(
                "Electricity",
                R.drawable.ic_electricity,
                RechargeEnum.ELECTRICITY
            )
        )*/
        comingSoonServices.add(ModelServiceView("Water", R.drawable.ic_water, RechargeEnum.WATER))
        comingSoonServices.add(
            ModelServiceView(
                "Gas Cylinder Booking",
                R.drawable.ic_gas,
                RechargeEnum.GAS
            )
        )
        comingSoonServices.add(
            ModelServiceView(
                "Broadband",
                R.drawable.ic_broadband,
                RechargeEnum.BROADBAND
            )
        )
        comingSoonServices.add(ModelServiceView("Loans", R.drawable.ic_loan, RechargeEnum.LOAN))
        comingSoonServices.add(ModelServiceView("DMT", R.drawable.ic_dmt, RechargeEnum.DMT))
        comingSoonServices.add(
            ModelServiceView(
                "Life Insurance",
                R.drawable.ic_life_insurance,
                RechargeEnum.LIFE_INSURANCE
            )
        )
        comingSoonServices.add(
            ModelServiceView(
                "FASTag",
                R.drawable.ic_fastag,
                RechargeEnum.FASTAG
            )
        )

        val comingSoonServicesCategory = ModelServiceCategory("Coming Soon", comingSoonServices)

        val comingSoonServicesCategoryModel =
            HomeParentModel(MyEnums.SERVICES, comingSoonServicesCategory)




        dataList.add(model1)
        dataList.add(model3)
//        dataList.add(model2)
        dataList.add(workingServiceCategoryModel)
        dataList.add(comingSoonServicesCategoryModel)
        return dataList
    }

}

sealed class HomePageState {
    object Idle : HomePageState()
    data class Loading(val isLoading: Boolean) : HomePageState()
    data class Error(val msg: String) : HomePageState()
    data class GotWalletBalance(val balance: Double) : HomePageState()
    data class GotPCashBalance(val balance: Double) : HomePageState()
}