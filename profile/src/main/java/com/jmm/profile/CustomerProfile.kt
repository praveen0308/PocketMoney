package com.jmm.profile

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.jmm.kyc.KycStatus
import com.jmm.kyc.ui.KycActivity
import com.jmm.model.mlmModels.CustomerProfileModel
import com.jmm.profile.databinding.FragmentCustomerProfileBinding
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomerProfile : BaseFragment<FragmentCustomerProfileBinding>(FragmentCustomerProfileBinding::inflate) {

    // ViewModels
    private val viewModel by activityViewModels<CustomerProfileViewModel>()

    // Variable
    private var userID: String = ""
    private var roleId = 0

    private lateinit var customerProfileModel: CustomerProfileModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
       /* binding.cpAddressKycStatus.root.setOnClickListener {
            val intent = Intent(requireActivity(),KycActivity::class.java)
            startActivity(intent)
        }*/
    }
    private fun initViews(){


        binding.cpPro.apply {
            root.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.yellow_800))
            ivIcon.setImageResource(R.drawable.ic_baseline_star_rate_24)
            tvText.text = "BASIC"
        }
    }
    override fun subscribeObservers() {
        viewModel.userId.observe(this) {
            userID = it
            viewModel.getUserProfileInfo(userID)
        }
        viewModel.userRoleID.observe(this) {
            roleId = it

        }

        viewModel.pageState.observe(this){state->
            displayLoading(false)
            when(state){
                is CustomerProfilePageState.Error -> showToast(state.msg)
                CustomerProfilePageState.Idle -> {}
                CustomerProfilePageState.Loading -> displayLoading(true)
                is CustomerProfilePageState.Processing -> showToast(state.msg)
                is CustomerProfilePageState.ReceivedProfileDetails -> {
                    customerProfileModel = state.detail
                    populateViews(customerProfileModel)

                }
            }
        }

    }

    private fun populateViews(detail: CustomerProfileModel) {
        binding.apply {
            nsvContent.visibility = View.VISIBLE

            /***
             *  Set label according to
             *  kycStatus
             *  0 - Kyc Required
             *  1 - Partial kyc
             *  2 - Full kyc
             *
             *  Usertype
             *  0 - Basic
             *  1 - PRO
             *
             * ***/

            binding.cpKycStatus.root.isVisible = true
//            binding.cpPro.root.isVisible = true
            /*when(authDetails.IsKYCRequired){

                0->binding.cpKycStatus.tvText.text = "KYC Required"
                1->binding.cpKycStatus.tvText.text = "Partial"
                2->binding.cpKycStatus.tvText.text = "Full KYC"
            }*/

            binding.cpKycStatus.apply {
                ivIcon.setImageResource(R.drawable.ic_baseline_check_circle_24)
                tvText.text = "Verified"
                root.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green_700))
            }

            binding.cpAddressKycStatus.apply {
                ivIcon.setImageResource(R.drawable.ic_baseline_check_circle_24)
                tvText.text = "Verified"
                root.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green_700))
            }
            binding.cpKycStatus.root.isVisible = false
            /*if (detail.IsKYCCompleted){
                binding.apply {
                    btnVerifyUsername.isVisible = false
                    layoutAddress.btnAction.isVisible = false


                }

                binding.cpKycStatus.apply {
                    ivIcon.setImageResource(R.drawable.ic_baseline_check_circle_24)
                    tvText.text = "Verified"
                    root.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.green_700))
                }
            }
            else{
                binding.apply {
                    btnVerifyUsername.isVisible = true
                    layoutAddress.btnAction.isVisible = true


                }

                binding.cpKycStatus.apply {
                    ivIcon.setImageResource(R.drawable.ic_round_close_24)
                    tvText.text = "Not Verified"
                    root.setCardBackgroundColor(ContextCompat.getColor(requireContext(),R.color.red_700))
                }
            }
            */
            cpKycStatus.root.isVisible = detail.IsNameKYC==KycStatus.Completed
            cpAddressKycStatus.root.isVisible = detail.IsAddressKYC==KycStatus.Completed
            setBtnStatus(btnVerifyUsername,detail.IsNameKYC)
            setBtnStatus(btnVerifyAddress,detail.IsAddressKYC)


         /*   when(authDetails.UserType){

                0->binding.cpPro.tvText.text = "BASIC"
                1->binding.cpPro.tvText.text = "PRO"
            }
*/
            /*** Customer Username ***/
            tvName.text = detail.FullName
            tvNameLeading.text = detail.FullName.take(1)

            btnVerifyUsername.setOnClickListener {
                // open enter pan details page
                findNavController().navigate(CustomerProfileDirections.actionCustomerProfileToVerifyPan())
            }


            /*** Customer Mobile No. ***/
            layoutMobileNo.apply {
                tvLabelDetail.text = "Mobile Number"
                etData.setText(detail.Mobile)
            }

            /*** Customer Email ***/
            layoutEmail.apply {

//                btnAction.visibility = View.VISIBLE
                tvLabelDetail.text = "Email"
                etData.setText(detail.EmailID)
            }
            btnVerifyAddress.setOnClickListener {
                /*** Open Upload Document Screen ***/
                val intent = Intent(requireActivity(),KycActivity::class.java)
                startActivity(intent)
            }

            /*** Customer Address ***/
            layoutAddress.apply {

                tvLabelDetail.text = "Address"
                etData.setText(detail.Address1)

            }
            /*** Customer SponsorName ***/
            layoutSponsorName.apply {
                tvLabelDetail.text = "Sponsor Name"
                etData.setText(detail.SponsorName)
            }
            /*** Customer Sponsor Id ***/
            layoutSponsorId.apply {
                tvLabelDetail.text = "Sponsor ID"
                etData.setText(detail.SponsorID)
            }


            layoutCity.apply {
                tvLabelDetail.text = "City"
                etData.setText(detail.CityID.toString())
            }

            layoutPincode.apply {
                tvLabelDetail.text = "Pincode"

                if(detail.PinNo.isNullOrBlank()){
                    etData.setText("N.A.")
                }else{
                    etData.setText(detail.PinNo)
                }

            }
            layoutState.apply {
                tvLabelDetail.text = "State"
                if(detail.StateID.isNullOrBlank()){
                    etData.setText("N.A.")
                }else{
                    etData.setText(detail.StateID)
                }

            }

        }
    }

    private fun setBtnStatus(btn:MaterialButton,status:Int){
        binding.apply {
            when(status){
                KycStatus.NotInitiated->{
                    btn.isVisible = true
                    btn.text = "Verify"
                    btn.isEnabled = true
                    btn.setTextColor(ContextCompat.getColor(requireContext(),R.color.colorPrimary))
                }
                KycStatus.Completed->{
                    btn.isVisible = false
                    /*btn.isClickable = false
                    btn.text = "Verified"
                    btn.setTextColor(ContextCompat.getColor(requireContext(),R.color.Green))*/
                }
                KycStatus.Pending->{
                    btn.isVisible = true
                    btn.text = "Pending"
                    btn.isEnabled = false
                    btn.setTextColor(ContextCompat.getColor(requireContext(),R.color.orange_700))
                }
                KycStatus.Rejected->{
                    btn.isVisible = true
                    btn.text = "Retry"
                    btn.isEnabled = true
                    btn.setTextColor(ContextCompat.getColor(requireContext(),R.color.red_700))
                }

            }
        }

    }
}