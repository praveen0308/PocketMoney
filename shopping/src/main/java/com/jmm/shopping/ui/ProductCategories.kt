package com.jmm.shopping.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.model.shopping_models.ProductCategory
import com.jmm.shopping.R
import com.jmm.shopping.adapters.ProductCategoriesAdapter
import com.jmm.shopping.databinding.FragmentProductCategoriesBinding
import com.jmm.shopping.viewmodel.StoreViewModel
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseFragment
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductCategories : BaseFragment<FragmentProductCategoriesBinding>(FragmentProductCategoriesBinding::inflate), ProductCategoriesAdapter.ProductCategoriesAdapterInterface,
    ApplicationToolbar.ApplicationToolbarListener {

    //ViewModels
    private val storeViewModel by viewModels<StoreViewModel>()

    // Adapters
    private lateinit var productCategoryAdapter: ProductCategoriesAdapter

    // Variable
    private val args by navArgs<ProductCategoriesArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvMainCategories()
        subscribeObservers()
        storeViewModel.getProductCategories()
        binding.toolbarProductCategories.setApplicationToolbarListener(this)
        binding.toolbarProductCategories.setToolbarTitle(args.mainCategoryName)
    }
    override fun subscribeObservers() {
        storeViewModel.productCategories.observe(viewLifecycleOwner) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let { list ->
                        productCategoryAdapter.setCategoryList(list.filter { it.MainCategoryId == args.mainCategoryId })
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

    override fun onCategoryClick(category: ProductCategory) {
        findNavController().navigate(R.id.action_productCategories_to_searchResult2,SearchResultArgs(category.Name).toBundle())
    }

    override fun onToolbarNavClick() {
        findNavController().popBackStack()
    }

    override fun onMenuClick() {

    }
}