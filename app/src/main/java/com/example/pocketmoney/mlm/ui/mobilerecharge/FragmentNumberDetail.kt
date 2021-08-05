package com.example.pocketmoney.mlm.ui.mobilerecharge

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentNumberDetailBinding
import com.example.pocketmoney.mlm.model.RechargeEnum
import com.example.pocketmoney.mlm.viewmodel.MobileNumberDetailViewModel
import com.example.pocketmoney.mlm.viewmodel.MobileRechargeViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.getMobileOperatorLogo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentNumberDetail : BaseFragment<FragmentNumberDetailBinding>(FragmentNumberDetailBinding::inflate) {


    private val viewModel by activityViewModels<MobileNumberDetailViewModel>()
    private val args by navArgs<FragmentNumberDetailArgs>()

    private lateinit var mOperator: String
    private lateinit var mCircle: String

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            btnChangeCircle.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentNumberDetail_to_selectLocation,SelectLocationArgs(RechargeEnum.CIRCLE).toBundle())
            }

            btnChangeOperator.setOnClickListener {
                findNavController().navigate(R.id.action_fragmentNumberDetail_to_selectOperator2,SelectOperatorArgs(RechargeEnum.PREPAID).toBundle())
            }

            btnConfirm.setOnClickListener {
                val intent = Intent()
                intent.putExtra("operator",mOperator)
                intent.putExtra("circle",mCircle)
                requireActivity().setResult(RESULT_OK,intent)
                requireActivity().finish()
            }
        }
    }
    override fun subscribeObservers() {
        viewModel.selectedOperator.observe(viewLifecycleOwner,{
            mOperator = it
            binding.ivOperatorLogo.setImageResource(getMobileOperatorLogo(it))
            binding.tvOperatorName.text = "$it Prepaid"
        })
        viewModel.selectedCircle.observe(viewLifecycleOwner,{
            mCircle = it
            binding.tvOperatorCircle.text = "$it"
        })

    }

}