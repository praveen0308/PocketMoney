package com.example.pocketmoney.shopping.ui

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pocketmoney.R
import com.example.pocketmoney.databinding.ActivityBuyProductBinding
import com.example.pocketmoney.shopping.adapters.ProductVariantAdapter
import com.example.pocketmoney.shopping.adapters.ProductVariantValuesAdapter
import com.example.pocketmoney.shopping.adapters.ShoppingHomeCategoriesAdapter
import com.example.pocketmoney.shopping.adapters.ShoppingHomeMasterAdapter
import com.example.pocketmoney.shopping.model.*
import com.example.pocketmoney.shopping.viewmodel.BuyProductViewModel
import com.example.pocketmoney.utils.ApplicationToolbar
import com.example.pocketmoney.utils.BaseActivity
import com.example.pocketmoney.utils.DataState
import com.example.pocketmoney.utils.Status
import com.example.pocketmoney.utils.myEnums.MyEnums
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class BuyProduct : BaseActivity<ActivityBuyProductBinding>(ActivityBuyProductBinding::inflate), ShoppingHomeCategoriesAdapter.ProductClickListener, ApplicationToolbar.ApplicationToolbarListener, ProductVariantValuesAdapter.ProductVariantValuesAdapterListener {

    // ViewModels
    private val viewModel by viewModels<BuyProductViewModel>()
    

    // Adapters
    private lateinit var shoppingHomeParentAdapter: ShoppingHomeMasterAdapter
    private lateinit var productVariantAdapter: ProductVariantAdapter

    // List
    private lateinit var productVariantList: MutableList<ModelProductVariant>

    // Variable
    private var productId: Int = 0
    private var productItemId: Int = 0
    private var userID: String= ""
    private var cartItemCount: Int = 0

    override fun onResume() {
        super.onResume()
        if (userID!=""){
        viewModel.getCartItemCount(userID)}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        productId = intent.getIntExtra("PRODUCT_ID", 0)
        productItemId = intent.getIntExtra("PRODUCT_ITEM_ID", 0)
        viewModel.getProductDetails(productItemId)
        viewModel.getProductVariantValues(productId)

        binding.btnAddToCart.setOnClickListener {
            viewModel.addToCart(productItemId, userID, 1)
        }

        binding.buyProductToolbar.setApplicationToolbarListener(this)

        setupRvSimilarProducts()
        setupRvProductVariants()


    }

    override fun subscribeObservers() {

        viewModel.userId.observe(this, {
            userID = it
            viewModel.getCartItemCount(userID)
        })
        viewModel.productDetail.observe(this, { dataState ->
            when (dataState) {
                is DataState.Success<ProductModel> -> {
                    displayLoading(false)
                    setupProductImagePager(dataState.data.Product_Image)
                    populateViews(dataState.data)

                    viewModel.getSimilarProducts(dataState.data.CategoryId)
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

        viewModel.productVariantValues.observe(this, { dataState ->
            when (dataState) {
                is DataState.Success<List<ProductVariantValue>> -> {
                    displayLoading(false)
//                    Timber.d(dataState.data.toString())
                    populateRvProductVariants(dataState.data)
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



        viewModel.similarProductList.observe(this, { dataState ->
            when (dataState) {
                is DataState.Success<List<ProductModel>> -> {
                    displayLoading(false)
                    populateRvSimilarProducts(dataState.data)
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

        viewModel.cartItemCount.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        cartItemCount = it
                        binding.buyProductToolbar.setMenuBadgeCount(cartItemCount)
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

        viewModel.addToCartOperationResult.observe(this, { _result ->
            when (_result.status) {
                Status.SUCCESS -> {
                    _result._data?.let {
                        viewModel.getCartItemCount(userID)
                        val snackbar: Snackbar = Snackbar.make(
                            binding.root, "Added Successfully",
                            Snackbar.LENGTH_LONG
                        )
                        snackbar.setAction("Go to Cart") {
                            // executed when DISMISS is clicked
                            val intent = Intent(this, YourCart::class.java)
                            startActivity(intent)

                        }
                        snackbar.show()
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


        viewModel.productItemIdAcVariant.observe(this, { dataState ->
            when (dataState) {
                is DataState.Success<Int> -> {
                    displayLoading(false)
                    productItemId = dataState.data
                    viewModel.getProductDetails(productItemId)

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

    private fun setupProductImagePager(images: List<ProductImage>) {
        binding.productImageViewPager.setImages(this@BuyProduct, images)
    }

    private fun populateViews(productModel: ProductModel) {

        binding.apply {
            btnAddToCart.visibility = View.VISIBLE
            if (productModel.StockQuantity == 0) {
                productShortDescription.tvProductStock.text = getString(R.string.out_of_stock)
                btnAddToCart.text = getString(R.string.out_of_stock)
                btnAddToCart.isEnabled = false

            } else {
                productShortDescription.tvProductStock.text = getString(R.string.in_stock)
                btnAddToCart.text = getString(R.string.add_to_cart)
                btnAddToCart.isEnabled = true
            }

            productShortDescription.apply {

                tvProductName.text = productModel.ProductName
                tvProductPrice.text = "₹ ${productModel.Price}"
                tvProductOldPrice.text = "₹ ${productModel.OldPrice}"
                tvProductOldPrice.paintFlags = tvProductOldPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                tvProductSavedMoney.text = "You Save ₹ ${productModel.Saving}"

                rtbProductRating.visibility=View.VISIBLE
                rtbProductRating.rating = 0F
                tvProductRatingsCount.text = "(0)"
            }
            tempProductFullDescription.text = productModel.Description.toString()
        }

    }

    private fun setupRvSimilarProducts() {

        shoppingHomeParentAdapter = ShoppingHomeMasterAdapter(this)
        binding.apply {
            rvSimilarProduct.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = shoppingHomeParentAdapter
            }
        }

    }

    private fun populateRvSimilarProducts(data: List<ProductModel>) {
        shoppingHomeParentAdapter.setContentList(prepareHomeData(data))
    }

    private fun setupRvProductVariants() {

        productVariantAdapter = ProductVariantAdapter(this)
        binding.apply {
            rvProductVariants.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(context)
                adapter = productVariantAdapter
            }
        }
    }

    private fun populateRvProductVariants(productVariantValueList: List<ProductVariantValue>) {
        productVariantList = ArrayList()

        val variantIDList = mutableListOf<Int>()
        for (variant in productVariantValueList) {
            variantIDList.add(variant.Varients_Id)
        }

        val uniqueVariantIDList = variantIDList.toSet().toList()


        for (id in uniqueVariantIDList) {
            val productVariant = ModelProductVariant()
            for (variant in productVariantValueList) {
                if (id == variant.Varients_Id) {
                    productVariant.VariantID = id
                    productVariant.title = variant.Varients_Code
                    if (productVariant.variantValueList != null) {
                        productVariant.variantValueList!!.add(variant)
                    } else {
                        val variantValueList = mutableListOf<ProductVariantValue>()
                        variantValueList.add(variant)
                        productVariant.variantValueList = variantValueList
                    }
                }
            }

            productVariantList.add(productVariant)
        }
//        Timber.d(productVariantList.toString())
        for (variant in productVariantList) {
            for (variantValue in variant.variantValueList!!) {
                variantValue.isSelected = variantValue.Item_Id == productItemId
            }
        }
        productVariantAdapter.setProductVariantList(productVariantList)
    }

    private fun prepareHomeData(response: List<ProductModel>): List<HomeContentMaster> {
        val contentList: MutableList<HomeContentMaster> = ArrayList()
        val latestProducts = HomeContentMaster(
                response,
                MyEnums.LATEST_PRODUCT,
                "You might also like"
        )
        contentList.add(latestProducts)
        return contentList
    }


    override fun onProductClick(viewType: MyEnums?, id: Int) {
        Toast.makeText(this@BuyProduct.applicationContext, id.toString(), Toast.LENGTH_LONG).show()
    }

    override fun onProductClick(productID: Int, itemID: Int) {
        val intent = Intent(this@BuyProduct, BuyProduct::class.java)
        intent.putExtra("PRODUCT_ID", productID)
        intent.putExtra("PRODUCT_ITEM_ID", itemID)
        startActivity(intent)
    }

    override fun onToolbarNavClick() {
        finish()
    }

    override fun onMenuClick() {
        val intent = Intent(this, YourCart::class.java)
        startActivity(intent)
    }

    override fun onItemClick(variantValue: ProductVariantValue) {
        for (variant in productVariantList) {
            if (variantValue.Varients_Id == variant.VariantID) {
                for (value in variant.variantValueList!!) {
                    if (value.Varients_Value_Code == variantValue.Varients_Value_Code) {
                        value.isSelected = variantValue.isSelected
                    }
                }
            }
        }

        var variantIds = ""
        var variantValueIds = ""
        for (i in 0 until productVariantList.size) {
            if (i != productVariantList.size - 1) {
                variantIds = variantIds + productVariantList[i].VariantID.toString() + ","
                for (j in 0 until productVariantList[i].variantValueList!!.size) {
                    if (productVariantList[i].variantValueList!![j].isSelected!!) {
                        variantValueIds = variantValueIds + productVariantList[i].variantValueList!![j].Varients_Value_Id.toString() + ","
                        break
                    }
                }
            } else {
                variantIds += productVariantList[i].VariantID.toString()

                for (j in 0 until productVariantList[i].variantValueList!!.size) {
                    if (productVariantList[i].variantValueList!![j].isSelected!!) {
                        variantValueIds += productVariantList[i].variantValueList!![j].Varients_Value_Id.toString()
                        break
                    }
                }
            }

        }

        viewModel.getProductItemIdAcVariant(productId, variantIds, variantValueIds)
    }

}
