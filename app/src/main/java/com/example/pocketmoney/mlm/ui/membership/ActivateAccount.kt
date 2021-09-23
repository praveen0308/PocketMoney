package com.example.pocketmoney.mlm.ui.membership

import android.os.Bundle
import androidx.activity.viewModels
import com.example.pocketmoney.R
import com.example.pocketmoney.common.PaymentMethods
import com.example.pocketmoney.databinding.ActivityActivateAccountBinding
import com.example.pocketmoney.mlm.viewmodel.ActivateAccountViewModel
import com.example.pocketmoney.utils.BaseActivity
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.PaymentEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ActivateAccount : BaseActivity<ActivityActivateAccountBinding>(ActivityActivateAccountBinding::inflate),
    PaymentMethods.PaymentMethodsInterface {

    private val viewModel by viewModels<ActivateAccountViewModel>()

    private var selectedMethod = 1
    private var userId = ""
    private var roleId = 0
    private val activationCharge = 300.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.btnActivate.setOnClickListener {
            when(selectedMethod){
                1->{
                    val sheet = ActivateAccUsingCoupon()
                    sheet.show(supportFragmentManager,sheet.tag)
                }
                2->{
                    val sheet = PaymentMethods(this)
                    sheet.show(supportFragmentManager,sheet.tag)
                }
            }
        }

        binding.rgPaymentChoices.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rb_coupon->{
                    selectedMethod = 1
                }
                R.id.rb_payment->{
                    selectedMethod = 2
                }
            }
        }
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this,{
            userId = it
        })
        viewModel.userRoleID.observe(this,{
            roleId = it
        })

        viewModel.addPaymentTransResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {

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


        viewModel.walletBalance.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it < activationCharge){
                            showToast("Insufficient Wallet Balance !!!")


                        }
                        else{
                            viewModel.activateAccountByPayment(userId,1)
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
                        if (it < activationCharge){
                            showToast("Insufficient PCash Balance !!!")
                        } else{
                            viewModel.activateAccountByPayment(userId,1)
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

        viewModel.isActivationSuccessful.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        viewModel.checkIsAccountActive(userId)
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


        viewModel.isAccountActive.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        if (it) {
                            val msg= "Congratulations! You have upgraded your pocketmoney account to PRO account. Start using the pocketmoney services and enjoy the unlimited benefits,Click https://www.pocketmoney.net.in"
                            viewModel.sendWhatsappMessage(userId,msg)
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



    }

    override fun onPaymentMethodSelected(method: PaymentEnum) {

        when(method){
            PaymentEnum.WALLET->{
                viewModel.getWalletBalance(userId,roleId)
            }
            PaymentEnum.PCASH->{
                viewModel.getPCashBalance(userId,roleId)
            }
            PaymentEnum.GATEWAY->{

            }
            else->{

            }
        }
    }


}