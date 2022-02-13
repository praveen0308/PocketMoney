package com.jmm.shopping.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.jmm.shopping.R
import com.jmm.shopping.adapters.CustomMenuAdapter
import com.jmm.shopping.databinding.FragmentSearchProductBinding
import com.jmm.util.ApplicationToolbar
import dagger.hilt.android.AndroidEntryPoint


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class SearchProduct : Fragment(), ApplicationToolbar.ApplicationToolbarListener {

    private var param1: String? = null
    private var param2: String? = null

    // Ui
    private var _binding: FragmentSearchProductBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    //Adapter
    private lateinit var customMenuAdapter: CustomMenuAdapter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applicationToolbar4.setApplicationToolbarListener(this)

        binding.etSearchView.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                findNavController().navigate(R.id.action_searchProduct_to_searchResult,SearchResultArgs(binding.etSearchView.text.toString()).toBundle())
                return@OnEditorActionListener true
            }
            false
        })
    }

    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchProduct().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onToolbarNavClick() {
        requireActivity().finish()
    }

    override fun onMenuClick() {

    }
}