package com.example.pocketmoney.common

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentPaymentMethodsBinding
import com.example.pocketmoney.utils.BaseFragment


class PaymentMethods : BaseFragment<FragmentPaymentMethodsBinding>(FragmentPaymentMethodsBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun subscribeObservers() {

    }

}