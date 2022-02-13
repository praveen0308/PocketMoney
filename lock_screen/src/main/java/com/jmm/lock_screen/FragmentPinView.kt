package com.jmm.lock_screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.andrognito.pinlockview.PinLockListener
import com.jmm.lock_screen.databinding.FragmentPinViewBinding
import com.jmm.navigation.NavRoute.MainDashboard
import com.jmm.util.BaseFullScreenDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import timber.log.Timber


@AndroidEntryPoint
class FragmentPinView : BaseFullScreenDialogFragment<FragmentPinViewBinding>(FragmentPinViewBinding::inflate) {

    private val viewModel by viewModels<PinViewModel>()
    private var isSecured = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isCancelable = false

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.pinLockView.attachIndicatorDots(binding.indicatorDots)
        binding.pinLockView.setPinLockListener(mPinLockListener)

        binding.btnForgotPin.setOnClickListener {

        }

    }

    private fun resetPin(){
        runBlocking {
            delay(500)
            binding.pinLockView.resetPinLockView()
        }
    }

    private val mPinLockListener: PinLockListener = object : PinLockListener {
        override fun onComplete(pin: String) {
            Timber.d( "Pin complete: $pin")




            when(viewModel.pinStatus){
                PinStatus.SettingNewPin->{
                    viewModel.pin = pin
                    binding.tvHint.text = "Confirm Your Pin"
                    viewModel.pinStatus = PinStatus.ConfirmNewPin
                    resetPin()
                }
                PinStatus.ConfirmNewPin->{
                    if (viewModel.pin==pin){
                        try {
                            viewModel.updateUserPin(pin)
                            viewModel.updateSecurity(true)
                            resetPin()
                        }catch (e:Exception){
                            Timber.e("Exception raise : $e")
                        }
                        finally {
                            showToast("Pin setup successful!!!")

                            startActivity(Intent(requireActivity(), Class.forName(MainDashboard)))
                            requireActivity().finish()
                        }


                    }
                    else{
                        viewModel.error.postValue("Pin doesn't match!!!")
                        binding.tvHint.text = "Re-enter your pin"
                    }
                }
                PinStatus.EnterPin->{
                    if (pin != viewModel.pin){
                        // on incorrect pin
                        viewModel.error.postValue("Incorrect Pin!!!")
                        binding.btnForgotPin.isVisible = true
                    }else{
                        // on correct pin
                        startActivity(Intent(requireActivity(), Class.forName(MainDashboard)))
                        requireActivity().finish()

                    }
                }
            }
        }

        override fun onEmpty() {
            Timber.d( "Pin empty")
        }

        override fun onPinChange(pinLength: Int, intermediatePin: String) {

            Timber.d( "Pin changed, new length $pinLength with intermediate pin $intermediatePin")
        }
    }



    override fun subscribeObservers() {
        viewModel.isSecured.observe(viewLifecycleOwner){
            isSecured = it
            if (isSecured){

                binding.apply {
                    viewModel.pinStatus = PinStatus.EnterPin
                    tvHint.text = "Enter Your Pin"
                }
            }else{
                binding.apply {
                    viewModel.pinStatus = PinStatus.SettingNewPin
                    tvHint.text = "Set New Pin"
                }

            }
        }

        viewModel.userPin.observe(viewLifecycleOwner){
            viewModel.pin = it
        }

        viewModel.error.observe(viewLifecycleOwner){
            binding.tvMessage.isVisible = !it.isNullOrEmpty()
            if (!it.isNullOrEmpty()){
                binding.tvMessage.text = it
            }
        }
    }

}

