package com.jmm.mlm.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.jmm.mlm.MainDashboard
import com.jmm.mlm.databinding.FragmentShopBinding
import com.jmm.model.myEnums.MyEnums
import com.jmm.model.shopping_models.HomeContentMaster
import com.jmm.model.shopping_models.ProductModel
import com.jmm.navigation.NavRoute.Checkout1
import com.jmm.repository.IResource
import com.jmm.shopping.adapters.ShoppingHomeCategoriesAdapter
import com.jmm.shopping.adapters.ShoppingHomeMasterAdapter
import com.jmm.shopping.ui.BuyProduct
import com.jmm.shopping.ui.SearchProductActivity
import com.jmm.shopping.viewmodel.ShopViewModel
import com.jmm.util.ApplicationToolbar
import com.jmm.util.BaseFragment
import com.jmm.util.Status
import com.jmm.util.identify
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class Shop : BaseFragment<FragmentShopBinding>(FragmentShopBinding::inflate),
    ShoppingHomeCategoriesAdapter.ProductClickListener,
    ApplicationToolbar.ApplicationToolbarListener {


    // Adapter
    private lateinit var shoppingHomeParentAdapter: ShoppingHomeMasterAdapter

    // ViewModel
    private val viewModel: ShopViewModel by viewModels()


    // Interface
    private lateinit var fragmentListener: ShopFragmentListener

    // Variable
    private var userID: String = ""
    private var cartItemCount: Int = 0

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)

        fragmentListener = activity as MainDashboard
    }

    override fun onResume() {
        super.onResume()
        if (userID != "") {
            viewModel.getCartItemCount(userID)
            (activity as MainDashboard).toggleBottomNav(true)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentShopSearchView.etSearchView.apply {

            inputType = InputType.TYPE_NULL
            setOnClickListener {
                startActivity(Intent(requireActivity(),SearchProductActivity::class.java))
//                findNavController().navigate(R.id.action_shop_to_searchProductActivity)
            }
        }

        setupRecyclerView()
        binding.fragmentShopToolbar.setApplicationToolbarListener(this)
//        viewModel.getProductList(ShoppingHomeEvent.GetProductList)
    }

    override fun subscribeObservers() {
        viewModel.products.observe(viewLifecycleOwner){result->
            result.data?.let { populateRecyclerView(it) }
            displayLoading(false)
            when(result){

                is IResource.Error -> {
                    showToast(result.error!!.identify())
                    Timber.e(result.error)
                }
                is IResource.Loading -> displayLoading(true)
                is IResource.Success ->result.data?.let { populateRecyclerView(it) }
            }

        }
        viewModel.productList.observe(viewLifecycleOwner) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        populateRecyclerView(it)
                        displayLoading(false)
                    }
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


        viewModel.userId.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                userID = it
                viewModel.getCartItemCount(userID)
            }
        }

        viewModel.cartItemCount.observe(viewLifecycleOwner) { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        cartItemCount = it
                        binding.fragmentShopToolbar.setMenuBadgeCount(cartItemCount)
                        displayLoading(false)
                    }
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


    private fun populateRecyclerView(data: List<ProductModel>) {

        shoppingHomeParentAdapter.setContentList(prepareHomeData(data))

    }

    private fun setupRecyclerView() {
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        shoppingHomeParentAdapter = ShoppingHomeMasterAdapter(this)
        binding.recyclerView.adapter = shoppingHomeParentAdapter
    }

    private fun prepareHomeData(response: List<ProductModel>): List<HomeContentMaster> {

        val contentList: MutableList<HomeContentMaster> = ArrayList()
        val featuredProductList: MutableList<ProductModel> = ArrayList()
        val latestProductList: MutableList<ProductModel> = ArrayList()
        val specialOffersList: MutableList<ProductModel> = ArrayList()
        for (i in response.indices) {
            if (response[i].FeaturedProductInd) {
                featuredProductList.add(response[i])
            }
            if (response[i].MainPageInd) {
                latestProductList.add(response[i])
            }
            if (response[i].SpecialOfferInd) {
                specialOffersList.add(response[i])
            }
        }
        val latestProducts = HomeContentMaster(
            latestProductList,
            MyEnums.LATEST_PRODUCT,
            "Latest Products"
        )

        val featuredProducts = HomeContentMaster(
            featuredProductList,
            MyEnums.FEATURED_PRODUCT,
            "Featured Products"
        )

        val specialOffers = HomeContentMaster(
            specialOffersList,
            MyEnums.SPECIAL_OFFER,
            "Special Offers"
        )


        contentList.add(latestProducts)
        contentList.add(featuredProducts)
        contentList.add(specialOffers)

        return contentList
    }


    interface ShopFragmentListener {
        fun onNavigationClick()
    }

    override fun onProductClick(viewType: MyEnums?, id: Int) {
        Toast.makeText(requireActivity().applicationContext, id.toString(), Toast.LENGTH_LONG)
            .show()
    }

    override fun onProductClick(productID: Int, itemID: Int) {

        val intent = Intent(activity, BuyProduct::class.java)
        intent.putExtra("PRODUCT_ID", productID)
        intent.putExtra("PRODUCT_ITEM_ID", itemID)
        startActivity(intent)

    }

    override fun onToolbarNavClick() {
        fragmentListener.onNavigationClick()
    }

    override fun onMenuClick() {
        startActivity(Intent(requireActivity(), Class.forName(Checkout1)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        progressBarHandler.hide()
    }
}