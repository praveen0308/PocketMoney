package com.sampurna.pocketmoney.mlm.ui.mobilerecharge

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentSelectLocationBinding
import com.sampurna.pocketmoney.mlm.adapters.SelectLocationAdapter
import com.sampurna.pocketmoney.mlm.model.RechargeEnum
import com.sampurna.pocketmoney.mlm.model.serviceModels.IdNameModel
import com.sampurna.pocketmoney.mlm.viewmodel.MobileNumberDetailViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SelectLocation : BaseFragment<FragmentSelectLocationBinding>(FragmentSelectLocationBinding::inflate),
    SelectLocationAdapter.SelectLocationInterface {

    private val mobileNumberDetailViewModel by activityViewModels<MobileNumberDetailViewModel>()
    private val args by navArgs<SelectLocationArgs>()

    private lateinit var selectLocationAdapter: SelectLocationAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvLocations()
        when(args.locationType){
            RechargeEnum.CIRCLE->mobileNumberDetailViewModel.getMobileServiceCircleList()
        }
    }
    override fun subscribeObservers() {
        mobileNumberDetailViewModel.mobileServiceCircleList.observe(viewLifecycleOwner, { _result ->
            when(_result.status)
            {
                Status.SUCCESS -> {
                    _result._data?.let {
                        selectLocationAdapter.setAnyList(it)
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


    private fun setupRvLocations() {
        selectLocationAdapter = SelectLocationAdapter(this)
        binding.rvLocations.apply {
            setHasFixedSize(true)
            val layoutManager = LinearLayoutManager(context)
            val dividerItemDecoration = DividerItemDecoration(
                context,
                layoutManager.orientation
            )
            addItemDecoration(dividerItemDecoration)

            this.layoutManager = layoutManager
            adapter = selectLocationAdapter
        }
    }

    override fun onLocationSelected(item: Any) {
        when (args.locationType) {
            RechargeEnum.CIRCLE-> {
                mobileNumberDetailViewModel.selectedCircle.postValue((item as IdNameModel).Name)
                findNavController().popBackStack()
            }
            RechargeEnum.STATE -> {
//                findNavController().navigate(R.id.action_selectOperator_to_dthRecharge)
            }
            RechargeEnum.BOARD -> {
//                findNavController().navigate(R.id.action_selectOperator_to_dthRecharge)
            }
        }

    }
}