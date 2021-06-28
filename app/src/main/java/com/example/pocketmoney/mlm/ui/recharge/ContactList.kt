package com.example.pocketmoney.mlm.ui.recharge

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentContactListBinding
import com.example.pocketmoney.mlm.adapters.ContactAdapter
import com.example.pocketmoney.mlm.model.ModelContact
import com.example.pocketmoney.mlm.viewmodel.RechargeViewModel
import com.example.pocketmoney.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class ContactList : Fragment(), ContactAdapter.ContactAdapterInterface {

    private val REQUEST_CODE = 1
    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController
    private lateinit var contactAdapter: ContactAdapter
    private val viewModel : RechargeViewModel by viewModels()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View{
        // Inflate the layout for this fragment

        _binding = FragmentContactListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
//        binding.myCustomTextInputLayout. = getString(R.string.msg_enter_name_n_mobile_no)
        setupRecyclerView()
        subscribeObservers()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!hasPhoneContactsPermission(Manifest.permission.READ_CONTACTS))
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS), REQUEST_CODE);
        } else {


        }
        viewModel.getContactList()
    }

    private fun subscribeObservers(){
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



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE -> setupRecyclerView()
        }
    }



    private fun displayLoading(state: Boolean) {
//        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }


    private fun displayError(message: String?){
        if(message != null){
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }else{
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
        }
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
            val dividerItemDecoration = DividerItemDecoration(context,
                    layoutManager.orientation)
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager

            adapter =  contactAdapter
        }
    }










    private fun hasPhoneContactsPermission(permission: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val hasPermission = context?.let { ContextCompat.checkSelfPermission(it.applicationContext, permission) }
            hasPermission == PackageManager.PERMISSION_GRANTED
        } else true
    }

    override fun onContactClick(contact: ModelContact) {
        navController.navigate(R.id.action_contactList_to_enterAmount)
        
    }


}