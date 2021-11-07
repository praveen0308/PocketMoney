package com.sampurna.pocketmoney.mlm.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sampurna.pocketmoney.databinding.FragmentCustomerAddressDetailsBinding
import com.sampurna.pocketmoney.mlm.model.mlmModels.CustomerProfileModel
import java.io.Serializable

private const val ARG_PARAM1 = "param1"


class CustomerAddressDetails : Fragment() {


    private var customerProfileModel: CustomerProfileModel? = null
    //UI
    private var _binding: FragmentCustomerAddressDetailsBinding? = null
    private val binding get() = _binding!!

    //ViewModels



    // Adapters


    // Variable



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
        _binding = FragmentCustomerAddressDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            country.label.text = "Country"
            country.etData.setText(customerProfileModel?.CountryID.toString())

            state.label.text = "State"
            state.etData.setText(customerProfileModel?.StateID.toString())

            city.label.text = "City"
            city.etData.setText(customerProfileModel?.CityID.toString())

            district.label.text = "District"
            district.etData.setText(customerProfileModel?.District.toString())


            location.label.text = "Address1"
            location.etData.setText(customerProfileModel?.Address1)



        }
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: Serializable) =
            CustomerAddressDetails().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                }
            }
    }
}