package com.jmm.shopping.ui

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import com.jmm.model.myEnums.OtherEnum
import com.jmm.model.shopping_models.ModelAddress
import com.jmm.model.shopping_models.ModelCity
import com.jmm.model.shopping_models.ModelState
import com.jmm.shopping.databinding.ActivityAddNewAddressBinding
import com.jmm.shopping.viewmodel.ManageAddressViewModel
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddNewAddress : BaseActivity<ActivityAddNewAddressBinding>(ActivityAddNewAddressBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<ManageAddressViewModel>()

    private lateinit var action : OtherEnum
    private var mID : Int=0
    private var selectedState:String=""
    private var selectedCity:Int=0
    private lateinit var userId:String

    private val states = mutableListOf<ModelState>()
    private val cities = mutableListOf<ModelCity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getAllStates()

        action = intent.getSerializableExtra("ACTION") as OtherEnum
        mID = intent.getIntExtra("ID",0)
        binding.toolbar.setApplicationToolbarListener(this)

        binding.btnSaveAddress.setOnClickListener {

            val fullName = binding.etFullName.text.toString()
            val mobileNumber = binding.etMobileNumber.text.toString()
            val address = binding.etAddress.text.toString()
            val locality = binding.etLocality.text.toString()
            val street = binding.etStreet.text.toString()
            val countryId = "IN"
            val stateId = selectedState
            val cityId = selectedCity
            val pincode = binding.etPincode.text.toString()

            val modelAddress = ModelAddress(
                Name = fullName,
                MobileNo = mobileNumber,
                Address1 = address,
                Locality = locality,
                Street = street,
                CountryID = countryId,
                StateID = stateId,
                CityID =  cityId.toString(),
                PostalCode = pincode,
                UserID = userId,
                AddressType = "Home",
                IsCancel = false,
                AddressID = mID
            )

            if (action==OtherEnum.ADD){
                viewModel.addNewAddress(modelAddress)
            }else{
                viewModel.updateAddress(modelAddress)
            }

        }

    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this) {
            userId = it

        }
        viewModel.stateList.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        states.clear()
                        states.addAll(it)
                        if (action == OtherEnum.EDIT) {
                            binding.toolbar.setToolbarTitle("Edit Address")
                            viewModel.getAddressDetails(mID, userId)
                        }
                        populateStateAdapter(states)
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
        }

        viewModel.citiesList.observe(this
        ) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        cities.clear()
                        cities.addAll(it)

                        populateCityAdapter(cities)
                        val mCity = cities.find { city->
                            city.ID == selectedCity
                        }

                        mCity?.let {city->
                            binding.actvCity.setText(city.City1)

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
        }

        viewModel.isSuccessFullyAdded.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        val intent = Intent()
                        intent.putExtra("Message", "Added successfully !!!")
                        setResult(RESULT_OK, intent)
                        finish()
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
        }



        viewModel.addressDetail.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateAddressDetails(it)
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
        }

        viewModel.isSuccessfullyUpdated.observe(this) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        val intent = Intent()
                        intent.putExtra("Message", "Updated successfully !!!")
                        setResult(RESULT_OK, intent)
                        finish()
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
        }


    }

    private fun populateAddressDetails(modelAddress: ModelAddress) {

        binding.apply {
            etFullName.setText(modelAddress.Name)
            etMobileNumber.setText(modelAddress.MobileNo)
            etLocality.setText(modelAddress.Locality)
            etAddress.setText(modelAddress.Address1)
            etStreet.setText(modelAddress.Street)
            etPincode.setText(modelAddress.PostalCode)
            val mState = states.find { state->
                state.StateCode == modelAddress.StateID!!
            }
            selectedState = modelAddress.StateID!!
            selectedCity = modelAddress.CityID.toString().toInt()
            mState?.let {state->
                actvState.setText(state.State1)
                viewModel.getCitiesByStateCode(state.StateCode)

            }


        }
    }

    private fun populateStateAdapter(stateList:MutableList<ModelState>){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, stateList)
        //actv is the AutoCompleteTextView from your layout file
        binding.actvState.threshold = 1 //start searching for values after typing first character
        binding.actvState.setAdapter(arrayAdapter)

        binding.actvState.setOnItemClickListener { parent, view, position, id ->
            val state = parent.getItemAtPosition(position) as ModelState
            selectedState = state.StateCode
            viewModel.getCitiesByStateCode(state.StateCode)
        }
    }

    private fun populateCityAdapter(cityList:MutableList<ModelCity>){
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, cityList)
        binding.actvCity.threshold = 1 //start searching for values after typing first character
        binding.actvCity.setAdapter(arrayAdapter)

        binding.actvCity.setOnItemClickListener { parent, view, position, id ->
            val city = parent.getItemAtPosition(position) as ModelCity
            selectedCity = city.ID
        }
    }

    override fun onToolbarNavClick() {
        val intent = Intent()
        intent.putExtra("Message", "Cancelled !!")
        setResult(RESULT_CANCELED, intent)
        finish()
    }

    override fun onMenuClick() {

    }
}