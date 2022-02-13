package com.jmm.shopping.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.jmm.model.shopping_models.ProductModel
import com.jmm.shopping.adapters.SearchResultProductAdapter
import com.jmm.shopping.databinding.FragmentSearchResultBinding
import com.jmm.shopping.viewmodel.ProductViewModel
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseFragment
import com.jmm.util.ProgressBarHandler
import com.jmm.util.Status
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResult : BaseFragment<FragmentSearchResultBinding>(FragmentSearchResultBinding::inflate), SearchResultProductAdapter.SearchProductClickListener,
    ApplicationToolbar.ApplicationToolbarListener {

    //ViewModels
    private val productViewModel: ProductViewModel by viewModels()

    // Adapters
    private lateinit var searchResultProductAdapter: SearchResultProductAdapter


    // Variable
    private lateinit var searchKeyWord: String
    private val args: SearchResultArgs by navArgs()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler =
            ProgressBarHandler(requireActivity())
        searchKeyWord = args.searchKeyword

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCartItemRecyclerView()

        subscribeObservers()

        productViewModel.searchProduct(searchKeyWord)
        binding.applicationToolbar.setApplicationToolbarListener(this)
        binding.applicationToolbar.setToolbarTitle(searchKeyWord)

        binding.btnSort.setOnClickListener {

        }
    }

    override fun subscribeObservers() {

        productViewModel.productListBySearch.observe(requireActivity()) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        onProductListEmpty(it)
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

    private fun onProductListEmpty(productList: List<ProductModel>) {
        if (productList.isEmpty()) {
            binding.emptyView.visibility = View.VISIBLE
        } else {
            searchResultProductAdapter.setProductList(productList.toMutableList())
        }
    }

    private fun setUpCartItemRecyclerView() {
        searchResultProductAdapter = SearchResultProductAdapter(this)
        binding.apply {
            rvProductList.apply {
                setHasFixedSize(true)
                layoutManager = GridLayoutManager(context, 2)
                adapter = searchResultProductAdapter
            }
        }
    }

    override fun onProductClick(productID: Int, itemID: Int) {
        val intent = Intent(activity, BuyProduct::class.java)
        intent.putExtra("PRODUCT_ID", productID)
        intent.putExtra("PRODUCT_ITEM_ID", itemID)
        startActivity(intent)
    }

    override fun onToolbarNavClick() {
        findNavController().popBackStack()
    }

    override fun onMenuClick() {
        findNavController().popBackStack()
    }



}