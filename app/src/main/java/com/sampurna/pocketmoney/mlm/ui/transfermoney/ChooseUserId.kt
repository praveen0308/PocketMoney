package com.sampurna.pocketmoney.mlm.ui.transfermoney

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentChooseUserIdBinding
import com.sampurna.pocketmoney.mlm.adapters.ContactAdapter
import com.sampurna.pocketmoney.mlm.model.ModelContact
import com.sampurna.pocketmoney.mlm.viewmodel.B2BTransferViewModel
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.DataState
import com.sampurna.pocketmoney.utils.extractMobileNumber
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseUserId : BaseFragment<FragmentChooseUserIdBinding>(FragmentChooseUserIdBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener, ContactAdapter.ContactAdapterInterface {

    private val viewModel by activityViewModels<B2BTransferViewModel>()
    private lateinit var contactAdapter: ContactAdapter

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
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbarChooseUserId.setApplicationToolbarListener(this)
        setupRecyclerView()
        binding.etMobileNumber.addTextChangedListener {
            binding.rvContacts.isVisible = true
            contactAdapter.getFilter().filter(it.toString())
//            if (it.toString().isEmpty()){
//                binding.rvContacts.isVisible = false
//            }
//            else{
//
//            }
        }
    }

    override fun subscribeObservers() {
        viewModel.contactList.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<List<ModelContact>> -> {
                    displayLoading(false)
                    val contactList = dataState.data.map {
                        ModelContact(
                            it.contactName,
                            extractMobileNumber(it.contactNumber.toString()),
                        )
                    }
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
        viewModel.setRecipientUserId(contact.contactNumber!!)
        findNavController().navigate(R.id.action_chooseUserId_to_payToWallet)
    }

}