package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentSelectContactBinding
import com.example.pocketmoney.mlm.adapters.ContactAdapter
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.utils.BaseActivity
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.MyCustomToolbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class SelectContact :
    BaseActivity<FragmentSelectContactBinding>(FragmentSelectContactBinding::inflate),
    ContactAdapter.ContactAdapterInterface, MyCustomToolbar.MyCustomToolbarListener {

    private lateinit var navController: NavController
    private lateinit var contactAdapter: ContactAdapter
    private val viewModelMobile: MobileRechargeViewModel by viewModels()
    private lateinit var contactsPermission: ActivityResultLauncher<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializeContactPermission()
        checkContactPermission()
        binding.myCustomToolbar.setCustomToolbarListener(this)

        setupRecyclerView()
        binding.editTextWithClear.addTextChangedListener {
            binding.rvContacts.isVisible = true
            contactAdapter.getFilter().filter(it.toString())
//            if (it.toString().isEmpty()){
//                binding.rvContacts.isVisible = false
//            }
//            else{
//
//            }
        }
        binding.btnAllow.setOnClickListener {
            contactsPermission.launch(Manifest.permission.READ_CONTACTS)
        }

    }

    override fun subscribeObservers() {
        viewModelMobile.contactList.observe(this, { dataState ->
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

    private fun initializeContactPermission(){
        contactsPermission =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                // Do something if permission granted
                if (isGranted) {
                    binding.groupPermissionDenied.isVisible = false
                    viewModelMobile.getContactList()

                } else {
                    binding.groupPermissionDenied.isVisible = true

                }
            }

    }

    private fun checkContactPermission(){
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            contactsPermission.launch(Manifest.permission.READ_CONTACTS)
        } else {
            binding.groupPermissionDenied.isVisible = false
            viewModelMobile.getContactList()
        }
    }
    private fun populateRecyclerView(data: List<ModelContact>) {
        Timber.d("Response $data")
        contactAdapter.setContactList(data)
        contactAdapter.getFilter().filter("")
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
        val intent = Intent()
        intent.putExtra("Number",contact.contactNumber)
        setResult(RESULT_OK,intent)
        finish()
//        navController.navigate(
//            R.id.action_selectContact_to_selectRechargePlan,
//            SelectRechargePlanArgs(
//                contact.contactNumber.toString(),
//                contact.contactName.toString()
//            ).toBundle()
//        )

    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }

}