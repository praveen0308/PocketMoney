package com.jmm.shopping.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.jmm.model.shopping_models.ProductMainCategory
import com.jmm.shopping.R
import com.jmm.shopping.adapters.ProductMainCategoriesAdapter
import com.jmm.shopping.databinding.FragmentProductMainCategoriesBinding
import com.jmm.shopping.viewmodel.StoreViewModel
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseFragment
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ProductMainCategories : BaseFragment<FragmentProductMainCategoriesBinding>(FragmentProductMainCategoriesBinding::inflate), ProductMainCategoriesAdapter.ProductMainCategoriesInterface,
    ApplicationToolbar.ApplicationToolbarListener {

    //ViewModels
    private val storeViewModel by viewModels<StoreViewModel>()

    // Adapters
    private lateinit var productMainCategoryAdapter:ProductMainCategoriesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRvMainCategories()
        subscribeObservers()
        storeViewModel.getProductMainCategories()
        binding.toolbarProductMainCategories.setApplicationToolbarListener(this)

    }
    override fun subscribeObservers() {
        storeViewModel.productMainCategories.observe(viewLifecycleOwner) { _result ->
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


    override fun onMainCategoryClick(mainCategory: ProductMainCategory) {
        findNavController().navigate(
            R.id.action_productMainCategories_to_productCategories,
            ProductCategoriesArgs(mainCategory.ID,mainCategory.Name).toBundle())
    }

    override fun onToolbarNavClick() {
        requireActivity().finish()
    }

    override fun onMenuClick() {

    }
}