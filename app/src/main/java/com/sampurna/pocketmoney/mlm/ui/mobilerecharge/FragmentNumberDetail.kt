package com.sampurna.pocketmoney.mlm.ui.mobilerecharge

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentNumberDetailBinding
import com.sampurna.pocketmoney.mlm.model.RechargeEnum
import com.sampurna.pocketmoney.mlm.viewmodel.MobileNumberDetailViewModel
import com.sampurna.pocketmoney.utils.BaseFragment
import com.sampurna.pocketmoney.utils.getMobileOperatorLogo
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
//                findNavController().navigate(R.id.action_fragmentNumberDetail_to_selectOperator2,SelectOperatorArgs(RechargeEnum.PREPAID).toBundle())
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