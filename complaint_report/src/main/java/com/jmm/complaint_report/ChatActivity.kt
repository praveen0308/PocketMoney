package com.jmm.complaint_report

import android.os.Bundle
import androidx.activity.viewModels
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.complaint_report.databinding.ActivityChatBinding
import com.jmm.model.ComplainModel
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatActivity : BaseActivity<ActivityChatBinding>(ActivityChatBinding::inflate),
    ChatAdapter.ChatInterface, ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<ChatActivityViewModel>()
    private var transactionId = ""
    private var referenceId = ""
    private var complaintId = ""
    private var userID = ""
    private var roleID = 0

    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRvChats()
        binding.toolbarChatActivity.setApplicationToolbarListener(this)
        binding.toolbarChatActivity.setToolbarTitle(transactionId.toString())
        transactionId = intent.getStringExtra("TransactionId").toString()
        referenceId = intent.getStringExtra("ReferenceId").toString()
        complaintId = intent.getStringExtra("ComplaintId").toString()

        if (complaintId.isNotEmpty()){
            viewModel.getComplaintChat(complaintId)
        }

        binding.etMessage.doAfterTextChanged { it ->
            binding.btnSend.isEnabled = !it.toString().isEmpty()

        }
        binding.btnSend.setOnClickListener {
            val comment = binding.etMessage.text.toString().trim()
            if (complaintId.isNotEmpty()){
                viewModel.actionOnComplain(
                    ComplainModel(
                    ActionFlagID = 1,
                    ComplainID = complaintId,
                    ResponderComment = comment
                )
                )
            }else{
                viewModel.addServiceComplaint(referenceId,transactionId,userID,comment)
            }


        }
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this, {
            userID = it

        })
        viewModel.userRoleID.observe(this, {
            roleID = it
            if (userID != "" && roleID != 0) {

            } else {
                checkAuthorization()
            }

        })

        viewModel.complaintChat.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        chatAdapter.setComplainModelList(it.reversed())
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

        viewModel.isCompliantAdded.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        complaintId = it
                        showToast("Sent successfully !!!")
                        binding.etMessage.setText("")
                        viewModel.getComplaintChat(complaintId)
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

        viewModel.actionOnComplainResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        binding.etMessage.setText("")
                        viewModel.getComplaintChat(complaintId)
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

    private fun setupRvChats(){
        chatAdapter = ChatAdapter(this)
        binding.rvChats.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
            adapter = chatAdapter
        }
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}