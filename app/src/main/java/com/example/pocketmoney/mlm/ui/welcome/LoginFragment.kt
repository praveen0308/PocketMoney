package com.example.pocketmoney.mlm.ui.welcome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentLoginBinding
import com.example.pocketmoney.mlm.model.UserModel
import com.example.pocketmoney.mlm.viewmodel.AccountViewModel
import com.example.pocketmoney.utils.Constants
import com.example.pocketmoney.utils.DataState
import dagger.hilt.android.AndroidEntryPoint
import dmax.dialog.SpotsDialog
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!
    private val viewModel : AccountViewModel by viewModels()

    private lateinit var dialog: android.app.AlertDialog

    private lateinit var navController:NavController
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        createProgressDialog()

        binding.btnRegister.setOnClickListener(View.OnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        })

        binding.btnSignIn.setOnClickListener{
            if (!binding.etUsername.text.isNullOrBlank()) {
                if (!binding.etPassword.text.isNullOrBlank()){
                viewModel.doLogin(
                    binding.etUsername.text.toString().trim(),
                    binding.etPassword.text.toString().trim()
                )}
                else Toast.makeText(context, "Password cannot be empty !!", Toast.LENGTH_SHORT).show()
            } else Toast.makeText(context, "Enter valid username..", Toast.LENGTH_SHORT).show()

        }
        subscribeObservers()
    }


    private fun subscribeObservers(){
        viewModel.userModel.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<UserModel?> -> {
                    displayLoading(false)

                    viewModel.updateWelcomeStatus(Constants.LOGIN_DONE)
                    navController.navigate(LoginFragmentDirections.actionLoginFragmentToMainDashboard())
                    activity?.finish()
                }
                is DataState.Loading -> {
                    displayLoading(true)

                }
                is DataState.Error -> {
                    displayLoading(false)
                    displayError(dataState.exception.message)
                }
            }
        })
    }

    private fun displayLoading(state: Boolean) {
        if (state) dialog.show() else dialog.dismiss()

    }

    private fun createProgressDialog(){
        dialog = SpotsDialog.Builder()
                .setContext(context)
                .setMessage("Logging In...")
                .setCancelable(false)
                .setTheme(R.style.CustomProgressDialog)
                .build()



    }

    private fun displayError(message: String?){
        if(message != null){
            Toast.makeText(context, "Incorrect Username & Password !!!", Toast.LENGTH_LONG).show()
            Timber.e(message)
        }else{
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }

}