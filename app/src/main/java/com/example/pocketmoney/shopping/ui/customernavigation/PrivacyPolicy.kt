package com.example.pocketmoney.shopping.ui.customernavigation

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentPrivacyPolicyBinding
import com.example.pocketmoney.mlm.ui.dashboard.MainDashboard
import com.example.pocketmoney.shopping.adapters.TitleTextAdapter
import com.example.pocketmoney.shopping.model.ModelTitleSubtitle
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseFragment

class PrivacyPolicy : BaseFragment<FragmentPrivacyPolicyBinding>(FragmentPrivacyPolicyBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private lateinit var titleTextAdapter: TitleTextAdapter
    override fun onStart() {
        super.onStart()
        (activity as MainDashboard).toggleBottomNav(false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvData()
        binding.applicationToolbar5.setApplicationToolbarListener(this)

        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->

            if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_UP) {
                findNavController().navigateUp()

                return@OnKeyListener true
            }
            false
        })
    }

    private fun setupRvData(){
        titleTextAdapter = TitleTextAdapter()
        binding.rvData.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = titleTextAdapter
        }
        populateData()
    }

    private fun populateData(){
        val dataList = mutableListOf<ModelTitleSubtitle>()
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_1),getString(R.string.policy_text_1)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_2),getString(R.string.policy_text_2)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_3),getString(R.string.policy_text_3)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_4),getString(R.string.policy_text_4)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_5),getString(R.string.policy_text_5)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_6),getString(R.string.policy_text_6)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_7),getString(R.string.policy_text_7)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_8),getString(R.string.policy_text_8)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_9),getString(R.string.policy_text_9)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_10),getString(R.string.policy_text_10)))
        dataList.add(ModelTitleSubtitle(getString(R.string.policy_title_11),getString(R.string.policy_text_11)))

        titleTextAdapter.setModelTitleSubtitleList(dataList)

    }


    override fun subscribeObservers() {

    }


    override fun onDestroy() {
        super.onDestroy()
        (activity as MainDashboard).toggleBottomNav(true)
    }

    override fun onToolbarNavClick() {
        findNavController().navigateUp()
    }

    override fun onMenuClick() {

    }


}