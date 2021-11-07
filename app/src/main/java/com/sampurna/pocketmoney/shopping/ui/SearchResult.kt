package com.sampurna.pocketmoney.shopping.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.sampurna.pocketmoney.databinding.FragmentSearchResultBinding
import com.sampurna.pocketmoney.shopping.adapters.SearchResultProductAdapter
import com.sampurna.pocketmoney.shopping.model.ProductModel
import com.sampurna.pocketmoney.shopping.ui.bottomsheetdialogs.SortingFilter
import com.sampurna.pocketmoney.shopping.viewmodel.FilterViewModel
import com.sampurna.pocketmoney.shopping.viewmodel.ProductViewModel
import com.sampurna.pocketmoney.utils.ApplicationToolbar
import com.sampurna.pocketmoney.utils.ProgressBarHandler
import com.sampurna.pocketmoney.utils.Status
import com.sampurna.pocketmoney.utils.myEnums.FilterEnum
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResult : Fragment(), SearchResultProductAdapter.SearchProductClickListener,
    ApplicationToolbar.ApplicationToolbarListener,SortingFilter.SortingFilterInterface {

    //UI
    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var progressBarHandler: ProgressBarHandler

    //ViewModels
    private val productViewModel: ProductViewModel by viewModels()
    private val filterViewModel: FilterViewModel by activityViewModels()

    // Adapters
    private lateinit var searchResultProductAdapter: SearchResultProductAdapter


    // Variable
    private lateinit var searchKeyWord: String
    private val args: SearchResultArgs by navArgs()
    private var selectedSortingFilter : FilterEnum = FilterEnum.PRICE_ASCENDING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        progressBarHandler =
            ProgressBarHandler(requireActivity())
        searchKeyWord = args.searchKeyword

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCartItemRecyclerView()

        subscribeObservers()
        binding.filterLayout.btnSorting.text = selectedSortingFilter.name
        productViewModel.searchProduct(searchKeyWord)
        binding.applicationToolbar.setApplicationToolbarListener(this)
        binding.applicationToolbar.setToolbarTitle(searchKeyWord)

        binding.filterLayout.btnSorting.setOnClickListener {
//            findNavController().navigate(R.id.action_searchResult_to_sortingFilter,SortingFilterArgs(selectedSortingFilter).toBundle())
        }
    }

    private fun subscribeObservers() {
//        filterViewModel.sortingFilterList.observe(viewLifecycleOwner, { list ->
//            binding.filterLayout.btnSorting.text = list.find { it.isSelected }!!.displayText
//        })
        productViewModel.productListBySearch.observe(requireActivity(), { _result ->
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
        })


    }

    private fun onProductListEmpty(productList: List<ProductModel>) {
        if (productList.size == 0) {
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

    override fun OnItemFilterItemClick(filter: FilterEnum) {
        this.selectedSortingFilter = filter
        binding.filterLayout.btnSorting.text = selectedSortingFilter.name
    }


}