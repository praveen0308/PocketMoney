package com.example.pocketmoney.shopping.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentProductCategoriesBinding
import com.example.pocketmoney.shopping.adapters.ProductCategoriesAdapter
import com.example.pocketmoney.shopping.model.ProductCategory
import com.example.pocketmoney.shopping.viewmodel.StoreViewModel
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.ProgressBarHandler
import com.example.pocketmoney.utils.Status
import dagger.hilt.android.AndroidEntryPoint

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

@AndroidEntryPoint
class ProductCategories : Fragment(), ProductCategoriesAdapter.ProductCategoriesAdapterInterface,
    ApplicationToolbar.ApplicationToolbarListener {

    private var param1: String? = null
    private var param2: String? = null

    // UI
    private var _binding: FragmentProductCategoriesBinding?=null
    private val binding get() = _binding!!
    private lateinit var progressBarHandler: ProgressBarHandler

    //ViewModels
    private val storeViewModel by viewModels<StoreViewModel>()

    // Adapters
    private lateinit var productCategoryAdapter: ProductCategoriesAdapter

    // Variable
    private val args by navArgs<ProductCategoriesArgs>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler = ProgressBarHandler(requireActivity())
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
        _binding = FragmentProductCategoriesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvMainCategories()
        subscribeObservers()
        storeViewModel.getProductCategories()
        binding.toolbarProductCategories.setApplicationToolbarListener(this)
        binding.toolbarProductCategories.setToolbarTitle(args.mainCategoryName)
    }
    private fun subscribeObservers() {
        storeViewModel.productCategories.observe(viewLifecycleOwner, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {list->
                        productCategoryAdapter.setCategoryList(list.filter { it.MainCategoryId==args.mainCategoryId })
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
        productCategoryAdapter = ProductCategoriesAdapter(this)
        binding.rvCategoryList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context)
//            layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
            adapter = productCategoryAdapter
        }
    }



    companion object {

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProductCategories().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onCategoryClick(category: ProductCategory) {
        findNavController().navigate(R.id.action_productCategories_to_searchResult2,SearchResultArgs(category.Name).toBundle())
    }

    override fun onToolbarNavClick() {
        findNavController().popBackStack()
    }

    override fun onMenuClick() {

    }
}