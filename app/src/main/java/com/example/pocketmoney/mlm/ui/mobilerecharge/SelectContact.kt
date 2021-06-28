package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentSelectContactBinding
import com.example.pocketmoney.mlm.adapters.ContactAdapter
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.viewmodel.RechargeViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.MyCustomToolbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SelectContact :
    BaseFragment<FragmentSelectContactBinding>(FragmentSelectContactBinding::inflate),
    ContactAdapter.ContactAdapterInterface, MyCustomToolbar.MyCustomToolbarListener {

    private val REQUEST_CODE = 1

    private lateinit var navController: NavController
    private lateinit var contactAdapter: ContactAdapter
    private val viewModel: RechargeViewModel by viewModels()

    private lateinit var contactsPermission: ActivityResultLauncher<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        contactsPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                // Do something if permission granted
                if (isGranted) {
                    binding.groupPermissionDenied.isVisible = false
                    viewModel.getContactList()

                } else {
                    binding.groupPermissionDenied.isVisible = true

                }
            }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.myCustomToolbar.setCustomToolbarListener(this)
        navController = Navigation.findNavController(view)

        setupRecyclerView()


        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            contactsPermission.launch(Manifest.permission.READ_CONTACTS)
        } else {
            binding.groupPermissionDenied.isVisible = false
            viewModel.getContactList()
        }

        binding.btnAllow.setOnClickListener {
            contactsPermission.launch(Manifest.permission.READ_CONTACTS)
        }


    }

    override fun subscribeObservers() {
        viewModel.contactList.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<List<ModelContact>> -> {
                    displayLoading(false)
                    populateRecyclerView(dataState.data)
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


    private fun populateRecyclerView(data: List<ModelContact>) {
        Timber.d("Response $data")
        contactAdapter.setContactList(data)

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

    override fun onContactClick(contact: ModelContact) {
        navController.navigate(
            R.id.action_selectContact_to_selectRechargePlan,
            SelectRechargePlanArgs(
                contact.contactNumber.toString(),
                contact.contactName.toString()
            ).toBundle()
        )

    }

    override fun onToolbarNavClick() {
        requireActivity().finish()
    }

    override fun onMenuClick() {

    }

}