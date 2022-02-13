package com.jmm.payout

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.jmm.payout.databinding.ActivityScanQrBinding
import com.jmm.util.BaseActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ScanQR : BaseActivity<ActivityScanQrBinding>(ActivityScanQrBinding::inflate) {
    private lateinit var codeScanner: CodeScanner
    private var userId: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        codeScanner = CodeScanner(this, binding.scannerView)
        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
        codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
        // ex. listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
        codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
        codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
        codeScanner.isFlashEnabled = true // Whether to enable flash or not
        codeScanner.decodeCallback =
            DecodeCallback { result ->
                runOnUiThread {
                    userId = result.text
                    Timber.d("Result >>> $result")
                    val format = result.barcodeFormat
                    Timber.d("Result >>> ${result.barcodeFormat}")
                    val uri: Uri = Uri.parse(result.text)
                    val params = getQueryParameters(uri)

                    Timber.d("split >>> $result")
                    val intent = Intent()
                    intent.putExtra("upiId", params[0])
                    intent.putExtra("name", params[1])
                    setResult(RESULT_OK, intent)
                    finish()
                }
            }

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun getQueryParameters(uri: Uri): List<String> {
        val params: MutableList<String> = ArrayList()
        for (paramName in uri.queryParameterNames) {
            params.addAll(uri.getQueryParameters(paramName))
        }
        Timber.d(params.toString())
        return params
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
        Dexter.withContext(this).withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    codeScanner.startPreview()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {
                    Toast.makeText(
                        this@ScanQR,
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