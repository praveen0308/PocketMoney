package com.sampurna.pocketmoney.shopping.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sampurna.pocketmoney.databinding.FragmentAddAddressBinding
import com.sampurna.pocketmoney.shopping.model.ModelCity
import com.sampurna.pocketmoney.shopping.model.ModelState
import com.sampurna.pocketmoney.shopping.viewmodel.AddressViewModel
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


@AndroidEntryPoint
class AddAddress : Fragment(), ApplicationToolbar.ApplicationToolbarListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    //UI
    private var _binding: FragmentAddAddressBinding? = null
    private val binding get() = _binding!!

    //ViewModels
    private val viewModel: AddressViewModel by viewModels()

    // Adapters
//    private lateinit var stateArrayAdapter: ArrayAdapter()

    // Variable
    private lateinit var userID: String
    private var selectedCountryId: Int=0
    private var selectedStateId: Int=0
    private var selectedCityId: Int=0



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
        _binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeObservers()
        viewModel.getAllStates()
        binding.toolbar.setApplicationToolbarListener(this)
        binding.btnSaveAddress.setOnClickListener {

            val fullName = binding.etFullName.text.toString()
            val mobileNumber = binding.etMobileNumber.text.toString()
            val address = binding.etAddress.text.toString()
            val locality = binding.etLocality.text.toString()
            val street = binding.etStreet.text.toString()
            val countryId = selectedCountryId
            val stateId = selectedStateId
            val cityId = selectedCityId
//
//            val modelAddress = ModelAddress(
//                    Name = fullName,
//                    MobileNo = mobileNumber,
//                    Address1 = address,
//                    Locality = locality,
//                    Street = street,
//                    CountryID = countryId,
//                    StateID = stateId,
//
//            )

        }
    }

    private fun populateStateAdapter(stateList:MutableList<ModelState>){
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, stateList)
        //actv is the AutoCompleteTextView from your layout file
        binding.actvState.threshold = 1 //start searching for values after typing first character
        binding.actvState.setAdapter(arrayAdapter)

        binding.actvState.setOnItemClickListener { parent, view, position, id ->
            val state = parent.getItemAtPosition(position) as ModelState
            viewModel.getCitiesByStateCode(state.StateCode)
//            Toast.makeText(context, "Selected State Id".plus(modelACTV.itemId), Toast.LENGTH_SHORT).show()
        }
    }

    private fun populateCityAdapter(cityList:MutableList<ModelCity>){
        val arrayAdapter = ArrayAdapter(requireContext(), android.R.layout.select_dialog_item, cityList)
        //actv is the AutoCompleteTextView from your layout file
        binding.actvCity.threshold = 1 //start searching for values after typing first character
        binding.actvCity.setAdapter(arrayAdapter)

        binding.actvCity.setOnItemClickListener { parent, view, position, id ->
            val city = parent.getItemAtPosition(position) as ModelCity

//            Toast.makeText(context, "Selected State Id".plus(modelACTV.itemId), Toast.LENGTH_SHORT).show()
        }
    }


    private fun subscribeObservers() {

        viewModel.userId.observe(viewLifecycleOwner, {
            userID = it
        })

        viewModel.stateList.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateStateAdapter(it.toMutableList())
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

        viewModel.citiesList.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateCityAdapter(it.toMutableList())
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

    private fun displayLoading(state: Boolean) {

        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                AddAddress().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onToolbarNavClick() {
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onMenuClick() {
        TODO("Not yet implemented")
    }
}