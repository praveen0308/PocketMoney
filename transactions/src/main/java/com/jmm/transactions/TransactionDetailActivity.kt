package com.jmm.transactions

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.format.DateFormat
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import com.jmm.core.utils.SDF_dmyhms
import com.jmm.core.utils.convertISOTimeToAny
import com.jmm.model.ModelTitleValue
import com.jmm.model.TransactionDetailModel
import com.jmm.navigation.NavRoute.ChatActivity
import com.jmm.navigation.NavRoute.ComplaintList
import com.jmm.transactions.databinding.ActivityTransactionDetailBinding
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseActivity
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

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
            val intent = Intent(this, Class.forName(ChatActivity))
            intent.putExtra("TransactionId",transactionId)
            intent.putExtra("ReferenceId",referenceId)
            intent.putExtra("ComplaintId",complaintId)
            startActivity(intent)
        }

        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(this, Class.forName(ComplaintList)))
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
                        if(complaintId.isNullOrEmpty()){
                            binding.apply {
                                btnNeedHelp.isVisible = true
                                btnViewHistory.isVisible = false
                            }
                        }else{
                            binding.apply {
                                btnNeedHelp.isVisible = false
                                btnViewHistory.isVisible = true
                            }
                        }
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
        paymentDetails.add(
            ModelTitleValue("Transaction On", convertISOTimeToAny(detail.Trans_Date.toString(),
            SDF_dmyhms).toString())
        )
        paymentDetails.add(ModelTitleValue("Type",detail.Trans_Type.toString()))
        paymentDetails.add(ModelTitleValue("Reference ID",detail.Reference_Id.toString()))
        val mode = when(detail.PaymentMode){
            1->"Wallet"
            2->"PCash"
            3->"Online"
            4->"Cash On Delivery"
            else->detail.PaymentMode
        }
        paymentDetails.add(ModelTitleValue("Payment Mode",mode.toString()))
        paymentDetails.add(ModelTitleValue("Amount","₹ ${detail.Amount.toString()}"))
        paymentDetailsAdapter.setModelTitleValueList(paymentDetails)

        val serviceDetails = mutableListOf<ModelTitleValue>()
        serviceDetails.add(ModelTitleValue("Mobile/Account",detail.Mobile_Account_No.toString()))
        serviceDetails.add(ModelTitleValue("Status",detail.Service_Status.toString()))
        if(detail.Trans_Type_Id == 20){
            if (!detail.Operator_Trans_ID.isNullOrEmpty()){
                serviceDetails.add(ModelTitleValue("Redeem Code",detail.Operator_Trans_ID.toString()))
                binding.btnCopyCode.isVisible =true
                binding.btnCopyCode.setOnClickListener {
                    val myClipboard: ClipboardManager = getSystemService(
                        CLIPBOARD_SERVICE
                    ) as ClipboardManager
                    myClipboard.apply {
                        setPrimaryClip(ClipData.newPlainText("redeemCode", detail.Operator_Trans_ID.toString()))
                    }
                    Toast.makeText(this, "Code copied!!", Toast.LENGTH_SHORT).show()
                }
            }

        }
        else{
            binding.btnCopyCode.isVisible =false
        }
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
        verifyStoragePermission()
        takeScreenShot(window.decorView)
    }

    private val REQUEST_EXTERNAL_STORAGE = 1
    private val PERMISSION_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    private fun takeScreenShot(view: View) {
        val date = Date()
        val format: CharSequence = DateFormat.format("MM-dd-yyyy_hh:mm:ss", date)
        try {
            val mainDir = File(
                getExternalFilesDir(Environment.DIRECTORY_PICTURES), "FilShare"
            )
            if (!mainDir.exists()) {
                val mkdir: Boolean = mainDir.mkdir()
            }
            val path: String = mainDir.toString() + "/" + "PocketMoney" + "-" + format + ".jpeg"
            view.isDrawingCacheEnabled = true
            val bitmap: Bitmap = Bitmap.createBitmap(view.drawingCache)
            view.isDrawingCacheEnabled = false
            val imageFile = File(path)
            val fileOutputStream = FileOutputStream(imageFile)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            shareScreenShot(imageFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //Share ScreenShot
    private fun shareScreenShot(imageFile: File) {
        val uri: Uri = FileProvider.getUriForFile(
            this,
            "com.sampurna.pocketmoney.fileProvider",
            imageFile
        )
        val intent = Intent()
        intent.action = Intent.ACTION_SEND
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_TEXT, "Transaction Detail")
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        try {
            this.startActivity(Intent.createChooser(intent, "Share With"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No App Available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun verifyStoragePermission() {
        val permission: Int =
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSION_STORAGE,
                REQUEST_EXTERNAL_STORAGE
            )
        }
    }

}