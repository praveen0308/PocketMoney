package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentRechargePaymentSheetBinding
import com.example.pocketmoney.mlm.model.serviceModels.UsedServiceDetailModel
import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.shopping.adapters.PaymentMethodAdapter
import com.example.pocketmoney.shopping.model.ModelPaymentMethod
import com.example.pocketmoney.utils.BaseBottomSheetDialogFragment
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.PaymentEnum

class RechargePaymentSheet : BaseBottomSheetDialogFragment<FragmentRechargePaymentSheetBinding>(FragmentRechargePaymentSheetBinding::inflate),
    PaymentMethodAdapter.PaymentMethodInterface {


    private lateinit var paymentMethodAdapter: PaymentMethodAdapter
    private val viewModel by activityViewModels<MobileRechargeViewModel>()

    private var selectedPaymentMethod = PaymentEnum.WALLET

    private lateinit var userId : String
    private var roleId = 0
    private var amountPayable = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRvPaymentMethods()
        binding.btnMakePayment.setOnClickListener {
            when(selectedPaymentMethod){
                PaymentEnum.WALLET->{
                    // check if wallet balance > payment amount
                    viewModel.getWalletBalance(userId,roleId)
                }
                PaymentEnum.PCASH->{
                    // check if wallet balance > payment amount
                    viewModel.getPCashBalance(userId,roleId)

                }
                PaymentEnum.GATEWAY->{
                    // show some message like redirecting to payment gateway
                    it.isEnabled = false
//                    startPayment()
                }

            }
        }
    }

    private fun setupRvPaymentMethods() {
        paymentMethodAdapter = PaymentMethodAdapter(getPaymentMethods(),this)
        binding.rvPaymentMethods.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = paymentMethodAdapter
        }

    }



    private fun getPaymentMethods(): MutableList<Any> {

        val paymentMethods = mutableListOf<Any>()
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.WALLET, "Wallet", R.drawable.ic_logo))
        paymentMethods.add(ModelPaymentMethod(PaymentEnum.PCASH, "PCash", R.drawable.ic_wallet))
        paymentMethods.add(
            ModelPaymentMethod(
                PaymentEnum.PAYTM,
                "Other Payment",
                R.drawable.ic_paytm_logo,
                true
            )
        )



        return paymentMethods
    }


    override fun subscribeObservers() {
        viewModel.userId.observe(this,{
            userId = it
        })

        viewModel.userRoleID.observe(this,{
            roleId = it
        })
        viewModel.selectedPaymentMethod.observe(viewLifecycleOwner,{

        })

//        viewModel.selectedRechargePlan.observe(viewLifecycleOwner,{})

        viewModel.walletBalance.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it < viewModel.selectedRechargePlan.value!!.rs!!.toDouble()){
                            showToast("Insufficient Wallet Balance !!!")
                            binding.btnMakePayment.isEnabled = true

                        }else{
                            /*viewModel.addUsedServiceDetail(
                                UsedServiceDetailModel(
                                    UserID = userId,
                                    MobileNo = viewModel.selectedContact.value!!.contactNumber,
                                    ServiceTypeID = 1,
                                    WalletTypeID = 1,
                                    OperatorCode = viewModel.selectedMobileOperator.value!!.operatorCode,
                                    RechargeAmt = viewModel.selectedRechargePlan.value!!.rs!!.toDouble(),
                                    ServiceField1 = "",
                                    ServiceProviderID = 3,
                                    Status = "Received",
                                    TransTypeID = 9
                                )
                            )*/
                        }
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

        viewModel.pCash.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it < viewModel.selectedRechargePlan.value!!.rs!!.toDouble()){
                            showToast("Insufficient Wallet Balance !!!")
                            binding.btnMakePayment.isEnabled = true

                        }else{
                            /*viewModel.addUsedServiceDetail(
                                UsedServiceDetailModel(
                                    UserID = userId,
                                    MobileNo = viewModel.selectedContact.value!!.contactNumber,
                                    ServiceTypeID = 1,
                                    WalletTypeID = 4,
                                    OperatorCode = viewModel.selectedMobileOperator.value!!.operatorCode,
                                    RechargeAmt = viewModel.selectedRechargePlan.value!!.rs!!.toDouble(),
                                    ServiceField1 = "",
                                    ServiceProviderID = 3,
                                    Status = "Received",
                                    TransTypeID = 9
                                )
                            )*/
                        }
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


        viewModel.progressStatus.observe(viewLifecycleOwner,{
//            displayLoading(it)
        })
    }

    override fun onPaymentModeSelected(item: ModelPaymentMethod) {
        selectedPaymentMethod = item.method
    }


}