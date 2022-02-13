package com.jmm.kyc.ui

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.jmm.kyc.KycPageState
import com.jmm.kyc.KycViewModel
import com.jmm.kyc.databinding.FragmentUploadDocumentBinding
import com.jmm.model.CustomerKYCModel
import com.jmm.util.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


@AndroidEntryPoint
class UploadDocument :
    BaseFragment<FragmentUploadDocumentBinding>(FragmentUploadDocumentBinding::inflate) {

    private val viewModel by activityViewModels<KycViewModel>()

    private val uploadOptions = arrayOf("Camera", "Gallery")
    private lateinit var dialogBuilder: androidx.appcompat.app.AlertDialog.Builder
    private lateinit var uploadOptionsDialog: androidx.appcompat.app.AlertDialog
    private var latestTmpUri: Uri? = null
    private var userId = ""
    private val takeImageResult =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
            if (isSuccess) {
                latestTmpUri?.let { uri ->
                    Timber.d("Camera image : $uri")
                    setNGetImage(uri)
                }
            }
        }

    private val selectImageFromGalleryResult =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                Timber.d("Gallery image : $uri")
                setNGetImage(uri)

            }
        }

    private fun setNGetImage(uri: Uri){
        try {
            val bitmap : Bitmap
            if(Build.VERSION.SDK_INT >= 29) {

                val source = ImageDecoder.createSource(requireContext().contentResolver, uri)
                bitmap = ImageDecoder.decodeBitmap(source)


                binding.ivDocument.setImageBitmap(bitmap)
            } else {
                bitmap = MediaStore.Images.Media.getBitmap(
                    requireContext().contentResolver,
                    uri
                )

                binding.ivDocument.setImageBitmap(bitmap)
            }
            viewModel.selectedImage = convertBitmapToEncodedString(bitmap)
            Timber.d("Base64 String >>>>>> ${viewModel.selectedImage}")

        } catch (e: Exception) {
            Timber.e("Exception : ${e.message}")
            Timber.e("Exception : $e")
            e.printStackTrace()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeDialog()
        binding.ivDocument.setOnClickListener {
//            uploadOptionsDialog.show()
            takeImage()
        }
        binding.btnConfirm.setOnClickListener {
            viewModel.uploadCustomerDocument(
                CustomerKYCModel(
                    DocumentNumber = "",
                    DocumentName="${Date().time}_${userId}_${viewModel.selectedDocumentType.documentName}",
                    base64Img = viewModel.selectedImage,
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


    override fun subscribeObservers() {
        viewModel.userId.observe(viewLifecycleOwner) {
            userId = it
        }
        viewModel.uploadDocumentPageState.observe(viewLifecycleOwner) { state ->
            displayLoading(false)
            hideLoadingDialog()
            when (state) {
                is KycPageState.Error -> displayError(state.msg)
                KycPageState.Idle -> {}
                KycPageState.Loading -> {
                    displayLoading(true)
                }
                KycPageState.DocumentUpdatedSuccessfully -> {
                    showToast("Updated successfully!!!")
                    requireActivity().finish()
                }
                is KycPageState.Processing -> {
                    showLoadingDialog(
                        state.msg
                    )
                }
            }
        }
    }

    private fun takeImage() {
        lifecycleScope.launchWhenStarted {
            getTmpFileUri().let { uri ->
                latestTmpUri = uri
                takeImageResult.launch(uri)
            }
        }
    }

    private fun selectImageFromGallery() = selectImageFromGalleryResult.launch("image/*")

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

    private fun initializeDialog() {
        dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        dialogBuilder.setTitle("Choose option")

        dialogBuilder.setItems(
            uploadOptions
        ) { dialog, which ->

            when (which) {
                0 -> {
                    showToast("camera")
                    takeImage()
                }
                1 -> {
                    showToast("gallery")
                    selectImageFromGallery()
                }
            }
        }
        uploadOptionsDialog = dialogBuilder.create()

    }

}