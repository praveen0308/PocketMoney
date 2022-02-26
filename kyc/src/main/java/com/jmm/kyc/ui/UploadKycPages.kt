package com.jmm.kyc.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import com.jmm.kyc.KycPageState
import com.jmm.kyc.KycViewModel
import com.jmm.kyc.databinding.FragmentUploadKycPagesBinding
import com.jmm.kyc.scan_document.ScanDocument
import com.jmm.model.CustomerKYCModel
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


@AndroidEntryPoint
class UploadKycPages : BaseFragment<FragmentUploadKycPagesBinding>(FragmentUploadKycPagesBinding::inflate) {

    private val viewModel by activityViewModels<KycViewModel>()


    private var userId = ""
    private var frontPageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if(data!=null){
                binding.layoutFrontPage.apply {
                    textView6.isVisible = false
                    val file = File(getRealPathFromURI(data.getStringExtra("uri")!!.toUri()))
                    val d = Drawable.createFromPath(file.absolutePath)
                    bgImage.background = d

                    val frontPageStr = setNGetImage(data.getStringExtra("uri")!!.toUri())
                    if(frontPageStr!=null) viewModel.frontPageImage = frontPageStr
                }
            }

        }
    }


    private var backPageResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if(data!=null){
                binding.layoutBackPage.apply {
                    textView6.isVisible = false
                    val file = File(getRealPathFromURI(data.getStringExtra("uri")!!.toUri()))
                    val d = Drawable.createFromPath(file.absolutePath)
                    bgImage.background = d
                    val backPageStr = setNGetImage(data.getStringExtra("uri")!!.toUri())
                    if(backPageStr!=null) viewModel.backPageImage = backPageStr

                }
            }
        }
    }


    private fun setNGetImage(uri: Uri):String?{

        try {
            val bitmap : Bitmap = if(Build.VERSION.SDK_INT >= 29) {

                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                ImageDecoder.decodeBitmap(source)


            } else {
                MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    uri
                )

            }

            Timber.d("Base64 String >>>>>> ")
            return convertBitmapToEncodedString(bitmap)


        } catch (e: Exception) {
            Timber.e("Exception : ${e.message}")
            Timber.e("Exception : $e")
            e.printStackTrace()
            return null
        }
    }
    private fun getTmpFileUri(): Uri {
        val tmpFile = File.createTempFile("tmp_image_file", ".jpeg").apply {
            createNewFile()
            deleteOnExit()
        }

        return FileProvider.getUriForFile(requireContext().applicationContext, "com.sampurna.pocketmoney.fileProvider", tmpFile)
    }

    private fun convertBitmapToEncodedString(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
        val byteArray = stream.toByteArray()

        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private fun getRealPathFromURI(contentURI: Uri): String? {
        val cursor: Cursor? = requireActivity().contentResolver.query(contentURI, null, null, null, null)
        return if (cursor == null) { // Source is Dropbox or other similar local file path
            contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            cursor.getString(idx)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            layoutFrontPage.btnUpload.setOnClickListener {
                val intent = Intent(requireActivity(),ScanDocument::class.java)
                frontPageResult.launch(intent)
            }
            layoutBackPage.btnUpload.setOnClickListener {
                val intent = Intent(requireActivity(),ScanDocument::class.java)
                backPageResult.launch(intent)
            }

            btnSubmit.setOnClickListener {
                if (viewModel.frontPageImage.isEmpty()){
                    showToast("Upload front page !!!")
                }else{
                    if (viewModel.backPageImage.isEmpty()){
                        showToast("Upload back page !!!")
                    }else{
                        viewModel.uploadFrontPage(
                            CustomerKYCModel(
                                DocumentNumber = viewModel.documentNumber,
                                DocumentName="${Date().time}_${userId}_AADHAAR_FRONT",
                                base64Img = viewModel.frontPageImage,
                                DocumentFileType = "jpeg",
                                UserID = userId,
                                AddressLine1 = viewModel.address1,
                                AddressLine2 = viewModel.address2,
                                PinCode = viewModel.pincode.toInt(),
                                State = viewModel.state,
                                City = viewModel.city,

                                )
                        )
                    }
                }
            }
        }


    }

    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner) {
            userId = it
        }

        viewModel.uploadDocumentPageState.observe(viewLifecycleOwner){state->
            when(state){
                is KycPageState.Error -> {
                    showToast(state.msg)
                    binding.layoutFrontPage.progressBar.isVisible = false
                    binding.layoutBackPage.progressBar.isVisible = false
                }
                KycPageState.Idle -> {

                }
                KycPageState.SuccessfullyBackPage -> {
                    binding.layoutBackPage.progressBar.isVisible = false
                    showToast("Successfully submitted !!!")
                    requireActivity().finish()
                }
                KycPageState.SuccessfullyFirstPage -> {
                    binding.layoutFrontPage.progressBar.isVisible = false
                    viewModel.uploadBackPage(
                        CustomerKYCModel(
                            DocumentNumber = viewModel.documentNumber,
                            DocumentName="${Date().time}_${userId}_AADHAAR_BACK",
                            base64Img = viewModel.backPageImage,
                            DocumentFileType = "jpeg",
                            UserID = userId,
                            AddressLine1 = viewModel.address1,
                            AddressLine2 = viewModel.address2,
                            PinCode = viewModel.pincode.toInt(),
                            State = viewModel.state,
                            City = viewModel.city,

                            )
                    )

                }
                KycPageState.UploadingBackPage -> {
                    binding.layoutBackPage.progressBar.isVisible = true
                }
                KycPageState.UploadingFirstPage -> {
                    binding.layoutFrontPage.progressBar.isVisible = true
                }
            }

        }
    }


}