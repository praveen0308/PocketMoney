package com.sampurna.pocketmoney.mlm.ui.payouts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentSelectBankBinding
import com.sampurna.pocketmoney.mlm.adapters.BankListAdapter
import com.sampurna.pocketmoney.mlm.model.payoutmodels.BankModel
import com.sampurna.pocketmoney.mlm.viewmodel.PayoutViewModel
import com.sampurna.pocketmoney.utils.MyCustomToolbar
import com.sampurna.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectBank : DialogFragment(), BankListAdapter.BankAdapterInterface,
    MyCustomToolbar.MyCustomToolbarListener {

    private val viewModel by activityViewModels<PayoutViewModel>()
    private var _binding: FragmentSelectBankBinding? = null
    private val binding get() = _binding!!

    private lateinit var bankListAdapter:BankListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        setStyle(STYLE_NORMAL, android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentSelectBankBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvBanks()
        subscribeObservers()
        viewModel.getBanks()
        binding.myCustomToolbar.setCustomToolbarListener(this)

        binding.editTextWithClear.addTextChangedListener {
            binding.rvBanks.isVisible = true
            bankListAdapter.getFilter().filter(it.toString())
//            if (it.toString().isEmpty()){
//                binding.rvContacts.isVisible = false
//            }
//            else{
//
//            }
        }
    }

    private fun subscribeObservers(){
        viewModel.banks.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        bankListAdapter.setBankList(it)
                        bankListAdapter.getFilter().filter("")
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

    private fun setupRvBanks(){
        bankListAdapter = BankListAdapter(this)
        binding.rvBanks.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                layoutManager.orientation
            )
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager
            adapter = bankListAdapter
        }
    }
    private fun displayLoading(visibility:Boolean){
        binding.progressBar.isVisible = visibility
    }

    private fun displayError(msg:String){
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun onBankClick(bank: BankModel) {
        viewModel.selectedBank.postValue(bank.BankName)
        viewModel.selectedBankIfsc.postValue(bank.IFSC.toString())
        dismiss()
    }

    override fun onToolbarNavClick() {
        dismiss()
    }

    override fun onMenuClick() {

    }
}