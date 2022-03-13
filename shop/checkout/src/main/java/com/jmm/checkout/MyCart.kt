package com.jmm.checkout

import android.os.Bundle
import android.view.View
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.jmm.checkout.databinding.FragmentMyCartBinding
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyCart : BaseFragment<FragmentMyCartBinding>(FragmentMyCartBinding::inflate){

    private val viewModel by activityViewModels<CheckoutViewModel>()
    private lateinit var userID: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        binding.root.children.forEach { it.isVisible = false }
        binding.btnCheckout.setOnClickListener {
            findNavController().navigate(MyCartDirections.actionMyCartToFinalCheckout())
        }
    }
    override fun subscribeObservers() {

        viewModel.userID.observe(this) {
            if (it.isNullOrEmpty()) {
                checkAuthorization()
            } else {
                userID = it
                viewModel.getCartItems(userID)
            }

        }
        viewModel.pageState.observe(this){state->
            displayLoading(false)
            when(state){
                CheckoutPageState.EmptyCart ->{
                    binding.root.children.forEach {
                        it.isVisible = false
                    }
                    binding.emptyView.isVisible =true
                }
                is CheckoutPageState.Error -> {
                    showToast(state.msg)
                }
                CheckoutPageState.Idle -> {

                }
                CheckoutPageState.Loading ->displayLoading(true)
            }

        }

    }


}