package com.jmm.mlm.viewmodel

import androidx.lifecycle.*
import com.jmm.mlm.R
import com.jmm.model.*
import com.jmm.model.mlmModels.CustomerAuthBalanceResponse
import com.jmm.model.myEnums.MyEnums
import com.jmm.model.shopping_models.BannerModel
import com.jmm.repository.AccountRepository
import com.jmm.repository.IResource
import com.jmm.repository.UserPreferencesRepository
import com.jmm.repository.WalletRepository
import com.jmm.repository.shopping_repo.StoreRepository
import com.jmm.util.connection.ConnectionLiveData
import com.jmm.util.identify
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
    private val storeRepository: StoreRepository,
    private val connectionLiveData: ConnectionLiveData

) : ViewModel() {
    val loginId = userPreferencesRepository.loginId.asLiveData()
    val userId = userPreferencesRepository.userId.asLiveData()
    val userType = userPreferencesRepository.isActive.asLiveData()
    val userName = userPreferencesRepository.userName.asLiveData()
    val userRoleID = userPreferencesRepository.userRoleId.asLiveData()
    val storeBanners = storeRepository.getBanners().asLiveData()

    init {
        connectionLiveData.hasActiveObservers()
    }

    val homePageState: MutableLiveData<HomePageState> = MutableLiveData(HomePageState.Idle)

    fun getCustomerBalanceWithAuth(userId:String,roleId:Int) {
        viewModelScope.launch {
            walletRepository
                .getCustomerBalanceWithAuth(userId, roleId)
                .onStart {
                    homePageState.postValue(HomePageState.Loading)
                }
                .catch { exception ->
                    exception.message?.let {
                        homePageState.postValue(HomePageState.Error(exception.identify()))
                        Timber.e("Error caused by >>> getCustomerBalanceWithAuth")
                        Timber.e("Exception >>> ${exception.message}")

                    }
                }
                .collect { response ->
                    homePageState.postValue(HomePageState.ReceivedUserData(response))
                }
        }
    }
    private val _accountData: MutableLiveData<IResource<CustomerDashboardDataModel>> =
        MutableLiveData<IResource<CustomerDashboardDataModel>>()
    val accountData: LiveData<IResource<CustomerDashboardDataModel>> = _accountData
    fun getDashboardData(userId: String, roleId: Int) {
        viewModelScope.launch {
            accountRepository
                .getDashboardData(userId, roleId)
                .onStart {
                    _accountData.postValue(IResource.Loading())
                }
                .catch {exception->
                    _accountData.postValue(IResource.Error(exception))
                }
                .collect {
                    _accountData.postValue(IResource.Success(it.data!!))
                }
        }

    }


    fun prepareHomeData(banners:MutableList<BannerModel>): List<HomeParentModel> {

        val itemList= mutableListOf<HomeParentModel>()

        /***
         * This is MyPocket Services on Home Screen
         * **/
        val myPocketServices= arrayListOf<ModelServiceView>()
        myPocketServices.add(
            ModelServiceView(
                "Add Money",
                R.drawable.ic_add_money,
                RechargeEnum.ADD_MONEY
            )
        )
        myPocketServices.add(
            ModelServiceView(
                "History",
                R.drawable.ic_history,
                RechargeEnum.PAYMENT_HISTORY
            )
        )
        myPocketServices.add(ModelServiceView("Wallet", R.drawable.ic_wallet, RechargeEnum.WALLET))
        myPocketServices.add(
            ModelServiceView(
                "Online Shopping",
                R.drawable.ic_shopping,
                RechargeEnum.SHOPPING
            )
        )


        val myPocketServicesCat1 = ModelServiceCategory("My Pocket", myPocketServices)
        val cat1 = HomeParentModel(MyEnums.SERVICES, myPocketServicesCat1)

        /***
         * This banner slider on home page
         * **/
        /*val bannerList= arrayListOf<ModelBanner>()
        bannerList.add(ModelBanner("First", R.drawable.banner1, 1))
        bannerList.add(ModelBanner("First", R.drawable.banner2, 1))
*/
        val model3 = HomeParentModel(MyEnums.OFFERS, offerBannerList = banners)


/*
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
*/




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
        comingSoonServices.add(
            ModelServiceView(
                "Electricity",
                R.drawable.ic_electricity,
                RechargeEnum.ELECTRICITY
            )
        )
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
//        comingSoonServices.add(ModelServiceView("DMT", R.drawable.ic_dmt, RechargeEnum.DMT))
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




        itemList.add(cat1)
        itemList.add(model3)
//        itemList.add(model2)
        itemList.add(workingServiceCategoryModel)
        itemList.add(comingSoonServicesCategoryModel)
        return itemList
    }

}

sealed class HomePageState {
    object Idle : HomePageState()
    object Loading : HomePageState()
    data class Error(val msg: String) : HomePageState()
    data class ReceivedBanners(val banners:List<BannerModel>) : HomePageState()
    data class ReceivedUserData(val data :CustomerAuthBalanceResponse) : HomePageState()
}