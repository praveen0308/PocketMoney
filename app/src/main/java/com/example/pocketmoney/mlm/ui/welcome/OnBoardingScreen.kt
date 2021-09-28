package com.example.pocketmoney.mlm.ui.welcome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentOnBoardingScreenBinding
import com.example.pocketmoney.mlm.adapters.OnBoardingAdapter
import com.example.pocketmoney.mlm.model.ModelOnBoardingItem
import com.example.pocketmoney.mlm.ui.dashboard.MainDashboard
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.mlm.viewmodel.OnBoardingScreenViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.example.pocketmoney.utils.Constants
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnBoardingScreen : BaseFragment<FragmentOnBoardingScreenBinding>(FragmentOnBoardingScreenBinding::inflate) {

    private lateinit var onBoardingAdapter: OnBoardingAdapter
    private val viewModel by viewModels<OnBoardingScreenViewModel>()

    var position = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boardingItemList: MutableList<ModelOnBoardingItem> = ArrayList()
        boardingItemList.add(
            ModelOnBoardingItem(
                title = "Make Payment",
                description = "We love to see you happy in shopping and we are here to make payment for your purchase",
                imageUrl = R.drawable.img_make_payment
        )
        )

        boardingItemList.add(
            ModelOnBoardingItem(
                title = "Pay Your Bills",
                description = "Pay your bills by using this app securely and easily.",
                imageUrl = R.drawable.img_pay_bills
        )
        )

        boardingItemList.add(
            ModelOnBoardingItem(
                title = "Online Shopping",
                description = "Shop products by using this app, Fast & Digital Shopping",
                imageUrl = R.drawable.img_shopping_app
        )
        )
        setOnBoardingItems(boardingItemList)

        position = binding.vpBoardingContents.currentItem

        binding.btnBoardingAction.setOnClickListener {
            if (position < boardingItemList.size) {
                position++
                binding.vpBoardingContents.currentItem = position
            }
            if (position == boardingItemList.size) {
                goToLoginPage()
            }
        }

        binding.tlBoardingIndicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position = tab!!.position
                binding.btnBoardingAction.text = if (tab.position == boardingItemList.size - 1) "Get Started" else "Next"

            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })

    }


    private fun setOnBoardingItems(onBoardingData: List<ModelOnBoardingItem>) {
        onBoardingAdapter = OnBoardingAdapter(onBoardingData)

        binding.vpBoardingContents.adapter = onBoardingAdapter
        TabLayoutMediator(binding.tlBoardingIndicator, binding.vpBoardingContents) { tab, position ->
//            tab.text = "OBJECT ${(position + 1)}"
        }.attach()


    }

    private fun goToLoginPage(){
        viewModel.updateWelcomeStatus(Constants.ONBOARDING_DONE)
        val intent = Intent(requireActivity(), MainDashboard::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        requireActivity().finish()
//        findNavController().navigate(OnBoardingScreenDirections.actionOnBoardingScreenToLoginFragment())
    }

    override fun subscribeObservers() {

    }


}