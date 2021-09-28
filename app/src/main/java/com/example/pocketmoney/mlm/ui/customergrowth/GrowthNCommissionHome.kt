package com.example.pocketmoney.mlm.ui.customergrowth

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.FragmentGrowthNCommissionHomeBinding
import com.example.pocketmoney.mlm.adapters.GrowthNCommissionAdapter
import com.example.pocketmoney.mlm.adapters.IncomeAdapter
import com.example.pocketmoney.mlm.model.IncomeModel
import com.example.pocketmoney.mlm.model.mlmModels.*
import com.example.pocketmoney.mlm.viewmodel.CustomerGrowthNCommissionViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.NavigationEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GrowthNCommissionHome :
    BaseFragment<FragmentGrowthNCommissionHomeBinding>(FragmentGrowthNCommissionHomeBinding::inflate),
    GrowthNCommissionAdapter.GrowthNCommissionInterface {

    private val viewModel by viewModels<CustomerGrowthNCommissionViewModel>()

    private lateinit var incomeAdapter: IncomeAdapter
    private val incomeList = mutableListOf<IncomeModel>()
    // Variables
    private var userID: String = ""
    private var roleID: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setRvData()

    }

    override fun subscribeObservers() {
        viewModel.userID.observe(this, {
            userID = it

        })
        viewModel.roleID.observe(this, {
            roleID = it
            if (userID != "" && roleID != 0) {

                // For fetching growth
                /*val requestModel1 = CustomerRequestModel1(
                    UserID = userID.toLong(), RoleID = roleID
                )
                viewModel.getCustomerGrowth(requestModel1)*/
            // For fetching commission
                val requestModel2 = GrowthComissionRequestModel(
                    userID, roleID
                )
                viewModel.getGrowthCommission(requestModel2)

            }
            else{
                checkAuthorization()
            }
        })

        viewModel.customerGrowth.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        incomeList.clear()
                        incomeList.add(
                            IncomeModel(
                                "Growth",
                                    NavigationEnum.GROWTH,
                                getCustomerGrowthList(it)
                            )
                        )


                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })
        viewModel.growthCommission.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        incomeList.clear()
                        incomeList.add(
                            IncomeModel(
                                "Commission",
                                NavigationEnum.COMMISSION,
                                getCommissionMenuList(it)
                            )
                        )
                        incomeAdapter.setIncomeModelList(incomeList)
                    }
                    displayLoading(false)
                }
                Status.LOADING -> {
                    displayLoading(true)
                }
                Status.ERROR -> {
                    displayLoading(false)
                    _result.message?.let {
                        displayError(it)
                    }
                }
            }
        })
    }

    private fun setRvData() {
        incomeAdapter = IncomeAdapter(this)
        binding.rvData.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = incomeAdapter
        }
    }

    private fun getCommissionMenuList(response: GrowthCommissionResponse): List<GrowthCommissionDataModel> {

        val commission = response.GrowthCommission
        val menuList = mutableListOf<GrowthCommissionDataModel>()

        menuList.add(
            GrowthCommissionDataModel(
                0,
                "Direct Commission",
                commission.DirectCommCount,
                response.DirectCommHistory,
                type = NavigationEnum.COMMISSION,
                subType = NavigationEnum.DIRECT_COMMISSION
            )
        )
     /*   menuList.add(
            GrowthCommissionDataModel(
                1,
                "Update Commission",
                commission.UpdateCommCount,
                response.UpdateCommHistory,
                type = NavigationEnum.COMMISSION,
                subType = NavigationEnum.UPDATE_COMMISSION
            )
        )*/
        menuList.add(
            GrowthCommissionDataModel(
                2,
                "Service Commission",
                commission.ServiceCommCount,
                response.ServiceCommHistory,
                type = NavigationEnum.COMMISSION,
                subType = NavigationEnum.SERVICE_COMMISSION

            )
        )
        menuList.add(
            GrowthCommissionDataModel(
                3,
                "Shopping Commission",
                commission.ShoppingCommCount,
                response.ShoppingCommHistory,
                type = NavigationEnum.COMMISSION,
                subType = NavigationEnum.SHOPPING_COMMISSION
            )
        )

        return menuList
    }

    private fun getCustomerGrowthList(response: CustomerGrowthResponse): List<GrowthCommissionDataModel> {

        val growthData = response.GrowthData
        val menuList = mutableListOf<GrowthCommissionDataModel>()

        menuList.add(
            GrowthCommissionDataModel(
                0,
                "System Growth",
                growthData.GrowthCount.toDouble(),
                response.GrowthHistory,
                true,
                subType = NavigationEnum.SYSTEM_GROWTH
            )
        )
        menuList.add(
            GrowthCommissionDataModel(
                1,
                "Update Count",
                growthData.UpdateCount.toDouble(),
                response.UpdateHistory,
                subType = NavigationEnum.UPDATE_COUNT
            )
        )
        menuList.add(
            GrowthCommissionDataModel(
                2,
                "Renewal Count",
                growthData.RenewalCount.toDouble(),
                response.RenewalHistory,
                subType = NavigationEnum.RENEWAL_COUNT
            )
        )
        return menuList
    }


    override fun onItemClick(item: GrowthCommissionDataModel) {
        findNavController().navigate(
            GrowthNCommissionHomeDirections.actionGrowthNCommissionHomeToGrowthNCommissionList(
                item.subType
            )
        )
    }

}