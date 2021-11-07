package com.sampurna.pocketmoney.shopping.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.sampurna.pocketmoney.R
import com.sampurna.pocketmoney.databinding.FragmentProductMainCategoriesBinding
import com.sampurna.pocketmoney.shopping.adapters.ProductMainCategoriesAdapter
import com.sampurna.pocketmoney.shopping.model.ProductMainCategory
import com.sampurna.pocketmoney.shopping.viewmodel.StoreViewModel
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.ProgressBarHandler
import com.sampurna.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class ProductMainCategories : Fragment(), ProductMainCategoriesAdapter.ProductMainCategoriesInterface,
    ApplicationToolbar.ApplicationToolbarListener {

    private var param1: String? = null
    private var param2: String? = null

    // UI
    private var _binding:FragmentProductMainCategoriesBinding?=null
    private val binding get() = _binding!!
    private lateinit var progressBarHandler: ProgressBarHandler

    //ViewModels
    private val storeViewModel by viewModels<StoreViewModel>()

    // Adapters
    private lateinit var productMainCategoryAdapter:ProductMainCategoriesAdapter
    private lateinit var checkoutOrderInterface: CheckoutOrderInterface

    // Variable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler =
            ProgressBarHandler(requireActivity())
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
        _binding = FragmentProductMainCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvMainCategories()
        subscribeObservers()
        storeViewModel.getProductMainCategories()
        binding.toolbarProductMainCategories.setApplicationToolbarListener(this)

    }
    private fun subscribeObservers() {
        storeViewModel.productMainCategories.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        productMainCategoryAdapter.setMainCategories(it)
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


    private fun displayLoading(state: Boolean) {
        if (state) progressBarHandler.show() else progressBarHandler.hide()
    }

    private fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
        }
    }

    private fun setupRvMainCategories(){
        productMainCategoryAdapter = ProductMainCategoriesAdapter(this)
        binding.rvMainCategoryList.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context,3)
//            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = productMainCategoryAdapter
        }
    }


    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductMainCategories().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onMainCategoryClick(mainCategory: ProductMainCategory) {
        findNavController().navigate(R.id.action_productMainCategories_to_productCategories,
            ProductCategoriesArgs(mainCategory.ID,mainCategory.Name).toBundle())
    }

    override fun onToolbarNavClick() {
        requireActivity().finish()
    }

    override fun onMenuClick() {

    }
}