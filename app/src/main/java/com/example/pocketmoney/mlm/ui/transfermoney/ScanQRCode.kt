package com.example.pocketmoney.mlm.ui.transfermoney

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentScanQRCodeBinding
import com.example.pocketmoney.mlm.viewmodel.B2BTransferViewModel
import com.example.pocketmoney.utils.BaseFragment
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanQRCode : BaseFragment<FragmentScanQRCodeBinding>(FragmentScanQRCodeBinding::inflate) {

    private val viewModel by activityViewModels<B2BTransferViewModel>()

    private lateinit var codeScanner: CodeScanner
    private var userId: String = ""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        codeScanner = CodeScanner(requireContext(), binding.scannerView)
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = false // Whether to enable flash or not
        codeScanner.decodeCallback =
            DecodeCallback { result ->
                requireActivity().runOnUiThread {
                    userId = result.text
                    viewModel.setRecipientUserId(userId)
                    findNavController().navigate(R.id.action_scanQRCode_to_payToWallet)
                }
            }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }

        binding.layoutEnterUserId.etMobileNumber.setOnClickListener {
            findNavController().navigate(R.id.action_scanQRCode_to_chooseUserId)
        }
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }


    override fun onResume() {
        super.onResume()
        requestForCamera()
    }

    private fun requestForCamera() {
        Dexter.withContext(requireContext()).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    codeScanner.startPreview()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        requireContext(),
                        "Camera Permission is Required.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            }).check()
    }

    override fun subscribeObservers() {

    }

}