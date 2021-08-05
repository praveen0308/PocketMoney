package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentEnterMobileNumberBinding
import com.example.pocketmoney.mlm.adapters.ContactAdapter
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.extractMobileNumber
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EnterMobileNumber : BaseFragment<FragmentEnterMobileNumberBinding>(FragmentEnterMobileNumberBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener, ContactAdapter.ContactAdapterInterface {

    private val viewModel by activityViewModels<MobileRechargeViewModel>()
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val mContactList  = mutableListOf<ModelContact>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            checkContactPermission()
        } else {
            viewModel.getContactList()

        }

        resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                val data: Intent? = result.data
                if (result.resultCode == Activity.RESULT_OK){
                    val number = data!!.getStringExtra("Number")!!

                    val mContact = mContactList.find { it.contactNumber.equals(number) }
                    viewModel.selectedContact.postValue(mContact)
                    findNavController().navigate(R.id.action_enterMobileNumber_to_selectRechargePlan)

                }
                else{
                    showToast("Cancelled !!")
                }

            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarChooseUserId.setApplicationToolbarListener(this)
        setupRecyclerView()
        binding.imageButton.setOnClickListener {
            val intent = Intent(requireActivity(),SelectContact::class.java)
            resultLauncher.launch(intent)
        }

        binding.etMobileNumber.addTextChangedListener {
            if (it.toString().isEmpty()){
                binding.rvContacts.isVisible = false
            }
            else{
                binding.rvContacts.isVisible = true
                contactAdapter.getFilter().filter(it.toString())
            }
        }
        binding.etMobileNumber.doAfterTextChanged { number->
            if (number!!.length==10){

                val mContact = mContactList.find { it.contactNumber!! == number.toString() }
                if (mContact!=null){
                    viewModel.selectedContact.postValue(mContact)
                }
                else{
                    val contact = ModelContact(contactNumber = number.toString())
                    viewModel.selectedContact.postValue(contact)
                }

                findNavController().navigate(R.id.action_enterMobileNumber_to_selectRechargePlan)
            }
        }
    }

    override fun subscribeObservers() {
        viewModel.contactList.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<List<ModelContact>> -> {
                    displayLoading(false)
                    mContactList.clear()
                    mContactList.addAll(dataState.data)
                    val contactList = dataState.data

                    contactAdapter.setContactList(contactList)
                }
                is DataState.Loading -> {
                    displayLoading(true)

                }
                is DataState.Error -> {
                    displayLoading(false)
                    displayError(dataState.exception.message)
                }
            }
        })
    }
    private fun setupRecyclerView() {
        contactAdapter = ContactAdapter(this)
        binding.rvContacts.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                layoutManager.orientation
            )
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager

            adapter = contactAdapter
        }
    }

    private fun checkContactPermission(){
        Dexter.withContext(requireContext()).withPermission(Manifest.permission.READ_CONTACTS)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    viewModel.getContactList()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        requireContext(),
                        "Contact Permission is Required.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    override fun onToolbarNavClick() {
        requireActivity().finish()
    }

    override fun onMenuClick() {
    }

    override fun onContactClick(contact: ModelContact) {
        viewModel.selectedContact.postValue(contact)
        findNavController().navigate(R.id.action_enterMobileNumber_to_selectRechargePlan)
    }

}