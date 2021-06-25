package com.example.pocketmoney.mlm.ui.dashboard.pages

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.*
import android.view.View.OnTouchListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.FragmentShopBinding
import com.example.pocketmoney.mlm.ui.dashboard.MainDashboard
import com.example.pocketmoney.shopping.adapters.ShoppingHomeCategoriesAdapter
import com.example.pocketmoney.shopping.adapters.ShoppingHomeMasterAdapter
import com.example.pocketmoney.shopping.model.HomeContentMaster
import com.example.pocketmoney.shopping.model.ProductModel
import com.example.pocketmoney.shopping.ui.BuyProduct
import com.example.pocketmoney.shopping.viewmodel.CartViewModel
import com.example.pocketmoney.shopping.viewmodel.ProductViewModel
import com.example.pocketmoney.shopping.viewmodel.ShoppingHomeEvent
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.myEnums.MyEnums
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class Shop : Fragment(), ShoppingHomeCategoriesAdapter.ProductClickListener, ApplicationToolbar.ApplicationToolbarListener {


    // Ui
    private var _binding: FragmentShopBinding? = null
    private val binding get() = _binding!!

    // Adapter
    private lateinit var shoppingHomeParentAdapter: ShoppingHomeMasterAdapter

    // ViewModel
    private val viewModel: ProductViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()

    // Interface
    private lateinit var fragmentListener: ShopFragmentListener

    // Variable
    private var userID: String = ""
    private var cartItemCount: Int = 0

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true)
        _binding = FragmentShopBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        fragmentListener = activity as MainDashboard
    }

    override fun onResume() {
        super.onResume()
        if (userID!="") {
            cartViewModel.getCartItemCount(userID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentShopSearchView.etSearchView.apply {

            inputType = InputType.TYPE_NULL
            setOnClickListener {
                findNavController().navigate(R.id.action_shop_to_searchProductActivity)
            }
        }

        setupRecyclerView()
        binding.fragmentShopToolbar.setApplicationToolbarListener(this)
        subscribeObservers()
        viewModel.getProductList(ShoppingHomeEvent.GetProductList)
    }

    private fun subscribeObservers() {

        viewModel.productList.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<List<ProductModel>> -> {
                    displayLoading(false)
                    displayRefreshing(false)
                    populateRecyclerView(dataState.data)
//                    Timber.d(dataState.data.toString())
                }
                is DataState.Loading -> {
                    displayLoading(true)

                }
                is DataState.Error -> {
                    displayLoading(false)
                    displayRefreshing(false)
                    displayError(dataState.exception.message)
                }
            }
        })


        cartViewModel.userID.observe(viewLifecycleOwner, {
            userID = it
            cartViewModel.getCartItemCount(userID)
        })

        cartViewModel.cartItemCount.observe(viewLifecycleOwner, { dataState ->
            when (dataState) {
                is DataState.Success<Int> -> {
                    displayLoading(false)
                    cartItemCount = dataState.data
                    binding.fragmentShopToolbar.setMenuBadgeCount(cartItemCount)
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
        binding.progressBar.visibility = if (state) View.VISIBLE else View.GONE
    }

    private fun displayRefreshing(loading: Boolean) {
//        binding.swipeRefreshLayout.isRefreshing = loading
    }

    private fun displayError(message: String?) {
        if (message != null) {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "Unknown error", Toast.LENGTH_LONG).show()
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
        Toast.makeText(requireActivity().applicationContext, id.toString(), Toast.LENGTH_LONG).show()
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
        findNavController().navigate(R.id.action_shop_to_yourCart)
    }

}