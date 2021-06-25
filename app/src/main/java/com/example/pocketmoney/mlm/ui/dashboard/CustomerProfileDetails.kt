package com.example.pocketmoney.mlm.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.pocketmoney.databinding.FragmentCustomerProfileDetailsBinding
import com.example.pocketmoney.mlm.model.mlmModels.CustomerProfileModel
import com.example.pocketmoney.shopping.adapters.AddressAdapter
import com.example.pocketmoney.shopping.ui.CheckoutOrderInterface
import com.example.pocketmoney.shopping.ui.checkoutorder.AddressArgs
import com.example.pocketmoney.shopping.viewmodel.AddressViewModel
import com.example.pocketmoney.shopping.viewmodel.ShoppingAuthViewModel
import com.example.pocketmoney.utils.myEnums.ShoppingEnum
import java.io.Serializable

private const val ARG_PARAM1 = "param1"

class CustomerProfileDetails : Fragment() {

    private var customerProfileModel: CustomerProfileModel? = null
    //UI
    private var _binding: FragmentCustomerProfileDetailsBinding? = null
    private val binding get() = _binding!!

    //ViewModels
    private val shoppingAuthViewModel: ShoppingAuthViewModel by viewModels()
    private val addressViewModel: AddressViewModel by viewModels()


    // Adapters
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var checkoutOrderInterface: CheckoutOrderInterface

    // Variable
    private lateinit var userID: String
    private var source: ShoppingEnum?=null
    private val args: AddressArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            customerProfileModel = it.getSerializable(ARG_PARAM1) as CustomerProfileModel
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCustomerProfileDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            username.label.text = "Username"
            username.etData.setText(customerProfileModel?.FullName)
            sponsorId.label.text = "Sponsor ID"
            sponsorId.etData.setText(customerProfileModel?.SponsorID)

            sponsorName.label.text = "Sponsor Name"
            sponsorName.etData.setText(customerProfileModel?.SponsorName)

            businessRole.label.text = "Business Role"
            businessRole.etData.setText(customerProfileModel?.BusinessType)


            registeredOn.label.text = "Registered On"
            registeredOn.etData.setText(customerProfileModel?.RegisteredOn)

            location.label.text = "Address1"
            location.etData.setText(customerProfileModel?.Address1)

            emailAddress.label.text = "Email address"
            emailAddress.etData.setText(customerProfileModel?.EmailID)


            phoneNumber.label.text = "Phone Number"
            phoneNumber.etData.setText(customerProfileModel?.Mobile)

            birthday.label.text = "Date Of Birth"
            birthday.etData.setText(customerProfileModel?.DOB.toString())

        }
    }
    companion object {

        @JvmStatic
        fun newInstance(param1: Serializable) =
            CustomerProfileDetails().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }
}