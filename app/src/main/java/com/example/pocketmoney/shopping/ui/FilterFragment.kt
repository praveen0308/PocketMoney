package com.example.pocketmoney.shopping.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.pocketmoney.databinding.FragmentFilterBinding
import com.example.pocketmoney.shopping.adapters.MasterPaymentMethodAdapter
import com.example.pocketmoney.shopping.ui.checkoutorder.PaymentArgs
import com.example.pocketmoney.shopping.viewmodel.AddressViewModel
import com.example.pocketmoney.shopping.viewmodel.CartViewModel
import com.example.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.example.pocketmoney.utils.ProgressBarHandler
import com.example.pocketmoney.utils.myEnums.ShoppingEnum

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class FilterFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    //UI
    private var _binding: FragmentFilterBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBarHandler: ProgressBarHandler

    //ViewModels
    private val shoppingAuthViewModel: ShoppingAuthViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    // Adapters
    private lateinit var masterPaymentMethodAdapter: MasterPaymentMethodAdapter
    private lateinit var checkoutOrderInterface: CheckoutOrderInterface

    // Variable
    private lateinit var userID: String
    private var selectedAddressId: Int = 0
    private var shippingCharge:Double=0.0
    private var source: ShoppingEnum? = null
    private val args: PaymentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        _binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                FilterFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}