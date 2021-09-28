package com.example.pocketmoney.common

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.databinding.ActivityTransactionDetailBinding
import com.example.pocketmoney.mlm.model.TransactionDetailModel
import com.example.pocketmoney.utils.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TransactionDetailActivity :
    BaseActivity<ActivityTransactionDetailBinding>(ActivityTransactionDetailBinding::inflate),
    ApplicationToolbar.ApplicationToolbarListener {

    private val viewModel by viewModels<TransactionDetailViewModel>()
    private lateinit var paymentDetailsAdapter: TransactionDetailsAdapter
    private lateinit var serviceDetailsAdapter: TransactionDetailsAdapter

    // Variable
    private var userName:String = ""
    private var userId:String = ""
    private var roleID : Int=0
    private var transactionId : String=""
    private var referenceId : String=""
    private var complaintId : String=""



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRvDetails()
        binding.toolbarTransactionDetail.setApplicationToolbarListener(this)
        transactionId = intent.getStringExtra("TransactionID").toString()
        if (!transactionId.isEmpty()){
            viewModel.viewTransactionDetails(transactionId)
        }

        binding.btnNeedHelp.setOnClickListener {
            val intent = Intent(this,ChatActivity::class.java)
            intent.putExtra("TransactionId",transactionId)
            intent.putExtra("ReferenceId",referenceId)
            intent.putExtra("ComplaintId",complaintId)
            startActivity(intent)
        }
    }

    override fun subscribeObservers() {
        viewModel.userId.observe(this, {
            userId = it

        })
        viewModel.userRoleID.observe(this, {
            roleID = it
            if (userId!="" && roleID!=0){

            }
        })

        viewModel.transactionDetailResponse.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        referenceId = it.Reference_Id.toString()
                        complaintId = it.ComplainID.toString()
                        populateTransactionDetail(it)
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

    private fun populateTransactionDetail(detail: TransactionDetailModel) {
        val paymentDetails = mutableListOf<ModelTitleValue>()
        paymentDetails.add(ModelTitleValue("Transaction ID",detail.Trans_Id.toString()))
        paymentDetails.add(ModelTitleValue("Transaction On", convertISOTimeToAny(detail.Trans_Date.toString(),
            SDF_dmyhms).toString()))
        paymentDetails.add(ModelTitleValue("Type",detail.Trans_Type.toString()))
        paymentDetails.add(ModelTitleValue("Reference ID",detail.Reference_Id.toString()))
        val mode = when(detail.PaymentMode){
            1->"Wallet"
            2->"Online"
            3->"Cash On Delivery"
            4->"PCash"
            else->detail.PaymentMode
        }
        paymentDetails.add(ModelTitleValue("Payment Mode",mode.toString()))
        paymentDetails.add(ModelTitleValue("Amount","₹ ${detail.Amount.toString()}"))
        paymentDetailsAdapter.setModelTitleValueList(paymentDetails)

        val serviceDetails = mutableListOf<ModelTitleValue>()
        serviceDetails.add(ModelTitleValue("Mobile/Account",detail.Mobile_Account_No.toString()))
        serviceDetails.add(ModelTitleValue("Status",detail.Service_Status.toString()))
//        serviceDetails.add(ModelTitleValue("Operator Trans Id",detail.Operator_Trans_ID.toString()))

        serviceDetailsAdapter.setModelTitleValueList(serviceDetails)
    }

    private fun setupRvDetails() {
        paymentDetailsAdapter = TransactionDetailsAdapter()
        binding.rvPaymentDetails.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = paymentDetailsAdapter
        }

        serviceDetailsAdapter = TransactionDetailsAdapter()
        binding.rvServiceDetails.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 2)
            adapter = serviceDetailsAdapter
        }
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {

    }
}